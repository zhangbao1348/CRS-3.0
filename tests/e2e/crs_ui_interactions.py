"""CRS 核心页面字段与功能级回归。

关联模块：订单、房态、预订控制、房型、基础价格、价格查询、报表。
当价格链路缺少可验证数据时，使用现有业务接口写入唯一测试记录，并在 finally 中定向清理。
"""

from __future__ import annotations

import argparse
import json
import os
import re
import subprocess
from datetime import date
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

from playwright.sync_api import sync_playwright


TEST_MARKER = "CODEX_UI_TEST_20260826"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="CRS 核心业务交互回归")
    parser.add_argument("--base-url", default="http://127.0.0.1:3001")
    parser.add_argument("--output", default="/private/tmp/crs-ui-interactions.json")
    return parser.parse_args()


def browser_api(page, path: str, method: str = "GET", body: dict[str, Any] | None = None) -> Any:
    return page.evaluate(
        """async ({ path, method, body }) => {
          const token = localStorage.getItem('crs_token');
          const tenantId = localStorage.getItem('crs_selected_tenant');
          const response = await fetch(path, {
            method,
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`,
              'X-Tenant-Id': tenantId,
              'X-Operator-Name': 'CODEX_UI_TEST_20260826',
            },
            body: body ? JSON.stringify(body) : undefined,
          });
          const payload = await response.json().catch(() => ({}));
          if (!response.ok) throw new Error(`${response.status} ${JSON.stringify(payload)}`);
          return payload;
        }""",
        {"path": path, "method": method, "body": body},
    )


def read_env_file(path: Path) -> dict[str, str]:
    values: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        values[key] = value
    return values


def cleanup_price(project_dir: Path, fixture: dict[str, Any]) -> None:
    env_values = read_env_file(project_dir / ".env.local")
    jdbc_url = env_values["CRS_DB_URL"].removeprefix("jdbc:")
    parsed = urlparse(jdbc_url)
    database = parsed.path.lstrip("/")
    mysql_env = os.environ.copy()
    mysql_env["MYSQL_PWD"] = env_values["CRS_DB_PASSWORD"]
    sql = (
        "DELETE FROM hotel_price_logs "
        f"WHERE tenant_id={int(fixture['tenantId'])} "
        f"AND hotel_code='{fixture['hotelCode']}' "
        f"AND rate_code='{fixture['rateCode']}' "
        f"AND operator_name='{TEST_MARKER}'; "
        "DELETE FROM hotel_prices "
        f"WHERE tenant_id={int(fixture['tenantId'])} "
        f"AND hotel_code='{fixture['hotelCode']}' "
        f"AND rate_code='{fixture['rateCode']}' "
        f"AND room_type_code='{fixture['roomTypeCode']}' "
        f"AND price_date='{fixture['priceDate']}';"
    )
    subprocess.run(
        [
            "mysql",
            "-h", parsed.hostname or "127.0.0.1",
            "-P", str(parsed.port or 3306),
            "-u", env_values["CRS_DB_USERNAME"],
            database,
            "-e", sql,
        ],
        check=True,
        env=mysql_env,
        capture_output=True,
        text=True,
    )


def select_ant_option(page, selector_index: int, option_text: str) -> None:
    page.locator(".crs-shell__content .ant-select-selector").nth(selector_index).click()
    page.locator(".ant-select-item-option").filter(has_text=option_text).first.click()


def set_month(page, month: str) -> None:
    month_input = page.locator('input[placeholder="选择月份"]').first
    month_input.click()
    month_input.fill(month)
    month_input.press("Enter")


def main() -> int:
    args = parse_args()
    username = os.environ.get("CRS_TEST_USERNAME")
    password = os.environ.get("CRS_TEST_PASSWORD")
    if not username or not password:
        raise SystemExit("请通过 CRS_TEST_USERNAME 和 CRS_TEST_PASSWORD 提供本地测试账号")

    project_dir = Path(__file__).resolve().parents[2]
    fixture: dict[str, Any] | None = None
    result: dict[str, Any] = {"marker": TEST_MARKER, "checks": [], "errors": [], "http4xx": []}

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        context = browser.new_context(viewport={"width": 1440, "height": 1000}, locale="zh-CN")
        page = context.new_page()
        page.on("console", lambda msg: result["errors"].append(f"console:{msg.text}") if msg.type == "error" else None)
        page.on("response", lambda response: (
            result["errors"].append(f"http:{response.status}:{response.url}") if response.status >= 500
            else result["http4xx"].append(f"http:{response.status}:{response.url}") if response.status >= 400
            else None
        ))

        try:
            page.goto(args.base_url + "/login", wait_until="networkidle")
            page.locator('input[autocomplete="username"]').fill(username)
            page.locator('input[autocomplete="current-password"]').fill(password)
            page.get_by_role("button", name="进入工作台").click()
            page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
            page.wait_for_timeout(1200)

            # 订单：字段可识别、查询/重置可执行、导出入口可达。
            page.goto(args.base_url + "/reservation/reservation-list", wait_until="networkidle")
            page.get_by_label("订单号").fill("CODEX-NOT-EXISTS")
            page.locator(".crs-shell__content .ant-btn-primary").first.click()
            page.wait_for_timeout(500)
            page.get_by_role("button", name="重置").click()
            page.wait_for_timeout(500)
            page.get_by_role("button", name="数据导出").click()
            page.wait_for_url(re.compile(r"/reports/data-export$"))
            result["checks"].append("订单查询、重置与数据导出入口")

            # 房态、预订控制：打开批量表单并核对关键字段，不提交业务变更。
            page.goto(args.base_url + "/inventory/room-status", wait_until="networkidle")
            page.get_by_role("button", name="批量修改").click()
            page.get_by_text("选择日期范围：", exact=True).wait_for()
            page.locator(".ant-modal-close").click()
            result["checks"].append("房态批量维护表单")

            page.goto(args.base_url + "/inventory/booking-control", wait_until="networkidle")
            page.get_by_role("button", name="批量修改").click()
            for field in ["选择日期范围", "取消规则", "提前预订天数", "最小连住天数", "最大连住天数"]:
                page.get_by_text(field, exact=True).wait_for()
            page.locator(".ant-modal-close").click()
            result["checks"].append("预订控制批量表单与必填字段")

            # 房型、价格计划：进入真实新增表单，检查必填字段，不保存。
            page.goto(args.base_url + "/room-management/room-type", wait_until="networkidle")
            page.get_by_role("button", name=re.compile("新增")).first.click()
            page.wait_for_timeout(500)
            assert page.locator(".ant-form-item-required").count() > 0
            result["checks"].append("房型新增表单必填字段")

            page.goto(args.base_url + "/rate-management/rate-plan", wait_until="networkidle")
            page.get_by_role("button", name=re.compile("新增")).first.click()
            page.wait_for_url(re.compile(r"/rate-management/add-rate-plan"))
            for field in ["价格计划代码", "价格计划名称", "价格计划类别", "市场码", "来源码"]:
                page.locator(".ant-form-item-label").filter(has_text=field).first.wait_for(timeout=15000)
            result["checks"].append("价格计划新增表单必填字段")

            # 价格链路：找一条真实基础价格计划和房型；远期空日期缺数据时写入唯一 fixture。
            page.goto(args.base_url + "/dashboard", wait_until="networkidle")
            context_values = page.evaluate(
                """() => {
                  const tenantId = localStorage.getItem('crs_selected_tenant');
                  return {
                    tenantId: Number(tenantId),
                    hotelCode: localStorage.getItem(`crs_selected_hotel_${tenantId}`),
                  };
                }"""
            )
            hotel_code = context_values["hotelCode"]
            plans = browser_api(page, f"/api/rate-plans/by-code/hotel/{hotel_code}").get("data", [])
            room_types = browser_api(page, f"/api/hotel-room-types/by-code/hotel/{hotel_code}").get("data", [])
            base_plan = next(plan for plan in plans if plan.get("rateType") == "basic")
            room_type = next(item for item in room_types if item.get("status") == "active")
            fixture_year = date.today().year + 2
            fixture_month = f"{fixture_year}-12"
            existing = browser_api(
                page,
                f"/api/hotel-prices?hotelCode={hotel_code}&rateCode={base_plan['rateCode']}"
                f"&startDate={fixture_month}-01&endDate={fixture_month}-31",
            ).get("data", [])
            occupied_dates = {str(item.get("priceDate", ""))[:10] for item in existing}
            fixture_date = next(f"{fixture_month}-{day:02d}" for day in range(10, 21) if f"{fixture_month}-{day:02d}" not in occupied_dates)
            fixture = {
                **context_values,
                "rateCode": base_plan["rateCode"],
                "roomTypeCode": room_type["roomTypeCode"],
                "priceDate": fixture_date,
                "priceWithTax": 9999.99,
                "status": "active",
            }
            browser_api(page, "/api/hotel-prices", "POST", fixture)

            page.goto(args.base_url + "/rate-management/rack-rate", wait_until="networkidle")
            select_ant_option(page, 0, base_plan["rateCode"])
            set_month(page, fixture_month)
            page.locator(".crs-shell__content .ant-btn-primary").first.click()
            page.locator('input[value="9999.99"]').wait_for(timeout=15000)
            result["checks"].append("基础价格真实数据查询")

            page.goto(args.base_url + "/rate-management/price-query", wait_until="networkidle")
            select_ant_option(page, 0, base_plan["rateCode"])
            set_month(page, fixture_month)
            page.locator(".crs-shell__content .ant-btn-primary").first.click()
            page.get_by_text("¥9999.99", exact=True).wait_for(timeout=15000)
            result["checks"].append("价格查询含税价真实数据回显")

        except Exception as error:
            page.screenshot(path="/private/tmp/crs-ui-interactions-failure.png", full_page=True)
            result["errors"].append(f"test:{type(error).__name__}:{error}:url={page.url}")
        finally:
            if fixture:
                try:
                    cleanup_price(project_dir, fixture)
                    result["fixtureCleaned"] = True
                except Exception as cleanup_error:
                    result["fixtureCleaned"] = False
                    result["errors"].append(f"cleanup:{type(cleanup_error).__name__}:{cleanup_error}")
            browser.close()

    result["passed"] = not result["errors"]
    Path(args.output).write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False))
    return 0 if result["passed"] else 1


if __name__ == "__main__":
    raise SystemExit(main())

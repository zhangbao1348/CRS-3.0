"""CRS 集团房价码跨模块全生命周期回归。

关联模块：集团房价码、酒店价格计划、酒店分配权限、渠道发布。
所有业务操作均通过页面执行；仅预期拒绝的物理删除和最终定向清理使用接口/数据库。
"""

from __future__ import annotations

import argparse
import json
import os
import re
import subprocess
import time
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

from playwright.sync_api import Locator, Page, expect, sync_playwright


HOTEL_CODE = "JJSH001"
HOTEL_NAME = "上海锦江饭店1"
ROOM_TYPE_CODE = "ST1"
CHANNEL_NAME = "携程"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="CRS 集团房价码全生命周页面回归")
    parser.add_argument("--base-url", default="http://127.0.0.1:3001")
    parser.add_argument("--output", default="/private/tmp/crs-group-rate-code-lifecycle.json")
    return parser.parse_args()


def read_env_file(path: Path) -> dict[str, str]:
    values: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        values[key] = value
    return values


def browser_request(
    page: Page,
    path: str,
    method: str = "GET",
    body: dict[str, Any] | list[dict[str, Any]] | None = None,
) -> dict[str, Any]:
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
              'X-Operator-Name': 'CODEX_E2E_GRC',
            },
            body: body === null ? undefined : JSON.stringify(body),
          });
          const payload = await response.json().catch(() => ({}));
          return { status: response.status, payload };
        }""",
        {"path": path, "method": method, "body": body},
    )


def form_item(page: Page, label: str) -> Locator:
    return page.locator(".ant-form-item").filter(
        has=page.locator(".ant-form-item-label").filter(has_text=label)
    ).first


def choose_form_option(page: Page, label: str, option_text: str | None = None) -> None:
    item = form_item(page, label)
    item.locator(".ant-select-selector").click()
    options = page.locator(
        ".ant-select-dropdown:visible .ant-select-item-option:not(.ant-select-item-option-disabled)"
    )
    if option_text:
        options.filter(has_text=option_text).first.click(force=True)
    else:
        options.first.click(force=True)


def search_group_rate_code(page: Page, code: str) -> Locator:
    page.locator('input[placeholder="房价码代码"]').fill(code)
    page.locator("button").filter(has_text="搜索").first.click()
    row = page.locator(".ant-table-tbody tr").filter(has_text=code).first
    row.wait_for(timeout=20000)
    return row


def open_group_edit(page: Page, code: str) -> None:
    row = search_group_rate_code(page, code)
    row.locator("button").filter(has_text="编辑").first.click()
    page.get_by_text("编辑集团房价码", exact=True).wait_for(timeout=20000)


def choose_multi_option(container: Locator, page: Page, index: int, option_text: str) -> None:
    container.locator(".ant-select-selector").nth(index).click()
    page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text=option_text
    ).first.click(force=True)
    page.keyboard.press("Escape")


def cleanup_fixture(project_dir: Path, rate_code: str) -> dict[str, int]:
    env_values = read_env_file(project_dir / ".env.local")
    jdbc_url = env_values["CRS_DB_URL"].removeprefix("jdbc:")
    parsed = urlparse(jdbc_url)
    database = parsed.path.lstrip("/") or "CRS"
    mysql_env = os.environ.copy()
    mysql_env["MYSQL_PWD"] = env_values["CRS_DB_PASSWORD"]
    safe_code = rate_code.replace("'", "''")
    sql = f"""
START TRANSACTION;
DELETE FROM channel_publish_records WHERE rate_code='{safe_code}';
DELETE FROM hotel_price_logs WHERE rate_code='{safe_code}';
DELETE FROM hotel_prices WHERE rate_code='{safe_code}';
DELETE FROM hotel_rate_code_allocations WHERE rate_code='{safe_code}';
DELETE FROM rate_plans WHERE rate_code='{safe_code}';
DELETE FROM group_rate_codes WHERE rate_code='{safe_code}';
COMMIT;
SELECT
  (SELECT COUNT(*) FROM channel_publish_records WHERE rate_code='{safe_code}'),
  (SELECT COUNT(*) FROM hotel_rate_code_allocations WHERE rate_code='{safe_code}'),
  (SELECT COUNT(*) FROM rate_plans WHERE rate_code='{safe_code}'),
  (SELECT COUNT(*) FROM group_rate_codes WHERE rate_code='{safe_code}');
"""
    completed = subprocess.run(
        [
            "mysql",
            "-h", parsed.hostname or "127.0.0.1",
            "-P", str(parsed.port or 3306),
            "-u", env_values["CRS_DB_USERNAME"],
            "-N",
            database,
            "-e", sql,
        ],
        check=True,
        env=mysql_env,
        capture_output=True,
        text=True,
    )
    counts = [int(value) for value in completed.stdout.strip().split("\t")[-4:]]
    return dict(zip(["publish", "allocation", "hotelPlan", "groupRateCode"], counts))


def main() -> int:
    args = parse_args()
    username = os.environ.get("CRS_TEST_USERNAME", "admin")
    password = os.environ.get("CRS_TEST_PASSWORD", "admin123")
    project_dir = Path(__file__).resolve().parents[2]
    suffix = str(int(time.time()))[-8:]
    rate_code = f"CODEX_GRC_{suffix}"
    rate_name = f"端到端测试房价码_{suffix}"
    synced_name = f"{rate_name}_已同步"
    result: dict[str, Any] = {
        "rateCode": rate_code,
        "checks": [],
        "expectedRejections": [],
        "errors": [],
        "warnings": [],
        "http5xx": [],
        "fixtureCleaned": False,
    }
    group_rate_code_id: int | None = None

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        context = browser.new_context(viewport={"width": 1440, "height": 1000}, locale="zh-CN")
        page = context.new_page()
        def handle_console(message: Any) -> None:
            if message.type != "error":
                return
            if "Static function can not consume context" in message.text:
                result["warnings"].append(f"console:{message.text}")
                return
            if "Failed to load resource" in message.text and "400" in message.text:
                result["warnings"].append(f"console:{message.text}")
                return
            result["errors"].append(f"console:{message.text}")

        page.on("console", handle_console)
        page.on(
            "response",
            lambda response: result["http5xx"].append(f"{response.status}:{response.url}")
            if response.status >= 500
            else None,
        )

        try:
            page.goto(args.base_url + "/login", wait_until="networkidle")
            page.locator('input[autocomplete="username"]').fill(username)
            page.locator('input[autocomplete="current-password"]').fill(password)
            page.get_by_role("button", name="进入工作台").click()
            page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
            page.wait_for_timeout(1200)
            result["checks"].append("登录并进入工作台")

            # 页面校验：代码格式不合法时禁止保存。
            page.goto(args.base_url + "/group-management/add-rate-code", wait_until="networkidle")
            page.locator('input[placeholder="请输入房价代码"]').fill("BAD-CODE")
            page.get_by_role("button", name="保存, 并下一步").click()
            page.get_by_text("房价代码只能包含英文字母、数字和下划线", exact=True).wait_for()
            result["checks"].append("房价码代码格式前端校验")

            # 创建基础房价码，填充真实房型与政策引用。
            page.locator('input[placeholder="请输入房价代码"]').fill(rate_code)
            page.locator('input[placeholder="请输入房价名称"]').fill(rate_name)
            choose_form_option(page, "房价大类")
            choose_form_option(page, "市场码")
            choose_form_option(page, "来源码")
            room_checkbox = page.get_by_text(re.compile(rf"{ROOM_TYPE_CODE}\uff09$")).first
            room_checkbox.wait_for(timeout=20000)
            room_checkbox.click()
            choose_form_option(page, "担保规则", "无需担保")
            choose_form_option(page, "取消规则", "免费取消")
            page.get_by_role("button", name="保存, 并下一步").click()
            page.wait_for_url(re.compile(r"/group-management/group-rate-code$"), timeout=30000)
            page.wait_for_timeout(1500)
            row = search_group_rate_code(page, rate_code)
            assert rate_name in row.inner_text()
            result["checks"].append("集团基础房价码页面创建及列表回显")

            record_response = browser_request(page, f"/api/group-rate-codes/code/{rate_code}")
            assert record_response["status"] == 200, record_response
            group_rate_code_id = int(record_response["payload"]["id"])

            duplicate_payload = {
                key: value
                for key, value in record_response["payload"].items()
                if key not in {"id", "createdAt", "updatedAt"}
            }
            duplicate_response = browser_request(page, "/api/group-rate-codes", "POST", duplicate_payload)
            assert duplicate_response["status"] in {400, 409}, duplicate_response
            result["expectedRejections"].append("集团房价码重复代码被后端拒绝")

            # 分配到单个酒店，仅开放基础信息修改权限。
            open_group_edit(page, rate_code)
            page.get_by_role("tab", name="房价码分配").click()
            hotel_row = page.locator(".ant-table-tbody tr").filter(has_text=HOTEL_NAME).first
            hotel_row.wait_for(timeout=30000)
            switches = hotel_row.locator("button.ant-switch")
            allocated_switch = switches.nth(0)
            basic_info_switch = switches.nth(1)
            allocated_switch.click()
            expect(allocated_switch).to_have_attribute("aria-checked", "true", timeout=10000)
            expect(basic_info_switch).to_be_enabled(timeout=10000)
            basic_info_switch.click()
            page.get_by_role("button", name="保存分配设置").click()
            page.wait_for_url(re.compile(r"/group-management/group-rate-code$"), timeout=30000)
            result["checks"].append("房价码分配到酒店及五类权限开关")

            # 酒店端查询和编辑权限回显。
            page.evaluate(
                """({ hotelCode }) => {
                  const tenantId = localStorage.getItem('crs_selected_tenant');
                  localStorage.setItem(`crs_selected_hotel_${tenantId}`, hotelCode);
                }""",
                {"hotelCode": HOTEL_CODE},
            )
            page.goto(args.base_url + "/rate-management/rate-plan", wait_until="networkidle")
            page.locator('input[placeholder="请输入价格计划代码"]').fill(rate_code)
            hotel_plan_row = page.locator(".ant-table-tbody tr").filter(has_text=rate_code).first
            hotel_plan_row.wait_for(timeout=30000)
            hotel_plan_text = hotel_plan_row.inner_text()
            assert f"集团（{rate_code}）" in hotel_plan_text, hotel_plan_text
            assert "启用" in hotel_plan_text, hotel_plan_text
            hotel_plan_row.locator("button").filter(has_text="编辑").first.click()
            page.wait_for_url(re.compile(r"/rate-management/edit-rate-plan/\d+$"), timeout=20000)
            page.wait_for_timeout(1200)
            assert form_item(page, "价格计划代码").locator("input").is_disabled()
            assert not form_item(page, "价格计划名称").locator("input").is_disabled()
            assert form_item(page, "类型").locator(".ant-select-disabled").count() == 1
            result["checks"].append("酒店端集团来源、详情回显与字段权限")

            # 集团修改后显式同步到已分配酒店。
            page.goto(args.base_url + "/group-management/group-rate-code", wait_until="networkidle")
            open_group_edit(page, rate_code)
            page.locator('input[placeholder="请输入房价名称"]').fill(synced_name)
            page.get_by_role("button", name="保存, 并下一步").click()
            page.locator(".ant-modal-confirm-title").filter(has_text="同步确认").first.wait_for(
                timeout=20000
            )
            page.get_by_role("button", name="同步更新").click()
            page.wait_for_url(re.compile(r"/group-management/group-rate-code$"), timeout=30000)
            page.goto(args.base_url + "/rate-management/rate-plan", wait_until="networkidle")
            page.locator('input[placeholder="请输入价格计划代码"]').fill(rate_code)
            synced_row = page.locator(".ant-table-tbody tr").filter(has_text=rate_code).first
            synced_row.wait_for(timeout=30000)
            assert synced_name in synced_row.inner_text()
            result["checks"].append("集团修改同步到酒店价格计划")

            # 渠道发布和取消发布均通过页面完成。
            page.goto(args.base_url + "/group-management/group-rate-code", wait_until="networkidle")
            open_group_edit(page, rate_code)
            page.get_by_role("tab", name="渠道发布").click()
            publish_pane = page.locator(".ant-tabs-tabpane-active")
            publish_pane.locator("button.ant-btn-primary").nth(0).click()
            publish_row = page.locator(".ant-table-tbody tr").filter(has_text=synced_name).last
            publish_row.wait_for(timeout=20000)
            choose_multi_option(publish_row, page, 0, CHANNEL_NAME)
            choose_multi_option(publish_row, page, 1, HOTEL_NAME)
            publish_row.get_by_text("标准双床房", exact=True).click()
            publish_pane.locator("button.ant-btn-primary").nth(1).click()
            page.get_by_text(re.compile(r"保存发布配置成功")).wait_for(timeout=30000)
            cancel_button = page.locator("button").filter(has_text="取消发布").first
            cancel_button.wait_for(timeout=20000)
            cancel_button.click()
            page.locator(".ant-modal-confirm-btns .ant-btn-primary").click()
            page.get_by_text("取消发布成功", exact=True).wait_for(timeout=20000)
            publish_response = browser_request(
                page, f"/api/channel-publish/group-rate-code/records?rateCode={rate_code}"
            )
            assert publish_response["status"] == 200
            assert publish_response["payload"] == [], publish_response
            result["checks"].append("渠道发布、记录回显与取消发布")

            # 集团停用与启用级联酒店计划状态。
            page.goto(args.base_url + "/group-management/group-rate-code", wait_until="networkidle")
            row = search_group_rate_code(page, rate_code)
            row.locator("button").filter(has_text="停用").first.click()
            page.locator(".ant-modal-confirm-btns .ant-btn-primary").click()
            page.get_by_text("停用成功", exact=True).wait_for(timeout=20000)
            stopped_row = search_group_rate_code(page, rate_code)
            assert "停用" in stopped_row.inner_text()
            stopped_row.locator("button").filter(has_text="启用").first.click()
            page.locator(".ant-modal-confirm-btns .ant-btn-primary").click()
            page.get_by_text("启用成功", exact=True).wait_for(timeout=20000)
            result["checks"].append("集团房价码停用和再启用")

            # 回收后酒店端保留记录但显示“已被集团回收”。
            open_group_edit(page, rate_code)
            page.get_by_role("tab", name="房价码分配").click()
            hotel_row = page.locator(".ant-table-tbody tr").filter(has_text=HOTEL_NAME).first
            hotel_row.wait_for(timeout=30000)
            allocated_switch = hotel_row.locator("button.ant-switch").nth(0)
            assert allocated_switch.get_attribute("aria-checked") == "true"
            allocated_switch.click()
            page.get_by_role("button", name="保存分配设置").click()
            page.wait_for_url(re.compile(r"/group-management/group-rate-code$"), timeout=30000)
            page.goto(args.base_url + "/rate-management/rate-plan", wait_until="networkidle")
            page.locator('input[placeholder="请输入价格计划代码"]').fill(rate_code)
            reclaimed_row = page.locator(".ant-table-tbody tr").filter(has_text=rate_code).first
            reclaimed_row.wait_for(timeout=30000)
            reclaimed_text = reclaimed_row.inner_text()
            assert "停用" in reclaimed_text and "已被集团回收" in reclaimed_text, reclaimed_text
            result["checks"].append("酒店价格计划回收后的来源和状态回显")

            delete_response = browser_request(
                page, f"/api/group-rate-codes/{group_rate_code_id}", "DELETE"
            )
            assert delete_response["status"] == 400, delete_response
            assert "不允许物理删除" in json.dumps(
                delete_response["payload"], ensure_ascii=False
            ) or "停用" in json.dumps(delete_response["payload"], ensure_ascii=False)
            result["expectedRejections"].append("集团房价码物理删除按业务规则被拒绝")

            page.screenshot(path="/private/tmp/crs-group-rate-code-lifecycle-final.png", full_page=True)
        except Exception as error:
            page.screenshot(path="/private/tmp/crs-group-rate-code-lifecycle-failure.png", full_page=True)
            result["errors"].append(f"test:{type(error).__name__}:{error}:url={page.url}")
        finally:
            browser.close()

    try:
        cleanup_counts = cleanup_fixture(project_dir, rate_code)
        result["cleanupCounts"] = cleanup_counts
        result["fixtureCleaned"] = all(count == 0 for count in cleanup_counts.values())
        if not result["fixtureCleaned"]:
            result["errors"].append(f"cleanup-residual:{cleanup_counts}")
    except Exception as cleanup_error:
        result["errors"].append(f"cleanup:{type(cleanup_error).__name__}:{cleanup_error}")

    if result["http5xx"]:
        result["errors"].append(f"http5xx:{result['http5xx']}")
    result["passed"] = not result["errors"]
    Path(args.output).write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False))
    return 0 if result["passed"] else 1


if __name__ == "__main__":
    raise SystemExit(main())

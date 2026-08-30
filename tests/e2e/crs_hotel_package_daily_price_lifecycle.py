"""CRS 酒店包价与每日价格真实页面全生命周期回归。"""

from __future__ import annotations

import calendar
import json
import re
import time
from datetime import date
from pathlib import Path
from typing import Any

from playwright.sync_api import Locator, Page, expect, sync_playwright

from crs_order_cancel_lifecycle import BASE_URL, login, request, run_sql, sql_text, unwrap


def item(page: Page, label: str) -> Locator:
    return page.locator(".ant-form-item").filter(
        has=page.locator(".ant-form-item-label").filter(has_text=label)
    ).first


def fill(page: Page, label: str, value: str) -> None:
    item(page, label).locator("input").fill(value)


def select(page: Page, label: str, option: str) -> None:
    target = item(page, label)
    target.locator(".ant-select-selector").click()
    dropdown = page.locator(".ant-select-dropdown:not(.ant-select-dropdown-hidden)").last
    expect(dropdown).to_be_visible(timeout=15000)
    dropdown.locator(".ant-select-item-option").filter(has_text=option).first.click(force=True)
    expect(target.locator(".ant-select-selection-item")).to_contain_text(option)
    expect(dropdown).to_be_hidden(timeout=5000)


def save_button(page: Page) -> Locator:
    return page.locator("button").filter(has_text=re.compile(r"保\s*存")).last


def cleanup(project_dir: Path, tenant_id: int, package_id: int | None, package_code: str) -> dict[str, int]:
    safe_code = sql_text(package_code)
    run_sql(
        project_dir,
        "DELETE FROM package_daily_prices "
        f"WHERE tenant_id={tenant_id} AND package_code='{safe_code}';"
        "DELETE FROM packages "
        f"WHERE tenant_id={tenant_id} AND code='{safe_code}';",
    )
    return {
        "package": int(run_sql(
            project_dir,
            f"SELECT COUNT(*) FROM packages WHERE tenant_id={tenant_id} AND code='{safe_code}';",
        ) or 0),
        "dailyPrice": int(run_sql(
            project_dir,
            f"SELECT COUNT(*) FROM package_daily_prices WHERE tenant_id={tenant_id} AND package_code='{safe_code}';",
        ) or 0),
    }


def main() -> None:
    project_dir = Path(__file__).resolve().parents[2]
    output = Path("/private/tmp/crs-hotel-package-daily-price-lifecycle.json")
    suffix = str(int(time.time()))[-8:]
    package_code = f"E2EPKG{suffix}"
    package_name = f"验收每日早餐{suffix}"
    edited_name = f"验收每日双早{suffix}"
    tenant_id = 0
    hotel_code = ""
    package_id: int | None = None
    result: dict[str, Any] = {
        "suffix": suffix, "checks": [], "expectedRejections": [], "errors": [],
        "http4xx": [], "http5xx": [], "cleanup": {},
    }
    stage = {"name": "启动"}

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1100})
        page.on("pageerror", lambda error: result["errors"].append(f"pageerror:{error}"))
        page.on(
            "console",
            lambda message: result["errors"].append(
                f"console:{stage['name']}:{page.url}:{message.text}"
            ) if message.type == "error" else None,
        )
        page.on(
            "response",
            lambda response: result["http5xx"].append(f"{response.status} {response.url}") if response.status >= 500
            else result["http4xx"].append(f"{response.status} {response.url}") if response.status >= 400 else None,
        )
        try:
            login(page)
            context = page.evaluate(
                """() => { const tenant = localStorage.getItem('crs_selected_tenant'); return {
                  tenantId: Number(tenant), hotelCode: localStorage.getItem(`crs_selected_hotel_${tenant}`)
                }; }"""
            )
            tenant_id = int(context["tenantId"])
            hotel_code = context["hotelCode"] or "JJSH001"

            page.goto(BASE_URL + "/rate-management/package-setting", wait_until="networkidle")
            stage["name"] = "新增表单"
            page.get_by_role("button", name="新增包价").click()
            page.wait_for_url("**/rate-management/add-package", timeout=15000)
            expect(page.get_by_role("heading", name="新增包价")).to_be_visible(timeout=15000)
            stage["name"] = "新增空表单校验"
            save_button(page).click()
            for label, text in (
                ("包价代码", "请输入包价代码"), ("包价名称", "请输入包价名称"),
                ("包价类型", "请选择包价类型"), ("发放频率", "请选择发放频率"),
                ("计数方式", "请选择计数方式"),
            ):
                expect(item(page, label).locator(".ant-form-item-explain-error")).to_have_text(text)
            result["checks"].append("包价新增基础字段必填校验")

            stage["name"] = "填写代码名称"
            fill(page, "包价代码", package_code)
            fill(page, "包价名称", package_name)
            stage["name"] = "选择包价类型"
            select(page, "包价类型", "早餐")
            stage["name"] = "选择发放频率"
            select(page, "发放频率", "每天1次")
            stage["name"] = "选择计数方式"
            select(page, "计数方式", "按房间")
            stage["name"] = "填写份数与计价"
            fill(page, "每房间份数", "2")
            page.get_by_role("radio", name="按日期设置价格").check()
            expect(item(page, "价格")).to_have_count(0)
            stage["name"] = "提交新增包价"
            with page.expect_response(
                lambda response: response.request.method == "POST" and response.url.endswith("/api/packages")
            ) as created:
                save_button(page).click()
            assert created.value.status == 201, created.value.text()
            package_id = int(created.value.json()["id"])
            page.wait_for_url(f"**/rate-management/edit-package?id={package_id}&tab=daily-price", timeout=15000)
            stage["name"] = "每日价格页"
            expect(page.get_by_role("tab", name="每日价格设置")).to_have_attribute("aria-selected", "true", timeout=15000)
            persisted = run_sql(
                project_dir,
                "SELECT CONCAT(name,'|',type,'|',frequency,'|',quantity_type,'|',fixed_quantity,'|',price_type,'|',COALESCE(fixed_price,'NULL')) "
                f"FROM packages WHERE id={package_id};",
            )
            assert persisted == f"{package_name}|早餐|daily|per_room|2|daily|NULL", persisted
            result["checks"].append("页面创建按日期包价并持久化标准频率、计数及 daily 模式")

            duplicate = request(page, "/api/packages", "POST", {
                "code": package_code, "name": "重复包价", "type": "早餐", "frequency": "daily",
                "quantityType": "fixed", "fixedQuantity": 1, "priceType": "daily", "status": "active",
            })
            assert duplicate["status"] == 400 and "已存在" in str(duplicate["payload"]), duplicate
            result["expectedRejections"].append("租户内重复包价代码返回 400")

            daily_inputs = page.locator(".ant-tabs-tabpane-active .ant-input-number-input")
            expect(daily_inputs.first).to_be_visible(timeout=15000)
            date_key = date.today().replace(day=1).isoformat()
            with page.expect_response(
                lambda response: response.request.method == "POST" and f"/api/packages/code/{package_code}/daily-prices" in response.url
            ) as single_saved:
                daily_inputs.first.fill("66.80")
                daily_inputs.first.blur()
            assert single_saved.value.status == 200, single_saved.value.text()
            assert run_sql(
                project_dir,
                "SELECT CAST(sale_price AS CHAR) FROM package_daily_prices "
                f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(hotel_code)}' "
                f"AND package_code='{sql_text(package_code)}' AND price_date='{date_key}';",
            ) == "66.80"

            with page.expect_response(
                lambda response: response.request.method == "POST" and f"/api/packages/code/{package_code}/daily-prices" in response.url
            ) as single_updated:
                daily_inputs.first.fill("77.90")
                daily_inputs.first.blur()
            assert single_updated.value.status == 200
            assert run_sql(
                project_dir,
                "SELECT CAST(sale_price AS CHAR) FROM package_daily_prices "
                f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(hotel_code)}' "
                f"AND package_code='{sql_text(package_code)}' AND price_date='{date_key}';",
            ) == "77.90"

            with page.expect_response(
                lambda response: response.request.method == "POST" and f"/api/packages/code/{package_code}/daily-prices" in response.url
            ) as single_deleted:
                daily_inputs.first.fill("")
                daily_inputs.first.blur()
            assert single_deleted.value.status == 200
            assert int(run_sql(
                project_dir,
                "SELECT COUNT(*) FROM package_daily_prices "
                f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(hotel_code)}' "
                f"AND package_code='{sql_text(package_code)}' AND price_date='{date_key}';",
            ) or 0) == 0
            result["checks"].append("每日价格单日新增、覆盖更新与清空删除")

            page.get_by_role("button", name="批量修改").click()
            stage["name"] = "批量弹窗"
            modal = page.locator(".ant-modal-content")
            expect(modal).to_be_visible()
            modal.locator(".ant-input-number-input").fill("88.88")
            with page.expect_response(
                lambda response: response.request.method == "POST" and f"/api/packages/code/{package_code}/daily-prices" in response.url
            ) as batch_saved:
                modal.get_by_role("button", name=re.compile(r"保\s*存")).click()
            assert batch_saved.value.status == 200, batch_saved.value.text()
            days_in_month = calendar.monthrange(date.today().year, date.today().month)[1]
            batch_count = int(run_sql(
                project_dir,
                "SELECT COUNT(*) FROM package_daily_prices "
                f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(hotel_code)}' "
                f"AND package_code='{sql_text(package_code)}' AND DATE_FORMAT(price_date,'%Y-%m')='{date.today():%Y-%m}';",
            ) or 0)
            assert batch_count == days_in_month, (batch_count, days_in_month)
            result["checks"].append("按整月日期段与全周控批量设置每日价格")

            page.get_by_role("tab", name="基础信息").click()
            stage["name"] = "编辑基础信息"
            expect(item(page, "包价代码").locator("input")).to_be_disabled()
            expect(item(page, "包价名称").locator("input")).to_have_value(package_name, timeout=15000)
            fill(page, "包价名称", edited_name)
            with page.expect_response(
                lambda response: response.request.method == "PUT" and response.url.endswith(f"/api/packages/{package_id}")
            ) as updated:
                save_button(page).click()
            assert updated.value.status == 200, updated.value.text()
            assert run_sql(project_dir, f"SELECT name FROM packages WHERE id={package_id};") == edited_name
            result["checks"].append("编辑回显、代码冻结及名称更新")

            hotels = unwrap(request(page, "/api/hotels")["payload"])
            other_hotel = next((hotel for hotel in hotels if hotel.get("hotelCode") != hotel_code), None)
            if other_hotel:
                other = request(
                    page,
                    f"/api/packages/code/{package_code}/daily-prices?hotelCode={other_hotel['hotelCode']}&month={date.today():%Y-%m}",
                )
                assert other["status"] == 200 and other["payload"].get("prices") == [], other
                result["checks"].append("包价每日价格按酒店隔离")

            invalid_month = request(
                page,
                f"/api/packages/code/{package_code}/daily-prices?hotelCode={hotel_code}&month=2026-99",
            )
            assert invalid_month["status"] == 400, invalid_month
            negative = request(page, f"/api/packages/code/{package_code}/daily-prices", "POST", {
                "hotelCode": hotel_code, "prices": [{"priceDate": date_key, "salePrice": -1}],
            })
            assert negative["status"] == 400 and "不能小于0" in str(negative["payload"]), negative
            anonymous = page.evaluate(
                """async ({code, hotelCode, month}) => {
                  const response = await fetch(`/api/packages/code/${code}/daily-prices?hotelCode=${hotelCode}&month=${month}`);
                  return response.status;
                }""",
                {"code": package_code, "hotelCode": hotel_code, "month": f"{date.today():%Y-%m}"},
            )
            assert anonymous == 401, anonymous
            result["expectedRejections"].extend([
                "非法月份返回 400", "负数每日价格返回 400", "匿名读取每日价格返回 401",
            ])

            run_sql(
                project_dir,
                "DELETE FROM package_daily_prices "
                f"WHERE tenant_id={tenant_id} AND package_code='{sql_text(package_code)}';",
            )
            deleted = request(page, f"/api/packages/{package_id}", "DELETE")
            assert deleted["status"] == 200, deleted
            result["checks"].append("删除孤立包价并验证关联价格已先清理")

            result["http4xx"] = [entry for entry in result["http4xx"] if not entry.startswith(("400 ", "401 "))]
            result["errors"] = [
                entry for entry in result["errors"]
                if "400 (Bad Request)" not in entry and "401 (Unauthorized)" not in entry
            ]
        except Exception as exc:
            result["errors"].append(f"test:{type(exc).__name__}:{exc}")
            page.screenshot(path="/private/tmp/crs-hotel-package-daily-price-lifecycle-failure.png", full_page=True)
            raise
        finally:
            if tenant_id:
                result["cleanup"] = cleanup(project_dir, tenant_id, package_id, package_code)
            browser.close()
            output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")

    assert result["errors"] == [], result["errors"]
    assert result["http4xx"] == [], result["http4xx"]
    assert result["http5xx"] == [], result["http5xx"]
    assert all(value == 0 for value in result["cleanup"].values()), result["cleanup"]
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()

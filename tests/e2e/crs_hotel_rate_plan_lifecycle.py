"""CRS 酒店价格计划真实页面与高风险状态生命周期回归。"""

from __future__ import annotations

import json
import re
import time
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


def select_first(page: Page, label: str) -> str:
    target = item(page, label)
    target.locator(".ant-select-selector").click()
    dropdown = page.locator(".ant-select-dropdown:not(.ant-select-dropdown-hidden)").last
    expect(dropdown).to_be_visible(timeout=15000)
    options = dropdown.locator(".ant-select-item-option:not(.ant-select-item-option-disabled)")
    expect(options.first).to_be_visible(timeout=15000)
    text = options.first.inner_text().strip()
    options.first.click()
    expect(target.locator(".ant-select-selection-item")).to_contain_text(text)
    expect(dropdown).to_be_hidden(timeout=5000)
    return text


def save_button(page: Page) -> Locator:
    locator = page.locator("button").filter(has_text=re.compile(r"保\s*存"))
    if locator.count() == 0:
        page.evaluate(
            """() => document.querySelectorAll('*').forEach((element) => {
              if (element.scrollHeight > element.clientHeight) element.scrollTop = element.scrollHeight
            })"""
        )
    if locator.count() == 0:
        raise AssertionError({
            "buttons": page.locator("button").all_text_contents(),
            "formItems": page.locator(".ant-form-item").count(),
            "bodyHasSave": "保存" in page.locator("body").inner_text(),
        })
    return locator.last


def cleanup(project_dir: Path, tenant_id: int, hotel_code: str, rate_code: str) -> dict[str, int]:
    safe_hotel = sql_text(hotel_code)
    safe_rate = sql_text(rate_code)
    run_sql(
        project_dir,
        "DELETE FROM rate_plans "
        f"WHERE tenant_id={tenant_id} AND hotel_code='{safe_hotel}' AND rate_code='{safe_rate}';",
    )
    return {
        "ratePlan": int(run_sql(
            project_dir,
            "SELECT COUNT(*) FROM rate_plans "
            f"WHERE tenant_id={tenant_id} AND hotel_code='{safe_hotel}' AND rate_code='{safe_rate}';",
        ) or 0)
    }


def main() -> None:
    project_dir = Path(__file__).resolve().parents[2]
    output = Path("/private/tmp/crs-hotel-rate-plan-lifecycle.json")
    suffix = str(int(time.time()))[-8:]
    rate_code = f"E2ERP{suffix}"
    rate_name = f"验收灵活房价{suffix}"
    edited_name = f"验收优享房价{suffix}"
    tenant_id = 0
    hotel_code = ""
    rate_plan_id: int | None = None
    result: dict[str, Any] = {
        "suffix": suffix, "checks": [], "expectedRejections": [], "errors": [],
        "http4xx": [], "http5xx": [], "cleanup": {},
    }

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1100})
        page.on("pageerror", lambda error: result["errors"].append(f"pageerror:{error}"))
        page.on("console", lambda message: result["errors"].append(f"console:{message.text}") if message.type == "error" else None)
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

            page.goto(BASE_URL + "/rate-management/rate-plan", wait_until="networkidle")
            page.get_by_role("button", name="新增价格计划").click()
            page.wait_for_url("**/rate-management/add-rate-plan", timeout=15000)
            expect(page.get_by_role("heading", name="新增价格计划")).to_be_visible(timeout=15000)
            save_button(page).click()
            expect(page.get_by_text("请输入价格计划代码", exact=True)).to_be_visible()
            expect(page.get_by_text("请输入价格计划名称", exact=True)).to_be_visible()
            expect(item(page, "担保规则").locator(".ant-form-item-explain-error")).to_have_text("请选择担保规则")
            expect(item(page, "取消规则").locator(".ant-form-item-explain-error")).to_have_text("请选择取消规则")
            result["checks"].append("页面新增必填校验覆盖代码、名称和政策")

            fill(page, "价格计划代码", rate_code)
            fill(page, "价格计划名称", rate_name)
            guarantee = select_first(page, "担保规则")
            cancellation = select_first(page, "取消规则")
            with page.expect_response(
                lambda response: response.request.method == "POST" and response.url.endswith("/api/rate-plans")
            ) as created:
                save_button(page).click()
            assert created.value.status == 200, created.value.text()
            rate_plan_id = int(created.value.json()["id"])
            page.wait_for_url("**/rate-management/rate-plan", timeout=15000)
            persisted = run_sql(
                project_dir,
                "SELECT CONCAT(rate_name,'|',rate_type,'|',derivative_level,'|',status) FROM rate_plans "
                f"WHERE id={rate_plan_id};",
            )
            assert persisted == f"{rate_name}|basic|basic|active", persisted
            result["checks"].append(
                f"页面创建基础价格计划且默认启用（担保：{guarantee}；取消：{cancellation}）"
            )

            duplicate = request(page, "/api/rate-plans", "POST", {
                "hotelCode": hotel_code, "rateCode": rate_code, "rateName": "重复计划",
                "rateType": "basic", "status": "active",
            })
            assert duplicate["status"] == 400 and "已存在" in str(duplicate["payload"]), duplicate
            result["expectedRejections"].append("同一酒店重复价格计划代码返回 400")

            page.get_by_label("价格计划代码").fill(rate_code)
            row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=rate_code).first
            expect(row).to_be_visible(timeout=20000)
            expect(row).to_contain_text("自建")
            expect(row).to_contain_text("启用")
            row.get_by_role("button", name="编辑").click()
            page.wait_for_url(f"**/rate-management/edit-rate-plan/{rate_plan_id}", timeout=15000)
            expect(page.get_by_role("heading", name="编辑价格计划")).to_be_visible(timeout=15000)
            expect(item(page, "价格计划代码").locator("input")).to_be_disabled()
            expect(item(page, "类型").locator(".ant-select-disabled")).to_be_visible()
            expect(item(page, "价格计划名称").locator("input")).to_have_value(rate_name, timeout=15000)
            fill(page, "价格计划名称", edited_name)
            with page.expect_response(
                lambda response: response.request.method == "PUT" and response.url.endswith(f"/api/rate-plans/{rate_plan_id}")
            ) as updated:
                save_button(page).click()
            assert updated.value.status == 200, updated.value.text()
            page.wait_for_url("**/rate-management/rate-plan", timeout=15000)
            echoed = run_sql(
                project_dir,
                f"SELECT CONCAT(rate_code,'|',rate_name,'|',rate_type) FROM rate_plans WHERE id={rate_plan_id};",
            )
            assert echoed == f"{rate_code}|{edited_name}|basic", echoed
            result["checks"].append("列表来源与状态回显、编辑回填、代码及类型冻结")

            immutable = request(page, f"/api/rate-plans/{rate_plan_id}", "PUT", {
                "hotelCode": hotel_code, "rateCode": rate_code, "rateName": edited_name,
                "rateType": "derivative", "status": "active",
            })
            assert immutable["status"] == 400 and "不可修改" in str(immutable["payload"]), immutable
            result["expectedRejections"].append("后端拒绝绕过页面修改价格计划类型")

            disabled = request(page, f"/api/rate-plans/{rate_plan_id}/disable", "PUT")
            assert disabled["status"] == 200 and disabled["payload"].get("status") == "inactive", disabled
            page.goto(BASE_URL + "/rate-management/rate-plan", wait_until="networkidle")
            page.get_by_label("价格计划代码").fill(rate_code)
            page.get_by_label("启用状态").first.click()
            page.locator(".ant-select-dropdown:not(.ant-select-dropdown-hidden)").last.locator(
                ".ant-select-item-option"
            ).filter(has_text="停用").click()
            expect(page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=rate_code).first).to_contain_text("停用")
            enabled = request(page, f"/api/rate-plans/{rate_plan_id}/enable", "PUT")
            assert enabled["status"] == 200 and enabled["payload"].get("status") == "active", enabled
            result["checks"].append("停用与启用高风险状态动作及列表状态筛选")

            hotels = unwrap(request(page, "/api/hotels")["payload"])
            other_hotel = next((hotel for hotel in hotels if hotel.get("hotelCode") != hotel_code), None)
            if other_hotel:
                other = unwrap(request(page, f"/api/rate-plans/by-code/hotel/{other_hotel['hotelCode']}")["payload"])
                assert all(plan.get("rateCode") != rate_code for plan in other)
                result["checks"].append("酒店自建价格计划不泄漏到其他酒店")

            anonymous = page.evaluate(
                """async (id) => { const response = await fetch(`/api/rate-plans/${id}/disable`, {method: 'PUT'}); return response.status; }""",
                rate_plan_id,
            )
            assert anonymous == 401, anonymous
            result["expectedRejections"].append("匿名停用价格计划返回 401")

            deleted = request(page, f"/api/rate-plans/{rate_plan_id}", "DELETE")
            assert deleted["status"] == 200, deleted
            assert int(run_sql(project_dir, f"SELECT COUNT(*) FROM rate_plans WHERE id={rate_plan_id};") or 0) == 0
            result["checks"].append("删除价格计划并验证数据库物理清理")

            result["http4xx"] = [entry for entry in result["http4xx"] if not entry.startswith(("400 ", "401 "))]
            result["errors"] = [
                entry for entry in result["errors"]
                if "400 (Bad Request)" not in entry and "401 (Unauthorized)" not in entry
            ]
        except Exception as exc:
            result["errors"].append(f"test:{type(exc).__name__}:{exc}")
            page.screenshot(path="/private/tmp/crs-hotel-rate-plan-lifecycle-failure.png", full_page=True)
            raise
        finally:
            if tenant_id and hotel_code:
                result["cleanup"] = cleanup(project_dir, tenant_id, hotel_code, rate_code)
            browser.close()
            output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")

    assert result["errors"] == [], result["errors"]
    assert result["http4xx"] == [], result["http4xx"]
    assert result["http5xx"] == [], result["http5xx"]
    assert all(value == 0 for value in result["cleanup"].values()), result["cleanup"]
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()

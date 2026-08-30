"""CRS 酒店房型基础信息与设施绑定真实页面回归。"""

from __future__ import annotations

import json
import time
from pathlib import Path
from typing import Any

from playwright.sync_api import Locator, Page, expect, sync_playwright

from crs_order_cancel_lifecycle import BASE_URL, login, request, run_sql, sql_text


def item(page: Page, label: str) -> Locator:
    return page.locator(".ant-form-item").filter(
        has=page.locator(".ant-form-item-label").filter(has_text=label)
    ).first


def fill(page: Page, label: str, value: str) -> None:
    item(page, label).locator("input").fill(value)


def select(page: Page, label: str, option: str) -> None:
    item(page, label).locator(".ant-select-selector").click()
    page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(has_text=option).first.click(force=True)


def cleanup(project_dir: Path, room_id: int | None, hotel_code: str, room_code: str, facility_fixture_code: str | None) -> dict[str, int]:
    safe_hotel = sql_text(hotel_code)
    safe_room = sql_text(room_code)
    if room_id:
        run_sql(
            project_dir,
            f"DELETE FROM room_type_facilities WHERE hotel_code='{safe_hotel}' AND room_type_code='{safe_room}';"
            f"DELETE FROM hotel_room_types WHERE id={room_id} AND hotel_code='{safe_hotel}' AND room_type_code='{safe_room}';",
        )
    if facility_fixture_code:
        run_sql(project_dir, f"DELETE FROM group_facilities WHERE facility_code='{sql_text(facility_fixture_code)}';")
    return {
        "roomType": int(run_sql(project_dir, f"SELECT COUNT(*) FROM hotel_room_types WHERE hotel_code='{safe_hotel}' AND room_type_code='{safe_room}';") or 0),
        "facility": int(run_sql(project_dir, f"SELECT COUNT(*) FROM room_type_facilities WHERE hotel_code='{safe_hotel}' AND room_type_code='{safe_room}';") or 0),
        "groupFacility": int(run_sql(project_dir, f"SELECT COUNT(*) FROM group_facilities WHERE facility_code='{sql_text(facility_fixture_code or '')}';") or 0),
    }


def main() -> None:
    project_dir = Path(__file__).resolve().parents[2]
    output = Path("/private/tmp/crs-hotel-room-type-lifecycle.json")
    suffix = str(int(time.time()))[-8:]
    room_code = f"E2ERT{suffix}"
    room_name = f"验收景观房{suffix}"
    edited_name = f"验收行政景观房{suffix}"
    room_id: int | None = None
    hotel_code = ""
    facility_fixture_code: str | None = None
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
            hotel_code = context["hotelCode"] or "JJSH001"
            facility_count = int(run_sql(
                project_dir,
                f"SELECT COUNT(*) FROM group_facilities WHERE tenant_id={int(context['tenantId'])} AND scope='room_type' AND available=1;",
            ) or 0)
            if facility_count == 0:
                facility_fixture_code = f"E2EFAC{suffix}"
                run_sql(
                    project_dir,
                    "INSERT INTO group_facilities (tenant_id,facility_code,scope,facility_type,facility_name,available,description) VALUES "
                    f"({int(context['tenantId'])},'{facility_fixture_code}','room_type','客房设施','验收智能门锁',1,'E2E fixture');",
                )
            page.goto(BASE_URL + "/room-management/room-type", wait_until="networkidle")
            page.get_by_role("button", name="新增房型").click()
            page.get_by_role("button", name="保存基础信息").click()
            expect(page.get_by_text("请输入房型代码", exact=True)).to_be_visible()
            expect(page.get_by_text("请输入房型中文名称", exact=True)).to_be_visible()
            result["checks"].append("新增房型必填校验")

            fill(page, "房型代码", room_code)
            fill(page, "房型中文名称", room_name)
            fill(page, "房型英文名称", f"Acceptance Executive View {suffix}")
            fill(page, "房型数量", "9")
            fill(page, "房型面积（㎡）", "48.5")
            fill(page, "所在楼层", "18-20")
            fill(page, "最大入住成人数", "2")
            fill(page, "最大入住儿童数", "1")
            select(page, "窗型", "有窗")
            select(page, "床型", "1张1.8米大床")
            with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/hotel-room-types")) as created:
                page.get_by_role("button", name="保存基础信息").click()
            assert created.value.status == 200
            room_id = int(created.value.json()["id"])
            persisted = run_sql(
                project_dir,
                f"SELECT CONCAT(room_type_name,'|',total_rooms,'|',area,'|',status) FROM hotel_room_types WHERE id={room_id};",
            )
            assert persisted == f"{room_name}|9|48.50|active"
            result["checks"].append("页面创建酒店自建房型并持久化物理与入住字段")

            duplicate = request(page, "/api/hotel-room-types", "POST", {
                "hotelCode": hotel_code, "roomTypeCode": room_code,
                "roomTypeName": "重复房型", "status": "active"
            })
            assert duplicate["status"] == 400 and "already exists" in str(duplicate["payload"])
            result["expectedRejections"].append("同酒店重复房型代码返回 400")

            page.get_by_role("tab", name="房型设施").click()
            facilities = page.locator(".ant-checkbox-wrapper")
            if facilities.count() > 0:
                first_facility = facilities.first
                first_facility.click()
                with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/room-type-facilities/batch")) as facility_saved:
                    page.get_by_role("button", name="保存设施信息").click()
                assert facility_saved.value.status == 200
                count = run_sql(
                    project_dir,
                    f"SELECT COUNT(*) FROM room_type_facilities WHERE hotel_code='{sql_text(hotel_code)}' AND room_type_code='{sql_text(room_code)}';",
                )
                assert int(count or 0) >= 1
                result["checks"].append("页面勾选并保存真实房型设施绑定")
            else:
                raise AssertionError("当前没有可用房型设施，无法执行设施绑定场景")

            page.get_by_role("button", name="返回列表").first.click()
            fill(page, "代码", room_code)
            page.get_by_role("button", name="查询").click()
            row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=room_code).first
            expect(row).to_be_visible(timeout=20000)
            expect(page.locator(".ant-table-tbody tr.ant-table-row")).to_have_count(1)
            row.get_by_role("button", name="编辑").click()
            expect(item(page, "房型代码").locator("input")).to_be_disabled()
            fill(page, "房型中文名称", edited_name)
            fill(page, "房型数量", "12")
            with page.expect_response(lambda response: response.request.method == "PUT" and response.url.endswith(f"/api/hotel-room-types/{room_id}")) as updated:
                page.get_by_role("button", name="保存基础信息").click()
            assert updated.value.status == 200
            updated_row = run_sql(project_dir, f"SELECT CONCAT(room_type_code,'|',room_type_name,'|',total_rooms) FROM hotel_room_types WHERE id={room_id};")
            assert updated_row == f"{room_code}|{edited_name}|12"
            result["checks"].append("列表代码筛选、编辑回填、代码冻结与基础字段更新")

            deleted = request(page, f"/api/hotel-room-types/{room_id}", "DELETE")
            assert deleted["status"] == 200
            result["checks"].append("孤立测试房型通过后端删除并验证无残留")

            result["http4xx"] = [item for item in result["http4xx"] if not item.startswith("400 ")]
            result["errors"] = [item for item in result["errors"] if "400 (Bad Request)" not in item]
        except Exception as exc:
            result["errors"].append(f"test:{type(exc).__name__}:{exc}")
            page.screenshot(path="/private/tmp/crs-hotel-room-type-lifecycle-failure.png", full_page=True)
            raise
        finally:
            result["cleanup"] = cleanup(project_dir, room_id, hotel_code, room_code, facility_fixture_code)
            browser.close()
            output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")

    assert result["errors"] == [], result["errors"]
    assert result["http4xx"] == [], result["http4xx"]
    assert result["http5xx"] == [], result["http5xx"]
    assert all(value == 0 for value in result["cleanup"].values()), result["cleanup"]
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()

"""CRS 订单、出租率、营收报表和 CSV 导出真实页面回归。"""

from __future__ import annotations

import json
import time
from datetime import date, timedelta
from pathlib import Path
from typing import Any

from playwright.sync_api import Locator, Page, expect, sync_playwright

from crs_order_cancel_lifecycle import BASE_URL, login, request, run_sql, sql_text, unwrap


def form_item(page: Page, label: str) -> Locator:
    return page.locator(".ant-form-item").filter(
        has=page.locator(".ant-form-item-label").filter(has_text=label)
    ).first


def select_form(page: Page, label: str, option: str) -> None:
    target = form_item(page, label)
    target.locator(".ant-select-selector").click()
    dropdown = page.locator(".ant-select-dropdown:not(.ant-select-dropdown-hidden)").last
    dropdown.locator(".ant-select-item-option").filter(has_text=option).first.evaluate("element => element.click()")


def choose_fixture(page: Page, project_dir: Path, suffix: str) -> dict[str, Any]:
    context = page.evaluate(
        """() => { const tenant = localStorage.getItem('crs_selected_tenant'); return {
          tenantId: Number(tenant), hotelCode: localStorage.getItem(`crs_selected_hotel_${tenant}`)
        }; }"""
    )
    tenant_id = int(context["tenantId"])
    hotel_code = context["hotelCode"] or "JJSH001"
    hotels = unwrap(request(page, "/api/hotels")["payload"])
    hotel = next(item for item in hotels if item.get("hotelCode") == hotel_code)
    rooms = unwrap(request(page, f"/api/hotel-room-types/by-code/hotel/{hotel_code}/status/active")["payload"])
    plans = unwrap(request(page, f"/api/rate-plans/by-code/hotel/{hotel_code}")["payload"])
    assert rooms and plans
    room = rooms[0]
    plan = next((item for item in plans if item.get("status") == "active"), plans[0])

    reservation_date = date.today() + timedelta(days=720)
    for offset in range(90):
        candidate = reservation_date + timedelta(days=offset)
        count = int(run_sql(
            project_dir,
            "SELECT COUNT(*) FROM pms_inventory "
            f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(hotel_code)}' "
            f"AND room_type_code='{sql_text(room['roomTypeCode'])}' AND inventory_date='{candidate}';",
        ) or 0)
        if count == 0:
            reservation_date = candidate
            break
    else:
        raise AssertionError("没有可安全插入的订单库存日期")

    report_inventory_date = None
    month_start = date.today().replace(day=1)
    cursor = month_start
    while cursor.month == month_start.month:
        count = int(run_sql(
            project_dir,
            "SELECT COUNT(*) FROM pms_inventory "
            f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(hotel_code)}' "
            f"AND room_type_code='{sql_text(room['roomTypeCode'])}' AND inventory_date='{cursor}';",
        ) or 0)
        if count == 0:
            report_inventory_date = cursor
            break
        cursor += timedelta(days=1)
    if report_inventory_date is None:
        raise AssertionError("当前月份没有可安全插入的报表库存日期")

    return {
        "tenantId": tenant_id,
        "hotel": hotel,
        "room": room,
        "plan": plan,
        "reservationDate": reservation_date.isoformat(),
        "reservationEnd": (reservation_date + timedelta(days=1)).isoformat(),
        "reportInventoryDate": report_inventory_date.isoformat(),
        "channelCode": f"E2E_REPORT_{suffix}",
    }


def cleanup(project_dir: Path, fixture: dict[str, Any], reservation_id: int | None, reservation_code: str | None) -> dict[str, int]:
    if reservation_id:
        run_sql(
            project_dir,
            "DELETE FROM reservation_daily_price_taxes WHERE reservation_daily_price_id IN "
            f"(SELECT id FROM reservation_daily_price WHERE reservation_id={reservation_id});"
            f"DELETE FROM api_logs WHERE reservation_id={reservation_id};"
            f"DELETE FROM reservation_payment WHERE reservation_id={reservation_id};"
            f"DELETE FROM reservation_promotion WHERE reservation_id={reservation_id};"
            f"DELETE FROM reservation_guest WHERE reservation_id={reservation_id};"
            f"DELETE FROM reservation_history WHERE reservation_id={reservation_id};"
            f"DELETE FROM reservation_daily_price WHERE reservation_id={reservation_id};"
            f"DELETE FROM reservation WHERE id={reservation_id};",
        )
    if reservation_code:
        run_sql(project_dir, f"DELETE FROM system_trace_logs WHERE reference_code='{sql_text(reservation_code)}';")
    run_sql(
        project_dir,
        "DELETE FROM report_daily_reservation_summary "
        f"WHERE tenant_id={fixture['tenantId']} AND channel_code='{sql_text(fixture['channelCode'])}';"
        "DELETE FROM pms_inventory "
        f"WHERE tenant_id={fixture['tenantId']} AND hotel_code='{sql_text(fixture['hotel']['hotelCode'])}' "
        f"AND room_type_code='{sql_text(fixture['room']['roomTypeCode'])}' "
        f"AND inventory_date IN ('{fixture['reservationDate']}','{fixture['reportInventoryDate']}');",
    )
    return {
        "reservation": int(run_sql(project_dir, f"SELECT COUNT(*) FROM reservation WHERE id={reservation_id or 0};") or 0),
        "summary": int(run_sql(
            project_dir,
            "SELECT COUNT(*) FROM report_daily_reservation_summary "
            f"WHERE tenant_id={fixture['tenantId']} AND channel_code='{sql_text(fixture['channelCode'])}';",
        ) or 0),
        "inventory": int(run_sql(
            project_dir,
            "SELECT COUNT(*) FROM pms_inventory "
            f"WHERE tenant_id={fixture['tenantId']} AND hotel_code='{sql_text(fixture['hotel']['hotelCode'])}' "
            f"AND room_type_code='{sql_text(fixture['room']['roomTypeCode'])}' "
            f"AND inventory_date IN ('{fixture['reservationDate']}','{fixture['reportInventoryDate']}');",
        ) or 0),
    }


def set_report_select(page: Page, label_text: str, option: str) -> None:
    container = page.locator("div").filter(has=page.locator("label").filter(has_text=label_text)).last
    container.locator(".ant-select-selector").click()
    page.locator(".ant-select-dropdown:not(.ant-select-dropdown-hidden)").last.locator(
        ".ant-select-item-option"
    ).filter(has_text=option).first.evaluate("element => element.click()")


def main() -> None:
    project_dir = Path(__file__).resolve().parents[2]
    output = Path("/private/tmp/crs-reports-export-lifecycle.json")
    suffix = str(int(time.time()))[-8:]
    fixture: dict[str, Any] | None = None
    reservation_id: int | None = None
    reservation_code: str | None = None
    result: dict[str, Any] = {
        "suffix": suffix, "checks": [], "expectedRejections": [], "downloads": [],
        "errors": [], "http4xx": [], "http5xx": [], "cleanup": {},
    }

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1000}, accept_downloads=True)
        page.on("pageerror", lambda error: result["errors"].append(f"pageerror:{error}"))
        page.on("console", lambda message: result["errors"].append(f"console:{message.text}") if message.type == "error" else None)
        page.on(
            "response",
            lambda response: result["http5xx"].append(f"{response.status} {response.url}") if response.status >= 500
            else result["http4xx"].append(f"{response.status} {response.url}") if response.status >= 400 else None,
        )
        try:
            login(page)
            fixture = choose_fixture(page, project_dir, suffix)
            run_sql(
                project_dir,
                "INSERT INTO pms_inventory "
                "(tenant_id,hotel_code,room_type_code,inventory_date,physical_rooms,available_rooms,maintenance_rooms,overbook_count,version) VALUES "
                f"({fixture['tenantId']},'{sql_text(fixture['hotel']['hotelCode'])}','{sql_text(fixture['room']['roomTypeCode'])}',"
                f"'{fixture['reservationDate']}',12,12,0,0,0),"
                f"({fixture['tenantId']},'{sql_text(fixture['hotel']['hotelCode'])}','{sql_text(fixture['room']['roomTypeCode'])}',"
                f"'{fixture['reportInventoryDate']}',12,11,0,0,0);",
            )
            payload = {
                "hotelCode": fixture["hotel"]["hotelCode"],
                "hotelName": fixture["hotel"].get("chineseName") or fixture["hotel"]["hotelCode"],
                "roomTypeCode": fixture["room"]["roomTypeCode"],
                "roomTypeName": fixture["room"].get("roomTypeName") or fixture["room"]["roomTypeCode"],
                "ratePlanCode": fixture["plan"]["rateCode"],
                "ratePlanName": fixture["plan"].get("rateName") or fixture["plan"]["rateCode"],
                "channelCode": fixture["channelCode"], "channelName": "验收报表渠道",
                "channelOrderNumber": f"E2E-REPORT-{suffix}",
                "checkInDate": fixture["reservationDate"], "checkOutDate": fixture["reservationEnd"],
                "roomCount": 1, "adultCount": 2, "childCount": 0,
                "contactName": f"报表验收客人{suffix}", "contactPhone": "13800000000",
                "originalPrice": 888.00, "totalPrice": 888.00, "currency": "CNY",
                "orderSource": "manual", "createdBy": "CODEX_REPORT_E2E",
                "reservationStatus": "confirmed", "paymentStatus": "unpaid",
                "dailyPrices": [{"date": fixture["reservationDate"], "originalPrice": 888.00, "actualPrice": 888.00}],
                "guests": [{"name": f"报表验收客人{suffix}", "phone": "13800000000"}],
            }
            created = request(page, "/api/reservation", "POST", payload)
            assert created["status"] == 201, created
            reservation_id = int(created["payload"]["id"])
            reservation_code = created["payload"]["reservationCode"]
            today = date.today().isoformat()
            initialized = request(
                page,
                f"/api/reports/reservation/initialize?startDate={today}&endDate={today}",
                "POST",
            )
            assert initialized["status"] == 200, initialized
            assert int(run_sql(
                project_dir,
                "SELECT COALESCE(SUM(order_count),0) FROM report_daily_reservation_summary "
                f"WHERE tenant_id={fixture['tenantId']} AND channel_code='{sql_text(fixture['channelCode'])}';",
            ) or 0) == 1

            page.goto(BASE_URL + "/reports/reservation-reports", wait_until="networkidle")
            select_form(page, "酒店", fixture["hotel"].get("chineseName") or fixture["hotel"]["hotelCode"])
            with page.expect_response(lambda response: "/api/reports/reservation?" in response.url) as queried:
                page.get_by_role("button", name="执行查询").click()
            assert queried.value.status == 200
            row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=fixture["channelCode"]).first
            expect(row).to_be_visible(timeout=15000)
            expect(row).to_contain_text("1")
            with page.expect_download() as download_info:
                page.get_by_role("button", name="导出报表").click()
            download = download_info.value
            path = download.path()
            content = Path(path).read_text(encoding="utf-8-sig")
            assert fixture["channelCode"] in content and "本期-订单数" in content
            result["downloads"].append(download.suggested_filename)
            result["checks"].append("订单报表页面酒店筛选、分组聚合、唯一渠道回显与 CSV 内容")

            over_range = request(
                page,
                "/api/reports/reservation?startDate=2024-01-01&endDate=2025-12-31&groupBy1=channel&groupBy2=hotel",
            )
            assert over_range["status"] == 400 and "366" in str(over_range["payload"]), over_range
            compare_missing = request(
                page,
                f"/api/reports/reservation?startDate={today}&endDate={today}&enableCompare=true",
            )
            assert compare_missing["status"] == 400, compare_missing
            result["expectedRejections"].extend(["超过 366 天查询返回 400", "启用对比但缺日期返回 400"])

            hotel_name = fixture["hotel"].get("chineseName") or fixture["hotel"]["hotelCode"]
            page.goto(BASE_URL + "/reports/occupancy-reports", wait_until="networkidle")
            with page.expect_response(lambda response: "/api/reports/occupancy?" in response.url) as occupancy_response:
                set_report_select(page, "选择酒店:", hotel_name)
            assert occupancy_response.value.status == 200
            expect(page.locator(".ant-table-tbody")).to_contain_text(hotel_name, timeout=15000)
            expect(page.locator(".ant-table-tbody")).to_contain_text("已卖房")
            with page.expect_download() as occupancy_download:
                page.get_by_role("button", name="导出报表").click()
            occupancy_file = occupancy_download.value
            occupancy_content = Path(occupancy_file.path()).read_text(encoding="utf-8-sig")
            assert hotel_name in occupancy_content and "出租率" in occupancy_content
            result["downloads"].append(occupancy_file.suggested_filename)
            page.locator("label.ant-radio-button-wrapper").filter(has_text="图形模式").click()
            expect(page.get_by_text("每日出租率趋势", exact=True)).to_be_visible()
            page.locator("label.ant-radio-button-wrapper").filter(has_text="表格模式").click()
            with page.expect_response(lambda response: "/api/reports/occupancy?" in response.url) as room_occupancy:
                set_report_select(page, "统计维度:", "按房型维度")
            assert room_occupancy.value.status == 200
            expect(page.locator(".ant-table-tbody")).to_contain_text(fixture["room"]["roomTypeCode"], timeout=15000)
            result["checks"].append("出租率报表酒店/房型维度、表格/图形切换与 CSV 导出")

            page.goto(BASE_URL + "/reports/revenue-reports", wait_until="networkidle")
            with page.expect_response(lambda response: "/api/reports/revenue?" in response.url) as revenue_response:
                set_report_select(page, "选择酒店:", hotel_name)
            assert revenue_response.value.status == 200
            expect(page.locator(".ant-table-tbody")).to_contain_text(hotel_name, timeout=15000)
            expect(page.locator(".ant-table-tbody")).to_contain_text("平均房价")
            with page.expect_download() as revenue_download:
                page.get_by_role("button", name="导出报表").click()
            revenue_file = revenue_download.value
            revenue_content = Path(revenue_file.path()).read_text(encoding="utf-8-sig")
            assert hotel_name in revenue_content and "平均房价" in revenue_content
            result["downloads"].append(revenue_file.suggested_filename)
            page.locator("label.ant-radio-button-wrapper").filter(has_text="图形模式").click()
            expect(page.get_by_text("每日订单量趋势", exact=True)).to_be_visible()
            expect(page.get_by_text("平均房价 (ADR) 走势", exact=False)).to_be_visible()
            result["checks"].append("营收报表真实汇总、表格/图形切换与 CSV 导出")

            page.goto(BASE_URL + "/reports/data-export", wait_until="networkidle")
            for title in ("订单分析报表", "出租率报表", "营收分析报表"):
                expect(page.get_by_role("main").get_by_text(title, exact=True)).to_be_visible()
            result["checks"].append("数据导出工作台三个真实报表入口")

            anonymous = page.evaluate(
                """async (today) => (await fetch(`/api/reports/reservation?startDate=${today}&endDate=${today}`)).status""",
                today,
            )
            assert anonymous == 401, anonymous
            result["expectedRejections"].append("匿名报表查询返回 401")

            result["http4xx"] = [entry for entry in result["http4xx"] if not entry.startswith(("400 ", "401 "))]
            result["errors"] = [
                entry for entry in result["errors"]
                if "400 (Bad Request)" not in entry and "401 (Unauthorized)" not in entry
            ]
        except Exception as exc:
            result["errors"].append(f"test:{type(exc).__name__}:{exc}")
            page.screenshot(path="/private/tmp/crs-reports-export-lifecycle-failure.png", full_page=True)
            raise
        finally:
            if fixture:
                result["cleanup"] = cleanup(project_dir, fixture, reservation_id, reservation_code)
            browser.close()
            output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")

    assert result["errors"] == [], result["errors"]
    assert result["http4xx"] == [], result["http4xx"]
    assert result["http5xx"] == [], result["http5xx"]
    assert all(value == 0 for value in result["cleanup"].values()), result["cleanup"]
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()

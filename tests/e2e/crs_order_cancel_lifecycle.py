"""CRS 订单取消、库存释放与操作审计真实页面回归。"""

from __future__ import annotations

import json
import os
import subprocess
import time
from datetime import date, timedelta
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

from playwright.sync_api import Page, expect, sync_playwright


BASE_URL = "http://127.0.0.1:3001"


def read_env(path: Path) -> dict[str, str]:
    values: dict[str, str] = {}
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            values[key] = value
    return values


def run_sql(project_dir: Path, sql: str) -> str:
    values = read_env(project_dir / ".env.local")
    parsed = urlparse(values["CRS_DB_URL"].removeprefix("jdbc:"))
    env = os.environ.copy()
    env["MYSQL_PWD"] = values["CRS_DB_PASSWORD"]
    completed = subprocess.run(
        [
            "mysql", "-h", parsed.hostname or "127.0.0.1", "-P", str(parsed.port or 3306),
            "-u", values["CRS_DB_USERNAME"], "-N", parsed.path.lstrip("/") or "CRS", "-e", sql,
        ],
        check=True, capture_output=True, text=True, env=env,
    )
    return completed.stdout.strip()


def sql_text(value: Any) -> str:
    return str(value).replace("\\", "\\\\").replace("'", "''")


def request(page: Page, path: str, method: str = "GET", body: Any = None) -> dict[str, Any]:
    return page.evaluate(
        """async ({path, method, body}) => {
          const response = await fetch(path, {
            method,
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${localStorage.getItem('crs_token')}`,
              'X-Tenant-Id': localStorage.getItem('crs_selected_tenant')
            },
            body: body === null ? undefined : JSON.stringify(body)
          });
          const payload = await response.json().catch(() => ({}));
          return {status: response.status, payload};
        }""",
        {"path": path, "method": method, "body": body},
    )


def unwrap(payload: Any) -> list[dict[str, Any]]:
    if isinstance(payload, list):
        return payload
    if isinstance(payload, dict) and isinstance(payload.get("data"), list):
        return payload["data"]
    if isinstance(payload, dict) and isinstance(payload.get("content"), list):
        return payload["content"]
    return []


def login(page: Page) -> None:
    page.goto(BASE_URL + "/login", wait_until="networkidle")
    page.locator('input[autocomplete="username"]').fill(os.environ.get("CRS_TEST_USERNAME", "admin"))
    page.locator('input[autocomplete="current-password"]').fill(os.environ.get("CRS_TEST_PASSWORD", "admin123"))
    page.get_by_role("button", name="进入工作台").click()
    page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
    page.wait_for_function("() => Boolean(localStorage.getItem('crs_selected_tenant'))", timeout=30000)


def choose_fixture(page: Page, project_dir: Path, suffix: str) -> dict[str, Any]:
    context = page.evaluate(
        """() => { const tenantId = localStorage.getItem('crs_selected_tenant'); return {
          tenantId: Number(tenantId), hotelCode: localStorage.getItem(`crs_selected_hotel_${tenantId}`)
        }; }"""
    )
    hotel_code = context["hotelCode"] or "JJSH001"
    hotels = unwrap(request(page, "/api/hotels?status=active&page=0&size=200")["payload"])
    hotel = next((item for item in hotels if item.get("hotelCode") == hotel_code), None)
    if hotel is None:
        hotel = {"hotelCode": hotel_code, "hotelName": hotel_code}
    rooms = unwrap(request(page, f"/api/hotel-room-types/by-code/hotel/{hotel_code}/status/active")["payload"])
    plans = unwrap(request(page, f"/api/rate-plans/by-code/hotel/{hotel_code}")["payload"])
    assert rooms, "测试酒店没有启用房型"
    assert plans, "测试酒店没有价格计划"
    room = rooms[0]
    plan = next((item for item in plans if item.get("status") == "active"), plans[0])

    tenant_id = int(context["tenantId"])
    start = date.today() + timedelta(days=720)
    for offset in range(60):
        target = start + timedelta(days=offset)
        count = run_sql(
            project_dir,
            "SELECT COUNT(*) FROM pms_inventory "
            f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(hotel_code)}' "
            f"AND room_type_code='{sql_text(room['roomTypeCode'])}' AND inventory_date='{target.isoformat()}';",
        )
        if count == "0":
            return {
                **context, "hotel": hotel, "room": room, "plan": plan,
                "checkIn": target.isoformat(), "checkOut": (target + timedelta(days=1)).isoformat(),
                "channelCode": f"CODEX_CANCEL_{suffix}",
            }
    raise AssertionError("未来 60 天内没有可安全插入的库存测试日期")


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
        run_sql(
            project_dir,
            f"DELETE FROM system_trace_logs WHERE reference_code='{sql_text(reservation_code)}';",
        )
    run_sql(
        project_dir,
        "DELETE FROM report_daily_reservation_summary "
        f"WHERE tenant_id={int(fixture['tenantId'])} AND channel_code='{sql_text(fixture['channelCode'])}';"
        "DELETE FROM pms_inventory "
        f"WHERE tenant_id={int(fixture['tenantId'])} AND hotel_code='{sql_text(fixture['hotel']['hotelCode'])}' "
        f"AND room_type_code='{sql_text(fixture['room']['roomTypeCode'])}' "
        f"AND inventory_date='{fixture['checkIn']}';",
    )
    checks = {
        "reservation": int(run_sql(project_dir, f"SELECT COUNT(*) FROM reservation WHERE id={reservation_id or 0};") or 0),
        "inventory": int(run_sql(
            project_dir,
            "SELECT COUNT(*) FROM pms_inventory "
            f"WHERE tenant_id={int(fixture['tenantId'])} AND hotel_code='{sql_text(fixture['hotel']['hotelCode'])}' "
            f"AND room_type_code='{sql_text(fixture['room']['roomTypeCode'])}' AND inventory_date='{fixture['checkIn']}';",
        ) or 0),
        "summary": int(run_sql(
            project_dir,
            "SELECT COUNT(*) FROM report_daily_reservation_summary "
            f"WHERE tenant_id={int(fixture['tenantId'])} AND channel_code='{sql_text(fixture['channelCode'])}';",
        ) or 0),
    }
    return checks


def main() -> None:
    project_dir = Path(__file__).resolve().parents[2]
    output = Path("/private/tmp/crs-order-cancel-lifecycle.json")
    suffix = str(int(time.time()))[-8:]
    result: dict[str, Any] = {
        "suffix": suffix, "checks": [], "expectedRejections": [], "errors": [],
        "http4xx": [], "http5xx": [], "cleanup": {},
    }
    fixture: dict[str, Any] | None = None
    reservation_id: int | None = None
    reservation_code: str | None = None

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
            fixture = choose_fixture(page, project_dir, suffix)
            run_sql(
                project_dir,
                "INSERT INTO pms_inventory "
                "(tenant_id,hotel_code,room_type_code,inventory_date,physical_rooms,available_rooms,maintenance_rooms,overbook_count,version) VALUES "
                f"({int(fixture['tenantId'])},'{sql_text(fixture['hotel']['hotelCode'])}',"
                f"'{sql_text(fixture['room']['roomTypeCode'])}','{fixture['checkIn']}',12,12,0,0,0);",
            )
            payload = {
                "hotelCode": fixture["hotel"]["hotelCode"],
                "hotelName": fixture["hotel"].get("hotelName") or fixture["hotel"]["hotelCode"],
                "roomTypeCode": fixture["room"]["roomTypeCode"],
                "roomTypeName": fixture["room"].get("roomTypeName") or fixture["room"]["roomTypeCode"],
                "ratePlanCode": fixture["plan"]["rateCode"],
                "ratePlanName": fixture["plan"].get("rateName") or fixture["plan"]["rateCode"],
                "channelCode": fixture["channelCode"], "channelName": "自动化验收渠道",
                "channelOrderNumber": f"E2E-CHANNEL-{suffix}",
                "checkInDate": fixture["checkIn"], "checkOutDate": fixture["checkOut"],
                "roomCount": 1, "adultCount": 1, "childCount": 0,
                "contactName": f"取消验收客人{suffix}", "contactPhone": "13800000000",
                "originalPrice": 888.00, "totalPrice": 888.00, "currency": "CNY",
                "orderSource": "manual", "createdBy": "CODEX_E2E_SETUP",
                "reservationStatus": "confirmed", "paymentStatus": "unpaid",
                "dailyPrices": [{"date": fixture["checkIn"], "originalPrice": 888.00, "actualPrice": 888.00}],
                "guests": [{"name": f"取消验收客人{suffix}", "phone": "13800000000"}],
            }
            created = request(page, "/api/reservation", "POST", payload)
            assert created["status"] == 201, created
            reservation_id = int(created["payload"]["id"])
            reservation_code = str(created["payload"]["reservationCode"])
            available_after_create = run_sql(
                project_dir,
                "SELECT available_rooms FROM pms_inventory "
                f"WHERE tenant_id={int(fixture['tenantId'])} AND hotel_code='{sql_text(fixture['hotel']['hotelCode'])}' "
                f"AND room_type_code='{sql_text(fixture['room']['roomTypeCode'])}' AND inventory_date='{fixture['checkIn']}';",
            )
            assert available_after_create == "11"
            result["checks"].append("登录态创建真实订单并从 12 间库存扣减至 11 间")

            page.goto(BASE_URL + f"/reservation/reservation-detail?id={reservation_id}", wait_until="networkidle")
            expect(page.get_by_text(reservation_code, exact=True)).to_be_visible()
            cancel_button = page.get_by_role("button", name="取消订单")
            expect(cancel_button).to_be_enabled()
            cancel_button.click()
            modal = page.locator(".ant-modal:visible").filter(has_text="取消订单").last
            modal.get_by_role("button", name="确认取消").click()
            expect(modal).to_be_visible()
            modal.get_by_role("textbox", name="取消原因").fill(f"自动化完整链路取消 {suffix}")
            with page.expect_response(
                lambda response: response.request.method == "PUT" and response.url.endswith(f"/api/reservation/{reservation_id}/cancel")
            ) as cancelled:
                modal.get_by_role("button", name="确认取消").click()
            assert cancelled.value.status == 200
            expect(page.get_by_text("已取消", exact=True).first).to_be_visible(timeout=20000)
            expect(cancel_button).to_be_disabled()
            expect(page.get_by_text(f"自动化完整链路取消 {suffix}", exact=True)).to_be_visible()
            result["checks"].append("页面必填校验、确认取消、状态/原因/操作人/时间回显与按钮禁用")

            detail = request(page, f"/api/reservation/{reservation_id}")
            assert detail["status"] == 200
            order_info = detail["payload"]["orderInfo"]
            assert order_info["reservationStatus"] == "cancelled"
            assert order_info["cancelReason"] == f"自动化完整链路取消 {suffix}"
            assert order_info["cancelledBy"] and order_info["cancelledBy"].lower() != "system"
            history = detail["payload"]["operationHistory"]
            cancel_history = next(item for item in history if item.get("action") == "CANCEL")
            assert suffix in cancel_history["content"]
            assert cancel_history["operatorDisplay"] not in (None, "-", "system", "系统")
            available_after_cancel = run_sql(
                project_dir,
                "SELECT available_rooms FROM pms_inventory "
                f"WHERE tenant_id={int(fixture['tenantId'])} AND hotel_code='{sql_text(fixture['hotel']['hotelCode'])}' "
                f"AND room_type_code='{sql_text(fixture['room']['roomTypeCode'])}' AND inventory_date='{fixture['checkIn']}';",
            )
            assert available_after_cancel == "12"
            result["checks"].append("取消历史记录当前登录用户，库存从 11 间恢复至 12 间")

            anonymous = page.evaluate(
                """async ({path, tenantId}) => { const response = await fetch(path, {
                  method: 'PUT', headers: {'Content-Type':'application/json','X-Tenant-Id':String(tenantId)},
                  body: JSON.stringify({cancelReason:'匿名非法取消'})
                }); return response.status; }""",
                {"path": f"/api/reservation/{reservation_id}/cancel", "tenantId": fixture["tenantId"]},
            )
            assert anonymous == 401
            result["expectedRejections"].append("匿名取消订单返回 401，未进入业务写操作")
            result["http4xx"] = [item for item in result["http4xx"] if not (item.startswith("401 ") and item.endswith(f"/api/reservation/{reservation_id}/cancel"))]
            result["errors"] = [item for item in result["errors"] if "401 (Unauthorized)" not in item]
        except Exception as exc:
            result["errors"].append(f"test:{type(exc).__name__}:{exc}")
            page.screenshot(path="/private/tmp/crs-order-cancel-lifecycle-failure.png", full_page=True)
            raise
        finally:
            if fixture is not None:
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

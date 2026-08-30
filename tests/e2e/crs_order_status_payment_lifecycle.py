"""CRS 订单状态机、人工干预与已支付退款取消页面回归。"""

from __future__ import annotations

import json
import time
from datetime import date, timedelta
from pathlib import Path
from typing import Any

from playwright.sync_api import Page, expect, sync_playwright

from crs_order_cancel_lifecycle import (
    BASE_URL,
    choose_fixture,
    cleanup,
    login,
    request,
    run_sql,
    sql_text,
)


def prepare_dates(project_dir: Path, base: dict[str, Any], count: int) -> list[str]:
    tenant_id = int(base["tenantId"])
    start = date.fromisoformat(base["checkIn"])
    dates: list[str] = []
    for offset in range(90):
        target = (start + timedelta(days=offset)).isoformat()
        existing = run_sql(
            project_dir,
            "SELECT COUNT(*) FROM pms_inventory "
            f"WHERE tenant_id={tenant_id} AND hotel_code='{sql_text(base['hotel']['hotelCode'])}' "
            f"AND room_type_code='{sql_text(base['room']['roomTypeCode'])}' AND inventory_date='{target}';",
        )
        if existing == "0":
            dates.append(target)
        if len(dates) == count:
            return dates
    raise AssertionError("没有足够的安全库存测试日期")


def create_order(page: Page, project_dir: Path, fixture: dict[str, Any], suffix: str, index: int) -> dict[str, Any]:
    run_sql(
        project_dir,
        "INSERT INTO pms_inventory "
        "(tenant_id,hotel_code,room_type_code,inventory_date,physical_rooms,available_rooms,maintenance_rooms,overbook_count,version) VALUES "
        f"({int(fixture['tenantId'])},'{sql_text(fixture['hotel']['hotelCode'])}',"
        f"'{sql_text(fixture['room']['roomTypeCode'])}','{fixture['checkIn']}',10,10,0,0,0);",
    )
    payload = {
        "hotelCode": fixture["hotel"]["hotelCode"],
        "hotelName": fixture["hotel"].get("hotelName") or fixture["hotel"]["hotelCode"],
        "roomTypeCode": fixture["room"]["roomTypeCode"],
        "roomTypeName": fixture["room"].get("roomTypeName") or fixture["room"]["roomTypeCode"],
        "ratePlanCode": fixture["plan"]["rateCode"],
        "ratePlanName": fixture["plan"].get("rateName") or fixture["plan"]["rateCode"],
        "channelCode": fixture["channelCode"], "channelName": "订单状态验收渠道",
        "channelOrderNumber": f"E2E-STATUS-{suffix}-{index}",
        "checkInDate": fixture["checkIn"], "checkOutDate": fixture["checkOut"],
        "roomCount": 1, "adultCount": 1, "childCount": 0,
        "contactName": f"状态验收客人{index}", "contactPhone": "13800000000",
        "originalPrice": 666.00, "totalPrice": 666.00, "currency": "CNY",
        "orderSource": "manual", "createdBy": "CODEX_E2E_SETUP",
        "reservationStatus": "confirmed", "paymentStatus": "unpaid",
        "dailyPrices": [{"date": fixture["checkIn"], "originalPrice": 666.00, "actualPrice": 666.00}],
        "guests": [{"name": f"状态验收客人{index}", "phone": "13800000000"}],
    }
    created = request(page, "/api/reservation", "POST", payload)
    assert created["status"] == 201, created
    return {
        "id": int(created["payload"]["id"]),
        "code": str(created["payload"]["reservationCode"]),
        "fixture": fixture,
    }


def choose_select(page: Page, aria_label: str, option: str) -> None:
    page.get_by_role("combobox", name=aria_label).click()
    page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(has_text=option).first.click(force=True)


def expected_resource_errors(result: dict[str, Any]) -> None:
    result["errors"] = [
        item for item in result["errors"]
        if not any(code in item for code in ("400 (Bad Request)", "401 (Unauthorized)"))
    ]


def main() -> None:
    project_dir = Path(__file__).resolve().parents[2]
    output = Path("/private/tmp/crs-order-status-payment-lifecycle.json")
    suffix = str(int(time.time()))[-8:]
    result: dict[str, Any] = {
        "suffix": suffix, "checks": [], "expectedRejections": [], "errors": [],
        "http4xx": [], "http5xx": [], "cleanup": {},
    }
    orders: list[dict[str, Any]] = []

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
            base = choose_fixture(page, project_dir, suffix)
            test_dates = prepare_dates(project_dir, base, 3)
            fixtures = []
            for index, test_date in enumerate(test_dates, start=1):
                fixture = {
                    **base,
                    "checkIn": test_date,
                    "checkOut": (date.fromisoformat(test_date) + timedelta(days=1)).isoformat(),
                    "channelCode": f"CODEX_STATUS_{suffix}_{index}",
                }
                fixtures.append(fixture)
                orders.append(create_order(page, project_dir, fixture, suffix, index))

            normal = orders[0]
            invalid = request(page, f"/api/reservation/{normal['id']}/status", "PUT", {"reservationStatus": "checked_out"})
            assert invalid["status"] == 400 and "不允许" in str(invalid["payload"])
            result["expectedRejections"].append("confirmed 直接变更 checked_out 被状态机拒绝")

            page.goto(BASE_URL + f"/reservation/reservation-detail?id={normal['id']}", wait_until="networkidle")
            page.get_by_role("button", name="更新状态").click()
            choose_select(page, "目标状态", "已入住")
            with page.expect_response(lambda response: response.request.method == "PUT" and response.url.endswith(f"/{normal['id']}/status")) as checkin:
                page.locator(".ant-modal:visible").get_by_role("button", name="确认更新").click()
            assert checkin.value.status == 200
            expect(page.get_by_text("已入住", exact=True).first).to_be_visible(timeout=20000)
            page.get_by_role("button", name="更新状态").click()
            choose_select(page, "目标状态", "已离店")
            with page.expect_response(lambda response: response.request.method == "PUT" and response.url.endswith(f"/{normal['id']}/status")) as checkout:
                page.locator(".ant-modal:visible").get_by_role("button", name="确认更新").click()
            assert checkout.value.status == 200
            expect(page.get_by_text("已离店", exact=True).first).to_be_visible(timeout=20000)
            normal_detail = request(page, f"/api/reservation/{normal['id']}")["payload"]
            actions = [item["action"] for item in normal_detail["operationHistory"]]
            assert "CHECK_IN" in actions and "CHECK_OUT" in actions
            assert run_sql(project_dir, f"SELECT status FROM reservation WHERE id={normal['id']};") == "completed"
            result["checks"].append("页面 confirmed→checked_in→checked_out，历史与 completed 记录状态同步")

            manual = orders[1]
            page.goto(BASE_URL + f"/reservation/reservation-detail?id={manual['id']}", wait_until="networkidle")
            page.get_by_role("button", name="人工干预").click()
            manual_modal = page.locator(".ant-modal:visible").filter(has_text="人工干预").last
            manual_modal.get_by_role("button", name="确认干预").click()
            expect(manual_modal).to_be_visible()
            manual_modal.get_by_role("textbox", name="干预原因").fill(f"特殊业务强制离店 {suffix}")
            choose_select(page, "强制目标状态", "已离店")
            with page.expect_response(lambda response: response.request.method == "PUT" and response.url.endswith(f"/{manual['id']}/manual-intervene")) as intervened:
                manual_modal.get_by_role("button", name="确认干预").click()
            assert intervened.value.status == 200
            expect(page.get_by_text("已离店", exact=True).first).to_be_visible(timeout=20000)
            manual_detail = request(page, f"/api/reservation/{manual['id']}")["payload"]
            assert manual_detail["orderInfo"]["isManual"] is True
            manual_history = next(item for item in manual_detail["operationHistory"] if item["action"] == "MANUAL_INTERVENE")
            assert suffix in manual_history["content"] and "confirmed → checked_out" in manual_history["content"]
            result["checks"].append("页面人工干预原因必填，并可跳过状态机强制离店及记录历史")

            paid = orders[2]
            run_sql(
                project_dir,
                "INSERT INTO reservation_payment "
                "(tenant_id,reservation_id,payment_method,payment_type,payment_amount,transaction_id,status,paid_at) VALUES "
                f"({int(paid['fixture']['tenantId'])},{paid['id']},'alipay','payment',666.00,'PAY-{suffix}','success',NOW());"
                f"UPDATE reservation SET payment_status='paid' WHERE id={paid['id']};",
            )
            page.goto(BASE_URL + f"/reservation/reservation-detail?id={paid['id']}", wait_until="networkidle")
            page.get_by_role("button", name="取消订单").click()
            cancel_modal = page.locator(".ant-modal:visible").filter(has_text="取消订单").last
            cancel_modal.get_by_role("textbox", name="取消原因").fill(f"已支付订单退款取消 {suffix}")
            with page.expect_response(lambda response: response.request.method == "PUT" and response.url.endswith(f"/{paid['id']}/cancel")) as paid_cancelled:
                cancel_modal.get_by_role("button", name="确认取消").click()
            assert paid_cancelled.value.status == 200
            paid_detail = request(page, f"/api/reservation/{paid['id']}")["payload"]
            assert paid_detail["orderInfo"]["reservationStatus"] == "cancelled"
            assert paid_detail["paymentInfo"]["paymentStatusCode"] == "refunded"
            refund = next(item for item in paid_detail["paymentInfo"]["payments"] if item["type"] == "refund")
            assert float(refund["amount"]) == 666.0 and refund["status"] == "已退款"
            paid_actions = [item["action"] for item in paid_detail["operationHistory"]]
            assert "REFUND" in paid_actions and "CANCEL" in paid_actions
            available = run_sql(
                project_dir,
                "SELECT available_rooms FROM pms_inventory "
                f"WHERE tenant_id={int(paid['fixture']['tenantId'])} AND hotel_code='{sql_text(paid['fixture']['hotel']['hotelCode'])}' "
                f"AND room_type_code='{sql_text(paid['fixture']['room']['roomTypeCode'])}' AND inventory_date='{paid['fixture']['checkIn']}';",
            )
            assert available == "10"
            result["checks"].append("已支付订单页面取消自动生成全额退款流水、payment_status=refunded 并恢复库存")

            for path, body in (
                (f"/api/reservation/{paid['id']}/status", {"reservationStatus": "checked_in"}),
                (f"/api/reservation/{paid['id']}/manual-intervene", {"reason": "匿名干预"}),
            ):
                status = page.evaluate(
                    """async ({path, tenantId, body}) => { const response = await fetch(path, {
                      method:'PUT', headers:{'Content-Type':'application/json','X-Tenant-Id':String(tenantId)},
                      body:JSON.stringify(body)}); return response.status; }""",
                    {"path": path, "tenantId": paid["fixture"]["tenantId"], "body": body},
                )
                assert status == 401
            result["expectedRejections"].append("匿名状态更新与人工干预均返回 401")

            result["http4xx"] = [
                item for item in result["http4xx"]
                if not (item.startswith("400 ") or item.startswith("401 "))
            ]
            expected_resource_errors(result)
        except Exception as exc:
            result["errors"].append(f"test:{type(exc).__name__}:{exc}")
            page.screenshot(path="/private/tmp/crs-order-status-payment-lifecycle-failure.png", full_page=True)
            raise
        finally:
            cleanup_totals = {"reservation": 0, "inventory": 0, "summary": 0}
            for order in orders:
                cleaned = cleanup(project_dir, order["fixture"], order["id"], order["code"])
                for key, value in cleaned.items():
                    cleanup_totals[key] += value
            result["cleanup"] = cleanup_totals
            browser.close()
            output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")

    assert result["errors"] == [], result["errors"]
    assert result["http4xx"] == [], result["http4xx"]
    assert result["http5xx"] == [], result["http5xx"]
    assert all(value == 0 for value in result["cleanup"].values()), result["cleanup"]
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()

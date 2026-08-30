"""CRS 房态、预订控制与基础价格真实风险操作回归。"""

from __future__ import annotations

import json
import os
import re
import subprocess
from datetime import date
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

from playwright.sync_api import Locator, Page, expect, sync_playwright


BASE_URL = "http://127.0.0.1:3001"


def read_env(path: Path) -> dict[str, str]:
    result: dict[str, str] = {}
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            result[key] = value
    return result


def run_sql(project_dir: Path, sql: str) -> str:
    values = read_env(project_dir / ".env.local")
    parsed = urlparse(values["CRS_DB_URL"].removeprefix("jdbc:"))
    env = os.environ.copy()
    env["MYSQL_PWD"] = values["CRS_DB_PASSWORD"]
    completed = subprocess.run(
        ["mysql", "-h", parsed.hostname or "127.0.0.1", "-P", str(parsed.port or 3306),
         "-u", values["CRS_DB_USERNAME"], "-N", parsed.path.lstrip("/") or "CRS", "-e", sql],
        check=True, capture_output=True, text=True, env=env,
    )
    return completed.stdout.strip()


def api(page: Page, path: str) -> Any:
    return page.evaluate(
        """async path => {
          const response = await fetch(path, { headers: {
            Authorization: `Bearer ${localStorage.getItem('crs_token')}`,
            'X-Tenant-Id': localStorage.getItem('crs_selected_tenant')
          }});
          const payload = await response.json().catch(() => ({}));
          if (!response.ok) throw new Error(`${response.status}:${JSON.stringify(payload)}`);
          return payload;
        }""", path,
    )


def unwrap(payload: Any) -> list[dict[str, Any]]:
    if isinstance(payload, list):
        return payload
    if isinstance(payload, dict) and isinstance(payload.get("data"), list):
        return payload["data"]
    return []


def login(page: Page) -> None:
    page.goto(BASE_URL + "/login", wait_until="networkidle")
    page.locator('input[autocomplete="username"]').fill(os.environ.get("CRS_TEST_USERNAME", "admin"))
    page.locator('input[autocomplete="current-password"]').fill(os.environ.get("CRS_TEST_PASSWORD", "admin123"))
    page.get_by_role("button", name="进入工作台").click()
    page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
    page.wait_for_function("() => Boolean(localStorage.getItem('crs_selected_tenant'))", timeout=30000)
    page.wait_for_timeout(1200)


def modal(page: Page) -> Locator:
    root = page.locator(".ant-modal:visible").last
    root.wait_for(timeout=15000)
    return root


def modal_item(page: Page, label: str) -> Locator:
    return modal(page).locator(".ant-form-item").filter(has_text=label).first


def modal_select(page: Page, label: str, option: str) -> None:
    modal_item(page, label).locator(".ant-select-selector").click(force=True)
    target = page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text=re.compile(rf"^\s*{re.escape(option)}\s*$")
    ).last
    target.wait_for(state="visible", timeout=15000)
    target.evaluate("element => element.click()")


def set_range(root: Locator, value: str) -> None:
    inputs = root.locator(".ant-picker-range input")
    assert inputs.count() == 2
    for field in (inputs.nth(0), inputs.nth(1)):
        field.evaluate("element => element.removeAttribute('readonly')")
        field.fill(value)
        field.press("Enter")


def modal_ok(page: Page) -> None:
    modal(page).locator(".ant-modal-footer .ant-btn-primary").last.click()


def move_to_month(page: Page, target_month: str) -> None:
    for _ in range(24):
        if page.get_by_text(target_month, exact=True).count():
            return
        page.get_by_role("button", name="下月").click()
        page.wait_for_timeout(150)
    raise AssertionError(f"无法导航到月份 {target_month}")


def select_page_option(page: Page, index: int, text: str) -> None:
    page.locator(".crs-shell__content .ant-select-selector").nth(index).click()
    target = page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(has_text=text).first
    target.wait_for(state="visible", timeout=15000)
    target.evaluate("element => element.click()")


def set_month_picker(page: Page, month: str) -> None:
    field = page.locator('input[placeholder="选择月份"]').first
    field.click()
    field.fill(month)
    field.press("Enter")


def choose_fixture(page: Page) -> dict[str, Any]:
    context = page.evaluate(
        """() => { const tenantId = localStorage.getItem('crs_selected_tenant'); return {
          tenantId: Number(tenantId), hotelCode: localStorage.getItem(`crs_selected_hotel_${tenantId}`)
        }; }"""
    )
    hotel_code = context["hotelCode"]
    plans = unwrap(api(page, f"/api/rate-plans/by-code/hotel/{hotel_code}"))
    rooms = unwrap(api(page, f"/api/hotel-room-types/by-code/hotel/{hotel_code}/status/active"))
    policies = unwrap(api(page, "/api/cancellation-policies"))
    plan = next(plan for plan in plans if plan.get("rateType") == "basic")
    room = rooms[0]
    policy = next(policy for policy in policies if policy.get("status") == "active")
    year = date.today().year + 1
    month = f"{year}-11"
    start, end = f"{month}-10", f"{month}-20"
    occupied = set()
    for path, key in (
        (f"/api/room-status?hotelCode={hotel_code}&dimensionType=hotel&dimensionCode=&startDate={start}&endDate={end}", "statusDate"),
        (f"/api/booking-controls?hotelCode={hotel_code}&dimensionType=hotel&dimensionCode=&startDate={start}&endDate={end}", "controlDate"),
        (f"/api/hotel-prices?hotelCode={hotel_code}&rateCode={plan['rateCode']}&startDate={start}&endDate={end}", "priceDate"),
    ):
        occupied.update(str(item.get(key, ""))[:10] for item in unwrap(api(page, path)))
    target_date = next(f"{month}-{day:02d}" for day in range(10, 21) if f"{month}-{day:02d}" not in occupied)
    return {**context, "month": month, "date": target_date, "plan": plan, "room": room, "policy": policy}


def test_room_status(page: Page, fixture: dict[str, Any], result: dict[str, Any]) -> None:
    page.goto(BASE_URL + "/inventory/room-status", wait_until="networkidle")
    move_to_month(page, fixture["month"])
    page.get_by_role("button", name="批量修改").click()
    modal(page).get_by_role("radio", name="关房").check()
    set_range(modal(page), fixture["date"])
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/room-status/batch")) as closed:
        modal_ok(page)
    assert closed.value.status == 200
    records = unwrap(api(page, f"/api/room-status?hotelCode={fixture['hotelCode']}&dimensionType=hotel&dimensionCode=&startDate={fixture['date']}&endDate={fixture['date']}"))
    assert records and records[0]["isOpen"] is False

    page.get_by_role("button", name="批量修改").click()
    modal(page).get_by_role("radio", name="开房").check()
    set_range(modal(page), fixture["date"])
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/room-status/batch")) as opened:
        modal_ok(page)
    assert opened.value.status == 200
    records = unwrap(api(page, f"/api/room-status?hotelCode={fixture['hotelCode']}&dimensionType=hotel&dimensionCode=&startDate={fixture['date']}&endDate={fixture['date']}"))
    assert records and records[0]["isOpen"] is True
    result["checks"].append("房态批量关房、接口回显、批量开房恢复")


def test_booking_control(page: Page, fixture: dict[str, Any], result: dict[str, Any]) -> None:
    page.goto(BASE_URL + "/inventory/booking-control", wait_until="networkidle")
    move_to_month(page, fixture["month"])
    for advance, min_stay, max_stay in ((7, 2, 8), (3, 1, 30)):
        page.get_by_role("button", name="批量修改").click()
        set_range(modal(page), fixture["date"])
        modal_select(page, "取消规则", f"{fixture['policy']['name']}（{fixture['policy']['code']}）")
        modal_item(page, "提前预订天数").locator("input").fill(str(advance))
        modal_item(page, "最小连住天数").locator("input").fill(str(min_stay))
        modal_item(page, "最大连住天数").locator("input").fill(str(max_stay))
        with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/booking-controls/batch")) as saved:
            modal_ok(page)
        assert saved.value.status == 200
        page.wait_for_timeout(300)
    controls = unwrap(api(page, f"/api/booking-controls?hotelCode={fixture['hotelCode']}&dimensionType=hotel&dimensionCode=&startDate={fixture['date']}&endDate={fixture['date']}"))
    assert controls and controls[0]["advanceBookingDays"] == 3 and controls[0]["minStay"] == 1 and controls[0]["maxStay"] == 30
    result["checks"].append("预订控制批量新增、取消政策联动、二次修改与回显")


def test_price(page: Page, fixture: dict[str, Any], result: dict[str, Any]) -> None:
    page.goto(BASE_URL + "/rate-management/rack-rate", wait_until="networkidle")
    select_page_option(page, 0, fixture["plan"]["rateCode"])
    set_month_picker(page, fixture["month"])
    page.locator(".crs-shell__content .ant-btn-primary").first.click()
    page.get_by_role("button", name="批量修改").click()
    set_range(modal(page), fixture["date"])
    room_label = modal(page).get_by_text(re.compile(rf"{re.escape(fixture['room']['roomTypeCode'])}"), exact=False).first
    room_line = room_label.locator("xpath=..")
    room_line.locator('input[placeholder*="含税价格"]').fill("8765.43")
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/hotel-prices/batch")) as priced:
        modal_ok(page)
    assert priced.value.status == 200
    prices = unwrap(api(page, f"/api/hotel-prices?hotelCode={fixture['hotelCode']}&rateCode={fixture['plan']['rateCode']}&startDate={fixture['date']}&endDate={fixture['date']}"))
    target = next(item for item in prices if item["roomTypeCode"] == fixture["room"]["roomTypeCode"])
    assert float(target["priceWithTax"]) == 8765.43 and target["status"] == "active"

    page.get_by_role("button", name="批量修改").click()
    set_range(modal(page), fixture["date"])
    room_label = modal(page).get_by_text(re.compile(rf"{re.escape(fixture['room']['roomTypeCode'])}"), exact=False).first
    room_line = room_label.locator("xpath=..")
    room_line.get_by_role("checkbox", name="删除").check()
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/hotel-prices/batch")) as deleted:
        modal_ok(page)
    assert deleted.value.status == 200
    prices = unwrap(api(page, f"/api/hotel-prices?hotelCode={fixture['hotelCode']}&rateCode={fixture['plan']['rateCode']}&startDate={fixture['date']}&endDate={fixture['date']}"))
    target = next(item for item in prices if item["roomTypeCode"] == fixture["room"]["roomTypeCode"])
    assert target["status"] == "inactive"
    result["checks"].append("基础价格批量发布、价格回显、批量删除与删除状态回显")


def cleanup(project_dir: Path, fixture: dict[str, Any], maxima: dict[str, int]) -> dict[str, int]:
    hotel, target_date, rate, room = fixture["hotelCode"], fixture["date"], fixture["plan"]["rateCode"], fixture["room"]["roomTypeCode"]
    run_sql(project_dir, f"""
DELETE FROM room_status WHERE tenant_id=1 AND hotel_code='{hotel}' AND dimension_type='hotel' AND dimension_code='' AND status_date='{target_date}';
DELETE FROM booking_controls WHERE tenant_id=1 AND hotel_code='{hotel}' AND dimension_type='hotel' AND dimension_code='' AND control_date='{target_date}';
DELETE FROM hotel_prices WHERE tenant_id=1 AND hotel_code='{hotel}' AND rate_code='{rate}' AND room_type_code='{room}' AND price_date='{target_date}';
DELETE FROM room_status_logs WHERE id>{maxima['room']} AND tenant_id=1 AND hotel_code='{hotel}' AND detail LIKE '%{target_date}%';
DELETE FROM booking_control_logs WHERE id>{maxima['booking']} AND tenant_id=1 AND hotel_code='{hotel}' AND detail LIKE '%{target_date}%';
DELETE FROM hotel_price_logs WHERE id>{maxima['price']} AND tenant_id=1 AND hotel_code='{hotel}' AND rate_code='{rate}';
""")
    output = run_sql(project_dir, f"""SELECT
 (SELECT COUNT(*) FROM room_status WHERE tenant_id=1 AND hotel_code='{hotel}' AND dimension_type='hotel' AND dimension_code='' AND status_date='{target_date}'),
 (SELECT COUNT(*) FROM booking_controls WHERE tenant_id=1 AND hotel_code='{hotel}' AND dimension_type='hotel' AND dimension_code='' AND control_date='{target_date}'),
 (SELECT COUNT(*) FROM hotel_prices WHERE tenant_id=1 AND hotel_code='{hotel}' AND rate_code='{rate}' AND room_type_code='{room}' AND price_date='{target_date}');""")
    values = [int(value) for value in output.split("\t")[-3:]]
    return dict(zip(["roomStatus", "bookingControl", "hotelPrice"], values))


def main() -> int:
    project_dir = Path(__file__).resolve().parents[2]
    maxima_raw = run_sql(project_dir, "SELECT COALESCE(MAX(id),0) FROM room_status_logs; SELECT COALESCE(MAX(id),0) FROM booking_control_logs; SELECT COALESCE(MAX(id),0) FROM hotel_price_logs;").splitlines()
    maxima = dict(zip(["room", "booking", "price"], [int(value) for value in maxima_raw[-3:]]))
    result: dict[str, Any] = {"checks": [], "errors": [], "http4xx": [], "http5xx": [], "cleanup": {}}
    fixture: dict[str, Any] | None = None
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1000}, locale="zh-CN")
        page.on("console", lambda message: result["errors"].append(f"console:{message.text}") if message.type == "error" else None)
        page.on("response", lambda response: result["http5xx"].append(f"{response.status}:{response.url}") if response.status >= 500 else result["http4xx"].append(f"{response.status}:{response.url}") if response.status >= 400 else None)
        try:
            login(page)
            fixture = choose_fixture(page)
            test_room_status(page, fixture, result)
            test_booking_control(page, fixture, result)
            test_price(page, fixture, result)
        except Exception as error:  # noqa: BLE001
            page.screenshot(path="/private/tmp/crs-inventory-price-risk-failure.png", full_page=True)
            result["errors"].append(f"test:{type(error).__name__}:{error}:url={page.url}")
        finally:
            if fixture:
                result["cleanup"] = cleanup(project_dir, fixture, maxima)
            browser.close()
    Path("/private/tmp/crs-inventory-price-risk-lifecycle.json").write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 1 if result["errors"] or result["http4xx"] or result["http5xx"] or any(result["cleanup"].values()) else 0


if __name__ == "__main__":
    raise SystemExit(main())

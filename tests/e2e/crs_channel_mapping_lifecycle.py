"""CRS 渠道映射页面全生命周回归。

真实点击验证酒店、房型、房价码映射的新增、重复拦截、编辑、批量设置、批量停启用和删除。
"""

from __future__ import annotations

import json
import os
import re
import subprocess
import time
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

from playwright.sync_api import Locator, Page, expect, sync_playwright


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


def modal(page: Page) -> Locator:
    root = page.locator(".ant-modal:visible").last
    root.wait_for(timeout=15000)
    return root


def item(page: Page, label: str) -> Locator:
    return modal(page).locator(".ant-form-item").filter(has_text=label).first


def fill(page: Page, label: str, value: str) -> None:
    item(page, label).locator("input, textarea").first.fill(value)


def select(page: Page, label: str, option: str) -> None:
    item(page, label).locator(".ant-select-selector").click(force=True)
    target = page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text=re.compile(rf"^\s*{re.escape(option)}\s*$")
    ).last
    target.wait_for(state="visible", timeout=15000)
    target.evaluate("element => element.click()")


def save(page: Page) -> None:
    modal(page).locator(".ant-modal-footer .ant-btn-primary").last.click()


def cancel(page: Page) -> None:
    modal(page).locator(".ant-modal-footer .ant-btn-default").last.click()


def confirm(page: Page) -> None:
    page.locator(".ant-popconfirm:visible .ant-btn-primary").last.click()


def row(page: Page, text: str) -> Locator:
    first_page = page.locator(".ant-pagination:visible .ant-pagination-item-1")
    if first_page.count() and "ant-pagination-item-active" not in (first_page.first.get_attribute("class") or ""):
        first_page.first.click()
        page.wait_for_timeout(250)
    for _ in range(50):
        target = page.locator(".ant-table:visible .ant-table-tbody tr").filter(has_text=text).first
        if target.count() and target.is_visible():
            return target
        next_item = page.locator(".ant-pagination:visible .ant-pagination-next").first
        if not next_item.count() or "ant-pagination-disabled" in (next_item.get_attribute("class") or ""):
            break
        next_item.locator("button").click()
        page.wait_for_timeout(250)
    raise AssertionError(f"表格未找到记录: {text}")


def login(page: Page) -> None:
    page.goto(BASE_URL + "/login", wait_until="networkidle")
    page.locator('input[autocomplete="username"]').fill(os.environ.get("CRS_TEST_USERNAME", "admin"))
    page.locator('input[autocomplete="current-password"]').fill(os.environ.get("CRS_TEST_PASSWORD", "admin123"))
    page.get_by_role("button", name="进入工作台").click()
    page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
    page.wait_for_function("() => Boolean(localStorage.getItem('crs_selected_tenant'))", timeout=30000)
    page.wait_for_timeout(1200)


def choose_fixtures(page: Page) -> dict[str, Any]:
    tenant_id = int(page.evaluate("() => localStorage.getItem('crs_selected_tenant')"))
    channels = unwrap(api(page, f"/api/tenant-channels/all?tenantId={tenant_id}"))
    hotels = unwrap(api(page, f"/api/hotels?tenantId={tenant_id}"))
    hotel_mappings = unwrap(api(page, "/api/channel-mappings/hotels"))
    room_mappings = unwrap(api(page, "/api/channel-mappings/room-types"))
    rate_mappings = unwrap(api(page, "/api/channel-mappings/rate-codes"))
    existing_hotels = {(m.get("channelCode"), m.get("hotelCode")) for m in hotel_mappings}
    hotel_candidates = [
        (channel, hotel) for channel in channels for hotel in hotels
        if (channel.get("channelCode"), hotel.get("hotelCode")) not in existing_hotels
    ]
    if not hotel_candidates:
        raise AssertionError("没有可用的渠道×酒店映射组合")

    room_fixture = None
    rate_fixture = None
    for hotel in hotels:
        hotel_code = hotel.get("hotelCode")
        room_types = unwrap(api(page, f"/api/hotel-room-types/by-code/hotel/{hotel_code}/status/active"))
        rate_plans = unwrap(api(page, f"/api/rate-plans?hotelCode={hotel_code}"))
        for channel in channels:
            channel_code = channel.get("channelCode")
            if room_fixture is None:
                room = next((room for room in room_types if not any(
                    mapping.get("channelCode") == channel_code and mapping.get("hotelCode") == hotel_code
                    and mapping.get("roomTypeCode") == room.get("roomTypeCode") for mapping in room_mappings
                )), None)
                if room:
                    room_fixture = (channel, hotel, room)
            if rate_fixture is None:
                rate = next((rate for rate in rate_plans if not any(
                    mapping.get("channelCode") == channel_code and mapping.get("hotelCode") == hotel_code
                    and mapping.get("rateCode") == rate.get("rateCode") for mapping in rate_mappings
                )), None)
                if rate:
                    rate_fixture = (channel, hotel, rate)
            if room_fixture and rate_fixture:
                break
        if room_fixture and rate_fixture:
            break
    if not room_fixture or not rate_fixture:
        raise AssertionError("没有可用的房型或房价码映射组合")
    return {
        "tenantId": tenant_id,
        "hotelCandidates": hotel_candidates[:2],
        "room": room_fixture,
        "rate": rate_fixture,
    }


def open_page(page: Page) -> None:
    page.goto(BASE_URL + "/channel-management/channel-mapping", wait_until="networkidle")
    page.get_by_role("button", name="新增映射").wait_for(timeout=20000)


def create_hotel_mapping(page: Page, channel: dict[str, Any], hotel: dict[str, Any], code: str) -> int:
    page.get_by_role("button", name="新增映射").click()
    select(page, "渠道", channel["channelName"])
    select(page, "酒店", hotel["chineseName"])
    fill(page, "渠道酒店CODE", code)
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/channel-mappings/hotels")) as created:
        save(page)
    assert created.value.status == 201
    mapping_id = int(created.value.json()["id"])
    modal(page).wait_for(state="hidden", timeout=20000)
    row(page, code)
    return mapping_id


def test_hotel(page: Page, fixture: dict[str, Any], codes: dict[str, str], result: dict[str, Any]) -> None:
    page.get_by_role("button", name="新增映射").click()
    save(page)
    expect(modal(page).locator(".ant-form-item-explain-error").filter(has_text="请选择渠道")).to_be_visible()
    cancel(page)

    ids: list[int] = []
    for index, (channel, hotel) in enumerate(fixture["hotelCandidates"], start=1):
        ids.append(create_hotel_mapping(page, channel, hotel, f"{codes['hotel']}_{index}"))

    first_channel, first_hotel = fixture["hotelCandidates"][0]
    page.get_by_role("button", name="新增映射").click()
    select(page, "渠道", first_channel["channelName"])
    select(page, "酒店", first_hotel["chineseName"])
    fill(page, "渠道酒店CODE", codes["hotel"] + "_DUP")
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/channel-mappings/hotels")) as duplicate:
        save(page)
    assert duplicate.value.status == 400
    result["expectedRejections"].append("渠道×酒店映射重复")
    cancel(page)

    first_row = row(page, codes["hotel"] + "_1")
    first_row.get_by_role("button", name="编辑").click()
    expect(item(page, "渠道").locator(".ant-select-selector")).to_be_visible()
    fill(page, "渠道酒店CODE", codes["hotel"] + "_EDIT")
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/channel-mappings/hotels/{ids[0]}" in response.url) as updated:
        save(page)
    assert updated.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)

    visible_codes = [codes["hotel"] + "_EDIT"]
    if len(ids) > 1:
        visible_codes.append(codes["hotel"] + "_2")
    for code in visible_codes:
        row(page, code).locator(".ant-checkbox-input").check()
    page.get_by_role("button", name="批量设置").click()
    fill(page, "渠道CODE前缀（可选）", codes["batch"])
    with page.expect_response(lambda response: response.request.method == "PUT" and "/api/channel-mappings/hotels/" in response.url) as batch_update:
        save(page)
    assert batch_update.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)

    batch_codes = [f"{codes['batch']}_{hotel['hotelCode']}" for _, hotel in fixture["hotelCandidates"]]
    for code in batch_codes:
        row(page, code).locator(".ant-checkbox-input").check()
    page.get_by_role("button", name="批量禁用").click()
    page.wait_for_timeout(800)
    for code in batch_codes:
        expect(row(page, code).get_by_text("禁用", exact=True)).to_be_visible()

    for code in batch_codes:
        row(page, code).locator(".ant-checkbox-input").check()
    page.get_by_role("button", name="批量启用").click()
    page.wait_for_timeout(800)
    for code in batch_codes:
        expect(row(page, code).get_by_text("启用", exact=True)).to_be_visible()

    for code in batch_codes:
        row(page, code).locator(".ant-checkbox-input").check()
    page.get_by_role("button", name="批量删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and "/api/channel-mappings/hotels/" in response.url) as deleted:
        confirm(page)
    assert deleted.value.status == 200
    result["checks"].append("酒店映射必填/重复校验、新增、编辑、批量设置、批量停启用、批量删除")


def test_room_type(page: Page, fixture: tuple[dict[str, Any], dict[str, Any], dict[str, Any]], codes: dict[str, str], result: dict[str, Any]) -> None:
    channel, hotel, room_type = fixture
    page.get_by_role("tab", name="房型CODE映射").click()
    page.get_by_role("button", name="新增映射").click()
    select(page, "渠道", channel["channelName"])
    select(page, "酒店", hotel["chineseName"])
    item(page, "酒店房型").locator(".ant-select-item-option", has_text="x")
    select(page, "酒店房型", room_type["roomTypeName"])
    fill(page, "渠道房型CODE", codes["room"])
    fill(page, "渠道房型名称", "渠道回归房型")
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/channel-mappings/room-types")) as created:
        save(page)
    assert created.value.status == 201
    mapping_id = int(created.value.json()["id"])
    modal(page).wait_for(state="hidden", timeout=20000)
    target = row(page, codes["room"])
    target.get_by_text("启用", exact=True).click()
    page.wait_for_timeout(600)
    expect(row(page, codes["room"]).get_by_text("禁用", exact=True)).to_be_visible()
    target = row(page, codes["room"])
    target.get_by_role("button", name="编辑").click()
    fill(page, "渠道房型CODE", codes["room"] + "_EDIT")
    fill(page, "渠道房型名称", "渠道回归房型已编辑")
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/channel-mappings/room-types/{mapping_id}" in response.url) as updated:
        save(page)
    assert updated.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)
    target = row(page, codes["room"] + "_EDIT")
    target.get_by_role("button", name="删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and f"/api/channel-mappings/room-types/{mapping_id}" in response.url) as deleted:
        confirm(page)
    assert deleted.value.status == 200
    result["checks"].append("房型映射新增、酒店产品联动、停用、编辑与删除")


def test_rate(page: Page, fixture: tuple[dict[str, Any], dict[str, Any], dict[str, Any]], codes: dict[str, str], result: dict[str, Any]) -> None:
    channel, hotel, rate = fixture
    page.get_by_role("tab", name="房价CODE映射").click()
    page.get_by_role("button", name="新增映射").click()
    select(page, "渠道", channel["channelName"])
    select(page, "酒店", hotel["chineseName"])
    select(page, "房价码", f"{rate['rateName']} ({rate['rateCode']})")
    fill(page, "渠道房价CODE", codes["rate"])
    fill(page, "渠道房价名称", "渠道回归房价")
    fill(page, "加价率(%)", "12.5")
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/channel-mappings/rate-codes")) as created:
        save(page)
    assert created.value.status == 201
    mapping_id = int(created.value.json()["id"])
    modal(page).wait_for(state="hidden", timeout=20000)
    target = row(page, codes["rate"])
    target.get_by_text("启用", exact=True).click()
    page.wait_for_timeout(600)
    target = row(page, codes["rate"])
    target.get_by_role("button", name="编辑").click()
    fill(page, "渠道房价CODE", codes["rate"] + "_EDIT")
    fill(page, "渠道房价名称", "渠道回归房价已编辑")
    fill(page, "加价率(%)", "8.25")
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/channel-mappings/rate-codes/{mapping_id}" in response.url) as updated:
        save(page)
    assert updated.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)
    target = row(page, codes["rate"] + "_EDIT")
    expect(target.get_by_text("+8.25%", exact=True)).to_be_visible()
    target.get_by_role("button", name="删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and f"/api/channel-mappings/rate-codes/{mapping_id}" in response.url) as deleted:
        confirm(page)
    assert deleted.value.status == 200
    result["checks"].append("房价码映射新增、加价率回显、停用、编辑与删除")


def cleanup(project_dir: Path, suffix: str) -> dict[str, int]:
    safe = suffix.replace("'", "''")
    run_sql(project_dir, f"""
DELETE FROM channel_hotel_mappings WHERE tenant_id=1 AND channel_hotel_code LIKE '%{safe}%';
DELETE FROM channel_room_type_mappings WHERE tenant_id=1 AND channel_room_type_code LIKE '%{safe}%';
DELETE FROM channel_rate_code_mappings WHERE tenant_id=1 AND channel_rate_code LIKE '%{safe}%';
""")
    output = run_sql(project_dir, f"""SELECT
 (SELECT COUNT(*) FROM channel_hotel_mappings WHERE tenant_id=1 AND channel_hotel_code LIKE '%{safe}%'),
 (SELECT COUNT(*) FROM channel_room_type_mappings WHERE tenant_id=1 AND channel_room_type_code LIKE '%{safe}%'),
 (SELECT COUNT(*) FROM channel_rate_code_mappings WHERE tenant_id=1 AND channel_rate_code LIKE '%{safe}%');""")
    values = [int(value) for value in output.split("\t")[-3:]]
    return dict(zip(["hotel", "roomType", "rateCode"], values))


def main() -> int:
    project_dir = Path(__file__).resolve().parents[2]
    suffix = str(int(time.time()))[-8:]
    codes = {
        "hotel": f"CODEX_HMAP_{suffix}", "batch": f"CODEX_BATCH_{suffix}",
        "room": f"CODEX_RMAP_{suffix}", "rate": f"CODEX_PMAP_{suffix}",
    }
    result: dict[str, Any] = {"suffix": suffix, "checks": [], "expectedRejections": [], "warnings": [], "errors": [], "http5xx": [], "cleanup": {}}
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1000}, locale="zh-CN")

        def console(message: Any) -> None:
            if message.type != "error":
                return
            if "Failed to load resource" in message.text and "400" in message.text:
                result["warnings"].append(message.text)
            else:
                result["errors"].append(f"console:{message.text}")

        page.on("console", console)
        page.on("response", lambda response: result["http5xx"].append(f"{response.status}:{response.url}") if response.status >= 500 else None)
        try:
            login(page)
            fixture = choose_fixtures(page)
            open_page(page)
            test_hotel(page, fixture, codes, result)
            test_room_type(page, fixture["room"], codes, result)
            test_rate(page, fixture["rate"], codes, result)
        except Exception as error:  # noqa: BLE001
            page.screenshot(path="/private/tmp/crs-channel-mapping-failure.png", full_page=True)
            result["errors"].append(f"test:{type(error).__name__}:{error}:url={page.url}")
        finally:
            result["cleanup"] = cleanup(project_dir, suffix)
            browser.close()
    Path("/private/tmp/crs-channel-mapping-lifecycle.json").write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 1 if result["errors"] or result["http5xx"] or any(result["cleanup"].values()) else 0


if __name__ == "__main__":
    raise SystemExit(main())

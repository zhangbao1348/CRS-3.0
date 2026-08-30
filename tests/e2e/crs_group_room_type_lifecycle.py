"""CRS 集团房型到酒店房型的页面全生命周回归。"""

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
HOTEL_CODE = "JJSH001"
HOTEL_NAME = "上海锦江饭店1"


def read_env(path: Path) -> dict[str, str]:
    values: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            values[key] = value
    return values


def browser_request(page: Page, path: str, method: str = "GET", body: Any = None) -> dict[str, Any]:
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
              'X-Operator-Name': 'CODEX_E2E_GRT',
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


def choose_first(page: Page, label: str) -> None:
    form_item(page, label).locator(".ant-select-selector").click()
    page.locator(
        ".ant-select-dropdown:visible .ant-select-item-option:not(.ant-select-item-option-disabled)"
    ).first.click(force=True)


def group_row(page: Page, room_code: str) -> Locator:
    page.locator('input[placeholder="房型编码"]').fill(room_code)
    page.locator("button").filter(has_text="搜索").first.click()
    row = page.locator(".ant-table-tbody tr").filter(has_text=room_code).first
    row.wait_for(timeout=20000)
    return row


def open_group_edit(page: Page, room_code: str) -> None:
    row = group_row(page, room_code)
    row.locator("button").filter(has_text="编辑").first.click()
    page.get_by_text("编辑集团房型", exact=True).wait_for(timeout=20000)


def set_hotel_context(page: Page) -> None:
    page.evaluate(
        """({ hotelCode }) => {
          const tenantId = localStorage.getItem('crs_selected_tenant');
          localStorage.setItem(`crs_selected_hotel_${tenantId}`, hotelCode);
        }""",
        {"hotelCode": HOTEL_CODE},
    )


def find_hotel_room_row(page: Page, room_code: str, visible: bool = True) -> Locator:
    page.locator('input[placeholder="房型代码"]').fill(room_code)
    page.locator("button").filter(has_text="查询").first.click()
    row = page.locator(".ant-table-tbody tr").filter(has_text=room_code).first
    if visible:
        row.wait_for(timeout=20000)
    return row


def cleanup(project_dir: Path, room_code: str) -> dict[str, int]:
    env_values = read_env(project_dir / ".env.local")
    parsed = urlparse(env_values["CRS_DB_URL"].removeprefix("jdbc:"))
    database = parsed.path.lstrip("/") or "CRS"
    mysql_env = os.environ.copy()
    mysql_env["MYSQL_PWD"] = env_values["CRS_DB_PASSWORD"]
    safe_code = room_code.replace("'", "''")
    sql = f"""
START TRANSACTION;
DELETE FROM room_type_facilities WHERE room_type_code='{safe_code}';
DELETE FROM group_room_type_hotel WHERE group_room_type_code='{safe_code}';
DELETE FROM hotel_room_types WHERE room_type_code='{safe_code}';
DELETE FROM group_room_types WHERE room_type_code='{safe_code}';
COMMIT;
SELECT
  (SELECT COUNT(*) FROM group_room_type_hotel WHERE group_room_type_code='{safe_code}'),
  (SELECT COUNT(*) FROM hotel_room_types WHERE room_type_code='{safe_code}'),
  (SELECT COUNT(*) FROM group_room_types WHERE room_type_code='{safe_code}');
"""
    completed = subprocess.run(
        [
            "mysql", "-h", parsed.hostname or "127.0.0.1", "-P", str(parsed.port or 3306),
            "-u", env_values["CRS_DB_USERNAME"], "-N", database, "-e", sql,
        ],
        env=mysql_env,
        capture_output=True,
        text=True,
        check=True,
    )
    values = [int(value) for value in completed.stdout.strip().split("\t")[-3:]]
    return dict(zip(["allocation", "hotelRoomType", "groupRoomType"], values))


def main() -> int:
    project_dir = Path(__file__).resolve().parents[2]
    suffix = str(int(time.time()))[-8:]
    room_code = f"CODEX_GRT_{suffix}"
    room_name = f"端到端集团房型_{suffix}"
    result: dict[str, Any] = {
        "roomTypeCode": room_code,
        "checks": [],
        "expectedRejections": [],
        "warnings": [],
        "errors": [],
        "http5xx": [],
        "fixtureCleaned": False,
    }

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1000}, locale="zh-CN")

        def handle_console(message: Any) -> None:
            if message.type != "error":
                return
            if "Static function can not consume context" in message.text:
                result["warnings"].append(message.text)
            elif "Failed to load resource" in message.text and "400" in message.text:
                result["warnings"].append(message.text)
            else:
                result["errors"].append(f"console:{message.text}")

        page.on("console", handle_console)
        page.on(
            "response",
            lambda response: result["http5xx"].append(f"{response.status}:{response.url}")
            if response.status >= 500 else None,
        )

        try:
            page.goto(BASE_URL + "/login", wait_until="networkidle")
            page.locator('input[autocomplete="username"]').fill(os.environ.get("CRS_TEST_USERNAME", "admin"))
            page.locator('input[autocomplete="current-password"]').fill(os.environ.get("CRS_TEST_PASSWORD", "admin123"))
            page.get_by_role("button", name="进入工作台").click()
            page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
            page.wait_for_timeout(1200)
            result["checks"].append("登录")

            page.goto(BASE_URL + "/group-management/add-group-room-type", wait_until="networkidle")
            page.locator('input[placeholder="请输入房型代码，如STD-KING"]').fill("BAD-CODE")
            page.locator("button").filter(has_text="保存并继续分配").first.click()
            page.get_by_text("房型代码只能包含英文字母、数字和下划线", exact=True).wait_for()
            result["checks"].append("集团房型代码格式校验")

            page.locator('input[placeholder="请输入房型代码，如STD-KING"]').fill(room_code)
            page.locator('input[placeholder="请输入房型名称"]').fill(room_name)
            choose_first(page, "房型大类")
            page.locator("button").filter(has_text="保存并继续分配").first.click()
            page.get_by_role("tab", name="房型分配").wait_for(timeout=20000)
            hotel_row = page.locator(".ant-table-tbody tr").filter(has_text=HOTEL_NAME).first
            hotel_row.wait_for(timeout=30000)
            page.wait_for_timeout(1500)
            allocated = hotel_row.locator("button.ant-switch").nth(0)
            allocated.click()
            expect(allocated).to_have_attribute("aria-checked", "true", timeout=10000)
            page.locator("button").filter(has_text="保存分配设置").first.click()
            page.wait_for_url(re.compile(r"/group-management/group-room-type$"), timeout=30000)
            assert room_code in group_row(page, room_code).inner_text()
            result["checks"].append("集团房型创建并分配到酒店")

            record = browser_request(page, f"/api/group-room-types/code/{room_code}")
            assert record["status"] == 200, record
            room_type_id = int(record["payload"]["id"])
            duplicate_payload = {
                key: value for key, value in record["payload"].items()
                if key not in {"id", "createdAt", "updatedAt", "roomTypeCategory"}
            }
            duplicate = browser_request(page, "/api/group-room-types", "POST", duplicate_payload)
            assert duplicate["status"] == 400, duplicate
            result["expectedRejections"].append("集团房型重复代码被拒绝")

            set_hotel_context(page)
            page.goto(BASE_URL + "/room-management/room-type", wait_until="networkidle")
            hotel_room_row = find_hotel_room_row(page, room_code)
            assert f"集团（{room_code}）" in hotel_room_row.inner_text()
            hotel_room_row.locator("button").filter(has_text="编辑").first.click()
            page.get_by_text(f"集团下发房型（{room_code}）", exact=True).wait_for(timeout=20000)
            assert page.locator('input[placeholder="输入房型中文名称"]').is_disabled()
            assert page.locator("button").filter(has_text="保存基础信息").first.is_disabled()
            hotel_record = browser_request(
                page, f"/api/hotel-room-types/by-code/hotel/{HOTEL_CODE}/room-type/{room_code}"
            )
            hotel_room_type = hotel_record["payload"]["data"]
            denied_update = browser_request(
                page,
                f"/api/hotel-room-types/{hotel_room_type['id']}",
                "PUT",
                {**hotel_room_type, "roomTypeName": "不应写入"},
            )
            assert denied_update["status"] == 400, denied_update
            result["expectedRejections"].append("集团未授权时酒店端写入被服务端拒绝")
            result["checks"].append("酒店房型来源回显与未授权只读")

            page.goto(BASE_URL + "/group-management/group-room-type", wait_until="networkidle")
            open_group_edit(page, room_code)
            page.get_by_role("tab", name="房型分配").click()
            hotel_row = page.locator(".ant-table-tbody tr").filter(has_text=HOTEL_NAME).first
            hotel_row.wait_for(timeout=30000)
            page.wait_for_timeout(1200)
            edit_switch = hotel_row.locator("button.ant-switch").nth(1)
            expect(edit_switch).to_be_enabled(timeout=10000)
            edit_switch.click()
            page.locator("button").filter(has_text="保存分配设置").first.click()
            page.wait_for_url(re.compile(r"/group-management/group-room-type$"), timeout=30000)

            page.goto(BASE_URL + "/room-management/room-type", wait_until="networkidle")
            hotel_room_row = find_hotel_room_row(page, room_code)
            hotel_room_row.locator("button").filter(has_text="编辑").first.click()
            name_input = page.locator('input[placeholder="输入房型中文名称"]')
            expect(name_input).to_be_enabled(timeout=20000)
            name_input.fill(room_name + "_酒店可编辑")
            page.locator('input[placeholder="输入房型数量"]').fill("8")
            page.locator('input[placeholder="输入成人数"]').fill("2")
            form_item(page, "窗型").locator(".ant-select-selector").click()
            page.locator(".ant-select-dropdown:visible .ant-select-item-option").first.click(force=True)
            form_item(page, "床型").locator(".ant-select-selector").click()
            page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
                has_text="1张1.8米大床"
            ).first.click(force=True)
            page.locator("button").filter(has_text="保存基础信息").first.click()
            page.get_by_text("基础信息保存成功", exact=True).wait_for(timeout=20000)
            result["checks"].append("集团授权后酒店房型可编辑")

            page.goto(BASE_URL + "/group-management/group-room-type", wait_until="networkidle")
            row = group_row(page, room_code)
            row.locator("button").filter(has_text="停用").first.click()
            page.locator(".ant-modal-confirm-btns .ant-btn-primary").click()
            page.get_by_text("停用成功", exact=True).wait_for(timeout=20000)
            page.goto(BASE_URL + "/room-management/room-type", wait_until="networkidle")
            assert find_hotel_room_row(page, room_code, visible=False).count() == 0

            page.goto(BASE_URL + "/group-management/group-room-type", wait_until="networkidle")
            row = group_row(page, room_code)
            row.locator("button").filter(has_text="启用").first.click()
            page.locator(".ant-modal-confirm-btns .ant-btn-primary").click()
            page.get_by_text("启用成功", exact=True).wait_for(timeout=20000)
            page.goto(BASE_URL + "/room-management/room-type", wait_until="networkidle")
            find_hotel_room_row(page, room_code).wait_for()
            result["checks"].append("集团房型停用/启用级联酒店显示")

            page.goto(BASE_URL + "/group-management/group-room-type", wait_until="networkidle")
            open_group_edit(page, room_code)
            page.get_by_role("tab", name="房型分配").click()
            hotel_row = page.locator(".ant-table-tbody tr").filter(has_text=HOTEL_NAME).first
            hotel_row.wait_for(timeout=30000)
            page.wait_for_timeout(1200)
            allocated = hotel_row.locator("button.ant-switch").nth(0)
            expect(allocated).to_have_attribute("aria-checked", "true")
            allocated.click()
            page.locator("button").filter(has_text="保存分配设置").first.click()
            page.wait_for_url(re.compile(r"/group-management/group-room-type$"), timeout=30000)
            page.goto(BASE_URL + "/room-management/room-type", wait_until="networkidle")
            assert find_hotel_room_row(page, room_code, visible=False).count() == 0
            result["checks"].append("集团房型回收后酒店端不再显示")

            delete_result = browser_request(page, f"/api/group-room-types/{room_type_id}", "DELETE")
            assert delete_result["status"] == 400, delete_result
            result["expectedRejections"].append("集团房型物理删除被业务规则拒绝")
            page.screenshot(path="/private/tmp/crs-group-room-type-lifecycle-final.png", full_page=True)
        except Exception as error:
            page.screenshot(path="/private/tmp/crs-group-room-type-lifecycle-failure.png", full_page=True)
            result["errors"].append(f"test:{type(error).__name__}:{error}:url={page.url}")
        finally:
            browser.close()

    try:
        counts = cleanup(project_dir, room_code)
        result["cleanupCounts"] = counts
        result["fixtureCleaned"] = all(value == 0 for value in counts.values())
        if not result["fixtureCleaned"]:
            result["errors"].append(f"cleanup-residual:{counts}")
    except Exception as error:
        result["errors"].append(f"cleanup:{type(error).__name__}:{error}")

    if result["http5xx"]:
        result["errors"].append(f"http5xx:{result['http5xx']}")
    result["passed"] = not result["errors"]
    Path("/private/tmp/crs-group-room-type-lifecycle.json").write_text(
        json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    print(json.dumps(result, ensure_ascii=False))
    return 0 if result["passed"] else 1


if __name__ == "__main__":
    raise SystemExit(main())

"""CRS 系统设置真实页面全生命周期回归。

用户、角色、字典与集团设置的新增、编辑、筛选、权限、停启用、引用拦截与删除均由 Chromium 页面触发。
数据库只用于 finally 中的定向残留清理和集团设置事前值的灾难恢复。
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

from playwright.sync_api import Browser, Locator, Page, expect, sync_playwright


BASE_URL = "http://127.0.0.1:3001"


def read_env(path: Path) -> dict[str, str]:
    values: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            values[key] = value
    return values


def run_sql(project_dir: Path, sql: str) -> str:
    values = read_env(project_dir / ".env.local")
    parsed = urlparse(values["CRS_DB_URL"].removeprefix("jdbc:"))
    mysql_env = os.environ.copy()
    mysql_env["MYSQL_PWD"] = values["CRS_DB_PASSWORD"]
    completed = subprocess.run(
        [
            "mysql", "-h", parsed.hostname or "127.0.0.1", "-P", str(parsed.port or 3306),
            "-u", values["CRS_DB_USERNAME"], "-N", parsed.path.lstrip("/") or "CRS", "-e", sql,
        ],
        check=True,
        capture_output=True,
        text=True,
        env=mysql_env,
    )
    return completed.stdout.strip()


def esc(value: str) -> str:
    return value.replace("'", "''")


def open_route(page: Page, route: str) -> None:
    page.goto(BASE_URL + route, wait_until="networkidle")
    page.locator(".ant-layout-content").wait_for(timeout=20000)


def modal(page: Page) -> Locator:
    root = page.locator(".ant-modal:visible").last
    root.wait_for(timeout=10000)
    return root


def modal_item(page: Page, label: str) -> Locator:
    root = modal(page)
    return root.locator(".ant-form-item").filter(has_text=label).first


def modal_fill(page: Page, label: str, value: str) -> None:
    modal_item(page, label).locator("input, textarea").first.fill(value)


def modal_select(page: Page, label: str, option: str) -> None:
    modal_item(page, label).locator(".ant-select-selector").click(force=True)
    option_locator = page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text=re.compile(rf"^\s*{re.escape(option)}\s*$")
    ).last
    option_locator.wait_for(state="visible", timeout=10000)
    option_locator.evaluate("element => element.click()")


def table_row(page: Page, text: str) -> Locator:
    row = page.locator(".ant-table-tbody tr").filter(has_text=text).first
    row.wait_for(timeout=20000)
    return row


def click_modal_ok(page: Page, label: str = "确认") -> None:
    del label
    modal(page).locator(".ant-modal-footer .ant-btn-primary").last.click()


def confirm_pop(page: Page) -> None:
    page.locator(".ant-popconfirm:visible .ant-btn-primary").last.click()


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
        }""",
        path,
    )


def login(page: Page, username: str, password: str, expect_success: bool = True, wait_tenant: bool = True) -> int:
    page.goto(BASE_URL + "/login", wait_until="networkidle")
    page.locator('input[autocomplete="username"]').fill(username)
    page.locator('input[autocomplete="current-password"]').fill(password)
    with page.expect_response(lambda response: "/api/auth/login" in response.url and response.request.method == "POST") as info:
        page.get_by_role("button", name="进入工作台").click()
    status = info.value.status
    if expect_success:
        assert status == 200
        page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
        if wait_tenant:
            page.wait_for_function("() => Boolean(localStorage.getItem('crs_selected_tenant'))", timeout=30000)
            page.wait_for_timeout(1200)
    else:
        assert status in {400, 401, 403}
        expect(page).to_have_url(re.compile(r"/login"))
    return status


def test_role(page: Page, codes: dict[str, str], result: dict[str, Any]) -> int:
    open_route(page, "/system-settings/role-management")
    page.get_by_role("button", name="新增角色").click()
    click_modal_ok(page, "保存")
    expect(page.get_by_text("请输入角色名称", exact=True)).to_be_visible()
    modal_fill(page, "角色名称", codes["role_name"])
    modal_fill(page, "角色代码", codes["role"])
    modal_fill(page, "描述", "系统设置页面回归角色")
    tree_checkbox = modal(page).locator(".ant-tree-checkbox").first
    tree_checkbox.wait_for(timeout=10000)
    tree_checkbox.click()
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/roles")) as created:
        click_modal_ok(page, "保存")
    assert created.value.status == 200
    role_payload = created.value.json()
    assert "password" not in json.dumps(role_payload)
    role_id = int(role_payload["data"]["id"])
    modal(page).wait_for(state="hidden", timeout=20000)

    page.get_by_role("button", name="新增角色").click()
    modal_fill(page, "角色名称", "重复角色")
    modal_fill(page, "角色代码", codes["role"])
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/roles")) as duplicate:
        click_modal_ok(page, "保存")
    assert duplicate.value.status == 400
    result["expectedRejections"].append("角色代码重复")
    modal(page).locator(".ant-modal-footer .ant-btn-default").last.click()

    page.locator('input[placeholder="角色名称/代码"]').fill(codes["role"])
    row = table_row(page, codes["role"])
    row.get_by_role("button", name="编辑").click()
    expect(modal_item(page, "角色代码").locator("input")).to_be_disabled()
    modal_fill(page, "角色名称", codes["role_name"] + "_已编辑")
    modal_select(page, "状态", "停用")
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/roles/{role_id}" in response.url) as updated:
        click_modal_ok(page, "保存")
    assert updated.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)

    row = table_row(page, codes["role"])
    row.get_by_role("button", name="编辑").click()
    modal_select(page, "状态", "启用")
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/roles/{role_id}" in response.url) as reenabled:
        click_modal_ok(page, "保存")
    assert reenabled.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)
    assigned = api(page, f"/api/roles/{role_id}/menus")
    assert assigned.get("data")
    result["checks"].append("角色必填/重复校验、新增、权限树分配、编辑、代码冻结、停启用")
    return role_id


def create_user(page: Page, codes: dict[str, str], result: dict[str, Any]) -> int:
    open_route(page, "/system-settings/user-management")
    page.get_by_role("button", name="新增用户").click()
    click_modal_ok(page)
    expect(page.get_by_text("请输入用户名", exact=True)).to_be_visible()
    modal_fill(page, "用户名", codes["username"])
    modal_fill(page, "姓名", codes["user_name"])
    modal_fill(page, "邮箱", codes["email"])
    modal_fill(page, "手机号", "13800000000")
    modal_fill(page, "密码", codes["password1"])
    modal(page).locator("label.ant-checkbox-wrapper").filter(has_text=codes["role_name"] + "_已编辑").click()
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/users")) as created:
        click_modal_ok(page)
    assert created.value.status == 200
    payload = created.value.json()
    assert "password" not in payload.get("data", {})
    user_id = int(payload["data"]["id"])
    modal(page).wait_for(state="hidden", timeout=20000)

    page.get_by_role("button", name="新增用户").click()
    modal_fill(page, "用户名", codes["username"])
    modal_fill(page, "姓名", "重复用户")
    modal_fill(page, "邮箱", "duplicate_" + codes["email"])
    modal_fill(page, "密码", codes["password1"])
    modal(page).locator("label.ant-checkbox-wrapper").filter(has_text=codes["role_name"] + "_已编辑").click()
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/users")) as duplicate:
        click_modal_ok(page)
    assert duplicate.value.status == 400
    result["expectedRejections"].append("用户名重复")
    modal(page).locator(".ant-modal-footer .ant-btn-default").last.click()

    page.locator('input[aria-label="用户名"]').fill(codes["username"][-6:])
    row = table_row(page, codes["username"])
    page.locator('input[aria-label="姓名"]').fill("不存在姓名")
    expect(page.locator(".ant-table-tbody tr.ant-table-placeholder")).to_be_visible()
    page.get_by_role("button", name="重置条件").click()
    row = table_row(page, codes["username"])

    row.get_by_role("button", name="编辑").click()
    expect(modal_item(page, "用户名").locator("input")).to_be_disabled()
    modal_fill(page, "姓名", codes["user_name"] + "_已编辑")
    modal_fill(page, "手机号", "13900000000")
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/users/{user_id}" in response.url) as updated:
        click_modal_ok(page)
    assert updated.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)

    row = table_row(page, codes["username"])
    row.get_by_role("button", name="重置密码").click()
    modal_fill(page, "新密码", codes["password2"])
    modal_fill(page, "确认密码", "mismatch")
    click_modal_ok(page)
    expect(page.get_by_text("两次输入的密码不一致", exact=True)).to_be_visible()
    modal_fill(page, "确认密码", codes["password2"])
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/users/{user_id}/password" in response.url) as reset:
        click_modal_ok(page)
    assert reset.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)
    result["checks"].append("用户必填/重复校验、多角色、四维筛选、编辑、密码一致性与重置")
    return user_id


def verify_user_status_and_cleanup(browser: Browser, page: Page, codes: dict[str, str], role_id: int, user_id: int, result: dict[str, Any]) -> None:
    login_context = browser.new_context(viewport={"width": 1280, "height": 900}, locale="zh-CN")
    login_page = login_context.new_page()
    login(login_page, codes["username"], codes["password2"], True, False)
    login_context.close()

    open_route(page, "/system-settings/user-management")
    page.locator('input[aria-label="用户名"]').fill(codes["username"])
    row = table_row(page, codes["username"])
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/users/{user_id}/status" in response.url) as disabled:
        row.get_by_role("button", name="禁用").click()
    assert disabled.value.status == 200

    disabled_context = browser.new_context(viewport={"width": 1280, "height": 900}, locale="zh-CN")
    login(disabled_context.new_page(), codes["username"], codes["password2"], False)
    disabled_context.close()

    row = table_row(page, codes["username"])
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/users/{user_id}/status" in response.url) as enabled:
        row.get_by_role("button", name="启用").click()
    assert enabled.value.status == 200

    open_route(page, "/system-settings/role-management")
    page.locator('input[placeholder="角色名称/代码"]').fill(codes["role"])
    role_row = table_row(page, codes["role"])
    role_row.get_by_role("button", name="删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and f"/api/roles/{role_id}" in response.url) as blocked:
        confirm_pop(page)
    assert blocked.value.status == 400
    result["expectedRejections"].append("角色被用户引用时禁止删除")

    open_route(page, "/system-settings/user-management")
    page.locator('input[aria-label="用户名"]').fill(codes["username"])
    user_row = table_row(page, codes["username"])
    user_row.get_by_role("button", name="删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and f"/api/users/{user_id}" in response.url) as user_deleted:
        confirm_pop(page)
    assert user_deleted.value.status == 200

    open_route(page, "/system-settings/role-management")
    page.locator('input[placeholder="角色名称/代码"]').fill(codes["role"])
    role_row = table_row(page, codes["role"])
    role_row.get_by_role("button", name="删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and f"/api/roles/{role_id}" in response.url) as role_deleted:
        confirm_pop(page)
    assert role_deleted.value.status == 200
    result["checks"].append("用户新密码登录、禁用后拒绝登录、恢复、引用删除拦截、用户/角色删除")


def test_dictionary(page: Page, codes: dict[str, str], result: dict[str, Any]) -> None:
    open_route(page, "/system-settings/dictionary-management")
    page.get_by_role("button", name="新增类型").click()
    click_modal_ok(page)
    expect(page.get_by_text("请输入类型名称", exact=True)).to_be_visible()
    modal_fill(page, "类型名称", codes["dict_name"])
    modal_fill(page, "类型编码", codes["dict"])
    with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/dictionary-types")) as created:
        click_modal_ok(page)
    assert created.value.status == 200
    type_id = int(created.value.json()["id"])
    modal(page).wait_for(state="hidden", timeout=20000)

    page.get_by_label("搜索字典类型").fill(codes["dict"])
    page.locator(".dictionary-management__search button").first.click()
    type_item = page.locator(".dictionary-management__type-item").filter(has_text=codes["dict"]).first
    type_item.wait_for(timeout=20000)
    type_item.click()

    for index in (1, 2):
        page.get_by_role("button", name="新增字典项").click()
        modal_fill(page, "项目名称", f"字典项{index}_{codes['suffix']}")
        modal_fill(page, "项目编码", f"{codes['item']}_{index}")
        modal_fill(page, "项目值", f"value_{index}")
        modal_item(page, "默认项").locator(".ant-switch").click()
        with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/dictionary-items")) as item_created:
            click_modal_ok(page)
        assert item_created.value.status == 200
        modal(page).wait_for(state="hidden", timeout=20000)

    items = api(page, f"/api/dictionary-items?typeCode={codes['dict']}")["data"]
    assert sum(1 for item in items if item["isDefault"]) == 1
    assert next(item for item in items if item["isDefault"])["itemCode"].endswith("_2")

    row2 = table_row(page, f"{codes['item']}_2")
    row2.get_by_role("button", name="编辑").click()
    modal_fill(page, "项目名称", f"字典项已编辑_{codes['suffix']}")
    modal_select(page, "状态", "停用")
    with page.expect_response(lambda response: response.request.method == "PUT" and "/api/dictionary-items/" in response.url) as item_updated:
        click_modal_ok(page)
    assert item_updated.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)

    type_item = page.locator(".dictionary-management__type-item").filter(has_text=codes["dict"]).first
    type_item.get_by_role("button", name="删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and f"/api/dictionary-types/{type_id}" in response.url) as blocked:
        confirm_pop(page)
    assert blocked.value.status == 400
    result["expectedRejections"].append("字典类型包含字典项时禁止删除")

    for code in (f"{codes['item']}_1", f"{codes['item']}_2"):
        row = table_row(page, code)
        row.get_by_role("button", name="删除").click()
        with page.expect_response(lambda response: response.request.method == "DELETE" and "/api/dictionary-items/" in response.url) as deleted:
            confirm_pop(page)
        assert deleted.value.status == 200

    type_item = page.locator(".dictionary-management__type-item").filter(has_text=codes["dict"]).first
    type_item.get_by_role("button", name="编辑").click()
    expect(modal_item(page, "类型编码").locator("input")).to_be_disabled()
    modal_fill(page, "类型名称", codes["dict_name"] + "_已编辑")
    modal_select(page, "状态", "停用")
    with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/dictionary-types/{type_id}" in response.url) as type_updated:
        click_modal_ok(page)
    assert type_updated.value.status == 200
    modal(page).wait_for(state="hidden", timeout=20000)

    type_item = page.locator(".dictionary-management__type-item").filter(has_text=codes["dict"]).first
    type_item.get_by_role("button", name="删除").click()
    with page.expect_response(lambda response: response.request.method == "DELETE" and f"/api/dictionary-types/{type_id}" in response.url) as deleted:
        confirm_pop(page)
    assert deleted.value.status == 200
    result["checks"].append("字典类型/项新增、默认值唯一、编辑停用、代码冻结、引用拦截与顺序删除")


def set_group_settings(page: Page, settings: dict[str, Any]) -> None:
    labels = {
        "groupControlMode": {"strong": "强管控", "weak": "弱管控"},
        "hourlyRoom": {"support": "支持", "notSupport": "不支持"},
        "otaPromotionMode": {
            "groupRegistration": "集团报名",
            "groupRuleHotelRegistration": "集团设置规则酒店报名",
            "hotelSelfManagement": "酒店自行管理",
        },
    }
    for key, options in labels.items():
        page.get_by_role("radio", name=options[settings[key]], exact=True).check()
    for key, label in (("showCtripPrice", "显示携程预测价格"), ("showMeituanPrice", "显示美团预测价格")):
        checkbox = page.get_by_role("checkbox", name=label, exact=True)
        if checkbox.is_checked() != bool(settings[key]):
            checkbox.click()


def test_group_settings(page: Page, result: dict[str, Any]) -> dict[str, Any]:
    open_route(page, "/system-settings/group-settings")
    original = api(page, "/api/group-settings")
    original = original.get("data", original)
    changed = {
        "groupControlMode": "weak" if original["groupControlMode"] == "strong" else "strong",
        "hourlyRoom": "notSupport" if original["hourlyRoom"] == "support" else "support",
        "otaPromotionMode": "hotelSelfManagement" if original["otaPromotionMode"] != "hotelSelfManagement" else "groupRegistration",
        "showCtripPrice": not original["showCtripPrice"],
        "showMeituanPrice": not original["showMeituanPrice"],
    }
    set_group_settings(page, changed)
    with page.expect_response(lambda response: response.request.method == "PUT" and response.url.endswith("/api/group-settings")) as saved:
        page.get_by_role("button", name="保存设置").click()
    assert saved.value.status == 200 and saved.value.json() == changed
    page.reload(wait_until="networkidle")
    for key, label in (("showCtripPrice", "显示携程预测价格"), ("showMeituanPrice", "显示美团预测价格")):
        assert page.get_by_role("checkbox", name=label, exact=True).is_checked() == changed[key]
    set_group_settings(page, original)
    with page.expect_response(lambda response: response.request.method == "PUT" and response.url.endswith("/api/group-settings")) as restored:
        page.get_by_role("button", name="保存设置").click()
    assert restored.value.status == 200
    result["checks"].append("集团管控/钟点房/OTA/预测价开关保存、重载回显与原值恢复")
    return original


def cleanup(project_dir: Path, codes: dict[str, str], original: str) -> dict[str, int]:
    username, role_code, type_code = esc(codes["username"]), esc(codes["role"]), esc(codes["dict"])
    run_sql(project_dir, f"""
DELETE ur FROM user_roles ur JOIN users u ON u.id=ur.user_id WHERE u.username='{username}';
DELETE FROM users WHERE username='{username}';
DELETE rm FROM role_menus rm JOIN roles r ON r.id=rm.role_id WHERE r.role_code='{role_code}';
DELETE ur FROM user_roles ur JOIN roles r ON r.id=ur.role_id WHERE r.role_code='{role_code}';
DELETE FROM roles WHERE role_code='{role_code}';
DELETE FROM dictionary_items WHERE tenant_id=1 AND type_code='{type_code}';
DELETE FROM dictionary_types WHERE tenant_id=1 AND type_code='{type_code}';
""")
    if original:
        parts = original.split("\t")
        run_sql(project_dir, f"""UPDATE group_settings SET
group_control_mode='{esc(parts[0])}', hourly_room='{esc(parts[1])}', ota_promotion_mode='{esc(parts[2])}',
show_ctrip_price={int(parts[3])}, show_meituan_price={int(parts[4])}
WHERE tenant_id=1;""")
    else:
        run_sql(project_dir, "DELETE FROM group_settings WHERE tenant_id=1;")
    residual = run_sql(project_dir, f"""
SELECT
 (SELECT COUNT(*) FROM users WHERE username='{username}'),
 (SELECT COUNT(*) FROM roles WHERE role_code='{role_code}'),
 (SELECT COUNT(*) FROM dictionary_types WHERE tenant_id=1 AND type_code='{type_code}'),
 (SELECT COUNT(*) FROM dictionary_items WHERE tenant_id=1 AND type_code='{type_code}');
""")
    values = [int(value) for value in residual.split("\t")[-4:]]
    return dict(zip(["user", "role", "dictionaryType", "dictionaryItems"], values))


def main() -> int:
    project_dir = Path(__file__).resolve().parents[2]
    suffix = str(int(time.time()))[-8:]
    codes = {
        "suffix": suffix,
        "role": f"CODEX_ROLE_{suffix}",
        "role_name": f"回归角色_{suffix}",
        "username": f"codex_user_{suffix}",
        "user_name": f"回归用户_{suffix}",
        "email": f"codex_{suffix}@example.test",
        "password1": "Codex123!",
        "password2": "Codex456!",
        "dict": f"CODEX_DICT_{suffix}",
        "dict_name": f"回归字典_{suffix}",
        "item": f"CODEX_ITEM_{suffix}",
    }
    original_db = run_sql(project_dir, "SELECT group_control_mode,hourly_room,ota_promotion_mode,show_ctrip_price,show_meituan_price FROM group_settings WHERE tenant_id=1;")
    result: dict[str, Any] = {"suffix": suffix, "checks": [], "expectedRejections": [], "warnings": [], "errors": [], "http5xx": [], "cleanup": {}}

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        context = browser.new_context(viewport={"width": 1440, "height": 1000}, locale="zh-CN")
        page = context.new_page()

        def handle_console(message: Any) -> None:
            if message.type != "error":
                return
            if "Failed to load resource" in message.text and "400" in message.text:
                result["warnings"].append(message.text)
            else:
                result["errors"].append(f"console:{message.text}")

        page.on("console", handle_console)
        page.on("response", lambda response: result["http5xx"].append(f"{response.status}:{response.url}") if response.status >= 500 else None)
        try:
            login(page, os.environ.get("CRS_TEST_USERNAME", "admin"), os.environ.get("CRS_TEST_PASSWORD", "admin123"))
            result["checks"].append("登录与租户上下文")
            role_id = test_role(page, codes, result)
            user_id = create_user(page, codes, result)
            verify_user_status_and_cleanup(browser, page, codes, role_id, user_id, result)
            test_dictionary(page, codes, result)
            test_group_settings(page, result)
        except Exception as error:  # noqa: BLE001
            page.screenshot(path="/private/tmp/crs-system-settings-failure.png", full_page=True)
            result["errors"].append(f"test:{type(error).__name__}:{error}:url={page.url}")
        finally:
            result["cleanup"] = cleanup(project_dir, codes, original_db)
            browser.close()

    Path("/private/tmp/crs-system-settings-lifecycle.json").write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 1 if result["errors"] or result["http5xx"] or any(result["cleanup"].values()) else 0


if __name__ == "__main__":
    raise SystemExit(main())

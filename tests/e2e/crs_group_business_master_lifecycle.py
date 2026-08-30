"""CRS 集团业务主数据页面全生命周期回归。

创建、编辑、筛选、状态切换、引用拦截和删除均由真实 Chromium 页面触发。
数据库仅用于创建可精确识别的引用夹具，以及 finally 中的定向清理与残留复核。
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
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            values[key] = value
    return values


def sql_escape(value: str) -> str:
    return value.replace("'", "''")


def run_sql(project_dir: Path, sql: str) -> str:
    env_values = read_env(project_dir / ".env.local")
    parsed = urlparse(env_values["CRS_DB_URL"].removeprefix("jdbc:"))
    database = parsed.path.lstrip("/") or "CRS"
    mysql_env = os.environ.copy()
    mysql_env["MYSQL_PWD"] = env_values["CRS_DB_PASSWORD"]
    completed = subprocess.run(
        [
            "mysql", "-h", parsed.hostname or "127.0.0.1", "-P", str(parsed.port or 3306),
            "-u", env_values["CRS_DB_USERNAME"], "-N", database, "-e", sql,
        ],
        check=True,
        capture_output=True,
        text=True,
        env=mysql_env,
    )
    return completed.stdout.strip()


def form_item(page: Page, label: str) -> Locator:
    return page.locator(".ant-form-item:visible").filter(
        has=page.locator(".ant-form-item-label").filter(has_text=label)
    ).first


def scoped_form_item(root: Locator, label: str) -> Locator:
    return root.locator(".ant-form-item").filter(has_text=label).first


def fill(page: Page, label: str, value: str) -> None:
    field = form_item(page, label).locator("input, textarea").first
    field.fill(value)


def select(page: Page, label: str, option: str) -> None:
    item = form_item(page, label)
    item.locator(".ant-select-selector").click()
    option_locator = page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text=re.compile(rf"^\s*{re.escape(option)}\s*$")
    ).last
    option_locator.wait_for(state="visible", timeout=10000)
    option_locator.click(force=True)


def modal_fill(page: Page, label: str, value: str) -> None:
    modal = page.locator(".ant-modal:visible")
    scoped_form_item(modal, label).locator("input, textarea").first.fill(value)


def modal_select(page: Page, label: str, option: str) -> None:
    modal = page.locator(".ant-modal:visible")
    scoped_form_item(modal, label).locator(".ant-select-selector").click(force=True)
    option_locator = page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text=re.compile(rf"^\s*{re.escape(option)}\s*$")
    ).last
    option_locator.wait_for(state="visible", timeout=10000)
    option_locator.click(force=True)


def table_row(page: Page, text: str) -> Locator:
    row = page.locator(".ant-table-tbody tr").filter(has_text=text).first
    row.wait_for(timeout=20000)
    return row


def open_route(page: Page, route: str) -> None:
    page.goto(BASE_URL + route, wait_until="networkidle")
    page.locator(".ant-layout-content").wait_for(timeout=20000)


def save_and_wait(page: Page, route: str, response_path: str | None = None) -> None:
    if response_path:
        with page.expect_response(
            lambda response: response.request.method in {"POST", "PUT"} and response_path in response.url,
            timeout=20000,
        ) as response_info:
            page.get_by_role("button", name="保存").last.click()
        assert response_info.value.status in {200, 201}, (response_info.value.status, response_info.value.url)
    else:
        page.get_by_role("button", name="保存").last.click()
    page.wait_for_url(lambda url: route in url, timeout=30000)


def confirm_delete(page: Page, row: Locator, expected_status: int, api_path: str) -> None:
    row.get_by_role("button", name=re.compile("删除")).click()
    with page.expect_response(
        lambda response: response.request.method == "DELETE" and api_path in response.url,
        timeout=20000,
    ) as response_info:
        page.locator("button").filter(has_text=re.compile(r"^\s*确\s*定\s*$")).last.click()
    assert response_info.value.status == expected_status, (response_info.value.status, response_info.value.url)


def insert_rate_reference(project_dir: Path, rate_code: str, **references: str) -> None:
    columns = [
        "group_id", "tenant_id", "created_at", "updated_at", "status", "rate_category",
        "rate_code", "rate_name", "derivative_level", "rate_type",
    ]
    values = ["1", "1", "NOW(6)", "NOW(6)", "'active'", "'CODEX_TEST'",
              f"'{sql_escape(rate_code)}'", "'业务主数据引用测试'", "'basic'", "'basic'"]
    for column, value in references.items():
        columns.append(column)
        values.append(f"'{sql_escape(value)}'")
    run_sql(
        project_dir,
        f"INSERT INTO group_rate_codes ({','.join(columns)}) VALUES ({','.join(values)});",
    )


def delete_rate_reference(project_dir: Path, rate_code: str) -> None:
    run_sql(
        project_dir,
        f"DELETE FROM group_rate_codes WHERE group_id=1 AND rate_code='{sql_escape(rate_code)}';",
    )


def cleanup_all(project_dir: Path, codes: dict[str, str]) -> dict[str, int]:
    escaped = {key: sql_escape(value) for key, value in codes.items()}
    run_sql(
        project_dir,
        f"""
START TRANSACTION;
DELETE FROM archives WHERE group_id=1 AND archive_id='{escaped['archive']}';
DELETE FROM group_rate_codes WHERE group_id=1 AND rate_code LIKE 'CODEX_BM_REF_%{escaped['suffix']}';
DELETE FROM tax_settings WHERE tenant_id=1 AND tax_code='{escaped['tax']}';
DELETE FROM packages WHERE tenant_id=1 AND code='{escaped['package']}';
DELETE FROM guarantee_policies WHERE tenant_id=1 AND code='{escaped['guarantee']}';
DELETE FROM cancellation_policies WHERE tenant_id=1 AND code='{escaped['cancellation']}';
DELETE FROM group_facilities WHERE facility_code='{escaped['facility']}';
COMMIT;
""",
    )
    output = run_sql(
        project_dir,
        f"""
SELECT
 (SELECT COUNT(*) FROM tax_settings WHERE tenant_id=1 AND tax_code='{escaped['tax']}'),
 (SELECT COUNT(*) FROM packages WHERE tenant_id=1 AND code='{escaped['package']}'),
 (SELECT COUNT(*) FROM guarantee_policies WHERE tenant_id=1 AND code='{escaped['guarantee']}'),
 (SELECT COUNT(*) FROM cancellation_policies WHERE tenant_id=1 AND code='{escaped['cancellation']}'),
 (SELECT COUNT(*) FROM group_facilities WHERE facility_code='{escaped['facility']}'),
 (SELECT COUNT(*) FROM archives WHERE group_id=1 AND archive_id='{escaped['archive']}'),
 (SELECT COUNT(*) FROM group_rate_codes WHERE group_id=1 AND rate_code LIKE 'CODEX_BM_REF_%{escaped['suffix']}');
""",
    )
    values = [int(value) for value in output.split("\t")[-7:]]
    return dict(zip(["tax", "package", "guarantee", "cancellation", "facility", "archive", "references"], values))


def test_tax(page: Page, codes: dict[str, str], result: dict[str, Any]) -> None:
    route = "/group-management/tax-setting"
    open_route(page, route)
    page.get_by_role("button", name="新增集团税率").click()
    page.get_by_role("button", name="保存").last.click()
    expect(page.get_by_text("请输入税率CODE", exact=True)).to_be_visible()
    fill(page, "税率CODE", "BAD-CODE")
    expect(page.get_by_text("税率CODE仅允许输入英文字母、数字和下划线", exact=True)).to_be_visible()
    fill(page, "税率CODE", codes["tax"])
    fill(page, "税率名称", f"回归税率_{codes['suffix']}")
    fill(page, "税率 (%)", "6.25")
    select(page, "状态", "启用")
    save_and_wait(page, route, "/api/tax-settings")

    page.get_by_role("button", name="新增集团税率").click()
    fill(page, "税率CODE", codes["tax"])
    fill(page, "税率名称", "重复税率")
    fill(page, "税率 (%)", "6")
    select(page, "状态", "启用")
    with page.expect_response(lambda response: response.request.method == "POST" and "/api/tax-settings" in response.url) as duplicate:
        page.get_by_role("button", name="保存").last.click()
    assert duplicate.value.status == 400
    result["expectedRejections"].append("税率重复编码")
    open_route(page, route)

    page.locator('input[placeholder="税率CODE"]').fill(codes["tax"])
    page.get_by_role("button", name="搜索").click()
    row = table_row(page, codes["tax"])
    row.get_by_role("button", name="编辑").click()
    expect(form_item(page, "税率CODE").locator("input")).to_be_disabled()
    fill(page, "税率名称", f"回归税率已编辑_{codes['suffix']}")
    fill(page, "税率 (%)", "8.50")
    select(page, "状态", "停用")
    save_and_wait(page, route, "/api/tax-settings/")
    result["checks"].append("税率字段校验/新增/编辑/编码冻结/筛选")


def test_package(page: Page, project_dir: Path, codes: dict[str, str], result: dict[str, Any]) -> None:
    route = "/group-management/package-setting"
    open_route(page, route)
    page.get_by_role("button", name="新增包价").click()
    page.get_by_role("button", name="保存").last.click()
    expect(page.get_by_text("请输入包价代码", exact=True)).to_be_visible()
    fill(page, "包价代码", codes["package"])
    fill(page, "包价名称", f"回归包价_{codes['suffix']}")
    select(page, "包价类型", "早餐")
    select(page, "发放频率", "每天1次")
    select(page, "计数方式", "按房间")
    fill(page, "每房间份数", "2")
    fill(page, "价格", "88.5")
    form_item(page, "描述").locator("textarea").fill("包价创建回归")
    save_and_wait(page, route, "/api/packages")

    page.locator('input[placeholder*="包价名称或代码"]').fill(codes["package"])
    row = table_row(page, codes["package"])
    row.get_by_role("button", name="编辑").click()
    expect(form_item(page, "包价代码").locator("input")).to_be_disabled()
    fill(page, "包价名称", f"回归包价已编辑_{codes['suffix']}")
    select(page, "发放频率", "每入住一次")
    select(page, "计数方式", "按人数")
    fill(page, "每人份数", "3")
    save_and_wait(page, route, "/api/packages/")

    page.get_by_role("button", name="新增包价").click()
    fill(page, "包价代码", codes["package"])
    fill(page, "包价名称", "重复包价")
    select(page, "包价类型", "早餐")
    select(page, "发放频率", "每天1次")
    select(page, "计数方式", "固定份数")
    fill(page, "固定份数", "1")
    with page.expect_response(lambda response: response.request.method == "POST" and "/api/packages" in response.url) as duplicate:
        page.get_by_role("button", name="保存").last.click()
    assert duplicate.value.status == 400
    result["expectedRejections"].append("包价重复编码")
    open_route(page, route)

    reference = f"CODEX_BM_REF_PKG_{codes['suffix']}"
    insert_rate_reference(project_dir, reference, packages=json.dumps([codes["package"]], ensure_ascii=False))
    row = table_row(page, codes["package"])
    confirm_delete(page, row, 400, "/api/packages/")
    expect(table_row(page, codes["package"])).to_be_visible()
    result["expectedRejections"].append("包价被房价码引用时禁止删除")
    delete_rate_reference(project_dir, reference)
    confirm_delete(page, table_row(page, codes["package"]), 200, "/api/packages/")
    page.wait_for_timeout(500)
    expect(page.locator(".ant-table-tbody tr").filter(has_text=codes["package"])).to_have_count(0)
    result["checks"].append("包价新增/编辑/动态份数/引用拦截/删除")


def test_guarantee(page: Page, project_dir: Path, codes: dict[str, str], result: dict[str, Any]) -> None:
    route = "/group-management/group-guarantee"
    open_route(page, route)
    page.get_by_role("button", name="新增担保政策").click()
    fill(page, "担保政策名称", f"回归担保_{codes['suffix']}")
    fill(page, "担保政策代码", codes["guarantee"])
    select(page, "担保类型", "信用卡")
    select(page, "担保子类型", "超时担保")
    select(page, "担保金额", "首晚")
    fill(page, "最晚到店时间", "25:70")
    page.get_by_role("button", name="保存").last.click()
    expect(page.get_by_text("请输入正确的 24 小时制时间，例如 18:00", exact=True)).to_be_visible()
    fill(page, "最晚到店时间", "22:30")
    select(page, "状态", "启用")
    save_and_wait(page, route, "/api/guarantee-policies")

    page.locator('input[placeholder="担保政策代码"]').fill(codes["guarantee"])
    row = table_row(page, codes["guarantee"])
    row.get_by_role("button", name="编辑").click()
    expect(form_item(page, "担保政策代码").locator("input")).to_be_disabled()
    select(page, "担保类型", "预付")
    expect(page.get_by_text("担保子类型", exact=True)).to_have_count(0)
    select(page, "状态", "停用")
    save_and_wait(page, route, "/api/guarantee-policies/")

    page.get_by_role("button", name="新增担保政策").click()
    fill(page, "担保政策名称", "重复担保")
    fill(page, "担保政策代码", codes["guarantee"])
    select(page, "担保类型", "无担保")
    select(page, "状态", "启用")
    with page.expect_response(lambda response: response.request.method == "POST" and "/api/guarantee-policies" in response.url) as duplicate:
        page.get_by_role("button", name="保存").last.click()
    assert duplicate.value.status == 400
    result["expectedRejections"].append("担保政策重复编码")
    open_route(page, route)

    reference = f"CODEX_BM_REF_GUA_{codes['suffix']}"
    insert_rate_reference(project_dir, reference, guarantee_rule=codes["guarantee"])
    row = table_row(page, codes["guarantee"])
    confirm_delete(page, row, 400, "/api/guarantee-policies/")
    result["expectedRejections"].append("担保政策被房价码引用时禁止删除")
    delete_rate_reference(project_dir, reference)
    confirm_delete(page, table_row(page, codes["guarantee"]), 200, "/api/guarantee-policies/")
    result["checks"].append("担保政策信用卡条件/时间校验/类型清值/引用拦截/删除")


def test_cancellation(page: Page, project_dir: Path, codes: dict[str, str], result: dict[str, Any]) -> None:
    route = "/group-management/group-cancellation"
    open_route(page, route)
    page.get_by_role("button", name="新增取消政策").click()
    fill(page, "取消政策名称", f"回归取消_{codes['suffix']}")
    fill(page, "取消政策代码", codes["cancellation"])
    select(page, "取消类型", "限时扣费")
    page.locator('input[placeholder="天数"]').fill("2")
    page.locator('input[placeholder="时间"]').fill("18:30")
    page.keyboard.press("Enter")
    page.locator(".ant-row").filter(has_text="入住前").locator(".ant-select-selector").click()
    page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(has_text="首晚").last.click()
    select(page, "状态", "启用")
    save_and_wait(page, route, "/api/cancellation-policies")

    page.locator('input[placeholder="取消政策代码"]').fill(codes["cancellation"])
    row = table_row(page, codes["cancellation"])
    row.get_by_role("button", name="编辑").click()
    expect(form_item(page, "取消政策代码").locator("input")).to_be_disabled()
    select(page, "取消类型", "不可取消")
    expect(page.locator('input[placeholder="天数"]')).to_have_count(0)
    select(page, "状态", "停用")
    save_and_wait(page, route, "/api/cancellation-policies/")

    page.get_by_role("button", name="新增取消政策").click()
    fill(page, "取消政策名称", "重复取消")
    fill(page, "取消政策代码", codes["cancellation"])
    select(page, "取消类型", "免费取消")
    select(page, "状态", "启用")
    with page.expect_response(lambda response: response.request.method == "POST" and "/api/cancellation-policies" in response.url) as duplicate:
        page.get_by_role("button", name="保存").last.click()
    assert duplicate.value.status == 400
    result["expectedRejections"].append("取消政策重复编码")
    open_route(page, route)

    reference = f"CODEX_BM_REF_CAN_{codes['suffix']}"
    insert_rate_reference(project_dir, reference, cancellation_rule=codes["cancellation"])
    row = table_row(page, codes["cancellation"])
    confirm_delete(page, row, 400, "/api/cancellation-policies/")
    result["expectedRejections"].append("取消政策被房价码引用时禁止删除")
    delete_rate_reference(project_dir, reference)
    confirm_delete(page, table_row(page, codes["cancellation"]), 200, "/api/cancellation-policies/")
    result["checks"].append("取消政策限时条件/回显/类型清值/引用拦截/删除")


def test_facility(page: Page, codes: dict[str, str], result: dict[str, Any]) -> None:
    route = "/group-management/facility-management"
    open_route(page, route)
    page.get_by_role("button", name="新增设施").click()
    page.locator(".ant-modal:visible").get_by_role("button", name="保存").click()
    expect(page.get_by_text("请选择设施分类", exact=True)).to_be_visible()
    modal_select(page, "设施分类", "交通服务")
    modal_fill(page, "设施名称", f"回归设施_{codes['suffix']}")
    modal_fill(page, "设施代码", codes["facility"])
    modal_fill(page, "设施描述", "集团设施回归")
    with page.expect_response(lambda response: response.request.method == "POST" and "/api/group-facilities" in response.url) as created:
        page.locator(".ant-modal:visible").get_by_role("button", name="保存").click()
    assert created.value.status == 200
    page.locator(".ant-modal:visible").wait_for(state="hidden", timeout=10000)

    form_item(page, "设施代码").locator("input").fill(codes["facility"])
    page.locator("button").filter(has_text=re.compile(r"查\s*询")).first.click()
    row = table_row(page, codes["facility"])

    page.get_by_role("button", name="新增设施").click()
    modal_select(page, "设施分类", "交通服务")
    modal_fill(page, "设施名称", "重复设施")
    modal_fill(page, "设施代码", codes["facility"])
    with page.expect_response(lambda response: response.request.method == "POST" and "/api/group-facilities" in response.url) as duplicate:
        page.locator(".ant-modal:visible").get_by_role("button", name="保存").click()
    assert duplicate.value.status == 400
    result["expectedRejections"].append("设施重复编码")
    page.locator(".ant-modal:visible .ant-modal-close").click()
    page.locator(".ant-modal:visible").wait_for(state="hidden", timeout=10000)

    row = table_row(page, codes["facility"])
    row.get_by_role("button", name="编辑").click()
    expect(scoped_form_item(page.locator(".ant-modal:visible"), "设施代码").locator("input")).to_be_disabled()
    modal_select(page, "适用范围", "房型设施")
    modal_select(page, "设施分类", "媒体科技")
    modal_fill(page, "设施名称", f"回归设施已编辑_{codes['suffix']}")
    modal_select(page, "状态", "不可用")
    with page.expect_response(lambda response: response.request.method == "PUT" and "/api/group-facilities/" in response.url) as updated:
        page.locator(".ant-modal:visible").get_by_role("button", name="保存").click()
    assert updated.value.status == 200
    page.locator(".ant-modal:visible").wait_for(state="hidden", timeout=10000)
    form_item(page, "设施代码").locator("input").fill(codes["facility"])
    page.locator("button").filter(has_text=re.compile(r"查\s*询")).first.click()
    expect(table_row(page, codes["facility"])).to_contain_text("房型设施")
    expect(table_row(page, codes["facility"])).to_contain_text("不可用")
    result["checks"].append("设施必填/新增/范围分类联动/编码冻结/状态/筛选")


def test_archive(page: Page, project_dir: Path, codes: dict[str, str], result: dict[str, Any]) -> None:
    reference = f"CODEX_BM_REF_ARC_{codes['suffix']}"
    insert_rate_reference(project_dir, reference)
    route = "/group-management/archive-management"
    open_route(page, route)
    page.get_by_role("button", name="新增档案").click()
    page.get_by_role("button", name="保存").click()
    expect(page.get_by_text("请输入档案ID", exact=True)).to_be_visible()
    fill(page, "档案ID", codes["archive"])
    fill(page, "档案名称", f"回归档案_{codes['suffix']}")
    select(page, "档案类型", "公司")
    fill(page, "预订代码", f"BOOK_{codes['suffix']}")
    fill(page, "档案联系人", "回归联系人")
    fill(page, "联系人电话", "13800000000")
    fill(page, "档案联系地址", "上海市回归测试地址")
    page.get_by_role("button", name="添加").click()
    selects = page.locator(".ant-table-tbody tr").last.locator(".ant-select-selector")
    selects.nth(0).click()
    page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text="上海锦江饭店1"
    ).last.click(force=True)
    selects.nth(1).click()
    selects.nth(1).locator("input[role='combobox']").fill(reference)
    page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(
        has_text=reference
    ).last.click(force=True)
    save_and_wait(page, route, "/api/archives")

    page.get_by_role("button", name="新增档案").click()
    fill(page, "档案ID", codes["archive"])
    fill(page, "档案名称", "重复档案")
    select(page, "档案类型", "公司")
    with page.expect_response(lambda response: response.request.method == "POST" and "/api/archives" in response.url) as duplicate:
        page.get_by_role("button", name="保存").click()
    assert duplicate.value.status == 400
    result["expectedRejections"].append("档案 ID 重复")
    open_route(page, route)

    fill(page, "档案ID", codes["archive"])
    page.locator("button").filter(has_text=re.compile(r"查\s*询")).first.click()
    row = table_row(page, codes["archive"])
    row.get_by_role("button", name="编辑").click()
    expect(form_item(page, "档案ID").locator("input")).to_be_disabled()
    fill(page, "档案名称", f"回归档案已编辑_{codes['suffix']}")
    select(page, "档案类型", "旅行社")
    save_and_wait(page, route, "/api/archives/")

    row = table_row(page, codes["archive"])
    row.locator(".ant-tag").filter(has_text="启用").click()
    with page.expect_response(lambda response: response.request.method == "PUT" and "/api/archives/" in response.url) as toggled:
        page.locator("button").filter(has_text=re.compile(r"^\s*确\s*定\s*$")).last.click()
    assert toggled.value.status == 200
    expect(table_row(page, codes["archive"])).to_contain_text("停用")
    result["checks"].append("档案必填/新增/酒店房价码分配/编辑/编码冻结/二次确认停用")


def main() -> int:
    project_dir = Path(__file__).resolve().parents[2]
    suffix = str(int(time.time()))[-8:]
    codes = {
        "suffix": suffix,
        "tax": f"CODEX_TAX_{suffix}",
        "package": f"CODEX_PKG_{suffix}",
        "guarantee": f"CODEX_GUA_{suffix}",
        "cancellation": f"CODEX_CAN_{suffix}",
        "facility": f"CODEX_FAC_{suffix}",
        "archive": f"CODEX_ARC_{suffix}",
    }
    result: dict[str, Any] = {
        "suffix": suffix,
        "checks": [],
        "expectedRejections": [],
        "errors": [],
        "warnings": [],
        "http5xx": [],
        "cleanup": {},
    }
    requested_modules = {
        item.strip() for item in os.environ.get("CRS_TEST_MODULES", "").split(",") if item.strip()
    }

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1000}, locale="zh-CN")

        def handle_console(message: Any) -> None:
            if message.type != "error":
                return
            if ("Failed to load resource" in message.text and "400" in message.text) \
                    or "There may be circular references" in message.text:
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
            page.wait_for_timeout(1800)
            result["checks"].append("登录与租户上下文")

            if not requested_modules or "tax" in requested_modules:
                test_tax(page, codes, result)
            if not requested_modules or "package" in requested_modules:
                test_package(page, project_dir, codes, result)
            if not requested_modules or "guarantee" in requested_modules:
                test_guarantee(page, project_dir, codes, result)
            if not requested_modules or "cancellation" in requested_modules:
                test_cancellation(page, project_dir, codes, result)
            if not requested_modules or "facility" in requested_modules:
                test_facility(page, codes, result)
            if not requested_modules or "archive" in requested_modules:
                test_archive(page, project_dir, codes, result)
        except Exception as error:  # noqa: BLE001 - 报告必须保留第一个真实失败
            result["errors"].append(f"test:{type(error).__name__}:{error}")
        finally:
            result["cleanup"] = cleanup_all(project_dir, codes)
            browser.close()

    report_path = Path("/private/tmp/crs-group-business-master-lifecycle.json")
    report_path.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 1 if result["errors"] or result["http5xx"] or any(result["cleanup"].values()) else 0


if __name__ == "__main__":
    raise SystemExit(main())

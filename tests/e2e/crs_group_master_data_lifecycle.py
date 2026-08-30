"""CRS 集团主数据页面全生命周与引用约束回归。

所有新增、编辑、删除和引用拒绝均由真实 Chromium 页面触发。
数据库仅用于创建可精确识别的引用夹具，以及在 finally 中定向清理夹具。
"""

from __future__ import annotations

import json
import os
import re
import subprocess
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

from playwright.sync_api import Locator, Page, expect, sync_playwright


BASE_URL = "http://127.0.0.1:3001"


@dataclass(frozen=True)
class MasterSpec:
    name: str
    route: str
    code_name: str
    prefix: str
    tree: bool


SPECS = (
    MasterSpec("市场码", "/group-management/market-code", "市场码", "MKT", True),
    MasterSpec("来源码", "/group-management/source-code", "来源码", "SRC", True),
    MasterSpec("渠道码", "/group-management/channel-code", "渠道码", "CHN", True),
    MasterSpec("房价大类", "/group-management/rate-category", "房价大类", "RAT", False),
    MasterSpec("房型大类", "/group-management/room-type-category", "房型大类", "RMT", False),
)


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
    return page.locator(".ant-form-item").filter(
        has=page.locator(".ant-form-item-label").filter(has_text=label)
    ).first


def node_row(page: Page, code: str) -> Locator:
    row = page.locator(".ant-tree-treenode").filter(has_text=code).first
    row.wait_for(timeout=20000)
    return row


def select_node(page: Page, code: str) -> None:
    wrapper = node_row(page, code).locator(".ant-tree-node-content-wrapper")
    if "ant-tree-node-selected" not in (wrapper.get_attribute("class") or ""):
        wrapper.click()
    expect(wrapper).to_have_class(re.compile(r"\bant-tree-node-selected\b"))


def modal_input(page: Page, label: str) -> Locator:
    return form_item(page, label).locator("input")


def submit_modal(page: Page) -> None:
    page.locator(".ant-modal:visible .ant-btn-primary").last.click()


def add_node(page: Page, spec: MasterSpec, code: str, name: str, parent_code: str | None = None) -> None:
    if parent_code is None:
        page.get_by_role("button", name="新增根节点").click()
    else:
        select_node(page, parent_code)
        page.get_by_role("button", name="新增子节点").click()
    modal_input(page, f"{spec.code_name}名称").fill(name)
    modal_input(page, f"{spec.code_name}CODE").fill(code)
    submit_modal(page)
    page.get_by_text("新增成功", exact=True).wait_for(timeout=20000)


def open_route(page: Page, route: str) -> None:
    page.goto(BASE_URL + route, wait_until="networkidle")
    page.locator(".ant-tree").wait_for(timeout=20000)


def edit_node(page: Page, spec: MasterSpec, code: str, new_name: str) -> None:
    select_node(page, code)
    page.get_by_role("button", name="修改").click()
    modal_input(page, f"{spec.code_name}名称").fill(new_name)
    submit_modal(page)
    page.get_by_text("修改成功", exact=True).wait_for(timeout=20000)
    expect(node_row(page, code)).to_contain_text(new_name)


def delete_node(page: Page, code: str, expected_status: int) -> None:
    select_node(page, code)
    page.get_by_role("button", name="删除").click()
    with page.expect_response(
        lambda response: response.request.method == "DELETE" and "/api/" in response.url,
        timeout=20000,
    ) as response_info:
        page.locator(".ant-modal-root button").filter(
            has_text=re.compile(r"^\s*确\s*定\s*$")
        ).last.click()
    assert response_info.value.status == expected_status, (
        response_info.value.status,
        response_info.value.url,
    )


def insert_reference_fixtures(project_dir: Path, codes: dict[str, list[str]], suffix: str) -> None:
    market_leaf = sql_escape(codes["MKT"][-1])
    source_leaf = sql_escape(codes["SRC"][-1])
    channel_leaf = sql_escape(codes["CHN"][-1])
    rate_category = sql_escape(codes["RAT"][0])
    room_category = sql_escape(codes["RMT"][0])
    rate_code = sql_escape(f"CODEX_MD_RATE_{suffix}")
    room_code = sql_escape(f"CODEX_MD_ROOM_{suffix}")
    sql = f"""
START TRANSACTION;
INSERT INTO group_rate_codes
  (group_id, tenant_id, created_at, updated_at, status, rate_category, rate_code, rate_name,
   market_code, source_code, derivative_level, rate_type)
VALUES
  (1, 1, NOW(6), NOW(6), 'active', '{rate_category}', '{rate_code}', '主数据引用测试',
   '{market_leaf}', '{source_leaf}', 'basic', 'basic');
INSERT INTO group_room_types
  (group_id, tenant_id, room_type_category_id, room_type_category_code, created_at, updated_at,
   status, room_type_code, room_type_name)
SELECT 1, 1, id, category_code, NOW(6), NOW(6), 'active', '{room_code}', '主数据引用测试'
FROM room_type_categories WHERE tenant_id=1 AND category_code='{room_category}';
INSERT INTO channel_publish_records
  (tenant_id, hotel_code, channel_code, rate_code, room_type_code, status)
VALUES (1, 'JJSH001', '{channel_leaf}', '{rate_code}', '{room_code}', 'published');
COMMIT;
"""
    run_sql(project_dir, sql)


def remove_reference_fixtures(project_dir: Path, suffix: str) -> None:
    rate_code = sql_escape(f"CODEX_MD_RATE_{suffix}")
    room_code = sql_escape(f"CODEX_MD_ROOM_{suffix}")
    run_sql(
        project_dir,
        f"""
START TRANSACTION;
DELETE FROM channel_publish_records WHERE tenant_id=1 AND rate_code='{rate_code}';
DELETE FROM group_room_types WHERE group_id=1 AND room_type_code='{room_code}';
DELETE FROM group_rate_codes WHERE group_id=1 AND rate_code='{rate_code}';
COMMIT;
""",
    )


def cleanup_all(project_dir: Path, codes: dict[str, list[str]], suffix: str) -> dict[str, int]:
    remove_reference_fixtures(project_dir, suffix)
    all_codes = [sql_escape(code) for values in codes.values() for code in values]
    quoted = ",".join(f"'{code}'" for code in all_codes) or "''"
    output = run_sql(
        project_dir,
        f"""
START TRANSACTION;
DELETE FROM market_codes WHERE tenant_id=1 AND code IN ({quoted});
DELETE FROM source_codes WHERE tenant_id=1 AND code IN ({quoted});
DELETE FROM channel_codes WHERE tenant_id=1 AND code IN ({quoted});
DELETE FROM rate_types WHERE tenant_id=1 AND code IN ({quoted});
DELETE FROM room_type_categories WHERE tenant_id=1 AND category_code IN ({quoted});
COMMIT;
SELECT
  (SELECT COUNT(*) FROM market_codes WHERE tenant_id=1 AND code IN ({quoted})),
  (SELECT COUNT(*) FROM source_codes WHERE tenant_id=1 AND code IN ({quoted})),
  (SELECT COUNT(*) FROM channel_codes WHERE tenant_id=1 AND code IN ({quoted})),
  (SELECT COUNT(*) FROM rate_types WHERE tenant_id=1 AND code IN ({quoted})),
  (SELECT COUNT(*) FROM room_type_categories WHERE tenant_id=1 AND category_code IN ({quoted}));
""",
    )
    values = [int(value) for value in output.split("\t")[-5:]]
    return dict(zip(["market", "source", "channel", "rateCategory", "roomCategory"], values))


def main() -> int:
    project_dir = Path(__file__).resolve().parents[2]
    suffix = str(int(time.time()))[-8:]
    codes = {
        spec.prefix: [f"CODEX_{spec.prefix}_{level}_{suffix}" for level in range(1, 4 if spec.tree else 2)]
        for spec in SPECS
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

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1440, "height": 1000}, locale="zh-CN")

        def handle_console(message: Any) -> None:
            if message.type != "error":
                return
            if "Failed to load resource" in message.text and "400" in message.text:
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
            # 等待超级管理员默认租户上下文完成持久化。
            page.wait_for_timeout(1800)
            result["checks"].append("登录")

            for spec in SPECS:
                open_route(page, spec.route)
                entity_codes = codes[spec.prefix]

                # 共享组件的字段校验，每个业务页都真实提交一次。
                page.get_by_role("button", name="新增根节点").click()
                modal_input(page, f"{spec.code_name}名称").fill("非法编码")
                modal_input(page, f"{spec.code_name}CODE").fill("BAD-CODE")
                submit_modal(page)
                page.get_by_text(f"{spec.code_name}CODE只能包含英文字母、数字和下划线", exact=True).wait_for()
                page.locator(".ant-modal:visible .ant-modal-close").click()
                page.locator(".ant-modal:visible").wait_for(state="hidden")

                add_node(page, spec, entity_codes[0], f"{spec.name}根_{suffix}")
                open_route(page, spec.route)

                # 重复编码必须在页面字段层被拒绝。
                page.get_by_role("button", name="新增根节点").click()
                modal_input(page, f"{spec.code_name}名称").fill("重复编码")
                modal_input(page, f"{spec.code_name}CODE").fill(entity_codes[0])
                submit_modal(page)
                page.get_by_text(f"{spec.code_name}CODE已存在", exact=True).wait_for(timeout=20000)
                page.locator(".ant-modal:visible .ant-modal-close").click()
                page.locator(".ant-modal:visible").wait_for(state="hidden")
                result["expectedRejections"].append(f"{spec.name}重复编码")

                if spec.tree:
                    add_node(page, spec, entity_codes[1], f"{spec.name}二级_{suffix}", entity_codes[0])
                    open_route(page, spec.route)
                    add_node(page, spec, entity_codes[2], f"{spec.name}三级_{suffix}", entity_codes[1])
                    open_route(page, spec.route)

                    # 第四级由页面发起，服务端必须拒绝。
                    select_node(page, entity_codes[2])
                    page.get_by_role("button", name="新增子节点").click()
                    modal_input(page, f"{spec.code_name}名称").fill("不应创建的四级")
                    modal_input(page, f"{spec.code_name}CODE").fill(f"CODEX_{spec.prefix}_4_{suffix}")
                    with page.expect_response(
                        lambda response: response.request.method == "POST" and "/api/" in response.url,
                        timeout=20000,
                    ) as rejected:
                        submit_modal(page)
                    assert rejected.value.status == 400, rejected.value.status
                    page.locator(".ant-modal:visible .ant-modal-close").click()
                    page.locator(".ant-modal:visible").wait_for(state="hidden")
                    result["expectedRejections"].append(f"{spec.name}第四级节点")

                leaf = entity_codes[-1]
                edit_node(page, spec, leaf, f"{spec.name}已编辑_{suffix}")
                result["checks"].append(f"{spec.name}新增/层级/编辑/唯一性")

            insert_reference_fixtures(project_dir, codes, suffix)

            # 从页面真实执行有风险的删除，验证父节点、叶子节点和一级大类的引用约束。
            for spec in SPECS:
                open_route(page, spec.route)
                delete_node(page, codes[spec.prefix][0], 400)
                expect(node_row(page, codes[spec.prefix][0])).to_be_visible()
                result["expectedRejections"].append(f"{spec.name}被引用时删除")

            remove_reference_fixtures(project_dir, suffix)

            # 解除引用后，再通过页面删除所有夹具。
            for spec in SPECS:
                open_route(page, spec.route)
                expected = 204 if spec.prefix in {"MKT", "SRC", "CHN"} else 200
                delete_node(page, codes[spec.prefix][0], expected)
                page.get_by_text("删除成功", exact=True).wait_for(timeout=20000)
                expect(page.locator(".ant-tree-treenode").filter(has_text=codes[spec.prefix][0])).to_have_count(0)
                result["checks"].append(f"{spec.name}解除引用后页面删除")
        except Exception as exc:  # noqa: BLE001 - 统一写入测试报告后再抛出。
            page.screenshot(path="/private/tmp/crs-group-master-data-failure.png", full_page=True)
            result["errors"].append(repr(exc))
        finally:
            try:
                result["cleanup"] = cleanup_all(project_dir, codes, suffix)
            except Exception as cleanup_error:  # noqa: BLE001
                result["errors"].append(f"cleanup:{cleanup_error!r}")
            browser.close()

    output = Path("/private/tmp/crs-group-master-data-lifecycle.json")
    output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 1 if result["errors"] or result["http5xx"] or any(result["cleanup"].values()) else 0


if __name__ == "__main__":
    raise SystemExit(main())

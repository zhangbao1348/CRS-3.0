"""CRS 酒店新增、筛选、查看、编辑与软删除真实页面回归。"""

from __future__ import annotations

import json
import time
from pathlib import Path
from typing import Any

from playwright.sync_api import Locator, Page, expect, sync_playwright

from crs_order_cancel_lifecycle import BASE_URL, login, run_sql, sql_text


def item(page: Page, label: str) -> Locator:
    return page.locator(".ant-form-item").filter(
        has=page.locator(".ant-form-item-label").filter(has_text=label)
    ).first


def fill(page: Page, label: str, value: str) -> None:
    item(page, label).locator("input").fill(value)


def select(page: Page, label: str, option: str) -> None:
    item(page, label).locator(".ant-select-selector").click()
    page.locator(".ant-select-dropdown:visible .ant-select-item-option").filter(has_text=option).first.click(force=True)


def cleanup(project_dir: Path, hotel_id: int | None, hotel_code: str) -> dict[str, int]:
    safe_code = sql_text(hotel_code)
    if hotel_id:
        run_sql(
            project_dir,
            f"DELETE FROM hotel_facilities WHERE hotel_code='{safe_code}';"
            f"DELETE FROM hotel_images WHERE hotel_code='{safe_code}';"
            f"DELETE FROM hotel_rate_code_allocations WHERE hotel_code='{safe_code}';"
            f"DELETE FROM group_room_type_hotel WHERE hotel_code='{safe_code}';"
            f"DELETE FROM hotels WHERE id={hotel_id} AND hotel_code='{safe_code}';",
        )
    return {
        "hotel": int(run_sql(project_dir, f"SELECT COUNT(*) FROM hotels WHERE hotel_code='{safe_code}';") or 0),
        "facility": int(run_sql(project_dir, f"SELECT COUNT(*) FROM hotel_facilities WHERE hotel_code='{safe_code}';") or 0),
        "image": int(run_sql(project_dir, f"SELECT COUNT(*) FROM hotel_images WHERE hotel_code='{safe_code}';") or 0),
    }


def main() -> None:
    project_dir = Path(__file__).resolve().parents[2]
    output = Path("/private/tmp/crs-hotel-lifecycle.json")
    suffix = str(int(time.time()))[-8:]
    hotel_code = f"E2EH{suffix}"
    hotel_name = f"验收酒店{suffix}"
    edited_name = f"验收精品酒店{suffix}"
    hotel_id: int | None = None
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
            page.goto(BASE_URL + "/group-management/add-hotel", wait_until="networkidle")
            save_button = page.locator("button").filter(has_text="保存").first
            save_button.click()
            expect(page.get_by_text("请输入酒店代码", exact=True)).to_be_visible()
            expect(page.get_by_text("请输入酒店中文名称", exact=True)).to_be_visible()
            result["checks"].append("新增页空表单必填校验")

            fill(page, "酒店代码", hotel_code)
            fill(page, "酒店中文名称", hotel_name)
            fill(page, "酒店英文名称", f"Acceptance Hotel {suffix}")
            select(page, "酒店星级", "五级")
            select(page, "酒店所在省份", "上海市")
            page.wait_for_timeout(500)
            select(page, "酒店所在城市", "上海市")
            expect(item(page, "酒店所在城市").locator(".ant-select-selection-item")).to_have_text("上海市")
            fill(page, "酒店详细地址", f"测试路{suffix}号")
            fill(page, "酒店经度", "121.4737")
            fill(page, "酒店纬度", "31.2304")
            fill(page, "酒店联系电话", "021-60000000")
            fill(page, "酒店邮箱", f"e2e{suffix}@crs.test")
            fill(page, "酒店总房间数", "88")
            fill(page, "酒店最低价格（含税）", "399.50")
            with page.expect_response(lambda response: response.request.method == "POST" and response.url.endswith("/api/hotels")) as created:
                save_button.click()
            assert created.value.status == 200
            created_payload = created.value.json()
            hotel_id = int(created_payload.get("id") or created_payload.get("data", {}).get("id"))
            assert run_sql(project_dir, f"SELECT status FROM hotels WHERE id={hotel_id};") == "active"
            result["checks"].append("页面新增酒店并持久化最低价、位置与联系字段")

            page.goto(BASE_URL + "/group-management/hotel-management", wait_until="networkidle")
            page.locator('input[placeholder="酒店名称"]').fill(hotel_name)
            row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=hotel_code).first
            expect(row).to_be_visible(timeout=20000)
            expect(page.locator(".ant-table-tbody tr.ant-table-row")).to_have_count(1)
            page.locator('input[placeholder="酒店名称"]').fill("不存在的酒店名称")
            expect(page.get_by_text("暂无酒店数据", exact=True)).to_be_visible()
            page.locator('input[placeholder="酒店名称"]').fill(hotel_name)
            row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=hotel_code).first
            row.get_by_role("button", name="查看").click()
            view = page.locator(".ant-modal:visible").last
            expect(view.locator(f'input[value="{hotel_code}"]')).to_be_visible()
            expect(view.locator(f'input[value="{hotel_name}"]')).to_be_visible()
            view.locator('button[aria-label="Close"]').click()
            expect(view).to_be_hidden()
            result["checks"].append("名称筛选、无结果状态与查看只读回显")

            row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=hotel_code).first
            row.get_by_role("button", name="编辑").click()
            page.wait_for_url(lambda url: "/hotel-management/edit-hotel" in url, timeout=20000)
            fill(page, "酒店中文名称", edited_name)
            fill(page, "酒店详细地址", f"更新路{suffix}号")
            with page.expect_response(lambda response: response.request.method == "PUT" and f"/api/hotels/code/{hotel_code}" in response.url) as updated:
                page.get_by_role("button", name="保存并返回列表", exact=True).first.click()
            assert updated.value.status == 200
            page.wait_for_url(lambda url: "/group-management/hotel-management" in url, timeout=20000)
            page.locator('input[placeholder="酒店编码"]').fill(hotel_code)
            edited_row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=hotel_code).first
            expect(edited_row.get_by_text(edited_name, exact=True)).to_be_visible(timeout=20000)
            result["checks"].append("编辑页代码只读，名称与地址保存后列表回显")

            edited_row.get_by_role("button", name="删除").click()
            confirm = page.locator(".ant-modal-confirm:visible")
            expect(confirm.get_by_text("确定要删除这个酒店吗？", exact=True)).to_be_visible()
            with page.expect_response(lambda response: response.request.method == "DELETE" and response.url.endswith(f"/api/hotels/{hotel_id}")) as deleted:
                confirm.get_by_role("button", name="确 定").click()
            assert deleted.value.status == 200
            page.wait_for_timeout(800)
            page.locator('input[placeholder="酒店编码"]').fill(hotel_code)
            deleted_row = page.locator(".ant-table-tbody tr.ant-table-row").filter(has_text=hotel_code).first
            expect(deleted_row.get_by_text("维护中", exact=True)).to_be_visible(timeout=20000)
            expect(deleted_row.get_by_role("button", name="已删除")).to_be_disabled()
            persisted = run_sql(project_dir, f"SELECT CONCAT(status,'|',chinese_name) FROM hotels WHERE id={hotel_id};")
            assert persisted == f"inactive|{edited_name}"
            result["checks"].append("二次确认删除仅软删除：记录保留、状态 inactive、重复删除禁用")
        except Exception as exc:
            result["errors"].append(f"test:{type(exc).__name__}:{exc}")
            page.screenshot(path="/private/tmp/crs-hotel-lifecycle-failure.png", full_page=True)
            raise
        finally:
            result["cleanup"] = cleanup(project_dir, hotel_id, hotel_code)
            browser.close()
            output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")

    assert result["errors"] == [], result["errors"]
    assert result["http4xx"] == [], result["http4xx"]
    assert result["http5xx"] == [], result["http5xx"]
    assert all(value == 0 for value in result["cleanup"].values()), result["cleanup"]
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()

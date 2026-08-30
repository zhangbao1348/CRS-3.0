"""CRS 需求页面 UI 审计。

关联模块：SOW 功能清单、React Router、Ant Design 页面层。
仅执行登录、导航和只读 DOM 检查；不创建、修改或删除业务数据。
"""

from __future__ import annotations

import argparse
import json
import os
from pathlib import Path
from typing import Any

from playwright.sync_api import TimeoutError as PlaywrightTimeoutError
from playwright.sync_api import sync_playwright


ROUTES = [
    ("首页", "/dashboard", "门店驾驶舱"),
    ("订单管理", "/reservation/reservation-list", "订单"),
    ("房控日历", "/inventory", "房控"),
    ("房态管理", "/inventory/room-status", "房态"),
    ("预订控制", "/inventory/booking-control", "预订控制"),
    ("房型管理", "/room-management/room-type", "房型"),
    ("价格计划", "/rate-management/rate-plan", "价格计划"),
    ("基础价格", "/rate-management/rack-rate", "价格"),
    ("包价设置", "/rate-management/package-setting", "包价"),
    ("价格查询", "/rate-management/price-query", "价格查询"),
    ("渠道列表", "/channel-management/channel-list", "渠道"),
    ("渠道映射", "/channel-management/channel-mapping", "映射"),
    ("订单报表", "/reports/reservation-reports", "订单分析报表"),
    ("出租率报表", "/reports/occupancy-reports", "出租率"),
    ("营收报表", "/reports/revenue-reports", "营收"),
    ("系统追踪", "/reports/system-trace", "追踪"),
    ("数据导出", "/reports/data-export", "数据导出"),
    ("酒店管理", "/group-management/hotel-management", "酒店"),
    ("集团房型", "/group-management/group-room-type", "集团房型"),
    ("集团房价码", "/group-management/group-rate-code", "房价码"),
    ("市场码", "/group-management/market-code", "市场码"),
    ("房价大类", "/group-management/rate-category", "房价大类"),
    ("房型大类", "/group-management/room-type-category", "房型大类"),
    ("渠道码", "/group-management/channel-code", "渠道码"),
    ("来源码", "/group-management/source-code", "来源码"),
    ("税和服务费", "/group-management/tax-setting", "税"),
    ("集团包价", "/group-management/package-setting", "包价"),
    ("担保政策", "/group-management/group-guarantee", "担保"),
    ("取消政策", "/group-management/group-cancellation", "取消"),
    ("集团设施", "/group-management/facility-management", "设施"),
    ("档案管理", "/group-management/archive-management", "档案"),
    ("用户管理", "/system-settings/user-management", "用户"),
    ("角色管理", "/system-settings/role-management", "角色"),
    ("集团设置", "/system-settings/group-settings", "集团设置"),
    ("字典管理", "/system-settings/dictionary-management", "字典管理"),
]

SCREENSHOT_ROUTES = {
    "/dashboard",
    "/reservation/reservation-list",
    "/inventory",
    "/inventory/room-status",
    "/inventory/booking-control",
    "/room-management/room-type",
    "/rate-management/rate-plan",
    "/channel-management/channel-mapping",
    "/group-management/hotel-management",
    "/group-management/group-rate-code",
    "/reports/reservation-reports",
    "/system-settings/user-management",
    "/system-settings/group-settings",
    "/system-settings/dictionary-management",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="CRS 页面、字段和 UI 结构只读审计")
    parser.add_argument("--base-url", default="http://127.0.0.1:3001")
    parser.add_argument("--output", default="/private/tmp/crs-ui-audit.json")
    parser.add_argument("--screenshots", default="/private/tmp/crs-ui-audit")
    parser.add_argument("--width", type=int, default=1440)
    parser.add_argument("--height", type=int, default=1000)
    parser.add_argument(
        "--route",
        action="append",
        help="仅审计指定路由；可重复传入，默认审计全部核心路由",
    )
    return parser.parse_args()


def audit_page(page, base_url: str, module: str, path: str, expected_text: str) -> dict[str, Any]:
    console_errors: list[str] = []
    response_errors: list[str] = []

    def on_console(message) -> None:
        if message.type == "error":
            console_errors.append(message.text)

    def on_response(response) -> None:
        if response.status >= 500:
            response_errors.append(f"{response.status} {response.url}")

    page.on("console", on_console)
    page.on("response", on_response)
    navigation_error = None
    try:
        page.goto(base_url + path, wait_until="domcontentloaded", timeout=30000)
        try:
            page.wait_for_load_state("networkidle", timeout=12000)
        except PlaywrightTimeoutError:
            navigation_error = "networkidle_timeout"
        page.wait_for_timeout(350)
    except Exception as error:  # Playwright error details belong in the audit artifact.
        navigation_error = str(error)

    body_text = page.locator("body").inner_text() if page.locator("body").count() else ""
    metrics = page.evaluate(
        """() => {
          const isVisible = (item) => {
            const style = getComputedStyle(item);
            return item.offsetParent !== null && style.visibility !== 'hidden' && style.display !== 'none';
          };
          const buttons = [...document.querySelectorAll('button')].filter(isVisible);
          const iconOnly = buttons.filter((item) => {
            const text = (item.innerText || '').trim();
            const descendantLabel = item.querySelector('[aria-label]')?.getAttribute('aria-label');
            return !text && !item.getAttribute('aria-label') && !item.getAttribute('title') && !descendantLabel;
          });
          const labels = [...document.querySelectorAll('.ant-form-item-label label')]
            .filter((item) => item.offsetParent !== null)
            .map((item) => (item.innerText || '').trim())
            .filter(Boolean);
          return {
            title: document.title,
            viewportWidth: window.innerWidth,
            documentWidth: document.documentElement.scrollWidth,
            horizontalOverflow: document.documentElement.scrollWidth > window.innerWidth + 2,
            visibleInputs: [...document.querySelectorAll('input, textarea, [role="combobox"]')]
              .filter((item) => item.offsetParent !== null).length,
            visibleButtons: buttons.length,
            visibleTables: [...document.querySelectorAll('.ant-table')]
              .filter((item) => item.offsetParent !== null).length,
            visibleTableRows: [...document.querySelectorAll('.ant-table-tbody > tr:not(.ant-table-measure-row)')]
              .filter(isVisible).length,
            visibleEmptyStates: [...document.querySelectorAll('.ant-empty')]
              .filter(isVisible).length,
            visibleStatisticValues: [...document.querySelectorAll('.ant-statistic-content')]
              .filter(isVisible)
              .map((item) => (item.innerText || '').trim()),
            visibleRequiredFields: [...document.querySelectorAll('.ant-form-item-required')]
              .filter((item) => item.offsetParent !== null).length,
            visibleFieldLabels: [...new Set(labels)],
            unlabeledIconButtons: iconOnly.length,
            unlabeledIconButtonSamples: iconOnly.slice(0, 5).map((item) => ({
              className: item.className,
              html: item.outerHTML.slice(0, 500),
            })),
          };
        }"""
    )
    page.remove_listener("console", on_console)
    page.remove_listener("response", on_response)
    return {
        "module": module,
        "path": path,
        "expectedText": expected_text,
        "expectedTextFound": expected_text in body_text,
        "unexpectedApplicationError": "Unexpected Application Error" in body_text,
        "dynamicImportError": "Failed to fetch dynamically imported module" in body_text,
        "navigationError": navigation_error,
        "consoleErrors": console_errors,
        "response5xx": response_errors,
        **metrics,
    }


def main() -> int:
    args = parse_args()
    username = os.environ.get("CRS_TEST_USERNAME")
    password = os.environ.get("CRS_TEST_PASSWORD")
    if not username or not password:
        raise SystemExit("请通过 CRS_TEST_USERNAME 和 CRS_TEST_PASSWORD 提供本地测试账号")

    output_path = Path(args.output)
    screenshot_dir = Path(args.screenshots)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    screenshot_dir.mkdir(parents=True, exist_ok=True)

    result: dict[str, Any] = {
        "baseUrl": args.base_url,
        "viewport": {"width": args.width, "height": args.height},
        "routes": [],
    }

    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        context = browser.new_context(
            base_url=args.base_url,
            viewport={"width": args.width, "height": args.height},
            locale="zh-CN",
        )
        page = context.new_page()
        page.goto(args.base_url + "/login", wait_until="networkidle")
        page.locator('input[autocomplete="username"]').fill(username)
        page.locator('input[autocomplete="current-password"]').fill(password)
        page.get_by_role("button", name="进入工作台").click()
        page.wait_for_url(lambda url: "/login" not in url, timeout=30000)
        page.wait_for_timeout(1500)

        result["colorBaseline"] = page.evaluate(
            """() => {
              const read = (selector, property) => {
                const node = document.querySelector(selector);
                return node ? getComputedStyle(node)[property] : null;
              };
              return {
                bodyBackground: read('body', 'backgroundColor'),
                bodyText: read('body', 'color'),
                siderBackground: read('.ant-layout-sider', 'backgroundColor'),
                headerBackground: read('.ant-layout-header', 'backgroundColor'),
                contentBackground: read('.ant-layout-content', 'backgroundColor'),
                cardBackground: read('.ant-card', 'backgroundColor'),
                primaryButtonBackground: read('.ant-btn-primary', 'backgroundColor'),
              };
            }"""
        )

        selected_paths = set(args.route or [])
        selected_routes = [
            route for route in ROUTES if not selected_paths or route[1] in selected_paths
        ]
        missing_paths = selected_paths - {route[1] for route in selected_routes}
        if missing_paths:
            raise SystemExit(f"未知审计路由: {', '.join(sorted(missing_paths))}")

        for module, path, expected_text in selected_routes:
            route_result = audit_page(page, args.base_url, module, path, expected_text)
            result["routes"].append(route_result)
            if path in SCREENSHOT_ROUTES:
                safe_name = path.strip("/").replace("/", "__") or "home"
                page.screenshot(path=str(screenshot_dir / f"{safe_name}.png"), full_page=True)

        browser.close()

    routes = result["routes"]
    result["summary"] = {
        "routeCount": len(routes),
        "expectedTextPassed": sum(item["expectedTextFound"] for item in routes),
        "applicationErrors": sum(item["unexpectedApplicationError"] for item in routes),
        "dynamicImportErrors": sum(item["dynamicImportError"] for item in routes),
        "navigationErrors": sum(bool(item["navigationError"]) for item in routes),
        "consoleErrors": sum(len(item["consoleErrors"]) for item in routes),
        "response5xx": sum(len(item["response5xx"]) for item in routes),
        "horizontalOverflowPages": sum(item["horizontalOverflow"] for item in routes),
        "unlabeledIconButtons": sum(item["unlabeledIconButtons"] for item in routes),
    }
    output_path.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result["summary"], ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

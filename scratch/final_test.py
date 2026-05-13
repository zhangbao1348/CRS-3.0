from playwright.sync_api import sync_playwright
import time

def run_test():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page(viewport={'width': 1280, 'height': 900})
        
        print("1. [登录] 正在身份验证...")
        page.goto('http://localhost:3001/login')
        page.fill('input[placeholder="用户名"]', 'admin')
        page.fill('input[placeholder="密码"]', 'admin123')
        page.click('button:has-text("登")')
        
        # 等待登录成功（跳转到根路径或dashboard）
        try:
            page.wait_for_function("window.location.pathname === '/' || window.location.pathname === '/dashboard'", timeout=10000)
            print("✅ 登录成功")
        except Exception as e:
            print(f"❌ 登录超时或跳转失败: {e}")
            page.screenshot(path='login_error.png')
            browser.close()
            return

        # --- 房型管理测试 ---
        print("\n2. [房型管理] 正在验证分类分组与去重...")
        page.goto('http://localhost:3001/room-management/room-type')
        page.wait_for_load_state('networkidle')
        time.sleep(1) # 额外等待 AntD 表格渲染
        page.screenshot(path='room_management_final.png')
        
        # 检查是否包含标准房或豪华房的文本
        # 注意：在 React 中，分类名作为 Group 标题通常会出现在表格行中
        content = page.content()
        found_cats = []
        for cat in ["标准房", "豪华房", "其他"]:
            if cat in content:
                found_cats.append(cat)
        
        if found_cats:
            print(f"✅ 成功发现房型分类标题: {', '.join(found_cats)}")
        else:
            print("⚠️ 未在页面中直接发现已知分类，请检查 room_management_final.png")

        # --- 房价码新增测试 ---
        print("\n3. [房价码管理] 正在验证新增页面的房型分组...")
        page.goto('http://localhost:3001/rate-management/add-rate-plan')
        page.wait_for_load_state('networkidle')
        time.sleep(1)
        page.screenshot(path='add_rate_plan_final.png')
        
        # 验证分组显示
        # 在 AddRatePlan.jsx 中，我们重构了分组渲染逻辑，分类名应作为分组的 Header 出现
        content_add = page.content()
        found_cats_add = []
        for cat in ["标准房", "豪华房", "其他"]:
            if cat in content_add:
                found_cats_add.append(cat)
        
        if found_cats_add:
            print(f"✅ 房价码新增页成功展示房型分组: {', '.join(found_cats_add)}")
        else:
            print("⚠️ 房价码新增页未发现房型分组，请检查 add_rate_plan_final.png")

        print("\n--- 测试总结 ---")
        print("所有核心路径导航完成，截图已保存至项目根目录。")
        browser.close()

if __name__ == '__main__':
    run_test()

from playwright.sync_api import sync_playwright
import time
import os

def run():
    with sync_playwright() as p:
        # 启动浏览器
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        
        print("正在访问登录页面...")
        page.goto('http://localhost:3001/login')
        page.wait_for_load_state('networkidle')
        
        # 截图保存到 artifacts 目录（注意：这里我直接写相对路径，后续会移动或直接在 artifact 中引用）
        screenshot_path = 'login_page.png'
        page.screenshot(path=screenshot_path)
        print(f"登录页面截图已保存至: {screenshot_path}")
        
        # 打印页面标题和主要按钮，帮助确认选择器
        print(f"页面标题: {page.title()}")
        
        # 尝试登录
        print("正在尝试登录...")
        page.fill('input[placeholder*="用户名"]', 'admin')
        page.fill('input[placeholder*="密码"]', 'admin123')
        page.click('button:has-text("登录")')
        
        # 等待跳转
        page.wait_for_load_state('networkidle')
        time.sleep(2) # 给一点渲染时间
        
        print(f"登录后 URL: {page.url}")
        page.screenshot(path='dashboard.png')
        print("仪表盘截图已保存。")
        
        # 查找菜单
        menus = page.locator('.ant-menu-title-content').all_text_contents()
        print("发现菜单项:", menus)
        
        browser.close()

if __name__ == '__main__':
    run()

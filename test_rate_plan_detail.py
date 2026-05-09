from playwright.sync_api import sync_playwright
import time

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    
    console_logs = []
    page.on('console', lambda msg: console_logs.append(f"[{msg.type}] {msg.text}"))
    
    page.goto('http://localhost:3001/login')
    page.wait_for_load_state('networkidle')
    page.fill('input[placeholder="请输入用户名"]', 'admin')
    page.fill('input[placeholder="请输入密码"]', 'admin123')
    page.click('button[type="submit"]')
    page.wait_for_load_state('networkidle')
    time.sleep(2)
    
    page.goto('http://localhost:3001/rate-management')
    page.wait_for_load_state('networkidle')
    time.sleep(2)
    
    hotel_select = page.locator('.ant-select').first
    hotel_select.click()
    time.sleep(1)
    options = page.locator('.ant-select-item')
    if options.count() > 0:
        options.first.click()
    time.sleep(2)
    
    page.screenshot(path='/tmp/rate_plan_list.png', full_page=True)
    print("列表页截图已保存")
    
    edit_btns = page.locator('button:has-text("编辑")')
    if edit_btns.count() > 0:
        edit_btns.first.click()
        time.sleep(3)
        page.wait_for_load_state('networkidle')
        
        page.screenshot(path='/tmp/rate_plan_detail.png', full_page=True)
        print("详情页截图已保存")
        
        form_items = page.locator('.ant-form-item')
        for i in range(min(form_items.count(), 20)):
            item = form_items.nth(i)
            label = item.locator('.ant-form-item-label').text_content().strip() if item.locator('.ant-form-item-label').count() > 0 else ''
            value = item.locator('.ant-select-selection-item, .ant-input, .ant-input-number').first.text_content().strip() if item.locator('.ant-select-selection-item, .ant-input, .ant-input-number').count() > 0 else ''
            if label and value:
                print(f"  {label}: {value}")
        
        errors = [log for log in console_logs if '[error]' in log]
        if errors:
            print(f"\n控制台错误:")
            for e in errors[:10]:
                print(f"  {e}")
    else:
        print("未找到编辑按钮")
    
    browser.close()

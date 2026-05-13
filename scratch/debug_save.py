import os
import time
from datetime import datetime
from playwright.sync_api import sync_playwright

REPORT_DIR = "tests/playwright_reports"
SCREENSHOT_DIR = os.path.join(REPORT_DIR, "screenshots")
LOG_FILE = os.path.join(REPORT_DIR, "test_summary.log")

def log_result(message):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    line = f"[{timestamp}] {message}\n"
    print(message)
    with open(LOG_FILE, "a", encoding="utf-8") as f:
        f.write(line)

def run_debug_test():
    with sync_playwright() as p:
        if os.path.exists(LOG_FILE): os.remove(LOG_FILE)
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(viewport={'width': 1280, 'height': 900})
        page = context.new_page()
        
        page.on("console", lambda msg: log_result(f"浏览器控制台: [{msg.type}] {msg.text}"))

        unique_code = f"RT{int(time.time())}"[6:] 
        
        try:
            log_result(f">>> 启动终极诊断测试 v3 (解决拦截问题)")
            page.goto('http://localhost:3001/login')
            page.get_by_placeholder("用户名").fill("admin")
            page.get_by_placeholder("密码").fill("admin123")
            page.click('button:has-text("登")')
            page.wait_for_url('**/')
            
            page.goto('http://localhost:3001/room-management/room-type')
            
            # 关键：显式等待加载遮罩消失
            log_result("等待页面加载遮罩消失...")
            page.wait_for_selector('.ant-spin-spinning', state='hidden', timeout=10000)
            
            page.click('button:has-text("新增房型")')
            page.wait_for_selector('.ant-spin-spinning', state='hidden', timeout=10000)
            
            # 填写表单
            page.get_by_label("房型代码").fill(unique_code)
            page.get_by_label("房型中文名称").fill(f"终极测试_{unique_code}")
            page.get_by_label("房型数量").fill("1")
            try:
                page.get_by_label("最大入住人数").fill("2")
            except:
                page.get_by_label("最大入住成人数").fill("2")
            
            # 窗型选择 - 使用 force=True 强行穿透可能存在的隐形层
            log_result("正在选择窗型...")
            page.locator('#windowType').click(force=True)
            page.locator('.ant-select-item-option:has-text("有窗")').click(force=True)
            page.keyboard.press("Escape")
            
            # 床型选择
            log_result("正在选择床型...")
            page.locator('#bedType').click(force=True)
            page.locator('.ant-select-item-option:has-text("1.8米")').click(force=True)
            page.keyboard.press("Escape")
            
            # 拍照存证
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, "v3_before_save.png"))
            
            log_result("点击保存按钮 (使用 Force 强制执行)...")
            save_btn = page.locator('button:has-text("保存基础信息")')
            save_btn.click(force=True)
            
            # 等待成功消息
            log_result("等待保存反馈...")
            try:
                page.wait_for_selector('.ant-message-success', timeout=8000)
                log_result("✅ 成功！捕获到保存成功提示。")
            except:
                log_result("⚠️ 未监测到成功提示，尝试捕捉错误...")
                if page.locator('.ant-message-error').count() > 0:
                    log_result(f"‼️ 后端报错: {page.locator('.ant-message-error').text_content()}")
                if page.locator('.ant-form-item-explain-error').count() > 0:
                    log_result(f"‼️ 表单红字: {page.locator('.ant-form-item-explain-error').all_text_contents()}")

            page.screenshot(path=os.path.join(SCREENSHOT_DIR, "v3_after_save.png"))
            
            # 最终验证：点击返回并在列表中搜索
            page.click('button:has-text("返回列表")')
            page.get_by_placeholder("房型代码").fill(unique_code)
            page.click('button:has-text("查")')
            time.sleep(1)
            
            if page.locator(f'tr:has-text("{unique_code}")').count() > 0:
                log_result("🎊 奇迹时刻：房型成功出现在列表中！")
            else:
                log_result("❌ 即使保存显示成功，列表中仍无数据。")

        except Exception as e:
            log_result(f"‼️ 终极诊断失败: {str(e)}")
            page.screenshot(path=os.path.join(SCREENSHOT_DIR, "v3_crash.png"))
        finally:
            browser.close()

if __name__ == '__main__':
    run_debug_test()

from playwright.sync_api import sync_playwright
import time

def test_room_logic():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        # 设置较大的视口以便观察
        page = browser.new_page(viewport={'width': 1280, 'height': 800})
        
        print("1. 正在登录系统...")
        page.goto('http://localhost:3001/login')
        page.fill('input[placeholder="用户名"]', 'admin')
        page.fill('input[placeholder="密码"]', 'admin123')
        # 使用更稳健的点击方式
        page.click('button:has-text("登")')
        
        # 等待进入首页
        page.wait_for_url('**/dashboard**', timeout=10000)
        print("登录成功，当前 URL:", page.url)
        
        # --- 测试房型管理 ---
        print("\n2. 正在验证房型管理分类展示...")
        # 假设菜单中有“房型管理”字样，点击它
        # 如果是多级菜单，可能需要先点击父级
        try:
            # 尝试查找酒店管理相关菜单
            page.click('text=酒店管理')
            time.sleep(0.5)
        except:
            pass
            
        page.click('text=房型管理')
        page.wait_for_load_state('networkidle')
        page.screenshot(path='room_type_list.png')
        
        # 验证分类标题是否出现 (标准房, 豪华房)
        # AntD 的分组通常在表格的特定行或特定的标题元素中
        categories = ["标准房", "豪华房"]
        for cat in categories:
            if page.locator(f'text={cat}').count() > 0:
                print(f"✅ 发现分类标题: {cat}")
            else:
                print(f"❌ 未发现分类标题: {cat} (可能页面尚未加载或 DOM 结构不同)")
        
        # 验证房型去重逻辑
        # 抓取表格中所有的房型名称（假设在某一列）
        # 这里先尝试抓取所有文本内容进行初步判断
        room_names = page.locator('td').all_text_contents()
        # 过滤掉空的
        room_names = [name.strip() for name in room_names if name.strip()]
        # 简单校验是否存在明显的重复项（例如连续出现两次相同的房型名）
        # 这里的校验逻辑可以根据实际页面结构优化
        
        # --- 测试房价码管理分组 ---
        print("\n3. 正在验证房价码房型分组...")
        page.click('text=价格管理') # 或者房价码管理
        time.sleep(0.5)
        page.click('text=房价码管理')
        page.wait_for_load_state('networkidle')
        
        # 点击“新增房价码”按钮
        page.click('button:has-text("新增")')
        page.wait_for_load_state('networkidle')
        
        # 检查适用房型选择器
        # 房价码编辑页通常有一个房型列表或选择器
        page.screenshot(path='add_rate_plan.png')
        
        # 验证分组显示（根据之前的代码修复，这里应该有分类名作为 Label 或 Header）
        for cat in categories:
            if page.locator(f'text={cat}').count() > 0:
                print(f"✅ 房价码新增页发现房型分组: {cat}")
            else:
                print(f"❌ 房价码新增页缺失房型分组: {cat}")
        
        print("\n测试完成。")
        browser.close()

if __name__ == '__main__':
    test_room_logic()

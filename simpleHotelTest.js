const puppeteer = require('puppeteer');

async function testHotelManagement() {
  console.log('开始测试集团管理-酒店管理功能...');
  
  // 启动浏览器
  const browser = await puppeteer.launch({
    headless: true,
    defaultViewport: { width: 1920, height: 1080 }
  });
  
  const page = await browser.newPage();
  
  try {
    // 导航到前端页面
    await page.goto('http://localhost:3000/');
    console.log('已导航到前端页面');
    
    // 等待页面加载
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // 尝试登录（如果需要）
    try {
      const isLoggedIn = await page.evaluate(() => {
        return document.querySelector('.ant-menu') !== null;
      });
      
      if (!isLoggedIn) {
        console.log('尝试登录...');
        // 假设登录页面的元素
        await page.type('#username', 'admin');
        await page.type('#password', 'admin');
        await page.click('button[type="submit"]');
        await page.waitForNavigation({ waitUntil: 'networkidle0' });
        console.log('登录成功');
      }
    } catch (error) {
      console.log('登录页面结构可能不同，跳过登录步骤');
    }
    
    // 导航至酒店管理页面
    console.log('导航至酒店管理页面...');
    
    // 点击集团管理菜单
    await page.waitForSelector('.ant-menu-item');
    const menuItems = await page.$$('.ant-menu-item');
    
    for (const item of menuItems) {
      const text = await item.evaluate(el => el.textContent);
      if (text.includes('集团管理')) {
        await item.click();
        console.log('点击了集团管理菜单');
        break;
      }
    }
    
    // 等待子菜单出现
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // 点击酒店管理子菜单
    const subMenuItems = await page.$$('.ant-menu-item');
    for (const item of subMenuItems) {
      const text = await item.evaluate(el => el.textContent);
      if (text.includes('酒店管理')) {
        await item.click();
        console.log('点击了酒店管理子菜单');
        break;
      }
    }
    
    // 等待页面加载
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 验证是否进入酒店管理页面
    const pageTitle = await page.evaluate(() => {
      return document.querySelector('.ant-page-header-title')?.textContent || '';
    });
    console.log('当前页面标题:', pageTitle);
    
    // 酒店数据
    const hotels = [
      {
        hotelCode: 'HOTEL006',
        chineseName: '上海虹桥酒店',
        englishName: 'Shanghai Hongqiao Hotel',
        starRating: '5',
        province: '上海市',
        city: '上海市',
        address: '上海市长宁区虹桥路2222号',
        phone: '021-58888889',
        email: 'info@hongqiao.com',
        introduction: '上海虹桥酒店位于虹桥商务区，交通便利，环境舒适。',
        totalRooms: '120'
      },
      {
        hotelCode: 'HOTEL007',
        chineseName: '北京国贸酒店',
        englishName: 'Beijing World Trade Hotel',
        starRating: '4',
        province: '北京市',
        city: '北京市',
        address: '北京市朝阳区建国门外大街1号',
        phone: '010-65555556',
        email: 'info@worldtrade.com',
        introduction: '北京国贸酒店位于CBD核心区，商务便利。',
        totalRooms: '100'
      },
      {
        hotelCode: 'HOTEL008',
        chineseName: '广州白云酒店',
        englishName: 'Guangzhou Baiyun Hotel',
        starRating: '4',
        province: '广东省',
        city: '广州市',
        address: '广州市白云区白云大道北1号',
        phone: '020-87777778',
        email: 'info@baiyun.com',
        introduction: '广州白云酒店位于白云区，交通便利。',
        totalRooms: '90'
      },
      {
        hotelCode: 'HOTEL009',
        chineseName: '杭州西溪酒店',
        englishName: 'Hangzhou Xixi Hotel',
        starRating: '5',
        province: '浙江省',
        city: '杭州市',
        address: '杭州市西湖区西溪湿地公园旁',
        phone: '0571-86666668',
        email: 'info@xixi.com',
        introduction: '杭州西溪酒店位于西溪湿地公园旁，环境优美。',
        totalRooms: '80'
      },
      {
        hotelCode: 'HOTEL010',
        chineseName: '深圳福田酒店',
        englishName: 'Shenzhen Futian Hotel',
        starRating: '4',
        province: '广东省',
        city: '深圳市',
        address: '深圳市福田区福田中心区',
        phone: '0755-26666668',
        email: 'info@futian.com',
        introduction: '深圳福田酒店位于福田中心区，商务便利。',
        totalRooms: '70'
      }
    ];
    
    // 新增酒店
    console.log('开始新增酒店...');
    for (let i = 0; i < hotels.length; i++) {
      const hotel = hotels[i];
      console.log(`新增酒店 ${i + 1}: ${hotel.chineseName}`);
      
      // 点击新增按钮
      const buttons = await page.$$('button');
      for (const btn of buttons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('新增酒店') || text.includes('添加')) {
          await btn.click();
          console.log('点击了新增酒店按钮');
          break;
        }
      }
      
      // 等待表单出现
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 填写表单
      try {
        // 尝试通过不同的选择器填写表单
        await page.type('input[placeholder*="酒店ID"]', hotel.hotelCode);
        await page.type('input[placeholder*="酒店名称"]', hotel.chineseName);
        await page.type('input[placeholder*="英文名称"]', hotel.englishName);
        await page.type('input[placeholder*="省份"]', hotel.province);
        await page.type('input[placeholder*="城市"]', hotel.city);
        await page.type('input[placeholder*="地址"]', hotel.address);
        await page.type('input[placeholder*="电话"]', hotel.phone);
        await page.type('input[placeholder*="邮箱"]', hotel.email);
        await page.type('textarea[placeholder*="简介"]', hotel.introduction);
        await page.type('input[placeholder*="房间数"]', hotel.totalRooms);
        
        // 选择星级
        await page.select('select', hotel.starRating);
      } catch (error) {
        console.log('表单填写遇到问题:', error.message);
      }
      
      // 点击保存按钮
      const saveButtons = await page.$$('button');
      for (const btn of saveButtons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('保存') || text.includes('确定')) {
          await btn.click();
          console.log('点击了保存按钮');
          break;
        }
      }
      
      // 等待保存完成
      await new Promise(resolve => setTimeout(resolve, 2000));
    }
    
    // 删除酒店
    console.log('开始删除酒店...');
    for (let i = 0; i < 3; i++) {
      console.log(`删除酒店 ${i + 1}`);
      
      // 点击删除按钮
      const deleteButtons = await page.$$('button');
      for (const btn of deleteButtons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('删除')) {
          await btn.click();
          console.log('点击了删除按钮');
          break;
        }
      }
      
      // 等待确认对话框
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 点击确认
      const confirmButtons = await page.$$('button');
      for (const btn of confirmButtons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('确认') || text.includes('确定')) {
          await btn.click();
          console.log('点击了确认删除按钮');
          break;
        }
      }
      
      // 等待删除完成
      await new Promise(resolve => setTimeout(resolve, 2000));
    }
    
    console.log('测试完成！');
    
  } catch (error) {
    console.error('测试过程中遇到错误:', error);
  } finally {
    // 关闭浏览器
    await browser.close();
    console.log('浏览器已关闭');
  }
}

// 运行测试
testHotelManagement();

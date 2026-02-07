const puppeteer = require('puppeteer');

describe('集团管理-酒店管理功能测试', () => {
  let browser;
  let page;

  beforeAll(async () => {
    // 增加超时时间
    jest.setTimeout(60000);
    
    // 启动浏览器
    browser = await puppeteer.launch({
      headless: true, // 无头模式，更快
      defaultViewport: { width: 1920, height: 1080 } // 设置视口大小
    });
    page = await browser.newPage();

    // 导航到登录页面
    await page.goto('http://localhost:3000/');
    
    // 等待页面加载完成
    await new Promise(resolve => setTimeout(resolve, 3000));

    // 登录系统（假设登录页面有用户名和密码输入框）
    // 注意：需要根据实际登录页面的元素选择器进行调整
    try {
      // 检查是否已经登录
      const isLoggedIn = await page.evaluate(() => {
        return document.querySelector('.ant-menu') !== null;
      });

      if (!isLoggedIn) {
        // 假设登录页面的用户名和密码输入框
        await page.type('#username', 'admin');
        await page.type('#password', 'admin');
        await page.click('button[type="submit"]');
        await page.waitForNavigation({ waitUntil: 'networkidle0' });
      }
    } catch (error) {
      console.log('登录页面结构可能不同，跳过登录步骤');
    }
  });

  afterAll(async () => {
    // 关闭浏览器
    await browser.close();
  });

  test('导航至酒店管理页面', async () => {
    // 点击左侧菜单"集团管理"
    await page.waitForSelector('.ant-menu-item');
    const menuItems = await page.$$('.ant-menu-item');
    
    // 查找集团管理菜单
    for (const item of menuItems) {
      const text = await item.evaluate(el => el.textContent);
      if (text.includes('集团管理')) {
        await item.click();
        break;
      }
    }

    // 等待子菜单出现
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 查找酒店管理子菜单
    const subMenuItems = await page.$$('.ant-menu-item');
    for (const item of subMenuItems) {
      const text = await item.evaluate(el => el.textContent);
      if (text.includes('酒店管理')) {
        await item.click();
        break;
      }
    }

    // 等待页面加载完成
    await new Promise(resolve => setTimeout(resolve, 2000));

    // 验证是否进入酒店管理页面
    const pageTitle = await page.evaluate(() => {
      return document.querySelector('.ant-page-header-title')?.textContent || '';
    });
    expect(pageTitle).toContain('酒店管理');
  });

  test('新增酒店测试', async () => {
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

    // 新增每个酒店
    for (const hotel of hotels) {
      // 点击"新增酒店"按钮
      await page.waitForSelector('button');
      const buttons = await page.$$('button');
      for (const btn of buttons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('新增酒店') || text.includes('添加')) {
          await btn.click();
          break;
        }
      }

      // 等待表单对话框出现
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 填写表单
      try {
        // 酒店ID
        await page.type('input[placeholder="酒店ID"]', hotel.hotelCode);
        // 酒店名称
        await page.type('input[placeholder="酒店名称"]', hotel.chineseName);
        // 英文名称
        await page.type('input[placeholder="英文名称"]', hotel.englishName);
        // 星级
        await page.select('select[name="starRating"]', hotel.starRating);
        // 省份
        await page.type('input[placeholder="省份"]', hotel.province);
        // 城市
        await page.type('input[placeholder="城市"]', hotel.city);
        // 地址
        await page.type('input[placeholder="地址"]', hotel.address);
        // 电话
        await page.type('input[placeholder="电话"]', hotel.phone);
        // 邮箱
        await page.type('input[placeholder="邮箱"]', hotel.email);
        // 简介
        await page.type('textarea[placeholder="酒店简介"]', hotel.introduction);
        // 总房间数
        await page.type('input[placeholder="总房间数"]', hotel.totalRooms);
      } catch (error) {
        console.log('表单元素选择器可能不同，尝试其他方式');
        // 尝试通过ant-design的表单元素选择器
        const formItems = await page.$$('.ant-form-item');
        let index = 0;
        for (const item of formItems) {
          const input = await item.$('input, select, textarea');
          if (input) {
            switch (index) {
              case 0:
                await input.type(hotel.hotelCode);
                break;
              case 1:
                await input.type(hotel.chineseName);
                break;
              case 2:
                await input.type(hotel.englishName);
                break;
              case 3:
                await input.select(hotel.starRating);
                break;
              case 4:
                await input.type(hotel.province);
                break;
              case 5:
                await input.type(hotel.city);
                break;
              case 6:
                await input.type(hotel.address);
                break;
              case 7:
                await input.type(hotel.phone);
                break;
              case 8:
                await input.type(hotel.email);
                break;
              case 9:
                await input.type(hotel.introduction);
                break;
              case 10:
                await input.type(hotel.totalRooms);
                break;
            }
            index++;
          }
        }
      }

      // 点击保存按钮
      const saveButtons = await page.$$('button');
      for (const btn of saveButtons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('保存') || text.includes('确定')) {
          await btn.click();
          break;
        }
      }

      // 等待保存完成
      await new Promise(resolve => setTimeout(resolve, 2000));
    }
  });

  test('表单验证测试', async () => {
    // 点击"新增酒店"按钮
    await page.waitForSelector('button');
    const buttons = await page.$$('button');
    for (const btn of buttons) {
      const text = await btn.evaluate(el => el.textContent);
      if (text.includes('新增酒店') || text.includes('添加')) {
        await btn.click();
        break;
      }
    }

    // 等待表单对话框出现
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 不填写任何信息，直接点击保存
    const saveButtons = await page.$$('button');
    for (const btn of saveButtons) {
      const text = await btn.evaluate(el => el.textContent);
      if (text.includes('保存') || text.includes('确定')) {
        await btn.click();
        break;
      }
    }

    // 等待错误提醒出现
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 验证是否显示错误提醒
    const hasError = await page.evaluate(() => {
      return document.querySelector('.ant-form-item-explain-error') !== null;
    });
    expect(hasError).toBe(true);

    // 关闭对话框
    const cancelButtons = await page.$$('button');
    for (const btn of cancelButtons) {
      const text = await btn.evaluate(el => el.textContent);
      if (text.includes('取消') || text.includes('关闭')) {
        await btn.click();
        break;
      }
    }

    await new Promise(resolve => setTimeout(resolve, 1000));

  test('编辑酒店测试', async () => {
    // 查找编辑按钮
    await new Promise(resolve => setTimeout(resolve, 1000));
    const editButtons = await page.$$('button');
    for (const btn of editButtons) {
      const text = await btn.evaluate(el => el.textContent);
      if (text.includes('编辑')) {
        await btn.click();
        break;
      }
    }

    // 等待表单对话框出现
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 修改酒店名称
    try {
      await page.type('input[placeholder="酒店名称"]', ' (修改)', { delay: 100 });
    } catch (error) {
      console.log('表单元素选择器可能不同');
    }

    // 点击保存按钮
    const saveButtons = await page.$$('button');
    for (const btn of saveButtons) {
      const text = await btn.evaluate(el => el.textContent);
      if (text.includes('保存') || text.includes('确定')) {
        await btn.click();
        break;
      }
    }

    // 等待保存完成
    await new Promise(resolve => setTimeout(resolve, 2000));
  });

  test('删除酒店测试', async () => {
    // 删除3个酒店
    for (let i = 0; i < 3; i++) {
      // 查找删除按钮
      await new Promise(resolve => setTimeout(resolve, 1000));
      const deleteButtons = await page.$$('button');
      for (const btn of deleteButtons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('删除')) {
          await btn.click();
          break;
        }
      }

      // 等待确认对话框出现
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 点击确认按钮
      const confirmButtons = await page.$$('button');
      for (const btn of confirmButtons) {
        const text = await btn.evaluate(el => el.textContent);
        if (text.includes('确认') || text.includes('确定')) {
          await btn.click();
          break;
        }
      }

      // 等待删除完成
      await new Promise(resolve => setTimeout(resolve, 2000));
    }
  });
});

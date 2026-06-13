const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');

(async () => {
  console.log('启动无头浏览器进行导出功能验证...');
  const browser = await puppeteer.launch({
    headless: true,
    executablePath: '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });
  const page = await browser.newPage();

  // 收集控制台报错
  page.on('console', msg => {
    console.log(`[浏览器控制台 - ${msg.type()}]:`, msg.text());
  });

  page.on('pageerror', err => {
    console.error('[浏览器 JS 报错]:', err.toString());
  });

  // 监听 alert 对话框
  page.on('dialog', async dialog => {
    console.log('【Alert 对话框被唤起】:', dialog.message());
    await dialog.accept();
  });

  try {
    console.log('正在载入登录页 http://localhost:3001/login ...');
    await page.goto('http://localhost:3001/login', { waitUntil: 'networkidle0' });

    console.log('正在输入账号密码...');
    await page.focus('input[placeholder*="账号"]');
    await page.keyboard.type('admin');
    await page.focus('input[placeholder*="密码"]');
    await page.keyboard.type('admin123');

    console.log('点击登录按钮...');
    await Promise.all([
      page.click('button[type="submit"]'),
      page.waitForNavigation({ waitUntil: 'networkidle0' })
    ]);

    console.log('登录成功，跳转至订单报表页 http://localhost:3001/reports/reservation-reports ...');
    await page.goto('http://localhost:3001/reports/reservation-reports', { waitUntil: 'networkidle0' });

    console.log('等待数据加载 3 秒...');
    await new Promise(r => setTimeout(r, 3000));

    console.log('获取页面中的按钮列表...');
    const buttons = await page.$$('button');
    let exportBtn = null;
    for (const btn of buttons) {
      const text = await page.evaluate(el => el.innerText, btn);
      if (text.includes('导出报表')) {
        exportBtn = btn;
        break;
      }
    }

    if (exportBtn) {
      console.log('成功定位到「导出报表」按钮，启用 CDP 下载拦截...');
      const downloadPath = path.resolve(__dirname);
      const client = await page.target().createCDPSession();
      await client.send('Page.setDownloadBehavior', {
        behavior: 'allow',
        downloadPath: downloadPath
      });

      console.log('触发「导出报表」按钮点击事件...');
      await exportBtn.click();

      console.log('等待 5 秒确认是否生成下载文件或触发错误...');
      await new Promise(r => setTimeout(r, 5000));

      // 检查是否有新生成的 CSV 文件
      const files = fs.readdirSync(downloadPath);
      const csvFiles = files.filter(f => f.endsWith('.csv'));
      if (csvFiles.length > 0) {
        console.log('【测试结果】: 成功检测到下载文件！列表如下:');
        csvFiles.forEach(f => console.log(`  - ${f}`));
        // 清理测试生成的文件
        csvFiles.forEach(f => fs.unlinkSync(path.join(downloadPath, f)));
        console.log('测试完成：功能确认完全实现且正常下载！');
      } else {
        console.log('【测试结果】: 未检测到下载文件，请检查控制台/堆栈报错。');
      }
    } else {
      console.log('【测试结果】: 失败！未在页面上找到「导出报表」按钮。');
    }

  } catch (error) {
    console.error('测试运行过程中发生异常:', error);
  } finally {
    await browser.close();
    console.log('无头浏览器已安全关闭。');
  }
})();

import React from 'react'
import { RouterProvider } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import { router } from './router'
import { HotelProvider } from './contexts/HotelContext.jsx'

// 自定义主题配置
const themeConfig = {
  token: {
    // 主色调
    colorPrimary: '#1890ff',
    colorPrimaryHover: '#40a9ff',
    colorPrimaryActive: '#096dd9',
    // 背景色
    colorBgContainer: '#ffffff',
    colorBgLayout: '#f0f2f5',
    // 文本色
    colorText: '#262626',
    colorTextSecondary: '#595959',
    colorTextPlaceholder: '#bfbfbf',
    // 边框色
    colorBorder: '#d9d9d9',
    colorBorderSecondary: '#f0f0f0',
    // 圆角
    borderRadius: 8,
    borderRadiusLG: 12,
    borderRadiusSM: 4,
    // 字体
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
    fontSize: 14,
    fontSizeLG: 16,
    fontSizeSM: 12,
    // 间距
    lineHeight: 1.5715,
    // 阴影
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)',
    boxShadowSecondary: '0 4px 12px rgba(0, 0, 0, 0.15)',
  },
}

function App() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={themeConfig}
    >
      <HotelProvider>
        <RouterProvider router={router} />
      </HotelProvider>
    </ConfigProvider>
  )
}

export default App
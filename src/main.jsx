import React from 'react'
import ReactDOM from 'react-dom/client'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import App from './App.jsx'
import './styles/index.css'
import './styles/experience.css'
import { initErrorTracker } from './utils/errorTracker.js'

dayjs.locale('zh-cn')

// 初始化前端全链路错误监听追踪
initErrorTracker()

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)

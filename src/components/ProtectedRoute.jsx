import React, { useContext } from 'react'
import { Navigate } from 'react-router-dom'
import { AuthContext } from '../contexts/AuthContext'

// 演示模式标志
const DEMO_MODE = false

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useContext(AuthContext)

  if (loading) {
    return <div>加载中...</div>
  }

  // 确保children是一个有效的React元素
  if (!children) {
    return <Navigate to="/" replace />
  }

  // 演示模式下直接显示内容，不需要登录
  if (DEMO_MODE) {
    return children
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />
}

export default ProtectedRoute

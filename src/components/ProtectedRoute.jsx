import { useContext } from 'react'
import { Navigate } from 'react-router-dom'
import { AuthContext } from '../contexts/AuthContext'

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useContext(AuthContext)

  if (loading) {
    return <div>加载中...</div>
  }

  // 确保children是一个有效的React元素
  if (!children) {
    return <Navigate to="/" replace />
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />
}

export default ProtectedRoute

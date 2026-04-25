import React, { createContext, useState, useEffect, useMemo } from 'react'
import { authApi } from '../utils/api'
import { crsMenuData } from '../utils/menuData'

const TOKEN_KEY = 'crs_token'
const USER_KEY = 'crs_user'
const MENUS_KEY = 'crs_menus'
// 演示模式标志
const DEMO_MODE = false

export const AuthContext = createContext()

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => {
    if (DEMO_MODE) return 'demo-token'
    return localStorage.getItem(TOKEN_KEY)
  })
  const [user, setUser] = useState(() => {
    if (DEMO_MODE) return {
      id: 1,
      username: 'demo',
      name: '演示用户',
      email: 'demo@example.com',
      roles: ['admin']
    }
    const savedUser = localStorage.getItem(USER_KEY)
    return savedUser ? JSON.parse(savedUser) : null
  })
  const [menus, setMenus] = useState(() => {
    // 始终使用静态菜单数据，避免localStorage存储React元素
    return crsMenuData
  })
  const [loading, setLoading] = useState(false)
  
  const isAuthenticated = useMemo(() => {
    if (DEMO_MODE) {
      return true
    }
    return !!token && !!user
  }, [token, user])
  
  useEffect(() => {
    if (token) {
      localStorage.setItem(TOKEN_KEY, token)
    } else {
      localStorage.removeItem(TOKEN_KEY)
    }
  }, [token])
  
  useEffect(() => {
    if (user) {
      localStorage.setItem(USER_KEY, JSON.stringify(user))
    } else {
      localStorage.removeItem(USER_KEY)
    }
  }, [user])
  
  // 监听localStorage变化，当token或user被清除时，更新状态
  useEffect(() => {
    const handleStorageChange = (event) => {
      if (event.key === TOKEN_KEY || event.key === USER_KEY) {
        const currentToken = localStorage.getItem(TOKEN_KEY)
        const savedUser = localStorage.getItem(USER_KEY)
        setToken(currentToken)
        setUser(savedUser ? JSON.parse(savedUser) : null)
      }
    }
    
    window.addEventListener('storage', handleStorageChange)
    
    // 定期检查localStorage，确保状态与localStorage同步
    const interval = setInterval(() => {
      const currentToken = localStorage.getItem(TOKEN_KEY)
      const savedUser = localStorage.getItem(USER_KEY)
      
      setToken(currentToken)
      setUser(savedUser ? JSON.parse(savedUser) : null)
    }, 1000)
    
    return () => {
      window.removeEventListener('storage', handleStorageChange)
      clearInterval(interval)
    }
  }, [])
  
  // 不再将菜单数据存储到localStorage，避免React元素被转换为普通对象
  // useEffect(() => {
  //   if (menus.length > 0) {
  //     localStorage.setItem(MENUS_KEY, JSON.stringify(menus))
  //   } else {
  //     localStorage.removeItem(MENUS_KEY)
  //   }
  // }, [menus])
  
  const fetchUserMenus = async (userId, systemType = 'crs') => {
    try {
      const response = await authApi.getUserMenus(userId, systemType)
      if (response.success) {
        setMenus(response.data || [])
      }
    } catch (error) {
      console.error('获取菜单失败:', error)
    }
  }
  
  const login = async (username, password, rememberMe) => {
    setLoading(true)
    try {
      const response = await authApi.login({ username, password })
      console.log('Login response:', response)
      if (response.success) {
        const { token: newToken, user: newUser, menus: newMenus } = response.data
        console.log('User data:', newUser)
        console.log('Menus from login:', newMenus)
        setToken(newToken)
        setUser(newUser)
        // 如果登录响应中包含菜单数据，直接使用
        if (newMenus && newMenus.length > 0) {
          setMenus(newMenus)
        } else {
          // 否则调用fetchUserMenus获取菜单
          await fetchUserMenus(newUser.id)
        }
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (error) {
      console.error('Login error:', error)
      throw error
    } finally {
      setLoading(false)
    }
  }
  
  const logout = async () => {
    setToken(null)
    setUser(null)
    setMenus([])
  }
  
  const value = {
    token,
    user,
    menus,
    isAuthenticated,
    loading,
    login,
    logout,
    fetchUserMenus,
  }
  
  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

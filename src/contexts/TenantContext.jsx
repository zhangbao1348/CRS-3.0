import React, { createContext, useState, useContext, useEffect } from 'react'
import { tenantApi } from '../utils/api'

const TenantContext = createContext()
const SELECTED_TENANT_KEY = 'crs_selected_tenant'

export const useTenantContext = () => {
  const context = useContext(TenantContext)
  if (!context) {
    throw new Error('useTenantContext must be used within a TenantProvider')
  }
  return context
}

export const TenantProvider = ({ children }) => {
  const [tenants, setTenants] = useState([])
  const [selectedTenant, setSelectedTenant] = useState(() => {
    const saved = localStorage.getItem(SELECTED_TENANT_KEY)
    return saved ? parseInt(saved, 10) : null
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const fetchTenants = async () => {
    setLoading(true)
    setError(null)
    try {
      // 页面加载时自动获取租户列表，不触发自动登出
      const response = await tenantApi.getAllTenants({
        params: {},
        metadata: { skipAutoLogout: true }
      })
      if (response.success) {
        const tenantList = response.data || []
        setTenants(tenantList)
        // 确保始终有一个选中的租户
        if (tenantList.length > 0) {
          if (!selectedTenant) {
            // 没有选中的租户时，默认选第一个
            setSelectedTenant(tenantList[0].id)
          } else {
            // 检查当前选中的租户是否还在列表中
            const currentTenantExists = tenantList.some(t => t.id === selectedTenant)
            if (!currentTenantExists) {
              // 当前选中的租户不存在了，选第一个
              setSelectedTenant(tenantList[0].id)
            }
          }
        }
      }
    } catch (err) {
      setError('获取租户列表失败')
      console.error('获取租户列表失败:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchTenants()
  }, [])

  const changeTenant = (tenantId) => {
    // 确保不会设置 null 或 0
    if (tenantId && tenantId > 0) {
      setSelectedTenant(tenantId)
    } else if (tenants.length > 0) {
      // 如果传入了无效值，就设置为第一个租户
      setSelectedTenant(tenants[0].id)
    }
  }

  // 监听 selectedTenant 变化，存储到 localStorage
  useEffect(() => {
    if (selectedTenant) {
      localStorage.setItem(SELECTED_TENANT_KEY, selectedTenant.toString())
    } else {
      localStorage.removeItem(SELECTED_TENANT_KEY)
    }
  }, [selectedTenant])

  const value = {
    tenants,
    selectedTenant,
    loading,
    error,
    changeTenant,
    fetchTenants
  }

  return (
    <TenantContext.Provider value={value}>
      {children}
    </TenantContext.Provider>
  )
}

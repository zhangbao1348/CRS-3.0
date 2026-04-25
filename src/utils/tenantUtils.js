// src/utils/tenantUtils.js

/**
 * 获取当前有效的租户ID
 * 优先级：
 * 1. 从localStorage中获取超管选择的租户ID
 * 2. 从用户信息中获取默认租户ID
 * 3. 返回null
 */
export const getCurrentTenantId = () => {
  // 首先从localStorage中获取超管选择的租户ID
  const selectedTenant = localStorage.getItem('crs_selected_tenant')
  if (selectedTenant) {
    return parseInt(selectedTenant, 10)
  }
  
  // 如果没有selectedTenant，从用户信息中获取默认租户ID
  const userStr = localStorage.getItem('crs_user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      return user.tenantId
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  
  return null
}

import axios from 'axios'
import { getCurrentTenantId } from './tenantUtils'

const API_BASE_URL = '/api'

// 请求上下文跟踪器
let isInitializing = false;

// 设置初始化状态的函数
export const setInitializingState = (state) => {
  isInitializing = state;
};

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('crs_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加租户ID到请求头
    const tenantId = getCurrentTenantId()
    if (tenantId) {
      config.headers['X-Tenant-Id'] = tenantId
    }
    
    // 添加初始化状态到请求配置中
    config.metadata = {
      ...config.metadata,
      isInitializing: isInitializing
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      // 检查请求配置中的 metadata，如果设置了 skipAutoLogout 则不自动跳转
      const skipAutoLogout = error.config?.metadata?.skipAutoLogout === true
      // 检查是否处于初始化状态，如果是则不自动跳转
      const isInitRequest = error.config?.metadata?.isInitializing === true
      
      if (!skipAutoLogout && !isInitRequest) {
        localStorage.removeItem('crs_token')
        localStorage.removeItem('crs_user')
        localStorage.removeItem('crs_menus')
        window.location.href = '/login'
      }
    }
    // 对于 400 等错误状态码，返回 error.response.data，这样前端可以正常处理错误信息
    if (error.response?.data) {
      return Promise.reject(error.response.data)
    }
    return Promise.reject(error)
  }
)

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  refreshToken: (data) => api.post('/auth/refresh', data),
  getUserMenus: (userId, systemType = 'crs') => api.get(`/auth/user/${userId}/menus/${systemType}`),
  getUserPermissions: (userId) => api.get(`/auth/user/${userId}/permissions`)
}

export const tenantApi = {
  getAllTenants: (options = {}) => api.get('/tenants', options),
  getTenantById: (id, options = {}) => api.get(`/tenants/${id}`, options),
  createTenant: (data, options = {}) => api.post('/tenants', data, options),
  updateTenant: (id, data, options = {}) => api.put(`/tenants/${id}`, data, options),
  deleteTenant: (id, options = {}) => api.delete(`/tenants/${id}`, options)
}

export const roleApi = {
  getAllRoles: () => api.get('/roles'),
  getActiveRoles: () => api.get('/roles/active'),
  getRoleById: (id) => api.get(`/roles/${id}`),
  getRoleByCode: (roleCode) => api.get(`/roles/code/${roleCode}`),
  createRole: (data) => api.post('/roles', data),
  updateRole: (id, data) => api.put(`/roles/${id}`, data),
  deleteRole: (id) => api.delete(`/roles/${id}`),
  getRoleMenus: (roleId) => api.get(`/roles/${roleId}/menus`),
  assignMenus: (roleId, menuIds) => api.post(`/roles/${roleId}/menus`, { menuIds })
}

export const menuApi = {
  getAllMenus: (params = {}) => api.get('/menus', { params }),
  getMenusBySystemType: (systemType) => api.get(`/menus/system/${systemType}`),
  getMenusByParentId: (systemType, parentId) => api.get(`/menus/system/${systemType}/parent/${parentId}`),
  getMenuById: (id) => api.get(`/menus/${id}`),
  createMenu: (data) => api.post('/menus', data),
  updateMenu: (id, data) => api.put(`/menus/${id}`, data),
  deleteMenu: (id) => api.delete(`/menus/${id}`)
}

export const userApi = {
  getAllUsers: (tenantId) => {
    const params = tenantId ? { tenantId } : {}
    return api.get('/users', { params })
  },
  getUserById: (id) => api.get(`/users/${id}`),
  createUser: (data) => api.post('/users', data),
  updateUser: (id, data) => api.put(`/users/${id}`, data),
  deleteUser: (id) => api.delete(`/users/${id}`),
  updateUserStatus: (id, status) => api.put(`/users/${id}/status`, { status }),
  resetPassword: (id, password) => api.put(`/users/${id}/password`, { password })
}

export const hotelApi = {
  getAllHotels: (tenantId, options = {}) => {
    const params = tenantId ? { tenantId } : {}
    return api.get('/hotels', { params, ...options })
  },
  getHotelById: (id, options = {}) => api.get(`/hotels/${id}`, options),
  getHotelByCode: (code, options = {}) => api.get(`/hotels/code/${code}`, options),
  getHotelsByTenantId: (tenantId, options = {}) => api.get(`/hotels/tenant/${tenantId}`, options),
  getHotelsByGroupId: (groupId, options = {}) => api.get(`/hotels/group/${groupId}`, options),
  createHotel: (data, options = {}) => api.post('/hotels', data, options),
  updateHotel: (id, data, options = {}) => api.put(`/hotels/${id}`, data, options),
  deleteHotel: (id, options = {}) => api.delete(`/hotels/${id}`, options),
  getHotelsByCity: (city, options = {}) => api.get(`/hotels/city/${city}`, options),
  getHotelsByStatus: (status, options = {}) => api.get(`/hotels/status/${status}`, options),
  checkHotelCode: (hotelId, options = {}) => api.get(`/hotels/${hotelId}/check-code`, options)
}

export const hotelRoomTypeApi = {
  getHotelRoomTypes: (hotelId, options = {}) => api.get(`/hotel-room-types/hotel/${hotelId}`, options)
}

export const channelCodeApi = {
  getAllChannelCodes: () => api.get('/channel-codes'),
  getChannelCodeById: (id) => api.get(`/channel-codes/${id}`),
  createChannelCode: (data) => api.post('/channel-codes', data),
  updateChannelCode: (id, data) => api.put(`/channel-codes/${id}`, data),
  deleteChannelCode: (id) => api.delete(`/channel-codes/${id}`),
  checkCodeUnique: (code, excludeId) => {
    const params = { code }
    if (excludeId) {
      params.id = excludeId
    }
    return api.get('/channel-codes/check-code', { params })
  }
}

export const groupFacilityApi = {
  getAllGroupFacilities: (options = {}) => api.get('/group-facilities', options),
  getGroupFacilityById: (id, options = {}) => api.get(`/group-facilities/${id}`, options),
  getGroupFacilitiesByType: (type, options = {}) => api.get(`/group-facilities/type/${type}`, options)
}

export const hotelFacilityApi = {
  getHotelFacilities: (hotelId, options = {}) => api.get(`/hotel-facilities/hotel/${hotelId}`, options),
  createHotelFacility: (data, options = {}) => api.post('/hotel-facilities', data, options),
  deleteHotelFacilities: (hotelId, options = {}) => api.delete(`/hotel-facilities/hotel/${hotelId}`, options)
}

export const hotelImageApi = {
  getHotelImages: (hotelId) => api.get(`/hotel-images/hotel/${hotelId}`),
  uploadHotelImage: (formData) => api.post('/hotel-images/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }),
  deleteHotelImage: (imageId) => api.delete(`/hotel-images/${imageId}`)
}

export const groupRateCodeApi = {
  getAllGroupRateCodes: (options = {}) => api.get('/group-rate-codes', options),
  getGroupRateCodeById: (id, options = {}) => api.get(`/group-rate-codes/${id}`, options),
  getGroupRateCodesByGroupId: (groupId, options = {}) => api.get(`/group-rate-codes/group/${groupId}`, options),
  getActiveGroupRateCodes: (options = {}) => api.get('/group-rate-codes/active', options),
  getSelectableParentRateCodes: (targetDerivativeLevel, excludeId, options = {}) => {
    const params = {}
    if (targetDerivativeLevel) params.targetDerivativeLevel = targetDerivativeLevel
    if (excludeId) params.excludeId = excludeId
    return api.get('/group-rate-codes/selectable-parents', { params, ...options })
  },
  getAllocations: (id, options = {}) => api.get(`/group-rate-codes/${id}/allocations`, options),
  allocate: (id, data, options = {}) => api.post(`/group-rate-codes/${id}/allocate`, data, options),
  syncToHotels: (id, hotelIds, options = {}) => api.post(`/group-rate-codes/${id}/sync-to-hotels`, { hotelIds }, options),
  createGroupRateCode: (data, options = {}) => api.post('/group-rate-codes', data, options),
  updateGroupRateCode: (id, data, options = {}) => api.put(`/group-rate-codes/${id}`, data, options),
  deleteGroupRateCode: (id, options = {}) => api.delete(`/group-rate-codes/${id}`, options),
  enableGroupRateCode: (id, options = {}) => api.put(`/group-rate-codes/${id}/enable`, options),
  disableGroupRateCode: (id, options = {}) => api.put(`/group-rate-codes/${id}/disable`, options)
}

export const hotelRateCodeAllocationApi = {
  getAllocationsByHotelId: (hotelId, options = {}) => api.get(`/hotel-rate-code-allocations/hotel/${hotelId}`, options),
  createAllocation: (data, options = {}) => api.post('/hotel-rate-code-allocations', data, options),
  updateAllocation: (id, data, options = {}) => api.put(`/hotel-rate-code-allocations/${id}`, data, options),
  deleteAllocationsByHotelId: (hotelId, options = {}) => api.delete(`/hotel-rate-code-allocations/hotel/${hotelId}`, options)
}

export const groupRoomTypeApi = {
  getAllGroupRoomTypes: (options = {}) => api.get('/group-room-types', options),
  getGroupRoomTypeById: (id, options = {}) => api.get(`/group-room-types/${id}`, options),
  getGroupRoomTypesByGroupId: (groupId, options = {}) => api.get(`/group-room-types/group/${groupId}`, options),
  getActiveGroupRoomTypes: (options = {}) => api.get('/group-room-types/active', options)
}

export const groupRoomTypeHotelApi = {
  getHotelRoomTypeAllocations: (hotelId, options = {}) => api.get(`/group-room-type-hotels/hotel/${hotelId}`, options),
  batchSaveRoomTypeAllocations: (data, options = {}) => api.post('/group-room-type-hotels', data, options)
}

export const taxSettingApi = {
  getAllTaxSettings: () => api.get('/tax-settings'),
  getTaxSettingById: (id) => api.get(`/tax-settings/${id}`),
  createTaxSetting: (data) => api.post('/tax-settings', data),
  updateTaxSetting: (id, data) => api.put(`/tax-settings/${id}`, data),
  deleteTaxSetting: (id) => api.delete(`/tax-settings/${id}`)
}

export const enumApi = {
  getAllEnums: () => api.get('/enums/all'),
  getTaxBearer: () => api.get('/enums/tax-bearer'),
  getTaxBaseType: () => api.get('/enums/tax-base-type'),
  getTaxCalculationRule: () => api.get('/enums/tax-calculation-rule'),
  getTaxDeductible: () => api.get('/enums/tax-deductible'),
  getTaxRefundable: () => api.get('/enums/tax-refundable'),
  getTaxSettlementRule: () => api.get('/enums/tax-settlement-rule'),
  getCommonStatus: () => api.get('/enums/common-status'),
  getCurrency: () => api.get('/enums/currency')
}

export const guaranteePolicyApi = {
  getAllGuaranteePolicies: (tenantId, options = {}) => {
    const params = tenantId ? { tenantId } : {}
    return api.get('/guarantee-policies', { params, ...options })
  },
  getGuaranteePolicyById: (id, options = {}) => api.get(`/guarantee-policies/${id}`, options),
  createGuaranteePolicy: (data, options = {}) => api.post('/guarantee-policies', data, options),
  updateGuaranteePolicy: (id, data, options = {}) => api.put(`/guarantee-policies/${id}`, data, options),
  deleteGuaranteePolicy: (id, options = {}) => api.delete(`/guarantee-policies/${id}`, options),
  checkCodeUnique: (code, tenantId, excludeId) => {
    const params = { code, tenantId }
    if (excludeId) {
      params.id = excludeId
    }
    return api.get('/guarantee-policies/check-code', { params })
  }
}

export const cancellationPolicyApi = {
  getAllCancellationPolicies: (tenantId, options = {}) => {
    const params = tenantId ? { tenantId } : {}
    return api.get('/cancellation-policies', { params, ...options })
  },
  getCancellationPolicyById: (id, options = {}) => api.get(`/cancellation-policies/${id}`, options),
  createCancellationPolicy: (data, options = {}) => api.post('/cancellation-policies', data, options),
  updateCancellationPolicy: (id, data, options = {}) => api.put(`/cancellation-policies/${id}`, data, options),
  deleteCancellationPolicy: (id, options = {}) => api.delete(`/cancellation-policies/${id}`, options),
  checkCodeUnique: (code, tenantId, excludeId) => {
    const params = { code, tenantId }
    if (excludeId) {
      params.id = excludeId
    }
    return api.get('/cancellation-policies/check-code', { params })
  }
}

export const ratePlanApi = {
  getRatePlans: (hotelId, options = {}) => {
    const params = hotelId ? { hotelId } : {}
    return api.get('/rate-plans', { params, ...options })
  },
  getRatePlanById: (id, options = {}) => api.get(`/rate-plans/${id}`, options),
  createRatePlan: (data, options = {}) => api.post('/rate-plans', data, options),
  updateRatePlan: (id, data, options = {}) => api.put(`/rate-plans/${id}`, data, options),
  deleteRatePlan: (id, options = {}) => api.delete(`/rate-plans/${id}`, options),
  enableRatePlan: (id, options = {}) => api.put(`/rate-plans/${id}/enable`, options),
  disableRatePlan: (id, options = {}) => api.put(`/rate-plans/${id}/disable`, options),
  checkRateCodeUnique: (code, hotelId, excludeId, options = {}) => {
    const params = { code, hotelId }
    if (excludeId) params.id = excludeId
    return api.get('/rate-plans/check-code', { params, ...options })
  },
  getPermissions: (id, options = {}) => api.get(`/rate-plans/${id}/permissions`, options),
  getSelectableParentRateCodes: (targetDerivativeLevel, excludeId, options = {}) => {
    const params = {}
    if (targetDerivativeLevel) params.targetDerivativeLevel = targetDerivativeLevel
    if (excludeId) params.excludeId = excludeId
    return api.get('/rate-plans/selectable-parents', { params, ...options })
  }
}

export const hotelPriceApi = {
  getPrices: (hotelCode, rateCode, roomTypeCode, startDate, endDate, options = {}) => {
    const params = { hotelCode }
    if (rateCode) params.rateCode = rateCode
    if (roomTypeCode) params.roomTypeCode = roomTypeCode
    if (startDate) params.startDate = startDate
    if (endDate) params.endDate = endDate
    return api.get('/hotel-prices', { params, ...options })
  },
  getPriceQueryData: (hotelCode, rateCode, startDate, endDate, options = {}) => {
    const params = { hotelCode }
    if (rateCode) params.rateCode = rateCode
    if (startDate) params.startDate = startDate
    if (endDate) params.endDate = endDate
    return api.get('/hotel-prices/query', { params, ...options })
  },
  savePrice: (data, options = {}) => api.post('/hotel-prices', data, options),
  batchSavePrices: (data, options = {}) => api.post('/hotel-prices/batch', data, options),
  getPriceLogs: (hotelCode, rateCode, options = {}) => {
    const params = { hotelCode }
    if (rateCode) params.rateCode = rateCode
    return api.get('/hotel-prices/logs', { params, ...options })
  }
}

export const tenantChannelApi = {
  getChannelsGrouped: (tenantId, options = {}) => {
    const params = tenantId ? { tenantId } : {}
    return api.get('/tenant-channels', { params, ...options })
  },
  getAllChannels: (tenantId, options = {}) => {
    const params = tenantId ? { tenantId } : {}
    return api.get('/tenant-channels/all', { params, ...options })
  },
  getChannelById: (id, options = {}) => api.get(`/tenant-channels/${id}`, options),
  getChannelByCode: (channelCode, tenantId, options = {}) => {
    const params = tenantId ? { tenantId } : {}
    return api.get(`/tenant-channels/code/${channelCode}`, { params, ...options })
  },
  updateChannel: (id, data, options = {}) => api.put(`/tenant-channels/${id}`, data, options),
  updateChannelByCode: (channelCode, data, tenantId, options = {}) => {
    const params = tenantId ? { tenantId } : {}
    return api.put(`/tenant-channels/code/${channelCode}`, data, { params, ...options })
  }
}

export const channelPublishApi = {
  getRateCodesWithRoomTypes: (hotelId, options = {}) =>
    api.get('/channel-publish/rate-codes', { params: { hotelId }, ...options }),
  getPublishedRecords: (tenantId, hotelCode, channelCode, options = {}) =>
    api.get('/channel-publish/records', { params: { tenantId, hotelCode, channelCode }, ...options }),
  batchPublish: (data, options = {}) =>
    api.post('/channel-publish/batch', data, options)
}

export const reservationApi = {
  list: (params = {}, options = {}) =>
    api.get('/reservation', { params, ...options }),
  getDetail: (id, options = {}) =>
    api.get(`/reservation/${id}`, options),
  getByCode: (code, options = {}) =>
    api.get(`/reservation/code/${code}`, options),
  create: (data, options = {}) =>
    api.post('/reservation', data, options),
  cancel: (id, data = {}, options = {}) =>
    api.put(`/reservation/${id}/cancel`, data, options),
  updateStatus: (id, data, options = {}) =>
    api.put(`/reservation/${id}/status`, data, options),
  manualIntervene: (id, data, options = {}) =>
    api.put(`/reservation/${id}/manual-intervene`, data, options),
  getByHotel: (hotelId, params = {}, options = {}) =>
    api.get(`/reservation/hotel/${hotelId}`, { params, ...options }),
  getToday: (params = {}, options = {}) =>
    api.get('/reservation/today', { params, ...options }),
  export: (params = {}, options = {}) =>
    api.get('/reservation/export', { params, responseType: 'blob', ...options })
}

export default api

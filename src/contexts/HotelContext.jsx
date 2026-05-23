import React, { createContext, useState, useContext, useEffect } from 'react'
import { hotelApi } from '../utils/api'
import { useTenantContext } from './TenantContext.jsx'

const HotelContext = createContext()
const SELECTED_HOTEL_KEY_PREFIX = 'crs_selected_hotel_'

export const useHotelContext = () => {
  const context = useContext(HotelContext)
  if (!context) {
    throw new Error('useHotelContext must be used within a HotelProvider')
  }
  return context
}

export const HotelProvider = ({ children }) => {
  const [hotels, setHotels] = useState([])
  const [selectedHotel, setSelectedHotel] = useState(null)       // hotelCode
  const [selectedHotelId, setSelectedHotelId] = useState(null)   // id（向后兼容）
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [hasInitializedSelection, setHasInitializedSelection] = useState(false)
  const { selectedTenant } = useTenantContext()

  const getHotelStorageKey = (tenantId) => `${SELECTED_HOTEL_KEY_PREFIX}${tenantId}`

  const fetchHotels = async () => {
    setLoading(true)
    setError(null)
    try {
      if (!selectedTenant) {
        // 没有选择租户时，清空酒店列表
        setHotels([])
        setSelectedHotel(null)
        setSelectedHotelId(null)
        setHasInitializedSelection(false)
        return
      }
      
      // 页面加载时自动获取酒店列表，不触发自动登出
      const response = await hotelApi.getAllHotels(selectedTenant, {
        metadata: { skipAutoLogout: true }
      })
      if (response.success) {
        const hotelList = response.data || []
        setHotels(hotelList)
        if (hotelList.length > 0) {
          const savedHotelCode = localStorage.getItem(getHotelStorageKey(selectedTenant))
          const matchedHotel = savedHotelCode
            ? hotelList.find(hotel => hotel.hotelCode === savedHotelCode)
            : null
          const nextHotel = matchedHotel || hotelList[0]
          setSelectedHotel(nextHotel.hotelCode)
          setSelectedHotelId(nextHotel.id)
        } else {
          // 没有酒店时，清空选中状态
          setSelectedHotel(null)
          setSelectedHotelId(null)
        }
        setHasInitializedSelection(true)
      } else {
        // 添加模拟酒店数据
        const mockHotels = [
          { id: 1, chineseName: '北京环球影城大酒店', hotelCode: 'BJS001' },
          { id: 2, chineseName: '上海迪士尼乐园酒店', hotelCode: 'SHA001' },
          { id: 3, chineseName: '广州长隆酒店', hotelCode: 'GZ001' },
          { id: 4, chineseName: '深圳华侨城洲际大酒店', hotelCode: 'SZ001' },
          { id: 5, chineseName: '杭州西子湖四季酒店', hotelCode: 'HZ001' }
        ]
        setHotels(mockHotels)
        setSelectedHotel(mockHotels[0].hotelCode)
        setSelectedHotelId(mockHotels[0].id)
        setHasInitializedSelection(true)
      }
    } catch (err) {
      setError('获取酒店列表失败')
      console.error('获取酒店列表失败:', err)
      // 添加模拟酒店数据
      const mockHotels = [
        { id: 1, chineseName: '北京环球影城大酒店', hotelCode: 'BJS001' },
        { id: 2, chineseName: '上海迪士尼乐园酒店', hotelCode: 'SHA001' },
        { id: 3, chineseName: '广州长隆酒店', hotelCode: 'GZ001' },
        { id: 4, chineseName: '深圳华侨城洲际大酒店', hotelCode: 'SZ001' },
        { id: 5, chineseName: '杭州西子湖四季酒店', hotelCode: 'HZ001' }
      ]
      setHotels(mockHotels)
      setSelectedHotel(mockHotels[0].hotelCode)
      setSelectedHotelId(mockHotels[0].id)
      setHasInitializedSelection(true)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    setHasInitializedSelection(false)
    fetchHotels()
  }, [selectedTenant])

  const changeHotel = (hotelCode) => {
    setSelectedHotel(hotelCode || null)
    // 根据hotelCode查找对应的id
    const hotel = hotels.find(h => h.hotelCode === hotelCode)
    setSelectedHotelId(hotel ? hotel.id : null)
  }

  useEffect(() => {
    if (!selectedTenant || !hasInitializedSelection) {
      return
    }

    const storageKey = getHotelStorageKey(selectedTenant)
    if (selectedHotel) {
      localStorage.setItem(storageKey, selectedHotel)
    } else {
      localStorage.removeItem(storageKey)
    }
  }, [selectedHotel, selectedTenant])

  const value = {
    hotels,
    selectedHotel,       // hotelCode
    selectedHotelId,     // id（向后兼容）
    loading,
    error,
    changeHotel,
    fetchHotels
  }

  return (
    <HotelContext.Provider value={value}>
      {children}
    </HotelContext.Provider>
  )
}

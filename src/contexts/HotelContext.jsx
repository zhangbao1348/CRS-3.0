import { createContext, useState, useContext, useEffect, useCallback } from 'react'
import { hotelApi } from '../utils/api'
import { useTenantContext } from './TenantContext.jsx'

const HotelContext = createContext()
const SELECTED_HOTEL_KEY_PREFIX = 'crs_selected_hotel_'
const getHotelStorageKey = (tenantId) => `${SELECTED_HOTEL_KEY_PREFIX}${tenantId}`

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

  const fetchHotels = useCallback(async () => {
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
        setHotels([])
        setSelectedHotel(null)
        setSelectedHotelId(null)
        setError(response.message || '获取酒店列表失败')
        setHasInitializedSelection(true)
      }
    } catch (err) {
      setError('获取酒店列表失败')
      console.error('获取酒店列表失败:', err)
      setHotels([])
      setSelectedHotel(null)
      setSelectedHotelId(null)
      setHasInitializedSelection(true)
    } finally {
      setLoading(false)
    }
  }, [selectedTenant])

  useEffect(() => {
    setHasInitializedSelection(false)
    fetchHotels()
  }, [fetchHotels])

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
  }, [hasInitializedSelection, selectedHotel, selectedTenant])

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

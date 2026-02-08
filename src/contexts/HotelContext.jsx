import React, { createContext, useState, useContext, useEffect } from 'react'
import axios from 'axios'

const HotelContext = createContext()

export const useHotelContext = () => {
  const context = useContext(HotelContext)
  if (!context) {
    throw new Error('useHotelContext must be used within a HotelProvider')
  }
  return context
}

export const HotelProvider = ({ children }) => {
  const [hotels, setHotels] = useState([])
  const [selectedHotel, setSelectedHotel] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // 获取酒店列表
  useEffect(() => {
    const fetchHotels = async () => {
      setLoading(true)
      setError(null)
      try {
        const response = await axios.get('http://localhost:8080/api/hotels')
        const hotelList = response.data
        setHotels(hotelList)
        // 默认选中第一个酒店
        if (hotelList.length > 0 && !selectedHotel) {
          setSelectedHotel(hotelList[0].id)
        }
      } catch (err) {
        setError('获取酒店列表失败')
        console.error('获取酒店列表失败:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchHotels()
  }, [])

  // 切换酒店
  const changeHotel = (hotelId) => {
    setSelectedHotel(hotelId)
  }

  return (
    <HotelContext.Provider value={{ hotels, selectedHotel, loading, error, changeHotel }}>
      {children}
    </HotelContext.Provider>
  )
}

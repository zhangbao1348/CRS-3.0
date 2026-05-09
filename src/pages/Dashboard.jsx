import React from 'react'
import { useHotelContext } from '../contexts/HotelContext'
import GroupDashboard from './Dashboard/GroupDashboard'
import HotelDashboard from './Dashboard/HotelDashboard'

/**
 * 首页路由分发器
 * 根据当前是否选择了酒店，自动切换集团首页或门店首页
 * - 未选择酒店 → GroupDashboard（集团运营总览）
 * - 已选择酒店 → HotelDashboard（门店运营概览）
 */
const Dashboard = () => {
  const { selectedHotel } = useHotelContext()

  if (selectedHotel) {
    return <HotelDashboard />
  }

  return <GroupDashboard />
}

export default Dashboard

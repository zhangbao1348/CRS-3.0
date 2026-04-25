import React from 'react'
import { Tabs } from 'antd'
import HotelRoomStatus from './HotelRoomStatus'
import RoomTypeRoomStatus from './RoomTypeRoomStatus'
import PriceLevelRoomStatus from './PriceLevelRoomStatus'
import ChannelLevelRoomStatus from './ChannelLevelRoomStatus'
import ChannelRoomTypeRoomStatus from './ChannelRoomTypeRoomStatus'
import MarketLevelRoomStatus from './MarketLevelRoomStatus'
import RateCategoryLevelRoomStatus from './RateCategoryLevelRoomStatus'

const RoomStatus = () => {
  return (
    <div>
      <Tabs 
        defaultActiveKey="1" 
        style={{ marginBottom: 16 }}
        items={[
          {
            key: '1',
            label: '酒店级房态管理',
            children: <HotelRoomStatus />
          },
          {
            key: '2',
            label: '房型级房态管理',
            children: <RoomTypeRoomStatus />
          },
          {
            key: '3',
            label: '房价级房态管理',
            children: <PriceLevelRoomStatus />
          },
          {
            key: '4',
            label: '渠道级房态管理',
            children: <ChannelLevelRoomStatus />
          },
          {
            key: '5',
            label: '渠道+房型级房态',
            children: <ChannelRoomTypeRoomStatus />
          },
          {
            key: '6',
            label: '市场码级房态管理',
            children: <MarketLevelRoomStatus />
          },
          {
            key: '7',
            label: '房价大类房态控制',
            children: <RateCategoryLevelRoomStatus />
          }
        ]}
      />
    </div>
  )
}

export default RoomStatus
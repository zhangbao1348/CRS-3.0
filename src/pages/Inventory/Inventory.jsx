import { Tabs } from 'antd'
import { CalendarOutlined } from '@ant-design/icons'
import MainInventoryCalendar from './MainInventoryCalendar'
import PMSInventoryCalendar from './PMSInventoryCalendar'
import HotelOverbooking from './HotelOverbooking'
import RoomTypeOverbooking from './RoomTypeOverbooking'
import PriceLevelInventory from './PriceLevelInventory'
import ChannelLevelInventory from './ChannelLevelInventory'
import MarketLevelInventory from './MarketLevelInventory'
import ChannelRoomTypeInventory from './ChannelRoomTypeInventory'
import RateCategoryLevelInventory from './RateCategoryLevelInventory'

const Inventory = () => {
  return (
    <div className="fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h1 className="page-title"><CalendarOutlined /> 房控日历</h1>
      </div>

      <Tabs defaultActiveKey="1" style={{ marginBottom: 16 }}
        items={[
          { key: '1', label: '主要房控日历', children: <MainInventoryCalendar /> },
          { key: '2', label: 'PMS房控日历', children: <PMSInventoryCalendar /> },
          { key: '3', label: '酒店超预订管理', children: <HotelOverbooking /> },
          { key: '4', label: '房型超预订管理', children: <RoomTypeOverbooking /> },
          { key: '5', label: '房价级房量管理', children: <PriceLevelInventory /> },
          { key: '6', label: '渠道级房量管理', children: <ChannelLevelInventory /> },
          { key: '7', label: '市场码级房量管理', children: <MarketLevelInventory /> },
          { key: '8', label: '渠道+房型级房量', children: <ChannelRoomTypeInventory /> },
          { key: '9', label: '房价大类房量控制', children: <RateCategoryLevelInventory /> },
        ]}
      />
    </div>
  )
}

export default Inventory

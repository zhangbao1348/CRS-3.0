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
import { PageScaffold } from '../../components/ui'

const Inventory = () => {
  return (
    <PageScaffold
      className="fade-in inventory-page"
      eyebrow="INVENTORY CONTROL"
      title={<><CalendarOutlined /> 房控日历</>}
      description="按库存维度查看和维护可售房量；切换标签不会改变当前酒店范围。"
    >
      <Tabs defaultActiveKey="1" className="inventory-page__tabs" destroyOnHidden
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
    </PageScaffold>
  )
}

export default Inventory

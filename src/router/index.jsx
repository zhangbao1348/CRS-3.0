import React from 'react'
import { createBrowserRouter } from 'react-router-dom'
import MainLayout from '../components/Layout/MainLayout'

// 首页组件
import Dashboard from '../pages/Dashboard'

// 订单管理
import ReservationList from '../pages/Reservation/ReservationList'

// 房控日历
import Inventory from '../pages/Inventory/Inventory'
import RoomInventory from '../pages/Inventory/RoomInventory'

// 集团管理页面
import HotelManagement from '../pages/GroupManagement/HotelManagement'
import AddHotel from '../pages/HotelManagement/AddHotel'
import EditHotel from '../pages/HotelManagement/EditHotel'
import GroupRoomType from '../pages/GroupManagement/GroupRoomType'
import AddGroupRoomType from '../pages/GroupManagement/AddGroupRoomType'
import GroupRateCode from '../pages/GroupManagement/GroupRateCode'
import AddGroupRateCode from '../pages/GroupManagement/AddGroupRateCode'
import MarketCode from '../pages/GroupManagement/MarketCode'
import ChannelCode from '../pages/GroupManagement/ChannelCode'
import SourceCode from '../pages/GroupManagement/SourceCode'
import TaxSetting from '../pages/GroupManagement/TaxSetting'
import PackageSetting from '../pages/GroupManagement/PackageSetting'
import AddPackage from '../pages/GroupManagement/AddPackage'
import EditPackage from '../pages/GroupManagement/EditPackage'
import GroupGuarantee from '../pages/GroupManagement/GroupGuarantee'
import GroupCancellation from '../pages/GroupManagement/GroupCancellation'
import GroupFacility from '../pages/GroupManagement/GroupFacility'

// 酒店管理页面
import RoomType from '../pages/HotelManagement/RoomType'
import RoomTypeDiff from '../pages/HotelManagement/RoomTypeDiff'
import PersonDiff from '../pages/HotelManagement/PersonDiff'
import RackRateCalendar from '../pages/HotelManagement/RackRateCalendar'
import RateCode from '../pages/HotelManagement/RateCode'

// 渠道管理页面
import ChannelList from '../pages/ChannelManagement/ChannelList'
import ChannelMapping from '../pages/ChannelManagement/ChannelMapping'

// 价格管理页面
import RateRoomTypeDiff from '../pages/RateManagement/RoomTypeDiff'
import RatePersonDiff from '../pages/RateManagement/PersonDiff'
import RatePackageSetting from '../pages/RateManagement/PackageSetting'
import RatePlan from '../pages/RateManagement/RatePlan'
import AddRatePlan from '../pages/RateManagement/AddRatePlan'
import RackRate from '../pages/RateManagement/RackRate'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout><Dashboard /></MainLayout>
  },
  {
    path: '/dashboard',
    element: <MainLayout><Dashboard /></MainLayout>
  },
  {
    path: '/reservation',
    element: <MainLayout><ReservationList /></MainLayout>
  },
  {
    path: '/reservation/reservation-list',
    element: <MainLayout><ReservationList /></MainLayout>
  },
  {
    path: '/reservation/reservation-history',
    element: <MainLayout><ReservationList /></MainLayout>
  },
  {
    path: '/inventory',
    element: <MainLayout><Inventory /></MainLayout>
  },
  {
    path: '/group-management',
    element: <MainLayout><Dashboard /></MainLayout>
  },
  {
    path: '/group-management/hotel-management',
    element: <MainLayout><HotelManagement /></MainLayout>
  },
  {
    path: '/group-management/add-hotel',
    element: <MainLayout><AddHotel /></MainLayout>
  },
  {
    path: '/hotel-management/edit-hotel',
    element: <MainLayout><EditHotel /></MainLayout>
  },
  {
    path: '/group-management/group-room-type',
    element: <MainLayout><GroupRoomType /></MainLayout>
  },
  {
    path: '/group-management/add-group-room-type',
    element: <MainLayout><AddGroupRoomType /></MainLayout>
  },
  {
    path: '/group-management/group-rate-code',
    element: <MainLayout><GroupRateCode /></MainLayout>
  },
  {
    path: '/group-management/add-rate-code',
    element: <MainLayout><AddGroupRateCode /></MainLayout>
  },
  {
    path: '/group-management/market-code',
    element: <MainLayout><MarketCode /></MainLayout>
  },
  {
    path: '/group-management/channel-code',
    element: <MainLayout><ChannelCode /></MainLayout>
  },
  {
    path: '/group-management/source-code',
    element: <MainLayout><SourceCode /></MainLayout>
  },
  {
    path: '/group-management/tax-setting',
    element: <MainLayout><TaxSetting /></MainLayout>
  },
  {
    path: '/group-management/package-setting',
    element: <MainLayout><PackageSetting /></MainLayout>
  },
  {
    path: '/group-management/add-package',
    element: <MainLayout><AddPackage /></MainLayout>
  },
  {
    path: '/group-management/edit-package',
    element: <MainLayout><EditPackage /></MainLayout>
  },
  {
    path: '/group-management/group-guarantee',
    element: <MainLayout><GroupGuarantee /></MainLayout>
  },
  {
    path: '/group-management/group-cancellation',
    element: <MainLayout><GroupCancellation /></MainLayout>
  },
  {
    path: '/group-management/facility-management',
    element: <MainLayout><GroupFacility /></MainLayout>
  },
  {
    path: '/hotel-operation',
    element: <MainLayout><Dashboard /></MainLayout>
  },
  {
    path: '/hotel-operation/room-type',
    element: <MainLayout><RoomType /></MainLayout>
  },
  {
    path: '/hotel-operation/room-type-diff',
    element: <MainLayout><RoomTypeDiff /></MainLayout>
  },
  {
    path: '/hotel-operation/person-diff',
    element: <MainLayout><PersonDiff /></MainLayout>
  },
  {
    path: '/hotel-operation/rack-rate-calendar',
    element: <MainLayout><RackRateCalendar /></MainLayout>
  },
  {
    path: '/hotel-operation/rate-code',
    element: <MainLayout><RateCode /></MainLayout>
  },
  // 新增房型管理路由，匹配菜单链接
  {
    path: '/room-management',
    element: <MainLayout><RoomType /></MainLayout>
  },
  {
    path: '/room-management/room-type',
    element: <MainLayout><RoomType /></MainLayout>
  },
  {
    path: '/channel-management',
    element: <MainLayout><ChannelList /></MainLayout>
  },
  {
    path: '/channel-management/channel-list',
    element: <MainLayout><ChannelList /></MainLayout>
  },
  {
    path: '/channel-management/channel-mapping',
    element: <MainLayout><ChannelMapping /></MainLayout>
  },
  {
    path: '/rate-management',
    element: <MainLayout><RatePlan /></MainLayout>
  },
  {
    path: '/rate-management/rate-plan',
    element: <MainLayout><RatePlan /></MainLayout>
  },
  {
    path: '/rate-management/seasonal-rate',
    element: <MainLayout><RatePersonDiff /></MainLayout>
  },
  {
    path: '/rate-management/room-type-diff',
    element: <MainLayout><RateRoomTypeDiff /></MainLayout>
  },
  {
    path: '/rate-management/person-diff',
    element: <MainLayout><RatePersonDiff /></MainLayout>
  },
  {
    path: '/rate-management/package-setting',
    element: <MainLayout><RatePackageSetting /></MainLayout>
  },
  {
    path: '/rate-management/rate-plan',
    element: <MainLayout><RatePlan /></MainLayout>
  },
  {
    path: '/rate-management/add-rate-plan',
    element: <MainLayout><AddRatePlan /></MainLayout>
  },
  {
    path: '/rate-management/rack-rate',
    element: <MainLayout><RackRate /></MainLayout>
  }
])
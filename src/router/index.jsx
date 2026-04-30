import React from 'react'
import { createBrowserRouter, Navigate } from 'react-router-dom'
import MainLayout from '../components/Layout/MainLayout'
import ProtectedRoute from '../components/ProtectedRoute'

// 登录页面
import Login from '../pages/Auth/Login'

// 首页组件
import Dashboard from '../pages/Dashboard'

// 订单管理
import ReservationList from '../pages/Reservation/ReservationList'
import ReservationDetail from '../pages/Reservation/ReservationDetail'

// 房控日历
import Inventory from '../pages/Inventory/Inventory'
import RoomStatus from '../pages/Inventory/RoomStatus'
import RoomInventory from '../pages/Inventory/RoomInventory'
import BookingControl from '../pages/Inventory/BookingControl'

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
import AddEditTax from '../pages/GroupManagement/AddEditTax'
import PackageSetting from '../pages/GroupManagement/PackageSetting'
import AddPackage from '../pages/GroupManagement/AddPackage'
import EditPackage from '../pages/GroupManagement/EditPackage'
import GroupGuarantee from '../pages/GroupManagement/GroupGuarantee'
import AddEditGuarantee from '../pages/GroupManagement/AddEditGuarantee'
import GroupCancellation from '../pages/GroupManagement/GroupCancellation'
import AddEditCancellation from '../pages/GroupManagement/AddEditCancellation'
import GroupFacility from '../pages/GroupManagement/GroupFacility'
import ArchiveManagement from '../pages/GroupManagement/ArchiveManagement'
import AddArchive from '../pages/GroupManagement/AddArchive'
import EditArchive from '../pages/GroupManagement/EditArchive'
import RateCategory from '../pages/GroupManagement/RateCategory'
import RoomTypeCategory from '../pages/GroupManagement/RoomTypeCategory'

// 酒店管理页面
import RoomType from '../pages/HotelManagement/RoomType'
import RackRateCalendar from '../pages/HotelManagement/RackRateCalendar'
import RateCode from '../pages/HotelManagement/RateCode'

// 渠道管理页面
import ChannelList from '../pages/ChannelManagement/ChannelList'
import ChannelMapping from '../pages/ChannelManagement/ChannelMapping'
import FliggySetting from '../pages/ChannelManagement/FliggySetting'
import ChannelSetting from '../pages/ChannelManagement/ChannelSetting'
import CtripSetting from '../pages/ChannelManagement/CtripSetting'
import AddCtripPromotion from '../pages/ChannelManagement/AddCtripPromotion'
import CtripPromotionRegistration from '../pages/ChannelManagement/CtripPromotionRegistration'

// 价格管理页面
import RatePackageSetting from '../pages/RateManagement/PackageSetting'
import RatePlan from '../pages/RateManagement/RatePlan'
import AddRatePlan from '../pages/RateManagement/AddRatePlan'
import RackRate from '../pages/RateManagement/RackRate'
import AddRatePackage from '../pages/RateManagement/AddPackage'
import EditRatePackage from '../pages/RateManagement/EditPackage'
import PriceQuery from '../pages/RateManagement/PriceQuery'

// 系统设置页面
import UserManagement from '../pages/SystemSettings/UserManagement'
import RoleManagement from '../pages/SystemSettings/RoleManagement'
import GroupSettings from '../pages/SystemSettings/GroupSettings'
// CRM页面
import CrmDashboard from '../pages/Crm/CrmDashboard'

// 小程序页面
import MiniProgramDashboard from '../pages/MiniProgram/MiniProgramDashboard'

// 超管设置页面
import TenantManagement from '../pages/SuperAdminSettings/TenantManagement'
import AddEditTenant from '../pages/SuperAdminSettings/AddEditTenant'
import SuperAdminRoleManagement from '../pages/SuperAdminSettings/RoleManagement'
import SuperAdminMenuManagement from '../pages/SuperAdminSettings/MenuManagement'

// 集团促销管理页面
import OTAPromotionManagement from '../pages/GroupPromotionManagement/OTAPromotionManagement'
import AddOTAPromotion from '../pages/GroupPromotionManagement/AddOTAPromotion'
import CtripActivityManagement from '../pages/GroupPromotionManagement/CtripActivityManagement'
import CtripActivityRegistration from '../pages/GroupPromotionManagement/CtripActivityRegistration'

// 报表页面
import ReservationReports from '../pages/Reports/ReservationReports'
import OccupancyReports from '../pages/Reports/OccupancyReports'
import RevenueReports from '../pages/Reports/RevenueReports'
import TestPage from '../pages/TestPage'
import TestPageWithLayout from '../pages/TestPageWithLayout'

// 演示模式标志
const DEMO_MODE = false

export const router = createBrowserRouter([
  {
    path: '/login',
    element: DEMO_MODE ? <Navigate to="/" replace /> : <Login />
  },
  {
    path: '/test',
    element: <TestPage />
  },
  {
    path: '/test-with-layout',
    element: <TestPageWithLayout />
  },
  {
    path: '/',
    element: <ProtectedRoute><MainLayout><Dashboard /></MainLayout></ProtectedRoute>
  },
  {
    path: '/dashboard',
    element: <ProtectedRoute><MainLayout><Dashboard /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reservation',
    element: <ProtectedRoute><MainLayout><ReservationList /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reservation/reservation-list',
    element: <ProtectedRoute><MainLayout><ReservationList /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reservation/reservation-detail',
    element: <ProtectedRoute><MainLayout><ReservationDetail /></MainLayout></ProtectedRoute>
  },

  {
    path: '/inventory',
    element: <ProtectedRoute><MainLayout><Inventory /></MainLayout></ProtectedRoute>
  },
  {
    path: '/inventory/room-status',
    element: <ProtectedRoute><MainLayout><RoomStatus /></MainLayout></ProtectedRoute>
  },
  {
    path: '/inventory/booking-control',
    element: <ProtectedRoute><MainLayout><BookingControl /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management',
    element: <ProtectedRoute><MainLayout><Dashboard /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/hotel-management',
    element: <ProtectedRoute><MainLayout><HotelManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/add-hotel',
    element: <ProtectedRoute><MainLayout><AddHotel /></MainLayout></ProtectedRoute>
  },
  {
    path: '/hotel-management/edit-hotel',
    element: <ProtectedRoute><MainLayout><EditHotel /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/group-room-type',
    element: <ProtectedRoute><MainLayout><GroupRoomType /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/add-group-room-type',
    element: <ProtectedRoute><MainLayout><AddGroupRoomType /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/group-rate-code',
    element: <ProtectedRoute><MainLayout><GroupRateCode /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/add-rate-code',
    element: <ProtectedRoute><MainLayout><AddGroupRateCode /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/market-code',
    element: <ProtectedRoute><MainLayout><MarketCode /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/channel-code',
    element: <ProtectedRoute><MainLayout><ChannelCode /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/source-code',
    element: <ProtectedRoute><MainLayout><SourceCode /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/tax-setting',
    element: <ProtectedRoute><MainLayout><TaxSetting /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/add-edit-tax',
    element: <ProtectedRoute><MainLayout><AddEditTax /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/package-setting',
    element: <ProtectedRoute><MainLayout><PackageSetting /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/add-package',
    element: <ProtectedRoute><MainLayout><AddPackage /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/edit-package',
    element: <ProtectedRoute><MainLayout><EditPackage /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/group-guarantee',
    element: <ProtectedRoute><MainLayout><GroupGuarantee /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/add-edit-guarantee',
    element: <ProtectedRoute><MainLayout><AddEditGuarantee /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/group-cancellation',
    element: <ProtectedRoute><MainLayout><GroupCancellation /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/add-edit-cancellation',
    element: <ProtectedRoute><MainLayout><AddEditCancellation /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/facility-management',
    element: <ProtectedRoute><MainLayout><GroupFacility /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/archive-management',
    element: <ProtectedRoute><MainLayout><ArchiveManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/archive-management/add',
    element: <ProtectedRoute><MainLayout><AddArchive /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/archive-management/edit/:id',
    element: <ProtectedRoute><MainLayout><EditArchive /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/rate-category',
    element: <ProtectedRoute><MainLayout><RateCategory /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-management/room-type-category',
    element: <ProtectedRoute><MainLayout><RoomTypeCategory /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management',
    element: <ProtectedRoute><MainLayout><Dashboard /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management/ota-promotion-management',
    element: <ProtectedRoute><MainLayout><OTAPromotionManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management/ota-promotion-management/add',
    element: <ProtectedRoute><MainLayout><AddOTAPromotion /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management/ctrip-activity-management',
    element: <ProtectedRoute><MainLayout><CtripActivityManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management/ctrip-activity-registration',
    element: <ProtectedRoute><MainLayout><CtripActivityRegistration /></MainLayout></ProtectedRoute>
  },
  {
    path: '/hotel-operation',
    element: <ProtectedRoute><MainLayout><Dashboard /></MainLayout></ProtectedRoute>
  },
  {
    path: '/hotel-operation/room-type',
    element: <ProtectedRoute><MainLayout><RoomType /></MainLayout></ProtectedRoute>
  },
  {
    path: '/hotel-operation/rack-rate-calendar',
    element: <ProtectedRoute><MainLayout><RackRateCalendar /></MainLayout></ProtectedRoute>
  },
  {
    path: '/hotel-operation/rate-code',
    element: <ProtectedRoute><MainLayout><RateCode /></MainLayout></ProtectedRoute>
  },
  {
    path: '/room-management',
    element: <ProtectedRoute><MainLayout><RoomType /></MainLayout></ProtectedRoute>
  },
  {
    path: '/room-management/room-type',
    element: <ProtectedRoute><MainLayout><RoomType /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management',
    element: <ProtectedRoute><MainLayout><ChannelList /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/channel-list',
    element: <ProtectedRoute><MainLayout><ChannelList /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/channel-mapping',
    element: <ProtectedRoute><MainLayout><ChannelMapping /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/fliggy-setting',
    element: <ProtectedRoute><MainLayout><FliggySetting /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/channel-setting/:channelCode',
    element: <ProtectedRoute><MainLayout><ChannelSetting /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/ctrip-setting',
    element: <ProtectedRoute><MainLayout><CtripSetting /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/ctrip-setting/promotion/add',
    element: <ProtectedRoute><MainLayout><AddCtripPromotion /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/ctrip-setting/promotion/edit/:id',
    element: <ProtectedRoute><MainLayout><AddCtripPromotion /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/ctrip-setting/promotion/registration/:id',
    element: <ProtectedRoute><MainLayout><CtripPromotionRegistration /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management',
    element: <ProtectedRoute><MainLayout><RatePlan /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/rate-plan',
    element: <ProtectedRoute><MainLayout><RatePlan /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/package-setting',
    element: <ProtectedRoute><MainLayout><RatePackageSetting /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/add-package',
    element: <ProtectedRoute><MainLayout><AddRatePackage /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/edit-package',
    element: <ProtectedRoute><MainLayout><EditRatePackage /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/add-rate-plan',
    element: <ProtectedRoute><MainLayout><AddRatePlan /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/edit-rate-plan/:id',
    element: <ProtectedRoute><MainLayout><AddRatePlan /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/rack-rate',
    element: <ProtectedRoute><MainLayout><RackRate /></MainLayout></ProtectedRoute>
  },
  {
    path: '/rate-management/price-query',
    element: <ProtectedRoute><MainLayout><PriceQuery /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reports',
    element: <ProtectedRoute><MainLayout><ReservationReports /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reports/reservation-reports',
    element: <ProtectedRoute><MainLayout><ReservationReports /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reports/occupancy-reports',
    element: <ProtectedRoute><MainLayout><OccupancyReports /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reports/revenue-reports',
    element: <ProtectedRoute><MainLayout><RevenueReports /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reports/data-export',
    element: <ProtectedRoute><MainLayout><ReservationReports /></MainLayout></ProtectedRoute>
  },
  {
    path: '/system-settings',
    element: <ProtectedRoute><MainLayout><UserManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/system-settings/user-management',
    element: <ProtectedRoute><MainLayout><UserManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/system-settings/role-management',
    element: <ProtectedRoute><MainLayout><RoleManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/system-settings/group-settings',
    element: <ProtectedRoute><MainLayout><GroupSettings /></MainLayout></ProtectedRoute>
  },

  {
    path: '/crm/dashboard',
    element: <ProtectedRoute><MainLayout><CrmDashboard /></MainLayout></ProtectedRoute>
  },
  {
    path: '/mini-program/dashboard',
    element: <ProtectedRoute><MainLayout><MiniProgramDashboard /></MainLayout></ProtectedRoute>
  },
  {
    path: '/super-admin-settings/tenant-management',
    element: <ProtectedRoute><MainLayout><TenantManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/super-admin-settings/tenant-management/add',
    element: <ProtectedRoute><MainLayout><AddEditTenant /></MainLayout></ProtectedRoute>
  },
  {
    path: '/super-admin-settings/tenant-management/:id',
    element: <ProtectedRoute><MainLayout><AddEditTenant /></MainLayout></ProtectedRoute>
  },
  {
    path: '/super-admin-settings/role-management',
    element: <ProtectedRoute><MainLayout><SuperAdminRoleManagement /></MainLayout></ProtectedRoute>
  },
  {
    path: '/super-admin-settings/menu-management',
    element: <ProtectedRoute><MainLayout><SuperAdminMenuManagement /></MainLayout></ProtectedRoute>
  }
])
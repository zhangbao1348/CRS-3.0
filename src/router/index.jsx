import { createBrowserRouter, Navigate } from 'react-router-dom'
import { lazy } from 'react'
import MainLayout from '../components/Layout/MainLayout'
import ProtectedRoute from '../components/ProtectedRoute'

// 登录页面
import Login from '../pages/Auth/Login'

// 首页组件
const Dashboard = lazy(() => import('../pages/Dashboard'))

// 订单管理
const ReservationList = lazy(() => import('../pages/Reservation/ReservationList'))
const ReservationDetail = lazy(() => import('../pages/Reservation/ReservationDetail'))

// 房控日历
const Inventory = lazy(() => import('../pages/Inventory/Inventory'))
const RoomStatus = lazy(() => import('../pages/Inventory/RoomStatus'))
const BookingControl = lazy(() => import('../pages/Inventory/BookingControl'))

// 集团管理页面
const HotelManagement = lazy(() => import('../pages/GroupManagement/HotelManagement'))
const AddHotel = lazy(() => import('../pages/HotelManagement/AddHotel'))
const EditHotel = lazy(() => import('../pages/HotelManagement/EditHotel'))
const GroupRoomType = lazy(() => import('../pages/GroupManagement/GroupRoomType'))
const AddGroupRoomType = lazy(() => import('../pages/GroupManagement/AddGroupRoomType'))
const GroupRateCode = lazy(() => import('../pages/GroupManagement/GroupRateCode'))
const AddGroupRateCode = lazy(() => import('../pages/GroupManagement/AddGroupRateCode'))
const MarketCode = lazy(() => import('../pages/GroupManagement/MarketCode'))
const ChannelCode = lazy(() => import('../pages/GroupManagement/ChannelCode'))
const SourceCode = lazy(() => import('../pages/GroupManagement/SourceCode'))
const TaxSetting = lazy(() => import('../pages/GroupManagement/TaxSetting'))
const AddEditTax = lazy(() => import('../pages/GroupManagement/AddEditTax'))
const PackageSetting = lazy(() => import('../pages/GroupManagement/PackageSetting'))
const AddPackage = lazy(() => import('../pages/GroupManagement/AddPackage'))
const EditPackage = lazy(() => import('../pages/GroupManagement/EditPackage'))
const GroupGuarantee = lazy(() => import('../pages/GroupManagement/GroupGuarantee'))
const AddEditGuarantee = lazy(() => import('../pages/GroupManagement/AddEditGuarantee'))
const GroupCancellation = lazy(() => import('../pages/GroupManagement/GroupCancellation'))
const AddEditCancellation = lazy(() => import('../pages/GroupManagement/AddEditCancellation'))
const GroupFacility = lazy(() => import('../pages/GroupManagement/GroupFacility'))
const ArchiveManagement = lazy(() => import('../pages/GroupManagement/ArchiveManagement'))
const AddArchive = lazy(() => import('../pages/GroupManagement/AddArchive'))
const EditArchive = lazy(() => import('../pages/GroupManagement/EditArchive'))
const RateCategory = lazy(() => import('../pages/GroupManagement/RateCategory'))
const RoomTypeCategory = lazy(() => import('../pages/GroupManagement/RoomTypeCategory'))

// 酒店管理页面
const RoomType = lazy(() => import('../pages/HotelManagement/RoomType'))
const RackRateCalendar = lazy(() => import('../pages/HotelManagement/RackRateCalendar'))
const RateCode = lazy(() => import('../pages/HotelManagement/RateCode'))

// 渠道管理页面
const ChannelList = lazy(() => import('../pages/ChannelManagement/ChannelList'))
const ChannelMapping = lazy(() => import('../pages/ChannelManagement/ChannelMapping'))
const ChannelSetting = lazy(() => import('../pages/ChannelManagement/ChannelSetting'))
const IntegrationUnavailable = lazy(() => import('../pages/IntegrationUnavailable'))

// 价格管理页面
const RatePackageSetting = lazy(() => import('../pages/RateManagement/PackageSetting'))
const RatePlan = lazy(() => import('../pages/RateManagement/RatePlan'))
const AddRatePlan = lazy(() => import('../pages/RateManagement/AddRatePlan'))
const RackRate = lazy(() => import('../pages/RateManagement/RackRate'))
const AddRatePackage = lazy(() => import('../pages/RateManagement/AddPackage'))
const EditRatePackage = lazy(() => import('../pages/RateManagement/EditPackage'))
const PriceQuery = lazy(() => import('../pages/RateManagement/PriceQuery'))

// 系统设置页面
const UserManagement = lazy(() => import('../pages/SystemSettings/UserManagement'))
const RoleManagement = lazy(() => import('../pages/SystemSettings/RoleManagement'))
const GroupSettings = lazy(() => import('../pages/SystemSettings/GroupSettings'))
const DictionaryManagement = lazy(() => import('../pages/SystemSettings/DictionaryManagement'))
// CRM页面
const CrmDashboard = lazy(() => import('../pages/Crm/CrmDashboard'))

// 小程序页面
const MiniProgramDashboard = lazy(() => import('../pages/MiniProgram/MiniProgramDashboard'))

// 超管设置页面
const TenantManagement = lazy(() => import('../pages/SuperAdminSettings/TenantManagement'))
const AddEditTenant = lazy(() => import('../pages/SuperAdminSettings/AddEditTenant'))
const SuperAdminRoleManagement = lazy(() => import('../pages/SuperAdminSettings/RoleManagement'))
const SuperAdminMenuManagement = lazy(() => import('../pages/SuperAdminSettings/MenuManagement'))

// 报表页面
const ReservationReports = lazy(() => import('../pages/Reports/ReservationReports'))
const OccupancyReports = lazy(() => import('../pages/Reports/OccupancyReports'))
const RevenueReports = lazy(() => import('../pages/Reports/RevenueReports'))
const DataExport = lazy(() => import('../pages/Reports/DataExport'))
const SystemTraceConsole = lazy(() => import('../pages/Reports/SystemTraceConsole'))
export const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />
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
    element: <ProtectedRoute><MainLayout><IntegrationUnavailable title="OTA 促销尚未接入" system="OTA 促销平台" /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management/ota-promotion-management/add',
    element: <ProtectedRoute><MainLayout><IntegrationUnavailable title="OTA 促销尚未接入" system="OTA 促销平台" /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management/ctrip-activity-management',
    element: <ProtectedRoute><MainLayout><IntegrationUnavailable title="携程活动尚未接入" system="携程活动平台" /></MainLayout></ProtectedRoute>
  },
  {
    path: '/group-promotion-management/ctrip-activity-registration',
    element: <ProtectedRoute><MainLayout><IntegrationUnavailable title="携程活动报名尚未接入" system="携程活动平台" /></MainLayout></ProtectedRoute>
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
    element: <Navigate to="/channel-management/channel-setting/FLIGGY" replace />
  },
  {
    path: '/channel-management/channel-setting/:channelCode',
    element: <ProtectedRoute><MainLayout><ChannelSetting /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/ctrip-setting',
    element: <Navigate to="/channel-management/channel-setting/CTRIP" replace />
  },
  {
    path: '/channel-management/ctrip-setting/promotion/add',
    element: <ProtectedRoute><MainLayout><IntegrationUnavailable title="携程促销尚未接入" system="携程促销平台" /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/ctrip-setting/promotion/edit/:id',
    element: <ProtectedRoute><MainLayout><IntegrationUnavailable title="携程促销尚未接入" system="携程促销平台" /></MainLayout></ProtectedRoute>
  },
  {
    path: '/channel-management/ctrip-setting/promotion/registration/:id',
    element: <ProtectedRoute><MainLayout><IntegrationUnavailable title="携程促销报名尚未接入" system="携程促销平台" /></MainLayout></ProtectedRoute>
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
    path: '/reports/system-trace',
    element: <ProtectedRoute><MainLayout><SystemTraceConsole /></MainLayout></ProtectedRoute>
  },
  {
    path: '/reports/data-export',
    element: <ProtectedRoute><MainLayout><DataExport /></MainLayout></ProtectedRoute>
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
    path: '/system-settings/dictionary-management',
    element: <ProtectedRoute><MainLayout><DictionaryManagement /></MainLayout></ProtectedRoute>
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

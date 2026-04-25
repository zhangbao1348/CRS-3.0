import { 
  HomeOutlined, 
  BuildOutlined, 
  ApartmentOutlined, 
  TableOutlined, 
  TagOutlined, 
  GlobalOutlined, 
  LinkOutlined, 
  FileTextOutlined, 
  SettingOutlined, 
  DollarOutlined, 
  CalendarOutlined, 
  UserOutlined, 
  SafetyCertificateOutlined, 
  CloseCircleOutlined,
  BarChartOutlined,
  PieChartOutlined,
  GiftOutlined,
  MessageOutlined,
  FilterOutlined,
  ExportOutlined,
  ImportOutlined,
  FolderOutlined,
  InboxOutlined,
  SearchOutlined
} from '@ant-design/icons'

// CRS系统菜单
export const crsMenuData = [
  {
    key: 'dashboard',
    label: '首页',
    path: '/dashboard',
    icon: <HomeOutlined />
  },
  {
    key: 'reservation',
    label: '订单',
    path: '/reservation',
    icon: <FileTextOutlined />,
    children: [
      {
        key: 'reservation-list',
        label: '订单',
        path: '/reservation/reservation-list',
        icon: <FileTextOutlined />
      }
    ]
  },
  {
    key: 'inventory-management',
    label: '库存管理',
    path: '/inventory-management',
    icon: <InboxOutlined />,
    children: [
      {
        key: 'inventory',
        label: '房控日历',
        path: '/inventory',
        icon: <CalendarOutlined />
      },
      {
        key: 'room-status',
        label: '房态管理',
        path: '/inventory/room-status',
        icon: <HomeOutlined />
      },
      {
        key: 'booking-control',
        label: '预订控制',
        path: '/inventory/booking-control',
        icon: <FilterOutlined />
      }
    ]
  },
  {
    key: 'room-management',
    label: '房型管理',
    path: '/room-management',
    icon: <ApartmentOutlined />,
    children: [
      {
        key: 'room-type',
        label: '房型管理',
        path: '/room-management/room-type',
        icon: <HomeOutlined />
      }
    ]
  },
  {
    key: 'rate-management',
    label: '价格计划管理',
    path: '/rate-management',
    icon: <DollarOutlined />,
    children: [
      {
        key: 'rate-plan',
        label: '价格计划',
        path: '/rate-management/rate-plan',
        icon: <TagOutlined />
      },
      {
        key: 'rack-rate',
        label: '基础价格设置',
        path: '/rate-management/rack-rate',
        icon: <DollarOutlined />
      },
      {
        key: 'package-setting',
        label: '包价设置',
        path: '/rate-management/package-setting',
        icon: <GiftOutlined />
      },
      {
        key: 'price-query',
        label: '价格查询',
        path: '/rate-management/price-query',
        icon: <SearchOutlined />
      }
    ]
  },


  {
    key: 'channel-management',
    label: '渠道管理',
    path: '/channel-management',
    icon: <LinkOutlined />,
    children: [
      {
        key: 'channel-list',
        label: '渠道列表',
        path: '/channel-management/channel-list',
        icon: <LinkOutlined />
      },
      {
        key: 'channel-mapping',
        label: '渠道映射',
        path: '/channel-management/channel-mapping',
        icon: <FilterOutlined />
      }
    ]
  },
  {
    key: 'reports',
    label: '数据及报表',
    path: '/reports',
    icon: <BarChartOutlined />,
    children: [
      {
        key: 'reservation-reports',
        label: '订单报表',
        path: '/reports/reservation-reports',
        icon: <BarChartOutlined />
      },
      {
        key: 'occupancy-reports',
        label: '出租率报表',
        path: '/reports/occupancy-reports',
        icon: <PieChartOutlined />
      },
      {
        key: 'revenue-reports',
        label: ' revenue报表',
        path: '/reports/revenue-reports',
        icon: <DollarOutlined />
      },
      {
        key: 'data-export',
        label: '数据导出',
        path: '/reports/data-export',
        icon: <ExportOutlined />
      }
    ]
  },
  {
    key: 'group-management',
    label: '集团管理',
    path: '/group-management',
    icon: <BuildOutlined />,
    children: [
      {
        key: 'hotel-management',
        label: '酒店管理',
        path: '/group-management/hotel-management',
        icon: <ApartmentOutlined />
      },
      {
        key: 'group-room-type',
        label: '集团房型管理',
        path: '/group-management/group-room-type',
        icon: <HomeOutlined />
      },
      {
        key: 'group-rate-code',
        label: '集团房价码管理',
        path: '/group-management/group-rate-code',
        icon: <DollarOutlined />
      },
      {
        key: 'market-code',
        label: '市场码管理',
        path: '/group-management/market-code',
        icon: <GlobalOutlined />
      },
      {
        key: 'rate-category',
        label: '房价大类管理',
        path: '/group-management/rate-category',
        icon: <DollarOutlined />
      },
      {
        key: 'room-type-category',
        label: '房型大类管理',
        path: '/group-management/room-type-category',
        icon: <ApartmentOutlined />
      },
      {
        key: 'channel-code',
        label: '渠道码管理',
        path: '/group-management/channel-code',
        icon: <LinkOutlined />
      },
      {
        key: 'source-code',
        label: '来源码管理',
        path: '/group-management/source-code',
        icon: <FileTextOutlined />
      },
      {
        key: 'tax-setting',
        label: '税和服务费设置',
        path: '/group-management/tax-setting',
        icon: <SettingOutlined />
      },
      {
        key: 'group-package-setting',
        label: '包价设置',
        path: '/group-management/package-setting',
        icon: <TagOutlined />
      },
      {
        key: 'group-guarantee',
        label: '集团担保政策管理',
        path: '/group-management/group-guarantee',
        icon: <SafetyCertificateOutlined />
      },
      {
        key: 'group-cancellation',
        label: '集团取消政策管理',
        path: '/group-management/group-cancellation',
        icon: <CloseCircleOutlined />
      },
      {
        key: 'facility-management',
        label: '集团设施管理',
        path: '/group-management/facility-management',
        icon: <BuildOutlined />
      },
      {
        key: 'archive-management',
        label: '档案管理',
        path: '/group-management/archive-management',
        icon: <FolderOutlined />
      }
    ]
  },
  {
    key: 'group-promotion-management',
    label: '集团促销管理',
    path: '/group-promotion-management',
    icon: <GiftOutlined />,
    children: [
      {
        key: 'ota-promotion-management',
        label: 'OTA促销管理',
        path: '/group-promotion-management/ota-promotion-management',
        icon: <GlobalOutlined />
      },
      {
        key: 'ctrip-activity-management',
        label: '携程活动管理',
        path: '/group-promotion-management/ctrip-activity-management',
        icon: <CalendarOutlined />
      }
    ]
  },
  {
    key: 'system-settings',
    label: '系统设置',
    path: '/system-settings',
    icon: <SettingOutlined />,
    children: [
      {
        key: 'user-management',
        label: '用户管理',
        path: '/system-settings/user-management',
        icon: <UserOutlined />
      },
      {
        key: 'role-management',
        label: '角色管理',
        path: '/system-settings/role-management',
        icon: <SafetyCertificateOutlined />
      },
      {
        key: 'group-settings',
        label: '集团设置',
        path: '/system-settings/group-settings',
        icon: <BuildOutlined />
      },
      {
        key: 'custom-channel-setting',
        label: '自定义渠道设置',
        path: '/system-settings/custom-channel-setting',
        icon: <LinkOutlined />
      }
    ]
  },
  {
    key: 'super-admin-settings',
    label: '超管设置',
    path: '/super-admin-settings',
    icon: <SafetyCertificateOutlined />,
    children: [
      {
        key: 'tenant-management',
        label: '租户管理',
        path: '/super-admin-settings/tenant-management',
        icon: <BuildOutlined />
      },
      {
        key: 'platform-settings',
        label: '平台设置',
        path: '/super-admin-settings/platform-settings',
        icon: <SettingOutlined />
      },
      {
        key: 'system-monitoring',
        label: '系统监控',
        path: '/super-admin-settings/system-monitoring',
        icon: <BarChartOutlined />
      }
    ]
  }
]

// CRM系统菜单
export const crmMenuData = [
  {
    key: 'crm-dashboard',
    label: '首页',
    path: '/crm/dashboard',
    icon: <HomeOutlined />
  },
  {
    key: 'customer-management',
    label: '客户管理',
    path: '/crm/customer-management',
    icon: <UserOutlined />,
    children: [
      {
        key: 'customer-list',
        label: '客户列表',
        path: '/crm/customer-management/customer-list',
        icon: <UserOutlined />
      },
      {
        key: 'member-management',
        label: '会员管理',
        path: '/crm/customer-management/member-management',
        icon: <SafetyCertificateOutlined />
      }
    ]
  },
  {
    key: 'contact-management',
    label: '联系人管理',
    path: '/crm/contact-management',
    icon: <MessageOutlined />,
    children: [
      {
        key: 'contact-list',
        label: '联系人列表',
        path: '/crm/contact-management/contact-list',
        icon: <MessageOutlined />
      }
    ]
  },
  {
    key: 'membership-management',
    label: '会员体系',
    path: '/crm/membership-management',
    icon: <GiftOutlined />,
    children: [
      {
        key: 'member-level',
        label: '会员等级',
        path: '/crm/membership-management/member-level',
        icon: <TagOutlined />
      },
      {
        key: 'points-management',
        label: '积分管理',
        path: '/crm/membership-management/points-management',
        icon: <DollarOutlined />
      },
      {
        key: 'coupon-management',
        label: '优惠券管理',
        path: '/crm/membership-management/coupon-management',
        icon: <GiftOutlined />
      }
    ]
  },
  {
    key: 'marketing-management',
    label: '营销管理',
    path: '/crm/marketing-management',
    icon: <BarChartOutlined />,
    children: [
      {
        key: 'campaign-management',
        label: '营销活动',
        path: '/crm/marketing-management/campaign-management',
        icon: <CalendarOutlined />
      },
      {
        key: 'sms-management',
        label: '短信管理',
        path: '/crm/marketing-management/sms-management',
        icon: <MessageOutlined />
      }
    ]
  },
  {
    key: 'crm-reports',
    label: '数据分析',
    path: '/crm/reports',
    icon: <PieChartOutlined />,
    children: [
      {
        key: 'customer-analysis',
        label: '客户分析',
        path: '/crm/reports/customer-analysis',
        icon: <BarChartOutlined />
      },
      {
        key: 'member-analysis',
        label: '会员分析',
        path: '/crm/reports/member-analysis',
        icon: <PieChartOutlined />
      }
    ]
  },
  {
    key: 'crm-settings',
    label: '系统设置',
    path: '/crm/settings',
    icon: <SettingOutlined />,
    children: [
      {
        key: 'crm-user-management',
        label: '用户管理',
        path: '/crm/settings/user-management',
        icon: <UserOutlined />
      },
      {
        key: 'crm-role-management',
        label: '角色管理',
        path: '/crm/settings/role-management',
        icon: <SafetyCertificateOutlined />
      }
    ]
  }
]

// 小程序系统菜单
export const miniProgramMenuData = [
  {
    key: 'mini-dashboard',
    label: '首页',
    path: '/mini-program/dashboard',
    icon: <HomeOutlined />
  },
  {
    key: 'mini-reservation',
    label: '预订管理',
    path: '/mini-program/reservation',
    icon: <CalendarOutlined />,
    children: [
      {
        key: 'mini-booking',
        label: '在线预订',
        path: '/mini-program/reservation/booking',
        icon: <CalendarOutlined />
      },
      {
        key: 'mini-my-reservations',
        label: '我的预订',
        path: '/mini-program/reservation/my-reservations',
        icon: <FileTextOutlined />
      }
    ]
  },
  {
    key: 'mini-room-info',
    label: '房间信息',
    path: '/mini-program/room-info',
    icon: <ApartmentOutlined />,
    children: [
      {
        key: 'mini-room-list',
        label: '房间列表',
        path: '/mini-program/room-info/room-list',
        icon: <ApartmentOutlined />
      },
      {
        key: 'mini-room-detail',
        label: '房间详情',
        path: '/mini-program/room-info/room-detail',
        icon: <HomeOutlined />
      }
    ]
  },
  {
    key: 'mini-personal-center',
    label: '个人中心',
    path: '/mini-program/personal-center',
    icon: <UserOutlined />,
    children: [
      {
        key: 'mini-profile',
        label: '个人资料',
        path: '/mini-program/personal-center/profile',
        icon: <UserOutlined />
      },
      {
        key: 'mini-coupon',
        label: '优惠券',
        path: '/mini-program/personal-center/coupon',
        icon: <GiftOutlined />
      },
      {
        key: 'mini-points',
        label: '我的积分',
        path: '/mini-program/personal-center/points',
        icon: <DollarOutlined />
      }
    ]
  },
  {
    key: 'mini-hotel-info',
    label: '酒店信息',
    path: '/mini-program/hotel-info',
    icon: <BuildOutlined />,
    children: [
      {
        key: 'mini-hotel-detail',
        label: '酒店详情',
        path: '/mini-program/hotel-info/detail',
        icon: <BuildOutlined />
      },
      {
        key: 'mini-facility',
        label: '设施服务',
        path: '/mini-program/hotel-info/facility',
        icon: <SettingOutlined />
      },
      {
        key: 'mini-traffic',
        label: '交通指南',
        path: '/mini-program/hotel-info/traffic',
        icon: <GlobalOutlined />
      }
    ]
  }
]

// 导出默认菜单（CRS）
export const menuData = crsMenuData
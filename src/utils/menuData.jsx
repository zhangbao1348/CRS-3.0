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
  ImportOutlined
} from '@ant-design/icons'

export const menuData = [
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
      },
      {
        key: 'reservation-history',
        label: '历史订单',
        path: '/reservation/reservation-history',
        icon: <FileTextOutlined />
      }
    ]
  },
  {
    key: 'inventory',
    label: '房控日历',
    path: '/inventory',
    icon: <CalendarOutlined />
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
        key: 'room-type-diff',
        label: '房型差价设置',
        path: '/rate-management/room-type-diff',
        icon: <ApartmentOutlined />
      },
      {
        key: 'person-diff',
        label: '人数差价设置',
        path: '/rate-management/person-diff',
        icon: <UserOutlined />
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
      }
    ]
  },

  {
    key: 'rfp',
    label: 'RFP',
    path: '/rfp',
    icon: <MessageOutlined />
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
        label: '入住率报表',
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
      },
      {
        key: 'data-import',
        label: '数据导入',
        path: '/reports/data-import',
        icon: <ImportOutlined />
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
        label: '税率设置',
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
      }
    ]
  }
]
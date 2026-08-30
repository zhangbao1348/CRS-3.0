import React, { Suspense, useContext, useEffect } from 'react'
import { Layout, Menu, Select, message, Dropdown, Avatar, Spin, Button } from 'antd'
import { 
  MenuFoldOutlined, 
  MenuUnfoldOutlined,
  HomeOutlined, 
  BuildOutlined, 
  ApartmentOutlined, 
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
  FilterOutlined,
  ExportOutlined,
  FolderOutlined,
  InboxOutlined,
  SearchOutlined,
  MenuOutlined,
  ExperimentOutlined,
  LogoutOutlined
} from '@ant-design/icons'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useHotelContext } from '../../contexts/HotelContext.jsx'
import { AuthContext } from '../../contexts/AuthContext.jsx'
import { useTenantContext } from '../../contexts/TenantContext.jsx'
import { crsMenuData, crmMenuData, miniProgramMenuData } from '../../utils/menuData.jsx'

const { Option } = Select

const { Header, Sider, Content } = Layout

const MENU_PATH_ALIAS_RULES = [
  { path: '/group-management/add-hotel', target: '/group-management/hotel-management' },
  { path: '/hotel-management/edit-hotel', target: '/group-management/hotel-management' },
  { path: '/group-management/add-group-room-type', target: '/group-management/group-room-type' },
  { path: '/group-management/add-rate-code', target: '/group-management/group-rate-code' },
  { path: '/group-management/add-edit-tax', target: '/group-management/tax-setting' },
  { path: '/group-management/add-package', target: '/group-management/package-setting' },
  { path: '/group-management/edit-package', target: '/group-management/package-setting' },
  { path: '/group-management/add-edit-guarantee', target: '/group-management/group-guarantee' },
  { path: '/group-management/add-edit-cancellation', target: '/group-management/group-cancellation' },
  { path: '/group-management/archive-management/add', target: '/group-management/archive-management' },
  { pattern: /^\/group-management\/archive-management\/edit\/[^/]+$/, target: '/group-management/archive-management' },
  { path: '/rate-management/add-package', target: '/rate-management/package-setting' },
  { path: '/rate-management/edit-package', target: '/rate-management/package-setting' },
  { path: '/rate-management/add-rate-plan', target: '/rate-management/rate-plan' },
  { pattern: /^\/rate-management\/edit-rate-plan\/[^/]+$/, target: '/rate-management/rate-plan' },
  { path: '/channel-management/ctrip-setting', target: '/channel-management/channel-list' },
  { path: '/channel-management/fliggy-setting', target: '/channel-management/channel-list' },
  { pattern: /^\/channel-management\/channel-setting\/[^/]+$/, target: '/channel-management/channel-list' },
  { path: '/channel-management/ctrip-setting/promotion/add', target: '/channel-management/channel-list' },
  { pattern: /^\/channel-management\/ctrip-setting\/promotion\/edit\/[^/]+$/, target: '/channel-management/channel-list' },
  { pattern: /^\/channel-management\/ctrip-setting\/promotion\/registration\/[^/]+$/, target: '/channel-management/channel-list' },
  { path: '/reservation/reservation-detail', target: '/reservation/reservation-list' }
]

const MainLayout = ({ children }) => {
  const [collapsed, setCollapsed] = React.useState(false)
  const [compactViewport, setCompactViewport] = React.useState(false)
  const [systemType] = React.useState('crs')
  const location = useLocation()
  const navigate = useNavigate()
  const { hotels, selectedHotel, loading: hotelLoading, changeHotel } = useHotelContext()
  const { tenants, selectedTenant, selectedTenantLabel, loading: tenantLoading, changeTenant } = useTenantContext()
  const { menus, user, logout } = useContext(AuthContext)

  const baseTenantOptions = tenants.map((tenant) => ({
    value: tenant.id,
    label: `${tenant.tenantName} (${tenant.tenantCode})`,
    title: `${tenant.tenantName} (${tenant.tenantCode})`
  }))

  const hasSelectedTenantOption = baseTenantOptions.some((tenant) => tenant.value === selectedTenant)
  const tenantOptions = !hasSelectedTenantOption && selectedTenant && selectedTenantLabel
    ? [
        {
          value: selectedTenant,
          label: selectedTenantLabel,
          title: selectedTenantLabel
        },
        ...baseTenantOptions
      ]
    : baseTenantOptions

  const toggleCollapsed = () => {
    setCollapsed(!collapsed)
  }

  useEffect(() => {
    const mediaQuery = window.matchMedia('(max-width: 768px)')
    const syncViewport = (event) => {
      const compact = event.matches
      setCompactViewport(compact)
      setCollapsed(compact)
    }
    syncViewport(mediaQuery)
    mediaQuery.addEventListener('change', syncViewport)
    return () => mediaQuery.removeEventListener('change', syncViewport)
  }, [])

  const handleLogout = async () => {
    try {
      await logout()
      message.success('退出登录成功')
      navigate('/login')
    } catch (error) {
      message.error('退出登录失败')
    }
  }

  const userMenuItems = [
    {
      key: 'logout',
      label: (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <LogoutOutlined style={{ marginRight: 8 }} />
          退出登录
        </div>
      ),
      onClick: handleLogout
    }
  ]

  // 动态导入图标组件
  const getIconComponent = (iconName) => {
    const icons = {
      'HomeOutlined': <HomeOutlined />,
      'FileTextOutlined': <FileTextOutlined />,
      'InboxOutlined': <InboxOutlined />,
      'CalendarOutlined': <CalendarOutlined />,
      'ApartmentOutlined': <ApartmentOutlined />,
      'DollarOutlined': <DollarOutlined />,
      'TagOutlined': <TagOutlined />,
      'UserOutlined': <UserOutlined />,
      'LinkOutlined': <LinkOutlined />,
      'BarChartOutlined': <BarChartOutlined />,
      'BuildOutlined': <BuildOutlined />,
      'SettingOutlined': <SettingOutlined />,
      'SafetyCertificateOutlined': <SafetyCertificateOutlined />,
      'FilterOutlined': <FilterOutlined />,
      'SearchOutlined': <SearchOutlined />,
      'GiftOutlined': <GiftOutlined />,
      'PieChartOutlined': <PieChartOutlined />,
      'ExportOutlined': <ExportOutlined />,
      'GlobalOutlined': <GlobalOutlined />,
      'FolderOutlined': <FolderOutlined />,
      'MenuOutlined': <MenuOutlined />,
      'CloseCircleOutlined': <CloseCircleOutlined />,
      'ExperimentOutlined': <ExperimentOutlined />
    }
    return icons[iconName] || <HomeOutlined />
  }

  // 将后端菜单数据转换为前端格式
  const convertMenuData = (backendMenus) => {
    // 过滤当前系统的菜单
    let systemMenus = backendMenus.filter(m => m.systemType === systemType)
    
    // 构建菜单树结构
    const buildMenuTree = () => {
      // 按sortOrder排序
      systemMenus.sort((a, b) => a.sortOrder - b.sortOrder)
      
      // 找到所有一级菜单（parent_code为crs-system的菜单）
      const rootMenus = systemMenus.filter(m => m.parentCode === 'crs-system')
      
      // 递归构建菜单树
      const buildMenu = (menu) => {
        // 找到当前菜单的子菜单
        const children = []
        
        // 通过parent_code查找子菜单
        const parentCodeChildren = systemMenus.filter(child => child.parentCode === menu.menuCode)
        if (parentCodeChildren.length > 0) {
          children.push(...parentCodeChildren)
        }
        
        // 为子菜单递归构建子菜单
        const item = {
          key: menu.menuCode,
          path: menu.path,
          label: menu.menuName,
          icon: getIconComponent(menu.icon)
        }
        
        if (children.length > 0) {
          // 为子菜单递归构建子菜单
          item.children = children.map(child => buildMenu(child))
        }
        
        return item
      }
      
      // 构建菜单树
      const tree = rootMenus.map(menu => buildMenu(menu))
      
      return tree
    }
    
    // 构建菜单树
    const tree = buildMenuTree()
    
    return tree
  }

  // 根据系统类型获取菜单数据
  const getMenuData = () => {
    // 优先使用权限菜单
    if (menus && menus.length > 0) {
      // 检查是否是静态菜单数据格式
      if (menus[0].key) {
        // 静态菜单数据格式，直接返回
        return menus
      } else {
        // 后端菜单数据格式，需要转换
        const convertedMenus = convertMenuData(menus)
        if (convertedMenus.length > 0) {
          return convertedMenus
        }
      }
    }
    // 如果没有动态菜单，使用静态菜单数据
    if (systemType === 'crm') {
      return crmMenuData
    } else if (systemType === 'mini') {
      return miniProgramMenuData
    }
    return crsMenuData
  }

  const resolveMenuPath = (path) => {
    const matchedRule = MENU_PATH_ALIAS_RULES.find((rule) => {
      if (rule.path) {
        return rule.path === path
      }

      return rule.pattern?.test(path)
    })

    return matchedRule?.target || path
  }

  // 递归构建菜单items
  const generateMenuItems = (menuItems) => {
    return menuItems.map((item, index) => {
      // 为了解决重复key的问题，使用key + index作为唯一标识
      const uniqueKey = `${item.key}-${index}`;
      
      if (item.children) {
        return {
          key: uniqueKey,
          label: (
            <span>
              {item.icon}
              <span>{item.label}</span>
            </span>
          ),
          children: generateMenuItems(item.children)
        }
      }
      return {
        key: uniqueKey,
        label: (
          <Link to={item.path}>
            {item.icon}
            <span>{item.label}</span>
          </Link>
        )
      }
    })
  }

  const getMenuState = (menuData, currentPath) => {
    const targetPath = resolveMenuPath(currentPath)

    const findMenuState = (items, parentKeys = []) => {
      for (let i = 0; i < items.length; i++) {
        const item = items[i]
        const itemKey = `${item.key}-${i}`

        if (item.path === targetPath) {
          return {
            selectedKeys: [itemKey],
            openKeys: parentKeys
          }
        }

        if (item.children) {
          const childState = findMenuState(item.children, [...parentKeys, itemKey])
          if (childState) {
            return childState
          }
        }
      }

      return null
    }

    return findMenuState(menuData) || {
      selectedKeys: [],
      openKeys: []
    }
  }
  
  // 获取路径对应的友好名称
  const getPathFriendlyName = (path) => {
    // 路径到中文名称的映射
    const pathNameMap = {
      '/inventory': '库存管理 > 房控日历',
      '/group-management/add-hotel': '集团管理 > 酒店管理 > 添加酒店',
      '/hotel-management/edit-hotel': '集团管理 > 酒店管理 > 编辑酒店',
      '/group-management/add-group-room-type': '集团管理 > 集团房型管理 > 添加集团房型',
      '/group-management/add-rate-code': '集团管理 > 集团房价码管理 > 添加集团房价码',
      '/group-management/add-edit-tax': '集团管理 > 税和服务费设置 > 添加/编辑税',
      '/group-management/add-package': '集团管理 > 包价设置 > 添加包价',
      '/group-management/edit-package': '集团管理 > 包价设置 > 编辑包价',
      '/rate-management/add-package': '价格计划管理 > 包价设置 > 新增包价',
      '/rate-management/edit-package': '价格计划管理 > 包价设置 > 编辑包价',
      '/rate-management/add-rate-plan': '价格计划管理 > 价格计划 > 新增价格计划',
      '/rate-management/edit-rate-plan/:id': '价格计划管理 > 价格计划 > 编辑价格计划',
      '/group-management/add-edit-guarantee': '集团管理 > 集团担保政策管理 > 添加/编辑担保政策',
      '/group-management/add-edit-cancellation': '集团管理 > 集团取消政策管理 > 添加/编辑取消政策',
      '/group-management/archive-management/add': '集团管理 > 档案管理 > 添加档案',
      '/group-management/archive-management/edit/:id': '集团管理 > 档案管理 > 编辑档案'
    }

    // 先检查是否有直接匹配的路径
    if (pathNameMap[path]) {
      return pathNameMap[path]
    }

    // 处理带参数的路径
    for (const [pattern, name] of Object.entries(pathNameMap)) {
      if (pattern.includes(':id')) {
        const regex = new RegExp('^' + pattern.replace(/:id/g, '[^/]+') + '$')
        if (regex.test(path)) {
          return name
        }
      }
    }

    const parts = path.split('/').filter(Boolean)
    const names = []

    const findName = (items, pathParts, index) => {
      if (index >= pathParts.length) return

      for (const item of items) {
        const itemParts = item.path.split('/').filter(Boolean)
        if (itemParts.length === index + 1 && itemParts[index] === pathParts[index]) {
          names.push(item.label)
          if (item.children) {
            findName(item.children, pathParts, index + 1)
          }
          break
        }
        // 如果当前item有children，检查children中是否有匹配完整path的
        if (item.children) {
          for (const child of item.children) {
            if (child.path === path) {
              names.push(item.label)
              names.push(child.label)
              return
            }
          }
        }
      }
    }

    findName(getMenuData(), parts, 0)
    if (names.length > 0) {
      return names.join(' > ')
    }

    // 如果菜单中没有找到，使用路径部分的映射
    const partNameMap = {
      'group-management': '集团管理',
      'hotel-management': '酒店管理',
      'edit-hotel': '编辑酒店',
      'add-hotel': '添加酒店',
      'group-room-type': '集团房型管理',
      'add-group-room-type': '添加集团房型',
      'group-rate-code': '集团房价码管理',
      'add-rate-code': '添加集团房价码',
      'market-code': '市场码管理',
      'channel-code': '渠道码管理',
      'source-code': '来源码管理',
      'tax-setting': '税和服务费设置',
      'package-setting': '包价设置',
      'group-guarantee': '集团担保政策管理',
      'group-cancellation': '集团取消政策管理',
      'facility-management': '集团设施管理',
      'archive-management': '档案管理',
      'rate-category': '房价大类管理',
      'room-type-category': '房型大类管理',
      'room-management': '房型管理',
      'room-type': '房型管理',
      'rack-rate-calendar': '基础价格日历',
      'rate-code': '房价码',
      'inventory': '库存管理',
      'room-status': '房态管理',
      'booking-control': '预订控制',
      'reservation': '订单管理',
      'reservation-list': '订单列表',
      'reservation-detail': '订单详情',
      'rate-management': '价格计划管理',
      'rate-plan': '价格计划',
      'rack-rate': '基础价格设置',
      'price-query': '价格查询',
      'channel-management': '渠道管理',
      'channel-list': '渠道列表',
      'channel-mapping': '渠道映射',
      'reports': '数据及报表',
      'reservation-reports': '订单报表',
      'occupancy-reports': '出租率报表',
      'revenue-reports': '收入报表',
      'data-export': '数据导出',
      'system-settings': '系统设置',
      'user-management': '用户管理',
      'role-management': '角色管理',
      'group-settings': '集团设置',
      'group-promotion-management': '集团促销管理',
      'ota-promotion-management': 'OTA促销管理',
      'ctrip-activity-management': '携程活动管理',
      'ctrip-activity-registration': '携程活动报名'
    }

    const mappedParts = parts.map(part => partNameMap[part] || part)
    return mappedParts.join(' > ')
  }

  const menuData = getMenuData()
  const menuItems = generateMenuItems(menuData)
  const menuState = getMenuState(menuData, location.pathname)
  const [openMenuKeys, setOpenMenuKeys] = React.useState(menuState.openKeys)
  const openMenuKeysSignature = menuState.openKeys.join('\u0000')

  useEffect(() => {
    setOpenMenuKeys(openMenuKeysSignature ? openMenuKeysSignature.split('\u0000') : [])
  }, [openMenuKeysSignature])

  return (
    <Layout className={`crs-shell${collapsed ? ' is-collapsed' : ''}${compactViewport ? ' is-compact' : ''}`}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        collapsedWidth={compactViewport ? 0 : 80}
        width={200}
        theme="light"
        className="crs-shell__sider"
      >
        <div className="logo crs-shell__logo">
          <div className="crs-shell__logo-title">
            {collapsed ? (systemType === 'crm' ? 'CRM' : systemType === 'mini' ? '小程序' : 'CRS') : (systemType === 'crm' ? 'CRM系统' : systemType === 'mini' ? '小程序系统' : 'CRS系统')}
          </div>
          {/* 暂时隐藏系统切换控件 */}
          {/* 
          {!collapsed && (
            <Select
              value={systemType}
              onChange={handleSystemChange}
              style={{ width: 140 }}
              size="small"
              options={[
                { value: 'crs', label: 'CRS系统' },
                { value: 'crm', label: 'CRM系统' },
                { value: 'mini', label: '小程序系统' }
              ]}
            />
          )}
          */}
        </div>
        <Menu
          mode="inline"
          selectedKeys={menuState.selectedKeys}
          openKeys={openMenuKeys}
          onOpenChange={setOpenMenuKeys}
          className="crs-shell__menu"
          items={menuItems}
          subMenuOpenDelay={0.2}
          subMenuCloseDelay={0.1}
          theme="dark"
        />
      </Sider>
      <Layout className="crs-shell__main">
        <Header className="crs-shell__header">
          <div className="crs-shell__header-start">
            <Button
              type="text"
              className="trigger crs-shell__trigger"
              aria-label={collapsed ? '展开主导航' : '收起主导航'}
              onClick={toggleCollapsed}
            >
              {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            </Button>
            <div className="crs-shell__breadcrumb" title={getPathFriendlyName(location.pathname)}>
              {getPathFriendlyName(location.pathname)}
            </div>
          </div>
          <div className="crs-shell__header-end">
            {/* 没有归属租户的用户显示租户切换控件 */}
            {systemType !== 'crm' && user?.tenantId === null && (
              <Select
                value={selectedTenant}
                onChange={changeTenant}
                className="crs-shell__context-select crs-shell__context-select--tenant"
                size="middle"
                placeholder="切换租户"
                showSearch
                allowClear
                loading={tenantLoading}
                optionFilterProp="label"
                options={tenantOptions}
                filterOption={(input, option) =>
                  (option?.label || '').toLowerCase().includes(input.toLowerCase()) ||
                  (option?.title || '').toLowerCase().includes(input.toLowerCase())
                }
              />
            )}
            {/* 酒店切换控件 */}
            {systemType !== 'crm' && (
              <Select
                value={selectedHotel}
                onChange={changeHotel}
                className="crs-shell__context-select crs-shell__context-select--hotel"
                size="middle"
                placeholder="切换酒店"
                showSearch
                allowClear
                loading={hotelLoading}
                filterOption={(input, option) =>
                  (option?.children || '').toLowerCase().includes(input.toLowerCase()) ||
                  (option?.props?.title || '').toLowerCase().includes(input.toLowerCase())
                }
              >
                {hotels.map(hotel => (
                  <Option 
                    key={hotel.hotelCode} 
                    value={hotel.hotelCode} 
                    title={`${hotel.chineseName} (${hotel.hotelCode})`}
                  >
                    {hotel.chineseName} ({hotel.hotelCode})
                  </Option>
                ))}
              </Select>
            )}
            <div className="crs-shell__user">
              <span className="crs-shell__user-label">欢迎，</span>
              <span className="crs-shell__user-name">{user?.name || user?.username || '用户'}</span>
            </div>
            <Dropdown 
              menu={{ items: userMenuItems }}
              placement="bottomRight"
              arrow
            >
              <div className="crs-shell__account" role="button" tabIndex={0} aria-label="打开个人账户菜单">
                <Avatar size="small" icon={<UserOutlined />} className="crs-shell__avatar" />
                <span>我的</span>
              </div>
            </Dropdown>
          </div>
        </Header>
        <Content className="crs-shell__content">
          <div className="page-container">
            <Suspense fallback={<div className="crs-shell__loading"><Spin size="large" /><span>正在加载业务模块</span></div>}>
              {children}
            </Suspense>
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}

export default MainLayout

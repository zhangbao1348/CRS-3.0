import React, { useContext, useEffect } from 'react'
import { Layout, Menu, Select, message, Dropdown, Avatar } from 'antd'
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
import { setInitializingState } from '../../utils/api.js'

const { Option } = Select

const { Header, Sider, Content } = Layout

const MainLayout = ({ children }) => {
  const [collapsed, setCollapsed] = React.useState(false)
  const [systemType, setSystemType] = React.useState('crs')
  const location = useLocation()
  const navigate = useNavigate()
  const { hotels, selectedHotel, loading: hotelLoading, changeHotel } = useHotelContext()
  const { tenants, selectedTenant, loading: tenantLoading, changeTenant } = useTenantContext()
  const { menus, user, logout } = useContext(AuthContext)

  const toggleCollapsed = () => {
    setCollapsed(!collapsed)
  }

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

  const handleSystemChange = (value) => {
    setSystemType(value)
    if (value === 'crm') {
      message.info('切换到CRM系统')
      navigate('/crm/dashboard')
    } else if (value === 'mini') {
      message.info('切换到小程序系统')
      navigate('/mini-program/dashboard')
    } else {
      message.info('切换到CRS系统')
      navigate('/dashboard')
    }
  }

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
    console.log('Backend menus:', backendMenus)
    // 过滤当前系统的菜单
    let systemMenus = backendMenus.filter(m => m.systemType === systemType)
    console.log('System menus:', systemMenus)
    
    // 检查库存管理菜单
    const inventoryMenus = systemMenus.filter(m => m.menuName.includes('库存') || m.path.includes('inventory'))
    console.log('Inventory menus:', inventoryMenus)
    
    // 构建菜单树结构
    const buildMenuTree = () => {
      // 按sortOrder排序
      systemMenus.sort((a, b) => a.sortOrder - b.sortOrder)
      
      // 找到所有一级菜单（parent_code为crs-system的菜单）
      const rootMenus = systemMenus.filter(m => m.parentCode === 'crs-system')
      console.log('Root menus:', rootMenus)
      
      // 检查一级菜单中的库存管理菜单
      const rootInventoryMenus = rootMenus.filter(m => m.menuName.includes('库存') || m.path.includes('inventory'))
      console.log('Root inventory menus:', rootInventoryMenus)
      
      // 递归构建菜单树
      const buildMenu = (menu) => {
        // 找到当前菜单的子菜单
        const children = []
        
        // 通过parent_code查找子菜单
        const parentCodeChildren = systemMenus.filter(child => child.parentCode === menu.menuCode)
        console.log(`Menu ${menu.menuName} (code: ${menu.menuCode}) parentCodeChildren:`, parentCodeChildren)
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
      
      console.log('Built menu tree:', tree)
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

  // 获取当前选中的菜单key
  const getSelectedKeys = () => {
    const path = location.pathname
    let selectedKey = ''
    
    const findKey = (items) => {
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.path === path) {
          selectedKey = `${item.key}-${i}`
          return true
        }
        if (item.children && findKey(item.children)) {
          return true
        }
      }
      return false
    }
    
    findKey(getMenuData())
    return selectedKey ? [selectedKey] : []
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

  const menuItems = generateMenuItems(getMenuData())

  return (
    <Layout>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={200}
        theme="light"
        style={{
          background: '#003366',
          boxShadow: 'none',
          borderRight: 'none'
        }}
      >
        <div className="logo" style={{
          height: 64,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          background: '#003366',
          margin: 0,
          padding: 0
        }}>
          <div style={{
            fontSize: 18,
            fontWeight: 600,
            color: '#fff',
            marginBottom: collapsed ? 0 : 8
          }}>
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
          selectedKeys={getSelectedKeys()}
          style={{ 
            height: '100%', 
            borderRight: 0
          }}
          items={menuItems}
          subMenuOpenDelay={0.2}
          subMenuCloseDelay={0.1}
          theme="dark"
        />
      </Sider>
      <Layout>
        <Header
          style={{
            padding: 0,
            background: '#003366',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <div
              className="trigger"
              style={{
                padding: '0 24px',
                fontSize: 18,
                cursor: 'pointer',
                transition: 'color 0.3s',
                color: '#fff'
              }}
              onClick={toggleCollapsed}
            >
              {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            </div>
            <div style={{
              fontSize: 16,
              fontWeight: 500,
              color: '#fff'
            }}>
              {getPathFriendlyName(location.pathname)}
            </div>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', padding: '0 24px' }}>
            {/* 没有归属租户的用户显示租户切换控件 */}
            {systemType !== 'crm' && user?.tenantId === null && (
              <Select
                value={selectedTenant}
                onChange={changeTenant}
                style={{ width: 200, marginRight: 16 }}
                size="middle"
                placeholder="切换租户"
                showSearch
                allowClear
                loading={tenantLoading}
                filterOption={(input, option) =>
                  (option?.children || '').toLowerCase().includes(input.toLowerCase()) ||
                  (option?.props?.title || '').toLowerCase().includes(input.toLowerCase())
                }
              >
                {tenants.map(tenant => (
                  <Option 
                    key={tenant.id} 
                    value={tenant.id} 
                    title={`${tenant.tenantName} (${tenant.tenantCode})`}
                  >
                    {tenant.tenantName} ({tenant.tenantCode})
                  </Option>
                ))}
              </Select>
            )}
            {/* 酒店切换控件 */}
            {systemType !== 'crm' && (
              <Select
                value={selectedHotel}
                onChange={changeHotel}
                style={{ width: 280, marginRight: 16 }}
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
            <div style={{ display: 'flex', alignItems: 'center', color: '#fff', marginRight: 16 }}>
              <span style={{ marginRight: 8 }}>欢迎，</span>
              <span style={{ fontWeight: 500 }}>{user?.name || user?.username || '用户'}</span>
            </div>
            <Dropdown 
              menu={{ items: userMenuItems }}
              placement="bottomRight"
              arrow
            >
              <div style={{ 
                display: 'flex', 
                alignItems: 'center', 
                cursor: 'pointer',
                color: '#fff',
                padding: '4px 8px',
                borderRadius: 4,
                transition: 'background-color 0.3s'
              }} 
              onMouseEnter={(e) => e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.1)'}
              onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
              >
                <Avatar size="small" icon={<UserOutlined />} style={{ marginRight: 8, backgroundColor: '#1890ff' }} />
                <span>我的</span>
              </div>
            </Dropdown>
          </div>
        </Header>
        <Content>
          <div className="page-container" style={{ overflow: 'auto' }}>
            {children}
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}

export default MainLayout
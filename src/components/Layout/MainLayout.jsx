import React, { useState } from 'react'
import { Layout, Menu, Select } from 'antd'
import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons'
import { Link, useLocation } from 'react-router-dom'
import { menuData } from '../../utils/menuData.jsx'

const { Option } = Select

const { Header, Sider, Content } = Layout

const MainLayout = ({ children }) => {
  const [collapsed, setCollapsed] = useState(false)
  const location = useLocation()

  const toggleCollapsed = () => {
    setCollapsed(!collapsed)
  }

  // 递归构建菜单items
  const generateMenuItems = (menuItems) => {
    return menuItems.map(item => {
      if (item.children) {
        return {
          key: item.key,
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
        key: item.key,
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
      for (const item of items) {
        if (item.path === path) {
          selectedKey = item.key
          return true
        }
        if (item.children && findKey(item.children)) {
          return true
        }
      }
      return false
    }
    
    findKey(menuData)
    return selectedKey ? [selectedKey] : []
  }
  
  // 获取路径对应的友好名称
  const getPathFriendlyName = (path) => {
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
      }
    }
    
    findName(menuData, parts, 0)
    return names.length > 0 ? names.join(' > ') : parts.join(' > ')
  }

  const menuItems = generateMenuItems(menuData)

  return (
    <Layout>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={200}
        theme="light"
      >
        <div className="logo" style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          fontSize: 18,
          fontWeight: 600,
          color: '#1890ff',
          background: '#fff',
          borderBottom: '1px solid #f0f0f0'
        }}>
          {collapsed ? 'CRS' : 'CRS系统'}
        </div>
        <Menu
          mode="inline"
          selectedKeys={getSelectedKeys()}
          style={{ height: '100%', borderRight: 0 }}
          items={menuItems}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            padding: 0,
            background: '#fff',
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
                transition: 'color 0.3s'
              }}
              onClick={toggleCollapsed}
            >
              {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            </div>
            <div style={{
              fontSize: 16,
              fontWeight: 500,
              color: '#262626'
            }}>
              {getPathFriendlyName(location.pathname)}
            </div>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', padding: '0 24px' }}>
          <Select
            defaultValue="1"
            style={{ width: 280, marginRight: 16 }}
            size="middle"
            placeholder="切换酒店"
            showSearch
            allowClear
            filterOption={(input, option) =>
              (option?.children || '').toLowerCase().includes(input.toLowerCase()) ||
              (option?.props?.title || '').toLowerCase().includes(input.toLowerCase())
            }
          >
            <Option value="1" title="北京王府井大酒店 (BJ-WFJC)">北京王府井大酒店 (BJ-WFJC)</Option>
            <Option value="2" title="上海外滩华尔道夫酒店 (SH-BT-HWD)">上海外滩华尔道夫酒店 (SH-BT-HWD)</Option>
            <Option value="3" title="广州天河希尔顿酒店 (GZ-TH-XED)">广州天河希尔顿酒店 (GZ-TH-XED)</Option>
            <Option value="4" title="深圳福田香格里拉酒店 (SZ-FT-XGL)">深圳福田香格里拉酒店 (SZ-FT-XGL)</Option>
          </Select>
          <div>欢迎使用CRS系统</div>
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
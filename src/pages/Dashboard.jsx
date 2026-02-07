import React, { useState } from 'react'
import { Card, Row, Col, Statistic, Table, Select, Button, Space, Badge, List, Tag, Progress, Tabs, DatePicker, Typography } from 'antd'
import { 
  CheckCircleOutlined, 
  CloseCircleOutlined, 
  UserOutlined, 
  DollarOutlined, 
  LineChartOutlined, 
  BarChartOutlined, 
  BellOutlined,
  InfoCircleOutlined,
  WarningOutlined,
  PlusOutlined,
  MinusOutlined,
  SyncOutlined
} from '@ant-design/icons'
import { Line, Bar } from '@ant-design/plots'

const { Option } = Select
const { RangePicker } = DatePicker
const { Title, Text } = Typography

const Dashboard = () => {
  // 统计数据
  const [stats, setStats] = useState({
    reservations: 1,
    cancellations: 0,
    checkins: 10,
    freeRooms: 0
  })

  // 房态数据
  const [roomStatus, setRoomStatus] = useState({
    available: 5,
    occupied: 3,
    maintenance: 1,
    cleaning: 1
  })

  // 可用库存数据
  const inventoryData = [
    { date: '31', value: 5 },
    { date: '01', value: 0 },
    { date: '02', value: 7 },
    { date: '03', value: 9 },
    { date: '04', value: 10 },
    { date: '05', value: 10 },
    { date: '06', value: 10 },
    { date: '07', value: 10 },
    { date: '08', value: 10 },
    { date: '09', value: 10 }
  ]

  // 数据概览
  const overviewData = [
    { label: '预订均价', value: 58, unit: 'CNY' },
    { label: '平均LOS', value: 1.1, unit: '' },
    { label: '平均提前预订时长', value: 1.3, unit: '天' },
    { label: '入住订单数', value: 39, unit: '单' },
    { label: 'ADR', value: 561.85, unit: 'CNY' }
  ]

  // 预订趋势数据 - 扩展为30天数据
  const generateTrendData = () => {
    const data = []
    const today = new Date()
    
    for (let i = 29; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(today.getDate() - i)
      const dateStr = date.toISOString().split('T')[0]
      const value = Math.floor(Math.random() * 20) + 5
      data.push({ date: dateStr, value })
    }
    return data
  }

  const bookingTrendData = generateTrendData()

  // 预订渠道数据 - 扩展为更多渠道
  const channelData = [
    { name: '携程', value: 40 },
    { name: '美团', value: 25 },
    { name: '飞猪', value: 15 },
    { name: 'Booking.com', value: 10 },
    { name: 'Agoda', value: 5 },
    { name: '其他', value: 5 }
  ]

  // 入住率趋势数据
  const occupancyTrendData = [
    { date: '2025-12-24', occupancy: 65, adr: 450 },
    { date: '2025-12-25', occupancy: 72, adr: 480 },
    { date: '2025-12-26', occupancy: 85, adr: 520 },
    { date: '2025-12-27', occupancy: 78, adr: 490 },
    { date: '2025-12-28', occupancy: 60, adr: 430 },
    { date: '2025-12-29', occupancy: 80, adr: 500 },
    { date: '2025-12-30', occupancy: 90, adr: 550 }
  ]

  // 渠道状态数据
  const channelStatus = [
    { name: '携程', status: '在线', color: 'green' },
    { name: '美团', status: '在线', color: 'green' },
    { name: '飞猪', status: '在线', color: 'green' },
    { name: 'Booking', status: '在线', color: 'green' },
    { name: 'Agoda', status: '在线', color: 'green' }
  ]

  // 平台通知数据
  const notifications = [
    { id: 1, title: '系统更新通知', content: '系统将于2025-12-31进行维护更新', time: '2025-12-31', type: 'info' },
    { id: 2, title: '价格调整提醒', content: '春节期间价格已调整，请查看', time: '2025-12-20', type: 'warning' },
    { id: 3, title: '新订单提醒', content: '您有一个新的订单，请及时处理', time: '2025-12-15', type: 'success' },
    { id: 4, title: '库存预警', content: '12月30日库存不足，请及时补充', time: '2025-12-10', type: 'warning' }
  ]

  // 统计卡片样式
  const statCardStyle = {
    borderRadius: 8,
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)',
    marginBottom: 16,
    border: '1px solid #f0f0f0'
  }

  // 统计卡片标题样式
  const statTitleStyle = {
    fontSize: 14,
    color: '#8c8c8c',
    marginBottom: 8
  }

  // 预订趋势图表配置
  const lineConfig = {
    data: bookingTrendData,
    xField: 'date',
    yField: 'value',
    smooth: true,
    color: '#1890ff',
    point: {
      size: 5,
      shape: 'diamond',
    },
    tooltip: {
      showMarkers: false,
    },
    xAxis: {
      label: {
        autoHide: true,
        autoRotate: false,
      },
    },
    yAxis: {
      label: {
        formatter: (v) => `${v}单`,
      },
    },
    area: {
      style: {
        fill: 'l(270 100% 0%) to(r(0%) rgba(24, 144, 255, 0.2) 0%)',
      },
    },
  }

  // 预订渠道图表配置
  const barConfig = {
    data: channelData,
    xField: 'name',
    yField: 'value',
    color: '#1890ff',
    label: {
      position: 'inside',
      style: {
        fill: '#FFFFFF',
        opacity: 0.8,
      },
    },
    xAxis: {
      label: {
        autoHide: true,
        autoRotate: false,
      },
    },
    yAxis: {
      label: {
        formatter: (v) => `${v}%`,
      },
    },
  }

  // 通知图标映射
  const notificationIconMap = {
    info: <InfoCircleOutlined />,
    warning: <WarningOutlined />,
    success: <CheckCircleOutlined />
  }

  // 通知颜色映射
  const notificationColorMap = {
    info: '#1890ff',
    warning: '#faad14',
    success: '#52c41a'
  }

  return (
    <div className="fade-in">
      {/* 欢迎区域 */}
      <Card style={{ marginBottom: 16, borderRadius: 8 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Title level={4} style={{ margin: 0 }}>欢迎，接待</Title>
            <Text type="secondary">南京站红山动物园雅斯阁酒店</Text>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Text>当前时间：</Text>
              <Text strong>{new Date().toLocaleString()}</Text>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Text>当前用户：</Text>
              <Text strong>admin</Text>
            </div>
          </div>
        </div>
      </Card>



      {/* 主要内容区域 */}
      <Row gutter={[16, 16]}>
        {/* 左侧内容 */}
        <Col xs={24} lg={16}>
          {/* 可用库存 */}
          <Card title="可用库存" style={{ marginBottom: 16, borderRadius: 8 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <div style={{ display: 'flex', gap: 8 }}>
                <Button type="default" size="small">查看房价日历</Button>
                <Select defaultValue="全部房型" style={{ width: 120 }} size="small">
                  <Option value="全部房型">全部房型</Option>
                  <Option value="标准大床房">标准大床房</Option>
                  <Option value="豪华大床房">豪华大床房</Option>
                  <Option value="标准双床房">标准双床房</Option>
                </Select>
              </div>
              <DatePicker.RangePicker size="small" />
            </div>
            
            <div style={{ display: 'flex', gap: 8, overflowX: 'auto', padding: '8px 0' }}>
              {inventoryData.map((item, index) => (
                <div 
                  key={index} 
                  style={{
                    minWidth: 60,
                    height: 80,
                    borderRadius: 6,
                    backgroundColor: index === 3 ? '#e6f7ff' : '#fafafa',
                    border: `1px solid ${index === 3 ? '#91d5ff' : '#e8e8e8'}`,
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    cursor: 'pointer',
                    transition: 'all 0.3s'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'translateY(-2px)'
                    e.currentTarget.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.1)'
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'translateY(0)'
                    e.currentTarget.style.boxShadow = 'none'
                  }}
                >
                  <Text style={{ fontSize: 12, color: '#8c8c8c', marginBottom: 4 }}>{item.date}</Text>
                  <Text strong style={{ fontSize: 18, color: '#262626' }}>{item.value}</Text>
                </div>
              ))}
            </div>
          </Card>

          {/* 数据概览 */}
          <Card title="数据概览" style={{ marginBottom: 16, borderRadius: 8 }}>
            <Row gutter={[16, 16]}>
              {overviewData.map((item, index) => (
                <Col xs={24} sm={12} md={8} key={index}>
                  <Card style={{
                    borderRadius: 8,
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.05)',
                    border: '1px solid #f0f0f0'
                  }}>
                    <div style={{ fontSize: 14, color: '#8c8c8c', marginBottom: 8 }}>{item.label}</div>
                    <div style={{ fontSize: 24, fontWeight: 600, color: '#262626' }}>
                      {item.value} {item.unit}
                    </div>
                  </Card>
                </Col>
              ))}
            </Row>
          </Card>

          {/* 图表区域 */}
          <Row gutter={[16, 16]}>
            <Col xs={24} lg={12}>
              <Card title="预订趋势" style={{ borderRadius: 8, height: 300 }}>
                <div style={{ height: 250 }}>
                  <Line {...lineConfig} />
                </div>
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card title="预订渠道" style={{ borderRadius: 8, height: 300 }}>
                <div style={{ height: 250 }}>
                  <Bar {...barConfig} />
                </div>
              </Card>
            </Col>
          </Row>
        </Col>

        {/* 右侧内容 */}
        <Col xs={24} lg={8}>
          {/* 住宿信息 */}
          <Card title="住宿" style={{ marginBottom: 16, borderRadius: 8 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0' }}>
                <div>
                  <Text strong>当前住宿</Text>
                </div>
                <Badge status="success" text="已连接" />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0' }}>
                <div>
                  <Text>南京站红山动物园雅斯阁酒店</Text>
                </div>
              </div>
            </Space>
          </Card>

          {/* PMS连接 */}
          <Card title="PMS连接" style={{ marginBottom: 16, borderRadius: 8 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0' }}>
                <div>
                  <Text>罗克佳华管理系统</Text>
                </div>
                <Badge status="success" text="已连接" />
              </div>
              <div style={{ display: 'flex', gap: 8 }}>
                <Button type="link" size="small">同步数据</Button>
                <Button type="link" size="small">设置</Button>
              </div>
            </Space>
          </Card>

          {/* 渠道状态 */}
          <Card title="渠道状态" style={{ marginBottom: 16, borderRadius: 8 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              {channelStatus.map((channel, index) => (
                <div key={index} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '4px 0' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Badge status={channel.status === '在线' ? 'success' : 'error'} />
                    <Text>{channel.name}</Text>
                  </div>
                  <div style={{ display: 'flex', gap: 4 }}>
                    <Button icon={<PlusOutlined />} size="small" type="text" />
                    <Button icon={<MinusOutlined />} size="small" type="text" />
                  </div>
                </div>
              ))}
              <Button type="dashed" block size="small" icon={<PlusOutlined />}>
                添加渠道
              </Button>
            </Space>
          </Card>

          {/* 平台通知 */}
          <Card title="平台通知" style={{ borderRadius: 8 }}>
            <List
              dataSource={notifications}
              renderItem={(item) => (
                <List.Item
                  key={item.id}
                  style={{ padding: '8px 0', borderBottom: '1px solid #f0f0f0' }}
                >
                  <List.Item.Meta
                    avatar={
                      <div style={{ color: notificationColorMap[item.type], fontSize: 16 }}>
                        {notificationIconMap[item.type]}
                      </div>
                    }
                    title={<Text ellipsis>{item.title}</Text>}
                    description={
                      <div>
                        <Text type="secondary" ellipsis style={{ fontSize: 12, display: 'block', marginBottom: 4 }}>{item.content}</Text>
                        <Text type="secondary" style={{ fontSize: 10 }}>{item.time}</Text>
                      </div>
                    }
                  />
                </List.Item>
              )}
              pagination={{ pageSize: 3, size: 'small' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard

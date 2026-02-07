import React, { useState } from 'react'
import { Card, Tabs, Tag, Row, Col, Typography } from 'antd'
import { LinkOutlined, CheckCircleOutlined } from '@ant-design/icons'
import meituanLogo from '../../assets/images/channels/meituan.svg'
import ctripLogo from '../../assets/images/channels/ctrip.svg'
import fliggyLogo from '../../assets/images/channels/fliggy.svg'

const { TabPane } = Tabs
const { Title } = Typography

// 模拟已连接渠道数据
const connectedChannels = [
  {
    id: 1,
    name: '飞猪',
    icon: fliggyLogo,
    status: 'connected',
    connectionTime: '20产品在售'
  },
  {
    id: 2,
    name: '红色动力',
    icon: 'https://via.placeholder.com/100?text=红色动力',
    status: 'connected',
    connectionTime: '20产品在售'
  },
  {
    id: 3,
    name: '美团',
    icon: meituanLogo,
    status: 'connected',
    connectionTime: '20产品在售'
  },
  {
    id: 4,
    name: '携程',
    icon: ctripLogo,
    status: 'connected',
    connectionTime: '20产品在售'
  }
]

// 模拟可连接渠道数据
const availableChannels = [
  {
    id: 6,
    name: 'Booking.com',
    icon: 'https://i.imgur.com/5cQZ1aR.png',
    status: 'available'
  },
  {
    id: 7,
    name: 'Agoda',
    icon: 'https://i.imgur.com/2bK8z1S.png',
    status: 'available'
  },
  {
    id: 8,
    name: 'Expedia',
    icon: 'https://i.imgur.com/7cF9z2a.png',
    status: 'available'
  },
  {
    id: 9,
    name: 'Hotels.com',
    icon: 'https://i.imgur.com/1aQz3fL.png',
    status: 'available'
  }
]

const ChannelList = () => {
  // 状态管理
  const [channels, setChannels] = useState({
    connected: connectedChannels,
    available: availableChannels
  })

  // 渲染渠道卡片
  const renderChannelCard = (channel) => {
    return (
      <Col xs={24} sm={12} md={8} lg={6} xl={6} key={channel.id}>
        <Card
          hoverable
          style={{
            borderRadius: 8,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)',
            border: '1px solid #f0f0f0',
            height: '100%',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px 0'
          }}
        >
          <img 
            src={channel.icon} 
            alt={channel.name} 
            style={{ 
              width: 80, 
              height: 80, 
              objectFit: 'contain', 
              marginBottom: 16,
              borderRadius: 8
            }} 
          />
          <div style={{ fontSize: 16, fontWeight: 500, marginBottom: 8 }}>{channel.name}</div>
          {channel.status === 'connected' && (
            <Tag color="green" icon={<CheckCircleOutlined />} style={{ marginBottom: 16 }}>
              {channel.connectionTime}
            </Tag>
          )}
        </Card>
      </Col>
    )
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <LinkOutlined />
        渠道管理
      </h1>
      
      <Tabs defaultActiveKey="connected" type="card" size="large" style={{ marginBottom: 24 }}>
        <TabPane tab="已连接渠道" key="connected">
          <Row gutter={[16, 16]}>
            {channels.connected.map(renderChannelCard)}
          </Row>
        </TabPane>
        <TabPane tab="可连接渠道" key="available">
          <Row gutter={[16, 16]}>
            {channels.available.map(renderChannelCard)}
          </Row>
        </TabPane>
      </Tabs>
    </div>
  )
}

export default ChannelList
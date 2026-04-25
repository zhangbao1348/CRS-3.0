import React, { useState } from 'react'
import { Card, Tabs, Tag, Row, Col, Typography } from 'antd'
import { LinkOutlined, CheckCircleOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import ctripLogo from '../../assets/images/channels/ctrip.webp'
import meituanLogo from '../../assets/images/channels/meituan.webp'
import feizhuLogo from '../../assets/images/channels/feizhu.jpeg'
import hongsejialiLogo from '../../assets/images/channels/hongsejiali.png'

const { TabPane } = Tabs
const { Title } = Typography

// 模拟已连接渠道数据
const connectedChannels = [
  {
    id: 4,
    name: '携程',
    icon: ctripLogo,
    status: 'connected',
    connectionTime: '20产品在售'
  },
  {
    id: 1,
    name: '飞猪',
    icon: feizhuLogo,
    status: 'connected',
    connectionTime: '20产品在售'
  },
  {
    id: 2,
    name: '红色加力',
    icon: hongsejialiLogo,
    status: 'connected',
    connectionTime: '20产品在售'
  },
  {
    id: 3,
    name: '美团',
    icon: meituanLogo,
    status: 'connected',
    connectionTime: '20产品在售'
  }
]

// 模拟可连接渠道数据
const availableChannels = [
  {
    id: 6,
    name: 'Booking.com',
    icon: 'https://www.booking.com/favicon.ico',
    status: 'available'
  },
  {
    id: 7,
    name: 'Agoda',
    icon: 'https://www.agoda.com/favicon.ico',
    status: 'available'
  },
  {
    id: 8,
    name: 'Expedia',
    icon: 'https://www.expedia.com/favicon.ico',
    status: 'available'
  },
  {
    id: 9,
    name: 'Hotels.com',
    icon: 'https://www.hotels.com/favicon.ico',
    status: 'available'
  }
]

const ChannelList = () => {
  // 状态管理
  const [channels, setChannels] = useState({
    connected: connectedChannels,
    available: availableChannels
  })
  const navigate = useNavigate()

  // 渲染渠道卡片
  const renderChannelCard = (channel) => {
    // 处理卡片点击
    const handleCardClick = () => {
      if (channel.name === '飞猪') {
        navigate('/channel-management/fliggy-setting')
      } else if (channel.name === '携程') {
        navigate('/channel-management/ctrip-setting')
      }
    }

    return (
      <Col xs={24} sm={12} md={8} lg={6} xl={6} key={channel.id}>
        <Card
          hoverable
          onClick={(channel.name === '飞猪' || channel.name === '携程') ? handleCardClick : undefined}
          style={{
            borderRadius: 16,
            boxShadow: '0 6px 20px rgba(0, 0, 0, 0.08)',
            border: '1px solid #e8e8e8',
            height: '100%',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '40px 0',
            transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
            cursor: (channel.name === '飞猪' || channel.name === '携程') ? 'pointer' : 'default',
            backgroundColor: '#ffffff'
          }}
          bodyStyle={{
            padding: 0
          }}
          hoverable={!!((channel.name === '飞猪' || channel.name === '携程'))}
        >
          <div style={{ 
            width: 160, 
            height: 160, 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            marginBottom: 24,
            backgroundColor: 
              channel.name === '携程' ? '#f0f9ff' : 
              channel.name === '美团' ? '#fff7e6' :
              channel.name === '飞猪' ? '#f6ffed' :
              channel.name === '红色加力' ? '#fff2f0' : '#fafafa',
            borderRadius: 16,
            border: 
              channel.name === '携程' ? '1px solid #e6f7ff' : 
              channel.name === '美团' ? '1px solid #ffd591' :
              channel.name === '飞猪' ? '1px solid #b7eb8f' :
              channel.name === '红色加力' ? '1px solid #ffccc7' : '1px solid #f0f0f0',
            transition: 'all 0.3s ease',
            overflow: 'hidden'
          }}>
            <img 
              src={channel.icon} 
              alt={channel.name} 
              style={{ 
                width: '100%', 
                height: '100%', 
                objectFit: 'contain',
                display: 'block',
                transition: 'all 0.3s ease'
              }} 
            />
          </div>
          <div style={{ 
            fontSize: 20, 
            fontWeight: 600, 
            marginBottom: 16,
            textAlign: 'center',
            color: '#333333',
            transition: 'all 0.3s ease'
          }}>{channel.name}</div>
          {channel.status === 'connected' && (
            <Tag 
              color="green" 
              icon={<CheckCircleOutlined />} 
              style={{ 
                marginBottom: 8,
                fontSize: 14,
                padding: '6px 16px',
                borderRadius: 20,
                boxShadow: '0 2px 8px rgba(45, 183, 128, 0.2)'
              }}
            >
              {channel.connectionTime}
            </Tag>
          )}
          {channel.status === 'available' && (
            <Tag 
              color="blue" 
              style={{ 
                marginBottom: 8,
                fontSize: 14,
                padding: '6px 16px',
                borderRadius: 20,
                boxShadow: '0 2px 8px rgba(24, 144, 255, 0.2)'
              }}
            >
              未连接
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
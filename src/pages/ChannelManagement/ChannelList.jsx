import { useState, useEffect } from 'react'
import { Card, Tabs, Tag, Row, Col, Spin, message } from 'antd'
import { LinkOutlined, CheckCircleOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { tenantChannelApi } from '../../utils/api'
import ctripLogo from '../../assets/images/channels/ctrip.webp'
import meituanLogo from '../../assets/images/channels/meituan.webp'
import feizhuLogo from '../../assets/images/channels/feizhu.jpeg'
import hongsejialiLogo from '../../assets/images/channels/hongsejiali.png'

// 本地LOGO映射（渠道代码 -> 本地图片）
const localLogoMap = {
  'CTRIP': ctripLogo,
  'MEITUAN': meituanLogo,
  'FLIGGY': feizhuLogo,
  'RED_POWER': hongsejialiLogo
}

// 渠道卡片背景色映射
const channelStyleMap = {
  'CTRIP': { bg: '#f0f9ff', border: '1px solid #e6f7ff' },
  'MEITUAN': { bg: '#fff7e6', border: '1px solid #ffd591' },
  'FLIGGY': { bg: '#f6ffed', border: '1px solid #b7eb8f' },
  'RED_POWER': { bg: '#fff2f0', border: '1px solid #ffccc7' }
}

// 渠道代码 -> 设置页面路由映射
const channelRouteMap = {
  'FLIGGY': '/channel-management/channel-setting/FLIGGY',
  'CTRIP': '/channel-management/channel-setting/CTRIP'
}

const ChannelList = () => {
  const [channels, setChannels] = useState({ connected: [], available: [] })
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    fetchChannels()
  }, [])

  const fetchChannels = async () => {
    setLoading(true)
    try {
      const response = await tenantChannelApi.getChannelsGrouped(1)
      const data = response
      // 转换后端数据为前端卡片格式
      const connected = (data.connected || []).map(ch => ({
        id: ch.id,
        name: ch.channelName,
        code: ch.channelCode,
        switchChannel: ch.switchChannel,
        icon: localLogoMap[ch.channelCode] || ch.logoUrl,
        status: 'connected',
        connectionTime: '20产品在售'
      }))
      const available = (data.available || []).map(ch => ({
        id: ch.id,
        name: ch.channelName,
        code: ch.channelCode,
        switchChannel: ch.switchChannel,
        icon: localLogoMap[ch.channelCode] || ch.logoUrl || '',
        status: 'available'
      }))
      setChannels({ connected, available })
    } catch (error) {
      console.error('获取渠道列表失败:', error)
      message.error('获取渠道列表失败')
    } finally {
      setLoading(false)
    }
  }

  const renderChannelCard = (channel) => {
    // 确定路由：专属渠道用固定路由，Real_Time_API 渠道用通用路由
    let route = channelRouteMap[channel.code]
    if (!route && channel.switchChannel === 'Real_Time_API') {
      route = `/channel-management/channel-setting/${channel.code}`
    }
    const style = channelStyleMap[channel.code] || { bg: '#fafafa', border: '1px solid #f0f0f0' }

    const handleCardClick = () => {
      if (route) {
        navigate(route)
      }
    }

    return (
      <Col xs={24} sm={12} md={8} lg={6} xl={6} key={channel.id}>
        <Card
          hoverable={!!route}
          onClick={route ? handleCardClick : undefined}
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
            cursor: route ? 'pointer' : 'default',
            backgroundColor: '#ffffff'
          }}
          styles={{ body: { padding: 0 } }}
        >
          <div style={{
            width: 160,
            height: 160,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: 24,
            backgroundColor: style.bg,
            borderRadius: 16,
            border: style.border,
            transition: 'all 0.3s ease',
            overflow: 'hidden'
          }}>
            {channel.icon && (
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
            )}
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

      <Spin spinning={loading}>
        <Tabs
          defaultActiveKey="connected"
          type="card"
          size="large"
          style={{ marginBottom: 24 }}
          items={[
            {
              key: 'connected',
              label: '已连接渠道',
              children: <Row gutter={[16, 16]}>{channels.connected.map(renderChannelCard)}</Row>
            },
            {
              key: 'available',
              label: '可连接渠道',
              children: <Row gutter={[16, 16]}>{channels.available.map(renderChannelCard)}</Row>
            }
          ]}
        />
      </Spin>
    </div>
  )
}

export default ChannelList

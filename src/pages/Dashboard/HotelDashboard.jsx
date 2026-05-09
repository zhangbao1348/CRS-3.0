import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Statistic, Table, Tag, List, Spin, Empty, Typography, Badge } from 'antd'
import {
  LoginOutlined,
  LogoutOutlined,
  HomeOutlined,
  ShoppingCartOutlined,
  PercentageOutlined,
  DollarOutlined,
  CalendarOutlined,
  FileTextOutlined,
  SearchOutlined,
  SettingOutlined
} from '@ant-design/icons'
import { Column, Bar } from '@ant-design/plots'
import { dashboardApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { useNavigate } from 'react-router-dom'

const { Title, Text } = Typography

/**
 * 门店首页
 * 展示单个酒店的运营数据概览
 * 关联模块: DashboardController（/api/dashboard/hotel）
 */
const HotelDashboard = () => {
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const { selectedHotel, hotels } = useHotelContext()
  const navigate = useNavigate()

  useEffect(() => {
    if (selectedHotel) {
      fetchData(selectedHotel)
    }
  }, [selectedHotel])

  const fetchData = async (hotelCode) => {
    setLoading(true)
    setError(null)
    try {
      const res = await dashboardApi.getHotelDashboard(hotelCode)
      if (res && res.error) {
        setError(res.error)
      } else {
        setData(res)
      }
    } catch (err) {
      console.error('加载门店首页数据失败:', err)
      const msg = err?.error || err?.message || '加载数据失败'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh' }}>
        <Spin size="large" tip="加载中..." />
      </div>
    )
  }

  if (error) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh', flexDirection: 'column' }}>
        <Empty description={error} />
        <Text type="secondary" style={{ marginTop: 16 }}>请先在顶部导航栏选择一个租户</Text>
      </div>
    )
  }

  if (!data) {
    return <Empty description="暂无数据" />
  }

  const { hotelInfo, stats, weekInventory = {}, recentOrders = [], bookingTrend = [], channelDistribution = [] } = data

  // 快捷操作
  const shortcuts = [
    { icon: <CalendarOutlined style={{ fontSize: 24, color: '#1890ff' }} />, title: '房控日历', path: '/inventory' },
    { icon: <FileTextOutlined style={{ fontSize: 24, color: '#52c41a' }} />, title: '订单管理', path: '/reservation/reservation-list' },
    { icon: <SearchOutlined style={{ fontSize: 24, color: '#faad14' }} />, title: '价格查询', path: '/rate-management/price-query' },
    { icon: <SettingOutlined style={{ fontSize: 24, color: '#722ed1' }} />, title: '房态管理', path: '/inventory/room-status' }
  ]

  // 库存日历数据
  const inventoryEntries = Object.entries(weekInventory)
  const totalRooms = hotelInfo?.totalRooms || 0

  // 柱状图配置 - 本周趋势
  const columnConfig = {
    data: bookingTrend,
    xField: 'date',
    yField: 'count',
    color: '#1890ff',
    columnWidthRatio: 0.5,
    label: { position: 'top', style: { fill: '#595959', fontSize: 12 } },
    xAxis: { label: { autoHide: true, autoRotate: false } },
    yAxis: { label: { formatter: (v) => `${v}单` } }
  }

  // 横向条形图 - 渠道分布
  const barConfig = {
    data: channelDistribution.map(c => ({ channel: c.channelName || '未知', count: c.count })),
    xField: 'count',
    yField: 'channel',
    color: '#1890ff',
    label: { position: 'right', style: { fill: '#595959' } },
    yAxis: { label: { autoRotate: false } }
  }

  const cardStyle = { borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }

  // 获取星级显示
  const starDisplay = hotelInfo?.starRating ? '★'.repeat(parseInt(hotelInfo.starRating) || 0) : ''

  return (
    <div className="fade-in">
      {/* 酒店信息头部 */}
      <Card style={{ marginBottom: 16, borderRadius: 8, background: 'linear-gradient(135deg, #003366 0%, #006699 100%)' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <Title level={4} style={{ margin: 0, color: '#fff' }}>{hotelInfo?.hotelName || '酒店'}</Title>
              {starDisplay && <Text style={{ color: '#ffd700', fontSize: 16 }}>{starDisplay}</Text>}
            </div>
            <Text style={{ color: 'rgba(255,255,255,0.75)' }}>{hotelInfo?.city} · 总房量 {totalRooms} 间</Text>
          </div>
          <Text style={{ color: 'rgba(255,255,255,0.6)' }}>{new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })}</Text>
        </div>
      </Card>

      {/* 核心 KPI */}
      <Row gutter={[12, 12]} style={{ marginBottom: 16 }}>
        <Col xs={12} sm={8} md={4}>
          <Card style={cardStyle} bodyStyle={{ padding: '16px 12px' }}>
            <Statistic title="今日入住" value={stats?.todayCheckIn || 0} prefix={<LoginOutlined style={{ color: '#52c41a' }} />} valueStyle={{ color: '#52c41a', fontSize: 22 }} />
          </Card>
        </Col>
        <Col xs={12} sm={8} md={4}>
          <Card style={cardStyle} bodyStyle={{ padding: '16px 12px' }}>
            <Statistic title="今日退房" value={stats?.todayCheckOut || 0} prefix={<LogoutOutlined style={{ color: '#faad14' }} />} valueStyle={{ color: '#faad14', fontSize: 22 }} />
          </Card>
        </Col>
        <Col xs={12} sm={8} md={4}>
          <Card style={cardStyle} bodyStyle={{ padding: '16px 12px' }}>
            <Statistic title="在住" value={stats?.inHouse || 0} prefix={<HomeOutlined style={{ color: '#1890ff' }} />} valueStyle={{ color: '#1890ff', fontSize: 22 }} />
          </Card>
        </Col>
        <Col xs={12} sm={8} md={4}>
          <Card style={cardStyle} bodyStyle={{ padding: '16px 12px' }}>
            <Statistic title="今日可售" value={stats?.todayAvailable || 0} prefix={<ShoppingCartOutlined style={{ color: '#722ed1' }} />} valueStyle={{ color: '#722ed1', fontSize: 22 }} />
          </Card>
        </Col>
        <Col xs={12} sm={8} md={4}>
          <Card style={cardStyle} bodyStyle={{ padding: '16px 12px' }}>
            <Statistic title="出租率" value={stats?.occupancyRate || 0} suffix="%" prefix={<PercentageOutlined style={{ color: '#eb2f96' }} />} valueStyle={{ color: '#eb2f96', fontSize: 22 }} />
          </Card>
        </Col>
        <Col xs={12} sm={8} md={4}>
          <Card style={cardStyle} bodyStyle={{ padding: '16px 12px' }}>
            <Statistic title="本月收入" value={Number(stats?.monthRevenue || 0)} prefix={<DollarOutlined style={{ color: '#fa8c16' }} />} valueStyle={{ color: '#fa8c16', fontSize: 22 }} precision={0} formatter={(val) => `¥${Number(val).toLocaleString()}`} />
          </Card>
        </Col>
      </Row>

      {/* 库存日历 + 快捷操作 & 渠道分布 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} lg={15}>
          <Card title="未来 7 天库存概览" style={cardStyle}>
            <div style={{ display: 'flex', gap: 8, overflowX: 'auto', padding: '4px 0' }}>
              {inventoryEntries.map(([date, available]) => {
                const isLow = available <= 2
                const isZero = available === 0
                const dayLabel = new Date(date + 'T00:00:00').toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric', weekday: 'short' })
                return (
                  <div
                    key={date}
                    style={{
                      minWidth: 80,
                      height: 90,
                      borderRadius: 8,
                      backgroundColor: isZero ? '#fff1f0' : isLow ? '#fffbe6' : '#f6ffed',
                      border: `1px solid ${isZero ? '#ffa39e' : isLow ? '#ffe58f' : '#b7eb8f'}`,
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'center',
                      alignItems: 'center',
                      cursor: 'pointer',
                      transition: 'all 0.3s'
                    }}
                    onClick={() => navigate('/inventory')}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.transform = 'translateY(-3px)'
                      e.currentTarget.style.boxShadow = '0 6px 16px rgba(0,0,0,0.12)'
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = 'translateY(0)'
                      e.currentTarget.style.boxShadow = 'none'
                    }}
                  >
                    <Text style={{ fontSize: 12, color: '#8c8c8c', marginBottom: 4 }}>{dayLabel}</Text>
                    <Text strong style={{ fontSize: 24, color: isZero ? '#ff4d4f' : isLow ? '#faad14' : '#52c41a' }}>{available}</Text>
                    <Text style={{ fontSize: 11, color: '#bfbfbf' }}>可售</Text>
                  </div>
                )
              })}
            </div>
          </Card>
        </Col>
        <Col xs={24} lg={9}>
          {/* 快捷操作 */}
          <Card title="快捷操作" style={{ ...cardStyle, marginBottom: 16 }}>
            <Row gutter={[12, 12]}>
              {shortcuts.map((s, i) => (
                <Col span={12} key={i}>
                  <div
                    onClick={() => navigate(s.path)}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 12,
                      padding: '12px 16px',
                      borderRadius: 8,
                      backgroundColor: '#fafafa',
                      cursor: 'pointer',
                      transition: 'all 0.3s',
                      border: '1px solid #f0f0f0'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.backgroundColor = '#e6f7ff'
                      e.currentTarget.style.borderColor = '#91d5ff'
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.backgroundColor = '#fafafa'
                      e.currentTarget.style.borderColor = '#f0f0f0'
                    }}
                  >
                    {s.icon}
                    <Text strong>{s.title}</Text>
                  </div>
                </Col>
              ))}
            </Row>
          </Card>
          {/* 渠道分布 */}
          <Card title="渠道订单分布" style={cardStyle}>
            {channelDistribution.length > 0 ? (
              <div style={{ height: 150 }}>
                <Bar {...barConfig} />
              </div>
            ) : (
              <Empty description="暂无渠道数据" style={{ padding: '20px 0' }} />
            )}
          </Card>
        </Col>
      </Row>

      {/* 今日订单 + 本周趋势 */}
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={14}>
          <Card title="最近订单" style={cardStyle}>
            <List
              dataSource={recentOrders}
              renderItem={(item) => (
                <List.Item
                  key={item.reservationCode}
                  style={{ padding: '10px 0', cursor: 'pointer' }}
                  onClick={() => navigate(`/reservation/reservation-detail?code=${item.reservationCode}`)}
                >
                  <List.Item.Meta
                    title={
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                          <Text strong>{item.contactName || '未知'}</Text>
                          <Text type="secondary" style={{ marginLeft: 8, fontSize: 12 }}>{item.reservationCode}</Text>
                        </div>
                        <Text strong style={{ color: '#1890ff' }}>¥{Number(item.totalPrice || 0).toLocaleString()}</Text>
                      </div>
                    }
                    description={
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Text type="secondary" style={{ fontSize: 12 }}>
                          {item.roomTypeName} · {item.channelName} · {item.checkInDate}~{item.checkOutDate} · {item.nights}晚{item.roomCount}间
                        </Text>
                        <Tag color={item.reservationStatus === 'confirmed' ? 'green' : item.reservationStatus === 'cancelled' ? 'red' : 'blue'} style={{ fontSize: 11 }}>
                          {item.reservationStatus === 'confirmed' ? '已确认' : item.reservationStatus === 'cancelled' ? '已取消' : item.reservationStatus}
                        </Tag>
                      </div>
                    }
                  />
                </List.Item>
              )}
              locale={{ emptyText: '暂无订单' }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={10}>
          <Card title="本周预订趋势" style={{ ...cardStyle, height: 350 }}>
            {bookingTrend.length > 0 ? (
              <div style={{ height: 270 }}>
                <Column {...columnConfig} />
              </div>
            ) : (
              <Empty description="暂无趋势数据" />
            )}
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default HotelDashboard

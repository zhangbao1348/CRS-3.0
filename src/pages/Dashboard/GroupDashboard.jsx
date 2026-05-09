import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Statistic, Table, Tag, List, Spin, Empty, Badge, Typography, Tooltip } from 'antd'
import {
  FileTextOutlined,
  LoginOutlined,
  DollarOutlined,
  BankOutlined,
  ToolOutlined,
  WarningOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined
} from '@ant-design/icons'
import { Line, Pie } from '@ant-design/plots'
import { dashboardApi } from '../../utils/api'
import { useNavigate } from 'react-router-dom'
import { useHotelContext } from '../../contexts/HotelContext'

const { Title, Text } = Typography

/**
 * 集团首页
 * 展示集团维度的运营数据概览
 * 关联模块: DashboardController（/api/dashboard/group）
 */
const GroupDashboard = () => {
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const navigate = useNavigate()
  const { changeHotel } = useHotelContext()

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await dashboardApi.getGroupDashboard()
      if (res && res.error) {
        setError(res.error)
      } else {
        setData(res)
      }
    } catch (err) {
      console.error('加载集团首页数据失败:', err)
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

  const { stats, hotelOverview = [], bookingTrend = [], channelDistribution = [], inventoryAlerts = [], recentOrders = [] } = data

  // 酒店运营概览表列定义
  const hotelColumns = [
    {
      title: '酒店名称',
      dataIndex: 'hotelName',
      key: 'hotelName',
      render: (text, record) => (
        <a onClick={() => {
          changeHotel(record.hotelCode)
          navigate('/dashboard')
        }} style={{ fontWeight: 500 }}>
          {text}
        </a>
      )
    },
    {
      title: '城市',
      dataIndex: 'city',
      key: 'city',
      width: 80
    },
    {
      title: '今日入住',
      dataIndex: 'todayCheckIn',
      key: 'todayCheckIn',
      width: 90,
      align: 'center',
      render: (val) => <Text strong style={{ color: val > 0 ? '#52c41a' : '#bfbfbf' }}>{val}</Text>
    },
    {
      title: '今日退房',
      dataIndex: 'todayCheckOut',
      key: 'todayCheckOut',
      width: 90,
      align: 'center',
      render: (val) => <Text strong>{val}</Text>
    },
    {
      title: '今日可用库存',
      dataIndex: 'todayAvailableRooms',
      key: 'todayAvailableRooms',
      width: 110,
      align: 'center',
      render: (val) => (
        <Text strong style={{ color: val <= 2 ? '#ff4d4f' : '#262626' }}>
          {val}
          {val <= 2 && <WarningOutlined style={{ color: '#ff4d4f', marginLeft: 4 }} />}
        </Text>
      )
    },
    {
      title: '本月收入',
      dataIndex: 'monthRevenue',
      key: 'monthRevenue',
      width: 120,
      align: 'right',
      render: (val) => <Text strong>¥{Number(val || 0).toLocaleString()}</Text>
    }
  ]

  // 折线图配置
  const lineConfig = {
    data: bookingTrend,
    xField: 'date',
    yField: 'count',
    smooth: true,
    color: '#1890ff',
    point: { size: 3, shape: 'circle' },
    xAxis: { label: { autoHide: true, autoRotate: false } },
    yAxis: { label: { formatter: (v) => `${v}单` } },
    area: { style: { fill: 'l(270) 0:#1890ff33 1:#1890ff05' } }
  }

  // 饼图配置
  const pieConfig = {
    data: channelDistribution.map(c => ({ type: c.channelName || '未知', value: c.count })),
    angleField: 'value',
    colorField: 'type',
    radius: 0.9,
    innerRadius: 0.6,
    label: { type: 'outer', content: '{name} {percentage}' },
    interactions: [{ type: 'element-active' }],
    statistic: {
      title: { content: '总订单', style: { fontSize: 14 } },
      content: {
        content: channelDistribution.reduce((sum, c) => sum + c.count, 0),
        style: { fontSize: 24, fontWeight: 600 }
      }
    }
  }

  const cardStyle = { borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }

  return (
    <div className="fade-in">
      {/* 欢迎区 */}
      <Card style={{ marginBottom: 16, borderRadius: 8, background: 'linear-gradient(135deg, #003366 0%, #005599 100%)' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Title level={4} style={{ margin: 0, color: '#fff' }}>集团运营总览</Title>
            <Text style={{ color: 'rgba(255,255,255,0.75)' }}>实时掌握旗下所有酒店的运营状况</Text>
          </div>
          <Text style={{ color: 'rgba(255,255,255,0.6)' }}>{new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })}</Text>
        </div>
      </Card>

      {/* 核心指标卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={12} md={8} lg={4} xl={4}>
          <Card style={cardStyle}>
            <Statistic title="今日新订单" value={stats?.todayNewOrders || 0} prefix={<FileTextOutlined style={{ color: '#1890ff' }} />} valueStyle={{ color: '#1890ff' }} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={5} xl={5}>
          <Card style={cardStyle}>
            <Statistic title="今日入住" value={stats?.todayCheckIn || 0} prefix={<LoginOutlined style={{ color: '#52c41a' }} />} valueStyle={{ color: '#52c41a' }} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={5} xl={5}>
          <Card style={cardStyle}>
            <Statistic title="本月收入" value={Number(stats?.monthRevenue || 0)} prefix={<DollarOutlined style={{ color: '#faad14' }} />} valueStyle={{ color: '#faad14' }} precision={0} formatter={(val) => `¥${Number(val).toLocaleString()}`} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={5} xl={5}>
          <Card style={cardStyle}>
            <Statistic title="活跃酒店" value={stats?.activeHotelCount || 0} prefix={<BankOutlined style={{ color: '#722ed1' }} />} valueStyle={{ color: '#722ed1' }} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={5} xl={5}>
          <Card style={cardStyle}>
            <Statistic title="待处理" value={stats?.pendingManual || 0} prefix={<ToolOutlined style={{ color: '#ff4d4f' }} />} valueStyle={{ color: stats?.pendingManual > 0 ? '#ff4d4f' : '#bfbfbf' }} />
          </Card>
        </Col>
      </Row>

      {/* 酒店运营概览 + 最新订单 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} lg={15}>
          <Card title="酒店运营概览" style={cardStyle}>
            <Table
              columns={hotelColumns}
              dataSource={hotelOverview}
              rowKey="hotelCode"
              pagination={false}
              size="small"
              scroll={{ x: 600 }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={9}>
          <Card title="最新订单" style={cardStyle}>
            <List
              dataSource={recentOrders}
              renderItem={(item) => (
                <List.Item
                  key={item.reservationCode}
                  style={{ padding: '8px 0', cursor: 'pointer' }}
                  onClick={() => navigate(`/reservation/reservation-detail?code=${item.reservationCode}`)}
                >
                  <List.Item.Meta
                    title={
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Text ellipsis style={{ maxWidth: 140 }}>{item.contactName || '未知'}</Text>
                        <Text strong style={{ color: '#1890ff' }}>¥{Number(item.totalPrice || 0).toLocaleString()}</Text>
                      </div>
                    }
                    description={
                      <div>
                        <Text type="secondary" style={{ fontSize: 12 }}>{item.hotelName} · {item.roomTypeName}</Text>
                        <br />
                        <Text type="secondary" style={{ fontSize: 11 }}>{item.checkInDate} ~ {item.checkOutDate}</Text>
                        <Tag color={item.reservationStatus === 'confirmed' ? 'green' : item.reservationStatus === 'cancelled' ? 'red' : 'blue'} style={{ marginLeft: 8, fontSize: 11 }}>
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
      </Row>

      {/* 趋势图 + 渠道分布 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} lg={14}>
          <Card title="近30天预订趋势" style={{ ...cardStyle, height: 350 }}>
            {bookingTrend.length > 0 ? (
              <div style={{ height: 270 }}><Line {...lineConfig} /></div>
            ) : (
              <Empty description="暂无趋势数据" />
            )}
          </Card>
        </Col>
        <Col xs={24} lg={10}>
          <Card title="渠道贡献分布" style={{ ...cardStyle, height: 350 }}>
            {channelDistribution.length > 0 ? (
              <div style={{ height: 270 }}><Pie {...pieConfig} /></div>
            ) : (
              <Empty description="暂无渠道数据" />
            )}
          </Card>
        </Col>
      </Row>

      {/* 库存预警 */}
      {inventoryAlerts.length > 0 && (
        <Card
          title={<span><WarningOutlined style={{ color: '#faad14', marginRight: 8 }} />库存预警（未来7天可用 ≤ 2）</span>}
          style={cardStyle}
        >
          <Table
            columns={[
              { title: '酒店编码', dataIndex: 'hotelCode', key: 'hotelCode', width: 120 },
              { title: '房型编码', dataIndex: 'roomTypeCode', key: 'roomTypeCode', width: 120 },
              { title: '日期', dataIndex: 'date', key: 'date', width: 120 },
              { title: '渠道', dataIndex: 'channelCode', key: 'channelCode', width: 100 },
              {
                title: '可用库存', dataIndex: 'availableRooms', key: 'availableRooms', width: 100,
                render: (val) => <Badge status={val === 0 ? 'error' : 'warning'} text={<Text strong style={{ color: val === 0 ? '#ff4d4f' : '#faad14' }}>{val}</Text>} />
              }
            ]}
            dataSource={inventoryAlerts}
            rowKey={(r, i) => `${r.hotelCode}-${r.roomTypeCode}-${r.date}-${i}`}
            pagination={false}
            size="small"
          />
        </Card>
      )}
    </div>
  )
}

export default GroupDashboard

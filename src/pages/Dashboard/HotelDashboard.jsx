import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Statistic, Table, Tag, List, Spin, Empty, Typography, Badge, Progress, Tooltip, Space, Radio } from 'antd'
import {
  ThunderboltOutlined,
  RiseOutlined,
  LineChartOutlined,
  AlertOutlined,
  DollarOutlined,
  GlobalOutlined,
  HistoryOutlined,
  CalendarOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  ArrowRightOutlined
} from '@ant-design/icons'
import { DualAxes, Pie } from '@ant-design/plots'
import { dashboardApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { useNavigate } from 'react-router-dom'

const { Title, Text } = Typography

const HotelDashboard = () => {
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [trendDays, setTrendDays] = useState('7')
  const { selectedHotel } = useHotelContext()
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
      // 模拟高级经营数据，实际开发中需后端对应适配
      const res = await dashboardApi.getHotelDashboard(hotelCode)
      
      // 注入流速分析模拟数据
      const pacingData = [
        { date: '05-12', occ: 82, velocity: '太快', color: '#ff4d4f', advice: '建议提价 15%' },
        { date: '05-13', occ: 75, velocity: '快', color: '#faad14', advice: '建议提价 5%' },
        { date: '05-14', occ: 68, velocity: '正常', color: '#52c41a', advice: '保持当前策略' },
        { date: '05-15', occ: 62, velocity: '正常', color: '#52c41a', advice: '保持当前策略' },
        { date: '05-16', occ: 45, velocity: '偏慢', color: '#1890ff', advice: '建议开启限时闪购' },
        { date: '05-17', occ: 38, velocity: '非常慢', color: '#722ed1', advice: '建议全渠道大促' },
        { date: '05-18', occ: 55, velocity: '正常', color: '#52c41a', advice: '保持观察' }
      ]

      // 注入渠道矩阵数据
      const channelMatrix = [
        { key: '1', channel: '直销小程序', bookings: 45, revenue: 22500, adr: 500, ratio: 35 },
        { key: '2', channel: '携程', bookings: 32, revenue: 17600, adr: 550, ratio: 28 },
        { key: '3', channel: '美团', bookings: 28, revenue: 12600, adr: 450, ratio: 20 },
        { key: '4', channel: '飞猪', bookings: 15, revenue: 8250, adr: 550, ratio: 12 },
        { key: '5', channel: '其他', bookings: 8, revenue: 3200, adr: 400, ratio: 5 }
      ]

      // 注入 30 天趋势数据
      const fullTrendData = Array.from({ length: 30 }, (_, i) => {
        const date = new Date()
        date.setDate(date.getDate() - (29 - i))
        return {
          date: date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }),
          occ: Math.floor(Math.random() * 40) + 50, // 50-90%
          adr: Math.floor(Math.random() * 200) + 400 // 400-600
        }
      })

      // 异常订单模拟
      const exceptions = [
        { id: 1, type: '超卖风险', detail: '高级大床房库存为 -2', level: 'error', time: '10分钟前' },
        { id: 2, type: '价格异常', detail: '携程成交价 199 低于底价 300', level: 'warning', time: '25分钟前' },
        { id: 3, type: '待确认', detail: '直销订单 R20260512001 未确认', level: 'processing', time: '5分钟前' }
      ]

      setData({ ...res, pacingData, channelMatrix, fullTrendData, exceptions })
    } catch (err) {
      setError('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div style={{ height: '80vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}><Spin size="large" /></div>
  if (error) return <Empty description={error} />

  const { hotelInfo, stats, pacingData, channelMatrix, fullTrendData, exceptions } = data

  // 根据选择的天数截取数据
  const displayTrendData = trendDays === '7' ? fullTrendData.slice(-7) : fullTrendData

  // 趋势图配置 (适配 @ant-design/plots v2)
  const trendConfig = {
    xField: 'date',
    children: [
      {
        data: displayTrendData,
        type: 'interval',
        yField: 'occ',
        style: { fill: '#1890ff', maxWidth: trendDays === '7' ? 40 : 15 },
        axis: { y: { title: '出租率', grid: null } },
      },
      {
        data: displayTrendData,
        type: 'line',
        yField: 'adr',
        style: { stroke: '#eb2f96', lineWidth: 3 },
        axis: { y: { position: 'right', title: '平均房价' } },
        label: trendDays === '7' ? { text: 'adr', position: 'top', style: { dy: -10 } } : false,
      }
    ],
    legend: {
      color: {
        itemMarker: 'circle',
        itemMarkerSize: 8,
      }
    }
  }

  const cardStyle = { borderRadius: 12, border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', background: '#fff' }

  return (
    <div style={{ padding: '0 0 24px 0' }} className="fade-in">
      {/* 顶部标题与核心概览 */}
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <Title level={2} style={{ marginBottom: 4 }}>{hotelInfo?.hotelName} <Tag color="blue" style={{ verticalAlign: 'middle', marginLeft: 8 }}>门店驾驶舱</Tag></Title>
          <Text type="secondary"><GlobalOutlined /> {hotelInfo?.city} · {new Date().toLocaleDateString()} · 实时经营分析中</Text>
        </div>
        <Space size="middle">
          <Statistic title="今日总营收" value={stats?.monthRevenue / 30} precision={2} prefix="¥" valueStyle={{ color: '#cf1322' }} />
          <Statistic title="实时出租率" value={stats?.occupancyRate} suffix="%" valueStyle={{ color: '#3f8600' }} />
        </Space>
      </div>

      <Row gutter={[20, 20]}>
        {/* 1. 未来 7 天流速监测 */}
        <Col span={24}>
          <Card 
            title={<span><ThunderboltOutlined style={{ color: '#faad14' }} /> 未来 7 天流速监测 (Pacing Analysis)</span>}
            style={cardStyle}
          >
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 16 }}>
              {pacingData.map(p => (
                <div key={p.date} style={{ 
                  padding: '16px', 
                  borderRadius: 12, 
                  background: p.color + '0a', 
                  border: `1px solid ${p.color}33`,
                  textAlign: 'center',
                  transition: 'all 0.3s'
                }} className="hover-scale">
                  <Text type="secondary" style={{ fontSize: 12 }}>{p.date}</Text>
                  <div style={{ margin: '8px 0' }}>
                    <Text strong style={{ fontSize: 24, display: 'block' }}>{p.occ}%</Text>
                    <Tag color={p.color}>{p.velocity}</Tag>
                  </div>
                  <Tooltip title={p.advice}>
                    <div style={{ fontSize: 11, color: '#8c8c8c' }}><RiseOutlined /> 预测建议</div>
                  </Tooltip>
                </div>
              ))}
            </div>
          </Card>
        </Col>

        {/* 2. 经营趋势双轴透视 */}
        <Col span={16}>
          <Card 
            title={<span><LineChartOutlined style={{ color: '#1890ff' }} /> 经营趋势透视 (OCC & ADR)</span>}
            extra={
              <Radio.Group 
                value={trendDays} 
                onChange={e => setTrendDays(e.target.value)} 
                size="small"
              >
                <Radio.Button value="7">近7天</Radio.Button>
                <Radio.Button value="30">近30天</Radio.Button>
              </Radio.Group>
            }
            style={cardStyle}
          >
            <div style={{ height: 350 }}>
              <DualAxes {...trendConfig} />
            </div>
          </Card>
        </Col>

        {/* 3. 异常监控中心 */}
        <Col span={8}>
          <Card 
            title={<span><AlertOutlined style={{ color: '#ff4d4f' }} /> 智能预警中心</span>}
            extra={<Text type="link" style={{ fontSize: 12 }}>处理全部 <ArrowRightOutlined /></Text>}
            style={{ ...cardStyle, height: '100%' }}
          >
            <List
              itemLayout="horizontal"
              dataSource={exceptions}
              renderItem={item => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<Badge status={item.level} />}
                    title={<Text strong style={{ fontSize: 13 }}>{item.type}</Text>}
                    description={
                      <div>
                        <div style={{ fontSize: 12, color: '#595959' }}>{item.detail}</div>
                        <div style={{ fontSize: 11, color: '#bfbfbf', marginTop: 4 }}><HistoryOutlined /> {item.time}</div>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>

        {/* 4. 渠道全量价值矩阵 */}
        <Col span={24}>
          <Card 
            title={<span><GlobalOutlined style={{ color: '#52c41a' }} /> 渠道全量价值矩阵</span>}
            style={cardStyle}
          >
            <Row gutter={24}>
              <Col span={16}>
                <Table 
                  dataSource={channelMatrix} 
                  pagination={false} 
                  size="middle"
                  columns={[
                    { title: '渠道', dataIndex: 'channel', key: 'channel', render: t => <Text strong>{t}</Text> },
                    { title: '预订量', dataIndex: 'bookings', key: 'bookings', sorter: (a, b) => a.bookings - b.bookings },
                    { title: '预订金额', dataIndex: 'revenue', key: 'revenue', render: v => `¥${v.toLocaleString()}`, sorter: (a, b) => a.revenue - b.revenue },
                    { title: '平均房价 (ADR)', dataIndex: 'adr', key: 'adr', render: v => `¥${v}` },
                    { title: '营收占比', dataIndex: 'ratio', key: 'ratio', render: v => <Progress percent={v} size="small" strokeColor="#52c41a" /> }
                  ]}
                />
              </Col>
              <Col span={8} style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <div style={{ height: 240, width: '100%' }}>
                  <Pie 
                    data={channelMatrix}
                    angleField="revenue"
                    colorField="channel"
                    radius={0.7}
                    innerRadius={0.4}
                    label={{
                      text: 'channel',
                      position: 'outside',
                      connector: true,
                    }}
                    legend={false}
                  />
                </div>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>

      <style jsx>{`
        .fade-in { animation: fadeIn 0.8s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
        .hover-scale:hover { transform: translateY(-5px); box-shadow: 0 8px 24px rgba(0,0,0,0.08); }
      `}</style>
    </div>
  )
}

export default HotelDashboard

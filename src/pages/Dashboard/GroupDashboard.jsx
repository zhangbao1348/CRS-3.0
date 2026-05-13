import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Statistic, Table, Tag, List, Spin, Empty, Badge, Typography, Tooltip, Space, Radio, Progress, Avatar } from 'antd'
import {
  BankOutlined,
  ThunderboltOutlined,
  LineChartOutlined,
  AlertOutlined,
  DollarOutlined,
  TrophyOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  GlobalOutlined,
  TeamOutlined,
  ArrowRightOutlined,
  HistoryOutlined
} from '@ant-design/icons'
import { Area, Pie } from '@ant-design/plots'
import { dashboardApi } from '../../utils/api'
import { useNavigate } from 'react-router-dom'
import { useHotelContext } from '../../contexts/HotelContext'

const { Title, Text } = Typography

const GroupDashboard = () => {
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [trendDays, setTrendDays] = useState('7')
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
      
      // 注入集团排行榜模拟数据
      const hotelRanking = [
        { key: '1', name: '上海静安中心店', revenue: 1250000, occ: 92, rank: 1 },
        { key: '2', name: '北京国贸大酒店', revenue: 980000, occ: 85, rank: 2 },
        { key: '3', name: '深圳南山旗舰店', revenue: 860000, occ: 78, rank: 3 },
        { key: '4', name: '广州天河店', revenue: 720000, occ: 75, rank: 4 },
        { key: '5', name: '成都春熙路店', revenue: 650000, occ: 88, rank: 5 }
      ]

      // 注入全域流速模拟
      const groupPacingData = [
        { date: '05-13', avgOcc: 78, velocity: '快', color: '#faad14' },
        { date: '05-14', avgOcc: 72, velocity: '正常', color: '#52c41a' },
        { date: '05-15', avgOcc: 65, velocity: '正常', color: '#52c41a' },
        { date: '05-16', avgOcc: 85, velocity: '极快', color: '#ff4d4f' },
        { date: '05-17', avgOcc: 92, velocity: '售罄风险', color: '#ff4d4f' },
        { date: '05-18', avgOcc: 80, velocity: '快', color: '#faad14' },
        { date: '05-19', avgOcc: 60, velocity: '正常', color: '#52c41a' }
      ]

      // 注入 30 天全域趋势数据
      const fullTrendData = Array.from({ length: 30 }, (_, i) => {
        const date = new Date()
        date.setDate(date.getDate() - (29 - i))
        return {
          date: date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }),
          orders: Math.floor(Math.random() * 200) + 500,
          revenue: Math.floor(Math.random() * 50000) + 150000
        }
      })

      // 跨店异常预警模拟
      const groupExceptions = [
        { id: 1, hotel: '北京国贸店', type: '库存超卖', detail: '未来 3 天豪华套房库存 -5', level: 'error', time: '5分钟前' },
        { id: 2, hotel: '上海静安店', type: '待确认订单', detail: '25 笔渠道订单等待同步', level: 'warning', time: '12分钟前' },
        { id: 3, hotel: '深圳南山店', type: '价格倒挂', detail: '美团价格显著低于直销', level: 'warning', time: '45分钟前' }
      ]

      setData({ ...res, hotelRanking, groupPacingData, fullTrendData, groupExceptions })
    } catch (err) {
      setError('加载集团数据失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div style={{ height: '80vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}><Spin size="large" /></div>
  if (error) return <Empty description={error} />

  const { stats, hotelRanking, groupPacingData, fullTrendData, groupExceptions, hotelOverview = [] } = data

  const displayTrendData = trendDays === '7' ? fullTrendData.slice(-7) : fullTrendData

  // 全域趋势图配置 (v2)
  const trendConfig = {
    data: displayTrendData,
    xField: 'date',
    yField: 'revenue',
    smooth: true,
    style: {
      fill: 'linear-gradient(-90deg, white 0%, #1890ff 100%)',
      fillOpacity: 0.2,
      lineWidth: 3,
      stroke: '#1890ff'
    },
    axis: { y: { label: { formatter: (v) => `¥${(v / 10000).toFixed(1)}w` } } },
  }

  const cardStyle = { borderRadius: 12, border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', background: '#fff' }

  return (
    <div style={{ padding: '0 0 24px 0' }} className="fade-in">
      {/* 集团头部信息 */}
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <Title level={2} style={{ marginBottom: 4 }}>集团全域驾驶舱 <Tag color="purple" style={{ verticalAlign: 'middle', marginLeft: 8 }}>全量监控</Tag></Title>
          <Text type="secondary"><BankOutlined /> 旗下共 {stats?.activeHotelCount} 家活跃门店 · 全域实时数据集成中</Text>
        </div>
        <Space size="large">
          <Statistic title="全域本月总营收" value={stats?.monthRevenue} precision={2} prefix="¥" valueStyle={{ color: '#cf1322', fontSize: 28 }} />
          <Statistic title="今日总订单" value={stats?.todayNewOrders} valueStyle={{ color: '#1890ff' }} suffix={<Text type="secondary" style={{ fontSize: 14 }}>+12% <ArrowUpOutlined style={{ color: '#52c41a' }} /></Text>} />
        </Space>
      </div>

      <Row gutter={[20, 20]}>
        {/* 1. 核心 KPI 矩阵 */}
        <Col span={24}>
          <Row gutter={16}>
            {[
              { title: '今日全域入住', value: stats?.todayCheckIn, icon: <TeamOutlined />, color: '#52c41a' },
              { title: '全域平均出租率', value: 78.5, icon: <LineChartOutlined />, color: '#1890ff', suffix: '%' },
              { title: '全域平均 ADR', value: 520, icon: <DollarOutlined />, color: '#faad14', prefix: '¥' },
              { title: '待处理异常', value: groupExceptions.length, icon: <AlertOutlined />, color: '#ff4d4f' }
            ].map((kpi, idx) => (
              <Col span={6} key={idx}>
                <Card style={cardStyle} bodyStyle={{ padding: '20px' }}>
                  <Statistic 
                    title={kpi.title} 
                    value={kpi.value} 
                    prefix={kpi.prefix} 
                    suffix={kpi.suffix} 
                    valueStyle={{ color: kpi.color, fontWeight: 'bold' }} 
                  />
                </Card>
              </Col>
            ))}
          </Row>
        </Col>

        {/* 2. 全域预订趋势 */}
        <Col span={16}>
          <Card 
            title={<span><LineChartOutlined style={{ color: '#1890ff' }} /> 全域预订金额趋势</span>}
            extra={
              <Radio.Group value={trendDays} onChange={e => setTrendDays(e.target.value)} size="small">
                <Radio.Button value="7">近7天</Radio.Button>
                <Radio.Button value="30">近30天</Radio.Button>
              </Radio.Group>
            }
            style={cardStyle}
          >
            <div style={{ height: 350 }}>
              <Area {...trendConfig} />
            </div>
          </Card>
        </Col>

        {/* 3. 酒店业绩巅峰榜 */}
        <Col span={8}>
          <Card 
            title={<span><TrophyOutlined style={{ color: '#faad14' }} /> 酒店业绩贡献榜 (本月)</span>}
            style={{ ...cardStyle, height: '100%' }}
          >
            <List
              dataSource={hotelRanking}
              renderItem={item => (
                <List.Item style={{ padding: '12px 0' }}>
                  <div style={{ display: 'flex', alignItems: 'center', width: '100%', gap: 12 }}>
                    <Avatar size="small" style={{ backgroundColor: item.rank <= 3 ? '#faad14' : '#f0f0f0', color: item.rank <= 3 ? '#fff' : '#8c8c8c' }}>{item.rank}</Avatar>
                    <div style={{ flex: 1 }}>
                      <Text strong style={{ fontSize: 13 }}>{item.name}</Text>
                      <div style={{ fontSize: 11, color: '#8c8c8c' }}>出租率 {item.occ}%</div>
                    </div>
                    <Text strong style={{ color: '#cf1322' }}>¥{(item.revenue / 10000).toFixed(1)}w</Text>
                  </div>
                </List.Item>
              )}
            />
          </Card>
        </Col>

        {/* 4. 集团全域流速监测 */}
        <Col span={24}>
          <Card 
            title={<span><ThunderboltOutlined style={{ color: '#faad14' }} /> 集团全域流速监测 (未来 7 天平均)</span>}
            style={cardStyle}
          >
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 16 }}>
              {groupPacingData.map(p => (
                <div key={p.date} style={{ 
                  padding: '16px', 
                  borderRadius: 12, 
                  background: p.color + '0a', 
                  border: `1px solid ${p.color}33`,
                  textAlign: 'center'
                }} className="hover-scale">
                  <Text type="secondary" style={{ fontSize: 12 }}>{p.date}</Text>
                  <div style={{ margin: '8px 0' }}>
                    <Text strong style={{ fontSize: 20, display: 'block' }}>{p.avgOcc}%</Text>
                    <Tag color={p.color} style={{ margin: 0 }}>{p.velocity}</Tag>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </Col>

        {/* 5. 跨店异常监控中心 */}
        <Col span={10}>
          <Card 
            title={<span><AlertOutlined style={{ color: '#ff4d4f' }} /> 集团异常监控中心</span>}
            style={{ ...cardStyle, height: '100%' }}
          >
            <List
              dataSource={groupExceptions}
              renderItem={item => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<Badge status={item.level} />}
                    title={<Space><Text type="secondary" style={{ fontSize: 12 }}>[{item.hotel}]</Text><Text strong>{item.type}</Text></Space>}
                    description={
                      <div>
                        <div style={{ fontSize: 12 }}>{item.detail}</div>
                        <div style={{ fontSize: 11, color: '#bfbfbf', marginTop: 4 }}><HistoryOutlined /> {item.time}</div>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>

        {/* 6. 旗下酒店运营矩阵 */}
        <Col span={14}>
          <Card title={<span><GlobalOutlined style={{ color: '#1890ff' }} /> 旗下酒店运营实时矩阵</span>} style={cardStyle}>
            <Table
              dataSource={hotelOverview}
              pagination={false}
              size="middle"
              columns={[
                { title: '酒店名称', dataIndex: 'hotelName', key: 'hotelName', render: (t, r) => <a onClick={() => { changeHotel(r.hotelCode); navigate('/dashboard'); }}>{t}</a> },
                { title: '今日到客', dataIndex: 'todayCheckIn', key: 'todayCheckIn' },
                { title: '剩余库存', dataIndex: 'todayAvailableRooms', key: 'todayAvailableRooms', render: v => <Text strong style={{ color: v <= 2 ? '#ff4d4f' : 'inherit' }}>{v}</Text> },
                { title: '本月营收', dataIndex: 'monthRevenue', key: 'monthRevenue', render: v => `¥${(v / 10000).toFixed(1)}w` },
                { title: '状态', key: 'status', render: () => <Tag color="green">正常</Tag> }
              ]}
            />
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

export default GroupDashboard

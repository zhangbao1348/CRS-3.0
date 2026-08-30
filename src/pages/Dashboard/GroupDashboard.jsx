import { useState, useEffect } from 'react'
import { Card, Row, Col, Statistic, Table, Tag, List, Spin, Empty, Badge, Typography, Space, Radio, Avatar, Select } from 'antd'
import {
  BankOutlined,
  ThunderboltOutlined,
  LineChartOutlined,
  AlertOutlined,
  TrophyOutlined,
  GlobalOutlined,
  TeamOutlined,
  ApartmentOutlined
} from '@ant-design/icons'
import { Area } from '@ant-design/plots'
import { dashboardApi } from '../../utils/api'
import { useNavigate } from 'react-router-dom'
import { useHotelContext } from '../../contexts/HotelContext'
import { useTenantContext } from '../../contexts/TenantContext'

const { Title, Text } = Typography
const { Option } = Select

const GroupDashboard = () => {
  const [loading, setLoading] = useState(true)
  const [pacingLoading, setPacingLoading] = useState(false)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [trendDays, setTrendDays] = useState('7')
  const [selectedPacingHotel, setSelectedPacingHotel] = useState(null)
  const navigate = useNavigate()
  const { changeHotel } = useHotelContext()
  const { selectedTenant, loading: tenantLoading } = useTenantContext()

  useEffect(() => {
    if (selectedTenant) {
      fetchData()
    }
  }, [selectedTenant])

  const fetchData = async (hotelCode = null) => {
    if (hotelCode) setPacingLoading(true)
    else setLoading(true)
    
    setError(null)
    try {
      const res = await dashboardApi.getGroupDashboard(hotelCode)
      const payload = res?.data || res || {}

      if (hotelCode) {
        // 仅更新流速数据
        setData(prev => ({ ...prev, groupPacing: payload.groupPacing || [] }))
      } else {
        setData(payload)
      }
    } catch (err) {
      setError('加载集团数据失败')
    } finally {
      setLoading(false)
      setPacingLoading(false)
    }
  }

  const handlePacingHotelChange = (val) => {
    setSelectedPacingHotel(val)
    fetchData(val)
  }

  if (loading || tenantLoading || !selectedTenant) return <div style={{ height: '80vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}><Spin size="large" /></div>
  if (error) return <Empty description={error} />

  const {
    stats = {},
    groupPacing = [],
    bookingTrend = [],
    inventoryAlerts = [],
    hotelOverview = []
  } = data || {}

  const hotelRanking = [...hotelOverview]
    .sort((a, b) => Number(b.monthRevenue || 0) - Number(a.monthRevenue || 0))
    .slice(0, 5)
    .map((hotel, index) => ({ ...hotel, rank: index + 1 }))
  const groupExceptions = inventoryAlerts.map((item, index) => ({
    id: `${item.hotelCode}-${item.roomTypeCode}-${item.date}-${index}`,
    hotel: item.hotelCode,
    type: '低库存预警',
    detail: `${item.date} ${item.roomTypeCode} 剩余 ${item.availableRooms ?? 0} 间`,
    level: Number(item.availableRooms) <= 0 ? 'error' : 'warning'
  }))

  const displayTrendData = trendDays === '7' ? bookingTrend.slice(-7) : bookingTrend

  // 全域趋势图配置 (v2)
  const trendConfig = {
    data: displayTrendData,
    xField: 'date',
    yField: 'count',
    smooth: true,
    style: {
      fill: 'linear-gradient(-90deg, white 0%, #1890ff 100%)',
      fillOpacity: 0.2,
      lineWidth: 3,
      stroke: '#1890ff'
    },
    axis: { y: { label: { formatter: (v) => `${v} 单` } } },
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
          <Statistic title="今日总订单" value={stats.todayNewOrders || 0} valueStyle={{ color: '#1890ff' }} />
        </Space>
      </div>

      <Row gutter={[20, 20]}>
        {/* 1. 核心 KPI 矩阵 */}
        <Col span={24}>
          <Row gutter={16}>
            {[
              { title: '今日全域入住', value: stats.todayCheckIn || 0, icon: <TeamOutlined />, color: '#52c41a' },
              { title: '活跃门店', value: stats.activeHotelCount || 0, icon: <ApartmentOutlined />, color: '#1890ff' },
              { title: '待处理人工订单', value: stats.pendingManual || 0, icon: <AlertOutlined />, color: '#faad14' },
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
            title={<span><LineChartOutlined style={{ color: '#1890ff' }} /> 全域订单趋势</span>}
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
                    <div style={{ fontSize: 11, color: '#8c8c8c' }}>剩余库存 {item.todayAvailableRooms ?? 0} 间</div>
                  </div>
                    <Text strong style={{ color: '#cf1322' }}>¥{(Number(item.monthRevenue || 0) / 10000).toFixed(1)}w</Text>
                  </div>
                </List.Item>
              )}
            />
          </Card>
        </Col>

        {/* 4. 集团流速监控 (支持酒店下钻) */}
        <Col span={24}>
          <Card 
            title={<span><ThunderboltOutlined style={{ color: '#faad14' }} /> 集团全域流速监测 (未来 7 天)</span>}
            extra={
              <Select 
                placeholder="选择酒店" 
                style={{ width: 200 }} 
                allowClear 
                onChange={handlePacingHotelChange}
                value={selectedPacingHotel}
              >
                <Option value="">集团全域平均</Option>
                {hotelOverview.map(h => (
                  <Option key={h.hotelCode} value={h.hotelCode}>{h.hotelName}</Option>
                ))}
              </Select>
            }
            style={cardStyle}
          >
            <Spin spinning={pacingLoading}>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 16 }}>
                {groupPacing.length > 0 ? groupPacing.map(p => (
                  <div key={p.date} style={{ 
                    padding: '16px', 
                    borderRadius: 12, 
                    background: (p.color || '#1890ff') + '0a', 
                    border: `1px solid ${(p.color || '#1890ff')}33`,
                    textAlign: 'center'
                  }} className="hover-scale">
                    <Text type="secondary" style={{ fontSize: 12 }}>{p.date}</Text>
                    <div style={{ margin: '8px 0' }}>
                      <Text strong style={{ fontSize: 20, display: 'block' }}>{p.avgOcc}%</Text>
                      <Tag color={p.color} style={{ margin: 0 }}>{p.velocity}</Tag>
                    </div>
                    {p.pickup !== undefined && (
                      <div style={{ fontSize: 11, color: '#8c8c8c', marginTop: 4 }}>
                        <ThunderboltOutlined /> {p.pickup} 笔预订
                      </div>
                    )}
                  </div>
                )) : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暂无流速数据" />}
              </div>
            </Spin>
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
                        <div style={{ fontSize: 11, color: '#bfbfbf', marginTop: 4 }}>{item.hotel}</div>
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

      <style>{`
        .fade-in { animation: fadeIn 0.8s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
        .hover-scale:hover { transform: translateY(-5px); box-shadow: 0 8px 24px rgba(0,0,0,0.08); }
      `}</style>
    </div>
  )
}

export default GroupDashboard

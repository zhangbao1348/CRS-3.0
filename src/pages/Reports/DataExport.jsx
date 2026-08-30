import { Button, Card, Col, Row, Space, Tag, Typography } from 'antd'
import { BarChartOutlined, DownloadOutlined, HomeOutlined, LineChartOutlined, RightOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Title, Paragraph, Text } = Typography

const exportTypes = [
  {
    key: 'reservation',
    title: '订单分析报表',
    description: '按酒店、渠道、市场码、房价大类和订单状态筛选，支持同环比后导出当前查询结果。',
    formats: 'XLSX / CSV',
    path: '/reports/reservation-reports',
    icon: <BarChartOutlined />
  },
  {
    key: 'occupancy',
    title: '出租率报表',
    description: '按月份、酒店或房型查看物理房量、维修房、已售房和出租率，并导出当前矩阵。',
    formats: 'XLSX / CSV',
    path: '/reports/occupancy-reports',
    icon: <HomeOutlined />
  },
  {
    key: 'revenue',
    title: '营收分析报表',
    description: '按月份、酒店或房型查看订单数、间夜和平均房价，并导出当前矩阵。',
    formats: 'XLSX / CSV',
    path: '/reports/revenue-reports',
    icon: <LineChartOutlined />
  }
]

/** 报表导出的独立工作台，不再复用订单报表路由冒充导出页面。 */
const DataExport = () => {
  const navigate = useNavigate()

  return (
    <div className="fade-in">
      <Space direction="vertical" size={4} style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}><DownloadOutlined /> 数据导出</Title>
        <Text type="secondary">先进入对应报表完成有界查询，再导出当前结果；在线订单查询最长 366 天。</Text>
      </Space>
      <Row gutter={[20, 20]}>
        {exportTypes.map(item => (
          <Col xs={24} lg={8} key={item.key}>
            <Card
              hoverable
              style={{ height: '100%', borderRadius: 12 }}
              title={<Space>{item.icon}<span>{item.title}</span></Space>}
              extra={<Tag color="blue">{item.formats}</Tag>}
            >
              <Paragraph type="secondary" style={{ minHeight: 66 }}>{item.description}</Paragraph>
              <Button type="primary" onClick={() => navigate(item.path)}>
                进入查询与导出 <RightOutlined />
              </Button>
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  )
}

export default DataExport

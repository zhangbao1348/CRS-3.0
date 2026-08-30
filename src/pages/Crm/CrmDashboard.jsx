import { Card, Row, Col, Statistic } from 'antd'
import { UserOutlined, MessageOutlined, GiftOutlined, BarChartOutlined } from '@ant-design/icons'

const CrmDashboard = () => {
  return (
    <div className="fade-in">
      <h1 className="page-title">CRM系统首页</h1>
      
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总客户数"
              value={12580}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="会员数"
              value={3250}
              prefix={<GiftOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="待处理咨询"
              value={86}
              prefix={<MessageOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="本月营销活动"
              value={12}
              prefix={<BarChartOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Card title="最近客户" style={{ height: 400 }}>
            <div>客户数据列表...</div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="待跟进任务" style={{ height: 400 }}>
            <div>任务列表...</div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default CrmDashboard

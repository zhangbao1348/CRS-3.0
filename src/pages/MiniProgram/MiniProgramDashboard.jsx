import { Card, Row, Col } from 'antd'
import { 
  CalendarOutlined, 
  HomeOutlined, 
  GiftOutlined,
  BuildOutlined
} from '@ant-design/icons'

const MiniProgramDashboard = () => {
  const quickActions = [
    { title: '在线预订', icon: <CalendarOutlined />, path: '/mini-program/reservation/booking' },
    { title: '我的预订', icon: <CalendarOutlined />, path: '/mini-program/reservation/my-reservations' },
    { title: '房间列表', icon: <HomeOutlined />, path: '/mini-program/room-info/room-list' },
    { title: '我的积分', icon: <GiftOutlined />, path: '/mini-program/personal-center/points' },
    { title: '优惠券', icon: <GiftOutlined />, path: '/mini-program/personal-center/coupon' },
    { title: '酒店详情', icon: <BuildOutlined />, path: '/mini-program/hotel-info/detail' }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">小程序系统首页</h1>
      
      <Card title="快捷入口" style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          {quickActions.map((action, index) => (
            <Col span={8} key={index}>
              <Card
                hoverable
                style={{ textAlign: 'center', cursor: 'pointer' }}
              >
                <div style={{ fontSize: 32, marginBottom: 8, color: '#1890ff' }}>
                  {action.icon}
                </div>
                <div style={{ fontSize: 14 }}>{action.title}</div>
              </Card>
            </Col>
          ))}
        </Row>
      </Card>

      <Row gutter={16}>
        <Col span={12}>
          <Card title="推荐房间" style={{ height: 400 }}>
            <div>房间列表展示...</div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="最新活动" style={{ height: 400 }}>
            <div>活动列表展示...</div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default MiniProgramDashboard

import { useContext, useEffect, useState } from 'react'
import {
  App as AntApp,
  Badge,
  Button,
  Card,
  Checkbox,
  Divider,
  Form,
  Input,
  Spin,
  Tag,
} from 'antd'
import {
  ApartmentOutlined,
  ArrowRightOutlined,
  AuditOutlined,
  LockOutlined,
  SafetyCertificateOutlined,
  UserOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { AuthContext } from '../../contexts/AuthContext'
import './Login.css'

const Login = () => {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { message } = AntApp.useApp()
  const { login, isAuthenticated, loading: authLoading } = useContext(AuthContext)

  // 关联模块：AuthContext。已登录用户无需重复认证，直接进入工作台。
  useEffect(() => {
    if (!authLoading && isAuthenticated) {
      navigate('/')
    }
  }, [isAuthenticated, authLoading, navigate])

  const onFinish = async (values) => {
    setLoading(true)
    try {
      await login(values.username, values.password, values.remember)
      message.success('登录成功')
      navigate('/')
    } catch (error) {
      message.error(error.message || '登录失败')
    } finally {
      setLoading(false)
    }
  }

  if (authLoading) {
    return (
      <main className="login-page" aria-label="认证加载中">
        <Card className="login-loading-card" variant="borderless">
          <Spin size="large">
            <span className="login-loading-label">正在验证登录状态</span>
          </Spin>
        </Card>
      </main>
    )
  }

  return (
    <main className="login-page">
      <section className="login-workspace" aria-label="中央预订系统登录">
        <aside className="login-brief" aria-labelledby="login-brief-title">
          <div className="login-brand-lockup">
            <span className="login-brand-mark" aria-hidden="true">
              <ApartmentOutlined />
            </span>
            <span>
              <strong>CRS CONTROL ROOM</strong>
              <small>HOTEL COMMERCE PLATFORM</small>
            </span>
          </div>

          <div className="login-brief-copy">
            <Tag className="login-eyebrow" bordered={false}>经营中枢 · 安全接入</Tag>
            <h1 id="login-brief-title">让每一次入住，<br />都运营得更确定。</h1>
            <p>
              面向酒店集团与门店的预订、库存、价格和渠道协同工作台。
              <span>Central Reservation System</span>
            </p>
          </div>

          <Card className="login-capability-card" variant="borderless">
            <div className="login-capability-heading">
              <span>运营能力</span>
              <Badge status="success" text="服务状态正常" />
            </div>
            <div className="login-capability-list">
              <div>
                <AuditOutlined aria-hidden="true" />
                <span><strong>统一预订</strong><small>覆盖集团与门店的订单协同</small></span>
              </div>
              <div>
                <SafetyCertificateOutlined aria-hidden="true" />
                <span><strong>安全治理</strong><small>按角色与租户隔离业务数据</small></span>
              </div>
            </div>
          </Card>

          <p className="login-brief-footer">© CRS Hospitality Operations</p>
        </aside>

        <section className="login-auth-panel" aria-labelledby="login-title">
          <div className="login-mobile-lockup" aria-hidden="true">
            <ApartmentOutlined />
            <span>CRS CONTROL ROOM</span>
          </div>

          <Card className="login-auth-card" variant="borderless">
            <Badge color="#0f766e" text="安全工作台" className="login-security-badge" />
            <header className="login-auth-header">
              <h2 id="login-title">欢迎回来</h2>
              <p>使用您的工作账号进入中央预订系统。</p>
            </header>

            <Form
              name="login"
              layout="vertical"
              initialValues={{ remember: true }}
              onFinish={onFinish}
              size="large"
              className="login-form"
            >
              <Form.Item
                name="username"
                label="登录账号"
                rules={[{ required: true, message: '请输入登录账号' }]}
              >
                <Input
                  prefix={<UserOutlined aria-hidden="true" />}
                  placeholder="请输入账号"
                  autoComplete="username"
                  autoFocus
                />
              </Form.Item>

              <Form.Item
                name="password"
                label="登录密码"
                rules={[{ required: true, message: '请输入登录密码' }]}
              >
                <Input.Password
                  prefix={<LockOutlined aria-hidden="true" />}
                  placeholder="请输入密码"
                  autoComplete="current-password"
                />
              </Form.Item>

              <Form.Item name="remember" valuePropName="checked" className="login-remember-item">
                <Checkbox>在此设备上保持登录</Checkbox>
              </Form.Item>

              <Form.Item className="login-submit-item">
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  block
                  className="login-submit-button"
                >
                  <span>进入工作台</span>
                  <ArrowRightOutlined aria-hidden="true" />
                </Button>
              </Form.Item>
            </Form>

            {import.meta.env.DEV && (
              <>
                <Divider className="login-divider" />
                <div className="login-demo-note" role="note">
                  <SafetyCertificateOutlined aria-hidden="true" />
                  <span>本地开发账号</span>
                  <strong>admin / admin123</strong>
                </div>
              </>
            )}
          </Card>
          <p className="login-auth-footer">如无法登录，请联系系统管理员核验账号权限。</p>
        </section>
      </section>
    </main>
  )
}

export default Login

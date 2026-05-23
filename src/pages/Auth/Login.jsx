import { useState, useContext, useEffect } from 'react'
import { Form, Input, Button, Card, Checkbox, message } from 'antd'
import { UserOutlined, LockOutlined, SafetyCertificateOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { AuthContext } from '../../contexts/AuthContext'
import './Login.css'

const Login = () => {
  const [loading, setLoading] = useState(false)
  const [rememberMe, setRememberMe] = useState(false)
  const navigate = useNavigate()
  const { login, isAuthenticated, loading: authLoading } = useContext(AuthContext)
  
  // 如果用户已经登录，自动跳转到首页
  useEffect(() => {
    console.log('Login组件 - isAuthenticated:', isAuthenticated)
    console.log('Login组件 - authLoading:', authLoading)
    if (!authLoading && isAuthenticated) {
      console.log('用户已登录，跳转到首页')
      navigate('/')
    }
  }, [isAuthenticated, authLoading, navigate])
  
  const onFinish = async (values) => {
    setLoading(true)
    try {
      await login(values.username, values.password, rememberMe)
      message.success('登录成功')
      navigate('/')
    } catch (error) {
      message.error(error.message || '登录失败')
    } finally {
      setLoading(false)
    }
  }
  
  // 显示加载状态
  if (authLoading) {
    return (
      <div className="login-container">
        <Card className="login-card">
          <div className="login-loading">
            加载中...
          </div>
        </Card>
      </div>
    )
  }
  
  return (
    <div className="login-container">
      <Card className="login-card" variant="borderless">
        <div className="login-badge">
          <SafetyCertificateOutlined />
          <span>Hotel Commerce Control Center</span>
        </div>

        <div className="login-header">
          <h1>中央预订系统</h1>
          <p>面向集团与门店的统一预订、库存、价格与渠道协同平台</p>
          <span className="login-subtitle">Central Reservation System</span>
        </div>

        <div className="login-panel-caption">
          <span>安全登录</span>
          <span>实时协同</span>
          <span>集团级运营</span>
        </div>

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
            label="用户名"
            rules={[{ required: true, message: '请输入用户名!' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="请输入登录账号"
              autoComplete="username"
            />
          </Form.Item>

          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: true, message: '请输入密码!' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请输入登录密码"
              autoComplete="current-password"
            />
          </Form.Item>

          <div className="login-actions-row">
            <Form.Item name="remember" valuePropName="checked" className="login-remember-item">
              <Checkbox
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
              >
                记住我
              </Checkbox>
            </Form.Item>
            <span className="login-hint">建议在个人设备上启用</span>
          </div>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              size="large"
              className="login-submit-button"
            >
              登录
            </Button>
          </Form.Item>
        </Form>

        <div className="login-footer">
          <div className="login-demo-account">
            <span>演示账号</span>
            <strong>admin / admin123</strong>
          </div>
          <div className="login-feature-list">
            <span>多酒店</span>
            <span>库存协同</span>
            <span>价格联动</span>
          </div>
        </div>
      </Card>
    </div>
  )
}

export default Login

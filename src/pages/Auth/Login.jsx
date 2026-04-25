import React, { useState, useContext, useEffect } from 'react'
import { Form, Input, Button, Card, Checkbox, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
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
          <div style={{ textAlign: 'center', padding: '40px' }}>
            加载中...
          </div>
        </Card>
      </div>
    )
  }
  
  return (
    <div className="login-container">
      <Card className="login-card">
        <div className="login-header">
          <h1>中央预订系统</h1>
          <p>Central Reservation System</p>
        </div>
        
        <Form
          name="login"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名!' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
              autoComplete="username"
            />
          </Form.Item>
          
          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码!' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
              autoComplete="current-password"
            />
          </Form.Item>
          
          <Form.Item name="remember" valuePropName="checked">
            <Checkbox
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
            >
              记住我
            </Checkbox>
          </Form.Item>
          
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              size="large"
            >
              登录
            </Button>
          </Form.Item>
        </Form>
        
        <div className="login-footer">
          <p>默认账号：admin / admin123</p>
        </div>
      </Card>
    </div>
  )
}

export default Login

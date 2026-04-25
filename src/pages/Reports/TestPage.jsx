import React from 'react'
import { Card, Typography } from 'antd'

const { Title, Text } = Typography

const TestPage = () => {
  return (
    <div className="fade-in">
      <h1 className="page-title">
        测试页面
      </h1>
      
      <Card>
        <div style={{ marginBottom: 24 }}>
          <Title level={5}>测试页面</Title>
          <Text>这是一个测试页面，用于检查报表路由是否正常工作。</Text>
        </div>
      </Card>
    </div>
  )
}

export default TestPage
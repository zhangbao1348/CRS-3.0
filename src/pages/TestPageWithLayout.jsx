import React from 'react'
import MainLayout from '../components/Layout/MainLayout'

const TestPageWithLayout = () => {
  return (
    <MainLayout>
      <div style={{ padding: '20px' }}>
        <h1>测试页面（带布局）</h1>
        <p>这是一个测试页面，用于测试MainLayout是否正常工作。</p>
      </div>
    </MainLayout>
  )
}

export default TestPageWithLayout
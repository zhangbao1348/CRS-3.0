import React from 'react'
import { Typography } from 'antd'
import { GiftOutlined } from '@ant-design/icons'

const { Title } = Typography

const PackageSetting = () => {
  return (
    <div className="fade-in">
      <h1 className="page-title">
        <GiftOutlined />
        包价设置
      </h1>
      <div style={{ textAlign: 'center', padding: '50px 0', color: '#999' }}>
        包价设置页面内容
      </div>
    </div>
  )
}

export default PackageSetting
import React, { useState } from 'react'
import { Card, Tabs, Table, Select, Input, Row, Col, Tag } from 'antd'
import { LinkOutlined } from '@ant-design/icons'

const { Option } = Select
const { TabPane } = Tabs

// 模拟渠道数据
const channels = [
  { id: 1, name: '飞猪', code: 'FLIGGY' },
  { id: 2, name: '红色动力', code: 'RED_POWER' },
  { id: 3, name: '美团', code: 'MEITUAN' },
  { id: 4, name: '携程', code: 'CTRIP' },
  { id: 5, name: 'Test Channel', code: 'TEST' }
]

// 模拟房型数据
const roomTypes = [
  { id: 1, name: '标准大床房', code: 'STD_KING' },
  { id: 2, name: '豪华大床房', code: 'DELUXE_KING' },
  { id: 3, name: '标准双床房', code: 'STD_TWIN' },
  { id: 4, name: '豪华双床房', code: 'DELUXE_TWIN' },
  { id: 5, name: '行政大床房', code: 'EXECUTIVE_KING' }
]

// 模拟房价码数据
const rateCodes = [
  { id: 1, name: 'RACK', code: 'RACK' },
  { id: 2, name: 'NET', code: 'NET' },
  { id: 3, name: 'CORP', code: 'CORP' },
  { id: 4, name: 'OTA', code: 'OTA' }
]

// 模拟渠道映射数据
const channelMappings = [
  {
    id: 1,
    channelId: 1,
    channelName: '飞猪',
    channelRoomType: 'Fliggy King',
    hotelRoomType: '标准大床房',
    status: 'active',
    updatedAt: '2025-12-15 10:30:00'
  },
  {
    id: 2,
    channelId: 1,
    channelName: '飞猪',
    channelRoomType: 'Fliggy Twin',
    hotelRoomType: '标准双床房',
    status: 'active',
    updatedAt: '2025-12-15 10:30:00'
  },
  {
    id: 3,
    channelId: 3,
    channelName: '美团',
    channelRoomType: 'Meituan Deluxe King',
    hotelRoomType: '豪华大床房',
    status: 'active',
    updatedAt: '2025-12-14 15:20:00'
  },
  {
    id: 4,
    channelId: 4,
    channelName: '携程',
    channelRoomType: 'Ctrip Standard King',
    hotelRoomType: '标准大床房',
    status: 'active',
    updatedAt: '2025-12-13 09:15:00'
  }
]

// 模拟价格映射数据
const priceMappings = [
  {
    id: 1,
    channelId: 1,
    channelName: '飞猪',
    channelRatePlan: 'Fliggy Basic',
    hotelRateCode: 'RACK',
    markup: 0,
    status: 'active',
    updatedAt: '2025-12-15 10:35:00'
  },
  {
    id: 2,
    channelId: 3,
    channelName: '美团',
    channelRatePlan: 'Meituan Standard',
    hotelRateCode: 'NET',
    markup: 5,
    status: 'active',
    updatedAt: '2025-12-14 15:25:00'
  },
  {
    id: 3,
    channelId: 4,
    channelName: '携程',
    channelRatePlan: 'Ctrip Premium',
    hotelRateCode: 'OTA',
    markup: 10,
    status: 'active',
    updatedAt: '2025-12-13 09:20:00'
  }
]

const ChannelMapping = () => {
  // 状态管理
  const [activeTab, setActiveTab] = useState('roomType')
  const [selectedChannel, setSelectedChannel] = useState(null)
  const [roomTypeMappings, setRoomTypeMappings] = useState(channelMappings)
  const [priceMappingsData, setPriceMappingsData] = useState(priceMappings)

  // 房型映射列配置
  const roomTypeColumns = [
    {
      title: '渠道',
      dataIndex: 'channelName',
      key: 'channelName',
      width: 120
    },
    {
      title: '渠道房型',
      dataIndex: 'channelRoomType',
      key: 'channelRoomType',
      width: 180
    },
    {
      title: '酒店房型',
      dataIndex: 'hotelRoomType',
      key: 'hotelRoomType',
      width: 180,
      render: (text) => (
        <Select defaultValue={text} style={{ width: 150 }}>
          {roomTypes.map(roomType => (
            <Option key={roomType.id} value={roomType.name}>{roomType.name}</Option>
          ))}
        </Select>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <Tag color={status === 'active' ? 'green' : 'red'}>
          {status === 'active' ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180
    },

  ]

  // 价格映射列配置
  const priceMappingColumns = [
    {
      title: '渠道',
      dataIndex: 'channelName',
      key: 'channelName',
      width: 120
    },
    {
      title: '渠道价格计划',
      dataIndex: 'channelRatePlan',
      key: 'channelRatePlan',
      width: 180
    },
    {
      title: '酒店价格码',
      dataIndex: 'hotelRateCode',
      key: 'hotelRateCode',
      width: 180,
      render: (text) => (
        <Select defaultValue={text} style={{ width: 150 }}>
          {rateCodes.map(rateCode => (
            <Option key={rateCode.id} value={rateCode.code}>{rateCode.name}</Option>
          ))}
        </Select>
      )
    },
    {
      title: '加价率(%)',
      dataIndex: 'markup',
      key: 'markup',
      width: 120,
      render: (markup) => (
        <Input type="number" defaultValue={markup} style={{ width: 100 }} />
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <Tag color={status === 'active' ? 'green' : 'red'}>
          {status === 'active' ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <FilterOutlined />
        渠道映射
      </h1>
      
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]} align="middle">
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select
              placeholder="选择渠道"
              style={{ width: '100%' }}
              allowClear
              onChange={setSelectedChannel}
            >
              {channels.map(channel => (
                <Option key={channel.id} value={channel.id}>{channel.name}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="渠道房型名称" allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="酒店房型名称" allowClear />
          </Col>

        </Row>
      </Card>
      
      <Tabs activeKey={activeTab} onChange={setActiveTab} type="card" size="large">
        <TabPane tab="房型映射" key="roomType">
          <Card>
            <Table
              columns={roomTypeColumns}
              dataSource={roomTypeMappings}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              scroll={{ x: 1000 }}
            />
          </Card>
        </TabPane>
        <TabPane tab="价格映射" key="price">
          <Card>
            <Table
              columns={priceMappingColumns}
              dataSource={priceMappingsData}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              scroll={{ x: 1000 }}
            />
          </Card>
        </TabPane>
      </Tabs>
    </div>
  )
}

export default ChannelMapping
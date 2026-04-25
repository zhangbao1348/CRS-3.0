import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, Button, Card, Row, Col, Input, Select, message, Space, Tag } from 'antd'
import { PlusOutlined, EditOutlined, SearchOutlined } from '@ant-design/icons'

const { Option } = Select

// 模拟促销数据
const mockPromotions = [
  {
    id: 1,
    rnum: 1,
    channel: '携程',
    promotionType: '限时抢购',
    eventName: '10月15日至10月19日 限时促销',
    discountModel: '折扣',
    discountValue: '8折',
    status: '有效'
  },
  {
    id: 2,
    rnum: 2,
    channel: '携程',
    promotionType: '今夜甩卖',
    eventName: '每日夜间9点甩卖',
    discountModel: '折扣',
    discountValue: '8折',
    status: '有效'
  },
  {
    id: 3,
    rnum: 3,
    channel: '携程',
    promotionType: '天天特价',
    eventName: '天天特价',
    discountModel: '立减',
    discountValue: '-230',
    status: '有效'
  },
  {
    id: 4,
    rnum: 4,
    channel: '携程',
    promotionType: '提前预定',
    eventName: '提前3天预订打8折',
    discountModel: '折扣',
    discountValue: '8折',
    status: '有效'
  },
  {
    id: 5,
    rnum: 5,
    channel: '美团',
    promotionType: '连住特惠',
    eventName: '连住3晚 打9折',
    discountModel: '折扣',
    discountValue: '9折',
    status: '有效'
  },
  {
    id: 6,
    rnum: 6,
    channel: '美团',
    promotionType: '新客特惠',
    eventName: '新客打8折',
    discountModel: '折扣',
    discountValue: '8折',
    status: '有效'
  },
  {
    id: 7,
    rnum: 7,
    channel: '美团',
    promotionType: '门店特惠',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: '无效'
  },
  {
    id: 8,
    rnum: 8,
    channel: '美团',
    promotionType: '周末特惠',
    eventName: '。。',
    discountModel: '',
    discountValue: '',
    status: '无效'
  },
  {
    id: 9,
    rnum: 9,
    channel: '美团',
    promotionType: '节日专享',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: '无效'
  },
  {
    id: 10,
    rnum: 10,
    channel: '美团',
    promotionType: '多间立减',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: '无效'
  },
  {
    id: 11,
    rnum: 11,
    channel: '携程',
    promotionType: '午夜特惠',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: ''
  },
  {
    id: 12,
    rnum: 12,
    channel: '携程',
    promotionType: '钟点房促销',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: ''
  },
  {
    id: 13,
    rnum: 13,
    channel: '携程',
    promotionType: '国内集团普通会员价',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: ''
  },
  {
    id: 14,
    rnum: 14,
    channel: '携程',
    promotionType: '国内集团白银会员价',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: ''
  },
  {
    id: 15,
    rnum: 15,
    channel: '携程',
    promotionType: '国内集团黄金会员价',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: ''
  },
  {
    id: 16,
    rnum: 16,
    channel: '携程',
    promotionType: '国内集团铂金会员价',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: ''
  },
  {
    id: 17,
    rnum: 17,
    channel: '携程',
    promotionType: '国内集团钻石系列会员价',
    eventName: '',
    discountModel: '',
    discountValue: '',
    status: ''
  }
]

// 模拟促销注册数据
const mockRegistrationData = [
  {
    id: 1,
    serialNumber: 1,
    property: 'BGOSH',
    rateCode: 'ADR(标准价)',
    room: 'ST, SE, SR',
    status: 'Registration Succeeded',
    statusClass: 'success'
  },
  {
    id: 2,
    serialNumber: 2,
    property: 'BGOSH',
    rateCode: 'AMD (会员价)',
    room: 'ST, SE, SR',
    status: 'Registration Failed',
    statusClass: 'error'
  },
  {
    id: 3,
    serialNumber: 3,
    property: 'BGOSH',
    rateCode: 'ACC (银卡会员价)',
    room: 'ST, SE, SR',
    status: 'Registration Failed',
    statusClass: 'error'
  },
  {
    id: 4,
    serialNumber: 4,
    property: 'BGOSH',
    rateCode: 'AAE (门市价)',
    room: 'ST, SE, SR',
    status: 'Registration Succeeded',
    statusClass: 'success'
  }
]

// 模拟渠道数据
const channels = [
  { value: 'CTRIP', label: '携程' },
  { value: 'MEITUAN', label: '美团' },
  { value: 'FLIGGY', label: '飞猪' }
]

// 模拟促销类型数据
const promotionTypes = [
  { value: '限时抢购', label: '限时抢购' },
  { value: '今夜甩卖', label: '今夜甩卖' },
  { value: '天天特价', label: '天天特价' },
  { value: '提前预定', label: '提前预定' },
  { value: '连住特惠', label: '连住特惠' },
  { value: '新客特惠', label: '新客特惠' },
  { value: '门店特惠', label: '门店特惠' },
  { value: '周末特惠', label: '周末特惠' },
  { value: '节日专享', label: '节日专享' },
  { value: '多间立减', label: '多间立减' },
  { value: '午夜特惠', label: '午夜特惠' },
  { value: '钟点房促销', label: '钟点房促销' }
]

// 模拟折扣类型数据
const discountTypes = [
  { value: '折扣', label: '折扣' },
  { value: '立减', label: '立减' }
]

const OTAPromotionManagement = () => {
  const navigate = useNavigate()
  const [promotions, setPromotions] = useState(mockPromotions)
  const [searchParams, setSearchParams] = useState({
    channel: '',
    eventName: ''
  })

  const handleAdd = () => {
    navigate('/group-promotion-management/ota-promotion-management/add')
  }

  const handleStatusChange = (record) => {
    const newStatus = record.status === '有效' ? '无效' : '有效'
    setPromotions(promotions.map(p => 
      p.id === record.id ? { ...p, status: newStatus } : p
    ))
    message.success(`已将活动 ${record.eventName || record.promotionType} 设置为${newStatus}`)
  }

  const handleEdit = (record) => {
    // 这里可以添加编辑功能，跳转到编辑页面
    message.info('编辑功能开发中')
  }

  const columns = [
    {
      title: '序号',
      dataIndex: 'rnum',
      key: 'rnum',
      width: 60
    },
    {
      title: '渠道',
      dataIndex: 'channel',
      key: 'channel',
      width: 100
    },
    {
      title: '促销类型',
      dataIndex: 'promotionType',
      key: 'promotionType',
      width: 150
    },
    {
      title: '活动名称',
      dataIndex: 'eventName',
      key: 'eventName',
      width: 200
    },
    {
      title: '折扣模式',
      dataIndex: 'discountModel',
      key: 'discountModel',
      width: 120
    },
    {
      title: '优惠金额/折扣',
      dataIndex: 'discountValue',
      key: 'discountValue',
      width: 150
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === '有效' ? 'green' : status === '无效' ? 'red' : 'gray'}>
          {status || ''}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'operate',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          {record.status === '有效' && (
            <Button type="link" size="small" danger onClick={() => handleStatusChange(record)}>
              设置为无效
            </Button>
          )}
          {record.status === '无效' && (
            <Button type="link" size="small" onClick={() => handleStatusChange(record)}>
              设置为有效
            </Button>
          )}
        </Space>
      )
    }
  ]



  return (
    <div className="fade-in">
      <h1 className="page-title">
        维护促销信息/报名
      </h1>
      
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]} align="middle">
          <Col xs={24} sm={12} md={8} lg={6}>
            <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'nowrap' }}>
              <span style={{ marginRight: 8, whiteSpace: 'nowrap' }}>渠道:</span>
              <Select
                style={{ flex: 1, minWidth: 100 }}
                value={searchParams.channel}
                placeholder="请选择"
                onChange={(value) => setSearchParams({...searchParams, channel: value})}
              >
                {channels.map(channel => (
                  <Option key={channel.value} value={channel.value}>{channel.label}</Option>
                ))}
              </Select>
            </div>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'nowrap' }}>
              <span style={{ marginRight: 8, whiteSpace: 'nowrap' }}>活动名称:</span>
              <Input
                style={{ flex: 1, minWidth: 120 }}
                value={searchParams.eventName}
                onChange={(e) => setSearchParams({...searchParams, eventName: e.target.value})}
              />
            </div>
          </Col>
          <Col xs={24} sm={24} md={8} lg={12} style={{ textAlign: 'left' }}>
            <Space>
              <Button type="default" icon={<SearchOutlined />}>搜索</Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增促销</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      <Table
        columns={columns}
        dataSource={promotions}
        rowKey="id"
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1000 }}
      />


    </div>
  )
}

export default OTAPromotionManagement
import React from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, Radio } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  GiftOutlined
} from '@ant-design/icons'

const { Option } = Select
const { Group: RadioGroup } = Radio

// 模拟套餐设置数据
const mockPackageSettings = [
  {
    id: 1,
    name: '含单早套餐',
    code: 'SINGLE_BREAKFAST',
    type: '早餐套餐',
    status: '启用',
    description: '包含单人早餐',
    price: '+¥50/人',
    validPeriod: '长期有效'
  },
  {
    id: 2,
    name: '含双早套餐',
    code: 'DOUBLE_BREAKFAST',
    type: '早餐套餐',
    status: '启用',
    description: '包含双人早餐',
    price: '+¥80/间',
    validPeriod: '长期有效'
  },
  {
    id: 3,
    name: '豪华套餐',
    code: 'LUXURY_PACKAGE',
    type: '综合套餐',
    status: '启用',
    description: '包含双早+下午茶+机场接送',
    price: '+¥200/间',
    validPeriod: '长期有效'
  },
  {
    id: 4,
    name: '商务套餐',
    code: 'BUSINESS_PACKAGE',
    type: '商务套餐',
    status: '启用',
    description: '包含双早+会议室2小时+洗衣服务',
    price: '+¥150/间',
    validPeriod: '长期有效'
  },
  {
    id: 5,
    name: '亲子套餐',
    code: 'FAMILY_PACKAGE',
    type: '亲子套餐',
    status: '启用',
    description: '包含双早+儿童早餐+儿童乐园门票',
    price: '+¥120/间',
    validPeriod: '长期有效'
  },
  {
    id: 6,
    name: '周末套餐',
    code: 'WEEKEND_PACKAGE',
    type: '限时套餐',
    status: '停用',
    description: '包含双早+延迟退房至14:00',
    price: '+¥60/间',
    validPeriod: '每周五至周日'
  }
]

const PackageSetting = () => {
  const columns = [
    {
      title: '套餐名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '套餐代码',
      dataIndex: 'code',
      key: 'code',
      width: 150
    },
    {
      title: '套餐类型',
      dataIndex: 'type',
      key: 'type',
      width: 120
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <span style={{ 
          color: status === '启用' ? '#52c41a' : '#ff4d4f',
          fontWeight: 500
        }}>
          {status}
        </span>
      )
    },
    {
      title: '套餐价格',
      dataIndex: 'price',
      key: 'price',
      width: 120
    },
    {
      title: '有效期',
      dataIndex: 'validPeriod',
      key: 'validPeriod',
      width: 150
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EyeOutlined />}>查看</Button>
          <Button type="link" size="small" icon={<EditOutlined />}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <GiftOutlined />
        包价设置
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="套餐名称" prefix={<SearchOutlined />} allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="套餐代码" allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="套餐类型" allowClear style={{ width: '100%' }}>
              <Option value="早餐套餐">早餐套餐</Option>
              <Option value="综合套餐">综合套餐</Option>
              <Option value="商务套餐">商务套餐</Option>
              <Option value="亲子套餐">亲子套餐</Option>
              <Option value="限时套餐">限时套餐</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="状态" allowClear style={{ width: '100%' }}>
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default">重置</Button>
              <Button type="primary" icon={<SearchOutlined />}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large">
          新增套餐
        </Button>
      </div>

      {/* 套餐列表表格 */}
      <Table
        columns={columns}
        dataSource={mockPackageSettings}
        rowKey="id"
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1200 }}
      />
    </div>
  )
}

export default PackageSetting
import React from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, InputNumber } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  CalculatorOutlined
} from '@ant-design/icons'

const { Option } = Select

// 模拟税费设置数据
const mockTaxSettings = [
  {
    id: 1,
    name: '增值税',
    code: 'VAT',
    type: '流转税',
    rate: 6,
    status: '启用',
    description: '住宿服务增值税',
    calculationType: '价外税',
    validPeriod: '长期有效'
  },
  {
    id: 2,
    name: '城市维护建设税',
    code: 'CITY_MAINTENANCE',
    type: '附加税',
    rate: 7,
    status: '启用',
    description: '城市维护建设税',
    calculationType: '价内税',
    validPeriod: '长期有效'
  },
  {
    id: 3,
    name: '教育费附加',
    code: 'EDUCATION_SURCHARGE',
    type: '附加税',
    rate: 3,
    status: '启用',
    description: '教育费附加',
    calculationType: '价内税',
    validPeriod: '长期有效'
  },
  {
    id: 4,
    name: '地方教育附加',
    code: 'LOCAL_EDUCATION',
    type: '附加税',
    rate: 2,
    status: '启用',
    description: '地方教育附加',
    calculationType: '价内税',
    validPeriod: '长期有效'
  },
  {
    id: 5,
    name: '文化事业建设费',
    code: 'CULTURAL_CONSTRUCTION',
    type: '其他税费',
    rate: 3,
    status: '停用',
    description: '文化事业建设费',
    calculationType: '价内税',
    validPeriod: '长期有效'
  },
  {
    id: 6,
    name: '旅游发展基金',
    code: 'TOURISM_DEVELOPMENT',
    type: '其他税费',
    rate: 1,
    status: '启用',
    description: '旅游发展基金',
    calculationType: '价内税',
    validPeriod: '长期有效'
  }
]

const TaxSetting = () => {
  const columns = [
    {
      title: '税费名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '税费代码',
      dataIndex: 'code',
      key: 'code',
      width: 150
    },
    {
      title: '税费类型',
      dataIndex: 'type',
      key: 'type',
      width: 120
    },

    {
      title: '计算方式',
      dataIndex: 'calculationType',
      key: 'calculationType',
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
        <CalculatorOutlined />
        税率设置
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="税费名称" prefix={<SearchOutlined />} allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="税费代码" allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="税费类型" allowClear style={{ width: '100%' }}>
              <Option value="流转税">流转税</Option>
              <Option value="附加税">附加税</Option>
              <Option value="其他税费">其他税费</Option>
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
          新增税率
        </Button>
      </div>

      {/* 税率列表表格 */}
      <Table
        columns={columns}
        dataSource={mockTaxSettings}
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

export default TaxSetting
import React from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, Radio } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  DollarOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Option } = Select
const { Group: RadioGroup } = Radio

// 模拟价格计划数据
const mockRatePlans = [
  {
    id: 1,
    name: '散客价',
    code: 'BAR',
    rateCategory: '公共价',
    marketCode: 'MARKET01',
    sourceCode: 'SOURCE01',
    type: '基础价格计划',
    status: '启用',
    includeBreakfast: '含单早',
    refundable: '可退',
    guarantee: '无需担保',
    promotion: '不限制'
  },
  {
    id: 2,
    name: '协议价',
    code: 'CORP',
    rateCategory: '协议价',
    marketCode: 'MARKET02',
    sourceCode: 'SOURCE02',
    type: '衍生价格计划',
    status: '启用',
    includeBreakfast: '含双早',
    refundable: '可退',
    guarantee: '无需担保',
    promotion: '限制部分优惠'
  },
  {
    id: 3,
    name: '团队价',
    code: 'GRP',
    rateCategory: '团队价',
    marketCode: 'MARKET03',
    sourceCode: 'SOURCE03',
    type: '衍生价格计划',
    status: '启用',
    includeBreakfast: '含双早',
    refundable: '不可退',
    guarantee: '需担保',
    promotion: '不限制'
  },
  {
    id: 4,
    name: '会员价',
    code: 'MEM',
    rateCategory: '会员价',
    marketCode: 'MARKET01',
    sourceCode: 'SOURCE01',
    type: '基础价格计划',
    status: '启用',
    includeBreakfast: '含单早',
    refundable: '可退',
    guarantee: '无需担保',
    promotion: '限制部分优惠'
  },
  {
    id: 5,
    name: '促销价',
    code: 'PROMO',
    rateCategory: '促销价',
    marketCode: 'MARKET04',
    sourceCode: 'SOURCE04',
    type: '衍生价格计划',
    status: '启用',
    includeBreakfast: '不含早',
    refundable: '可退',
    guarantee: '需担保',
    promotion: '限制部分优惠'
  },
  {
    id: 6,
    name: '长住价',
    code: 'LONG',
    rateCategory: '长住价',
    marketCode: 'MARKET05',
    sourceCode: 'SOURCE05',
    type: '衍生价格计划',
    status: '停用',
    includeBreakfast: '含双早',
    refundable: '不可退',
    guarantee: '需担保',
    promotion: '不限制'
  }
]

const RatePlan = () => {
  const navigate = useNavigate()
  
  // 处理新增价格计划
  const handleAddRatePlan = () => {
    navigate('/rate-management/add-rate-plan')
  }
  
  const columns = [
    {
      title: '价格计划名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '价格计划代码',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: '价格类别',
      dataIndex: 'rateCategory',
      key: 'rateCategory',
      width: 120
    },
    {
      title: '市场码',
      dataIndex: 'marketCode',
      key: 'marketCode',
      width: 120
    },
    {
      title: '来源码',
      dataIndex: 'sourceCode',
      key: 'sourceCode',
      width: 120
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 120
    },
    {
      title: '早餐',
      dataIndex: 'includeBreakfast',
      key: 'includeBreakfast',
      width: 120
    },
    {
      title: '退改政策',
      dataIndex: 'refundable',
      key: 'refundable',
      width: 120
    },
    {
      title: '担保政策',
      dataIndex: 'guarantee',
      key: 'guarantee',
      width: 150
    },
    {
      title: '促销优惠',
      dataIndex: 'promotion',
      key: 'promotion',
      width: 150
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
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EyeOutlined />}>查看</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => navigate('/rate-management/add-rate-plan', { state: { record } })}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <DollarOutlined />
        价格计划管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="价格计划名称" prefix={<SearchOutlined />} allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="价格计划代码" allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="价格类型" allowClear style={{ width: '100%' }}>
              <Option value="公共价">公共价</Option>
              <Option value="协议价">协议价</Option>
              <Option value="团队价">团队价</Option>
              <Option value="会员价">会员价</Option>
              <Option value="促销价">促销价</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="状态" allowClear style={{ width: '100%' }}>
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12}>
            <Space>
              <Button type="default">重置</Button>
              <Button type="primary" icon={<SearchOutlined />}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddRatePlan}>
          新增价格计划
        </Button>
      </div>

      {/* 价格计划列表表格 */}
      <Table
        columns={columns}
        dataSource={mockRatePlans}
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

export default RatePlan
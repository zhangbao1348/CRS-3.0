import React from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  CloseCircleOutlined
} from '@ant-design/icons'

const { Option } = Select

// 模拟集团取消政策数据
const mockCancellationPolicies = [
  {
    id: 1,
    name: '免费取消',
    code: 'FREE_CANCEL',
    type: '免费取消',
    status: '启用',
    description: '入住前24小时可免费取消',
    cancellationFee: '无',
    deadline: '入住前24小时',
    validPeriod: '长期有效'
  },
  {
    id: 2,
    name: '部分费用',
    code: 'PARTIAL_FEE',
    type: '部分费用',
    status: '启用',
    description: '入住前12小时取消收取50%房费，12小时内取消收取100%房费',
    cancellationFee: '50%-100%房费',
    deadline: '入住前12小时',
    validPeriod: '长期有效'
  },
  {
    id: 3,
    name: '不可取消',
    code: 'NON_REFUNDABLE',
    type: '不可取消',
    status: '启用',
    description: '预订后不可取消，无论何时取消均收取100%房费',
    cancellationFee: '100%房费',
    deadline: '预订后立即生效',
    validPeriod: '长期有效'
  },
  {
    id: 4,
    name: '特殊取消',
    code: 'SPECIAL_CANCEL',
    type: '特殊取消',
    status: '启用',
    description: '根据特殊情况协商取消，具体费用双方协商',
    cancellationFee: '协商确定',
    deadline: '协商确定',
    validPeriod: '长期有效'
  },
  {
    id: 5,
    name: '提前7天取消',
    code: '7DAYS_ADVANCE',
    type: '提前取消',
    status: '启用',
    description: '入住前7天可免费取消，7天内取消收取100%房费',
    cancellationFee: '100%房费（7天内）',
    deadline: '入住前7天',
    validPeriod: '长期有效'
  },
  {
    id: 6,
    name: '提前14天取消',
    code: '14DAYS_ADVANCE',
    type: '提前取消',
    status: '停用',
    description: '入住前14天可免费取消，14天内取消收取100%房费',
    cancellationFee: '100%房费（14天内）',
    deadline: '入住前14天',
    validPeriod: '长期有效'
  }
]

const GroupCancellation = () => {
  const columns = [
    {
      title: '取消政策名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '取消政策代码',
      dataIndex: 'code',
      key: 'code',
      width: 150
    },
    {
      title: '取消类型',
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
      title: '取消费用',
      dataIndex: 'cancellationFee',
      key: 'cancellationFee',
      width: 150
    },
    {
      title: '取消截止时间',
      dataIndex: 'deadline',
      key: 'deadline',
      width: 180
    },
    {
      title: '有效期',
      dataIndex: 'validPeriod',
      key: 'validPeriod',
      width: 120
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
        <CloseCircleOutlined />
        集团取消政策管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="取消政策名称" prefix={<SearchOutlined />} allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="取消政策代码" allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="取消类型" allowClear style={{ width: '100%' }}>
              <Option value="免费取消">免费取消</Option>
              <Option value="部分费用">部分费用</Option>
              <Option value="不可取消">不可取消</Option>
              <Option value="特殊取消">特殊取消</Option>
              <Option value="提前取消">提前取消</Option>
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
          新增取消政策
        </Button>
      </div>

      {/* 取消政策列表表格 */}
      <Table
        columns={columns}
        dataSource={mockCancellationPolicies}
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

export default GroupCancellation
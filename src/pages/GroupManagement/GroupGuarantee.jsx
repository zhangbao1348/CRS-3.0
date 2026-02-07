import React from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, Radio } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  LockOutlined
} from '@ant-design/icons'

const { Option } = Select
const { Group: RadioGroup } = Radio

// 模拟集团担保政策数据
const mockGuaranteePolicies = [
  {
    id: 1,
    name: '无需担保',
    code: 'NO_GUARANTEE',
    type: '无担保',
    status: '启用',
    description: '无需支付担保金，支持免费取消',
    cancellationPolicy: '可免费取消',
    validPeriod: '长期有效'
  },
  {
    id: 2,
    name: '信用卡担保',
    code: 'CC_GUARANTEE',
    type: '信用卡',
    status: '启用',
    description: '需提供信用卡担保，超时取消将收取首晚房费',
    cancellationPolicy: '入住前24小时可取消',
    validPeriod: '长期有效'
  },
  {
    id: 3,
    name: '预付担保',
    code: 'PREPAY_GUARANTEE',
    type: '预付',
    status: '启用',
    description: '需全额预付房费，不可取消',
    cancellationPolicy: '不可取消',
    validPeriod: '长期有效'
  },
  {
    id: 4,
    name: '公司担保',
    code: 'CORP_GUARANTEE',
    type: '公司',
    status: '启用',
    description: '需公司签署担保协议，挂账结算',
    cancellationPolicy: '入住前48小时可取消',
    validPeriod: '长期有效'
  },
  {
    id: 5,
    name: '第三方担保',
    code: 'THIRD_PARTY_GUARANTEE',
    type: '第三方',
    status: '启用',
    description: '由第三方平台提供担保，按照平台规则执行',
    cancellationPolicy: '按照平台规则',
    validPeriod: '长期有效'
  },
  {
    id: 6,
    name: '特殊担保',
    code: 'SPECIAL_GUARANTEE',
    type: '特殊',
    status: '停用',
    description: '特殊情况下的担保政策，需单独审批',
    cancellationPolicy: '协商解决',
    validPeriod: '长期有效'
  }
]

const GroupGuarantee = () => {
  const columns = [
    {
      title: '担保政策名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '担保政策代码',
      dataIndex: 'code',
      key: 'code',
      width: 150
    },
    {
      title: '担保类型',
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
      title: '取消政策',
      dataIndex: 'cancellationPolicy',
      key: 'cancellationPolicy',
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
        <LockOutlined />
        集团担保政策管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="担保政策名称" prefix={<SearchOutlined />} allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="担保政策代码" allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="担保类型" allowClear style={{ width: '100%' }}>
              <Option value="无担保">无担保</Option>
              <Option value="信用卡">信用卡</Option>
              <Option value="预付">预付</Option>
              <Option value="公司">公司</Option>
              <Option value="第三方">第三方</Option>
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
          新增担保政策
        </Button>
      </div>

      {/* 担保政策列表表格 */}
      <Table
        columns={columns}
        dataSource={mockGuaranteePolicies}
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

export default GroupGuarantee
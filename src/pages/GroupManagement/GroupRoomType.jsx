import React from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  HomeOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Option } = Select

// 模拟集团房型数据
const mockRoomTypes = [
  {
    id: 1,
    name: '豪华大床房',
    code: 'DLK',
    category: '大床房',
    beds: '1张2米大床',
    area: '45㎡',
    floor: '5-10层',
    window: '有窗',
    status: '启用'
  },
  {
    id: 2,
    name: '豪华双床房',
    code: 'DSK',
    category: '双床房',
    beds: '2张1.2米单人床',
    area: '42㎡',
    floor: '5-10层',
    window: '有窗',
    status: '启用'
  },
  {
    id: 3,
    name: '行政大床房',
    code: 'XLK',
    category: '大床房',
    beds: '1张2米大床',
    area: '55㎡',
    floor: '11-15层',
    window: '有窗',
    status: '启用'
  },
  {
    id: 4,
    name: '行政双床房',
    code: 'XSK',
    category: '双床房',
    beds: '2张1.2米单人床',
    area: '52㎡',
    floor: '11-15层',
    window: '有窗',
    status: '启用'
  },
  {
    id: 5,
    name: '总统套房',
    code: 'ZTF',
    category: '套房',
    beds: '1张2.2米大床',
    area: '120㎡',
    floor: '16层',
    window: '有窗',
    status: '启用'
  },
  {
    id: 6,
    name: '标准大床房',
    code: 'BLK',
    category: '大床房',
    beds: '1张1.8米大床',
    area: '35㎡',
    floor: '1-4层',
    window: '部分无窗',
    status: '停用'
  }
]

const GroupRoomType = () => {
  const navigate = useNavigate()

  // 处理新增房型
  const handleAddRoomType = () => {
    navigate('/group-management/add-group-room-type')
  }

  // 处理编辑房型
  const handleEditRoomType = (record) => {
    navigate('/group-management/add-group-room-type', { state: { record } })
  }

  const columns = [
    {
      title: '房型名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '房型编码',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: '房型大类',
      dataIndex: 'category',
      key: 'category',
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
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EyeOutlined />}>查看</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditRoomType(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <HomeOutlined />
        集团房型管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="房型名称" prefix={<SearchOutlined />} allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="房型编码" allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="房型分类" allowClear style={{ width: '100%' }}>
              <Option value="大床房">大床房</Option>
              <Option value="双床房">双床房</Option>
              <Option value="套房">套房</Option>
              <Option value="家庭房">家庭房</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="状态" allowClear style={{ width: '100%' }}>
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={8} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default">重置</Button>
              <Button type="primary" icon={<SearchOutlined />}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddRoomType}>
          新增房型
        </Button>
      </div>

      {/* 房型列表表格 */}
      <Table
        columns={columns}
        dataSource={mockRoomTypes}
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

export default GroupRoomType
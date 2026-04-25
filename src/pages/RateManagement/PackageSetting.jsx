import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, message } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  EyeOutlined,
  GiftOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

const { Option } = Select

const PackageSetting = () => {
  const [packages, setPackages] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    type: '',
    status: ''
  })
  const navigate = useNavigate()
  
  // 包价类型选项
  const packageTypes = [
    { value: '早餐', label: '早餐' },
    { value: '午餐', label: '午餐' },
    { value: '晚餐', label: '晚餐' },
    { value: '下午茶', label: '下午茶' },
    { value: '门票', label: '门票' },
    { value: '其他', label: '其他' },
    { value: '免费增早', label: '免费增早' },
    { value: '延时退房', label: '延时退房' },
    { value: '提前入住', label: '提前入住' }
  ]
  
  // 状态选项
  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]
  
  // 初始化加载包价列表
  useEffect(() => {
    fetchPackages()
  }, [])
  
  // 获取包价列表
  const fetchPackages = async () => {
    setLoading(true)
    try {
      // 模拟数据
      const mockPackages = [
        {
          id: 1,
          name: '早餐套餐',
          code: 'BB',
          type: '早餐',
          status: '启用',
          description: '包含自助早餐',
          price: '¥50'
        },
        {
          id: 2,
          name: '早晚餐套餐',
          code: 'HB',
          type: '综合',
          status: '启用',
          description: '包含自助早餐和晚餐',
          price: '¥120'
        },
        {
          id: 3,
          name: '全餐套餐',
          code: 'FB',
          type: '综合',
          status: '停用',
          description: '包含早餐、午餐和晚餐',
          price: '¥180'
        },
        {
          id: 4,
          name: '下午茶套餐',
          code: 'TEA',
          type: '下午茶',
          status: '启用',
          description: '包含下午茶点心和饮品',
          price: '¥80'
        }
      ]
      setPackages(mockPackages)
    } catch (error) {
      console.error('获取包价列表失败:', error)
      message.error('获取包价列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }
  
  // 处理搜索
  const handleSearch = async () => {
    setLoading(true)
    try {
      // 模拟搜索功能
      const mockPackages = [
        {
          id: 1,
          name: '早餐套餐',
          code: 'BB',
          type: '早餐',
          status: '启用',
          description: '包含自助早餐',
          price: '¥50'
        },
        {
          id: 2,
          name: '早晚餐套餐',
          code: 'HB',
          type: '综合',
          status: '启用',
          description: '包含自助早餐和晚餐',
          price: '¥120'
        }
      ]
      setPackages(mockPackages)
    } catch (error) {
      console.error('搜索包价失败:', error)
      message.error('搜索失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }
  
  // 处理重置
  const handleReset = () => {
    setSearchParams({
      name: '',
      code: '',
      type: '',
      status: ''
    })
    fetchPackages()
  }
  
  // 处理新增包价
  const handleAddPackage = () => {
    navigate('/rate-management/add-package')
  }
  
  // 处理编辑包价
  const handleEditPackage = (record) => {
    navigate(`/rate-management/edit-package?id=${record.id}`)
  }
  

  
  // 列配置
  const columns = [
    {
      title: '包价名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '包价代码',
      dataIndex: 'code',
      key: 'code',
      width: 150
    },
    {
      title: '包价类型',
      dataIndex: 'type',
      key: 'type',
      width: 120
    },
    {
      title: '包价价格',
      dataIndex: 'price',
      key: 'price',
      width: 120
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true
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
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditPackage(record)}>编辑</Button>
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
            <Input 
              placeholder="包价名称" 
              prefix={<SearchOutlined />} 
              allowClear
              value={searchParams.name}
              onChange={(e) => setSearchParams({...searchParams, name: e.target.value})}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="包价代码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({...searchParams, code: e.target.value})}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="包价类型" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.type || undefined}
              onChange={(value) => setSearchParams({...searchParams, type: value})}
            >
              {packageTypes.map(item => (
                <Option key={item.value} value={item.value}>{item.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.status || undefined}
              onChange={(value) => setSearchParams({...searchParams, status: value})}
            >
              {statusOptions.map(item => (
                <Option key={item.value} value={item.value}>{item.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={24} lg={24} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" onClick={handleReset} style={{ height: 32 }}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} style={{ height: 32 }}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddPackage}>
          新增包价
        </Button>
      </div>

      {/* 包价列表表格 */}
      <Table
        columns={columns}
        dataSource={packages}
        rowKey="id"
        loading={loading}
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
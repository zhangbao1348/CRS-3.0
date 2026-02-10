import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, message } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
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
    { value: '综合', label: '综合' }
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
      const response = await axios.get('http://localhost:8080/api/packages')
      // 转换数据格式以匹配前端需求
      const formattedPackages = response.data.map(pkg => {
        let validPeriod = '长期有效'
        if (pkg.startDate && pkg.endDate) {
          validPeriod = `${pkg.startDate} 至 ${pkg.endDate}`
        } else if (pkg.startDate) {
          validPeriod = `从 ${pkg.startDate} 开始`
        } else if (pkg.endDate) {
          validPeriod = `至 ${pkg.endDate} 结束`
        }
        return {
          id: pkg.id,
          name: pkg.name,
          code: pkg.code,
          type: pkg.type,
          status: pkg.status === 'active' ? '启用' : '停用',
          description: pkg.description,
          price: pkg.fixedPrice ? `¥${pkg.fixedPrice}` : '酒店设置',
          validPeriod: validPeriod
        }
      })
      setPackages(formattedPackages)
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
      // 构建搜索参数
      const params = {}
      if (searchParams.name) params.name = searchParams.name
      if (searchParams.code) params.code = searchParams.code
      if (searchParams.type) params.type = searchParams.type
      if (searchParams.status) params.status = searchParams.status
      
      // 调用搜索API
      const response = await axios.post('http://localhost:8080/api/packages/search', params)
      
      // 转换数据格式
      const formattedPackages = response.data.map(pkg => {
        let validPeriod = '长期有效'
        if (pkg.startDate && pkg.endDate) {
          validPeriod = `${pkg.startDate} 至 ${pkg.endDate}`
        } else if (pkg.startDate) {
          validPeriod = `从 ${pkg.startDate} 开始`
        } else if (pkg.endDate) {
          validPeriod = `至 ${pkg.endDate} 结束`
        }
        return {
          id: pkg.id,
          name: pkg.name,
          code: pkg.code,
          type: pkg.type,
          status: pkg.status === 'active' ? '启用' : '停用',
          description: pkg.description,
          price: pkg.fixedPrice ? `¥${pkg.fixedPrice}` : '酒店设置',
          validPeriod: validPeriod
        }
      })
      setPackages(formattedPackages)
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
    navigate('/group-management/add-package')
  }
  
  // 处理编辑包价
  const handleEditPackage = (record) => {
    navigate(`/group-management/edit-package?id=${record.id}`)
  }
  
  // 处理删除包价
  const handleDeletePackage = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/packages/${id}`)
      message.success('包价删除成功')
      fetchPackages()
    } catch (error) {
      console.error('删除包价失败:', error)
      if (error.response && error.response.data) {
        message.error(error.response.data)
      } else {
        message.error('删除失败，请稍后重试')
      }
    }
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
      title: '包价价格',
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
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditPackage(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger onClick={() => handleDeletePackage(record.id)}>删除</Button>
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
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="包价代码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({...searchParams, code: e.target.value})}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="包价类型" 
              allowClear 
              style={{ width: '100%' }}
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
              style={{ width: '100%' }}
              value={searchParams.status || undefined}
              onChange={(value) => setSearchParams({...searchParams, status: value})}
            >
              {statusOptions.map(item => (
                <Option key={item.value} value={item.value}>{item.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" onClick={handleReset}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>搜索</Button>
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
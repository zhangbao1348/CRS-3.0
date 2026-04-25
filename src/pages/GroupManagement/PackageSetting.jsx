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
import api from '../../utils/api'

// 演示模式标志
const DEMO_MODE = false

// 模拟包价数据
const mockPackages = [
  {
    id: 1,
    name: '早餐包价',
    code: 'BREAKFAST',
    type: '早餐',
    status: '启用',
    description: '包含每日早餐',
    price: '¥30'
  },
  {
    id: 2,
    name: '午餐包价',
    code: 'LUNCH',
    type: '午餐',
    status: '启用',
    description: '包含每日午餐',
    price: '¥50'
  },
  {
    id: 3,
    name: '晚餐包价',
    code: 'DINNER',
    type: '晚餐',
    status: '启用',
    description: '包含每日晚餐',
    price: '¥80'
  },
  {
    id: 4,
    name: '三餐包价',
    code: 'THREE_MEALS',
    type: '综合',
    status: '启用',
    description: '包含每日三餐',
    price: '¥150'
  },
  {
    id: 5,
    name: '免费增早',
    code: 'FREE_BREAKFAST',
    type: '免费增早',
    status: '启用',
    description: '免费增加一份早餐',
    price: '酒店设置'
  },
  {
    id: 6,
    name: '延时退房',
    code: 'LATE_CHECKOUT',
    type: '延时退房',
    status: '启用',
    description: '可延迟退房至14:00',
    price: '¥20'
  },
  {
    id: 7,
    name: '提前入住',
    code: 'EARLY_CHECKIN',
    type: '提前入住',
    status: '启用',
    description: '可提前入住至10:00',
    price: '¥20'
  },
  {
    id: 8,
    name: '行政礼遇',
    code: 'EXECUTIVE_LOUNGE',
    type: '综合',
    status: '启用',
    description: '包含行政酒廊使用权',
    price: '¥100'
  },
  {
    id: 9,
    name: 'SPA包价',
    code: 'SPA_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含一次SPA体验',
    price: '¥200'
  },
  {
    id: 10,
    name: '健身包价',
    code: 'FITNESS_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含健身房使用权',
    price: '¥50'
  },
  {
    id: 11,
    name: '洗衣包价',
    code: 'LAUNDRY_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含洗衣服务',
    price: '¥80'
  },
  {
    id: 12,
    name: '接机包价',
    code: 'AIRPORT_PICKUP',
    type: '综合',
    status: '启用',
    description: '包含机场接机服务',
    price: '¥150'
  },
  {
    id: 13,
    name: '送机包价',
    code: 'AIRPORT_DROPOFF',
    type: '综合',
    status: '启用',
    description: '包含机场送机服务',
    price: '¥150'
  },
  {
    id: 14,
    name: '会议包价',
    code: 'MEETING_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含会议室使用权',
    price: '¥300'
  },
  {
    id: 15,
    name: '婚礼包价',
    code: 'WEDDING_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含婚礼场地使用权',
    price: '¥5000'
  },
  {
    id: 16,
    name: '生日包价',
    code: 'BIRTHDAY_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含生日蛋糕和布置',
    price: '¥200'
  },
  {
    id: 17,
    name: '蜜月包价',
    code: 'HONEYMOON_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含蜜月布置和香槟',
    price: '¥500'
  },
  {
    id: 18,
    name: '家庭包价',
    code: 'FAMILY_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含儿童用品和活动',
    price: '¥300'
  },
  {
    id: 19,
    name: '商务包价',
    code: 'BUSINESS_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含商务中心服务',
    price: '¥100'
  },
  {
    id: 20,
    name: '度假包价',
    code: 'VACATION_PACKAGE',
    type: '综合',
    status: '启用',
    description: '包含景点门票和活动',
    price: '¥500'
  }
]

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
    { value: '综合', label: '综合' },
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
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据
        setPackages(mockPackages)
      } else {
        // 非演示模式下从后端获取数据
        const response = await api.get('/packages')
        // 转换数据格式以匹配前端需求
        const formattedPackages = response.map(pkg => ({
          id: pkg.id,
          name: pkg.name,
          code: pkg.code,
          type: pkg.type,
          status: pkg.status === 'active' ? '启用' : '停用',
          description: pkg.description,
          price: pkg.fixedPrice ? `¥${pkg.fixedPrice}` : '酒店设置'
        }))
        setPackages(formattedPackages)
      }
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
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据进行过滤
        let filteredPackages = [...mockPackages]
        if (searchParams.name) {
          filteredPackages = filteredPackages.filter(pkg => pkg.name.includes(searchParams.name))
        }
        if (searchParams.code) {
          filteredPackages = filteredPackages.filter(pkg => pkg.code.includes(searchParams.code))
        }
        if (searchParams.type) {
          filteredPackages = filteredPackages.filter(pkg => pkg.type === searchParams.type)
        }
        if (searchParams.status) {
          filteredPackages = filteredPackages.filter(pkg => {
            if (searchParams.status === 'active') {
              return pkg.status === '启用'
            } else {
              return pkg.status === '停用'
            }
          })
        }
        setPackages(filteredPackages)
      } else {
        // 非演示模式下从后端获取数据
        // 构建搜索参数
        const params = {}
        if (searchParams.name) params.name = searchParams.name
        if (searchParams.code) params.code = searchParams.code
        if (searchParams.type) params.type = searchParams.type
        if (searchParams.status) params.status = searchParams.status
        
        // 调用搜索API
        const response = await api.post('/packages/search', params)
        
        // 转换数据格式
        const formattedPackages = response.map(pkg => ({
          id: pkg.id,
          name: pkg.name,
          code: pkg.code,
          type: pkg.type,
          status: pkg.status === 'active' ? '启用' : '停用',
          description: pkg.description,
          price: pkg.fixedPrice ? `¥${pkg.fixedPrice}` : '酒店设置'
        }))
        setPackages(formattedPackages)
      }
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
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
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
import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, message, Spin, Popconfirm, Tag } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  HomeOutlined,
  ReloadOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

// 演示模式标志
const DEMO_MODE = false

// 模拟房型大类数据
const mockCategories = [
  { id: 1, categoryName: '标准间' },
  { id: 2, categoryName: '大床房' },
  { id: 3, categoryName: '套房' },
  { id: 4, categoryName: '总统套房' }
]

// 模拟集团房型数据
const mockRoomTypes = [
  {
    id: 1,
    name: '标准单人间',
    code: 'STD_SINGLE',
    categoryId: 1,
    maxOccupancy: 1,
    sortOrder: 1,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 2,
    name: '标准双人间',
    code: 'STD_DOUBLE',
    categoryId: 1,
    maxOccupancy: 2,
    sortOrder: 2,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 3,
    name: '豪华单人间',
    code: 'DLX_SINGLE',
    categoryId: 2,
    maxOccupancy: 1,
    sortOrder: 3,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 4,
    name: '豪华双人间',
    code: 'DLX_DOUBLE',
    categoryId: 2,
    maxOccupancy: 2,
    sortOrder: 4,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 5,
    name: '商务套房',
    code: 'BUSINESS_SUITE',
    categoryId: 3,
    maxOccupancy: 2,
    sortOrder: 5,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 6,
    name: '行政套房',
    code: 'EXECUTIVE_SUITE',
    categoryId: 3,
    maxOccupancy: 2,
    sortOrder: 6,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 7,
    name: '总统套房',
    code: 'PRESIDENTIAL_SUITE',
    categoryId: 4,
    maxOccupancy: 4,
    sortOrder: 7,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 8,
    name: '家庭套房',
    code: 'FAMILY_SUITE',
    categoryId: 3,
    maxOccupancy: 4,
    sortOrder: 8,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 9,
    name: '无障碍房间',
    code: 'ACCESSIBLE',
    categoryId: 1,
    maxOccupancy: 2,
    sortOrder: 9,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 10,
    name: '连通房',
    code: 'CONNECTING',
    categoryId: 2,
    maxOccupancy: 4,
    sortOrder: 10,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 11,
    name: '蜜月套房',
    code: 'HONEYMOON_SUITE',
    categoryId: 3,
    maxOccupancy: 2,
    sortOrder: 11,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 12,
    name: '海景套房',
    code: 'OCEAN_VIEW_SUITE',
    categoryId: 3,
    maxOccupancy: 2,
    sortOrder: 12,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 13,
    name: '山景套房',
    code: 'MOUNTAIN_VIEW_SUITE',
    categoryId: 3,
    maxOccupancy: 2,
    sortOrder: 13,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 14,
    name: '花园套房',
    code: 'GARDEN_SUITE',
    categoryId: 3,
    maxOccupancy: 2,
    sortOrder: 14,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 15,
    name: '阁楼套房',
    code: 'PENTHOUSE_SUITE',
    categoryId: 4,
    maxOccupancy: 4,
    sortOrder: 15,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 16,
    name: '豪华大床房',
    code: 'DLX_KING',
    categoryId: 2,
    maxOccupancy: 2,
    sortOrder: 16,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 17,
    name: '豪华双床房',
    code: 'DLX_TWIN',
    categoryId: 2,
    maxOccupancy: 2,
    sortOrder: 17,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 18,
    name: '标准大床房',
    code: 'STD_KING',
    categoryId: 2,
    maxOccupancy: 2,
    sortOrder: 18,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 19,
    name: '标准双床房',
    code: 'STD_TWIN',
    categoryId: 2,
    maxOccupancy: 2,
    sortOrder: 19,
    status: '启用',
    statusValue: 'active'
  },
  {
    id: 20,
    name: '迷你套房',
    code: 'MINI_SUITE',
    categoryId: 3,
    maxOccupancy: 2,
    sortOrder: 20,
    status: '启用',
    statusValue: 'active'
  }
]

const { Option } = Select

const GroupRoomType = () => {
  const navigate = useNavigate()

  const [roomTypes, setRoomTypes] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    categoryId: null
  })

  const handleAddRoomType = () => {
    navigate('/group-management/add-group-room-type')
  }
  
  const handleEditRoomType = (record) => {
    navigate('/group-management/add-group-room-type', { state: { record } })
  }

  const handleDeleteRoomType = async (record) => {
    try {
      setLoading(true)
      await axios.delete(`/api/group-room-types/${record.id}`)
      message.success('删除成功')
      fetchRoomTypes()
    } catch (error) {
      console.error('删除失败:', error)
      message.error('删除失败: ' + (error.response?.data?.error || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  const fetchCategories = async () => {
    try {
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据
        setCategories(mockCategories)
      } else {
        // 非演示模式下从后端获取数据
        const response = await axios.get('/api/room-type-categories/group/1')
        setCategories(response.data)
      }
    } catch (error) {
      console.error('获取房型大类失败:', error)
    }
  }
  
  const fetchRoomTypes = async () => {
    try {
      setLoading(true)
      setError(null)
      
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据
        const formattedRoomTypes = mockRoomTypes.map(item => {
          const category = mockCategories.find(c => c.id === item.categoryId)
          return {
            ...item,
            category: category?.categoryName || '未分类'
          }
        })
        
        const filteredRoomTypes = formattedRoomTypes.filter(item => {
          if (searchParams.name && !item.name.includes(searchParams.name)) return false
          if (searchParams.code && !item.code.includes(searchParams.code)) return false
          if (searchParams.categoryId && item.categoryId !== searchParams.categoryId) return false
          return true
        })
        
        setRoomTypes(filteredRoomTypes)
      } else {
        // 非演示模式下从后端获取数据
        const params = {}
        if (searchParams.categoryId) params.categoryId = searchParams.categoryId
        
        // 同时获取房型大类和房型数据
        const [categoriesResponse, roomTypesResponse] = await Promise.all([
          axios.get('/api/room-type-categories/group/1'),
          axios.get('/api/group-room-types/group/1', { params })
        ])
        
        const currentCategories = categoriesResponse.data
        setCategories(currentCategories)
        
        const formattedRoomTypes = roomTypesResponse.data.map(item => {
          const category = currentCategories.find(c => Number(c.id) === Number(item.roomTypeCategoryId))
          return {
            id: item.id,
            name: item.roomTypeName,
            code: item.roomTypeCode,
            categoryId: item.roomTypeCategoryId,
            category: category ? category.categoryName : '未分类',
            maxOccupancy: item.maxOccupancy || 2,
            sortOrder: item.sortOrder || 0,
            status: item.status === 'active' ? '启用' : '停用',
            statusValue: item.status
          }
        })
        
        const filteredRoomTypes = formattedRoomTypes.filter(item => {
          if (searchParams.name && !item.name.includes(searchParams.name)) return false
          if (searchParams.code && !item.code.includes(searchParams.code)) return false
          return true
        })
        
        setRoomTypes(filteredRoomTypes)
      }
    } catch (error) {
      console.error('获取房型数据失败:', error)
      setError('获取数据失败: ' + (error.response?.data?.error || error.message))
    } finally {
      setLoading(false)
    }
  }
  
  useEffect(() => {
    // 直接调用 fetchRoomTypes，它会同时获取房型大类和房型数据
    fetchRoomTypes()
  }, [])
  
  const handleSearchParamChange = (key, value) => {
    setSearchParams(prev => ({
      ...prev,
      [key]: value
    }))
  }
  
  const handleSearch = () => {
    // 先获取最新的房型大类数据，再搜索房型
    fetchCategories().then(() => {
      fetchRoomTypes()
    })
  }
  
  const handleReset = () => {
    setSearchParams({
      name: '',
      code: '',
      categoryId: null
    })
    // 先获取最新的房型大类数据，再获取房型
    fetchCategories().then(() => {
      fetchRoomTypes()
    })
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
      width: 120,
      render: (text, record) => (
        <Tag color={record.categoryId ? 'blue' : 'default'}>{text}</Tag>
      )
    },
    {
      title: '最大入住人数',
      dataIndex: 'maxOccupancy',
      key: 'maxOccupancy',
      width: 120
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      width: 80
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => (
        <Tag color={status === '启用' ? 'green' : 'red'}>
          {status}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditRoomType(record)}>编辑</Button>
          <Popconfirm
            title="确定要删除这个房型吗？"
            description="删除后不可恢复"
            onConfirm={() => handleDeleteRoomType(record)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
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
      
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="房型名称" 
              prefix={<SearchOutlined />} 
              allowClear
              value={searchParams.name}
              onChange={(e) => handleSearchParamChange('name', e.target.value)}
              style={{ height: 32 }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="房型编码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => handleSearchParamChange('code', e.target.value)}
              style={{ height: 32 }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="房型大类" 
              allowClear 
              style={{ width: '100%', height: 32 }}
              value={searchParams.categoryId}
              onChange={(value) => handleSearchParamChange('categoryId', value)}
            >
              {categories.map(category => (
                <Option key={category.id} value={category.id}>
                  {category.categoryName}
                </Option>
              ))}
            </Select>
          </Col>
        </Row>
        <Row style={{ marginTop: 16 }}>
          <Col span={24} style={{ textAlign: 'right' }}>
            <Space>
              <Button onClick={handleReset} style={{ height: 32 }}>重置</Button>
              <Button 
                type="primary" 
                icon={<SearchOutlined />} 
                onClick={handleSearch}
                style={{ height: 32 }}
              >
                搜索
              </Button>
              <Button 
                icon={<ReloadOutlined />} 
                onClick={() => {
                  // 先获取最新的房型大类数据，再获取房型
                  fetchCategories().then(() => {
                    fetchRoomTypes()
                  })
                }}
                style={{ height: 32 }}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddRoomType}>
          新增房型
        </Button>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '100px 0' }}>
          <Spin size="large" tip="加载中..." />
        </div>
      ) : error ? (
        <div style={{ textAlign: 'center', padding: '100px 0', color: '#ff4d4f' }}>
          <p>{error}</p>
          <Button type="primary" onClick={fetchRoomTypes} style={{ marginTop: 16 }}>
            重新加载
          </Button>
        </div>
      ) : (
        <Table
          columns={columns}
          dataSource={roomTypes}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
          }}
          scroll={{ x: 1200 }}
          locale={{
            emptyText: '暂无集团房型数据'
          }}
        />
      )}
    </div>
  )
}

export default GroupRoomType
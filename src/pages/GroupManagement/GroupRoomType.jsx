import { useState, useEffect } from 'react'
import { App, Table, Button, Space, Card, Row, Col, Input, Select, Spin, Tag } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  HomeOutlined,
  ReloadOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { getCurrentTenantId } from '../../utils/tenantUtils'

const { Option } = Select

const GroupRoomType = () => {
  const navigate = useNavigate()
  const { message, modal } = App.useApp()

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

  const handleChangeStatus = async (record, status) => {
    try {
      setLoading(true)
      await axios.put(`/api/group-room-types/${record.id}/${status === 'active' ? 'enable' : 'disable'}`)
      message.success(status === 'active' ? '启用成功' : '停用成功')
      fetchRoomTypes()
    } catch (error) {
      console.error('状态修改失败:', error)
      message.error('状态修改失败: ' + (error.response?.data?.error || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  const confirmChangeStatus = (record, status) => {
    const action = status === 'active' ? '启用' : '停用'
    modal.confirm({
      title: `确认${action}`,
      content: status === 'active'
        ? `确定要启用房型“${record.name}”吗？`
        : `停用房型“${record.name}”后，已下发酒店的对应房型也将停用。确定继续吗？`,
      okText: action,
      cancelText: '取消',
      onOk: () => handleChangeStatus(record, status)
    })
  }

  const fetchCategories = async () => {
    try {
      const groupId = getCurrentTenantId()
      if (!groupId) throw new Error('缺少当前集团上下文')
      const response = await axios.get(`/api/room-type-categories/group/${groupId}`)
      setCategories(Array.isArray(response.data) ? response.data : [])
    } catch (error) {
      console.error('获取房型大类失败:', error)
    }
  }
  
  const fetchRoomTypes = async () => {
    try {
      setLoading(true)
      setError(null)
      
      {
        const groupId = getCurrentTenantId()
        if (!groupId) throw new Error('缺少当前集团上下文')
        const params = {}
        if (searchParams.categoryId) params.categoryId = searchParams.categoryId
        
        // 同时获取房型大类和房型数据
        const [categoriesResponse, roomTypesResponse] = await Promise.all([
          axios.get(`/api/room-type-categories/group/${groupId}`),
          axios.get(`/api/group-room-types/group/${groupId}`, { params })
        ])
        
        const currentCategories = Array.isArray(categoriesResponse.data) ? categoriesResponse.data : []
        const currentRoomTypes = Array.isArray(roomTypesResponse.data) ? roomTypesResponse.data : []
        setCategories(currentCategories)
        
        const formattedRoomTypes = currentRoomTypes.map(item => {
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
    // 页面初次进入时加载；后续查询由显式查询动作触发。
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
          {record.statusValue === 'active' ? (
            <Button type="link" size="small" danger onClick={() => confirmChangeStatus(record, 'inactive')}>停用</Button>
          ) : (
            <Button type="link" size="small" onClick={() => confirmChangeStatus(record, 'active')}>启用</Button>
          )}
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
          <Spin size="large"><span>加载中...</span></Spin>
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

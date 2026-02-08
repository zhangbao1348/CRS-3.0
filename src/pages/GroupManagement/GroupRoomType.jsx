import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, message, Spin, Modal } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  HomeOutlined,
  ReloadOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

const { Option } = Select

const GroupRoomType = () => {
  const navigate = useNavigate()
  
  // 状态管理
  const [roomTypes, setRoomTypes] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    category: '',
    status: ''
  })
  const [deleteModalVisible, setDeleteModalVisible] = useState(false)
  const [deleteId, setDeleteId] = useState(null)
  
  // 处理新增房型
  const handleAddRoomType = () => {
    navigate('/group-management/add-group-room-type')
  }
  
  // 处理编辑房型
  const handleEditRoomType = (record) => {
    navigate('/group-management/add-group-room-type', { state: { record } })
  }
  
  // 处理查看房型
  const handleViewRoomType = (record) => {
    // 可以实现查看详情的逻辑
    message.info(`查看房型: ${record.name}`)
  }
  
  // 处理删除房型
  const handleDeleteRoomType = (record) => {
    setDeleteId(record.id)
    setDeleteModalVisible(true)
  }
  
  // 确认删除
  const handleConfirmDelete = async () => {
    if (!deleteId) return
    
    try {
      setLoading(true)
      await axios.delete(`http://localhost:8080/api/group-room-types/${deleteId}`)
      message.success('房型删除成功')
      // 重新获取数据
      fetchRoomTypes()
      setDeleteModalVisible(false)
    } catch (error) {
      message.error('删除失败: ' + (error.response?.data?.error || '未知错误'))
    } finally {
      setLoading(false)
    }
  }
  
  // 从后端API获取集团房型数据
  const fetchRoomTypes = async () => {
    try {
      setLoading(true)
      setError(null)
      
      // 构建查询参数
      const params = {}
      Object.entries(searchParams).forEach(([key, value]) => {
        if (value) params[key] = value
      })
      
      const response = await axios.get('http://localhost:8080/api/group-room-types', {
        params
      })
      
      // 转换数据格式以匹配前端展示需求
      const formattedRoomTypes = response.data.map(item => ({
        id: item.id,
        name: item.roomTypeName,
        code: item.roomTypeCode,
        category: '未分类', // 可以根据实际数据调整
        status: item.status === 'active' ? '启用' : '停用'
      }))
      
      setRoomTypes(formattedRoomTypes)
    } catch (error) {
      setError('获取房型数据失败')
      message.error('获取数据失败: ' + (error.message || '网络错误'))
    } finally {
      setLoading(false)
    }
  }
  
  // 初始化时获取数据
  useEffect(() => {
    fetchRoomTypes()
  }, [])
  
  // 处理搜索参数变化
  const handleSearchParamChange = (key, value) => {
    setSearchParams(prev => ({
      ...prev,
      [key]: value
    }))
  }
  
  // 处理搜索
  const handleSearch = () => {
    fetchRoomTypes()
  }
  
  // 处理重置
  const handleReset = () => {
    setSearchParams({
      name: '',
      code: '',
      category: '',
      status: ''
    })
    fetchRoomTypes()
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
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewRoomType(record)}>查看</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditRoomType(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger onClick={() => handleDeleteRoomType(record)}>删除</Button>
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
            <Input 
              placeholder="房型名称" 
              prefix={<SearchOutlined />} 
              allowClear
              value={searchParams.name}
              onChange={(e) => handleSearchParamChange('name', e.target.value)}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="房型编码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => handleSearchParamChange('code', e.target.value)}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="房型分类" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.category || undefined}
              onChange={(value) => handleSearchParamChange('category', value)}
            >
              <Option value="大床房">大床房</Option>
              <Option value="双床房">双床房</Option>
              <Option value="套房">套房</Option>
              <Option value="家庭房">家庭房</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.status || undefined}
              onChange={(value) => handleSearchParamChange('status', value === '启用' ? 'active' : 'inactive')}
            >
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={8} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" onClick={handleReset}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>搜索</Button>
              <Button 
                icon={<ReloadOutlined />} 
                onClick={fetchRoomTypes}
                loading={loading}
              >
                刷新
              </Button>
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
          scroll={{ x: 1000 }}
          locale={{
            emptyText: '暂无集团房型数据'
          }}
        />
      )}
      
      {/* 删除确认弹窗 */}
      <Modal
        title="确认删除"
        open={deleteModalVisible}
        onOk={handleConfirmDelete}
        onCancel={() => setDeleteModalVisible(false)}
        okText="确认删除"
        cancelText="取消"
        okButtonProps={{ danger: true }}
      >
        <p>确定要删除该集团房型吗？此操作不可撤销。</p>
      </Modal>
    </div>
  )
}

export default GroupRoomType
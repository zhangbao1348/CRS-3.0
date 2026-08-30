import { useState, useEffect } from 'react'
import { App, Table, Button, Space, Card, Row, Col, Input, Select, Popconfirm } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined,
  CloseCircleOutlined,
  DeleteOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import api from '../../utils/api'

const { Option } = Select

const GroupCancellation = () => {
  const { message } = App.useApp()
  const [cancellationPolicies, setCancellationPolicies] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchName, setSearchName] = useState('')
  const [searchCode, setSearchCode] = useState('')
  const [searchType, setSearchType] = useState('')
  const [searchStatus, setSearchStatus] = useState('')
  const navigate = useNavigate()
  
  // 加载数据
  useEffect(() => {
    loadCancellationPolicies()
  }, [])
  
  const loadCancellationPolicies = async () => {
    setLoading(true)
    try {
      const data = await api.get('/cancellation-policies')
      setCancellationPolicies(data)
    } catch (error) {
      console.error('加载取消政策数据失败:', error)
      message.error('加载取消政策数据失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }
  
  const handleAddCancellation = () => {
    navigate('/group-management/add-edit-cancellation')
  }
  
  const handleEditCancellation = (record) => {
    navigate('/group-management/add-edit-cancellation', { state: { record } })
  }

  /** 删除前二次确认，服务端负责校验房价码引用。 */
  const handleDeleteCancellation = async (record) => {
    try {
      await api.delete(`/cancellation-policies/${record.id}`)
      message.success('取消政策删除成功')
      loadCancellationPolicies()
    } catch (error) {
      message.error(error?.error || error?.message || '取消政策删除失败')
    }
  }
  
  // 搜索过滤
  const filteredPolicies = cancellationPolicies.filter(item => {
    if (searchName && !item.name?.includes(searchName)) return false
    if (searchCode && !item.code?.includes(searchCode)) return false
    if (searchType && item.type !== searchType) return false
    if (searchStatus) {
      const statusMap = { '启用': 'active', '停用': 'inactive' }
      if (item.status !== statusMap[searchStatus]) return false
    }
    return true
  })
  
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
      render: (status) => {
        const statusMap = { 'active': '启用', 'inactive': '停用' }
        const displayStatus = statusMap[status] || status
        return (
          <span style={{ 
            color: status === 'active' || status === '启用' ? '#52c41a' : '#ff4d4f',
            fontWeight: 500
          }}>
            {displayStatus}
          </span>
        )
      }
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditCancellation(record)}>编辑</Button>
          <Popconfirm
            title="确认删除该取消政策？"
            description="已被房价码引用的政策将被系统拒绝删除。"
            okText="确定"
            cancelText="取消"
            okButtonProps={{ danger: true }}
            onConfirm={() => handleDeleteCancellation(record)}
          >
            <Button danger type="link" size="small" icon={<DeleteOutlined />} aria-label={`删除取消政策 ${record.name}`}>删除</Button>
          </Popconfirm>
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
            <Input 
              placeholder="取消政策名称" 
              prefix={<SearchOutlined />} 
              allowClear 
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="取消政策代码" 
              allowClear 
              value={searchCode}
              onChange={(e) => setSearchCode(e.target.value)}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="取消类型" 
              allowClear 
              style={{ width: '100%' }}
              value={searchType}
              onChange={setSearchType}
            >
              <Option value="免费取消">免费取消</Option>
              <Option value="限时扣费">限时扣费</Option>
              <Option value="不可取消">不可取消</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ width: '100%' }}
              value={searchStatus}
              onChange={setSearchStatus}
            >
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" onClick={() => {
                setSearchName('')
                setSearchCode('')
                setSearchType('')
                setSearchStatus('')
              }}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={loadCancellationPolicies}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddCancellation}>
          新增取消政策
        </Button>
      </div>

      {/* 取消政策列表表格 */}
      <Table
        columns={columns}
        dataSource={filteredPolicies}
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

export default GroupCancellation

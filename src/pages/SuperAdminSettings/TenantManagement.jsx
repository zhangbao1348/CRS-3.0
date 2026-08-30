import { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, message, Modal } from 'antd'
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { tenantApi } from '../../utils/api'

const { Option } = Select

const TenantManagement = () => {
  const [tenants, setTenants] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    status: ''
  })
  const navigate = useNavigate()

  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]

  useEffect(() => {
    fetchTenants()
  }, [])

  const fetchTenants = async () => {
    setLoading(true)
    try {
      const response = await tenantApi.getAllTenants()
      if (response.success) {
        const tenantsData = response.data.map(tenant => ({
          id: tenant.id,
          name: tenant.tenantName,
          code: tenant.tenantCode,
          status: tenant.status === 'active' ? '启用' : '停用',
          userCount: 0,
          hotelCount: tenant.hotelCount,
          expireDate: tenant.expireDate,
          contact: tenant.contactName,
          phone: tenant.contactPhone
        }))
        setTenants(tenantsData)
      } else {
        message.error(response.message || '获取租户列表失败')
      }
    } catch (error) {
      console.error('获取租户列表失败:', error)
      message.error('获取租户列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  const handleAdd = () => {
    navigate('/super-admin-settings/tenant-management/add')
  }

  const handleEdit = (record) => {
    navigate(`/super-admin-settings/tenant-management/${record.id}`)
  }

  const handleDelete = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除租户"${record.name}"吗？`,
      onOk: async () => {
        try {
          const response = await tenantApi.deleteTenant(record.id)
          if (response.success) {
            message.success('租户删除成功')
            fetchTenants()
          } else {
            message.error(response.message || '删除租户失败')
          }
        } catch (error) {
          console.error('删除租户失败:', error)
          message.error('删除租户失败，请稍后重试')
        }
      }
    })
  }

  const columns = [
    {
      title: '租户名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '租户代码',
      dataIndex: 'code',
      key: 'code',
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
      title: '用户数',
      dataIndex: 'userCount',
      key: 'userCount',
      width: 100
    },
    {
      title: '酒店数',
      dataIndex: 'hotelCount',
      key: 'hotelCount',
      width: 100
    },
    {
      title: '到期日期',
      dataIndex: 'expireDate',
      key: 'expireDate',
      width: 120
    },
    {
      title: '联系人',
      dataIndex: 'contact',
      key: 'contact',
      width: 100
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      key: 'phone',
      width: 140
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger onClick={() => handleDelete(record)}>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">租户管理</h1>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input
              placeholder="租户名称"
              prefix={<SearchOutlined />}
              allowClear
              value={searchParams.name}
              onChange={(e) => setSearchParams({ ...searchParams, name: e.target.value })}
              style={{
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input
              placeholder="租户代码"
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({ ...searchParams, code: e.target.value })}
              style={{
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
            />
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
              onChange={(value) => setSearchParams({ ...searchParams, status: value })}
            >
              {statusOptions.map(item => (
                <Option key={item.value} value={item.value}>{item.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" onClick={fetchTenants} style={{ height: 32 }}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} style={{ height: 32 }}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAdd}>
          新增租户
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={tenants}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1400 }}
      />
    </div>
  )
}

export default TenantManagement

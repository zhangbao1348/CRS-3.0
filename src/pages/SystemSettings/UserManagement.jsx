import React, { useState, useEffect } from 'react'
import { 
  Table, 
  Button, 
  Space, 
  Card, 
  Row, 
  Col, 
  Input, 
  Select, 
  Modal, 
  Form, 
  Switch,
  Checkbox,
  Tag,
  Popconfirm,
  message,
  Divider
} from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  EyeOutlined,
  DeleteOutlined,
  UserOutlined,
  LockOutlined,
  UnlockOutlined,
  SafetyOutlined
} from '@ant-design/icons'
import { userApi, tenantApi, roleApi } from '../../utils/api'

// 演示模式标志
const DEMO_MODE = false

// 模拟用户数据
const mockUsers = [
  {
    id: 1,
    username: 'admin',
    name: '系统管理员',
    email: 'admin@example.com',
    phone: '13800138000',
    tenantId: null,
    tenantName: '平台',
    roleIds: [1],
    roleNames: ['超级管理员'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-01 00:00:00'
  },
  {
    id: 2,
    username: 'demo',
    name: '演示用户',
    email: 'demo@example.com',
    phone: '13800138001',
    tenantId: 1,
    tenantName: '万豪国际集团',
    roleIds: [2],
    roleNames: ['集团管理员'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-02 00:00:00'
  },
  {
    id: 3,
    username: 'hotel1',
    name: '酒店管理员',
    email: 'hotel1@example.com',
    phone: '13800138002',
    tenantId: 1,
    tenantName: '万豪国际集团',
    roleIds: [3],
    roleNames: ['酒店管理员'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-03 00:00:00'
  },
  {
    id: 4,
    username: 'user1',
    name: '普通用户',
    email: 'user1@example.com',
    phone: '13800138003',
    tenantId: 2,
    tenantName: '希尔顿酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-04 00:00:00'
  },
  {
    id: 5,
    username: 'user2',
    name: '测试用户',
    email: 'user2@example.com',
    phone: '13800138004',
    tenantId: 3,
    tenantName: '洲际酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '停用',
    statusValue: 'inactive',
    createdAt: '2025-01-05 00:00:00'
  },
  {
    id: 6,
    username: 'user3',
    name: '新用户',
    email: 'user3@example.com',
    phone: '13800138005',
    tenantId: 4,
    tenantName: '凯悦酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-06 00:00:00'
  },
  {
    id: 7,
    username: 'user4',
    name: '老用户',
    email: 'user4@example.com',
    phone: '13800138006',
    tenantId: 5,
    tenantName: '雅高酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-07 00:00:00'
  },
  {
    id: 8,
    username: 'user5',
    name: 'VIP用户',
    email: 'user5@example.com',
    phone: '13800138007',
    tenantId: 1,
    tenantName: '万豪国际集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-08 00:00:00'
  },
  {
    id: 9,
    username: 'user6',
    name: '临时用户',
    email: 'user6@example.com',
    phone: '13800138008',
    tenantId: 2,
    tenantName: '希尔顿酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '停用',
    statusValue: 'inactive',
    createdAt: '2025-01-09 00:00:00'
  },
  {
    id: 10,
    username: 'user7',
    name: '高级用户',
    email: 'user7@example.com',
    phone: '13800138009',
    tenantId: 3,
    tenantName: '洲际酒店集团',
    roleIds: [3],
    roleNames: ['酒店管理员'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-10 00:00:00'
  },
  {
    id: 11,
    username: 'user8',
    name: '中级用户',
    email: 'user8@example.com',
    phone: '13800138010',
    tenantId: 4,
    tenantName: '凯悦酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-11 00:00:00'
  },
  {
    id: 12,
    username: 'user9',
    name: '初级用户',
    email: 'user9@example.com',
    phone: '13800138011',
    tenantId: 5,
    tenantName: '雅高酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-12 00:00:00'
  },
  {
    id: 13,
    username: 'user10',
    name: '试用用户',
    email: 'user10@example.com',
    phone: '13800138012',
    tenantId: 1,
    tenantName: '万豪国际集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-13 00:00:00'
  },
  {
    id: 14,
    username: 'user11',
    name: '正式用户',
    email: 'user11@example.com',
    phone: '13800138013',
    tenantId: 2,
    tenantName: '希尔顿酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-14 00:00:00'
  },
  {
    id: 15,
    username: 'user12',
    name: '资深用户',
    email: 'user12@example.com',
    phone: '13800138014',
    tenantId: 3,
    tenantName: '洲际酒店集团',
    roleIds: [3],
    roleNames: ['酒店管理员'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-15 00:00:00'
  },
  {
    id: 16,
    username: 'user13',
    name: '专家用户',
    email: 'user13@example.com',
    phone: '13800138015',
    tenantId: 4,
    tenantName: '凯悦酒店集团',
    roleIds: [2],
    roleNames: ['集团管理员'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-16 00:00:00'
  },
  {
    id: 17,
    username: 'user14',
    name: '大师用户',
    email: 'user14@example.com',
    phone: '13800138016',
    tenantId: 5,
    tenantName: '雅高酒店集团',
    roleIds: [3],
    roleNames: ['酒店管理员'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-17 00:00:00'
  },
  {
    id: 18,
    username: 'user15',
    name: '王者用户',
    email: 'user15@example.com',
    phone: '13800138017',
    tenantId: 1,
    tenantName: '万豪国际集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-18 00:00:00'
  },
  {
    id: 19,
    username: 'user16',
    name: '钻石用户',
    email: 'user16@example.com',
    phone: '13800138018',
    tenantId: 2,
    tenantName: '希尔顿酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-19 00:00:00'
  },
  {
    id: 20,
    username: 'user17',
    name: '白金用户',
    email: 'user17@example.com',
    phone: '13800138019',
    tenantId: 3,
    tenantName: '洲际酒店集团',
    roleIds: [4],
    roleNames: ['普通用户'],
    status: '启用',
    statusValue: 'active',
    createdAt: '2025-01-20 00:00:00'
  }
]

// 模拟租户数据
const mockTenants = [
  { id: 1, tenantCode: 'MARRIOT', tenantName: '万豪国际集团' },
  { id: 2, tenantCode: 'HILTON', tenantName: '希尔顿酒店集团' },
  { id: 3, tenantCode: 'IHG', tenantName: '洲际酒店集团' },
  { id: 4, tenantCode: 'HYATT', tenantName: '凯悦酒店集团' },
  { id: 5, tenantCode: 'ACCOR', tenantName: '雅高酒店集团' }
]

// 模拟角色数据
const mockRoles = [
  { id: 1, roleCode: 'SUPER_ADMIN', roleName: '超级管理员', tenantId: null, tenantName: '平台' },
  { id: 2, roleCode: 'GROUP_ADMIN', roleName: '集团管理员', tenantId: null, tenantName: '平台' },
  { id: 3, roleCode: 'HOTEL_ADMIN', roleName: '酒店管理员', tenantId: null, tenantName: '平台' },
  { id: 4, roleCode: 'USER', roleName: '普通用户', tenantId: null, tenantName: '平台' }
]

const { Option } = Select
const { Group: CheckboxGroup } = Checkbox

const UserManagement = () => {
  const [users, setUsers] = useState([])
  const [tenants, setTenants] = useState([])
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(false)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isPasswordModalVisible, setIsPasswordModalVisible] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  const [passwordUser, setPasswordUser] = useState(null)
  const [searchParams, setSearchParams] = useState({ name: '', username: '', status: '', tenantId: undefined })
  const [form] = Form.useForm()
  const [passwordForm] = Form.useForm()

  useEffect(() => {
    fetchUsers()
    fetchAllTenants()
    fetchAllRoles()
  }, [])

  const fetchUsers = async () => {
    setLoading(true)
    try {
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据
        setUsers(mockUsers)
      } else {
        // 非演示模式下从后端获取数据
        const response = await userApi.getAllUsers(searchParams.tenantId)
        if (response.success) {
          const usersData = response.data.map(user => ({
            ...user,
            status: user.status === 'active' ? '启用' : '停用',
            statusValue: user.status,
            key: user.id
          }))
          setUsers(usersData)
        } else {
          message.error(response.message || '获取用户列表失败')
        }
      }
    } catch (error) {
      console.error('获取用户列表失败:', error)
      message.error('获取用户列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  const fetchAllTenants = async () => {
    try {
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据
        setTenants(mockTenants)
      } else {
        // 非演示模式下从后端获取数据
        const response = await tenantApi.getAllTenants()
        if (response.success) {
          setTenants(response.data)
        }
      }
    } catch (error) {
      console.error('获取租户列表失败:', error)
    }
  }

  const fetchAllRoles = async () => {
    try {
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据
        setRoles(mockRoles)
      } else {
        // 非演示模式下从后端获取数据
        const response = await roleApi.getAllRoles()
        if (response.success) {
          setRoles(response.data)
        }
      }
    } catch (error) {
      console.error('获取角色列表失败:', error)
    }
  }

  const handleAdd = () => {
    setEditingUser(null)
    form.resetFields()
    setIsModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingUser(record)
    form.setFieldsValue({
      username: record.username,
      name: record.name,
      email: record.email,
      phone: record.phone,
      tenantId: record.tenantId,
      roleIds: record.roleIds
    })
    setIsModalVisible(true)
  }

  const handleResetPassword = (record) => {
    setPasswordUser(record)
    passwordForm.resetFields()
    setIsPasswordModalVisible(true)
  }

  const handleDelete = async (record) => {
    try {
      const response = await userApi.deleteUser(record.id)
      if (response.success) {
        message.success('用户删除成功')
        fetchUsers()
      } else {
        message.error(response.message || '删除用户失败')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      message.error('删除用户失败，请稍后重试')
    }
  }

  const handleToggleStatus = async (record) => {
    try {
      const newStatus = record.statusValue === 'active' ? 'inactive' : 'active'
      const response = await userApi.updateUserStatus(record.id, newStatus)
      if (response.success) {
        message.success('用户状态更新成功')
        fetchUsers()
      } else {
        message.error(response.message || '更新用户状态失败')
      }
    } catch (error) {
      console.error('更新用户状态失败:', error)
      message.error('更新用户状态失败，请稍后重试')
    }
  }

  const handleOk = async () => {
    try {
      const values = await form.validateFields()
      const userData = {
        username: values.username,
        name: values.name,
        email: values.email,
        phone: values.phone,
        tenantId: values.tenantId || null,
        roleIds: values.roleIds || []
      }

      let response
      if (editingUser) {
        response = await userApi.updateUser(editingUser.id, userData)
      } else {
        userData.password = values.password
        response = await userApi.createUser(userData)
      }

      if (response.success) {
        message.success(editingUser ? '用户信息已更新' : '用户创建成功')
        setIsModalVisible(false)
        fetchUsers()
      } else {
        message.error(response.message || '保存失败，请稍后重试')
      }
    } catch (error) {
      console.error('保存用户失败:', error)
      message.error('保存失败，请稍后重试')
    }
  }

  const handlePasswordOk = async () => {
    try {
      const values = await passwordForm.validateFields()
      const response = await userApi.resetPassword(passwordUser.id, values.password)
      if (response.success) {
        message.success('密码重置成功')
        setIsPasswordModalVisible(false)
      } else {
        message.error(response.message || '重置密码失败')
      }
    } catch (error) {
      console.error('重置密码失败:', error)
      message.error('重置密码失败，请稍后重试')
    }
  }

  const columns = [
    {
      title: '归属',
      dataIndex: 'tenantName',
      key: 'tenantName',
      width: 120,
      render: (tenantName) => (
        <span style={{
          color: tenantName === '平台' ? '#1890ff' : '#52c41a',
          fontWeight: 500
        }}>
          {tenantName}
        </span>
      )
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
      render: (text) => (
        <Space>
          <UserOutlined />
          <span>{text}</span>
        </Space>
      )
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 120
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 200
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 130
    },
    {
      title: '角色',
      dataIndex: 'roleNames',
      key: 'roleNames',
      width: 200,
      render: (roleNames) => (
        <Space wrap size={[0, 4]}>
          {roleNames?.map((role, index) => (
            <Tag key={index} size="small" color="purple">
              {role}
            </Tag>
          ))}
        </Space>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status, record) => (
        <Tag color={status === '启用' ? 'green' : 'red'}>
          {status}
        </Tag>
      )
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170
    },
    {
      title: '操作',
      key: 'action',
      width: 280,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" size="small" icon={<LockOutlined />} onClick={() => handleResetPassword(record)}>
            重置密码
          </Button>
          <Button 
            type="link" 
            size="small" 
            icon={record.status === '启用' ? <UnlockOutlined /> : <LockOutlined />}
            onClick={() => handleToggleStatus(record)}
          >
            {record.status === '启用' ? '禁用' : '启用'}
          </Button>
          <Popconfirm
            title="确认删除该用户吗？"
            onConfirm={() => handleDelete(record)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <UserOutlined />
        用户管理
      </h1>
      
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="用户名" 
              prefix={<SearchOutlined />} 
              allowClear 
              value={searchParams.username}
              onChange={(e) => setSearchParams({ ...searchParams, username: e.target.value })}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="姓名" 
              allowClear 
              value={searchParams.name}
              onChange={(e) => setSearchParams({ ...searchParams, name: e.target.value })}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.status}
              onChange={(value) => setSearchParams({ ...searchParams, status: value })}
            >
              <Option value="active">启用</Option>
              <Option value="inactive">禁用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="归属" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.tenantId}
              onChange={(value) => setSearchParams({ ...searchParams, tenantId: value })}
            >
              <Option value={null}>平台</Option>
              {tenants.map(tenant => (
                <Option key={tenant.id} value={tenant.id}>{tenant.tenantName}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12}>
            <Space>
              <Button type="default" onClick={fetchUsers}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={fetchUsers}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAdd}>
          新增用户
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={users}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1600 }}
      />

      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        width={800}
        okText="确认"
        cancelText="取消"
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            status: 'active'
          }}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="归属"
                name="tenantId"
              >
                <Select placeholder="选择归属（不选则为平台用户）" allowClear>
                  <Option value={null}>平台</Option>
                  {tenants.map(tenant => (
                    <Option key={tenant.id} value={tenant.id}>{tenant.tenantName}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="用户名"
                name="username"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input placeholder="请输入用户名" disabled={!!editingUser} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="姓名"
                name="name"
                rules={[{ required: true, message: '请输入姓名' }]}
              >
                <Input placeholder="请输入姓名" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="邮箱"
                name="email"
                rules={[
                  { required: true, message: '请输入邮箱' },
                  { type: 'email', message: '请输入有效的邮箱地址' }
                ]}
              >
                <Input placeholder="请输入邮箱" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="手机号"
                name="phone"
              >
                <Input placeholder="请输入手机号" />
              </Form.Item>
            </Col>
            <Col span={12}>
              {!editingUser && (
                <Form.Item
                  label="密码"
                  name="password"
                  rules={[{ required: true, message: '请输入密码' }]}
                >
                  <Input.Password placeholder="请输入密码" />
                </Form.Item>
              )}
            </Col>
          </Row>
          
          <Divider />
          
          <Form.Item
            label="角色"
            name="roleIds"
            rules={[{ required: true, message: '请选择角色' }]}
          >
            <CheckboxGroup>
              <Row gutter={16}>
                {roles.map(role => (
                  <Col span={8} key={role.id}>
                    <Checkbox value={role.id}>
                      {role.roleName}
                      {role.tenantName && role.tenantName !== '平台' && (
                        <span style={{ color: '#999', marginLeft: 8 }}>({role.tenantName})</span>
                      )}
                    </Checkbox>
                  </Col>
                ))}
              </Row>
            </CheckboxGroup>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`重置密码 - ${passwordUser?.name || ''}`}
        open={isPasswordModalVisible}
        onOk={handlePasswordOk}
        onCancel={() => setIsPasswordModalVisible(false)}
        width={500}
        okText="确认"
        cancelText="取消"
      >
        <Form
          form={passwordForm}
          layout="vertical"
        >
          <Form.Item
            label="新密码"
            name="password"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码长度至少6位' }
            ]}
          >
            <Input.Password placeholder="请输入新密码" />
          </Form.Item>
          <Form.Item
            label="确认密码"
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: '请确认密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve()
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'))
                }
              })
            ]}
          >
            <Input.Password placeholder="请再次输入密码" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default UserManagement

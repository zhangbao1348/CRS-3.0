import { useState, useEffect, useMemo } from 'react'
import { 
  Table, 
  Button, 
  Space, 
  Row, 
  Col, 
  Input, 
  Select, 
  Modal, 
  Form, 
  Checkbox,
  Tag,
  Popconfirm,
  App,
  Divider
} from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  UserOutlined,
  LockOutlined,
  UnlockOutlined,
} from '@ant-design/icons'
import { userApi, tenantApi, roleApi } from '../../utils/api'
import { FilterPanel, PageScaffold, TablePanel } from '../../components/ui'

const { Option } = Select
const { Group: CheckboxGroup } = Checkbox

const UserManagement = () => {
  const { message } = App.useApp()
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

  const filteredUsers = useMemo(() => users.filter(user => {
    const username = searchParams.username.trim().toLowerCase()
    const name = searchParams.name.trim().toLowerCase()
    const tenantMatches = searchParams.tenantId === undefined
      || (searchParams.tenantId === '' ? user.tenantId == null : user.tenantId === searchParams.tenantId)
    return (!username || user.username?.toLowerCase().includes(username))
      && (!name || user.name?.toLowerCase().includes(name))
      && (!searchParams.status || user.statusValue === searchParams.status)
      && tenantMatches
  }), [searchParams, users])

  useEffect(() => {
    fetchUsers()
    fetchAllTenants()
    fetchAllRoles()
  }, [])

  const fetchUsers = async () => {
    setLoading(true)
    try {
      const response = await userApi.getAllUsers()
      if (response.success) {
        const usersData = response.data.map(user => ({
          ...user,
          status: user.status === 'active' ? '启用' : '停用',
          statusValue: user.status,
          key: user.id
        }))
        setUsers(usersData)
      } else {
        setUsers([])
        message.error(response.message || '获取用户列表失败')
      }
    } catch (error) {
      message.error(error?.message || '获取用户列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  const fetchAllTenants = async () => {
    try {
      const response = await tenantApi.getAllTenants()
      if (response.success) {
        setTenants(response.data)
      }
    } catch (error) {
      message.error(error?.message || '获取租户列表失败')
    }
  }

  const fetchAllRoles = async () => {
    try {
      const response = await roleApi.getAllRoles()
      if (response.success) {
        setRoles(response.data)
      }
    } catch (error) {
      message.error(error?.message || '获取角色列表失败')
    }
  }

  const handleResetFilters = () => {
    setSearchParams({ name: '', username: '', status: '', tenantId: undefined })
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
      message.error(error?.message || '删除用户失败，请稍后重试')
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
      message.error(error?.message || '更新用户状态失败，请稍后重试')
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
      if (!error?.errorFields) {
        message.error(error?.message || '保存失败，请稍后重试')
      }
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
      if (!error?.errorFields) {
        message.error(error?.message || '重置密码失败，请稍后重试')
      }
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
      width: 180
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 130,
      responsive: ['xxl']
    },
    {
      title: '角色',
      dataIndex: 'roleNames',
      key: 'roleNames',
      width: 150,
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
      render: (status) => (
        <Tag color={status === '启用' ? 'green' : 'red'}>
          {status}
        </Tag>
      )
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170,
      responsive: ['xxl']
    },
    {
      title: '操作',
      key: 'action',
      width: 230,
      fixed: 'right',
      render: (_, record) => (
        <Space size={[4, 4]} wrap>
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
    <PageScaffold
      className="fade-in"
      eyebrow="ACCESS ADMINISTRATION"
      title={<><UserOutlined /> 用户管理</>}
      description="维护用户身份、集团归属、角色和启停状态；密码操作与资料编辑分开处理。"
      actions={<Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增用户</Button>}
    >
      <FilterPanel extra={<Button onClick={handleResetFilters}>重置条件</Button>}>
        <div className="ui-filter-grid">
          <label className="ui-field">
            <span className="ui-field__label">用户名</span>
            <Input 
              aria-label="用户名"
              placeholder="请输入用户名"
              prefix={<SearchOutlined />} 
              allowClear 
              value={searchParams.username}
              onChange={(e) => setSearchParams({ ...searchParams, username: e.target.value })}
            />
          </label>
          <label className="ui-field">
            <span className="ui-field__label">姓名</span>
            <Input 
              aria-label="姓名"
              placeholder="请输入姓名"
              allowClear 
              value={searchParams.name}
              onChange={(e) => setSearchParams({ ...searchParams, name: e.target.value })}
            />
          </label>
          <label className="ui-field">
            <span className="ui-field__label">状态</span>
            <Select 
              aria-label="状态"
              placeholder="全部状态"
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.status}
              onChange={(value) => setSearchParams({ ...searchParams, status: value })}
            >
              <Option value="active">启用</Option>
              <Option value="inactive">禁用</Option>
            </Select>
          </label>
          <label className="ui-field">
            <span className="ui-field__label">归属集团</span>
            <Select 
              aria-label="归属集团"
              placeholder="全部归属"
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.tenantId}
              onChange={(value) => setSearchParams({ ...searchParams, tenantId: value })}
            >
              <Option value="">平台</Option>
              {tenants.map(tenant => (
                <Option key={tenant.id} value={tenant.id}>{tenant.tenantName}</Option>
              ))}
            </Select>
          </label>
        </div>
      </FilterPanel>

      <TablePanel
        title="用户列表"
        description={`当前筛选结果 ${filteredUsers.length} 个用户`}
        actions={<Button type="primary" icon={<SearchOutlined />} onClick={fetchUsers}>执行查询</Button>}
      >
        <Table
        columns={columns}
        dataSource={filteredUsers}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1000 }}
        />
      </TablePanel>

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
                  <Option value="">平台</Option>
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
                  rules={[
                    { required: true, message: '请输入密码' },
                    { min: 6, message: '密码长度至少6位' }
                  ]}
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
    </PageScaffold>
  )
}

export default UserManagement

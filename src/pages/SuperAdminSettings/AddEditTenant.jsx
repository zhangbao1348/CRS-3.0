import { useEffect, useState } from 'react'
import { Card, Row, Col, Input, InputNumber, Select, Button, DatePicker, Space, message, Form, Tabs, Table, Modal, Tag, Checkbox } from 'antd'
import { ArrowLeftOutlined, SaveOutlined, PlusOutlined, EditOutlined, DeleteOutlined, LockOutlined, UnlockOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import dayjs from 'dayjs'
import { tenantApi, userApi, roleApi } from '../../utils/api'

const { Option } = Select
const { Group: CheckboxGroup } = Checkbox

const AddEditTenant = () => {
  const navigate = useNavigate()
  const { id } = useParams()
  const [form] = Form.useForm()
  const [activeTab, setActiveTab] = useState('basic')
  const [users, setUsers] = useState([])
  const [allRoles, setAllRoles] = useState([])
  const [loading, setLoading] = useState(false)
  const [isUserModalVisible, setIsUserModalVisible] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  const [userForm] = Form.useForm()

  const isAddMode = id === 'add'
  const isEditMode = !isAddMode && id !== undefined

  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]

  const getTenantById = async (tenantId) => {
    try {
      const response = await tenantApi.getTenantById(tenantId)
      if (response.success) {
        return response.data
      }
      return null
    } catch (error) {
      console.error('获取租户信息失败:', error)
      return null
    }
  }

  const fetchTenantUsers = async () => {
    setLoading(true)
    try {
      const response = await userApi.getAllUsers(parseInt(id))
      if (response.success) {
        const usersData = response.data.map(user => ({
          ...user,
          status: user.status === 'active' ? '启用' : '停用',
          key: user.id
        }))
        setUsers(usersData)
      }
    } catch (error) {
      console.error('获取用户列表失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const fetchAllRoles = async () => {
    try {
      const response = await roleApi.getAllRoles()
      if (response.success) {
        setAllRoles(response.data)
      }
    } catch (error) {
      console.error('获取角色列表失败:', error)
    }
  }

  useEffect(() => {
    const loadTenantData = async () => {
      if (isEditMode) {
        const tenant = await getTenantById(id)
        if (tenant) {
          form.setFieldsValue({
            name: tenant.tenantName,
            code: tenant.tenantCode,
            status: tenant.status,
            contact: tenant.contactName,
            phone: tenant.contactPhone,
            hotelCount: tenant.hotelCount,
            email: tenant.contactEmail,
            address: tenant.address,
            expireDate: tenant.expireDate ? dayjs(tenant.expireDate) : null
          })
          await fetchTenantUsers()
          await fetchAllRoles()
        } else {
          message.error('租户不存在')
          navigate('/super-admin-settings/tenant-management')
        }
      } else if (isAddMode) {
        form.setFieldsValue({
          status: 'active',
          expireDate: dayjs().add(1, 'year')
        })
        setUsers([])
      }
    }
    loadTenantData()
  }, [isAddMode, isEditMode, id, form, navigate])

  const handleBack = () => {
    navigate('/super-admin-settings/tenant-management')
  }

  const handleSave = async () => {
    try {
      const values = await form.validateFields()
      const tenantData = {
        tenantName: values.name,
        tenantCode: values.code,
        status: values.status,
        expireDate: values.expireDate ? values.expireDate.format('YYYY-MM-DD') : null,
        contactName: values.contact,
        contactPhone: values.phone,
        contactEmail: values.email,
        hotelCount: values.hotelCount,
        address: values.address
      }

      let response
      if (isEditMode) {
        response = await tenantApi.updateTenant(id, tenantData)
      } else {
        response = await tenantApi.createTenant(tenantData)
      }

      if (response.success) {
        message.success(isEditMode ? '租户更新成功' : '租户创建成功')
        setTimeout(() => {
          navigate('/super-admin-settings/tenant-management')
        }, 1000)
      } else {
        message.error(response.message || '保存失败，请稍后重试')
      }
    } catch (error) {
      console.error('保存租户失败:', error)
      message.error('保存失败，请稍后重试')
    }
  }

  const openUserModal = (user = null) => {
    setEditingUser(user)
    if (user) {
      userForm.setFieldsValue({
        username: user.username,
        name: user.name,
        roleIds: user.roleIds,
        phone: user.phone,
        email: user.email,
        status: user.status === '启用' ? 'active' : 'inactive'
      })
    } else {
      userForm.resetFields()
      userForm.setFieldsValue({
        status: 'active'
      })
    }
    setIsUserModalVisible(true)
  }

  const handleUserModalOk = async () => {
    try {
      const values = await userForm.validateFields()
      const userData = {
        username: values.username,
        name: values.name,
        email: values.email,
        phone: values.phone,
        tenantId: parseInt(id),
        roleIds: values.roleIds
      }

      if (editingUser) {
        userData.password = values.password
        const response = await userApi.updateUser(editingUser.id, userData)
        if (response.success) {
          message.success('用户更新成功')
          await fetchTenantUsers()
        } else {
          message.error(response.message || '更新用户失败')
        }
      } else {
        userData.password = values.password
        const response = await userApi.createUser(userData)
        if (response.success) {
          message.success('用户创建成功')
          await fetchTenantUsers()
        } else {
          message.error(response.message || '创建用户失败')
        }
      }

      setIsUserModalVisible(false)
    } catch (error) {
      console.error('保存用户失败:', error)
      message.error('保存失败，请稍后重试')
    }
  }

  const handleToggleStatus = async (record) => {
    try {
      const newStatus = record.status === '启用' ? 'inactive' : 'active'
      const response = await userApi.updateUserStatus(record.id, newStatus)
      if (response.success) {
        message.success('用户状态更新成功')
        await fetchTenantUsers()
      } else {
        message.error(response.message || '更新用户状态失败')
      }
    } catch (error) {
      console.error('更新用户状态失败:', error)
      message.error('更新用户状态失败，请稍后重试')
    }
  }

  const handleDeleteUser = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除用户"${record.name}"吗？`,
      onOk: async () => {
        try {
          const response = await userApi.deleteUser(record.id)
          if (response.success) {
            message.success('用户删除成功')
            await fetchTenantUsers()
          } else {
            message.error(response.message || '删除用户失败')
          }
        } catch (error) {
          console.error('删除用户失败:', error)
          message.error('删除用户失败，请稍后重试')
        }
      }
    })
  }

  const userColumns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 120
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
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 140
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 200
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
      width: 250,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openUserModal(record)}>编辑</Button>
          <Button 
            type="link" 
            size="small" 
            icon={record.status === '启用' ? <UnlockOutlined /> : <LockOutlined />}
            onClick={() => handleToggleStatus(record)}
          >
            {record.status === '启用' ? '禁用' : '启用'}
          </Button>
          <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDeleteUser(record)}>删除</Button>
        </Space>
      )
    }
  ]

  const tenantRoles = allRoles.filter(role => role.tenantId === parseInt(id) || role.tenantName === '平台')

  return (
    <div className="fade-in">
      <h1 className="page-title">{isEditMode ? '编辑租户' : '新增租户'}</h1>

      <Card style={{ maxWidth: 1000 }}>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={[
            {
              key: 'basic',
              label: '基础信息',
              children: (
                <Form
                  form={form}
                  layout="vertical"
                >
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="name"
                        label="租户名称"
                        rules={[{ required: true, message: '请输入租户名称' }]}
                      >
                        <Input placeholder="请输入租户名称" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="code"
                        label="租户代码"
                        rules={[
                          { required: true, message: '请输入租户代码' },
                          { pattern: /^[A-Za-z0-9_]+$/, message: '租户代码只能包含英文字母、数字和下划线' }
                        ]}
                      >
                        <Input placeholder="请输入租户代码" disabled={isEditMode} />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="status"
                        label="状态"
                        rules={[{ required: true, message: '请选择状态' }]}
                      >
                        <Select placeholder="请选择状态">
                          {statusOptions.map(item => (
                            <Option key={item.value} value={item.value}>{item.label}</Option>
                          ))}
                        </Select>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="expireDate"
                        label="到期日期"
                        rules={[{ required: true, message: '请选择到期日期' }]}
                      >
                        <DatePicker
                          style={{ width: '100%' }}
                          placeholder="请选择到期日期"
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="contact"
                        label="联系人"
                        rules={[{ required: true, message: '请输入联系人' }]}
                      >
                        <Input placeholder="请输入联系人" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="phone"
                        label="联系电话"
                        rules={[
                          { required: true, message: '请输入联系电话' },
                          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
                        ]}
                      >
                        <Input placeholder="请输入联系电话" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="hotelCount"
                        label="酒店数量"
                        rules={[
                          { type: 'number', min: 0, message: '酒店数量必须大于等于0' }
                        ]}
                      >
                        <InputNumber style={{ width: '100%' }} min={0} placeholder="请输入酒店数量" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Form.Item
                    name="email"
                    label="电子邮箱"
                    rules={[
                      { type: 'email', message: '请输入正确的邮箱格式' }
                    ]}
                  >
                    <Input placeholder="请输入电子邮箱" />
                  </Form.Item>
                  <Form.Item
                    name="address"
                    label="地址"
                  >
                    <Input.TextArea rows={3} placeholder="请输入地址" />
                  </Form.Item>
                </Form>
              )
            },
            {
              key: 'users',
              label: '用户管理',
              children: (
                <div>
                  {isEditMode ? (
                    <>
                      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
                        <Button type="primary" icon={<PlusOutlined />} onClick={() => openUserModal()}>
                          新增用户
                        </Button>
                      </div>
                      <Table
                        columns={userColumns}
                        dataSource={users}
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
                    </>
                  ) : (
                    <div style={{ textAlign: 'center', padding: '60px 0', color: '#999' }}>
                      请先保存租户信息后再管理用户
                    </div>
                  )}
                </div>
              )
            }
          ]}
        />

        <div style={{ marginTop: 32, textAlign: 'center' }}>
          <Space size="large">
            <Button
              type="default"
              size="large"
              icon={<ArrowLeftOutlined />}
              onClick={handleBack}
            >
              返回
            </Button>
            <Button
              type="primary"
              size="large"
              icon={<SaveOutlined />}
              onClick={handleSave}
            >
              保存
            </Button>
          </Space>
        </div>
      </Card>

      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={isUserModalVisible}
        onOk={handleUserModalOk}
        onCancel={() => setIsUserModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={700}
      >
        <Form
          form={userForm}
          layout="vertical"
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="username"
                label="用户名"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input placeholder="请输入用户名" disabled={!!editingUser} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="name"
                label="姓名"
                rules={[{ required: true, message: '请输入姓名' }]}
              >
                <Input placeholder="请输入姓名" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="status"
                label="状态"
                rules={[{ required: true, message: '请选择状态' }]}
              >
                <Select placeholder="请选择状态">
                  {statusOptions.map(item => (
                    <Option key={item.value} value={item.value}>{item.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="password"
                label="密码"
                rules={[
                  ...(!editingUser ? [{ required: true, message: '请输入密码' }] : []),
                  { min: 6, message: '密码长度至少6位' }
                ]}
              >
                <Input.Password placeholder={editingUser ? '留空则不修改密码' : '请输入密码'} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="phone"
                label="手机号"
                rules={[
                  { required: true, message: '请输入手机号' },
                  { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
                ]}
              >
                <Input placeholder="请输入手机号" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="email"
                label="邮箱"
                rules={[
                  { required: true, message: '请输入邮箱' },
                  { type: 'email', message: '请输入正确的邮箱格式' }
                ]}
              >
                <Input placeholder="请输入邮箱" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="roleIds"
            label="角色"
            rules={[{ required: true, message: '请选择角色' }]}
          >
            <CheckboxGroup>
              <Row gutter={16}>
                {tenantRoles.map(role => (
                  <Col span={12} key={role.id}>
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
    </div>
  )
}

export default AddEditTenant

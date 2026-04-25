import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, InputNumber, Select, message, Modal, Form, Tree } from 'antd'
import { SearchOutlined, PlusOutlined, EditOutlined, DeleteOutlined, SafetyOutlined } from '@ant-design/icons'
import { roleApi, menuApi, tenantApi } from '../../utils/api'

const { Option } = Select

const RoleManagement = () => {
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchParams, setSearchParams] = useState({ name: '', code: '', status: '' })
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isMenuModalVisible, setIsMenuModalVisible] = useState(false)
  const [menuModalLoading, setMenuModalLoading] = useState(false)
  const [editingRole, setEditingRole] = useState(null)
  const [currentRole, setCurrentRole] = useState(null)
  const [allMenus, setAllMenus] = useState([])
  const [allTenants, setAllTenants] = useState([])
  const [checkedKeys, setCheckedKeys] = useState([])
  const [form] = Form.useForm()

  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]

  useEffect(() => {
    fetchRoles()
    fetchAllMenus()
    fetchAllTenants()
  }, [])

  const fetchAllTenants = async () => {
    try {
      const response = await tenantApi.getAllTenants()
      if (response.success) {
        setAllTenants(response.data)
      }
    } catch (error) {
      console.error('获取租户列表失败:', error)
    }
  }

  const fetchRoles = async () => {
    setLoading(true)
    try {
      const response = await roleApi.getAllRoles()
      if (response.success) {
        const rolesData = response.data.map(role => ({
          id: role.id,
          tenantId: role.tenantId,
          tenantName: role.tenantName,
          roleCode: role.roleCode,
          roleName: role.roleName,
          description: role.description,
          status: role.status === 'active' ? '启用' : '停用',
          statusValue: role.status,
          sortOrder: role.sortOrder
        }))
        setRoles(rolesData)
      } else {
        message.error(response.message || '获取角色列表失败')
      }
    } catch (error) {
      console.error('获取角色列表失败:', error)
      message.error('获取角色列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  const fetchAllMenus = async () => {
    try {
      const response = await menuApi.getAllMenus()
      if (response.success) {
        setAllMenus(response.data)
      }
    } catch (error) {
      console.error('获取菜单列表失败:', error)
    }
  }

  const buildMenuTree = (menuList, parentId = 0) => {
    // 特殊处理：当parentId为0时，找到所有parentId为1的菜单作为根菜单的子菜单
    if (parentId === 0) {
      const rootMenu = menuList.find(menu => menu.parentId === 0);
      if (rootMenu) {
        const rootChildren = menuList
          .filter(menu => menu.parentId === 1)
          .sort((a, b) => a.sortOrder - b.sortOrder)
          .map(menu => ({
            title: menu.menuName,
            key: menu.id,
            children: buildMenuTree(menuList, menu.id)
          }));
        return [{
          title: rootMenu.menuName,
          key: rootMenu.id,
          children: rootChildren
        }];
      }
      return [];
    }
    return menuList
      .filter(menu => menu.parentId === parentId)
      .sort((a, b) => a.sortOrder - b.sortOrder)
      .map(menu => ({
        title: menu.menuName,
        key: menu.id,
        children: buildMenuTree(menuList, menu.id)
      }))
  }

  const handleAdd = () => {
    setEditingRole(null)
    form.resetFields()
    form.setFieldsValue({ status: 'active', tenantId: null })
    setIsModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingRole(record)
    form.setFieldsValue({
      roleCode: record.roleCode,
      roleName: record.roleName,
      description: record.description,
      status: record.statusValue,
      sortOrder: record.sortOrder,
      tenantId: record.tenantId
    })
    setIsModalVisible(true)
  }

  const handleDelete = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除角色"${record.roleName}"吗？`,
      onOk: async () => {
        try {
          const response = await roleApi.deleteRole(record.id)
          if (response.success) {
            message.success('角色删除成功')
            fetchRoles()
          } else {
            message.error(response.message || '删除角色失败')
          }
        } catch (error) {
          console.error('删除角色失败:', error)
          message.error('删除角色失败，请稍后重试')
        }
      }
    })
  }

  const handleAssignMenu = async (record) => {
    setCurrentRole(record)
    setMenuModalLoading(true)
    setCheckedKeys([])
    try {
      const response = await roleApi.getRoleMenus(record.id)
      if (response.success && response.data) {
        console.log('当前角色已分配的菜单ID:', response.data)
        setCheckedKeys(Array.isArray(response.data) ? response.data : [])
      } else {
        setCheckedKeys([])
      }
    } catch (error) {
      console.error('获取角色菜单失败:', error)
      message.error('加载角色菜单失败')
      setCheckedKeys([])
    } finally {
      setMenuModalLoading(false)
      setIsMenuModalVisible(true)
    }
  }

  const handleMenuModalOk = async () => {
    try {
      const response = await roleApi.assignMenus(currentRole.id, checkedKeys)
      if (response.success) {
        message.success('菜单分配成功')
        setIsMenuModalVisible(false)
      } else {
        message.error(response.message || '菜单分配失败')
      }
    } catch (error) {
      console.error('菜单分配失败:', error)
      message.error('菜单分配失败，请稍后重试')
    }
  }

  const handleCheck = (checkedKeysObj) => {
    if (Array.isArray(checkedKeysObj)) {
      setCheckedKeys(checkedKeysObj)
    } else if (checkedKeysObj && checkedKeysObj.checked) {
      setCheckedKeys(checkedKeysObj.checked)
    }
  }

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()
      const roleData = {
        roleCode: values.roleCode,
        roleName: values.roleName,
        description: values.description,
        status: values.status,
        sortOrder: values.sortOrder || 0,
        tenantId: values.tenantId || null
      }

      let response
      if (editingRole) {
        response = await roleApi.updateRole(editingRole.id, roleData)
      } else {
        response = await roleApi.createRole(roleData)
      }

      if (response.success) {
        message.success(editingRole ? '角色更新成功' : '角色创建成功')
        setIsModalVisible(false)
        fetchRoles()
      } else {
        message.error(response.message || '保存失败，请稍后重试')
      }
    } catch (error) {
      console.error('保存角色失败:', error)
      message.error('保存失败，请稍后重试')
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
      title: '角色编码',
      dataIndex: 'roleCode',
      key: 'roleCode',
      width: 150
    },
    {
      title: '角色名称',
      dataIndex: 'roleName',
      key: 'roleName',
      width: 150
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 200
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      width: 100
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
          <Button type="link" size="small" icon={<SafetyOutlined />} onClick={() => handleAssignMenu(record)}>分配菜单</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger onClick={() => handleDelete(record)}>删除</Button>
        </Space>
      )
    }
  ]

  const menuTreeData = buildMenuTree(allMenus)

  return (
    <div className="fade-in">
      <h1 className="page-title">角色管理</h1>
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input
              placeholder="角色编码"
              prefix={<SearchOutlined />}
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({ ...searchParams, code: e.target.value })}
              style={{ height: 32, display: 'flex', alignItems: 'center' }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input
              placeholder="角色名称"
              allowClear
              value={searchParams.name}
              onChange={(e) => setSearchParams({ ...searchParams, name: e.target.value })}
              style={{ height: 32, display: 'flex', alignItems: 'center' }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select
              placeholder="状态"
              allowClear
              style={{ width: '100%', height: 32, display: 'flex', alignItems: 'center' }}
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
              <Button type="default" onClick={fetchRoles} style={{ height: 32 }}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} style={{ height: 32 }}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAdd}>
          新增角色
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={roles}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1100 }}
      />
      <Modal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="tenantId"
            label="归属"
          >
            <Select placeholder="选择归属（不选则为平台角色）" allowClear>
              <Option value={null}>平台</Option>
              {allTenants.map(tenant => (
                <Option key={tenant.id} value={tenant.id}>{tenant.tenantName}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="roleCode"
            label="角色编码"
            rules={[
              { required: true, message: '请输入角色编码' },
              { pattern: /^[A-Za-z0-9_]+$/, message: '角色编码只能包含英文字母、数字和下划线' }
            ]}
          >
            <Input placeholder="请输入角色编码" />
          </Form.Item>
          <Form.Item
            name="roleName"
            label="角色名称"
            rules={[{ required: true, message: '请输入角色名称' }]}
          >
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>
          <Form.Item
            name="sortOrder"
            label="排序"
          >
            <InputNumber style={{ width: '100%' }} min={0} placeholder="请输入排序" />
          </Form.Item>
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
        </Form>
      </Modal>
      <Modal
        title={`分配菜单 - ${currentRole?.roleName || ''}`}
        open={isMenuModalVisible}
        onOk={handleMenuModalOk}
        onCancel={() => setIsMenuModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={600}
        confirmLoading={menuModalLoading}
      >
        {menuModalLoading ? (
          <div style={{ textAlign: 'center', padding: '40px' }}>
            加载中...
          </div>
        ) : (
          <div style={{ maxHeight: 500, overflowY: 'auto' }}>
            <Tree
              checkable
              treeData={menuTreeData}
              checkedKeys={checkedKeys}
              onCheck={handleCheck}
              defaultExpandAll
            />
          </div>
        )}
      </Modal>
    </div>
  )
}

export default RoleManagement

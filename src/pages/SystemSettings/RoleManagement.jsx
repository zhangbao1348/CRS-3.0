import { useEffect, useMemo, useState } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, Modal, Form, Tree, Tag, Popconfirm, App, Empty } from 'antd'
import { SearchOutlined, PlusOutlined, EditOutlined, EyeOutlined, DeleteOutlined, SafetyCertificateOutlined, FolderOutlined, FileTextOutlined } from '@ant-design/icons'
import { menuApi, roleApi } from '../../utils/api'

const { Option } = Select
const { DirectoryTree } = Tree

const unwrapList = response => {
  if (Array.isArray(response)) return response
  if (Array.isArray(response?.data)) return response.data
  if (Array.isArray(response?.data?.data)) return response.data.data
  return []
}

/** 将后端菜单注册表转换为权限树，树节点 key 始终使用真实菜单主键。 */
const buildMenuTree = menus => {
  const nodes = new Map(menus.map(menu => [menu.id, {
    title: menu.menuName,
    key: menu.id,
    icon: menu.menuType === 'menu' ? <FolderOutlined /> : <FileTextOutlined />,
    sortOrder: menu.sortOrder || 0,
    children: []
  }]))
  const roots = []
  menus.forEach(menu => {
    const node = nodes.get(menu.id)
    const parent = nodes.get(menu.parentId)
    if (parent && menu.parentId !== menu.id) parent.children.push(node)
    else roots.push(node)
  })
  const sortNodes = list => list
    .sort((a, b) => a.sortOrder - b.sortOrder)
    .map(node => ({ ...node, children: sortNodes(node.children) }))
  return sortNodes(roots)
}

const RoleManagement = () => {
  const { message } = App.useApp()
  const [roles, setRoles] = useState([])
  const [menus, setMenus] = useState([])
  const [loading, setLoading] = useState(false)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [editingRole, setEditingRole] = useState(null)
  const [viewOnly, setViewOnly] = useState(false)
  const [checkedKeys, setCheckedKeys] = useState([])
  const [keyword, setKeyword] = useState('')
  const [statusFilter, setStatusFilter] = useState(undefined)
  const [form] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try {
      const [roleResponse, menuResponse] = await Promise.all([
        roleApi.getAllRoles(), menuApi.getMenusBySystemType('crs')
      ])
      setRoles(unwrapList(roleResponse))
      setMenus(unwrapList(menuResponse))
    } catch (error) {
      setRoles([])
      setMenus([])
      message.error('加载角色权限数据失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadData() }, [])

  const menuTreeData = useMemo(() => buildMenuTree(menus), [menus])
  const filteredRoles = useMemo(() => roles.filter(role => {
    const query = keyword.trim().toLowerCase()
    const matchesKeyword = !query || role.roleName?.toLowerCase().includes(query) || role.roleCode?.toLowerCase().includes(query)
    return matchesKeyword && (!statusFilter || role.status === statusFilter)
  }), [roles, keyword, statusFilter])

  const openRole = async (role, readOnly) => {
    setEditingRole(role)
    setViewOnly(readOnly)
    form.setFieldsValue(role)
    setCheckedKeys([])
    setIsModalVisible(true)
    try {
      setCheckedKeys(unwrapList(await roleApi.getRoleMenus(role.id)))
    } catch (error) {
      message.error('加载角色菜单失败')
    }
  }

  const handleAdd = () => {
    setEditingRole(null)
    setViewOnly(false)
    setCheckedKeys([])
    form.resetFields()
    form.setFieldsValue({ status: 'active' })
    setIsModalVisible(true)
  }

  const handleDelete = async role => {
    try {
      await roleApi.deleteRole(role.id)
      message.success(`角色 ${role.roleName} 已删除`)
      await loadData()
    } catch (error) {
      message.error('删除角色失败，服务器未保存任何变更')
    }
  }

  const handleOk = async () => {
    if (viewOnly) {
      setIsModalVisible(false)
      return
    }
    try {
      const values = await form.validateFields()
      const payload = { ...values, dataScope: editingRole?.dataScope || 'all' }
      const response = editingRole
        ? await roleApi.updateRole(editingRole.id, payload)
        : await roleApi.createRole(payload)
      const roleId = editingRole?.id || response?.data?.id || response?.id
      if (!roleId) throw new Error('角色保存响应缺少主键')
      await roleApi.assignMenus(roleId, checkedKeys.map(Number))
      message.success(editingRole ? '角色信息已更新' : '角色创建成功')
      setIsModalVisible(false)
      await loadData()
    } catch (error) {
      if (!error?.errorFields) {
        message.error(error?.message || '保存角色失败，服务器未完成变更')
      }
    }
  }

  const columns = [
    { title: '角色名称', dataIndex: 'roleName', key: 'roleName', width: 160, render: text => <Space><SafetyCertificateOutlined /><span>{text}</span></Space> },
    { title: '角色代码', dataIndex: 'roleCode', key: 'roleCode', width: 180 },
    { title: '归属', dataIndex: 'tenantName', key: 'tenantName', width: 160, render: value => value || '平台' },
    { title: '描述', dataIndex: 'description', key: 'description' },
    { title: '状态', dataIndex: 'status', key: 'status', width: 90, render: status => <Tag color={status === 'active' ? 'green' : 'red'}>{status === 'active' ? '启用' : '停用'}</Tag> },
    { title: '操作', key: 'action', width: 220, fixed: 'right', render: (_, role) => <Space>
      <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => openRole(role, true)}>查看</Button>
      <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openRole(role, false)}>编辑</Button>
      <Popconfirm title="确认删除该角色吗？" onConfirm={() => handleDelete(role)} okText="确认" cancelText="取消">
        <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
      </Popconfirm>
    </Space> }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title"><SafetyCertificateOutlined /> 角色管理</h1>
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} md={8} lg={6}><Input value={keyword} onChange={event => setKeyword(event.target.value)} placeholder="角色名称/代码" prefix={<SearchOutlined />} allowClear /></Col>
          <Col xs={24} md={8} lg={6}>
            <Select value={statusFilter} onChange={setStatusFilter} placeholder="状态" allowClear style={{ width: '100%' }}>
              <Option value="active">启用</Option><Option value="inactive">停用</Option>
            </Select>
          </Col>
          <Col xs={24} md={8} lg={12}><Space>
            <Button onClick={() => { setKeyword(''); setStatusFilter(undefined) }}>重置</Button>
            <Button icon={<SearchOutlined />} onClick={loadData}>刷新</Button>
          </Space></Col>
        </Row>
      </Card>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增角色</Button>
      </div>
      <Table loading={loading} columns={columns} dataSource={filteredRoles} rowKey="id" locale={{ emptyText: <Empty description="暂无真实角色数据" /> }} pagination={{ pageSize: 10, showSizeChanger: true, showTotal: total => `共 ${total} 条` }} scroll={{ x: 1100 }} />
      <Modal title={viewOnly ? '查看角色' : (editingRole ? '编辑角色' : '新增角色')} open={isModalVisible} onOk={handleOk} onCancel={() => setIsModalVisible(false)} width={900} okText={viewOnly ? '关闭' : '保存'} cancelButtonProps={{ style: viewOnly ? { display: 'none' } : undefined }}>
        <Form form={form} layout="vertical" disabled={viewOnly}>
          <Row gutter={16}>
            <Col span={12}><Form.Item label="角色名称" name="roleName" rules={[{ required: true, message: '请输入角色名称' }]}><Input /></Form.Item></Col>
            <Col span={12}><Form.Item label="角色代码" name="roleCode" rules={[{ required: true, message: '请输入角色代码' }, { pattern: /^[A-Za-z0-9_]+$/, message: '角色代码只能包含英文字母、数字和下划线' }]}><Input disabled={!!editingRole || viewOnly} /></Form.Item></Col>
          </Row>
          <Form.Item label="状态" name="status"><Select><Option value="active">启用</Option><Option value="inactive">停用</Option></Select></Form.Item>
          <Form.Item label="描述" name="description"><Input.TextArea rows={3} /></Form.Item>
          <Form.Item label="菜单权限">
            <div style={{ maxHeight: 400, overflow: 'auto', border: '1px solid #f0f0f0', padding: 16, borderRadius: 4 }}>
              {menuTreeData.length > 0 ? <DirectoryTree checkable={!viewOnly} defaultExpandAll checkedKeys={checkedKeys} onCheck={keys => setCheckedKeys(Array.isArray(keys) ? keys : keys.checked)} treeData={menuTreeData} /> : <Empty description="暂无菜单注册数据" />}
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default RoleManagement

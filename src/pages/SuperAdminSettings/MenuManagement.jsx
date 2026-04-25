import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, InputNumber, Select, message, Modal, Form } from 'antd'
import { SearchOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { menuApi } from '../../utils/api'

const { Option } = Select

const MenuManagement = () => {
  const [menus, setMenus] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchParams, setSearchParams] = useState({ name: '', code: '', status: '' })
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [editingMenu, setEditingMenu] = useState(null)
  const [form] = Form.useForm()

  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]

  const menuTypeOptions = [
    { value: 'menu', label: '菜单' },
    { value: 'button', label: '按钮' }
  ]

  const systemTypeOptions = [
    { value: 'crs', label: 'CRS系统' }
  ]

  useEffect(() => {
    fetchMenus()
  }, [])

  const fetchMenus = async (params = {}) => {
    setLoading(true)
    try {
      const response = await menuApi.getAllMenus(params)
      if (response.success) {
        const menusData = response.data.map(menu => ({
          id: menu.id,
          parentId: menu.parentId,
          menuCode: menu.menuCode,
          menuName: menu.menuName,
          menuType: menu.menuType,
          menuTypeName: menu.menuType === 'menu' ? '菜单' : '按钮',
          path: menu.path,
          component: menu.component,
          icon: menu.icon,
          sortOrder: menu.sortOrder,
          status: menu.status === 'active' ? '启用' : '停用',
          statusValue: menu.status,
          systemType: menu.systemType
        }))
        setMenus(menusData)
      } else {
        message.error(response.message || '获取菜单列表失败')
      }
    } catch (error) {
      console.error('获取菜单列表失败:', error)
      message.error('获取菜单列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  const buildMenuTree = (menuList, parentId = 0) => {
    return menuList
      .filter(menu => menu.parentId === parentId)
      .map(menu => ({
        ...menu,
        children: buildMenuTree(menuList, menu.id)
      }))
  }

  const getParentMenuOptions = () => {
    const options = [{ value: 0, label: '顶级菜单' }]
    const addMenus = (menuList, level = 0) => {
      menuList.forEach(menu => {
        const prefix = '　'.repeat(level)
        options.push({
          value: menu.id,
          label: `${prefix}${menu.menuName}`
        })
        if (menu.children && menu.children.length > 0) {
          addMenus(menu.children, level + 1)
        }
      })
    }
    const menuTree = buildMenuTree(menus)
    addMenus(menuTree)
    return options
  }

  const handleAdd = () => {
    setEditingMenu(null)
    form.resetFields()
    form.setFieldsValue({
      status: 'active',
      systemType: 'crs',
      menuType: 'menu',
      parentId: 0
    })
    setIsModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingMenu(record)
    form.setFieldsValue({
      parentId: record.parentId,
      menuCode: record.menuCode,
      menuName: record.menuName,
      menuType: record.menuType,
      path: record.path,
      component: record.component,
      icon: record.icon,
      sortOrder: record.sortOrder,
      status: record.statusValue,
      systemType: record.systemType
    })
    setIsModalVisible(true)
  }

  const handleDelete = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除菜单"${record.menuName}"吗？`,
      onOk: async () => {
        try {
          const response = await menuApi.deleteMenu(record.id)
          if (response.success) {
            message.success('菜单删除成功')
            fetchMenus()
          } else {
            message.error(response.message || '删除菜单失败')
          }
        } catch (error) {
          console.error('删除菜单失败:', error)
          message.error('删除菜单失败，请稍后重试')
        }
      }
    })
  }

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()
      const menuData = {
        parentId: values.parentId,
        menuCode: values.menuCode,
        menuName: values.menuName,
        menuType: values.menuType,
        path: values.path,
        component: values.component,
        icon: values.icon,
        sortOrder: values.sortOrder || 0,
        status: values.status,
        systemType: values.systemType
      }

      let response
      if (editingMenu) {
        response = await menuApi.updateMenu(editingMenu.id, menuData)
      } else {
        response = await menuApi.createMenu(menuData)
      }

      if (response.success) {
        message.success(editingMenu ? '菜单更新成功' : '菜单创建成功')
        setIsModalVisible(false)
        fetchMenus()
      } else {
        message.error(response.message || '保存失败，请稍后重试')
      }
    } catch (error) {
      console.error('保存菜单失败:', error)
      message.error('保存失败，请稍后重试')
    }
  }

  const columns = [
    {
      title: '菜单编码',
      dataIndex: 'menuCode',
      key: 'menuCode',
      width: 150
    },
    {
      title: '菜单名称',
      dataIndex: 'menuName',
      key: 'menuName',
      width: 150
    },
    {
      title: '类型',
      dataIndex: 'menuTypeName',
      key: 'menuTypeName',
      width: 80
    },
    {
      title: '路径',
      dataIndex: 'path',
      key: 'path',
      width: 150
    },
    {
      title: '组件',
      dataIndex: 'component',
      key: 'component',
      width: 150
    },
    {
      title: '图标',
      dataIndex: 'icon',
      key: 'icon',
      width: 100
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
      width: 80,
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
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger onClick={() => handleDelete(record)}>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">菜单管理</h1>
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input
              placeholder="菜单编码"
              prefix={<SearchOutlined />}
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({ ...searchParams, code: e.target.value })}
              style={{ height: 32, display: 'flex', alignItems: 'center' }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input
              placeholder="菜单名称"
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
              <Button type="default" onClick={() => {
                setSearchParams({ name: '', code: '', status: '' })
                fetchMenus()
              }} style={{ height: 32 }}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={() => {
                const params = {}
                if (searchParams.code) params.menuCode = searchParams.code
                if (searchParams.name) params.menuName = searchParams.name
                if (searchParams.status) params.status = searchParams.status
                fetchMenus(params)
              }} style={{ height: 32 }}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAdd}>
          新增菜单
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={menus}
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
      <Modal
        title={editingMenu ? '编辑菜单' : '新增菜单'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={700}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="parentId"
                label="上级菜单"
              >
                <Select placeholder="请选择上级菜单">
                  {getParentMenuOptions().map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="systemType"
                label="系统类型"
                rules={[{ required: true, message: '请选择系统类型' }]}
              >
                <Select placeholder="请选择系统类型">
                  {systemTypeOptions.map(item => (
                    <Option key={item.value} value={item.value}>{item.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="menuCode"
                label="菜单编码"
                rules={[
                  { required: true, message: '请输入菜单编码' },
                  { pattern: /^[A-Za-z0-9_]+$/, message: '菜单编码只能包含英文字母、数字和下划线' }
                ]}
              >
                <Input placeholder="请输入菜单编码" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="menuName"
                label="菜单名称"
                rules={[{ required: true, message: '请输入菜单名称' }]}
              >
                <Input placeholder="请输入菜单名称" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="menuType"
                label="菜单类型"
                rules={[{ required: true, message: '请选择菜单类型' }]}
              >
                <Select placeholder="请选择菜单类型">
                  {menuTypeOptions.map(item => (
                    <Option key={item.value} value={item.value}>{item.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="icon"
                label="图标"
              >
                <Input placeholder="请输入图标" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="path"
                label="路径"
              >
                <Input placeholder="请输入路径" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="component"
                label="组件"
              >
                <Input placeholder="请输入组件" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="sortOrder"
                label="排序"
              >
                <InputNumber style={{ width: '100%' }} min={0} placeholder="请输入排序" />
              </Form.Item>
            </Col>
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
          </Row>
        </Form>
      </Modal>
    </div>
  )
}

export default MenuManagement

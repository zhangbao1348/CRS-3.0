import React, { useState } from 'react'
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
  Tree, 
  Tag,
  Popconfirm,
  message
} from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  EyeOutlined,
  DeleteOutlined,
  SafetyCertificateOutlined,
  FolderOutlined,
  FileTextOutlined
} from '@ant-design/icons'

const { Option } = Select
const { DirectoryTree } = Tree

const mockRoles = [
  {
    id: 1,
    name: '超级管理员',
    code: 'SUPER_ADMIN',
    description: '系统最高权限，可以访问所有功能',
    status: '启用',
    createTime: '2024-01-01 00:00:00',
    updateTime: '2024-01-01 00:00:00',
    permissions: ['dashboard', 'reservation', 'inventory', 'room-management', 'rate-management', 'rfp', 'channel-management', 'reports', 'group-management', 'system-settings']
  },
  {
    id: 2,
    name: '酒店经理',
    code: 'HOTEL_MANAGER',
    description: '酒店管理员，可以管理酒店的日常运营',
    status: '启用',
    createTime: '2024-01-02 10:00:00',
    updateTime: '2024-01-02 10:00:00',
    permissions: ['dashboard', 'reservation', 'inventory', 'room-management', 'rate-management', 'reports']
  },
  {
    id: 3,
    name: '前台接待',
    code: 'RECEPTION',
    description: '前台接待，可以处理订单和入住',
    status: '启用',
    createTime: '2024-01-03 14:00:00',
    updateTime: '2024-01-03 14:00:00',
    permissions: ['dashboard', 'reservation', 'inventory']
  },
  {
    id: 4,
    name: '财务',
    code: 'FINANCE',
    description: '财务人员，可以查看报表和财务数据',
    status: '启用',
    createTime: '2024-01-04 09:00:00',
    updateTime: '2024-01-04 09:00:00',
    permissions: ['dashboard', 'reports']
  },
  {
    id: 5,
    name: '销售',
    code: 'SALES',
    description: '销售人员，可以管理价格和渠道',
    status: '启用',
    createTime: '2024-01-05 11:00:00',
    updateTime: '2024-01-05 11:00:00',
    permissions: ['dashboard', 'rate-management', 'channel-management', 'reports']
  }
]

const menuTreeData = [
  {
    title: '首页',
    key: 'dashboard',
    icon: <FolderOutlined />,
    children: []
  },
  {
    title: '订单',
    key: 'reservation',
    icon: <FolderOutlined />,
    children: [
      { title: '订单', key: 'reservation-list', icon: <FileTextOutlined /> }
    ]
  },
  {
    title: '库存管理',
    key: 'inventory',
    icon: <FolderOutlined />,
    children: [
      { title: '房控日历', key: 'inventory', icon: <FileTextOutlined /> },
      { title: '房态管理', key: 'room-status', icon: <FileTextOutlined /> }
    ]
  },
  {
    title: '房型管理',
    key: 'room-management',
    icon: <FolderOutlined />,
    children: [
      { title: '房型管理', key: 'room-type', icon: <FileTextOutlined /> }
    ]
  },
  {
    title: '价格计划管理',
    key: 'rate-management',
    icon: <FolderOutlined />,
    children: [
      { title: '价格计划', key: 'rate-plan', icon: <FileTextOutlined /> },
      { title: '基础价格设置', key: 'rack-rate', icon: <FileTextOutlined /> }
    ]
  },
  {
    title: 'RFP',
    key: 'rfp',
    icon: <FolderOutlined />,
    children: []
  },
  {
    title: '渠道管理',
    key: 'channel-management',
    icon: <FolderOutlined />,
    children: [
      { title: '渠道列表', key: 'channel-list', icon: <FileTextOutlined /> },
      { title: '渠道映射', key: 'channel-mapping', icon: <FileTextOutlined /> }
    ]
  },
  {
    title: '数据及报表',
    key: 'reports',
    icon: <FolderOutlined />,
    children: [
      { title: '订单报表', key: 'reservation-reports', icon: <FileTextOutlined /> },
      { title: '入住率报表', key: 'occupancy-reports', icon: <FileTextOutlined /> },
      { title: '数据导出', key: 'data-export', icon: <FileTextOutlined /> }
    ]
  },
  {
    title: '集团管理',
    key: 'group-management',
    icon: <FolderOutlined />,
    children: [
      { title: '酒店管理', key: 'hotel-management', icon: <FileTextOutlined /> },
      { title: '集团房型管理', key: 'group-room-type', icon: <FileTextOutlined /> },
      { title: '集团房价码管理', key: 'group-rate-code', icon: <FileTextOutlined /> },
      { title: '市场码管理', key: 'market-code', icon: <FileTextOutlined /> },
      { title: '渠道码管理', key: 'channel-code', icon: <FileTextOutlined /> },
      { title: '来源码管理', key: 'source-code', icon: <FileTextOutlined /> },
      { title: '税和服务费设置', key: 'tax-setting', icon: <FileTextOutlined /> },
      { title: '包价设置', key: 'group-package-setting', icon: <FileTextOutlined /> },
      { title: '集团担保政策管理', key: 'group-guarantee', icon: <FileTextOutlined /> },
      { title: '集团取消政策管理', key: 'group-cancellation', icon: <FileTextOutlined /> },
      { title: '集团设施管理', key: 'facility-management', icon: <FileTextOutlined /> },
      { title: '档案管理', key: 'archive-management', icon: <FileTextOutlined /> }
    ]
  },
  {
    title: '系统设置',
    key: 'system-settings',
    icon: <FolderOutlined />,
    children: [
      { title: '用户管理', key: 'user-management', icon: <FileTextOutlined /> },
      { title: '角色管理', key: 'role-management', icon: <FileTextOutlined /> }
    ]
  }
]

const RoleManagement = () => {
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [editingRole, setEditingRole] = useState(null)
  const [form] = Form.useForm()
  const [checkedKeys, setCheckedKeys] = useState([])

  const columns = [
    {
      title: '角色名称',
      dataIndex: 'name',
      key: 'name',
      width: 150,
      render: (text) => (
        <Space>
          <SafetyCertificateOutlined />
          <span>{text}</span>
        </Space>
      )
    },
    {
      title: '角色代码',
      dataIndex: 'code',
      key: 'code',
      width: 150
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 250
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
      dataIndex: 'createTime',
      key: 'createTime',
      width: 170
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleView(record)}>
            查看
          </Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除该角色吗？"
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

  const handleAdd = () => {
    setEditingRole(null)
    setCheckedKeys([])
    form.resetFields()
    setIsModalVisible(true)
  }

  const handleView = (record) => {
    setEditingRole(record)
    setCheckedKeys(record.permissions || [])
    form.setFieldsValue(record)
    setIsModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingRole(record)
    setCheckedKeys(record.permissions || [])
    form.setFieldsValue(record)
    setIsModalVisible(true)
  }

  const handleDelete = (record) => {
    message.success(`角色 ${record.name} 已删除`)
  }

  const handleOk = () => {
    form.validateFields().then(values => {
      message.success(editingRole ? '角色信息已更新' : '角色创建成功')
      setIsModalVisible(false)
    })
  }

  const onCheck = (keys) => {
    setCheckedKeys(keys)
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <SafetyCertificateOutlined />
        角色管理
      </h1>
      
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input placeholder="角色名称/代码" prefix={<SearchOutlined />} allowClear />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select placeholder="状态" allowClear style={{ width: '100%' }}>
              <Option value="启用">启用</Option>
              <Option value="禁用">禁用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={18}>
            <Space>
              <Button type="default">重置</Button>
              <Button type="primary" icon={<SearchOutlined />}>搜索</Button>
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
        dataSource={mockRoles}
        rowKey="id"
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1300 }}
      />

      <Modal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        width={900}
        okText="确认"
        cancelText="取消"
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            status: '启用'
          }}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="角色名称"
                name="name"
                rules={[{ required: true, message: '请输入角色名称' }]}
              >
                <Input placeholder="请输入角色名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="角色代码"
                name="code"
                rules={[
                  { required: true, message: '请输入角色代码' },
                  { pattern: /^[A-Za-z0-9_]+$/, message: '角色代码只能包含英文字母、数字和下划线' }
                ]}
              >
                <Input placeholder="请输入角色代码" disabled={!!editingRole} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="状态"
                name="status"
              >
                <Select>
                  <Option value="启用">启用</Option>
                  <Option value="禁用">禁用</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label="描述"
            name="description"
          >
            <Input.TextArea placeholder="请输入角色描述" rows={3} />
          </Form.Item>
          
          <Form.Item
            label="菜单权限"
            name="permissions"
          >
            <div style={{ maxHeight: 400, overflow: 'auto', border: '1px solid #f0f0f0', padding: 16, borderRadius: 4 }}>
              <DirectoryTree
                checkable
                defaultExpandAll
                checkedKeys={checkedKeys}
                onCheck={onCheck}
                treeData={menuTreeData}
              />
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default RoleManagement

import { useEffect, useMemo, useState } from 'react'
import {
  Button,
  Card,
  Col,
  Empty,
  Form,
  Input,
  InputNumber,
  List,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Switch,
  Table,
  Tag,
  Typography,
  message
} from 'antd'
import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  SearchOutlined,
  SettingOutlined
} from '@ant-design/icons'
import { dictionaryApi } from '../../utils/api'

const statusOptions = [
  { label: '启用', value: 'active' },
  { label: '停用', value: 'inactive' }
]

const DictionaryManagement = () => {
  const [typeKeyword, setTypeKeyword] = useState('')
  const [itemKeyword, setItemKeyword] = useState('')
  const [dictionaryTypes, setDictionaryTypes] = useState([])
  const [dictionaryItems, setDictionaryItems] = useState([])
  const [selectedType, setSelectedType] = useState(null)
  const [typeLoading, setTypeLoading] = useState(false)
  const [itemLoading, setItemLoading] = useState(false)
  const [typeModalVisible, setTypeModalVisible] = useState(false)
  const [itemModalVisible, setItemModalVisible] = useState(false)
  const [editingType, setEditingType] = useState(null)
  const [editingItem, setEditingItem] = useState(null)
  const [typeForm] = Form.useForm()
  const [itemForm] = Form.useForm()

  const selectedTypeCode = selectedType?.typeCode

  useEffect(() => {
    loadDictionaryTypes(typeKeyword)
  }, [])

  useEffect(() => {
    if (selectedTypeCode) {
      loadDictionaryItems(selectedTypeCode, itemKeyword)
    } else {
      setDictionaryItems([])
    }
  }, [selectedTypeCode])

  const itemColumns = useMemo(() => ([
    {
      title: '项目名称',
      dataIndex: 'itemName',
      key: 'itemName',
      width: 180
    },
    {
      title: '项目编码',
      dataIndex: 'itemCode',
      key: 'itemCode',
      width: 160
    },
    {
      title: '项目值',
      dataIndex: 'itemValue',
      key: 'itemValue',
      width: 160
    },
    {
      title: '默认项',
      dataIndex: 'isDefault',
      key: 'isDefault',
      width: 90,
      render: (value) => value ? <Tag color="gold">默认</Tag> : '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 90,
      render: (value) => (
        <Tag color={value === 'active' ? 'green' : 'red'}>
          {value === 'active' ? '启用' : '停用'}
        </Tag>
      )
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      width: 80
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description'
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditItem(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除该字典项吗？"
            onConfirm={() => handleDeleteItem(record)}
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
  ]), [])

  const loadDictionaryTypes = async (keyword = '') => {
    setTypeLoading(true)
    try {
      const response = await dictionaryApi.getDictionaryTypes(keyword)
      const list = response?.data || []
      setDictionaryTypes(list)

      if (!list.length) {
        setSelectedType(null)
        setDictionaryItems([])
        return
      }

      if (!selectedType) {
        setSelectedType(list[0])
        return
      }

      const matchedType = list.find((item) => item.id === selectedType.id)
      setSelectedType(matchedType || list[0])
    } catch (error) {
      message.error(error?.message || error?.error || '获取字典类型失败')
    } finally {
      setTypeLoading(false)
    }
  }

  const loadDictionaryItems = async (typeCode, keyword = '') => {
    if (!typeCode) {
      return
    }
    setItemLoading(true)
    try {
      const response = await dictionaryApi.getDictionaryItems(typeCode, keyword)
      setDictionaryItems(response?.data || [])
    } catch (error) {
      message.error(error?.message || error?.error || '获取字典项失败')
    } finally {
      setItemLoading(false)
    }
  }

  const handleSearchTypes = () => {
    loadDictionaryTypes(typeKeyword.trim())
  }

  const handleSearchItems = () => {
    if (!selectedTypeCode) {
      return
    }
    loadDictionaryItems(selectedTypeCode, itemKeyword.trim())
  }

  const handleAddType = () => {
    setEditingType(null)
    typeForm.resetFields()
    typeForm.setFieldsValue({
      status: 'active',
      builtIn: false,
      sortOrder: 0
    })
    setTypeModalVisible(true)
  }

  const handleEditType = (record) => {
    setEditingType(record)
    typeForm.setFieldsValue({
      ...record,
      builtIn: !!record.builtIn
    })
    setTypeModalVisible(true)
  }

  const handleDeleteType = async (record) => {
    try {
      await dictionaryApi.deleteDictionaryType(record.id)
      message.success('字典类型删除成功')
      await loadDictionaryTypes(typeKeyword.trim())
    } catch (error) {
      message.error(error?.error || error?.message || '删除字典类型失败')
    }
  }

  const handleSubmitType = async () => {
    try {
      const values = await typeForm.validateFields()
      if (editingType) {
        await dictionaryApi.updateDictionaryType(editingType.id, values)
        message.success('字典类型更新成功')
      } else {
        await dictionaryApi.createDictionaryType(values)
        message.success('字典类型新增成功')
      }
      setTypeModalVisible(false)
      await loadDictionaryTypes(typeKeyword.trim())
    } catch (error) {
      if (error?.errorFields) {
        return
      }
      message.error(error?.error || error?.message || '保存字典类型失败')
    }
  }

  const handleAddItem = () => {
    if (!selectedTypeCode) {
      message.warning('请先选择字典类型')
      return
    }
    setEditingItem(null)
    itemForm.resetFields()
    itemForm.setFieldsValue({
      typeCode: selectedTypeCode,
      status: 'active',
      isDefault: false,
      sortOrder: 0
    })
    setItemModalVisible(true)
  }

  const handleEditItem = (record) => {
    setEditingItem(record)
    itemForm.setFieldsValue({
      ...record,
      typeCode: selectedTypeCode,
      isDefault: !!record.isDefault
    })
    setItemModalVisible(true)
  }

  const handleDeleteItem = async (record) => {
    try {
      await dictionaryApi.deleteDictionaryItem(record.id)
      message.success('字典项删除成功')
      await loadDictionaryItems(selectedTypeCode, itemKeyword.trim())
    } catch (error) {
      message.error(error?.error || error?.message || '删除字典项失败')
    }
  }

  const handleSubmitItem = async () => {
    try {
      const values = await itemForm.validateFields()
      if (editingItem) {
        await dictionaryApi.updateDictionaryItem(editingItem.id, values)
        message.success('字典项更新成功')
      } else {
        await dictionaryApi.createDictionaryItem(values)
        message.success('字典项新增成功')
      }
      setItemModalVisible(false)
      await loadDictionaryItems(selectedTypeCode, itemKeyword.trim())
    } catch (error) {
      if (error?.errorFields) {
        return
      }
      message.error(error?.error || error?.message || '保存字典项失败')
    }
  }

  return (
    <div style={{ padding: 24 }}>
      <Row gutter={16}>
        <Col span={8}>
          <Card
            title="字典类型"
            extra={(
              <Button type="primary" icon={<PlusOutlined />} onClick={handleAddType}>
                新增类型
              </Button>
            )}
          >
            <Space style={{ width: '100%', marginBottom: 16 }}>
              <Input
                value={typeKeyword}
                onChange={(event) => setTypeKeyword(event.target.value)}
                placeholder="搜索类型名称/编码"
                onPressEnter={handleSearchTypes}
                prefix={<SearchOutlined />}
              />
              <Button onClick={handleSearchTypes}>查询</Button>
            </Space>

            <List
              loading={typeLoading}
              dataSource={dictionaryTypes}
              locale={{ emptyText: <Empty description="暂无字典类型" /> }}
              renderItem={(item) => (
                <List.Item
                  style={{
                    cursor: 'pointer',
                    padding: '12px 8px',
                    background: selectedType?.id === item.id ? '#f0f7ff' : 'transparent',
                    borderRadius: 8,
                    marginBottom: 8
                  }}
                  onClick={() => setSelectedType(item)}
                  actions={[
                    <Button key="edit" type="link" size="small" onClick={(event) => {
                      event.stopPropagation()
                      handleEditType(item)
                    }}>
                      编辑
                    </Button>,
                    <Popconfirm
                      key="delete"
                      title="确认删除该字典类型吗？"
                      onConfirm={(event) => {
                        event?.stopPropagation?.()
                        handleDeleteType(item)
                      }}
                      okText="确认"
                      cancelText="取消"
                    >
                      <Button
                        type="link"
                        size="small"
                        danger
                        onClick={(event) => event.stopPropagation()}
                      >
                        删除
                      </Button>
                    </Popconfirm>
                  ]}
                >
                  <List.Item.Meta
                    avatar={<SettingOutlined style={{ fontSize: 18, color: '#1677ff' }} />}
                    title={(
                      <Space>
                        <span>{item.typeName}</span>
                        <Tag color={item.status === 'active' ? 'green' : 'red'}>
                          {item.status === 'active' ? '启用' : '停用'}
                        </Tag>
                        {item.builtIn ? <Tag color="gold">内置</Tag> : null}
                      </Space>
                    )}
                    description={(
                      <Space direction="vertical" size={2}>
                        <Typography.Text type="secondary">{item.typeCode}</Typography.Text>
                        <Typography.Text type="secondary">{item.description || '暂无描述'}</Typography.Text>
                      </Space>
                    )}
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>

        <Col span={16}>
          <Card
            title={selectedType ? `字典项 - ${selectedType.typeName}` : '字典项'}
            extra={(
              <Space>
                <Button icon={<PlusOutlined />} type="primary" onClick={handleAddItem} disabled={!selectedTypeCode}>
                  新增字典项
                </Button>
              </Space>
            )}
          >
            {!selectedType ? (
              <Empty description="请先选择左侧字典类型" />
            ) : (
              <>
                <Space style={{ width: '100%', marginBottom: 16 }}>
                  <Input
                    value={itemKeyword}
                    onChange={(event) => setItemKeyword(event.target.value)}
                    placeholder="搜索字典项名称/编码"
                    onPressEnter={handleSearchItems}
                    prefix={<SearchOutlined />}
                  />
                  <Button onClick={handleSearchItems}>查询</Button>
                </Space>

                <Table
                  rowKey="id"
                  loading={itemLoading}
                  columns={itemColumns}
                  dataSource={dictionaryItems}
                  pagination={{ pageSize: 10 }}
                  locale={{ emptyText: <Empty description="暂无字典项" /> }}
                />
              </>
            )}
          </Card>
        </Col>
      </Row>

      <Modal
        title={editingType ? '编辑字典类型' : '新增字典类型'}
        open={typeModalVisible}
        onOk={handleSubmitType}
        onCancel={() => setTypeModalVisible(false)}
        destroyOnHidden
      >
        <Form form={typeForm} layout="vertical">
          <Form.Item
            name="typeName"
            label="类型名称"
            rules={[{ required: true, message: '请输入类型名称' }]}
          >
            <Input placeholder="请输入类型名称" />
          </Form.Item>
          <Form.Item
            name="typeCode"
            label="类型编码"
            rules={[
              { required: true, message: '请输入类型编码' },
              { pattern: /^[A-Za-z0-9_]+$/, message: '编码只允许英文字母、数字和下划线' }
            ]}
          >
            <Input placeholder="请输入类型编码" disabled={!!editingType} />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true, message: '请选择状态' }]}>
            <Select
              options={statusOptions}
              placeholder="请选择状态"
            />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="builtIn" label="内置类型" valuePropName="checked">
            <Switch checkedChildren="是" unCheckedChildren="否" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={editingItem ? '编辑字典项' : '新增字典项'}
        open={itemModalVisible}
        onOk={handleSubmitItem}
        onCancel={() => setItemModalVisible(false)}
        destroyOnHidden
      >
        <Form form={itemForm} layout="vertical">
          <Form.Item name="typeCode" label="所属字典类型">
            <Input disabled />
          </Form.Item>
          <Form.Item
            name="itemName"
            label="项目名称"
            rules={[{ required: true, message: '请输入项目名称' }]}
          >
            <Input placeholder="请输入项目名称" />
          </Form.Item>
          <Form.Item
            name="itemCode"
            label="项目编码"
            rules={[
              { required: true, message: '请输入项目编码' },
              { pattern: /^[A-Za-z0-9_]+$/, message: '编码只允许英文字母、数字和下划线' }
            ]}
          >
            <Input placeholder="请输入项目编码" />
          </Form.Item>
          <Form.Item name="itemValue" label="项目值">
            <Input placeholder="不填则默认等于项目编码" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true, message: '请选择状态' }]}>
            <Select
              options={statusOptions}
              placeholder="请选择状态"
            />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="isDefault" label="默认项" valuePropName="checked">
            <Switch checkedChildren="是" unCheckedChildren="否" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default DictionaryManagement

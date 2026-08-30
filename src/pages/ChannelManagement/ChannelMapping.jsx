import { useCallback, useEffect, useState } from 'react'
import { Alert, App, Card, Tabs, Table, Select, Input, Row, Col, Tag, Button, Space, Popconfirm, Form, Modal } from 'antd'
import { LinkOutlined, PlusOutlined, EditOutlined, DeleteOutlined, SettingOutlined } from '@ant-design/icons'
import { channelMappingApi, hotelApi, hotelRoomTypeApi, ratePlanApi, tenantChannelApi } from '../../utils/api'
import { useTenantContext } from '../../contexts/TenantContext.jsx'
import { PageScaffold } from '../../components/ui'

const { Option } = Select

const normalizeList = (response) => {
  if (Array.isArray(response)) return response
  if (Array.isArray(response?.data)) return response.data
  return []
}

const formatError = (error, fallback) => {
  const detail = error?.message || error?.error || fallback
  const traceId = error?.traceId || error?.trace_id
  return traceId ? `${detail}（Trace: ${traceId}）` : detail
}

const ChannelMapping = () => {
  const { message } = App.useApp()
  const { selectedTenant } = useTenantContext()
  const [activeTab, setActiveTab] = useState('hotel')
  const [selectedChannel, setSelectedChannel] = useState(null)
  const [selectedHotel, setSelectedHotel] = useState(null)
  const [channels, setChannels] = useState([])
  const [hotels, setHotels] = useState([])
  const [roomTypes, setRoomTypes] = useState([])
  const [rateCodes, setRateCodes] = useState([])
  const [hotelMappingsData, setHotelMappingsData] = useState([])
  const [roomTypeMappingsData, setRoomTypeMappingsData] = useState([])
  const [rateCodeMappingsData, setRateCodeMappingsData] = useState([])
  const [loading, setLoading] = useState(false)
  const [loadError, setLoadError] = useState('')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isBatchModalVisible, setIsBatchModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [form] = Form.useForm()
  const [batchForm] = Form.useForm()
  const formHotelCode = Form.useWatch('hotelCode', form)

  const refreshMappings = useCallback(async () => {
    const [hotelResult, roomTypeResult, rateCodeResult] = await Promise.all([
      channelMappingApi.listHotels(),
      channelMappingApi.listRoomTypes(),
      channelMappingApi.listRateCodes()
    ])
    setHotelMappingsData(normalizeList(hotelResult))
    setRoomTypeMappingsData(normalizeList(roomTypeResult))
    setRateCodeMappingsData(normalizeList(rateCodeResult))
  }, [])

  const loadPage = useCallback(async () => {
    if (!selectedTenant) return
    setLoading(true)
    setLoadError('')
    try {
      const [channelResult, hotelResult] = await Promise.all([
        tenantChannelApi.getAllChannels(selectedTenant),
        hotelApi.getAllHotels(selectedTenant)
      ])
      setChannels(normalizeList(channelResult).map(item => ({ id: item.id, name: item.channelName, code: item.channelCode })))
      setHotels(normalizeList(hotelResult).map(item => ({ id: item.id, name: item.chineseName, code: item.hotelCode })))
      await refreshMappings()
    } catch (error) {
      const detail = formatError(error, '渠道映射加载失败')
      setLoadError(detail)
      message.error(detail)
    } finally {
      setLoading(false)
    }
  }, [refreshMappings, selectedTenant])

  useEffect(() => {
    setSelectedChannel(null)
    setSelectedHotel(null)
    setSelectedRowKeys([])
    loadPage()
  }, [loadPage])

  useEffect(() => {
    if (!isModalVisible || !formHotelCode || activeTab === 'hotel') {
      setRoomTypes([])
      setRateCodes([])
      return
    }
    let active = true
    Promise.all([
      hotelRoomTypeApi.getHotelRoomTypesByCodeAndStatus(formHotelCode, 'active'),
      ratePlanApi.getRatePlans(formHotelCode)
    ]).then(([roomTypeResult, rateCodeResult]) => {
      if (!active) return
      setRoomTypes(normalizeList(roomTypeResult).map(item => ({ id: item.id, name: item.roomTypeName, code: item.roomTypeCode })))
      setRateCodes(normalizeList(rateCodeResult).map(item => ({ id: item.id, name: item.rateName, code: item.rateCode })))
    }).catch(error => {
      if (active) message.error(formatError(error, '酒店产品数据加载失败'))
    })
    return () => { active = false }
  }, [activeTab, formHotelCode, isModalVisible])

  // 筛选数据（关联查询原则：使用 channelCode/hotelCode，而非 channelId/hotelId）
  const getFilteredData = (data) => {
    let filtered = [...data]
    if (selectedChannel) {
      // 使用 channelCode 过滤，符合CODE关联规范
      filtered = filtered.filter(item => item.channelCode === selectedChannel || channels.find(c => c.code === selectedChannel)?.name === item.channelName)
    }
    if (selectedHotel) {
      // 使用 hotelCode 过滤，符合CODE关联规范
      filtered = filtered.filter(item => item.hotelCode === selectedHotel)
    }
    return filtered
  }

  // 打开新增/编辑模态框
  const handleOpenModal = (record = null) => {
    setEditingRecord(record)
    if (record) {
      form.setFieldsValue(record)
    } else {
      form.resetFields()
    }
    setIsModalVisible(true)
  }

  // 关闭模态框
  const handleCloseModal = () => {
    setIsModalVisible(false)
    setEditingRecord(null)
  }

  // 打开批量设置模态框
  const handleOpenBatchModal = () => {
    batchForm.resetFields()
    setIsBatchModalVisible(true)
  }

  // 关闭批量设置模态框
  const handleCloseBatchModal = () => {
    setIsBatchModalVisible(false)
    setSelectedRowKeys([])
  }

  // 保存映射
  const getTabApi = () => ({
    hotel: { create: channelMappingApi.createHotel, update: channelMappingApi.updateHotel, remove: channelMappingApi.deleteHotel, toggle: channelMappingApi.toggleHotel },
    roomType: { create: channelMappingApi.createRoomType, update: channelMappingApi.updateRoomType, remove: channelMappingApi.deleteRoomType, toggle: channelMappingApi.toggleRoomType },
    rateCode: { create: channelMappingApi.createRateCode, update: channelMappingApi.updateRateCode, remove: channelMappingApi.deleteRateCode, toggle: channelMappingApi.toggleRateCode }
  })[activeTab]

  const handleSave = async () => {
    try {
      const values = await form.validateFields()
      const payload = { ...editingRecord, ...values }
      const tabApi = getTabApi()
      if (editingRecord) await tabApi.update(editingRecord.id, payload)
      else await tabApi.create(payload)
      message.success(editingRecord ? '更新成功' : '新增成功')
      handleCloseModal()
      await refreshMappings()
    } catch (error) {
      if (!error?.errorFields) message.error(formatError(error, '保存映射失败'))
    }
  }

  // 批量保存映射
  const handleBatchSave = async () => {
    try {
      const values = await batchForm.validateFields()
      const { channelCodePrefix, ...otherValues } = values
      const currentData = getCurrentDataSource()
      const selectedRecords = currentData.filter(item => selectedRowKeys.includes(item.id))
      if (selectedRecords.length === 0) {
        message.warning('请先选择要批量设置的记录')
        return
      }
      const tabApi = getTabApi()
      const payloads = selectedRecords.map(item => {
        const targetField = activeTab === 'hotel' ? 'channelHotelCode' : activeTab === 'roomType' ? 'channelRoomTypeCode' : 'channelRateCode'
        return {
          item,
          payload: {
            ...item,
            ...Object.fromEntries(Object.entries(otherValues).filter(([, value]) => value !== undefined && value !== '')),
            [targetField]: channelCodePrefix ? `${channelCodePrefix}_${activeTab === 'hotel' ? item.hotelCode : activeTab === 'roomType' ? item.roomTypeCode : item.rateCode}` : item[targetField]
          }
        }
      })
      const results = await Promise.allSettled(payloads.map(({ item, payload }) => tabApi.update(item.id, payload)))
      const successCount = results.filter(result => result.status === 'fulfilled').length
      if (successCount !== results.length) message.warning(`已完成 ${successCount}/${results.length} 条，其余记录请按 Trace 排查`)
      else message.success(`成功批量设置 ${successCount} 条记录`)
      handleCloseBatchModal()
      await refreshMappings()
    } catch (error) {
      if (!error?.errorFields) message.error(formatError(error, '批量设置失败'))
    }
  }

  // 删除映射
  const handleDelete = async (id) => {
    try {
      await getTabApi().remove(id)
      message.success('删除成功')
      await refreshMappings()
    } catch (error) {
      message.error(formatError(error, '删除映射失败'))
    }
  }

  // 批量删除
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要删除的记录')
      return
    }

    const results = await Promise.allSettled(selectedRowKeys.map(id => getTabApi().remove(id)))
    const successCount = results.filter(result => result.status === 'fulfilled').length
    if (successCount === results.length) message.success(`成功删除 ${successCount} 条记录`)
    else message.warning(`已删除 ${successCount}/${results.length} 条记录`)
    setSelectedRowKeys([])
    await refreshMappings()
  }

  // 切换状态
  const toggleStatus = async (id, currentStatus) => {
    try {
      await getTabApi().toggle(id)
      message.success(`已${currentStatus === 'active' ? '禁用' : '启用'}`)
      await refreshMappings()
    } catch (error) {
      message.error(formatError(error, '状态更新失败'))
    }
  }

  // 批量切换状态
  const handleBatchToggleStatus = async (status) => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要操作的记录')
      return
    }

    const targets = getCurrentDataSource().filter(item => selectedRowKeys.includes(item.id) && item.status !== status)
    const results = await Promise.allSettled(targets.map(item => getTabApi().toggle(item.id)))
    const successCount = results.filter(result => result.status === 'fulfilled').length
    message.success(`成功${status === 'active' ? '启用' : '禁用'} ${successCount} 条记录`)
    await refreshMappings()
  }

  // 表格行选择
  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys) => {
      setSelectedRowKeys(newSelectedRowKeys)
    }
  }

  // 酒店映射列配置
  const hotelColumns = [
    {
      title: '渠道',
      dataIndex: 'channelName',
      key: 'channelName',
      width: 100
    },
    {
      title: '酒店名称',
      dataIndex: 'hotelName',
      key: 'hotelName',
      width: 160
    },
    {
      title: '酒店CODE',
      dataIndex: 'hotelCode',
      key: 'hotelCode',
      width: 120,
      render: (text) => <Tag color="blue">{text}</Tag>
    },
    {
      title: '渠道酒店CODE',
      dataIndex: 'channelHotelCode',
      key: 'channelHotelCode',
      width: 160,
      render: (text) => <Tag color="orange">{text}</Tag>
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status, record) => (
        <Tag 
          color={status === 'active' ? 'green' : 'red'}
          style={{ cursor: 'pointer' }}
          onClick={() => toggleStatus(record.id, status)}
        >
          {status === 'active' ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            size="small"
            onClick={() => handleOpenModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗?"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button 
              type="link" 
              danger 
              icon={<DeleteOutlined />} 
              size="small"
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  // 房型映射列配置
  const roomTypeColumns = [
    {
      title: '渠道',
      dataIndex: 'channelName',
      key: 'channelName',
      width: 100
    },
    {
      title: '酒店',
      dataIndex: 'hotelName',
      key: 'hotelName',
      width: 140
    },
    {
      title: '酒店房型',
      dataIndex: 'roomTypeName',
      key: 'roomTypeName',
      width: 130
    },
    {
      title: '房型CODE',
      dataIndex: 'roomTypeCode',
      key: 'roomTypeCode',
      width: 120,
      render: (text) => <Tag color="blue">{text}</Tag>
    },
    {
      title: '渠道房型名称',
      dataIndex: 'channelRoomTypeName',
      key: 'channelRoomTypeName',
      width: 130
    },
    {
      title: '渠道房型CODE',
      dataIndex: 'channelRoomTypeCode',
      key: 'channelRoomTypeCode',
      width: 160,
      render: (text) => <Tag color="orange">{text}</Tag>
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status, record) => (
        <Tag 
          color={status === 'active' ? 'green' : 'red'}
          style={{ cursor: 'pointer' }}
          onClick={() => toggleStatus(record.id, status)}
        >
          {status === 'active' ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            size="small"
            onClick={() => handleOpenModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗?"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button 
              type="link" 
              danger 
              icon={<DeleteOutlined />} 
              size="small"
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  // 房价码映射列配置
  const rateCodeColumns = [
    {
      title: '渠道',
      dataIndex: 'channelName',
      key: 'channelName',
      width: 100
    },
    {
      title: '酒店',
      dataIndex: 'hotelName',
      key: 'hotelName',
      width: 140
    },
    {
      title: '房价码名称',
      dataIndex: 'rateCodeName',
      key: 'rateCodeName',
      width: 120
    },
    {
      title: '房价CODE',
      dataIndex: 'rateCode',
      key: 'rateCode',
      width: 100,
      render: (text) => <Tag color="blue">{text}</Tag>
    },
    {
      title: '渠道房价名称',
      dataIndex: 'channelRateName',
      key: 'channelRateName',
      width: 130
    },
    {
      title: '渠道房价CODE',
      dataIndex: 'channelRateCode',
      key: 'channelRateCode',
      width: 150,
      render: (text) => <Tag color="orange">{text}</Tag>
    },
    {
      title: '加价率(%)',
      dataIndex: 'markup',
      key: 'markup',
      width: 100,
      render: (markup) => (
        <Tag color={markup > 0 ? 'red' : 'green'}>
          {markup > 0 ? '+' : ''}{markup}%
        </Tag>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status, record) => (
        <Tag 
          color={status === 'active' ? 'green' : 'red'}
          style={{ cursor: 'pointer' }}
          onClick={() => toggleStatus(record.id, status)}
        >
          {status === 'active' ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            size="small"
            onClick={() => handleOpenModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗?"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button 
              type="link" 
              danger 
              icon={<DeleteOutlined />} 
              size="small"
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  // 获取当前Tab的数据源
  const getCurrentDataSource = () => {
    if (activeTab === 'hotel') return getFilteredData(hotelMappingsData)
    if (activeTab === 'roomType') return getFilteredData(roomTypeMappingsData)
    if (activeTab === 'rateCode') return getFilteredData(rateCodeMappingsData)
    return []
  }

  // 获取当前Tab的列配置
  const getCurrentColumns = () => {
    if (activeTab === 'hotel') return hotelColumns
    if (activeTab === 'roomType') return roomTypeColumns
    if (activeTab === 'rateCode') return rateCodeColumns
    return []
  }

  return (
    <PageScaffold
      className="fade-in"
      eyebrow="CHANNEL CODE MAPPING"
      title={<><LinkOutlined /> 渠道映射</>}
      description="维护集团酒店、房型和房价码与外部渠道 CODE 的对应关系；批量操作仅作用于当前选中记录。"
    >
      {loadError && (
        <Alert
          type="error"
          showIcon
          closable
          message="真实映射数据加载失败"
          description={loadError}
          style={{ marginBottom: 16 }}
          action={<Button size="small" onClick={loadPage}>重新加载</Button>}
        />
      )}
      
      <Card className="ui-panel">
        <Row gutter={[16, 16]} align="middle">
          <Col xs={24} sm={12} md={8} lg={6} className="ui-field">
            <span className="ui-field__label">渠道</span>
            <Select
              aria-label="渠道"
              placeholder="全部渠道"
              style={{ width: '100%' }}
              allowClear
              value={selectedChannel}
              onChange={setSelectedChannel}
            >
              {channels.map(channel => (
                // Option value 使用 channelCode（符合CODE关联规范）
                <Option key={channel.code} value={channel.code}>{channel.name}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6} className="ui-field">
            <span className="ui-field__label">酒店</span>
            <Select
              aria-label="酒店"
              placeholder="全部酒店"
              style={{ width: '100%' }}
              allowClear
              value={selectedHotel}
              onChange={setSelectedHotel}
            >
              {hotels.map(hotel => (
                // Option value 使用 hotelCode（符合CODE关联规范）
                <Option key={hotel.code} value={hotel.code}>{hotel.name}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6} className="ui-field ui-field--action">
            <span className="ui-field__label">映射维护</span>
            <Space>
              <Button 
                type="primary" 
                icon={<PlusOutlined />}
                onClick={() => handleOpenModal()}
              >
                新增映射
              </Button>
            </Space>
          </Col>
        </Row>
        {selectedRowKeys.length > 0 && (
          <Row style={{ marginTop: 16, paddingTop: 16, borderTop: '1px solid #f0f0f0' }}>
            <Col>
              <Space>
                <span style={{ color: '#666' }}>已选择 {selectedRowKeys.length} 项</span>
                <Button 
                  icon={<SettingOutlined />}
                  onClick={handleOpenBatchModal}
                >
                  批量设置
                </Button>
                <Button 
                  onClick={() => handleBatchToggleStatus('active')}
                >
                  批量启用
                </Button>
                <Button 
                  onClick={() => handleBatchToggleStatus('inactive')}
                >
                  批量禁用
                </Button>
                <Popconfirm
                  title="确定要批量删除吗?"
                  onConfirm={handleBatchDelete}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button danger>
                    批量删除
                  </Button>
                </Popconfirm>
                <Button onClick={() => setSelectedRowKeys([])}>
                  取消选择
                </Button>
              </Space>
            </Col>
          </Row>
        )}
      </Card>
      
      <Tabs 
        activeKey={activeTab} 
        onChange={(key) => {
          setActiveTab(key)
          setSelectedRowKeys([])
        }}
        type="card" 
        size="large"
        items={[
          { key: 'hotel', label: '酒店CODE映射', children: <Card className="ui-panel"><Table rowSelection={rowSelection} columns={getCurrentColumns()} dataSource={getCurrentDataSource()} loading={loading} rowKey="id" pagination={{ pageSize: 10 }} scroll={{ x: 1200 }} /></Card> },
          { key: 'roomType', label: '房型CODE映射', children: <Card className="ui-panel"><Table rowSelection={rowSelection} columns={getCurrentColumns()} dataSource={getCurrentDataSource()} loading={loading} rowKey="id" pagination={{ pageSize: 10 }} scroll={{ x: 1400 }} /></Card> },
          { key: 'rateCode', label: '房价CODE映射', children: <Card className="ui-panel"><Table rowSelection={rowSelection} columns={getCurrentColumns()} dataSource={getCurrentDataSource()} loading={loading} rowKey="id" pagination={{ pageSize: 10 }} scroll={{ x: 1500 }} /></Card> }
        ]}
      />

      {/* 新增/编辑模态框 */}
      <Modal
        title={editingRecord ? '编辑映射' : '新增映射'}
        open={isModalVisible}
        onOk={handleSave}
        onCancel={handleCloseModal}
        okText="保存"
        cancelText="取消"
        width={600}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="渠道"
                name="channelCode"
                rules={[{ required: true, message: '请选择渠道' }]}
              >
                <Select placeholder="请选择渠道">
                  {channels.map(channel => (
                    <Option key={channel.code} value={channel.code}>{channel.name}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="酒店"
                name="hotelCode"
                rules={[{ required: true, message: '请选择酒店' }]}
              >
                <Select placeholder="请选择酒店">
                  {hotels.map(hotel => (
                    <Option key={hotel.code} value={hotel.code}>{hotel.name}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          {activeTab === 'hotel' && (
            <Form.Item
              label="渠道酒店CODE"
              name="channelHotelCode"
              rules={[{ required: true, message: '请输入渠道酒店CODE' }]}
            >
              <Input placeholder="请输入渠道酒店CODE" />
            </Form.Item>
          )}

          {activeTab === 'roomType' && (
            <>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="酒店房型"
                    name="roomTypeCode"
                    rules={[{ required: true, message: '请选择酒店房型' }]}
                  >
                    <Select placeholder="请选择酒店房型">
                      {roomTypes.map(rt => (
                        <Option key={rt.code} value={rt.code}>{rt.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="渠道房型CODE"
                    name="channelRoomTypeCode"
                    rules={[{ required: true, message: '请输入渠道房型CODE' }]}
                  >
                    <Input placeholder="请输入渠道房型CODE" />
                  </Form.Item>
                </Col>
              </Row>
              <Form.Item
                label="渠道房型名称"
                name="channelRoomTypeName"
                rules={[{ required: true, message: '请输入渠道房型名称' }]}
              >
                <Input placeholder="请输入渠道房型名称" />
              </Form.Item>
            </>
          )}

          {activeTab === 'rateCode' && (
            <>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="房价码"
                    name="rateCode"
                    rules={[{ required: true, message: '请选择房价码' }]}
                  >
                    <Select placeholder="请选择房价码">
                      {rateCodes.map(rc => (
                        <Option key={rc.code} value={rc.code}>{rc.name} ({rc.code})</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="渠道房价CODE"
                    name="channelRateCode"
                    rules={[{ required: true, message: '请输入渠道房价CODE' }]}
                  >
                    <Input placeholder="请输入渠道房价CODE" />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="渠道房价名称"
                    name="channelRateName"
                    rules={[{ required: true, message: '请输入渠道房价名称' }]}
                  >
                    <Input placeholder="请输入渠道房价名称" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="加价率(%)"
                    name="markup"
                    rules={[{ required: true, message: '请输入加价率' }]}
                  >
                    <Input type="number" placeholder="请输入加价率" />
                  </Form.Item>
                </Col>
              </Row>
            </>
          )}
        </Form>
      </Modal>

      {/* 批量设置模态框 */}
      <Modal
        title="批量设置"
        open={isBatchModalVisible}
        onOk={handleBatchSave}
        onCancel={handleCloseBatchModal}
        okText="批量设置"
        cancelText="取消"
        width={600}
      >
        <Form form={batchForm} layout="vertical">
          <div style={{ marginBottom: 16, color: '#666' }}>
            将为选中的 {selectedRowKeys.length} 条记录进行批量设置
          </div>
          
          <Form.Item
            label="渠道CODE前缀（可选）"
            name="channelCodePrefix"
            extra="填写后将自动生成格式：前缀_原CODE"
          >
            <Input placeholder="例如：CTRIP" />
          </Form.Item>

          {activeTab === 'roomType' && (
            <Form.Item
              label="渠道房型名称（可选）"
              name="channelRoomTypeName"
              extra="不填写则保持原值"
            >
              <Input placeholder="请输入渠道房型名称" />
            </Form.Item>
          )}

          {activeTab === 'rateCode' && (
            <>
              <Form.Item
                label="渠道房价名称（可选）"
                name="channelRateName"
                extra="不填写则保持原值"
              >
                <Input placeholder="请输入渠道房价名称" />
              </Form.Item>
              <Form.Item
                label="加价率(%)（可选）"
                name="markup"
                extra="不填写则保持原值"
              >
                <Input type="number" placeholder="请输入加价率" />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </PageScaffold>
  )
}

export default ChannelMapping

import React, { useState } from 'react'
import { Card, Tabs, Table, Select, Input, Row, Col, Tag, Button, Space, Popconfirm, Form, Modal, message, Checkbox } from 'antd'
import { LinkOutlined, PlusOutlined, EditOutlined, DeleteOutlined, SettingOutlined } from '@ant-design/icons'

const { Option } = Select
const { TabPane } = Tabs

// 模拟渠道数据
const channels = [
  { id: 1, name: '携程', code: 'CTRIP' },
  { id: 2, name: '飞猪', code: 'FLIGGY' },
  { id: 3, name: '美团', code: 'MEITUAN' },
  { id: 4, name: '红色加力', code: 'RED_POWER' }
]

// 模拟酒店数据
const hotels = [
  { id: 1, name: '上海宝丽嘉酒店', code: 'SHBLJ001' },
  { id: 2, name: '杭州钓美酒店', code: 'HZDM001' },
  { id: 3, name: '北京王府井酒店', code: 'BJWFJ001' },
  { id: 4, name: '深圳南山酒店', code: 'SZNS001' }
]

// 模拟房型数据
const roomTypes = [
  { id: 1, name: '标准大床房', code: 'STD_KING' },
  { id: 2, name: '豪华大床房', code: 'DELUXE_KING' },
  { id: 3, name: '标准双床房', code: 'STD_TWIN' },
  { id: 4, name: '豪华双床房', code: 'DELUXE_TWIN' },
  { id: 5, name: '行政大床房', code: 'EXECUTIVE_KING' }
]

// 模拟房价码数据
const rateCodes = [
  { id: 1, name: '牌价', code: 'RACK' },
  { id: 2, name: '净价', code: 'NET' },
  { id: 3, name: '企业价', code: 'CORP' },
  { id: 4, name: 'OTA价', code: 'OTA' },
  { id: 5, name: '周末价', code: 'WEEKEND' }
]

// 模拟酒店映射数据
const hotelMappings = [
  {
    id: 1,
    channelId: 1,
    channelName: '携程',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    hotelCode: 'SHBLJ001',
    channelHotelCode: 'CTRIP_SH_BLJ_001',
    status: 'active',
    updatedAt: '2025-12-15 10:30:00'
  },
  {
    id: 2,
    channelId: 1,
    channelName: '携程',
    hotelId: 2,
    hotelName: '杭州钓美酒店',
    hotelCode: 'HZDM001',
    channelHotelCode: 'CTRIP_HZ_DM_001',
    status: 'active',
    updatedAt: '2025-12-15 10:35:00'
  },
  {
    id: 3,
    channelId: 2,
    channelName: '飞猪',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    hotelCode: 'SHBLJ001',
    channelHotelCode: 'FLIGGY_SH_001',
    status: 'active',
    updatedAt: '2025-12-14 15:20:00'
  }
]

// 模拟房型映射数据
const roomTypeMappings = [
  {
    id: 1,
    channelId: 1,
    channelName: '携程',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    roomTypeId: 1,
    roomTypeName: '标准大床房',
    roomTypeCode: 'STD_KING',
    channelRoomTypeCode: 'CTRIP_STD_KING',
    channelRoomTypeName: '携程标准大床房',
    status: 'active',
    updatedAt: '2025-12-15 10:30:00'
  },
  {
    id: 2,
    channelId: 1,
    channelName: '携程',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    roomTypeId: 2,
    roomTypeName: '豪华大床房',
    roomTypeCode: 'DELUXE_KING',
    channelRoomTypeCode: 'CTRIP_DELUXE_KING',
    channelRoomTypeName: '携程豪华大床房',
    status: 'active',
    updatedAt: '2025-12-15 10:35:00'
  },
  {
    id: 3,
    channelId: 2,
    channelName: '飞猪',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    roomTypeId: 1,
    roomTypeName: '标准大床房',
    roomTypeCode: 'STD_KING',
    channelRoomTypeCode: 'FLIGGY_KING',
    channelRoomTypeName: '飞猪大床房',
    status: 'active',
    updatedAt: '2025-12-14 15:20:00'
  }
]

// 模拟房价码映射数据
const rateCodeMappings = [
  {
    id: 1,
    channelId: 1,
    channelName: '携程',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    rateCodeId: 1,
    rateCodeName: '牌价',
    rateCode: 'RACK',
    channelRateCode: 'CTRIP_RACK',
    channelRateName: '携程门市价',
    markup: 0,
    status: 'active',
    updatedAt: '2025-12-15 10:30:00'
  },
  {
    id: 2,
    channelId: 1,
    channelName: '携程',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    rateCodeId: 4,
    rateCodeName: 'OTA价',
    rateCode: 'OTA',
    channelRateCode: 'CTRIP_OTA',
    channelRateName: '携程OTA价',
    markup: 10,
    status: 'active',
    updatedAt: '2025-12-15 10:35:00'
  },
  {
    id: 3,
    channelId: 2,
    channelName: '飞猪',
    hotelId: 1,
    hotelName: '上海宝丽嘉酒店',
    rateCodeId: 1,
    rateCodeName: '牌价',
    rateCode: 'RACK',
    channelRateCode: 'FLIGGY_BASIC',
    channelRateName: '飞猪基础价',
    markup: 5,
    status: 'active',
    updatedAt: '2025-12-14 15:20:00'
  }
]

const ChannelMapping = () => {
  const [activeTab, setActiveTab] = useState('hotel')
  const [selectedChannel, setSelectedChannel] = useState(null)
  const [selectedHotel, setSelectedHotel] = useState(null)
  const [hotelMappingsData, setHotelMappingsData] = useState(hotelMappings)
  const [roomTypeMappingsData, setRoomTypeMappingsData] = useState(roomTypeMappings)
  const [rateCodeMappingsData, setRateCodeMappingsData] = useState(rateCodeMappings)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isBatchModalVisible, setIsBatchModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [form] = Form.useForm()
  const [batchForm] = Form.useForm()

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
  const handleSave = () => {
    form.validateFields().then(values => {
      if (editingRecord) {
        // 编辑模式
        if (activeTab === 'hotel') {
          setHotelMappingsData(prev => prev.map(item => 
            item.id === editingRecord.id ? { ...item, ...values, updatedAt: new Date().toLocaleString('zh-CN') } : item
          ))
        } else if (activeTab === 'roomType') {
          setRoomTypeMappingsData(prev => prev.map(item => 
            item.id === editingRecord.id ? { ...item, ...values, updatedAt: new Date().toLocaleString('zh-CN') } : item
          ))
        } else if (activeTab === 'rateCode') {
          setRateCodeMappingsData(prev => prev.map(item => 
            item.id === editingRecord.id ? { ...item, ...values, updatedAt: new Date().toLocaleString('zh-CN') } : item
          ))
        }
        message.success('更新成功')
      } else {
        // 新增模式
        const newRecord = {
          ...values,
          id: Date.now(),
          channelName: channels.find(c => c.code === values.channelCode)?.name || '',
          hotelName: hotels.find(h => h.code === values.hotelCode)?.name || '',
          hotelCode: values.hotelCode,
          status: 'active',
          updatedAt: new Date().toLocaleString('zh-CN')
        }
        
        if (activeTab === 'hotel') {
          setHotelMappingsData(prev => [...prev, newRecord])
        } else if (activeTab === 'roomType') {
          setRoomTypeMappingsData(prev => [...prev, {
            ...newRecord,
            roomTypeName: roomTypes.find(r => r.code === values.roomTypeCode)?.name || '',
            roomTypeCode: values.roomTypeCode
          }])
        } else if (activeTab === 'rateCode') {
          setRateCodeMappingsData(prev => [...prev, {
            ...newRecord,
            rateCodeName: rateCodes.find(r => r.code === values.rateCode)?.name || '',
            rateCode: values.rateCode
          }])
        }
        message.success('新增成功')
      }
      handleCloseModal()
    })
  }

  // 批量保存映射
  const handleBatchSave = () => {
    batchForm.validateFields().then(values => {
      const { channelId, hotelId, channelCodePrefix, ...otherValues } = values
      const currentData = getCurrentDataSource()
      const selectedRecords = currentData.filter(item => selectedRowKeys.includes(item.id))
      
      if (selectedRecords.length === 0) {
        message.warning('请先选择要批量设置的记录')
        return
      }

      let successCount = 0
      const updateTime = new Date().toLocaleString('zh-CN')

      if (activeTab === 'hotel') {
        setHotelMappingsData(prev => prev.map(item => {
          if (selectedRowKeys.includes(item.id)) {
            successCount++
            return {
              ...item,
              channelHotelCode: channelCodePrefix ? `${channelCodePrefix}_${item.hotelCode}` : item.channelHotelCode,
              updatedAt: updateTime
            }
          }
          return item
        }))
      } else if (activeTab === 'roomType') {
        setRoomTypeMappingsData(prev => prev.map(item => {
          if (selectedRowKeys.includes(item.id)) {
            successCount++
            return {
              ...item,
              channelRoomTypeCode: channelCodePrefix ? `${channelCodePrefix}_${item.roomTypeCode}` : item.channelRoomTypeCode,
              channelRoomTypeName: otherValues.channelRoomTypeName || item.channelRoomTypeName,
              updatedAt: updateTime
            }
          }
          return item
        }))
      } else if (activeTab === 'rateCode') {
        setRateCodeMappingsData(prev => prev.map(item => {
          if (selectedRowKeys.includes(item.id)) {
            successCount++
            return {
              ...item,
              channelRateCode: channelCodePrefix ? `${channelCodePrefix}_${item.rateCode}` : item.channelRateCode,
              channelRateName: otherValues.channelRateName || item.channelRateName,
              markup: otherValues.markup !== undefined ? otherValues.markup : item.markup,
              updatedAt: updateTime
            }
          }
          return item
        }))
      }

      message.success(`成功批量设置 ${successCount} 条记录`)
      handleCloseBatchModal()
    })
  }

  // 删除映射
  const handleDelete = (id) => {
    if (activeTab === 'hotel') {
      setHotelMappingsData(prev => prev.filter(item => item.id !== id))
    } else if (activeTab === 'roomType') {
      setRoomTypeMappingsData(prev => prev.filter(item => item.id !== id))
    } else if (activeTab === 'rateCode') {
      setRateCodeMappingsData(prev => prev.filter(item => item.id !== id))
    }
    message.success('删除成功')
  }

  // 批量删除
  const handleBatchDelete = () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要删除的记录')
      return
    }

    if (activeTab === 'hotel') {
      setHotelMappingsData(prev => prev.filter(item => !selectedRowKeys.includes(item.id)))
    } else if (activeTab === 'roomType') {
      setRoomTypeMappingsData(prev => prev.filter(item => !selectedRowKeys.includes(item.id)))
    } else if (activeTab === 'rateCode') {
      setRateCodeMappingsData(prev => prev.filter(item => !selectedRowKeys.includes(item.id)))
    }
    message.success(`成功删除 ${selectedRowKeys.length} 条记录`)
    setSelectedRowKeys([])
  }

  // 切换状态
  const toggleStatus = (id, currentStatus) => {
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active'
    if (activeTab === 'hotel') {
      setHotelMappingsData(prev => prev.map(item => 
        item.id === id ? { ...item, status: newStatus } : item
      ))
    } else if (activeTab === 'roomType') {
      setRoomTypeMappingsData(prev => prev.map(item => 
        item.id === id ? { ...item, status: newStatus } : item
      ))
    } else if (activeTab === 'rateCode') {
      setRateCodeMappingsData(prev => prev.map(item => 
        item.id === id ? { ...item, status: newStatus } : item
      ))
    }
    message.success(`已${newStatus === 'active' ? '启用' : '禁用'}`)
  }

  // 批量切换状态
  const handleBatchToggleStatus = (status) => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要操作的记录')
      return
    }

    if (activeTab === 'hotel') {
      setHotelMappingsData(prev => prev.map(item => 
        selectedRowKeys.includes(item.id) ? { ...item, status: status } : item
      ))
    } else if (activeTab === 'roomType') {
      setRoomTypeMappingsData(prev => prev.map(item => 
        selectedRowKeys.includes(item.id) ? { ...item, status: status } : item
      ))
    } else if (activeTab === 'rateCode') {
      setRateCodeMappingsData(prev => prev.map(item => 
        selectedRowKeys.includes(item.id) ? { ...item, status: status } : item
      ))
    }
    message.success(`成功${status === 'active' ? '启用' : '禁用'} ${selectedRowKeys.length} 条记录`)
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
    <div className="fade-in">
      <h1 className="page-title">
        <LinkOutlined />
        渠道映射
      </h1>
      
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]} align="middle">
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select
              placeholder="选择渠道"
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
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select
              placeholder="选择酒店"
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
          <Col xs={24} sm={12} md={8} lg={6}>
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
        onChange={setActiveTab} 
        type="card" 
        size="large"
      >
        <TabPane tab="酒店CODE映射" key="hotel">
          <Card>
            <Table
              rowSelection={rowSelection}
              columns={getCurrentColumns()}
              dataSource={getCurrentDataSource()}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              scroll={{ x: 1200 }}
            />
          </Card>
        </TabPane>
        <TabPane tab="房型CODE映射" key="roomType">
          <Card>
            <Table
              rowSelection={rowSelection}
              columns={getCurrentColumns()}
              dataSource={getCurrentDataSource()}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              scroll={{ x: 1400 }}
            />
          </Card>
        </TabPane>
        <TabPane tab="房价CODE映射" key="rateCode">
          <Card>
            <Table
              rowSelection={rowSelection}
              columns={getCurrentColumns()}
              dataSource={getCurrentDataSource()}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              scroll={{ x: 1500 }}
            />
          </Card>
        </TabPane>
      </Tabs>

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
    </div>
  )
}

export default ChannelMapping

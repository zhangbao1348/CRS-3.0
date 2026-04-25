import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, message, Modal } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

const { Option } = Select

const CustomChannelSetting = () => {
  const navigate = useNavigate()
  const [channels, setChannels] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    status: ''
  })

  // 状态选项
  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]

  // 初始化加载渠道列表
  useEffect(() => {
    fetchChannels()
  }, [])

  // 获取渠道列表
  const fetchChannels = async () => {
    setLoading(true)
    try {
      // 模拟数据
      const mockChannels = [
        {
          id: 1,
          name: '自定义携程渠道',
          code: 'CTRIP',
          integrationType: 'swith',
          swithChannel: 'debi',
          status: '启用',
          description: '自定义携程渠道配置'
        },
        {
          id: 2,
          name: '自定义美团渠道',
          code: 'MEITUAN',
          integrationType: 'api',
          apiStandard: 'realtime',
          status: '启用',
          description: '自定义美团渠道配置'
        }
      ]
      setChannels(mockChannels)
    } catch (error) {
      console.error('获取渠道列表失败:', error)
      message.error('获取渠道列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  // 处理搜索
  const handleSearch = async () => {
    setLoading(true)
    try {
      // 模拟搜索功能
      const mockChannels = [
        {
          id: 1,
          name: '自定义OTA渠道',
          code: 'CUSTOM_OTA',
          status: '启用',
          description: '自定义OTA渠道配置'
        }
      ]
      setChannels(mockChannels)
    } catch (error) {
      console.error('搜索渠道失败:', error)
      message.error('搜索失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  // 处理重置
  const handleReset = () => {
    setSearchParams({
      name: '',
      code: '',
      status: ''
    })
    fetchChannels()
  }

  // 处理新增
  const handleAdd = () => {
    navigate('/system-settings/custom-channel-setting/add')
  }

  // 处理编辑
  const handleEdit = (record) => {
    navigate(`/system-settings/custom-channel-setting/edit/${record.id}`)
  }

  // 处理删除
  const handleDelete = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除渠道"${record.name}"吗？`,
      onOk: () => {
        message.success('渠道删除成功')
        fetchChannels()
      }
    })
  }

  // 列配置
  const columns = [
    {
      title: '渠道名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '渠道代码',
      dataIndex: 'code',
      key: 'code',
      width: 180
    },
    {
      title: '对接类型',
      dataIndex: 'integrationType',
      key: 'integrationType',
      width: 150,
      render: (type) => {
        const typeMap = {
          'swith': '通过SWITH通道对接',
          'api': '通过标准API对接'
        }
        return typeMap[type] || '-'
      }
    },
    {
      title: '对接方式',
      dataIndex: 'integrationType',
      key: 'integrationMethod',
      width: 150,
      render: (type, record) => {
        if (type === 'swith') {
          const channelMap = {
            'debi': '德比',
            'changlian': '畅联'
          }
          return channelMap[record.swithChannel] || '-'
        } else if (type === 'api') {
          const apiMap = {
            'realtime': '实时预订API',
            'push': '推送落地API',
            'tmc': 'TMC API'
          }
          return apiMap[record.apiStandard] || '-'
        }
        return '-'
      }
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
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true
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
      <h1 className="page-title">
        自定义渠道设置
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="渠道名称" 
              prefix={<SearchOutlined />} 
              allowClear
              value={searchParams.name}
              onChange={(e) => setSearchParams({...searchParams, name: e.target.value})}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="渠道代码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({...searchParams, code: e.target.value})}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.status || undefined}
              onChange={(value) => setSearchParams({...searchParams, status: value})}
            >
              {statusOptions.map(item => (
                <Option key={item.value} value={item.value}>{item.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" onClick={handleReset} style={{ height: 32 }}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} style={{ height: 32 }}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAdd}>
          新增渠道
        </Button>
      </div>

      {/* 渠道列表表格 */}
      <Table
        columns={columns}
        dataSource={channels}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 1300 }}
      />
    </div>
  )
}

export default CustomChannelSetting

import React, { useState, useEffect } from 'react'
import { Card, Form, Input, Select, Button, message, Row, Col, Table, Checkbox } from 'antd'
import { SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import axios from 'axios'

const { Option } = Select

const AddCustomChannel = () => {
  const navigate = useNavigate()
  const { id } = useParams()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [integrationType, setIntegrationType] = useState('')
  const [apiStandard, setApiStandard] = useState('')
  const [apiTableData, setApiTableData] = useState([])
  const [apiKey, setApiKey] = useState('')

  // 状态选项
  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]

  // 生成API秘钥
  const generateApiKey = () => {
    const timestamp = Date.now().toString(36)
    const randomStr = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
    const apiKey = `CRS-${timestamp.toUpperCase()}-${randomStr.toUpperCase().substring(0, 16)}`
    return apiKey
  }

  // 对接类型选项
  const integrationTypeOptions = [
    { value: 'swith', label: '通过SWITH通道对接' },
    { value: 'api', label: '通过标准API对接' }
  ]

  // SWITH通道选项
  const swithChannelOptions = [
    { value: 'debi', label: '德比' },
    { value: 'changlian', label: '畅联' }
  ]

  // 标准API选项
  const apiStandardOptions = [
    { value: 'realtime', label: '实时预订API' },
    { value: 'push', label: '推送落地API' },
    { value: 'tmc', label: 'TMC API' }
  ]

  // 集团渠道码数据（与集团管理下的渠道代码一致）
  const groupChannelCodes = [
    {
      key: '1',
      title: '在线渠道',
      code: 'ONLINE',
      children: [
        {
          key: '1-1',
          title: 'OTA渠道',
          code: 'OTA',
          children: [
            { key: '1-1-1', title: '携程', code: 'CTRIP' },
            { key: '1-1-2', title: '美团', code: 'MEITUAN' },
            { key: '1-1-3', title: '飞猪', code: 'FLIGGY' }
          ]
        },
        {
          key: '1-2',
          title: '直销渠道',
          code: 'DIRECT',
          children: [
            { key: '1-2-1', title: '官网', code: 'WEBSITE' },
            { key: '1-2-2', title: 'APP', code: 'APP' },
            { key: '1-2-3', title: '微信小程序', code: 'WXMINI' }
          ]
        }
      ]
    },
    {
      key: '2',
      title: '线下渠道',
      code: 'OFFLINE',
      children: [
        { key: '2-1', title: '旅行社', code: 'TRAVEL' },
        { key: '2-2', title: '企业协议', code: 'CORP' }
      ]
    }
  ]

  // 从树形结构中提取所有叶子节点作为渠道代码选项
  const extractLeafNodes = (nodes, options = []) => {
    nodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        extractLeafNodes(node.children, options)
      } else {
        options.push({
          value: node.code,
          label: `${node.title} (${node.code})`
        })
      }
    })
    return options
  }

  // 渠道代码选项（只包含叶子节点）
  const channelCodeOptions = extractLeafNodes(groupChannelCodes)

  // API表格数据
  const apiDataConfig = {
    'realtime': [
      { key: '1', required: true, apiName: '查询房态', caller: '渠道', address: '/api/hotel/status' },
      { key: '2', required: true, apiName: '查询房价', caller: '渠道', address: '/api/hotel/price' },
      { key: '3', required: true, apiName: '创建订单', caller: '渠道', address: '/api/order/create' },
      { key: '4', required: false, apiName: '取消订单', caller: '渠道', address: '/api/order/cancel' },
      { key: '5', required: false, apiName: '订单状态查询', caller: '渠道', address: '/api/order/status' }
    ],
    'push': [
      { key: '1', required: true, apiName: '房态推送', caller: 'CRS', address: '/api/push/status' },
      { key: '2', required: true, apiName: '房价推送', caller: 'CRS', address: '/api/push/price' },
      { key: '3', required: true, apiName: '订单推送', caller: '渠道', address: '/api/push/order' },
      { key: '4', required: false, apiName: '订单确认', caller: 'CRS', address: '/api/push/confirm' }
    ],
    'tmc': [
      { key: '1', required: true, apiName: '企业认证', caller: 'TMC', address: '/api/tmc/auth' },
      { key: '2', required: true, apiName: '协议价查询', caller: 'TMC', address: '/api/tmc/rate' },
      { key: '3', required: true, apiName: '差旅预订', caller: 'TMC', address: '/api/tmc/book' },
      { key: '4', required: false, apiName: '差旅审批', caller: 'TMC', address: '/api/tmc/approve' },
      { key: '5', required: false, apiName: '账单查询', caller: 'TMC', address: '/api/tmc/bill' }
    ]
  }

  // 监听接口标准变化，更新API表格数据
  useEffect(() => {
    if (apiStandard && apiDataConfig[apiStandard]) {
      setApiTableData(apiDataConfig[apiStandard])
    } else {
      setApiTableData([])
    }
  }, [apiStandard])

  // 页面加载时生成API秘钥（新增模式）
  useEffect(() => {
    if (!id) {
      const newApiKey = generateApiKey()
      setApiKey(newApiKey)
    }
  }, [id])

  // 加载渠道数据（编辑模式）
  useEffect(() => {
    if (id) {
      loadChannelData()
    }
  }, [id])

  // 加载渠道详情
  const loadChannelData = async () => {
    setLoading(true)
    try {
      // 模拟数据
      const mockChannel = {
        id: id,
        name: '自定义携程渠道',
        code: 'CTRIP',
        integrationType: 'swith',
        swithChannel: 'debi',
        status: 'active',
        description: '自定义携程渠道配置',
        apiKey: 'CRS-KS3VQ3J8-7X9Y2Z4A6B8C0D2E4F6'
      }
      
      form.setFieldsValue({
        name: mockChannel.name,
        code: mockChannel.code,
        status: mockChannel.status,
        description: mockChannel.description,
        integrationType: mockChannel.integrationType,
        swithChannel: mockChannel.swithChannel,
        apiStandard: mockChannel.apiStandard
      })
      setIntegrationType(mockChannel.integrationType || '')
      setApiStandard(mockChannel.apiStandard || '')
      setApiKey(mockChannel.apiKey || '')
    } catch (error) {
      console.error('加载渠道数据失败:', error)
      message.error('加载渠道数据失败，请稍后重试')
      navigate('/system-settings/custom-channel-setting')
    } finally {
      setLoading(false)
    }
  }

  // 处理表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setLoading(true)
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      message.success(id ? '渠道更新成功' : '渠道创建成功')
      navigate('/system-settings/custom-channel-setting')
    } catch (error) {
      console.error('保存渠道失败:', error)
      message.error('保存失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  // 处理返回
  const handleBack = () => {
    navigate('/system-settings/custom-channel-setting')
  }

  return (
    <div className="fade-in">
      <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center' }}>
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={handleBack}
          style={{ marginRight: 16 }}
        >
          返回
        </Button>
        <h1 className="page-title">
          {id ? '编辑自定义渠道' : '新增自定义渠道'}
        </h1>
      </div>
      
      <div style={{ maxWidth: 600, margin: '0 auto' }}>
        <Card>
          <Form
            form={form}
            layout="vertical"
          >
            <Form.Item
              name="name"
              label="渠道名称"
              rules={[{ required: true, message: '请输入渠道名称' }]}
            >
              <Input placeholder="请输入渠道名称" />
            </Form.Item>

            <Form.Item
              name="code"
              label="选择渠道代码"
              rules={[{ required: true, message: '请选择渠道代码' }]}
            >
              <Select placeholder="请选择渠道代码" disabled={!!id}>
                {channelCodeOptions.map(item => (
                  <Option key={item.value} value={item.value}>{item.label}</Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
              name="integrationType"
              label="对接类型"
              rules={[{ required: true, message: '请选择对接类型' }]}
            >
              <Select 
                placeholder="请选择对接类型"
                onChange={(value) => setIntegrationType(value)}
              >
                {integrationTypeOptions.map(item => (
                  <Option key={item.value} value={item.value}>{item.label}</Option>
                ))}
              </Select>
            </Form.Item>

            {integrationType === 'swith' && (
              <Form.Item
                name="swithChannel"
                label="通道选择"
                rules={[{ required: true, message: '请选择通道' }]}
              >
                <Select placeholder="请选择通道">
                  {swithChannelOptions.map(item => (
                    <Option key={item.value} value={item.value}>{item.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            )}

            {integrationType === 'api' && (
              <>
                <Form.Item
                  name="apiStandard"
                  label="接口标准选择"
                  rules={[{ required: true, message: '请选择接口标准' }]}
                >
                  <Select 
                    placeholder="请选择接口标准"
                    onChange={(value) => setApiStandard(value)}
                  >
                    {apiStandardOptions.map(item => (
                      <Option key={item.value} value={item.value}>{item.label}</Option>
                    ))}
                  </Select>
                </Form.Item>
                
                {apiTableData.length > 0 && (
                  <div style={{ marginTop: 16, marginBottom: 24 }}>
                    <h4 style={{ marginBottom: 12, color: '#1890ff' }}>API内容</h4>
                    <Table
                      dataSource={apiTableData}
                      pagination={false}
                      size="small"
                      bordered
                      columns={[
                        {
                          title: '需要',
                          dataIndex: 'required',
                          key: 'required',
                          width: 80,
                          align: 'center',
                          render: (required) => (
                            <Checkbox checked={required} disabled />
                          )
                        },
                        {
                          title: 'API名称',
                          dataIndex: 'apiName',
                          key: 'apiName'
                        },
                        {
                          title: '调用方',
                          dataIndex: 'caller',
                          key: 'caller',
                          width: 100
                        },
                        {
                          title: '接口地址',
                          dataIndex: 'address',
                          key: 'address'
                        }
                      ]}
                    />
                  </div>
                )}
              </>
            )}

            <Form.Item
              label="API秘钥"
            >
              <Input 
                value={apiKey}
                disabled
                placeholder="API秘钥将自动生成"
              />
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

            <Form.Item
              name="description"
              label="描述"
            >
              <Input.TextArea rows={4} placeholder="请输入渠道描述" />
            </Form.Item>
          </Form>
          
          <div style={{ marginTop: 24, textAlign: 'center' }}>
            <Button icon={<ArrowLeftOutlined />} onClick={handleBack} style={{ marginRight: 16 }}>
              返回
            </Button>
            <Button type="primary" icon={<SaveOutlined />} onClick={handleSubmit} loading={loading}>
              保存
            </Button>
          </div>
        </Card>
      </div>
    </div>
  )
}

export default AddCustomChannel

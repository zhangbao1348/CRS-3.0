import { useState, useEffect } from 'react'
import { App, Form, Input, Select, Button, Card, Row, Col, InputNumber, TimePicker } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import api from '../../utils/api'
import dayjs from 'dayjs'

const { Option } = Select

const AddEditCancellation = () => {
  const [form] = Form.useForm()
  const { message } = App.useApp()
  const [loading, setLoading] = useState(false)
  const [cancellationType, setCancellationType] = useState('')
  const navigate = useNavigate()
  const location = useLocation()
  
  // 获取路由参数中的编辑数据
  const record = location.state?.record
  const isEditing = !!record
  
  // 取消类型选项
  const cancellationTypeOptions = [
    { value: '免费取消', label: '免费取消' },
    { value: '限时扣费', label: '限时扣费' },
    { value: '不可取消', label: '不可取消' }
  ]
  
  // 状态选项
  const statusOptions = [
    { value: '启用', label: '启用' },
    { value: '停用', label: '停用' }
  ]
  
  // 初始化表单数据
  useEffect(() => {
    if (isEditing && record) {
      // 状态映射：active/inactive -> 启用/停用
      const statusMap = { 'active': '启用', 'inactive': '停用' }
      // 时间格式转换
      let timeValue = null
      if (record.cancellationTime) {
        timeValue = dayjs(`2024-01-01 ${record.cancellationTime}`)
      }
      
      form.setFieldsValue({
        name: record.name,
        code: record.code,
        type: record.type,
        cancellationDays: record.cancellationDays,
        cancellationTime: timeValue,
        cancellationFeeType: record.cancellationFeeType,
        status: statusMap[record.status] || record.status,
        description: record.description
      })
      setCancellationType(record.type)
    }
  }, [isEditing, record, form])
  
  // 处理表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setLoading(true)
      
      // 状态映射：启用/停用 -> active/inactive
      const statusMap = { '启用': 'active', '停用': 'inactive' }
      
      // 准备提交数据
      const submitData = {
        name: values.name,
        code: values.code,
        type: values.type,
        cancellationDays: values.cancellationDays || null,
        cancellationTime: values.cancellationTime ? values.cancellationTime.format('HH:mm') : null,
        cancellationFeeType: values.cancellationFeeType || null,
        status: statusMap[values.status] || values.status,
        description: values.description
      }
      
      // 调用后端API
      if (isEditing && record.id) {
        // 编辑模式
        await api.put(`/cancellation-policies/${record.id}`, submitData)
        message.success('取消政策更新成功')
      } else {
        // 创建模式
        await api.post('/cancellation-policies', submitData)
        message.success('取消政策创建成功')
      }
      
      // 保存成功后返回列表页面
      setTimeout(() => {
        navigate('/group-management/group-cancellation')
      }, 1000)
    } catch (error) {
      if (!error?.errorFields) {
        const detail = typeof error === 'string' ? error : (error?.error || error?.message || '未知错误')
        message.error('保存失败: ' + detail)
      }
    } finally {
      setLoading(false)
    }
  }
  
  return (
    <div className="fade-in">
      <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center' }}>
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={() => navigate('/group-management/group-cancellation')}
          style={{ marginRight: 16 }}
        >
          返回
        </Button>
        <h1 className="page-title">
          {isEditing ? '编辑取消政策' : '新增取消政策'}
        </h1>
      </div>
      
      <Card style={{ maxWidth: 800, margin: '0 auto' }}>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="name"
                label="取消政策名称"
                rules={[{ required: true, message: '请输入取消政策名称' }]}
              >
                <Input placeholder="请输入取消政策名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="code"
                label="取消政策代码"
                rules={[
                  { required: true, message: '请输入取消政策代码' },
                  { pattern: /^[A-Za-z0-9_]+$/, message: '取消政策代码仅允许输入英文字母、数字和下划线' }
                ]}
              >
                <Input placeholder="请输入取消政策代码" disabled={isEditing} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="type"
                label="取消类型"
                rules={[{ required: true, message: '请选择取消类型' }]}
              >
                <Select 
                  placeholder="请选择取消类型"
                  onChange={(value) => {
                    setCancellationType(value)
                    if (value !== '限时扣费') {
                      form.setFieldsValue({ cancellationDays: undefined, cancellationTime: undefined, cancellationFeeType: undefined })
                    }
                  }}
                >
                  {cancellationTypeOptions.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          {/* 限时扣费特有字段 */}
          {cancellationType === '限时扣费' && (
            <Row gutter={[8, 8]} style={{ alignItems: 'center' }}>
              <Col>
                <span style={{ whiteSpace: 'nowrap' }}>入住前</span>
              </Col>
              <Col span={3}>
                <Form.Item
                  name="cancellationDays"
                  noStyle
                  rules={[
                    { required: true, message: '请输入天数' },
                    { type: 'integer', min: 1, message: '提前天数必须是大于 0 的整数，请重新输入' }
                  ]}
                >
                  <InputNumber min={1} precision={0} placeholder="天数" style={{ width: '100%' }} />
                </Form.Item>
              </Col>
              <Col>
                <span style={{ whiteSpace: 'nowrap' }}>天，</span>
              </Col>
              <Col span={4}>
                <Form.Item
                  name="cancellationTime"
                  noStyle
                  rules={[{ required: true, message: '请选择时间' }]}
                >
                  <TimePicker style={{ width: '100%' }} placeholder="时间" format="HH:mm" />
                </Form.Item>
              </Col>
              <Col>
                <span style={{ whiteSpace: 'nowrap' }}>前免费取消，</span>
              </Col>
              <Col>
                <span style={{ whiteSpace: 'nowrap' }}>之后扣</span>
              </Col>
              <Col span={4}>
                <Form.Item
                  name="cancellationFeeType"
                  noStyle
                  rules={[{ required: true, message: '请选择扣费类型' }]}
                >
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    <Option value="首晚">首晚</Option>
                    <Option value="全额房费">全额房费</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
          )}
          
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={4} placeholder="请输入取消政策描述" />
          </Form.Item>
          
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="status"
                label="状态"
                initialValue="启用"
                rules={[{ required: true, message: '请选择政策状态' }]}
              >
                <Select placeholder="请选择状态">
                  {statusOptions.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <div style={{ marginTop: 32, display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              icon={<ArrowLeftOutlined />}
              onClick={() => navigate('/group-management/group-cancellation')}
              style={{ marginRight: 12 }}
            >
              返回
            </Button>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              loading={loading}
              htmlType="submit"
              size="large"
            >
              保存
            </Button>
          </div>
        </Form>
      </Card>
    </div>
  )
}

export default AddEditCancellation

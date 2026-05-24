import { useState, useEffect } from 'react'
import { Form, Input, Select, Button, Card, Row, Col, message } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import api from '../../utils/api'
import {
  GUARANTEE_TYPE_OPTIONS,
  isCreditCardGuaranteeType,
  normalizeGuaranteeType
} from '../../utils/guaranteePolicy'

const { Option } = Select

const AddEditGuarantee = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [guaranteeType, setGuaranteeType] = useState('')
  const [guaranteeSubType, setGuaranteeSubType] = useState('')
  const navigate = useNavigate()
  const location = useLocation()
  
  // 获取路由参数中的编辑数据
  const record = location.state?.record
  const isEditing = !!record
  
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
      const normalizedGuaranteeType = normalizeGuaranteeType(record.type)
      
      form.setFieldsValue({
        name: record.name,
        code: record.code,
        type: normalizedGuaranteeType,
        guaranteeSubType: record.guaranteeSubType,
        guaranteeAmount: record.guaranteeAmount,
        latestArrivalTime: record.latestArrivalTime || record.latestCheckInTime,
        status: statusMap[record.status] || record.status,
        description: record.description
      })
      setGuaranteeType(normalizedGuaranteeType)
      setGuaranteeSubType(record.guaranteeSubType || '')
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
        guaranteeSubType: values.guaranteeSubType || null,
        guaranteeAmount: values.guaranteeAmount || null,
        latestArrivalTime: values.latestArrivalTime || null,
        status: statusMap[values.status] || values.status,
        description: values.description
      }
      
      // 调用后端API
      if (isEditing && record.id) {
        // 编辑模式
        await api.put(`/guarantee-policies/${record.id}`, submitData)
        message.success('担保政策更新成功')
      } else {
        // 创建模式
        await api.post('/guarantee-policies', submitData)
        message.success('担保政策创建成功')
      }
      
      // 保存成功后返回列表页面
      setTimeout(() => {
        navigate('/group-management/group-guarantee')
      }, 1000)
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败: ' + (error.response?.data || error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }
  
  return (
    <div className="fade-in">
      <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center' }}>
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={() => navigate('/group-management/group-guarantee')}
          style={{ marginRight: 16 }}
        >
          返回
        </Button>
        <h1 className="page-title">
          {isEditing ? '编辑担保政策' : '新增担保政策'}
        </h1>
      </div>
      
      <Card style={{ maxWidth: 800, margin: '0 auto' }}>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="name"
                label="担保政策名称"
                rules={[{ required: true, message: '请输入担保政策名称' }]}
              >
                <Input placeholder="请输入担保政策名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="code"
                label="担保政策代码"
                rules={[
                  { required: true, message: '请输入担保政策代码' },
                  { pattern: /^[A-Za-z0-9_]+$/, message: '担保政策代码只能包含英文字母、数字和下划线' }
                ]}
              >
                <Input placeholder="请输入担保政策代码" disabled={isEditing} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="type"
                label="担保类型"
                rules={[{ required: true, message: '请选择担保类型' }]}
              >
                <Select 
                  placeholder="请选择担保类型"
                  onChange={(value) => {
                    setGuaranteeType(value)
                    if (!isCreditCardGuaranteeType(value)) {
                      setGuaranteeSubType('')
                      form.setFieldsValue({ guaranteeSubType: undefined, guaranteeAmount: undefined, latestArrivalTime: undefined })
                    }
                  }}
                >
                  {GUARANTEE_TYPE_OPTIONS.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          {/* 信用卡担保特有字段 */}
          {isCreditCardGuaranteeType(guaranteeType) && (
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="guaranteeSubType"
                  label="担保类型"
                  rules={[{ required: true, message: '请选择担保类型' }]}
                >
                  <Select 
                    placeholder="请选择担保类型"
                    onChange={(value) => setGuaranteeSubType(value)}
                  >
                    <Option value="一律担保">一律担保</Option>
                    <Option value="超时担保">超时担保</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="guaranteeAmount"
                  label="担保金额"
                  rules={[{ required: true, message: '请选择担保金额' }]}
                >
                  <Select placeholder="请选择担保金额">
                    <Option value="首晚">首晚</Option>
                    <Option value="全额">全额</Option>
                  </Select>
                </Form.Item>
              </Col>
              {guaranteeSubType === '超时担保' && (
                <Col span={12}>
                  <Form.Item
                    name="latestArrivalTime"
                    label="最晚到店时间"
                    rules={[{ required: true, message: '请输入最晚到店时间' }]}
                  >
                    <Input placeholder="请输入最晚到店时间，如：22:00" />
                  </Form.Item>
                </Col>
              )}
            </Row>
          )}
          

          
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={4} placeholder="请输入担保政策描述" />
          </Form.Item>
          
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="status"
                label="状态"
                initialValue="启用"
                rules={[{ required: true, message: '请选择状态' }]}
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
              onClick={() => navigate('/group-management/group-guarantee')}
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

export default AddEditGuarantee

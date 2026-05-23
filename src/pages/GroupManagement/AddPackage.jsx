import { useState } from 'react'
import { Form, Input, Select, Checkbox, Button, message, Row, Col, InputNumber, Card } from 'antd'
import { SaveOutlined, LeftOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import api from '../../utils/api'

const { Option } = Select

const frequencyOptions = [
  { value: 'daily', label: '每天1次' },
  { value: 'per_stay', label: '每入住一次' },
  { value: 'arrival_day', label: '到达当天发放一次' },
  { value: 'departure_day', label: '最后一天发放一次' },
  { value: 'except_departure', label: '除最后一天每天一次' }
]

const quantityTypeOptions = [
  { value: 'fixed', label: '固定份数' },
  { value: 'per_order', label: '按订单' },
  { value: 'per_room', label: '按房间' },
  { value: 'per_person', label: '按人数' },
  { value: 'per_adult', label: '按成人数' },
  { value: 'per_child', label: '按儿童数' }
]

const getQuantityLabel = (quantityType) => {
  const quantityLabelMap = {
    fixed: '固定份数',
    per_order: '每订单份数',
    per_room: '每房间份数',
    per_person: '每人份数',
    per_adult: '每成人份数',
    per_child: '每儿童份数'
  }

  return quantityLabelMap[quantityType] || '份数'
}

const getErrorMessage = (error, fallbackMessage) => {
  if (typeof error?.error === 'string') {
    return error.error
  }

  if (typeof error?.response?.data?.error === 'string') {
    return error.response.data.error
  }

  return fallbackMessage
}

const AddPackage = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [quantityType, setQuantityType] = useState('')
  const navigate = useNavigate()
  
  // 包价类型选项
  const packageTypes = [
    { value: '早餐', label: '早餐' },
    { value: '午餐', label: '午餐' },
    { value: '晚餐', label: '晚餐' },
    { value: '综合', label: '综合' },
    { value: '下午茶', label: '下午茶' },
    { value: '门票', label: '门票' },
    { value: '其他', label: '其他' },
    { value: '免费增早', label: '免费增早' },
    { value: '延时退房', label: '延时退房' },
    { value: '提前入住', label: '提前入住' }
  ]
  
  // 处理表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setLoading(true)
      
      // 构建包价数据
      const packageData = {
        code: values.code,
        name: values.name,
        description: values.description || '',
        type: values.type,
        quantityType: values.quantityType,
        fixedQuantity: values.fixedQuantity,
        frequency: values.frequency,
        priceType: 'group',
        fixedPrice: values.fixedPrice,
        taxIncluded: values.taxIncluded || false,
        status: 'active'
      }
      
      // 创建包价
      await api.post('/packages', packageData)
      message.success('包价创建成功')
      
      // 跳转到包价列表页面
      navigate('/group-management/package-setting')
    } catch (error) {
      console.error('保存包价失败:', error)
      message.error(getErrorMessage(error, '保存失败，请稍后重试'))
    } finally {
      setLoading(false)
    }
  }
  
  // 处理返回
  const handleBack = () => {
    navigate('/group-management/package-setting')
  }
  
  return (
    <div className="fade-in" style={{ paddingBottom: 32 }}>
      <div style={{ maxWidth: 1080, margin: '0 auto' }}>
        <div
          style={{
            marginBottom: 24,
            padding: 28,
            borderRadius: 24,
            background: 'linear-gradient(135deg, #0f274f 0%, #173d73 55%, #245aa8 100%)',
            boxShadow: '0 20px 48px rgba(15, 39, 79, 0.18)',
            color: '#ffffff'
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', gap: 24, alignItems: 'flex-start', flexWrap: 'wrap' }}>
            <div>
              <div
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  padding: '6px 12px',
                  borderRadius: 999,
                  background: 'rgba(255,255,255,0.14)',
                  fontSize: 12,
                  fontWeight: 600,
                  letterSpacing: '0.04em',
                  marginBottom: 14
                }}
              >
                GROUP PACKAGE
              </div>
              <div style={{ fontSize: 30, fontWeight: 700, lineHeight: 1.25 }}>
                新增集团包价
              </div>
              <div style={{ marginTop: 10, maxWidth: 620, color: 'rgba(255,255,255,0.82)', lineHeight: 1.8 }}>
                创建集团统一包价模板，便于后续下发到酒店侧使用。请先完成基础规则，再补充价格与说明信息。
              </div>
            </div>
            <Button
              icon={<LeftOutlined />}
              onClick={handleBack}
              style={{
                borderRadius: 12,
                borderColor: 'rgba(255,255,255,0.24)',
                color: '#ffffff',
                background: 'rgba(255,255,255,0.08)'
              }}
            >
              返回列表
            </Button>
          </div>
        </div>

        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            taxIncluded: false
          }}
        >
          <Card
            variant="borderless"
            style={{
              borderRadius: 20,
              boxShadow: '0 12px 32px rgba(15, 23, 42, 0.08)',
              marginBottom: 24
            }}
            styles={{ body: { padding: 28 } }}
          >
            <div style={{ marginBottom: 24 }}>
              <div style={{ fontSize: 20, fontWeight: 700, color: '#102a43' }}>基础信息</div>
              <div style={{ marginTop: 8, color: '#6b7a90', lineHeight: 1.8 }}>
                填写集团包价的基础属性、发放规则和份数口径，确保集团标准配置统一。
              </div>
            </div>

            <Row gutter={[20, 8]}>
              <Col xs={24} md={12}>
                <Form.Item
                  name="code"
                  label="包价代码"
                  rules={[
                    { required: true, message: '请输入包价代码' },
                    { pattern: /^[A-Za-z0-9_]+$/, message: '包价代码只能包含英文字母、数字和下划线' }
                  ]}
                >
                  <Input placeholder="请输入包价代码" size="large" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  name="name"
                  label="包价名称"
                  rules={[{ required: true, message: '请输入包价名称' }]}
                >
                  <Input placeholder="请输入包价名称" size="large" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  name="type"
                  label="包价类型"
                  rules={[{ required: true, message: '请选择包价类型' }]}
                >
                  <Select placeholder="请选择包价类型" size="large">
                    {packageTypes.map((item) => (
                      <Option key={item.value} value={item.value}>{item.label}</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  name="frequency"
                  label="发放频率"
                  rules={[{ required: true, message: '请选择发放频率' }]}
                >
                  <Select placeholder="请选择发放频率" size="large">
                    {frequencyOptions.map((item) => (
                      <Option key={item.value} value={item.value}>{item.label}</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  name="quantityType"
                  label="计数方式"
                  rules={[{ required: true, message: '请选择计数方式' }]}
                >
                  <Select
                    placeholder="请选择计数方式"
                    size="large"
                    onChange={(value) => {
                      setQuantityType(value)
                      form.setFieldValue('fixedQuantity', undefined)
                    }}
                  >
                    {quantityTypeOptions.map((item) => (
                      <Option key={item.value} value={item.value}>{item.label}</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                {quantityType && (
                  <Form.Item
                    name="fixedQuantity"
                    label={getQuantityLabel(quantityType)}
                    rules={[{ required: true, message: `请输入${getQuantityLabel(quantityType)}` }]}
                  >
                    <InputNumber
                      style={{ width: '100%' }}
                      min={1}
                      precision={0}
                      size="large"
                      placeholder={`请输入${getQuantityLabel(quantityType)}`}
                    />
                  </Form.Item>
                )}
              </Col>
            </Row>
          </Card>

          <Card
            variant="borderless"
            style={{
              borderRadius: 20,
              boxShadow: '0 12px 32px rgba(15, 23, 42, 0.08)',
              marginBottom: 24
            }}
            styles={{ body: { padding: 28 } }}
          >
            <div style={{ marginBottom: 24 }}>
              <div style={{ fontSize: 20, fontWeight: 700, color: '#102a43' }}>价格与说明</div>
              <div style={{ marginTop: 8, color: '#6b7a90', lineHeight: 1.8 }}>
                集团包价当前仅支持固定价格，价格可选录入；未填写时将按集团设置状态展示。
              </div>
            </div>

            <Row gutter={[20, 8]}>
              <Col xs={24} md={12}>
                <Form.Item label="计价方式" required>
                  <div
                    style={{
                      minHeight: 48,
                      display: 'flex',
                      alignItems: 'center',
                      gap: 10,
                      padding: '0 16px',
                      borderRadius: 14,
                      border: '1px solid #d9e3f0',
                      background: 'linear-gradient(180deg, #f9fbff 0%, #f4f8ff 100%)'
                    }}
                  >
                    <div
                      style={{
                        width: 10,
                        height: 10,
                        borderRadius: '50%',
                        background: '#1677ff',
                        boxShadow: '0 0 0 4px rgba(22,119,255,0.12)'
                      }}
                    />
                    <span style={{ fontWeight: 600, color: '#123a72' }}>固定价格</span>
                  </div>
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  name="fixedPrice"
                  label="价格"
                >
                  <InputNumber
                    style={{ width: '100%' }}
                    placeholder="请输入价格"
                    min={0}
                    step={0.01}
                    size="large"
                    prefix="¥"
                  />
                </Form.Item>
              </Col>
              <Col xs={24}>
                <Form.Item
                  name="taxIncluded"
                  valuePropName="checked"
                >
                  <Checkbox>设置价格是否含税</Checkbox>
                </Form.Item>
              </Col>
            </Row>

            <Form.Item
              name="description"
              label="描述"
            >
              <Input.TextArea rows={5} placeholder="请输入包价描述" showCount maxLength={300} />
            </Form.Item>
          </Card>

          <div
            style={{
              padding: 20,
              borderRadius: 20,
              background: '#ffffff',
              boxShadow: '0 12px 32px rgba(15, 23, 42, 0.08)',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              gap: 16,
              flexWrap: 'wrap'
            }}
          >
            <div style={{ color: '#6b7a90', lineHeight: 1.8 }}>
              保存后将生成集团统一包价模板，后续可在集团范围内继续维护与下发。
            </div>
            <div style={{ display: 'flex', gap: 12 }}>
              <Button
                icon={<LeftOutlined />}
                onClick={handleBack}
                size="large"
                style={{ borderRadius: 12 }}
              >
                返回
              </Button>
              <Button
                type="primary"
                icon={<SaveOutlined />}
                loading={loading}
                htmlType="submit"
                size="large"
                style={{ borderRadius: 12, minWidth: 120 }}
              >
                保存
              </Button>
            </div>
          </div>
        </Form>
      </div>
    </div>
  )
}

export default AddPackage

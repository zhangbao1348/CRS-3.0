import { useState } from 'react'
import { Form, Tabs, Input, Select, Radio, Button, message, Row, Col, InputNumber, Checkbox } from 'antd'
import { SaveOutlined, LeftOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import api from '../../utils/api'

const { TabPane } = Tabs
const { Option } = Select
const { Group: RadioGroup } = Radio

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
  const [priceType, setPriceType] = useState('fixed')
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
        priceType: values.priceType === 'daily' ? 'daily' : 'group',
        fixedPrice: values.priceType === 'fixed' ? values.fixedPrice : null,
        taxIncluded: values.taxIncluded || false,
        status: 'active'
      }
      
      // 创建包价
      const createdPackage = await api.post('/packages', packageData)
      message.success('包价创建成功')

      if (values.priceType === 'daily' && createdPackage?.id) {
        navigate(`/rate-management/edit-package?id=${createdPackage.id}&tab=daily-price`)
      } else {
        navigate('/rate-management/package-setting')
      }
    } catch (error) {
      console.error('保存包价失败:', error)
      message.error(getErrorMessage(error, '保存失败，请稍后重试'))
    } finally {
      setLoading(false)
    }
  }
  
  // 处理返回
  const handleBack = () => {
    navigate('/rate-management/package-setting')
  }
  
  return (
    <div className="fade-in">
      <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center' }}>
        <Button
          icon={<LeftOutlined />}
          onClick={handleBack}
          style={{ marginRight: 16 }}
        >
          返回
        </Button>
        <h1 className="page-title">
          新增包价
        </h1>
      </div>
      
      <div style={{ maxWidth: 800, margin: '0 auto' }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            priceType: 'fixed',
            taxIncluded: false
          }}
        >
          <Tabs defaultActiveKey="1">
            <TabPane tab="基础信息" key="1">
              <Row gutter={[16, 16]}>
                <Col span={12}>
                  <Form.Item
                    name="code"
                    label="包价代码"
                    rules={[
                      { required: true, message: '请输入包价代码' },
                      { pattern: /^[A-Za-z0-9_]+$/, message: '包价代码只能包含英文字母、数字和下划线' }
                    ]}
                  >
                    <Input placeholder="请输入包价代码" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="name"
                    label="包价名称"
                    rules={[{ required: true, message: '请输入包价名称' }]}
                  >
                    <Input placeholder="请输入包价名称" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="type"
                    label="包价类型"
                    rules={[{ required: true, message: '请选择包价类型' }]}
                  >
                    <Select placeholder="请选择包价类型">
                      {packageTypes.map(item => (
                        <Option key={item.value} value={item.value}>{item.label}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="frequency"
                    label="发放频率"
                    rules={[{ required: true, message: '请选择发放频率' }]}
                  >
                    <Select placeholder="请选择发放频率">
                      {frequencyOptions.map(item => (
                        <Option key={item.value} value={item.value}>{item.label}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="quantityType"
                    label="计数方式"
                    rules={[{ required: true, message: '请选择计数方式' }]}
                  >
                    <Select 
                      placeholder="请选择计数方式"
                      onChange={(value) => {
                        setQuantityType(value)
                        form.setFieldValue('fixedQuantity', undefined)
                      }}
                    >
                      {quantityTypeOptions.map(item => (
                        <Option key={item.value} value={item.value}>{item.label}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  {quantityType && (
                    <Form.Item
                      name="fixedQuantity"
                      label={getQuantityLabel(quantityType)}
                      rules={[{ required: true, message: `请输入${getQuantityLabel(quantityType)}` }]}
                    >
                      <InputNumber style={{ width: '100%' }} min={1} precision={0} placeholder={`请输入${getQuantityLabel(quantityType)}`} />
                    </Form.Item>
                  )}
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="priceType"
                    label="计价方式"
                    rules={[{ required: true, message: '请选择计价方式' }]}
                  >
                    <RadioGroup
                      onChange={(e) => {
                        const nextPriceType = e.target.value
                        setPriceType(nextPriceType)
                        if (nextPriceType !== 'fixed') {
                          form.setFieldValue('fixedPrice', null)
                        }
                      }}
                    >
                      <Radio value="fixed">固定价格</Radio>
                      <Radio value="daily">按日期设置价格</Radio>
                    </RadioGroup>
                  </Form.Item>
                </Col>
                {priceType === 'fixed' && (
                  <Col span={12}>
                    <Form.Item
                      name="fixedPrice"
                      label="价格"
                      dependencies={['priceType']}
                      rules={[
                        ({ getFieldValue }) => ({
                          required: getFieldValue('priceType') === 'fixed',
                          message: '请输入价格'
                        })
                      ]}
                    >
                      <InputNumber
                        style={{ width: '100%' }}
                        placeholder="请输入价格"
                        min={0}
                        step={0.01}
                        prefix="¥"
                      />
                    </Form.Item>
                  </Col>
                )}
                <Col span={12}>
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
                <Input.TextArea rows={4} placeholder="请输入包价描述" />
              </Form.Item>
            </TabPane>
          </Tabs>
          
          <div style={{ marginTop: 32, padding: '20px', backgroundColor: '#f5f5f5', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                icon={<LeftOutlined />}
                onClick={handleBack}
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
          </div>
        </Form>
      </div>
    </div>
  )
}

export default AddPackage

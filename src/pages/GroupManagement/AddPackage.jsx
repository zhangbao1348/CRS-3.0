import React, { useState } from 'react'
import { Form, Tabs, Input, Select, Radio, Checkbox, Button, message, DatePicker } from 'antd'
import { SaveOutlined, LeftOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import dayjs from 'dayjs'

const { TabPane } = Tabs
const { Option } = Select
const { Group: RadioGroup } = Radio

const AddPackage = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('1')
  const navigate = useNavigate()
  
  // 包价类型选项
  const packageTypes = [
    { value: '早餐', label: '早餐' },
    { value: '午餐', label: '午餐' },
    { value: '晚餐', label: '晚餐' },
    { value: '综合', label: '综合' }
  ]
  
  // 发放频率选项
  const frequencyOptions = [
    { value: '每天出现一次', label: '每天出现一次' },
    { value: '每次入住出现一次', label: '每次入住出现一次' },
    { value: '每周出现一次', label: '每周出现一次' }
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
        fixedQuantity: values.quantityType === 'fixed' ? values.fixedQuantity : null,
        frequency: values.frequency,
        priceType: values.priceType,
        fixedPrice: values.priceType === 'group' ? values.fixedPrice : null,
        taxIncluded: values.taxIncluded || false,
        startDate: values.startDate ? values.startDate.format('YYYY-MM-DD') : null,
        endDate: values.endDate ? values.endDate.format('YYYY-MM-DD') : null,
        status: 'active'
      }
      
      // 创建包价
      const response = await axios.post('http://localhost:8080/api/packages', packageData)
      message.success('包价创建成功')
      
      // 跳转到包价列表页面
      navigate('/group-management/package-setting')
    } catch (error) {
      console.error('保存包价失败:', error)
      if (error.response && error.response.data) {
        message.error(error.response.data)
      } else {
        message.error('保存失败，请稍后重试')
      }
    } finally {
      setLoading(false)
    }
  }
  
  // 处理价格类型变化
  const handlePriceTypeChange = (value) => {
    if (value === 'group') {
      setActiveTab('2')
    }
  }
  
  // 处理返回
  const handleBack = () => {
    navigate('/group-management/package-setting')
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
          新增集团包价
        </h1>
      </div>
      
      <div style={{ maxWidth: 800, margin: '0 auto' }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Form.Item
            name="code"
            label="包价代码"
            rules={[{ required: true, message: '请输入包价代码' }]}
          >
            <Input placeholder="请输入包价代码" />
          </Form.Item>
          
          <Form.Item
            name="name"
            label="包价名称"
            rules={[{ required: true, message: '请输入包价名称' }]}
          >
            <Input placeholder="请输入包价名称" />
          </Form.Item>
          
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
          
          <Form.Item
            name="quantityType"
            label="份数"
            rules={[{ required: true, message: '请选择份数类型' }]}
          >
            <RadioGroup>
              <Radio value="fixed">固定份数</Radio>
              <Radio value="per_person">按人数</Radio>
            </RadioGroup>
          </Form.Item>
          
          <Form.Item
            name="fixedQuantity"
            label="固定份数"
            rules={[
              {
                required: ({ quantityType }) => quantityType === 'fixed',
                message: '请输入固定份数'
              }
            ]}
          >
            <Input type="number" min={1} placeholder="请输入固定份数" />
          </Form.Item>
          
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
          
          <Form.Item
            name="priceType"
            label="价格"
            rules={[{ required: true, message: '请选择价格类型' }]}
          >
            <RadioGroup>
              <Radio value="group">集团统一价格</Radio>
              <Radio value="hotel">酒店设置价格</Radio>
            </RadioGroup>
          </Form.Item>
          
          <Form.Item
            name="fixedPrice"
            label="固定价格"
            rules={[
              {
                required: ({ priceType }) => priceType === 'group',
                message: '请输入包价的价格'
              }
            ]}
          >
            <Input type="number" min={0} step={0.01} placeholder="请输入包价的价格" />
          </Form.Item>
          
          <Form.Item
            name="taxIncluded"
            valuePropName="checked"
          >
            <Checkbox>设置价格是否含税</Checkbox>
          </Form.Item>
          
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={4} placeholder="请输入包价描述" />
          </Form.Item>
          
          <Form.Item
            name="startDate"
            label="开始日期"
          >
            <DatePicker style={{ width: '100%' }} placeholder="请选择开始日期" />
          </Form.Item>
          
          <Form.Item
            name="endDate"
            label="结束日期"
          >
            <DatePicker style={{ width: '100%' }} placeholder="请选择结束日期" />
          </Form.Item>
          
          <div style={{ marginTop: 32, display: 'flex', justifyContent: 'flex-end' }}>
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
        </Form>
      </div>
    </div>
  )
}

export default AddPackage
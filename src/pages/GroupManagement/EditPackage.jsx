import React, { useState, useEffect } from 'react'
import { Form, Tabs, Input, Select, Radio, Checkbox, Button, message, DatePicker } from 'antd'
import { SaveOutlined, LeftOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import axios from 'axios'
import dayjs from 'dayjs'

const { TabPane } = Tabs
const { Option } = Select
const { Group: RadioGroup } = Radio

const EditPackage = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('1')
  const navigate = useNavigate()
  const location = useLocation()
  
  // 获取包价ID
  const packageId = new URLSearchParams(location.search).get('id')
  
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
  
  // 加载包价数据
  useEffect(() => {
    if (packageId) {
      loadPackageData()
    } else {
      message.error('包价ID不存在')
      navigate('/group-management/package-setting')
    }
  }, [packageId, navigate])
  
  // 加载包价详情
  const loadPackageData = async () => {
    setLoading(true)
    try {
      const response = await axios.get(`http://localhost:8080/api/packages/${packageId}`)
      const packageData = response.data
      
      // 转换数据格式以匹配表单
      const formData = {
        code: packageData.code,
        name: packageData.name,
        description: packageData.description,
        type: packageData.type,
        quantityType: packageData.quantityType,
        fixedQuantity: packageData.fixedQuantity,
        frequency: packageData.frequency,
        priceType: packageData.priceType,
        fixedPrice: packageData.fixedPrice,
        taxIncluded: packageData.taxIncluded,
        startDate: packageData.startDate ? dayjs(packageData.startDate) : null,
        endDate: packageData.endDate ? dayjs(packageData.endDate) : null
      }
      
      form.setFieldsValue(formData)
      
      // 价格设置已移至基础信息标签页，无需设置激活标签页
      setActiveTab('1')
    } catch (error) {
      console.error('加载包价数据失败:', error)
      message.error('加载包价数据失败，请稍后重试')
      navigate('/group-management/package-setting')
    } finally {
      setLoading(false)
    }
  }
  
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
      
      // 更新包价
      const response = await axios.put(`http://localhost:8080/api/packages/${packageId}`, packageData)
      message.success('包价更新成功')
      
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
    // 价格设置已移至基础信息标签页，无需切换标签页
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
          编辑集团包价
        </h1>
      </div>
      
      <div style={{ maxWidth: 800, margin: '0 auto' }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Tabs activeKey={activeTab} onChange={setActiveTab}>
            <TabPane tab="基础信息" key="1">
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
                <RadioGroup onChange={handlePriceTypeChange}>
                  <Radio value="group">集团统一价格</Radio>
                  <Radio value="hotel">酒店设置价格</Radio>
                </RadioGroup>
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
            </TabPane>
            
            <TabPane tab="价格信息" key="2">
              <div style={{ padding: '20px', textAlign: 'center', color: '#999' }}>
                价格设置已移至基础信息标签页
              </div>
            </TabPane>
            
            <TabPane tab="分配酒店" key="3">
              <div style={{ padding: '20px', textAlign: 'center', color: '#999' }}>
                酒店分配功能待实现
              </div>
            </TabPane>
          </Tabs>
          
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

export default EditPackage
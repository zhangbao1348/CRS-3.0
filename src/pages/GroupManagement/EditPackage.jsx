import React, { useState, useEffect } from 'react'
import { Form, Tabs, Input, Select, Radio, Checkbox, Button, message, Row, Col } from 'antd'
import { SaveOutlined, LeftOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import api from '../../utils/api'

const { TabPane } = Tabs
const { Option } = Select
const { Group: RadioGroup } = Radio

const EditPackage = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('1')
  const [quantityType, setQuantityType] = useState('')
  const navigate = useNavigate()
  const location = useLocation()
  
  // 获取包价ID
  const packageId = new URLSearchParams(location.search).get('id')
  
  // 包价类型选项
  const packageTypes = [
    { value: '早餐', label: '早餐' },
    { value: '午餐', label: '午餐' },
    { value: '晚餐', label: '晚餐' },
    { value: '下午茶', label: '下午茶' },
    { value: '门票', label: '门票' },
    { value: '其他', label: '其他' },
    { value: '免费增早', label: '免费增早' },
    { value: '延时退房', label: '延时退房' },
    { value: '提前入住', label: '提前入住' }
  ]
  
  // 发放频率选项
  const frequencyOptions = [
    { value: 'daily', label: '每天1次' },
    { value: 'per_stay', label: '每入住一次' },
    { value: 'arrival_day', label: '到达当天发放一次' },
    { value: 'departure_day', label: '最后一天发放一次' },
    { value: 'except_departure', label: '除最后一天每天一次' }
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
      const packageData = await api.get(`/packages/${packageId}`)
      
      // 转换数据格式以匹配表单
      const formData = {
        code: packageData.code,
        name: packageData.name,
        description: packageData.description,
        type: packageData.type,
        quantityType: packageData.quantityType,
        fixedQuantity: packageData.fixedQuantity,
        frequency: packageData.frequency,
        taxIncluded: packageData.taxIncluded
      }
      
      form.setFieldsValue(formData)
      
      // 设置计数方式状态
      setQuantityType(formData.quantityType)
      
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
        taxIncluded: values.taxIncluded || false,
        status: 'active'
      }
      
      // 更新包价
      const response = await api.put(`/packages/${packageId}`, packageData)
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
                    <Input placeholder="请输入包价代码" disabled />
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
                      onChange={(value) => setQuantityType(value)}
                    >
                      <Option value="fixed">固定份数</Option>
                      <Option value="per_order">按订单</Option>
                      <Option value="per_room">按房间</Option>
                      <Option value="per_person">按人数</Option>
                      <Option value="per_adult">按成人数</Option>
                      <Option value="per_child">按儿童数</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  {quantityType === 'fixed' && (
                    <Form.Item
                      name="fixedQuantity"
                      label="固定份数"
                      rules={[{ required: true, message: '请输入固定份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入固定份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_order' && (
                    <Form.Item
                      name="fixedQuantity"
                      label="每订单份数"
                      rules={[{ required: true, message: '请输入每订单份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每订单份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_room' && (
                    <Form.Item
                      name="fixedQuantity"
                      label="每房间份数"
                      rules={[{ required: true, message: '请输入每房间份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每房间份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_person' && (
                    <Form.Item
                      name="fixedQuantity"
                      label="每人份数"
                      rules={[{ required: true, message: '请输入每人份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每人份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_adult' && (
                    <Form.Item
                      name="fixedQuantity"
                      label="每成人份数"
                      rules={[{ required: true, message: '请输入每成人份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每成人份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_child' && (
                    <Form.Item
                      name="fixedQuantity"
                      label="每儿童份数"
                      rules={[{ required: true, message: '请输入每儿童份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每儿童份数" />
                    </Form.Item>
                  )}
                </Col>
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

export default EditPackage
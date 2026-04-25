import React, { useState, useEffect } from 'react'
import { Form, Modal, Tabs, Input, Select, Radio, Checkbox, Button, message } from 'antd'
import { SaveOutlined, CloseOutlined } from '@ant-design/icons'
import api from '../../utils/api'

const { TabPane } = Tabs
const { Option } = Select
const { Group: RadioGroup } = Radio

const AddEditPackage = ({ visible, onCancel, onOk, packageId }) => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('1')
  const [quantityType, setQuantityType] = useState('')
  const [priceType, setPriceType] = useState('group')
  
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
    if (visible && packageId) {
      loadPackageData()
    } else if (visible) {
      form.resetFields()
      setActiveTab('1')
    }
  }, [visible, packageId, form])
  
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
        quantityType: packageData.quantityType === 'fixed' ? 'fixed' : 'per_person',
        fixedQuantity: packageData.fixedQuantity,
        frequency: packageData.frequency,
        priceType: packageData.priceType === 'group' ? 'group' : 'hotel',
        fixedPrice: packageData.fixedPrice,
        taxIncluded: packageData.taxIncluded
      }
      
      form.setFieldsValue(formData)
      
      // 根据价格类型设置激活的标签页
      if (packageData.priceType === 'group' && packageData.fixedPrice) {
        setActiveTab('2')
      }
    } catch (error) {
      console.error('加载包价数据失败:', error)
      message.error('加载包价数据失败，请稍后重试')
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
        status: 'active'
      }
      
      let response
      if (packageId) {
        // 更新包价
        response = await api.put(`/packages/${packageId}`, packageData)
        message.success('包价更新成功')
      } else {
        // 创建包价
        response = await api.post('/packages', packageData)
        message.success('包价创建成功')
      }
      
      onOk(response.data)
      form.resetFields()
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
  
  return (
    <Modal
      title={packageId ? '编辑集团包价' : '新增集团包价'}
      open={visible}
      onCancel={onCancel}
      footer={null}
      width={800}
      destroyOnClose
    >
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
              rules={[
                { required: true, message: '请输入包价代码' },
                { pattern: /^[A-Za-z0-9_]+$/, message: '包价代码只能包含英文字母、数字和下划线' }
              ]}
            >
              <Input placeholder="请输入包价代码" disabled={!!packageId} />
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
              <RadioGroup 
                onChange={(e) => {
                  setPriceType(e.target.value)
                  handlePriceTypeChange(e)
                }}
              >
                <Radio value="group">集团统一价格</Radio>
                <Radio value="hotel">日历价格</Radio>
              </RadioGroup>
            </Form.Item>
            
            {priceType === 'group' && (
              <Form.Item
                name="fixedPrice"
                label="价格"
                rules={[
                  {
                    required: true,
                    message: '请输入包价的价格'
                  }
                ]}
              >
                <Input type="number" min={0} step={0.01} placeholder="请输入包价的价格" />
              </Form.Item>
            )}
            
            <Form.Item
              name="description"
              label="描述"
            >
              <Input.TextArea rows={4} placeholder="请输入包价描述" />
            </Form.Item>
          </TabPane>
          
          <TabPane tab="价格信息" key="2">
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
          
          <TabPane tab="分配酒店" key="3">
            <div style={{ padding: '20px', textAlign: 'center', color: '#999' }}>
              酒店分配功能待实现
            </div>
          </TabPane>
        </Tabs>
        
        <div style={{ marginTop: 24, display: 'flex', justifyContent: 'flex-end' }}>
          <Button
            icon={<CloseOutlined />}
            onClick={onCancel}
            style={{ marginRight: 8 }}
          >
            取消
          </Button>
          <Button
            type="primary"
            icon={<SaveOutlined />}
            loading={loading}
            htmlType="submit"
          >
            保存
          </Button>
        </div>
      </Form>
    </Modal>
  )
}

export default AddEditPackage
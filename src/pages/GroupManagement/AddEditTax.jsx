import React, { useState, useEffect } from 'react'
import { Form, Input, Select, InputNumber, Button, Card, Row, Col, message } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import { taxSettingApi, enumApi } from '../../utils/api'

const { Option } = Select

const AddEditTax = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [enumLoading, setEnumLoading] = useState(true)
  const navigate = useNavigate()
  const location = useLocation()
  
  // 获取路由参数中的编辑数据
  const record = location.state?.record
  const isEditing = !!record
  
  // 状态枚举选项
  const [statusOptions, setStatusOptions] = useState([])
  
  // 从API获取状态枚举
  const fetchEnums = async () => {
    try {
      setEnumLoading(true)
      const response = await enumApi.getCommonStatus()
      if (response && Array.isArray(response)) {
        setStatusOptions(response)
      }
    } catch (error) {
      console.error('获取枚举选项失败:', error)
      message.error('获取状态选项失败')
    } finally {
      setEnumLoading(false)
    }
  }
  
  // 组件挂载时获取枚举
  useEffect(() => {
    fetchEnums()
  }, [])
  
  // 初始化表单数据
  useEffect(() => {
    if (isEditing && record) {
      form.setFieldsValue({
        taxCode: record.taxCode,
        legalName: record.legalName,
        rateAmount: record.rateAmount,
        status: record.status
      })
    }
  }, [isEditing, record, form])
  
  // 处理表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setLoading(true)
      
      const submitData = {
        ...values
      }
      
      if (isEditing && record.id) {
        // 编辑模式
        await taxSettingApi.updateTaxSetting(record.id, submitData)
        message.success('集团税率更新成功')
      } else {
        // 创建模式
        await taxSettingApi.createTaxSetting(submitData)
        message.success('集团税率创建成功')
      }
      
      setTimeout(() => {
        navigate('/group-management/tax-setting')
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
          onClick={() => navigate('/group-management/tax-setting')}
          style={{ marginRight: 16 }}
        >
          返回
        </Button>
        <h1 className="page-title">
          {isEditing ? '编辑集团税率' : '新增集团税率'}
        </h1>
      </div>
      
      <Card style={{ maxWidth: 800, margin: '0 auto' }}>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <h3 style={{ marginBottom: 16, fontWeight: 600 }}>税率规则设置</h3>
          
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="taxCode"
                label="税率CODE"
                rules={[
                  { required: true, message: '请输入税率CODE' },
                  { pattern: /^[A-Za-z0-9_-]+$/, message: '税率CODE只能包含英文字母、数字、短横线和下划线' }
                ]}
                extra="唯一编码标识，保存后不可修改。例：VAT-CN-001、SERVICE-CN-001"
              >
                <Input placeholder="请输入税率CODE" disabled={isEditing} />
              </Form.Item>
            </Col>
            
            <Col span={12}>
              <Form.Item
                name="legalName"
                label="税率名称"
                rules={[{ required: true, message: '请输入税率名称' }]}
                extra="税费法定展示全称。例：中国增值税(VAT)、服务费"
              >
                <Input placeholder="请输入税率名称" />
              </Form.Item>
            </Col>
            
            <Col span={12}>
              <Form.Item
                name="rateAmount"
                label="税率 (%)"
                rules={[{ required: true, message: '请输入税率百分比' }]}
                extra="以百分比为单位的比例税，例：输入 6 代表 6%"
              >
                <InputNumber 
                  min={0} 
                  max={100}
                  step={0.01} 
                  formatter={value => `${value}%`}
                  parser={value => value.replace('%', '')}
                  placeholder="请输入比例数值" 
                  style={{ width: '100%' }} 
                />
              </Form.Item>
            </Col>
            
            <Col span={12}>
              <Form.Item
                name="status"
                label="状态"
                rules={[{ required: true, message: '请选择状态' }]}
              >
                <Select placeholder="请选择状态" loading={enumLoading}>
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
              onClick={() => navigate('/group-management/tax-setting')}
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

export default AddEditTax

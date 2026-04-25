import React, { useState, useEffect } from 'react'
import { Form, Input, Select, InputNumber, Button, Card, Row, Col, message } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import { taxSettingApi, enumApi } from '../../utils/api'

const { Option } = Select
const { TextArea } = Input

const AddEditTax = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [enumLoading, setEnumLoading] = useState(true)
  const navigate = useNavigate()
  const location = useLocation()
  
  // 获取路由参数中的编辑数据
  const record = location.state?.record
  const isEditing = !!record
  
  // 枚举选项状态
  const [enumOptions, setEnumOptions] = useState({
    taxBearer: [],
    taxBaseType: [],
    taxCalculationRule: [],
    taxDeductible: [],
    taxRefundable: [],
    taxSettlementRule: [],
    currency: [],
    commonStatus: []
  })
  
  // 从API获取枚举选项
  const fetchEnums = async () => {
    try {
      setEnumLoading(true)
      const [
        taxBearer, taxBaseType, taxCalculationRule, 
        taxDeductible, taxRefundable, taxSettlementRule,
        currency, commonStatus
      ] = await Promise.all([
        enumApi.getTaxBearer(),
        enumApi.getTaxBaseType(),
        enumApi.getTaxCalculationRule(),
        enumApi.getTaxDeductible(),
        enumApi.getTaxRefundable(),
        enumApi.getTaxSettlementRule(),
        enumApi.getCurrency(),
        enumApi.getCommonStatus()
      ])
      setEnumOptions({
        taxBearer,
        taxBaseType,
        taxCalculationRule,
        taxDeductible,
        taxRefundable,
        taxSettlementRule,
        currency,
        commonStatus
      })
    } catch (error) {
      console.error('获取枚举选项失败:', error)
      message.error('获取枚举选项失败')
    } finally {
      setEnumLoading(false)
    }
  }
  
  // 创建反向映射函数
  const createReverseMap = (options) => {
    const map = {}
    options.forEach(opt => {
      map[opt.value] = opt.label
      map[opt.label] = opt.value
    })
    return map
  }
  
  // 各种枚举映射 - 使用API获取的枚举
  const BEARER_MAP = createReverseMap(enumOptions.taxBearer)
  const BASE_TYPE_MAP = createReverseMap(enumOptions.taxBaseType)
  const CALCULATION_RULE_MAP = createReverseMap(enumOptions.taxCalculationRule)
  const DEDUCTIBLE_MAP = createReverseMap(enumOptions.taxDeductible)
  const REFUNDABLE_MAP = createReverseMap(enumOptions.taxRefundable)
  const SETTLEMENT_RULE_MAP = createReverseMap(enumOptions.taxSettlementRule)
  const CURRENCY_MAP = createReverseMap(enumOptions.currency)
  const STATUS_MAP = createReverseMap(enumOptions.commonStatus)
  
  // 组件挂载时获取枚举
  useEffect(() => {
    fetchEnums()
  }, [])
  
  // 初始化表单数据 - Select 的 value 是英文，所以直接使用
  useEffect(() => {
    if (isEditing && record && !enumLoading) {
      form.setFieldsValue({
        taxCode: record.taxCode,
        legalName: record.legalName,
        bearer: record.bearer,
        baseType: record.baseType,
        rateAmount: record.rateAmount,
        rateCurrency: record.rateCurrency,
        calculationRule: record.calculationRule,
        deductible: record.deductible,
        refundable: record.refundable,
        settlementRule: record.settlementRule,
        complianceRequirements: record.complianceRequirements,
        remarks: record.remarks,
        status: record.status
      })
    }
  }, [isEditing, record, form, enumLoading])
  
  // 处理表单提交 - Select 的 value 是英文，所以直接使用
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setLoading(true)
      
      // 准备提交数据，直接使用 values
      const submitData = {
        ...values
      }
      
      // 调用后端API
      if (isEditing && record.id) {
        // 编辑模式
        await taxSettingApi.updateTaxSetting(record.id, submitData)
        message.success('集团税率更新成功')
      } else {
        // 创建模式
        await taxSettingApi.createTaxSetting(submitData)
        message.success('集团税率创建成功')
      }
      
      // 保存成功后返回列表页面
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
      
      <Card style={{ maxWidth: 1000, margin: '0 auto' }}>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          {/* 基础信息 */}
          <h3 style={{ marginBottom: 16, fontWeight: 600 }}>基础信息</h3>
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="taxCode"
                label="税费项编码"
                rules={[
                  { required: true, message: '请输入税费项编码' },
                  { pattern: /^[A-Za-z0-9_]+$/, message: '税费项编码只能包含英文字母、数字和下划线' }
                ]}
                extra="例：VAT-CN-001、CITYTAX-FR-PAR-001"
              >
                <Input placeholder="请输入税费项编码" disabled={isEditing} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="legalName"
                label="税费法定全称"
                rules={[{ required: true, message: '请输入税费法定全称' }]}
                extra="中英文双语，例：中国增值税(VAT)"
              >
                <Input placeholder="请输入税费法定全称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="bearer"
                label="税费承担主体"
                rules={[{ required: true, message: '请选择税费承担主体' }]}
              >
                <Select placeholder="请选择税费承担主体" loading={enumLoading}>
                  {enumOptions.taxBearer.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="baseType"
                label="计税基数类型"
                rules={[{ required: true, message: '请选择计税基数类型' }]}
              >
                <Select placeholder="请选择计税基数类型" loading={enumLoading}>
                  {enumOptions.taxBaseType.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          {/* 税率信息 */}
          <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>税率信息</h3>
          <Row gutter={[16, 16]}>
            <Col span={8}>
              <Form.Item
                name="rateAmount"
                label="税率/定额标准"
                rules={[{ required: true, message: '请输入税率/定额标准' }]}
              >
                <InputNumber min={0} step={0.01} placeholder="请输入数值" style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="rateCurrency"
                label="单位"
                rules={[{ required: true, message: '请选择单位' }]}
              >
                <Select placeholder="请选择单位" loading={enumLoading}>
                  {enumOptions.currency.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                extra="比例税填百分比（例：6%），定额税填数值+币种（例：200日元/人/晚）"
              >
              </Form.Item>
            </Col>
          </Row>
          
          {/* 计税规则 */}
          <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>计税规则</h3>
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="calculationRule"
                label="计税计算规则"
                rules={[{ required: true, message: '请选择计税计算规则' }]}
              >
                <Select placeholder="请选择计税计算规则" loading={enumLoading}>
                  {enumOptions.taxCalculationRule.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="deductible"
                label="是否可进项抵扣"
                rules={[{ required: true, message: '请选择是否可进项抵扣' }]}
              >
                <Select placeholder="请选择是否可进项抵扣" loading={enumLoading}>
                  {enumOptions.taxDeductible.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="refundable"
                label="取消订单是否可退"
                rules={[{ required: true, message: '请选择取消订单是否可退' }]}
              >
                <Select placeholder="请选择取消订单是否可退" loading={enumLoading}>
                  {enumOptions.taxRefundable.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="settlementRule"
                label="结算缴纳规则"
                rules={[{ required: true, message: '请选择结算缴纳规则' }]}
              >
                <Select placeholder="请选择结算缴纳规则" loading={enumLoading}>
                  {enumOptions.taxSettlementRule.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="status"
                label="状态"
                rules={[{ required: true, message: '请选择状态' }]}
              >
                <Select placeholder="请选择状态" loading={enumLoading}>
                  {enumOptions.commonStatus.map(option => (
                    <Option key={option.value} value={option.value}>{option.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          {/* 合规与备注 */}
          <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>合规与备注</h3>
          <Row gutter={[16, 16]}>
            <Col span={24}>
              <Form.Item
                name="complianceRequirements"
                label="合规申报要求"
              >
                <TextArea rows={4} placeholder="请输入合规申报要求，如申报周期、主管税局、凭证留存要求、免税条件等" />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                name="remarks"
                label="备注"
              >
                <TextArea rows={3} placeholder="请输入备注信息" />
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

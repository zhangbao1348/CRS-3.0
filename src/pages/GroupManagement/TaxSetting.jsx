import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, message } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { taxSettingApi, enumApi } from '../../utils/api'

const { Option } = Select

// 创建枚举值到标签的映射函数
const createEnumMap = (options) => {
  const map = {}
  options?.forEach(opt => {
    map[opt.value] = opt.label
  })
  return map
}

const TaxSetting = () => {
  const navigate = useNavigate()
  const [taxList, setTaxList] = useState([])
  const [loading, setLoading] = useState(false)
  const [enumLoading, setEnumLoading] = useState(true)
  const [rawTaxData, setRawTaxData] = useState([])
  const [enumOptions, setEnumOptions] = useState({
    taxBearer: [],
    taxBaseType: [],
    taxCalculationRule: [],
    taxDeductible: [],
    taxRefundable: [],
    taxSettlementRule: [],
    commonStatus: [],
    currency: []
  })
  
  // 获取枚举选项
  const fetchEnums = async () => {
    try {
      setEnumLoading(true)
      const response = await enumApi.getAllEnums()
      if (response) {
        setEnumOptions(response)
      }
    } catch (error) {
      console.error('获取枚举选项失败:', error)
    } finally {
      setEnumLoading(false)
    }
  }

  // 获取税费原始数据
  const fetchTaxSettings = async () => {
    setLoading(true)
    try {
      console.log('开始获取税费设置数据...')
      const response = await taxSettingApi.getAllTaxSettings()
      console.log('税费设置API响应:', response)
      if (response && Array.isArray(response)) {
        setRawTaxData(response)
      } else {
        console.error('API返回格式不正确:', response)
        message.error('获取税费设置列表失败')
      }
    } catch (error) {
      console.error('加载税费设置数据失败:', error)
      message.error('加载税费设置数据失败: ' + (error.message || error))
    } finally {
      setLoading(false)
    }
  }

  // 根据枚举选项映射数据
  const mapTaxData = (data, enums) => {
    if (!data || !enums) return []
    
    // 创建枚举映射
    const BEARER_MAP = createEnumMap(enums.taxBearer)
    const BASE_TYPE_MAP = createEnumMap(enums.taxBaseType)
    const CURRENCY_MAP = createEnumMap(enums.currency)
    const CALCULATION_RULE_MAP = createEnumMap(enums.taxCalculationRule)
    const DEDUCTIBLE_MAP = createEnumMap(enums.taxDeductible)
    const REFUNDABLE_MAP = createEnumMap(enums.taxRefundable)
    const SETTLEMENT_RULE_MAP = createEnumMap(enums.taxSettlementRule)
    const STATUS_MAP = createEnumMap(enums.commonStatus)
    
    return data.map(item => ({
      ...item,
      bearerLabel: BEARER_MAP[item.bearer] || item.bearer,
      baseTypeLabel: BASE_TYPE_MAP[item.baseType] || item.baseType,
      rateCurrencyLabel: CURRENCY_MAP[item.rateCurrency] || item.rateCurrency,
      calculationRuleLabel: CALCULATION_RULE_MAP[item.calculationRule] || item.calculationRule,
      deductibleLabel: DEDUCTIBLE_MAP[item.deductible] || item.deductible,
      refundableLabel: REFUNDABLE_MAP[item.refundable] || item.refundable,
      settlementRuleLabel: SETTLEMENT_RULE_MAP[item.settlementRule] || item.settlementRule,
      status: STATUS_MAP[item.status] || item.status
    }))
  }

  // 初始加载数据
  useEffect(() => {
    const fetchData = async () => {
      await Promise.all([fetchEnums(), fetchTaxSettings()])
    }
    fetchData()
  }, [])

  // 当枚举或原始数据变化时，重新映射数据
  useEffect(() => {
    if (!enumLoading && rawTaxData.length > 0) {
      const mappedData = mapTaxData(rawTaxData, enumOptions)
      console.log('映射后的数据:', mappedData)
      setTaxList(mappedData)
    }
  }, [enumLoading, rawTaxData, enumOptions])
  
  const handleAddTax = () => {
    navigate('/group-management/add-edit-tax')
  }
  
  const handleEditTax = (record) => {
    navigate('/group-management/add-edit-tax', { state: { record } })
  }
  
  const columns = [
    {
      title: '税费项编码',
      dataIndex: 'taxCode',
      key: 'taxCode',
      width: 150
    },
    {
      title: '税费法定全称',
      dataIndex: 'legalName',
      key: 'legalName',
      width: 220
    },
    {
      title: '税费承担主体',
      dataIndex: 'bearerLabel',
      key: 'bearerLabel',
      width: 140
    },
    {
      title: '计税基数类型',
      dataIndex: 'baseTypeLabel',
      key: 'baseTypeLabel',
      width: 160
    },
    {
      title: '税率/定额标准',
      key: 'rate',
      width: 140,
      render: (_, record) => (
        <span>{record.rateAmount}{record.rateCurrencyLabel}</span>
      )
    },
    {
      title: '计税计算规则',
      dataIndex: 'calculationRuleLabel',
      key: 'calculationRuleLabel',
      width: 180
    },
    {
      title: '是否可进项抵扣',
      dataIndex: 'deductibleLabel',
      key: 'deductibleLabel',
      width: 130
    },
    {
      title: '取消订单是否可退',
      dataIndex: 'refundableLabel',
      key: 'refundableLabel',
      width: 140
    },
    {
      title: '结算缴纳规则',
      dataIndex: 'settlementRuleLabel',
      key: 'settlementRuleLabel',
      width: 200
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <span style={{ 
          color: status === '启用' ? '#52c41a' : '#ff4d4f',
          fontWeight: 500
        }}>
          {status}
        </span>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditTax(record)}>编辑</Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        集团税率管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="税费项编码" 
              prefix={<SearchOutlined />} 
              allowClear 
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="税费法定全称" 
              allowClear 
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="税费承担主体" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
            >
              {enumOptions.taxBearer.map(option => (
                <Option key={option.value} value={option.value}>{option.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
            >
              {enumOptions.commonStatus.map(option => (
                <Option key={option.value} value={option.value}>{option.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" style={{ height: 32 }}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} style={{ height: 32 }}>搜索</Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddTax}>
          新增集团税率
        </Button>
      </div>

      {/* 税率列表表格 */}
      <Table
        columns={columns}
        dataSource={taxList}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 2000 }}
      />
    </div>
  )
}

export default TaxSetting

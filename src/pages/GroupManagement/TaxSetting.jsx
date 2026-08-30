import { useState, useEffect } from 'react'
import { App, Table, Button, Space, Card, Row, Col, Input, Select } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { taxSettingApi, enumApi } from '../../utils/api'

const { Option } = Select

const TaxSetting = () => {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [taxList, setTaxList] = useState([])
  const [loading, setLoading] = useState(false)
  const [enumLoading, setEnumLoading] = useState(true)
  const [rawTaxData, setRawTaxData] = useState([])
  const [statusOptions, setStatusOptions] = useState([])
  const [filters, setFilters] = useState({ taxCode: '', legalName: '', status: '' })

  // 获取状态枚举选项
  const fetchEnums = async () => {
    try {
      setEnumLoading(true)
      const response = await enumApi.getCommonStatus()
      if (response && Array.isArray(response)) {
        setStatusOptions(response)
      }
    } catch (error) {
      message.error(error?.error || '获取状态选项失败')
    } finally {
      setEnumLoading(false)
    }
  }

  // 获取税费设置原始数据
  const fetchTaxSettings = async () => {
    setLoading(true)
    try {
      const response = await taxSettingApi.getAllTaxSettings()
      if (response && Array.isArray(response)) {
        setRawTaxData(response)
        setTaxList(response)
      } else {
        message.error('获取税费设置列表失败')
      }
    } catch (error) {
      message.error('加载税费设置数据失败: ' + (error?.error || error?.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  // 初始加载数据
  useEffect(() => {
    fetchEnums()
    fetchTaxSettings()
  }, [])

  // 状态显示映射函数
  const getStatusLabel = (statusValue) => {
    const found = statusOptions.find(opt => opt.value === statusValue)
    return found ? found.label : (statusValue === 'active' ? '启用' : '停用')
  }

  const handleAddTax = () => {
    navigate('/group-management/add-edit-tax')
  }

  const handleEditTax = (record) => {
    navigate('/group-management/add-edit-tax', { state: { record } })
  }

  const handleSearch = () => {
    const code = filters.taxCode.trim().toLowerCase()
    const name = filters.legalName.trim().toLowerCase()
    setTaxList(rawTaxData.filter(item => (
      (!code || item.taxCode?.toLowerCase().includes(code))
      && (!name || item.legalName?.toLowerCase().includes(name))
      && (!filters.status || item.status === filters.status)
    )))
  }

  const handleReset = () => {
    setFilters({ taxCode: '', legalName: '', status: '' })
    setTaxList(rawTaxData)
  }

  const columns = [
    {
      title: '税率CODE',
      dataIndex: 'taxCode',
      key: 'taxCode',
      width: 180
    },
    {
      title: '税率名称',
      dataIndex: 'legalName',
      key: 'legalName',
      width: 280
    },
    {
      title: '税率',
      key: 'rate',
      width: 160,
      render: (_, record) => (
        <span style={{ fontWeight: 500 }}>{record.rateAmount}%</span>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (status) => {
        const label = getStatusLabel(status)
        return (
          <span style={{ 
            color: label === '启用' || status === 'active' ? '#52c41a' : '#ff4d4f',
            fontWeight: 500
          }}>
            {label}
          </span>
        )
      }
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
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
              placeholder="税率CODE" 
              prefix={<SearchOutlined />} 
              allowClear 
              value={filters.taxCode}
              onChange={(event) => setFilters(current => ({ ...current, taxCode: event.target.value }))}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="税率名称" 
              allowClear 
              value={filters.legalName}
              onChange={(event) => setFilters(current => ({ ...current, legalName: event.target.value }))}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              loading={enumLoading}
              value={filters.status || undefined}
              onChange={(value) => setFilters(current => ({ ...current, status: value || '' }))}
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
            >
              {statusOptions.map(option => (
                <Option key={option.value} value={option.value}>{option.label}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={8} lg={6} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" style={{ height: 32 }} onClick={handleReset}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} style={{ height: 32 }} onClick={handleSearch}>搜索</Button>
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
      />
    </div>
  )
}

export default TaxSetting

import { useState, useEffect } from 'react'
import { App, Table, Button, Space, Card, Row, Col, Input, Select, Spin } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DollarOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import api from '../../utils/api'

const { Option } = Select

const GroupRateCode = () => {
  const navigate = useNavigate()
  const { message, modal } = App.useApp()

  // 状态管理
  const [rateCodes, setRateCodes] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    rateCategory: '',
    marketCode: '',
    sourceCode: '',
    type: '',
    derivativeLevel: '',
    promotion: '',
    status: '',
    rateClass: ''
  })
  const [marketCodes, setMarketCodes] = useState([])
  const [loadingMarketCodes, setLoadingMarketCodes] = useState(false)
  const [sourceCodes, setSourceCodes] = useState([])
  const [loadingSourceCodes, setLoadingSourceCodes] = useState(false)
  const [rateCategories, setRateCategories] = useState([])
  const [allPackages, setAllPackages] = useState([])
  const [guaranteePolicies, setGuaranteePolicies] = useState([])
  const [cancellationPolicies, setCancellationPolicies] = useState([])

  
  // 处理新增房价码
  const handleAddRateCode = () => {
    navigate('/group-management/add-rate-code')
  }
  
  // 处理编辑房价码
  const handleEditRateCode = (record) => {
    navigate('/group-management/add-rate-code', { state: { record } })
  }
  
  // 处理启用房价码
  const handleEnableRateCode = async (record) => {
    try {
      setLoading(true)
      await api.put(`/group-rate-codes/${record.id}/enable`)
      message.success('启用成功')
      fetchRateCodes()
    } catch (error) {
      console.error('启用失败:', error)
      message.error('启用失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }
  
  // 处理停用房价码
  const handleDisableRateCode = async (record) => {
    try {
      setLoading(true)
      await api.put(`/group-rate-codes/${record.id}/disable`)
      message.success('停用成功')
      fetchRateCodes()
    } catch (error) {
      console.error('停用失败:', error)
      message.error('停用失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }
  
  // 处理停用前确认（检查子衍生码）
  const confirmDisableRateCode = (record) => {
    // 检查是否有子衍生码（通过查看列表中parentRateCodeId匹配的记录）
    const hasChildren = record.derivativeLevelValue === 'basic' || record.derivativeLevelValue === 'level1'
    
    if (hasChildren) {
      modal.confirm({
        title: '确认停用',
        content: `停用房价码"${record.name}"将同时停用其下所有衍生码，并将已下发到酒店的该房价码全部置为无效。确定要继续吗？`,
        onOk: () => handleDisableRateCode(record)
      })
    } else {
      modal.confirm({
        title: '确认停用',
        content: `停用房价码"${record.name}"将同时将已下发到酒店的该房价码置为无效。确定要继续吗？`,
        onOk: () => handleDisableRateCode(record)
      })
    }
  }
  

  

  
  // 从后端API获取集团房价码数据
  const fetchRateCodes = async () => {
    try {
      setLoading(true)
      setError(null)
      
      // 构建查询参数
      const params = {}
      Object.entries(searchParams).forEach(([key, value]) => {
        if (value) params[key] = value
      })
      
      
      const response = await api.get('/group-rate-codes', {
        params
      })
      
      
      // 转换数据格式以匹配前端展示需求
      const formattedRateCodes = response.map(item => {
        // 转换衍生层级为显示文本
        let derivativeLevelText = ''
        if (item.derivativeLevel === 'basic') {
          derivativeLevelText = '基础价格'
        } else if (item.derivativeLevel === 'level1') {
          derivativeLevelText = '一级衍生价'
        } else if (item.derivativeLevel === 'level2') {
          derivativeLevelText = '二级衍生价'
        }
        
        // 根据rateType设置类型显示
        let typeText = ''
        if (item.rateType === 'basic') {
          typeText = '基础房价码'
        } else if (item.rateType === 'level1') {
          typeText = '一级衍生码'
        } else if (item.rateType === 'level2') {
          typeText = '二级衍生码'
        } else if (item.rateType === 'derivative') {
          typeText = '衍生房价码'
        }
        
        return {
          id: item.id,
          name: item.rateName,
          code: item.rateCode,
          rateCategory: (() => {
            const cat = rateCategories.find(c => c.code === item.rateCategory)
            return cat ? `${cat.name}（${cat.code}）` : (item.rateCategory || '')
          })(),
          marketCode: (() => {
            const mc = marketCodes.find(c => c.code === item.marketCode)
            return mc ? `${mc.name}（${mc.code}）` : (item.marketCode || '')
          })(),
          sourceCode: (() => {
            const sc = sourceCodes.find(c => c.code === item.sourceCode)
            return sc ? `${sc.name}（${sc.code}）` : (item.sourceCode || '')
          })(),
          type: typeText || '基础房价码',
          derivativeLevel: derivativeLevelText,
          derivativeLevelValue: item.derivativeLevel,
          parentRateCodeId: item.parentRateCodeId,
          status: item.status === 'active' ? '启用' : '停用',
          packages: (() => {
            try {
              const pkgs = item.packages ? (typeof item.packages === 'string' ? JSON.parse(item.packages) : item.packages) : []
              if (!Array.isArray(pkgs)) return []
              return pkgs.map(code => {
                const pkg = allPackages.find(p => p.code === code)
                return pkg ? `${pkg.name}（${pkg.code}）` : code
              })
            } catch (e) {
              return []
            }
          })(),
          refundable: (() => {
            const cp = cancellationPolicies.find(p => p.code === item.cancellationRule)
            return cp ? `${cp.name}（${cp.code}）` : (item.cancellationRule || '')
          })(),
          guarantee: (() => {
            const gp = guaranteePolicies.find(p => p.code === item.guaranteeRule)
            return gp ? `${gp.name}（${gp.code}）` : (item.guaranteeRule || '')
          })(),
          promotion: (() => {
            const promoMap = { 'unlimited': '不限制', 'limited': '限制部分', 'disabled': '不可用' }
            return promoMap[item.promotionRule] || item.promotionRule || ''
          })()
        }
      })
      
      setRateCodes(formattedRateCodes)
    } catch (error) {
      console.error('获取房价码数据失败:', error)
      setRateCodes([])
      message.error(error?.message || '获取集团房价码失败')
    } finally {
      setLoading(false)
    }
  }
  
  // 获取第三级市场码
  const fetchThirdLevelMarketCodes = async () => {
    try {
      setLoadingMarketCodes(true)
      const response = await api.get('/market-codes/third-level')
      setMarketCodes(response)
    } catch (error) {
      console.error('获取第三级市场码失败:', error)
      setMarketCodes([])
      message.error('获取市场码数据失败，请稍后重试')
    } finally {
      setLoadingMarketCodes(false)
    }
  }

  // 获取第三级来源码
  const fetchThirdLevelSourceCodes = async () => {
    try {
      setLoadingSourceCodes(true)
      const response = await api.get('/source-codes/third-level')
      setSourceCodes(response)
    } catch (error) {
      console.error('获取第三级来源码失败:', error)
      setSourceCodes([])
      message.error('获取来源码数据失败，请稍后重试')
    } finally {
      setLoadingSourceCodes(false)
    }
  }

  // 初始化时获取数据
  useEffect(() => {
    fetchThirdLevelMarketCodes()
    fetchThirdLevelSourceCodes()
    fetchRateCategories()
    fetchAllPackages()
    fetchPolicies()
  }, [])

  // 当引用数据加载完成后获取房价码列表
  useEffect(() => {
    fetchRateCodes()
  }, [marketCodes, sourceCodes, rateCategories, allPackages, guaranteePolicies, cancellationPolicies])

  // 获取房价大类
  const fetchRateCategories = async () => {
    try {
      const response = await api.get('/rate-types/active')
      setRateCategories(response || [])
    } catch (error) {
      console.error('获取房价大类失败:', error)
      setRateCategories([])
    }
  }

  // 获取包价数据
  const fetchAllPackages = async () => {
    try {
      const response = await api.get('/packages')
      setAllPackages(response || [])
    } catch (error) {
      console.error('获取包价数据失败:', error)
      setAllPackages([])
    }
  }

  // 获取担保和取消政策
  const fetchPolicies = async () => {
    try {
      const [gpRes, cpRes] = await Promise.all([
        api.get('/guarantee-policies'),
        api.get('/cancellation-policies')
      ])
      setGuaranteePolicies(gpRes || [])
      setCancellationPolicies(cpRes || [])
    } catch (error) {
      console.error('获取政策数据失败:', error)
    }
  }
  
  // 处理搜索参数变化
  const handleSearchParamChange = (key, value) => {
    setSearchParams(prev => ({
      ...prev,
      [key]: value || ''
    }))
  }
  
  // 处理搜索
  const handleSearch = () => {
    fetchRateCodes()
  }
  
  
  const columns = [
    {
      title: '房价码名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '房价码代码',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: '房价类别',
      dataIndex: 'rateCategory',
      key: 'rateCategory',
      width: 120
    },
    {
      title: '市场码',
      dataIndex: 'marketCode',
      key: 'marketCode',
      width: 120
    },
    {
      title: '来源码',
      dataIndex: 'sourceCode',
      key: 'sourceCode',
      width: 120
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 120
    },
    {
      title: '包价',
      dataIndex: 'packages',
      key: 'packages',
      width: 150,
      render: (packages) => {
        if (!packages || packages.length === 0) return '-'
        return (
          <div>
            {packages.map((pkg, index) => (
              <div key={index}>{pkg}</div>
            ))}
          </div>
        )
      }
    },
    {
      title: '退改政策',
      dataIndex: 'refundable',
      key: 'refundable',
      width: 120
    },
    {
      title: '担保政策',
      dataIndex: 'guarantee',
      key: 'guarantee',
      width: 150
    },
    {
      title: '促销优惠',
      dataIndex: 'promotion',
      key: 'promotion',
      width: 150
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
      width: 220,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditRateCode(record)}>编辑</Button>
          {record.status === '启用' ? (
            <Button 
              type="link" 
              size="small" 
              danger
              onClick={() => confirmDisableRateCode(record)}
            >
              停用
            </Button>
          ) : (
            <Button 
              type="link" 
              size="small" 
              style={{ color: '#52c41a' }}
              onClick={() => {
                modal.confirm({
                  title: '确认启用',
                  content: `确定要启用房价码"${record.name}"吗？`,
                  onOk: () => handleEnableRateCode(record)
                })
              }}
            >
              启用
            </Button>
          )}
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <DollarOutlined />
        集团房价码管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="房价码名称" 
              prefix={<SearchOutlined />} 
              allowClear
              value={searchParams.name}
              onChange={(e) => handleSearchParamChange('name', e.target.value)}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="房价码代码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => handleSearchParamChange('code', e.target.value)}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="房价类别" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.rateCategory || undefined}
              onChange={(value) => handleSearchParamChange('rateCategory', value)}
            >
              <Option value="公共价">公共价</Option>
              <Option value="协议价">协议价</Option>
              <Option value="团队价">团队价</Option>
              <Option value="会员价">会员价</Option>
              <Option value="促销价">促销价</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="市场码" 
              allowClear 
              loading={loadingMarketCodes}
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.marketCode || undefined}
              onChange={(value) => handleSearchParamChange('marketCode', value)}
            >
              {marketCodes.map(code => (
                <Option key={code.id} value={code.code}>
                  {code.name} ({code.code})
                </Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="来源码" 
              allowClear 
              loading={loadingSourceCodes}
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.sourceCode || undefined}
              onChange={(value) => handleSearchParamChange('sourceCode', value)}
            >
              {sourceCodes.map(code => (
                <Option key={code.id} value={code.code}>
                  {code.name} ({code.code})
                </Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="类型" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.type || undefined}
              onChange={(value) => handleSearchParamChange('type', value)}
            >
              <Option value="基础房价码">基础房价码</Option>
              <Option value="一级衍生码">一级衍生码</Option>
              <Option value="二级衍生码">二级衍生码</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="房价大类" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.rateClass || undefined}
              onChange={(value) => handleSearchParamChange('rateClass', value)}
            >
              <Option value="public">公共价</Option>
              <Option value="agreement">协议价</Option>
              <Option value="team">团队价</Option>
              <Option value="member">会员价</Option>
              <Option value="promotion">促销价</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="促销优惠" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.promotion || undefined}
              onChange={(value) => handleSearchParamChange('promotion', value)}
            >
              <Option value="不限制">不限制</Option>
              <Option value="限制部分优惠">限制部分优惠</Option>
              <Option value="不可用优惠">不可用优惠</Option>
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
              value={searchParams.status === 'active' ? '启用' : searchParams.status === 'inactive' ? '停用' : undefined}
              onChange={(value) => handleSearchParamChange('status', value === '启用' ? 'active' : value === '停用' ? 'inactive' : '')}
            >
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </Col>
        </Row>
        <Row style={{ marginTop: 16 }}>
          <Col span={24} style={{ textAlign: 'right' }}>
            <Button 
              type="primary" 
              icon={<SearchOutlined />} 
              onClick={handleSearch}
              style={{ height: 32 }}
            >
              搜索
            </Button>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddRateCode}>
          新增房价码
        </Button>
      </div>

      {/* 房价码列表表格 */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '100px 0' }}>
          <Spin size="large"><span>加载中...</span></Spin>
        </div>
      ) : error ? (
        <div style={{ textAlign: 'center', padding: '100px 0', color: '#ff4d4f' }}>
          <p>{error}</p>
          <Button type="primary" onClick={fetchRateCodes} style={{ marginTop: 16 }}>
            重新加载
          </Button>
        </div>
      ) : (
        <Table
          columns={columns}
          dataSource={rateCodes}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
          }}
          scroll={{ x: 1000 }}
          locale={{
            emptyText: '暂无集团房价码数据'
          }}
        />
      )}
      

    </div>
  )
}

export default GroupRateCode

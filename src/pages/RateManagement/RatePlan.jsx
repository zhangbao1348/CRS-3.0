import { useState, useEffect } from 'react'
import { App, Table, Button, Space, Input, Select, Tag } from 'antd'
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DollarOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useHotelContext } from '../../contexts/HotelContext.jsx'
import { ratePlanApi } from '../../utils/api.js'
import { FilterPanel, PageScaffold, TablePanel } from '../../components/ui'

const { Option } = Select

const RatePlan = () => {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const { selectedHotel } = useHotelContext()
  const [ratePlans, setRatePlans] = useState([])
  const [loading, setLoading] = useState(false)

  // 引用数据
  const [marketCodes, setMarketCodes] = useState([])
  const [sourceCodes, setSourceCodes] = useState([])
  const [rateCategories, setRateCategories] = useState([])
  const [allPackages, setAllPackages] = useState([])
  const [guaranteePolicies, setGuaranteePolicies] = useState([])
  const [cancellationPolicies, setCancellationPolicies] = useState([])

  // 筛选参数
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    rateCategory: '',
    type: '',
    status: ''
  })

  const handleAddRatePlan = () => {
    navigate('/rate-management/add-rate-plan')
  }

  const handleEditRatePlan = (record) => {
    navigate(`/rate-management/edit-rate-plan/${record.id}`)
  }

  // 加载引用数据
  const fetchReferenceData = async () => {
    try {
      const [mcRes, scRes, rcRes, pkgRes, gpRes, cpRes] = await Promise.all([
        axios.get('/api/market-codes/third-level'),
        axios.get('/api/source-codes/third-level'),
        axios.get('/api/rate-types/active'),
        axios.get('/api/packages'),
        axios.get('/api/guarantee-policies'),
        axios.get('/api/cancellation-policies')
      ])
      setMarketCodes(mcRes.data || [])
      setSourceCodes(scRes.data || [])
      setRateCategories(rcRes.data || [])
      setAllPackages(pkgRes.data || [])
      setGuaranteePolicies(gpRes.data || [])
      setCancellationPolicies(cpRes.data || [])
    } catch {
      message.error('加载价格计划关联数据失败')
    }
  }

  // 获取价格计划列表
  const fetchRatePlans = async () => {
    if (!selectedHotel) {
      setRatePlans([])
      return
    }

    setLoading(true)
    try {
      const response = await ratePlanApi.getRatePlansByHotelCode(selectedHotel)
      if (response.success) {
        const data = response.data || []
        const formattedData = data.map(plan => {
          // 类型映射
          let typeText = '基础价格计划'
          if (plan.rateType === 'level1') typeText = '一级衍生码'
          else if (plan.rateType === 'level2') typeText = '二级衍生码'
          else if (plan.rateType === 'derivative') {
            typeText = plan.derivativeLevel === 'level2' ? '二级衍生码' : '一级衍生码'
          }

          // 房价大类名称映射
          const cat = rateCategories.find(c => c.code === plan.rateCategory)
          const rateCategoryDisplay = cat ? `${cat.name}（${cat.code}）` : (plan.rateCategory || '')

          // 市场码名称映射
          const mc = marketCodes.find(c => c.code === plan.marketCode)
          const marketCodeDisplay = mc ? `${mc.name}（${mc.code}）` : (plan.marketCode || '')

          const sc = sourceCodes.find(c => c.code === plan.sourceCode)
          const sourceCodeDisplay = sc ? `${sc.name}（${sc.code}）` : (plan.sourceCode || '')

          // 包价名称映射
          let packagesList = []
          try {
            const pkgs = plan.packages ? (typeof plan.packages === 'string' ? JSON.parse(plan.packages) : plan.packages) : []
            if (Array.isArray(pkgs)) {
              packagesList = pkgs.map(code => {
                const pkg = allPackages.find(p => p.code === code)
                return pkg ? `${pkg.name}（${pkg.code}）` : code
              })
            }
          } catch (e) {
            packagesList = []
          }

          return {
            id: plan.id,
            name: plan.rateName,
            code: plan.rateCode,
            sourceGroupRateCode: plan.sourceGroupRateCode || '',
            rateCategory: rateCategoryDisplay,
            marketCode: marketCodeDisplay,
            sourceCode: sourceCodeDisplay,
            type: typeText,
            packages: packagesList,
            status: plan.status === 'active' ? '启用' : '停用',
            refundable: (() => {
              const cp = cancellationPolicies.find(p => p.code === plan.cancellationRule)
              return cp ? `${cp.name}（${cp.code}）` : (plan.cancellationRule || '')
            })(),
            guarantee: (() => {
              const gp = guaranteePolicies.find(p => p.code === plan.guaranteeRule)
              return gp ? `${gp.name}（${gp.code}）` : (plan.guaranteeRule || '')
            })(),
            promotion: (() => {
              const promoMap = { 'unlimited': '不限制', 'limited': '限制部分', 'disabled': '不可用' }
              return promoMap[plan.promotionRule] || plan.promotionRule || ''
            })()
          }
        })
        setRatePlans(formattedData)
      } else {
        message.error('获取价格计划失败')
        setRatePlans([])
      }
    } catch {
      message.error('获取价格计划失败')
      setRatePlans([])
    } finally {
      setLoading(false)
    }
  }

  // 初始化加载引用数据
  useEffect(() => {
    fetchReferenceData()
  }, [])

  // 当酒店或引用数据变化时重新获取列表
  useEffect(() => {
    fetchRatePlans()
  }, [selectedHotel, marketCodes, sourceCodes, rateCategories, allPackages, guaranteePolicies, cancellationPolicies])

  // 前端筛选
  const filteredData = ratePlans.filter(item => {
    if (searchParams.name && !item.name.includes(searchParams.name)) return false
    if (searchParams.code && !item.code.includes(searchParams.code)) return false
    if (searchParams.rateCategory && !item.rateCategory.includes(searchParams.rateCategory)) return false
    if (searchParams.type && item.type !== searchParams.type) return false
    if (searchParams.status && item.status !== searchParams.status) return false
    return true
  })

  const handleReset = () => {
    setSearchParams({ name: '', code: '', rateCategory: '', type: '', status: '' })
  }

  const columns = [
    {
      title: '价格计划名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '价格计划代码',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: '房价大类',
      dataIndex: 'rateCategory',
      key: 'rateCategory',
      width: 150
    },
    {
      title: '市场码',
      dataIndex: 'marketCode',
      key: 'marketCode',
      width: 150
    },
    {
      title: '来源码',
      dataIndex: 'sourceCode',
      key: 'sourceCode',
      width: 150
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 120
    },
    {
      title: '来源',
      dataIndex: 'sourceGroupRateCode',
      key: 'sourceGroupRateCode',
      width: 150,
      render: (sourceGroupRateCode) => sourceGroupRateCode ? (
        <Tag color="blue">集团（{sourceGroupRateCode}）</Tag>
      ) : (
        <Tag color="green">自建</Tag>
      )
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
      render: (status, record) => (
        <Space size={6} wrap>
          <span style={{
            color: status === '启用' ? '#52c41a' : '#ff4d4f',
            fontWeight: 500
          }}>
            {status}
          </span>
          {record.sourceGroupRateCode && status === '停用' && <Tag>已被集团回收</Tag>}
        </Space>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditRatePlan(record)}>编辑</Button>
        </Space>
      )
    }
  ]

  return (
    <PageScaffold
      className="fade-in"
      eyebrow="RATE PLAN CATALOG"
      title={<><DollarOutlined /> 价格计划管理</>}
      description="维护基础与衍生价格计划，统一关联市场码、来源码、包价和退改担保政策。"
      actions={(
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAddRatePlan}>
          新增价格计划
        </Button>
      )}
    >
      <FilterPanel extra={<Button onClick={handleReset}>重置条件</Button>}>
        <div className="ui-filter-grid">
          <label className="ui-field">
            <span className="ui-field__label">价格计划名称</span>
            <Input
              aria-label="价格计划名称"
              placeholder="请输入价格计划名称"
              prefix={<SearchOutlined />}
              allowClear
              value={searchParams.name}
              onChange={(e) => setSearchParams({...searchParams, name: e.target.value})}
            />
          </label>
          <label className="ui-field">
            <span className="ui-field__label">价格计划代码</span>
            <Input
              aria-label="价格计划代码"
              placeholder="请输入价格计划代码"
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({...searchParams, code: e.target.value})}
            />
          </label>
          <label className="ui-field">
            <span className="ui-field__label">计划类型</span>
            <Select
              aria-label="计划类型"
              placeholder="全部类型"
              allowClear
              style={{ width: '100%' }}
              value={searchParams.type || undefined}
              onChange={(value) => setSearchParams({...searchParams, type: value || ''})}
            >
              <Option value="基础价格计划">基础价格计划</Option>
              <Option value="一级衍生码">一级衍生码</Option>
              <Option value="二级衍生码">二级衍生码</Option>
            </Select>
          </label>
          <label className="ui-field">
            <span className="ui-field__label">启用状态</span>
            <Select
              aria-label="启用状态"
              placeholder="全部状态"
              allowClear
              style={{ width: '100%' }}
              value={searchParams.status || undefined}
              onChange={(value) => setSearchParams({...searchParams, status: value || ''})}
            >
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </label>
        </div>
      </FilterPanel>

      <TablePanel title="价格计划列表" description={`当前显示 ${filteredData.length} 条计划`}>
        <Table
          loading={loading}
          columns={columns}
          dataSource={filteredData}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
          }}
          scroll={{ x: 1750 }}
          locale={{ emptyText: '暂无价格计划数据' }}
        />
      </TablePanel>
    </PageScaffold>
  )
}

export default RatePlan

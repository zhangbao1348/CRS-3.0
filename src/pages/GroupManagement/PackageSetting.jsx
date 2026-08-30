import { useCallback, useEffect, useState } from 'react'
import dayjs from 'dayjs'
import { App, Table, Button, Space, Card, Row, Col, Input, Select, Tag, Typography, Popconfirm } from 'antd'
import {
  PlusOutlined,
  EditOutlined,
  GiftOutlined,
  ReloadOutlined,
  DeleteOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { packageApi } from '../../utils/api'

const { Option } = Select
const { Text } = Typography

const DEFAULT_SEARCH_PARAMS = {
  keyword: '',
  type: '',
  frequency: '',
  quantityType: '',
  status: ''
}

const FILTER_CONTROL_STYLE = {
  width: '100%',
  height: 40
}

const frequencyOptions = [
  { value: 'daily', label: '每天1次' },
  { value: 'per_stay', label: '每入住一次' },
  { value: 'arrival_day', label: '到达当天发放一次' },
  { value: 'departure_day', label: '最后一天发放一次' },
  { value: 'except_departure', label: '除最后一天每天一次' }
]

const quantityTypeOptions = [
  { value: 'fixed', label: '固定份数' },
  { value: 'per_order', label: '按订单' },
  { value: 'per_room', label: '按房间' },
  { value: 'per_person', label: '按人数' },
  { value: 'per_adult', label: '按成人数' },
  { value: 'per_child', label: '按儿童数' }
]

const frequencyLabelMap = {
  daily: '每天1次',
  per_stay: '每入住一次',
  arrival_day: '到达当天发放一次',
  departure_day: '最后一天发放一次',
  except_departure: '除最后一天每天一次'
}

const quantityTypeLabelMap = {
  fixed: '固定份数',
  per_order: '按订单',
  per_room: '按房间',
  per_person: '按人数',
  per_adult: '按成人数',
  per_child: '按儿童数'
}

const formatPackagePrice = (pkg) => {
  if (pkg.fixedPrice !== null && pkg.fixedPrice !== undefined) {
    return `¥${Number(pkg.fixedPrice).toFixed(2)}`
  }

  if (pkg.priceType === 'group') {
    return '集团设置'
  }

  return '-'
}

const getFrequencyLabel = (frequency) => frequencyLabelMap[frequency] || frequency || '-'

const getQuantityTypeLabel = (quantityType) => quantityTypeLabelMap[quantityType] || quantityType || '-'

const formatQuantitySummary = (pkg) => {
  const quantityLabel = getQuantityTypeLabel(pkg.quantityType)
  const quantityValue = pkg.fixedQuantity ?? '-'
  return `${quantityLabel} / ${quantityValue}`
}

const formatUpdatedAt = (value) => {
  if (!value) {
    return '-'
  }

  const parsedValue = dayjs(value)
  if (!parsedValue.isValid()) {
    return '-'
  }

  return parsedValue.format('YYYY-MM-DD HH:mm')
}

const PackageSetting = () => {
  const { message } = App.useApp()
  const [packages, setPackages] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchParams, setSearchParams] = useState(DEFAULT_SEARCH_PARAMS)
  const navigate = useNavigate()

  const packageTypes = [
    { value: '早餐', label: '早餐' },
    { value: '午餐', label: '午餐' },
    { value: '晚餐', label: '晚餐' },
    { value: '综合', label: '综合' },
    { value: '下午茶', label: '下午茶' },
    { value: '门票', label: '门票' },
    { value: '其他', label: '其他' },
    { value: '免费增早', label: '免费增早' },
    { value: '延时退房', label: '延时退房' },
    { value: '提前入住', label: '提前入住' }
  ]

  const statusOptions = [
    { value: 'active', label: '启用' },
    { value: 'inactive', label: '停用' }
  ]

  const fetchPackages = useCallback(async (params) => {
    setLoading(true)
    try {
      const requestData = {
        keyword: params.keyword?.trim() || '',
        type: params.type || '',
        frequency: params.frequency || '',
        quantityType: params.quantityType || '',
        status: params.status || ''
      }

      const response = await packageApi.searchPackages(requestData)
      setPackages(Array.isArray(response) ? response : [])
    } catch (error) {
      message.error(error?.error || '获取包价列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(() => {
      fetchPackages(searchParams)
    }, 300)

    return () => window.clearTimeout(timer)
  }, [fetchPackages, searchParams])

  const handleReset = () => {
    setSearchParams(DEFAULT_SEARCH_PARAMS)
  }

  const handleRefresh = () => {
    fetchPackages(searchParams)
  }

  const handleAddPackage = () => {
    navigate('/group-management/add-package')
  }

  const handleEditPackage = (record) => {
    navigate(`/group-management/edit-package?id=${record.id}`)
  }

  /** 删除动作保留二次确认，引用约束交由服务端最终判定。 */
  const handleDeletePackage = async (record) => {
    try {
      await packageApi.deletePackage(record.id)
      message.success('包价删除成功')
      fetchPackages(searchParams)
    } catch (error) {
      message.error(error?.error || error?.message || '包价删除失败')
    }
  }

  const columns = [
    {
      title: '包价名称',
      key: 'name',
      width: 240,
      render: (_, record) => (
        <div>
          <div style={{ fontWeight: 700, color: '#102a43', marginBottom: 4 }}>{record.name}</div>
          <Text type="secondary" style={{ fontSize: 12 }}>
            {record.description || '暂无描述'}
          </Text>
        </div>
      )
    },
    {
      title: '包价代码',
      key: 'code',
      width: 150,
      render: (_, record) => (
        <Text code style={{ fontSize: 12 }}>
          {record.code}
        </Text>
      )
    },
    {
      title: '包价类型',
      key: 'type',
      width: 120,
      render: (_, record) => <Tag color="blue">{record.type}</Tag>
    },
    {
      title: '发放频率',
      key: 'frequency',
      width: 170,
      render: (_, record) => getFrequencyLabel(record.frequency)
    },
    {
      title: '计数方式/份数',
      key: 'quantity',
      width: 160,
      render: (_, record) => formatQuantitySummary(record)
    },
    {
      title: '固定价格',
      key: 'price',
      width: 130,
      render: (_, record) => (
        <span style={{ fontWeight: 700, color: record.fixedPrice !== null && record.fixedPrice !== undefined ? '#cf1322' : '#475569' }}>
          {formatPackagePrice(record)}
        </span>
      )
    },
    {
      title: '含税',
      key: 'taxIncluded',
      width: 100,
      render: (_, record) => (
        <Tag color={record.taxIncluded ? 'green' : 'default'}>
          {record.taxIncluded ? '含税' : '未税'}
        </Tag>
      )
    },
    {
      title: '状态',
      key: 'status',
      width: 100,
      render: (_, record) => (
        <Tag color={record.status === 'active' ? 'success' : 'default'}>
          {record.status === 'active' ? '启用' : '停用'}
        </Tag>
      )
    },
    {
      title: '更新时间',
      key: 'updatedAt',
      width: 168,
      render: (_, record) => (
        <Text type="secondary">{formatUpdatedAt(record.updatedAt)}</Text>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditPackage(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除该包价？"
            description="已被房价码引用的包价将被系统拒绝删除。"
            okText="确定"
            cancelText="取消"
            okButtonProps={{ danger: true }}
            onConfirm={() => handleDeletePackage(record)}
          >
            <Button danger type="link" size="small" icon={<DeleteOutlined />} aria-label={`删除包价 ${record.name}`}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in" style={{ paddingBottom: 32 }}>
      <style>
        {`
          .group-package-filter-input {
            height: 40px;
            border-radius: 8px;
            font-size: 14px;
          }

          .group-package-filter-input input,
          .group-package-filter-input input::placeholder {
            font-size: 14px;
          }

          .group-package-filter-select.ant-select.ant-select-lg .ant-select-selector {
            height: 40px !important;
            border-radius: 8px !important;
            padding: 0 11px !important;
            font-size: 14px !important;
          }

          .group-package-filter-select.ant-select.ant-select-single.ant-select-lg .ant-select-selector .ant-select-selection-wrap,
          .group-package-filter-select.ant-select.ant-select-single.ant-select-lg .ant-select-selector .ant-select-selection-search,
          .group-package-filter-select.ant-select.ant-select-single.ant-select-lg .ant-select-selector .ant-select-selection-item,
          .group-package-filter-select.ant-select.ant-select-single.ant-select-lg .ant-select-selector .ant-select-selection-placeholder {
            line-height: 38px !important;
            font-size: 14px !important;
          }

          .group-package-filter-select.ant-select.ant-select-single.ant-select-lg .ant-select-selection-search-input {
            height: 38px !important;
            font-size: 14px !important;
          }
        `}
      </style>
      <div style={{ maxWidth: 1360, margin: '0 auto' }}>
        <div
          style={{
            marginBottom: 24,
            padding: 28,
            borderRadius: 24,
            background: 'linear-gradient(135deg, #0f274f 0%, #173d73 55%, #245aa8 100%)',
            boxShadow: '0 20px 48px rgba(15, 39, 79, 0.18)',
            color: '#ffffff'
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', gap: 24, flexWrap: 'wrap' }}>
            <div style={{ maxWidth: 760 }}>
              <div
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 8,
                  padding: '6px 12px',
                  borderRadius: 999,
                  background: 'rgba(255,255,255,0.14)',
                  fontSize: 12,
                  fontWeight: 600,
                  letterSpacing: '0.04em',
                  marginBottom: 14
                }}
              >
                <GiftOutlined />
                GROUP PACKAGE
              </div>
              <div style={{ fontSize: 30, fontWeight: 700, lineHeight: 1.25 }}>
                集团包价设置
              </div>
            </div>

            <Button
              type="primary"
              icon={<PlusOutlined />}
              size="large"
              onClick={handleAddPackage}
              style={{
                borderRadius: 14,
                minWidth: 136,
                height: 48,
                boxShadow: '0 10px 24px rgba(8, 35, 74, 0.28)',
                background: '#ffffff',
                color: '#123a72'
              }}
            >
              新增包价
            </Button>
          </div>
        </div>

        <Card
          variant="borderless"
          style={{
            borderRadius: 20,
            boxShadow: '0 12px 32px rgba(15, 23, 42, 0.08)',
            marginBottom: 20
          }}
          styles={{ body: { padding: 24 } }}
        >
          <Row gutter={[16, 16]}>
            <Col xs={24} md={12} lg={8}>
              <Input
                className="group-package-filter-input"
                size="large"
                allowClear
                placeholder="搜索包价名称或代码"
                value={searchParams.keyword}
                onChange={(e) => setSearchParams((prev) => ({ ...prev, keyword: e.target.value }))}
                style={FILTER_CONTROL_STYLE}
              />
            </Col>
            <Col xs={24} sm={12} md={12} lg={4}>
              <Select
                className="group-package-filter-select"
                size="large"
                allowClear
                placeholder="包价类型"
                value={searchParams.type || undefined}
                onChange={(value) => setSearchParams((prev) => ({ ...prev, type: value || '' }))}
                style={FILTER_CONTROL_STYLE}
              >
                {packageTypes.map((item) => (
                  <Option key={item.value} value={item.value}>{item.label}</Option>
                ))}
              </Select>
            </Col>
            <Col xs={24} sm={12} md={12} lg={4}>
              <Select
                className="group-package-filter-select"
                size="large"
                allowClear
                placeholder="发放频率"
                value={searchParams.frequency || undefined}
                onChange={(value) => setSearchParams((prev) => ({ ...prev, frequency: value || '' }))}
                style={FILTER_CONTROL_STYLE}
              >
                {frequencyOptions.map((item) => (
                  <Option key={item.value} value={item.value}>{item.label}</Option>
                ))}
              </Select>
            </Col>
            <Col xs={24} sm={12} md={12} lg={4}>
              <Select
                className="group-package-filter-select"
                size="large"
                allowClear
                placeholder="计数方式"
                value={searchParams.quantityType || undefined}
                onChange={(value) => setSearchParams((prev) => ({ ...prev, quantityType: value || '' }))}
                style={FILTER_CONTROL_STYLE}
              >
                {quantityTypeOptions.map((item) => (
                  <Option key={item.value} value={item.value}>{item.label}</Option>
                ))}
              </Select>
            </Col>
            <Col xs={24} sm={12} md={12} lg={4}>
              <Select
                className="group-package-filter-select"
                size="large"
                allowClear
                placeholder="状态"
                value={searchParams.status || undefined}
                onChange={(value) => setSearchParams((prev) => ({ ...prev, status: value || '' }))}
                style={FILTER_CONTROL_STYLE}
              >
                {statusOptions.map((item) => (
                  <Option key={item.value} value={item.value}>{item.label}</Option>
                ))}
              </Select>
            </Col>
          </Row>

          <div
            style={{
              marginTop: 18,
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              gap: 12,
              flexWrap: 'wrap'
            }}
          >
            <Text type="secondary">
              共 {packages.length} 条结果
            </Text>
            <Space>
              <Button icon={<ReloadOutlined />} onClick={handleRefresh}>
                刷新
              </Button>
              <Button onClick={handleReset}>
                重置
              </Button>
            </Space>
          </div>
        </Card>

        <Card
          variant="borderless"
          style={{
            borderRadius: 20,
            boxShadow: '0 12px 32px rgba(15, 23, 42, 0.08)'
          }}
          styles={{ body: { padding: 12 } }}
        >
          <Table
            columns={columns}
            dataSource={packages}
            rowKey="id"
            loading={loading}
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
            }}
            scroll={{ x: 1400 }}
          />
        </Card>
      </div>
    </div>
  )
}

export default PackageSetting

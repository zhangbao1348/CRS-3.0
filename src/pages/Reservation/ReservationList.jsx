import { useState, useEffect } from 'react'
import { App as AntApp, Typography, Form, Input, Select, Button, Table, Tag, Badge } from 'antd'
import { SearchOutlined, ReloadOutlined, ExportOutlined, PhoneOutlined, CheckCircleOutlined, DollarOutlined, GlobalOutlined, ShoppingOutlined, HomeOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { reservationApi } from '../../utils/api'
import { getCurrentTenantId } from '../../utils/tenantUtils'
import { useHotelContext } from '../../contexts/HotelContext'
import { FilterPanel, PageScaffold, TablePanel } from '../../components/ui'

const { Text } = Typography
const { Option } = Select

const channelIconMap = {
  CTRIP: <GlobalOutlined />,
  FLIGGY: <ShoppingOutlined />,
  MEITUAN: <HomeOutlined />,
  BOOKING: <GlobalOutlined />,
  QUNAR: <GlobalOutlined />,
  RED_POWER: <HomeOutlined />,
  PMS: <PhoneOutlined />
}

const statusFilterMap = {
  '所有状态': '',
  '已确认': 'confirmed',
  '待确认': 'pending',
  '待支付': 'pending_payment',
  '已取消': 'cancelled',
  '取消失败': 'cancel_failed',
  '已入住': 'checked_in',
  '已离店': 'checked_out',
  'Noshow': 'noshow'
}

const channelFilterMap = {
  '所有渠道': '',
  '携程': 'CTRIP',
  '飞猪': 'FLIGGY',
  '美团': 'MEITUAN',
  'PMS': 'PMS'
}

const ReservationList = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [orderData, setOrderData] = useState([])
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [pagination, setPagination] = useState({ current: 1, pageSize: 20, total: 0 })
  const { message } = AntApp.useApp()
  const navigate = useNavigate()
  const { selectedHotel } = useHotelContext()

  const fetchOrders = async (page = 1, pageSize = 20) => {
    setLoading(true)
    try {
      const tenantId = getCurrentTenantId()
      const formValues = form.getFieldsValue()
      const params = {
        tenantId: tenantId || undefined,
        hotelCode: selectedHotel || undefined,
        page,
        pageSize,
        reservationStatus: statusFilterMap[formValues.status] || undefined,
        channelId: channelFilterMap[formValues.channel] || undefined,
        orderNo: formValues.orderNumber || undefined,
        guestName: formValues.guestName || undefined
      }

      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === '') {
          delete params[key]
        }
      })

      const data = await reservationApi.list(params)
      const items = (data.content || []).map(item => ({
        key: item.id,
        id: item.id,
        channelOrderNumber: item.channelOrderNumber || '-',
        crsOrderNumber: item.crsOrderNumber || '-',
        pmsNumber: item.pmsNumber || '-',
        status: item.status,
        statusColor: item.statusColor,
        channel: item.channel,
        channelCode: item.channelCode,
        channelIcon: channelIconMap[item.channelCode] || <GlobalOutlined />,
        bookingTime: item.bookingTime,
        checkInDate: item.checkInDate,
        checkOutDate: item.checkOutDate,
        nights: item.nights,
        roomCount: item.roomCount,
        totalPrice: item.totalPrice,
        currency: item.currency || 'CNY',
        guestName: item.guestName,
        hotelName: item.hotelName,
        roomTypeName: item.roomTypeName,
        ratePlanName: item.ratePlanName,
        isManual: item.isManual || false,
        reservationStatus: item.reservationStatus
      }))

      setOrderData(items)
      setPagination({
        current: data.currentPage || page,
        pageSize: data.pageSize || pageSize,
        total: data.totalElements || 0
      })
    } catch (error) {
      console.error('获取订单列表失败:', error)
      message.error('获取订单列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = () => {
    fetchOrders(1, pagination.pageSize)
  }

  const handleReset = () => {
    form.resetFields()
    setSelectedRowKeys([])
    fetchOrders(1, pagination.pageSize)
  }

  const handleExport = () => {
    navigate('/reports/data-export')
  }

  const handleViewOrder = (record) => {
    navigate(`/reservation/reservation-detail?id=${record.id}`)
  }

  const handleTableChange = (pag) => {
    fetchOrders(pag.current, pag.pageSize)
  }

  useEffect(() => {
    fetchOrders()
  }, [selectedHotel])

  return (
    <PageScaffold
      className="fade-in"
      eyebrow="RESERVATION OPERATIONS"
      title="订单管理"
      description="集中查询中央预订、渠道与 PMS 订单，核对入住信息和订单状态。"
      actions={(
        <Button icon={<ExportOutlined />} onClick={handleExport}>
          数据导出
        </Button>
      )}
    >
      <FilterPanel>
          <Form
            form={form}
            layout="inline"
            initialValues={{
              orderNumber: '',
              status: '所有状态',
              channel: '所有渠道',
              guestName: ''
            }}
          >
            <Form.Item name="orderNumber" label="订单号">
              <Input placeholder="渠道 / CRS / PMS 订单号" allowClear />
            </Form.Item>
            <Form.Item name="status" label="订单状态">
              <Select placeholder="请选择订单状态">
                <Option value="所有状态">所有状态</Option>
                <Option value="已确认">已确认</Option>
                <Option value="待确认">待确认</Option>
                <Option value="待支付">待支付</Option>
                <Option value="已取消">已取消</Option>
                <Option value="取消失败">取消失败</Option>
                <Option value="已入住">已入住</Option>
                <Option value="已离店">已离店</Option>
                <Option value="Noshow">Noshow</Option>
              </Select>
            </Form.Item>
            <Form.Item name="channel" label="预订渠道">
              <Select placeholder="请选择预订渠道">
                <Option value="所有渠道">所有渠道</Option>
                <Option value="携程">携程</Option>
                <Option value="飞猪">飞猪</Option>
                <Option value="美团">美团</Option>
                <Option value="PMS">PMS</Option>
              </Select>
            </Form.Item>
            <Form.Item name="guestName" label="入住人">
              <Input placeholder="请输入入住人姓名" allowClear />
            </Form.Item>
            <Form.Item className="ui-filter-panel__actions">
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} loading={loading}>
                查询
              </Button>
              <Button className="ui-filter-panel__secondary-action" icon={<ReloadOutlined />} onClick={handleReset}>
                重置
              </Button>
            </Form.Item>
          </Form>
      </FilterPanel>

      <TablePanel
        title="订单结果"
        description={`共 ${pagination.total} 条订单${selectedRowKeys.length ? `，已选择 ${selectedRowKeys.length} 条` : ''}`}
      >
        <Table
          loading={loading}
          rowSelection={{
            selectedRowKeys,
            onChange: setSelectedRowKeys,
            columnTitle: '选择',
          }}
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`
          }}
          onChange={handleTableChange}
          scroll={{ x: 1000 }}
          dataSource={orderData}
          rowKey="key"
          columns={[
            {
              title: '订单号',
              key: 'orderNumber',
              width: 250,
              render: (text, record) => (
                <div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <CheckCircleOutlined style={{ color: '#1890ff', marginRight: 8 }} />
                    <Text strong>{record.channelOrderNumber}</Text>
                  </div>
                  <div style={{ fontSize: 12, color: '#666', marginTop: 4 }}>
                    CRS {record.crsOrderNumber}
                  </div>
                  <div style={{ fontSize: 12, color: '#666', marginTop: 2 }}>
                    PMS {record.pmsNumber}
                  </div>
                  {record.isManual && (
                    <Badge
                      count="人工干预"
                      title="人工干预单"
                      style={{ backgroundColor: '#ff4d4f', marginTop: 4 }}
                    />
                  )}
                </div>
              )
            },
            {
              title: '状态',
              dataIndex: 'status',
              key: 'status',
              width: 100,
              render: (text, record) => (
                <Tag color={record.statusColor} style={{ fontSize: 12, padding: '4px 8px' }}>
                  {text}
                </Tag>
              )
            },
            {
              title: '渠道',
              dataIndex: 'channel',
              key: 'channel',
              width: 100,
              render: (text, record) => (
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <span style={{ marginRight: 8 }}>{record.channelIcon}</span>
                  {text}
                </div>
              )
            },
            {
              title: '预订时间',
              dataIndex: 'bookingTime',
              key: 'bookingTime',
              width: 150
            },
            {
              title: '住宿日期',
              dataIndex: 'checkInDate',
              key: 'checkInDate',
              width: 150,
              render: (text, record) => (
                <Text>
                  {text} ~ {record.checkOutDate}
                </Text>
              )
            },
            {
              title: '入住人',
              dataIndex: 'guestName',
              key: 'guestName',
              width: 100
            },
            {
              title: '总房价',
              dataIndex: 'totalPrice',
              key: 'totalPrice',
              width: 120,
              render: (text) => (
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <DollarOutlined style={{ marginRight: 4, fontSize: 12 }} />
                  CNY {typeof text === 'number' ? text.toFixed(2) : '0.00'}
                </div>
              )
            },
            {
              title: '操作',
              key: 'action',
              width: 80,
              render: (text, record) => (
                <Button size="small" onClick={() => handleViewOrder(record)}>
                  查看
                </Button>
              )
            }
          ]}
        />
      </TablePanel>
    </PageScaffold>
  )
}

export default ReservationList

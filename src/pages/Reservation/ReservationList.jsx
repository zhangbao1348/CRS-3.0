import React, { useState } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, DatePicker, Tag, Tooltip, Modal, Tabs } from 'antd'
import {
  SearchOutlined,
  ExportOutlined,
  EyeOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  FilterOutlined,
  PlusOutlined,
  FileTextOutlined
} from '@ant-design/icons'

const { Option } = Select
const { RangePicker } = DatePicker
const { TabPane } = Tabs

// 模拟订单数据 - 扩展为20条记录
const mockOrders = Array.from({ length: 20 }, (_, index) => {
  // 基础信息
  const id = index + 1
  const orderNo = `11281447904909777_${id}`
  const confirmationNo = `FMNJCC/J0251231_YFNJCC_000113${String(1198 + index).padStart(5, '0')}`
  const internalNo = `GPPCU161813${String(184 + index).padStart(3, '0')}`
  const pmsNo = internalNo
  
  // 随机生成日期和时间
  const baseDate = new Date(2025, 11, 28 + Math.floor(Math.random() * 10))
  const orderTime = baseDate.toISOString().replace('T', ' ').substring(0, 19)
  const checkinDate = new Date(baseDate)
  checkinDate.setDate(checkinDate.getDate() + Math.floor(Math.random() * 3))
  const checkoutDate = new Date(checkinDate)
  checkoutDate.setDate(checkoutDate.getDate() + (Math.floor(Math.random() * 3) + 1))
  
  // 随机生成状态、渠道等
  const statuses = ['已确认', '已入住', '待确认', '已取消']
  const channels = ['携程', '美团', '飞猪', 'Booking.com', 'Agoda']
  const roomTypes = ['标准大床房', '豪华大床房', '探索大床房', '探索双床房', '好眠大床房', '探索家庭房', '探索大床套间']
  const paymentMethods = ['在线支付', '到店支付', '信用卡担保']
  const bookingSources = ['OTA', '国际OTA', '官网']
  const specialRequests = ['无特殊要求', '需要无烟房', '需要婴儿床', '需要连通房', '需要高楼层', '需要安静房间']
  
  const status = statuses[index % 4]
  const channel = channels[Math.floor(Math.random() * channels.length)]
  const roomType = roomTypes[Math.floor(Math.random() * roomTypes.length)]
  const paymentMethod = paymentMethods[Math.floor(Math.random() * paymentMethods.length)]
  const bookingSource = channel.includes('OTA') ? 'OTA' : '国际OTA'
  const specialRequest = specialRequests[Math.floor(Math.random() * specialRequests.length)]
  
  // 随机生成客人信息和价格
  const guestNames = ['张**', '李**', '王**', '刘**', '陈**', '杨**', '赵**', '黄**', '周**', '吴**', 'Smith John', 'Johnson Mary', 'Williams David']
  const guestName = guestNames[Math.floor(Math.random() * guestNames.length)]
  const guestPhone = guestName.includes('**') ? `${135 + Math.floor(Math.random() * 10)}****${Math.floor(Math.random() * 10000)}` : `+${Math.floor(Math.random() * 9)}****${Math.floor(Math.random() * 10000)}`
  const adults = Math.floor(Math.random() * 4) + 1
  const children = Math.floor(Math.random() * 3)
  const totalPrice = parseFloat((Math.random() * 600 + 396).toFixed(2))
  
  return {
    id,
    orderNo,
    status,
    channel,
    orderTime,
    checkinDate: checkinDate.toISOString().split('T')[0],
    checkoutDate: checkoutDate.toISOString().split('T')[0],
    totalPrice,
    roomType,
    guestName,
    guestPhone,
    adults,
    children,
    paymentMethod,
    bookingSource,
    specialRequests: specialRequest,
    confirmationNo,
    internalNo,
    pmsNo
  }
})

// 订单状态配置
const orderStatusConfig = {
  '已确认': { color: 'green', icon: <CheckCircleOutlined />, text: '已确认' },
  '已入住': { color: 'blue', icon: <ClockCircleOutlined />, text: '已入住' },
  '待确认': { color: 'orange', icon: <ClockCircleOutlined />, text: '待确认' },
  '已取消': { color: 'red', icon: <CloseCircleOutlined />, text: '已取消' }
}

const ReservationList = () => {
  // 状态管理
  const [orders, setOrders] = useState(mockOrders)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [selectedOrder, setSelectedOrder] = useState(null)
  const [filterVisible, setFilterVisible] = useState(false)
  const [activeTab, setActiveTab] = useState('today')

  // 筛选条件
  const [filters, setFilters] = useState({
    orderNo: '',
    status: '所有状态',
    channel: '所有渠道',
    bookingTime: null,
    checkinDate: null,
    checkoutDate: null
  })

  // 表格列配置
  const columns = [
    {
      title: '选择',
      dataIndex: 'id',
      key: 'id',
      width: 60,
      render: (id) => <input type="checkbox" />
    },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 180,
      render: (orderNo) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          <Tooltip title="查看订单详情">
            <a href="#" onClick={() => handleViewOrder(orderNo)}>{orderNo}</a>
          </Tooltip>
          <Tag color="blue" size="small">PMS</Tag>
        </div>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => {
        const config = orderStatusConfig[status]
        return (
          <Tag color={config.color} icon={config.icon}>
            {config.text}
          </Tag>
        )
      }
    },
    {
      title: '渠道',
      dataIndex: 'channel',
      key: 'channel',
      width: 100,
      render: (channel) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          <Tag color="green" size="small">{channel}</Tag>
        </div>
      )
    },
    {
      title: '预订时间',
      dataIndex: 'orderTime',
      key: 'orderTime',
      width: 150
    },
    {
      title: '住宿日期',
      dataIndex: ['checkinDate', 'checkoutDate'],
      key: 'stayDate',
      width: 150,
      render: (dates) => {
        const [checkin, checkout] = dates
        return `${checkin} ~ ${checkout}`
      }
    },
    {
      title: '总价(CNY)',
      dataIndex: 'totalPrice',
      key: 'totalPrice',
      width: 120,
      render: (price) => (
        <span style={{ fontWeight: 600, color: '#faad14' }}>
          {price.toFixed(2)}
        </span>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="查看">
            <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewOrder(record.orderNo)} />
          </Tooltip>
        </Space>
      )
    }
  ]

  // 查看订单详情
  const handleViewOrder = (orderNo) => {
    const order = orders.find(o => o.orderNo === orderNo)
    if (order) {
      setSelectedOrder(order)
      setIsModalVisible(true)
    }
  }

  // 关闭订单详情
  const handleCloseModal = () => {
    setIsModalVisible(false)
    setSelectedOrder(null)
  }

  // 搜索订单
  const handleSearch = () => {
    // 这里可以添加搜索逻辑
    console.log('Searching with filters:', filters)
  }

  // 重置筛选条件
  const handleReset = () => {
    setFilters({
      orderNo: '',
      status: '所有状态',
      channel: '所有渠道',
      bookingTime: null,
      checkinDate: null,
      checkoutDate: null
    })
  }

  // 导出订单
  const handleExport = () => {
    console.log('Exporting orders...')
  }

  // 切换标签页
  const handleTabChange = (key) => {
    setActiveTab(key)
  }

  return (
    <div className="fade-in">
      {/* 标题区域 */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h1 className="page-title">
          <FileTextOutlined /> 订单
        </h1>
        <div style={{ display: 'flex', gap: 8 }}>
          <Button type="default" icon={<FilterOutlined />} onClick={() => setFilterVisible(!filterVisible)}>
            筛选条件
          </Button>
          <Button type="primary" icon={<ExportOutlined />} onClick={handleExport}>
            导出
          </Button>
        </div>
      </div>

      {/* 标签页 */}
      <Card style={{ marginBottom: 16, borderRadius: 8 }}>
        <Tabs activeKey={activeTab} onChange={handleTabChange}>
          <TabPane tab="今日" key="today">
            {/* 今日订单内容 */}
          </TabPane>
          <TabPane tab="明日" key="tomorrow">
            {/* 明日订单内容 */}
          </TabPane>
          <TabPane tab="近期" key="recent">
            {/* 近期订单内容 */}
          </TabPane>
          <TabPane tab="所有" key="all">
            {/* 所有订单内容 */}
          </TabPane>
        </Tabs>
      </Card>

      {/* 筛选条件区域 */}
      {filterVisible && (
        <Card title="筛选条件" style={{ marginBottom: 16, borderRadius: 8 }}>
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Input 
                placeholder="请输入订单号" 
                value={filters.orderNo}
                onChange={(e) => setFilters({...filters, orderNo: e.target.value})}
              />
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Select 
                placeholder="所有状态" 
                value={filters.status}
                onChange={(value) => setFilters({...filters, status: value})}
                style={{ width: '100%' }}
              >
                <Option value="所有状态">所有状态</Option>
                <Option value="已确认">已确认</Option>
                <Option value="已入住">已入住</Option>
                <Option value="待确认">待确认</Option>
                <Option value="已取消">已取消</Option>
              </Select>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Select 
                placeholder="所有渠道" 
                value={filters.channel}
                onChange={(value) => setFilters({...filters, channel: value})}
                style={{ width: '100%' }}
              >
                <Option value="所有渠道">所有渠道</Option>
                <Option value="携程">携程</Option>
                <Option value="美团">美团</Option>
                <Option value="飞猪">飞猪</Option>
                <Option value="Booking.com">Booking.com</Option>
                <Option value="Agoda">Agoda</Option>
              </Select>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <RangePicker 
                placeholder={['预订开始时间', '预订结束时间']} 
                value={filters.bookingTime}
                onChange={(value) => setFilters({...filters, bookingTime: value})}
                style={{ width: '100%' }}
              />
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <DatePicker 
                placeholder="入住日期" 
                value={filters.checkinDate}
                onChange={(value) => setFilters({...filters, checkinDate: value})}
                style={{ width: '100%' }}
              />
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <DatePicker 
                placeholder="离店日期" 
                value={filters.checkoutDate}
                onChange={(value) => setFilters({...filters, checkoutDate: value})}
                style={{ width: '100%' }}
              />
            </Col>
            <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
              <Space>
                <Button type="default" onClick={handleReset}>重置</Button>
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>搜索</Button>
              </Space>
            </Col>
          </Row>
        </Card>
      )}

      {/* 订单表格 */}
      <Card style={{ borderRadius: 8 }}>
        <Table
          columns={columns}
          dataSource={orders}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
          }}
          scroll={{ x: 1200 }}
        />
      </Card>

      {/* 订单详情弹窗 */}
      <Modal
        title="订单详情"
        open={isModalVisible}
        onCancel={handleCloseModal}
        footer={null}
        width={800}
        destroyOnClose
      >
        {selectedOrder && (
          <div>
            <Tabs defaultActiveKey="basic">
              <TabPane tab="基本信息" key="basic">
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 16, margin: '16px 0' }}>
                  <div>
                    <div style={{ fontWeight: 600, marginBottom: 4 }}>订单信息</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>订单号: {selectedOrder.orderNo}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>确认号: {selectedOrder.confirmationNo}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>内部号: {selectedOrder.internalNo}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>PMS单号: {selectedOrder.pmsNo}</div>
                  </div>
                  <div>
                    <div style={{ fontWeight: 600, marginBottom: 4 }}>预订信息</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>预订时间: {selectedOrder.orderTime}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>预订渠道: {selectedOrder.channel}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>预订来源: {selectedOrder.bookingSource}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>支付方式: {selectedOrder.paymentMethod}</div>
                  </div>
                  <div>
                    <div style={{ fontWeight: 600, marginBottom: 4 }}>客人信息</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>客人姓名: {selectedOrder.guestName}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>联系电话: {selectedOrder.guestPhone}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>成人: {selectedOrder.adults}人</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>儿童: {selectedOrder.children}人</div>
                  </div>
                  <div>
                    <div style={{ fontWeight: 600, marginBottom: 4 }}>住宿信息</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>房型: {selectedOrder.roomType}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>入住日期: {selectedOrder.checkinDate}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>离店日期: {selectedOrder.checkoutDate}</div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>总价: {selectedOrder.totalPrice.toFixed(2)} CNY</div>
                  </div>
                </div>
                <div style={{ marginTop: 16 }}>
                  <div style={{ fontWeight: 600, marginBottom: 4 }}>特殊要求</div>
                  <div style={{ fontSize: 12, color: '#8c8c8c' }}>{selectedOrder.specialRequests}</div>
                </div>
              </TabPane>
              <TabPane tab="价格明细" key="price">
                <div style={{ padding: '16px 0' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid #f0f0f0' }}>
                    <span>房费</span>
                    <span>{selectedOrder.totalPrice.toFixed(2)} CNY</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid #f0f0f0' }}>
                    <span>服务费</span>
                    <span>0.00 CNY</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid #f0f0f0' }}>
                    <span>税费</span>
                    <span>0.00 CNY</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontWeight: 600 }}>
                    <span>总计</span>
                    <span>{selectedOrder.totalPrice.toFixed(2)} CNY</span>
                  </div>
                </div>
              </TabPane>
              <TabPane tab="操作日志" key="log">
                <div style={{ padding: '16px 0' }}>
                  <div style={{ fontSize: 12, color: '#8c8c8c', marginBottom: 8 }}>
                    2025-12-31 00:01:11 - 订单创建
                  </div>
                  <div style={{ fontSize: 12, color: '#8c8c8c', marginBottom: 8 }}>
                    2025-12-31 00:01:15 - 订单已确认
                  </div>
                </div>
              </TabPane>
            </Tabs>
          </div>
        )}
      </Modal>
    </div>
  )
}



export default ReservationList
import React, { useState, useEffect } from 'react'
import { Card, Typography, Form, Input, Select, Button, Table, Space, Tag, Checkbox, message, Badge } from 'antd'
import { SearchOutlined, ReloadOutlined, ExportOutlined, DownOutlined, PhoneOutlined, CheckCircleOutlined, CloseCircleOutlined, DollarOutlined, GlobalOutlined, ShoppingOutlined, HomeOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Title, Text } = Typography
const { Option } = Select

const ReservationList = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [orderData, setOrderData] = useState([])
  const navigate = useNavigate()

  // 模拟订单数据
  const mockOrderData = [
    {
      key: '1',
      channelOrderNumber: '112814479949077',
      crsOrderNumber: 'CRS123456789',
      pmsNumber: 'YFNJCC/0251231_YFNJCC_0001131198',
      status: '已确认',
      statusColor: 'green',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-31 00:01:11',
      checkInDate: '2025-12-30',
      checkOutDate: '2025-12-31',
      totalPrice: 396.00,
      isManual: false,
      guestName: '张三'
    },
    {
      key: '2',
      channelOrderNumber: '112814479947981',
      crsOrderNumber: 'CRS123456790',
      pmsNumber: 'YFNJCC/0251230_YFNJCC_2359537115',
      status: '已确认',
      statusColor: 'green',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-30 23:59:52',
      checkInDate: '2025-12-30',
      checkOutDate: '2025-12-31',
      totalPrice: 422.00,
      isManual: false,
      guestName: '李四'
    },
    {
      key: '3',
      channelOrderNumber: '5008013766286861837',
      crsOrderNumber: 'CRS123456791',
      pmsNumber: 'YFNJCC/0251230_YFNJCC_2358122150',
      status: '已确认',
      statusColor: 'green',
      channel: '美团',
      channelIcon: <HomeOutlined />,
      bookingTime: '2025-12-30 23:58:10',
      checkInDate: '2025-12-31',
      checkOutDate: '2026-01-01',
      totalPrice: 607.00,
      isManual: false,
      guestName: '王五'
    },
    {
      key: '4',
      channelOrderNumber: '4501815373003003933',
      crsOrderNumber: 'CRS123456792',
      pmsNumber: 'YFNJCC/0251230_YFNJCC_1554576767',
      status: '取消失败',
      statusColor: 'red',
      channel: '飞猪',
      channelIcon: <ShoppingOutlined />,
      bookingTime: '2025-12-30 15:54:55',
      checkInDate: '2025-12-30',
      checkOutDate: '2025-12-31',
      totalPrice: 578.00,
      isManual: true,
      guestName: '赵六'
    },
    {
      key: '5',
      channelOrderNumber: '1128144786236519',
      crsOrderNumber: 'CRS123456793',
      pmsNumber: 'YFNJCC/0251230_YFNJCC_0949503278',
      status: '已取消',
      statusColor: 'green',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-30 09:49:48',
      checkInDate: '2026-01-02',
      checkOutDate: '2026-01-03',
      totalPrice: 668.00,
      isManual: false,
      guestName: '孙七'
    },
    {
      key: '6',
      channelOrderNumber: '1128144776236520',
      crsOrderNumber: 'CRS123456794',
      pmsNumber: 'YFNJCC/0251229_YFNJCC_1549503279',
      status: '已确认',
      statusColor: 'green',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-29 15:49:49',
      checkInDate: '2026-01-03',
      checkOutDate: '2026-01-04',
      totalPrice: 598.00,
      isManual: false,
      guestName: '周八'
    },
    {
      key: '7',
      channelOrderNumber: '5008013766286861838',
      crsOrderNumber: 'CRS123456795',
      pmsNumber: 'YFNJCC/0251229_YFNJCC_1458122151',
      status: '已确认',
      statusColor: 'green',
      channel: '美团',
      channelIcon: <HomeOutlined />,
      bookingTime: '2025-12-29 14:58:11',
      checkInDate: '2026-01-04',
      checkOutDate: '2026-01-05',
      totalPrice: 628.00,
      isManual: false,
      guestName: '吴九'
    },
    {
      key: '8',
      channelOrderNumber: '4501815373003003934',
      crsOrderNumber: 'CRS123456796',
      pmsNumber: 'YFNJCC/0251229_YFNJCC_1354576768',
      status: '已入住',
      statusColor: 'blue',
      channel: '飞猪',
      channelIcon: <ShoppingOutlined />,
      bookingTime: '2025-12-29 13:54:56',
      checkInDate: '2025-12-31',
      checkOutDate: '2026-01-02',
      totalPrice: 798.00,
      isManual: false,
      guestName: '郑十'
    },
    {
      key: '9',
      channelOrderNumber: '1128144766236521',
      crsOrderNumber: 'CRS123456797',
      pmsNumber: 'YFNJCC/0251228_YFNJCC_1249503280',
      status: '已离店',
      statusColor: 'gray',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-28 12:49:50',
      checkInDate: '2025-12-29',
      checkOutDate: '2025-12-30',
      totalPrice: 498.00,
      isManual: false,
      guestName: '王十一'
    },
    {
      key: '10',
      channelOrderNumber: '5008013766286861839',
      crsOrderNumber: 'CRS123456798',
      pmsNumber: 'YFNJCC/0251228_YFNJCC_1158122152',
      status: '已确认',
      statusColor: 'green',
      channel: '美团',
      channelIcon: <HomeOutlined />,
      bookingTime: '2025-12-28 11:58:12',
      checkInDate: '2026-01-05',
      checkOutDate: '2026-01-06',
      totalPrice: 568.00,
      isManual: false,
      guestName: '李十二'
    },
    {
      key: '11',
      channelOrderNumber: '4501815373003003935',
      crsOrderNumber: 'CRS123456799',
      pmsNumber: 'YFNJCC/0251228_YFNJCC_1054576769',
      status: '已确认',
      statusColor: 'green',
      channel: '飞猪',
      channelIcon: <ShoppingOutlined />,
      bookingTime: '2025-12-28 10:54:57',
      checkInDate: '2026-01-06',
      checkOutDate: '2026-01-07',
      totalPrice: 638.00,
      isManual: false,
      guestName: '张十三'
    },
    {
      key: '12',
      channelOrderNumber: '1128144756236522',
      crsOrderNumber: 'CRS123456800',
      pmsNumber: 'YFNJCC/0251227_YFNJCC_0949503281',
      status: '已确认',
      statusColor: 'green',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-27 09:49:51',
      checkInDate: '2026-01-07',
      checkOutDate: '2026-01-08',
      totalPrice: 588.00,
      isManual: false,
      guestName: '刘十四'
    },
    {
      key: '13',
      channelOrderNumber: '5008013766286861840',
      crsOrderNumber: 'CRS123456801',
      pmsNumber: 'YFNJCC/0251227_YFNJCC_0858122153',
      status: '已确认',
      statusColor: 'green',
      channel: '美团',
      channelIcon: <HomeOutlined />,
      bookingTime: '2025-12-27 08:58:13',
      checkInDate: '2026-01-08',
      checkOutDate: '2026-01-09',
      totalPrice: 618.00,
      isManual: false,
      guestName: '陈十五'
    },
    {
      key: '14',
      channelOrderNumber: '4501815373003003936',
      crsOrderNumber: 'CRS123456802',
      pmsNumber: 'YFNJCC/0251227_YFNJCC_0754576770',
      status: '已确认',
      statusColor: 'green',
      channel: '飞猪',
      channelIcon: <ShoppingOutlined />,
      bookingTime: '2025-12-27 07:54:58',
      checkInDate: '2026-01-09',
      checkOutDate: '2026-01-10',
      totalPrice: 598.00,
      isManual: false,
      guestName: '杨十六'
    },
    {
      key: '15',
      channelOrderNumber: '1128144746236523',
      crsOrderNumber: 'CRS123456803',
      pmsNumber: 'YFNJCC/0251226_YFNJCC_0649503282',
      status: '已确认',
      statusColor: 'green',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-26 06:49:52',
      checkInDate: '2026-01-10',
      checkOutDate: '2026-01-11',
      totalPrice: 628.00,
      isManual: false,
      guestName: '黄十七'
    },
    {
      key: '16',
      channelOrderNumber: '5008013766286861841',
      crsOrderNumber: 'CRS123456804',
      pmsNumber: 'YFNJCC/0251226_YFNJCC_0558122154',
      status: '已确认',
      statusColor: 'green',
      channel: '美团',
      channelIcon: <HomeOutlined />,
      bookingTime: '2025-12-26 05:58:14',
      checkInDate: '2026-01-11',
      checkOutDate: '2026-01-12',
      totalPrice: 578.00,
      isManual: false,
      guestName: '周十八'
    },
    {
      key: '17',
      channelOrderNumber: '4501815373003003937',
      crsOrderNumber: 'CRS123456805',
      pmsNumber: 'YFNJCC/0251226_YFNJCC_0454576771',
      status: '已确认',
      statusColor: 'green',
      channel: '飞猪',
      channelIcon: <ShoppingOutlined />,
      bookingTime: '2025-12-26 04:54:59',
      checkInDate: '2026-01-12',
      checkOutDate: '2026-01-13',
      totalPrice: 608.00,
      isManual: false,
      guestName: '吴十九'
    },
    {
      key: '18',
      channelOrderNumber: '1128144736236524',
      crsOrderNumber: 'CRS123456806',
      pmsNumber: 'YFNJCC/0251225_YFNJCC_0349503283',
      status: '已确认',
      statusColor: 'green',
      channel: '携程',
      channelIcon: <GlobalOutlined />,
      bookingTime: '2025-12-25 03:49:53',
      checkInDate: '2026-01-13',
      checkOutDate: '2026-01-14',
      totalPrice: 588.00,
      isManual: false,
      guestName: '郑二十'
    },
    {
      key: '19',
      channelOrderNumber: '5008013766286861842',
      crsOrderNumber: 'CRS123456807',
      pmsNumber: 'YFNJCC/0251225_YFNJCC_0258122155',
      status: '已确认',
      statusColor: 'green',
      channel: '美团',
      channelIcon: <HomeOutlined />,
      bookingTime: '2025-12-25 02:58:15',
      checkInDate: '2026-01-14',
      checkOutDate: '2026-01-15',
      totalPrice: 638.00,
      isManual: false,
      guestName: '王二十一'
    },
    {
      key: '20',
      channelOrderNumber: '4501815373003003938',
      crsOrderNumber: 'CRS123456808',
      pmsNumber: 'YFNJCC/0251225_YFNJCC_0154576772',
      status: '已确认',
      statusColor: 'green',
      channel: '飞猪',
      channelIcon: <ShoppingOutlined />,
      bookingTime: '2025-12-25 01:54:00',
      checkInDate: '2026-01-15',
      checkOutDate: '2026-01-16',
      totalPrice: 598.00,
      isManual: false,
      guestName: '李二十二'
    }
  ]

  // 处理搜索
  const handleSearch = () => {
    setLoading(true)
    // 模拟API请求
    setTimeout(() => {
      setOrderData(mockOrderData)
      setLoading(false)
      message.success('查询成功')
    }, 1000)
  }

  // 处理重置
  const handleReset = () => {
    form.resetFields()
  }

  // 处理导出
  const handleExport = () => {
    message.info('导出功能开发中')
  }

  // 处理查看订单
  const handleViewOrder = (record) => {
    navigate('/reservation/reservation-detail')
  }

  // 初始化加载数据
  useEffect(() => {
    handleSearch()
  }, [])

  return (
    <div className="fade-in">
      <h1 className="page-title">
        订单
      </h1>
      
      <Card>
        {/* 搜索区域 */}
        <div style={{ marginBottom: 24 }}>
          <Form
            form={form}
            layout="inline"
            initialValues={{
              orderNumber: '',
              status: '所有状态',
              channel: '携程',
              guestName: ''
            }}
          >
            <Form.Item name="orderNumber">
              <Input placeholder="请输入订单号" style={{ width: 180 }} />
            </Form.Item>
            <Form.Item name="status">
              <Select placeholder="所有状态" style={{ width: 120 }}>
                <Option value="所有状态">所有状态</Option>
                <Option value="已确认">已确认</Option>
                <Option value="已取消">已取消</Option>
                <Option value="取消失败">取消失败</Option>
                <Option value="已入住">已入住</Option>
                <Option value="已离店">已离店</Option>
              </Select>
            </Form.Item>
            <Form.Item name="channel">
              <Select placeholder="所有渠道" style={{ width: 120 }}>
                <Option value="所有渠道">所有渠道</Option>
                <Option value="携程">携程</Option>
                <Option value="飞猪">飞猪</Option>
                <Option value="美团">美团</Option>
                <Option value="PMS">PMS</Option>
              </Select>
            </Form.Item>
            <Form.Item name="guestName">
              <Input placeholder="请输入入住人" style={{ width: 150 }} />
            </Form.Item>
            <Form.Item>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} loading={loading}>
                搜索
              </Button>
            </Form.Item>
            <Form.Item>
              <Button icon={<ReloadOutlined />} onClick={handleReset}>
                重置
              </Button>
            </Form.Item>

            <Form.Item>
              <Button icon={<ExportOutlined />} onClick={handleExport} style={{ float: 'right' }}>
                导出
              </Button>
            </Form.Item>
          </Form>
          

        </div>
        
        {/* 订单列表 */}
        <Table
          loading={loading}
          pagination={false}
          scroll={{ x: 1000 }}
          dataSource={orderData}
          rowKey="key"
          columns={[
            {
              title: (
                <Checkbox>
                  <span style={{ marginLeft: 8 }}>全选</span>
                </Checkbox>
              ),
              dataIndex: 'selected',
              key: 'selected',
              width: 40,
              render: () => <Checkbox />
            },
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
                  CNY {text.toFixed(2)}
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
      </Card>
    </div>
  )
}

export default ReservationList
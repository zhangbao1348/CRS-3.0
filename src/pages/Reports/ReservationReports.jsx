import React, { useState, useEffect } from 'react'
import { Card, Typography, Divider, Form, Input, Select, DatePicker, Button, Table, Space, Row, Col, message, Checkbox, Radio } from 'antd'
import { SearchOutlined, ExportOutlined } from '@ant-design/icons'

const { Title, Text } = Typography
const { Option } = Select
const { RangePicker } = DatePicker

const ReservationReports = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [reportData, setReportData] = useState([])
  const [totalData, setTotalData] = useState(null)

  // 模拟数据 - 丰富数据内容
  const mockReportData = [
    {
      key: '1',
      channel: '携程',
      hotels: [
        {
          key: '1-1',
          hotel: '上海宝丽嘉',
          currentPeriod: {
            orderCount: 15,
            orderCountChange: '↑50%',
            orderAmount: 10000,
            orderAmountChange: '↑20%',
            orderPoints: 20000,
            orderPointsChange: '↑20%',
            roomNights: 50,
            roomNightsChange: '↑20%',
            avgRate: 1278,
            avgRateChange: '↑20%'
          },
          previousPeriod: {
            orderCount: 10,
            orderAmount: 7500,
            orderPoints: 16000,
            roomNights: 40,
            avgRate: 1400
          }
        },
        {
          key: '1-2',
          hotel: '杭州钓美',
          currentPeriod: {
            orderCount: 8,
            orderCountChange: '↑33%',
            orderAmount: 5600,
            orderAmountChange: '↑15%',
            orderPoints: 12000,
            orderPointsChange: '↑18%',
            roomNights: 25,
            roomNightsChange: '↑25%',
            avgRate: 980,
            avgRateChange: '↑10%'
          },
          previousPeriod: {
            orderCount: 6,
            orderAmount: 4800,
            orderPoints: 10200,
            roomNights: 20,
            avgRate: 890
          }
        }
      ]
    },
    {
      key: '2',
      channel: '飞猪',
      hotels: [
        {
          key: '2-1',
          hotel: '上海宝丽嘉',
          currentPeriod: {
            orderCount: 6,
            orderCountChange: '↑20%',
            orderAmount: 4200,
            orderAmountChange: '↑12%',
            orderPoints: 8500,
            orderPointsChange: '↑15%',
            roomNights: 18,
            roomNightsChange: '↑10%',
            avgRate: 1150,
            avgRateChange: '↑8%'
          },
          previousPeriod: {
            orderCount: 5,
            orderAmount: 3750,
            orderPoints: 7400,
            roomNights: 16,
            avgRate: 1060
          }
        },
        {
          key: '2-2',
          hotel: '杭州钓美',
          currentPeriod: {
            orderCount: 12,
            orderCountChange: '↑40%',
            orderAmount: 7800,
            orderAmountChange: '↑25%',
            orderPoints: 15600,
            orderPointsChange: '↑22%',
            roomNights: 35,
            roomNightsChange: '↑30%',
            avgRate: 890,
            avgRateChange: '↑12%'
          },
          previousPeriod: {
            orderCount: 8,
            orderAmount: 6240,
            orderPoints: 12800,
            roomNights: 27,
            avgRate: 795
          }
        }
      ]
    }
  ]

  // 模拟总计数据 - 根据丰富后的数据计算
  const mockTotalData = {
    currentPeriod: {
      orderCount: 41, // 15 + 8 + 6 + 12
      orderCountChange: '↑36%',
      orderAmount: 27600, // 10000 + 5600 + 4200 + 7800
      orderAmountChange: '↑19%',
      orderPoints: 56100, // 20000 + 12000 + 8500 + 15600
      orderPointsChange: '↑19%',
      roomNights: 128, // 50 + 25 + 18 + 35
      roomNightsChange: '↑22%',
      avgRate: 1056, // 27600 / 128
      avgRateChange: '↑12%'
    },
    previousPeriod: {
      orderCount: 29, // 10 + 6 + 5 + 8
      orderAmount: 22290, // 7500 + 4800 + 3750 + 6240
      orderPoints: 46400, // 16000 + 10200 + 7400 + 12800
      roomNights: 103, // 40 + 20 + 16 + 27
      avgRate: 1065 // 22290 / 103
    }
  }

  // 处理搜索
  const handleSearch = () => {
    setLoading(true)
    // 模拟API请求
    setTimeout(() => {
      setReportData(mockReportData)
      setTotalData(mockTotalData)
      setLoading(false)
      message.success('查询成功')
    }, 1000)
  }

  // 处理导出
  const handleExport = () => {
    message.info('导出报表功能开发中')
  }

  // 处理查看订单明细
  const handleViewOrderDetails = (record) => {
    message.info('查看订单明细功能开发中')
  }

  // 初始化加载数据
  useEffect(() => {
    handleSearch()
  }, [])

  return (
    <div className="fade-in">
      <h1 className="page-title">
        报表
      </h1>
      
      <Card>
        {/* 筛选区域 */}
        <div style={{ marginBottom: 24, border: '1px solid #e8e8e8', padding: 16, borderRadius: 4, backgroundColor: '#fafafa' }}>
          <Form
            form={form}
            layout="inline"
            initialValues={{
              hotel: '上海宝丽嘉',
              bookingDate: [],
              quickSearch: '今天',
              dataComparison: false,
              orderDate: [],
              quickSearchComparison: '同比',
              orderStatus: '',
              marketCode: '',
              channelCode: '',
              ratePlan: '',
              groupBy1: 'channel',
              groupBy2: 'hotel',
              paymentMethod: '',
              memberBooking: false,
              canEarnPoints: false,
              onlineBooking: false
            }}
          >
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col flex="auto">
                <Form.Item label="酒店" name="hotel">
                  <Select placeholder="请选择酒店" style={{ width: 140 }}>
                    <Option value="">全部酒店</Option>
                    <Option value="上海宝丽嘉">上海宝丽嘉</Option>
                    <Option value="杭州钓美">杭州钓美</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="预订日期" name="bookingDate">
                  <RangePicker style={{ width: 220 }} />
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="快速选择" name="quickSearch">
                  <Select placeholder="快速选择" style={{ width: 120 }}>
                    <Option value="今天">今天</Option>
                    <Option value="本周">本周</Option>
                    <Option value="上周">上周</Option>
                    <Option value="本月">本月</Option>
                    <Option value="上月">上月</Option>
                    <Option value="今年">今年</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="数据对比" name="dataComparison">
                  <Checkbox>订单日期</Checkbox>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="" name="orderDate">
                  <RangePicker style={{ width: 220 }} />
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="快速选择" name="quickSearchComparison">
                  <Select placeholder="快速选择" style={{ width: 80 }}>
                    <Option value="同比">同比</Option>
                    <Option value="环比">环比</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col flex="auto">
                <Form.Item label="订单状态" name="orderStatus">
                  <Select placeholder="请选择订单状态" style={{ width: 140 }}>
                    <Option value="">全部订单状态</Option>
                    <Option value="confirmed">已确认</Option>
                    <Option value="canceled">已取消</Option>
                    <Option value="checkIn">已入住</Option>
                    <Option value="checkOut">已离店</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="市场代码" name="marketCode">
                  <Select placeholder="请选择市场代码" style={{ width: 140 }}>
                    <Option value="">请选择市场代码</Option>
                    <Option value="DOM">国内</Option>
                    <Option value="INT">国际</Option>
                    <Option value="CORP">企业</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="渠道代码" name="channelCode">
                  <Select placeholder="请选择渠道代码" style={{ width: 140 }}>
                    <Option value="">请选择渠道代码</Option>
                    <Option value="CTRIP">携程</Option>
                    <Option value="FLIGGY">飞猪</Option>
                    <Option value="BOOKING">Booking</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="房价大类" name="ratePlan">
                  <Select placeholder="请选择房价大类" style={{ width: 140 }}>
                    <Option value="">请选择房价大类</Option>
                    <Option value="ROOM">房费</Option>
                    <Option value="PACKAGE">包价</Option>
                    <Option value="ADDON">附加服务</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            
            <Row gutter={16} style={{ marginBottom: 8 }}>
              <Col flex="auto">
                <Form.Item label="分组依据1" name="groupBy1">
                  <Select placeholder="请选择分组依据" style={{ width: 140 }}>
                    <Option value="channel">渠道</Option>
                    <Option value="hotel">酒店</Option>
                    <Option value="market">市场</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="分组依据2" name="groupBy2">
                  <Select placeholder="请选择分组依据" style={{ width: 140 }}>
                    <Option value="hotel">酒店</Option>
                    <Option value="roomType">房型</Option>
                    <Option value="ratePlan">价格计划</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="订单支付方式" name="paymentMethod">
                  <Radio.Group>
                    <Radio value="">全部</Radio>
                    <Radio value="points">积分</Radio>
                    <Radio value="nonPoints">非积分</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="其他信息" name="memberBooking">
                  <Checkbox>会员预订</Checkbox>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="" name="canEarnPoints">
                  <Checkbox>可累积积分</Checkbox>
                </Form.Item>
              </Col>
              <Col flex="auto">
                <Form.Item label="线上订单" name="onlineBooking">
                  <Checkbox>是</Checkbox>
                </Form.Item>
              </Col>
              <Col flex="auto" style={{ textAlign: 'right' }}>
                <Space>
                  <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} loading={loading}>
                    执行
                  </Button>
                  <Button icon={<ExportOutlined />} onClick={handleExport}>
                    导出
                  </Button>
                </Space>
              </Col>
            </Row>
          </Form>
        </div>
        
        <Divider />
        
        {/* 报表区域 */}
        <div style={{ marginBottom: 24, border: '1px solid #e8e8e8', padding: 16, borderRadius: 4 }}>
          {/* 表头标题 */}
          <div style={{ marginBottom: 10, display: 'flex', justifyContent: 'center' }}>
            <div style={{ textAlign: 'center', width: 80 + 100 }}></div>
            <div style={{ textAlign: 'center', width: (80 + 100 + 120 + 80 + 100) }}>2022-07-01 - 2022-07-07</div>
            <div style={{ textAlign: 'center', width: (80 + 100 + 120 + 80 + 100) }}>2021-07-01 - 2021-07-07</div>
            <div style={{ textAlign: 'center', width: 100 }}></div>
          </div>
          
          <Table
            loading={loading}
            pagination={false}
            scroll={{ x: 1200 }}
            bordered
            dataSource={reportData.flatMap(channel => 
              channel.hotels.map((hotel, index) => ({
                ...hotel,
                channel: channel.channel,
                isFirst: index === 0,
                channelKey: channel.key,
                hotelCount: channel.hotels.length
              }))
            )}
            rowKey="key"
            columns={[
              {
                title: '分组',
                dataIndex: 'channel',
                key: 'channel',
                width: 80,
                textAlign: 'center',
                render: (text, record) => {
                  if (record.isFirst) {
                    const channelData = reportData.find(c => c.key === record.channelKey);
                    return {
                      children: text,
                      props: {
                        rowSpan: channelData.hotels.length
                      }
                    };
                  }
                  return {
                    children: '',
                    props: {
                      rowSpan: 0
                    }
                  };
                }
              },
              {
                title: '酒店',
                dataIndex: 'hotel',
                key: 'hotel',
                width: 100,
                textAlign: 'center'
              },
              {
                title: '订单数(单数)',
                key: 'orderCount',
                width: 80,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.orderCount} {record.currentPeriod.orderCountChange && <span style={{ color: 'green' }}>{record.currentPeriod.orderCountChange}</span>}
                  </span>
                )
              },
              {
                title: '订单总金额',
                key: 'orderAmount',
                width: 100,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.orderAmount > 0 ? `CNY ${record.currentPeriod.orderAmount}` : ''} {record.currentPeriod.orderAmountChange && <span style={{ color: 'green' }}>{record.currentPeriod.orderAmountChange}</span>}
                  </span>
                )
              },
              {
                title: '订单支付总积分数',
                key: 'orderPoints',
                width: 120,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.orderPoints > 0 ? record.currentPeriod.orderPoints : ''} {record.currentPeriod.orderPointsChange && <span style={{ color: 'green' }}>{record.currentPeriod.orderPointsChange}</span>}
                  </span>
                )
              },
              {
                title: '间夜数',
                key: 'roomNights',
                width: 80,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.roomNights > 0 ? record.currentPeriod.roomNights : ''} {record.currentPeriod.roomNightsChange && <span style={{ color: 'green' }}>{record.currentPeriod.roomNightsChange}</span>}
                  </span>
                )
              },
              {
                title: '间夜平均价',
                key: 'avgRate',
                width: 100,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.avgRate > 0 ? `CNY ${record.currentPeriod.avgRate}` : ''} {record.currentPeriod.avgRateChange && <span style={{ color: 'green' }}>{record.currentPeriod.avgRateChange}</span>}
                  </span>
                )
              },
              {
                title: '订单数',
                key: 'prevOrderCount',
                width: 80,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.orderCount
              },
              {
                title: '订单总金额',
                key: 'prevOrderAmount',
                width: 100,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.orderAmount > 0 ? `CNY ${record.previousPeriod.orderAmount}` : ''
              },
              {
                title: '订单支付总积分数',
                key: 'prevOrderPoints',
                width: 120,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.orderPoints
              },
              {
                title: '间夜数',
                key: 'prevRoomNights',
                width: 80,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.roomNights
              },
              {
                title: '间夜平均价',
                key: 'prevAvgRate',
                width: 100,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.avgRate > 0 ? record.previousPeriod.avgRate : ''
              },
              {
                title: '操作',
                key: 'action',
                width: 100,
                textAlign: 'center',
                render: (text, record) => (
                  <Button size="small" onClick={() => handleViewOrderDetails(record)}>
                    查看订单明细
                  </Button>
                )
              }
            ]}
          />
        </div>
        
        {/* 总计区域 */}
        <div style={{ border: '1px solid #e8e8e8', padding: 16, borderRadius: 4 }}>
          <h3 style={{ marginBottom: 16 }}>总计</h3>
          
          {/* 总计表头标题 */}
          <div style={{ marginBottom: 10, display: 'flex', justifyContent: 'center' }}>
            <div style={{ textAlign: 'center', width: (80 + 100 + 120 + 80 + 100) }}>2022-07-01 - 2022-07-07</div>
            <div style={{ textAlign: 'center', width: (80 + 100 + 120 + 80 + 100) }}>2021-07-01 - 2021-07-07</div>
            <div style={{ textAlign: 'center', width: 100 }}></div>
          </div>
          
          <Table
            pagination={false}
            scroll={{ x: 1200 }}
            bordered
            dataSource={totalData ? [totalData] : []}
            columns={[
              {
                title: '订单数',
                key: 'orderCount',
                width: 80,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.orderCount} <span style={{ color: 'green' }}>{record.currentPeriod.orderCountChange}</span>
                  </span>
                )
              },
              {
                title: '订单总金额',
                key: 'orderAmount',
                width: 100,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    CNY {record.currentPeriod.orderAmount} <span style={{ color: 'green' }}>{record.currentPeriod.orderAmountChange}</span>
                  </span>
                )
              },
              {
                title: '订单支付总积分数',
                key: 'orderPoints',
                width: 120,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.orderPoints} <span style={{ color: 'green' }}>{record.currentPeriod.orderPointsChange}</span>
                  </span>
                )
              },
              {
                title: '间夜数',
                key: 'roomNights',
                width: 80,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    {record.currentPeriod.roomNights} <span style={{ color: 'green' }}>{record.currentPeriod.roomNightsChange}</span>
                  </span>
                )
              },
              {
                title: '间夜平均价',
                key: 'avgRate',
                width: 100,
                textAlign: 'center',
                render: (text, record) => (
                  <span>
                    CNY {record.currentPeriod.avgRate} <span style={{ color: 'green' }}>{record.currentPeriod.avgRateChange}</span>
                  </span>
                )
              },
              {
                title: '订单数',
                key: 'prevOrderCount',
                width: 80,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.orderCount
              },
              {
                title: '订单总金额',
                key: 'prevOrderAmount',
                width: 100,
                textAlign: 'center',
                render: (text, record) => `CNY ${record.previousPeriod.orderAmount}`
              },
              {
                title: '订单支付总积分数',
                key: 'prevOrderPoints',
                width: 120,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.orderPoints
              },
              {
                title: '间夜数',
                key: 'prevRoomNights',
                width: 80,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.roomNights
              },
              {
                title: '间夜平均价',
                key: 'prevAvgRate',
                width: 100,
                textAlign: 'center',
                render: (text, record) => record.previousPeriod.avgRate
              },
              {
                title: '操作',
                key: 'action',
                width: 100,
                textAlign: 'center',
                render: () => (
                  <Button size="small" onClick={() => handleViewOrderDetails(totalData)}>
                    查看订单明细
                  </Button>
                )
              }
            ]}
          />
        </div>
      </Card>
    </div>
  )
}

export default ReservationReports
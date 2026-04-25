import React, { useState } from 'react'
import { Select, Button, DatePicker, Table } from 'antd'
import { SearchOutlined, ExportOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { Option } = Select

const RevenueReports = () => {
  const [selectedMonth, setSelectedMonth] = useState(dayjs())
  const [selectedHotel, setSelectedHotel] = useState('全集团')
  const [selectedStatisticMethod, setSelectedStatisticMethod] = useState('按酒店纬度')

  const hotels = ['全集团', '上海宝丽嘉', '杭州钓美', '北京王府井', '深圳南山']

  const roomTypes = {
    '上海宝丽嘉': ['豪华大床房', '行政套房', '标准双床房', '总统套房'],
    '杭州钓美': ['湖景房', '山景房', '豪华套房', '标准间'],
    '北京王府井': ['城景房', '豪华间', '套房', '标准房'],
    '深圳南山': ['海景房', '行政房', '豪华套房', '标准间']
  }

  const generateDailyOrders = (baseOrders, variance = 0) => {
    const orders = {}
    for (let i = 1; i <= 31; i++) {
      const randomVariance = Math.floor(Math.random() * (variance * 2 + 1)) - variance
      const orderCount = Math.max(0, baseOrders + randomVariance)
      orders[`day${i}`] = orderCount
    }
    return orders
  }

  const generateDailyRates = (baseRate, variance = 50) => {
    const rates = {}
    for (let i = 1; i <= 31; i++) {
      const randomVariance = (Math.random() * variance * 2) - variance
      const rate = Math.max(100, baseRate + randomVariance)
      rates[`day${i}`] = rate.toFixed(0)
    }
    return rates
  }

  const generateGroupDailyOrders = () => {
    const data = {}
    for (let i = 1; i <= 31; i++) {
      data[`day${i}`] = Math.floor(Math.random() * 30) + 150
    }
    return data
  }

  const generateGroupDailyRates = () => {
    const data = {}
    for (let i = 1; i <= 31; i++) {
      data[`day${i}`] = (Math.random() * 200 + 800).toFixed(0)
    }
    return data
  }

  const getHotelDataSource = () => {
    return [
      {
        key: 'g1',
        hotel: '全集团',
        inventoryType: '总订单数',
        ...generateGroupDailyOrders()
      },
      {
        key: 'g2',
        hotel: '全集团',
        inventoryType: '平均房价',
        ...generateGroupDailyRates()
      },
      {
        key: '1',
        hotel: '上海宝丽嘉',
        inventoryType: '总订单数',
        ...generateDailyOrders(95, 5)
      },
      {
        key: '2',
        hotel: '上海宝丽嘉',
        inventoryType: '平均房价',
        ...generateDailyRates(1200, 100)
      },
      {
        key: '3',
        hotel: '杭州钓美',
        inventoryType: '总订单数',
        ...generateDailyOrders(58, 5)
      },
      {
        key: '4',
        hotel: '杭州钓美',
        inventoryType: '平均房价',
        ...generateDailyRates(980, 80)
      },
      {
        key: '5',
        hotel: '北京王府井',
        inventoryType: '总订单数',
        ...generateDailyOrders(120, 5)
      },
      {
        key: '6',
        hotel: '北京王府井',
        inventoryType: '平均房价',
        ...generateDailyRates(1350, 120)
      },
      {
        key: '7',
        hotel: '深圳南山',
        inventoryType: '总订单数',
        ...generateDailyOrders(72, 5)
      },
      {
        key: '8',
        hotel: '深圳南山',
        inventoryType: '平均房价',
        ...generateDailyRates(1100, 90)
      }
    ]
  }

  const getRoomTypeDataSource = () => {
    const data = []
    let keyIndex = 0

    data.push({
      key: `rt_g1`,
      hotel: '全集团',
      roomType: '全房型',
      inventoryType: '总订单数',
      ...generateGroupDailyOrders()
    })
    data.push({
      key: `rt_g2`,
      hotel: '全集团',
      roomType: '全房型',
      inventoryType: '平均房价',
      ...generateGroupDailyRates()
    })

    const addHotelRoomTypes = (hotelName, baseOrders, baseRate) => {
      const types = roomTypes[hotelName] || []
      types.forEach((roomType, rtIndex) => {
        const rtBaseOrders = Math.floor(baseOrders / types.length) + (rtIndex === 0 ? baseOrders % types.length : 0)
        const rtBaseRate = baseRate + (rtIndex * 150)
        data.push({
          key: `rt_${keyIndex++}`,
          hotel: hotelName,
          roomType: roomType,
          inventoryType: '总订单数',
          ...generateDailyOrders(rtBaseOrders, 3)
        })
        data.push({
          key: `rt_${keyIndex++}`,
          hotel: hotelName,
          roomType: roomType,
          inventoryType: '平均房价',
          ...generateDailyRates(rtBaseRate, 50)
        })
      })
    }

    addHotelRoomTypes('上海宝丽嘉', 95, 1000)
    addHotelRoomTypes('杭州钓美', 58, 850)
    addHotelRoomTypes('北京王府井', 120, 1150)
    addHotelRoomTypes('深圳南山', 72, 950)

    return data
  }

  const getFilteredData = () => {
    let data
    if (selectedStatisticMethod === '按酒店纬度') {
      data = getHotelDataSource()
    } else {
      data = getRoomTypeDataSource()
    }

    if (selectedHotel === '全集团') {
      return data
    }

    return data.filter(item => item.hotel === selectedHotel)
  }

  const generateDateTitle = (day) => {
    const date = selectedMonth.date(day)
    const weekDay = ['日', '一', '二', '三', '四', '五', '六'][date.day()]
    return (
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontSize: 12 }}>{date.format('MM-DD')}</div>
        <div style={{ fontSize: 12, color: '#999' }}>{weekDay}</div>
      </div>
    )
  }

  const getColumns = () => {
    const columns = []

    const filteredData = getFilteredData()

    columns.push({
      title: '酒店',
      dataIndex: 'hotel',
      key: 'hotel',
      width: 120,
      align: 'center',
      onCell: (record, index) => {
        if (selectedStatisticMethod === '按酒店纬度') {
          if (index === 0 || record.hotel !== filteredData[index - 1].hotel) {
            return { rowSpan: 2 }
          }
          return { rowSpan: 0 }
        } else {
          if (record.hotel === '全集团') {
            if (index === 0) {
              return { rowSpan: 2 }
            }
            return { rowSpan: 0 }
          } else {
            if (index === 0 || record.hotel !== filteredData[index - 1].hotel) {
              return { rowSpan: 8 }
            }
            return { rowSpan: 0 }
          }
        }
      },
      render: (text, record, index) => {
        if (selectedStatisticMethod === '按酒店纬度') {
          if (index === 0 || !filteredData[index - 1] || record.hotel !== filteredData[index - 1].hotel) {
            return text
          }
          return null
        } else {
          if (record.hotel === '全集团') {
            if (index === 0) {
              return text
            }
            return null
          } else {
            if (index === 0 || !filteredData[index - 1] || record.hotel !== filteredData[index - 1].hotel) {
              return text
            }
            return null
          }
        }
      }
    })

    if (selectedStatisticMethod === '按房型纬度') {
      columns.push({
        title: '房型',
        dataIndex: 'roomType',
        key: 'roomType',
        width: 120,
        align: 'center',
        onCell: (record, index) => {
          if (index === 0 || record.roomType !== filteredData[index - 1].roomType || record.hotel !== filteredData[index - 1].hotel) {
            return { rowSpan: 2 }
          }
          return { rowSpan: 0 }
        },
        render: (text, record, index) => {
          if (index === 0 || !filteredData[index - 1] || record.roomType !== filteredData[index - 1].roomType || record.hotel !== filteredData[index - 1].hotel) {
            return text
          }
          return null
        }
      })
    }

    columns.push({
      title: '指标类型',
      dataIndex: 'inventoryType',
      key: 'inventoryType',
      width: 120,
      align: 'center'
    })

    for (let i = 1; i <= 31; i++) {
      columns.push({
        title: generateDateTitle(i),
        dataIndex: `day${i}`,
        key: `day${i}`,
        width: 80,
        align: 'center',
        render: (text, record) => {
          if (text !== undefined) {
            if (record.inventoryType === '平均房价') {
              return <span>¥{text}</span>
            }
            return <span>{text}</span>
          }
          return '-'
        }
      })
    }

    return columns
  }

  const filteredData = getFilteredData()
  const columns = getColumns()

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <span>营收报表</span>
      </h1>

      <div style={{ marginBottom: 16, padding: 16, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div>
            <label style={{ marginRight: 8 }}>酒店:</label>
            <Select value={selectedHotel} style={{ width: 140 }} onChange={setSelectedHotel}>
              {hotels.map(hotel => (
                <Option key={hotel} value={hotel}>{hotel}</Option>
              ))}
            </Select>
          </div>
          <div>
            <label style={{ marginRight: 8 }}>统计方式:</label>
            <Select value={selectedStatisticMethod} style={{ width: 140 }} onChange={setSelectedStatisticMethod}>
              <Option value="按酒店纬度">按酒店纬度</Option>
              <Option value="按房型纬度">按房型纬度</Option>
            </Select>
          </div>
          <div>
            <label style={{ marginRight: 8 }}>月份:</label>
            <DatePicker 
              picker="month"
              value={selectedMonth} 
              onChange={setSelectedMonth}
              style={{ width: 150 }}
            />
          </div>
          <div style={{ marginLeft: 'auto' }}>
            <Button type="primary" icon={<SearchOutlined />}>搜索</Button>
            <Button icon={<ExportOutlined />} style={{ marginLeft: 8 }}>导出</Button>
          </div>
        </div>
      </div>

      <Table
        columns={columns}
        dataSource={filteredData}
        pagination={false}
        scroll={{ x: 3000 }}
        bordered
        size="small"
        style={{ backgroundColor: '#fff' }}
      />
    </div>
  )
}

export default RevenueReports

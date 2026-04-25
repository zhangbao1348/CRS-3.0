import React, { useState } from 'react'
import { Select, Button, DatePicker, Table } from 'antd'
import { SearchOutlined, ExportOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { Option } = Select

const OccupancyReports = () => {
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

  const generateDailyRooms = (baseRooms, variance = 0) => {
    const rooms = {}
    for (let i = 1; i <= 31; i++) {
      const randomVariance = Math.floor(Math.random() * (variance * 2 + 1)) - variance
      const roomCount = Math.max(0, baseRooms + randomVariance)
      rooms[`day${i}`] = roomCount
    }
    return rooms
  }

  const generateDailyRates = (baseRate, variance = 5) => {
    const rates = {}
    for (let i = 1; i <= 31; i++) {
      const randomVariance = (Math.random() * variance * 2) - variance
      const rate = Math.max(0, Math.min(100, baseRate + randomVariance))
      rates[`day${i}`] = rate.toFixed(1)
    }
    return rates
  }

  const generateGroupDailyData = () => {
    const data = {}
    for (let i = 1; i <= 31; i++) {
      data[`day${i}`] = Math.floor(Math.random() * 20) + 180
    }
    return data
  }

  const generateGroupDailyMaintenance = () => {
    const data = {}
    for (let i = 1; i <= 31; i++) {
      data[`day${i}`] = Math.floor(Math.random() * 4) + 5
    }
    return data
  }

  const generateGroupDailySold = () => {
    const data = {}
    for (let i = 1; i <= 31; i++) {
      data[`day${i}`] = Math.floor(Math.random() * 30) + 140
    }
    return data
  }

  const generateGroupDailyOrders = () => {
    const data = {}
    for (let i = 1; i <= 31; i++) {
      data[`day${i}`] = Math.floor(Math.random() * 20) + 140
    }
    return data
  }

  const generateGroupDailyRates = () => {
    const data = {}
    for (let i = 1; i <= 31; i++) {
      data[`day${i}`] = (Math.random() * 10 + 75).toFixed(1)
    }
    return data
  }

  const getHotelDataSource = () => {
    return [
      {
        key: 'g1',
        hotel: '全集团',
        inventoryType: '酒店总房量',
        ...generateGroupDailyData()
      },
      {
        key: 'g2',
        hotel: '全集团',
        inventoryType: '维修房',
        ...generateGroupDailyMaintenance()
      },
      {
        key: 'g3',
        hotel: '全集团',
        inventoryType: '已卖房',
        ...generateGroupDailySold()
      },
      {
        key: 'g4',
        hotel: '全集团',
        inventoryType: '订单数',
        ...generateGroupDailyOrders()
      },
      {
        key: 'g5',
        hotel: '全集团',
        inventoryType: '出租率',
        ...generateGroupDailyRates()
      },
      {
        key: '1',
        hotel: '上海宝丽嘉',
        inventoryType: '酒店总房量',
        ...generateDailyRooms(120)
      },
      {
        key: '2',
        hotel: '上海宝丽嘉',
        inventoryType: '维修房',
        ...generateDailyRooms(2, 1)
      },
      {
        key: '3',
        hotel: '上海宝丽嘉',
        inventoryType: '已卖房',
        ...generateDailyRooms(34, 2)
      },
      {
        key: '4',
        hotel: '上海宝丽嘉',
        inventoryType: '订单数',
        ...generateDailyRooms(95, 5)
      },
      {
        key: '5',
        hotel: '上海宝丽嘉',
        inventoryType: '出租率',
        ...generateDailyRates(85.0)
      },
      {
        key: '6',
        hotel: '杭州钓美',
        inventoryType: '酒店总房量',
        ...generateDailyRooms(80)
      },
      {
        key: '7',
        hotel: '杭州钓美',
        inventoryType: '维修房',
        ...generateDailyRooms(1, 1)
      },
      {
        key: '8',
        hotel: '杭州钓美',
        inventoryType: '已卖房',
        ...generateDailyRooms(24, 2)
      },
      {
        key: '9',
        hotel: '杭州钓美',
        inventoryType: '订单数',
        ...generateDailyRooms(58, 5)
      },
      {
        key: '10',
        hotel: '杭州钓美',
        inventoryType: '出租率',
        ...generateDailyRates(78.2)
      },
      {
        key: '11',
        hotel: '北京王府井',
        inventoryType: '酒店总房量',
        ...generateDailyRooms(150)
      },
      {
        key: '12',
        hotel: '北京王府井',
        inventoryType: '维修房',
        ...generateDailyRooms(3, 1)
      },
      {
        key: '13',
        hotel: '北京王府井',
        inventoryType: '已卖房',
        ...generateDailyRooms(128, 5)
      },
      {
        key: '14',
        hotel: '北京王府井',
        inventoryType: '订单数',
        ...generateDailyRooms(120, 5)
      },
      {
        key: '15',
        hotel: '北京王府井',
        inventoryType: '出租率',
        ...generateDailyRates(85.3)
      },
      {
        key: '16',
        hotel: '深圳南山',
        inventoryType: '酒店总房量',
        ...generateDailyRooms(90)
      },
      {
        key: '17',
        hotel: '深圳南山',
        inventoryType: '维修房',
        ...generateDailyRooms(2, 1)
      },
      {
        key: '18',
        hotel: '深圳南山',
        inventoryType: '已卖房',
        ...generateDailyRooms(76, 5)
      },
      {
        key: '19',
        hotel: '深圳南山',
        inventoryType: '订单数',
        ...generateDailyRooms(72, 5)
      },
      {
        key: '20',
        hotel: '深圳南山',
        inventoryType: '出租率',
        ...generateDailyRates(84.4)
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
      inventoryType: '酒店总房量',
      ...generateGroupDailyData()
    })
    data.push({
      key: `rt_g2`,
      hotel: '全集团',
      roomType: '全房型',
      inventoryType: '维修房',
      ...generateGroupDailyMaintenance()
    })
    data.push({
      key: `rt_g3`,
      hotel: '全集团',
      roomType: '全房型',
      inventoryType: '已卖房',
      ...generateGroupDailySold()
    })
    data.push({
      key: `rt_g4`,
      hotel: '全集团',
      roomType: '全房型',
      inventoryType: '订单数',
      ...generateGroupDailyOrders()
    })
    data.push({
      key: `rt_g5`,
      hotel: '全集团',
      roomType: '全房型',
      inventoryType: '出租率',
      ...generateGroupDailyRates()
    })

    const addHotelRoomTypes = (hotelName, baseRooms) => {
      const types = roomTypes[hotelName] || []
      types.forEach((roomType, rtIndex) => {
        const rtBaseRooms = Math.floor(baseRooms / types.length) + (rtIndex === 0 ? baseRooms % types.length : 0)
        data.push({
          key: `rt_${keyIndex++}`,
          hotel: hotelName,
          roomType: roomType,
          inventoryType: '酒店总房量',
          ...generateDailyRooms(rtBaseRooms)
        })
        data.push({
          key: `rt_${keyIndex++}`,
          hotel: hotelName,
          roomType: roomType,
          inventoryType: '维修房',
          ...generateDailyRooms(Math.floor(rtBaseRooms * 0.02), 1)
        })
        data.push({
          key: `rt_${keyIndex++}`,
          hotel: hotelName,
          roomType: roomType,
          inventoryType: '已卖房',
          ...generateDailyRooms(Math.floor(rtBaseRooms * 0.8), 3)
        })
        data.push({
          key: `rt_${keyIndex++}`,
          hotel: hotelName,
          roomType: roomType,
          inventoryType: '订单数',
          ...generateDailyRooms(Math.floor(rtBaseRooms * 0.75), 3)
        })
        data.push({
          key: `rt_${keyIndex++}`,
          hotel: hotelName,
          roomType: roomType,
          inventoryType: '出租率',
          ...generateDailyRates(78 + Math.random() * 15)
        })
      })
    }

    addHotelRoomTypes('上海宝丽嘉', 120)
    addHotelRoomTypes('杭州钓美', 80)
    addHotelRoomTypes('北京王府井', 150)
    addHotelRoomTypes('深圳南山', 90)

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
            return { rowSpan: 5 }
          }
          return { rowSpan: 0 }
        } else {
          if (record.hotel === '全集团') {
            if (index === 0) {
              return { rowSpan: 5 }
            }
            return { rowSpan: 0 }
          } else {
            if (index === 0 || record.hotel !== filteredData[index - 1].hotel) {
              return { rowSpan: 20 }
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
            return { rowSpan: 5 }
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
      title: '库存类型',
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
            if (record.inventoryType === '出租率') {
              return <span>{text}%</span>
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
        <span>出租率报表</span>
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

export default OccupancyReports

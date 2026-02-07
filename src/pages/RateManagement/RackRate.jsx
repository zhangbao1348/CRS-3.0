import React, { useState } from 'react'
import { Tabs, Button, Checkbox, DatePicker, Input, Table, Card, Row, Col } from 'antd'
import { DollarOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { RangePicker } = DatePicker
const { TabPane } = Tabs

// 模拟日期数据
const generateDates = () => {
  const dates = []
  const today = dayjs()
  for (let i = 0; i < 8; i++) {
    const date = today.add(i, 'day')
    dates.push({
      date: date.format('YYYY-MM-DD'),
      weekday: date.format('dddd')
    })
  }
  return dates
}

// 模拟房型数据
const roomTypes = [
  { code: 'ST', name: '标准双床' },
  { code: 'STR', name: '高级大床' },
  { code: 'ST1', name: '海景双床' },
  { code: 'ST2', name: '海景大床' },
  { code: 'ST3', name: '海景套房' }
]

const RackRate = () => {
  // 状态管理
  const [activeTabKey, setActiveTabKey] = useState('bar')
  const [dates, setDates] = useState(generateDates())
  const [dateRange, setDateRange] = useState(null)
  const [showMultiPrice, setShowMultiPrice] = useState(false)
  // 初始化基准价格为300
  const initialBasePrices = {};
  generateDates().forEach((_, index) => {
    initialBasePrices[index] = 300;
  });
  const [basePrices, setBasePrices] = useState(initialBasePrices)
  const [roomPrices, setRoomPrices] = useState({
    ST: Array(8).fill(300),
    STR: Array(8).fill(320),
    ST1: Array(8).fill(340),
    ST2: Array(8).fill(360),
    ST3: Array(8).fill(380)
  })

  // 处理日期范围变化
  const handleDateRangeChange = (dates) => {
    setDateRange(dates)
    // 生成新的日期数组
    if (dates && dates.length === 2) {
      const newDates = []
      let currentDate = dates[0]
      const endDate = dates[1]
      
      while (currentDate.isBefore(endDate) || currentDate.isSame(endDate, 'day')) {
        newDates.push({
          date: currentDate.format('YYYY-MM-DD'),
          weekday: currentDate.format('dddd')
        })
        currentDate = currentDate.add(1, 'day')
      }
      
      setDates(newDates)
    }
  }

  // 处理基准价格变化
  const handleBasePriceChange = (index, value) => {
    setBasePrices(prev => ({
      ...prev,
      [index]: value
    }))
    // 如果基准价格变化，根据房型系数计算所有房型的价格
    const updatedRoomPrices = { ...roomPrices }
    // 房型系数配置（可以根据实际需求调整）
    const roomCoefficients = {
      ST: 1.0,   // 标准双床 基准价格
      STR: 1.1,  // 高级大床 基准价格*1.1
      ST1: 1.2,  // 海景双床 基准价格*1.2
      ST2: 1.3,  // 海景大床 基准价格*1.3
      ST3: 1.4   // 海景套房 基准价格*1.4
    }
    
    Object.keys(updatedRoomPrices).forEach(roomType => {
      updatedRoomPrices[roomType] = updatedRoomPrices[roomType].map((price, i) => {
        if (i === index) {
          // 根据房型系数计算新价格
          const basePrice = value || 0
          return Math.round(basePrice * roomCoefficients[roomType])
        }
        return price
      })
    })
    setRoomPrices(updatedRoomPrices)
  }

  // 处理查询
  const handleSearch = () => {
    console.log('查询价格数据', { dateRange, showMultiPrice })
    // 这里可以添加查询逻辑
  }

  // 处理批量修改
  const handleBatchUpdate = () => {
    console.log('批量修改价格')
    // 这里可以添加批量修改逻辑
  }

  // 渲染日期表头
  const renderDateHeaders = () => {
    return dates.map((date, index) => (
      <th key={index}>
        {date.date}<br/>
        ({date.weekday === 'Sunday' ? '日' : 
          date.weekday === 'Monday' ? '一' :
          date.weekday === 'Tuesday' ? '二' :
          date.weekday === 'Wednesday' ? '三' :
          date.weekday === 'Thursday' ? '四' :
          date.weekday === 'Friday' ? '五' : '六'})
      </th>
    ))
  }

  return (
    <div className="page-container">
      <h1 className="page-title">
        <DollarOutlined /> 基础价格设置
      </h1>
      <Card bordered={false}>
        <Tabs 
          activeKey={activeTabKey} 
          onChange={setActiveTabKey}
          style={{ marginBottom: 20 }}
        >
          <TabPane tab="门市价(BAR)" key="bar" />
          <TabPane tab="大客户固定价(SCAS)" key="scas" />
          <TabPane tab="大客户双早(SCASBB)" key="scas-double" />
        </Tabs>

        <div style={{ marginBottom: 20 }}>
          <Row gutter={16} align="middle">
            <Col>
              <span style={{ marginRight: 10 }}>日期:</span>
              <RangePicker
                value={dateRange}
                onChange={handleDateRangeChange}
                style={{ marginRight: 20 }}
              />
            </Col>
            <Col>
              <span style={{ marginRight: 10 }}>查看多人价:</span>
              <Checkbox
                checked={showMultiPrice}
                onChange={(e) => setShowMultiPrice(e.target.checked)}
              >
                是
              </Checkbox>
            </Col>
            <Col offset={8}>
              <Button type="primary" onClick={handleSearch} style={{ marginRight: 10 }}>
                查询
              </Button>
              <Button type="primary" onClick={handleBatchUpdate}>
                批量修改
              </Button>
            </Col>
          </Row>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th style={{ border: '1px solid #d9d9d9', padding: '8px', textAlign: 'center', width: '120px' }}></th>
                {renderDateHeaders()}
              </tr>
            </thead>
            <tbody>
              {/* 基准价格行 */}
              <tr>
                <td style={{ border: '1px solid #d9d9d9', padding: '8px', textAlign: 'center', fontWeight: 'bold' }}>基准价格</td>
                {dates.map((_, index) => (
                  <td key={index} style={{ border: '1px solid #d9d9d9', padding: '8px', textAlign: 'center' }}>
                    <Input
                      type="number"
                      style={{ width: '80px', textAlign: 'center' }}
                      value={basePrices[index] || 300}
                      onChange={(e) => handleBasePriceChange(index, Number(e.target.value))}
                    />
                  </td>
                ))}
              </tr>
              {/* 房型价格行 */}
              {roomTypes.map(room => (
                <tr key={room.code}>
                  <td style={{ border: '1px solid #d9d9d9', padding: '8px', textAlign: 'center' }}>
                    {room.name} ({room.code})
                  </td>
                  {dates.map((_, index) => (
                    <td key={index} style={{ border: '1px solid #d9d9d9', padding: '8px', textAlign: 'center' }}>
                      <Input
                        type="number"
                        style={{ width: '80px', textAlign: 'center', backgroundColor: '#f5f5f5' }}
                        value={roomPrices[room.code][index]}
                        readOnly
                      />
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  )
}

export default RackRate
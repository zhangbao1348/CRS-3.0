import React, { useState } from 'react'
import { Table, Select, Button, Space, Checkbox, DatePicker, Form, InputNumber, Row, Col, Radio } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { Option } = Select
const { RangePicker } = DatePicker

// 模拟房型数据
const mockRoomTypes = [
  { code: 'BQ', name: 'Banquet Room', physical: 1, overBook: 0, totalAvailable: 8, reserved: 0, remaining: 8, actualAvailable: 8 },
  { code: 'HT00001', name: '测试房型', physical: 0, overBook: 0, totalAvailable: 0, reserved: 0, remaining: 0, actualAvailable: 0 },
  { code: 'CHEN', name: '一居室', physical: 0, overBook: 0, totalAvailable: 1, reserved: 0, remaining: 1, actualAvailable: 1 },
  { code: 'SK', name: '独栋大套房', physical: 0, overBook: 0, totalAvailable: 35, reserved: 0, remaining: 35, actualAvailable: 35 },
  { code: 'PF', name: 'PF', physical: 0, overBook: 0, totalAvailable: 0, reserved: 0, remaining: 0, actualAvailable: 0 },
  { code: 'ST', name: '标准双床房', physical: 0, overBook: 0, totalAvailable: 15, reserved: 0, remaining: 15, actualAvailable: 15 },
  { code: 'DK', name: '高级大床房', physical: 0, overBook: 0, totalAvailable: 21, reserved: 0, remaining: 21, actualAvailable: 21 },
  { code: 'DT', name: '豪华双床房', physical: 0, overBook: 0, totalAvailable: 119, reserved: 0, remaining: 119, actualAvailable: 119 },
  { code: 'BS', name: '商务套房', physical: 1, overBook: 0, totalAvailable: 6, reserved: 0, remaining: 6, actualAvailable: 6 },
  { code: 'SSDCF', name: '舒适大床房', physical: 0, overBook: 0, totalAvailable: 0, reserved: 0, remaining: 0, actualAvailable: 0 }
]

const RoomInventory = () => {
  // 状态管理
  const [selectedDate, setSelectedDate] = useState(dayjs('2026-01-23'))
  const [stayDays, setStayDays] = useState(15)
  const [selectedRoomTypes, setSelectedRoomTypes] = useState([])
  const [roomTypesData, setRoomTypesData] = useState(mockRoomTypes)

  // 处理日期选择
  const handleDateChange = (date) => {
    if (date) {
      setSelectedDate(date)
    }
  }

  // 处理住客天数选择
  const handleStayDaysChange = (e) => {
    setStayDays(e.target.value)
  }

  // 处理房型选择
  const handleRoomTypeChange = (checkedValues) => {
    setSelectedRoomTypes(checkedValues)
  }

  // 处理搜索
  const handleSearch = () => {
    // 搜索逻辑
    console.log('搜索条件:', { selectedDate, stayDays, selectedRoomTypes })
  }

  // 处理物理量变更
  const handlePhysicalChange = (code, value) => {
    const updatedData = roomTypesData.map(item => {
      if (item.code === code) {
        return {
          ...item,
          physical: value
        }
      }
      return item
    })
    setRoomTypesData(updatedData)
  }

  // 处理超额预订量变更
  const handleOverBookChange = (code, value) => {
    const updatedData = roomTypesData.map(item => {
      if (item.code === code) {
        return {
          ...item,
          overBook: value
        }
      }
      return item
    })
    setRoomTypesData(updatedData)
  }

  // 表格列配置
  const columns = [
    {
      title: (
        <Checkbox 
          indeterminate={selectedRoomTypes.length > 0 && selectedRoomTypes.length < roomTypesData.length}
          checked={selectedRoomTypes.length === roomTypesData.length}
          onChange={(e) => {
            if (e.target.checked) {
              setSelectedRoomTypes(roomTypesData.map(item => item.code))
            } else {
              setSelectedRoomTypes([])
            }
          }}
        >
          房型
        </Checkbox>
      ),
      key: 'code',
      render: (_, record) => (
        <Checkbox 
          checked={selectedRoomTypes.includes(record.code)}
          onChange={(e) => {
            if (e.target.checked) {
              setSelectedRoomTypes([...selectedRoomTypes, record.code])
            } else {
              setSelectedRoomTypes(selectedRoomTypes.filter(code => code !== record.code))
            }
          }}
        >
          {record.code}
        </Checkbox>
      )
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: 150
    },
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date',
      width: 120,
      render: () => selectedDate.format('YYYY-MM-DD')
    },
    {
      title: '物理量',
      dataIndex: 'physical',
      key: 'physical',
      width: 100,
      render: (_, record) => (
        <InputNumber
          value={record.physical}
          onChange={(value) => handlePhysicalChange(record.code, value)}
          style={{ width: 80 }}
        />
      )
    },
    {
      title: '超额预订量',
      dataIndex: 'overBook',
      key: 'overBook',
      width: 120,
      render: (_, record) => (
        <InputNumber
          value={record.overBook}
          onChange={(value) => handleOverBookChange(record.code, value)}
          style={{ width: 80 }}
        />
      )
    },
    {
      title: '总可售房量(物理房量+超额预订量)',
      dataIndex: 'totalAvailable',
      key: 'totalAvailable',
      width: 180
    },
    {
      title: '预留房',
      dataIndex: 'reserved',
      key: 'reserved',
      width: 100
    },
    {
      title: '已售',
      dataIndex: 'booked',
      key: 'booked',
      width: 100,
      render: (_, record) => {
        const booked = record.totalAvailable - record.remaining
        return (
          <span style={{ color: '#fa541c' }}>{booked}</span>
        )
      }
    },
    {
      title: '剩余房量',
      dataIndex: 'remaining',
      key: 'remaining',
      width: 100,
      render: (_, record) => {
        return (
          <span style={{ color: '#52c41a' }}>{record.remaining}</span>
        )
      }
    },
    {
      title: '实际可售房量',
      dataIndex: 'actualAvailable',
      key: 'actualAvailable',
      width: 120
    }
  ]

  return (
    <div className="fade-in">
      {/* 标题区域 */}
      <h1 className="page-title">
        <span>房型库存</span>
      </h1>

      {/* 筛选区域 */}
      <Form layout="inline" style={{ marginBottom: 16, padding: 16, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
        <Row gutter={16} align="middle">
          <Col>
            <Form.Item label="库存日期" labelCol={{ span: 8 }} wrapperCol={{ span: 16 }}>
              <DatePicker
                value={selectedDate}
                onChange={handleDateChange}
                format="YYYY-MM-DD"
                style={{ width: 150 }}
              />
            </Form.Item>
          </Col>
          <Col>
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
              搜索
            </Button>
          </Col>
        </Row>
      </Form>

      {/* 数据表格 */}
      <Table
        columns={columns}
        dataSource={roomTypesData}
        rowKey="code"
        pagination={false}
        scroll={{ x: 1200, y: 600 }}
        bordered
        size="small"
        style={{ backgroundColor: '#fff' }}
      />
    </div>
  )
}

export default RoomInventory
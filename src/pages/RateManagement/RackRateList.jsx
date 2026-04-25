import React, { useState } from 'react'
import { Table, Card, Button, DatePicker, Select, Input, Space, Tag } from 'antd'
import { SearchOutlined, TableOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { RangePicker } = DatePicker
const { Option } = Select

// 模拟酒店数据
const hotels = [
  { id: 1, name: '上海希尔顿酒店', code: 'SHA001' },
  { id: 2, name: '北京万豪酒店', code: 'BJS001' },
  { id: 3, name: '广州洲际酒店', code: 'GZH001' }
]

// 模拟房型数据
const roomTypes = [
  { code: 'ST', name: '标准双床' },
  { code: 'STR', name: '高级大床' },
  { code: 'ST1', name: '海景双床' },
  { code: 'ST2', name: '海景大床' },
  { code: 'ST3', name: '海景套房' }
]

// 生成模拟列表数据
const generateMockData = () => {
  const data = []
  const today = dayjs()
  let id = 1

  hotels.forEach(hotel => {
    roomTypes.forEach(roomType => {
      for (let i = 0; i < 30; i++) {
        const date = today.add(i, 'day')
        const basePrice = 300 + Math.floor(Math.random() * 200)
        const price1 = basePrice
        const price2 = Math.round(basePrice * 1.07)
        
        data.push({
          id: id++,
          key: id,
          hotelId: hotel.id,
          hotelName: hotel.name,
          hotelCode: hotel.code,
          roomTypeCode: roomType.code,
          roomTypeName: roomType.name,
          date: date.format('YYYY-MM-DD'),
          weekday: date.format('dddd'),
          basePrice,
          price1,
          price2,
          status: Math.random() > 0.2 ? 'active' : 'inactive'
        })
      }
    })
  })

  return data
}

const RackRateList = () => {
  const [dataSource, setDataSource] = useState(generateMockData())
  const [filteredData, setFilteredData] = useState(dataSource)
  const [loading, setLoading] = useState(false)

  // 搜索条件
  const [searchParams, setSearchParams] = useState({
    hotelId: null,
    roomTypeCode: null,
    dateRange: null,
    keyword: ''
  })

  // 处理搜索
  const handleSearch = () => {
    setLoading(true)
    
    setTimeout(() => {
      let result = [...dataSource]

      // 按酒店筛选
      if (searchParams.hotelId) {
        result = result.filter(item => item.hotelId === searchParams.hotelId)
      }

      // 按房型筛选
      if (searchParams.roomTypeCode) {
        result = result.filter(item => item.roomTypeCode === searchParams.roomTypeCode)
      }

      // 按日期范围筛选
      if (searchParams.dateRange && searchParams.dateRange.length === 2) {
        const startDate = searchParams.dateRange[0]
        const endDate = searchParams.dateRange[1]
        result = result.filter(item => {
          const itemDate = dayjs(item.date)
          return itemDate.isAfter(startDate.subtract(1, 'day')) && itemDate.isBefore(endDate.add(1, 'day'))
        })
      }

      // 按关键词筛选
      if (searchParams.keyword) {
        const keyword = searchParams.keyword.toLowerCase()
        result = result.filter(item => 
          item.hotelName.toLowerCase().includes(keyword) ||
          item.roomTypeName.toLowerCase().includes(keyword) ||
          item.hotelCode.toLowerCase().includes(keyword) ||
          item.roomTypeCode.toLowerCase().includes(keyword)
        )
      }

      setFilteredData(result)
      setLoading(false)
    }, 500)
  }

  // 处理重置
  const handleReset = () => {
    setSearchParams({
      hotelId: null,
      roomTypeCode: null,
      dateRange: null,
      keyword: ''
    })
    setFilteredData(dataSource)
  }

  // 表格列配置
  const columns = [
    {
      title: '酒店',
      dataIndex: 'hotelName',
      key: 'hotelName',
      width: 200,
      render: (text, record) => (
        <div>
          <div style={{ fontWeight: 'bold' }}>{text}</div>
          <div style={{ fontSize: '12px', color: '#999' }}>({record.hotelCode})</div>
        </div>
      )
    },
    {
      title: '房型',
      dataIndex: 'roomTypeName',
      key: 'roomTypeName',
      width: 150,
      render: (text, record) => (
        <div>
          <div>{text}</div>
          <div style={{ fontSize: '12px', color: '#999' }}>({record.roomTypeCode})</div>
        </div>
      )
    },
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date',
      width: 120,
      render: (text, record) => (
        <div>
          <div>{text}</div>
          <div style={{ fontSize: '12px', color: '#999' }}>
            ({record.weekday === 'Sunday' ? '周日' : 
              record.weekday === 'Monday' ? '周一' :
              record.weekday === 'Tuesday' ? '周二' :
              record.weekday === 'Wednesday' ? '周三' :
              record.weekday === 'Thursday' ? '周四' :
              record.weekday === 'Friday' ? '周五' : '周六'})
          </div>
        </div>
      )
    },
    {
      title: '基准价格',
      dataIndex: 'basePrice',
      key: 'basePrice',
      width: 120,
      align: 'right',
      render: (price) => `¥${price}`
    },
    {
      title: '1人价',
      dataIndex: 'price1',
      key: 'price1',
      width: 100,
      align: 'right',
      render: (price) => `¥${price}`
    },
    {
      title: '2人价',
      dataIndex: 'price2',
      key: 'price2',
      width: 100,
      align: 'right',
      render: (price) => `¥${price}`
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <Tag color={status === 'active' ? 'green' : 'red'}>
          {status === 'active' ? '启用' : '停用'}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small">编辑</Button>
          <Button type="link" size="small" danger>
            {record.status === 'active' ? '停用' : '启用'}
          </Button>
        </Space>
      )
    }
  ]

  return (
    <div className="page-container">
      <h1 className="page-title">
        <TableOutlined /> 基础价格设置-列表
      </h1>
      
      <Card bordered={false}>
        {/* 搜索区域 */}
        <div style={{ marginBottom: 20, padding: 16, backgroundColor: '#fafafa', borderRadius: 8 }}>
          <Space wrap size="middle">
            <Select
              placeholder="选择酒店"
              style={{ width: 200 }}
              value={searchParams.hotelId}
              onChange={(value) => setSearchParams({ ...searchParams, hotelId: value })}
              allowClear
            >
              {hotels.map(hotel => (
                <Option key={hotel.id} value={hotel.id}>
                  {hotel.name} ({hotel.code})
                </Option>
              ))}
            </Select>

            <Select
              placeholder="选择房型"
              style={{ width: 200 }}
              value={searchParams.roomTypeCode}
              onChange={(value) => setSearchParams({ ...searchParams, roomTypeCode: value })}
              allowClear
            >
              {roomTypes.map(roomType => (
                <Option key={roomType.code} value={roomType.code}>
                  {roomType.name} ({roomType.code})
                </Option>
              ))}
            </Select>

            <RangePicker
              placeholder={['开始日期', '结束日期']}
              value={searchParams.dateRange}
              onChange={(dates) => setSearchParams({ ...searchParams, dateRange: dates })}
              style={{ width: 300 }}
            />

            <Input
              placeholder="关键词搜索"
              prefix={<SearchOutlined />}
              style={{ width: 200 }}
              value={searchParams.keyword}
              onChange={(e) => setSearchParams({ ...searchParams, keyword: e.target.value })}
              allowClear
            />

            <Space>
              <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
                搜索
              </Button>
              <Button onClick={handleReset}>重置</Button>
            </Space>
          </Space>
        </div>

        {/* 操作按钮 */}
        <div style={{ marginBottom: 16 }}>
          <Space>
            <Button type="primary">批量修改</Button>
            <Button>批量导出</Button>
            <Button>查看日志</Button>
          </Space>
        </div>

        {/* 表格 */}
        <Table
          columns={columns}
          dataSource={filteredData}
          loading={loading}
          rowKey="id"
          scroll={{ x: 1200, y: 600 }}
          pagination={{
            pageSize: 50,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>
    </div>
  )
}

export default RackRateList

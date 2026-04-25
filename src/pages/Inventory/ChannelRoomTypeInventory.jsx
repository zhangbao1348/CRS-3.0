import React, { useState, useRef, useEffect } from 'react'
import { Table, Select, Button, Modal, Form, Input, DatePicker, message } from 'antd'
import dayjs from 'dayjs'

const { RangePicker } = DatePicker
import { FilterOutlined, PlusOutlined, MinusOutlined } from '@ant-design/icons'

const { Option } = Select

const ChannelRoomTypeInventory = () => {
  const today = new Date(2025, 11, 15)
  today.setHours(0, 0, 0, 0)
  const currentYear = today.getFullYear()
  const currentMonth = String(today.getMonth() + 1).padStart(2, '0')
  const [selectedMonth, setSelectedMonth] = useState(`${currentYear}-${currentMonth}`)
  const [selectedChannel, setSelectedChannel] = useState('全部渠道')
  const [selectedRoomType, setSelectedRoomType] = useState('全部房型')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [currentRow, setCurrentRow] = useState(null)
  const [currentDate, setCurrentDate] = useState(null)
  const [form] = Form.useForm()

  const roomTypes = [
    { code: '1KGS', name: '标准大床房', total: 40 },
    { code: '1KGP', name: '高级大床房', total: 30 },
    { code: '1KGH', name: '探索大床房', total: 25 },
    { code: '2TGH', name: '探索双床房', total: 35 }
  ]

  const channels = [
    { code: 'CTRIP', name: '携程' },
    { code: 'MEITUAN', name: '美团' },
    { code: 'ELONG', name: '艺龙' },
    { code: 'FLIGGY', name: '飞猪' }
  ]

  const getDaysInMonth = (monthStr) => {
    const [year, month] = monthStr.split('-').map(Number)
    return new Date(year, month, 0).getDate()
  }

  const getDayOfWeek = (year, month, day) => {
    const days = ['日', '一', '二', '三', '四', '五', '六']
    return days[new Date(year, month - 1, day).getDay()]
  }

  const getDateInfo = (year, month, day) => {
    const date = new Date(year, month - 1, day)
    date.setHours(0, 0, 0, 0)
    const diffDays = Math.floor((date.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
    let label = ''
    if (diffDays === 0) label = '今天'
    else if (diffDays === 1) label = '明天'
    else if (diffDays === 2) label = '后天'
    const isPast = diffDays < 0
    const dayOfWeek = date.getDay()
    return { label, isPast, isFriday: dayOfWeek === 5, isSaturday: dayOfWeek === 6 }
  }

  const generateDates = (monthStr) => {
    const daysInMonth = getDaysInMonth(monthStr)
    const [year, month] = monthStr.split('-').map(Number)
    const dates = []
    for (let i = 1; i <= daysInMonth; i++) {
      const dateInfo = getDateInfo(year, month, i)
      dates.push({
        key: `date_${i}`,
        day: i,
        dateStr: `${String(month).padStart(2, '0')}.${String(i).padStart(2, '0')}`,
        dayOfWeek: getDayOfWeek(year, month, i),
        ...dateInfo
      })
    }
    return dates
  }

  const dates = generateDates(selectedMonth)

  const handlePrevMonth = () => {
    const [year, month] = selectedMonth.split('-').map(Number)
    const newMonth = month === 1 ? 12 : month - 1
    const newYear = month === 1 ? year - 1 : year
    setSelectedMonth(`${newYear}-${String(newMonth).padStart(2, '0')}`)
  }

  const handleNextMonth = () => {
    const [year, month] = selectedMonth.split('-').map(Number)
    const newMonth = month === 12 ? 1 : month + 1
    const newYear = month === 12 ? year + 1 : year
    setSelectedMonth(`${newYear}-${String(newMonth).padStart(2, '0')}`)
  }

  const generateData = () => {
    const data = []
    const inventoryTypes = [
      { key: 'sold', label: '已售', color: '#1890ff' },
      { key: 'remaining', label: '剩余可售', color: '#52c41a' },
      { key: 'limit', label: '库存限制', color: '#faad14' }
    ]

    roomTypes.forEach(roomType => {
      channels.forEach(channel => {
        inventoryTypes.forEach(invType => {
          const row = {
            key: `${roomType.code}_${channel.code}_${invType.key}`,
            roomTypeCode: roomType.code,
            roomTypeName: roomType.name,
            channelCode: channel.code,
            channelName: channel.name,
            inventoryType: invType.key,
            inventoryLabel: invType.label,
            inventoryColor: invType.color,
            isEditable: invType.key === 'limit'
          }

          dates.forEach((date, dateIndex) => {
            const isLimitNotSet = Math.random() > 0.7
            const limit = isLimitNotSet ? '-' : Math.floor(Math.random() * 30) + 5
            const sold = Math.floor(Math.random() * 20)
            const remaining = isLimitNotSet ? '-' : Math.max(0, limit - sold)

            if (invType.key === 'sold') {
              row[date.key] = sold
            } else if (invType.key === 'remaining') {
              row[date.key] = remaining
            } else {
              row[date.key] = limit
            }
          })

          data.push(row)
        })
      })
    })

    return data
  }

  const [tableData] = useState(generateData())

  const filteredData = tableData.filter(item => {
    const channelMatch = selectedChannel === '全部渠道' || item.channelCode === selectedChannel
    const roomTypeMatch = selectedRoomType === '全部房型' || item.roomTypeCode === selectedRoomType
    return channelMatch && roomTypeMatch
  })

  const tableRef = useRef(null)
  const todayIndex = dates.findIndex(d => d.label === '今天')

  useEffect(() => {
    const timer = setTimeout(() => {
      const wrapper = document.querySelector('.ant-table-wrapper')
      if (wrapper) {
        const scrollContainer = wrapper.querySelector('.ant-table-body') || wrapper
        if (scrollContainer) {
          scrollContainer.scrollLeft = todayIndex * 50 + 80
        }
      }
    }, 1000)
    return () => clearTimeout(timer)
  }, [todayIndex, selectedMonth])

  const renderDateHeader = (date) => {
    const bgColor = date.isPast ? '#f5f5f5' : date.isFriday ? '#fff7e6' : date.isSaturday ? '#f9f0ff' : '#fff'
    const textColor = date.isPast ? '#999' : date.isFriday ? '#fa8c16' : date.isSaturday ? '#722ed1' : '#333'
    return (
      <div style={{ textAlign: 'center', fontSize: 12, padding: '2px 0', backgroundColor: bgColor, color: textColor, borderRadius: 2 }}>
        <div>{date.dateStr}</div>
        <div style={{ fontSize: 10, display: 'flex', justifyContent: 'center', gap: 2 }}>
          <span>{date.dayOfWeek}</span>
          {date.label && <span style={{ color: '#1890ff', fontWeight: 500 }}>{date.label}</span>}
        </div>
      </div>
    )
  }

  const columns = [
    {
      title: (
        <div style={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '8px 0' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: 'center' }}>
            <Button type="text" icon={<MinusOutlined />} size="small" onClick={handlePrevMonth}>上月</Button>
            <span style={{ fontSize: 12, whiteSpace: 'nowrap' }}>{selectedMonth}</span>
            <Button type="text" icon={<PlusOutlined />} size="small" onClick={handleNextMonth}>下月</Button>
          </div>
        </div>
      ),
      key: 'roomTypeChannel',
      width: 260,
      fixed: 'left',
      render: (_, record, index) => ({
        children: (
          <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
            <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
            <span style={{ fontWeight: 600 }}>{record.roomTypeName}</span>
            <span style={{ marginLeft: 4, color: '#666' }}>| {record.channelName}</span>
          </div>
        ),
        props: { rowSpan: index % 3 === 0 ? 3 : 0 }
      })
    },
    {
      title: '库存类型',
      dataIndex: 'inventoryLabel',
      key: 'inventoryType',
      width: 100,
      fixed: 'left',
      render: (text, record) => (
        <div style={{ fontSize: 12, color: record.inventoryColor, fontWeight: 500 }}>
          {text}
        </div>
      )
    },
    ...dates.map(date => ({
      title: renderDateHeader(date),
      dataIndex: date.key,
      key: date.key,
      width: 50,
      render: (value, record) => (
        <div 
          style={{
            textAlign: 'center',
            padding: '4px 0',
            backgroundColor: '#fff',
            border: '1px solid #e8e8e8',
            fontSize: 12,
            color: record.inventoryColor,
            cursor: record.isEditable && value !== '-' ? 'pointer' : 'default'
          }}
          onClick={() => {
            if (record.isEditable && value !== '-') {
              setCurrentRow(record)
              setCurrentDate(date)
              form.setFieldsValue({ limit: value })
              setIsModalVisible(true)
            }
          }}
        >
          {value}
        </div>
      )
    }))
  ]

  const handleSubmit = () => {
    form.validateFields().then(values => {
      if (currentRow && currentDate) {
        message.success(`${currentRow.roomTypeName}-${currentRow.channelName} ${currentDate.dateStr} 库存限制已更新为 ${values.limit}`)
      } else {
        const roomTypeNames = roomTypes.filter(rt => values.roomTypes.includes(rt.code)).map(rt => rt.name).join('、')
        const channelNames = channels.filter(c => values.channels.includes(c.code)).map(c => c.name).join('、')
        message.success(`批量修改成功：${roomTypeNames} + ${channelNames} 在所选日期范围的库存限制已更新为 ${values.limit}`)
      }
      setIsModalVisible(false)
    })
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 16 }}>
        <Select
          value={selectedChannel}
          style={{ width: 180 }}
          size="middle"
          onChange={setSelectedChannel}
        >
          <Option value="全部渠道">全部渠道</Option>
          {channels.map(c => (
            <Option key={c.code} value={c.code}>
              {c.name}
            </Option>
          ))}
        </Select>
        <Select
          value={selectedRoomType}
          style={{ width: 180 }}
          size="middle"
          onChange={setSelectedRoomType}
        >
          <Option value="全部房型">全部房型</Option>
          {roomTypes.map(rt => (
            <Option key={rt.code} value={rt.code}>
              {rt.name}
            </Option>
          ))}
        </Select>
        <Button type="text" icon={<FilterOutlined />} size="middle">
          筛选
        </Button>
        <Button type="text" size="middle" onClick={() => {
          setCurrentRow(null)
          setCurrentDate(null)
          form.setFieldsValue({ limit: 0 })
          setIsModalVisible(true)
        }}>
          批量修改
        </Button>
      </div>

      <Table
        ref={tableRef}
        columns={columns}
        dataSource={filteredData}
        rowKey="key"
        pagination={false}
        scroll={{ x: 1500, y: 500 }}
        bordered
        size="small"
        style={{ backgroundColor: '#fff' }}
        rowStyle={{ height: 35 }}
      />

      <Modal
        title={currentRow && currentDate 
          ? `维护 ${currentRow.roomTypeName}-${currentRow.channelName} - ${currentDate.dateStr} 库存限制` 
          : '批量修改库存限制'}
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={() => setIsModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={500}
      >
        <Form form={form} layout="vertical">
          {!currentRow && !currentDate && (
            <>
              <Form.Item
                name="roomTypes"
                label="选择房型"
                rules={[{ required: true, message: '请选择房型' }]}
              >
                <Select
                  mode="multiple"
                  placeholder="请选择房型"
                  style={{ width: '100%' }}
                >
                  {roomTypes.map(rt => (
                    <Option key={rt.code} value={rt.code}>{rt.name}</Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item
                name="channels"
                label="选择渠道"
                rules={[{ required: true, message: '请选择渠道' }]}
              >
                <Select
                  mode="multiple"
                  placeholder="请选择渠道"
                  style={{ width: '100%' }}
                >
                  {channels.map(c => (
                    <Option key={c.code} value={c.code}>{c.name}</Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item
                name="dateRange"
                label="选择日期范围"
                rules={[{ required: true, message: '请选择日期范围' }]}
              >
                <RangePicker style={{ width: '100%' }} />
              </Form.Item>
            </>
          )}
          <Form.Item
            name="limit"
            label="库存限制数量"
            rules={[{ required: true, message: '请输入库存限制数量' }]}
          >
            <Input type="number" placeholder="请输入库存限制数量" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ChannelRoomTypeInventory
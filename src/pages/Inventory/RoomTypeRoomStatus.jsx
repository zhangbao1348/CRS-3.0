import React, { useState, useRef, useEffect } from 'react'
import { Table, Select, Button } from 'antd'
import { FilterOutlined, PlusOutlined, MinusOutlined, HomeOutlined } from '@ant-design/icons'

const { Option } = Select

const RoomTypeRoomStatus = () => {
  const today = new Date(2025, 11, 15)
  today.setHours(0, 0, 0, 0)
  const currentYear = today.getFullYear()
  const currentMonth = String(today.getMonth() + 1).padStart(2, '0')
  const [selectedMonth, setSelectedMonth] = useState(`${currentYear}-${currentMonth}`)
  
  const roomTypes = [
    { key: '1KGS', code: '1KGS', name: '标准大床房' },
    { key: '1KGP', code: '1KGP', name: '高级大床房' },
    { key: '1KGH', code: '1KGH', name: '探索大床房' },
    { key: '2TGH', code: '2TGH', name: '探索双床房' }
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

  const generateData = () => {
    const data = []
    roomTypes.forEach(roomType => {
      const row = {
        key: roomType.key,
        roomTypeCode: roomType.code,
        roomTypeName: roomType.name
      }
      dates.forEach(date => {
        row[date.key] = Math.random() > 0.2
      })
      data.push(row)
    })
    return data
  }

  const [tableData] = useState(generateData())

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
      key: 'roomType',
      width: 260,
      fixed: 'left',
      render: (_, record) => (
        <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
          <HomeOutlined style={{ marginRight: 6, color: '#1890ff', fontSize: 14 }} />
          <span style={{ fontWeight: 600 }}>{record.roomTypeName}</span>
        </div>
      )
    },
    ...dates.map(date => ({
      title: renderDateHeader(date),
      dataIndex: date.key,
      key: date.key,
      width: 50,
      onCell: () => ({
        style: {
          backgroundColor: date.isPast ? '#fafafa' : 'transparent',
          padding: '4px 0'
        }
      }),
      render: (isOpen) => (
        <div style={{ 
          textAlign: 'center', 
          fontSize: 12,
          color: date.isPast ? '#ccc' : isOpen ? '#52c41a' : '#ff4d4f',
          fontWeight: 500
        }}>
          {isOpen ? '开' : '关'}
        </div>
      )
    }))
  ]

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 16 }}>
        <Button type="text" icon={<FilterOutlined />} size="middle">筛选</Button>
      </div>
      <Table
        ref={tableRef}
        columns={columns}
        dataSource={tableData}
        rowKey="key"
        pagination={false}
        scroll={{ x: dates.length * 50 + 280, y: 400 }}
        bordered
        size="small"
        style={{ backgroundColor: '#fff' }}
        rowStyle={{ height: 35 }}
      />
    </div>
  )
}

export default RoomTypeRoomStatus

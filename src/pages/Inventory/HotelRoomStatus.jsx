import React, { useState, useRef, useEffect } from 'react'
import { Button, Modal, Form, Input, message } from 'antd'
import { LeftOutlined, RightOutlined } from '@ant-design/icons'

const HotelRoomStatus = () => {
  const [selectedMonth, setSelectedMonth] = useState('2025-12')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [selectedDate, setSelectedDate] = useState(null)
  const [form] = Form.useForm()

  const dates = [
    { key: 'date_0', day: 31, dateStr: '12.31', dayOfWeek: '周三' },
    { key: 'date_1', day: 1, dateStr: '01.01', dayOfWeek: '周四' },
    { key: 'date_2', day: 2, dateStr: '01.02', dayOfWeek: '周五' },
    { key: 'date_3', day: 3, dateStr: '01.03', dayOfWeek: '周六' },
    { key: 'date_4', day: 4, dateStr: '01.04', dayOfWeek: '周日' },
    { key: 'date_5', day: 5, dateStr: '01.05', dayOfWeek: '周一' },
    { key: 'date_6', day: 6, dateStr: '01.06', dayOfWeek: '周二' },
    { key: 'date_7', day: 7, dateStr: '01.07', dayOfWeek: '周三' },
    { key: 'date_8', day: 8, dateStr: '01.08', dayOfWeek: '周四' },
    { key: 'date_9', day: 9, dateStr: '01.09', dayOfWeek: '周五' },
    { key: 'date_10', day: 10, dateStr: '01.10', dayOfWeek: '周六' },
    { key: 'date_11', day: 11, dateStr: '01.11', dayOfWeek: '周日' },
    { key: 'date_12', day: 12, dateStr: '01.12', dayOfWeek: '周一' },
    { key: 'date_13', day: 13, dateStr: '01.13', dayOfWeek: '周二' }
  ]

  const [roomStatusData, setRoomStatusData] = useState(() => {
    const data = {}
    dates.forEach(d => {
      data[d.key] = Math.random() > 0.2
    })
    return data
  })

  const getDaysInMonth = (monthStr) => {
    const [year, month] = monthStr.split('-').map(Number)
    return new Date(year, month, 0).getDate()
  }

  const getFirstDayOfMonth = (monthStr) => {
    const [year, month] = monthStr.split('-').map(Number)
    return new Date(year, month - 1, 1).getDay()
  }

  const daysInMonth = getDaysInMonth(selectedMonth)
  const firstDay = getFirstDayOfMonth(selectedMonth)

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

  const handleDateClick = (dateInfo) => {
    if (!dateInfo) return
    setSelectedDate(dateInfo)
    form.setFieldsValue({ open: roomStatusData[dateInfo.key] ?? true })
    setIsModalVisible(true)
  }

  const handleSubmit = () => {
    form.validateFields().then(values => {
      setRoomStatusData(prev => ({
        ...prev,
        [selectedDate.key]: values.open
      }))
      message.success(`${selectedDate.dateStr} 房态已更新为 ${values.open ? '开' : '关'}`)
      setIsModalVisible(false)
    })
  }

  const renderCalendar = () => {
    const dayNames = ['日', '一', '二', '三', '四', '五', '六']

    return (
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            {dayNames.map(day => (
              <th key={day} style={{ 
                padding: '8px', 
                border: '1px solid #d9d9d9', 
                backgroundColor: '#fafafa',
                textAlign: 'center'
              }}>
                {day}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {(() => {
            const rows = []
            let cells = []
            
            for (let i = 0; i < firstDay; i++) {
              cells.push(<td key={`empty-${i}`} style={{ border: '1px solid #d9d9d9', minHeight: 60 }}></td>)
            }
            
            for (let i = 1; i <= daysInMonth; i++) {
              const findDate = dates.find(d => d.day === i)
              const isOpen = roomStatusData[findDate?.key] ?? true
              const dayOfWeek = new Date(selectedMonth.split('-')[0], selectedMonth.split('-')[1] - 1, i).getDay()
              const isWeekend = dayOfWeek === 0 || dayOfWeek === 6

              cells.push(
                <td 
                  key={i} 
                  style={{ 
                    border: '1px solid #d9d9d9', 
                    padding: 8, 
                    cursor: 'pointer',
                    backgroundColor: isWeekend ? '#fffbf0' : '#fff',
                    textAlign: 'center'
                  }}
                  onClick={() => handleDateClick(findDate)}
                >
                  <div style={{ fontWeight: 500, marginBottom: 4 }}>{i}</div>
                  <div style={{ 
                    color: isOpen ? '#52c41a' : '#ff4d4f',
                    fontSize: 12
                  }}>
                    {isOpen ? '开' : '关'}
                  </div>
                </td>
              )

              if ((firstDay + i) % 7 === 0) {
                rows.push(<tr key={`row-${rows.length}`}>{cells}</tr>)
                cells = []
              }
            }

            if (cells.length > 0) {
              while (cells.length < 7) {
                cells.push(<td key={`empty-end-${cells.length}`} style={{ border: '1px solid #d9d9d9' }}></td>)
              }
              rows.push(<tr key={`row-${rows.length}`}>{cells}</tr>)
            }

            return rows
          })()}
        </tbody>
      </table>
    )
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 16, gap: 16 }}>
        <Button icon={<LeftOutlined />} onClick={handlePrevMonth}>上月</Button>
        <span style={{ fontSize: 16, fontWeight: 500, minWidth: 80, textAlign: 'center' }}>
          {selectedMonth}月
        </span>
        <Button icon={<RightOutlined />} onClick={handleNextMonth}>下月</Button>
      </div>

      {renderCalendar()}

      <Modal
        title={`维护 ${selectedDate?.dateStr} 房态`}
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={() => setIsModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={300}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="open"
            label="房态"
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default HotelRoomStatus
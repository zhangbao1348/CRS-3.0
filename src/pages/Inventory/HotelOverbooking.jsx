import React, { useState } from 'react'
import { Table, Select, Button, Modal, Form, Input, DatePicker, message } from 'antd'
import { FilterOutlined, LeftOutlined, RightOutlined, EditOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { RangePicker } = DatePicker

const { Option } = Select

const HotelOverbooking = () => {
  const [selectedMonth, setSelectedMonth] = useState('2025-12')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [selectedDate, setSelectedDate] = useState(null)
  const [form] = Form.useForm()

  const generateDates = (month) => {
    const dates = []
    const [year, mon] = month.split('-').map(Number)
    const daysInMonth = new Date(year, mon, 0).getDate()
    
    for (let i = 1; i <= daysInMonth; i++) {
      const date = new Date(year, mon - 1, i)
      const dayOfWeek = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()]
      dates.push({
        date: date,
        dateStr: `${mon}.${i.toString().padStart(2, '0')}`,
        dayOfWeek: dayOfWeek,
        day: i,
        key: `date_${year}-${mon}-${i.toString().padStart(2, '0')}`
      })
    }
    return dates
  }

  const dates = generateDates(selectedMonth)

  const overbookData = {}
  dates.forEach(date => {
    overbookData[date.key] = Math.floor(Math.random() * 5)
  })

  const getDaysInMonth = (month) => {
    const [year, mon] = month.split('-').map(Number)
    return new Date(year, mon, 0).getDate()
  }

  const getFirstDayOfMonth = (month) => {
    const [year, mon] = month.split('-').map(Number)
    return new Date(year, mon - 1, 1).getDay()
  }

  const daysInMonth = getDaysInMonth(selectedMonth)
  const firstDay = getFirstDayOfMonth(selectedMonth)

  const handlePrevMonth = () => {
    const [year, month] = selectedMonth.split('-').map(Number)
    let newYear = year
    let newMonth = month - 1
    if (newMonth < 1) {
      newMonth = 12
      newYear -= 1
    }
    setSelectedMonth(`${newYear}-${newMonth.toString().padStart(2, '0')}`)
  }

  const handleNextMonth = () => {
    const [year, month] = selectedMonth.split('-').map(Number)
    let newYear = year
    let newMonth = month + 1
    if (newMonth > 12) {
      newMonth = 1
      newYear += 1
    }
    setSelectedMonth(`${newYear}-${newMonth.toString().padStart(2, '0')}`)
  }

  const handleDateClick = (date) => {
    setSelectedDate(date)
    form.setFieldsValue({
      overbookCount: overbookData[date.key] || 0
    })
    setIsModalVisible(true)
  }

  const handleSubmit = () => {
    form.validateFields().then(values => {
      if (selectedDate) {
        overbookData[selectedDate.key] = values.overbookCount
        message.success(`${selectedDate.dateStr} 超预订数量已更新为 ${values.overbookCount}`)
      } else {
        message.success(`批量修改成功：${values.dateRange[0].format('MM.DD')}-${values.dateRange[1].format('MM.DD')} 超预订已更新为 ${values.overbookCount}`)
      }
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
              const overbookCount = overbookData[findDate?.key] || 0
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
                    color: '#000',
                    fontSize: 12
                  }}>
                    {overbookCount > 0 ? `超预订: ${overbookCount}` : '超预订: 0'}
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
        <Button 
          icon={<EditOutlined />} 
          style={{ marginLeft: 24 }}
          onClick={() => {
            setSelectedDate(null)
            form.setFieldsValue({ overbookCount: 0 })
            setIsModalVisible(true)
          }}
        >
          批量修改
        </Button>
      </div>

      {renderCalendar()}

      <Modal
        title={selectedDate 
          ? `维护 ${selectedDate.dateStr} 超预订数量` 
          : '批量修改酒店超预订'}
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={() => setIsModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={500}
      >
        <Form form={form} layout="vertical">
          {!selectedDate && (
            <Form.Item
              name="dateRange"
              label="选择日期范围"
              rules={[{ required: true, message: '请选择日期范围' }]}
            >
              <RangePicker style={{ width: '100%' }} />
            </Form.Item>
          )}
          <Form.Item
            name="overbookCount"
            label="超预订数量"
            rules={[{ required: true, message: '请输入超预订数量' }]}
          >
            <Input type="number" placeholder="请输入超预订数量" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default HotelOverbooking
import React, { useState } from 'react'
import { Table, Select, Button, Modal, Form, Input, DatePicker, message, Row, Col, Card, Tabs } from 'antd'
import { FilterOutlined, LeftOutlined, RightOutlined, EditOutlined, PlusOutlined, HomeOutlined, TagOutlined, LinkOutlined, DollarOutlined, GlobalOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { RangePicker } = DatePicker
const { Option } = Select
const { TabPane } = Tabs

const BookingControl = () => {
  const [selectedMonth, setSelectedMonth] = useState('2025-12')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [selectedDate, setSelectedDate] = useState(null)
  const [form] = Form.useForm()
  const [activeTab, setActiveTab] = useState('hotel')
  const [selectedHotel, setSelectedHotel] = useState(undefined)
  const [selectedRateCode, setSelectedRateCode] = useState(undefined)
  const [selectedChannel, setSelectedChannel] = useState(undefined)
  const [selectedRateCategory, setSelectedRateCategory] = useState(undefined)
  const [selectedMarket, setSelectedMarket] = useState(undefined)

  const isSelectionValid = () => {
    if (activeTab === 'hotel') return true
    if (activeTab === 'rate' && selectedRateCode) return true
    if (activeTab === 'channel' && selectedChannel) return true
    if (activeTab === 'rateCategory' && selectedRateCategory) return true
    if (activeTab === 'market' && selectedMarket) return true
    return false
  }

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

  const controlData = {}
  dates.forEach(date => {
    controlData[date.key] = {
      cancellationRule: ['免费取消', '限时取消', '不可取消'][Math.floor(Math.random() * 3)],
      advanceBooking: Math.floor(Math.random() * 14) + 1,
      minStay: Math.floor(Math.random() * 3) + 1,
      maxStay: Math.floor(Math.random() * 10) + 5
    }
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
    const data = controlData[date.key] || {}
    form.setFieldsValue({
      cancellationRule: data.cancellationRule || '免费取消',
      advanceBooking: data.advanceBooking || 1,
      minStay: data.minStay || 1,
      maxStay: data.maxStay || 30
    })
    setIsModalVisible(true)
  }

  const handleSubmit = () => {
    form.validateFields().then(values => {
      if (selectedDate) {
        controlData[selectedDate.key] = values
        message.success(`${selectedDate.dateStr} 预订控制已更新`)
      } else {
        message.success('批量修改成功')
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
              cells.push(<td key={`empty-${i}`} style={{ border: '1px solid #d9d9d9', minHeight: 80 }}></td>)
            }
            
            for (let i = 1; i <= daysInMonth; i++) {
              const findDate = dates.find(d => d.day === i)
              const data = controlData[findDate?.key] || {}
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
                    textAlign: 'center',
                    minHeight: 100
                  }}
                  onClick={() => handleDateClick(findDate)}
                >
                  <div style={{ fontWeight: 500, marginBottom: 4 }}>{i}</div>
                  <div style={{ fontSize: 11, lineHeight: 1.6 }}>
                    <div style={{ color: '#1890ff', marginBottom: 2 }}>
                      {data.cancellationRule || '-'}
                    </div>
                    <div style={{ color: '#52c41a', marginBottom: 2 }}>
                      提前: {data.advanceBooking || 0}天
                    </div>
                    <div style={{ color: '#faad14' }}>
                      连住: {data.minStay || 1}-{data.maxStay || 30}晚
                    </div>
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
    <div className="fade-in">
      <h1 className="page-title">预订控制</h1>
      
      <Card style={{ marginBottom: 16 }}>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="酒店预订控制" key="hotel" icon={<HomeOutlined />} />
          <TabPane tab="房价预订控制" key="rate" icon={<TagOutlined />} />
          <TabPane tab="渠道预订控制" key="channel" icon={<LinkOutlined />} />
          <TabPane tab="房价大类预订控制" key="rateCategory" icon={<DollarOutlined />} />
          <TabPane tab="市场预订控制" key="market" icon={<GlobalOutlined />} />
        </Tabs>
      </Card>

      <Card>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          {activeTab === 'rate' && (
            <Col span={8}>
              <Select
                placeholder="请选择房价码"
                style={{ width: '100%' }}
                value={selectedRateCode}
                onChange={setSelectedRateCode}
              >
                <Option value="RACK">牌价（RACK）</Option>
                <Option value="WEB">网络价（WEB）</Option>
                <Option value="CORP">企业价（CORP）</Option>
                <Option value="MEMBER">会员价（MEMBER）</Option>
                <Option value="PKG">套餐价（PKG）</Option>
              </Select>
            </Col>
          )}
          {activeTab === 'channel' && (
            <Col span={8}>
              <Select
                placeholder="请选择渠道"
                style={{ width: '100%' }}
                value={selectedChannel}
                onChange={setSelectedChannel}
              >
                <Option value="ctrip">携程</Option>
                <Option value="meituan">美团</Option>
                <Option value="fliggy">飞猪</Option>
                <Option value="qunar">去哪儿</Option>
                <Option value="elong">艺龙</Option>
                <Option value="direct">直连</Option>
              </Select>
            </Col>
          )}
          {activeTab === 'rateCategory' && (
            <Col span={8}>
              <Select
                placeholder="请选择房价大类"
                style={{ width: '100%' }}
                value={selectedRateCategory}
                onChange={setSelectedRateCategory}
              >
                <Option value="public">公开价</Option>
                <Option value="member">会员价</Option>
                <Option value="corporate">协议价</Option>
                <Option value="promotion">促销价</Option>
              </Select>
            </Col>
          )}
          {activeTab === 'market' && (
            <Col span={8}>
              <Select
                placeholder="请选择市场"
                style={{ width: '100%' }}
                value={selectedMarket}
                onChange={setSelectedMarket}
              >
                <Option value="domestic">国内市场</Option>
                <Option value="overseas">海外市场</Option>
                <Option value="corporate">企业市场</Option>
                <Option value="leisure">休闲市场</Option>
              </Select>
            </Col>
          )}
        </Row>
        
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 16, gap: 16 }}>
          <Button icon={<LeftOutlined />} onClick={handlePrevMonth}>上月</Button>
          <span style={{ fontSize: 16, fontWeight: 500, minWidth: 80, textAlign: 'center' }}>
            {selectedMonth}月
          </span>
          <Button icon={<RightOutlined />} onClick={handleNextMonth}>下月</Button>
          <Button 
            icon={<EditOutlined />} 
            style={{ marginLeft: 24 }}
            disabled={!isSelectionValid()}
            onClick={() => {
              setSelectedDate(null)
              form.setFieldsValue({
                cancellationRule: '免费取消',
                advanceBooking: 1,
                minStay: 1,
                maxStay: 30
              })
              setIsModalVisible(true)
            }}
          >
            批量修改
          </Button>
        </div>

        {isSelectionValid() ? (
          renderCalendar()
        ) : (
          <div style={{ 
            textAlign: 'center', 
            padding: '60px 20px', 
            color: '#999',
            fontSize: '16px'
          }}>
            请先选择{activeTab === 'rate' ? '房价码' : activeTab === 'channel' ? '渠道' : activeTab === 'rateCategory' ? '房价大类' : '市场'}
          </div>
        )}

        <Modal
          title={selectedDate 
            ? `维护 ${selectedDate.dateStr} 预订控制` 
            : '批量修改预订控制'}
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
              name="cancellationRule"
              label="取消规则名称"
              rules={[{ required: true, message: '请选择取消规则' }]}
            >
              <Select>
                <Option value="免费取消">免费取消</Option>
                <Option value="限时取消">限时取消</Option>
                <Option value="不可取消">不可取消</Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="advanceBooking"
              label="提前预订天数"
              rules={[{ required: true, message: '请输入提前预订天数' }]}
            >
              <Input type="number" min={0} placeholder="请输入提前预订天数" />
            </Form.Item>
            <Form.Item
              name="minStay"
              label="最小连住天数"
              rules={[{ required: true, message: '请输入最小连住天数' }]}
            >
              <Input type="number" min={1} placeholder="请输入最小连住天数" />
            </Form.Item>
            <Form.Item
              name="maxStay"
              label="最大连住天数"
              rules={[{ required: true, message: '请输入最大连住天数' }]}
            >
              <Input type="number" min={1} placeholder="请输入最大连住天数" />
            </Form.Item>
          </Form>
        </Modal>
      </Card>
    </div>
  )
}

export default BookingControl

import React, { useState } from 'react'
import { Form, Tabs, Input, Select, Radio, Button, message, Row, Col, InputNumber, Modal, DatePicker } from 'antd'
import { SaveOutlined, LeftOutlined, EditOutlined, PlusOutlined, LeftOutlined as LeftIcon, RightOutlined as RightIcon } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import dayjs from 'dayjs'

const { TabPane } = Tabs
const { Option } = Select
const { Group: RadioGroup } = Radio
const { RangePicker } = DatePicker

const AddPackage = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('1')
  const [quantityType, setQuantityType] = useState('')
  const [priceType, setPriceType] = useState('fixed')
  const [selectedMonth, setSelectedMonth] = useState('2025-12')
  const [isPriceModalVisible, setIsPriceModalVisible] = useState(false)
  const [selectedDate, setSelectedDate] = useState(null)
  const [priceForm] = Form.useForm()
  const [isBatchModalVisible, setIsBatchModalVisible] = useState(false)
  const [batchForm] = Form.useForm()
  const navigate = useNavigate()
  
  // 包价类型选项
  const packageTypes = [
    { value: '早餐', label: '早餐' },
    { value: '午餐', label: '午餐' },
    { value: '晚餐', label: '晚餐' },
    { value: '下午茶', label: '下午茶' },
    { value: '门票', label: '门票' },
    { value: '其他', label: '其他' },
    { value: '免费增早', label: '免费增早' },
    { value: '延时退房', label: '延时退房' },
    { value: '提前入住', label: '提前入住' }
  ]
  
  // 发放频率选项
  const frequencyOptions = [
    { value: '每天1次', label: '每天1次' },
    { value: '到达当天发放一次', label: '到达当天发放一次' },
    { value: '最后一天发放一次', label: '最后一天发放一次' },
    { value: '除最后一天每天一次', label: '除最后一天每天一次' }
  ]
  
  // 生成日期
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
  
  // 价格数据
  const [priceData, setPriceData] = useState({})
  
  // 初始化价格数据
  React.useEffect(() => {
    const data = {}
    dates.forEach(date => {
      data[date.key] = {
        price: Math.floor(Math.random() * 500) + 100
      }
    })
    setPriceData(data)
  }, [selectedMonth])
  
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
    const data = priceData[date.key] || {}
    priceForm.setFieldsValue({
      price: data.price || 0
    })
    setIsPriceModalVisible(true)
  }
  
  const handlePriceSubmit = () => {
    priceForm.validateFields().then(values => {
      if (selectedDate) {
        setPriceData(prev => ({
          ...prev,
          [selectedDate.key]: {
            ...prev[selectedDate.key],
            price: values.price
          }
        }))
        message.success(`${selectedDate.dateStr} 价格已更新`)
      }
      setIsPriceModalVisible(false)
    })
  }
  
  const handleBatchSubmit = () => {
    batchForm.validateFields().then(values => {
      const [startDate, endDate] = values.dateRange
      const newPriceData = { ...priceData }
      
      let currentDate = dayjs(startDate)
      const end = dayjs(endDate)
      
      while (currentDate.isBefore(end) || currentDate.isSame(end)) {
        const dateKey = `date_${currentDate.format('YYYY-M-D')}`
        const [year, month, day] = currentDate.format('YYYY-M-D').split('-').map(Number)
        const fullDateKey = `date_${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`
        
        newPriceData[fullDateKey] = {
          ...newPriceData[fullDateKey],
          price: values.price
        }
        
        currentDate = currentDate.add(1, 'day')
      }
      
      setPriceData(newPriceData)
      message.success('批量修改成功')
      setIsBatchModalVisible(false)
      batchForm.resetFields()
    })
  }
  
  // 处理表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setLoading(true)
      
      // 构建包价数据
      const packageData = {
        code: values.code,
        name: values.name,
        description: values.description || '',
        type: values.type,
        quantityType: values.quantityType,
        fixedQuantity: values.quantityType === 'fixed' ? values.fixedQuantity : null,
        frequency: values.frequency,
        priceType: values.priceType,
        fixedPrice: values.priceType === 'fixed' ? values.fixedPrice : null,
        priceData: priceData,
        taxIncluded: values.taxIncluded || false,
        status: 'active'
      }
      
      // 创建包价
      const response = await axios.post('/api/packages', packageData)
      message.success('包价创建成功')
      
      // 跳转到包价列表页面
      navigate('/rate-management/package-setting')
    } catch (error) {
      console.error('保存包价失败:', error)
      if (error.response && error.response.data) {
        message.error(error.response.data)
      } else {
        message.error('保存失败，请稍后重试')
      }
    } finally {
      setLoading(false)
    }
  }
  
  // 处理返回
  const handleBack = () => {
    navigate('/rate-management/package-setting')
  }
  
  // 处理TAB切换
  const handleTabChange = (key) => {
    setActiveTab(key)
  }
  
  // 渲染日历
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
              const data = priceData[findDate?.key] || {}
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
                    minHeight: 80
                  }}
                  onClick={() => handleDateClick(findDate)}
                >
                  <div style={{ fontWeight: 500, marginBottom: 4 }}>{i}</div>
                  <div style={{ fontSize: 14, color: '#1890ff', fontWeight: 500 }}>
                    ¥{data.price || 0}
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
      <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center' }}>
        <Button
          icon={<LeftOutlined />}
          onClick={handleBack}
          style={{ marginRight: 16 }}
        >
          返回
        </Button>
        <h1 className="page-title">
          新增包价
        </h1>
      </div>
      
      <div style={{ maxWidth: 800, margin: '0 auto' }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Tabs activeKey={activeTab} onChange={handleTabChange}>
            <TabPane tab="基础信息" key="1">
              <Row gutter={[16, 16]}>
                <Col span={12}>
                  <Form.Item
                    name="code"
                    label="包价代码"
                    rules={[
                      { required: true, message: '请输入包价代码' },
                      { pattern: /^[A-Za-z0-9_]+$/, message: '包价代码只能包含英文字母、数字和下划线' }
                    ]}
                  >
                    <Input placeholder="请输入包价代码" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="name"
                    label="包价名称"
                    rules={[{ required: true, message: '请输入包价名称' }]}
                  >
                    <Input placeholder="请输入包价名称" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="type"
                    label="包价类型"
                    rules={[{ required: true, message: '请选择包价类型' }]}
                  >
                    <Select placeholder="请选择包价类型">
                      {packageTypes.map(item => (
                        <Option key={item.value} value={item.value}>{item.label}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="frequency"
                    label="发放频率"
                    rules={[{ required: true, message: '请选择发放频率' }]}
                  >
                    <Select placeholder="请选择发放频率">
                      {frequencyOptions.map(item => (
                        <Option key={item.value} value={item.value}>{item.label}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="quantityType"
                    label="计数方式"
                    rules={[{ required: true, message: '请选择计数方式' }]}
                  >
                    <Select 
                      placeholder="请选择计数方式"
                      onChange={(value) => setQuantityType(value)}
                    >
                      <Option value="per_order">按订单</Option>
                      <Option value="per_room">按房间</Option>
                      <Option value="per_person">按人数</Option>
                      <Option value="per_adult">按成人数</Option>
                      <Option value="per_child">按儿童数</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  {quantityType === 'per_order' && (
                    <Form.Item
                      name="perOrderQuantity"
                      label="每订单份数"
                      rules={[{ required: true, message: '请输入每订单份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每订单份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_room' && (
                    <Form.Item
                      name="perRoomQuantity"
                      label="每房间份数"
                      rules={[{ required: true, message: '请输入每房间份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每房间份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_person' && (
                    <Form.Item
                      name="perPersonQuantity"
                      label="每人份数"
                      rules={[{ required: true, message: '请输入每人份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每人份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_adult' && (
                    <Form.Item
                      name="perAdultQuantity"
                      label="每成人份数"
                      rules={[{ required: true, message: '请输入每成人份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每成人份数" />
                    </Form.Item>
                  )}
                  {quantityType === 'per_child' && (
                    <Form.Item
                      name="perChildQuantity"
                      label="每儿童份数"
                      rules={[{ required: true, message: '请输入每儿童份数' }]}
                    >
                      <Input type="number" min={1} placeholder="请输入每儿童份数" />
                    </Form.Item>
                  )}
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="priceType"
                    label="计价方式"
                    rules={[{ required: true, message: '请选择计价方式' }]}
                  >
                    <RadioGroup onChange={(e) => setPriceType(e.target.value)}>
                      <Radio value="fixed">固定价格</Radio>
                      <Radio value="date">按日期设置价格</Radio>
                    </RadioGroup>
                  </Form.Item>
                </Col>
                {priceType === 'fixed' && (
                  <Col span={12}>
                    <Form.Item
                      name="fixedPrice"
                      label="价格"
                      dependencies={['priceType']}
                      rules={[
                        {
                          required: ({ priceType }) => priceType === 'fixed',
                          message: '请输入价格'
                        }
                      ]}
                    >
                      <InputNumber
                        style={{ width: '100%' }}
                        placeholder="请输入价格"
                        min={0}
                        step={0.01}
                        prefix="¥"
                      />
                    </Form.Item>
                  </Col>
                )}
              </Row>
              
              <Form.Item
                name="description"
                label="描述"
              >
                <Input.TextArea rows={4} placeholder="请输入包价描述" />
              </Form.Item>
            </TabPane>
            
            {priceType === 'date' && (
              <TabPane tab="设置价格" key="2">
                <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 16 }}>
                  <Button icon={<LeftIcon />} onClick={handlePrevMonth}>上月</Button>
                  <span style={{ fontSize: 16, fontWeight: 500, minWidth: 80, textAlign: 'center' }}>
                    {selectedMonth.replace('-', '年')}月
                  </span>
                  <Button icon={<RightIcon />} onClick={handleNextMonth}>下月</Button>
                  <Button 
                    type="primary" 
                    icon={<PlusOutlined />}
                    style={{ marginLeft: 24 }}
                    onClick={() => setIsBatchModalVisible(true)}
                  >
                    批量修改
                  </Button>
                </div>
                
                {renderCalendar()}
              </TabPane>
            )}
          </Tabs>
          
          <div style={{ marginTop: 32, padding: '20px', backgroundColor: '#f5f5f5', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                icon={<LeftOutlined />}
                onClick={handleBack}
                style={{ marginRight: 12 }}
              >
                返回
              </Button>
              <Button
                type="primary"
                icon={<SaveOutlined />}
                loading={loading}
                htmlType="submit"
                size="large"
              >
                保存
              </Button>
            </div>
          </div>
        </Form>
      </div>
      
      {/* 单个日期修改价格 */}
      <Modal
        title={selectedDate ? `${selectedDate.dateStr} 设置价格` : '设置价格'}
        open={isPriceModalVisible}
        onOk={handlePriceSubmit}
        onCancel={() => setIsPriceModalVisible(false)}
        okText="确认"
        cancelText="取消"
      >
        <Form form={priceForm} layout="vertical">
          <Form.Item
            name="price"
            label="价格"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="请输入价格"
              min={0}
              step={0.01}
              prefix="¥"
            />
          </Form.Item>
        </Form>
      </Modal>
      
      {/* 批量修改价格 */}
      <Modal
        title="批量修改价格"
        open={isBatchModalVisible}
        onOk={handleBatchSubmit}
        onCancel={() => setIsBatchModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={500}
      >
        <Form form={batchForm} layout="vertical">
          <Form.Item
            name="dateRange"
            label="日期范围"
            rules={[{ required: true, message: '请选择日期范围' }]}
          >
            <RangePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="price"
            label="价格"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="请输入价格"
              min={0}
              step={0.01}
              prefix="¥"
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default AddPackage

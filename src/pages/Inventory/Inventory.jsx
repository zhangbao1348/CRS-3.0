import React, { useState } from 'react'
import { Table, Select, Button, Space, Modal, Form, message, Tabs } from 'antd'
import { 
  SyncOutlined, 
  PlusOutlined, 
  MinusOutlined, 
  ExportOutlined,
  ImportOutlined,
  CalendarOutlined,
  ReloadOutlined,
  FilterOutlined
} from '@ant-design/icons'

const { Option } = Select

// 模拟房控数据
const mockRoomTypes = [
  { code: '1KGS', name: '标准大床房', total: 40 },
  { code: '1KGP', name: '高级大床房', total: 30 },
  { code: '1KGH', name: '探索大床房', total: 25 },
  { code: '2TGH', name: '探索双床房', total: 35 },
  { code: 'XHGS', name: '好眠大床房', total: 20 },
  { code: '2FGH', name: '探索家庭房', total: 15 },
  { code: '1SGH', name: '探索大床套间', total: 10 },
  { code: '2FZT', name: '海底小纵队主题亲子房', total: 8 },
  { code: '2FYT', name: 'D.Buck小黄鸭主题亲子房', total: 8 }
]

// 模拟价格计划数据
const mockPricePlans = [
  { code: 'OTA', name: 'OTA价' },
  { code: 'INTL_OTA', name: '国际OTA价' },
  { code: 'MEMBER', name: '会员价' },
  { code: 'RACK', name: '门市价' }
]

// 模拟日期数据
const generateDates = () => {
  const dates = []
  const today = new Date('2025-12-31') // 从指定日期开始
  
  // 生成14天日期
  for (let i = 0; i < 14; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    
    // 格式化日期
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const dayOfMonth = date.getDate().toString().padStart(2, '0')
    const dayOfWeek = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()]
    const dateStr = `${month}.${dayOfMonth}`
    
    dates.push({
      date: date,
      dayOfMonth: dayOfMonth,
      dayOfWeek: dayOfWeek,
      dateStr: dateStr,
      key: `date_${i}`
    })
  }
  
  return dates
}

// 生成模拟库存数据，更接近图片中的数据分布
const generateInventoryData = (roomTypes, dates) => {
  const data = []
  
  roomTypes.forEach(roomType => {
    const row = {
      roomTypeCode: roomType.code,
      roomTypeName: roomType.name,
      total: roomType.total
    }
    
    // 为每个日期生成可用和已订数据
    dates.forEach((date, dateIndex) => {
      let available, booked;
      
      // 模拟不同日期的预订情况
      if (dateIndex < 2) {
        // 前两个日期，生成较低的可售数量
        available = Math.floor(Math.random() * 5) + 1
        booked = Math.max(0, Math.floor(Math.random() * 3))
      } else {
        // 后续日期，生成较高的可售数量
        available = Math.max(5, Math.floor(roomType.total * 0.8 + Math.random() * roomType.total * 0.3))
        booked = dateIndex % 3 === 0 ? Math.floor(Math.random() * 5) : 0
      }
      
      row[date.key] = {
        available: available,
        booked: booked
      }
    })
    
    data.push(row)
  })
  
  return data
}

const Inventory = () => {
  // 状态管理
  const [dates] = useState(generateDates())
  const [roomTypes] = useState(mockRoomTypes)
  const [inventoryData, setInventoryData] = useState(generateInventoryData(mockRoomTypes, generateDates()))
  const [selectedRoomType, setSelectedRoomType] = useState('全部房型')
  const [selectedPricePlan, setSelectedPricePlan] = useState('全部价格计划')
  const [selectedControlType, setSelectedControlType] = useState('携程')
  const [selectedInventoryType, setSelectedInventoryType] = useState('库存')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [currentRow, setCurrentRow] = useState(null)
  const [currentDate, setCurrentDate] = useState(null)
  const [form] = Form.useForm()

  // 筛选数据
  const filteredData = selectedRoomType === '全部房型' 
    ? inventoryData 
    : inventoryData.filter(item => item.roomTypeCode === selectedRoomType)

  // 表格列配置
  const columns = [
    {
      title: (
        <div style={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '8px 0' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: 'center' }}>
            <Button type="text" icon={<MinusOutlined />} size="small">
              上月
            </Button>
            <span style={{ fontSize: 12 }}>2025-12-31</span>
            <Button type="text" icon={<PlusOutlined />} size="small">
              下月
            </Button>
          </div>
        </div>
      ),
      key: 'roomType',
      width: 160,
      render: (_, record) => (
        <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
          <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
          <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
          <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
        </div>
      )
    },
    ...dates.map(date => ({
      title: (
        <div style={{ textAlign: 'center', fontSize: 12, padding: '2px 0' }}>
          <div>{date.dateStr}</div>
          <div style={{ fontSize: 10, color: '#999' }}>{date.dayOfWeek}</div>
        </div>
      ),
      dataIndex: date.key,
      key: date.key,
      width: 60,
      render: (data, row) => {
        return (
          <div 
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              padding: '4px 0',
              backgroundColor: '#fff',
              border: '1px solid #e8e8e8',
              fontSize: 11,
              height: 45
            }}
            onClick={() => handleUpdateInventory(row, date.key)}
          >
            <div style={{ color: '#1890ff', lineHeight: '18px' }}>可售 {data.available}</div>
            <div style={{ color: '#faad14', lineHeight: '18px' }}>已订 {data.booked === 0 ? '-' : data.booked}</div>
          </div>
        )
      }
    }))
  ]

  // 更新库存
  const handleUpdateInventory = (row, dateKey) => {
    setCurrentRow(row)
    setCurrentDate(dateKey)
    form.setFieldsValue({
      available: row[dateKey].available,
      booked: row[dateKey].booked
    })
    setIsModalVisible(true)
  }

  // 提交库存更新
  const handleSubmit = () => {
    form.validateFields()
      .then(values => {
        const { available, booked } = values
        const total = available + booked
        
        // 更新数据
        const updatedData = inventoryData.map(row => {
          if (row.roomTypeCode === currentRow.roomTypeCode) {
            return {
              ...row,
              [currentDate]: {
                available,
                booked
              }
            }
          }
          return row
        })
        
        setInventoryData(updatedData)
        setIsModalVisible(false)
        message.success('库存更新成功')
      })
      .catch(errorInfo => {
        console.log('表单验证失败:', errorInfo)
      })
  }

  // 批量更新库存
  const handleBatchUpdate = () => {
    message.info('批量更新功能开发中...')
  }

  // 刷新数据
  const handleRefresh = () => {
    message.success('数据已刷新')
  }

  // 导出数据
  const handleExport = () => {
    message.info('数据导出功能开发中...')
  }

  // 导入数据
  const handleImport = () => {
    message.info('数据导入功能开发中...')
  }

  return (
    <div className="fade-in">
      {/* 标题区域 */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h1 className="page-title">
          <CalendarOutlined /> 房控日历
        </h1>
        <Button type="primary" onClick={handleBatchUpdate}>
          批量更新
        </Button>
      </div>

      {/* 标签页 */}
      <Tabs 
        defaultActiveKey="1" 
        style={{ marginBottom: 16 }}
        items={[
          {
            key: '1',
            label: '主要房控日历',
            children: (
              <>
                {/* 筛选区域 */}
                <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 16 }}>
                  <Select 
                    value={selectedControlType}
                    style={{ width: 120 }} 
                    size="middle"
                    onChange={setSelectedControlType}
                  >
                    <Option value="携程">携程</Option>
                    <Option value="美团">美团</Option>
                    <Option value="艺龙">艺龙</Option>
                    <Option value="飞猪">飞猪</Option>
                    <Option value="其他">其他</Option>
                  </Select>
                  <Select 
                    value={selectedInventoryType}
                    style={{ width: 120 }} 
                    size="middle"
                    onChange={setSelectedInventoryType}
                  >
                    <Option value="库存">库存</Option>
                    <Option value="房价">房价</Option>
                    <Option value="开关房">开关房</Option>
                    <Option value="限制条件">限制条件</Option>
                  </Select>
                  <Select 
                    value={selectedRoomType}
                    style={{ width: 180 }} 
                    size="middle"
                    onChange={setSelectedRoomType}
                  >
                    <Option value="全部房型">全部房型</Option>
                    {roomTypes.map(roomType => (
                      <Option key={roomType.code} value={roomType.code}>
                        {roomType.name}
                      </Option>
                    ))}
                  </Select>
                  <Select 
                    value={selectedPricePlan}
                    style={{ width: 180 }} 
                    size="middle"
                    onChange={setSelectedPricePlan}
                  >
                    <Option value="全部价格计划">全部价格计划</Option>
                    {mockPricePlans.map(plan => (
                      <Option key={plan.code} value={plan.code}>
                        {plan.name}
                      </Option>
                    ))}
                  </Select>
                  <Button type="text" icon={<FilterOutlined />} size="middle">
                    筛选
                  </Button>
                  <div style={{ marginLeft: 'auto', display: 'flex', gap: 8 }}>
                    <Button type="text" icon={<SyncOutlined />} size="middle" onClick={handleRefresh}>
                      刷新
                    </Button>
                    <Button type="text" icon={<ImportOutlined />} size="middle" onClick={handleImport}>
                      导入
                    </Button>
                    <Button type="text" icon={<ExportOutlined />} size="middle" onClick={handleExport}>
                      导出
                    </Button>
                  </div>
                </div>



                {/* 日历表格 */}
                <Table
                  columns={columns}
                  dataSource={filteredData}
                  rowKey="roomTypeCode"
                  pagination={false}
                  scroll={{ x: 1500, y: 600 }}
                  bordered
                  size="small"
                  style={{ backgroundColor: '#fff' }}
                  rowStyle={{ height: 45 }}
                  headerStyle={{ backgroundColor: '#f5f5f5' }}
                />
              </>
            )
          },
          {
            key: '2',
            label: 'PMS房控日历',
            children: (
              <div style={{ textAlign: 'center', padding: '50px 0', color: '#999' }}>
                PMS房控日历功能开发中...
              </div>
            )
          }
        ]}
      />

      {/* 库存更新弹窗 */}
      <Modal
        title="更新库存"
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={() => setIsModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={400}
      >
        {currentRow && currentDate && (
          <div>
            <div style={{ marginBottom: 16, padding: '12px', backgroundColor: '#fafafa', borderRadius: 6 }}>
              <div style={{ fontWeight: 600, marginBottom: 4 }}>
                {currentRow.roomTypeName} ({currentRow.roomTypeCode})
              </div>
              <div style={{ fontSize: 14, color: '#8c8c8c' }}>
                总数量: {currentRow.total} 间
              </div>
            </div>
            <Form form={form} layout="vertical">
              <Form.Item
                name="available"
                label="可售数量"
                rules={[{ required: true, message: '请输入可售数量' }, { type: 'number', min: 0 }]}
              >
                <Input type="number" placeholder="请输入可售数量" />
              </Form.Item>
              <Form.Item
                name="booked"
                label="已订数量"
                rules={[{ required: true, message: '请输入已订数量' }, { type: 'number', min: 0 }]}
              >
                <Input type="number" placeholder="请输入已订数量" />
              </Form.Item>
            </Form>
          </div>
        )}
      </Modal>
    </div>
  )
}

export default Inventory
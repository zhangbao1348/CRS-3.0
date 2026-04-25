import React, { useState, useEffect } from 'react'
import { Table, Select, Button, Space, Modal, Form, message, Tabs, Input } from 'antd'
import { 
  SyncOutlined, 
  PlusOutlined, 
  MinusOutlined, 
  CalendarOutlined,
  ReloadOutlined,
  FilterOutlined
} from '@ant-design/icons'
import PMSInventoryCalendar from './PMSInventoryCalendar'
import HotelOverbooking from './HotelOverbooking'
import RoomTypeOverbooking from './RoomTypeOverbooking'
import PriceLevelInventory from './PriceLevelInventory'
import ChannelLevelInventory from './ChannelLevelInventory'
import MarketLevelInventory from './MarketLevelInventory'
import ChannelRoomTypeInventory from './ChannelRoomTypeInventory'
import RateCategoryLevelInventory from './RateCategoryLevelInventory'

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

// 模拟日期数据
const generateDates = () => {
  const dates = []
  const today = new Date('2025-12-31')
  
  for (let i = 0; i < 14; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    
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

// 生成模拟库存数据
const generateInventoryData = (roomTypes, dates, inventoryType = '库存') => {
  const data = []
  
  if (inventoryType === '房价') {
    const pricePlans = [
      { code: 'RACK', name: '牌价' },
      { code: 'OTA', name: 'OTA价' },
      { code: 'MEMBER', name: '会员价' },
      { code: 'WEEKEND', name: '周末价' }
    ]
    
    roomTypes.forEach(roomType => {
      pricePlans.forEach(price => {
        const row = {
          key: `${roomType.code}_${price.code}`,
          roomTypeCode: roomType.code,
          roomTypeName: roomType.name,
          priceCode: price.code,
          priceName: price.name,
          total: roomType.total
        }
        
        dates.forEach((date, dateIndex) => {
          const basePrice = Math.floor(Math.random() * 500) + 200
          row[date.key] = basePrice + (dateIndex % 3) * 50
        })
        
        data.push(row)
      })
    })
  } else if (inventoryType === '开关房') {
    const roomStatuses = [
      { key: 'available', label: '可售', color: '#1890ff' },
      { key: 'sold', label: '已售', color: '#faad14' },
      { key: 'status', label: '房态', color: '#52c41a' }
    ]
    
    roomTypes.forEach(roomType => {
      roomStatuses.forEach(status => {
        const row = {
          key: `${roomType.code}_${status.key}`,
          roomTypeCode: roomType.code,
          roomTypeName: roomType.name,
          inventoryType: status.key,
          inventoryLabel: status.label,
          inventoryColor: status.color,
          total: roomType.total
        }
        
        dates.forEach((date, dateIndex) => {
          if (status.key === 'available') {
            row[date.key] = Math.floor(Math.random() * 20) + 1
          } else if (status.key === 'sold') {
            row[date.key] = Math.floor(Math.random() * 10)
          } else {
            row[date.key] = Math.random() > 0.3 ? '开' : '关'
          }
        })
        
        data.push(row)
      })
    })
  } else {
    const inventoryTypes = [
      { key: 'available', label: '可售', color: '#1890ff' },
      { key: 'sold', label: '已售', color: '#faad14' }
    ]
    
    roomTypes.forEach(roomType => {
      inventoryTypes.forEach(invType => {
        const row = {
          key: `${roomType.code}_${invType.key}`,
          roomTypeCode: roomType.code,
          roomTypeName: roomType.name,
          inventoryType: invType.key,
          inventoryLabel: invType.label,
          inventoryColor: invType.color,
          total: roomType.total
        }
        
        dates.forEach((date, dateIndex) => {
          let value
          if (invType.key === 'available') {
            value = Math.floor(Math.random() * 20) + 1
          } else {
            value = Math.floor(Math.random() * 10)
          }
          row[date.key] = value
        })
        
        data.push(row)
      })
    })
  }
  
  return data
}

const Inventory = () => {
  const [dates] = useState(generateDates())
  const [roomTypes] = useState(mockRoomTypes)
  const [inventoryData, setInventoryData] = useState([])
  const [selectedRoomType, setSelectedRoomType] = useState('全部房型')
  const [selectedPricePlan, setSelectedPricePlan] = useState('全部价格计划')
  const [selectedControlType, setSelectedControlType] = useState('携程')
  const [selectedInventoryType, setSelectedInventoryType] = useState('库存')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [currentRow, setCurrentRow] = useState(null)
  const [currentDate, setCurrentDate] = useState(null)
  const [form] = Form.useForm()

  // 初始加载和库存类型变化时更新数据
  useEffect(() => {
    setInventoryData(generateInventoryData(mockRoomTypes, dates, selectedInventoryType))
  }, [selectedInventoryType])

  const filteredData = selectedRoomType === '全部房型' 
    ? inventoryData 
    : inventoryData.filter(item => item.roomTypeCode === selectedRoomType)

  const getColumns = () => {
    if (selectedInventoryType === '房价') {
      return [
        {
          title: (
            <div style={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '8px 0' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: 'center' }}>
                <Button type="text" icon={<MinusOutlined />} size="small">上月</Button>
                <span style={{ fontSize: 12 }}>2025-12</span>
                <Button type="text" icon={<PlusOutlined />} size="small">下月</Button>
              </div>
            </div>
          ),
          key: 'roomType',
          width: 200,
          fixed: 'left',
          render: (_, record, index) => ({
            children: (
              <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
                <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
                <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
                <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
              </div>
            ),
            props: { rowSpan: index % 4 === 0 ? 4 : 0 }
          })
        },
        {
          title: '房价码',
          dataIndex: 'priceName',
          key: 'priceCode',
          width: 80,
          fixed: 'left',
          render: (text) => (
            <div style={{ fontSize: 12, color: '#52c41a', fontWeight: 500 }}>{text}</div>
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
          render: (value) => (
            <div 
              style={{
                textAlign: 'center',
                padding: '4px 0',
                backgroundColor: '#fff',
                border: '1px solid #e8e8e8',
                fontSize: 11,
                color: '#52c41a',
                fontWeight: 500,
                cursor: 'pointer'
              }}
              onClick={() => handleUpdatePrice(_, date.key)}
            >
              ¥{value}
            </div>
          )
        }))
      ]
    } else if (selectedInventoryType === '开关房') {
      return [
        {
          title: (
            <div style={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '8px 0' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: 'center' }}>
                <Button type="text" icon={<MinusOutlined />} size="small">上月</Button>
                <span style={{ fontSize: 12 }}>2025-12</span>
                <Button type="text" icon={<PlusOutlined />} size="small">下月</Button>
              </div>
            </div>
          ),
          key: 'roomType',
          width: 200,
          fixed: 'left',
          render: (_, record, index) => ({
            children: (
              <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
                <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
                <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
                <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
              </div>
            ),
            props: { rowSpan: index % 3 === 0 ? 3 : 0 }
          })
        },
        {
          title: '库存类型',
          dataIndex: 'inventoryLabel',
          key: 'inventoryType',
          width: 80,
          fixed: 'left',
          render: (text, record) => (
            <div style={{ fontSize: 12, color: record.inventoryColor, fontWeight: 500 }}>{text}</div>
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
          width: 50,
          render: (value, record) => {
            const isStatusRow = record.inventoryType === 'status'
            const bgColor = isStatusRow 
              ? (value === '开' ? '#f6ffed' : '#fff1f0')
              : '#fff'
            
            return (
              <div 
                style={{
                  textAlign: 'center',
                  padding: '4px 0',
                  backgroundColor: bgColor,
                  border: '1px solid #e8e8e8',
                  fontSize: 11,
                  color: record.inventoryColor,
                  fontWeight: 500,
                  cursor: 'pointer'
                }}
                onClick={() => handleUpdateInventory(record, date.key)}
              >
                {value}
              </div>
            )
          }
        }))
      ]
    } else {
      return [
        {
          title: (
            <div style={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '8px 0' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: 'center' }}>
                <Button type="text" icon={<MinusOutlined />} size="small">上月</Button>
                <span style={{ fontSize: 12 }}>2025-12</span>
                <Button type="text" icon={<PlusOutlined />} size="small">下月</Button>
              </div>
            </div>
          ),
          key: 'roomType',
          width: 160,
          fixed: 'left',
          render: (_, record, index) => ({
            children: (
              <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
                <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
                <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
                <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
              </div>
            ),
            props: { rowSpan: index % 2 === 0 ? 2 : 0 }
          })
        },
        {
          title: '库存类型',
          dataIndex: 'inventoryLabel',
          key: 'inventoryType',
          width: 80,
          fixed: 'left',
          render: (text, record) => (
            <div style={{ fontSize: 12, color: record.inventoryColor, fontWeight: 500 }}>{text}</div>
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
          width: 50,
          render: (value, record) => (
            <div 
              style={{
                textAlign: 'center',
                padding: '4px 0',
                backgroundColor: '#fff',
                border: '1px solid #e8e8e8',
                fontSize: 11,
                color: record.inventoryColor,
                fontWeight: 500,
                cursor: 'pointer'
              }}
              onClick={() => handleUpdateInventory(record, date.key)}
            >
              {value}
            </div>
          )
        }))
      ]
    }
  }

  const columns = getColumns()

  const handleUpdateInventory = (row, dateKey) => {
    setCurrentRow(row)
    setCurrentDate(dateKey)
    form.setFieldsValue({ available: row[dateKey], booked: row[dateKey] })
    setIsModalVisible(true)
  }

  const handleUpdatePrice = (row, dateKey) => {
    setCurrentRow(row)
    setCurrentDate(dateKey)
    form.setFieldsValue({ price: row[dateKey] })
    setIsModalVisible(true)
  }

  const handleSubmit = () => {
    form.validateFields().then(values => {
      message.success(selectedInventoryType === '房价' ? '房价更新成功' : '库存更新成功')
      setIsModalVisible(false)
    })
  }

  const handleRefresh = () => message.success('数据已刷新')

  return (
    <div className="fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h1 className="page-title"><CalendarOutlined /> 房控日历</h1>
      </div>

      <Tabs defaultActiveKey="1" style={{ marginBottom: 16 }}
        items={[
          {
            key: '1',
            label: '主要房控日历',
            children: (
              <>
                <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 16 }}>
                  <Select value={selectedControlType} style={{ width: 120 }} size="middle" onChange={setSelectedControlType}>
                    <Option value="携程">携程</Option>
                    <Option value="美团">美团</Option>
                    <Option value="艺龙">艺龙</Option>
                    <Option value="飞猪">飞猪</Option>
                    <Option value="其他">其他</Option>
                  </Select>
                  <Select value={selectedInventoryType} style={{ width: 120 }} size="middle" onChange={setSelectedInventoryType}>
                    <Option value="库存">库存</Option>
                    <Option value="房价">房价</Option>
                    <Option value="开关房">开关房</Option>
                  </Select>
                  <Select value={selectedRoomType} style={{ width: 180 }} size="middle" onChange={setSelectedRoomType}>
                    <Option value="全部房型">全部房型</Option>
                    {roomTypes.map(rt => <Option key={rt.code} value={rt.code}>{rt.name}</Option>)}
                  </Select>
                  <Select value={selectedPricePlan} style={{ width: 180 }} size="middle" onChange={setSelectedPricePlan}>
                    <Option value="全部价格计划">全部价格计划</Option>
                    <Option value="RACK">牌价</Option>
                    <Option value="OTA">OTA价</Option>
                    <Option value="MEMBER">会员价</Option>
                  </Select>
                  <Button type="text" icon={<FilterOutlined />} size="middle">筛选</Button>
                  <div style={{ marginLeft: 'auto', display: 'flex', gap: 8 }}>
                    <Button type="text" icon={<SyncOutlined />} size="middle" onClick={handleRefresh}>刷新</Button>
                  </div>
                </div>

                <Table
                  columns={columns}
                  dataSource={filteredData}
                  rowKey="key"
                  pagination={false}
                  scroll={{ x: 1500, y: 600 }}
                  bordered
                  size="small"
                  style={{ backgroundColor: '#fff' }}
                  rowStyle={{ height: selectedInventoryType === '房价' ? 30 : 35 }}
                />
              </>
            )
          },
          {
            key: '2',
            label: 'PMS房控日历',
            children: <PMSInventoryCalendar />
          },
          {
            key: '3',
            label: '酒店超预订管理',
            children: (
              <HotelOverbooking />
            )
          },
          {
            key: '4',
            label: '房型超预订管理',
            children: (
              <RoomTypeOverbooking />
            )
          },
          {
            key: '5',
            label: '房价级房量管理',
            children: (
              <PriceLevelInventory />
            )
          },
          {
            key: '6',
            label: '渠道级房量管理',
            children: (
              <ChannelLevelInventory />
            )
          },
          {
            key: '7',
            label: '市场码级房量管理',
            children: (
              <MarketLevelInventory />
            )
          },
          {
            key: '8',
            label: '渠道+房型级房量',
            children: (
              <ChannelRoomTypeInventory />
            )
          },
          {
            key: '9',
            label: '房价大类房量控制',
            children: (
              <RateCategoryLevelInventory />
            )
          }
        ]}
      />

      <Modal
        title={selectedInventoryType === '房价' ? '更新房价' : '更新库存'}
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={() => setIsModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={400}
      >
        {selectedInventoryType === '房价' ? (
          <Form form={form} layout="vertical">
            <Form.Item name="price" label="房价" rules={[{ required: true, message: '请输入房价' }]}>
              <Input type="number" placeholder="请输入房价" prefix="¥" />
            </Form.Item>
          </Form>
        ) : (
          <Form form={form} layout="vertical">
            <Form.Item name="available" label="可售数量" rules={[{ required: true, message: '请输入可售数量' }]}>
              <Input type="number" placeholder="请输入可售数量" />
            </Form.Item>
            <Form.Item name="booked" label="已订数量" rules={[{ required: true, message: '请输入已订数量' }]}>
              <Input type="number" placeholder="请输入已订数量" />
            </Form.Item>
          </Form>
        )}
      </Modal>
    </div>
  )
}

export default Inventory
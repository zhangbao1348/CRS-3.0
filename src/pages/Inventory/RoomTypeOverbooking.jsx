import React, { useState } from 'react'
import { Table, Select, Button, Modal, Form, Input, DatePicker, message } from 'antd'
import { FilterOutlined, PlusOutlined, MinusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { RangePicker } = DatePicker

const { Option } = Select

const RoomTypeOverbooking = () => {
  const [selectedRoomType, setSelectedRoomType] = useState('全部房型')
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [currentRow, setCurrentRow] = useState(null)
  const [currentDate, setCurrentDate] = useState(null)
  const [form] = Form.useForm()

  const roomTypes = [
    { code: '1KGS', name: '标准大床房', total: 40 },
    { code: '1KGP', name: '高级大床房', total: 30 },
    { code: '1KGH', name: '探索大床房', total: 25 },
    { code: '2TGH', name: '探索双床房', total: 35 },
    { code: 'XHGS', name: '好眠大床房', total: 20 },
    { code: '2FGH', name: '探索家庭房', total: 15 }
  ]

  const dates = [
    { key: 'date_0', dateStr: '12.31', dayOfWeek: '周三' },
    { key: 'date_1', dateStr: '01.01', dayOfWeek: '周四' },
    { key: 'date_2', dateStr: '01.02', dayOfWeek: '周五' },
    { key: 'date_3', dateStr: '01.03', dayOfWeek: '周六' },
    { key: 'date_4', dateStr: '01.04', dayOfWeek: '周日' },
    { key: 'date_5', dateStr: '01.05', dayOfWeek: '周一' },
    { key: 'date_6', dateStr: '01.06', dayOfWeek: '周二' },
    { key: 'date_7', dateStr: '01.07', dayOfWeek: '周三' },
    { key: 'date_8', dateStr: '01.08', dayOfWeek: '周四' },
    { key: 'date_9', dateStr: '01.09', dayOfWeek: '周五' },
    { key: 'date_10', dateStr: '01.10', dayOfWeek: '周六' },
    { key: 'date_11', dateStr: '01.11', dayOfWeek: '周日' },
    { key: 'date_12', dateStr: '01.12', dayOfWeek: '周一' },
    { key: 'date_13', dateStr: '01.13', dayOfWeek: '周二' }
  ]

  const getOverbookingValue = (roomType, date) => {
    return Math.floor(Math.random() * 5)
  }

  const flattenData = []
  roomTypes.forEach(roomType => {
    const row = {
      key: roomType.code,
      roomTypeCode: roomType.code,
      roomTypeName: roomType.name,
      total: roomType.total
    }
    dates.forEach(date => {
      row[date.key] = getOverbookingValue(roomType, date)
    })
    flattenData.push(row)
  })

  const filteredData = selectedRoomType === '全部房型' 
    ? flattenData 
    : flattenData.filter(item => item.roomTypeCode === selectedRoomType)

  const columns = [
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
      width: 50,
      render: (value, record) => (
        <div 
          style={{
            textAlign: 'center',
            padding: '4px 0',
            backgroundColor: '#fff',
            border: '1px solid #e8e8e8',
            fontSize: 12,
            color: '#000',
            cursor: 'pointer'
          }}
          onClick={() => {
            setCurrentRow(record)
            setCurrentDate(date)
            form.setFieldsValue({ overbooking: value })
            setIsModalVisible(true)
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
        message.success(`${currentRow.roomTypeName} ${currentDate.dateStr} 超预订已更新为 ${values.overbooking}`)
      } else {
        const roomNames = roomTypes.filter(rt => values.roomTypes.includes(rt.code)).map(rt => rt.name).join('、')
        const dateRange = `${values.dateRange[0].format('MM.DD')}-${values.dateRange[1].format('MM.DD')}`
        message.success(`批量修改成功：${roomNames} 在 ${dateRange} 的超预订已更新为 ${values.overbooking}`)
      }
      setIsModalVisible(false)
    })
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 16 }}>
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
        <Button type="text" icon={<FilterOutlined />} size="middle">
          筛选
        </Button>
        <Button type="text" size="middle" onClick={() => {
          setCurrentRow(null)
          setCurrentDate(null)
          form.setFieldsValue({ overbooking: 0 })
          setIsModalVisible(true)
        }}>
          批量修改
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={filteredData}
        rowKey="key"
        pagination={false}
        scroll={{ x: 1200, y: 500 }}
        bordered
        size="small"
        style={{ backgroundColor: '#fff' }}
        rowStyle={{ height: 40 }}
      />

      <Modal
        title={currentRow && currentDate 
          ? `维护 ${currentRow.roomTypeName} - ${currentDate.dateStr} 超预订` 
          : '批量修改房型超预订'}
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
                name="dateRange"
                label="选择日期范围"
                rules={[{ required: true, message: '请选择日期范围' }]}
              >
                <RangePicker style={{ width: '100%' }} />
              </Form.Item>
            </>
          )}
          <Form.Item
            name="overbooking"
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

export default RoomTypeOverbooking
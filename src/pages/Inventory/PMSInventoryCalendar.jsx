import React, { useState } from 'react'
import { Table, Select, Button, Modal, message } from 'antd'
import { FilterOutlined, PlusOutlined, MinusOutlined, SyncOutlined } from '@ant-design/icons'

const { Option } = Select

const PMSInventoryCalendar = () => {
  const [selectedRoomType, setSelectedRoomType] = useState('全部房型')
  const [isLogModalVisible, setIsLogModalVisible] = useState(false)

  const mockLogs = [
    { key: '1', time: '2025-12-31 10:30:15', action: '库存同步', status: '成功', detail: 'STD: 20间' },
    { key: '2', time: '2025-12-31 10:30:10', action: '房价同步', status: '成功', detail: '牌价: ¥500' },
    { key: '3', time: '2025-12-31 10:30:05', action: '库存同步', status: '失败', detail: '连接超时' },
    { key: '4', time: '2025-12-31 09:00:00', action: '房价同步', status: '成功', detail: 'OTA价: ¥450' },
    { key: '5', time: '2025-12-31 08:00:00', action: '库存同步', status: '成功', detail: 'DLX: 15间' }
  ]

  const roomTypes = [
    { code: 'STD', name: '标准间', total: 20 },
    { code: 'DLX', name: '豪华间', total: 15 },
    { code: 'SUITE', name: '套房', total: 5 },
    { code: 'FAM', name: '家庭房', total: 10 }
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

  const inventoryTypes = [
    { key: 'physical', label: '物理房型数', color: '#1890ff' },
    { key: 'sold', label: '已售', color: '#52c41a' },
    { key: 'maintenance', label: '维修', color: '#faad14' },
    { key: 'overbooked', label: '超预订', color: '#f5222d' },
    { key: 'available', label: '可售', color: '#13c2c2' }
  ]

  const getInventoryValue = (roomType, date, type) => {
    switch (type) {
      case 'physical':
        return roomType.total
      case 'sold':
        return Math.floor(Math.random() * 10)
      case 'maintenance':
        return Math.random() > 0.8 ? Math.floor(Math.random() * 2) : '-'
      case 'overbooked':
        return Math.random() > 0.9 ? Math.floor(Math.random() * 3) : '-'
      case 'available':
        return roomType.total - Math.floor(Math.random() * 10)
      default:
        return '-'
    }
  }

  const flattenData = []
  roomTypes.forEach(roomType => {
    inventoryTypes.forEach(invType => {
      const row = {
        key: `${roomType.code}_${invType.key}`,
        roomTypeCode: roomType.code,
        roomTypeName: roomType.name,
        inventoryType: invType.key,
        inventoryLabel: invType.label,
        inventoryColor: invType.color
      }
      dates.forEach(date => {
        row[date.key] = getInventoryValue(roomType, date, invType.key)
      })
      flattenData.push(row)
    })
  })

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
      render: (_, record, index) => {
        return {
          children: (
            <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
              <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
              <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
              <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
            </div>
          ),
          props: {
            rowSpan: index % 5 === 0 ? 5 : 0
          }
        }
      }
    },
    {
      title: '库存类型',
      dataIndex: 'inventoryLabel',
      key: 'inventoryType',
      width: 80,
      fixed: 'left',
      render: (text, record) => (
        <div style={{ fontSize: 12, color: record.inventoryColor, fontWeight: 500 }}>
          {text}
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
        <div style={{ textAlign: 'center', fontSize: 12, color: record.inventoryColor, fontWeight: 500 }}>
          {value}
        </div>
      )
    }))
  ]

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
        <Button type="text" icon={<SyncOutlined />} size="middle" onClick={() => setIsLogModalVisible(true)}>
          查看同步日志
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={flattenData}
        rowKey="key"
        pagination={false}
        scroll={{ x: 1500, y: 500 }}
        bordered
        size="small"
        style={{ backgroundColor: '#fff' }}
        rowStyle={{ height: 35 }}
      />

      <Modal
        title="PMS库存同步日志"
        open={isLogModalVisible}
        onCancel={() => setIsLogModalVisible(false)}
        footer={null}
        width={700}
      >
        <Table
          dataSource={mockLogs}
          rowKey="key"
          size="small"
          columns={[
            { title: '时间', dataIndex: 'time', key: 'time', width: 180 },
            { title: '操作', dataIndex: 'action', key: 'action', width: 100 },
            { 
              title: '状态', 
              dataIndex: 'status', 
              key: 'status', 
              width: 80,
              render: (status) => (
                <span style={{ color: status === '成功' ? '#52c41a' : '#f5222d' }}>
                  {status}
                </span>
              )
            },
            { title: '详情', dataIndex: 'detail', key: 'detail' }
          ]}
          pagination={false}
        />
      </Modal>
    </div>
  )
}

export default PMSInventoryCalendar
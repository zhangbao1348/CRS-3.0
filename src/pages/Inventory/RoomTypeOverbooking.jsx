import React, { useState, useEffect, useCallback, useContext } from 'react'
import { Table, Select, Button, Modal, Form, Input, DatePicker, message, Tag } from 'antd'
import { FilterOutlined, PlusOutlined, MinusOutlined, HistoryOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { hotelRoomTypeApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { RangePicker } = DatePicker
const { Option } = Select

const RoomTypeOverbooking = () => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const { user } = useContext(AuthContext)
  const getOp = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  const today = dayjs()
  const [selectedMonth, setSelectedMonth] = useState(today.format('YYYY-MM'))
  const [selectedRoomType, setSelectedRoomType] = useState('all')
  const [roomTypes, setRoomTypes] = useState([])
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [currentRow, setCurrentRow] = useState(null)
  const [currentDate, setCurrentDate] = useState(null)
  const [form] = Form.useForm()
  const [tableData, setTableData] = useState([])
  const [loading, setLoading] = useState(false)
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [logs, setLogs] = useState([])
  const [loadingLogs, setLoadingLogs] = useState(false)

  // 加载房型
  useEffect(() => {
    if (!hotelCode) return
    hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode).then(res => {
      setRoomTypes((res?.data || []).filter(r => r.status === 'active'))
    }).catch(() => {})
  }, [hotelCode])

  // 生成日期列
  const generateDates = useCallback((monthStr) => {
    const [year, mon] = monthStr.split('-').map(Number)
    const daysInMonth = new Date(year, mon, 0).getDate()
    const dayNames = ['日', '一', '二', '三', '四', '五', '六']
    const dates = []
    for (let i = 1; i <= daysInMonth; i++) {
      const d = new Date(year, mon - 1, i)
      const dateStr = `${String(mon).padStart(2, '0')}.${String(i).padStart(2, '0')}`
      const fullDate = `${monthStr}-${String(i).padStart(2, '0')}`
      const isPast = dayjs(fullDate).isBefore(today, 'day')
      dates.push({ key: `date_${i}`, day: i, dateStr, dayOfWeek: dayNames[d.getDay()], fullDate, isPast })
    }
    return dates
  }, [today])

  const dates = generateDates(selectedMonth)

  // 加载超预订数据
  const fetchData = useCallback(async () => {
    if (!hotelCode || roomTypes.length === 0) return
    setLoading(true)
    try {
      const [year, mon] = selectedMonth.split('-').map(Number)
      const startDate = `${selectedMonth}-01`
      const endDate = `${selectedMonth}-${new Date(year, mon, 0).getDate()}`

      // 为每个房型查询数据
      const rows = []
      for (const rt of roomTypes) {
        const res = await api.get('/overbooking', {
          params: { hotelCode, dimensionType: 'room_type', dimensionCode: rt.roomTypeCode, startDate, endDate }
        })
        const dataMap = {}
        ;(res?.data || []).forEach(item => { dataMap[dayjs(item.overbookDate).format('YYYY-MM-DD')] = item.overbookCount })

        const row = { key: rt.roomTypeCode, roomTypeCode: rt.roomTypeCode, roomTypeName: rt.roomTypeName }
        dates.forEach(date => { row[date.key] = dataMap[date.fullDate] ?? 0 })
        rows.push(row)
      }
      setTableData(rows)
    } catch (err) { console.error('加载超预订数据失败:', err) }
    finally { setLoading(false) }
  }, [hotelCode, selectedMonth, roomTypes, dates])

  useEffect(() => { fetchData() }, [selectedMonth, roomTypes, hotelCode])

  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  const filteredData = selectedRoomType === 'all' ? tableData : tableData.filter(item => item.roomTypeCode === selectedRoomType)

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const count = parseInt(values.overbooking)
      if (currentRow && currentDate) {
        await api.post('/overbooking', {
          hotelCode, dimensionType: 'room_type', dimensionCode: currentRow.roomTypeCode,
          overbookDate: currentDate.fullDate, overbookCount: count
        }, { headers: { 'X-Operator-Name': getOp() } })
        message.success('保存成功')
      } else {
        const [start, end] = values.dateRange
        const selectedRTs = values.roomTypes || []
        const records = []
        for (const rtCode of selectedRTs) {
          let cur = start.startOf('day')
          while (cur.isBefore(end) || cur.isSame(end, 'day')) {
            records.push({ hotelCode, dimensionType: 'room_type', dimensionCode: rtCode, overbookDate: cur.format('YYYY-MM-DD'), overbookCount: count })
            cur = cur.add(1, 'day')
          }
        }
        if (records.length > 0) {
          await api.post('/overbooking/batch', records, { headers: { 'X-Operator-Name': getOp() } })
          message.success(`批量保存成功，共 ${records.length} 条`)
        }
      }
      setIsModalVisible(false)
      fetchData()
    } catch (err) { if (!err.errorFields) message.error('保存失败') }
  }

  const columns = [
    {
      title: (
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: 'center' }}>
          <Button type="text" icon={<MinusOutlined />} size="small" onClick={handlePrevMonth}>上月</Button>
          <span style={{ fontSize: 12, whiteSpace: 'nowrap' }}>{selectedMonth}</span>
          <Button type="text" icon={<PlusOutlined />} size="small" onClick={handleNextMonth}>下月</Button>
        </div>
      ),
      key: 'roomType',
      width: 200,
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
        <div style={{ textAlign: 'center', fontSize: 12, padding: '2px 0', backgroundColor: date.isPast ? '#f5f5f5' : '#fff', color: date.isPast ? '#999' : '#333' }}>
          <div>{date.dateStr}</div>
          <div style={{ fontSize: 10, color: '#999' }}>{date.dayOfWeek}</div>
        </div>
      ),
      dataIndex: date.key,
      key: date.key,
      width: 55,
      render: (value, record) => (
        <div style={{ textAlign: 'center', padding: '4px 0', backgroundColor: '#fff', border: '1px solid #e8e8e8', fontSize: 12,
          cursor: date.isPast ? 'not-allowed' : 'pointer', opacity: date.isPast ? 0.5 : 1 }}
          onClick={() => {
            if (date.isPast) return
            setCurrentRow(record); setCurrentDate(date)
            form.setFieldsValue({ overbooking: value })
            setIsModalVisible(true)
          }}>
          {value}
        </div>
      )
    }))
  ]

  if (!hotelCode) return <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>请先选择酒店</div>

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 16 }}>
        <Select value={selectedRoomType} style={{ width: 220 }} size="middle" onChange={setSelectedRoomType} showSearch optionFilterProp="children">
          <Option value="all">全部房型</Option>
          {roomTypes.map(rt => <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>{rt.roomTypeName}（{rt.roomTypeCode}）</Option>)}
        </Select>
        <Button type="text" size="middle" onClick={() => { setCurrentRow(null); setCurrentDate(null); form.resetFields(); form.setFieldsValue({ overbooking: 0 }); setIsModalVisible(true) }}>
          批量修改
        </Button>
        <Button type="text" icon={<HistoryOutlined />} size="middle" onClick={async () => {
          setLogModalVisible(true); setLoadingLogs(true)
          try {
            // 查所有房型的日志
            const res = await api.get('/overbooking/logs', { params: { hotelCode, dimensionType: 'room_type', dimensionCode: '' } })
            setLogs(res?.data || [])
          } catch (e) { console.error(e) } finally { setLoadingLogs(false) }
        }}>日志</Button>
      </div>

      <Table columns={columns} dataSource={filteredData} rowKey="key" pagination={false}
        scroll={{ x: 1200, y: 500 }} bordered size="small" loading={loading}
        style={{ backgroundColor: '#fff' }} locale={{ emptyText: '暂无数据' }} />

      <Modal title={currentRow && currentDate ? `维护 ${currentRow.roomTypeName} - ${currentDate.dateStr} 超预订` : '批量修改房型超预订'}
        open={isModalVisible} onOk={handleSubmit} onCancel={() => setIsModalVisible(false)} okText="确认" cancelText="取消" width={500}>
        <Form form={form} layout="vertical">
          {!currentRow && !currentDate && (
            <>
              <Form.Item name="roomTypes" label="选择房型" rules={[{ required: true, message: '请选择房型' }]}>
                <Select mode="multiple" placeholder="请选择房型" style={{ width: '100%' }}>
                  {roomTypes.map(rt => <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>{rt.roomTypeName}（{rt.roomTypeCode}）</Option>)}
                </Select>
              </Form.Item>
              <Form.Item name="dateRange" label="选择日期范围" rules={[{ required: true, message: '请选择日期范围' }]}>
                <RangePicker style={{ width: '100%' }} />
              </Form.Item>
            </>
          )}
          <Form.Item name="overbooking" label="超预订数量" rules={[{ required: true, message: '请输入超预订数量' }]}>
            <Input type="number" min={0} placeholder="请输入超预订数量" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="房型超预订操作日志" open={logModalVisible} onCancel={() => setLogModalVisible(false)} footer={null} width={850}>
        <Table dataSource={logs} rowKey="id" loading={loadingLogs} size="small" pagination={{ pageSize: 10 }} scroll={{ y: 400 }}
          columns={[
            { title: '操作时间', dataIndex: 'operationTime', width: 170, render: v => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-' },
            { title: '操作人', dataIndex: 'operatorName', width: 100 },
            { title: '房型', dataIndex: 'dimensionCode', width: 120, render: v => {
              if (!v) return '全部'
              const rt = roomTypes.find(r => r.roomTypeCode === v)
              return rt ? `${rt.roomTypeName}（${rt.roomTypeCode}）` : v
            }},
            { title: '类型', dataIndex: 'operationType', width: 90, render: v => <Tag color={v === 'batch' ? 'blue' : 'green'}>{v === 'batch' ? '批量' : '单个'}</Tag> },
            { title: '明细', dataIndex: 'detail', render: v => {
              if (!v) return '-'
              try { const d = JSON.parse(v); if (d.dates) return `${d.dates}，设为 ${d.count}`; return `${d.date}：${d.old} → ${d.new}` } catch { return v }
            }}
          ]} />
      </Modal>
    </div>
  )
}

export default RoomTypeOverbooking

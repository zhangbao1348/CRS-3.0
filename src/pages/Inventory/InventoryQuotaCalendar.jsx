import { useState, useEffect, useCallback, useContext } from 'react'
import { Button, Modal, Form, Input, DatePicker, message, Spin, Table, Tag } from 'antd'
import { LeftOutlined, RightOutlined, EditOutlined, HistoryOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { RangePicker } = DatePicker

const InventoryQuotaCalendar = ({ dimensionType, dimensionCode, dimensionLabel }) => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const { user } = useContext(AuthContext)
  const getOp = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  const [selectedMonth, setSelectedMonth] = useState(dayjs().format('YYYY-MM'))
  const [loading, setLoading] = useState(false)
  const [quotaData, setQuotaData] = useState({})
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [selectedDate, setSelectedDate] = useState(null)
  const [form] = Form.useForm()
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [logs, setLogs] = useState([])
  const [loadingLogs, setLoadingLogs] = useState(false)

  const getMinEditableDate = () => {
    const now = dayjs(); return now.hour() < 6 ? now.subtract(1, 'day').startOf('day') : now.startOf('day')
  }
  const isDateEditable = (dateStr) => !dayjs(dateStr).isBefore(getMinEditableDate(), 'day')

  const fetchData = useCallback(async () => {
    if (!hotelCode || !dimensionCode) return
    setLoading(true)
    try {
      const [year, mon] = selectedMonth.split('-').map(Number)
      const res = await api.get('/inventory-quota', {
        params: { hotelCode, dimensionType, dimensionCode, startDate: `${selectedMonth}-01`, endDate: `${selectedMonth}-${new Date(year, mon, 0).getDate()}` }
      })
      const map = {}
      ;(res?.data || []).forEach(item => { map[dayjs(item.quotaDate).format('YYYY-MM-DD')] = item })
      setQuotaData(map)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }, [hotelCode, selectedMonth, dimensionType, dimensionCode])

  useEffect(() => { if (dimensionCode) fetchData() }, [fetchData])

  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  const handleDateClick = (dateStr) => {
    if (!isDateEditable(dateStr)) { message.warning('不能修改过去日期的库存限制'); return }
    setSelectedDate(dateStr)
    const data = quotaData[dateStr]
    form.setFieldsValue({ quotaLimit: data?.quotaLimit ?? '' })
    setIsModalVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const limit = values.quotaLimit !== '' && values.quotaLimit != null ? parseInt(values.quotaLimit) : null
      if (selectedDate) {
        await api.post('/inventory-quota', { hotelCode, dimensionType, dimensionCode, quotaDate: selectedDate, quotaLimit: limit },
          { headers: { 'X-Operator-Name': getOp() } })
        message.success('保存成功')
      } else {
        const [start, end] = values.dateRange
        const records = []
        let cur = start.startOf('day')
        while (cur.isBefore(end) || cur.isSame(end, 'day')) {
          if (isDateEditable(cur.format('YYYY-MM-DD'))) {
            records.push({ hotelCode, dimensionType, dimensionCode, quotaDate: cur.format('YYYY-MM-DD'), quotaLimit: limit })
          }
          cur = cur.add(1, 'day')
        }
        if (records.length === 0) { message.warning('没有可修改的日期'); return }
        await api.post('/inventory-quota/batch', records, { headers: { 'X-Operator-Name': getOp() } })
        message.success(`批量保存成功，共 ${records.length} 天`)
      }
      setIsModalVisible(false); fetchData()
    } catch (err) { if (!err.errorFields) message.error('保存失败') }
  }

  const renderCalendar = () => {
    const [year, mon] = selectedMonth.split('-').map(Number)
    const daysInMonth = new Date(year, mon, 0).getDate()
    const firstDay = new Date(year, mon - 1, 1).getDay()
    const dayNames = ['日', '一', '二', '三', '四', '五', '六']
    return (
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead><tr>{dayNames.map(d => <th key={d} style={{ padding: 8, border: '1px solid #d9d9d9', background: '#fafafa', textAlign: 'center' }}>{d}</th>)}</tr></thead>
        <tbody>{(() => {
          const rows = []; let cells = []
          for (let i = 0; i < firstDay; i++) cells.push(<td key={`e${i}`} style={{ border: '1px solid #d9d9d9' }} />)
          for (let i = 1; i <= daysInMonth; i++) {
            const dateStr = `${selectedMonth}-${String(i).padStart(2, '0')}`
            const data = quotaData[dateStr]
            const limit = data?.quotaLimit
            const sold = data?.soldCount || 0
            const remaining = limit != null ? Math.max(0, limit - sold) : null
            const wd = new Date(year, mon - 1, i).getDay()
            const editable = isDateEditable(dateStr)
            cells.push(
              <td key={i} style={{ border: '1px solid #d9d9d9', padding: 6, cursor: editable ? 'pointer' : 'not-allowed',
                background: (wd === 0 || wd === 6) ? '#fffbf0' : '#fff', textAlign: 'center', verticalAlign: 'top',
                opacity: editable ? 1 : 0.5, fontSize: 11 }}
                onClick={() => editable && handleDateClick(dateStr)}>
                <div style={{ fontWeight: 500, marginBottom: 2 }}>{i}</div>
                <div style={{ color: '#1890ff' }}>限: {limit != null ? limit : '-'}</div>
                <div style={{ color: '#faad14' }}>售: {sold > 0 ? sold : '-'}</div>
                <div style={{ color: remaining != null && remaining <= 0 ? '#ff4d4f' : '#52c41a', fontWeight: 500 }}>
                  余: {remaining != null ? remaining : '-'}
                </div>
              </td>
            )
            if ((firstDay + i) % 7 === 0) { rows.push(<tr key={`r${rows.length}`}>{cells}</tr>); cells = [] }
          }
          if (cells.length > 0) { while (cells.length < 7) cells.push(<td key={`ee${cells.length}`} style={{ border: '1px solid #d9d9d9' }} />); rows.push(<tr key={`r${rows.length}`}>{cells}</tr>) }
          return rows
        })()}</tbody>
      </table>
    )
  }

  if (!hotelCode) return <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>请先选择酒店</div>
  if (!dimensionCode) return <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>请先选择{dimensionLabel}</div>

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 16, gap: 16 }}>
        <Button icon={<LeftOutlined />} onClick={handlePrevMonth}>上月</Button>
        <span style={{ fontSize: 16, fontWeight: 500, minWidth: 100, textAlign: 'center' }}>{selectedMonth}</span>
        <Button icon={<RightOutlined />} onClick={handleNextMonth}>下月</Button>
        <Button icon={<EditOutlined />} style={{ marginLeft: 24 }}
          onClick={() => { setSelectedDate(null); form.resetFields(); setIsModalVisible(true) }}>批量修改</Button>
        <Button icon={<HistoryOutlined />} style={{ marginLeft: 8 }}
          onClick={async () => {
            setLogModalVisible(true); setLoadingLogs(true)
            try { const res = await api.get('/inventory-quota/logs', { params: { hotelCode, dimensionType, dimensionCode } }); setLogs(res?.data || []) }
            catch (e) { console.error(e) } finally { setLoadingLogs(false) }
          }}>日志</Button>
      </div>
      <Spin spinning={loading}>{renderCalendar()}</Spin>
      <Modal title={selectedDate ? `维护 ${selectedDate} 库存限制` : '批量修改库存限制'}
        open={isModalVisible} onOk={handleSubmit} onCancel={() => setIsModalVisible(false)} okText="确认" cancelText="取消" width={500}>
        <Form form={form} layout="vertical">
          {!selectedDate && <Form.Item name="dateRange" label="日期范围" rules={[{ required: true }]}>
            <RangePicker style={{ width: '100%' }} disabledDate={d => d && d.isBefore(getMinEditableDate(), 'day')} /></Form.Item>}
          <Form.Item name="quotaLimit" label="库存限制（留空表示不限制）">
            <Input type="number" min={0} placeholder="留空表示不限制" />
          </Form.Item>
        </Form>
      </Modal>
      <Modal title="房量控制操作日志" open={logModalVisible} onCancel={() => setLogModalVisible(false)} footer={null} width={700}>
        <Table dataSource={logs} rowKey="id" loading={loadingLogs} size="small" pagination={{ pageSize: 10 }} scroll={{ y: 400 }}
          columns={[
            { title: '操作时间', dataIndex: 'operationTime', width: 170, render: v => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-' },
            { title: '操作人', dataIndex: 'operatorName', width: 100 },
            { title: '类型', dataIndex: 'operationType', width: 90, render: v => <Tag color={v === 'batch' ? 'blue' : 'green'}>{v === 'batch' ? '批量' : '单个'}</Tag> },
            { title: '明细', dataIndex: 'detail', render: v => {
              if (!v) return '-'
              try { const d = JSON.parse(v); if (d.dates) return `${d.dates}，限制设为 ${d.quotaLimit}`; return `${d.date}：${d.old} → ${d.new}` } catch { return v }
            }}
          ]} />
      </Modal>
    </div>
  )
}

export default InventoryQuotaCalendar

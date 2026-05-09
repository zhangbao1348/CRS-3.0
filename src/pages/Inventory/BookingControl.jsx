import React, { useState, useEffect, useCallback, useContext } from 'react'
import { Select, Button, Modal, Form, Input, DatePicker, message, Row, Col, Card, Tabs, Spin, Table, Tag } from 'antd'
import { LeftOutlined, RightOutlined, EditOutlined, HistoryOutlined, HomeOutlined, TagOutlined, LinkOutlined, DollarOutlined, GlobalOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { ratePlanApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { RangePicker } = DatePicker
const { Option } = Select

const BookingControl = () => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const { user } = useContext(AuthContext)
  const [selectedMonth, setSelectedMonth] = useState(dayjs().format('YYYY-MM'))
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [selectedDate, setSelectedDate] = useState(null)
  const [form] = Form.useForm()
  const [activeTab, setActiveTab] = useState('hotel')
  const [loading, setLoading] = useState(false)
  const [controlData, setControlData] = useState({})

  // 日志
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [logs, setLogs] = useState([])
  const [loadingLogs, setLoadingLogs] = useState(false)

  const getOperatorName = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  // 维度筛选值
  const [selectedRateCode, setSelectedRateCode] = useState(undefined)
  const [selectedChannel, setSelectedChannel] = useState(undefined)
  const [selectedRateCategory, setSelectedRateCategory] = useState(undefined)
  const [selectedMarket, setSelectedMarket] = useState(undefined)

  // 下拉数据源（从后端 API 加载）
  const [ratePlans, setRatePlans] = useState([])
  const [cancellationPolicies, setCancellationPolicies] = useState([])
  const [channelCodes, setChannelCodes] = useState([])
  const [rateCategories, setRateCategories] = useState([])
  const [marketCodes, setMarketCodes] = useState([])

  // 取消规则名称映射（code -> name(code)）
  const getCancelRuleDisplay = (code) => {
    const policy = cancellationPolicies.find(p => p.code === code)
    return policy ? `${policy.name}（${policy.code}）` : code
  }

  // 加载下拉数据
  useEffect(() => {
    if (!hotelCode) return
    ratePlanApi.getRatePlansByHotelCode(hotelCode).then(res => setRatePlans(res?.data || [])).catch(() => {})
  }, [hotelCode])

  useEffect(() => {
    // 加载集团取消政策
    api.get('/cancellation-policies').then(res => {
      const list = Array.isArray(res) ? res : (res?.data || [])
      setCancellationPolicies(list.map(p => ({ code: p.policyCode || p.code, name: p.policyName || p.name, id: p.id })))
    }).catch(() => {})
    // 加载三级渠道码
    api.get('/channel-codes/third-level').then(res => {
      const list = Array.isArray(res) ? res : (res?.data || [])
      setChannelCodes(list)
    }).catch(() => setChannelCodes([]))
    // 加载房价大类
    api.get('/rate-types/active').then(res => {
      const list = Array.isArray(res) ? res : (res?.data || [])
      setRateCategories(list)
    }).catch(() => setRateCategories([]))
    // 加载三级市场码
    api.get('/market-codes/third-level').then(res => {
      const list = Array.isArray(res) ? res : (res?.data || [])
      setMarketCodes(list)
    }).catch(() => setMarketCodes([]))
  }, [])

  const getDimensionCode = useCallback(() => {
    if (activeTab === 'hotel') return ''
    if (activeTab === 'rate') return selectedRateCode || ''
    if (activeTab === 'channel') return selectedChannel || ''
    if (activeTab === 'rateCategory') return selectedRateCategory || ''
    if (activeTab === 'market') return selectedMarket || ''
    return ''
  }, [activeTab, selectedRateCode, selectedChannel, selectedRateCategory, selectedMarket])

  const isSelectionValid = () => {
    if (activeTab === 'hotel') return true
    if (activeTab === 'rate') return !!selectedRateCode
    if (activeTab === 'channel') return !!selectedChannel
    if (activeTab === 'rateCategory') return !!selectedRateCategory
    if (activeTab === 'market') return !!selectedMarket
    return false
  }

  const fetchData = useCallback(async () => {
    if (!hotelCode || !isSelectionValid()) return
    setLoading(true)
    try {
      const [year, mon] = selectedMonth.split('-').map(Number)
      const startDate = `${selectedMonth}-01`
      const endDate = `${selectedMonth}-${new Date(year, mon, 0).getDate()}`
      const res = await api.get('/booking-controls', {
        params: { hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode(), startDate, endDate }
      })
      const list = res?.data || []
      const map = {}
      list.forEach(item => { map[dayjs(item.controlDate).format('YYYY-MM-DD')] = item })
      setControlData(map)
    } catch (err) {
      console.error('查询预订控制失败:', err)
    } finally {
      setLoading(false)
    }
  }, [hotelCode, selectedMonth, activeTab, getDimensionCode])

  useEffect(() => { if (isSelectionValid()) fetchData() }, [selectedMonth, activeTab, selectedRateCode, selectedChannel, selectedRateCategory, selectedMarket, hotelCode])

  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  const handleDateClick = (dateStr) => {
    setSelectedDate(dateStr)
    const data = controlData[dateStr] || {}
    form.setFieldsValue({
      cancellationRule: data.cancellationRule || undefined,
      advanceBooking: data.advanceBookingDays || 0,
      minStay: data.minStay || 1,
      maxStay: data.maxStay || 30
    })
    setIsModalVisible(true)
  }

  const handleBatchClick = () => {
    setSelectedDate(null)
    form.resetFields()
    form.setFieldsValue({ advanceBooking: 0, minStay: 1, maxStay: 30 })
    setIsModalVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (selectedDate) {
        await api.post('/booking-controls', {
          hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode(),
          controlDate: selectedDate, cancellationRule: values.cancellationRule,
          advanceBookingDays: parseInt(values.advanceBooking), minStay: parseInt(values.minStay), maxStay: parseInt(values.maxStay)
        }, { headers: { 'X-Operator-Name': getOperatorName() } })
        message.success('保存成功')
      } else {
        const [start, end] = values.dateRange
        const records = []
        let cur = start.startOf('day')
        while (cur.isBefore(end) || cur.isSame(end, 'day')) {
          records.push({
            hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode(),
            controlDate: cur.format('YYYY-MM-DD'), cancellationRule: values.cancellationRule,
            advanceBookingDays: parseInt(values.advanceBooking), minStay: parseInt(values.minStay), maxStay: parseInt(values.maxStay)
          })
          cur = cur.add(1, 'day')
        }
        await api.post('/booking-controls/batch', records, { headers: { 'X-Operator-Name': getOperatorName() } })
        message.success(`批量保存成功，共 ${records.length} 天`)
      }
      setIsModalVisible(false)
      fetchData()
    } catch (err) {
      if (err.errorFields) return
      message.error('保存失败')
    }
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
            const data = controlData[dateStr] || {}
            const wd = new Date(year, mon - 1, i).getDay()
            cells.push(
              <td key={i} style={{ border: '1px solid #d9d9d9', padding: 8, cursor: 'pointer', background: (wd === 0 || wd === 6) ? '#fffbf0' : '#fff', textAlign: 'center', verticalAlign: 'top' }}
                onClick={() => handleDateClick(dateStr)}>
                <div style={{ fontWeight: 500, marginBottom: 4 }}>{i}</div>
                {data.cancellationRule && (
                  <div style={{ fontSize: 11, lineHeight: 1.6 }}>
                    <div style={{ color: '#1890ff', marginBottom: 2 }}>{getCancelRuleDisplay(data.cancellationRule)}</div>
                    <div style={{ color: '#52c41a', marginBottom: 2 }}>提前: {data.advanceBookingDays || 0}天</div>
                    <div style={{ color: '#faad14' }}>连住: {data.minStay || 1}-{data.maxStay || 30}晚</div>
                  </div>
                )}
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

  return (
    <div className="fade-in">
      <h1 className="page-title">预订控制</h1>
      <Card style={{ marginBottom: 16 }}>
        <Tabs activeKey={activeTab} onChange={k => { setActiveTab(k); setControlData({}) }}
          items={[
            { key: 'hotel', label: <span><HomeOutlined /> 酒店预订控制</span> },
            { key: 'rate', label: <span><TagOutlined /> 房价预订控制</span> },
            { key: 'channel', label: <span><LinkOutlined /> 渠道预订控制</span> },
            { key: 'rateCategory', label: <span><DollarOutlined /> 房价大类预订控制</span> },
            { key: 'market', label: <span><GlobalOutlined /> 市场预订控制</span> }
          ]} />
      </Card>
      <Card>
        <Row gutter={16} style={{ marginBottom: 16 }}>
          {activeTab === 'rate' && (
            <Col span={8}>
              <Select placeholder="请选择房价码" style={{ width: '100%' }} value={selectedRateCode} onChange={setSelectedRateCode} allowClear showSearch optionFilterProp="children">
                {ratePlans.map(rp => <Option key={rp.rateCode} value={rp.rateCode}>{rp.rateName}（{rp.rateCode}）</Option>)}
              </Select>
            </Col>
          )}
          {activeTab === 'channel' && (
            <Col span={8}>
              <Select placeholder="请选择渠道" style={{ width: '100%' }} value={selectedChannel} onChange={setSelectedChannel} allowClear showSearch optionFilterProp="children">
                {channelCodes.map(c => <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>)}
              </Select>
            </Col>
          )}
          {activeTab === 'rateCategory' && (
            <Col span={8}>
              <Select placeholder="请选择房价大类" style={{ width: '100%' }} value={selectedRateCategory} onChange={setSelectedRateCategory} allowClear showSearch optionFilterProp="children">
                {rateCategories.map(c => <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>)}
              </Select>
            </Col>
          )}
          {activeTab === 'market' && (
            <Col span={8}>
              <Select placeholder="请选择市场码" style={{ width: '100%' }} value={selectedMarket} onChange={setSelectedMarket} allowClear showSearch optionFilterProp="children">
                {marketCodes.map(c => <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>)}
              </Select>
            </Col>
          )}
        </Row>
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 16, gap: 16 }}>
          <Button icon={<LeftOutlined />} onClick={handlePrevMonth}>上月</Button>
          <span style={{ fontSize: 16, fontWeight: 500, minWidth: 100, textAlign: 'center' }}>{selectedMonth}</span>
          <Button icon={<RightOutlined />} onClick={handleNextMonth}>下月</Button>
          <Button icon={<EditOutlined />} style={{ marginLeft: 24 }} disabled={!isSelectionValid()} onClick={handleBatchClick}>批量修改</Button>
          <Button icon={<HistoryOutlined />} style={{ marginLeft: 8 }} disabled={!isSelectionValid()} onClick={async () => {
            setLogModalVisible(true)
            setLoadingLogs(true)
            try {
              const res = await api.get('/booking-controls/logs', { params: { hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode() } })
              setLogs(res?.data || [])
            } catch (err) { console.error(err) }
            finally { setLoadingLogs(false) }
          }}>日志</Button>
        </div>
        <Spin spinning={loading}>
          {isSelectionValid() ? renderCalendar() : (
            <div style={{ textAlign: 'center', padding: 60, color: '#999', fontSize: 16 }}>
              请先选择{activeTab === 'rate' ? '房价码' : activeTab === 'channel' ? '渠道' : activeTab === 'rateCategory' ? '房价大类' : '市场码'}
            </div>
          )}
        </Spin>

        <Modal title={selectedDate ? `维护 ${selectedDate} 预订控制` : '批量修改预订控制'}
          open={isModalVisible} onOk={handleSubmit} onCancel={() => setIsModalVisible(false)}
          okText="确认" cancelText="取消" width={500}>
          <Form form={form} layout="vertical">
            {!selectedDate && (
              <Form.Item name="dateRange" label="选择日期范围" rules={[{ required: true, message: '请选择日期范围' }]}>
                <RangePicker style={{ width: '100%' }} />
              </Form.Item>
            )}
            <Form.Item name="cancellationRule" label="取消规则" rules={[{ required: true, message: '请选择取消规则' }]}>
              <Select placeholder="请选择取消规则" showSearch optionFilterProp="children">
                {cancellationPolicies.map(p => <Option key={p.code} value={p.code}>{p.name}（{p.code}）</Option>)}
              </Select>
            </Form.Item>
            <Form.Item name="advanceBooking" label="提前预订天数" rules={[{ required: true }]}>
              <Input type="number" min={0} placeholder="天数" />
            </Form.Item>
            <Form.Item name="minStay" label="最小连住天数" rules={[{ required: true }]}>
              <Input type="number" min={1} placeholder="最小天数" />
            </Form.Item>
            <Form.Item name="maxStay" label="最大连住天数" rules={[{ required: true }]}>
              <Input type="number" min={1} placeholder="最大天数" />
            </Form.Item>
          </Form>
        </Modal>

        <Modal title="预订控制操作日志" open={logModalVisible} onCancel={() => setLogModalVisible(false)} footer={null} width={900}>
          <Table dataSource={logs} rowKey="id" loading={loadingLogs} size="small" pagination={{ pageSize: 10 }} scroll={{ y: 400 }}
            columns={[
              { title: '操作时间', dataIndex: 'operationTime', key: 'time', width: 170, render: v => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-' },
              { title: '操作人', dataIndex: 'operatorName', key: 'operator', width: 100 },
              { title: '操作类型', dataIndex: 'operationType', key: 'type', width: 100,
                render: v => <Tag color={v === 'batch' ? 'blue' : 'green'}>{v === 'batch' ? '批量修改' : '单个修改'}</Tag> },
              { title: '操作明细', dataIndex: 'detail', key: 'detail', render: v => {
                if (!v) return '-'
                try {
                  const d = JSON.parse(v)
                  if (d.dates) {
                    return <div>
                      <div>日期：{d.dates}</div>
                      <div>取消规则：{getCancelRuleDisplay(d.values?.cancellationRule)}</div>
                      <div>提前预订：{d.values?.advanceBookingDays}天，连住：{d.values?.minStay}-{d.values?.maxStay}晚</div>
                    </div>
                  }
                  if (d.changes) {
                    return <div>
                      <div>日期：{d.date}</div>
                      {Object.entries(d.changes).map(([k, val]) => {
                        const labels = { cancellationRule: '取消规则', advanceBookingDays: '提前天数', minStay: '最小连住', maxStay: '最大连住' }
                        const [oldV, newV] = val.split('→')
                        const displayOld = k === 'cancellationRule' ? getCancelRuleDisplay(oldV) : oldV
                        const displayNew = k === 'cancellationRule' ? getCancelRuleDisplay(newV) : newV
                        return <div key={k}>{labels[k] || k}：{displayOld ? `${displayOld} → ${displayNew}` : displayNew}</div>
                      })}
                    </div>
                  }
                  return v
                } catch { return v }
              }}
            ]} />
        </Modal>
      </Card>
    </div>
  )
}

export default BookingControl

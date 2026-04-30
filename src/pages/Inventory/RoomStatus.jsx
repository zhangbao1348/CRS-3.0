import React, { useState, useEffect, useCallback, useContext } from 'react'
import { Select, Button, Modal, DatePicker, message, Row, Col, Card, Tabs, Spin, Radio, Table, Tag } from 'antd'
import { LeftOutlined, RightOutlined, EditOutlined, HistoryOutlined, HomeOutlined, AppstoreOutlined, TagOutlined, LinkOutlined, BlockOutlined, GlobalOutlined, DollarOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { ratePlanApi, hotelRoomTypeApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { RangePicker } = DatePicker
const { Option } = Select

const RoomStatus = () => {
  const { selectedHotel: hotelCode, selectedHotelId } = useHotelContext()
  const { user } = useContext(AuthContext)
  const [selectedMonth, setSelectedMonth] = useState(dayjs().format('YYYY-MM'))
  const [activeTab, setActiveTab] = useState('hotel')
  const [loading, setLoading] = useState(false)
  const [statusData, setStatusData] = useState({})

  const getOperatorName = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  // 日期限制：6点前可改昨天，6点后只能改今天及以后
  const getMinEditableDate = () => {
    const now = dayjs()
    const hour = now.hour()
    return hour < 6 ? now.subtract(1, 'day').startOf('day') : now.startOf('day')
  }
  const isDateEditable = (dateStr) => !dayjs(dateStr).isBefore(getMinEditableDate(), 'day')

  // 批量操作弹窗
  const [batchModalVisible, setBatchModalVisible] = useState(false)
  const [batchDateRange, setBatchDateRange] = useState(null)
  const [batchIsOpen, setBatchIsOpen] = useState(true)
  const [batchRoomTypeCodes, setBatchRoomTypeCodes] = useState([])

  // 日志弹窗
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [logs, setLogs] = useState([])
  const [loadingLogs, setLoadingLogs] = useState(false)

  // 维度筛选值
  const [selectedRoomType, setSelectedRoomType] = useState(undefined)
  const [selectedRateCode, setSelectedRateCode] = useState(undefined)
  const [selectedChannel, setSelectedChannel] = useState(undefined)
  const [selectedChannelForRT, setSelectedChannelForRT] = useState(undefined)
  const [selectedRoomTypeForCh, setSelectedRoomTypeForCh] = useState(undefined)
  const [selectedMarket, setSelectedMarket] = useState(undefined)
  const [selectedRateCategory, setSelectedRateCategory] = useState(undefined)

  // 下拉数据源
  const [roomTypes, setRoomTypes] = useState([])
  const [ratePlans, setRatePlans] = useState([])
  const [channelCodes, setChannelCodes] = useState([])
  const [marketCodes, setMarketCodes] = useState([])
  const [rateCategories, setRateCategories] = useState([])

  // 加载酒店房型
  useEffect(() => {
    if (!selectedHotelId) return
    hotelRoomTypeApi.getHotelRoomTypes(selectedHotelId).then(res => {
      setRoomTypes(res?.data || [])
    }).catch(() => {})
  }, [selectedHotelId])

  // 加载房价码
  useEffect(() => {
    if (!selectedHotelId) return
    ratePlanApi.getRatePlans(selectedHotelId).then(res => {
      setRatePlans(res?.data || [])
    }).catch(() => {})
  }, [selectedHotelId])

  // 加载渠道、市场、房价大类
  useEffect(() => {
    api.get('/channel-codes/third-level').then(res => {
      const list = Array.isArray(res) ? res : (res?.data || [])
      setChannelCodes(list)
    }).catch(() => setChannelCodes([]))

    api.get('/market-codes/third-level').then(res => {
      const list = Array.isArray(res) ? res : (res?.data || [])
      setMarketCodes(list)
    }).catch(() => setMarketCodes([]))

    api.get('/rate-types/active').then(res => {
      const list = Array.isArray(res) ? res : (res?.data || [])
      setRateCategories(list)
    }).catch(() => setRateCategories([]))
  }, [])

  const getDimensionCode = useCallback(() => {
    if (activeTab === 'hotel') return ''
    if (activeTab === 'room_type') return selectedRoomType || ''
    if (activeTab === 'rate') return selectedRateCode || ''
    if (activeTab === 'channel') return selectedChannel || ''
    if (activeTab === 'channel_room_type') {
      if (selectedChannelForRT && selectedRoomTypeForCh) return `${selectedChannelForRT}:${selectedRoomTypeForCh}`
      return ''
    }
    if (activeTab === 'market') return selectedMarket || ''
    if (activeTab === 'rate_category') return selectedRateCategory || ''
    return ''
  }, [activeTab, selectedRoomType, selectedRateCode, selectedChannel, selectedChannelForRT, selectedRoomTypeForCh, selectedMarket, selectedRateCategory])

  const isSelectionValid = useCallback(() => {
    if (activeTab === 'hotel') return true
    if (activeTab === 'room_type') return !!selectedRoomType
    if (activeTab === 'rate') return !!selectedRateCode
    if (activeTab === 'channel') return !!selectedChannel
    if (activeTab === 'channel_room_type') return !!selectedChannelForRT && !!selectedRoomTypeForCh
    if (activeTab === 'market') return !!selectedMarket
    if (activeTab === 'rate_category') return !!selectedRateCategory
    return false
  }, [activeTab, selectedRoomType, selectedRateCode, selectedChannel, selectedChannelForRT, selectedRoomTypeForCh, selectedMarket, selectedRateCategory])

  const fetchData = useCallback(async () => {
    if (!hotelCode || !isSelectionValid()) return
    setLoading(true)
    try {
      const [year, mon] = selectedMonth.split('-').map(Number)
      const startDate = `${selectedMonth}-01`
      const endDate = `${selectedMonth}-${new Date(year, mon, 0).getDate()}`
      const res = await api.get('/room-status', {
        params: { hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode(), startDate, endDate }
      })
      const list = res?.data || []
      const map = {}
      list.forEach(item => { map[dayjs(item.statusDate).format('YYYY-MM-DD')] = item })
      setStatusData(map)
    } catch (err) {
      console.error('查询房态失败:', err)
    } finally {
      setLoading(false)
    }
  }, [hotelCode, selectedMonth, activeTab, getDimensionCode, isSelectionValid])

  useEffect(() => {
    if (isSelectionValid()) fetchData()
  }, [selectedMonth, activeTab, selectedRoomType, selectedRateCode, selectedChannel, selectedChannelForRT, selectedRoomTypeForCh, selectedMarket, selectedRateCategory, hotelCode])

  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  // 单击日期切换开关
  const handleDateClick = async (dateStr) => {
    if (!hotelCode) return
    if (!isDateEditable(dateStr)) {
      message.warning('不能修改过去日期的房态')
      return
    }
    const existing = statusData[dateStr]
    const newIsOpen = existing ? !existing.isOpen : false // 默认开，点击变关
    try {
      await api.post('/room-status', {
        hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode(),
        statusDate: dateStr, isOpen: newIsOpen
      }, { headers: { 'X-Operator-Name': getOperatorName() } })
      setStatusData(prev => ({
        ...prev,
        [dateStr]: { ...prev[dateStr], statusDate: dateStr, isOpen: newIsOpen }
      }))
      message.success(`${dateStr} 已${newIsOpen ? '开房' : '关房'}`)
    } catch (err) {
      message.error('操作失败')
    }
  }

  // 批量操作
  const handleBatchSubmit = async () => {
    if (!batchDateRange || batchDateRange.length !== 2) {
      message.warning('请选择日期范围')
      return
    }
    
    // 渠道+房型级需要选择房型
    if (activeTab === 'channel_room_type') {
      if (!selectedChannelForRT) { message.warning('请先选择渠道'); return }
      if (batchRoomTypeCodes.length === 0) { message.warning('请选择至少一个房型'); return }
    }
    
    const [start, end] = batchDateRange
    const records = []
    
    if (activeTab === 'channel_room_type' && batchRoomTypeCodes.length > 0) {
      // 渠道+房型级：为每个选中的房型 × 每个日期生成记录
      let cur = start.startOf('day')
      while (cur.isBefore(end) || cur.isSame(end, 'day')) {
        batchRoomTypeCodes.forEach(rtCode => {
          records.push({
            hotelCode, dimensionType: activeTab,
            dimensionCode: `${selectedChannelForRT}:${rtCode}`,
            statusDate: cur.format('YYYY-MM-DD'), isOpen: batchIsOpen
          })
        })
        cur = cur.add(1, 'day')
      }
    } else {
      // 其他维度：正常生成
      let cur = start.startOf('day')
      while (cur.isBefore(end) || cur.isSame(end, 'day')) {
        records.push({
          hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode(),
          statusDate: cur.format('YYYY-MM-DD'), isOpen: batchIsOpen
        })
        cur = cur.add(1, 'day')
      }
    }
    
    try {
      await api.post('/room-status/batch', records, { headers: { 'X-Operator-Name': getOperatorName() } })
      message.success(`批量${batchIsOpen ? '开房' : '关房'}成功，共 ${records.length} 条`)
      setBatchModalVisible(false)
      setBatchDateRange(null)
      setBatchRoomTypeCodes([])
      fetchData()
    } catch (err) {
      message.error('批量操作失败')
    }
  }

  const getFilterHint = () => {
    const hints = {
      room_type: '房型',
      rate: '房价码',
      channel: '渠道',
      channel_room_type: '渠道和房型',
      market: '市场码',
      rate_category: '房价大类'
    }
    return hints[activeTab] || ''
  }

  const renderCalendar = () => {
    const [year, mon] = selectedMonth.split('-').map(Number)
    const daysInMonth = new Date(year, mon, 0).getDate()
    const firstDay = new Date(year, mon - 1, 1).getDay()
    const dayNames = ['日', '一', '二', '三', '四', '五', '六']
    return (
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            {dayNames.map(d => (
              <th key={d} style={{ padding: 8, border: '1px solid #d9d9d9', background: '#fafafa', textAlign: 'center' }}>{d}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {(() => {
            const rows = []
            let cells = []
            for (let i = 0; i < firstDay; i++) {
              cells.push(<td key={`e${i}`} style={{ border: '1px solid #d9d9d9' }} />)
            }
            for (let i = 1; i <= daysInMonth; i++) {
              const dateStr = `${selectedMonth}-${String(i).padStart(2, '0')}`
              const data = statusData[dateStr]
              // 没有记录时默认为"开"
              const isOpen = data ? data.isOpen : true
              const isClosed = data && !data.isOpen
              const wd = new Date(year, mon - 1, i).getDay()
              const isWeekend = wd === 0 || wd === 6
              const editable = isDateEditable(dateStr)
              cells.push(
                <td key={i} style={{
                  border: '1px solid #d9d9d9', padding: 8, cursor: editable ? 'pointer' : 'not-allowed',
                  background: isWeekend ? '#fffbf0' : '#fff', textAlign: 'center', verticalAlign: 'top',
                  opacity: editable ? 1 : 0.5
                }} onClick={() => editable && handleDateClick(dateStr)}>
                  <div style={{ fontWeight: 500, marginBottom: 4 }}>{i}</div>
                  <div style={{
                    color: isClosed ? '#ff4d4f' : '#52c41a',
                    fontSize: 12, fontWeight: 500
                  }}>
                    {isClosed ? '关' : '开'}
                  </div>
                </td>
              )
              if ((firstDay + i) % 7 === 0) { rows.push(<tr key={`r${rows.length}`}>{cells}</tr>); cells = [] }
            }
            if (cells.length > 0) {
              while (cells.length < 7) cells.push(<td key={`ee${cells.length}`} style={{ border: '1px solid #d9d9d9' }} />)
              rows.push(<tr key={`r${rows.length}`}>{cells}</tr>)
            }
            return rows
          })()}
        </tbody>
      </table>
    )
  }

  const renderFilters = () => {
    return (
      <Row gutter={16} style={{ marginBottom: 16 }}>
        {activeTab === 'room_type' && (
          <Col span={8}>
            <Select placeholder="请选择房型" style={{ width: '100%' }} value={selectedRoomType}
              onChange={setSelectedRoomType} allowClear showSearch optionFilterProp="children">
              {roomTypes.map(rt => (
                <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>
                  {rt.roomTypeName}（{rt.roomTypeCode}）
                </Option>
              ))}
            </Select>
          </Col>
        )}
        {activeTab === 'rate' && (
          <Col span={8}>
            <Select placeholder="请选择房价码" style={{ width: '100%' }} value={selectedRateCode}
              onChange={setSelectedRateCode} allowClear showSearch optionFilterProp="children">
              {ratePlans.map(rp => (
                <Option key={rp.rateCode} value={rp.rateCode}>
                  {rp.rateName}（{rp.rateCode}）
                </Option>
              ))}
            </Select>
          </Col>
        )}
        {activeTab === 'channel' && (
          <Col span={8}>
            <Select placeholder="请选择渠道" style={{ width: '100%' }} value={selectedChannel}
              onChange={setSelectedChannel} allowClear showSearch optionFilterProp="children">
              {channelCodes.map(c => (
                <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
              ))}
            </Select>
          </Col>
        )}
        {activeTab === 'channel_room_type' && (
          <>
            <Col span={6}>
              <Select placeholder="请选择渠道" style={{ width: '100%' }} value={selectedChannelForRT}
                onChange={setSelectedChannelForRT} allowClear showSearch optionFilterProp="children">
                {channelCodes.map(c => (
                  <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
                ))}
              </Select>
            </Col>
            <Col span={6}>
              <Select placeholder="请选择房型" style={{ width: '100%' }} value={selectedRoomTypeForCh}
                onChange={setSelectedRoomTypeForCh} allowClear showSearch optionFilterProp="children">
                {roomTypes.map(rt => (
                  <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>
                    {rt.roomTypeName}（{rt.roomTypeCode}）
                  </Option>
                ))}
              </Select>
            </Col>
          </>
        )}
        {activeTab === 'market' && (
          <Col span={8}>
            <Select placeholder="请选择市场码" style={{ width: '100%' }} value={selectedMarket}
              onChange={setSelectedMarket} allowClear showSearch optionFilterProp="children">
              {marketCodes.map(c => (
                <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
              ))}
            </Select>
          </Col>
        )}
        {activeTab === 'rate_category' && (
          <Col span={8}>
            <Select placeholder="请选择房价大类" style={{ width: '100%' }} value={selectedRateCategory}
              onChange={setSelectedRateCategory} allowClear showSearch optionFilterProp="children">
              {rateCategories.map(c => (
                <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
              ))}
            </Select>
          </Col>
        )}
      </Row>
    )
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">房态管理</h1>
      <Card style={{ marginBottom: 16 }}>
        <Tabs activeKey={activeTab} onChange={k => { setActiveTab(k); setStatusData({}) }}
          items={[
            { key: 'hotel', label: <span><HomeOutlined /> 酒店级房态</span> },
            { key: 'room_type', label: <span><AppstoreOutlined /> 房型级房态</span> },
            { key: 'rate', label: <span><TagOutlined /> 房价级房态</span> },
            { key: 'channel', label: <span><LinkOutlined /> 渠道级房态</span> },
            { key: 'channel_room_type', label: <span><BlockOutlined /> 渠道+房型级房态</span> },
            { key: 'market', label: <span><GlobalOutlined /> 市场码级房态</span> },
            { key: 'rate_category', label: <span><DollarOutlined /> 房价大类房态</span> }
          ]} />
      </Card>
      <Card>
        {renderFilters()}
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 16, gap: 16 }}>
          <Button icon={<LeftOutlined />} onClick={handlePrevMonth}>上月</Button>
          <span style={{ fontSize: 16, fontWeight: 500, minWidth: 100, textAlign: 'center' }}>{selectedMonth}</span>
          <Button icon={<RightOutlined />} onClick={handleNextMonth}>下月</Button>
          <Button type="primary" style={{ marginLeft: 24 }} disabled={!isSelectionValid()}
            onClick={() => { setBatchRoomTypeCodes([]); setBatchIsOpen(true); setBatchModalVisible(true) }}>
            <EditOutlined /> 批量修改
          </Button>
          <Button icon={<HistoryOutlined />} style={{ marginLeft: 8 }} disabled={!isSelectionValid()}
            onClick={async () => {
              setLogModalVisible(true); setLoadingLogs(true)
              try {
                const res = await api.get('/room-status/logs', { params: { hotelCode, dimensionType: activeTab, dimensionCode: getDimensionCode() } })
                setLogs(res?.data || [])
              } catch (err) { console.error(err) }
              finally { setLoadingLogs(false) }
            }}>日志</Button>
        </div>
        <Spin spinning={loading}>
          {isSelectionValid() ? renderCalendar() : (
            <div style={{ textAlign: 'center', padding: 60, color: '#999', fontSize: 16 }}>
              请先选择{getFilterHint()}
            </div>
          )}
        </Spin>

        <Modal title="批量修改房态"
          open={batchModalVisible}
          onOk={handleBatchSubmit}
          onCancel={() => { setBatchModalVisible(false); setBatchDateRange(null); setBatchRoomTypeCodes([]) }}
          okText="确认" cancelText="取消" width={500}>
          <div style={{ marginBottom: 16 }}>
            <div style={{ marginBottom: 8 }}>操作：</div>
            <Radio.Group value={batchIsOpen} onChange={e => setBatchIsOpen(e.target.value)}>
              <Radio value={true}><span style={{ color: '#52c41a', fontWeight: 500 }}>开房</span></Radio>
              <Radio value={false}><span style={{ color: '#ff4d4f', fontWeight: 500 }}>关房</span></Radio>
            </Radio.Group>
          </div>
          <div style={{ marginBottom: 16 }}>
            <div style={{ marginBottom: 8 }}>选择日期范围：</div>
            <RangePicker style={{ width: '100%' }} value={batchDateRange} onChange={setBatchDateRange}
              disabledDate={d => d && d.isBefore(getMinEditableDate(), 'day')} />
          </div>
          {activeTab === 'channel_room_type' && (
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8 }}>选择房型（可多选）：</div>
              <Select mode="multiple" placeholder="请选择房型" style={{ width: '100%' }}
                value={batchRoomTypeCodes} onChange={setBatchRoomTypeCodes}
                showSearch optionFilterProp="children">
                {roomTypes.map(rt => (
                  <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>
                    {rt.roomTypeName}（{rt.roomTypeCode}）
                  </Option>
                ))}
              </Select>
            </div>
          )}
          <div style={{ color: batchIsOpen ? '#52c41a' : '#ff4d4f', fontWeight: 500 }}>
            操作：将选中日期范围内的房态设置为「{batchIsOpen ? '开' : '关'}」
          </div>
        </Modal>

        <Modal title="房态操作日志" open={logModalVisible} onCancel={() => setLogModalVisible(false)} footer={null} width={800}>
          <Table dataSource={logs} rowKey="id" loading={loadingLogs} size="small" pagination={{ pageSize: 10 }} scroll={{ y: 400 }}
            columns={[
              { title: '操作时间', dataIndex: 'operationTime', key: 'time', width: 170, render: v => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-' },
              { title: '操作人', dataIndex: 'operatorName', key: 'op', width: 100 },
              { title: '操作类型', dataIndex: 'operationType', key: 'type', width: 100,
                render: v => <Tag color={v === 'batch' ? 'blue' : 'green'}>{v === 'batch' ? '批量修改' : '单个修改'}</Tag> },
              { title: '操作明细', dataIndex: 'detail', key: 'detail', render: v => {
                if (!v) return '-'
                try {
                  const d = JSON.parse(v)
                  if (d.dates) return <div>{d.dates}，{d.action}</div>
                  if (d.date) return <div>{d.date}：{d.old} → {d.new}</div>
                  return v
                } catch { return v }
              }}
            ]} />
        </Modal>
      </Card>
    </div>
  )
}

export default RoomStatus

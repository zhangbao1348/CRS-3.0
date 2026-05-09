import React, { useState, useEffect, useCallback, useContext } from 'react'
import { Button, Checkbox, DatePicker, Input, Card, Row, Col, Modal, Form, Select, message, Spin, Empty, Table, Tag } from 'antd'
import { DollarOutlined, HistoryOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { ratePlanApi, hotelRoomTypeApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { RangePicker } = DatePicker

const WEEKDAY_MAP = ['日', '一', '二', '三', '四', '五', '六']

const OP_TYPE_MAP = {
  create: { text: '新增', color: 'green' },
  update: { text: '修改', color: 'blue' },
  delete: { text: '删除', color: 'red' },
  batch_update: { text: '批量修改', color: 'blue' },
  batch_delete: { text: '批量删除', color: 'red' }
}

const RackRate = () => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const { user } = useContext(AuthContext)

  // Filter state
  const [ratePlans, setRatePlans] = useState([])
  const [selectedRateCode, setSelectedRateCode] = useState(null)
  const [roomTypes, setRoomTypes] = useState([])
  const [dateRange, setDateRange] = useState([dayjs(), dayjs().add(13, 'day')])

  // Price grid state
  const [dates, setDates] = useState([])
  const [prices, setPrices] = useState({}) // { roomTypeCode: { 'yyyy-MM-dd': { value, status } } }
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  // Batch modal
  const [batchModalVisible, setBatchModalVisible] = useState(false)
  const [batchForm] = Form.useForm()
  const [batchWeekdays, setBatchWeekdays] = useState([])
  const [batchRoomPrices, setBatchRoomPrices] = useState({})

  // Log modal
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [logs, setLogs] = useState([])
  const [loadingLogs, setLoadingLogs] = useState(false)

  // 获取操作人名称（URL编码，避免HTTP头不支持中文）
  const getOperatorName = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  // Load rate plans when hotel changes
  useEffect(() => {
    if (!hotelCode) {
      setRatePlans([])
      return
    }
    ratePlanApi.getRatePlansByHotelCode(hotelCode).then(res => {
      setRatePlans(res?.data || [])
    }).catch(() => {}).finally(() => setLoading(false))
  }, [hotelCode])

  useEffect(() => {
    if (!hotelCode) { setRoomTypes([]); return }
    hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode).then(res => {
      setRoomTypes((res?.data || []).filter(r => r.status === 'active'))
    }).catch(() => {})
  }, [hotelCode])

  // Build date columns from range
  const buildDates = useCallback((range) => {
    if (!range || range.length !== 2) return []
    const result = []
    let cur = range[0].startOf('day')
    const end = range[1].startOf('day')
    while (cur.isBefore(end) || cur.isSame(end, 'day')) {
      result.push(cur.format('YYYY-MM-DD'))
      cur = cur.add(1, 'day')
    }
    return result
  }, [])

  // Fetch prices - 包含已删除的价格记录
  const fetchPrices = useCallback(async () => {
    if (!hotelCode || !selectedRateCode || !dateRange || dateRange.length !== 2) return
    setLoading(true)
    try {
      const cols = buildDates(dateRange)
      setDates(cols)
      const res = await api.get('/hotel-prices', {
        params: {
          hotelCode,
          rateCode: selectedRateCode,
          startDate: dateRange[0].format('YYYY-MM-DD'),
          endDate: dateRange[1].format('YYYY-MM-DD')
        }
      })
      const list = res?.data || []
      const map = {}
      roomTypes.forEach(rt => { map[rt.roomTypeCode] = {} })
      list.forEach(p => {
        if (!map[p.roomTypeCode]) map[p.roomTypeCode] = {}
        const d = dayjs(p.priceDate).format('YYYY-MM-DD')
        if (p.status === 'inactive') {
          // 已删除的价格标记为 deleted
          map[p.roomTypeCode][d] = { value: '', status: 'deleted' }
        } else {
          map[p.roomTypeCode][d] = { value: p.priceWithTax != null ? Number(p.priceWithTax) : '', status: 'active' }
        }
      })
      setPrices(map)
    } catch (err) {
      console.error('查询价格失败:', err)
      message.error('查询价格失败')
    } finally {
      setLoading(false)
    }
  }, [hotelCode, selectedRateCode, dateRange, roomTypes, buildDates])

  // Save single price on blur
  const handlePriceSave = async (roomTypeCode, dateStr, value) => {
    if (value === '' || value == null) return
    setSaving(true)
    try {
      await api.post('/hotel-prices', {
        hotelCode,
        rateCode: selectedRateCode,
        roomTypeCode,
        priceDate: dateStr,
        priceWithTax: Number(value),
        status: 'active'
      }, {
        headers: { 'X-Operator-Name': getOperatorName() }
      })
      // 更新本地状态为 active
      setPrices(prev => ({
        ...prev,
        [roomTypeCode]: { ...prev[roomTypeCode], [dateStr]: { value: Number(value), status: 'active' } }
      }))
    } catch (err) {
      console.error('保存失败:', err)
      message.error('保存失败')
    } finally {
      setSaving(false)
    }
  }

  // Handle cell value change (local state only)
  const handleCellChange = (roomTypeCode, dateStr, value) => {
    setPrices(prev => ({
      ...prev,
      [roomTypeCode]: { ...prev[roomTypeCode], [dateStr]: { value, status: 'active' } }
    }))
  }

  // Batch modal
  const openBatchModal = () => {
    batchForm.resetFields()
    setBatchWeekdays([1, 2, 3, 4, 5, 6, 0])
    const initPrices = {}
    roomTypes.forEach(rt => { initPrices[rt.roomTypeCode] = { price: '', delete: false } })
    setBatchRoomPrices(initPrices)
    setBatchModalVisible(true)
  }

  const handleBatchConfirm = async () => {
    try {
      const values = await batchForm.validateFields()
      const { batchDateRange } = values
      if (!batchDateRange || batchDateRange.length !== 2) {
        message.warning('请选择日期范围'); return
      }

      const updateRooms = Object.entries(batchRoomPrices)
        .filter(([, v]) => !v.delete && v.price !== '' && v.price != null)
      const deleteRooms = Object.entries(batchRoomPrices)
        .filter(([, v]) => v.delete)

      if (updateRooms.length === 0 && deleteRooms.length === 0) {
        message.warning('请至少为一个房型输入价格或勾选删除'); return
      }

      const records = []
      let cur = batchDateRange[0].startOf('day')
      const end = batchDateRange[1].startOf('day')
      while (cur.isBefore(end) || cur.isSame(end, 'day')) {
        const wd = cur.day()
        if (batchWeekdays.length === 0 || batchWeekdays.includes(wd)) {
          const dateStr = cur.format('YYYY-MM-DD')
          updateRooms.forEach(([rtCode, v]) => {
            records.push({
              hotelCode,
              rateCode: selectedRateCode,
              roomTypeCode: rtCode,
              priceDate: dateStr,
              priceWithTax: Number(v.price),
              status: 'active'
            })
          })
          deleteRooms.forEach(([rtCode]) => {
            records.push({
              hotelCode,
              rateCode: selectedRateCode,
              roomTypeCode: rtCode,
              priceDate: dateStr,
              priceWithTax: 0,
              status: 'inactive'
            })
          })
        }
        cur = cur.add(1, 'day')
      }
      if (records.length === 0) { message.warning('没有匹配的日期'); return }

      setLoading(true)
      await api.post('/hotel-prices/batch', records, {
        headers: { 'X-Operator-Name': getOperatorName() }
      })
      message.success(`批量操作成功，共 ${records.length} 条`)
      setBatchModalVisible(false)
      await fetchPrices()
    } catch (err) {
      console.error('批量操作失败:', err)
      message.error('批量操作失败: ' + (err?.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  // Log modal
  const openLogModal = async () => {
    setLogModalVisible(true)
    setLoadingLogs(true)
    try {
      const res = await api.get('/hotel-prices/logs', {
        params: { hotelCode, rateCode: selectedRateCode }
      })
      setLogs(res?.data || [])
    } catch (err) {
      console.error('查询日志失败:', err)
      message.error('查询日志失败')
    } finally {
      setLoadingLogs(false)
    }
  }

  // 格式化日志明细
  const formatLogDetail = (detail, operationType) => {
    if (!detail) return '-'
    try {
      const items = JSON.parse(detail)
      // 构建房型名称映射
      const rtNameMap = {}
      roomTypes.forEach(rt => { rtNameMap[rt.roomTypeCode] = rt.roomTypeName })

      return items.map((item, idx) => {
        const rtName = rtNameMap[item.roomTypeCode] || item.roomTypeCode
        const dates = item.dates || []
        const dateStr = dates.length <= 3
          ? dates.join('、')
          : `${dates[0]} ~ ${dates[dates.length - 1]}（共${dates.length}天）`

        if (operationType === 'delete' || operationType === 'batch_delete') {
          return <div key={idx}>{rtName}：{dateStr} 价格已删除</div>
        }
        const priceInfo = item.oldPrice
          ? `${item.oldPrice} → ${item.newPrice}`
          : `设为 ${item.newPrice}`
        return <div key={idx}>{rtName}：{dateStr} {priceInfo}</div>
      })
    } catch {
      return detail
    }
  }

  const logColumns = [
    {
      title: '操作时间',
      dataIndex: 'operationTime',
      key: 'operationTime',
      width: 170,
      render: v => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'
    },
    {
      title: '操作人',
      dataIndex: 'operatorName',
      key: 'operatorName',
      width: 100
    },
    {
      title: '操作类型',
      dataIndex: 'operationType',
      key: 'operationType',
      width: 100,
      render: v => {
        const info = OP_TYPE_MAP[v] || { text: v, color: 'default' }
        return <Tag color={info.color}>{info.text}</Tag>
      }
    },
    {
      title: '日期范围',
      key: 'dateRange',
      width: 200,
      render: (_, record) => {
        const s = record.startDate ? dayjs(record.startDate).format('YYYY-MM-DD') : ''
        const e = record.endDate ? dayjs(record.endDate).format('YYYY-MM-DD') : ''
        return s === e ? s : `${s} ~ ${e}`
      }
    },
    {
      title: '操作明细',
      dataIndex: 'detail',
      key: 'detail',
      render: (v, record) => formatLogDetail(v, record.operationType)
    }
  ]

  // Empty state
  if (!hotelCode) {
    return (
      <div className="page-container">
        <h1 className="page-title"><DollarOutlined /> 基础价格设置</h1>
        <Card bordered={false}><Empty description="请先选择酒店" /></Card>
      </div>
    )
  }

  const cellStyle = { border: '1px solid #d9d9d9', padding: '6px 8px', textAlign: 'center' }
  const thStyle = { ...cellStyle, background: '#fafafa', fontWeight: 600, whiteSpace: 'nowrap' }

  return (
    <div className="page-container">
      <h1 className="page-title"><DollarOutlined /> 基础价格设置</h1>
      <Card bordered={false}>
        {/* Filters */}
        <Row gutter={16} align="middle" style={{ marginBottom: 16 }}>
          <Col>
            <span style={{ marginRight: 8 }}>价格码:</span>
            <Select
              showSearch
              placeholder="请选择价格码"
              optionFilterProp="label"
              value={selectedRateCode}
              onChange={setSelectedRateCode}
              style={{ width: 280 }}
              options={ratePlans.map(rp => ({
                value: rp.rateCode,
                label: `${rp.rateName}（${rp.rateCode}）`
              }))}
            />
          </Col>
        </Row>
        <Row gutter={16} align="middle" style={{ marginBottom: 16 }}>
          <Col>
            <span style={{ marginRight: 8 }}>日期:</span>
            <RangePicker value={dateRange} onChange={setDateRange} />
          </Col>
          <Col flex="auto" style={{ textAlign: 'right' }}>
            <Button type="primary" onClick={fetchPrices} loading={loading} style={{ marginRight: 8 }}>
              查询
            </Button>
            <Button type="primary" onClick={openBatchModal} disabled={!selectedRateCode} style={{ marginRight: 8 }}>
              批量修改
            </Button>
            <Button icon={<HistoryOutlined />} onClick={openLogModal} disabled={!selectedRateCode}>
              日志
            </Button>
          </Col>
        </Row>

        {/* Price Grid */}
        <Spin spinning={loading || saving}>
          {dates.length > 0 && roomTypes.length > 0 ? (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', minWidth: dates.length * 100 + 160 }}>
                <thead>
                  <tr>
                    <th style={{ ...thStyle, width: 160, position: 'sticky', left: 0, zIndex: 2, background: '#fafafa' }}>房型</th>
                    {dates.map(d => {
                      const dj = dayjs(d)
                      const wd = dj.day()
                      const isWeekend = wd === 0 || wd === 6
                      return (
                        <th key={d} style={{ ...thStyle, color: isWeekend ? '#f5222d' : undefined, minWidth: 90 }}>
                          {d}<br />({WEEKDAY_MAP[wd]})
                        </th>
                      )
                    })}
                  </tr>
                </thead>
                <tbody>
                  {roomTypes.map(rt => (
                    <tr key={rt.roomTypeCode}>
                      <td style={{ ...cellStyle, fontWeight: 500, position: 'sticky', left: 0, background: '#fff', zIndex: 1 }}>
                        {rt.roomTypeName}（{rt.roomTypeCode}）
                      </td>
                      {dates.map(d => {
                        const cell = prices[rt.roomTypeCode]?.[d]
                        const isDeleted = cell?.status === 'deleted'
                        const val = cell?.value

                        if (isDeleted) {
                          return (
                            <td key={d} style={{ ...cellStyle, color: '#999', fontSize: 18, fontWeight: 600 }}>
                              -
                            </td>
                          )
                        }

                        return (
                          <td key={d} style={cellStyle}>
                            <Input
                              type="number"
                              size="small"
                              style={{ width: 80, textAlign: 'center' }}
                              value={val ?? ''}
                              onChange={e => handleCellChange(rt.roomTypeCode, d, e.target.value)}
                              onBlur={e => handlePriceSave(rt.roomTypeCode, d, e.target.value)}
                            />
                          </td>
                        )
                      })}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            !loading && <Empty description="请点击查询加载价格数据" />
          )}
        </Spin>
      </Card>

      {/* Batch Modal */}
      <Modal
        title="批量设置价格"
        open={batchModalVisible}
        onOk={handleBatchConfirm}
        onCancel={() => setBatchModalVisible(false)}
        width={640}
        okText="保存"
        cancelText="取消"
        confirmLoading={loading}
      >
        <Form form={batchForm} layout="vertical">
          <Form.Item label="日期范围" name="batchDateRange" rules={[{ required: true, message: '请选择日期范围' }]}>
            <RangePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item label="周控">
            <div>
              <Checkbox
                indeterminate={batchWeekdays.length > 0 && batchWeekdays.length < 7}
                checked={batchWeekdays.length === 7}
                onChange={e => setBatchWeekdays(e.target.checked ? [1, 2, 3, 4, 5, 6, 0] : [])}
              >全选</Checkbox>
              {[1, 2, 3, 4, 5, 6, 0].map(wd => (
                <Checkbox
                  key={wd}
                  checked={batchWeekdays.includes(wd)}
                  onChange={e => {
                    setBatchWeekdays(prev => e.target.checked ? [...prev, wd] : prev.filter(w => w !== wd))
                  }}
                  style={{ marginLeft: 8 }}
                >
                  {WEEKDAY_MAP[wd]}
                </Checkbox>
              ))}
            </div>
          </Form.Item>

          <Form.Item label="房型与价格">
            <div>
              {roomTypes.map(rt => {
                const item = batchRoomPrices[rt.roomTypeCode] || { price: '', delete: false }
                return (
                  <div key={rt.roomTypeCode} style={{ display: 'flex', alignItems: 'center', marginBottom: 8 }}>
                    <span style={{ width: 180, fontWeight: 500 }}>{rt.roomTypeName}（{rt.roomTypeCode}）</span>
                    <Input
                      type="number"
                      placeholder="含税价格（不填则不更新）"
                      style={{ width: 200, marginRight: 12 }}
                      value={item.price}
                      disabled={item.delete}
                      onChange={e => {
                        setBatchRoomPrices(prev => ({
                          ...prev,
                          [rt.roomTypeCode]: { ...prev[rt.roomTypeCode], price: e.target.value }
                        }))
                      }}
                    />
                    <Checkbox
                      checked={item.delete}
                      onChange={e => {
                        setBatchRoomPrices(prev => ({
                          ...prev,
                          [rt.roomTypeCode]: { ...prev[rt.roomTypeCode], delete: e.target.checked, price: e.target.checked ? '' : prev[rt.roomTypeCode].price }
                        }))
                      }}
                      style={{ color: '#ff4d4f' }}
                    >
                      删除
                    </Checkbox>
                  </div>
                )
              })}
            </div>
          </Form.Item>
        </Form>
      </Modal>

      {/* Log Modal */}
      <Modal
        title="价格操作日志"
        open={logModalVisible}
        onCancel={() => setLogModalVisible(false)}
        footer={null}
        width={900}
      >
        <Table
          dataSource={logs}
          columns={logColumns}
          rowKey="id"
          loading={loadingLogs}
          pagination={{ pageSize: 10, showSizeChanger: false }}
          size="small"
          scroll={{ y: 400 }}
        />
      </Modal>
    </div>
  )
}

export default RackRate

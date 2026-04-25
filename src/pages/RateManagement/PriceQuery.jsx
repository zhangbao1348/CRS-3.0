import React, { useState, useEffect, useCallback } from 'react'
import { Button, DatePicker, Card, Row, Col, Select, Spin, Empty, message } from 'antd'
import { SearchOutlined, ReloadOutlined, DollarOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { ratePlanApi, hotelRoomTypeApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'

const WEEKDAY_MAP = ['日', '一', '二', '三', '四', '五', '六']

const PriceQuery = () => {
  const { selectedHotel: hotelCode, selectedHotelId } = useHotelContext()

  const [ratePlans, setRatePlans] = useState([])
  const [selectedRateCode, setSelectedRateCode] = useState(null)
  const [roomTypes, setRoomTypes] = useState([])
  const [selectedMonth, setSelectedMonth] = useState(dayjs())
  const [dates, setDates] = useState([])
  const [prices, setPrices] = useState({}) // { roomTypeCode: { 'yyyy-MM-dd': value } }
  const [loading, setLoading] = useState(false)

  // 加载价格计划
  useEffect(() => {
    if (!selectedHotelId) { setRatePlans([]); setSelectedRateCode(null); return }
    ratePlanApi.getRatePlans(selectedHotelId).then(res => {
      const list = res?.data || []
      setRatePlans(list)
      if (list.length > 0) setSelectedRateCode(list[0].rateCode)
      else setSelectedRateCode(null)
    }).catch(() => setRatePlans([]))
  }, [selectedHotelId])

  // 加载房型
  useEffect(() => {
    if (!selectedHotelId) { setRoomTypes([]); return }
    hotelRoomTypeApi.getHotelRoomTypes(selectedHotelId).then(res => {
      const list = (res?.data || []).filter(r => r.status === 'active')
      setRoomTypes(list)
    }).catch(() => setRoomTypes([]))
  }, [selectedHotelId])

  // 生成月份日期列表
  const buildMonthDates = useCallback((month) => {
    const result = []
    const daysInMonth = month.daysInMonth()
    for (let i = 1; i <= daysInMonth; i++) {
      result.push(month.date(i).format('YYYY-MM-DD'))
    }
    return result
  }, [])

  // 查询价格
  const fetchPrices = useCallback(async () => {
    if (!hotelCode || !selectedRateCode) return
    setLoading(true)
    try {
      const cols = buildMonthDates(selectedMonth)
      setDates(cols)
      const startDate = selectedMonth.startOf('month').format('YYYY-MM-DD')
      const endDate = selectedMonth.endOf('month').format('YYYY-MM-DD')
      const res = await api.get('/hotel-prices', {
        params: { hotelCode, rateCode: selectedRateCode, startDate, endDate }
      })
      const list = res?.data || []
      const map = {}
      roomTypes.forEach(rt => { map[rt.roomTypeCode] = {} })
      list.forEach(p => {
        if (!map[p.roomTypeCode]) map[p.roomTypeCode] = {}
        const d = dayjs(p.priceDate).format('YYYY-MM-DD')
        if (p.status === 'inactive') {
          map[p.roomTypeCode][d] = '-'
        } else {
          map[p.roomTypeCode][d] = p.priceWithTax != null ? Number(p.priceWithTax) : null
        }
      })
      setPrices(map)
    } catch (err) {
      console.error('查询价格失败:', err)
      message.error('查询价格失败')
    } finally {
      setLoading(false)
    }
  }, [hotelCode, selectedRateCode, selectedMonth, roomTypes, buildMonthDates])

  const handleMonthChange = (month) => {
    if (month) setSelectedMonth(month)
  }

  const handleReset = () => {
    setSelectedMonth(dayjs())
    if (ratePlans.length > 0) setSelectedRateCode(ratePlans[0].rateCode)
    setPrices({})
    setDates([])
  }

  if (!hotelCode) {
    return (
      <div className="page-container">
        <h1 className="page-title"><DollarOutlined /> 价格查询</h1>
        <Card bordered={false}><Empty description="请先选择酒店" /></Card>
      </div>
    )
  }

  const cellStyle = { border: '1px solid #d9d9d9', padding: '6px 4px', textAlign: 'center', fontSize: 13 }
  const thStyle = { ...cellStyle, background: '#fafafa', fontWeight: 600, whiteSpace: 'nowrap' }

  return (
    <div className="page-container">
      <h1 className="page-title"><DollarOutlined /> 价格查询</h1>
      <Card bordered={false}>
        <Row gutter={16} align="middle" style={{ marginBottom: 16 }}>
          <Col>
            <span style={{ marginRight: 8 }}>价格计划:</span>
            <Select
              showSearch
              placeholder="请选择价格计划"
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
          <Col>
            <span style={{ marginRight: 8 }}>月份:</span>
            <DatePicker.MonthPicker
              value={selectedMonth}
              onChange={handleMonthChange}
              placeholder="选择月份"
            />
          </Col>
          <Col>
            <Button type="primary" icon={<SearchOutlined />} onClick={fetchPrices} loading={loading} style={{ marginRight: 8 }}>
              查询
            </Button>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>重置</Button>
          </Col>
        </Row>

        <Spin spinning={loading}>
          {dates.length > 0 && roomTypes.length > 0 ? (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', minWidth: dates.length * 70 + 200 }}>
                <thead>
                  <tr>
                    <th style={{ ...thStyle, width: 200, position: 'sticky', left: 0, zIndex: 2, background: '#fafafa' }}>房型</th>
                    {dates.map(d => {
                      const dj = dayjs(d)
                      const wd = dj.day()
                      const isWeekend = wd === 0 || wd === 6
                      return (
                        <th key={d} style={{ ...thStyle, color: isWeekend ? '#f5222d' : undefined, minWidth: 65 }}>
                          {dj.format('MM-DD')}<br />({WEEKDAY_MAP[wd]})
                        </th>
                      )
                    })}
                  </tr>
                </thead>
                <tbody>
                  {roomTypes.map(rt => (
                    <tr key={rt.roomTypeCode}>
                      <td style={{ ...cellStyle, fontWeight: 500, position: 'sticky', left: 0, background: '#fff', zIndex: 1, whiteSpace: 'nowrap' }}>
                        {rt.roomTypeName}（{rt.roomTypeCode}）
                      </td>
                      {dates.map(d => {
                        const val = prices[rt.roomTypeCode]?.[d]
                        const isWeekend = dayjs(d).day() === 0 || dayjs(d).day() === 6
                        let display = ''
                        let color = '#333'
                        if (val === '-') {
                          display = '-'
                          color = '#999'
                        } else if (val != null && val !== '') {
                          display = `¥${val}`
                        }
                        return (
                          <td key={d} style={{ ...cellStyle, color, background: isWeekend ? '#fff7e6' : undefined }}>
                            {display}
                          </td>
                        )
                      })}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            !loading && <Empty description={dates.length === 0 ? '请点击查询加载价格数据' : '暂无房型数据'} />
          )}
        </Spin>
      </Card>
    </div>
  )
}

export default PriceQuery

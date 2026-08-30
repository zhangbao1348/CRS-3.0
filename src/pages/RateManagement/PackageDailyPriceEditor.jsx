import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { App, Card, Row, Col, Button, DatePicker, InputNumber, Empty, Spin, Modal, Form, Checkbox } from 'antd'
import dayjs from 'dayjs'
import { packageApi } from '../../utils/api'

const WEEKDAY_MAP = ['日', '一', '二', '三', '四', '五', '六']
const WEEKDAY_OPTIONS = [
  { label: '周日', value: 0 },
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 }
]
const ALL_WEEKDAY_VALUES = WEEKDAY_OPTIONS.map((item) => item.value)

const normalizePriceValue = (value) => {
  if (value === '' || value === undefined || value === null) {
    return null
  }
  return Number(value)
}

const PackageDailyPriceEditor = ({ hotelCode, packageCode, packageName }) => {
  const { message } = App.useApp()
  const [batchForm] = Form.useForm()
  const [selectedMonth, setSelectedMonth] = useState(dayjs())
  const [priceMap, setPriceMap] = useState({})
  const [loading, setLoading] = useState(false)
  const [savingDateKey, setSavingDateKey] = useState(null)
  const [batchVisible, setBatchVisible] = useState(false)
  const [batchSaving, setBatchSaving] = useState(false)
  const persistedPriceMapRef = useRef({})
  const selectedWeekdays = Form.useWatch('weekdays', batchForm) || []

  const monthDates = useMemo(() => {
    const result = []
    const totalDays = selectedMonth.daysInMonth()

    for (let day = 1; day <= totalDays; day += 1) {
      result.push(selectedMonth.date(day))
    }

    return result
  }, [selectedMonth])

  const calendarWeeks = useMemo(() => {
    const monthStart = selectedMonth.startOf('month')
    const monthEnd = selectedMonth.endOf('month')
    const calendarStart = monthStart.startOf('week')
    const calendarEnd = monthEnd.endOf('week')
    const weeks = []
    let cursor = calendarStart
    let currentWeek = []

    while (cursor.isBefore(calendarEnd) || cursor.isSame(calendarEnd, 'day')) {
      currentWeek.push({
        date: cursor,
        isCurrentMonth: cursor.month() === selectedMonth.month()
      })

      if (currentWeek.length === 7) {
        weeks.push(currentWeek)
        currentWeek = []
      }

      cursor = cursor.add(1, 'day')
    }

    if (currentWeek.length > 0) {
      weeks.push(currentWeek)
    }

    return weeks
  }, [selectedMonth])

  const loadDailyPrices = useCallback(async () => {
    if (!hotelCode || !packageCode) {
      setPriceMap({})
      persistedPriceMapRef.current = {}
      return
    }

    setLoading(true)
    try {
      const response = await packageApi.getDailyPrices(
        packageCode,
        hotelCode,
        selectedMonth.format('YYYY-MM')
      )

      const nextPriceMap = {}
      ;(response?.prices || []).forEach((item) => {
        nextPriceMap[item.priceDate] = item.salePrice
      })

      setPriceMap(nextPriceMap)
      persistedPriceMapRef.current = nextPriceMap
    } catch (error) {
      message.error(error?.error || '加载包价每日价格失败')
    } finally {
      setLoading(false)
    }
  }, [hotelCode, packageCode, selectedMonth])

  useEffect(() => {
    loadDailyPrices()
  }, [loadDailyPrices])

  const handlePriceChange = (dateKey, value) => {
    setPriceMap((prev) => ({
      ...prev,
      [dateKey]: value
    }))
  }

  const normalizeResponsePrices = (response) => {
    const nextPriceMap = {}
    ;(response?.prices || []).forEach((item) => {
      nextPriceMap[item.priceDate] = item.salePrice
    })
    setPriceMap(nextPriceMap)
    persistedPriceMapRef.current = nextPriceMap
  }

  const handlePriceBlur = async (dateKey, rawValue) => {
    if (!hotelCode || !packageCode) {
      return
    }

    const currentValue = normalizePriceValue(rawValue)
    const persistedValue = normalizePriceValue(persistedPriceMapRef.current[dateKey])

    if (currentValue === persistedValue) {
      return
    }

    setSavingDateKey(dateKey)
    try {
      const response = await packageApi.saveDailyPrices(packageCode, {
        hotelCode,
        prices: [
          {
            priceDate: dateKey,
            salePrice: currentValue
          }
        ]
      })

      normalizeResponsePrices(response)
      message.success('保存成功')
    } catch (error) {
      setPriceMap((prev) => ({
        ...prev,
        [dateKey]: persistedPriceMapRef.current[dateKey]
      }))
      message.error(error?.error || '保存包价每日价格失败')
    } finally {
      setSavingDateKey(null)
    }
  }

  const openBatchModal = () => {
    batchForm.setFieldsValue({
      dateRange: [selectedMonth.startOf('month'), selectedMonth.endOf('month')],
      weekdays: ALL_WEEKDAY_VALUES,
      price: undefined
    })
    setBatchVisible(true)
  }

  const handleBatchSave = async () => {
    if (!hotelCode || !packageCode) {
      return
    }

    try {
      const values = await batchForm.validateFields()
      const [startDate, endDate] = values.dateRange
      const weekdays = values.weekdays || []
      const matchedDates = monthDates.filter((date) => {
        const isInRange =
          (date.isAfter(startDate, 'day') || date.isSame(startDate, 'day')) &&
          (date.isBefore(endDate, 'day') || date.isSame(endDate, 'day'))
        return isInRange && weekdays.includes(date.day())
      })

      if (matchedDates.length === 0) {
        message.warning('当前条件下没有匹配到可修改的日期')
        return
      }

      setBatchSaving(true)
      const response = await packageApi.saveDailyPrices(packageCode, {
        hotelCode,
        prices: matchedDates.map((date) => ({
          priceDate: date.format('YYYY-MM-DD'),
          salePrice: values.price
        }))
      })

      normalizeResponsePrices(response)
      setBatchVisible(false)
      message.success('批量修改成功')
    } catch (error) {
      if (error?.errorFields) {
        return
      }
      message.error(error?.error || '批量修改每日价格失败')
    } finally {
      setBatchSaving(false)
    }
  }

  if (!hotelCode) {
    return <Empty description="请先选择酒店" />
  }

  if (!packageCode) {
    return <Empty description="包价信息不存在" />
  }

  const allWeekdaysChecked = selectedWeekdays.length === ALL_WEEKDAY_VALUES.length
  const weekdaysIndeterminate = selectedWeekdays.length > 0 && !allWeekdaysChecked

  return (
    <Card style={{ marginTop: 8 }}>
      <Row gutter={[16, 16]} align="middle" style={{ marginBottom: 16 }}>
        <Col span={24}>
          <div style={{ fontWeight: 500 }}>
            {packageName || packageCode}（{packageCode}）
          </div>
          <div style={{ color: '#8c8c8c', marginTop: 4 }}>
            当前酒店：{hotelCode}
          </div>
        </Col>
        <Col>
          <span style={{ marginRight: 8 }}>月份:</span>
          <DatePicker.MonthPicker
            value={selectedMonth}
            onChange={(value) => value && setSelectedMonth(value)}
            placeholder="选择月份"
            allowClear={false}
          />
        </Col>
        <Col>
          <Button type="primary" onClick={openBatchModal}>
            批量修改
          </Button>
        </Col>
      </Row>

      <Spin spinning={loading}>
        <div style={{ border: '1px solid #f0f0f0', borderRadius: 8, overflow: 'hidden' }}>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(7, minmax(0, 1fr))',
              background: '#fafafa',
              borderBottom: '1px solid #f0f0f0'
            }}
          >
            {WEEKDAY_MAP.map((weekday, index) => {
              const isWeekend = index === 0 || index === 6

              return (
                <div
                  key={weekday}
                  style={{
                    padding: '12px 8px',
                    textAlign: 'center',
                    fontWeight: 600,
                    color: isWeekend ? '#f5222d' : '#262626',
                    borderRight: index < 6 ? '1px solid #f0f0f0' : 'none'
                  }}
                >
                  星期{weekday}
                </div>
              )
            })}
          </div>
          {calendarWeeks.map((week, rowIndex) => (
            <div
              key={`week-${rowIndex}`}
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(7, minmax(0, 1fr))',
                borderBottom: rowIndex < calendarWeeks.length - 1 ? '1px solid #f0f0f0' : 'none'
              }}
            >
              {week.map(({ date, isCurrentMonth }, columnIndex) => {
                const weekday = date.day()
                const isWeekend = weekday === 0 || weekday === 6
                const dateKey = date.format('YYYY-MM-DD')

                return (
                  <div
                    key={dateKey}
                    style={{
                      minHeight: 132,
                      padding: 12,
                      background: isCurrentMonth ? (isWeekend ? '#fff7e6' : '#ffffff') : '#fafafa',
                      borderRight: columnIndex < 6 ? '1px solid #f0f0f0' : 'none',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: 12
                    }}
                  >
                    <div
                      style={{
                        color: isCurrentMonth ? (isWeekend ? '#f5222d' : '#262626') : '#bfbfbf',
                        fontWeight: isCurrentMonth ? 600 : 400
                      }}
                    >
                      <span>{date.format('MM-DD')}</span>
                    </div>
                    {isCurrentMonth ? (
                      <InputNumber
                        style={{ width: '100%' }}
                        min={0}
                        precision={2}
                        step={0.01}
                        value={priceMap[dateKey]}
                        onChange={(value) => handlePriceChange(dateKey, value)}
                        onBlur={(event) => handlePriceBlur(dateKey, event.target.value)}
                        placeholder="价格"
                        prefix="¥"
                        disabled={savingDateKey === dateKey}
                      />
                    ) : (
                      <div
                        style={{
                          flex: 1,
                          borderRadius: 6,
                          background: '#f5f5f5'
                        }}
                      />
                    )}
                  </div>
                )
              })}
            </div>
          ))}
        </div>
      </Spin>
      <Modal
        title="批量修改"
        open={batchVisible}
        onCancel={() => setBatchVisible(false)}
        onOk={handleBatchSave}
        confirmLoading={batchSaving}
        okText="保存"
        cancelText="取消"
        destroyOnHidden
      >
        <Form form={batchForm} layout="vertical">
          <Form.Item
            name="dateRange"
            label="日期段"
            rules={[{ required: true, message: '请选择日期段' }]}
          >
            <DatePicker.RangePicker
              style={{ width: '100%' }}
              allowClear={false}
              disabledDate={(current) => {
                if (!current) {
                  return false
                }
                return current.month() !== selectedMonth.month() || current.year() !== selectedMonth.year()
              }}
            />
          </Form.Item>
          <Form.Item label="周控" required>
            <div>
              <div style={{ marginBottom: 12 }}>
                <Checkbox
                  checked={allWeekdaysChecked}
                  indeterminate={weekdaysIndeterminate}
                  onChange={(e) => {
                    batchForm.setFieldValue('weekdays', e.target.checked ? ALL_WEEKDAY_VALUES : [])
                  }}
                >
                  全选（周日至周六）
                </Checkbox>
              </div>
              <Form.Item
                name="weekdays"
                noStyle
                rules={[{ required: true, message: '请至少选择一个星期' }]}
              >
                <Checkbox.Group
                  options={WEEKDAY_OPTIONS}
                  style={{ display: 'flex', flexWrap: 'wrap', gap: 12 }}
                />
              </Form.Item>
            </div>
          </Form.Item>
          <Form.Item
            name="price"
            label="价格"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              precision={2}
              step={0.01}
              prefix="¥"
              placeholder="请输入价格"
            />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

export default PackageDailyPriceEditor

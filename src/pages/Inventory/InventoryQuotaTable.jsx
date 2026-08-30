import { useState, useEffect, useCallback, useContext, useMemo } from 'react'
import { Select, Button, Modal, Form, Input, DatePicker, message, Table, Tag, Space, Spin } from 'antd'
import { LeftOutlined, RightOutlined, EditOutlined, HistoryOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import PropTypes from 'prop-types'
import api from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { RangePicker } = DatePicker
const { Option } = Select

const WEEKDAY_NAMES = ['日', '一', '二', '三', '四', '五', '六']

/**
 * 共享库存房量控制表格组件
 * 使用 Ant Design Table 横向滚动展示多维度项的库存数据
 */
const InventoryQuotaTable = ({ dimensionType, dimensionItems, dimensionLabel, loading: externalLoading, customFilter }) => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const { user } = useContext(AuthContext)
  const getOp = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  // Internal state
  const [selectedMonth, setSelectedMonth] = useState(dayjs().format('YYYY-MM'))
  const [filterCode, setFilterCode] = useState('全部')
  const [quotaDataMap, setQuotaDataMap] = useState({})
  const [dataLoading, setDataLoading] = useState(false)
  const [editModalVisible, setEditModalVisible] = useState(false)
  const [editRecord, setEditRecord] = useState(null)
  const [batchModalVisible, setBatchModalVisible] = useState(false)
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [logs, setLogs] = useState([])
  const [loadingLogs, setLoadingLogs] = useState(false)

  const [editForm] = Form.useForm()
  const [batchForm] = Form.useForm()

  // 6am cutoff rule
  const getMinEditableDate = useCallback(() => {
    const now = dayjs()
    return now.hour() < 6 ? now.subtract(1, 'day').startOf('day') : now.startOf('day')
  }, [])

  const isDateEditable = useCallback((dateStr) => {
    return !dayjs(dateStr).isBefore(getMinEditableDate(), 'day')
  }, [getMinEditableDate])

  // Generate days for the selected month
  const monthDays = useMemo(() => {
    const [year, mon] = selectedMonth.split('-').map(Number)
    const daysInMonth = new Date(year, mon, 0).getDate()
    const days = []
    for (let i = 1; i <= daysInMonth; i++) {
      const dateStr = `${selectedMonth}-${String(i).padStart(2, '0')}`
      const wd = new Date(year, mon - 1, i).getDay()
      days.push({ dateStr, day: i, weekday: wd })
    }
    return days
  }, [selectedMonth])

  // Data fetching: fetch all dimension items for the selected month
  const fetchData = useCallback(async () => {
    if (!hotelCode || !dimensionItems || dimensionItems.length === 0) return
    setDataLoading(true)
    try {
      const [year, mon] = selectedMonth.split('-').map(Number)
      const daysInMonth = new Date(year, mon, 0).getDate()
      const startDate = `${selectedMonth}-01`
      const endDate = `${selectedMonth}-${String(daysInMonth).padStart(2, '0')}`

      const results = await Promise.all(
        dimensionItems.map(item =>
          api.get('/inventory-quota', {
            params: { hotelCode, dimensionType, dimensionCode: item.code, startDate, endDate }
          }).then(res => ({ code: item.code, data: res?.data || [] }))
            .catch(() => ({ code: item.code, data: [] }))
        )
      )

      const newMap = {}
      results.forEach(({ code, data }) => {
        const dateMap = {}
        data.forEach(item => {
          const dateStr = dayjs(item.quotaDate).format('YYYY-MM-DD')
          dateMap[dateStr] = { quotaLimit: item.quotaLimit, soldCount: item.soldCount || 0 }
        })
        newMap[code] = dateMap
      })
      setQuotaDataMap(newMap)
    } catch (err) {
      console.error('获取库存数据失败:', err)
    } finally {
      setDataLoading(false)
    }
  }, [hotelCode, dimensionType, dimensionItems, selectedMonth])

  useEffect(() => {
    if (dimensionItems && dimensionItems.length > 0) {
      fetchData()
    }
  }, [fetchData])

  // Month navigation
  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  // Filter dimension items
  const filteredItems = useMemo(() => {
    if (filterCode === '全部') return dimensionItems || []
    return (dimensionItems || []).filter(item => item.code === filterCode)
  }, [dimensionItems, filterCode])

  // Transform data to table rows: each dimension item → 3 rows
  const tableData = useMemo(() => {
    const rows = []
    filteredItems.forEach(item => {
      const dimData = quotaDataMap[item.code] || {}
      const soldRow = { key: `${item.code}_sold`, dimCode: item.code, dimName: item.name, type: 'sold', label: '已售' }
      const remainingRow = { key: `${item.code}_remaining`, dimCode: item.code, dimName: item.name, type: 'remaining', label: '剩余可售' }
      const limitRow = { key: `${item.code}_limit`, dimCode: item.code, dimName: item.name, type: 'limit', label: '库存限制' }

      monthDays.forEach(({ dateStr }) => {
        const dayData = dimData[dateStr] || {}
        const quotaLimit = dayData.quotaLimit !== undefined ? dayData.quotaLimit : null
        const soldCount = dayData.soldCount || 0

        soldRow[dateStr] = soldCount || 0
        remainingRow[dateStr] = quotaLimit != null ? Math.max(0, quotaLimit - soldCount) : null
        limitRow[dateStr] = quotaLimit
      })

      rows.push(soldRow, remainingRow, limitRow)
    })
    return rows
  }, [filteredItems, quotaDataMap, monthDays])

  // Table columns
  const columns = useMemo(() => {
    const cols = [
      {
        title: (
          <Space>
            <Button size="small" icon={<LeftOutlined />} onClick={handlePrevMonth}>上月</Button>
            <span style={{ fontWeight: 500, minWidth: 80, textAlign: 'center', display: 'inline-block' }}>{selectedMonth}</span>
            <Button size="small" icon={<RightOutlined />} onClick={handleNextMonth}>下月</Button>
          </Space>
        ),
        dataIndex: 'dimName',
        key: 'dimName',
        fixed: 'left',
        width: 160,
        render: (text, record, index) => {
          return {
            children: text,
            props: { rowSpan: index % 3 === 0 ? 3 : 0 },
          }
        },
      },
      {
        title: '类型',
        dataIndex: 'label',
        key: 'label',
        fixed: 'left',
        width: 90,
      },
    ]

    // Date columns
    monthDays.forEach(({ dateStr, day, weekday }) => {
      const isWeekend = weekday === 0 || weekday === 6
      const editable = isDateEditable(dateStr)
      const isPast = !editable

      cols.push({
        title: (
          <div style={{ textAlign: 'center', fontSize: 12, lineHeight: '1.3' }}>
            <div>{`${String(selectedMonth.split('-')[1]).padStart(2, '0')}.${String(day).padStart(2, '0')}`}</div>
            <div>{WEEKDAY_NAMES[weekday]}</div>
          </div>
        ),
        dataIndex: dateStr,
        key: dateStr,
        width: 65,
        onHeaderCell: () => ({
          style: { background: isWeekend ? '#fffbf0' : undefined, padding: '4px 2px' },
        }),
        onCell: (record) => {
          const isLimit = record.type === 'limit'
          const clickable = isLimit && editable
          return {
            style: {
              background: isWeekend ? '#fffbf0' : undefined,
              opacity: isPast ? 0.5 : 1,
              cursor: clickable ? 'pointer' : 'default',
              textAlign: 'center',
              padding: '4px 2px',
            },
            onClick: clickable ? () => handleCellClick(record, dateStr) : undefined,
          }
        },
        render: (value, record) => {
          if (value == null) return '-'
          if (record.type === 'remaining' && value === 0) {
            return <span style={{ color: '#ff4d4f', fontWeight: 500 }}>0</span>
          }
          if (record.type === 'remaining') {
            return <span style={{ color: '#52c41a' }}>{value}</span>
          }
          if (record.type === 'sold') {
            return <span style={{ color: '#1890ff' }}>{value || '-'}</span>
          }
          // limit row
          return value != null ? value : '-'
        },
      })
    })

    return cols
  }, [selectedMonth, monthDays, isDateEditable])

  // Cell click handler for limit row
  const handleCellClick = (record, dateStr) => {
    if (!isDateEditable(dateStr)) {
      message.warning('不能修改过去日期的库存限制')
      return
    }
    const dimData = quotaDataMap[record.dimCode] || {}
    const dayData = dimData[dateStr] || {}
    setEditRecord({ dimCode: record.dimCode, dimName: record.dimName, dateStr })
    editForm.setFieldsValue({ quotaLimit: dayData.quotaLimit ?? '' })
    setEditModalVisible(true)
  }

  // Single edit save
  const handleEditSave = async () => {
    try {
      const values = await editForm.validateFields()
      const limit = values.quotaLimit !== '' && values.quotaLimit != null ? parseInt(values.quotaLimit) : null
      await api.post('/inventory-quota', {
        hotelCode,
        dimensionType,
        dimensionCode: editRecord.dimCode,
        quotaDate: editRecord.dateStr,
        quotaLimit: limit,
      }, { headers: { 'X-Operator-Name': getOp() } })
      message.success('保存成功')
      setEditModalVisible(false)
      fetchData()
    } catch (err) {
      if (!err.errorFields) message.error('保存失败')
    }
  }

  // Batch edit save
  const handleBatchSave = async () => {
    try {
      const values = await batchForm.validateFields()
      const limit = values.quotaLimit !== '' && values.quotaLimit != null ? parseInt(values.quotaLimit) : null
      const [start, end] = values.dateRange
      const dimCode = values.dimensionCode
      const records = []
      let cur = start.startOf('day')
      while (cur.isBefore(end) || cur.isSame(end, 'day')) {
        const dateStr = cur.format('YYYY-MM-DD')
        if (isDateEditable(dateStr)) {
          records.push({
            hotelCode,
            dimensionType,
            dimensionCode: dimCode,
            quotaDate: dateStr,
            quotaLimit: limit,
          })
        }
        cur = cur.add(1, 'day')
      }
      if (records.length === 0) {
        message.warning('没有可修改的日期')
        return
      }
      await api.post('/inventory-quota/batch', records, { headers: { 'X-Operator-Name': getOp() } })
      message.success(`批量保存成功，共 ${records.length} 天`)
      setBatchModalVisible(false)
      batchForm.resetFields()
      fetchData()
    } catch (err) {
      if (!err.errorFields) message.error('批量保存失败')
    }
  }

  // Log fetch
  const handleShowLogs = async () => {
    setLogModalVisible(true)
    setLoadingLogs(true)
    try {
      const dimCode = filterCode === '全部' ? '' : filterCode
      const res = await api.get('/inventory-quota/logs', {
        params: { hotelCode, dimensionType, dimensionCode: dimCode }
      })
      setLogs(res?.data || [])
    } catch (e) {
      console.error('获取日志失败:', e)
    } finally {
      setLoadingLogs(false)
    }
  }

  // Log table columns
  const logColumns = [
    { title: '操作时间', dataIndex: 'operationTime', width: 170, render: v => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-' },
    { title: '操作人', dataIndex: 'operatorName', width: 100 },
    { title: dimensionLabel, dataIndex: 'dimensionCode', width: 120, render: v => {
      if (!v) return '全部'
      const item = (dimensionItems || []).find(i => i.code === v)
      return item ? item.name : v
    }},
    { title: '类型', dataIndex: 'operationType', width: 90, render: v => <Tag color={v === 'batch' ? 'blue' : 'green'}>{v === 'batch' ? '批量' : '单个'}</Tag> },
    {
      title: '明细', dataIndex: 'detail', render: v => {
        if (!v) return '-'
        try {
          const d = JSON.parse(v)
          if (d.dates) return `${d.dates}，限制设为 ${d.quotaLimit}`
          return `${d.date}：${d.old} → ${d.new}`
        } catch { return v }
      }
    },
  ]

  if (!hotelCode) return <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>请先选择酒店</div>

  return (
    <div>
      {/* Toolbar: filter + action buttons */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        {customFilter ? customFilter : (
          <Space>
            <span>{dimensionLabel}筛选：</span>
            <Select
              value={filterCode}
              onChange={setFilterCode}
              style={{ width: 220 }}
              showSearch
              optionFilterProp="children"
            >
              <Option value="全部">全部</Option>
              {(dimensionItems || []).map(item => (
                <Option key={item.code} value={item.code}>{item.name}</Option>
              ))}
            </Select>
          </Space>
        )}
        <Space>
          <Button icon={<EditOutlined />} onClick={() => { batchForm.resetFields(); setBatchModalVisible(true) }}>
            批量修改
          </Button>
          <Button icon={<HistoryOutlined />} onClick={handleShowLogs}>
            日志
          </Button>
        </Space>
      </div>

      {/* Main table */}
      <Spin spinning={dataLoading || !!externalLoading}>
        <Table
          columns={columns}
          dataSource={tableData}
          pagination={false}
          bordered
          size="small"
          scroll={{ x: 'max-content', y: 500 }}
          rowKey="key"
        />
      </Spin>

      {/* Single edit modal */}
      <Modal
        title={editRecord ? `维护 ${editRecord.dimName} - ${editRecord.dateStr} 库存限制` : '编辑库存限制'}
        open={editModalVisible}
        onOk={handleEditSave}
        onCancel={() => setEditModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={400}
      >
        <Form form={editForm} layout="vertical">
          <Form.Item name="quotaLimit" label="库存限制（留空表示不限制）">
            <Input type="number" min={0} placeholder="留空表示不限制" />
          </Form.Item>
        </Form>
      </Modal>

      {/* Batch edit modal */}
      <Modal
        title="批量修改库存限制"
        open={batchModalVisible}
        onOk={handleBatchSave}
        onCancel={() => { setBatchModalVisible(false); batchForm.resetFields() }}
        okText="确认"
        cancelText="取消"
        width={500}
      >
        <Form form={batchForm} layout="vertical">
          <Form.Item name="dimensionCode" label={dimensionLabel} rules={[{ required: true, message: `请选择${dimensionLabel}` }]}>
            <Select placeholder={`请选择${dimensionLabel}`} showSearch optionFilterProp="children">
              {(dimensionItems || []).map(item => (
                <Option key={item.code} value={item.code}>{item.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="dateRange" label="日期范围" rules={[{ required: true, message: '请选择日期范围' }]}>
            <RangePicker style={{ width: '100%' }} disabledDate={d => d && d.isBefore(getMinEditableDate(), 'day')} />
          </Form.Item>
          <Form.Item name="quotaLimit" label="库存限制（留空表示不限制）">
            <Input type="number" min={0} placeholder="留空表示不限制" />
          </Form.Item>
        </Form>
      </Modal>

      {/* Log modal */}
      <Modal
        title="房量控制操作日志"
        open={logModalVisible}
        onCancel={() => setLogModalVisible(false)}
        footer={null}
        width={850}
      >
        <Table
          dataSource={logs}
          rowKey="id"
          loading={loadingLogs}
          size="small"
          pagination={{ pageSize: 10 }}
          scroll={{ y: 400 }}
          columns={logColumns}
        />
      </Modal>
    </div>
  )
}

InventoryQuotaTable.propTypes = {
  dimensionType: PropTypes.string.isRequired,
  dimensionItems: PropTypes.arrayOf(PropTypes.shape({
    code: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
  })).isRequired,
  dimensionLabel: PropTypes.string.isRequired,
  loading: PropTypes.bool,
  customFilter: PropTypes.node,
}

export default InventoryQuotaTable

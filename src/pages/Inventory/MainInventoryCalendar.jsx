import { useState, useEffect, useCallback, useContext, useMemo } from 'react'
import { Table, Select, Button, Space, Modal, Form, Input, message, Spin, Tag } from 'antd'
import { LeftOutlined, RightOutlined, FilterOutlined, ReloadOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { hotelRoomTypeApi, ratePlanApi, hotelPriceApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { Option } = Select
const WEEKDAY_NAMES = ['日', '一', '二', '三', '四', '五', '六']

/**
 * 主要房控日历
 * 支持3种视图：库存、房价、开关房
 */
const MainInventoryCalendar = () => {
  const { selectedHotel: hotelCode, selectedHotelId } = useHotelContext()
  const { user } = useContext(AuthContext)
  const getOp = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  const [selectedMonth, setSelectedMonth] = useState(dayjs().format('YYYY-MM'))
  const [viewType, setViewType] = useState('库存')
  const [filterChannel, setFilterChannel] = useState('全部渠道')
  const [filterRoomType, setFilterRoomType] = useState('全部房型')
  const [filterRatePlan, setFilterRatePlan] = useState('全部价格计划')
  const [roomTypes, setRoomTypes] = useState([])
  const [ratePlans, setRatePlans] = useState([])
  const [channels, setChannels] = useState([])
  const [loading, setLoading] = useState(false)

  // 数据状态
  const [pmsData, setPmsData] = useState([])       // 库存模式：PMS库存数据
  const [priceData, setPriceData] = useState([])    // 房价模式：价格数据
  const [roomStatusData, setRoomStatusData] = useState([]) // 开关房模式：房态数据

  // 编辑弹窗
  const [editModalVisible, setEditModalVisible] = useState(false)
  const [editRecord, setEditRecord] = useState(null)
  const [editForm] = Form.useForm()

  // 加载房型列表
  useEffect(() => {
    if (!selectedHotelId) return
    hotelRoomTypeApi.getHotelRoomTypes(selectedHotelId)
      .then(res => setRoomTypes((res?.data || []).filter(r => r.status === 'active')))
      .catch(() => {})
  }, [selectedHotelId])

  // 加载价格计划列表
  useEffect(() => {
    if (!selectedHotelId) return
    ratePlanApi.getRatePlans(selectedHotelId)
      .then(res => setRatePlans(res?.data || []))
      .catch(() => {})
  }, [selectedHotelId])

  // 加载渠道列表
  useEffect(() => {
    api.get('/channel-codes/third-level')
      .then(res => setChannels(Array.isArray(res) ? res : (res?.data || [])))
      .catch(() => {})
  }, [])

  // 生成当月日期列表
  const monthDays = useMemo(() => {
    const [year, mon] = selectedMonth.split('-').map(Number)
    const daysInMonth = new Date(year, mon, 0).getDate()
    return Array.from({ length: daysInMonth }, (_, i) => {
      const day = i + 1
      const dateStr = `${selectedMonth}-${String(day).padStart(2, '0')}`
      const wd = new Date(year, mon - 1, day).getDay()
      return { dateStr, day, weekday: wd }
    })
  }, [selectedMonth])

  const dateRange = useMemo(() => {
    const [year, mon] = selectedMonth.split('-').map(Number)
    const daysInMonth = new Date(year, mon, 0).getDate()
    return {
      startDate: `${selectedMonth}-01`,
      endDate: `${selectedMonth}-${String(daysInMonth).padStart(2, '0')}`,
    }
  }, [selectedMonth])

  // 加载数据
  const fetchData = useCallback(async () => {
    if (!hotelCode || roomTypes.length === 0) return
    setLoading(true)
    try {
      if (viewType === '库存') {
        const res = await api.get('/pms-inventory', {
          params: { hotelCode, ...dateRange }
        })
        setPmsData(res?.data || [])
      } else if (viewType === '房价') {
        const res = await hotelPriceApi.getPriceQueryData(hotelCode, null, dateRange.startDate, dateRange.endDate)
        setPriceData(res?.data?.prices || res?.prices || [])
      } else if (viewType === '开关房') {
        // 为每个房型查询房态
        const results = await Promise.all(
          roomTypes.map(rt =>
            api.get('/room-status', {
              params: { hotelCode, dimensionType: 'room_type', dimensionCode: rt.roomTypeCode, ...dateRange }
            }).then(res => ({ code: rt.roomTypeCode, data: res?.data || [] }))
              .catch(() => ({ code: rt.roomTypeCode, data: [] }))
          )
        )
        setRoomStatusData(results)
      }
    } catch (err) {
      console.error('加载数据失败:', err)
    } finally {
      setLoading(false)
    }
  }, [hotelCode, roomTypes, selectedMonth, viewType, dateRange])

  useEffect(() => { fetchData() }, [fetchData])

  // 月份切换
  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  // 筛选后的房型
  const filteredRoomTypes = useMemo(() => {
    if (filterRoomType === '全部房型') return roomTypes
    return roomTypes.filter(rt => rt.roomTypeCode === filterRoomType)
  }, [roomTypes, filterRoomType])

  // ========== 库存模式 ==========
  const inventoryTableData = useMemo(() => {
    if (viewType !== '库存') return []
    const dataMap = {}
    pmsData.forEach(item => {
      if (!dataMap[item.roomTypeCode]) dataMap[item.roomTypeCode] = {}
      dataMap[item.roomTypeCode][item.inventoryDate] = item
    })
    const rows = []
    filteredRoomTypes.forEach(rt => {
      const rtData = dataMap[rt.roomTypeCode] || {}
      const availRow = { key: `${rt.roomTypeCode}_available`, roomTypeCode: rt.roomTypeCode, roomTypeName: rt.roomTypeName, type: 'available', label: '可售', color: '#1890ff' }
      const soldRow = { key: `${rt.roomTypeCode}_sold`, roomTypeCode: rt.roomTypeCode, roomTypeName: rt.roomTypeName, type: 'sold', label: '已售', color: '#faad14' }
      monthDays.forEach(({ dateStr }) => {
        const d = rtData[dateStr]
        availRow[dateStr] = d ? d.availableRooms : '-'
        soldRow[dateStr] = d ? d.soldCount : '-'
      })
      rows.push(availRow, soldRow)
    })
    return rows
  }, [viewType, pmsData, filteredRoomTypes, monthDays])

  // ========== 房价模式 ==========
  const priceTableData = useMemo(() => {
    if (viewType !== '房价') return []
    // 按房型+房价码分组
    const dataMap = {}
    priceData.forEach(item => {
      const key = `${item.roomTypeCode}_${item.rateCode}`
      if (!dataMap[key]) dataMap[key] = {}
      const dateStr = typeof item.priceDate === 'string' ? item.priceDate.substring(0, 10) : dayjs(item.priceDate).format('YYYY-MM-DD')
      dataMap[key][dateStr] = item
    })

    const filteredPlans = filterRatePlan === '全部价格计划'
      ? ratePlans
      : ratePlans.filter(rp => rp.rateCode === filterRatePlan)

    const rows = []
    filteredRoomTypes.forEach(rt => {
      filteredPlans.forEach(rp => {
        const key = `${rt.roomTypeCode}_${rp.rateCode}`
        const rpData = dataMap[key] || {}
        const row = {
          key,
          roomTypeCode: rt.roomTypeCode,
          roomTypeName: rt.roomTypeName,
          rateCode: rp.rateCode,
          rateName: rp.rateName,
          _planCount: filteredPlans.length,
        }
        monthDays.forEach(({ dateStr }) => {
          const d = rpData[dateStr]
          row[dateStr] = d ? d.priceWithTax : null
        })
        rows.push(row)
      })
    })
    return rows
  }, [viewType, priceData, filteredRoomTypes, ratePlans, filterRatePlan, monthDays])

  // ========== 开关房模式 ==========
  const roomStatusTableData = useMemo(() => {
    if (viewType !== '开关房') return []
    const statusMap = {}
    roomStatusData.forEach(({ code, data }) => {
      statusMap[code] = {}
      data.forEach(item => {
        const dateStr = typeof item.statusDate === 'string' ? item.statusDate.substring(0, 10) : dayjs(item.statusDate).format('YYYY-MM-DD')
        statusMap[code][dateStr] = item.isOpen
      })
    })
    const rows = []
    filteredRoomTypes.forEach(rt => {
      const rtStatus = statusMap[rt.roomTypeCode] || {}
      const availRow = { key: `${rt.roomTypeCode}_available`, roomTypeCode: rt.roomTypeCode, roomTypeName: rt.roomTypeName, type: 'available', label: '可售', color: '#1890ff' }
      const soldRow = { key: `${rt.roomTypeCode}_sold`, roomTypeCode: rt.roomTypeCode, roomTypeName: rt.roomTypeName, type: 'sold', label: '已售', color: '#faad14' }
      const statusRow = { key: `${rt.roomTypeCode}_status`, roomTypeCode: rt.roomTypeCode, roomTypeName: rt.roomTypeName, type: 'status', label: '房态', color: '#52c41a' }

      // 库存数据从pmsData获取
      const pmsMap = {}
      pmsData.forEach(item => {
        if (item.roomTypeCode === rt.roomTypeCode) pmsMap[item.inventoryDate] = item
      })

      monthDays.forEach(({ dateStr }) => {
        const d = pmsMap[dateStr]
        availRow[dateStr] = d ? d.availableRooms : '-'
        soldRow[dateStr] = d ? d.soldCount : '-'
        // 房态：无记录默认"开"
        const isOpen = rtStatus[dateStr] !== undefined ? rtStatus[dateStr] : true
        statusRow[dateStr] = isOpen ? '开' : '关'
      })
      rows.push(availRow, soldRow, statusRow)
    })
    return rows
  }, [viewType, roomStatusData, pmsData, filteredRoomTypes, monthDays])

  // 开关房模式也需要加载PMS数据
  useEffect(() => {
    if (viewType === '开关房' && hotelCode) {
      api.get('/pms-inventory', { params: { hotelCode, ...dateRange } })
        .then(res => setPmsData(res?.data || []))
        .catch(() => {})
    }
  }, [viewType, hotelCode, dateRange])

  // 当前表格数据
  const tableData = viewType === '库存' ? inventoryTableData : viewType === '房价' ? priceTableData : roomStatusTableData

  // 每个房型的行数
  const rowsPerGroup = viewType === '库存' ? 2 : viewType === '房价' ? (filterRatePlan === '全部价格计划' ? ratePlans.length : 1) : 3

  // 房态切换
  const handleToggleStatus = async (record, dateStr) => {
    const currentVal = record[dateStr]
    const newIsOpen = currentVal !== '开'
    try {
      const dateObj = dateStr
      await api.post('/room-status', {
        hotelCode,
        dimensionType: 'room_type',
        dimensionCode: record.roomTypeCode,
        statusDate: dateObj,
        isOpen: newIsOpen,
      }, { headers: { 'X-Operator-Name': getOp() } })
      message.success(`${record.roomTypeName} ${dateStr} 已${newIsOpen ? '开房' : '关房'}`)
      fetchData()
    } catch (err) {
      message.error('操作失败')
    }
  }

  // 房价编辑
  const handlePriceClick = (record, dateStr) => {
    setEditRecord({ ...record, dateStr, editType: 'price' })
    editForm.setFieldsValue({ price: record[dateStr] || '' })
    setEditModalVisible(true)
  }

  // 库存编辑（编辑可售和已售）
  const handleInventoryClick = (record, dateStr) => {
    // 找到同房型的另一行数据
    const pmsMap = {}
    pmsData.forEach(item => {
      if (item.roomTypeCode === record.roomTypeCode) pmsMap[item.inventoryDate] = item
    })
    const d = pmsMap[dateStr]
    setEditRecord({ ...record, dateStr, editType: 'inventory', pmsItem: d })
    editForm.setFieldsValue({
      available: d?.availableRooms || 0,
      sold: d?.soldCount || 0,
    })
    setEditModalVisible(true)
  }

  // 保存编辑
  const handleEditSave = async () => {
    try {
      const values = await editForm.validateFields()
      if (editRecord.editType === 'price') {
        const price = parseFloat(values.price)
        await hotelPriceApi.savePrice({
          hotelCode,
          rateCode: editRecord.rateCode,
          roomTypeCode: editRecord.roomTypeCode,
          priceDate: editRecord.dateStr,
          priceWithTax: price,
          priceWithoutTax: price,
          status: 'active',
        }, { headers: { 'X-Operator-Name': getOp() } })
        message.success('房价保存成功')
      } else {
        // 库存编辑 - 更新PMS库存
        await api.post('/pms-inventory', {
          hotelCode,
          roomTypeCode: editRecord.roomTypeCode,
          inventoryDate: editRecord.dateStr,
          physicalRooms: editRecord.pmsItem?.physicalRooms || 0,
          availableRooms: parseInt(values.available),
          maintenanceRooms: editRecord.pmsItem?.maintenanceRooms || 0,
          overbookCount: editRecord.pmsItem?.overbookCount || 0,
        })
        message.success('库存保存成功')
      }
      setEditModalVisible(false)
      fetchData()
    } catch (err) {
      if (!err.errorFields) message.error('保存失败')
    }
  }

  // 表格列
  const columns = useMemo(() => {
    const planCount = filterRatePlan === '全部价格计划' ? ratePlans.length : 1
    const groupSize = viewType === '库存' ? 2 : viewType === '房价' ? (planCount || 1) : 3

    const cols = [
      {
        title: (
          <Space>
            <Button size="small" icon={<LeftOutlined />} onClick={handlePrevMonth}>上月</Button>
            <span style={{ fontWeight: 500, minWidth: 80, textAlign: 'center', display: 'inline-block' }}>{selectedMonth}</span>
            <Button size="small" icon={<RightOutlined />} onClick={handleNextMonth}>下月</Button>
          </Space>
        ),
        key: 'roomType',
        width: 170,
        fixed: 'left',
        render: (_, record, index) => ({
          children: (
            <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
              <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
              <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
              <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
            </div>
          ),
          props: { rowSpan: index % groupSize === 0 ? groupSize : 0 },
        }),
      },
      {
        title: viewType === '房价' ? '房价码' : '库存类型',
        dataIndex: viewType === '房价' ? 'rateName' : 'label',
        key: 'subType',
        width: 90,
        fixed: 'left',
        render: (text, record) => (
          <div style={{ fontSize: 12, color: record.color || '#52c41a', fontWeight: 500 }}>{text}</div>
        ),
      },
    ]

    // 日期列
    monthDays.forEach(({ dateStr, day, weekday }) => {
      const isWeekend = weekday === 0 || weekday === 6
      cols.push({
        title: (
          <div style={{ textAlign: 'center', fontSize: 12, lineHeight: '1.3' }}>
            <div>{`${String(selectedMonth.split('-')[1]).padStart(2, '0')}.${String(day).padStart(2, '0')}`}</div>
            <div style={{ fontSize: 10, color: '#999' }}>{WEEKDAY_NAMES[weekday]}</div>
          </div>
        ),
        dataIndex: dateStr,
        key: dateStr,
        width: viewType === '房价' ? 70 : 55,
        onHeaderCell: () => ({
          style: { background: isWeekend ? '#fffbf0' : undefined, padding: '4px 2px' },
        }),
        onCell: (record) => {
          const isStatusRow = record.type === 'status'
          const isOpen = record[dateStr] === '开'
          const clickable = viewType === '开关房' && isStatusRow
          const isPriceCell = viewType === '房价'
          const isInventoryCell = viewType === '库存'
          return {
            style: {
              background: isStatusRow ? (isOpen ? '#f6ffed' : '#fff1f0') : (isWeekend ? '#fffbf0' : undefined),
              textAlign: 'center',
              padding: '4px 2px',
              cursor: (clickable || isPriceCell || isInventoryCell) ? 'pointer' : 'default',
            },
            onClick: clickable
              ? () => handleToggleStatus(record, dateStr)
              : isPriceCell
                ? () => handlePriceClick(record, dateStr)
                : isInventoryCell
                  ? () => handleInventoryClick(record, dateStr)
                  : undefined,
          }
        },
        render: (value, record) => {
          if (value == null || value === '-') return <span style={{ color: '#ccc' }}>-</span>
          if (viewType === '房价') {
            return <span style={{ fontSize: 11, color: '#52c41a', fontWeight: 500 }}>¥{value}</span>
          }
          if (record.type === 'status') {
            return <span style={{ fontSize: 11, color: value === '开' ? '#52c41a' : '#f5222d', fontWeight: 500 }}>{value}</span>
          }
          return <span style={{ fontSize: 11, color: record.color, fontWeight: 500 }}>{value}</span>
        },
      })
    })

    return cols
  }, [selectedMonth, monthDays, viewType, ratePlans, filterRatePlan])

  if (!hotelCode) return <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>请先选择酒店</div>

  return (
    <div>
      {/* 工具栏 */}
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 12, flexWrap: 'wrap' }}>
        <Select value={filterChannel} style={{ width: 160 }} onChange={setFilterChannel} showSearch optionFilterProp="children">
          <Option value="全部渠道">全部渠道</Option>
          {channels.map(c => (
            <Option key={c.code} value={c.code}>{c.name}</Option>
          ))}
        </Select>
        <Select value={viewType} style={{ width: 120 }} onChange={setViewType}>
          <Option value="库存">库存</Option>
          <Option value="房价">房价</Option>
          <Option value="开关房">开关房</Option>
        </Select>
        <Select value={filterRoomType} style={{ width: 200 }} onChange={setFilterRoomType} showSearch optionFilterProp="children">
          <Option value="全部房型">全部房型</Option>
          {roomTypes.map(rt => (
            <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>{rt.roomTypeName}（{rt.roomTypeCode}）</Option>
          ))}
        </Select>
        {viewType === '房价' && (
          <Select value={filterRatePlan} style={{ width: 200 }} onChange={setFilterRatePlan} showSearch optionFilterProp="children">
            <Option value="全部价格计划">全部价格计划</Option>
            {ratePlans.map(rp => (
              <Option key={rp.rateCode} value={rp.rateCode}>{rp.rateName}（{rp.rateCode}）</Option>
            ))}
          </Select>
        )}
        <Button icon={<FilterOutlined />} onClick={fetchData}>筛选</Button>
        <Button icon={<ReloadOutlined />} onClick={fetchData}>刷新</Button>
      </div>

      {/* 数据表格 */}
      <Spin spinning={loading}>
        <Table
          columns={columns}
          dataSource={tableData}
          rowKey="key"
          pagination={false}
          scroll={{ x: 'max-content', y: 600 }}
          bordered
          size="small"
          locale={{ emptyText: '暂无数据' }}
        />
      </Spin>

      {/* 编辑弹窗 */}
      <Modal
        title={editRecord?.editType === 'price'
          ? `编辑房价 - ${editRecord?.roomTypeName} / ${editRecord?.rateName} / ${editRecord?.dateStr}`
          : `编辑库存 - ${editRecord?.roomTypeName} / ${editRecord?.dateStr}`}
        open={editModalVisible}
        onOk={handleEditSave}
        onCancel={() => setEditModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={400}
      >
        <Form form={editForm} layout="vertical">
          {editRecord?.editType === 'price' ? (
            <Form.Item name="price" label="房价（含税）" rules={[{ required: true, message: '请输入房价' }]}>
              <Input type="number" min={0} placeholder="请输入房价" prefix="¥" />
            </Form.Item>
          ) : (
            <>
              <Form.Item name="available" label="可售数量" rules={[{ required: true, message: '请输入可售数量' }]}>
                <Input type="number" min={0} placeholder="请输入可售数量" />
              </Form.Item>
              <Form.Item name="sold" label="已售数量">
                <Input type="number" min={0} placeholder="已售数量（只读）" disabled />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  )
}

export default MainInventoryCalendar

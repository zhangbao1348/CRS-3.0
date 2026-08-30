import { useState, useEffect, useCallback, useMemo } from 'react'
import { Table, Select, Button, Modal, Tag, Spin, Space } from 'antd'
import { LeftOutlined, RightOutlined, SyncOutlined, FilterOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { hotelRoomTypeApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'

const { Option } = Select

const WEEKDAY_NAMES = ['日', '一', '二', '三', '四', '五', '六']

const INVENTORY_TYPES = [
  { key: 'physical', label: '物理房型数', color: '#1890ff' },
  { key: 'sold', label: '已售', color: '#52c41a' },
  { key: 'maintenance', label: '维修', color: '#faad14' },
  { key: 'overbooked', label: '超预订', color: '#f5222d' },
  { key: 'available', label: '可售', color: '#13c2c2' },
]

const PMSInventoryCalendar = () => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const [selectedMonth, setSelectedMonth] = useState(dayjs().format('YYYY-MM'))
  const [selectedRoomType, setSelectedRoomType] = useState('全部房型')
  const [roomTypes, setRoomTypes] = useState([])
  const [pmsData, setPmsData] = useState([])
  const [loading, setLoading] = useState(false)
  const [roomTypesLoading, setRoomTypesLoading] = useState(false)
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [logs, setLogs] = useState([])
  const [loadingLogs, setLoadingLogs] = useState(false)

  // 加载房型列表
  useEffect(() => {
    if (!hotelCode) return
    setRoomTypesLoading(true)
    hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
      .then(res => setRoomTypes((res?.data || []).filter(r => r.status === 'active')))
      .catch(() => {})
      .finally(() => setRoomTypesLoading(false))
  }, [hotelCode])

  // 生成当月日期列表
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

  // 加载PMS库存数据
  const fetchData = useCallback(async () => {
    if (!hotelCode || roomTypes.length === 0) return
    setLoading(true)
    try {
      const [year, mon] = selectedMonth.split('-').map(Number)
      const daysInMonth = new Date(year, mon, 0).getDate()
      const startDate = `${selectedMonth}-01`
      const endDate = `${selectedMonth}-${String(daysInMonth).padStart(2, '0')}`

      const params = { hotelCode, startDate, endDate }
      if (selectedRoomType !== '全部房型') {
        params.roomTypeCode = selectedRoomType
      }
      const res = await api.get('/pms-inventory', { params })
      setPmsData(res?.data || [])
    } catch (err) {
      console.error('获取PMS库存数据失败:', err)
    } finally {
      setLoading(false)
    }
  }, [hotelCode, selectedMonth, roomTypes, selectedRoomType])

  useEffect(() => { fetchData() }, [fetchData])

  // 月份切换
  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  // 筛选后的房型列表
  const filteredRoomTypes = useMemo(() => {
    if (selectedRoomType === '全部房型') return roomTypes
    return roomTypes.filter(rt => rt.roomTypeCode === selectedRoomType)
  }, [roomTypes, selectedRoomType])

  // 构建数据Map: { roomTypeCode: { dateStr: { physicalRooms, soldCount, ... } } }
  const dataMap = useMemo(() => {
    const map = {}
    pmsData.forEach(item => {
      const rtCode = item.roomTypeCode
      const dateStr = item.inventoryDate
      if (!map[rtCode]) map[rtCode] = {}
      map[rtCode][dateStr] = item
    })
    return map
  }, [pmsData])

  // 转换为表格行数据：每个房型5行
  const tableData = useMemo(() => {
    const rows = []
    filteredRoomTypes.forEach(rt => {
      INVENTORY_TYPES.forEach(invType => {
        const row = {
          key: `${rt.roomTypeCode}_${invType.key}`,
          roomTypeCode: rt.roomTypeCode,
          roomTypeName: rt.roomTypeName,
          inventoryType: invType.key,
          inventoryLabel: invType.label,
          inventoryColor: invType.color,
        }
        monthDays.forEach(({ dateStr }) => {
          const dayData = (dataMap[rt.roomTypeCode] || {})[dateStr]
          if (!dayData) {
            row[dateStr] = '-'
          } else {
            switch (invType.key) {
              case 'physical': row[dateStr] = dayData.physicalRooms; break
              case 'sold': row[dateStr] = dayData.soldCount; break
              case 'maintenance': row[dateStr] = dayData.maintenanceRooms || '-'; break
              case 'overbooked': row[dateStr] = dayData.overbookCount || '-'; break
              case 'available': row[dateStr] = dayData.availableRooms; break
              default: row[dateStr] = '-'
            }
          }
        })
        rows.push(row)
      })
    })
    return rows
  }, [filteredRoomTypes, dataMap, monthDays])

  // 表格列定义
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
        key: 'roomType',
        width: 160,
        fixed: 'left',
        render: (_, record, index) => ({
          children: (
            <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
              <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
              <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
              <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
            </div>
          ),
          props: { rowSpan: index % 5 === 0 ? 5 : 0 },
        }),
      },
      {
        title: '库存类型',
        dataIndex: 'inventoryLabel',
        key: 'inventoryType',
        width: 90,
        fixed: 'left',
        render: (text, record) => (
          <div style={{ fontSize: 12, color: record.inventoryColor, fontWeight: 500 }}>{text}</div>
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
        width: 60,
        onHeaderCell: () => ({
          style: { background: isWeekend ? '#fffbf0' : undefined, padding: '4px 2px' },
        }),
        onCell: () => ({
          style: {
            background: isWeekend ? '#fffbf0' : undefined,
            textAlign: 'center',
            padding: '4px 2px',
          },
        }),
        render: (value, record) => {
          if (value === '-' || value == null) return <span style={{ color: '#ccc' }}>-</span>
          return <span style={{ fontSize: 12, color: record.inventoryColor, fontWeight: 500 }}>{value}</span>
        },
      })
    })

    return cols
  }, [selectedMonth, monthDays])

  // 查看同步日志
  const handleShowSyncLogs = async () => {
    setLogModalVisible(true)
    setLoadingLogs(true)
    try {
      const res = await api.get('/pms-inventory/sync-logs', { params: { hotelCode } })
      setLogs(res?.data || [])
    } catch (e) {
      console.error('获取同步日志失败:', e)
    } finally {
      setLoadingLogs(false)
    }
  }

  if (!hotelCode) return <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>请先选择酒店</div>

  return (
    <div>
      {/* 工具栏 */}
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 16 }}>
        <Select
          value={selectedRoomType}
          style={{ width: 220 }}
          onChange={setSelectedRoomType}
          showSearch
          optionFilterProp="children"
        >
          <Option value="全部房型">全部房型</Option>
          {roomTypes.map(rt => (
            <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>
              {rt.roomTypeName}（{rt.roomTypeCode}）
            </Option>
          ))}
        </Select>
        <Button icon={<FilterOutlined />} onClick={fetchData}>筛选</Button>
        <Button icon={<SyncOutlined />} onClick={handleShowSyncLogs}>查看同步日志</Button>
      </div>

      {/* 数据表格 */}
      <Spin spinning={loading || roomTypesLoading}>
        <Table
          columns={columns}
          dataSource={tableData}
          rowKey="key"
          pagination={false}
          scroll={{ x: 'max-content', y: 500 }}
          bordered
          size="small"
          locale={{ emptyText: '暂无PMS库存数据' }}
        />
      </Spin>

      {/* 同步日志弹窗 */}
      <Modal
        title="PMS库存同步日志"
        open={logModalVisible}
        onCancel={() => setLogModalVisible(false)}
        footer={null}
        width={750}
      >
        <Table
          dataSource={logs}
          rowKey="id"
          loading={loadingLogs}
          size="small"
          pagination={{ pageSize: 10 }}
          scroll={{ y: 400 }}
          columns={[
            {
              title: '同步时间', dataIndex: 'syncTime', width: 170,
              render: v => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-',
            },
            {
              title: '同步类型', dataIndex: 'syncType', width: 100,
              render: v => <Tag color={v === 'full' ? 'blue' : 'green'}>{v === 'full' ? '全量同步' : '增量同步'}</Tag>,
            },
            {
              title: '状态', dataIndex: 'syncStatus', width: 80,
              render: v => <Tag color={v === 'success' ? 'green' : 'red'}>{v === 'success' ? '成功' : '失败'}</Tag>,
            },
            {
              title: '详情', dataIndex: 'detail',
              render: (v, record) => {
                if (record.syncStatus === 'failed' && record.errorMessage) return record.errorMessage
                if (!v) return '-'
                try {
                  const d = JSON.parse(v)
                  return d.summary || v
                } catch { return v }
              },
            },
          ]}
        />
      </Modal>
    </div>
  )
}

export default PMSInventoryCalendar

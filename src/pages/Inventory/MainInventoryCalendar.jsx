import { useState, useEffect, useCallback, useContext, useMemo } from 'react'
import { Table, Select, Button, Space, Modal, Form, Input, message, Spin, Timeline, Card, Tag, Alert } from 'antd'
import {
  LeftOutlined, RightOutlined, FilterOutlined, ReloadOutlined,
  CheckCircleOutlined, CloseCircleOutlined, InfoCircleOutlined,
  SafetyCertificateOutlined, DashboardOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { hotelRoomTypeApi, ratePlanApi, hotelPriceApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import { AuthContext } from '../../contexts/AuthContext'

const { Option } = Select
const WEEKDAY_NAMES = ['日', '一', '二', '三', '四', '五', '六']

/**
 * 主要房控日历
 * 支持3种视图：库存、房价、开关房（多选同时展示）
 * 点击"可售"库存单元格：只读展示算力推导明细弹框，不允许编辑
 */
const MainInventoryCalendar = () => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const { user } = useContext(AuthContext)
  const getOp = () => encodeURIComponent(user?.name || user?.username || '系统用户')

  const [selectedMonth, setSelectedMonth] = useState(dayjs().format('YYYY-MM'))
  const [viewTypes, setViewTypes] = useState(['库存'])
  const [filterChannel, setFilterChannel] = useState('CTRIP')
  const [filterRoomType, setFilterRoomType] = useState('全部房型')
  const [filterRatePlan, setFilterRatePlan] = useState('全部价格计划')
  const [roomTypes, setRoomTypes] = useState([])
  const [ratePlans, setRatePlans] = useState([])
  const [channels, setChannels] = useState([])
  const [loading, setLoading] = useState(false)
  const [publishedRoomTypeCodes, setPublishedRoomTypeCodes] = useState([])

  // 数据状态
  const [pmsData, setPmsData] = useState([])
  const [priceData, setPriceData] = useState([])
  const [roomStatusData, setRoomStatusData] = useState([])

  // 编辑弹窗（仅用于房价）
  const [editModalVisible, setEditModalVisible] = useState(false)
  const [editRecord, setEditRecord] = useState(null)
  const [editForm] = Form.useForm()

  // 算力明细弹窗（只读）
  const [calcModalVisible, setCalcModalVisible] = useState(false)
  const [calcDetailData, setCalcDetailData] = useState(null)

  // 加载房型列表
  useEffect(() => {
    if (!hotelCode) return
    hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
      .then(res => setRoomTypes((res?.data || []).filter(r => r.status === 'active')))
      .catch(() => {})
  }, [hotelCode])

  useEffect(() => {
    if (!hotelCode) return
    ratePlanApi.getRatePlansByHotelCode(hotelCode)
      .then(res => setRatePlans(res?.data || []))
      .catch(() => {})
  }, [hotelCode])

  // 加载渠道列表
  useEffect(() => {
    api.get('/channel-codes/third-level')
      .then(res => setChannels(Array.isArray(res) ? res : (res?.data || [])))
      .catch(() => {})
  }, [])

  // 加载已发布至当前渠道的房型
  useEffect(() => {
    if (!hotelCode || !filterChannel) return
    api.get('/channel-publish/records', { params: { hotelCode, channelCode: filterChannel } })
      .then(res => {
        const records = Array.isArray(res) ? res : (res?.data || [])
        const publishedCodes = records
          .filter(r => r.status === 'published' || !r.status)
          .map(r => r.roomTypeCode)
        setPublishedRoomTypeCodes(Array.from(new Set(publishedCodes)))
      })
      .catch(() => setPublishedRoomTypeCodes([]))
  }, [hotelCode, filterChannel])

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
      const needPms = viewTypes.includes('库存') || viewTypes.includes('开关房')
      const needPrice = viewTypes.includes('房价')
      const needStatus = viewTypes.includes('开关房')

      const promises = []

      if (needPms) {
        const rateCodeParam = filterRatePlan && filterRatePlan !== '全部价格计划' ? filterRatePlan : '';
        promises.push(
          api.get('/pms-inventory', { params: { hotelCode, channelCode: filterChannel, rateCode: rateCodeParam, ...dateRange } })
            .then(res => {
              const data = res?.data || []
              console.log('[Major Calendar] PMS 库存加载成功, 条数:', data.length)
              setPmsData(data)
            })
            .catch(err => {
              console.error('[Major Calendar] 加载库存失败:', err)
              setPmsData([])
            })
        )
      } else {
        setPmsData([])
      }

      if (needPrice) {
        promises.push(
          hotelPriceApi.getPriceQueryData(hotelCode, null, dateRange.startDate, dateRange.endDate)
            .then(res => {
              const data = res?.data?.prices || res?.prices || []
              console.log('[Major Calendar] 房价数据加载成功, 条数:', data.length)
              setPriceData(data)
            })
            .catch(err => {
              console.error('[Major Calendar] 加载房价数据失败:', err)
              setPriceData([])
            })
        )
      } else {
        setPriceData([])
      }

      if (needStatus) {
        promises.push(
          Promise.all(
            roomTypes.map(rt =>
              api.get('/room-status', {
                params: { hotelCode, dimensionType: 'room_type', dimensionCode: rt.roomTypeCode, ...dateRange }
              }).then(res => ({ code: rt.roomTypeCode, data: res?.data || [] }))
                .catch(() => ({ code: rt.roomTypeCode, data: [] }))
            )
          ).then(results => {
            console.log('[Major Calendar] 房态数据加载完成')
            setRoomStatusData(results)
          }).catch(err => {
            console.error('[Major Calendar] 房态合并处理失败:', err)
            setRoomStatusData([])
          })
        )
      } else {
        setRoomStatusData([])
      }

      if (promises.length > 0) {
        await Promise.all(promises)
      }
    } catch (err) {
      console.error('[Major Calendar] 数据加载任务组异常:', err)
    } finally {
      setLoading(false)
    }
  }, [hotelCode, roomTypes, viewTypes, filterChannel, filterRatePlan, dateRange])

  useEffect(() => { fetchData() }, [fetchData])

  // 月份切换
  const handlePrevMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').subtract(1, 'month').format('YYYY-MM'))
  const handleNextMonth = () => setSelectedMonth(dayjs(selectedMonth + '-01').add(1, 'month').format('YYYY-MM'))

  // 过滤出已发布至所选渠道的房型
  const publishedRoomTypes = useMemo(() => {
    return roomTypes.filter(rt => publishedRoomTypeCodes.includes(rt.roomTypeCode))
  }, [roomTypes, publishedRoomTypeCodes])

  // 筛选后的房型
  const filteredRoomTypes = useMemo(() => {
    if (filterRoomType === '全部房型') return publishedRoomTypes
    return publishedRoomTypes.filter(rt => rt.roomTypeCode === filterRoomType)
  }, [publishedRoomTypes, filterRoomType])

  // ========== 融合拼接表格数据 ==========
  const tableData = useMemo(() => {
    if (roomTypes.length === 0) return []

    const rows = []
    const showPms = viewTypes.includes('库存') || viewTypes.includes('开关房')
    const showPrice = viewTypes.includes('房价')
    const showStatus = viewTypes.includes('开关房')

    // 构建快捷映射
    const pmsMap = {}
    pmsData.forEach(item => {
      if (!pmsMap[item.roomTypeCode]) pmsMap[item.roomTypeCode] = {}
      pmsMap[item.roomTypeCode][item.inventoryDate] = item
    })

    const priceMap = {}
    priceData.forEach(item => {
      const key = `${item.roomTypeCode}_${item.rateCode}`
      if (!priceMap[key]) priceMap[key] = {}
      const dateStr = typeof item.priceDate === 'string' ? item.priceDate.substring(0, 10) : dayjs(item.priceDate).format('YYYY-MM-DD')
      priceMap[key][dateStr] = item
    })

    const statusMap = {}
    roomStatusData.forEach(({ code, data }) => {
      statusMap[code] = {}
      data.forEach(item => {
        const dateStr = typeof item.statusDate === 'string' ? item.statusDate.substring(0, 10) : dayjs(item.statusDate).format('YYYY-MM-DD')
        statusMap[code][dateStr] = item.isOpen
      })
    })

    const filteredPlans = filterRatePlan === '全部价格计划'
      ? ratePlans
      : ratePlans.filter(rp => rp.rateCode === filterRatePlan)

    filteredRoomTypes.forEach(rt => {
      const rtRows = []
      const rtPms = pmsMap[rt.roomTypeCode] || {}

      if (showPms) {
        const availRow = {
          key: `${rt.roomTypeCode}_available`,
          roomTypeCode: rt.roomTypeCode,
          roomTypeName: rt.roomTypeName,
          type: 'available',
          label: '大盘可售',
          color: '#1890ff'
        }
        const soldRow = {
          key: `${rt.roomTypeCode}_sold`,
          roomTypeCode: rt.roomTypeCode,
          roomTypeName: rt.roomTypeName,
          type: 'sold',
          label: '已售',
          color: '#faad14'
        }

        monthDays.forEach(({ dateStr }) => {
          const d = rtPms[dateStr]
          availRow[dateStr] = d ? d.availableRooms : '-'
          soldRow[dateStr] = d ? d.soldCount : '-'
        })

        rtRows.push(availRow, soldRow)
      }

      if (showPrice) {
        filteredPlans.forEach(rp => {
          // 1. 房价价格行
          const key = `${rt.roomTypeCode}_${rp.rateCode}`
          const rpData = priceMap[key] || {}
          const row = {
            key,
            roomTypeCode: rt.roomTypeCode,
            roomTypeName: rt.roomTypeName,
            rateCode: rp.rateCode,
            rateName: rp.rateName,
            type: 'price',
            label: rp.rateName,
            color: '#722ed1'
          }
          monthDays.forEach(({ dateStr }) => {
            const d = rpData[dateStr]
            row[dateStr] = d ? d.priceWithTax : null
          })
          rtRows.push(row)

          // 2. 专属产品可售库存行（与房价行共生成对出现）
          if (viewTypes.includes('库存')) {
            const availKey = `${rt.roomTypeCode}_${rp.rateCode}_avail`
            const availRow = {
              key: availKey,
              roomTypeCode: rt.roomTypeCode,
              roomTypeName: rt.roomTypeName,
              rateCode: rp.rateCode,
              rateName: rp.rateName,
              type: 'product_available',
              label: `${rp.rateName}（${rp.rateCode}）-可售`,
              color: '#36cfc9' // 使用中青天蓝色差异化
            }
            monthDays.forEach(({ dateStr }) => {
              const d = rtPms[dateStr]
              if (d && d.productAvailability && d.productAvailability[rp.rateCode]) {
                const pAvail = d.productAvailability[rp.rateCode]
                availRow[dateStr] = pAvail.availableRooms
                availRow[`_detail_${dateStr}`] = pAvail.availabilityDetail
              } else {
                availRow[dateStr] = '-'
              }
            })
            rtRows.push(availRow)
          }

          // 3. 专属产品综合房态行（房价-库存-房态共生排版）
          if (viewTypes.includes('开关房')) {
            const statusKey = `${rt.roomTypeCode}_${rp.rateCode}_status`
            const statusRow = {
              key: statusKey,
              roomTypeCode: rt.roomTypeCode,
              roomTypeName: rt.roomTypeName,
              rateCode: rp.rateCode,
              rateName: rp.rateName,
              type: 'product_status',
              label: `${rp.rateName}（${rp.rateCode}）-房态`,
              color: '#52c41a' // 使用中绿色
            }
            monthDays.forEach(({ dateStr }) => {
              const d = rtPms[dateStr]
              let isOpen = true
              let specDetail = null
              if (d && d.productAvailability && d.productAvailability[rp.rateCode]) {
                const pAvail = d.productAvailability[rp.rateCode]
                specDetail = pAvail.availabilityDetail
                if (pAvail.availabilityDetail && pAvail.availabilityDetail.rejectReason) {
                  if (pAvail.availabilityDetail.rejectReason.includes('房态关闭')) {
                    isOpen = false
                  }
                }
              }
              statusRow[dateStr] = isOpen ? '开' : '关'
              statusRow[`_detail_${dateStr}`] = specDetail
            })
            rtRows.push(statusRow)
          }
        })
      }

      if (showStatus) {
        const rtStatus = statusMap[rt.roomTypeCode] || {}
        const statusRow = {
          key: `${rt.roomTypeCode}_status`,
          roomTypeCode: rt.roomTypeCode,
          roomTypeName: rt.roomTypeName,
          type: 'status',
          label: '大盘房态', // 重命名为大盘房态作为大盘参考
          color: '#52c41a'
        }
        monthDays.forEach(({ dateStr }) => {
          const d = rtPms[dateStr]
          let isOpen = true
          // 若后端算力模型中该日期返回的 rejectReason 包含 "房态关闭"，说明被大盘相关维度（酒店/房型/渠道/渠道+房型）熔断关闭
          if (d && d.availabilityDetail && d.availabilityDetail.rejectReason) {
            if (d.availabilityDetail.rejectReason.includes('房态关闭')) {
              isOpen = false
            }
          } else {
            // 兜底：如果无算力数据，以 room_type 维度数据库记录为准
            isOpen = rtStatus[dateStr] !== undefined ? rtStatus[dateStr] : true
          }
          statusRow[dateStr] = isOpen ? '开' : '关'
          statusRow[`_detail_${dateStr}`] = d?.availabilityDetail ?? null
        })
        rtRows.push(statusRow)
      }

      rtRows.forEach((r, idx) => {
        r._groupSize = rtRows.length
        r._isFirstInGroup = idx === 0
      })

      rows.push(...rtRows)
    })

    return rows
  }, [viewTypes, pmsData, priceData, roomStatusData, filteredRoomTypes, ratePlans, filterRatePlan, monthDays])

  // 房态切换
  const handleToggleStatus = async (record, dateStr) => {
    const currentVal = record[dateStr]
    const newIsOpen = currentVal !== '开'
    try {
      await api.post('/room-status', {
        hotelCode,
        dimensionType: 'room_type',
        dimensionCode: record.roomTypeCode,
        statusDate: dateStr,
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

  // 点击库存可售单元格 —— 统一只读展示算力推导明细弹框，不允许编辑
  const handleInventoryClick = (record, dateStr) => {
    const d = pmsData.find(item => 
      item.roomTypeCode === record.roomTypeCode && 
      (item.inventoryDate === dateStr || 
       (typeof item.inventoryDate === 'string' && item.inventoryDate.substring(0, 10) === dateStr))
    )

    // 优先从专属库存行提取该产品在该日期绑定的专属算力明细
    const customDetail = record[`_detail_${dateStr}`]
    const finalDetail = customDetail !== undefined ? customDetail : (d?.availabilityDetail ?? d?.availability_detail ?? null)

    setCalcDetailData({
      roomTypeCode: record.roomTypeCode,
      roomTypeName: record.roomTypeName,
      dateStr,
      channelCode: filterChannel,
      pmsItem: d,
      detail: finalDetail,
      cellValue: record[dateStr],
      isStatusType: record.type === 'status' || record.type === 'product_status',
      rateName: record.rateName,
      rateCode: record.rateCode
    })
    setCalcModalVisible(true)
  }

  // 保存房价编辑
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
      }
      setEditModalVisible(false)
      fetchData()
    } catch (err) {
      if (!err.errorFields) message.error('保存失败')
    }
  }

  // 表格列
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
        width: 170,
        fixed: 'left',
        render: (_, record) => ({
          children: (
            <div style={{ fontSize: 12, display: 'flex', alignItems: 'center' }}>
              <span style={{ marginRight: 4, fontSize: 14 }}>🛏</span>
              <span style={{ fontWeight: 600 }}>{record.roomTypeCode}</span>
              <span style={{ marginLeft: 4 }}>{record.roomTypeName}</span>
            </div>
          ),
          props: { rowSpan: record._isFirstInGroup ? record._groupSize : 0 },
        }),
      },
      {
        title: '类型/房价码',
        key: 'subType',
        width: 100,
        fixed: 'left',
        render: (_, record) => {
          const text = record.type === 'price' ? `${record.rateName}（${record.rateCode}）` : record.label
          const color = record.type === 'price' ? '#722ed1' : record.color || '#52c41a'
          return (
            <div style={{ fontSize: 12, color: color, fontWeight: 500 }}>{text}</div>
          )
        },
      },
    ]

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
        width: 70,
        onHeaderCell: () => ({
          style: { background: isWeekend ? '#fffbf0' : undefined, padding: '4px 2px' },
        }),
        onCell: (record) => {
          const isStatusRow = record.type === 'status' || record.type === 'product_status'
          const isOpen = record[dateStr] === '开'
          const isPriceCell = record.type === 'price'
          const isInventoryCell = record.type === 'available' || record.type === 'product_available'
          const clickable = isStatusRow || isInventoryCell
          return {
            style: {
              background: isStatusRow ? (isOpen ? '#f6ffed' : '#fff1f0') : (isWeekend ? '#fffbf0' : undefined),
              textAlign: 'center',
              padding: '4px 2px',
              cursor: (clickable || isPriceCell) ? 'pointer' : 'default',
            },
            onClick: isPriceCell
              ? () => handlePriceClick(record, dateStr)
              : clickable
                ? () => handleInventoryClick(record, dateStr)
                : undefined,
          }
        },
        render: (value, record) => {
          if (value == null || value === '-') return <span style={{ color: '#ccc' }}>-</span>
          if (record.type === 'price') {
            return <span style={{ fontSize: 11, color: '#722ed1', fontWeight: 500 }}>¥{value}</span>
          }
          if (record.type === 'status' || record.type === 'product_status') {
            return <span style={{ fontSize: 11, color: value === '开' ? '#52c41a' : '#f5222d', fontWeight: 500 }}>{value}</span>
          }
          return <span style={{ fontSize: 11, color: record.color, fontWeight: 500 }}>{value}</span>
        },
      })
    })

    return cols
  }, [selectedMonth, monthDays, viewTypes, ratePlans, filterRatePlan])

  if (!hotelCode) return <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>请先选择酒店</div>

  return (
    <div>
      {/* 工具栏 */}
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 16, gap: 12, flexWrap: 'wrap' }}>
        <Select value={filterChannel} style={{ width: 160 }} onChange={setFilterChannel} showSearch optionFilterProp="children">
          {channels.map(c => (
            <Option key={c.code} value={c.code}>{c.name}</Option>
          ))}
        </Select>
        <Select
          mode="multiple"
          value={viewTypes}
          style={{ minWidth: 200, maxWidth: 300 }}
          onChange={(vals) => setViewTypes(vals.length === 0 ? ['库存'] : vals)}
          placeholder="请选择数据显示类型"
          maxTagCount="responsive"
        >
          <Option value="库存">库存</Option>
          <Option value="房价">房价</Option>
          <Option value="开关房">开关房</Option>
        </Select>
        <Select value={filterRoomType} style={{ width: 200 }} onChange={setFilterRoomType} showSearch optionFilterProp="children">
          <Option value="全部房型">全部房型</Option>
          {publishedRoomTypes.map(rt => (
            <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>{rt.roomTypeName}（{rt.roomTypeCode}）</Option>
          ))}
        </Select>
        {(viewTypes.includes('房价') || viewTypes.includes('库存')) && (
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

      {/* 房价编辑弹窗 */}
      <Modal
        title={`编辑房价 - ${editRecord?.roomTypeName} / ${editRecord?.rateName} / ${editRecord?.dateStr}`}
        open={editModalVisible}
        onOk={handleEditSave}
        onCancel={() => setEditModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={400}
      >
        <Form form={editForm} layout="vertical">
          <Form.Item name="price" label="房价（含税）" rules={[{ required: true, message: '请输入房价' }]}>
            <Input type="number" min={0} placeholder="请输入房价" prefix="¥" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 剩余库存实时算力推导明细弹窗（只读） */}
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 16 }}>
            <span>📅</span>
            <span style={{ fontWeight: 600 }}>
              {calcDetailData?.isStatusType ? '综合房态多维度交集决策明细' : '剩余库存实时算力推导明细'}
            </span>
          </div>
        }
        open={calcModalVisible}
        onCancel={() => setCalcModalVisible(false)}
        footer={[
          <Button key="close" type="primary" onClick={() => setCalcModalVisible(false)}>
            我知道了
          </Button>
        ]}
        width={580}
        destroyOnClose
      >
        {calcDetailData && (() => {
          const { roomTypeCode, roomTypeName, dateStr, channelCode, pmsItem, detail, cellValue, isStatusType, rateCode, rateName } = calcDetailData
          const finalAvail = pmsItem?.availableRooms ?? (typeof cellValue === 'number' ? cellValue : (parseInt(cellValue) || 0))
          const channelName = channels.find(c => c.code === channelCode)?.name || channelCode

          // 新增：房态阻断专属明细展示，彻底剥离任何“库存/配额”等无关字样，专注于开关房时间线
          if (isStatusType) {
            const isClosed = cellValue === '关' || detail?.rejectReason?.includes('房态关闭')
            const rejectStr = detail?.rejectReason || ''

            // 4 层大盘维度
            const dims = [
              { key: 'hotel', label: '第一层级：酒店整体房态 (Hotel)', type: 'hotel' },
              { key: 'room_type', label: '第二层级：房型级房态 (RoomType)', type: 'room_type' },
              { key: 'channel', label: '第三层级：渠道级房态 (Channel)', type: 'channel' },
              { key: 'channel_room_type', label: '第四层级：渠道+房型房态 (Channel:RoomType)', type: 'channel_room_type' }
            ]

            // 若有特定产品参数，追加 3 层产品级精细维度（一共 7 层）
            if (rateCode) {
              dims.push(
                { key: 'rate', label: '第五层级：价格计划房态 (Rate)', type: 'rate' },
                { key: 'rate_category', label: '第六层级：房价大类房态 (RateCategory)', type: 'rate_category' },
                { key: 'market', label: '第七层级：市场码房态 (Market)', type: 'market' }
              )
            }

            return (
              <div style={{ marginTop: 12 }}>
                {/* 房态头部状态汇总 */}
                <Card
                  size="small"
                  style={{
                    background: isClosed ? 'linear-gradient(135deg, #fff1f0 0%, #fffbfa 100%)' : 'linear-gradient(135deg, #f6ffed 0%, #fafff0 100%)',
                    border: isClosed ? '1px solid #ffa39e' : '1px solid #b7eb8f',
                    borderRadius: 8,
                    marginBottom: 20
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 12px' }}>
                    <div>
                      <div style={{ fontSize: 13, color: '#666' }}>
                        房型：<strong style={{ color: '#111' }}>{roomTypeName} ({roomTypeCode})</strong>
                      </div>
                      {rateCode && (
                        <div style={{ fontSize: 13, color: '#666', marginTop: 4 }}>
                          产品：<strong style={{ color: '#722ed1' }}>{rateName} ({rateCode})</strong>
                        </div>
                      )}
                      <div style={{ fontSize: 13, color: '#666', marginTop: 4 }}>
                        日期：<strong style={{ color: '#111' }}>{dateStr}</strong> | 渠道：<strong style={{ color: '#111' }}>{channelName}</strong>
                      </div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontSize: 12, color: '#888' }}>综合交集决策房态</div>
                      <div style={{ marginTop: 4 }}>
                        {isClosed ? (
                          <Tag color="error" style={{ margin: 0, fontSize: 14, padding: '4px 12px', fontWeight: 600 }}>已关闭 (关)</Tag>
                        ) : (
                          <Tag color="success" style={{ margin: 0, fontSize: 14, padding: '4px 12px', fontWeight: 600 }}>开放售卖 (开)</Tag>
                        )}
                      </div>
                    </div>
                  </div>
                  {isClosed && rejectStr && (
                    <Alert
                      message={
                        <div style={{ fontSize: 12 }}>
                          <strong>房态阻断源：</strong>{rejectStr}
                        </div>
                      }
                      type="error"
                      showIcon
                      style={{ marginTop: 10, padding: '4px 12px' }}
                    />
                  )}
                </Card>

                {/* 开关房 7 大层级决策时间线 */}
                <Timeline mode="left" style={{ paddingLeft: 12 }}>
                  {dims.map(dim => {
                    const isDimBlocked = isClosed && rejectStr.includes(dim.type)

                    let statusLabel = '默认开启'
                    let statusColor = 'gray'
                    let dotIcon = <CheckCircleOutlined style={{ fontSize: 15, color: '#bfbfbf' }} />

                    if (isClosed) {
                      if (isDimBlocked) {
                        statusLabel = '已关闭'
                        statusColor = 'red'
                        dotIcon = <CloseCircleOutlined style={{ fontSize: 15, color: '#f5222d' }} />
                      } else {
                        statusLabel = '正常开启'
                        statusColor = 'green'
                        dotIcon = <CheckCircleOutlined style={{ fontSize: 15, color: '#52c41a' }} />
                      }
                    } else {
                      statusLabel = '正常开启'
                      statusColor = 'green'
                      dotIcon = <CheckCircleOutlined style={{ fontSize: 15, color: '#52c41a' }} />
                    }

                    return (
                      <Timeline.Item key={dim.key} dot={dotIcon} color={statusColor === 'red' ? 'red' : 'green'}>
                        <div style={{ fontWeight: 600, fontSize: 13, color: isDimBlocked ? '#f5222d' : '#111' }}>
                          {dim.label}
                        </div>
                        <div style={{ fontSize: 12, color: isDimBlocked ? '#ff4d4f' : '#888', marginTop: 4 }}>
                          层级状态：<span style={{ fontWeight: 600, color: isDimBlocked ? '#f5222d' : '#52c41a' }}>{statusLabel}</span>
                          {isDimBlocked && ' ⚠️ 本层级开关房配置为“关”，强行熔断最终综合房态。'}
                        </div>
                      </Timeline.Item>
                    )
                  })}
                </Timeline>
              </div>
            )
          }

          // 无算力明细时，展示基础 PMS 库存信息（只读兜底）
          if (!detail) {
            const displayPhysical = pmsItem?.physicalRooms !== null && pmsItem?.physicalRooms !== undefined ? pmsItem.physicalRooms : finalAvail
            const displayAvail = pmsItem?.availableRooms !== null && pmsItem?.availableRooms !== undefined ? pmsItem.availableRooms : finalAvail
            const displaySold = pmsItem?.soldCount !== null && pmsItem?.soldCount !== undefined ? pmsItem.soldCount : 0
            const displayMaint = pmsItem?.maintenanceRooms !== null && pmsItem?.maintenanceRooms !== undefined ? pmsItem.maintenanceRooms : 0

            return (
              <div style={{ marginTop: 12 }}>
                <Card
                  size="small"
                  style={{
                    background: 'linear-gradient(135deg, #e6f7ff 0%, #f0f5ff 100%)',
                    border: '1px solid #adc6ff',
                    borderRadius: 8,
                    marginBottom: 20
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 12px' }}>
                    <div>
                      <div style={{ fontSize: 13, color: '#666' }}>
                        房型：<strong style={{ color: '#111' }}>{roomTypeName} ({roomTypeCode})</strong>
                      </div>
                      <div style={{ fontSize: 13, color: '#666', marginTop: 4 }}>
                        日期：<strong style={{ color: '#111' }}>{dateStr}</strong> | 渠道：<strong style={{ color: '#111' }}>{channelName}</strong>
                      </div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontSize: 12, color: '#888' }}>当日可售库存</div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 6, justifyContent: 'flex-end', marginTop: 2 }}>
                        <span style={{ fontSize: 32, fontWeight: 700, color: finalAvail > 0 ? '#1890ff' : '#f5222d', lineHeight: 1 }}>{finalAvail}</span>
                        <span style={{ fontSize: 13, color: finalAvail > 0 ? '#1890ff' : '#f5222d', fontWeight: 500 }}>间</span>
                      </div>
                    </div>
                  </div>
                </Card>
                <Timeline mode="left" style={{ paddingLeft: 12 }}>
                  <Timeline.Item dot={<InfoCircleOutlined style={{ fontSize: 16, color: '#1890ff' }} />} color="blue">
                    <div style={{ fontWeight: 600, fontSize: 14, color: '#111' }}>PMS 物理库存基础数据</div>
                    <div style={{
                      background: '#f5f5f5',
                      padding: '8px 12px',
                      borderRadius: 6,
                      marginTop: 6,
                      fontSize: 12,
                      display: 'grid',
                      gridTemplateColumns: '1fr 1fr',
                      gap: 8
                    }}>
                      <div>🏢 物理总间数：<strong>{displayPhysical}</strong> 间</div>
                      <div>🔧 维修房数：<strong>{displayMaint}</strong> 间</div>
                      <div>✅ 可售库存：<strong style={{ color: '#1890ff' }}>{displayAvail}</strong> 间</div>
                      <div>🛒 已售间数：<strong style={{ color: '#faad14' }}>{displaySold}</strong> 间</div>
                    </div>
                    {(!pmsItem || pmsItem.physicalRooms == null) && (
                      <div style={{ fontSize: 12, color: '#faad14', marginTop: 6 }}>
                        ⚠️ 该日期在 PMS 系统中暂无物理库存记录，可售数量来自渠道配额算力计算结果。
                      </div>
                    )}
                  </Timeline.Item>
                  <Timeline.Item dot={<CheckCircleOutlined style={{ fontSize: 16, color: '#52c41a' }} />} color="green">
                    <div style={{ fontWeight: 600, fontSize: 14, color: '#111' }}>最终可售库存</div>
                    <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>
                      PMS 物理库存数据展示（只读），渠道实时算力明细数据暂未返回。
                    </div>
                    <div style={{ fontSize: 13, color: '#111', marginTop: 6, fontWeight: 500 }}>
                      最终结果：<strong style={{ color: finalAvail > 0 ? '#52c41a' : '#f5222d', fontSize: 15 }}>{finalAvail}</strong> 间（只读，不可编辑）
                    </div>
                  </Timeline.Item>
                </Timeline>
              </div>
            )
          }

          // 有算力明细时，展示完整三步推导路径
          const pmsAvail = detail.pmsAvailable ?? 0
          const hotelAvail = detail.hotelAvailable ?? 0
          const physicalBase = Math.min(pmsAvail, hotelAvail)

          // 引入多命名（驼峰、全小写、蛇形）安全网提取方案，确保百分百捕获配额数值，解决渠道加房型等配额规则未成功引入的问题
          const channelQuota = detail.channelQuotaRemaining ?? detail.channel_quota_remaining ?? detail.channelQuota ?? detail.channelquota;
          const channelRoomTypeQuota = detail.channelRoomTypeQuotaRemaining ?? detail.channelRoomtypeQuotaRemaining ?? detail.channel_room_type_quota_remaining ?? detail.channelRoomtype ?? detail.channelroomtype;
          const rateQuota = detail.rateQuotaRemaining ?? detail.rate_quota_remaining ?? detail.rateQuota ?? detail.ratequota;
          const marketQuota = detail.marketQuotaRemaining ?? detail.market_quota_remaining ?? detail.marketQuota ?? detail.marketquota;
          const rateCategoryQuota = detail.rateCategoryQuotaRemaining ?? detail.rate_category_quota_remaining ?? detail.rateCategoryQuota ?? detail.ratecategory;

          const quotas = [
            { label: '渠道配额 (Channel)', val: channelQuota },
            { label: '渠道房型配额 (Channel RoomType)', val: channelRoomTypeQuota },
            { label: '价格计划配额 (Rate)', val: rateQuota },
            { label: '销售市场配额 (Market)', val: marketQuota },
            { label: '价格类别配额 (Rate Category)', val: rateCategoryQuota }
          ].filter(q => q.val !== null && q.val !== undefined)

          const isBlocked = detail.rejectReason || finalAvail === 0

          return (
            <div style={{ marginTop: 12 }}>
              {/* 头部汇总卡片 */}
              <Card
                size="small"
                style={{
                  background: isBlocked ? 'linear-gradient(135deg, #fff1f0 0%, #fffbfa 100%)' : 'linear-gradient(135deg, #e6f7ff 0%, #f0f5ff 100%)',
                  border: isBlocked ? '1px solid #ffa39e' : '1px solid #adc6ff',
                  borderRadius: 8,
                  marginBottom: 20
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 12px' }}>
                  <div>
                    <div style={{ fontSize: 13, color: '#666' }}>
                      房型：<strong style={{ color: '#111' }}>{roomTypeName} ({roomTypeCode})</strong>
                    </div>
                    <div style={{ fontSize: 13, color: '#666', marginTop: 4 }}>
                      日期：<strong style={{ color: '#111' }}>{dateStr}</strong> | 渠道：<strong style={{ color: '#111' }}>{channelName}</strong>
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: 12, color: '#888' }}>实时计算剩余可售</div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 6, justifyContent: 'flex-end', marginTop: 2 }}>
                      <span style={{ fontSize: 32, fontWeight: 700, color: isBlocked ? '#f5222d' : '#1890ff', lineHeight: 1 }}>
                        {finalAvail}
                      </span>
                      <span style={{ fontSize: 13, color: isBlocked ? '#f5222d' : '#1890ff', fontWeight: 500 }}>间</span>
                    </div>
                    <div style={{ marginTop: 4 }}>
                      {isBlocked ? (
                        <Tag color="error" style={{ margin: 0 }}>已拦截/不可售</Tag>
                      ) : (
                        <Tag color="success" style={{ margin: 0 }}>正常销售中</Tag>
                      )}
                    </div>
                  </div>
                </div>
                {detail.rejectReason && (
                  <Alert
                    message={
                      <div style={{ fontSize: 12 }}>
                        <strong>阻断原因：</strong>{detail.rejectReason}
                      </div>
                    }
                    type="error"
                    showIcon
                    style={{ marginTop: 10, padding: '4px 12px' }}
                  />
                )}
              </Card>

              {/* 三步算力推导时间线 */}
              <Timeline mode="left" style={{ paddingLeft: 12 }}>

                {/* 步骤一：房型物理可用库存 */}
                <Timeline.Item
                  dot={<InfoCircleOutlined style={{ fontSize: 16, color: '#1890ff' }} />}
                  color="blue"
                >
                  <div style={{ fontWeight: 600, fontSize: 14, color: '#111' }}>步骤 1：房型物理可用库存</div>
                  <div style={{
                    background: '#f5f5f5',
                    padding: '8px 12px',
                    borderRadius: 6,
                    marginTop: 6,
                    fontSize: 12,
                    display: 'flex',
                    flexDirection: 'column',
                    gap: 4
                  }}>
                    <div>🏢 房型物理剩余可用数：<strong style={{ color: '#1890ff', fontSize: 13 }}>{detail.pmsAvailable ?? 0}</strong> 间
                      {(detail.roomTypeOverbookCount ?? 0) > 0 && <span style={{ color: '#52c41a', marginLeft: 6 }}>(含房型超卖 +{detail.roomTypeOverbookCount} 间)</span>}
                    </div>
                  </div>
                </Timeline.Item>

                {/* 步骤二：销售配额限制过滤 */}
                <Timeline.Item
                  dot={<SafetyCertificateOutlined style={{ fontSize: 16, color: quotas.length > 0 ? '#faad14' : '#8c8c8c' }} />}
                  color={quotas.length > 0 ? 'orange' : 'gray'}
                >
                  <div style={{ fontWeight: 600, fontSize: 14, color: '#111' }}>步骤 2：销售配额限制过滤 (Quota Filters)</div>
                  {quotas.length === 0 ? (
                    <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>
                      📝 当前渠道大盘级在该房型、价格计划上未设置任何硬性配额限制，无缝透传房型物理库存。
                    </div>
                  ) : (
                    <>
                      <div style={{ fontSize: 12, color: '#666', marginTop: 4 }}>
                        检测到设置了以下销售配额（剩余可订数）：
                      </div>
                      <div style={{
                        background: '#fffbe6',
                        border: '1px solid #ffe58f',
                        padding: '8px 12px',
                        borderRadius: 6,
                        marginTop: 6,
                        fontSize: 12,
                        display: 'flex',
                        flexDirection: 'column',
                        gap: 4
                      }}>
                        {quotas.map((q, idx) => (
                          <div key={idx} style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <span>🏷️ {q.label}：</span>
                            <strong>{q.val} 间</strong>
                          </div>
                        ))}
                      </div>
                      <div style={{ fontSize: 12, color: '#333', marginTop: 6 }}>
                        ➔ 融合成交配额限制最小值：<strong style={{ color: '#faad14' }}>{Math.min(...quotas.map(q => q.val ?? 0))}</strong> 间
                      </div>
                    </>
                  )}
                </Timeline.Item>

                {/* 步骤三：最终决策 */}
                <Timeline.Item
                  dot={<DashboardOutlined style={{ fontSize: 18, color: '#52c41a' }} />}
                  color="green"
                >
                  <div style={{ fontWeight: 600, fontSize: 14, color: '#111' }}>步骤 3：最终剩余可订库存决策</div>
                  <div style={{ fontSize: 12, color: '#555', marginTop: 4 }}>
                    合并算力公式：<code>最终可售 = Min(房型物理可用数, 各销售配额剩余)</code>
                  </div>
                  <div style={{ fontSize: 13, color: '#111', marginTop: 6, fontWeight: 500 }}>
                    最终结果：<code>Min({detail.pmsAvailable ?? 0}{quotas.length > 0 ? `, ${Math.min(...quotas.map(q => q.val ?? 0))}` : ''})</code> = <strong style={{ color: '#52c41a', fontSize: 15 }}>{finalAvail}</strong> 间。
                  </div>
                </Timeline.Item>
              </Timeline>
            </div>
          )
        })()}
      </Modal>
    </div>
  )
}

export default MainInventoryCalendar

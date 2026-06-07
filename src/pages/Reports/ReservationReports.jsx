import { useState, useEffect } from 'react'
import { Card, Form, Select, DatePicker, Button, Table, Row, Col, message, Checkbox, Radio } from 'antd'
import { SearchOutlined, ExportOutlined, InfoCircleOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api, { reportApi, hotelApi, marketCodeApi, rateTypeApi } from '../../utils/api'

const { Option } = Select
const { RangePicker } = DatePicker

// ==========================================
// 模拟报表演示数据 - 丰富页面首次展现与预览内容
// ==========================================
// 已移除模拟数据常量


/**
 * @module Reports
 * @function renderTrendBadge
 * @description 渲染同环比增长率的微标组件，直观地以颜色和箭头的形式展示增长（绿）与下跌（红）
 * @param {string} changeStr - 同环比变化率字符串，例如 "↑20%", "↓5%"
 * @returns {React.ReactNode} 格式化后的趋势胶囊微标
 */
const renderTrendBadge = (changeStr) => {
  if (!changeStr) return null;
  const isUp = changeStr.includes('↑') || parseFloat(changeStr) > 0;
  const isDown = changeStr.includes('↓') || parseFloat(changeStr) < 0;
  
  const style = {
    display: 'inline-flex',
    alignItems: 'center',
    padding: '2px 8px',
    borderRadius: '4px',
    fontSize: '11px',
    fontWeight: '600',
    lineHeight: '1.2',
    marginLeft: '6px',
    verticalAlign: 'middle',
    fontFamily: 'SFProText-Semibold, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto'
  };

  if (isUp) {
    return (
      <span style={{ ...style, backgroundColor: '#e6f4ea', color: '#137333' }}>
        {changeStr}
      </span>
    );
  } else if (isDown) {
    return (
      <span style={{ ...style, backgroundColor: '#fce8e6', color: '#c5221f' }}>
        {changeStr}
      </span>
    );
  }
  return (
    <span style={{ ...style, backgroundColor: '#f1f3f4', color: '#5f6368' }}>
      {changeStr}
    </span>
  );
};

/**
 * @module Reports
 * @function renderTrendValue
 * @description 渲染表格单元格内的数据趋势组件，上面是本期值（加粗、大字体），下面是对比趋势微标（右对齐）
 * @param {string|number} val - 本期数值
 * @param {string} changeStr - 同环比变化率字符串
 * @returns {React.ReactNode} 精确排版的数据趋势单元格内容
 */
const renderTrendValue = (val, changeStr) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', justifyContent: 'center' }}>
      <span style={{ 
        fontFamily: 'SFProText-Medium, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto', 
        color: '#1e293b', 
        fontWeight: '500',
        fontSize: '13px'
      }}>
        {val}
      </span>
      {changeStr && (
        <span style={{ marginTop: '2px' }}>
          {renderTrendBadge(changeStr)}
        </span>
      )}
    </div>
  );
};

const ReservationReports = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [initializing, setInitializing] = useState(false)
  const [showInitHint, setShowInitHint] = useState(false) // 对比期数据全空时提示初始化
  
  // 默认初始状态为空，直接查询真实数据
  const [reportData, setReportData] = useState([])
  const [totalData, setTotalData] = useState(null)

  const [hotels, setHotels] = useState([])
  const [channelCodes, setChannelCodes] = useState([])
  const [marketCodes, setMarketCodes] = useState([])
  const [rateCategories, setRateCategories] = useState([])

  // 用于在筛选改变时强制 Table 表头日期范围同步更新的局部表单状态缓存
  const [formValues, setFormValues] = useState({
    bookingDate: [dayjs().startOf('month'), dayjs().endOf('month')],
    orderDate: [dayjs().subtract(1, 'year').startOf('month'), dayjs().subtract(1, 'year').endOf('month')],
    dataComparison: false,
    groupBy1: 'channel',
    groupBy2: 'hotel'
  })

  // 当前在界面 Table 渲染生效的分组依据状态，在接口响应成功后才同步，防止实时修改输入框造成表格列割裂
  const [activeGroupBys, setActiveGroupBys] = useState({
    groupBy1: 'channel',
    groupBy2: 'hotel'
  })

  useEffect(() => {
    hotelApi.getAllHotels().then(res => {
      setHotels(res?.data || res || [])
    }).catch(err => console.error(err))

    api.get('/channel-codes/third-level').then(res => {
      setChannelCodes(res?.data || res || [])
    }).catch(err => console.error(err))

    marketCodeApi.getThirdLevelMarketCodes().then(res => {
      setMarketCodes(res?.data || res || [])
    }).catch(err => console.error(err))

    rateTypeApi.getActiveRateTypes().then(res => {
      setRateCategories(res?.data || res || [])
    }).catch(err => console.error(err))
  }, [])

  // 获取日期展示文字
  const getPeriodLabel = (dates) => {
    if (dates && dates.length === 2 && dates[0] && dates[1]) {
      return `${dates[0].format('YYYY-MM-DD')} ~ ${dates[1].format('YYYY-MM-DD')}`
    }
    return '-'
  }

  // 处理搜索
  const handleSearch = () => {
    setLoading(true)
    const values = form.getFieldsValue()

    // 格式化日期参数
    let startDate = null
    let endDate = null
    if (values.bookingDate && values.bookingDate.length === 2 && values.bookingDate[0] && values.bookingDate[1]) {
      startDate = values.bookingDate[0].format('YYYY-MM-DD')
      endDate = values.bookingDate[1].format('YYYY-MM-DD')
    } else {
      startDate = dayjs().subtract(1, 'month').format('YYYY-MM-DD')
      endDate = dayjs().format('YYYY-MM-DD')
    }

    let compareStartDate = null
    let compareEndDate = null
    if (values.dataComparison && values.orderDate && values.orderDate.length === 2 && values.orderDate[0] && values.orderDate[1]) {
      compareStartDate = values.orderDate[0].format('YYYY-MM-DD')
      compareEndDate = values.orderDate[1].format('YYYY-MM-DD')
    }

    const params = {
      startDate,
      endDate,
      hotelCode: values.hotel || undefined,
      channelCode: values.channelCode || undefined,
      marketCode: values.marketCode || undefined,
      rateCategoryCode: values.rateCategory || undefined,
      orderStatus: values.orderStatus || undefined,
      groupBy1: values.groupBy1 || '',
      groupBy2: values.groupBy2 || '',
      paymentMethod: values.paymentMethod || undefined,
      memberBooking: values.memberBooking !== undefined ? values.memberBooking : undefined,
      canEarnPoints: values.canEarnPoints !== undefined ? values.canEarnPoints : undefined,
      onlineBooking: values.onlineBooking !== undefined ? values.onlineBooking : undefined,
      enableCompare: values.dataComparison || false,
      compareStartDate,
      compareEndDate
    }

    reportApi.getReservationReport(params)
      .then(res => {
        // 如果后端有返回有效数据且不为空
        if (res && res.reportData && res.reportData.length > 0) {
          setReportData(res.reportData)
          setTotalData(res.totalData || null)
          setActiveGroupBys({
            groupBy1: params.groupBy1,
            groupBy2: params.groupBy2
          })
          message.success('查询成功')

          // 检测对比期是否全为0（汇总表缺历史数据场景）
          if (params.enableCompare && compareStartDate && compareEndDate) {
            const prev = res.totalData?.previousPeriod
            const allZero = prev &&
              prev.orderCount === 0 &&
              prev.orderAmount === 0 &&
              prev.roomNights === 0
            setShowInitHint(!!allZero)
          } else {
            setShowInitHint(false)
          }
        } else {
          setReportData([])
          setTotalData(null)
          setActiveGroupBys({
            groupBy1: params.groupBy1,
            groupBy2: params.groupBy2
          })
          setShowInitHint(false)
          message.info('未查询到相关订单统计记录')
        }
      })
      .catch(err => {
        setReportData([])
        setTotalData(null)
        setShowInitHint(false)
        message.warning(`获取真实数据失败（${err?.message || '网络连接失败'}）`)
      })
      .finally(() => {
        setLoading(false)
      })
  }

  // 处理导出
  const handleExport = () => {
    message.info('导出报表功能开发中')
  }

  /**
   * 初始化汇总表数据：同时覆盖本期和对比期的历史预聚合数据
   * 解决 report_daily_reservation_summary 表缺历史数据导致对比列全空的问题
   */
  const handleInitialize = async () => {
    const values = form.getFieldsValue()
    const tasks = []

    // 本期初始化
    if (values.bookingDate && values.bookingDate[0] && values.bookingDate[1]) {
      tasks.push({
        label: '本期',
        startDate: values.bookingDate[0].format('YYYY-MM-DD'),
        endDate: values.bookingDate[1].format('YYYY-MM-DD')
      })
    }
    // 对比期初始化
    if (values.dataComparison && values.orderDate && values.orderDate[0] && values.orderDate[1]) {
      tasks.push({
        label: '对比期',
        startDate: values.orderDate[0].format('YYYY-MM-DD'),
        endDate: values.orderDate[1].format('YYYY-MM-DD')
      })
    }

    if (tasks.length === 0) {
      message.warning('请先选择查询日期范围')
      return
    }

    setInitializing(true)
    try {
      for (const task of tasks) {
        await reportApi.initialize({ startDate: task.startDate, endDate: task.endDate })
      }
      message.success(`已成功初始化 ${tasks.map(t => t.label).join(' & ')} 的汇总数据，正在重新查询...`)
      setShowInitHint(false)
      // 初始化完成后自动重新查询
      setTimeout(() => handleSearch(), 300)
    } catch (err) {
      message.error(`初始化失败：${err?.message || '请检查后端服务是否已启动'}`)
    } finally {
      setInitializing(false)
    }
  }

  // 处理查看订单明细
  const handleViewOrderDetails = (record) => {
    const values = form.getFieldsValue()
    let startDate = null
    let endDate = null
    if (values.bookingDate && values.bookingDate.length === 2 && values.bookingDate[0] && values.bookingDate[1]) {
      startDate = values.bookingDate[0].format('YYYY-MM-DD')
      endDate = values.bookingDate[1].format('YYYY-MM-DD')
    }
    
    const query = new URLSearchParams()
    if (values.hotel) query.append('hotelCode', values.hotel)
    
    // 解析层级合并行的过滤参数
    if (record.groupCode) {
      const g1 = activeGroupBys.groupBy1
      if (g1 === 'channel') query.append('channelCode', record.groupCode)
      if (g1 === 'market') query.append('marketCode', record.groupCode)
    }
    if (record.subGroupCode) {
      const g2 = activeGroupBys.groupBy2
      if (g2 === 'roomType') query.append('roomTypeCode', record.subGroupCode)
      if (g2 === 'ratePlan') query.append('ratePlanCode', record.subGroupCode)
    }
    if (startDate && endDate) {
      query.append('startDate', startDate)
      query.append('endDate', endDate)
    }
    if (values.orderStatus) {
      query.append('reservationStatus', values.orderStatus)
    }
    
    window.open(`/reservation?${query.toString()}`, '_blank')
  }

  // 快速选择与对比日期计算联动
  const handleValuesChange = (changedValues, allValues) => {
    // 1. 如果修改了预订日期快速选择 (quickSearch)
    if ('quickSearch' in changedValues) {
      const val = changedValues.quickSearch
      let range = []
      const now = dayjs()
      switch (val) {
        case '今天':
          range = [now.startOf('day'), now.endOf('day')]
          break;
        case '本周':
          range = [now.startOf('week'), now.endOf('week')]
          break;
        case '上周':
          range = [now.subtract(1, 'week').startOf('week'), now.subtract(1, 'week').endOf('week')]
          break;
        case '本月':
          range = [now.startOf('month'), now.endOf('month')]
          break;
        case '上月':
          range = [now.subtract(1, 'month').startOf('month'), now.subtract(1, 'month').endOf('month')]
          break;
        case '今年':
          range = [now.startOf('year'), now.endOf('year')]
          break;
        default:
          break;
      }
      if (range.length === 2) {
        form.setFieldsValue({ bookingDate: range })
        allValues.bookingDate = range // 同步到 allValues 供对比区间计算
      }
    }

    // 2. 如果手动修改了预订日期 (bookingDate)
    if ('bookingDate' in changedValues) {
      form.setFieldsValue({ quickSearch: undefined })
      allValues.quickSearch = undefined
    }

    // 3. 计算数据对比日期联动
    const shouldRecalculateComparison = 
      ('dataComparison' in changedValues && allValues.dataComparison) || 
      ('quickSearchComparison' in changedValues && allValues.dataComparison) || 
      ('bookingDate' in changedValues && allValues.dataComparison) ||
      ('quickSearch' in changedValues && allValues.dataComparison);

    if (shouldRecalculateComparison) {
      const bookingDate = allValues.bookingDate
      const compMode = allValues.quickSearchComparison
      if (bookingDate && bookingDate.length === 2 && bookingDate[0] && bookingDate[1]) {
        const start = bookingDate[0]
        const end = bookingDate[1]
        let compareRange = []
        if (compMode === '同比') {
          compareRange = [start.subtract(1, 'year'), end.subtract(1, 'year')]
        } else if (compMode === '环比') {
          const diffDays = end.diff(start, 'day') + 1
          compareRange = [start.subtract(diffDays, 'day'), start.subtract(1, 'day')]
        }
        if (compareRange.length === 2) {
          form.setFieldsValue({ orderDate: compareRange })
        }
      }
    }

    // 4. 如果手动修改了对比日期 (orderDate)
    if ('orderDate' in changedValues) {
      form.setFieldsValue({ quickSearchComparison: undefined })
    }

    // 同步到表单局部状态缓存，表头日期段即可实时对应渲染
    setFormValues({ ...allValues })
  }

  // 初始化加载数据
  useEffect(() => {
    const timer = setTimeout(() => {
      handleSearch()
    }, 500)
    return () => clearTimeout(timer)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // 渲染 KPI 数据卡片区
  const renderKpiCards = () => {
    const data = totalData?.currentPeriod;
    const hasCompare = formValues.dataComparison;

    const items = [
      {
        title: '总订单数',
        value: data?.orderCount !== undefined ? data.orderCount.toLocaleString() : '—',
        // 若没有开启对比，隐藏百分比胶囊微标
        change: hasCompare ? data?.orderCountChange : null,
        tip: '统计区间内生成并确认为有效状态的订单总数'
      },
      {
        title: '总订单金额',
        value: data?.orderAmount !== undefined ? `¥${data.orderAmount.toLocaleString()}` : '—',
        change: hasCompare ? data?.orderAmountChange : null,
        tip: '统计区间内订单的总金额汇总（本币）'
      },
      {
        title: '总间夜数',
        value: data?.roomNights !== undefined ? data.roomNights.toLocaleString() : '—',
        change: hasCompare ? data?.roomNightsChange : null,
        tip: '所售客房的物理入住间夜总数'
      },
      {
        title: '间夜平均价 (ADR)',
        value: data?.avgRate !== undefined ? `¥${data.avgRate.toLocaleString()}` : '—',
        change: hasCompare ? data?.avgRateChange : null,
        tip: '客房平均每日房价（总房租金额/总间夜数）'
      }
    ];

    return (
      <Row gutter={[16, 16]} style={{ marginBottom: 20 }}>
        {items.map((item, index) => (
          <Col xs={24} sm={12} md={6} key={index}>
            <div 
              className="kpi-card-hover"
              style={{
                background: '#ffffff',
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '16px 20px',
                boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.02)',
                position: 'relative',
                overflow: 'hidden'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ color: '#64748b', fontSize: '13px', fontWeight: '500' }}>
                  {item.title}
                </span>
                <span style={{ color: '#94a3b8', cursor: 'help', fontSize: '13px' }} title={item.tip}>
                  <InfoCircleOutlined />
                </span>
              </div>
              <div style={{ display: 'flex', alignItems: 'baseline', flexWrap: 'wrap', gap: '8px' }}>
                <span style={{ 
                  fontSize: '24px', 
                  fontWeight: '600', 
                  color: '#0f172a',
                  fontFamily: 'SFProDisplay-Bold, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto'
                }}>
                  {item.value}
                </span>
                {item.change && renderTrendBadge(item.change)}
              </div>
            </div>
          </Col>
        ))}
      </Row>
    );
  };

  // 构造表格列配置（Grouping Columns 多级表头方案 - 始终展示对比列）
  const getColumns = () => {
    const getDimensionLabel = (val, defaultLabel) => {
      if (!val) return defaultLabel;
      switch (val) {
        case 'channel': return '渠道';
        case 'hotel': return '酒店';
        case 'market': return '市场';
        case 'roomType': return '房型';
        case 'ratePlan': return '价格计划';
        case 'rateCategory': return '价格大类';
        default: return val;
      }
    };
    const hasCompare = formValues.dataComparison;

    const dimensionColumns = [];

    if (activeGroupBys.groupBy1) {
      dimensionColumns.push({
        title: getDimensionLabel(activeGroupBys.groupBy1, '维度一'),
        dataIndex: 'channel',
        key: 'channel',
        width: 120,
        align: 'center',
        render: (text, record) => {
          if (activeGroupBys.groupBy2) {
            if (record.isFirst) {
              const channelData = reportData.find(c => c.key === record.channelKey);
              return {
                children: text,
                props: {
                  rowSpan: channelData?.hotels?.length || 1
                }
              };
            }
            return {
              children: '',
              props: {
                rowSpan: 0
              }
            };
          }
          return text;
        }
      });
    }

    if (activeGroupBys.groupBy2) {
      dimensionColumns.push({
        title: getDimensionLabel(activeGroupBys.groupBy2, '维度二'),
        dataIndex: 'hotel',
        key: 'hotel',
        width: 140,
        align: 'center'
      });
    }

    const cols = [];

    if (dimensionColumns.length > 0) {
      cols.push({
        title: '统计维度',
        children: dimensionColumns
      });
    }

    cols.push(
      {
        title: `本期数据 (${getPeriodLabel(formValues.bookingDate)})`,
        children: [
          {
            title: '订单数',
            key: 'orderCount',
            width: 100,
            align: 'right',
            render: (text, record) => renderTrendValue(
              record.currentPeriod.orderCount?.toLocaleString() || 0,
              hasCompare ? record.currentPeriod.orderCountChange : null
            )
          },
          {
            title: '总金额',
            key: 'orderAmount',
            width: 130,
            align: 'right',
            render: (text, record) => renderTrendValue(
              record.currentPeriod.orderAmount > 0 ? `¥${record.currentPeriod.orderAmount.toLocaleString()}` : '—',
              hasCompare ? record.currentPeriod.orderAmountChange : null
            )
          },
          {
            title: '支付积分',
            key: 'orderPoints',
            width: 120,
            align: 'right',
            render: (text, record) => renderTrendValue(
              record.currentPeriod.orderPoints > 0 ? record.currentPeriod.orderPoints.toLocaleString() : '—',
              hasCompare ? record.currentPeriod.orderPointsChange : null
            )
          },
          {
            title: '间夜数',
            key: 'roomNights',
            width: 100,
            align: 'right',
            render: (text, record) => renderTrendValue(
              record.currentPeriod.roomNights > 0 ? record.currentPeriod.roomNights : '—',
              hasCompare ? record.currentPeriod.roomNightsChange : null
            )
          },
          {
            title: '平均房价',
            key: 'avgRate',
            width: 120,
            align: 'right',
            render: (text, record) => renderTrendValue(
              record.currentPeriod.avgRate > 0 ? `¥${record.currentPeriod.avgRate.toLocaleString()}` : '—',
              hasCompare ? record.currentPeriod.avgRateChange : null
            )
          }
        ]
      },
      {
        title: hasCompare 
          ? `对比期数据 (${getPeriodLabel(formValues.orderDate)})` 
          : '对比期数据 (未开启对比)',
        children: [
          {
            title: '订单数',
            key: 'prevOrderCount',
            width: 100,
            align: 'right',
            render: (text, record) => {
              if (!hasCompare) return <span style={{ color: '#94a3b8' }}>—</span>;
              return (
                <span style={{ color: '#475569', fontSize: '13px' }}>
                  {record.previousPeriod.orderCount?.toLocaleString() || 0}
                </span>
              );
            }
          },
          {
            title: '总金额',
            key: 'prevOrderAmount',
            width: 130,
            align: 'right',
            render: (text, record) => {
              if (!hasCompare) return <span style={{ color: '#94a3b8' }}>—</span>;
              return (
                <span style={{ color: '#475569', fontSize: '13px' }}>
                  {record.previousPeriod.orderAmount > 0 ? `¥${record.previousPeriod.orderAmount.toLocaleString()}` : '—'}
                </span>
              );
            }
          },
          {
            title: '支付积分',
            key: 'prevOrderPoints',
            width: 120,
            align: 'right',
            render: (text, record) => {
              if (!hasCompare) return <span style={{ color: '#94a3b8' }}>—</span>;
              return (
                <span style={{ color: '#475569', fontSize: '13px' }}>
                  {record.previousPeriod.orderPoints > 0 ? record.previousPeriod.orderPoints.toLocaleString() : '—'}
                </span>
              );
            }
          },
          {
            title: '间夜数',
            key: 'prevRoomNights',
            width: 100,
            align: 'right',
            render: (text, record) => {
              if (!hasCompare) return <span style={{ color: '#94a3b8' }}>—</span>;
              return (
                <span style={{ color: '#475569', fontSize: '13px' }}>
                  {record.previousPeriod.roomNights || '—'}
                </span>
              );
            }
          },
          {
            title: '平均房价',
            key: 'prevAvgRate',
            width: 120,
            align: 'right',
            render: (text, record) => {
              if (!hasCompare) return <span style={{ color: '#94a3b8' }}>—</span>;
              return (
                <span style={{ color: '#475569', fontSize: '13px' }}>
                  {record.previousPeriod.avgRate > 0 ? `¥${record.previousPeriod.avgRate.toLocaleString()}` : '—'}
                </span>
              );
            }
          }
        ]
      },
      {
        title: '操作',
        key: 'action',
        width: 90,
        align: 'center',
        fixed: 'right',
        render: (text, record) => (
          <Button type="link" size="small" onClick={() => handleViewOrderDetails(record)} style={{ padding: 0 }}>
            明细
          </Button>
        )
      }
    );

    return cols;
  };

  return (
    <div className="fade-in" style={{ padding: '0px 0px 24px 0px' }}>
      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(-4px); }
          to { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in {
          animation: fadeIn 0.2s ease-out forwards;
        }
        .kpi-card-hover {
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }
        .kpi-card-hover:hover {
          transform: translateY(-2px);
          box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.02);
          border-color: #cbd5e1 !important;
        }
        .business-table .ant-table-thead > tr > th {
          background-color: #f8fafc !important;
          color: #334155 !important;
          font-weight: 600 !important;
          border-bottom: 2px solid #e2e8f0 !important;
          font-size: 13px !important;
        }
        .business-table .ant-table-tbody > tr > td {
          border-bottom: 1px solid #f1f5f9 !important;
          font-size: 13px !important;
        }
        .business-table .ant-table-summary {
          background-color: #f8fafc !important;
          border-top: 2px solid #e2e8f0 !important;
        }
      `}</style>

      <h1 className="page-title" style={{ fontSize: '20px', fontWeight: '600', color: '#0f172a', marginBottom: '20px' }}>
        订单分析报表
      </h1>

      {/* 对比期数据缺失提示 */}
      {showInitHint && (
        <div style={{
          backgroundColor: '#fffbeb',
          border: '1px solid #fcd34d',
          borderRadius: '6px',
          padding: '10px 16px',
          marginBottom: '16px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          color: '#92400e',
          fontSize: '13px'
        }}>
          <span>
            <InfoCircleOutlined style={{ marginRight: 8, color: '#d97706' }} />
            检测到<b>对比期汇总数据为空</b>（汇总表缺少历史记录）。请点击「初始化」将原始订单数据聚合写入汇总表。
          </span>
          <Button 
            size="small" 
            type="primary"
            danger
            onClick={handleInitialize} 
            loading={initializing}
            style={{ fontSize: '12px', height: '24px', whiteSpace: 'nowrap' }}
          >
            一键初始化汇总数据
          </Button>
        </div>
      )}
      
      <Card 
        bordered={false} 
        style={{ 
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.05)', 
          borderRadius: '8px', 
          border: '1px solid #e2e8f0',
          marginBottom: '20px'
        }}
        bodyStyle={{ padding: '20px' }}
      >
        {/* 筛选区域 - 取消折叠，全局精美平铺 */}
        <div style={{ marginBottom: 0 }}>
          <Form
            form={form}
            layout="vertical"
            onValuesChange={handleValuesChange}
            initialValues={{
              hotel: '',
              bookingDate: [dayjs().startOf('month'), dayjs().endOf('month')],
              quickSearch: '本月',
              dataComparison: false,
              orderDate: [dayjs().subtract(1, 'year').startOf('month'), dayjs().subtract(1, 'year').endOf('month')],
              quickSearchComparison: '同比',
              orderStatus: '',
              marketCode: '',
              channelCode: '',
              rateCategory: '',
              groupBy1: 'channel',
              groupBy2: 'hotel',
              paymentMethod: '',
              memberBooking: false,
              canEarnPoints: false,
              onlineBooking: false
            }}
          >
            <Row gutter={[16, 12]}>
              {/* 第一行 */}
              <Col xs={24} sm={12} md={4}>
                <Form.Item label="酒店" name="hotel" style={{ marginBottom: 0 }}>
                  <Select placeholder="请选择酒店" style={{ width: '100%' }}>
                    <Option value="">全部酒店</Option>
                    {hotels.map(h => (
                      <Option key={h.hotelCode} value={h.hotelCode}>{h.chineseName}</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item label="预订日期段" name="bookingDate" style={{ marginBottom: 0 }}>
                  <RangePicker style={{ width: '100%' }} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={3}>
                <Form.Item label="快速日期" name="quickSearch" style={{ marginBottom: 0 }}>
                  <Select placeholder="快速选择" style={{ width: '100%' }} allowClear>
                    <Option value="今天">今天</Option>
                    <Option value="本周">本周</Option>
                    <Option value="上周">上周</Option>
                    <Option value="本月">本月</Option>
                    <Option value="上月">上月</Option>
                    <Option value="今年">今年</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={3} style={{ display: 'flex', alignItems: 'center', height: '56px', paddingTop: '10px' }}>
                <Form.Item name="dataComparison" valuePropName="checked" style={{ marginBottom: 0 }}>
                  <Checkbox>对比数据</Checkbox>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item 
                  label="对比期日期段" 
                  name="orderDate" 
                  style={{ marginBottom: 0, opacity: formValues.dataComparison ? 1 : 0.4, transition: 'opacity 0.2s' }}
                >
                  <RangePicker style={{ width: '100%' }} disabled={!formValues.dataComparison} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={2}>
                <Form.Item 
                  label="对比类型" 
                  name="quickSearchComparison" 
                  style={{ marginBottom: 0, opacity: formValues.dataComparison ? 1 : 0.4, transition: 'opacity 0.2s' }}
                >
                  <Select placeholder="对比" style={{ width: '100%' }} disabled={!formValues.dataComparison}>
                    <Option value="同比">同比</Option>
                    <Option value="环比">环比</Option>
                  </Select>
                </Form.Item>
              </Col>

              {/* 第二行 */}
              <Col xs={24} sm={12} md={4}>
                <Form.Item label="订单状态" name="orderStatus" style={{ marginBottom: 0 }}>
                  <Select placeholder="请选择状态" style={{ width: '100%' }} allowClear>
                    <Option value="">全部订单状态</Option>
                    <Option value="confirmed">已确认</Option>
                    <Option value="canceled">已取消</Option>
                    <Option value="checkIn">已入住</Option>
                    <Option value="checkOut">已离店</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={4}>
                <Form.Item label="市场代码" name="marketCode" style={{ marginBottom: 0 }}>
                  <Select placeholder="全部市场" style={{ width: '100%' }} allowClear showSearch optionFilterProp="children">
                    <Option value="">全部市场</Option>
                    {marketCodes.map(c => (
                      <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={4}>
                <Form.Item label="渠道代码" name="channelCode" style={{ marginBottom: 0 }}>
                  <Select placeholder="全部渠道" style={{ width: '100%' }} allowClear showSearch optionFilterProp="children">
                    <Option value="">全部渠道</Option>
                    {channelCodes.map(c => (
                      <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={4}>
                <Form.Item label="房价大类" name="rateCategory" style={{ marginBottom: 0 }}>
                  <Select placeholder="全部大类" style={{ width: '100%' }} allowClear showSearch optionFilterProp="children">
                    <Option value="">全部大类</Option>
                    {rateCategories.map(c => (
                      <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={4}>
                <Form.Item label="分组依据一" name="groupBy1" style={{ marginBottom: 0 }}>
                  <Select placeholder="不分组 (可选)" style={{ width: '100%' }} allowClear>
                    <Option value="channel" disabled={formValues.groupBy2 === 'channel'}>渠道</Option>
                    <Option value="hotel" disabled={formValues.groupBy2 === 'hotel'}>酒店</Option>
                    <Option value="market" disabled={formValues.groupBy2 === 'market'}>市场</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={4}>
                <Form.Item label="分组依据二" name="groupBy2" style={{ marginBottom: 0 }}>
                  <Select placeholder="不分组 (可选)" style={{ width: '100%' }} allowClear>
                    <Option value="hotel" disabled={formValues.groupBy1 === 'hotel'}>酒店</Option>
                    <Option value="roomType" disabled={formValues.groupBy1 === 'roomType'}>房型</Option>
                    <Option value="ratePlan" disabled={formValues.groupBy1 === 'ratePlan'}>价格计划</Option>
                  </Select>
                </Form.Item>
              </Col>

              {/* 第三行 */}
              <Col xs={24} sm={12} md={6}>
                <Form.Item label="订单支付方式" name="paymentMethod" style={{ marginBottom: 0 }}>
                  <Radio.Group style={{ width: '100%' }}>
                    <Radio value="">全部支付</Radio>
                    <Radio value="points">纯积分</Radio>
                    <Radio value="nonPoints">非积分</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={12} style={{ display: 'flex', alignItems: 'center', height: '56px', paddingTop: '20px', gap: '16px' }}>
                <Form.Item name="memberBooking" valuePropName="checked" style={{ marginBottom: 0 }}>
                  <Checkbox>会员预订</Checkbox>
                </Form.Item>
                <Form.Item name="canEarnPoints" valuePropName="checked" style={{ marginBottom: 0 }}>
                  <Checkbox>可累积积分</Checkbox>
                </Form.Item>
                <Form.Item name="onlineBooking" valuePropName="checked" style={{ marginBottom: 0 }}>
                  <Checkbox>线上渠道</Checkbox>
                </Form.Item>
              </Col>
              <Col xs={24} md={6} style={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', height: '56px', paddingTop: '20px', gap: '8px' }}>
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} loading={loading}>
                  执行查询
                </Button>
                <Button 
                  onClick={handleInitialize} 
                  loading={initializing}
                  title="将原始订单数据聚合写入汇总表，解决对比数据为空的问题"
                >
                  初始化数据
                </Button>
                <Button icon={<ExportOutlined />} onClick={handleExport}>
                  导出报表
                </Button>
              </Col>
            </Row>
          </Form>
        </div>
      </Card>
      
      {/* KPI 数据概览卡片区 */}
      {renderKpiCards()}
      
      <Card 
        bordered={false} 
        style={{ 
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.05)', 
          borderRadius: '8px',
          border: '1px solid #e2e8f0'
        }}
        bodyStyle={{ padding: '0px' }}
      >
        {/* 报表 Table */}
        <Table
          className="business-table"
          loading={loading}
          pagination={false}
          scroll={{ x: 1200 }}
          dataSource={reportData.flatMap(channel => 
            channel.hotels.map((hotel, index) => ({
              ...hotel,
              channel: channel.channel,
              isFirst: index === 0,
              channelKey: channel.key,
              hotelCount: channel.hotels.length
            }))
          )}
          rowKey="key"
          columns={getColumns()}
          summary={() => {
            if (!totalData || reportData.length === 0) return null;
            
            const current = totalData.currentPeriod;
            const prev = totalData.previousPeriod;
            const hasCompare = formValues.dataComparison;

            const dimCount = (activeGroupBys.groupBy1 ? 1 : 0) + (activeGroupBys.groupBy2 ? 1 : 0);
            if (dimCount === 0) return null;

            return (
              <Table.Summary fixed="bottom">
                <Table.Summary.Row style={{ backgroundColor: '#f8fafc', fontWeight: '600' }}>
                  <Table.Summary.Cell index={0} colSpan={dimCount} align="center">
                    <span style={{ color: '#1e293b', fontWeight: '600' }}>总计</span>
                  </Table.Summary.Cell>
                  
                  {/* 本期数据总计 */}
                  <Table.Summary.Cell index={2} align="right">
                    {renderTrendValue(
                      current.orderCount?.toLocaleString() || 0,
                      hasCompare ? current.orderCountChange : null
                    )}
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={3} align="right">
                    {renderTrendValue(
                      current.orderAmount > 0 ? `¥${current.orderAmount.toLocaleString()}` : '—',
                      hasCompare ? current.orderAmountChange : null
                    )}
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={4} align="right">
                    {renderTrendValue(
                      current.orderPoints > 0 ? current.orderPoints.toLocaleString() : '—',
                      hasCompare ? current.orderPointsChange : null
                    )}
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={5} align="right">
                    {renderTrendValue(
                      current.roomNights > 0 ? current.roomNights : '—',
                      hasCompare ? current.roomNightsChange : null
                    )}
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={6} align="right">
                    {renderTrendValue(
                      current.avgRate > 0 ? `¥${current.avgRate.toLocaleString()}` : '—',
                      hasCompare ? current.avgRateChange : null
                    )}
                  </Table.Summary.Cell>

                  {/* 对比期数据总计 */}
                  <Table.Summary.Cell index={7} align="right">
                    <span style={{ color: hasCompare ? '#475569' : '#94a3b8', fontSize: '13px', fontWeight: '500' }}>
                      {hasCompare ? (prev.orderCount?.toLocaleString() || 0) : '—'}
                    </span>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={8} align="right">
                    <span style={{ color: hasCompare ? '#475569' : '#94a3b8', fontSize: '13px', fontWeight: '500' }}>
                      {hasCompare ? (prev.orderAmount > 0 ? `¥${prev.orderAmount.toLocaleString()}` : '—') : '—'}
                    </span>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={9} align="right">
                    <span style={{ color: hasCompare ? '#475569' : '#94a3b8', fontSize: '13px', fontWeight: '500' }}>
                      {hasCompare ? (prev.orderPoints > 0 ? prev.orderPoints.toLocaleString() : '—') : '—'}
                    </span>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={10} align="right">
                    <span style={{ color: hasCompare ? '#475569' : '#94a3b8', fontSize: '13px', fontWeight: '500' }}>
                      {hasCompare ? (prev.roomNights || '—') : '—'}
                    </span>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={11} align="right">
                    <span style={{ color: hasCompare ? '#475569' : '#94a3b8', fontSize: '13px', fontWeight: '500' }}>
                      {hasCompare ? (prev.avgRate > 0 ? `¥${prev.avgRate.toLocaleString()}` : '—') : '—'}
                    </span>
                  </Table.Summary.Cell>
                  
                  {/* 操作列明细查看 */}
                  <Table.Summary.Cell index={12} align="center">
                    <Button type="link" size="small" onClick={() => handleViewOrderDetails(totalData)} style={{ padding: 0 }}>
                      明细
                    </Button>
                  </Table.Summary.Cell>
                </Table.Summary.Row>
              </Table.Summary>
            );
          }}
        />
      </Card>
    </div>
  )
}

export default ReservationReports
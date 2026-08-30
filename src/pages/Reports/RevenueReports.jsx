import { useState, useEffect } from 'react'
import { App, Select, Button, DatePicker, Table, Card, Radio, Row, Col } from 'antd'
import { SearchOutlined, ExportOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { Line } from '@ant-design/plots'
import { hotelApi, reportApi } from '../../utils/api'

const { Option } = Select

const RevenueReports = () => {
  const { message } = App.useApp()
  const [selectedMonth, setSelectedMonth] = useState(dayjs())
  const [selectedHotel, setSelectedHotel] = useState('全集团')
  const [selectedStatisticMethod, setSelectedStatisticMethod] = useState('按酒店纬度')
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(false)
  const [reportData, setReportData] = useState([])
  const [viewMode, setViewMode] = useState('table') // 'table' | 'chart'

  // 1. 初始化动态加载酒店列表
  useEffect(() => {
    hotelApi.getAllHotels()
      .then(res => {
        const hotelList = res?.data || res || []
        setHotels([{ hotelCode: '全集团', chineseName: '全集团' }, ...hotelList])
      })
      .catch(() => {
        message.error('加载酒店列表失败')
      })
  }, [])

  // 2. 发起真实 API 营收查询
  const handleSearch = () => {
    setLoading(true)
    const params = {
      hotelCode: selectedHotel === '全集团' ? undefined : selectedHotel,
      month: selectedMonth.format('YYYY-MM-DD'),
      statisticMethod: selectedStatisticMethod
    }

    reportApi.getRevenueReport(params)
      .then(res => {
        if (res && res.length > 0) {
          setReportData(res)
        } else {
          setReportData([])
          message.info('未查询到当前查询月份的营收数据')
        }
      })
      .catch(err => {
        setReportData([])
        message.error(`加载营收数据失败：${err?.message || '网络连接失败'}`)
      })
      .finally(() => {
        setLoading(false)
      })
  }

  // 参数改变时联动自动刷新
  useEffect(() => {
    const timer = setTimeout(() => {
      handleSearch()
    }, 300)
    return () => clearTimeout(timer)
  }, [selectedHotel, selectedStatisticMethod, selectedMonth])

  const handleExport = () => {
    if (!reportData || reportData.length === 0) {
      message.warning('当前暂无数据可供导出，请先执行查询')
      return
    }

    const daysInMonth = selectedMonth.daysInMonth()
    const headers = ['酒店']
    if (selectedStatisticMethod === '按房型纬度') {
      headers.push('房型')
    }
    headers.push('指标类型')

    for (let i = 1; i <= daysInMonth; i++) {
      const date = selectedMonth.date(i)
      const weekDay = ['日', '一', '二', '三', '四', '五', '六'][date.day()]
      headers.push(`${date.format('MM-DD')}(${weekDay})`)
    }

    const csvRows = []
    
    // 转义单元格内容
    const escapeCsvCell = (val) => {
      if (val === undefined || val === null) return ''
      let str = String(val)
      if (str.includes(',') || str.includes('\n') || str.includes('"')) {
        str = '"' + str.replace(/"/g, '""') + '"'
      }
      return str
    }

    // 表头行
    csvRows.push(headers.map(escapeCsvCell).join(','))

    // 数据行
    reportData.forEach(row => {
      const csvRow = [row.hotel]
      if (selectedStatisticMethod === '按房型纬度') {
        csvRow.push(row.roomType || '-')
      }
      csvRow.push(row.inventoryType)

      for (let i = 1; i <= daysInMonth; i++) {
        const val = row[`day${i}`]
        if (val !== undefined && val !== null) {
          if (row.inventoryType === '平均房价') {
            csvRow.push(`¥${val}`)
          } else {
            csvRow.push(val)
          }
        } else {
          csvRow.push('-')
        }
      }
      csvRows.push(csvRow.map(escapeCsvCell).join(','))
    })

    const csvContent = '\uFEFF' + csvRows.join('\n')
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.setAttribute('href', url)
    
    const fileName = `营收分析报表_${selectedHotel}_${selectedStatisticMethod}_${selectedMonth.format('YYYYMM')}.csv`
    link.setAttribute('download', fileName)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    message.success('营收报表导出成功！')
  }

  const generateDateTitle = (day) => {
    const date = selectedMonth.date(day)
    const weekDay = ['日', '一', '二', '三', '四', '五', '六'][date.day()]
    return (
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontSize: 11 }}>{date.format('MM-DD')}</div>
        <div style={{ fontSize: 11, color: '#999' }}>{weekDay}</div>
      </div>
    )
  }

  // 动态计算合并行 rowSpan 属性
  const calculateRowSpan = (field, record, index) => {
    if (index === 0 || reportData[index][field] !== reportData[index - 1][field]) {
      let span = 1
      for (let i = index + 1; i < reportData.length; i++) {
        if (reportData[i][field] === record[field]) {
          span++
        } else {
          break
        }
      }
      return span
    }
    return 0
  }

  // 3. 桥接图表天级数据至折线趋势图数据结构
  const prepareChartData = () => {
    const orderData = []
    const adrData = []
    const daysInMonth = selectedMonth.daysInMonth()

    reportData.forEach(row => {
      let seriesName = row.hotel
      if (selectedStatisticMethod === '按房型纬度') {
        seriesName = `${row.hotel} - ${row.roomType || '全房型'}`
      }

      for (let d = 1; d <= daysInMonth; d++) {
        const dayVal = row[`day${d}`]
        const val = dayVal !== undefined && dayVal !== null ? Number(dayVal) : 0
        const dateStr = selectedMonth.date(d).format('MM-DD')

        const dataPoint = {
          date: dateStr,
          value: val,
          series: seriesName
        }

        if (row.inventoryType === '总订单数') {
          orderData.push(dataPoint)
        } else if (row.inventoryType === '平均房价') {
          adrData.push(dataPoint)
        }
      }
    })

    return { orderData, adrData }
  }

  const getChartConfig = (data, title, yUnit) => ({
    data,
    xField: 'date',
    yField: 'value',
    colorField: 'series',
    interaction: {
      tooltip: {
        shared: true,
        showMarkers: true,
      },
    },
    style: {
      lineWidth: 2,
    },
    axis: {
      y: {
        title: `${title} (${yUnit})`,
      }
    }
  })

  const getColumns = () => {
    const columns = []
    const daysInMonth = selectedMonth.daysInMonth()

    columns.push({
      title: '酒店',
      dataIndex: 'hotel',
      key: 'hotel',
      width: 140,
      align: 'center',
      onCell: (record, index) => ({
        rowSpan: calculateRowSpan('hotel', record, index)
      })
    })

    if (selectedStatisticMethod === '按房型纬度') {
      columns.push({
        title: '房型',
        dataIndex: 'roomType',
        key: 'roomType',
        width: 140,
        align: 'center',
        onCell: (record, index) => {
          if (index === 0 || record.roomType !== reportData[index - 1].roomType || record.hotel !== reportData[index - 1].hotel) {
            let span = 1
            for (let i = index + 1; i < reportData.length; i++) {
              if (reportData[i].roomType === record.roomType && reportData[i].hotel === record.hotel) {
                span++
              } else {
                break
              }
            }
            return { rowSpan: span }
          }
          return { rowSpan: 0 }
        }
      })
    }

    columns.push({
      title: '指标类型',
      dataIndex: 'inventoryType',
      key: 'inventoryType',
      width: 120,
      align: 'center',
      render: (text) => {
        if (text === '平均房价') return <strong style={{ color: '#1890ff' }}>{text}</strong>
        return text
      }
    })

    for (let i = 1; i <= 31; i++) {
      columns.push({
        title: generateDateTitle(i),
        dataIndex: `day${i}`,
        key: `day${i}`,
        width: 80,
        align: 'center',
        render: (text, record) => {
          if (i > daysInMonth) return <span style={{ color: '#d9d9d9' }}>—</span>
          if (text !== undefined && text !== null) {
            if (record.inventoryType === '平均房价') {
              return <span style={{ fontFamily: 'Consolas, monospace' }}>¥{text}</span>
            }
            return <span>{text}</span>
          }
          return '-'
        }
      })
    }

    return columns
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <span>营收分析报表</span>
      </h1>

      <div style={{ marginBottom: 16, padding: 16, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16, flexWrap: 'wrap' }}>
          <div>
            <label style={{ marginRight: 8, fontWeight: '500' }}>选择酒店:</label>
            <Select value={selectedHotel} style={{ width: 180 }} onChange={setSelectedHotel}>
              {hotels.map(h => (
                <Option key={h.hotelCode} value={h.hotelCode}>{h.chineseName}</Option>
              ))}
            </Select>
          </div>
          <div>
            <label style={{ marginRight: 8, fontWeight: '500' }}>统计维度:</label>
            <Select value={selectedStatisticMethod} style={{ width: 140 }} onChange={setSelectedStatisticMethod}>
              <Option value="按酒店纬度">按酒店维度</Option>
              <Option value="按房型纬度">按房型维度</Option>
            </Select>
          </div>
          <div>
            <label style={{ marginRight: 8, fontWeight: '500' }}>统计月份:</label>
            <DatePicker 
              picker="month"
              value={selectedMonth} 
              onChange={setSelectedMonth}
              style={{ width: 150 }}
              allowClear={false}
            />
          </div>
          <div style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center' }}>
            <Radio.Group value={viewMode} onChange={e => setViewMode(e.target.value)} buttonStyle="solid" style={{ marginRight: 12 }}>
              <Radio.Button value="table">表格模式</Radio.Button>
              <Radio.Button value="chart">图形模式</Radio.Button>
            </Radio.Group>
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} loading={loading}>
              执行查询
            </Button>
            <Button icon={<ExportOutlined />} onClick={handleExport} style={{ marginLeft: 8 }}>
              导出报表
            </Button>
          </div>
        </div>
      </div>

      {viewMode === 'table' ? (
        <Card variant="borderless" styles={{ body: { padding: 0 } }} style={{ boxShadow: '0 4px 12px rgba(0,0,0,0.02)', borderRadius: 8, overflow: 'hidden' }}>
          <Table
            columns={getColumns()}
            dataSource={reportData}
            pagination={false}
            loading={loading}
            scroll={{ x: 3200, y: 600 }}
            bordered
            size="small"
            className="business-table"
            rowKey="key"
          />
        </Card>
      ) : (
        <Row gutter={[16, 16]}>
          <Col span={24}>
            <Card 
              title={<span style={{ fontWeight: 600 }}>每日订单量趋势</span>} 
              variant="borderless"
              style={{ boxShadow: '0 4px 12px rgba(0,0,0,0.02)', borderRadius: 8 }}
            >
              <div style={{ height: 350 }}>
                {prepareChartData().orderData.length > 0 ? (
                  <Line {...getChartConfig(prepareChartData().orderData, '订单量', '单')} />
                ) : (
                  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: '#999' }}>暂无订单趋势数据</div>
                )}
              </div>
            </Card>
          </Col>
          <Col span={24}>
            <Card 
              title={<span style={{ fontWeight: 600 }}>客房平均房价 (ADR) 走势</span>} 
              variant="borderless"
              style={{ boxShadow: '0 4px 12px rgba(0,0,0,0.02)', borderRadius: 8 }}
            >
              <div style={{ height: 350 }}>
                {prepareChartData().adrData.length > 0 ? (
                  <Line {...getChartConfig(prepareChartData().adrData, '平均房价', '元')} />
                ) : (
                  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: '#999' }}>暂无房价走势数据</div>
                )}
              </div>
            </Card>
          </Col>
        </Row>
      )}
    </div>
  )
}

export default RevenueReports

import React, { useState, useEffect } from 'react'
import { Select, Button, DatePicker, Table, Card, message, Alert } from 'antd'
import { SearchOutlined, ExportOutlined, InfoCircleOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { hotelApi, reportApi } from '../../utils/api'

const { Option } = Select

const OccupancyReports = () => {
  const [selectedMonth, setSelectedMonth] = useState(dayjs())
  const [selectedHotel, setSelectedHotel] = useState('全集团')
  const [selectedStatisticMethod, setSelectedStatisticMethod] = useState('按酒店纬度')
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(false)
  const [reportData, setReportData] = useState([])

  // 1. 初始化动态加载酒店数据源
  useEffect(() => {
    hotelApi.getAllHotels()
      .then(res => {
        const hotelList = res?.data || res || []
        setHotels([{ hotelCode: '全集团', chineseName: '全集团' }, ...hotelList])
      })
      .catch(err => {
        console.error('加载酒店列表失败:', err)
        message.error('加载酒店列表失败')
      })
  }, [])

  // 2. 发起真实 API 数据查询
  const handleSearch = () => {
    setLoading(true)
    const params = {
      hotelCode: selectedHotel === '全集团' ? undefined : selectedHotel,
      month: selectedMonth.format('YYYY-MM-DD'),
      statisticMethod: selectedStatisticMethod
    }

    reportApi.getOccupancyReport(params)
      .then(res => {
        if (res && res.length > 0) {
          setReportData(res)
        } else {
          setReportData([])
          message.info('未查询到当前查询月份的出租率数据')
        }
      })
      .catch(err => {
        setReportData([])
        message.error(`加载出租率数据失败：${err?.message || '网络连接失败'}`)
      })
      .finally(() => {
        setLoading(false)
      })
  }

  // 参数改变时联动重载
  useEffect(() => {
    const timer = setTimeout(() => {
      handleSearch()
    }, 300)
    return () => clearTimeout(timer)
  }, [selectedHotel, selectedStatisticMethod, selectedMonth])

  const handleExport = () => {
    message.success('已触发导出出租率报表，数据处理中...')
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

  // 动态合并单元格计算
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
          // 仅当酒店和房型均一致时合并
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
      title: '库存类型',
      dataIndex: 'inventoryType',
      key: 'inventoryType',
      width: 120,
      align: 'center',
      render: (text) => {
        if (text === '出租率') return <strong style={{ color: '#13c2c2' }}>{text}</strong>
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
            if (record.inventoryType === '出租率') {
              return <span style={{ fontWeight: '600', color: parseFloat(text) > 85 ? '#389e0d' : '#262626' }}>{text}%</span>
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
        <span>出租率分析报表</span>
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
              <Option value="按酒店纬度">按酒店纬度</Option>
              <Option value="按房型纬度">按房型纬度</Option>
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
          <div style={{ marginLeft: 'auto' }}>
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} loading={loading}>
              执行查询
            </Button>
            <Button icon={<ExportOutlined />} onClick={handleExport} style={{ marginLeft: 8 }}>
              导出报表
            </Button>
          </div>
        </div>
      </div>

      <Card bordered={false} bodyStyle={{ padding: 0 }} style={{ boxShadow: '0 4px 12px rgba(0,0,0,0.02)', borderRadius: 8, overflow: 'hidden' }}>
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
    </div>
  )
}

export default OccupancyReports

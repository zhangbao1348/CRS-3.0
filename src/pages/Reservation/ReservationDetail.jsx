import { useState, useEffect } from 'react'
import { Card, Typography, Descriptions, Table, Tag, Button, Space, Modal, Tabs, message } from 'antd'
import { CloseCircleOutlined, HistoryOutlined } from '@ant-design/icons'
import { useSearchParams } from 'react-router-dom'
import { reservationApi } from '../../utils/api'

const { Text } = Typography

const ReservationDetail = () => {
  const [searchParams] = useSearchParams()
  const reservationId = searchParams.get('id')

  const [orderDetail, setOrderDetail] = useState(null)
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [selectedDateDetail, setSelectedDateDetail] = useState(null)
  const [historyModalVisible, setHistoryModalVisible] = useState(false)
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [selectedApiLog, setSelectedApiLog] = useState(null)

  const fetchDetail = async () => {
    if (!reservationId) {
      message.error('缺少订单ID')
      return
    }
    setLoading(true)
    try {
      const data = await reservationApi.getDetail(reservationId)
      setOrderDetail(data)
    } catch (error) {
      console.error('获取订单详情失败:', error)
      message.error('获取订单详情失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchDetail()
  }, [reservationId])

  const handleDateClick = (item) => {
    setSelectedDateDetail(item)
    setModalVisible(true)
  }

  const handleModalClose = () => {
    setSelectedDateDetail(null)
    setModalVisible(false)
  }

  const handleHistoryClick = () => {
    setHistoryModalVisible(true)
  }

  const handleHistoryModalClose = () => {
    setHistoryModalVisible(false)
  }

  const handleLogClick = (apiLog) => {
    setSelectedApiLog(apiLog)
    setLogModalVisible(true)
  }

  const handleLogModalClose = () => {
    setSelectedApiLog(null)
    setLogModalVisible(false)
  }

  const formatLogContent = (content) => {
    if (!content) {
      return '暂无日志数据'
    }
    if (typeof content !== 'string') {
      try {
        return JSON.stringify(content, null, 2)
      } catch (error) {
        return String(content)
      }
    }
    try {
      return JSON.stringify(JSON.parse(content), null, 2)
    } catch (error) {
      return content
    }
  }

  const handleCancelOrder = async () => {
    if (!reservationId) return
    try {
      await reservationApi.cancel(reservationId, { cancelReason: 'CRS手动取消' })
      message.success('订单取消成功')
      fetchDetail()
    } catch (error) {
      console.error('取消订单失败:', error)
      message.error('取消订单失败')
    }
  }

  if (loading || !orderDetail) {
    return <Card loading={true} />
  }

  const orderInfo = orderDetail.orderInfo || {}
  const hotelInfo = orderDetail.hotelInfo || {}
  const bookingInfo = orderDetail.bookingInfo || {}
  const guestInfo = orderDetail.guestInfo || []
  const priceInfo = orderDetail.priceInfo || {}
  const promotionInfo = orderDetail.promotionInfo || []
  const paymentInfo = orderDetail.paymentInfo || {}
  const policyInfo = orderDetail.policyInfo || {}
  const remarkInfo = orderDetail.remarkInfo || {}
  const operationHistory = orderDetail.operationHistory || []

  // 组装当前选中日期的各项 Tab 的数据源
  const getFeeDataSource = () => {
    if (!selectedDateDetail) return []
    return [
      { key: '1', name: '折扣前房费', value: selectedDateDetail.originalPrice !== null ? `¥${Number(selectedDateDetail.originalPrice).toFixed(2)}` : '-' },
      { key: '2', name: '折扣后房费', value: selectedDateDetail.actualPrice !== null ? `¥${Number(selectedDateDetail.actualPrice || selectedDateDetail.price || 0).toFixed(2)}` : '-' }
    ]
  }

  const getPackageDataSource = () => {
    if (!selectedDateDetail) return []
    let list = []
    if (selectedDateDetail.packagesJson) {
      try {
        const pkgs = JSON.parse(selectedDateDetail.packagesJson)
        if (Array.isArray(pkgs)) {
          list = pkgs.map((pkg, idx) => {
            let typeDesc = pkg.type || '其他'
            if (typeDesc === 'breakfast') typeDesc = '早餐'
            else if (typeDesc === 'transfer') typeDesc = '接送服务'
            else if (typeDesc === 'scenic') typeDesc = '景区门票'
            else if (typeDesc === 'dinner') typeDesc = '正餐'
            
            return {
              key: String(idx + 1),
              typeDesc: typeDesc,
              name: pkg.name || pkg.code,
              quantityDesc: `${pkg.quantity || 1} 份`,
              priceDesc: pkg.price !== undefined && pkg.price !== null ? `¥${Number(pkg.price).toFixed(2)}` : '-',
              exclusivePriceDesc: pkg.exclusivePrice !== undefined && pkg.exclusivePrice !== null ? `¥${Number(pkg.exclusivePrice).toFixed(2)}` : '-',
              inclusivePriceDesc: pkg.inclusivePrice !== undefined && pkg.inclusivePrice !== null ? `¥${Number(pkg.inclusivePrice).toFixed(2)}` : '-'
            }
          })
        }
      } catch (e) {
        console.error('解析包价快照JSON失败:', e)
      }
    }
    if (list.length === 0) {
      return [{ key: 'none', typeDesc: '-', name: '无包价信息', quantityDesc: '-', priceDesc: '-', exclusivePriceDesc: '-', inclusivePriceDesc: '-' }]
    }
    return list
  }

  const getTaxDataSource = () => {
    if (!selectedDateDetail) return []
    return [
      { key: '1', name: '增值税', value: selectedDateDetail.taxAmount !== null && selectedDateDetail.taxAmount !== undefined ? `¥${Number(selectedDateDetail.taxAmount).toFixed(2)}` : '-' },
      { key: '2', name: '服务费', value: selectedDateDetail.serviceCharge !== null && selectedDateDetail.serviceCharge !== undefined ? `¥${Number(selectedDateDetail.serviceCharge).toFixed(2)}` : '-' }
    ]
  }

  const getRoomTypeDisplay = () => {
    const name = hotelInfo.roomTypeName || '-'
    const code = hotelInfo.roomTypeCode
    if (code && code !== '-') {
      return `${name}（${code}）`
    }
    return name
  }

  const getRatePlanDisplay = () => {
    const name = hotelInfo.ratePlanName || hotelInfo.roomType || '-'
    const code = hotelInfo.ratePlanCode
    if (code && code !== '-') {
      return `${name}（${code}）`
    }
    return name
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        订单详情
      </h1>
      
      {/* 主要信息 */}
      <Card style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <Descriptions size="small" column={3}>
              <Descriptions.Item label="CRS订单号">{orderInfo.crsOrderNumber}</Descriptions.Item>
              <Descriptions.Item label="渠道订单号">{orderInfo.channelOrderNumber || '-'}</Descriptions.Item>
              <Descriptions.Item label="来源渠道">{orderInfo.sourceChannel}</Descriptions.Item>
              <Descriptions.Item label="PMS单号">
                <div>
                  {orderInfo.pmsNumber || '-'}
                </div>
              </Descriptions.Item>
              <Descriptions.Item label="订单创建时间">{orderInfo.createTime}</Descriptions.Item>
            </Descriptions>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', marginTop: 4 }}>
            <Tag color={orderInfo.statusColor} style={{ marginRight: 16, fontSize: 14, padding: '4px 12px' }}>
              {orderInfo.status}
            </Tag>
            <Space>
              <Button 
                type="default" 
                icon={<CloseCircleOutlined />} 
                onClick={handleCancelOrder}
                disabled={orderInfo.reservationStatus === 'cancelled'}
              >
                取消订单
              </Button>
              <Button 
                type="default" 
                icon={<HistoryOutlined />} 
                onClick={handleHistoryClick}
              >
                订单详情操作历史
              </Button>
            </Space>
          </div>
        </div>
      </Card>

      {/* 酒店及房间信息 */}
      <Card title="酒店及房间信息" style={{ marginBottom: 24 }}>
        <Descriptions size="small" column={2}>
          <Descriptions.Item label="酒店">{hotelInfo.hotelName}</Descriptions.Item>
          <Descriptions.Item label="入住日期">
            <span>
              {hotelInfo.checkInDate} - {hotelInfo.checkOutDate} 
              ({hotelInfo.nights}晚) {hotelInfo.roomCount}间
            </span>
          </Descriptions.Item>
          <Descriptions.Item label="房型">
            <Text strong>{getRoomTypeDisplay()}</Text>
          </Descriptions.Item>
          <Descriptions.Item label="房价名称">
            <Text strong>{getRatePlanDisplay()}</Text>
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 客人信息 */}
      <Card title="客人信息" style={{ marginBottom: 24 }}>
        <div style={{ marginBottom: 16 }}>
          <Text strong style={{ marginBottom: 8, display: 'block' }}>预订人</Text>
          <Table 
            size="small" 
            bordered 
            pagination={false}
            dataSource={[{ key: '1', ...bookingInfo }]} 
            columns={[
              { title: '预订人姓名', dataIndex: 'name' },
              { title: '预订人手机号', dataIndex: 'phone' },
              { title: '预订人邮箱', dataIndex: 'email' },
              { title: '预订人会员等级', dataIndex: 'memberLevel' },
              { title: '预订人会员号', dataIndex: 'memberNumber' }
            ]}
          />
        </div>
        <div style={{ marginTop: 16 }}>
          <Text strong style={{ marginBottom: 8, display: 'block' }}>入住人</Text>
          <Table 
            size="small" 
            bordered 
            pagination={false}
            dataSource={guestInfo.map((g, i) => ({ key: i + 1, ...g }))} 
            columns={[
              { title: '房间号', dataIndex: 'roomNumber' },
              { title: '入住人姓名', dataIndex: 'name' },
              { title: '入住人手机号', dataIndex: 'phone' },
              { title: '入住人邮箱', dataIndex: 'email' },
              { title: '入住人会员等级', dataIndex: 'memberLevel' },
              { title: '入住人会员号', dataIndex: 'memberNo' },
              { title: 'PMS 订单号', dataIndex: 'pmsAccount' },
              { title: 'PMS状态', dataIndex: 'pmsStatus' }
            ]}
          />
        </div>
        
        <div style={{ marginTop: 16 }}>
          <Text strong style={{ marginBottom: 8, display: 'block' }}>订单备注信息</Text>
          <Descriptions size="small" column={1} bordered>
            <Descriptions.Item label="客人备注">
              {remarkInfo.guestRemark || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="门店备注">
              {remarkInfo.hotelRemark || '-'}
            </Descriptions.Item>
          </Descriptions>
        </div>
      </Card>



      {/* 价格信息 */}
      <Card title="价格信息" style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', marginBottom: 16 }}>
          <div style={{ marginRight: 48 }}>
            <Text>订单原价: </Text>
            <Text strong style={{ fontSize: 16, color: '#ff4d4f', textDecoration: 'line-through' }}>¥{priceInfo.originalPrice || 0}</Text>
          </div>
          <div>
            <Text>订单金额: </Text>
            <Text strong style={{ fontSize: 16, color: '#ff4d4f' }}>¥{(priceInfo.actualPrice || 0).toFixed(2)}</Text>
          </div>
        </div>
        <Text strong style={{ marginBottom: 8, display: 'block' }}>每日价格信息</Text>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16, marginBottom: 24 }}>
          {(priceInfo.dailyPrices || []).map((item, index) => (
            <div key={index} style={{ 
              border: '1px solid #d9d9d9', 
              borderRadius: '4px', 
              padding: '12px', 
              width: '140px',
              textAlign: 'center',
              cursor: 'pointer',
              '&:hover': {
                borderColor: '#1890ff'
              }
            }} onClick={() => handleDateClick(item)}>
              <div style={{ marginBottom: 8, fontWeight: 'bold' }}>{item.date}</div>
              {item.originalPrice && <div style={{ marginBottom: 4, textDecoration: 'line-through', fontSize: '12px', color: '#999' }}>¥{Number(item.originalPrice).toFixed(2)}</div>}
              <div style={{ marginBottom: 4 }}>¥{Number(item.actualPrice || item.price || 0).toFixed(2)}</div>
              {item.breakfastIncluded && <div style={{ marginBottom: 4, fontSize: '12px', color: '#52c41a' }}>{item.breakfastCount ? `${item.breakfastCount}份早餐` : '含早餐'}</div>}
              {item.breakfast && <div style={{ marginBottom: 4, fontSize: '12px', color: '#52c41a' }}>{item.breakfast}</div>}
            </div>
          ))}
        </div>
        
        <Text strong style={{ marginBottom: 8, display: 'block' }}>促销信息</Text>
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={promotionInfo.map((item, index) => ({ key: index + 1, name: item.name || item.promotionName, discount: item.discount || item.discountType, amount: item.amount || item.discountAmount, code: item.code || item.promotionCode, provider: item.provider }))} 
          columns={[
            { title: '优惠名称', dataIndex: 'name' },
            { title: '折扣', dataIndex: 'discount' },
            { title: '优惠金额', dataIndex: 'amount', render: (text) => text ? `¥${text}` : '-' },
            { title: '优惠券码', dataIndex: 'code' },
            { title: '优惠承担方', dataIndex: 'provider' }
          ]}
          style={{ marginBottom: 24 }}
        />
        
        <Text strong style={{ marginBottom: 8, display: 'block' }}>支付信息</Text>
        <div style={{ marginBottom: 16, display: 'flex', gap: 48 }}>
          <Text strong>担保规则: {policyInfo.guaranteePolicyDesc || paymentInfo.guaranteeType || '-'}</Text>
          <Text strong>支付状态: {paymentInfo.paymentStatus || '-'}</Text>
        </div>
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={(paymentInfo.payments || []).map((p, i) => ({ key: i + 1, method: p.method || p.paymentMethod, transactionId: p.transactionId, amount: p.amount || p.paymentAmount, time: p.time || p.paidAt, cardNumber: p.cardNumber || p.creditCardLast4, securityCode: p.securityCode, expiryDate: p.expiryDate || p.creditCardExpiry }))} 
          columns={[
            { title: '支付方式', dataIndex: 'method' },
            { title: '支付流水号', dataIndex: 'transactionId' },
            { title: '支付金额', dataIndex: 'amount', render: (text) => text ? `¥${text}` : '' },
            { title: '支付时间', dataIndex: 'time' },
            { title: '信用卡号', dataIndex: 'cardNumber' },
            { title: '安全码', dataIndex: 'securityCode' },
            { title: '有效期', dataIndex: 'expiryDate' }
          ]}
          style={{ marginBottom: 24 }}
        />
      </Card>
      {/* 日期点击弹框 */}
      <Modal
        title="价格详情"
        open={modalVisible}
    onCancel={handleModalClose}
    footer={null}
    width={800}
  >
    <Tabs defaultActiveKey="1">
      <Tabs.TabPane tab="费用" key="1">
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={getFeeDataSource()} 
          columns={[
            { title: '', dataIndex: 'name', width: 150 },
            { title: '', dataIndex: 'value' }
          ]}
        />
      </Tabs.TabPane>
      <Tabs.TabPane tab="包价" key="2">
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={getPackageDataSource()} 
          columns={[
            { title: '包价类型', dataIndex: 'typeDesc', width: 110 },
            { title: '包价名称', dataIndex: 'name' },
            { title: '包价份数', dataIndex: 'quantityDesc', width: 90 },
            { title: '包价价格', dataIndex: 'priceDesc', width: 100 },
            { title: '不含税价', dataIndex: 'exclusivePriceDesc', width: 100 },
            { title: '含税价', dataIndex: 'inclusivePriceDesc', width: 100 }
          ]}
        />
      </Tabs.TabPane>
      <Tabs.TabPane tab="税费" key="3">
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={getTaxDataSource()} 
          columns={[
            { title: '', dataIndex: 'name', width: 150 },
            { title: '', dataIndex: 'value' }
          ]}
        />
      </Tabs.TabPane>
    </Tabs>
  </Modal>
      
      {/* 操作历史弹框 */}
      <Modal
        title="操作历史"
        open={historyModalVisible}
        onCancel={handleHistoryModalClose}
        footer={null}
        width={800}
      >
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={operationHistory.map((item, index) => ({
            key: index + 1,
            content: item.content || item.action,
            result: item.result,
            operator: item.operatorDisplay || item.operator,
            time: item.operationTime || item.time || item.createdAt,
            apiLog: item.apiLog,
            hasApiLog: item.hasApiLog
          }))} 
          columns={[
            { title: '操作内容', dataIndex: 'content', width: 150 },
            { title: '结果', dataIndex: 'result' },
            { title: '操作人', dataIndex: 'operator' },
            { title: '操作时间', dataIndex: 'time', width: 180 },
            {
              title: '接口日志',
              dataIndex: 'hasApiLog',
              render: (_, record) => (
                record.hasApiLog && record.apiLog
                  ? <a onClick={() => handleLogClick(record.apiLog)}>查看日志</a>
                  : '-'
              )
            }
          ]}
        />
      </Modal>
      
      {/* 接口日志弹框 */}
      <Modal
        title="接口日志"
        open={logModalVisible}
        onCancel={handleLogModalClose}
        footer={null}
        width={800}
      >
        <div style={{ marginBottom: 12 }}>
          <Text type="secondary">
            日志时间：{selectedApiLog?.createdAt || '-'}
          </Text>
        </div>
        <div style={{ marginBottom: 16 }}>
          <Text strong>入参：</Text>
          <div style={{ 
            border: '1px solid #d9d9d9', 
            borderRadius: '4px', 
            padding: '12px', 
            height: '150px', 
            overflow: 'auto',
            marginTop: 8
          }}>
            <pre style={{ margin: 0, fontSize: '12px' }}>
              {formatLogContent(selectedApiLog?.requestBody)}
            </pre>
          </div>
        </div>
        <div style={{ marginBottom: 16 }}>
          <Text strong>出参：</Text>
          <div style={{ 
            border: '1px solid #d9d9d9', 
            borderRadius: '4px', 
            padding: '12px', 
            height: '150px', 
            overflow: 'auto',
            marginTop: 8
          }}>
            <pre style={{ margin: 0, fontSize: '12px' }}>
              {formatLogContent(selectedApiLog?.responseBody)}
            </pre>
          </div>
        </div>
        <div>
          <Text strong>失败原因：</Text>
          <div style={{
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
            padding: '12px',
            minHeight: '52px',
            marginTop: 8,
            whiteSpace: 'pre-wrap',
            wordBreak: 'break-all'
          }}>
            {selectedApiLog?.errorMessage || '-'}
          </div>
        </div>
      </Modal>
    </div>
  )
}

export default ReservationDetail

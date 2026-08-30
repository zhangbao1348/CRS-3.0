import { useState, useEffect } from 'react'
import { App as AntApp, Card, Typography, Descriptions, Table, Tag, Button, Space, Modal, Tabs, Input, Select } from 'antd'
import { CloseCircleOutlined, HistoryOutlined, SwapOutlined, ToolOutlined } from '@ant-design/icons'
import { useSearchParams } from 'react-router-dom'
import { reservationApi } from '../../utils/api'

const { Text } = Typography

const statusLabels = {
  pending: '待确认',
  pending_payment: '待支付',
  confirmed: '已确认',
  checked_in: '已入住',
  checked_out: '已离店',
  no_show: '未到'
}

const normalStatusTransitions = {
  pending: ['confirmed'],
  pending_payment: ['confirmed'],
  confirmed: ['checked_in', 'no_show'],
  checked_in: ['checked_out']
}

const ReservationDetail = () => {
  const { message } = AntApp.useApp()
  const [searchParams] = useSearchParams()
  const reservationId = searchParams.get('id')

  const [orderDetail, setOrderDetail] = useState(null)
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [selectedDateDetail, setSelectedDateDetail] = useState(null)
  const [historyModalVisible, setHistoryModalVisible] = useState(false)
  const [logModalVisible, setLogModalVisible] = useState(false)
  const [selectedApiLog, setSelectedApiLog] = useState(null)
  const [cancelModalVisible, setCancelModalVisible] = useState(false)
  const [cancelReason, setCancelReason] = useState('')
  const [cancelSubmitting, setCancelSubmitting] = useState(false)
  const [statusModalVisible, setStatusModalVisible] = useState(false)
  const [targetStatus, setTargetStatus] = useState(null)
  const [statusSubmitting, setStatusSubmitting] = useState(false)
  const [manualModalVisible, setManualModalVisible] = useState(false)
  const [manualReason, setManualReason] = useState('')
  const [manualTargetStatus, setManualTargetStatus] = useState(null)
  const [manualSubmitting, setManualSubmitting] = useState(false)

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
      message.error(error?.response?.data?.error || '获取订单详情失败')
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
    const reason = cancelReason.trim()
    if (!reason) {
      message.warning('请填写取消原因')
      return
    }
    setCancelSubmitting(true)
    try {
      await reservationApi.cancel(reservationId, { cancelReason: reason })
      message.success('订单取消成功')
      setCancelModalVisible(false)
      setCancelReason('')
      await fetchDetail()
    } catch (error) {
      message.error(error?.response?.data?.error || '取消订单失败')
    } finally {
      setCancelSubmitting(false)
    }
  }

  const handleStatusUpdate = async () => {
    if (!reservationId || !targetStatus) {
      message.warning('请选择目标状态')
      return
    }
    setStatusSubmitting(true)
    try {
      await reservationApi.updateStatus(reservationId, { reservationStatus: targetStatus })
      message.success('订单状态更新成功')
      setStatusModalVisible(false)
      setTargetStatus(null)
      await fetchDetail()
    } catch (error) {
      message.error(error?.response?.data?.error || '订单状态更新失败')
    } finally {
      setStatusSubmitting(false)
    }
  }

  const handleManualIntervene = async () => {
    const reason = manualReason.trim()
    if (!reason) {
      message.warning('请填写人工干预原因')
      return
    }
    setManualSubmitting(true)
    try {
      await reservationApi.manualIntervene(reservationId, {
        reason,
        reservationStatus: manualTargetStatus || undefined
      })
      message.success('人工干预已记录')
      setManualModalVisible(false)
      setManualReason('')
      setManualTargetStatus(null)
      await fetchDetail()
    } catch (error) {
      message.error(error?.response?.data?.error || '人工干预失败')
    } finally {
      setManualSubmitting(false)
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
  const cancellableStatuses = ['confirmed', 'pending', 'pending_payment']
  const canCancel = cancellableStatuses.includes(orderInfo.reservationStatus)
  const availableStatusTransitions = normalStatusTransitions[orderInfo.reservationStatus] || []

  // 组装当前选中日期的各项 Tab 的数据源
  const getFeeDataSource = () => {
    if (!selectedDateDetail) return []
    const actPrice = Number(selectedDateDetail.actualPrice || selectedDateDetail.price || 0)
    let origPrice = selectedDateDetail.originalPrice !== null ? Number(selectedDateDetail.originalPrice) : actPrice
    
    // 如果折扣前原价被误存为了不含税裸房费，且在此处导致了折扣前价格小于折扣后售价的错误逻辑，则自动纠正为含税售价
    if (origPrice < actPrice) {
      origPrice = actPrice
    }
    
    return [
      { key: '1', name: '折扣前房费', value: `¥${origPrice.toFixed(2)}` },
      { key: '2', name: '折扣后房费', value: `¥${actPrice.toFixed(2)}` }
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
              code: pkg.code || '-',
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
      return [{ key: 'none', typeDesc: '-', code: '-', name: '无包价信息', quantityDesc: '-', priceDesc: '-', exclusivePriceDesc: '-', inclusivePriceDesc: '-' }]
    }
    return list
  }

  const getTaxDataSource = () => {
    if (!selectedDateDetail) return []
    // 优先使用后端动态返回的多税种细表
    if (selectedDateDetail.taxes && selectedDateDetail.taxes.length > 0) {
      return selectedDateDetail.taxes.map((t, idx) => ({
        key: String(idx + 1),
        code: t.taxCode || '-',
        name: t.taxName || '-',
        rate: t.rateAmount !== null && t.rateAmount !== undefined ? `${Number(t.rateAmount).toFixed(2)}%` : '-',
        value: t.calculatedAmount !== null && t.calculatedAmount !== undefined ? `¥${Number(t.calculatedAmount).toFixed(2)}` : '-'
      }))
    }
    // 向下兼容历史老订单
    const fallbackList = []
    if (selectedDateDetail.taxAmount !== null && selectedDateDetail.taxAmount !== undefined && Number(selectedDateDetail.taxAmount) > 0) {
      fallbackList.push({ key: '1', code: 'ZENGZHISHUI', name: '增值税', rate: '6.00%', value: `¥${Number(selectedDateDetail.taxAmount).toFixed(2)}` })
    }
    if (selectedDateDetail.serviceCharge !== null && selectedDateDetail.serviceCharge !== undefined && Number(selectedDateDetail.serviceCharge) > 0) {
      fallbackList.push({ key: '2', code: 'FUWUFEI', name: '服务费', rate: '10.00%', value: `¥${Number(selectedDateDetail.serviceCharge).toFixed(2)}` })
    }
    if (fallbackList.length === 0) {
      return [{ key: 'none', code: '-', name: '无税费信息', rate: '-', value: '-' }]
    }
    return fallbackList
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
              {orderInfo.reservationStatus === 'cancelled' && (
                <Descriptions.Item label="取消时间">{orderInfo.cancelledAt || '-'}</Descriptions.Item>
              )}
              {orderInfo.reservationStatus === 'cancelled' && (
                <Descriptions.Item label="取消操作人">{orderInfo.cancelledBy || '-'}</Descriptions.Item>
              )}
              {orderInfo.reservationStatus === 'cancelled' && (
                <Descriptions.Item label="取消原因" span={2}>{orderInfo.cancelReason || '-'}</Descriptions.Item>
              )}
            </Descriptions>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', marginTop: 4 }}>
            <Tag color={orderInfo.statusColor} style={{ marginRight: 16, fontSize: 14, padding: '4px 12px' }}>
              {orderInfo.status}
            </Tag>
            <Space>
              <Button
                icon={<SwapOutlined />}
                onClick={() => setStatusModalVisible(true)}
                disabled={availableStatusTransitions.length === 0}
              >
                更新状态
              </Button>
              <Button
                icon={<ToolOutlined />}
                onClick={() => setManualModalVisible(true)}
                disabled={orderInfo.reservationStatus === 'cancelled'}
              >
                人工干预
              </Button>
              <Button 
                type="default" 
                icon={<CloseCircleOutlined />} 
                onClick={() => setCancelModalVisible(true)}
                disabled={!canCancel}
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

      <Modal
        title="取消订单"
        open={cancelModalVisible}
        okText="确认取消"
        cancelText="返回"
        okButtonProps={{ danger: true, loading: cancelSubmitting }}
        onOk={handleCancelOrder}
        onCancel={() => {
          if (!cancelSubmitting) {
            setCancelModalVisible(false)
            setCancelReason('')
          }
        }}
        destroyOnHidden
      >
        <Text type="secondary">取消后将释放对应日期的库存，并记录当前登录用户与操作原因。</Text>
        <div style={{ marginTop: 16 }}>
          <Text strong><Text type="danger">* </Text>取消原因</Text>
          <Input.TextArea
            aria-label="取消原因"
            value={cancelReason}
            onChange={(event) => setCancelReason(event.target.value)}
            placeholder="请填写取消原因"
            maxLength={500}
            showCount
            autoSize={{ minRows: 3, maxRows: 6 }}
            style={{ marginTop: 8 }}
          />
        </div>
      </Modal>

      <Modal
        title="更新订单状态"
        open={statusModalVisible}
        okText="确认更新"
        cancelText="返回"
        confirmLoading={statusSubmitting}
        onOk={handleStatusUpdate}
        onCancel={() => {
          if (!statusSubmitting) {
            setStatusModalVisible(false)
            setTargetStatus(null)
          }
        }}
        destroyOnHidden
      >
        <Text type="secondary">状态变更将写入订单操作历史，且只能按正常业务状态机流转。</Text>
        <Select
          aria-label="目标状态"
          value={targetStatus}
          onChange={setTargetStatus}
          placeholder="请选择目标状态"
          options={availableStatusTransitions.map(value => ({ value, label: statusLabels[value] }))}
          style={{ width: '100%', marginTop: 16 }}
        />
      </Modal>

      <Modal
        title="人工干预"
        open={manualModalVisible}
        okText="确认干预"
        cancelText="返回"
        confirmLoading={manualSubmitting}
        onOk={handleManualIntervene}
        onCancel={() => {
          if (!manualSubmitting) {
            setManualModalVisible(false)
            setManualReason('')
            setManualTargetStatus(null)
          }
        }}
        destroyOnHidden
      >
        <Text type="secondary">用于处理正常状态机无法覆盖的特殊情况。原因必填，目标状态选填。</Text>
        <div style={{ marginTop: 16 }}>
          <Text strong><Text type="danger">* </Text>干预原因</Text>
          <Input.TextArea
            aria-label="干预原因"
            value={manualReason}
            onChange={(event) => setManualReason(event.target.value)}
            placeholder="请说明干预原因"
            maxLength={500}
            showCount
            autoSize={{ minRows: 3, maxRows: 6 }}
            style={{ marginTop: 8 }}
          />
        </div>
        <div style={{ marginTop: 16 }}>
          <Text strong>强制调整状态（选填）</Text>
          <Select
            aria-label="强制目标状态"
            allowClear
            value={manualTargetStatus}
            onChange={setManualTargetStatus}
            placeholder="不调整状态"
            options={Object.entries(statusLabels)
              .filter(([value]) => value !== orderInfo.reservationStatus)
              .map(([value, label]) => ({ value, label }))}
            style={{ width: '100%', marginTop: 8 }}
          />
        </div>
      </Modal>

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
          <Descriptions size="small" column={1} bordered styles={{ label: { width: '150px' } }}>
            <Descriptions.Item label="客人备注">
              {remarkInfo.guestRemark || remarkInfo.specialRequest || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="门店备注">
              {remarkInfo.hotelRemark || '-'}
            </Descriptions.Item>
          </Descriptions>
        </div>
      </Card>



      {/* 价格信息 */}
      <Card title="价格信息" style={{ marginBottom: 24 }}>
        {(() => {
          // 累加每日税费及服务费
          let totalTax = 0;
          let totalSvc = 0;
          let dailyPrices = priceInfo.dailyPrices || [];
          dailyPrices.forEach(dp => {
            if (dp.taxes && dp.taxes.length > 0) {
              dp.taxes.forEach(t => {
                totalTax += Number(t.calculatedAmount || 0);
              });
            } else {
              // 兼容老数据
              totalTax += Number(dp.taxAmount || 0);
              totalSvc += Number(dp.serviceCharge || 0);
            }
          });

          const calculatedTotalTax = totalTax + totalSvc;
          const actualPrice = priceInfo.actualPrice || 0;
          const originalPrice = priceInfo.originalPrice || 0;
          const exclusivePrice = Math.max(0, actualPrice - calculatedTotalTax);

          const hasRealDiscount = originalPrice > actualPrice && Math.abs(originalPrice - exclusivePrice) > 1;
          const displayOriginalPrice = hasRealDiscount ? originalPrice : actualPrice;

          return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 16 }}>
              <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 24 }}>
                <div>
                  <Text>订单原价: </Text>
                  {hasRealDiscount ? (
                    <Text strong style={{ fontSize: 16, color: '#999', textDecoration: 'line-through', marginRight: 16 }}>
                      ¥{Number(displayOriginalPrice).toFixed(2)}
                    </Text>
                  ) : (
                    <Text strong style={{ fontSize: 16, color: '#555', marginRight: 16 }}>
                      ¥{Number(displayOriginalPrice).toFixed(2)}
                    </Text>
                  )}
                </div>
                <div>
                  <Text>订单金额 (含税): </Text>
                  <Text strong style={{ fontSize: 20, color: '#ff4d4f' }}>
                    ¥{Number(actualPrice).toFixed(2)}
                  </Text>
                </div>
                {calculatedTotalTax > 0 && (
                  <div style={{ background: '#f5f5f5', padding: '4px 12px', borderRadius: '4px', fontSize: '13px' }}>
                    <Text type="secondary">房费 (不含税): </Text>
                    <Text strong style={{ color: '#555' }}>¥{exclusivePrice.toFixed(2)}</Text>
                    <span style={{ margin: '0 8px', color: '#ccc' }}>|</span>
                    <Text type="secondary">服务费及税费: </Text>
                    <Text strong style={{ color: '#e28743' }}>¥{calculatedTotalTax.toFixed(2)}</Text>
                  </div>
                )}
              </div>
            </div>
          );
        })()}
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
              {item.originalPrice && Number(item.originalPrice) > Number(item.actualPrice || item.price || 0) && (
                <div style={{ marginBottom: 4, textDecoration: 'line-through', fontSize: '12px', color: '#999' }}>
                  ¥{Number(item.originalPrice).toFixed(2)}
                </div>
              )}
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
            { title: '包价代码', dataIndex: 'code', width: 110 },
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
            { title: '税费CODE', dataIndex: 'code', width: 180 },
            { title: '税费名称', dataIndex: 'name' },
            { title: '税率 (%)', dataIndex: 'rate', width: 120 },
            { title: '税费金额', dataIndex: 'value', width: 150 }
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

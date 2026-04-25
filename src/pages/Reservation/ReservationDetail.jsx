import React, { useState, useEffect } from 'react'
import { Card, Typography, Descriptions, Table, Tag, Button, Space, Divider, Modal, Tabs } from 'antd'
import { CloseCircleOutlined, HistoryOutlined } from '@ant-design/icons'

const { Title, Text } = Typography

const ReservationDetail = () => {
  // 模拟订单详情数据
  const [orderDetail, setOrderDetail] = useState({
    crsOrderNumber: '12345678',
    channelOrderNumber: '123456789',
    sourceChannel: '携程',
    status: '已确认',
    statusColor: 'green',
    pmsNumber: 'PMS123456789',
    createTime: '2026-01-23 23:23:23',
    hotelInfo: {
      hotelName: '上海丰汇大酒店',
      roomType: 'OTA-2B (OTA双早)',
      roomNumber: 'ST (高级大床房)',
      checkInDate: '2026-01-21',
      checkOutDate: '2026-01-24',
      nights: 3,
      roomCount: 2
    },
    bookingInfo: {
      name: '张三',
      phone: '13800000000',
      email: 'zhangsan@163.com',
      memberLevel: '金卡',
      memberNumber: '12345678'
    },
    guestInfo: [
      {
        roomNumber: '房间1',
        name: '张三',
        phone: '13800000000',
        email: 'zhangsan@163.com',
        memberLevel: '金卡',
        memberNumber: '12345678',
        pmsAccount: 'PMS123',
        pmsStatus: '入住中'
      },
      {
        roomNumber: '房间2',
        name: '李四',
        phone: '13800000000',
        email: 'zhang@163.com',
        memberLevel: '金卡',
        memberNumber: '12345678',
        pmsAccount: 'PMS245',
        pmsStatus: '已入住'
      },
      {
        roomNumber: '房间3',
        name: '王五',
        phone: '13800000000',
        email: 'wangwu@163.com',
        memberLevel: '金卡',
        memberNumber: '12345678',
        pmsAccount: 'PMS345',
        pmsStatus: '待入住'
      },
      {
        roomNumber: '房间4',
        name: '赵六',
        phone: '13800000000',
        email: 'zhaoliu@163.com',
        memberLevel: '金卡',
        memberNumber: '12345678',
        pmsAccount: 'PMS456',
        pmsStatus: '待入住'
      }
    ],
    reimbursementInfo: {
      companyName: '科大讯飞科技有限公司',
      companyTaxNumber: '138XXXXXXXX',
      companyMemberNumber: '金卡',
      reimbursementType: '公司'
    },
    priceInfo: {
      originalPrice: 1500,
      actualPrice: 1234.00,
      dailyPrices: [
        {
          date: '05-11(周五)',
          price: 299.00,
          breakfast: '2份早餐',
          special: ''
        },
        {
          date: '05-12(周六)',
          price: 299.00,
          breakfast: '2份早餐',
          special: ''
        },
        {
          date: '05-13(周日)',
          price: 299.00,
          breakfast: '2份早餐',
          special: '地上打地铺'
        },
        {
          date: '05-14(周二)',
          price: 200.00,
          breakfast: '',
          special: '2B早餐'
        },
        {
          date: '05-15(周三)',
          price: 200.00,
          breakfast: '',
          special: ''
        },
        {
          date: '05-16(周四)',
          price: 200.00,
          breakfast: '',
          special: ''
        }
      ]
    },
    promotionInfo: [
      {
        name: '携程天天特价',
        discount: '8折',
        amount: 100,
        code: '',
        provider: '酒店'
      },
      {
        name: '会员费优惠券',
        discount: '9折',
        amount: 50,
        code: '123476542',
        provider: '集团'
      },
      {
        name: '积分抵扣',
        discount: '',
        amount: 50,
        code: '',
        provider: '集团'
      }
    ],
    paymentInfo: {
      paymentMethod: '微信支付/支付宝/银行卡/信用卡',
      paymentStatus: '已完成支付/银行已扣款',
      paymentDetails: [
        {
          method: '微信/支付宝',
          transactionId: '123456',
          amount: 1200,
          time: '2026-01-25 12:32:23',
          cardNumber: '',
          securityCode: '',
          expiryDate: ''
        },
        {
          method: '信用卡',
          transactionId: '',
          amount: 150,
          time: '',
          cardNumber: 'XXXXXXXXXXXX',
          securityCode: 'XXX',
          expiryDate: '28-03'
        }
      ]
    },
    commissionInfo: {
      rate: '15%',
      amount: 50
    },
    remarkInfo: {
      guestRemark: '客人需要无烟房，要求高楼层，安静一些',
      hotelRemark: '已安排无烟房，12楼，面向花园'
    }
  })
  
  // 弹框状态
  const [modalVisible, setModalVisible] = useState(false)
  const [historyModalVisible, setHistoryModalVisible] = useState(false)
  const [logModalVisible, setLogModalVisible] = useState(false)
  
  // 处理日期框点击
  const handleDateClick = () => {
    setModalVisible(true)
  }
  
  // 处理弹框关闭
  const handleModalClose = () => {
    setModalVisible(false)
  }
  
  // 处理操作历史按钮点击
  const handleHistoryClick = () => {
    setHistoryModalVisible(true)
  }
  
  // 处理操作历史弹框关闭
  const handleHistoryModalClose = () => {
    setHistoryModalVisible(false)
  }
  
  // 处理接口日志链接点击
  const handleLogClick = () => {
    setLogModalVisible(true)
  }
  
  // 处理接口日志弹框关闭
  const handleLogModalClose = () => {
    setLogModalVisible(false)
  }

  // 处理取消订单
  const handleCancelOrder = () => {
    console.log('取消订单')
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
              <Descriptions.Item label="CRS订单号">{orderDetail.crsOrderNumber}</Descriptions.Item>
              <Descriptions.Item label="渠道订单号">{orderDetail.channelOrderNumber}</Descriptions.Item>
              <Descriptions.Item label="来源渠道">{orderDetail.sourceChannel}</Descriptions.Item>
              <Descriptions.Item label="PMS单号">
                <div>
                  {orderDetail.pmsNumber}<br />
                  PMS987654321<br />
                  PMS112233445
                </div>
              </Descriptions.Item>
              <Descriptions.Item label="订单创建时间">{orderDetail.createTime}</Descriptions.Item>
            </Descriptions>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', marginTop: 4 }}>
            <Tag color={orderDetail.statusColor} style={{ marginRight: 16, fontSize: 14, padding: '4px 12px' }}>
              {orderDetail.status}
            </Tag>
            <Space>
              <Button 
                type="default" 
                icon={<CloseCircleOutlined />} 
                onClick={handleCancelOrder}
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
        <Descriptions size="small" column={3}>
          <Descriptions.Item label="酒店">{orderDetail.hotelInfo.hotelName}</Descriptions.Item>
          <Descriptions.Item label="房型">{orderDetail.hotelInfo.roomType}</Descriptions.Item>
          <Descriptions.Item label="房号">{orderDetail.hotelInfo.roomNumber}</Descriptions.Item>
          <Descriptions.Item label="入住日期">
            <span>
              {orderDetail.hotelInfo.checkInDate} - {orderDetail.hotelInfo.checkOutDate} 
              ({orderDetail.hotelInfo.nights}晚) {orderDetail.hotelInfo.roomCount}间
            </span>
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
            dataSource={[{ key: '1', ...orderDetail.bookingInfo }]} 
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
            dataSource={orderDetail.guestInfo} 
            columns={[
              { title: '房间号', dataIndex: 'roomNumber' },
              { title: '预订人姓名', dataIndex: 'name' },
              { title: '预订人手机号', dataIndex: 'phone' },
              { title: '预订人邮箱', dataIndex: 'email' },
              { title: '预订人会员等级', dataIndex: 'memberLevel' },
              { title: '预订人会员号', dataIndex: 'memberNumber' },
              { title: 'PMS 订单号', dataIndex: 'pmsAccount' },
              { title: 'PMS状态', dataIndex: 'pmsStatus' }
            ]}
          />
        </div>
        <div style={{ marginTop: 16 }}>
          <Text strong style={{ marginBottom: 8, display: 'block' }}>档案信息</Text>
          <Table 
            size="small" 
            bordered 
            pagination={false}
            dataSource={[{ key: '1', ...orderDetail.reimbursementInfo }]} 
            columns={[
              { title: '公司名称', dataIndex: 'companyName' },
              { title: '公司卡会员号', dataIndex: 'companyTaxNumber' },
              { title: '公司卡会员等级', dataIndex: 'companyMemberNumber' },
              { title: '档案类型', dataIndex: 'reimbursementType' }
            ]}
          />
        </div>
        
        <div style={{ marginTop: 16 }}>
          <Text strong style={{ marginBottom: 8, display: 'block' }}>订单备注信息</Text>
          <Descriptions size="small" column={1} bordered>
            <Descriptions.Item label="客人备注">
              {orderDetail.remarkInfo.guestRemark}
            </Descriptions.Item>
            <Descriptions.Item label="门店备注">
              {orderDetail.remarkInfo.hotelRemark}
            </Descriptions.Item>
          </Descriptions>
        </div>
      </Card>



      {/* 价格信息 */}
      <Card title="价格信息" style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', marginBottom: 16 }}>
          <div style={{ marginRight: 48 }}>
            <Text>订单原价: </Text>
            <Text strong style={{ fontSize: 16, color: '#ff4d4f', textDecoration: 'line-through' }}>¥{orderDetail.priceInfo.originalPrice}</Text>
          </div>
          <div>
            <Text>订单金额: </Text>
            <Text strong style={{ fontSize: 16, color: '#ff4d4f' }}>¥{orderDetail.priceInfo.actualPrice.toFixed(2)}</Text>
          </div>
        </div>
        <Text strong style={{ marginBottom: 8, display: 'block' }}>每日价格信息</Text>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16, marginBottom: 24 }}>
          {orderDetail.priceInfo.dailyPrices.map((item, index) => (
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
            }} onClick={handleDateClick}>
              <div style={{ marginBottom: 8, fontWeight: 'bold' }}>{item.date}</div>
              <div style={{ marginBottom: 4, textDecoration: 'line-through', fontSize: '12px', color: '#999' }}>¥399.00</div>
              <div style={{ marginBottom: 4 }}>¥{item.price.toFixed(2)}</div>
              {item.breakfast && <div style={{ marginBottom: 4, fontSize: '12px', color: '#52c41a' }}>{item.breakfast}</div>}
              {item.date === '05-13(周日)' && (
                <div style={{ fontSize: '12px', color: '#52c41a' }}>1张迪士尼门票</div>
              )}
            </div>
          ))}
        </div>
        
        <Text strong style={{ marginBottom: 8, display: 'block' }}>促销信息</Text>
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={orderDetail.promotionInfo.map((item, index) => ({ key: index + 1, ...item }))} 
          columns={[
            { title: '优惠名称', dataIndex: 'name' },
            { title: '折扣', dataIndex: 'discount' },
            { title: '优惠金额', dataIndex: 'amount', render: (text) => `¥${text}` },
            { title: '优惠券码', dataIndex: 'code' },
            { title: '优惠承担方', dataIndex: 'provider' }
          ]}
          style={{ marginBottom: 24 }}
        />
        
        <Text strong style={{ marginBottom: 8, display: 'block' }}>支付信息</Text>
        <div style={{ marginBottom: 16, display: 'flex', gap: 48 }}>
          <Text strong>担保规则: 预付/现付无担保/现付信用卡担保</Text>
          <Text strong>支付状态: 已支付/无需支付/现付已担保</Text>
        </div>
        <Table 
          size="small" 
          bordered 
          pagination={false}
          dataSource={[
            {
              key: '1',
              method: '微信/支付宝',
              transactionId: '123456',
              amount: 1200,
              time: '2026-01-23 23:23:23',
              cardNumber: 'XXXXXXXX',
              securityCode: 'XXX',
              expiryDate: '26-03'
            },
            {
              key: '2',
              method: '信用卡担保',
              transactionId: '',
              amount: '',
              time: '',
              cardNumber: 'XXXXXXXX',
              securityCode: 'XXX',
              expiryDate: '26-03'
            },
            {
              key: '3',
              method: '公司担保',
              transactionId: '',
              amount: '',
              time: '',
              cardNumber: '',
              securityCode: '',
              expiryDate: ''
            }
          ]} 
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
        
        <Text strong style={{ marginBottom: 8, display: 'block' }}>佣金信息</Text>
        <Descriptions size="small" column={2}>
          <Descriptions.Item label="订单佣金比例">{orderDetail.commissionInfo.rate}</Descriptions.Item>
          <Descriptions.Item label="佣金金额">¥{orderDetail.commissionInfo.amount}</Descriptions.Item>
        </Descriptions>
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
              dataSource={[
                { key: '1', name: '折扣前房费', value: '111' },
                { key: '2', name: '折扣1', value: '2' },
                { key: '3', name: '折扣2', value: '3' },
                { key: '4', name: '折扣后房费', value: '98' },
                { key: '5', name: '折扣后税费', value: '' }
              ]} 
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
              dataSource={[
                { key: '1', name: '早餐', value: '2份 (1*2人)' },
                { key: '2', name: '接机', value: '1份 (1*1单)' },
                { key: '3', name: '吉祥物玩偶', value: '1份 (1*1儿童)' }
              ]} 
              columns={[
                { title: '', dataIndex: 'name', width: 150 },
                { title: '', dataIndex: 'value' }
              ]}
            />
          </Tabs.TabPane>
          <Tabs.TabPane tab="税费" key="3">
            <Table 
              size="small" 
              bordered 
              pagination={false}
              dataSource={[
                { key: '1', name: '增值税', value: '¥6.00' },
                { key: '2', name: '城市建设税', value: '¥20.00' },
                { key: '3', name: '服务费', value: '¥10.00' }
              ]} 
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
          dataSource={[
            { key: '1', content: '创建订单', result: '成功', operator: '携程', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '2', content: 'CRS创建订单', result: '成功', operator: 'CRS', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '3', content: '创建PMS订单', result: '成功', operator: 'CRS', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '4', content: '客人入住', result: '成功', operator: 'PMS', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '5', content: '客人入住通知携程', result: '成功', operator: 'CRS', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '6', content: '客人离店', result: '成功', operator: 'PMS', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '7', content: '客人NOSHOW', result: '成功', operator: 'PMS', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '8', content: '取消订单', result: '成功', operator: '携程', time: '2026-01-23 12:12:12', log: '查看接口日志' },
            { key: '9', content: '取消PMS订单', result: '成功', operator: 'CRS', time: '2026-01-23 12:12:12', log: '查看接口日志' }
          ]} 
          columns={[
            { title: '操作内容', dataIndex: 'content', width: 150 },
            { title: '结果', dataIndex: 'result' },
            { title: '操作人', dataIndex: 'operator' },
            { title: '操作时间', dataIndex: 'time', width: 180 },
            { title: '接口日志', dataIndex: 'log', render: (text) => <a href="#" onClick={handleLogClick}>{text}</a> }
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
{`{
  "orderId": "123456789",
  "hotelId": "987654",
  "guestName": "张三",
  "phone": "13800000000",
  "checkInDate": "2026-01-21",
  "checkOutDate": "2026-01-24",
  "roomType": "ST",
  "roomCount": 1,
  "price": 1234.00,
  "channel": "携程",
  "paymentMethod": "微信支付"
}`}
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
{`{
  "code": "0",
  "message": "success",
  "data": {
    "orderId": "123456789",
    "pmsOrderId": "PMS123456789",
    "status": "已确认",
    "createTime": "2026-01-23 12:12:12",
    "price": 1234.00
  }
}`}
            </pre>
          </div>
        </div>
        <div>
          <Text strong>失败原因：</Text>
          <Text>XXXXXX</Text>
        </div>
      </Modal>
    </div>
  )
}

export default ReservationDetail
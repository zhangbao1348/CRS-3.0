import React, { useState } from 'react'
import { Form, Input, Select, Checkbox, Button, Space, Card, Row, Col, Tabs, Tag, Radio, Table, Switch } from 'antd'
import { PlusOutlined, CloseOutlined } from '@ant-design/icons'

const { Option } = Select

const AddRatePlan = () => {
  // 状态管理
  const [form] = Form.useForm()
  const [includedPackages, setIncludedPackages] = useState([])
  const [selectedPackages, setSelectedPackages] = useState([])
  // 优惠券和促销规则状态
  const [couponRule, setCouponRule] = useState('unlimited')
  const [promotionRule, setPromotionRule] = useState('unlimited')
  // 当前选择的价格计划类型
  const [rateType, setRateType] = useState('basic')

  // 包价/早餐选项
  const packageOptions = [
    { id: 1, code: 'ZC0001', name: '早餐00001' },
    { id: 2, code: 'lunch0915', name: '午餐0915' },
    { id: 3, code: 'TEST', name: '含早餐' }
  ]

  // 处理包价选择
  const handlePackageChange = (value) => {
    setSelectedPackages(value)
  }

  // 添加包价
  const handleAddPackage = () => {
    // 模拟添加包价逻辑
    console.log('添加包价')
  }

  // 保存并下一步
  const handleSave = () => {
    form.validateFields().then(values => {
      console.log('表单数据:', values)
      // 处理保存逻辑
    }).catch(errorInfo => {
      console.log('表单验证失败:', errorInfo)
    })
  }



  // 定义标签页内容
  const tabItems = [
    {
      key: '1',
      label: '价格计划维护',
      children: (
        <Card style={{ marginBottom: 24 }}>
          <Form form={form} layout="vertical" style={{ maxWidth: 800, overflow: 'visible' }}>
            {/* 基础信息 */}
            <h3 style={{ marginBottom: 16, fontWeight: 600 }}>基础信息</h3>
            
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="rateCode"
                  label="价格计划代码"
                  rules={[{ required: true, message: '请输入价格计划代码' }]}
                >
                  <Input placeholder="请输入价格计划代码" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="rateName"
                  label="价格计划名称"
                  rules={[{ required: true, message: '请输入价格计划名称' }]}
                >
                  <Input placeholder="请输入价格计划名称" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="rateCategory"
                  label="价格计划类别"
                >
                  <Select placeholder="请选择价格计划类别">
                    <Option value="1">市场码1</Option>
                    <Option value="2">市场码2</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="marketCode"
                  label="市场码"
                  rules={[{ required: true, message: '请选择市场码' }]}
                >
                  <Select placeholder="请选择市场码">
                    <Option value="1">市场码1</Option>
                    <Option value="2">市场码2</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="sourceCode"
                  label="来源码"
                  rules={[{ required: true, message: '请选择来源码' }]}
                >
                  <Select placeholder="请选择来源码">
                    <Option value="1">来源码1</Option>
                    <Option value="2">来源码2</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="rateType"
                  label="类型"
                  rules={[{ required: true, message: '请选择类型' }]}
                  initialValue="basic"
                >
                  <Select 
                    placeholder="请选择类型"
                    onChange={(value) => setRateType(value)}
                    defaultValue="basic"
                  >
                    <Option value="basic">基础价格计划</Option>
                    <Option value="derivative">衍生价格计划</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            
            {/* 包价/早餐 */}
            <Form.Item
              name="packages"
              label="包价/早餐"
            >
              <div style={{ marginBottom: 8 }}>
                <Select
                  mode="tags"
                  value={selectedPackages}
                  onChange={handlePackageChange}
                  style={{ width: 'calc(100% - 40px)' }}
                  placeholder="请选择或输入包价/早餐"
                >
                  {packageOptions.map(pkg => (
                    <Option key={pkg.id} value={pkg.code}>
                      {pkg.code} - {pkg.name}
                    </Option>
                  ))}
                </Select>
                <Button
                  type="primary"
                  icon={<PlusOutlined />}
                  style={{ marginLeft: 8 }}
                  onClick={handleAddPackage}
                  size="middle"
                />
              </div>
              <div style={{ marginTop: 8 }}>
                {includedPackages.map(pkg => (
                  <Tag
                    key={pkg.id}
                    closable
                    onClose={() => {
                      setIncludedPackages(includedPackages.filter(item => item.id !== pkg.id))
                      setSelectedPackages(selectedPackages.filter(code => code !== pkg.code))
                    }}
                    style={{ marginRight: 8, marginBottom: 8 }}
                  >
                    {pkg.code} - {pkg.name}
                  </Tag>
                ))}
              </div>
            </Form.Item>
            
            {/* 适用房型 */}
            <Form.Item
              name="applicableRoomTypes"
              label="适用房型"
            >
              <Space wrap>
                <Checkbox value="standard">标准房</Checkbox>
                <Checkbox value="king">大床房</Checkbox>
                <Checkbox value="city-view-twin">城景双床房</Checkbox>
                <Checkbox value="city-view-king">城景大床房</Checkbox>
                <Checkbox value="sea-view-king">海景大床房</Checkbox>
                <Checkbox value="sea-view-twin">海景双床房</Checkbox>
              </Space>
            </Form.Item>
            
            {/* 价格信息 - 无论选择什么价格计划类型都显示 */}
            <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>价格信息</h3>
            
            {rateType === 'derivative' ? (
              <Row gutter={[16, 16]}>
                <Col span={12}>
                  <Form.Item
                    name="parentRateCode"
                    label="父级价格计划"
                    rules={[{ required: true, message: '请选择父级价格计划' }]}
                  >
                    <Select placeholder="请选择父级价格计划">
                      <Option value="1">父级价格计划1</Option>
                      <Option value="2">父级价格计划2</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="discount"
                    label="折扣"
                    rules={[{ required: true, message: '请输入折扣' }]}
                  >
                    <Input addonAfter="%" placeholder="请输入折扣" type="number" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="rounding"
                    label="取整方式"
                    rules={[{ required: true, message: '请选择取整方式' }]}
                  >
                    <Select placeholder="请选择取整方式">
                      <Option value="round">四舍五入</Option>
                      <Option value="floor">向下取整</Option>
                      <Option value="ceil">向上取整</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
            ) : (
              <Row gutter={[16, 16]}>
                <Col span={12}>
                  <Form.Item
                    name="roomTypeDiff"
                    label="房型价差选择"
                    rules={[{ required: true, message: '请选择房型价差' }]}
                  >
                    <Select placeholder="请选择房型价差">
                      <Option value="diff1">差价体系1</Option>
                      <Option value="diff2">差价体系2</Option>
                      <Option value="diff3">差价体系3</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="personDiff"
                    label="人数价差选择"
                    rules={[{ required: true, message: '请选择人数价差' }]}
                  >
                    <Select placeholder="请选择人数价差">
                      <Option value="diff1">差价体系1</Option>
                      <Option value="diff2">差价体系2</Option>
                      <Option value="diff3">差价体系3</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
            )}
            
            {/* 预订限制 */}
            <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>预订限制</h3>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>需要个人会员:</div>
              <Space wrap>
                <Checkbox value="silver">银卡</Checkbox>
                <Checkbox value="gold">金卡</Checkbox>
                <Checkbox value="platinum">铂金卡</Checkbox>
                <Checkbox value="diamond">黑金卡</Checkbox>
              </Space>
            </div>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>需要企业会员:</div>
              <Space wrap>
                <Checkbox value="silver-company">银卡</Checkbox>
                <Checkbox value="gold-company">金卡</Checkbox>
                <Checkbox value="platinum-company">铂金卡</Checkbox>
                <Checkbox value="diamond-company">黑金卡</Checkbox>
              </Space>
            </div>
            
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <div style={{ marginBottom: 8, fontWeight: 500 }}>提前预订天数限制:</div>
                <Space>
                  <Input placeholder="最小值" style={{ width: 100 }} type="number" />
                  <span>-</span>
                  <Input placeholder="最大值" style={{ width: 100 }} type="number" />
                </Space>
              </Col>
              <Col span={12}>
                <div style={{ marginBottom: 8, fontWeight: 500 }}>连住天数限制:</div>
                <Space>
                  <Input placeholder="最小值" style={{ width: 100 }} type="number" />
                  <span>-</span>
                  <Input placeholder="最大值" style={{ width: 100 }} type="number" />
                </Space>
              </Col>
            </Row>
            
            {/* 担保及取消规则 */}
            <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>担保及取消规则</h3>
            
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="guaranteeRule"
                  label="担保规则"
                  rules={[{ required: true, message: '请选择担保规则' }]}
                  initialValue="prepay"
                >
                  <Select placeholder="请选择担保规则" defaultValue="prepay">
                    <Option value="prepay">预付</Option>
                    <Option value="credit-card">信用卡担保</Option>
                    <Option value="no-guarantee">无需担保</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="cancellationRule"
                  label="取消规则"
                  rules={[{ required: true, message: '请选择取消规则' }]}
                  initialValue="day-before"
                >
                  <Select placeholder="请选择取消规则" defaultValue="day-before">
                    <Option value="day-before">当天18:00免费取消</Option>
                    <Option value="24h">提前24小时免费取消</Option>
                    <Option value="non-refundable">不可取消</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            
            {/* 促销优惠 */}
            <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>促销优惠</h3>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>可用优惠券:</div>
              <Space wrap>
                <Radio.Group 
                  name="coupon-rule" 
                  value={couponRule}
                  onChange={(e) => setCouponRule(e.target.value)}
                >
                  <Radio value="unlimited">不限制</Radio>
                  <Radio value="limited">限制部分优惠券</Radio>
                  <Radio value="disabled">不可用优惠券</Radio>
                </Radio.Group>
              </Space>
              {couponRule === 'limited' && (
                <div style={{ marginTop: 8 }}>
                  <Space wrap>
                    <Checkbox value="300-30">满300减30</Checkbox>
                    <Checkbox value="500-80">满500打8折</Checkbox>
                  </Space>
                </div>
              )}
            </div>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>可用促销:</div>
              <Space wrap>
                <Radio.Group 
                  name="promotion-rule" 
                  value={promotionRule}
                  onChange={(e) => setPromotionRule(e.target.value)}
                >
                  <Radio value="unlimited">不限制</Radio>
                  <Radio value="limited">限制部分优惠</Radio>
                  <Radio value="disabled">不可用优惠</Radio>
                </Radio.Group>
              </Space>
              {promotionRule === 'limited' && (
                <div style={{ marginTop: 8 }}>
                  <Space wrap>
                    <Checkbox value="promo-300-30">满300减30</Checkbox>
                    <Checkbox value="promo-500-80">满500打8折</Checkbox>
                  </Space>
                </div>
              )}
            </div>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>可用积分兑换:</div>
              <Space wrap>
                <Checkbox value="allow-points">允许</Checkbox>
              </Space>
            </div>
            
            {/* 保存按钮 */}
            <Form.Item style={{ marginTop: 32 }}>
              <Button type="primary" size="large" onClick={handleSave}>
                保存
              </Button>
            </Form.Item>
          </Form>
        </Card>
      )
    }
  ]

  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', overflow: 'auto' }}>
      <h1 className="page-title">新增编辑价格计划</h1>
      
      <Tabs defaultActiveKey="1" items={tabItems} />
    </div>
  )
}

export default AddRatePlan
import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Radio, Button, Tabs, Card, Divider, Collapse, Checkbox, Table, message } from 'antd'
import { SaveOutlined, InfoCircleOutlined, PlusOutlined, EditOutlined } from '@ant-design/icons'

const { TabPane } = Tabs
const { Panel } = Collapse
const { Option } = Select

const FliggySetting = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [archiveRelation, setArchiveRelation] = useState('payment')
  const [selectedPriceCodes, setSelectedPriceCodes] = useState([])
  const [selectedRoomTypes, setSelectedRoomTypes] = useState([])
  const [publishing, setPublishing] = useState(false)
  const [priceCodeSearch, setPriceCodeSearch] = useState('')
  const [rateCodes, setRateCodes] = useState([
    { id: 1, code: 'RACK', name: '牌价', roomTypes: ['标准大床房', '标准双床房', '豪华大床房'] },
    { id: 2, code: 'WEEKEND', name: '周末价', roomTypes: ['标准大床房', '标准双床房'] },
    { id: 3, code: 'CORP', name: '企业价', roomTypes: ['豪华大床房', '豪华套房'] },
    { id: 4, code: 'PKG', name: '套餐价', roomTypes: ['标准大床房'] },
    { id: 5, code: 'MEMBER', name: '会员价', roomTypes: ['标准大床房', '标准双床房', '豪华套房'] },
    { id: 6, code: 'VIP', name: 'VIP价', roomTypes: ['豪华套房'] },
    { id: 7, code: 'BIZ', name: '商务价', roomTypes: ['标准大床房', '标准双床房'] },
    { id: 8, code: 'GROUP', name: '团队价', roomTypes: ['标准大床房', '标准双床房', '豪华大床房', '豪华套房'] }
  ])
  const [publishedList, setPublishedList] = useState([
    { key: '1', channel: '飞猪', priceCode: 'RACK', roomTypes: '标准大床房、标准双床房、豪华大床房', status: '已发布', updateTime: '2025-01-15 10:30' },
  ])

  const filteredRateCodes = rateCodes.filter(rc => 
    rc.name.includes(priceCodeSearch) || rc.code.toLowerCase().includes(priceCodeSearch.toLowerCase())
  )

  const handlePriceCodeSelect = (codes) => {
    setSelectedPriceCodes(codes)
    if (codes.length === 0) {
      setSelectedRoomTypes([])
    } else {
      const allRoomTypes = []
      rateCodes.filter(rc => codes.includes(rc.code)).forEach(rc => {
        rc.roomTypes.forEach(rt => {
          if (!allRoomTypes.includes(rt)) {
            allRoomTypes.push(rt)
          }
        })
      })
      setSelectedRoomTypes(allRoomTypes)
    }
  }

  const handleRoomTypeSelect = (types) => {
    setSelectedRoomTypes(types)
  }

  const getAvailableRoomTypes = () => {
    const allRoomTypes = []
    rateCodes.filter(rc => selectedPriceCodes.includes(rc.code)).forEach(rc => {
      rc.roomTypes.forEach(rt => {
        if (!allRoomTypes.includes(rt)) {
          allRoomTypes.push(rt)
        }
      })
    })
    return allRoomTypes
  }

  const handlePublish = () => {
    if (selectedPriceCodes.length === 0) {
      message.warning('请选择至少一个房价码')
      return
    }
    if (selectedRoomTypes.length === 0) {
      message.warning('请选择至少一个房型')
      return
    }
    setPublishing(true)
    setTimeout(() => {
      selectedPriceCodes.forEach((code, index) => {
        const rateCode = rateCodes.find(rc => rc.code === code)
        setPublishedList(prev => [...prev, {
          key: `${Date.now()}-${index}`,
          channel: '飞猪',
          priceCode: code,
          roomTypes: selectedRoomTypes.join('、'),
          status: '已发布',
          updateTime: new Date().toLocaleString('zh-CN')
        }])
      })
      message.success(`成功发布 ${selectedPriceCodes.length} 个房价码到 ${selectedRoomTypes.length} 个房型`)
      setPublishing(false)
    }, 1000)
  }

  // 处理表单提交
  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      console.log('提交飞猪渠道设置:', values)
      // 这里可以添加API调用逻辑
      alert('保存成功')
    } catch (error) {
      console.error('保存失败:', error)
      alert('保存失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <div>
          <h1 className="page-title" style={{ marginBottom: 8 }}>
            飞猪 <a href="#" style={{ fontSize: 14, color: '#1890ff', marginLeft: 8 }}>了解更多</a>
          </h1>
          <div style={{ fontSize: 14, color: '#8c8c8c' }}>
            渠道管理 &gt; 渠道详情
          </div>
        </div>
        <Button 
          type="primary" 
          icon={<SaveOutlined />} 
          onClick={() => form.submit()}
          loading={loading}
        >
          保存
        </Button>
      </div>

      <Tabs defaultActiveKey="channel" size="large" style={{ marginBottom: 24 }}>
        <TabPane tab="渠道设置" key="channel">
          <Form
            form={form}
            layout="vertical"
            onFinish={handleSubmit}
            initialValues={{
              currency: 'CNY',
              hotelType: '旗舰店',
              appkey: '33763207',
              appsecret: 'cbedd64d61d2f020f8cc4e75145',
              sessionkey: '61012226cb8c37650003bd3179461a82e3e5754a6b74e22090158e',
              vendor: 'taobao',
              priceType: 'multiRoom',
              fliggyPriceType: 'AAT',
              commissionType: 'percentage'
            }}
          >
            <Card title="基础" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Form.Item
                  name="currency"
                  label="币种*"
                >
                  <Select style={{ width: '100%' }}>
                    <Option value="CNY">CNY</Option>
                    <Option value="USD">USD</Option>
                    <Option value="EUR">EUR</Option>
                  </Select>
                </Form.Item>
                <Form.Item
                  name="hotelType"
                  label="飞猪酒店类型*"
                >
                  <Select style={{ width: '100%' }}>
                    <Option value="旗舰店">旗舰店</Option>
                    <Option value="普通店">普通店</Option>
                  </Select>
                </Form.Item>
                <Form.Item
                  name="appkey"
                  label="Appkey*"
                >
                  <Input />
                </Form.Item>
                <Form.Item
                  name="appsecret"
                  label="Appsecret*"
                >
                  <Input />
                </Form.Item>
                <Form.Item
                  name="sessionkey"
                  label="Sessionkey*"
                >
                  <Input />
                </Form.Item>
                <Form.Item
                  name="vendor"
                  label="Vendor*"
                >
                  <Input />
                </Form.Item>
              </div>
              <Form.Item
                  name="priceType"
                  label="价格类型*"
                >
                <Radio.Group>
                  <Radio value="multiRoom">多人同价</Radio>
                  <Radio value="multiPerson">多人价</Radio>
                </Radio.Group>
              </Form.Item>
            </Card>

            <Card title="价格规则" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Form.Item
                  name="fliggyPriceType"
                  label="发送到飞猪的价格类型*"
                >
                  <Radio.Group>
                    <Radio value="ABT">不含税价(ABT)</Radio>
                    <Radio value="AAT">含税价(AAT)</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item
                  name="prepaidCommission"
                  label="预付佣金"
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Select style={{ width: 120 }} defaultValue="percentage">
                      <Option value="percentage">百分比</Option>
                      <Option value="fixed">固定金额</Option>
                    </Select>
                    <Input style={{ flex: 1 }} placeholder="请输入..." />
                  </div>
                </Form.Item>
                <Form.Item
                  name="payAtHotelCommission"
                  label="现付佣金"
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Select style={{ width: 120 }} defaultValue="percentage">
                      <Option value="percentage">百分比</Option>
                      <Option value="fixed">固定金额</Option>
                    </Select>
                    <Input style={{ flex: 1 }} placeholder="请输入..." />
                  </div>
                </Form.Item>
              </div>
            </Card>



            <Card title="档案" style={{ marginBottom: 24 }}>
              <div style={{ padding: 16 }}>
                <Form.Item
                  name="archiveRelation"
                  label="关联档案"
                >
                  <Radio.Group value={archiveRelation} onChange={(e) => setArchiveRelation(e.target.value)}>
                    <Radio value="payment">按支付方式</Radio>
                    <Radio value="price">按房价</Radio>
                  </Radio.Group>
                </Form.Item>

                {archiveRelation === 'payment' && (
                  <div style={{ marginTop: 16 }}>
                    <div style={{ display: 'flex', gap: 24 }}>
                      <div style={{ flex: 1 }}>
                        <Form.Item
                          name="prepaidArchive"
                          label="预付档案"
                        >
                          <Select placeholder="请选择预付档案" style={{ width: '100%' }}>
                            <Option value="prepaid1">预付档案1</Option>
                            <Option value="prepaid2">预付档案2</Option>
                            <Option value="prepaid3">预付档案3</Option>
                          </Select>
                        </Form.Item>
                      </div>
                      <div style={{ flex: 1 }}>
                        <Form.Item
                          name="payAtHotelArchive"
                          label="现付档案"
                        >
                          <Select placeholder="请选择现付档案" style={{ width: '100%' }}>
                            <Option value="pay1">现付档案1</Option>
                            <Option value="pay2">现付档案2</Option>
                            <Option value="pay3">现付档案3</Option>
                          </Select>
                        </Form.Item>
                      </div>
                    </div>
                  </div>
                )}

                {archiveRelation === 'price' && (
                  <div style={{ marginTop: 16 }}>
                    <Table
                      dataSource={rateCodes}
                      pagination={false}
                      columns={[
                        {
                          title: '房价码',
                          dataIndex: 'name',
                          key: 'rateCode',
                          render: (text, record) => `${text}（${record.code}）`
                        },
                        {
                          title: '关联档案',
                          dataIndex: 'archive',
                          key: 'archive',
                          render: () => (
                            <Select placeholder="请选择关联档案" style={{ width: '100%' }}>
                              <Option value="archive1">档案1</Option>
                              <Option value="archive2">档案2</Option>
                              <Option value="archive3">档案3</Option>
                            </Select>
                          )
                        }
                      ]}
                      rowKey="id"
                    />
                  </div>
                )}
              </div>
            </Card>
          </Form>
        </TabPane>
        <TabPane tab="渠道发布" key="room-price">
          <Card>
            <div style={{ marginBottom: 16 }}>
              <Input 
                placeholder="搜索房价码..." 
                value={priceCodeSearch}
                onChange={(e) => setPriceCodeSearch(e.target.value)}
                allowClear
                style={{ width: 300 }}
              />
            </div>
            <div style={{ border: '1px solid #e8e8e8', borderRadius: 4 }}>
              {filteredRateCodes.map((rc, index) => (
                <div key={rc.code} style={{ 
                  borderBottom: index < filteredRateCodes.length - 1 ? '1px solid #e8e8e8' : 'none',
                  padding: '12px 16px'
                }}>
                  <Checkbox 
                    checked={selectedPriceCodes.includes(rc.code)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedPriceCodes([...selectedPriceCodes, rc.code])
                      } else {
                        setSelectedPriceCodes(selectedPriceCodes.filter(c => c !== rc.code))
                      }
                    }}
                  >
                    <span style={{ fontWeight: 500 }}>{rc.name} ({rc.code})</span>
                  </Checkbox>
                  <div style={{ marginTop: 8, marginLeft: 24 }}>
                    <span style={{ color: '#666', fontSize: 12, marginRight: 8 }}>关联房型:</span>
                    {rc.roomTypes.map(rt => (
                      <Checkbox 
                        key={rt}
                        checked={selectedRoomTypes.includes(rt) && selectedPriceCodes.includes(rc.code)}
                        disabled={!selectedPriceCodes.includes(rc.code)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            if (!selectedRoomTypes.includes(rt)) {
                              setSelectedRoomTypes([...selectedRoomTypes, rt])
                            }
                          } else {
                            setSelectedRoomTypes(selectedRoomTypes.filter(t => t !== rt))
                          }
                        }}
                        style={{ marginRight: 16 }}
                      >
                        {rt}
                      </Checkbox>
                    ))}
                  </div>
                </div>
              ))}
            </div>
            <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ color: '#666' }}>
                已选择 {selectedPriceCodes.length} 个房价码，{selectedRoomTypes.length} 个房型
              </span>
              <Button type="primary" icon={<SaveOutlined />} onClick={handlePublish} loading={publishing}>
                批量发布
              </Button>
            </div>
          </Card>
        </TabPane>
      </Tabs>
    </div>
  )
}

export default FliggySetting
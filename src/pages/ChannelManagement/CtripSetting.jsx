import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Form, Input, InputNumber, Select, Radio, Button, Tabs, Card, Checkbox, Table, message, Row, Col, Space, Tag } from 'antd'
import { SaveOutlined, SearchOutlined, PlusOutlined, EditOutlined, CalendarOutlined } from '@ant-design/icons'

// 添加同步状态动画样式
const style = document.createElement('style')
style.textContent = `
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`
document.head.appendChild(style)

const { TabPane } = Tabs
const { Option } = Select

const CtripSetting = () => {
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [archiveRelation, setArchiveRelation] = useState('payment')
  const [selectedPriceCodes, setSelectedPriceCodes] = useState(['RACK', 'WEEKEND'])
  const [selectedRoomTypes, setSelectedRoomTypes] = useState(['标准大床房', '标准双床房'])
  const [publishing, setPublishing] = useState(false)
  const [priceCodeSearch, setPriceCodeSearch] = useState('')
  const [promotions, setPromotions] = useState([
    { id: 1, rnum: 1, promotionType: '限时抢购', eventName: '10月15日至10月19日 限时促销', discountModel: '折扣', discountValue: '8折', status: '有效' },
    { id: 2, rnum: 2, promotionType: '今夜甩卖', eventName: '每日夜间9点甩卖', discountModel: '折扣', discountValue: '8折', status: '有效' },
    { id: 3, rnum: 3, promotionType: '天天特价', eventName: '天天特价', discountModel: '立减', discountValue: '-230', status: '有效' },
    { id: 4, rnum: 4, promotionType: '提前预定', eventName: '提前3天预订打8折', discountModel: '折扣', discountValue: '8折', status: '有效' },
    { id: 5, rnum: 5, promotionType: '午夜特惠', eventName: '', discountModel: '', discountValue: '', status: '' },
    { id: 6, rnum: 6, promotionType: '钟点房促销', eventName: '', discountModel: '', discountValue: '', status: '' }
  ])

  // 模拟携程活动数据
  const mockCtripActivities = [
    {
      id: 1,
      activityName: '[携程特惠] 约会好去处，情侣度假65折',
      description: '[促销标签] "十亿豪补"\n[活动说明] 与爱妻、闺蜜红色加倍计算，与促销活动不叠加，如同一个房型参与多个促销活动，以力度最大的为准。\n[温馨提示] 此活动长期有效，到期前一周将与您联系，如一周内未答复将自动延续',
      registrationDate: '2019-10-04至2019-12-31',
      stayDate: '2019-10-04至2020-12-31',
      details: '入住日期：2020-01-08至2020-03-31\n可预订日期：现订现付\n价格：468元，原价为498元'
    },
    {
      id: 2,
      activityName: '[时令全覆盖] 万物复苏，春暖花开',
      description: '[促销标签] "十亿豪补"\n[活动说明] 春暖花开，秋收农富，时令覆盖伴随每个季节',
      registrationDate: '2020-05-15至2022-12-31',
      stayDate: '2020-07-15至2022-12-31',
      details: '2023-01-28至2023-03-21\n2023-04-08至2023-04-13\n2023-04-15至2023-04-27\n2023-04-29至2023-05-07\n2023-05-14至2023-05-20\n2023-05-28至2023-06-02\n2023-06-08至2023-06-09\n2023-06-22至2023-06-23'
    },
    {
      id: 3,
      activityName: '[错峰黄金期] 暑假日错峰专属通道',
      description: '[促销标签] "百日壕赚"\n[流量激励] 首页活动专属展场+榜单专属展示+促销榜单',
      registrationDate: '2020-02-28至2022-12-31',
      stayDate: '2020-01-06至2022-12-31',
      details: '2023-05-04至2023-05-08\n2023-05-14至2023-05-18\n2023-05-22至2023-05-26\n2023-05-29至2023-06-02\n2023-06-05至2023-06-09\n2023-06-12至2023-06-16\n2023-06-19至2023-06-23\n2023-06-26至2023-06-30\n2023-07-03至2023-07-07\n2023-07-10至2023-07-14\n2023-07-17至2023-07-21\n2023-07-24至2023-07-28\n2023-07-31至2023-08-04\n2023-08-07至2023-08-11\n2023-08-14至2023-08-18\n2023-08-21至2023-08-25\n2023-08-28至2023-09-01\n2023-09-04至2023-09-08\n2023-09-11至2023-09-15\n2023-09-18至2023-09-22\n2023-09-25至2023-09-29\n2023-10-09至2023-10-13\n2023-10-16至2023-10-20\n2023-10-23至2023-10-27\n2023-10-30至2023-11-03\n2023-11-06至2023-11-10\n2023-11-13至2023-11-17\n2023-11-20至2023-11-24\n2023-11-27至2023-12-01\n2023-12-04至2023-12-08\n2023-12-11至2023-12-15\n2023-12-18至2023-12-22\n2023-12-25至2023-12-29'
    },
    {
      id: 4,
      activityName: '[特惠一口价] 约价狂欢，超值498元起',
      description: '[促销标签] "十亿豪补"\n[活动说明] 酒店活动页面价为468元起，加8元享1407元，参与酒店需保证原价高于468元，加价方式详见活动规则。\n[温馨提示] 如同一个房型参与多个促销活动，以力度最大的为准，促销活动之间不叠加。',
      registrationDate: '2020-03-23至2022-12-31',
      stayDate: '2020-04-15至2022-12-31',
      details: '入住日期：2020-06-10至2023-03-31\n价格：468元，原价为411元'
    }
  ]

  const [activities, setActivities] = useState(mockCtripActivities)
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
  // 生成随机同步状态
  const getRandomSyncStatus = () => {
    const statuses = ['syncing', 'success', 'failed']
    return statuses[Math.floor(Math.random() * statuses.length)]
  }

  const [publishedList, setPublishedList] = useState([
    { key: '1', channel: '携程', priceCode: 'RACK', roomTypes: '标准大床房、标准双床房、豪华大床房', status: '已发布', syncStatus: getRandomSyncStatus(), updateTime: '2025-01-15 10:30' },
  ])
  const [localRoomTypes] = useState([
    { id: 1, code: '1KGS', name: '标准大床房' },
    { id: 2, code: '2TGS', name: '标准双床房' },
    { id: 3, code: '1KGP', name: '高级大床房' },
    { id: 4, code: '1KGH', name: '探索大床房' },
    { id: 5, code: '2TGH', name: '探索双床房' },
    { id: 6, code: '1KGS', name: '豪华大床房' },
    { id: 7, code: '1KSU', name: '豪华套房' }
  ])
  const [channelRoomTypes] = useState([
    { id: 1, code: 'CTRIP_1', name: '标准大床房', maxOccupancy: 2 },
    { id: 2, code: 'CTRIP_2', name: '标准双床房', maxOccupancy: 2 },
    { id: 3, code: 'CTRIP_3', name: '高级大床房', maxOccupancy: 2 },
    { id: 4, code: 'CTRIP_4', name: '豪华大床房', maxOccupancy: 2 },
    { id: 5, code: 'CTRIP_5', name: '豪华套房', maxOccupancy: 4 },
    { id: 6, code: 'CTRIP_6', name: '商务大床房', maxOccupancy: 2 }
  ])
  const [currentHotelType, setCurrentHotelType] = useState('prepaid')

  const [prepaidRoomTypeMappings, setPrepaidRoomTypeMappings] = useState([
    { id: 1, localRoomType: '1KGS', channelRoomType: 'CTRIP_1' },
    { id: 2, localRoomType: '2TGS', channelRoomType: 'CTRIP_2' },
    { id: 3, localRoomType: '1KGP', channelRoomType: 'CTRIP_3' },
    { id: 4, localRoomType: '', channelRoomType: 'CTRIP_4' },
    { id: 5, localRoomType: '', channelRoomType: 'CTRIP_5' },
    { id: 6, localRoomType: '', channelRoomType: 'CTRIP_6' }
  ])
  const [postpaidRoomTypeMappings, setPostpaidRoomTypeMappings] = useState([
    { id: 1, localRoomType: '', channelRoomType: 'CTRIP_1' },
    { id: 2, localRoomType: '', channelRoomType: 'CTRIP_2' },
    { id: 3, localRoomType: '', channelRoomType: 'CTRIP_3' },
    { id: 4, localRoomType: '', channelRoomType: 'CTRIP_4' },
    { id: 5, localRoomType: '', channelRoomType: 'CTRIP_5' },
    { id: 6, localRoomType: '', channelRoomType: 'CTRIP_6' }
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

  // 初始化默认选中的房型
  useEffect(() => {
    if (selectedPriceCodes.length > 0) {
      const allRoomTypes = []
      rateCodes.filter(rc => selectedPriceCodes.includes(rc.code)).forEach(rc => {
        rc.roomTypes.forEach(rt => {
          if (!allRoomTypes.includes(rt)) {
            allRoomTypes.push(rt)
          }
        })
      })
      setSelectedRoomTypes(allRoomTypes)
    }
  }, [])

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
          channel: '携程',
          priceCode: code,
          roomTypes: selectedRoomTypes.join('、'),
          status: '已发布',
          syncStatus: getRandomSyncStatus(),
          updateTime: new Date().toLocaleString('zh-CN')
        }])
      })
      message.success(`成功发布 ${selectedPriceCodes.length} 个房价码到 ${selectedRoomTypes.length} 个房型`)
      setPublishing(false)
    }, 1000)
  }

  const handleRoomTypeMappingChange = (id, field, value) => {
    const setMappings = currentHotelType === 'prepaid' ? setPrepaidRoomTypeMappings : setPostpaidRoomTypeMappings
    setMappings(prev => {
      const newMappings = [...prev]
      const index = newMappings.findIndex(m => m.id === id)
      if (index !== -1) {
        newMappings[index] = { ...newMappings[index], [field]: value }
      }
      return newMappings
    })
  }

  const handleSaveRoomTypeMappings = () => {
    message.success('房型匹配保存成功')
  }

  // 促销管理处理函数
  const handleOpenPromotionModal = (record = null) => {
    if (record) {
      navigate(`/channel-management/ctrip-setting/promotion/edit/${record.id}`)
    } else {
      navigate('/channel-management/ctrip-setting/promotion/add')
    }
  }



  const handlePromotionStatusChange = (record) => {
    const newStatus = record.status === '有效' ? '无效' : '有效'
    setPromotions(promotions.map(p => 
      p.id === record.id ? { ...p, status: newStatus } : p
    ))
    message.success(`已将活动 ${record.eventName || record.promotionType} 设置为${newStatus}`)
  }

  const handleActivityRegistration = (record) => {
    navigate(`/group-promotion-management/ctrip-activity-registration?id=${record.id}`)
  }

  // 处理表单提交
  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      console.log('提交携程渠道设置:', values)
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
      <div style={{ marginBottom: 24 }}>
        <div>
          <h1 className="page-title" style={{ marginBottom: 8 }}>
            携程 <a href="#" style={{ fontSize: 14, color: '#1890ff', marginLeft: 8 }}>了解更多</a>
          </h1>
          <div style={{ fontSize: 14, color: '#8c8c8c' }}>
            渠道管理 &gt; 渠道详情
          </div>
        </div>
      </div>

      <Tabs defaultActiveKey="1">
        <TabPane tab="基础设置" key="1">
          <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ fontWeight: 500, fontSize: 16 }}>基础设置</div>
            <Button 
              type="primary" 
              icon={<SaveOutlined />} 
              onClick={() => form.submit()}
              loading={loading}
            >
              保存设置
            </Button>
          </div>
          <Form
            form={form}
            layout="vertical"
            onFinish={handleSubmit}
            initialValues={{
              channelName: '携程',
              channelCode: 'CTRIP',
              prepaidHotelId: '',
              postpaidHotelId: '',
              multiplePersonPrice: 'singleDoubleSame',
              priceRounding: 'keep'
            }}
          >
            <Card title="基础" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Form.Item
                  label="渠道名称"
                  name="channelName"
                  rules={[{ required: true, message: '请输入渠道名称' }]}
                >
                  <Input placeholder="请输入渠道名称" disabled />
                </Form.Item>
                <Form.Item
                  label="渠道编码"
                  name="channelCode"
                  rules={[
                    { required: true, message: '请输入渠道编码' },
                    { pattern: /^[A-Za-z0-9_]+$/, message: '渠道编码只能包含英文字母、数字和下划线' }
                  ]}
                >
                  <Input placeholder="请输入渠道编码" disabled />
                </Form.Item>
              </div>
            </Card>

            <Card title="接口配置" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Form.Item
                  label="携程预付酒店ID"
                  name="prepaidHotelId"
                  rules={[{ required: true, message: '请输入携程预付酒店ID' }]}
                >
                  <Input placeholder="请输入携程预付酒店ID" />
                </Form.Item>
                <Form.Item
                  label="携程现付酒店ID"
                  name="postpaidHotelId"
                  rules={[{ required: true, message: '请输入携程现付酒店ID' }]}
                >
                  <Input placeholder="请输入携程现付酒店ID" />
                </Form.Item>
              </div>
            </Card>

            <Card title="价格设置" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Form.Item
                  label="是否支持多个人价"
                  name="multiplePersonPrice"
                  rules={[{ required: true, message: '请选择是否支持多个人价' }]}
                >
                  <Radio.Group>
                    <Radio value="singleDoubleSame">单双同价</Radio>
                    <Radio value="multiple">多人价</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item
                  label="同步价格取整方式"
                  name="priceRounding"
                  rules={[{ required: true, message: '请选择同步价格取整方式' }]}
                >
                  <Radio.Group>
                    <Radio value="keep">保持原值</Radio>
                    <Radio value="ceil">向上取整</Radio>
                    <Radio value="floor">向下取整</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item
                  label="创建订单是否校验价格"
                  name="validateOrderPrice"
                >
                  <Radio.Group defaultValue={true}>
                    <Radio value={true}>是</Radio>
                    <Radio value={false}>否</Radio>
                  </Radio.Group>
                </Form.Item>
              </div>
            </Card>

            <Card title="价格规则" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
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
        <TabPane tab="房型匹配" key="room-mapping">
          <Card>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div style={{ fontWeight: 500, fontSize: 16 }}>设置CRS与携程房型关联关系</div>
              <Button type="primary" icon={<SaveOutlined />} onClick={handleSaveRoomTypeMappings}>
                保存关联
              </Button>
            </div>
            <Tabs 
              activeKey={currentHotelType} 
              onChange={setCurrentHotelType}
              style={{ marginBottom: 16 }}
            >
              <TabPane tab="预付酒店" key="prepaid" />
              <TabPane tab="现付酒店" key="postpaid" />
            </Tabs>
            <div style={{ border: '1px solid #e8e8e8', borderRadius: 4 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16, padding: '12px 16px', backgroundColor: '#fafafa', borderBottom: '1px solid #e8e8e8', fontWeight: 500 }}>
                <div>携程房型</div>
                <div>携程最大入住人数</div>
                <div>CRS房型</div>
              </div>
              {(currentHotelType === 'prepaid' ? prepaidRoomTypeMappings : postpaidRoomTypeMappings).map((mapping, index) => {
                const channelRoomType = channelRoomTypes.find(rt => rt.code === mapping.channelRoomType)
                
                return (
                  <div key={mapping.id} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16, padding: '12px 16px', borderBottom: index < (currentHotelType === 'prepaid' ? prepaidRoomTypeMappings : postpaidRoomTypeMappings).length - 1 ? '1px solid #e8e8e8' : 'none', alignItems: 'center' }}>
                    <div style={{ fontWeight: 500 }}>{channelRoomType?.name || mapping.channelRoomType}</div>
                    <div style={{ color: '#666' }}>{channelRoomType?.maxOccupancy || '-'}</div>
                    <Select
                      placeholder="请选择CRS房型"
                      value={mapping.localRoomType}
                      onChange={(value) => handleRoomTypeMappingChange(mapping.id, 'localRoomType', value)}
                      style={{ width: '100%' }}
                    >
                      {localRoomTypes.map(rt => (
                        <Option key={rt.code} value={rt.code}>{rt.name}({rt.code})</Option>
                      ))}
                    </Select>
                  </div>
                )
              })}
            </div>
          </Card>
        </TabPane>
        <TabPane tab="渠道发布" key="room-price">
          <Card>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Input 
                placeholder="搜索房价码..." 
                value={priceCodeSearch}
                onChange={(e) => setPriceCodeSearch(e.target.value)}
                allowClear
                style={{ width: 300 }}
              />
              <div style={{ display: 'flex', gap: 8 }}>
                <Button onClick={() => message.info('同步产品功能开发中')}>
                  同步产品
                </Button>
                <Button onClick={() => message.info('同步ARI功能开发中')}>
                  同步ARI
                </Button>
                <Button type="primary" icon={<SaveOutlined />} onClick={handlePublish} loading={publishing}>
                  批量发布
                </Button>
              </div>
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
                    {rc.roomTypes.map(rt => {
                      // 生成随机同步状态
                      const getRandomStatus = () => {
                        const statuses = ['syncing', 'success', 'failed']
                        return statuses[Math.floor(Math.random() * statuses.length)]
                      }
                      
                      // 渲染同步状态图标
                      const renderSyncStatus = () => {
                        // 当房价码未被选中时，房型禁用，不显示状态
                        if (!selectedPriceCodes.includes(rc.code)) {
                          return null
                        }
                        
                        const status = getRandomStatus()
                        switch (status) {
                          case 'syncing':
                            return <span style={{ display: 'inline-block', width: 12, height: 12, border: '1px solid #1890ff', borderTop: '1px solid transparent', borderRadius: '50%', animation: 'spin 1s linear infinite', marginLeft: 4, verticalAlign: 'middle' }}></span>
                          case 'success':
                            return <span style={{ color: '#52c41a', fontSize: 12, marginLeft: 4, verticalAlign: 'middle' }}>✅</span>
                          case 'failed':
                            return <span style={{ color: '#ff4d4f', fontSize: 12, marginLeft: 4, verticalAlign: 'middle' }}>×</span>
                          default:
                            return null
                        }
                      }
                      
                      return (
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
                          <span style={{ display: 'inline-flex', alignItems: 'center' }}>
                            {rt}
                            {renderSyncStatus()}
                          </span>
                        </Checkbox>
                      )
                    })}
                  </div>
                </div>
              ))}
            </div>
            <div style={{ marginTop: 16, display: 'flex', justifyContent: 'flex-start', alignItems: 'center' }}>
              <span style={{ color: '#666' }}>
                已选择 {selectedPriceCodes.length} 个房价码，{selectedRoomTypes.length} 个房型
              </span>
            </div>

            <div style={{ marginTop: 24 }}>
              <h3 style={{ marginBottom: 16 }}>发布记录</h3>
              <Table
                dataSource={publishedList}
                pagination={false}
                columns={[
                  {
                    title: '渠道',
                    dataIndex: 'channel',
                    key: 'channel'
                  },
                  {
                    title: '房价码',
                    dataIndex: 'priceCode',
                    key: 'priceCode'
                  },
                  {
                    title: '房型',
                    dataIndex: 'roomTypes',
                    key: 'roomTypes'
                  },
                  {
                    title: '状态',
                    dataIndex: 'status',
                    key: 'status'
                  },
                  {
                    title: '同步状态',
                    dataIndex: 'syncStatus',
                    key: 'syncStatus',
                    render: (status) => {
                      switch (status) {
                        case 'syncing':
                          return <span style={{ display: 'inline-block', width: 12, height: 12, border: '1px solid #1890ff', borderTop: '1px solid transparent', borderRadius: '50%', animation: 'spin 1s linear infinite', verticalAlign: 'middle' }}></span>
                        case 'success':
                          return <span style={{ color: '#52c41a', fontSize: 12, verticalAlign: 'middle' }}>✅</span>
                        case 'failed':
                          return <span style={{ color: '#ff4d4f', fontSize: 12, verticalAlign: 'middle' }}>×</span>
                        default:
                          return null
                      }
                    }
                  },
                  {
                    title: '更新时间',
                    dataIndex: 'updateTime',
                    key: 'updateTime'
                  },
                  {
                    title: '操作',
                    key: 'action',
                    render: () => (
                      <Button size="small" onClick={() => message.info('查看日志功能开发中')}>
                        查看日志
                      </Button>
                    )
                  }
                ]}
                rowKey="key"
              />
            </div>
          </Card>
        </TabPane>
        <TabPane tab="促销管理" key="promotion">
          <Card>
            <Row gutter={[16, 16]} align="middle" style={{ marginBottom: 16 }}>
              <Col xs={24} sm={12} md={8} lg={6}>
                <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'nowrap' }}>
                  <span style={{ marginRight: 8, whiteSpace: 'nowrap' }}>促销类型:</span>
                  <Select
                    style={{ flex: 1, minWidth: 100 }}
                    placeholder="请选择"
                  >
                    {[
                      { value: '限时抢购', label: '限时抢购' },
                      { value: '今夜甩卖', label: '今夜甩卖' },
                      { value: '天天特价', label: '天天特价' },
                      { value: '提前预定', label: '提前预定' },
                      { value: '午夜特惠', label: '午夜特惠' },
                      { value: '钟点房促销', label: '钟点房促销' }
                    ].map(type => (
                      <Option key={type.value} value={type.value}>{type.label}</Option>
                    ))}
                  </Select>
                </div>
              </Col>
              <Col xs={24} sm={12} md={8} lg={6}>
                <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'nowrap' }}>
                  <span style={{ marginRight: 8, whiteSpace: 'nowrap' }}>活动名称:</span>
                  <Input
                    style={{ flex: 1, minWidth: 120 }}
                    placeholder="请输入活动名称"
                  />
                </div>
              </Col>
              <Col xs={24} sm={24} md={8} lg={12} style={{ textAlign: 'left' }}>
                <Space>
                  <Button type="default" icon={<SearchOutlined />}>搜索</Button>
                  <Button type="primary" icon={<PlusOutlined />} onClick={() => handleOpenPromotionModal()}>新增促销</Button>
                </Space>
              </Col>
            </Row>

            <Table
              columns={[
                {
                  title: '序号',
                  dataIndex: 'rnum',
                  key: 'rnum',
                  width: 60
                },
                {
                  title: '促销类型',
                  dataIndex: 'promotionType',
                  key: 'promotionType',
                  width: 150
                },
                {
                  title: '活动名称',
                  dataIndex: 'eventName',
                  key: 'eventName',
                  width: 250
                },
                {
                  title: '折扣模式',
                  dataIndex: 'discountModel',
                  key: 'discountModel',
                  width: 120
                },
                {
                  title: '优惠金额/折扣',
                  dataIndex: 'discountValue',
                  key: 'discountValue',
                  width: 150
                },
                {
                  title: '状态',
                  dataIndex: 'status',
                  key: 'status',
                  width: 80,
                  render: (status) => (
                    <Tag color={status === '有效' ? 'green' : status === '无效' ? 'red' : 'gray'}>
                      {status || ''}
                    </Tag>
                  )
                },
                {
                  title: '操作',
                  key: 'operate',
                  width: 250,
                  render: (_, record) => (
                    <Space size="middle">
                      <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleOpenPromotionModal(record)}>
                        编辑
                      </Button>
                      <Button type="link" size="small" onClick={() => navigate(`/channel-management/ctrip-setting/promotion/registration/${record.id}`)}>
                        促销报名
                      </Button>
                      {record.status === '有效' && (
                        <Button type="link" size="small" danger onClick={() => handlePromotionStatusChange(record)}>
                          设置为无效
                        </Button>
                      )}
                      {record.status === '无效' && (
                        <Button type="link" size="small" onClick={() => handlePromotionStatusChange(record)}>
                          设置为有效
                        </Button>
                      )}
                    </Space>
                  )
                }
              ]}
              dataSource={promotions}
              rowKey="id"
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
              }}
              scroll={{ x: 1000 }}
            />
          </Card>
        </TabPane>
        <TabPane tab="活动管理" key="activity">
          <Card>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
              <span style={{ marginRight: 8 }}>活动名称:</span>
              <Input style={{ width: 200, marginRight: 8 }} placeholder="请输入活动名称" />
              <Button type="primary">查询</Button>
            </div>

            <Table
              columns={[
                {
                  title: '活动编号',
                  dataIndex: 'serialNumber',
                  key: 'serialNumber',
                  width: 100,
                  render: (_, record) => record.id
                },
                {
                  title: '渠道',
                  dataIndex: 'channel',
                  key: 'channel',
                  width: 100,
                  render: () => '携程'
                },
                {
                  title: '活动名称',
                  dataIndex: 'activityName',
                  key: 'activityName',
                  width: 180
                },
                {
                  title: '活动解释',
                  dataIndex: 'description',
                  key: 'description',
                  width: 300
                },
                {
                  title: '报名日期',
                  dataIndex: 'registrationDate',
                  key: 'registrationDate',
                  width: 150
                },
                {
                  title: '入住日期',
                  dataIndex: 'stayDate',
                  key: 'stayDate',
                  width: 150
                },
                {
                  title: '详情',
                  dataIndex: 'details',
                  key: 'details',
                  width: 200
                },
                {
                  title: '操作',
                  key: 'operate',
                  width: 100,
                  render: (_, record) => (
                    <a href="#" style={{ color: '#1890ff' }} onClick={() => handleActivityRegistration(record)}>活动报名</a>
                  )
                }
              ]}
              dataSource={activities}
              rowKey="id"
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
              }}
              scroll={{ x: 2000 }}
            />
          </Card>
        </TabPane>
      </Tabs>

    </div>
  )
}

export default CtripSetting

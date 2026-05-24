import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Radio, Button, Tabs, Card, Checkbox, Table, message, Spin } from 'antd'
import { SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { tenantChannelApi, channelPublishApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext.jsx'

const { TabPane } = Tabs
const { Option } = Select

const ChannelSetting = () => {
  const navigate = useNavigate()
  const { channelCode } = useParams()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [channelData, setChannelData] = useState(null)
  const [selectedPriceCodes, setSelectedPriceCodes] = useState([])
  const [selectedRoomTypesMap, setSelectedRoomTypesMap] = useState({}) // { rateCode: [roomTypeCode, ...] }
  const [publishing, setPublishing] = useState(false)
  const [priceCodeSearch, setPriceCodeSearch] = useState('')
  const [rateCodes, setRateCodes] = useState([])
  const [publishedRecords, setPublishedRecords] = useState([])

  const { selectedHotel } = useHotelContext()

  const filteredRateCodes = rateCodes.filter(rc =>
    rc.name.includes(priceCodeSearch) || rc.code.toLowerCase().includes(priceCodeSearch.toLowerCase())
  )

  useEffect(() => {
    loadChannelData()
    loadRateCodes()
  }, [channelCode, selectedHotel])

  // 加载已发布记录并回显选中状态
  const loadPublishedRecords = async () => {
    try {
      const records = await channelPublishApi.getPublishedRecords(1, selectedHotel, channelCode)
      if (records && records.length > 0) {
        const codes = new Set()
        const roomMap = {}
        for (const r of records) {
          codes.add(r.rateCode)
          if (!roomMap[r.rateCode]) roomMap[r.rateCode] = []
          if (!roomMap[r.rateCode].includes(r.roomTypeCode)) {
            roomMap[r.rateCode].push(r.roomTypeCode)
          }
        }
        setSelectedPriceCodes([...codes])
        setSelectedRoomTypesMap(roomMap)
      } else {
        // 关键修复：如果新酒店没有发布记录，必须清空上个酒店遗留的状态
        setSelectedPriceCodes([])
        setSelectedRoomTypesMap({})
      }
    } catch (error) {
      console.error('加载已发布记录失败:', error)
    }
  }

  const loadRateCodes = async () => {
    try {
      const data = await channelPublishApi.getRateCodesWithRoomTypesByCode(selectedHotel)
      const formatted = (data || []).map(rc => ({
        code: rc.rateCode,
        name: rc.rateName,
        roomTypes: (rc.roomTypes || []).map(rt => ({ code: rt.code, name: rt.name }))
      }))
      setRateCodes(formatted)
      // 房价码加载完后再加载已发布记录
      loadPublishedRecords()
    } catch (error) {
      console.error('加载房价码数据失败:', error)
    }
  }

  const loadChannelData = async () => {
    setLoading(true)
    try {
      const data = await tenantChannelApi.getChannelByCode(channelCode, 1)
      setChannelData(data)
      const getChannelTypeText = (type) => {
        const types = {
          'Real_Time_API': '实时接口 (API)',
          'Push_API': '推送接口',
          'Manual': '手动录入',
          'Web_Service': 'Web服务'
        }
        return types[type] || type
      }

      form.setFieldsValue({
        channelName: data.channelName,
        channelCode: data.channelCode,
        switchChannel: getChannelTypeText(data.switchChannel),
        accessKey: data.accessKey || '',
        priceRounding: data.priceRounding || 'keep',
        prepaidOrderRequiresPayment: data.prepaidOrderRequiresPayment !== false,
        cancelOrderChecksCancellationRule: data.cancelOrderChecksCancellationRule !== false,
        cancelFailureRequiresManualIntervention: data.cancelFailureRequiresManualIntervention !== false,
        prepaidCommissionType: data.prepaidCommissionType || 'percentage',
        prepaidCommissionValue: data.prepaidCommissionValue || '',
        postpaidCommissionType: data.postpaidCommissionType || 'percentage',
        postpaidCommissionValue: data.postpaidCommissionValue || ''
      })
    } catch (error) {
      console.error('加载渠道数据失败:', error)
      message.error('加载渠道数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSave = async () => {
    try {
      const values = await form.validateFields()
      setSaving(true)
      
      const updateData = {
        accessKey: values.accessKey,
        priceRounding: values.priceRounding,
        prepaidOrderRequiresPayment: values.prepaidOrderRequiresPayment,
        cancelOrderChecksCancellationRule: values.cancelOrderChecksCancellationRule,
        cancelFailureRequiresManualIntervention: values.cancelFailureRequiresManualIntervention,
        prepaidCommissionType: values.prepaidCommissionType,
        prepaidCommissionValue: values.prepaidCommissionValue || null,
        postpaidCommissionType: values.postpaidCommissionType,
        postpaidCommissionValue: values.postpaidCommissionValue || null
      }

      if (channelData?.connected === false) {
        updateData.connected = true
      }

      const updatedChannel = await tenantChannelApi.updateChannelByCode(channelCode, updateData, 1)
      if (updatedChannel) {
        setChannelData(updatedChannel)
      }
      message.success('保存成功')
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败')
    } finally {
      setSaving(false)
    }
  }

  const handlePublish = async () => {
    if (selectedPriceCodes.length === 0) {
      message.warning('请选择至少一个房价码')
      return
    }
    const totalRoomTypes = selectedPriceCodes.reduce((sum, rc) => sum + (selectedRoomTypesMap[rc] || []).length, 0)
    if (totalRoomTypes === 0) {
      message.warning('请选择至少一个房型')
      return
    }
    setPublishing(true)
    try {
      // 只发送选中的房价码及其各自的房型
      const rateCodeRoomTypesMap = {}
      for (const rc of selectedPriceCodes) {
        const rooms = selectedRoomTypesMap[rc] || []
        if (rooms.length > 0) {
          rateCodeRoomTypesMap[rc] = rooms
        }
      }
      await channelPublishApi.batchPublish({
        tenantId: 1,
        hotelCode: selectedHotel,
        channelCode: channelCode,
        rateCodeRoomTypesMap
      })
      message.success(`成功发布 ${selectedPriceCodes.length} 个房价码，共 ${totalRoomTypes} 个房型`)
    } catch (error) {
      console.error('发布失败:', error)
      message.error('发布失败')
    } finally {
      setPublishing(false)
    }
  }

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 400 }}>
        <Spin size="large" />
      </div>
    )
  }

  if (!channelData) {
    return (
      <div style={{ textAlign: 'center', padding: 100 }}>
        <p>未找到渠道数据</p>
        <Button onClick={() => navigate('/channel-management/channel-list')}>返回渠道列表</Button>
      </div>
    )
  }

  return (
    <div className="fade-in">
      <div style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 8 }}>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/channel-management/channel-list')} style={{ marginRight: 16 }} />
          <h1 className="page-title" style={{ marginBottom: 0 }}>{channelData.channelName}</h1>
        </div>
        <div style={{ fontSize: 14, color: '#8c8c8c', marginLeft: 48 }}>渠道管理 &gt; 渠道详情</div>
      </div>

      <Tabs defaultActiveKey="basic" size="large">
        <TabPane tab="基础信息" key="basic">
          <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
            <Button type="primary" icon={<SaveOutlined />} onClick={handleSave} loading={saving}>保存设置</Button>
          </div>
          <Form form={form} layout="vertical">
            <Card title="基础信息" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16 }}>
                <Form.Item name="channelName" label="渠道名称">
                  <Input disabled />
                </Form.Item>
                <Form.Item name="channelCode" label="渠道代码">
                  <Input disabled />
                </Form.Item>
                <Form.Item name="switchChannel" label="通道类型">
                  <Input disabled />
                </Form.Item>
              </div>
            </Card>

            {channelCode !== 'WXMINI' && (
              <Card title="接口配置" style={{ marginBottom: 24 }}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 16 }}>
                  <Form.Item name="accessKey" label="对接 Key">
                    <Input placeholder="请输入对接 Key" />
                  </Form.Item>
                </div>
              </Card>
            )}

            <Card title="价格设置" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Form.Item name="priceRounding" label="同步价格取整方式">
                  <Radio.Group>
                    <Radio value="keep">保持原值</Radio>
                    <Radio value="ceil">向上取整</Radio>
                    <Radio value="floor">向下取整</Radio>
                  </Radio.Group>
                </Form.Item>
              </div>
            </Card>

            <Card title="订单规则" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16 }}>
                <Form.Item name="prepaidOrderRequiresPayment" label="预付订单是否需要支付">
                  <Radio.Group>
                    <Radio value={true}>是</Radio>
                    <Radio value={false}>否</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item name="cancelOrderChecksCancellationRule" label="取消订单是否校验取消规则">
                  <Radio.Group>
                    <Radio value={true}>是</Radio>
                    <Radio value={false}>否</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item name="cancelFailureRequiresManualIntervention" label="取消失败时，是否需要人工介入">
                  <Radio.Group>
                    <Radio value={true}>是</Radio>
                    <Radio value={false}>否</Radio>
                  </Radio.Group>
                </Form.Item>
              </div>
            </Card>

            <Card title="佣金设置" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <Form.Item label="预付佣金">
                  <div style={{ display: 'flex', gap: 8 }}>
                    <Form.Item name="prepaidCommissionType" noStyle>
                      <Select style={{ width: 120 }}>
                        <Option value="percentage">百分比</Option>
                        <Option value="fixed">固定金额</Option>
                      </Select>
                    </Form.Item>
                    <Form.Item name="prepaidCommissionValue" noStyle>
                      <Input style={{ flex: 1 }} placeholder="请输入..." />
                    </Form.Item>
                  </div>
                </Form.Item>
                <Form.Item label="现付佣金">
                  <div style={{ display: 'flex', gap: 8 }}>
                    <Form.Item name="postpaidCommissionType" noStyle>
                      <Select style={{ width: 120 }}>
                        <Option value="percentage">百分比</Option>
                        <Option value="fixed">固定金额</Option>
                      </Select>
                    </Form.Item>
                    <Form.Item name="postpaidCommissionValue" noStyle>
                      <Input style={{ flex: 1 }} placeholder="请输入..." />
                    </Form.Item>
                  </div>
                </Form.Item>
              </div>
            </Card>
          </Form>
        </TabPane>

        <TabPane tab="渠道发布" key="publish">
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
                        key={rt.code}
                        checked={(selectedRoomTypesMap[rc.code] || []).includes(rt.code) && selectedPriceCodes.includes(rc.code)}
                        disabled={!selectedPriceCodes.includes(rc.code)}
                        onChange={(e) => {
                          const current = selectedRoomTypesMap[rc.code] || []
                          if (e.target.checked) {
                            setSelectedRoomTypesMap({ ...selectedRoomTypesMap, [rc.code]: [...current, rt.code] })
                          } else {
                            setSelectedRoomTypesMap({ ...selectedRoomTypesMap, [rc.code]: current.filter(t => t !== rt.code) })
                          }
                        }}
                        style={{ marginRight: 16 }}
                      >
                        {rt.name}({rt.code})
                      </Checkbox>
                    ))}
                  </div>
                </div>
              ))}
            </div>
            <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ color: '#666' }}>
                已选择 {selectedPriceCodes.length} 个房价码，{selectedPriceCodes.reduce((sum, rc) => sum + (selectedRoomTypesMap[rc] || []).length, 0)} 个房型
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

export default ChannelSetting

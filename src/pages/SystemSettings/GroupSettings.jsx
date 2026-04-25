import React, { useState } from 'react'
import { Card, Typography, Divider, Form, Radio, Button, message, Checkbox } from 'antd'

const { Title, Text } = Typography

const GroupSettings = () => {
  const [form] = Form.useForm()
  
  const loadSettings = () => {
    const saved = localStorage.getItem('groupSettings')
    if (saved) {
      return JSON.parse(saved)
    }
    return {
      groupControlMode: 'strong',
      hourlyRoom: 'support',
      otaPromotionMode: 'groupRegistration',
      showCtripPrice: false,
      showMeituanPrice: false
    }
  }
  
  const settings = loadSettings()
  
  const [groupControlMode, setGroupControlMode] = useState(settings.groupControlMode)
  const [hourlyRoom, setHourlyRoom] = useState(settings.hourlyRoom)
  const [otaPromotionMode, setOtaPromotionMode] = useState(settings.otaPromotionMode)
  const [showCtripPrice, setShowCtripPrice] = useState(settings.showCtripPrice)
  const [showMeituanPrice, setShowMeituanPrice] = useState(settings.showMeituanPrice)

  const handleSaveSettings = () => {
    form.validateFields().then(values => {
      const settingsToSave = {
        groupControlMode,
        hourlyRoom,
        otaPromotionMode,
        showCtripPrice,
        showMeituanPrice
      }
      localStorage.setItem('groupSettings', JSON.stringify(settingsToSave))
      
      const event = new StorageEvent('storage', {
        key: 'groupSettings',
        newValue: JSON.stringify(settingsToSave)
      })
      window.dispatchEvent(event)
      
      console.log('保存设置:', settingsToSave)
      message.success('设置保存成功')
    }).catch(error => {
      console.error('表单验证失败:', error)
    })
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        集团设置
      </h1>
      
      <Card>
        <div style={{ marginBottom: 24 }}>
          <Title level={4}>集团设置</Title>
          <Text>管理集团的管控模式和功能配置</Text>
        </div>
        
        <Divider />
        
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            groupControlMode: settings.groupControlMode,
            hourlyRoom: settings.hourlyRoom,
            otaPromotionMode: settings.otaPromotionMode
          }}
        >
          <Form.Item
            label="集团管控模式"
            name="groupControlMode"
            rules={[{ required: true, message: '请选择集团管控模式' }]}
          >
            <Radio.Group onChange={(e) => setGroupControlMode(e.target.value)}>
              <Radio value="strong">强管控</Radio>
              <Radio value="weak">弱管控</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="钟点房"
            name="hourlyRoom"
            rules={[{ required: true, message: '请选择是否支持钟点房' }]}
          >
            <Radio.Group onChange={(e) => setHourlyRoom(e.target.value)}>
              <Radio value="support">支持</Radio>
              <Radio value="notSupport">不支持</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="OTA促销模式"
            name="otaPromotionMode"
            rules={[{ required: true, message: '请选择OTA促销模式' }]}
          >
            <Radio.Group onChange={(e) => setOtaPromotionMode(e.target.value)}>
              <Radio value="groupRegistration">集团报名</Radio>
              <Radio value="groupRuleHotelRegistration">集团设置规则酒店报名</Radio>
              <Radio value="hotelSelfManagement">酒店自行管理</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="预测价格显示配置"
          >
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <Checkbox
                checked={showCtripPrice}
                onChange={(e) => setShowCtripPrice(e.target.checked)}
              >
                显示携程预测价格
              </Checkbox>
              <Checkbox
                checked={showMeituanPrice}
                onChange={(e) => setShowMeituanPrice(e.target.checked)}
              >
                显示美团预测价格
              </Checkbox>
            </div>
          </Form.Item>
        </Form>
        
        <Divider />
        
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Button type="primary" onClick={handleSaveSettings}>
            保存设置
          </Button>
        </div>
      </Card>
    </div>
  )
}

export default GroupSettings
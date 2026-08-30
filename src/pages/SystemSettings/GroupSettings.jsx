import { useCallback, useEffect, useState } from 'react'
import { Card, Divider, Form, Radio, Button, App, Checkbox, Spin, Alert } from 'antd'
import { groupSettingsApi } from '../../utils/api'
import { PageScaffold } from '../../components/ui'

const defaultSettings = {
  groupControlMode: 'strong',
  hourlyRoom: 'support',
  otaPromotionMode: 'groupRegistration',
  showCtripPrice: false,
  showMeituanPrice: false
}

/** 集团设置由后端按租户持久化，浏览器不再充当配置数据库。 */
const GroupSettings = () => {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [loadError, setLoadError] = useState(false)

  const loadSettings = useCallback(async () => {
    setLoading(true)
    setLoadError(false)
    try {
      const response = await groupSettingsApi.get()
      form.setFieldsValue({ ...defaultSettings, ...(response?.data || response || {}) })
    } catch (error) {
      form.setFieldsValue(defaultSettings)
      setLoadError(true)
      message.error('加载集团设置失败，当前仅显示默认值')
    } finally {
      setLoading(false)
    }
  }, [form])

  useEffect(() => { loadSettings() }, [loadSettings])

  const handleSaveSettings = async () => {
    try {
      setSaving(true)
      const values = await form.validateFields()
      await groupSettingsApi.save(values)
      message.success('集团设置已保存到服务器')
      setLoadError(false)
    } catch (error) {
      if (!error?.errorFields) {
        message.error(error?.message || '保存失败，服务器未保存任何变更')
      }
    } finally {
      setSaving(false)
    }
  }

  return (
    <PageScaffold
      className="fade-in"
      eyebrow="GROUP GOVERNANCE"
      title="集团设置"
      description="管理当前集团的管控模式和功能配置；保存后对该集团下所有登录设备生效。"
      actions={(
        <Button type="primary" onClick={handleSaveSettings} loading={saving} disabled={loading || loadError}>
          保存设置
        </Button>
      )}
    >
      <Card className="ui-panel">
        {loadError && <Alert style={{ marginBottom: 16 }} type="warning" showIcon message="真实配置加载失败，禁止在未恢复连接前保存默认值。" />}
        <Divider />
        <Spin spinning={loading}>
          <Form form={form} layout="vertical" initialValues={defaultSettings}>
            <Form.Item label="集团管控模式" name="groupControlMode" rules={[{ required: true, message: '请选择集团管控模式' }]}>
              <Radio.Group><Radio value="strong">强管控</Radio><Radio value="weak">弱管控</Radio></Radio.Group>
            </Form.Item>
            <Form.Item label="钟点房" name="hourlyRoom" rules={[{ required: true, message: '请选择是否支持钟点房' }]}>
              <Radio.Group><Radio value="support">支持</Radio><Radio value="notSupport">不支持</Radio></Radio.Group>
            </Form.Item>
            <Form.Item label="OTA促销模式" name="otaPromotionMode" rules={[{ required: true, message: '请选择OTA促销模式' }]}>
              <Radio.Group>
                <Radio value="groupRegistration">集团报名</Radio>
                <Radio value="groupRuleHotelRegistration">集团设置规则酒店报名</Radio>
                <Radio value="hotelSelfManagement">酒店自行管理</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item label="预测价格显示配置">
              <Form.Item name="showCtripPrice" valuePropName="checked" noStyle><Checkbox>显示携程预测价格</Checkbox></Form.Item>
              <br />
              <Form.Item name="showMeituanPrice" valuePropName="checked" noStyle><Checkbox>显示美团预测价格</Checkbox></Form.Item>
            </Form.Item>
          </Form>
        </Spin>
      </Card>
    </PageScaffold>
  )
}

export default GroupSettings

import { useState, useEffect } from 'react'
import { App, Card, Form, Input, Select, Button, Space, Table } from 'antd'
import { SaveOutlined, ArrowLeftOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { hotelApi, groupRateCodeApi, archiveApi } from '../../utils/api'
import { getCurrentTenantId } from '../../utils/tenantUtils'

const { Option } = Select

const EditArchive = () => {
  const [form] = Form.useForm()
  const { message } = App.useApp()
  const navigate = useNavigate()
  const { id } = useParams()
  const [loading, setLoading] = useState(false)
  const [rateCodes, setRateCodes] = useState([])
  const [hotels, setHotels] = useState([])
  const [rateCodesList, setRateCodesList] = useState([])

  useEffect(() => {
    const loadInitData = async () => {
      const tenantId = getCurrentTenantId()
      if (tenantId) {
        // 1. 加载接口基础数据（捕获并隔离网络异常）
        try {
          const [hotelRes, rateRes] = await Promise.all([
            hotelApi.getHotelsByTenantId(tenantId),
            groupRateCodeApi.getActiveGroupRateCodes()
          ])
          if (hotelRes && hotelRes.success) {
            setHotels(hotelRes.data || [])
          }
          setRateCodesList(Array.isArray(rateRes) ? rateRes : (rateRes?.data || []))
        } catch (apiError) {
          message.error(apiError?.error || '加载酒店和房价码数据失败')
        }

        // 2. 从后端加载档案数据并回填
        try {
          const res = await archiveApi.getArchiveById(parseInt(id))
          if (res) {
            const archive = res.data || res
            if (archive) {
              let rc = archive.rateCodes
              if (typeof rc === 'string' && rc.startsWith('[')) {
                try {
                  rc = JSON.parse(rc)
                } catch (e) {
                  rc = []
                }
              }
              const formattedArchive = {
                ...archive,
                contact: archive.contactName || archive.contact,
                phone: archive.contactPhone || archive.phone,
                rateCodes: Array.isArray(rc) ? rc : []
              }
              form.setFieldsValue(formattedArchive)
              setRateCodes(formattedArchive.rateCodes.map((item, index) => ({ ...item, key: item.key || index })))
            }
          }
        } catch (apiLoadError) {
          message.error('加载档案失败，请返回列表后重试')
        }
      }
    }
    loadInitData()
  }, [id, form])

  const columns = [
    {
      title: '酒店',
      dataIndex: 'hotel',
      key: 'hotel',
      render: (_, record, index) => (
        <Form.Item
          name={['rateCodes', index, 'hotel']}
          rules={[{ required: true, message: '请选择酒店' }]}
          noStyle
        >
          <Select placeholder="请选择酒店" style={{ width: '100%' }}>
            {hotels.map(h => (
              <Option key={h.hotelCode} value={h.hotelCode}>{h.chineseName}</Option>
            ))}
          </Select>
        </Form.Item>
      )
    },
    {
      title: '房价码',
      dataIndex: 'rateCode',
      key: 'rateCode',
      render: (_, record, index) => (
        <Form.Item
          name={['rateCodes', index, 'rateCode']}
          rules={[{ required: true, message: '请选择房价码' }]}
          noStyle
        >
          <Select placeholder="请选择房价码" style={{ width: '100%' }} mode="multiple">
            {rateCodesList.map(r => (
              <Option key={r.rateCode} value={r.rateCode}>{r.rateName} ({r.rateCode})</Option>
            ))}
          </Select>
        </Form.Item>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_, record, index) => (
        <Button
          type="link"
          danger
          icon={<DeleteOutlined />}
          onClick={() => handleDeleteRateCode(index)}
        >
          删除
        </Button>
      )
    }
  ]

  const handleAddRateCode = () => {
    setRateCodes([...rateCodes, { key: Date.now() }])
  }

  const handleDeleteRateCode = (index) => {
    const newRateCodes = [...rateCodes]
    newRateCodes.splice(index, 1)
    setRateCodes(newRateCodes)

    // 同步清理 Form 里的字段值以防止删除后表单项错位
    const currentRateCodes = form.getFieldValue('rateCodes') || []
    const updatedRateCodes = [...currentRateCodes]
    updatedRateCodes.splice(index, 1)
    form.setFieldsValue({ rateCodes: updatedRateCodes })
  }

  const handleSave = async () => {
    try {
      setLoading(true)
      const values = await form.validateFields()
      const payload = {
        ...values,
        contactName: values.contact,
        contactPhone: values.phone,
        rateCodes: JSON.stringify(values.rateCodes || [])
      }
      await archiveApi.updateArchive(parseInt(id), payload)
      message.success('保存成功')
      navigate('/group-management/archive-management')
    } catch (error) {
      if (!error?.errorFields) {
        const detail = typeof error === 'string' ? error : (error?.error || error?.message)
        message.error(detail || '保存失败，服务器未保存任何变更')
      }
    } finally {
      setLoading(false)
    }
  }

  const handleCancel = () => {
    navigate('/group-management/archive-management')
  }

  return (
    <div className="fade-in">
      <Form
        form={form}
        layout="vertical"
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <div>
            <h1 className="page-title" style={{ marginBottom: 8 }}>编辑档案</h1>
            <div style={{ fontSize: 14, color: '#8c8c8c' }}>
              集团管理 &gt; 档案管理 &gt; 编辑档案
            </div>
          </div>
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={handleCancel}>
              返回
            </Button>
            <Button type="primary" icon={<SaveOutlined />} onClick={handleSave} loading={loading}>
              保存
            </Button>
          </Space>
        </div>

        <Card title="基本信息" style={{ marginBottom: 16 }}>
          <div style={{ maxWidth: 800 }}>
            <Form.Item
              name="archiveId"
              label="档案ID"
              rules={[
                { required: true, message: '请输入档案ID' },
                { pattern: /^[A-Za-z0-9_]+$/, message: '档案ID只能包含英文字母、数字和下划线' }
              ]}
            >
              <Input disabled placeholder="请输入档案ID" />
            </Form.Item>
            <Form.Item
              name="name"
              label="档案名称"
              rules={[{ required: true, message: '请输入档案名称' }]}
            >
              <Input placeholder="请输入档案名称" />
            </Form.Item>
            <Form.Item
              name="type"
              label="档案类型"
              rules={[{ required: true, message: '请选择档案类型' }]}
            >
              <Select placeholder="请选择档案类型">
                <Option value="公司">公司</Option>
                <Option value="旅行社">旅行社</Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="bookingCode"
              label="预订代码"
            >
              <Input placeholder="请输入预订代码" />
            </Form.Item>
            <Form.Item
              name="contact"
              label="档案联系人"
            >
              <Input placeholder="请输入档案联系人" />
            </Form.Item>
            <Form.Item
              name="phone"
              label="联系人电话"
            >
              <Input placeholder="请输入联系人电话" />
            </Form.Item>
            <Form.Item
              name="address"
              label="档案联系地址"
            >
              <Input.TextArea placeholder="请输入档案联系地址" rows={3} />
            </Form.Item>
          </div>
        </Card>

        <Card
          title="分配房价码"
          extra={
            <Button type="link" icon={<PlusOutlined />} onClick={handleAddRateCode}>
              添加
            </Button>
          }
        >
          <Table
            columns={columns}
            dataSource={rateCodes.map((item, index) => ({ ...item, key: item.key || index }))}
            pagination={false}
            rowKey="key"
          />
        </Card>
      </Form>
    </div>
  )
}

export default EditArchive

import React, { useState, useEffect } from 'react'
import { Card, Form, Input, Select, Button, Space, message, Table } from 'antd'
import { SaveOutlined, ArrowLeftOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'

const { Option } = Select

const EditArchive = () => {
  const [form] = Form.useForm()
  const navigate = useNavigate()
  const { id } = useParams()
  const [loading, setLoading] = useState(false)
  const [rateCodes, setRateCodes] = useState([])

  useEffect(() => {
    const savedData = localStorage.getItem('archiveData')
    if (savedData) {
      const parsedData = JSON.parse(savedData)
      const archive = parsedData.find(item => item.id === parseInt(id))
      if (archive) {
        form.setFieldsValue(archive)
        if (archive.rateCodes) {
          setRateCodes(archive.rateCodes.map((item, index) => ({ ...item, key: item.key || index })))
        }
      }
    }
  }, [id, form])

  const columns = [
    {
      title: '酒店',
      dataIndex: 'hotel',
      key: 'hotel',
      render: (_, record, index) => (
        <Form.Item
          name={['rateCodes', index, 'hotel']}
          noStyle
        >
          <Select placeholder="请选择酒店" style={{ width: '100%' }}>
            <Option value="hotel1">北京王府井酒店</Option>
            <Option value="hotel2">上海外滩酒店</Option>
            <Option value="hotel3">广州天河酒店</Option>
            <Option value="hotel4">深圳南山酒店</Option>
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
          noStyle
        >
          <Select placeholder="请选择房价码" style={{ width: '100%' }}>
            <Option value="RACK">牌价</Option>
            <Option value="WEEKEND">周末价</Option>
            <Option value="CORP">企业价</Option>
            <Option value="PKG">套餐价</Option>
            <Option value="PROMO">促销价</Option>
          </Select>
        </Form.Item>
      )
    },
    {
      title: '房价名称',
      dataIndex: 'rateName',
      key: 'rateName',
      render: (_, record, index) => (
        <Form.Item
          name={['rateCodes', index, 'rateName']}
          noStyle
        >
          <Input placeholder="请输入房价名称" />
        </Form.Item>
      )
    },
    {
      title: '操作',
      key: 'action',
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
  }

  const handleSave = async () => {
    try {
      const values = await form.validateFields()
      const savedData = localStorage.getItem('archiveData')
      let existingData = savedData ? JSON.parse(savedData) : []
      const index = existingData.findIndex(item => item.id === parseInt(id))
      if (index !== -1) {
        existingData[index] = {
          ...existingData[index],
          ...values,
          rateCodes: rateCodes
        }
        localStorage.setItem('archiveData', JSON.stringify(existingData))
        message.success('保存成功')
        navigate('/group-management/archive-management')
      }
    } catch (error) {
      console.error('验证失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleCancel = () => {
    navigate('/group-management/archive-management')
  }

  return (
    <div className="fade-in">
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
        <Form
          form={form}
          layout="vertical"
          style={{ maxWidth: 800 }}
        >
          <Form.Item
            name="archiveId"
            label="档案ID"
            rules={[
              { required: true, message: '请输入档案ID' },
              { pattern: /^[A-Za-z0-9_]+$/, message: '档案ID只能包含英文字母、数字和下划线' }
            ]}
          >
            <Input placeholder="请输入档案ID" />
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
            name="level"
            label="档案等级"
            rules={[{ required: true, message: '请选择档案等级' }]}
          >
            <Select placeholder="请选择档案等级">
              <Option value="银卡">银卡</Option>
              <Option value="金卡">金卡</Option>
              <Option value="铂金卡">铂金卡</Option>
              <Option value="黑卡">黑卡</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="contact"
            label="档案联系人"
          >
            <Input placeholder="请输入档案联系人" />
          </Form.Item>
          <Form.Item
            name="address"
            label="档案联系地址"
          >
            <Input.TextArea placeholder="请输入档案联系地址" rows={3} />
          </Form.Item>
        </Form>
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
    </div>
  )
}

export default EditArchive

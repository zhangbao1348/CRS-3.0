import React, { useState } from 'react'
import { Card, Button, message, Table, Space, Tag, Modal, Form, Select, Input, DatePicker } from 'antd'
import { ArrowLeftOutlined, PlusOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'

const { Option } = Select

const CtripPromotionRegistration = () => {
  const navigate = useNavigate()
  const { id } = useParams()
  const [isRegistrationModalVisible, setIsRegistrationModalVisible] = useState(false)
  const [registrationForm] = Form.useForm()

  // 模拟促销报名数据
  const [registrationData, setRegistrationData] = useState([
    {
      id: 1,
      serialNumber: 1,
      property: '上海宝丽嘉酒店',
      rateCode: 'ADR(标准价)',
      room: 'ST, SE, SR',
      status: 'Registration Succeeded',
      statusClass: 'success'
    },
    {
      id: 2,
      serialNumber: 2,
      property: '上海宝丽嘉酒店',
      rateCode: 'AMD (会员价)',
      room: 'ST, SE, SR',
      status: 'Registration Failed',
      statusClass: 'error'
    },
    {
      id: 3,
      serialNumber: 3,
      property: '上海宝丽嘉酒店',
      rateCode: 'ACC (银卡会员价)',
      room: 'ST, SE, SR',
      status: 'Registration Failed',
      statusClass: 'error'
    },
    {
      id: 4,
      serialNumber: 4,
      property: '上海宝丽嘉酒店',
      rateCode: 'AAE (门市价)',
      room: 'ST, SE, SR',
      status: 'Registration Succeeded',
      statusClass: 'success'
    }
  ])

  // 模拟酒店数据
  const hotels = [
    { value: 'hotel1', label: '上海宝丽嘉酒店' },
    { value: 'hotel2', label: '杭州钓美酒店' },
    { value: 'hotel3', label: '北京王府井酒店' },
    { value: 'hotel4', label: '深圳南山酒店' }
  ]

  // 报名表格列定义
  const registrationColumns = [
    {
      title: '序号',
      dataIndex: 'serialNumber',
      key: 'serialNumber',
      width: 100
    },
    {
      title: '酒店',
      dataIndex: 'property',
      key: 'property',
      width: 180
    },
    {
      title: '价格码',
      dataIndex: 'rateCode',
      key: 'rateCode',
      width: 150
    },
    {
      title: '房型',
      dataIndex: 'room',
      key: 'room',
      width: 150,
      render: (room) => {
        const roomMap = {
          'ST': '标准大床房',
          'SE': '标准双床房',
          'SR': '豪华套房'
        }
        return room.split(', ').map(code => {
          const name = roomMap[code] || code
          return <div key={code}>{code} ({name})</div>
        })
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 150,
      render: (status, record) => (
        <Tag color={record.statusClass === 'success' ? 'green' : 'red'}>
          {status === 'Registration Succeeded' ? '报名成功' : status === 'Registration Failed' ? '报名失败' : status}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'operate',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          {record.statusClass === 'success' ? (
            <Button type="link" size="small" icon={<DeleteOutlined />} onClick={() => handleCancelRegistration(record)}>
              取消报名
            </Button>
          ) : (
            <>
              <Button type="link" size="small" icon={<ReloadOutlined />} onClick={() => handleRetryRegistration(record)}>
                重试
              </Button>
              <Button type="link" size="small" onClick={() => handleErrorLog(record)}>
                错误日志
              </Button>
            </>
          )}
        </Space>
      )
    }
  ]

  const handleBack = () => {
    navigate('/channel-management/ctrip-setting')
  }

  const handleOpenRegistrationModal = () => {
    registrationForm.resetFields()
    setIsRegistrationModalVisible(true)
  }

  const handleHotelChange = (value) => {
    const now = new Date()
    const threeMonthsLater = new Date()
    threeMonthsLater.setMonth(now.getMonth() + 3)
    
    registrationForm.setFieldsValue({
      saleValidity: [now, threeMonthsLater],
      stayValidity: [now, threeMonthsLater]
    })
  }

  const handleRateCodeChange = (value) => {
    const now = new Date()
    const threeMonthsLater = new Date()
    threeMonthsLater.setMonth(now.getMonth() + 3)
    
    let rateCodeName = ''
    switch(value) {
      case 'ADR':
        rateCodeName = '标准价'
        break
      case 'AMD':
        rateCodeName = '会员价'
        break
      case 'ACC':
        rateCodeName = '银卡会员价'
        break
      case 'AAE':
        rateCodeName = '门市价'
        break
      default:
        rateCodeName = value
    }
    
    registrationForm.setFieldsValue({
      rateCodeName: rateCodeName
    })
    
    registrationForm.validateFields(['saleValidity', 'stayValidity']).then(values => {
      if (!values.saleValidity) {
        registrationForm.setFieldsValue({
          saleValidity: [now, threeMonthsLater]
        })
      }
      if (!values.stayValidity) {
        registrationForm.setFieldsValue({
          stayValidity: [now, threeMonthsLater]
        })
      }
    })
  }

  const handleRegistrationOk = () => {
    registrationForm.validateFields().then(values => {
      message.success('促销报名成功')
      setIsRegistrationModalVisible(false)
    })
  }

  const handleRegistrationCancel = () => {
    setIsRegistrationModalVisible(false)
  }

  const handleRetryRegistration = (record) => {
    message.success(`正在重试 ${record.rateCode} 的报名...`)
    setTimeout(() => {
      setRegistrationData(registrationData.map(item => 
        item.id === record.id ? { ...item, status: 'Registration Succeeded', statusClass: 'success' } : item
      ))
      message.success(`${record.rateCode} 报名成功`)
    }, 1000)
  }

  const handleCancelRegistration = (record) => {
    message.success(`已取消 ${record.rateCode} 的报名`)
  }

  const handleErrorLog = (record) => {
    message.info(`查看 ${record.rateCode} 的错误日志`)
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        促销报名
      </h1>
      
      <Card>
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Button icon={<ArrowLeftOutlined />} onClick={handleBack}>
            返回
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleOpenRegistrationModal}>
            报名新价格
          </Button>
        </div>
        
        <Table
          columns={registrationColumns}
          dataSource={registrationData}
          rowKey="id"
          pagination={false}
          scroll={{ x: 700 }}
        />
      </Card>

      <Modal
        title="新增促销报名"
        open={isRegistrationModalVisible}
        onOk={handleRegistrationOk}
        onCancel={handleRegistrationCancel}
        width={500}
        okText="报名"
        cancelText="取消"
      >
        <Form
          form={registrationForm}
          layout="vertical"
        >
          <Form.Item
            label="*请选择酒店:"
            name="hotel"
            rules={[{ required: true, message: '请选择酒店' }]}
          >
            <Select placeholder="请选择酒店" onChange={handleHotelChange}>
              {hotels.map(hotel => (
                <Option key={hotel.value} value={hotel.value}>{hotel.label}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="*房价码:"
            name="rateCode"
            rules={[{ required: true, message: '请输入房价码' }]}
          >
            <Input placeholder="请输入房价码" onChange={(e) => handleRateCodeChange(e.target.value)} />
          </Form.Item>
          <Form.Item
            label="房价码名称:"
            name="rateCodeName"
          >
            <Input placeholder="请输入房价码名称" />
          </Form.Item>
          <Form.Item
            label="房价码售卖有效期:"
            name="saleValidity"
          >
            <DatePicker.RangePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            label="房价码入住有效期:"
            name="stayValidity"
          >
            <DatePicker.RangePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            label="*是否需要根据折扣反算促销前价格:"
            name="needCalculateOriginalPrice"
            rules={[{ required: true, message: '请选择是否需要反算' }]}
          >
            <Select placeholder="请选择">
              <Option value="yes">是</Option>
              <Option value="no">否</Option>
            </Select>
          </Form.Item>
          <div style={{ color: '#666', fontSize: '12px', marginBottom: 16 }}>
            注意，当前房价码价格如果是促销后价格，需要选择时，推送价格时进行重新计算价格
          </div>
        </Form>
      </Modal>

    </div>
  )
}

export default CtripPromotionRegistration

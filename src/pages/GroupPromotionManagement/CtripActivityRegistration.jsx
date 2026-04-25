import React, { useState, useEffect } from 'react'
import { Card, Button, Radio, message, Tabs, Table, Space, Tag, Modal, Form, Select, Input } from 'antd'
import { ArrowLeftOutlined, SaveOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'

const { TabPane } = Tabs

const CtripActivityRegistration = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const [activity, setActivity] = useState(null)
  const [selectedDiscount, setSelectedDiscount] = useState('8.8折')
  const [activeTab, setActiveTab] = useState('basic')
  const [isRegistrationModalVisible, setIsRegistrationModalVisible] = useState(false)
  const [registrationForm] = Form.useForm()

  // 模拟报名数据
  const registrationData = [
    {
      id: 1,
      serialNumber: 1,
      property: 'BGOSH',
      rateCode: 'ADR(标准价)',
      room: 'ST, SE, SR',
      status: 'Registration Succeeded',
      statusClass: 'success'
    },
    {
      id: 2,
      serialNumber: 2,
      property: 'BGOSH',
      rateCode: 'AMD (会员价)',
      room: 'ST, SE, SR',
      status: 'Registration Failed',
      statusClass: 'error'
    },
    {
      id: 3,
      serialNumber: 3,
      property: 'BGOSH',
      rateCode: 'ACC (银卡会员价)',
      room: 'ST, SE, SR',
      status: 'Registration Failed',
      statusClass: 'error'
    },
    {
      id: 4,
      serialNumber: 4,
      property: 'BGOSH',
      rateCode: 'AAE (门市价)',
      room: 'ST, SE, SR',
      status: 'Registration Succeeded',
      statusClass: 'success'
    }
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
      width: 100
    },
    {
      title: '价格代码',
      dataIndex: 'rateCode',
      key: 'rateCode',
      width: 150
    },
    {
      title: '房型',
      dataIndex: 'room',
      key: 'room',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 150,
      render: (status, record) => (
        <Tag color={record.statusClass === 'success' ? 'green' : 'red'}>
          {status === 'Registration Succeeded' ? '报名成功' : '报名失败'}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'operate',
      width: 150,
      render: (_, record) => (
        <div>
          <a href="#" style={{ color: '#1890ff' }}>取消报名</a>
          <a href="#" style={{ color: '#1890ff', marginLeft: 8 }}>日志</a>
        </div>
      )
    }
  ]

  // 模拟活动数据
  const mockActivities = [
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

  useEffect(() => {
    // 从URL参数中获取活动ID
    const activityId = new URLSearchParams(location.search).get('id')
    if (activityId) {
      const foundActivity = mockActivities.find(act => act.id === parseInt(activityId))
      if (foundActivity) {
        setActivity(foundActivity)
      }
    }
  }, [location.search])

  const handleSave = () => {
    message.success('活动报名成功')
    navigate('/group-promotion-management/ctrip-activity-management')
  }

  const handleBack = () => {
    navigate('/group-promotion-management/ctrip-activity-management')
  }

  const handleRegistrationModalOpen = () => {
    setIsRegistrationModalVisible(true)
  }

  const handleRegistrationModalCancel = () => {
    setIsRegistrationModalVisible(false)
    registrationForm.resetFields()
  }

  const handleRegistrationSubmit = (values) => {
    // 处理报名提交
    console.log('报名提交:', values)
    message.success('活动报名成功')
    setIsRegistrationModalVisible(false)
    registrationForm.resetFields()
  }

  if (!activity) {
    return <div>加载中...</div>
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        活动报名
      </h1>
      
      <Card>
        <div style={{ marginBottom: 24 }}>
          <Tabs activeKey={activeTab} onChange={setActiveTab}>
            <TabPane tab="基本信息" key="basic">
              <div style={{ marginBottom: 24 }}>
                <h3>活动名称: {activity.activityName}</h3>
              </div>
              
              <div style={{ marginBottom: 24 }}>
                <h4>活动描述:</h4>
                <pre style={{ whiteSpace: 'pre-wrap', margin: 0 }}>{activity.description}</pre>
              </div>
              
              <div style={{ marginBottom: 24 }}>
                <h4>报名日期: {activity.registrationDate}</h4>
              </div>
              
              <div style={{ marginBottom: 24 }}>
                <h4>入住日期: {activity.stayDate}</h4>
              </div>
              
              <div style={{ marginBottom: 24 }}>
                <h4>详情:</h4>
                <pre style={{ whiteSpace: 'pre-wrap', margin: 0 }}>{activity.details}</pre>
              </div>
              
              <div style={{ marginBottom: 24 }}>
                <h4>选择:</h4>
                <Radio.Group value={selectedDiscount} onChange={(e) => setSelectedDiscount(e.target.value)}>
                  <Radio value="8.8折">8.8折</Radio>
                  <Radio value="8折">8折</Radio>
                  <Radio value="7折">7折</Radio>
                </Radio.Group>
              </div>
              
              <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center' }}>
                <Button icon={<ArrowLeftOutlined />} onClick={handleBack} style={{ marginRight: 16 }}>
                  返回
                </Button>
                <Button type="primary" icon={<SaveOutlined />} onClick={handleSave} style={{ width: 120 }}>
                  保存
                </Button>
              </div>
            </TabPane>
            <TabPane tab="活动报名" key="registration">
              <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center' }}>
                <Button type="primary" style={{ marginRight: 12 }} onClick={handleRegistrationModalOpen}>活动报名</Button>
              </div>
              
              <Table
                columns={registrationColumns}
                dataSource={registrationData}
                rowKey="id"
                pagination={{
                  showTotal: (total, range) => `分页`
                }}
              />
            </TabPane>
          </Tabs>
        </div>
      </Card>

      {/* 活动报名弹框 */}
      <Modal
        title="新增促销报名"
        open={isRegistrationModalVisible}
        onCancel={handleRegistrationModalCancel}
        footer={null}
        width={600}
      >
        <Form
          form={registrationForm}
          onFinish={handleRegistrationSubmit}
          layout="vertical"
        >
          <Form.Item
            label="*请选择酒店："
            name="hotel"
            rules={[{ required: true, message: '请选择酒店' }]}
          >
            <Select placeholder="请选择酒店" style={{ width: 300 }}>
              <Select.Option value="1">酒店1</Select.Option>
              <Select.Option value="2">酒店2</Select.Option>
              <Select.Option value="3">酒店3</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="*房价码："
            name="rateCode"
            rules={[{ required: true, message: '请输入房价码' }]}
          >
            <Input style={{ width: 300 }} />
          </Form.Item>

          <Form.Item
            label="房价码名称："
            name="rateCodeName"
          >
            <Input style={{ width: 300 }} />
          </Form.Item>

          <Form.Item
            label="房价码售卖有效期："
            name="sellValidity"
          >
            <Input style={{ width: 300 }} placeholder="YYYY-MM-DD 至 YYYY-MM-DD" />
          </Form.Item>

          <Form.Item
            label="房价码入住有效期："
            name="stayValidity"
          >
            <Input style={{ width: 300 }} placeholder="YYYY-MM-DD 至 YYYY-MM-DD" />
          </Form.Item>

          <Form.Item
            label="*是否需要根据折扣反算促销前价格："
            name="needCalculateOriginalPrice"
            rules={[{ required: true, message: '请选择' }]}
          >
            <Select style={{ width: 300 }}>
              <Select.Option value="true">是</Select.Option>
              <Select.Option value="false">否</Select.Option>
            </Select>
          </Form.Item>

          <div style={{ margin: '20px 0', color: 'red' }}>
            注意，当前房价码价格如果是促销后价格，需要选择时，推送价格时进行重新计算价格
          </div>

          <Form.Item>
            <div style={{ display: 'flex', justifyContent: 'center', gap: 20 }}>
              <Button type="primary" htmlType="submit" style={{ width: 120, height: 40 }}>
                报名
              </Button>
              <Button onClick={handleRegistrationModalCancel} style={{ width: 120, height: 40 }}>
                取消
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default CtripActivityRegistration
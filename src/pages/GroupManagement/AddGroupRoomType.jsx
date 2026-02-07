import React, { useState } from 'react'
import { Form, Input, Select, Radio, Button, Tabs, Card, Row, Col, Table, Switch } from 'antd'
import { PlusOutlined } from '@ant-design/icons'

const { Option } = Select

const AddGroupRoomType = () => {
  const [form] = Form.useForm()
  
  // 酒店分配数据
  const [hotelData, setHotelData] = useState([
    {
      key: '1',
      hotel: '酒店A',
      allocated: false,
      roomInfoEditable: false
    },
    {
      key: '2',
      hotel: '酒店B',
      allocated: false,
      roomInfoEditable: false
    }
  ])

  // 保存并下一步
  const handleSave = () => {
    form.validateFields().then(values => {
      console.log('表单数据:', values)
      // 处理保存逻辑
    }).catch(errorInfo => {
      console.log('表单验证失败:', errorInfo)
    })
  }

  // 处理Switch开关变化
  const handleSwitchChange = (record, field) => {
    return (checked) => {
      setHotelData(hotelData.map(item => {
        if (item.key === record.key) {
          if (field === 'allocated') {
            // 如果是分配状态变化，当取消分配时，将可修改状态设为false
            return {
              ...item,
              allocated: checked,
              ...(checked ? {} : {
                roomInfoEditable: false
              })
            }
          } else {
            // 只有当酒店被分配时，才能修改其他字段
            if (!record.allocated) {
              return item
            }
            return {
              ...item,
              [field]: checked
            }
          }
        }
        return item
      }))
    }
  }

  // 定义标签页内容
  const tabItems = [
    {
      key: '1',
      label: '基础信息',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 600 }}>
          <Form form={form} layout="vertical">
            <Row gutter={[16, 16]}>
              <Col span={24}>
                <Form.Item
                  name="roomCode"
                  label="房型代码"
                  rules={[{ required: true, message: '请输入房型代码' }]}
                >
                  <Input placeholder="请输入房型代码" />
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  name="roomName"
                  label="房型名称"
                  rules={[{ required: true, message: '请输入房型名称' }]}
                >
                  <Input placeholder="请输入房型名称" />
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  name="roomCategory"
                  label="房型大类"
                  rules={[{ required: true, message: '请选择房型大类' }]}
                >
                  <Select placeholder="请选择房型大类" style={{ width: '100%' }}>
                    <Option value="大床房">大床房</Option>
                    <Option value="双床房">双床房</Option>
                    <Option value="套房">套房</Option>
                    <Option value="家庭房">家庭房</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  name="status"
                  label="房型状态"
                  rules={[{ required: true, message: '请选择房型状态' }]}
                >
                  <Radio.Group>
                    <Radio value="有效">有效</Radio>
                    <Radio value="无效">无效</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item style={{ marginTop: 32 }}>
                  <Button type="primary" size="large" onClick={handleSave}>
                    保存, 并下一步
                  </Button>
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Card>
      )
    },
    {
      key: '2',
      label: '房型分配',
      children: (
        <Card style={{ marginBottom: 24 }}>
          <Table
            columns={[
              {
                title: '酒店',
                dataIndex: 'hotel',
                key: 'hotel',
                width: 150
              },
              {
                title: '是否分配到酒店',
                dataIndex: 'allocated',
                key: 'allocated',
                width: 150,
                render: (text, record) => (
                  <Switch 
                    checked={text} 
                    onChange={handleSwitchChange(record, 'allocated')}
                  />
                )
              },
              {
                title: '房型信息是否可以修改',
                dataIndex: 'roomInfoEditable',
                key: 'roomInfoEditable',
                width: 180,
                render: (text, record) => (
                  <Switch 
                    checked={text} 
                    onChange={handleSwitchChange(record, 'roomInfoEditable')}
                    disabled={!record.allocated}
                  />
                )
              }
            ]}
            dataSource={hotelData}
            pagination={false}
            bordered
            size="middle"
          />
        </Card>
      )
    }
  ]

  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', overflow: 'auto' }}>
      <h1 className="page-title">集团房型新增/编辑</h1>
      
      <Tabs defaultActiveKey="1" items={tabItems} />
    </div>
  )
}

export default AddGroupRoomType
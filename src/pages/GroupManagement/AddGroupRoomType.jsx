import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Radio, Button, Tabs, Card, Row, Col, Table, Switch, message, Spin } from 'antd'
import { PlusOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import axios from 'axios'

const { Option } = Select

const AddGroupRoomType = () => {
  // 路由管理
  const navigate = useNavigate()
  const location = useLocation()
  
  // 状态管理
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [hotelData, setHotelData] = useState([])
  const [hotels, setHotels] = useState([])
  const [isEditing, setIsEditing] = useState(false)
  const [currentId, setCurrentId] = useState(null)
  const [activeTab, setActiveTab] = useState(location.state?.activeTab || '1')

  // 获取路由参数中的编辑数据和酒店列表
  useEffect(() => {
    const fetchData = async () => {
      try {
        console.log('开始获取数据...')
        // 先获取酒店列表
        await fetchHotels()
        
        // 然后获取房型分配状态（如果是编辑模式）
        const record = location.state?.record
        console.log('location.state:', location.state)
        if (record) {
          setIsEditing(true)
          setCurrentId(record.id)
          // 填充表单数据
          form.setFieldsValue({
            roomCode: record.code,
            roomName: record.name,
            roomCategory: record.category,
            status: record.status === '启用' ? 'active' : 'inactive'
          })
          // 获取酒店分配状态
          await fetchRoomTypeAllocations(record.id)
        }
      } catch (error) {
        console.error('获取数据失败:', error)
      }
    }
    
    fetchData()
  }, [location.state, form])

  // 获取酒店列表
  const fetchHotels = async () => {
    try {
      console.log('开始获取酒店列表...')
      const response = await axios.get('http://localhost:8080/api/hotels')
      const hotelsList = response.data
      console.log('获取到的酒店列表:', hotelsList)
      setHotels(hotelsList)
      // 初始化酒店数据
      if (Array.isArray(hotelsList)) {
        const initialHotelData = hotelsList.map(hotel => ({
          key: hotel.id.toString(),
          hotel: hotel.chineseName,
          hotelId: hotel.id,
          allocated: false,
          roomInfoEditable: false
        }))
        console.log('初始化的酒店数据:', initialHotelData)
        setHotelData(initialHotelData)
      } else {
        console.error('获取到的酒店列表不是数组:', hotelsList)
        message.error('获取酒店列表格式错误')
      }
    } catch (error) {
      console.error('获取酒店列表失败:', error)
      message.error('获取酒店列表失败')
    }
  }
  
  // 获取集团房型的酒店分配状态
  const fetchRoomTypeAllocations = async (roomTypeId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/group-room-type-hotels/group/${roomTypeId}`)
      const allocations = response.data
      console.log('获取到的酒店分配状态:', allocations)
      
      // 更新酒店分配状态
      setHotelData(prevHotelData => {
        const updatedHotelData = prevHotelData.map(item => {
          const allocation = allocations.find(a => a.hotelId === item.hotelId)
          if (allocation) {
            return {
              ...item,
              allocated: allocation.allocated,
              roomInfoEditable: allocation.roomInfoEditable
            }
          }
          return item
        })
        console.log('更新后的酒店数据:', updatedHotelData)
        return updatedHotelData
      })
    } catch (error) {
      console.warn('获取酒店分配状态失败，将使用默认值:', error.message)
      // 继续执行其他操作，不中断流程
    }
  }
  
  // 保存并下一步
  const handleSave = async () => {
    try {
      setLoading(true)
      const values = await form.validateFields()
      console.log('表单数据:', values)
      
      // 准备提交数据
      const submitData = {
        roomTypeCode: values.roomCode,
        roomTypeName: values.roomName,
        description: values.description || '',
        status: values.status === 'active' ? 'active' : 'inactive',
        groupId: 1 // 默认集团ID，可以根据实际情况调整
      }
      
      // 调用后端API
      let roomTypeId = currentId
      if (isEditing && currentId) {
        // 编辑模式
        const response = await axios.put(`http://localhost:8080/api/group-room-types/${currentId}`, submitData)
        message.success('房型更新成功')
      } else {
        // 创建模式
        const response = await axios.post('http://localhost:8080/api/group-room-types', submitData)
        message.success('房型创建成功')
        roomTypeId = response.data.id
        setCurrentId(roomTypeId)
        setIsEditing(true)
      }
      
      // 保存成功后显示提示并返回列表页面
      message.success('房型保存成功')
      navigate('/group-management/group-room-type')
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败: ' + (error.response?.data?.error || error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
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

  // 分配到酒店
  const handleAllocateToHotel = async () => {
    if (!currentId) {
      message.error('请先保存房型基础信息')
      return
    }
    
    try {
      setLoading(true)
      
      // 准备分配数据
      const allocationData = hotelData.filter(item => item.allocated).map(item => ({
        groupRoomTypeId: currentId,
        hotelId: item.hotelId,
        allocated: item.allocated,
        roomInfoEditable: item.roomInfoEditable
      }))
      
      // 调用后端API
      await axios.post('http://localhost:8080/api/group-room-type-hotels', allocationData)
      
      message.success('酒店分配成功')
      // 分配成功后返回列表页面
      navigate('/group-management/group-room-type')
    } catch (error) {
      console.error('酒店分配失败:', error)
      message.error('酒店分配失败: ' + (error.response?.data?.error || error.message || '未知错误'))
      // 即使失败也返回列表页面，避免用户卡在分配页面
      setTimeout(() => {
        navigate('/group-management/group-room-type')
      }, 1500)
    } finally {
      setLoading(false)
    }
  }

  // 定义标签页内容
  const tabItems = [
    {
      key: '1',
      label: '基础信息',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 800 }}>
          <Form form={form} layout="vertical">
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="roomCode"
                  label="房型代码"
                  rules={[{ required: true, message: '请输入房型代码' }]}
                >
                  <Input placeholder="请输入房型代码" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="roomName"
                  label="房型名称"
                  rules={[{ required: true, message: '请输入房型名称' }]}
                >
                  <Input placeholder="请输入房型名称" />
                </Form.Item>
              </Col>
              <Col span={12}>
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
              <Col span={12}>
                <Form.Item
                  name="status"
                  label="房型状态"
                  rules={[{ required: true, message: '请选择房型状态' }]}
                  initialValue="active"
                >
                  <Radio.Group>
                    <Radio value="active">启用</Radio>
                    <Radio value="inactive">停用</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  name="description"
                  label="房型描述"
                >
                  <Input.TextArea placeholder="请输入房型描述" rows={4} />
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item style={{ marginTop: 32 }}>
                  <Button type="primary" size="large" onClick={handleSave} loading={loading}>
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
          <div style={{ marginTop: 16, textAlign: 'right' }}>
            <Button type="primary" onClick={handleAllocateToHotel}>
              保存分配设置
            </Button>
          </div>
        </Card>
      )
    }
  ]

  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', overflow: 'auto' }}>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 className="page-title">
          {isEditing ? '编辑集团房型' : '新增集团房型'}
        </h1>
        <Button 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/group-management/group-room-type')}
        >
          返回列表
        </Button>
      </div>
      
      <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems} />
    </div>
  )
}

export default AddGroupRoomType
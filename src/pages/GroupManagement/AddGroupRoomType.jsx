import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Radio, Button, Tabs, Card, Row, Col, Table, Switch, message, Spin, InputNumber, Space, Modal, Checkbox } from 'antd'
import { PlusOutlined, ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import axios from 'axios'
import { groupRoomTypeApi, hotelApi } from '../../utils/api'
import { getCurrentTenantId } from '../../utils/tenantUtils'

const { Option } = Select

const AddGroupRoomType = () => {
  const navigate = useNavigate()
  const location = useLocation()
  
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [hotelData, setHotelData] = useState([])
  const [hotels, setHotels] = useState([])
  const [categories, setCategories] = useState([])
  const [isEditing, setIsEditing] = useState(false)
  const [currentId, setCurrentId] = useState(null)
  const [activeTab, setActiveTab] = useState(location.state?.activeTab || '1')
  
  // 全选/取消全选状态
  const [selectAll, setSelectAll] = useState(false)
  const [selectAllRoomInfo, setSelectAllRoomInfo] = useState(false)
  
  // 批量分配模态框
  const [batchModalVisible, setBatchModalVisible] = useState(false)
  const [selectedHotels, setSelectedHotels] = useState([])
  const [selectedLimits, setSelectedLimits] = useState([])
  
  // 分组状态
  const [groupBy, setGroupBy] = useState('none')
  const [hotelBaseData, setHotelBaseData] = useState([])

  useEffect(() => {
    const fetchData = async () => {
      try {
        await fetchCategories()
        const baseData = await fetchHotels()
        setHotelBaseData(baseData)
        
        const record = location.state?.record
        if (record) {
          setIsEditing(true)
          setCurrentId(record.id)
          await fetchRoomTypeDetail(record.id)
          if (baseData.length > 0) {
            await fetchRoomTypeAllocations(record.id, baseData)
          }
        }
      } catch (error) {
        console.error('获取数据失败:', error)
      }
    }
    
    fetchData()
  }, [location.state, form])
  
  // 监听tab切换，当切换到房型分配时加载分配数据
  useEffect(() => {
    if (activeTab === '2' && isEditing && currentId && hotelBaseData.length > 0) {
      console.log('切换到房型分配标签，开始加载分配数据...')
      fetchRoomTypeAllocations(currentId, hotelBaseData)
    }
  }, [activeTab, isEditing, currentId, hotelBaseData])

  const fetchCategories = async () => {
    try {
      const response = await axios.get('/api/room-type-categories/group/1')
      setCategories(response.data)
    } catch (error) {
      console.error('获取房型大类失败:', error)
    }
  }

  const fetchRoomTypeDetail = async (id) => {
    try {
      const roomType = await groupRoomTypeApi.getGroupRoomTypeById(id)
      form.setFieldsValue({
        roomCode: roomType.roomTypeCode,
        roomName: roomType.roomTypeName,
        roomCategoryId: roomType.roomTypeCategoryId,
        maxOccupancy: roomType.maxOccupancy,
        sortOrder: roomType.sortOrder,
        description: roomType.description,
        status: roomType.status
      })
    } catch (error) {
      console.error('获取房型详情失败:', error)
    }
  }

  const fetchHotels = async () => {
    try {
      const groupId = getCurrentTenantId()
      if (!groupId) {
        setHotelData([])
        return []
      }
      
      const response = await hotelApi.getHotelsByTenantId(groupId)
      if (response && response.success && response.data && response.data.length > 0) {
        const formattedHotelData = response.data.map((hotel, index) => ({
          key: String(hotel.id || index),
          hotelId: hotel.id,
          hotelCode: hotel.hotelCode,
          hotel: hotel.chineseName,
          region: hotel.province,
          city: hotel.city,
          brand: '',
          allocated: false,
          roomInfoEditable: false
        }))
        setHotelData(formattedHotelData)
        setHotels(response.data)
        return formattedHotelData
      } else {
        setHotelData([])
        setHotels([])
        return []
      }
    } catch (error) {
      console.error('获取酒店列表失败:', error)
      message.error('获取酒店列表失败')
      setHotelData([])
      return []
    }
  }
  
  const fetchRoomTypeAllocations = async (roomTypeId, baseData) => {
    if (!roomTypeId) return
    try {
      console.log('开始获取房型分配数据... roomTypeId:', roomTypeId)
      const response = await axios.get(`/api/group-room-types/${roomTypeId}/allocations`)
      const allocations = response.data
      console.log('获取到的分配数据:', allocations)
      console.log('基础酒店数据:', baseData)
      
      if (Array.isArray(allocations)) {
        if (baseData) {
          const merged = baseData.map(item => {
            // 关联查询原则：使用 hotelCode 匹配，而非 hotelId（符合CODE关联规范）
            const alloc = allocations.find(a => a.hotelCode && a.hotelCode === item.hotelCode)
            console.log(`处理酒店 ${item.hotelCode} (${item.hotel}), 找到分配:`, alloc)
            if (alloc) {
              return {
                ...item,
                allocated: alloc.allocated || false,
                roomInfoEditable: alloc.roomInfoEditable || false
              }
            }
            return item
          })
          console.log('合并后的酒店数据:', merged)
          setHotelData(merged)
        } else {
          setHotelData(prev => prev.map(item => {
            // 关联查询原则：使用 hotelCode 匹配，而非 hotelId
            const alloc = allocations.find(a => a.hotelCode && a.hotelCode === item.hotelCode)
            if (alloc) {
              return {
                ...item,
                allocated: alloc.allocated || false,
                roomInfoEditable: alloc.roomInfoEditable || false
              }
            }
            return item
          }))
        }
      }
    } catch (error) {
      console.error('获取酒店分配状态失败:', error)
    }
  }
  
  const handleSave = async () => {
    try {
      setLoading(true)
      const values = await form.validateFields()
      const groupId = getCurrentTenantId()
      
      const submitData = {
        roomTypeCode: values.roomCode,
        roomTypeName: values.roomName,
        roomTypeCategoryId: values.roomCategoryId,
        maxOccupancy: values.maxOccupancy || 2,
        sortOrder: values.sortOrder || 0,
        description: values.description || '',
        status: values.status || 'active',
        groupId: groupId
      }
      
      let roomTypeId = currentId
      if (isEditing && currentId) {
        await axios.put(`/api/group-room-types/${currentId}`, submitData)
        message.success('房型更新成功')
      } else {
        const response = await axios.post('/api/group-room-types', submitData)
        message.success('房型创建成功')
        roomTypeId = response.data.id
        setCurrentId(roomTypeId)
        setIsEditing(true)
      }
      
      setActiveTab('2')
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败: ' + (error.response?.data?.error || error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  const handleSwitchChange = (record, field) => {
    return (checked) => {
      const updatedData = hotelData.map(item => {
        if (item.key === record.key) {
          if (field === 'allocated') {
            return {
              ...item,
              allocated: checked,
              ...(checked ? {} : { roomInfoEditable: false })
            }
          } else {
            if (!record.allocated) {
              return item
            }
            return { ...item, [field]: checked }
          }
        }
        return item
      })
      
      setHotelData(updatedData)
      
      if (field === 'allocated') {
        const allChecked = updatedData.every(item => item.allocated)
        setSelectAll(allChecked)
      } else if (field === 'roomInfoEditable') {
        const allAllocated = updatedData.filter(item => item.allocated)
        const allChecked = allAllocated.every(item => item.roomInfoEditable)
        setSelectAllRoomInfo(allChecked)
      }
    }
  }

  const handleAllocateToHotel = async () => {
    if (!currentId) {
      message.error('请先保存房型基础信息')
      return
    }
    
    try {
      setLoading(true)
      
      const allocationData = hotelData.map(item => ({
        groupRoomTypeId: currentId,
        hotelId: item.hotelId,
        hotelCode: item.hotelCode,
        allocated: item.allocated,
        roomInfoEditable: item.roomInfoEditable
      }))
      
      await axios.post(`/api/group-room-types/${currentId}/allocate`, allocationData)
      
      message.success('酒店分配成功')
      navigate('/group-management/group-room-type')
    } catch (error) {
      console.error('酒店分配失败:', error)
      message.error('酒店分配失败: ' + (error.response?.data?.error || error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  const handleSaveAll = async () => {
    try {
      setLoading(true)
      const values = await form.validateFields()
      const groupId = getCurrentTenantId()
      
      const submitData = {
        roomTypeCode: values.roomCode,
        roomTypeName: values.roomName,
        roomTypeCategoryId: values.roomCategoryId,
        maxOccupancy: values.maxOccupancy || 2,
        sortOrder: values.sortOrder || 0,
        description: values.description || '',
        status: values.status || 'active',
        groupId: groupId
      }
      
      let roomTypeId = currentId
      if (isEditing && currentId) {
        await axios.put(`/api/group-room-types/${currentId}`, submitData)
      } else {
        const response = await axios.post('/api/group-room-types', submitData)
        roomTypeId = response.data.id
        setCurrentId(roomTypeId)
        setIsEditing(true)
      }
      
      const allocationData = hotelData.map(item => ({
        groupRoomTypeId: roomTypeId,
        hotelId: item.hotelId,
        hotelCode: item.hotelCode,
        allocated: item.allocated,
        roomInfoEditable: item.roomInfoEditable
      }))
      
      await axios.post(`/api/group-room-types/${roomTypeId}/allocate`, allocationData)
      
      message.success('保存成功')
      navigate('/group-management/group-room-type')
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败: ' + (error.response?.data?.error || error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  const handleSelectAll = (checked) => {
    setSelectAll(checked)
    const newData = hotelData.map(item => ({
      ...item,
      allocated: checked
    }))
    setHotelData(newData)
  }
  
  const handleSelectAllRoomInfo = (checked) => {
    setSelectAllRoomInfo(checked)
    const newData = hotelData.map(item => {
      if (item.allocated) {
        return { ...item, roomInfoEditable: checked }
      }
      return item
    })
    setHotelData(newData)
  }

  const handleBatchAllocate = () => {
    setSelectedHotels([])
    setSelectedLimits([])
    setBatchModalVisible(true)
  }

  const handleBatchConfirm = () => {
    if (selectedHotels.length === 0) {
      message.warning('请至少选择一个酒店')
      return
    }
    
    setHotelData(hotelData.map(item => {
      if (selectedHotels.includes(item.key)) {
        const updatedItem = { ...item, allocated: true }
        selectedLimits.forEach(limit => {
          updatedItem[limit] = true
        })
        return updatedItem
      }
      return item
    }))
    
    setBatchModalVisible(false)
    message.success('批量分配成功')
  }

  const handleBatchCancel = () => {
    setBatchModalVisible(false)
  }

  const getGroupedData = () => {
    if (groupBy === 'none') {
      return { '所有酒店': hotelData }
    } else if (groupBy === 'region') {
      const grouped = {}
      hotelData.forEach(hotel => {
        if (!grouped[hotel.region]) {
          grouped[hotel.region] = []
        }
        grouped[hotel.region].push(hotel)
      })
      return grouped
    } else if (groupBy === 'city') {
      const grouped = {}
      hotelData.forEach(hotel => {
        if (!grouped[hotel.city]) {
          grouped[hotel.city] = []
        }
        grouped[hotel.city].push(hotel)
      })
      return grouped
    } else if (groupBy === 'brand') {
      const grouped = {}
      hotelData.forEach(hotel => {
        if (!grouped[hotel.brand]) {
          grouped[hotel.brand] = []
        }
        grouped[hotel.brand].push(hotel)
      })
      return grouped
    }
    return { '所有酒店': hotelData }
  }

  const tabItems = [
    {
      key: '1',
      label: '基础信息',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 900 }}>
          <Form form={form} layout="vertical">
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="roomCode"
                  label="房型代码"
                  rules={[
                    { required: true, message: '请输入房型代码' },
                    { pattern: /^[A-Za-z0-9_]+$/, message: '房型代码只能包含英文字母、数字和下划线' }
                  ]}
                >
                  <Input placeholder="请输入房型代码，如STD-KING" disabled={isEditing} />
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
                  name="roomCategoryId"
                  label="房型大类"
                  rules={[{ required: true, message: '请选择房型大类' }]}
                >
                  <Select placeholder="请选择房型大类" style={{ width: '100%' }}>
                    {categories.map(category => (
                      <Option key={category.id} value={category.id}>
                        {category.categoryName}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="maxOccupancy"
                  label="最大入住人数"
                  initialValue={2}
                >
                  <InputNumber min={1} max={10} style={{ width: '100%' }} placeholder="请输入最大入住人数" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="sortOrder"
                  label="排序"
                  initialValue={0}
                >
                  <InputNumber min={0} style={{ width: '100%' }} placeholder="数字越小越靠前" />
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
                <Form.Item style={{ marginTop: 16 }}>
                  <Space>
                    <Button type="primary" size="large" onClick={handleSave} loading={loading} icon={<PlusOutlined />}>
                      保存并继续分配
                    </Button>
                    <Button type="default" size="large" onClick={handleSaveAll} loading={loading} icon={<SaveOutlined />}>
                      保存全部
                    </Button>
                  </Space>
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
          <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <span style={{ marginRight: 8 }}>分组方式：</span>
              <Select
                value={groupBy}
                onChange={setGroupBy}
                style={{ width: 150 }}
              >
                <Option value="none">不分组</Option>
                <Option value="region">按区域</Option>
                <Option value="city">按城市</Option>
                <Option value="brand">按品牌</Option>
              </Select>
            </div>
            <Button type="primary" onClick={handleBatchAllocate}>
              批量分配
            </Button>
          </div>
          
          {Object.entries(getGroupedData()).map(([groupName, groupData]) => (
            <div key={groupName} style={{ marginBottom: 24 }}>
              <h4 style={{ marginBottom: 12, color: '#1890ff' }}>{groupName}</h4>
              <Table
                columns={[
                  {
                    title: '酒店',
                    dataIndex: 'hotel',
                    key: 'hotel',
                    width: 200
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                        <span>是否分配到酒店</span>
                        <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                          <Checkbox
                            checked={groupData.every(item => item.allocated)}
                            onChange={(e) => {
                              const newData = hotelData.map(item => {
                                if (groupData.some(gd => gd.key === item.key)) {
                                  return { ...item, allocated: e.target.checked }
                                }
                                return item
                              })
                              setHotelData(newData)
                            }}
                            style={{ marginRight: 6 }}
                          />
                          <span style={{ fontSize: '12px', color: '#52c41a' }}>分配到所有酒店</span>
                        </div>
                      </div>
                    ),
                    dataIndex: 'allocated',
                    key: 'allocated',
                    width: 180,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleSwitchChange(record, 'allocated')}
                      />
                    )
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                        <span>允许酒店修改房型信息</span>
                        <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                          <Checkbox
                            checked={groupData.filter(item => item.allocated).length > 0 && 
                                    groupData.filter(item => item.allocated).every(item => item.roomInfoEditable)}
                            onChange={(e) => {
                              const newData = hotelData.map(item => {
                                if (groupData.some(gd => gd.key === item.key) && item.allocated) {
                                  return { ...item, roomInfoEditable: e.target.checked }
                                }
                                return item
                              })
                              setHotelData(newData)
                            }}
                            style={{ marginRight: 6 }}
                          />
                          <span style={{ fontSize: '12px', color: '#52c41a' }}>全部酒店可以修改</span>
                        </div>
                      </div>
                    ),
                    dataIndex: 'roomInfoEditable',
                    key: 'roomInfoEditable',
                    width: 200,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleSwitchChange(record, 'roomInfoEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  }
                ]}
                dataSource={groupData}
                pagination={false}
                bordered
                size="middle"
                rowKey="key"
              />
            </div>
          ))}
          
          <div style={{ marginTop: 16, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => navigate('/group-management/group-room-type')}>
                取消
              </Button>
              <Button type="primary" onClick={handleAllocateToHotel} loading={loading} icon={<SaveOutlined />}>
                保存分配设置
              </Button>
            </Space>
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
      
      <Modal
        title="批量分配"
        open={batchModalVisible}
        onOk={handleBatchConfirm}
        onCancel={handleBatchCancel}
      >
        <div style={{ marginBottom: 16 }}>
          <div style={{ marginBottom: 8, fontWeight: 500 }}>选择酒店：</div>
          <Checkbox.Group onChange={setSelectedHotels}>
            <Space direction="vertical" wrap>
              {hotelData.map(hotel => (
                <Checkbox key={hotel.key} value={hotel.key}>
                  {hotel.hotel}
                </Checkbox>
              ))}
            </Space>
          </Checkbox.Group>
        </div>
        <div>
          <div style={{ marginBottom: 8, fontWeight: 500 }}>选择权限：</div>
          <Checkbox.Group onChange={setSelectedLimits}>
            <Space wrap>
              <Checkbox value="roomInfoEditable">允许酒店修改房型信息</Checkbox>
            </Space>
          </Checkbox.Group>
        </div>
      </Modal>
    </div>
  )
}

export default AddGroupRoomType

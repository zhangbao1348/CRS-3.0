import React, { useState, useEffect } from 'react'
import { Button, Input, Table, Form, Select, Upload, Space, Spin, Tabs, Checkbox, Card, Row, Col, message } from 'antd'
import { PlusOutlined, EditOutlined, UploadOutlined, SearchOutlined, ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useHotelContext } from '../../contexts/HotelContext.jsx'
import api, { groupFacilityApi } from '../../utils/api'
import axios from 'axios'

const { Option } = Select

const RoomType = () => {
  const [roomTypes, setRoomTypes] = useState([])
  const [filteredRoomTypes, setFilteredRoomTypes] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchForm] = Form.useForm()
  const [editForm] = Form.useForm()
  const [selectedRoomType, setSelectedRoomType] = useState(null)
  const [isEditMode, setIsEditMode] = useState(false)
  const [viewMode, setViewMode] = useState('list') // 'list' | 'edit'
  const [activeTab, setActiveTab] = useState('1')
  const [saving, setSaving] = useState(false)

  // 房型设施相关
  const [groupFacilities, setGroupFacilities] = useState([])
  const [roomFacilities, setRoomFacilities] = useState({})

  const { selectedHotel, selectedHotelId } = useHotelContext()

  // 房型设施分类（与集团设施管理中 room_type 范围的分类一致）
  const facilityCategories = [
    { key: '清洁服务', label: '清洁服务' },
    { key: '网络与通讯', label: '网络与通讯' },
    { key: '客房布局和家具', label: '客房布局和家具' },
    { key: '洗浴用品', label: '洗浴用品' },
    { key: '食品饮品', label: '食品饮品' },
    { key: '卫浴设施', label: '卫浴设施' },
    { key: '媒体科技', label: '媒体科技' },
    { key: '客房设施', label: '客房设施' },
    { key: '室外景观', label: '室外景观' },
    { key: '便利设施', label: '便利设施' }
  ]

  // 获取房型列表
  const fetchRoomTypes = async () => {
    if (!selectedHotelId) { setRoomTypes([]); setFilteredRoomTypes([]); return }
    try {
      setLoading(true)
      const response = await axios.get(`/api/hotel-room-types/hotel/${selectedHotelId}`)
      const responseData = response.data
      const roomTypeList = responseData.success ? (responseData.data || []) : (Array.isArray(responseData) ? responseData : [])
      const activeRoomTypes = roomTypeList.filter(item => item.status === 'active')
      const data = activeRoomTypes.map(item => ({
        id: item.id,
        code: item.roomTypeCode,
        name: item.roomTypeName,
        englishName: item.englishName || '',
        roomQuantity: item.totalRooms || 0,
        area: item.area || 0,
        floor: item.floor || '',
        windowType: item.windowType || '',
        maxAdults: item.maxOccupancy || 0,
        maxChildren: item.maxChildren || 0,
        bedType: item.bedType || '',
        image: item.image || '',
        status: item.status === 'active' ? '启用' : '停用'
      }))
      setRoomTypes(data)
      setFilteredRoomTypes(data)
    } catch (error) {
      console.error('获取房型数据失败:', error)
      setRoomTypes([])
      setFilteredRoomTypes([])
      message.error('获取房型数据失败')
    } finally {
      setLoading(false)
    }
  }

  // 获取房型设施（scope=room_type）
  const fetchGroupFacilities = async () => {
    try {
      const facilities = await groupFacilityApi.getAllGroupFacilities({ params: { scope: 'room_type' } })
      setGroupFacilities(Array.isArray(facilities) ? facilities : [])
    } catch (err) {
      console.error('获取房型设施失败:', err)
      setGroupFacilities([])
    }
  }

  useEffect(() => { fetchRoomTypes() }, [selectedHotelId])
  useEffect(() => { fetchGroupFacilities() }, [])

  // 搜索
  const handleSearch = (values) => {
    let result = [...roomTypes]
    if (values.code) result = result.filter(item => item.code.includes(values.code))
    if (values.name) result = result.filter(item => item.name.includes(values.name))
    setFilteredRoomTypes(result)
  }

  // 新增
  const handleAdd = () => {
    setIsEditMode(false)
    setSelectedRoomType(null)
    editForm.resetFields()
    setRoomFacilities({})
    setActiveTab('1')
    setViewMode('edit')
  }

  // 编辑
  const handleEdit = (record) => {
    setIsEditMode(true)
    setSelectedRoomType(record)
    editForm.setFieldsValue({
      code: record.code,
      name: record.name,
      englishName: record.englishName || '',
      roomQuantity: record.roomQuantity || 0,
      area: record.area || '',
      floor: record.floor || '',
      maxAdults: record.maxAdults || 0,
      maxChildren: record.maxChildren || 0,
      windowType: record.windowType || undefined,
      bedType: record.bedType || undefined
    })
    // 加载已有房型设施
    loadRoomFacilities(record.id)
    setActiveTab('1')
    setViewMode('edit')
  }

  // 加载房型设施
  const loadRoomFacilities = async (roomTypeId) => {
    try {
      const res = await api.get('/room-type-facilities', { params: { roomTypeId } })
      const list = res?.data || []
      const grouped = {}
      list.forEach(f => {
        // 找到对应的分类 key
        const cat = facilityCategories.find(c => c.label === f.facilityType)
        if (cat) {
          if (!grouped[cat.key]) grouped[cat.key] = []
          grouped[cat.key].push(f.facilityCode)
        }
      })
      setRoomFacilities(grouped)
    } catch (err) {
      console.error('加载房型设施失败:', err)
      setRoomFacilities({})
    }
  }

  // 返回列表
  const handleBack = () => {
    setViewMode('list')
    setSelectedRoomType(null)
    editForm.resetFields()
  }

  // 保存基础信息
  const handleSaveBasicInfo = async () => {
    try {
      const values = await editForm.validateFields()
      setSaving(true)
      // 映射前端字段名到后端实体字段名
      const submitData = {
        roomTypeCode: values.code,
        roomTypeName: values.name,
        englishName: values.englishName || '',
        totalRooms: values.roomQuantity ? parseInt(values.roomQuantity) : null,
        area: values.area ? parseFloat(values.area) : null,
        floor: values.floor || '',
        maxOccupancy: values.maxAdults ? parseInt(values.maxAdults) : null,
        maxChildren: values.maxChildren ? parseInt(values.maxChildren) : null,
        windowType: values.windowType || '',
        bedType: values.bedType || '',
        hotelId: selectedHotelId
      }
      if (isEditMode) {
        await axios.put(`/api/hotel-room-types/${selectedRoomType.id}`, submitData)
        message.success('基础信息保存成功')
      } else {
        const response = await axios.post('/api/hotel-room-types', { ...submitData, status: 'active' })
        setIsEditMode(true)
        setSelectedRoomType({ ...values, id: response.data.id })
        message.success('房型新增成功')
      }
      await fetchRoomTypes()
    } catch (error) {
      if (error.errorFields) return
      message.error('操作失败: ' + (error.response?.data?.error || error.message || '网络错误'))
    } finally {
      setSaving(false)
    }
  }

  // 保存设施信息
  const handleSaveFacilities = async () => {
    if (!isEditMode || !selectedRoomType) {
      message.warning('请先保存基础信息')
      return
    }
    try {
      setSaving(true)
      // 收集所有勾选的设施
      const facilityList = []
      facilityCategories.forEach(cat => {
        const codes = roomFacilities[cat.key] || []
        codes.forEach(code => {
          const facility = groupFacilities.find(f => f.facilityCode === code)
          if (facility) {
            facilityList.push({
              facilityType: facility.facilityType,
              facilityName: facility.facilityName,
              facilityCode: facility.facilityCode
            })
          }
        })
      })
      await api.post('/room-type-facilities/batch', {
        roomTypeId: selectedRoomType.id,
        hotelId: selectedHotelId,
        hotelCode: selectedHotel,
        roomTypeCode: selectedRoomType.code,
        facilities: facilityList
      })
      message.success('房型设施保存成功')
    } catch (err) {
      console.error('保存设施失败:', err)
      message.error('保存设施失败')
    } finally {
      setSaving(false)
    }
  }

  // 设施勾选变更
  const handleFacilityChange = (category, checkedValues) => {
    setRoomFacilities(prev => ({ ...prev, [category]: checkedValues }))
  }

  // 按分类获取设施列表
  const getFacilitiesByType = (typeLabel) => {
    return groupFacilities.filter(f => f.facilityType === typeLabel && f.available !== false)
  }

  // 列配置
  const columns = [
    { title: '代码', dataIndex: 'code', key: 'code', width: 120 },
    { title: '名称', dataIndex: 'name', key: 'name', width: 150 },
    { title: '英文名称', dataIndex: 'englishName', key: 'englishName', width: 180 },
    { title: '房型数量', dataIndex: 'roomQuantity', key: 'roomQuantity', width: 100 },
    { title: '面积', dataIndex: 'area', key: 'area', width: 100, render: v => v ? `${v} ㎡` : '-' },
    { title: '楼层', dataIndex: 'floor', key: 'floor', width: 80 },
    { title: '窗型', dataIndex: 'windowType', key: 'windowType', width: 80 },
    { title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: s => <span style={{ color: s === '启用' ? '#52c41a' : '#ff4d4f', fontWeight: 500 }}>{s}</span>
    },
    { title: '操作', key: 'action', width: 80,
      render: (_, record) => <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
    }
  ]

  // ========== 列表视图 ==========
  if (viewMode === 'list') {
    return (
      <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh' }}>
        <h1 className="page-title" style={{ marginBottom: 24 }}>房型管理</h1>
        <Card bordered={false} style={{ marginBottom: 16 }}>
          <Form form={searchForm} layout="inline" onFinish={handleSearch}>
            <Form.Item name="code" label="代码"><Input placeholder="房型代码" style={{ width: 160 }} /></Form.Item>
            <Form.Item name="name" label="名称"><Input placeholder="房型名称" style={{ width: 200 }} /></Form.Item>
            <Form.Item><Button type="primary" htmlType="submit" icon={<SearchOutlined />}>查询</Button></Form.Item>
            <Form.Item><Button onClick={() => { searchForm.resetFields(); setFilteredRoomTypes(roomTypes) }}>重置</Button></Form.Item>
            <Form.Item><Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增房型</Button></Form.Item>
          </Form>
        </Card>
        <Card bordered={false}>
          <Spin spinning={loading}>
            <Table columns={columns} dataSource={filteredRoomTypes} rowKey="id"
              pagination={{ pageSize: 10, showSizeChanger: true, showTotal: (t, r) => `${r[0]}-${r[1]} 共 ${t} 条` }}
              size="middle" locale={{ emptyText: '暂无房型数据' }} />
          </Spin>
        </Card>
      </div>
    )
  }

  // ========== 编辑视图 ==========
  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh' }}>
      <h1 className="page-title" style={{ marginBottom: 24 }}>{isEditMode ? '编辑房型' : '新增房型'}</h1>

      <Card bordered={false}>
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={[
          {
            key: '1',
            label: '基础信息',
            children: (
              <div>
                <Form form={editForm} layout="vertical" style={{ maxWidth: 800 }}>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item name="code" label="房型代码" rules={[
                        { required: true, message: '请输入房型代码' },
                        { pattern: /^[A-Za-z0-9_]+$/, message: '只能包含英文字母、数字和下划线' }
                      ]}>
                        <Input placeholder="输入房型代码" disabled={isEditMode} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="name" label="房型中文名称" rules={[{ required: true, message: '请输入房型中文名称' }]}>
                        <Input placeholder="输入房型中文名称" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="englishName" label="房型英文名称">
                        <Input placeholder="输入房型英文名称" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="roomQuantity" label="房型数量" rules={[{ required: true, message: '请输入房型数量' }]}>
                        <Input type="number" placeholder="输入房型数量" min={0} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="area" label="房型面积（㎡）">
                        <Input type="number" placeholder="输入面积" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="floor" label="所在楼层">
                        <Input placeholder="输入楼层" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="maxAdults" label="最大入住成人数" rules={[{ required: true, message: '请输入' }]}>
                        <Input type="number" placeholder="输入成人数" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="maxChildren" label="最大入住儿童数">
                        <Input type="number" placeholder="输入儿童数" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="windowType" label="窗型" rules={[{ required: true, message: '请选择窗型' }]}>
                        <Select placeholder="请选择窗型">
                          <Option value="有窗">有窗</Option>
                          <Option value="无窗">无窗</Option>
                        </Select>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="bedType" label="床型" rules={[{ required: true, message: '请选择床型' }]}>
                        <Select placeholder="请选择床型">
                          <Option value="1张1.8米大床">1张1.8米大床</Option>
                          <Option value="2张1.2米单人床">2张1.2米单人床</Option>
                          <Option value="1张1.5米大床">1张1.5米大床</Option>
                          <Option value="3张1.2米单人床">3张1.2米单人床</Option>
                        </Select>
                      </Form.Item>
                    </Col>
                  </Row>
                </Form>
                <div style={{ borderTop: '1px solid #f0f0f0', paddingTop: 16, marginTop: 16, display: 'flex', justifyContent: 'flex-end' }}>
                  <Space>
                    <Button icon={<ArrowLeftOutlined />} onClick={handleBack}>返回列表</Button>
                    <Button type="primary" icon={<SaveOutlined />} loading={saving} onClick={handleSaveBasicInfo}>保存基础信息</Button>
                  </Space>
                </div>
              </div>
            )
          },
          {
            key: '2',
            label: '房型设施',
            children: (
              <div>
                <div style={{ maxWidth: 800 }}>
                  {facilityCategories.map(cat => {
                    const items = getFacilitiesByType(cat.label)
                    if (items.length === 0) return null
                    return (
                      <div key={cat.key} style={{ marginBottom: 24 }}>
                        <div style={{ fontWeight: 600, marginBottom: 8, fontSize: 14, color: '#1890ff' }}>{cat.label}</div>
                        <Checkbox.Group
                          value={roomFacilities[cat.key] || []}
                          onChange={(vals) => handleFacilityChange(cat.key, vals)}
                        >
                          <Space wrap>
                            {items.map(f => (
                              <Checkbox key={f.facilityCode} value={f.facilityCode}>
                                {f.facilityName}（{f.facilityCode}）
                              </Checkbox>
                            ))}
                          </Space>
                        </Checkbox.Group>
                      </div>
                    )
                  })}
                  {groupFacilities.length === 0 && (
                    <div style={{ textAlign: 'center', color: '#999', padding: 40 }}>
                      暂无房型设施数据，请先在集团设施管理中添加适用范围为"房型设施"的设施
                    </div>
                  )}
                </div>
                <div style={{ borderTop: '1px solid #f0f0f0', paddingTop: 16, marginTop: 16, display: 'flex', justifyContent: 'flex-end' }}>
                  <Space>
                    <Button icon={<ArrowLeftOutlined />} onClick={handleBack}>返回列表</Button>
                    <Button type="primary" icon={<SaveOutlined />} onClick={handleSaveFacilities}>保存设施信息</Button>
                  </Space>
                </div>
              </div>
            )
          }
        ]} />
      </Card>
    </div>
  )
}

export default RoomType

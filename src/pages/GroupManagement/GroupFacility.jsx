import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, Form, Modal, message, Spin } from 'antd'
import { PlusOutlined, EditOutlined, SaveOutlined } from '@ant-design/icons'
import axios from 'axios'

const { Option } = Select

// 适用范围选项
const scopeOptions = [
  { label: '酒店设施', value: 'hotel' },
  { label: '房型设施', value: 'room_type' }
]

// 按适用范围分组的设施分类
const facilityTypesByScope = {
  hotel: [
    { label: '交通服务', value: '交通服务' },
    { label: '餐饮服务', value: '餐饮服务' },
    { label: '清洁服务', value: '清洁服务' },
    { label: '休闲娱乐', value: '休闲娱乐' },
    { label: '商务服务', value: '商务服务' },
    { label: '前台服务', value: '前台服务' }
  ],
  room_type: [
    { label: '清洁服务', value: '清洁服务' },
    { label: '网络与通讯', value: '网络与通讯' },
    { label: '客房布局和家具', value: '客房布局和家具' },
    { label: '洗浴用品', value: '洗浴用品' },
    { label: '食品饮品', value: '食品饮品' },
    { label: '卫浴设施', value: '卫浴设施' },
    { label: '媒体科技', value: '媒体科技' },
    { label: '客房设施', value: '客房设施' },
    { label: '室外景观', value: '室外景观' },
    { label: '便利设施', value: '便利设施' }
  ]
}

// 所有分类的合集（用于列表显示）
const allFacilityTypes = [
  ...facilityTypesByScope.hotel,
  ...facilityTypesByScope.room_type.filter(t => !facilityTypesByScope.hotel.some(h => h.value === t.value))
]

const GroupFacility = () => {
  const [facilities, setFacilities] = useState([])
  const [filteredFacilities, setFilteredFacilities] = useState([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [isEditing, setIsEditing] = useState(false)
  const [currentFacility, setCurrentFacility] = useState(null)
  const [form] = Form.useForm()
  const [searchForm] = Form.useForm()
  const [currentScope, setCurrentScope] = useState('hotel')
  const [searchScope, setSearchScope] = useState(null)

  const fetchFacilities = async () => {
    setLoading(true)
    try {
      const response = await axios.get('/api/group-facilities')
      setFacilities(response.data)
      setFilteredFacilities(response.data)
    } catch (error) {
      console.error('获取设施列表失败:', error)
      message.error('获取设施列表失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchFacilities() }, [])

  // 搜索过滤
  const handleSearch = (values) => {
    let result = [...facilities]
    if (values.searchScope) {
      result = result.filter(f => f.scope === values.searchScope)
    }
    if (values.searchType) {
      result = result.filter(f => f.facilityType === values.searchType)
    }
    if (values.searchName) {
      result = result.filter(f => f.facilityName && f.facilityName.includes(values.searchName))
    }
    if (values.searchCode) {
      result = result.filter(f => f.facilityCode && f.facilityCode.includes(values.searchCode))
    }
    setFilteredFacilities(result)
  }

  const handleSearchReset = () => {
    searchForm.resetFields()
    setSearchScope(null)
    setFilteredFacilities(facilities)
  }

  const handleSearchScopeChange = (value) => {
    setSearchScope(value)
    searchForm.setFieldsValue({ searchType: undefined })
  }

  const handleAddFacility = () => {
    setIsEditing(false)
    setCurrentFacility(null)
    setCurrentScope('hotel')
    form.resetFields()
    form.setFieldsValue({ scope: 'hotel' })
    setModalVisible(true)
  }

  const handleEditFacility = (record) => {
    setIsEditing(true)
    setCurrentFacility(record)
    const scope = record.scope || 'hotel'
    setCurrentScope(scope)
    form.setFieldsValue({
      scope,
      facilityType: record.facilityType,
      facilityName: record.facilityName,
      facilityCode: record.facilityCode,
      description: record.description,
      available: record.available
    })
    setModalVisible(true)
  }

  const handleScopeChange = (value) => {
    setCurrentScope(value)
    // 清空分类选择，因为分类选项变了
    form.setFieldsValue({ facilityType: undefined })
  }

  const handleSubmit = async (values) => {
    try {
      if (isEditing) {
        const response = await axios.put(`/api/group-facilities/${currentFacility.id}`, values)
        const updated = facilities.map(f => f.id === currentFacility.id ? response.data : f)
        setFacilities(updated)
        setFilteredFacilities(updated)
        message.success('编辑设施成功')
      } else {
        const response = await axios.post('/api/group-facilities', values)
        const updated = [...facilities, response.data]
        setFacilities(updated)
        setFilteredFacilities(updated)
        message.success('新增设施成功')
      }
      setModalVisible(false)
    } catch (error) {
      console.error('保存设施失败:', error)
      message.error('保存设施失败')
    }
  }

  const columns = [
    { title: '设施ID', dataIndex: 'id', key: 'id', width: 70 },
    { title: '适用范围', dataIndex: 'scope', key: 'scope', width: 100,
      render: t => { const o = scopeOptions.find(i => i.value === t); return o ? o.label : (t || '酒店设施') }
    },
    { title: '设施分类', dataIndex: 'facilityType', key: 'facilityType', width: 130 },
    { title: '设施名称', dataIndex: 'facilityName', key: 'facilityName', width: 150 },
    { title: '设施代码', dataIndex: 'facilityCode', key: 'facilityCode', width: 150 },
    { title: '设施描述', dataIndex: 'description', key: 'description', width: 200, ellipsis: true },
    { title: '状态', dataIndex: 'available', key: 'available', width: 80,
      render: t => <span style={{ color: t ? '#52c41a' : '#faad14', fontWeight: 500 }}>{t ? '可用' : '不可用'}</span>
    },
    { title: '操作', key: 'action', width: 80,
      render: (_, record) => <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditFacility(record)}>编辑</Button>
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">集团设施管理</h1>

      <Card bordered={false} style={{ marginBottom: 16 }}>
        <Form form={searchForm} layout="inline" onFinish={handleSearch}>
          <Form.Item name="searchScope" label="适用范围">
            <Select placeholder="全部" allowClear style={{ width: 120 }} onChange={handleSearchScopeChange}>
              {scopeOptions.map(item => <Option key={item.value} value={item.value}>{item.label}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="searchType" label="设施分类">
            <Select placeholder="全部" allowClear style={{ width: 150 }}>
              {(searchScope ? (facilityTypesByScope[searchScope] || []) : allFacilityTypes).map(item =>
                <Option key={item.value} value={item.value}>{item.label}</Option>
              )}
            </Select>
          </Form.Item>
          <Form.Item name="searchName" label="设施名称">
            <Input placeholder="模糊搜索" style={{ width: 140 }} allowClear />
          </Form.Item>
          <Form.Item name="searchCode" label="设施代码">
            <Input placeholder="模糊搜索" style={{ width: 140 }} allowClear />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">查询</Button>
          </Form.Item>
          <Form.Item>
            <Button onClick={handleSearchReset}>重置</Button>
          </Form.Item>
        </Form>
      </Card>

      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddFacility}>新增设施</Button>
      </div>
      <Spin spinning={loading}>
        <Table columns={columns} dataSource={filteredFacilities} rowKey="id"
          pagination={{ pageSize: 10, showSizeChanger: true, showQuickJumper: true, showTotal: (t, r) => `${r[0]}-${r[1]} 共 ${t} 条` }}
          scroll={{ x: 900 }} locale={{ emptyText: '暂无设施数据' }} />
      </Spin>

      <Modal title={isEditing ? '编辑设施' : '新增设施'} open={modalVisible}
        onCancel={() => setModalVisible(false)} footer={null} width={600} destroyOnClose>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Row gutter={[16, 0]}>
            <Col span={12}>
              <Form.Item name="scope" label="适用范围" rules={[{ required: true, message: '请选择适用范围' }]}>
                <Select placeholder="请选择适用范围" onChange={handleScopeChange}>
                  {scopeOptions.map(item => <Option key={item.value} value={item.value}>{item.label}</Option>)}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="facilityType" label="设施分类" rules={[{ required: true, message: '请选择设施分类' }]}>
                <Select placeholder="请先选择适用范围">
                  {(facilityTypesByScope[currentScope] || []).map(item =>
                    <Option key={item.value} value={item.value}>{item.label}</Option>
                  )}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="facilityName" label="设施名称" rules={[{ required: true, message: '请输入设施名称' }]}>
                <Input placeholder="请输入设施名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="facilityCode" label="设施代码" rules={[
                { required: true, message: '请输入设施代码' },
                { pattern: /^[A-Za-z0-9_]+$/, message: '只能包含英文字母、数字和下划线' }
              ]}>
                <Input placeholder="请输入设施代码" disabled={isEditing} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="description" label="设施描述">
                <Input.TextArea placeholder="请输入设施描述" rows={3} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="available" label="状态" initialValue={true}>
                <Select placeholder="请选择状态">
                  <Option value={true}>可用</Option>
                  <Option value={false}>不可用</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={24} style={{ textAlign: 'right', marginTop: 16 }}>
              <Space>
                <Button onClick={() => setModalVisible(false)}>取消</Button>
                <Button type="primary" icon={<SaveOutlined />} htmlType="submit">保存</Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  )
}

export default GroupFacility

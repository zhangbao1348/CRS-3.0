import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, Form, Modal, message, Spin, Popconfirm } from 'antd'
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  SaveOutlined, 
  CloseCircleOutlined
} from '@ant-design/icons'
import axios from 'axios'

const { Option } = Select

const GroupFacility = () => {
  // 状态管理
  const [facilities, setFacilities] = useState([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [isEditing, setIsEditing] = useState(false)
  const [currentFacility, setCurrentFacility] = useState(null)
  const [form] = Form.useForm()
  
  // 设施分类选项
  const facilityTypes = [
    { label: '交通服务', value: 'transportation' },
    { label: '餐饮服务', value: 'dining' },
    { label: '清洁服务', value: 'cleaning' }
  ]
  
  // 获取设施列表
  const fetchFacilities = async () => {
    setLoading(true)
    try {
      // 调用后端API获取所有集团设施
      const response = await axios.get('http://localhost:8080/api/group-facilities')
      setFacilities(response.data)
    } catch (error) {
      console.error('获取设施列表失败:', error)
      message.error('获取设施列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }
  
  // 初始化时获取设施列表
  useEffect(() => {
    fetchFacilities()
  }, [])
  
  // 处理新增设施
  const handleAddFacility = () => {
    setIsEditing(false)
    setCurrentFacility(null)
    form.resetFields()
    setModalVisible(true)
  }
  
  // 处理编辑设施
  const handleEditFacility = (record) => {
    setIsEditing(true)
    setCurrentFacility(record)
    form.setFieldsValue({
      facilityType: record.facilityType,
      facilityName: record.facilityName,
      facilityCode: record.facilityCode,
      description: record.description,
      available: record.available
    })
    setModalVisible(true)
  }
  
  // 处理删除设施
  const handleDeleteFacility = async (id) => {
    try {
      // 调用后端删除API
      await axios.delete(`http://localhost:8080/api/group-facilities/${id}`)
      
      // 更新前端数据
      setFacilities(facilities.filter(facility => facility.id !== id))
      message.success('删除设施成功')
    } catch (error) {
      console.error('删除设施失败:', error)
      message.error('删除设施失败，请稍后重试')
    }
  }
  
  // 处理表单提交
  const handleSubmit = async (values) => {
    try {
      if (isEditing) {
        // 编辑设施
        const response = await axios.put(`http://localhost:8080/api/group-facilities/${currentFacility.id}`, values)
        
        // 更新前端数据
        const updatedFacilities = facilities.map(facility => 
          facility.id === currentFacility.id ? response.data : facility
        )
        setFacilities(updatedFacilities)
        message.success('编辑设施成功')
      } else {
        // 新增设施
        const response = await axios.post('http://localhost:8080/api/group-facilities', values)
        
        // 更新前端数据
        setFacilities([...facilities, response.data])
        message.success('新增设施成功')
      }
      setModalVisible(false)
    } catch (error) {
      console.error('保存设施失败:', error)
      message.error('保存设施失败，请稍后重试')
    }
  }
  
  // 列配置
  const columns = [
    {
      title: '设施ID',
      dataIndex: 'id',
      key: 'id',
      width: 80
    },
    {
      title: '设施分类',
      dataIndex: 'facilityType',
      key: 'facilityType',
      width: 120,
      render: (text) => {
        const type = facilityTypes.find(item => item.value === text)
        return type ? type.label : text
      }
    },
    {
      title: '设施名称',
      dataIndex: 'facilityName',
      key: 'facilityName',
      width: 150
    },
    {
      title: '设施代码',
      dataIndex: 'facilityCode',
      key: 'facilityCode',
      width: 150
    },
    {
      title: '设施描述',
      dataIndex: 'description',
      key: 'description',
      width: 200,
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'available',
      key: 'available',
      width: 80,
      render: (text) => (
        <span style={{ 
          color: text ? '#52c41a' : '#faad14',
          fontWeight: 500
        }}>
          {text ? '可用' : '不可用'}
        </span>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditFacility(record)}>编辑</Button>
          <Popconfirm
            title="确定要删除这个设施吗？"
            onConfirm={() => handleDeleteFacility(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" icon={<DeleteOutlined />} danger>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ]
  
  return (
    <div className="fade-in">
      <h1 className="page-title">
        集团设施管理
      </h1>
      
      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddFacility}>
          新增设施
        </Button>
      </div>
      
      {/* 设施列表表格 */}
      <Spin spinning={loading}>
        <Table
          columns={columns}
          dataSource={facilities}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
          }}
          scroll={{ x: 800 }}
          locale={{ emptyText: '暂无设施数据' }}
        />
      </Spin>
      
      {/* 新增/编辑设施模态框 */}
      <Modal
        title={isEditing ? '编辑设施' : '新增设施'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Row gutter={[16, 16]}>
            <Col span={24}>
              <Form.Item
                name="facilityType"
                label="设施分类"
                rules={[{ required: true, message: '请选择设施分类' }]}
              >
                <Select placeholder="请选择设施分类">
                  {facilityTypes.map(item => (
                    <Option key={item.value} value={item.value}>{item.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="facilityName"
                label="设施名称"
                rules={[{ required: true, message: '请输入设施名称' }]}
              >
                <Input placeholder="请输入设施名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="facilityCode"
                label="设施代码"
                rules={[{ required: true, message: '请输入设施代码' }]}
              >
                <Input placeholder="请输入设施代码" />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                name="description"
                label="设施描述"
              >
                <Input.TextArea placeholder="请输入设施描述" rows={3} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                name="available"
                label="状态"
              >
                <Select placeholder="请选择状态">
                  <Option value={true}>可用</Option>
                  <Option value={false}>不可用</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={24} style={{ textAlign: 'right', marginTop: 24 }}>
              <Space>
                <Button size="large" onClick={() => setModalVisible(false)}>
                  取消
                </Button>
                <Button type="primary" size="large" icon={<SaveOutlined />} htmlType="submit">
                  保存
                </Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
)
}

export default GroupFacility
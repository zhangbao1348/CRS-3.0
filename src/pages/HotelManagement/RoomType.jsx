import React, { useState } from 'react'
import { Button, Input, Table, Modal, Form, Select, Upload, Space } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, UploadOutlined, SearchOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { Search } = Input
const { Option } = Select
const { Dragger } = Upload

// 模拟房型数据
const mockRoomTypes = [
  {
    id: 1,
    code: 'STD',
    name: '标准间',
    englishName: 'Standard Room',
    roomQuantity: 10,
    area: 25,
    floor: '3-5',
    windowType: '有窗',
    maxAdults: 2,
    maxChildren: 0,
    bedType: '2张1.2米单人床',
    image: 'https://via.placeholder.com/200x150'
  },
  {
    id: 2,
    code: 'DLX',
    name: '豪华间',
    englishName: 'Deluxe Room',
    roomQuantity: 8,
    area: 35,
    floor: '6-8',
    windowType: '有窗',
    maxAdults: 2,
    maxChildren: 1,
    bedType: '1张1.8米大床',
    image: 'https://via.placeholder.com/200x150'
  },
  {
    id: 3,
    code: 'SUT',
    name: '套房',
    englishName: 'Suite',
    roomQuantity: 5,
    area: 50,
    floor: '9-10',
    windowType: '有窗',
    maxAdults: 3,
    maxChildren: 1,
    bedType: '1张2米大床+1张1.2米单人床',
    image: 'https://via.placeholder.com/200x150'
  },
  {
    id: 4,
    code: 'TWN',
    name: '双床间',
    englishName: 'Twin Room',
    roomQuantity: 12,
    area: 28,
    floor: '3-5',
    windowType: '有窗',
    maxAdults: 2,
    maxChildren: 0,
    bedType: '2张1.2米单人床',
    image: 'https://via.placeholder.com/200x150'
  }
]

const RoomType = () => {
  // 状态管理
  const [roomTypes, setRoomTypes] = useState(mockRoomTypes)
  const [filteredRoomTypes, setFilteredRoomTypes] = useState(mockRoomTypes)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isEditMode, setIsEditMode] = useState(false)
  const [form] = Form.useForm()
  const [selectedRoomType, setSelectedRoomType] = useState(null)
  
  // 搜索功能
  const handleSearch = (values) => {
    const { code, name } = values
    let result = [...roomTypes]
    
    if (code) {
      result = result.filter(item => item.code.includes(code))
    }
    
    if (name) {
      result = result.filter(item => item.name.includes(name))
    }
    
    setFilteredRoomTypes(result)
  }
  
  // 打开新增模态框
  const handleAdd = () => {
    setIsEditMode(false)
    setSelectedRoomType(null)
    form.resetFields()
    setIsModalVisible(true)
  }
  
  // 打开编辑模态框
  const handleEdit = (record) => {
    setIsEditMode(true)
    setSelectedRoomType(record)
    form.setFieldsValue(record)
    setIsModalVisible(true)
  }
  
  // 删除房型
  const handleDelete = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除房型 ${record.code} - ${record.name} 吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        setRoomTypes(roomTypes.filter(item => item.id !== record.id))
        setFilteredRoomTypes(filteredRoomTypes.filter(item => item.id !== record.id))
      }
    })
  }
  
  // 保存房型
  const handleSave = () => {
    form.validateFields().then(values => {
      if (isEditMode) {
        // 编辑模式
        const updatedRoomTypes = roomTypes.map(item => {
          if (item.id === selectedRoomType.id) {
            return { ...item, ...values }
          }
          return item
        })
        setRoomTypes(updatedRoomTypes)
        setFilteredRoomTypes(updatedRoomTypes)
      } else {
        // 新增模式
        const newRoomType = {
          id: Date.now(),
          ...values
        }
        setRoomTypes([...roomTypes, newRoomType])
        setFilteredRoomTypes([...filteredRoomTypes, newRoomType])
      }
      setIsModalVisible(false)
      form.resetFields()
    })
  }
  
  // 表格列配置
  const columns = [
    {
      title: '代码',
      dataIndex: 'code',
      key: 'code',
      width: 100
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: 150
    },
    {
      title: '英文名称',
      dataIndex: 'englishName',
      key: 'englishName',
      width: 180
    },
    {
      title: '房型数量',
      dataIndex: 'roomQuantity',
      key: 'roomQuantity',
      width: 120
    },
    {
      title: '面积',
      dataIndex: 'area',
      key: 'area',
      width: 100,
      render: (area) => `${area} 平方米`
    },
    {
      title: '楼层',
      dataIndex: 'floor',
      key: 'floor',
      width: 100
    },
    {
      title: '窗型',
      dataIndex: 'windowType',
      key: 'windowType',
      width: 100
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Button type="text" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="text" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
            删除
          </Button>
        </Space>
      )
    }
  ]
  
  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh' }}>
      <h1 className="page-title" style={{ marginBottom: 24 }}>房型管理</h1>
      
      {/* 查询表单 */}
      <div style={{ marginBottom: 24, padding: 24, background: '#fff', borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <Form layout="inline" onFinish={handleSearch} style={{ width: '100%' }}>
          <Form.Item name="code" label="代码" style={{ marginRight: 24 }}>
            <Input placeholder="输入房型代码" style={{ width: 200 }} />
          </Form.Item>
          <Form.Item name="name" label="名称" style={{ marginRight: 24 }}>
            <Input placeholder="输入房型名称" style={{ width: 250 }} />
          </Form.Item>
          <Form.Item style={{ marginRight: 16 }}>
            <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
              查询
            </Button>
          </Form.Item>
          <Form.Item style={{ marginRight: 16 }}>
            <Button type="default" onClick={() => form.resetFields() && setFilteredRoomTypes(roomTypes)}>
              重置
            </Button>
          </Form.Item>
          <Form.Item>
            <Button type="primary" onClick={handleAdd} icon={<PlusOutlined />}>
              新增
            </Button>
          </Form.Item>
        </Form>
      </div>
      
      {/* 房型列表 */}
      <div style={{ background: '#fff', borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <Table
          columns={columns}
          dataSource={filteredRoomTypes}
          rowKey="id"
          pagination={false}
          size="middle"
          style={{ minWidth: 800 }}
        />
      </div>
      
      {/* 新增/编辑模态框 */}
      <Modal
        title={isEditMode ? '编辑房型' : '新增房型'}
        visible={isModalVisible}
        onOk={handleSave}
        onCancel={() => setIsModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="code"
            label="房型代码"
            rules={[{ required: true, message: '请输入房型代码' }]}
          >
            <Input placeholder="输入房型代码" />
          </Form.Item>
          
          <Form.Item
            name="name"
            label="房型中文名称"
            rules={[{ required: true, message: '请输入房型中文名称' }]}
          >
            <Input placeholder="输入房型中文名称" />
          </Form.Item>
          
          <Form.Item
            name="englishName"
            label="房型英文名称"
          >
            <Input placeholder="输入房型英文名称" />
          </Form.Item>
          
          <Form.Item
            name="roomQuantity"
            label="房型数量"
            rules={[{ required: true, message: '请输入房型数量' }]}
          >
            <Input type="number" placeholder="输入房型数量" min={0} step={1} />
          </Form.Item>
          
          <Form.Item
            name="area"
            label="房型面积"
          >
            <Input type="number" placeholder="输入房型面积" />
          </Form.Item>
          
          <Form.Item
            name="floor"
            label="所在楼层"
          >
            <Input placeholder="输入所在楼层" />
          </Form.Item>
          
          <Form.Item
            name="maxAdults"
            label="最大入住成人数"
            rules={[{ required: true, message: '请输入最大入住成人数' }]}
          >
            <Input type="number" placeholder="输入最大入住成人数" />
          </Form.Item>
          
          <Form.Item
            name="maxChildren"
            label="最大入住人数"
          >
            <Input type="number" placeholder="输入最大入住人数" />
          </Form.Item>
          
          <Form.Item
            name="windowType"
            label="窗型"
            rules={[{ required: true, message: '请选择窗型' }]}
          >
            <Select placeholder="请选择窗型">
              <Option value="有窗">有窗</Option>
              <Option value="无窗">无窗</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            name="bedType"
            label="床型"
            rules={[{ required: true, message: '请选择床型' }]}
          >
            <Select placeholder="请选择床型">
              <Option value="1张1.8米大床">1张1.8米大床</Option>
              <Option value="2张1.2米单人床">2张1.2米单人床</Option>
              <Option value="1张1.5米大床">1张1.5米大床</Option>
              <Option value="3张1.2米单人床">3张1.2米单人床</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            name="image"
            label="图片"
          >
            <Dragger
              name="image"
              multiple={false}
              beforeUpload={() => false}
            >
              <p className="ant-upload-drag-icon">
                <UploadOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽文件到此处上传</p>
              <p className="ant-upload-hint">支持单文件上传</p>
            </Dragger>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default RoomType
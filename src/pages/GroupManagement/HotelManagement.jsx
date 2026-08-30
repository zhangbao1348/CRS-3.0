import { useState, useEffect, useMemo } from 'react'
import { App as AntApp, Table, Button, Space, Card, Row, Col, Input, Select, DatePicker, Modal, Form, Spin, Tabs } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  ApartmentOutlined
} from '@ant-design/icons'
import '@wangeditor/editor/dist/css/style.css'
import { hotelApi } from '../../utils/api'
import { useTenantContext } from '../../contexts/TenantContext.jsx'

const { RangePicker } = DatePicker
const { Option } = Select

const HotelManagement = () => {
  const { message, modal } = AntApp.useApp()
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(false)
  const [isViewModalVisible, setIsViewModalVisible] = useState(false)
  const [form] = Form.useForm()
  const [viewForm] = Form.useForm()
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    city: '',
    status: ''
  })
  
  const { selectedTenant } = useTenantContext()
  
  const fetchHotels = async () => {
    setLoading(true)
    try {
      const response = await hotelApi.getHotelsByTenantId(selectedTenant)
      if (response.success) {
        const hotelsData = response.data.map(hotel => ({
          id: hotel.id,
          name: hotel.chineseName,
          code: hotel.hotelCode,
          province: hotel.province,
          city: hotel.city,
          address: hotel.address,
          taxRate: 13,
          status: hotel.status === 'active' ? '正常' : '维护中',
          createdAt: hotel.createdAt ? new Date(hotel.createdAt).toISOString().split('T')[0] : '',
          tenantId: hotel.tenantId
        }))
        setHotels(hotelsData)
      }
    } catch (error) {
      message.error(error?.response?.data?.message || '获取酒店列表失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchHotels()
  }, [selectedTenant])

  const handleSearch = () => {
    form.submit()
  }

  const handleReset = () => {
    setSearchParams({
      name: '',
      code: '',
      city: '',
      status: ''
    })
    form.resetFields()
  }

  const filteredHotels = useMemo(() => hotels.filter(hotel => {
    const name = searchParams.name.trim().toLowerCase()
    const code = searchParams.code.trim().toLowerCase()
    if (name && !String(hotel.name || '').toLowerCase().includes(name)) return false
    if (code && !String(hotel.code || '').toLowerCase().includes(code)) return false
    if (searchParams.city && hotel.city !== searchParams.city) return false
    if (searchParams.status && hotel.status !== searchParams.status) return false
    return true
  }), [hotels, searchParams])

  const handleAddHotel = () => {
    window.location.href = '/group-management/add-hotel'
  }

  const handleViewHotel = (record) => {
    viewForm.setFieldsValue({
      hotelCode: record.code,
      hotelChineseName: record.name,
      hotelProvince: record.province,
      hotelCity: record.city,
      hotelAddress: record.address
    })
    setIsViewModalVisible(true)
  }

  const handleEditHotel = (record) => {
    window.location.href = `/hotel-management/edit-hotel?id=${record.id}`
  }

  const handleDeleteHotel = async (id) => {
    modal.confirm({
      title: '确认删除',
      content: '确定要删除这个酒店吗？',
      onOk: async () => {
        try {
          await hotelApi.deleteHotel(id)
          message.success('酒店已软删除并置为维护中')
          fetchHotels()
        } catch (error) {
          message.error(error?.response?.data?.message || '删除酒店失败，请稍后重试')
        }
      }
    })
  }

  const viewTabItems = [
    {
      key: '1',
      label: '酒店基本信息',
      children: (
        <Card style={{ marginBottom: 24 }}>
          <Form form={viewForm} layout="vertical" style={{ maxWidth: 800 }}>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="hotelCode"
                  label="酒店代码"
                >
                  <Input placeholder="请输入酒店代码" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelChineseName"
                  label="酒店中文名称"
                >
                  <Input placeholder="请输入酒店中文名称" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelProvince"
                  label="酒店所在省份"
                >
                  <Select placeholder="请选择酒店所在省份" disabled>
                    <Option value="江苏省">江苏省</Option>
                    <Option value="浙江省">浙江省</Option>
                    <Option value="上海市">上海市</Option>
                    <Option value="北京市">北京市</Option>
                    <Option value="广东省">广东省</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelCity"
                  label="酒店所在城市"
                >
                  <Select placeholder="请选择酒店所在城市" disabled>
                    <Option value="苏州市">苏州市</Option>
                    <Option value="南京市">南京市</Option>
                    <Option value="杭州市">杭州市</Option>
                    <Option value="上海市">上海市</Option>
                    <Option value="北京市">北京市</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  name="hotelAddress"
                  label="酒店详细地址"
                >
                  <Input placeholder="请输入酒店详细地址" disabled />
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Card>
      )
    }
  ]

  const columns = [
    {
      title: '酒店名称',
      dataIndex: 'name',
      key: 'name',
      width: 200
    },
    {
      title: '酒店编码',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: '省份',
      dataIndex: 'province',
      key: 'province',
      width: 100
    },
    {
      title: '城市',
      dataIndex: 'city',
      key: 'city',
      width: 100
    },
    {
      title: '地址',
      dataIndex: 'address',
      key: 'address',
      ellipsis: true
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 120
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <span style={{ 
          color: status === '正常' ? '#52c41a' : '#faad14',
          fontWeight: 500
        }}>
          {status}
        </span>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewHotel(record)}>查看</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditHotel(record)}>编辑</Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            disabled={record.status !== '正常'}
            onClick={() => handleDeleteHotel(record.id)}
          >
            {record.status === '正常' ? '删除' : '已删除'}
          </Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <ApartmentOutlined />
        酒店管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="酒店名称" 
              prefix={<SearchOutlined />} 
              allowClear 
              value={searchParams.name}
              onChange={(e) => setSearchParams({...searchParams, name: e.target.value})}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="酒店编码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => setSearchParams({...searchParams, code: e.target.value})}
              style={{ 
                height: 32, 
                display: 'flex', 
                alignItems: 'center'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="城市" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.city || undefined}
              onChange={(value) => setSearchParams({...searchParams, city: value})}
            >
              {[...new Set(hotels.map(hotel => hotel.city).filter(Boolean))].sort().map(city => (
                <Option key={city} value={city}>{city}</Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ 
                width: '100%',
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
              value={searchParams.status || undefined}
              onChange={(value) => setSearchParams({...searchParams, status: value})}
            >
              <Option value="正常">正常</Option>
              <Option value="维护中">维护中</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12}>
            <RangePicker 
              placeholder={['创建开始日期', '创建结束日期']} 
              style={{ 
                height: 32,
                display: 'flex',
                alignItems: 'center'
              }}
            />
          </Col>
        </Row>
        <Row style={{ marginTop: 16 }}>
          <Col span={24} style={{ textAlign: 'right' }}>
            <Button 
              type="primary" 
              icon={<SearchOutlined />} 
              onClick={handleSearch}
              style={{ height: 32, marginRight: 8 }}
            >
              搜索
            </Button>
            <Button 
              onClick={handleReset}
              style={{ height: 32 }}
            >
              重置
            </Button>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddHotel}>
          新增酒店
        </Button>
      </div>

      {/* 酒店列表表格 */}
      <Spin spinning={loading}>
        <Table
          columns={columns}
          dataSource={filteredHotels}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
          }}
          scroll={{ x: 1000 }}
          locale={{ emptyText: '暂无酒店数据' }}
        />
      </Spin>

      {/* 查看酒店详情模态框 */}
      <Modal
        title="查看酒店详情"
        open={isViewModalVisible}
        onCancel={() => setIsViewModalVisible(false)}
        footer={null}
        width={1000}
      >
        <Tabs
          defaultActiveKey="1"
          items={viewTabItems}
        />
      </Modal>
    </div>
  )
}

export default HotelManagement

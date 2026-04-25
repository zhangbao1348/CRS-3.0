import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, DatePicker, Modal, Form, message, Spin, Tabs, InputNumber, Checkbox, Upload, Image, Radio } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  ApartmentOutlined,
  CheckCircleOutlined,
  WarningOutlined
} from '@ant-design/icons'
import { Editor } from '@wangeditor/editor-for-react'
import '@wangeditor/editor/dist/css/style.css'
import { hotelApi } from '../../utils/api'
import { useTenantContext } from '../../contexts/TenantContext.jsx'

// 演示模式标志
const DEMO_MODE = false

// 模拟酒店数据
const mockHotels = [
  {
    id: 1,
    name: '南京站红山动物园雅斯阁酒店',
    code: 'NJ001',
    province: '江苏省',
    city: '南京市',
    address: '南京市玄武区红山路1号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-01',
    tenantId: 1
  },
  {
    id: 2,
    name: '上海外滩华尔道夫酒店',
    code: 'SH001',
    province: '上海市',
    city: '上海市',
    address: '上海市黄浦区中山东一路2号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-02',
    tenantId: 1
  },
  {
    id: 3,
    name: '北京国贸大酒店',
    code: 'BJ001',
    province: '北京市',
    city: '北京市',
    address: '北京市朝阳区建国门外大街1号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-03',
    tenantId: 1
  },
  {
    id: 4,
    name: '广州塔君悦酒店',
    code: 'GZ001',
    province: '广东省',
    city: '广州市',
    address: '广州市海珠区阅江西路222号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-04',
    tenantId: 1
  },
  {
    id: 5,
    name: '深圳湾万怡酒店',
    code: 'SZ001',
    province: '广东省',
    city: '深圳市',
    address: '深圳市南山区工业七路3号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-05',
    tenantId: 1
  },
  {
    id: 6,
    name: '杭州西湖索菲特酒店',
    code: 'HZ001',
    province: '浙江省',
    city: '杭州市',
    address: '杭州市西湖区曙光路120号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-06',
    tenantId: 1
  },
  {
    id: 7,
    name: '成都太古里尼依格罗酒店',
    code: 'CD001',
    province: '四川省',
    city: '成都市',
    address: '成都市锦江区红星路三段1号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-07',
    tenantId: 1
  },
  {
    id: 8,
    name: '重庆解放碑威斯汀酒店',
    code: 'CQ001',
    province: '重庆市',
    city: '重庆市',
    address: '重庆市渝中区新华路222号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-08',
    tenantId: 1
  },
  {
    id: 9,
    name: '武汉光谷凯悦酒店',
    code: 'WH001',
    province: '湖北省',
    city: '武汉市',
    address: '武汉市洪山区珞喻路1077号',
    taxRate: 13,
    status: '维护中',
    createdAt: '2025-01-09',
    tenantId: 1
  },
  {
    id: 10,
    name: '西安兵马俑希尔顿酒店',
    code: 'XA001',
    province: '陕西省',
    city: '西安市',
    address: '西安市临潼区秦俑馆路1号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-10',
    tenantId: 1
  },
  {
    id: 11,
    name: '厦门鼓浪屿悦华酒店',
    code: 'XM001',
    province: '福建省',
    city: '厦门市',
    address: '厦门市思明区鼓浪屿晃岩路25号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-11',
    tenantId: 1
  },
  {
    id: 12,
    name: '青岛海景花园大酒店',
    code: 'QD001',
    province: '山东省',
    city: '青岛市',
    address: '青岛市市南区彰化路2号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-12',
    tenantId: 1
  },
  {
    id: 13,
    name: '大连金石滩鲁能希尔顿度假酒店',
    code: 'DL001',
    province: '辽宁省',
    city: '大连市',
    address: '大连市金州区金石滩国家旅游度假区海滨西路57号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-13',
    tenantId: 1
  },
  {
    id: 14,
    name: '三亚亚龙湾希尔顿度假酒店',
    code: 'SY001',
    province: '海南省',
    city: '三亚市',
    address: '三亚市亚龙湾国家旅游度假区',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-14',
    tenantId: 1
  },
  {
    id: 15,
    name: '昆明滇池洲际酒店',
    code: 'KM001',
    province: '云南省',
    city: '昆明市',
    address: '昆明市西山区怡景路5号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-15',
    tenantId: 1
  },
  {
    id: 16,
    name: '贵阳铂尔曼大酒店',
    code: 'GY001',
    province: '贵州省',
    city: '贵阳市',
    address: '贵阳市观山湖区林城西路8号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-16',
    tenantId: 1
  },
  {
    id: 17,
    name: '南宁南湖公园万丽酒店',
    code: 'NN001',
    province: '广西壮族自治区',
    city: '南宁市',
    address: '南宁市青秀区双拥路30号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-17',
    tenantId: 1
  },
  {
    id: 18,
    name: '南昌滕王阁雅高美爵酒店',
    code: 'NC001',
    province: '江西省',
    city: '南昌市',
    address: '南昌市东湖区抚河北路1号',
    taxRate: 13,
    status: '维护中',
    createdAt: '2025-01-18',
    tenantId: 1
  },
  {
    id: 19,
    name: '长沙梅溪湖金茂豪华精选酒店',
    code: 'CS001',
    province: '湖南省',
    city: '长沙市',
    address: '长沙市岳麓区梅溪湖环湖路1177号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-19',
    tenantId: 1
  },
  {
    id: 20,
    name: '郑州国际会展中心万豪酒店',
    code: 'ZZ001',
    province: '河南省',
    city: '郑州市',
    address: '郑州市金水区商务外环路5号',
    taxRate: 13,
    status: '正常',
    createdAt: '2025-01-20',
    tenantId: 1
  }
]

const { RangePicker } = DatePicker
const { Option } = Select

const HotelManagement = () => {
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(false)
  const [isViewModalVisible, setIsViewModalVisible] = useState(false)
  const [selectedHotel, setSelectedHotel] = useState(null)
  const [form] = Form.useForm()
  const [viewForm] = Form.useForm()
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    city: '',
    status: ''
  })
  
  const { selectedTenant } = useTenantContext()
  
  const statusOptions = [
    { value: 'active', label: '正常' },
    { value: 'inactive', label: '维护中' }
  ]

  const fetchHotels = async () => {
    setLoading(true)
    try {
      if (DEMO_MODE) {
        // 演示模式下使用模拟数据
        setHotels(mockHotels)
      } else {
        // 非演示模式下从后端获取数据
        const response = await hotelApi.getAllHotels(selectedTenant)
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
      }
    } catch (error) {
      console.error('获取酒店列表失败:', error)
      message.error('获取酒店列表失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchHotels()
  }, [selectedTenant])

  const handleSearch = () => {
    fetchHotels()
  }

  const handleReset = () => {
    setSearchParams({
      name: '',
      code: '',
      city: '',
      status: ''
    })
    form.resetFields()
    fetchHotels()
  }

  const handleAddHotel = () => {
    window.location.href = '/group-management/add-hotel'
  }

  const handleViewHotel = (record) => {
    setSelectedHotel(record)
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
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个酒店吗？',
      onOk: async () => {
        try {
          await hotelApi.deleteHotel(id)
          message.success('删除酒店成功')
          fetchHotels()
        } catch (error) {
          console.error('删除酒店失败:', error)
          message.error('删除酒店失败，请稍后重试')
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
          <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDeleteHotel(record.id)}>删除</Button>
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
              <Option value="北京">北京</Option>
              <Option value="上海">上海</Option>
              <Option value="广州">广州</Option>
              <Option value="深圳">深圳</Option>
              <Option value="杭州">杭州</Option>
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
          dataSource={hotels}
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

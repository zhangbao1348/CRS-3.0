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
  WarningOutlined,
  PlusOutlined as PlusIcon
} from '@ant-design/icons'
import { Editor } from '@wangeditor/editor-for-react'
import '@wangeditor/editor/dist/css/style.css'
import axios from 'axios'

const { RangePicker } = DatePicker

const HotelManagement = () => {
  // 状态管理
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(false)

  const [viewingHotel, setViewingHotel] = useState(null)
  const [viewModalVisible, setViewModalVisible] = useState(false)
  const [form] = Form.useForm()
  const [viewForm] = Form.useForm()
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    city: '',
    status: ''
  })
  
  // 房型数据状态
  const [roomTypeData, setRoomTypeData] = useState([
    { key: '1', roomType: '高级大床房', allocated: false, roomInfoEditable: false },
    { key: '2', roomType: '高级双床房', allocated: false, roomInfoEditable: false }
  ])
  
  // 房价码数据状态
  const [rateCodeData, setRateCodeData] = useState([
    { 
      key: '1', 
      rateCode: '房价码A', 
      allocated: false, 
      basicInfoEditable: false, 
      priceInfoEditable: false, 
      bookingLimitEditable: false, 
      guaranteeRuleEditable: false, 
      promotionEditable: false
    },
    { 
      key: '2', 
      rateCode: '房价码B', 
      allocated: false, 
      basicInfoEditable: false, 
      priceInfoEditable: false, 
      bookingLimitEditable: false, 
      guaranteeRuleEditable: false, 
      promotionEditable: false
    }
  ])
  
  // 富文本编辑器配置
  const editorConfig = {
    placeholder: '请输入酒店简介',
    onChange: (editor) => {
      form.setFieldsValue({ hotelIntroduction: editor.getHtml() })
    }
  }
  
  // 处理房型分配Switch变化
  const handleRoomTypeSwitchChange = (record, field) => {
    return (checked) => {
      setRoomTypeData(roomTypeData.map(item => {
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
            // 只有当房型被分配时，才能修改可修改状态
            if (!item.allocated) {
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
  
  // 处理房价码分配Switch变化
  const handleRateCodeSwitchChange = (record, field) => {
    return (checked) => {
      setRateCodeData(rateCodeData.map(item => {
        if (item.key === record.key) {
          if (field === 'allocated') {
            // 如果是分配状态变化，当取消分配时，将所有可修改状态设为false
            return {
              ...item,
              allocated: checked,
              ...(checked ? {} : {
                basicInfoEditable: false,
                priceInfoEditable: false,
                bookingLimitEditable: false,
                guaranteeRuleEditable: false,
                promotionEditable: false
              })
            }
          } else {
            // 只有当房价码被分配时，才能修改其他字段
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

  // 获取酒店列表
  const fetchHotels = async () => {
    setLoading(true)
    try {
      const response = await axios.get('http://localhost:8080/api/hotels')
      // 转换数据格式以匹配前端需求
      const formattedHotels = response.data.map(hotel => ({
        id: hotel.id,
        name: hotel.chineseName,
        code: hotel.hotelCode,
        province: hotel.province,
        city: hotel.city,
        address: hotel.address,
        taxRate: 13, // 后端Hotel实体中没有taxRate字段，需要根据实际情况调整
        status: hotel.status === 'active' ? '正常' : '维护中',
        createdAt: hotel.createdAt ? new Date(hotel.createdAt).toISOString().split('T')[0] : ''
      }))
      setHotels(formattedHotels)
    } catch (error) {
      console.error('获取酒店列表失败:', error)
      message.error('获取酒店列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  // 初始化时获取酒店列表
  useEffect(() => {
    fetchHotels()
  }, [])

  // 处理搜索
  const handleSearch = () => {
    fetchHotels() // 实际项目中应该根据searchParams过滤数据
  }

  // 处理重置
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

  // 处理新增酒店
  const handleAddHotel = () => {
    window.location.href = '/group-management/add-hotel'
  }

  // 处理查看酒店
  const handleViewHotel = async (record) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/hotels/${record.id}`)
      const hotel = response.data
      setViewingHotel(hotel)
      viewForm.setFieldsValue({
        hotelCode: hotel.hotelCode,
        hotelChineseName: hotel.chineseName,
        hotelEnglishName: hotel.englishName,
        hotelStarRating: hotel.starRating,
        hotelProvince: hotel.province,
        hotelCity: hotel.city,
        hotelAddress: hotel.address,
        hotelLongitude: hotel.longitude,
        hotelLatitude: hotel.latitude,
        hotelPhone: hotel.phone,
        hotelEmail: hotel.email,
        hotelIntroduction: hotel.introduction,
        hotelTotalRooms: 0 // 后端Hotel实体中没有totalRooms字段
      })
      setViewModalVisible(true)
    } catch (error) {
      console.error('获取酒店详情失败:', error)
      message.error('获取酒店详情失败，请稍后重试')
    }
  }
  
  // 查看酒店的标签页内容
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
                  name="hotelEnglishName"
                  label="酒店英文名称"
                >
                  <Input placeholder="请输入酒店英文名称" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelStarRating"
                  label="酒店星级"
                >
                  <Select placeholder="请选择酒店星级" disabled>
                    <Option value="1">一级</Option>
                    <Option value="2">二级</Option>
                    <Option value="3">三级</Option>
                    <Option value="4">四级</Option>
                    <Option value="5">五级</Option>
                  </Select>
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
              <Col span={12}>
                <Form.Item
                  name="hotelLongitude"
                  label="酒店经度"
                >
                  <Input placeholder="请输入经度" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelLatitude"
                  label="酒店纬度"
                >
                  <Input placeholder="请输入纬度" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelPhone"
                  label="酒店联系电话"
                >
                  <Input placeholder="请输入酒店联系电话" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelEmail"
                  label="酒店邮箱"
                >
                  <Input placeholder="请输入酒店邮箱" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelTotalRooms"
                  label="酒店总房间数"
                >
                  <InputNumber placeholder="请输入酒店总房间数" style={{ width: '100%' }} min={1} disabled />
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  name="hotelIntroduction"
                  label="酒店简介"
                >
                  <div style={{ border: '1px solid #d9d9d9', borderRadius: '4px', padding: 8, minHeight: 200 }}>
                    <div dangerouslySetInnerHTML={{ __html: viewForm.getFieldValue('hotelIntroduction') || '' }} />
                  </div>
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Card>
      )
    },
    {
      key: '2',
      label: '酒店设施',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 800 }}>
          <Form form={viewForm} layout="vertical">
            {/* 交通服务 */}
            <Form.Item
              name="transportationServices"
              label="交通服务"
            >
              <Checkbox.Group disabled>
                <Space wrap>
                  <Checkbox value="paidParking">收费停车场</Checkbox>
                  <Checkbox value="freeParking">免费停车场</Checkbox>
                  <Checkbox value="freeShuttle">免费接送机</Checkbox>
                  <Checkbox value="paidShuttle">收费接送机</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            {/* 餐饮服务 */}
            <Form.Item
              name="diningServices"
              label="餐饮服务"
            >
              <Checkbox.Group disabled>
                <Space wrap>
                  <Checkbox value="buffetRestaurant">自助早餐厅</Checkbox>
                  <Checkbox value="cafe">咖啡厅</Checkbox>
                  <Checkbox value="chineseRestaurant">中餐厅</Checkbox>
                  <Checkbox value="westernRestaurant">西餐厅</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            {/* 清洁服务 */}
            <Form.Item
              name="cleaningServices"
              label="清洁服务"
            >
              <Checkbox.Group disabled>
                <Space wrap>
                  <Checkbox value="laundryService">外送洗衣服务</Checkbox>
                  <Checkbox value="dryer">干衣机</Checkbox>
                  <Checkbox value="iron">熨斗/挂烫机</Checkbox>
                  <Checkbox value="laundryRoom">洗衣房</Checkbox>
                  <Checkbox value="valetService">熨衣服务</Checkbox>
                  <Checkbox value="washingService">洗衣服务</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
          </Form>
        </Card>
      )
    },
    {
      key: '3',
      label: '酒店图片',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 800 }}>
          <Form form={viewForm} layout="vertical">
            {/* 酒店店图 */}
            <Form.Item
              name="hotelLogo"
              label="酒店店图"
            >
              <div style={{ border: '1px solid #d9d9d9', borderRadius: '4px', padding: 16, textAlign: 'center' }}>
                <span>酒店店图</span>
              </div>
            </Form.Item>
            
            {/* 外观图片 */}
            <Form.Item
              name="externalImages"
              label="外观图片"
            >
              <div style={{ border: '1px solid #d9d9d9', borderRadius: '4px', padding: 16, textAlign: 'center' }}>
                <span>外观图片</span>
              </div>
            </Form.Item>
            
            {/* 餐厅图片 */}
            <Form.Item
              name="restaurantImages"
              label="餐厅图片"
            >
              <div style={{ border: '1px solid #d9d9d9', borderRadius: '4px', padding: 16, textAlign: 'center' }}>
                <span>餐厅图片</span>
              </div>
            </Form.Item>
            
            {/* 大堂图片 */}
            <Form.Item
              name="lobbyImages"
              label="大堂图片"
            >
              <div style={{ border: '1px solid #d9d9d9', borderRadius: '4px', padding: 16, textAlign: 'center' }}>
                <span>大堂图片</span>
              </div>
            </Form.Item>
          </Form>
        </Card>
      )
    },
    {
      key: '4',
      label: '酒店管控',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 1000 }}>
          <Form form={viewForm} layout="vertical">
            {/* 酒店创建权限 */}
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
              <Col span={12}>
                <Form.Item
                  name="allowCreateRateCode"
                  label="酒店创建房价码"
                >
                  <Radio.Group disabled>
                    <Radio value="allow">允许</Radio>
                    <Radio value="disallow">不允许</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="allowCreateRoomType"
                  label="酒店创建房型"
                >
                  <Radio.Group disabled>
                    <Radio value="allow">允许</Radio>
                    <Radio value="disallow">不允许</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
            </Row>
            
            {/* 分配房价码表格 */}
            <Form.Item style={{ marginBottom: 32 }}>
              <h3 style={{ marginBottom: 16, fontWeight: 600 }}>分配房价码</h3>
              <Table
                columns={[
                  {
                    title: '房价码',
                    dataIndex: 'rateCode',
                    key: 'rateCode',
                    width: 150
                  },
                  {
                    title: '是否分配到酒店',
                    dataIndex: 'allocated',
                    key: 'allocated',
                    width: 150,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  },
                  {
                    title: '基础信息是否可修改',
                    dataIndex: 'basicInfoEditable',
                    key: 'basicInfoEditable',
                    width: 180,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  },
                  {
                    title: '价格信息是否可修改',
                    dataIndex: 'priceInfoEditable',
                    key: 'priceInfoEditable',
                    width: 180,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  },
                  {
                    title: '预订限制是否可修改',
                    dataIndex: 'bookingLimitEditable',
                    key: 'bookingLimitEditable',
                    width: 180,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  },
                  {
                    title: '担保/取消规则是否可修改',
                    dataIndex: 'guaranteeRuleEditable',
                    key: 'guaranteeRuleEditable',
                    width: 220,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  },
                  {
                    title: '促销优惠是否可修改',
                    dataIndex: 'promotionEditable',
                    key: 'promotionEditable',
                    width: 180,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  }
                ]}
                dataSource={rateCodeData}
                pagination={false}
                bordered
                size="middle"
              />
            </Form.Item>
            
            {/* 分配房型表格 */}
            <Form.Item style={{ marginBottom: 32 }}>
              <h3 style={{ marginBottom: 16, fontWeight: 600 }}>分配房型</h3>
              <Table
                columns={[
                  {
                    title: '房型',
                    dataIndex: 'roomType',
                    key: 'roomType',
                    width: 150
                  },
                  {
                    title: '是否分配到酒店',
                    dataIndex: 'allocated',
                    key: 'allocated',
                    width: 150,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  },
                  {
                    title: '房型信息是否可以修改',
                    dataIndex: 'roomInfoEditable',
                    key: 'roomInfoEditable',
                    width: 180,
                    render: (text) => (
                      <span>{text ? '是' : '否'}</span>
                    )
                  }
                ]}
                dataSource={roomTypeData}
                pagination={false}
                size="small"
                bordered
              />
            </Form.Item>
          </Form>
        </Card>
      )
    }
  ]

  // 处理编辑酒店
  const handleEditHotel = (record) => {
    // 跳转到编辑酒店页面，并传递酒店ID作为查询参数
    window.location.href = `/hotel-management/edit-hotel?id=${record.id}`
  }

  // 处理删除酒店
  const handleDeleteHotel = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/hotels/${id}`)
      message.success('删除酒店成功')
      fetchHotels() // 重新获取酒店列表
    } catch (error) {
      console.error('删除酒店失败:', error)
      message.error('删除酒店失败，请稍后重试')
    }
  }

  // 处理编辑提交
  const handleEditSubmit = async (values) => {
    try {
      // 准备酒店基本信息
      // 将星级转换为数字格式，以适应数据库字段长度限制
      let starRatingValue = values.hotelStarRating;
      if (values.hotelStarRating === '一级') {
        starRatingValue = '1';
      } else if (values.hotelStarRating === '二级') {
        starRatingValue = '2';
      } else if (values.hotelStarRating === '三级') {
        starRatingValue = '3';
      } else if (values.hotelStarRating === '四级') {
        starRatingValue = '4';
      } else if (values.hotelStarRating === '五级') {
        starRatingValue = '5';
      }
      
      await axios.put(`http://localhost:8080/api/hotels/${editingHotel.id}`, {
        hotelCode: values.hotelCode,
        chineseName: values.hotelChineseName,
        englishName: values.hotelEnglishName || values.hotelChineseName,
        starRating: starRatingValue,
        province: values.hotelProvince,
        city: values.hotelCity,
        address: values.hotelAddress,
        longitude: values.hotelLongitude ? parseFloat(values.hotelLongitude) : null,
        latitude: values.hotelLatitude ? parseFloat(values.hotelLatitude) : null,
        phone: values.hotelPhone,
        email: values.hotelEmail,
        introduction: values.hotelIntroduction || '',
        status: 'active',
        groupId: 1 // 默认集团ID，需要根据实际情况调整
      })
      
      message.success('编辑酒店成功')
      setEditModalVisible(false)
      fetchHotels() // 重新获取酒店列表
    } catch (error) {
      console.error('编辑酒店失败:', error)
      message.error('编辑酒店失败，请稍后重试')
    }
  }



  // 列配置
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
      title: '税率(%)',
      dataIndex: 'taxRate',
      key: 'taxRate',
      width: 100,
      render: (taxRate) => (
        <span>{taxRate || '-'}</span>
      )
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
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 120
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditHotel(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger onClick={() => handleDeleteHotel(record.id)}>删除</Button>
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
      <Card style={{ marginBottom: 24, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.08)' }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="酒店名称" 
              prefix={<SearchOutlined />} 
              allowClear 
              value={searchParams.name}
              onChange={(e) => setSearchParams({...searchParams, name: e.target.value})}
              style={{ 
                height: 40, 
                border: '1px solid #d9d9d9', 
                borderRadius: 4, 
                transition: 'all 0.3s',
                '&:focus': {
                  borderColor: '#1890ff',
                  boxShadow: '0 0 0 2px rgba(24, 144, 255, 0.2)'
                }
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
                height: 40, 
                border: '1px solid #d9d9d9', 
                borderRadius: 4, 
                transition: 'all 0.3s'
              }}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="城市" 
              allowClear 
              style={{ 
                width: '100%',
                height: 40,
                border: '1px solid #d9d9d9',
                borderRadius: 4
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
                height: 40,
                border: '1px solid #d9d9d9',
                borderRadius: 4
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
                width: '100%',
                height: 40,
                border: '1px solid #d9d9d9',
                borderRadius: 4
              }} 
            />
          </Col>
          <Col xs={24} sm={24} md={8} lg={12} style={{ textAlign: 'right', display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
            <Space size="middle">
              <Button 
                type="default" 
                onClick={handleReset}
                style={{ 
                  height: 40, 
                  padding: '0 16px',
                  border: '1px solid #d9d9d9',
                  borderRadius: 4
                }}
              >
                重置
              </Button>
              <Button 
                type="primary" 
                icon={<SearchOutlined />} 
                onClick={handleSearch}
                style={{ 
                  height: 40, 
                  padding: '0 16px',
                  borderRadius: 4,
                  boxShadow: '0 2px 0 rgba(0, 0, 0, 0.045)'
                }}
              >
                搜索
              </Button>
            </Space>
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
          scroll={{ x: 800 }}
          locale={{ emptyText: '暂无酒店数据' }}
        />
      </Spin>

      {/* 查看酒店模态框 */}
      <Modal
        title="查看酒店"
        open={viewModalVisible}
        onCancel={() => setViewModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setViewModalVisible(false)}>
            关闭
          </Button>
        ]}
        width={1200}
        destroyOnClose
      >
        <Tabs defaultActiveKey="1" items={viewTabItems} />
      </Modal>


    </div>
  )
}

export default HotelManagement
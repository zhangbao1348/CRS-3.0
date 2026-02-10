import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Button, Tabs, Card, Row, Col, InputNumber, Checkbox, Space, Upload, Image, Radio, Table, Switch, message } from 'antd'
import { PlusOutlined, LeftOutlined } from '@ant-design/icons'
import { Editor } from '@wangeditor/editor-for-react'
import '@wangeditor/editor/dist/css/style.css'
import axios from 'axios'

const { Option } = Select

const EditHotel = () => {
  const [form] = Form.useForm()
  const [htmlContent, setHtmlContent] = useState('')
  const [hotelId, setHotelId] = useState(null)
  const [loading, setLoading] = useState(true)
  
  // 设施数据状态
  const [facilities, setFacilities] = useState({
    transportationServices: [],
    diningServices: [],
    cleaningServices: []
  })
  
  // 酒店图片状态
  const [hotelImages, setHotelImages] = useState({
    logo: [],
    external: [],
    restaurant: [],
    lobby: []
  })
  
  // 表单数据状态
  const [formData, setFormData] = useState({})
  
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
      setHtmlContent(editor.getHtml())
      form.setFieldsValue({ hotelIntroduction: editor.getHtml() })
    }
  }
  
  // 根据设施代码获取设施名称
  const getFacilityName = (code) => {
    const facilityMap = {
      // 交通服务
      paidParking: '收费停车场',
      freeParking: '免费停车场',
      freeShuttle: '免费接送机',
      paidShuttle: '收费接送机',
      // 餐饮服务
      buffetRestaurant: '自助早餐厅',
      cafe: '咖啡厅',
      chineseRestaurant: '中餐厅',
      westernRestaurant: '西餐厅',
      // 清洁服务
      laundryService: '外送洗衣服务',
      dryer: '干衣机',
      iron: '熨斗/挂烫机',
      laundryRoom: '洗衣房',
      valetService: '熨衣服务',
      washingService: '洗衣服务'
    }
    return facilityMap[code] || code
  }
  
  // 从URL获取酒店ID并加载数据
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search)
    const id = urlParams.get('id')
    if (id) {
      setHotelId(id)
      loadHotelData(id)
    } else {
      setLoading(false)
      message.error('未找到酒店ID')
    }
  }, [])
  
  // 当设施数据变化时，更新表单
  useEffect(() => {
    if (form && !loading) {
      console.log('Updating form with facilities:', facilities)
      try {
        form.setFieldsValue({
          transportationServices: facilities.transportationServices,
          diningServices: facilities.diningServices,
          cleaningServices: facilities.cleaningServices
        })
        console.log('Facilities updated in form via useEffect')
      } catch (error) {
        console.error('Error updating form with facilities:', error)
      }
    }
  }, [facilities, form, loading])
  
  // 加载酒店数据
  const loadHotelData = async (id) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/hotels/${id}`)
      const hotel = response.data
      
      // 将星级转换为显示格式
      let starRatingDisplay = hotel.starRating
      if (hotel.starRating === '1') {
        starRatingDisplay = '一级'
      } else if (hotel.starRating === '2') {
        starRatingDisplay = '二级'
      } else if (hotel.starRating === '3') {
        starRatingDisplay = '三级'
      } else if (hotel.starRating === '4') {
        starRatingDisplay = '四级'
      } else if (hotel.starRating === '5') {
        starRatingDisplay = '五级'
      }
      
      // 填充表单数据
        form.setFieldsValue({
          hotelCode: hotel.hotelCode,
          hotelChineseName: hotel.chineseName,
          hotelEnglishName: hotel.englishName,
          hotelStarRating: starRatingDisplay,
          hotelProvince: hotel.province,
          hotelCity: hotel.city,
          hotelAddress: hotel.address,
          hotelLongitude: hotel.longitude,
          hotelLatitude: hotel.latitude,
          hotelPhone: hotel.phone,
          hotelEmail: hotel.email,
          hotelIntroduction: hotel.introduction,
          hotelTotalRooms: hotel.totalRooms || 0
        })
      
      // 加载酒店设施
      try {
        const facilitiesResponse = await axios.get(`http://localhost:8080/api/hotel-facilities/hotel/${id}`)
        const hotelFacilities = facilitiesResponse.data
        
        // 分类设施
        const transportationServices = []
        const diningServices = []
        const cleaningServices = []
        
        // 处理返回的设施数据
        if (Array.isArray(hotelFacilities)) {
          hotelFacilities.forEach(facility => {
            if (facility.facilityCode) {
              const facilityCode = facility.facilityCode
              
              // 根据代码分类
              if (['paidParking', 'freeParking', 'freeShuttle', 'paidShuttle'].includes(facilityCode)) {
                transportationServices.push(facilityCode)
              } else if (['buffetRestaurant', 'cafe', 'chineseRestaurant', 'westernRestaurant'].includes(facilityCode)) {
                diningServices.push(facilityCode)
              } else if (['laundryService', 'dryer', 'iron', 'laundryRoom', 'valetService', 'washingService'].includes(facilityCode)) {
                cleaningServices.push(facilityCode)
              }
            }
          })
        }
        
        // 更新设施状态
        setFacilities({
          transportationServices,
          diningServices,
          cleaningServices
        })
      } catch (facilityError) {
        console.error('加载酒店设施失败:', facilityError)
        // 设施加载失败不影响酒店基本信息的显示
      }
      
      // 加载酒店图片
      try {
        const imagesResponse = await axios.get(`http://localhost:8080/api/hotel-images/hotel/${id}`)
        const images = imagesResponse.data
        
        if (Array.isArray(images)) {
          // 分类图片
          const logoImages = []
          const externalImages = []
          const restaurantImages = []
          const lobbyImages = []
          
          images.forEach(image => {
            const imageItem = {
              uid: image.id,
              name: image.imageName,
              status: 'done',
              url: `http://localhost:8080/api/hotel-images/view/${image.id}`
            }
            
            switch (image.imageType) {
              case 'logo':
                logoImages.push(imageItem)
                break
              case 'external':
                externalImages.push(imageItem)
                break
              case 'restaurant':
                restaurantImages.push(imageItem)
                break
              case 'lobby':
                lobbyImages.push(imageItem)
                break
            }
          })
          
          // 更新图片状态
          setHotelImages({
            logo: logoImages,
            external: externalImages,
            restaurant: restaurantImages,
            lobby: lobbyImages
          })
        }
      } catch (imageError) {
        console.error('加载酒店图片失败:', imageError)
        // 图片加载失败不影响酒店基本信息的显示
      }
      
      setLoading(false)
    } catch (error) {
      console.error('加载酒店数据失败:', error)
      message.error('加载酒店数据失败，请稍后重试')
      setLoading(false)
    }
  }
  
  // 处理保存酒店信息
  const handleSave = async (values) => {
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
      
      const hotelData = {
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
        totalRooms: values.hotelTotalRooms,
        status: 'active',
        groupId: 1 // 默认集团ID，需要根据实际情况调整
      }
      
      // 调用后端API更新酒店
      const response = await axios.put(`http://localhost:8080/api/hotels/${hotelId}`, hotelData)
      
      console.log('酒店更新成功:', response.data)
      
      // 保存酒店设施
      try {
        // 收集所有选中的设施
        const selectedFacilities = [];
        
        // 交通服务
        if (values.transportationServices && Array.isArray(values.transportationServices)) {
          values.transportationServices.forEach(service => {
            selectedFacilities.push({
              facilityName: getFacilityName(service),
              facilityCode: service,
              facilityType: '交通服务'
            });
          });
        }
        
        // 餐饮服务
        if (values.diningServices && Array.isArray(values.diningServices)) {
          values.diningServices.forEach(service => {
            selectedFacilities.push({
              facilityName: getFacilityName(service),
              facilityCode: service,
              facilityType: '餐饮服务'
            });
          });
        }
        
        // 清洁服务
        if (values.cleaningServices && Array.isArray(values.cleaningServices)) {
          values.cleaningServices.forEach(service => {
            selectedFacilities.push({
              facilityName: getFacilityName(service),
              facilityCode: service,
              facilityType: '清洁服务'
            });
          });
        }
        
        // 先删除酒店原有的所有设施
        await axios.delete(`http://localhost:8080/api/hotel-facilities/hotel/${hotelId}`);
        
        // 保存新的设施
        if (selectedFacilities.length > 0) {
          for (const facility of selectedFacilities) {
            const facilityData = {
              hotelId: hotelId,
              facilityName: facility.facilityName,
              facilityCode: facility.facilityCode,
              facilityType: facility.facilityType
            };
            await axios.post('http://localhost:8080/api/hotel-facilities', facilityData);
          }
        }
        
        console.log('酒店设施保存成功');
      } catch (facilityError) {
        console.error('保存酒店设施失败:', facilityError);
        // 设施保存失败不影响酒店基本信息的保存
      }
      
      message.success('保存成功！')
      
      // 跳转到酒店列表页面
      window.location.href = '/group-management/hotel-management'
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败，请稍后重试')
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
  
  // 定义标签页内容
  const tabItems = [
    {
      key: '1',
      label: '酒店基本信息',
      children: (
        <Card style={{ marginBottom: 24 }}>
          <Form form={form} layout="vertical" style={{ maxWidth: 800 }} onFinish={handleSave}>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="hotelCode"
                  label="酒店代码"
                  rules={[{ required: true, message: '请输入酒店代码' }]}
                >
                  <Input placeholder="请输入酒店代码" disabled />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelChineseName"
                  label="酒店中文名称"
                  rules={[{ required: true, message: '请输入酒店中文名称' }]}
                >
                  <Input placeholder="请输入酒店中文名称" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelEnglishName"
                  label="酒店英文名称"
                >
                  <Input placeholder="请输入酒店英文名称" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelStarRating"
                  label="酒店星级"
                  rules={[{ required: true, message: '请选择酒店星级' }]}
                >
                  <Select placeholder="请选择酒店星级">
                    <Option value="一级">一级</Option>
                    <Option value="二级">二级</Option>
                    <Option value="三级">三级</Option>
                    <Option value="四级">四级</Option>
                    <Option value="五级">五级</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelProvince"
                  label="酒店所在省份"
                  rules={[{ required: true, message: '请选择酒店所在省份' }]}
                >
                  <Select placeholder="请选择酒店所在省份">
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
                  rules={[{ required: true, message: '请选择酒店所在城市' }]}
                >
                  <Select placeholder="请选择酒店所在城市">
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
                  rules={[{ required: true, message: '请输入酒店详细地址' }]}
                >
                  <Input placeholder="请输入酒店详细地址" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelLongitude"
                  label="酒店经度"
                >
                  <Input placeholder="请输入经度" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelLatitude"
                  label="酒店纬度"
                >
                  <Input placeholder="请输入纬度" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelPhone"
                  label="酒店联系电话"
                  rules={[{ required: true, message: '请输入酒店联系电话' }]}
                >
                  <Input placeholder="请输入酒店联系电话" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelEmail"
                  label="酒店邮箱"
                  rules={[{ required: true, message: '请输入酒店邮箱' }]}
                >
                  <Input placeholder="请输入酒店邮箱" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelTotalRooms"
                  label="酒店总房间数"
                  rules={[{ required: true, message: '请输入酒店总房间数' }]}
                >
                  <InputNumber placeholder="请输入酒店总房间数" style={{ width: '100%' }} min={1} />
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item
                  name="hotelIntroduction"
                  label="酒店简介"
                >
                  <div style={{ border: '1px solid #d9d9d9', borderRadius: '4px' }}>
                    <Editor 
                      defaultContent="" 
                      config={editorConfig} 
                    />
                  </div>
                </Form.Item>
              </Col>
              <Col span={24} style={{ textAlign: 'center', marginTop: 32 }}>
                <Form.Item>
                  <Space>
                    <Button size="large" onClick={() => window.location.href = '/group-management/hotel-management'}>
                      <LeftOutlined /> 返回
                    </Button>
                    <Button type="primary" size="large" onClick={() => form.submit()}>
                      保存并返回列表
                    </Button>
                    <Button type="default" size="large" onClick={() => {
                      form.submit()
                      // 保存后切换到酒店设施页面
                      setTimeout(() => {
                        const tabs = document.querySelector('.ant-tabs-nav')
                        if (tabs) {
                          const tabButtons = tabs.querySelectorAll('.ant-tabs-tab')
                          if (tabButtons[1]) {
                            tabButtons[1].click()
                          }
                        }
                      }, 1000)
                    }}>
                      保存，并下一步
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
      label: '酒店设施',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 800 }}>
          <Form form={form} layout="vertical" onFinish={handleSave}>
            {/* 交通服务 */}
            <Form.Item
              name="transportationServices"
              label="交通服务"
            >
              <Checkbox.Group>
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
              <Checkbox.Group>
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
              <Checkbox.Group>
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
            
            {/* 保存按钮 */}
            <Form.Item style={{ marginTop: 32 }}>
              <div style={{ textAlign: 'center' }}>
                <Space>
                  <Button size="large" onClick={() => window.location.href = '/group-management/hotel-management'}>
                    <LeftOutlined /> 返回
                  </Button>
                  <Button type="primary" size="large" onClick={() => form.submit()}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => {
                    form.submit()
                    // 保存后切换到酒店设施页面
                    setTimeout(() => {
                      const tabs = document.querySelector('.ant-tabs-nav')
                      if (tabs) {
                        const tabButtons = tabs.querySelectorAll('.ant-tabs-tab')
                        if (tabButtons[2]) {
                          tabButtons[2].click()
                        }
                      }
                    }, 1000)
                  }}>
                    保存，并下一步
                  </Button>
                </Space>
              </div>
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
          <Form form={form} layout="vertical" onFinish={handleSave}>
            {/* 酒店店图 */}
            <Form.Item
              name="hotelLogo"
              label="酒店店图"
            >
              <Upload
                action="http://localhost:8080/api/hotel-images/upload"
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelId: parseInt(hotelId), imageType: 'logo' }}
                fileList={hotelImages.logo}
                onChange={(info) => {
                  console.log('Upload info:', info);
                  if (info.file.status === 'done') {
                    message.success(`${info.file.name} 上传成功`);
                    // 更新图片列表
                    const newImage = {
                      uid: info.file.response.id,
                      name: info.file.name,
                      status: 'done',
                      url: `http://localhost:8080/api/hotel-images/view/${info.file.response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      logo: [newImage]
                    }));
                  } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} 上传失败`);
                    console.error('Upload error:', info.file.error);
                  }
                }}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    logo: prev.logo.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    axios.delete(`http://localhost:8080/api/hotel-images/${file.uid}`)
                      .then(() => {
                        message.success('图片删除成功');
                      })
                      .catch(error => {
                        console.error('删除图片失败:', error);
                        message.error('删除图片失败，请稍后重试');
                      });
                  }
                }}
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            {/* 外观图片 */}
            <Form.Item
              name="externalImages"
              label="外观图片"
            >
              <Upload
                action="http://localhost:8080/api/hotel-images/upload"
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelId: parseInt(hotelId), imageType: 'external' }}
                fileList={hotelImages.external}
                onChange={(info) => {
                  if (info.file.status === 'done') {
                    message.success(`${info.file.name} 上传成功`);
                    // 更新图片列表
                    const newImage = {
                      uid: info.file.response.id,
                      name: info.file.name,
                      status: 'done',
                      url: `http://localhost:8080/api/hotel-images/view/${info.file.response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      external: [newImage]
                    }));
                  } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} 上传失败`);
                  }
                }}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    external: prev.external.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    axios.delete(`http://localhost:8080/api/hotel-images/${file.uid}`)
                      .then(() => {
                        message.success('图片删除成功');
                      })
                      .catch(error => {
                        console.error('删除图片失败:', error);
                        message.error('删除图片失败，请稍后重试');
                      });
                  }
                }}
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            {/* 餐厅图片 */}
            <Form.Item
              name="restaurantImages"
              label="餐厅图片"
            >
              <Upload
                action="http://localhost:8080/api/hotel-images/upload"
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelId: parseInt(hotelId), imageType: 'restaurant' }}
                fileList={hotelImages.restaurant}
                onChange={(info) => {
                  if (info.file.status === 'done') {
                    message.success(`${info.file.name} 上传成功`);
                    // 更新图片列表
                    const newImage = {
                      uid: info.file.response.id,
                      name: info.file.name,
                      status: 'done',
                      url: `http://localhost:8080/api/hotel-images/view/${info.file.response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      restaurant: [newImage]
                    }));
                  } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} 上传失败`);
                  }
                }}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    restaurant: prev.restaurant.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    axios.delete(`http://localhost:8080/api/hotel-images/${file.uid}`)
                      .then(() => {
                        message.success('图片删除成功');
                      })
                      .catch(error => {
                        console.error('删除图片失败:', error);
                        message.error('删除图片失败，请稍后重试');
                      });
                  }
                }}
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            {/* 大堂图片 */}
            <Form.Item
              name="lobbyImages"
              label="大堂图片"
            >
              <Upload
                action="http://localhost:8080/api/hotel-images/upload"
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelId: parseInt(hotelId), imageType: 'lobby' }}
                fileList={hotelImages.lobby}
                onChange={(info) => {
                  if (info.file.status === 'done') {
                    message.success(`${info.file.name} 上传成功`);
                    // 更新图片列表
                    const newImage = {
                      uid: info.file.response.id,
                      name: info.file.name,
                      status: 'done',
                      url: `http://localhost:8080/api/hotel-images/view/${info.file.response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      lobby: [newImage]
                    }));
                  } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} 上传失败`);
                  }
                }}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    lobby: prev.lobby.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    axios.delete(`http://localhost:8080/api/hotel-images/${file.uid}`)
                      .then(() => {
                        message.success('图片删除成功');
                      })
                      .catch(error => {
                        console.error('删除图片失败:', error);
                        message.error('删除图片失败，请稍后重试');
                      });
                  }
                }}
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            {/* 保存按钮 */}
            <Form.Item style={{ marginTop: 32 }}>
              <div style={{ textAlign: 'center' }}>
                <Space>
                  <Button size="large" onClick={() => window.location.href = '/group-management/hotel-management'}>
                    <LeftOutlined /> 返回
                  </Button>
                  <Button type="primary" size="large" onClick={() => form.submit()}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => {
                    form.submit()
                    // 保存后切换到酒店管控页面
                    setTimeout(() => {
                      const tabs = document.querySelector('.ant-tabs-nav')
                      if (tabs) {
                        const tabButtons = tabs.querySelectorAll('.ant-tabs-tab')
                        if (tabButtons[3]) {
                          tabButtons[3].click()
                        }
                      }
                    }, 1000)
                  }}>
                    保存，并下一步
                  </Button>
                </Space>
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
          <Form form={form} layout="vertical" onFinish={handleSave}>
            {/* 酒店创建权限 */}
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
              <Col span={12}>
                <Form.Item
                  name="allowCreateRateCode"
                  label="酒店创建房价码"
                >
                  <Radio.Group defaultValue="allow">
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
                  <Radio.Group defaultValue="allow">
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
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'allocated')}
                      />
                    )
                  },
                  {
                    title: '基础信息是否可修改',
                    dataIndex: 'basicInfoEditable',
                    key: 'basicInfoEditable',
                    width: 180,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'basicInfoEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: '价格信息是否可修改',
                    dataIndex: 'priceInfoEditable',
                    key: 'priceInfoEditable',
                    width: 180,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'priceInfoEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: '预订限制是否可修改',
                    dataIndex: 'bookingLimitEditable',
                    key: 'bookingLimitEditable',
                    width: 180,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'bookingLimitEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: '担保/取消规则是否可修改',
                    dataIndex: 'guaranteeRuleEditable',
                    key: 'guaranteeRuleEditable',
                    width: 220,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'guaranteeRuleEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: '促销优惠是否可修改',
                    dataIndex: 'promotionEditable',
                    key: 'promotionEditable',
                    width: 180,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'promotionEditable')}
                        disabled={!record.allocated}
                      />
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
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRoomTypeSwitchChange(record, 'allocated')}
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
                        onChange={handleRoomTypeSwitchChange(record, 'roomInfoEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  }
                ]}
                dataSource={roomTypeData}
                pagination={false}
                size="small"
                bordered
              />
            </Form.Item>
            
            {/* 保存按钮 */}
            <Form.Item style={{ marginTop: 32 }}>
              <div style={{ textAlign: 'center' }}>
                <Space>
                  <Button size="large" onClick={() => window.location.href = '/group-management/hotel-management'}>
                    <LeftOutlined /> 返回
                  </Button>
                  <Button type="primary" size="large" onClick={() => form.submit()}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => {
                    form.submit()
                    // 保存后返回列表
                    setTimeout(() => {
                      window.location.href = '/group-management/hotel-management'
                    }, 1000)
                  }}>
                    保存，并下一步
                  </Button>
                </Space>
              </div>
            </Form.Item>
          </Form>
        </Card>
      )
    }
  ]

  if (loading) {
    return (
      <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <h1 className="page-title">加载中...</h1>
      </div>
    )
  }

  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', overflow: 'auto' }}>
      <h1 className="page-title">编辑酒店</h1>
      
      <Tabs defaultActiveKey="1" items={tabItems} />
    </div>
  )
}

export default EditHotel
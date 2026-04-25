import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Button, Tabs, Card, Row, Col, InputNumber, Checkbox, Space, Upload, Image, Radio, Table, Switch, message } from 'antd'
import { PlusOutlined, LeftOutlined } from '@ant-design/icons'
import { Editor } from '@wangeditor/editor-for-react'
import '@wangeditor/editor/dist/css/style.css'
import { tenantApi, hotelApi, hotelFacilityApi, hotelImageApi, groupRateCodeApi, groupRoomTypeApi, hotelRateCodeAllocationApi, groupRoomTypeHotelApi } from '../../utils/api'
import { useTenantContext } from '../../contexts/TenantContext'

const { Option } = Select

const AddHotel = () => {
  const [form] = Form.useForm()
  const [htmlContent, setHtmlContent] = useState('')
  const [supportMultiPrice, setSupportMultiPrice] = useState('no')
  const [tenants, setTenants] = useState([])
  const { selectedTenant } = useTenantContext()
  const [currentTenantName, setCurrentTenantName] = useState('')
  const [activeTabKey, setActiveTabKey] = useState('1')
  const [hotelId, setHotelId] = useState(null)
  
  // 集团房价码数据状态
  const [groupRateCodes, setGroupRateCodes] = useState([])
  
  // 集团房型数据状态
  const [groupRoomTypes, setGroupRoomTypes] = useState([])
  
  // 房价码数据状态
  const [rateCodeData, setRateCodeData] = useState([])
  
  // 房价码分组状态
  const [rateCodeGroupBy, setRateCodeGroupBy] = useState('none')
  
  // 房型数据状态
  const [roomTypeData, setRoomTypeData] = useState([])
  
  // 房型分组状态
  const [roomTypeGroupBy, setRoomTypeGroupBy] = useState('none')
  
  // 富文本编辑器配置
  const editorConfig = {
    placeholder: '请输入酒店简介',
    onChange: (editor) => {
      setHtmlContent(editor.getHtml())
      form.setFieldsValue({ hotelIntroduction: editor.getHtml() })
    }
  }
  
  // 加载租户列表和集团数据
  useEffect(() => {
    const loadTenants = async () => {
      try {
        const response = await tenantApi.getAllTenants()
        if (response.success) {
          const tenantList = response.data || []
          setTenants(tenantList)
          if (selectedTenant) {
            const tenant = tenantList.find(t => t.id === selectedTenant)
            setCurrentTenantName(tenant ? tenant.tenantName : '未知租户')
          } else {
            setCurrentTenantName('平台')
          }
        }
      } catch (error) {
        console.error('加载租户列表失败:', error)
      }
    }
    loadTenants()
    
    // 加载集团房价码
    const loadGroupRateCodes = async () => {
      try {
        const rateCodes = await groupRateCodeApi.getActiveGroupRateCodes()
        const codes = Array.isArray(rateCodes) ? rateCodes : (rateCodes.data || [])
        setGroupRateCodes(codes)
        setRateCodeData(codes.map((code, index) => ({
          key: String(code.id),
          rateCode: code.rateName,
          rateCodeValue: code.rateCode,
          rateCodeCode: code.rateCode || '',
          rateCategory: code.rateCategory || '',
          derivativeLevel: code.derivativeLevel || 'basic',
          allocated: false,
          basicInfoEditable: false,
          priceInfoEditable: false,
          bookingLimitEditable: false,
          guaranteeRuleEditable: false,
          promotionEditable: false
        })))
      } catch (error) {
        console.error('加载集团房价码失败:', error)
      }
    }
    loadGroupRateCodes()
    
    // 加载集团房型
    const loadGroupRoomTypes = async () => {
      try {
        console.log('开始加载集团房型...')
        const response = await groupRoomTypeApi.getAllGroupRoomTypes()
        console.log('集团房型API返回:', response)
        if (response.success) {
          const types = response.data || []
          console.log('解析到的房型数据:', types)
          setGroupRoomTypes(types)
          const mappedData = types.map((type, index) => ({
            key: String(type.id),
            roomType: type.roomTypeName || type.roomType,
            roomTypeCode: type.roomTypeCode || '',
            roomCategory: type.roomCategory || '',
            allocated: false,
            roomInfoEditable: false
          }))
          console.log('映射后的roomTypeData:', mappedData)
          setRoomTypeData(mappedData)
        }
      } catch (error) {
        console.error('加载集团房型失败:', error)
      }
    }
    loadGroupRoomTypes()
  }, [selectedTenant])
  
  // 按分组方式处理房价码数据
  const getGroupedRateCodeData = () => {
    if (rateCodeGroupBy === 'none') {
      return { '所有房价码': rateCodeData }
    } else if (rateCodeGroupBy === 'category') {
      const grouped = {}
      rateCodeData.forEach(rateCode => {
        const category = rateCode.rateCategory || '其他'
        if (!grouped[category]) {
          grouped[category] = []
        }
        grouped[category].push(rateCode)
      })
      return grouped
    }
    return { '所有房价码': rateCodeData }
  }
  
  // 按分组方式处理房型数据
  const getGroupedRoomTypeData = () => {
    if (roomTypeGroupBy === 'none') {
      return { '所有房型': roomTypeData }
    } else if (roomTypeGroupBy === 'category') {
      const grouped = {}
      roomTypeData.forEach(roomType => {
        const category = roomType.roomCategory || '其他'
        if (!grouped[category]) {
          grouped[category] = []
        }
        grouped[category].push(roomType)
      })
      return grouped
    }
    return { '所有房型': roomTypeData }
  }
  
  // 处理房型分配Switch变化
  const handleRoomTypeSwitchChange = (record, field) => {
    return (checked) => {
      setRoomTypeData(roomTypeData.map(item => {
        if (item.key === record.key) {
          if (field === 'allocated') {
            return {
              ...item,
              allocated: checked,
              ...(checked ? {} : {
                roomInfoEditable: false
              })
            }
          } else {
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
  
  // 处理房价码全选
  const handleRateCodeSelectAll = (field, checked) => {
    setRateCodeData(rateCodeData.map(item => {
      if (field === 'allocated') {
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
        if (!item.allocated) {
          return item
        }
        return {
          ...item,
          [field]: checked
        }
      }
    }))
  }
  
  // 处理房型全选
  const handleRoomTypeSelectAll = (field, checked) => {
    setRoomTypeData(roomTypeData.map(item => {
      if (field === 'allocated') {
        return {
          ...item,
          allocated: checked,
          ...(checked ? {} : {
            roomInfoEditable: false
          })
        }
      } else {
        if (!item.allocated) {
          return item
        }
        return {
          ...item,
          [field]: checked
        }
      }
    }))
  }
  
  // 判断房价码某列是否全部选中
  const isRateCodeAllSelected = (field) => {
    if (rateCodeData.length === 0) return false
    const itemsToCheck = field === 'allocated' 
      ? rateCodeData 
      : rateCodeData.filter(item => item.allocated)
    
    if (itemsToCheck.length === 0) return false
    return itemsToCheck.every(item => item[field])
  }
  
  // 判断房型某列是否全部选中
  const isRoomTypeAllSelected = (field) => {
    if (roomTypeData.length === 0) return false
    const itemsToCheck = field === 'allocated' 
      ? roomTypeData 
      : roomTypeData.filter(item => item.allocated)
    
    if (itemsToCheck.length === 0) return false
    return itemsToCheck.every(item => item[field])
  }
  
  // 判断房价码表头全选复选框是否应该禁用
  const isRateCodeHeaderCheckboxDisabled = (field) => {
    if (field === 'allocated') return false
    return !rateCodeData.some(item => item.allocated)
  }
  
  // 判断房型表头全选复选框是否应该禁用
  const isRoomTypeHeaderCheckboxDisabled = (field) => {
    if (field === 'allocated') return false
    return !roomTypeData.some(item => item.allocated)
  }
  
  // 获取衍生级别标签
  const getDerivativeLevelTag = (level) => {
    const colors = {
      basic: 'blue',
      level1: 'green',
      level2: 'orange'
    }
    const labels = {
      basic: '基础',
      level1: '一级衍生',
      level2: '二级衍生'
    }
    return (
      <Tag color={colors[level] || 'default'} style={{ fontSize: '8px', padding: '0 4px', lineHeight: '16px', marginLeft: '4px', position: 'relative', top: '-8px' }}>
        {labels[level] || level}
      </Tag>
    )
  }
  
  // 保存基本信息
  const saveBasicInfo = async (values) => {
    try {
      let starRatingValue = values.hotelStarRating
      if (values.hotelStarRating === '一级') starRatingValue = '1'
      else if (values.hotelStarRating === '二级') starRatingValue = '2'
      else if (values.hotelStarRating === '三级') starRatingValue = '3'
      else if (values.hotelStarRating === '四级') starRatingValue = '4'
      else if (values.hotelStarRating === '五级') starRatingValue = '5'
      
      const multiPriceOptionsStr = Array.isArray(values.multiPriceOptions) 
        ? values.multiPriceOptions.join(',') 
        : (values.multiPriceOptions || '')
      
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
        minimumPrice: values.minimumPrice || null,
        status: 'active',
        supportMultiPrice: values.supportMultiPrice || 'no',
        multiPriceOptions: multiPriceOptionsStr,
        supportRoomTypePriceDiff: values.supportRoomTypePriceDiff || 'no',
        supportPersonPriceDiff: values.supportPersonPriceDiff || 'no',
        allowCreateRateCode: values.allowCreateRateCode || 'allow',
        allowCreateRoomType: values.allowCreateRoomType || 'allow',
        tenantId: selectedTenant
      }
      
      let response
      if (hotelId) {
        response = await hotelApi.updateHotel(hotelId, hotelData)
      } else {
        response = await hotelApi.createHotel(hotelData)
        if (response.id) {
          setHotelId(response.id)
        }
      }
      
      return true
    } catch (error) {
      console.error('保存酒店基本信息失败:', error)
      message.error('保存酒店基本信息失败')
      return false
    }
  }
  
  // 保存设施
  const saveFacilities = async (values) => {
    if (!hotelId) {
      message.warning('请先保存酒店基本信息')
      return false
    }
    
    try {
      const facilities = []
      if (values.transportationServices) {
        values.transportationServices.forEach(value => {
          facilities.push({
            hotelId: hotelId,
            facilityType: 'transportation',
            facilityCode: value,
            facilityName: getFacilityName(value),
            available: true
          })
        })
      }
      if (values.diningServices) {
        values.diningServices.forEach(value => {
          facilities.push({
            hotelId: hotelId,
            facilityType: 'dining',
            facilityCode: value,
            facilityName: getFacilityName(value),
            available: true
          })
        })
      }
      if (values.cleaningServices) {
        values.cleaningServices.forEach(value => {
          facilities.push({
            hotelId: hotelId,
            facilityType: 'cleaning',
            facilityCode: value,
            facilityName: getFacilityName(value),
            available: true
          })
        })
      }
      if (values.recreationServices) {
        values.recreationServices.forEach(value => {
          facilities.push({
            hotelId: hotelId,
            facilityType: 'recreation',
            facilityCode: value,
            facilityName: getFacilityName(value),
            available: true
          })
        })
      }
      if (values.businessServices) {
        values.businessServices.forEach(value => {
          facilities.push({
            hotelId: hotelId,
            facilityType: 'business',
            facilityCode: value,
            facilityName: getFacilityName(value),
            available: true
          })
        })
      }
      if (values.frontDeskServices) {
        values.frontDeskServices.forEach(value => {
          facilities.push({
            hotelId: hotelId,
            facilityType: 'frontDesk',
            facilityCode: value,
            facilityName: getFacilityName(value),
            available: true
          })
        })
      }
      if (values.generalFacilities) {
        values.generalFacilities.forEach(value => {
          facilities.push({
            hotelId: hotelId,
            facilityType: 'general',
            facilityCode: value,
            facilityName: getFacilityName(value),
            available: true
          })
        })
      }
      
      if (facilities.length > 0) {
        try {
          await hotelFacilityApi.deleteHotelFacilities(hotelId)
        } catch (error) {
          console.log('删除现有设施失败（可能是首次添加）:', error.message)
        }
        for (const facility of facilities) {
          await hotelFacilityApi.createHotelFacility(facility)
        }
      }
      return true
    } catch (error) {
      console.error('保存酒店设施失败:', error)
      message.error('保存酒店设施失败')
      return false
    }
  }
  
  // 保存房价码分配
  const saveRateCodeAllocations = async () => {
    if (!hotelId) {
      message.warning('请先保存酒店基本信息')
      return false
    }
    
    try {
      await hotelRateCodeAllocationApi.deleteAllocationsByHotelId(hotelId)
      
      const allocatedRateCodes = rateCodeData.filter(item => item.allocated)
      for (const item of allocatedRateCodes) {
        const allocation = {
          hotelId: hotelId,
          groupRateCodeId: parseInt(item.key),
          basicInfoEditable: item.basicInfoEditable || false,
          priceInfoEditable: item.priceInfoEditable || false,
          bookingLimitEditable: item.bookingLimitEditable || false,
          guaranteeRuleEditable: item.guaranteeRuleEditable || false,
          promotionEditable: item.promotionEditable || false
        }
        await hotelRateCodeAllocationApi.createAllocation(allocation)
      }
      return true
    } catch (error) {
      console.error('保存房价码分配失败:', error)
      message.error('保存房价码分配失败')
      return false
    }
  }
  
  // 保存房型分配
  const saveRoomTypeAllocations = async () => {
    if (!hotelId) {
      message.warning('请先保存酒店基本信息')
      return false
    }
    
    try {
      const allocations = roomTypeData
        .filter(item => item.allocated)
        .map(item => ({
          hotelId: hotelId,
          groupRoomTypeId: parseInt(item.key),
          roomInfoEditable: item.roomInfoEditable || false
        }))
      
      await groupRoomTypeHotelApi.batchSaveRoomTypeAllocations(allocations)
      return true
    } catch (error) {
      console.error('保存房型分配失败:', error)
      message.error('保存房型分配失败')
      return false
    }
  }
  
  // 获取设施名称
  const getFacilityName = (code) => {
    const facilityMap = {
      paidParking: '收费停车场',
      freeParking: '免费停车场',
      freeShuttle: '免费接送机',
      paidShuttle: '收费接送机',
      airportPickup: '接机服务',
      airportDropoff: '送机服务',
      carRental: '租车服务',
      taxiService: '叫车服务',
      buffetRestaurant: '自助早餐厅',
      cafe: '咖啡厅',
      chineseRestaurant: '中餐厅',
      westernRestaurant: '西餐厅',
      japaneseRestaurant: '日餐厅',
      lobbyBar: '大堂吧',
      roomService: '客房送餐',
      teaLounge: '茶室',
      bar: '酒吧',
      bakery: '面包房',
      laundryService: '外送洗衣服务',
      dryer: '干衣机',
      iron: '熨斗/挂烫机',
      laundryRoom: '洗衣房',
      valetService: '熨衣服务',
      washingService: '洗衣服务',
      dailyCleaning: '每日清洁',
      turnDownService: '夜床服务',
      indoorPool: '室内泳池',
      outdoorPool: '室外泳池',
      fitnessCenter: '健身房',
      spa: 'SPA水疗',
      sauna: '桑拿',
      steamRoom: '蒸汽房',
      massage: '按摩',
      beautySalon: '美容美发',
      gameRoom: '游戏室',
      cinema: '电影院',
      ktv: 'KTV',
      businessCenter: '商务中心',
      meetingRooms: '会议室',
      conferenceRooms: '多功能厅',
      secretarialService: '秘书服务',
      fax: '传真服务',
      printing: '打印服务',
      photocopying: '复印服务',
      wifi: '免费WiFi',
      expressDelivery: '快递服务',
      '24hourFrontDesk': '24小时前台',
      luggageStorage: '行李寄存',
      concierge: '礼宾服务',
      ticketService: '票务服务',
      tourDesk: '旅游咨询',
      foreignExchange: '外币兑换',
      checkinEarly: '提前入住',
      checkoutLate: '延迟退房',
      safeDepositBox: '保险箱',
      elevator: '电梯',
      accessibility: '无障碍设施',
      smokingArea: '吸烟区',
      nonSmoking: '无烟楼层',
      garden: '花园',
      terrace: '露台',
      sunDeck: '日光浴场',
      giftShop: '礼品店',
      miniMarket: '迷你市场',
      atm: 'ATM机',
      pharmacy: '药店'
    }
    return facilityMap[code] || code
  }
  
  // 处理Tab切换
  const handleTabChange = (key) => {
    setActiveTabKey(key)
  }
  
  // 保存当前Tab
  const handleSave = async (values) => {
    let success = false
    
    try {
      switch (activeTabKey) {
        case '1':
          success = await saveBasicInfo(values)
          break
        case '2':
          success = await saveFacilities(values)
          break
        case '3':
          success = true
          message.success('图片已保存')
          break
        case '4':
          success = await saveBasicInfo(values)
          break
        case '5':
          success = await saveRateCodeAllocations()
          break
        case '6':
          success = await saveRoomTypeAllocations()
          break
        default:
          success = await saveBasicInfo(values)
      }
      
      if (success) {
        message.success('保存成功！')
      }
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败，请稍后重试')
    }
  }
  
  // 保存并返回
  const handleSaveAndReturn = async (values) => {
    let success = false
    
    try {
      switch (activeTabKey) {
        case '1':
          success = await saveBasicInfo(values)
          break
        case '2':
          success = await saveFacilities(values)
          break
        case '3':
          success = true
          message.success('图片已保存')
          break
        case '4':
          success = await saveBasicInfo(values)
          break
        case '5':
          success = await saveRateCodeAllocations()
          break
        case '6':
          success = await saveRoomTypeAllocations()
          break
        default:
          success = await saveBasicInfo(values)
      }
      
      if (success) {
        message.success('保存成功！')
        window.location.href = '/group-management/hotel-management'
      }
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败，请稍后重试')
    }
  }
  
  // 保存并下一步
  const handleSaveAndNext = async (values) => {
    let success = false
    
    try {
      switch (activeTabKey) {
        case '1':
          success = await saveBasicInfo(values)
          break
        case '2':
          success = await saveFacilities(values)
          break
        case '3':
          success = true
          message.success('图片已保存')
          break
        case '4':
          success = await saveBasicInfo(values)
          break
        case '5':
          success = await saveRateCodeAllocations()
          break
        case '6':
          success = await saveRoomTypeAllocations()
          break
        default:
          success = await saveBasicInfo(values)
      }
      
      if (success) {
        message.success('保存成功！')
        const nextKey = String(parseInt(activeTabKey) + 1)
        if (nextKey <= '6') {
          setActiveTabKey(nextKey)
        }
      }
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败，请稍后重试')
    }
  }
  
  // 定义标签页内容
  const tabItems = [
    {
      key: '1',
      label: '酒店基本信息',
      children: (
        <Card style={{ marginBottom: 24 }}>
          <Form form={form} layout="vertical" style={{ maxWidth: 800 }}>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="hotelCode"
                  label="酒店代码"
                  rules={[
                    { required: true, message: '请输入酒店代码' },
                    { pattern: /^[A-Za-z0-9_]+$/, message: '酒店代码只能包含英文字母、数字和下划线' }
                  ]}
                >
                  <Input placeholder="请输入酒店代码" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label="归属集团"
                >
                  <Tag color="blue">{currentTenantName}</Tag>
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
                  rules={[{ required: true, message: '请输入酒店经度' }]}
                >
                  <Input placeholder="请输入经度" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="hotelLatitude"
                  label="酒店纬度"
                  rules={[{ required: true, message: '请输入酒店纬度' }]}
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
              <Col span={12}>
                <Form.Item
                  name="minimumPrice"
                  label="最低售价"
                >
                  <InputNumber placeholder="请输入最低售价" style={{ width: '100%' }} min={0} precision={2} prefix="¥" />
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
                    <Button type="primary" size="large" onClick={() => handleSave(form.getFieldsValue())}>
                      保存
                    </Button>
                    <Button type="primary" size="large" onClick={() => handleSaveAndNext(form.getFieldsValue())}>
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
          <Form form={form} layout="vertical">
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
                  <Checkbox value="airportPickup">接机服务</Checkbox>
                  <Checkbox value="airportDropoff">送机服务</Checkbox>
                  <Checkbox value="carRental">租车服务</Checkbox>
                  <Checkbox value="taxiService">叫车服务</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
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
                  <Checkbox value="japaneseRestaurant">日餐厅</Checkbox>
                  <Checkbox value="lobbyBar">大堂吧</Checkbox>
                  <Checkbox value="roomService">客房送餐</Checkbox>
                  <Checkbox value="teaLounge">茶室</Checkbox>
                  <Checkbox value="bar">酒吧</Checkbox>
                  <Checkbox value="bakery">面包房</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
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
                  <Checkbox value="dailyCleaning">每日清洁</Checkbox>
                  <Checkbox value="turnDownService">夜床服务</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            <Form.Item
              name="recreationServices"
              label="休闲娱乐"
            >
              <Checkbox.Group>
                <Space wrap>
                  <Checkbox value="indoorPool">室内泳池</Checkbox>
                  <Checkbox value="outdoorPool">室外泳池</Checkbox>
                  <Checkbox value="fitnessCenter">健身房</Checkbox>
                  <Checkbox value="spa">SPA水疗</Checkbox>
                  <Checkbox value="sauna">桑拿</Checkbox>
                  <Checkbox value="steamRoom">蒸汽房</Checkbox>
                  <Checkbox value="massage">按摩</Checkbox>
                  <Checkbox value="beautySalon">美容美发</Checkbox>
                  <Checkbox value="gameRoom">游戏室</Checkbox>
                  <Checkbox value="cinema">电影院</Checkbox>
                  <Checkbox value="ktv">KTV</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            <Form.Item
              name="businessServices"
              label="商务服务"
            >
              <Checkbox.Group>
                <Space wrap>
                  <Checkbox value="businessCenter">商务中心</Checkbox>
                  <Checkbox value="meetingRooms">会议室</Checkbox>
                  <Checkbox value="conferenceRooms">多功能厅</Checkbox>
                  <Checkbox value="secretarialService">秘书服务</Checkbox>
                  <Checkbox value="fax">传真服务</Checkbox>
                  <Checkbox value="printing">打印服务</Checkbox>
                  <Checkbox value="photocopying">复印服务</Checkbox>
                  <Checkbox value="wifi">免费WiFi</Checkbox>
                  <Checkbox value="expressDelivery">快递服务</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            <Form.Item
              name="frontDeskServices"
              label="前台服务"
            >
              <Checkbox.Group>
                <Space wrap>
                  <Checkbox value="24hourFrontDesk">24小时前台</Checkbox>
                  <Checkbox value="luggageStorage">行李寄存</Checkbox>
                  <Checkbox value="concierge">礼宾服务</Checkbox>
                  <Checkbox value="ticketService">票务服务</Checkbox>
                  <Checkbox value="tourDesk">旅游咨询</Checkbox>
                  <Checkbox value="foreignExchange">外币兑换</Checkbox>
                  <Checkbox value="checkinEarly">提前入住</Checkbox>
                  <Checkbox value="checkoutLate">延迟退房</Checkbox>
                  <Checkbox value="safeDepositBox">保险箱</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            <Form.Item
              name="generalFacilities"
              label="通用设施"
            >
              <Checkbox.Group>
                <Space wrap>
                  <Checkbox value="elevator">电梯</Checkbox>
                  <Checkbox value="accessibility">无障碍设施</Checkbox>
                  <Checkbox value="smokingArea">吸烟区</Checkbox>
                  <Checkbox value="nonSmoking">无烟楼层</Checkbox>
                  <Checkbox value="garden">花园</Checkbox>
                  <Checkbox value="terrace">露台</Checkbox>
                  <Checkbox value="sunDeck">日光浴场</Checkbox>
                  <Checkbox value="giftShop">礼品店</Checkbox>
                  <Checkbox value="miniMarket">迷你市场</Checkbox>
                  <Checkbox value="atm">ATM机</Checkbox>
                  <Checkbox value="pharmacy">药店</Checkbox>
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            <Form.Item style={{ marginTop: 32, textAlign: 'center' }}>
              <Space>
                <Button icon={<LeftOutlined />} onClick={() => setActiveTabKey('1')}>
                  返回
                </Button>
                <Button type="primary" size="large" onClick={() => handleSave(form.getFieldsValue())}>
                  保存
                </Button>
                <Button type="primary" size="large" onClick={() => handleSaveAndNext(form.getFieldsValue())}>
                  保存，并下一步
                </Button>
              </Space>
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
          <Form form={form} layout="vertical">
            <Form.Item
              name="hotelLogo"
              label="酒店店图"
            >
              <Upload
                name="logo"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            <Form.Item
              name="externalImages"
              label="外观图片"
            >
              <Upload
                name="externalImages"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            <Form.Item
              name="restaurantImages"
              label="餐厅图片"
            >
              <Upload
                name="restaurantImages"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            <Form.Item
              name="lobbyImages"
              label="大堂图片"
            >
              <Upload
                name="lobbyImages"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传</div>
                </div>
              </Upload>
            </Form.Item>
            
            <Form.Item
              name="hotelVideo"
              label="酒店视频"
            >
              <Upload
                name="hotelVideo"
                listType="text"
                maxCount={1}
                accept="video/*"
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传视频</div>
                </div>
              </Upload>
            </Form.Item>
            
            <Form.Item style={{ marginTop: 32, textAlign: 'center' }}>
              <Space>
                <Button icon={<LeftOutlined />} onClick={() => setActiveTabKey('2')}>
                  返回
                </Button>
                <Button type="primary" size="large" onClick={() => handleSave(form.getFieldsValue())}>
                  保存
                </Button>
                <Button type="primary" size="large" onClick={() => handleSaveAndNext(form.getFieldsValue())}>
                  保存，并下一步
                </Button>
              </Space>
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
          <Form form={form} layout="vertical">
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
              <Col span={12}>
                <Form.Item
                  name="allowCreateRateCode"
                  label="酒店创建房价码"
                  initialValue="allow"
                >
                  <Radio.Group>
                    <Radio value="allow">允许</Radio>
                    <Radio value="disallow">不允许</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="allowCreateRoomType"
                  label="酒店创建房型"
                  initialValue="allow"
                >
                  <Radio.Group>
                    <Radio value="allow">允许</Radio>
                    <Radio value="disallow">不允许</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
            </Row>
            
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
              <Col span={12}>
                <Form.Item
                  name="supportMultiPrice"
                  label="酒店支持多人价"
                  initialValue="no"
                >
                  <Radio.Group 
                    onChange={(e) => setSupportMultiPrice(e.target.value)}
                  >
                    <Radio value="yes">是</Radio>
                    <Radio value="no">否</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
            </Row>
            {supportMultiPrice === 'yes' && (
              <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
                <Col span={24}>
                  <Form.Item
                    name="multiPriceOptions"
                    label="支持的多人价格"
                  >
                    <Checkbox.Group>
                      <Space wrap>
                        <Checkbox value="1P">1人价</Checkbox>
                        <Checkbox value="2P">2人价</Checkbox>
                        <Checkbox value="3P">3人价</Checkbox>
                        <Checkbox value="4P">4人价</Checkbox>
                        <Checkbox value="5P">5人价</Checkbox>
                        <Checkbox value="1C">1儿童价</Checkbox>
                        <Checkbox value="2C">2儿童价</Checkbox>
                      </Space>
                    </Checkbox.Group>
                  </Form.Item>
                </Col>
              </Row>
            )}
            
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
              <Col span={12}>
                <Form.Item
                  name="supportRoomTypePriceDiff"
                  label="支持房型价差"
                  initialValue="no"
                >
                  <Radio.Group>
                    <Radio value="yes">是</Radio>
                    <Radio value="no">否</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="supportPersonPriceDiff"
                  label="支持人数价差"
                  initialValue="no"
                >
                  <Radio.Group>
                    <Radio value="yes">是</Radio>
                    <Radio value="no">否</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
            </Row>
            
            <Form.Item style={{ marginTop: 32, textAlign: 'center' }}>
              <Space>
                <Button icon={<LeftOutlined />} onClick={() => setActiveTabKey('3')}>
                  返回
                </Button>
                <Button type="primary" size="large" onClick={() => handleSave(form.getFieldsValue())}>
                  保存
                </Button>
                <Button type="primary" size="large" onClick={() => handleSaveAndNext(form.getFieldsValue())}>
                  保存，并下一步
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>
      )
    },
    {
      key: '5',
      label: '分配房价码',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 1200 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h3 style={{ margin: 0, fontWeight: 600 }}>分配房价码</h3>
            <div>
              <span style={{ marginRight: 8 }}>分组方式：</span>
              <Select
                value={rateCodeGroupBy}
                onChange={setRateCodeGroupBy}
                style={{ width: 150 }}
              >
                <Option value="none">不分组</Option>
                <Option value="category">按房价大类</Option>
              </Select>
            </div>
          </div>
          
          {Object.entries(getGroupedRateCodeData()).map(([category, data]) => (
            <div key={category} style={{ marginBottom: 24 }}>
              {rateCodeGroupBy !== 'none' && (
                <h4 style={{ marginBottom: 8, color: '#1890ff', fontWeight: 600 }}>
                  {category}
                </h4>
              )}
              <Table
                columns={[
                  {
                    title: '房价码',
                    dataIndex: 'rateCode',
                    key: 'rateCode',
                    width: 250,
                    render: (text, record) => (
                      <div style={{ position: 'relative', display: 'inline-block' }}>
                        <span>{record.rateCode}（{record.rateCodeCode}）</span>
                        {getDerivativeLevelTag(record.derivativeLevel)}
                      </div>
                    )
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>是否分配到酒店</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRateCodeAllSelected('allocated')}
                            onChange={(e) => handleRateCodeSelectAll('allocated', e.target.checked)}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部房价码可用</span>
                        </div>
                      </div>
                    ),
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
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>基础信息可修改</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRateCodeAllSelected('basicInfoEditable')}
                            onChange={(e) => handleRateCodeSelectAll('basicInfoEditable', e.target.checked)}
                            disabled={isRateCodeHeaderCheckboxDisabled('basicInfoEditable')}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部可修改</span>
                        </div>
                      </div>
                    ),
                    dataIndex: 'basicInfoEditable',
                    key: 'basicInfoEditable',
                    width: 160,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'basicInfoEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>价格信息可修改</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRateCodeAllSelected('priceInfoEditable')}
                            onChange={(e) => handleRateCodeSelectAll('priceInfoEditable', e.target.checked)}
                            disabled={isRateCodeHeaderCheckboxDisabled('priceInfoEditable')}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部可修改</span>
                        </div>
                      </div>
                    ),
                    dataIndex: 'priceInfoEditable',
                    key: 'priceInfoEditable',
                    width: 160,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'priceInfoEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>预订限制可修改</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRateCodeAllSelected('bookingLimitEditable')}
                            onChange={(e) => handleRateCodeSelectAll('bookingLimitEditable', e.target.checked)}
                            disabled={isRateCodeHeaderCheckboxDisabled('bookingLimitEditable')}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部可修改</span>
                        </div>
                      </div>
                    ),
                    dataIndex: 'bookingLimitEditable',
                    key: 'bookingLimitEditable',
                    width: 160,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'bookingLimitEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>担保/取消规则可修改</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRateCodeAllSelected('guaranteeRuleEditable')}
                            onChange={(e) => handleRateCodeSelectAll('guaranteeRuleEditable', e.target.checked)}
                            disabled={isRateCodeHeaderCheckboxDisabled('guaranteeRuleEditable')}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部可修改</span>
                        </div>
                      </div>
                    ),
                    dataIndex: 'guaranteeRuleEditable',
                    key: 'guaranteeRuleEditable',
                    width: 200,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'guaranteeRuleEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>促销优惠可修改</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRateCodeAllSelected('promotionEditable')}
                            onChange={(e) => handleRateCodeSelectAll('promotionEditable', e.target.checked)}
                            disabled={isRateCodeHeaderCheckboxDisabled('promotionEditable')}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部可修改</span>
                        </div>
                      </div>
                    ),
                    dataIndex: 'promotionEditable',
                    key: 'promotionEditable',
                    width: 160,
                    render: (text, record) => (
                      <Switch 
                        checked={text} 
                        onChange={handleRateCodeSwitchChange(record, 'promotionEditable')}
                        disabled={!record.allocated}
                      />
                    )
                  }
                ]}
                dataSource={data}
                pagination={false}
                bordered
                size="middle"
                rowKey="key"
              />
            </div>
          ))}
          
          <div style={{ marginTop: 32, textAlign: 'center' }}>
            <Space>
              <Button icon={<LeftOutlined />} onClick={() => setActiveTabKey('4')}>
                返回
              </Button>
              <Button type="primary" size="large" onClick={() => handleSave(form.getFieldsValue())}>
                保存
              </Button>
              <Button type="primary" size="large" onClick={() => handleSaveAndNext(form.getFieldsValue())}>
                保存，并下一步
              </Button>
            </Space>
          </div>
        </Card>
      )
    },
    {
      key: '6',
      label: '分配房型',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 1000 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h3 style={{ margin: 0, fontWeight: 600 }}>分配房型</h3>
            <div>
              <span style={{ marginRight: 8 }}>分组方式：</span>
              <Select
                value={roomTypeGroupBy}
                onChange={setRoomTypeGroupBy}
                style={{ width: 150 }}
              >
                <Option value="none">不分组</Option>
                <Option value="category">按房型大类</Option>
              </Select>
            </div>
          </div>
          
          {Object.entries(getGroupedRoomTypeData()).map(([category, data]) => (
            <div key={category} style={{ marginBottom: 24 }}>
              {roomTypeGroupBy !== 'none' && (
                <h4 style={{ marginBottom: 8, color: '#1890ff', fontWeight: 600 }}>
                  {category}
                </h4>
              )}
              <Table
                columns={[
                  {
                    title: '房型',
                    dataIndex: 'roomType',
                    key: 'roomType',
                    width: 200,
                    render: (text, record) => {
                      console.log('表格中的record:', record)
                      return (
                        <span>{record.roomType}（{record.roomTypeCode}）</span>
                      )
                    }
                  },
                  {
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>是否分配到酒店</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRoomTypeAllSelected('allocated')}
                            onChange={(e) => handleRoomTypeSelectAll('allocated', e.target.checked)}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部房型可用</span>
                        </div>
                      </div>
                    ),
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
                    title: (
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px' }}>
                        <span>房型信息是否可以修改</span>
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                          <Checkbox
                            checked={isRoomTypeAllSelected('roomInfoEditable')}
                            onChange={(e) => handleRoomTypeSelectAll('roomInfoEditable', e.target.checked)}
                            disabled={isRoomTypeHeaderCheckboxDisabled('roomInfoEditable')}
                          />
                          <span style={{ color: '#52c41a', fontSize: '12px' }}>全部可修改</span>
                        </div>
                      </div>
                    ),
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
                dataSource={data}
                pagination={false}
                bordered
                size="small"
                rowKey="key"
              />
            </div>
          ))}
          
          <div style={{ marginTop: 32, textAlign: 'center' }}>
            <Space>
              <Button icon={<LeftOutlined />} onClick={() => setActiveTabKey('5')}>
                返回
              </Button>
              <Button type="primary" size="large" onClick={() => handleSave(form.getFieldsValue())}>
                保存
              </Button>
              <Button type="primary" size="large" onClick={handleSaveAndReturn}>
                保存并返回列表
              </Button>
            </Space>
          </div>
        </Card>
      )
    }
  ]

  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', overflow: 'auto' }}>
      <h1 className="page-title">新增酒店</h1>
      
      <Tabs activeKey={activeTabKey} onChange={handleTabChange} items={tabItems} />
    </div>
  )
}

export default AddHotel

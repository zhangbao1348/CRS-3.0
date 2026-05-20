import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Button, Tabs, Card, Row, Col, InputNumber, Checkbox, Space, Upload, Image, Radio, Table, Switch, message } from 'antd'
import { PlusOutlined, LeftOutlined } from '@ant-design/icons'
import { Editor } from '@wangeditor/editor-for-react'
import '@wangeditor/editor/dist/css/style.css'
import { hotelApi, hotelFacilityApi, hotelImageApi, groupFacilityApi, hotelRateCodeAllocationApi, groupRateCodeApi, groupRoomTypeApi, groupRoomTypeHotelApi } from '../../utils/api'

const { Option } = Select

const EditHotel = () => {
  const [form] = Form.useForm()
  const [htmlContent, setHtmlContent] = useState('')
  const [hotelId, setHotelId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [supportMultiPrice, setSupportMultiPrice] = useState('no')
  const [activeTabKey, setActiveTabKey] = useState('1')
  const [loadedTabs, setLoadedTabs] = useState(new Set(['1']))
  const [hotel, setHotel] = useState(null)
  
  // 集团设施数据状态
  const [groupFacilities, setGroupFacilities] = useState([])
  
  // 集团房价码数据状态
  const [groupRateCodes, setGroupRateCodes] = useState([])
  
  // 集团房型数据状态
  const [groupRoomTypes, setGroupRoomTypes] = useState([])
  
  // 设施数据状态
  const [facilities, setFacilities] = useState({
    transportationServices: [],
    diningServices: [],
    cleaningServices: [],
    businessServices: [],
    recreationServices: [],
    frontDeskServices: []
  })
  
  // 酒店图片状态
  const [hotelImages, setHotelImages] = useState({
    logo: [],
    external: [],
    restaurant: [],
    lobby: [],
    video: []
  })
  
  // 表单数据状态
  const [formData, setFormData] = useState({})
  
  // 房型数据状态
  const [roomTypeData, setRoomTypeData] = useState([
    { key: '1', roomType: '高级大床房', allocated: false, roomInfoEditable: false },
    { key: '2', roomType: '高级双床房', allocated: false, roomInfoEditable: false }
  ])
  
  // 房价码数据状态
  const [filterRateCategory, setFilterRateCategory] = useState('')
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
    const facility = groupFacilities.find(f => f.facilityCode === code)
    return facility ? facility.facilityName : code
  }
  
  // 从URL获取酒店ID并加载数据
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search)
    const id = urlParams.get('id')
    if (id) {
      const numericId = parseInt(id)
      setHotelId(numericId)
      loadHotelData(numericId)
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
          cleaningServices: facilities.cleaningServices,
          businessServices: facilities.businessServices,
          recreationServices: facilities.recreationServices,
          frontDeskServices: facilities.frontDeskServices
        })
        console.log('Facilities updated in form via useEffect')
      } catch (error) {
        console.error('Error updating form with facilities:', error)
      }
    }
  }, [facilities, form, loading])
  
  // 加载酒店基础数据
  const loadHotelData = async (id) => {
    try {
      console.log('开始加载酒店基础数据，ID:', id)
      
      // 先加载集团设施（因为设施Tab需要这个数据）
      try {
        const facilities = await groupFacilityApi.getAllGroupFacilities({
          params: { scope: 'hotel' },
          metadata: { skipAutoLogout: true }
        })
        setGroupFacilities(facilities)
        console.log('集团设施加载成功:', facilities)
      } catch (facilityError) {
        console.error('加载集团设施失败:', facilityError)
      }
      
      // 使用带skipAutoLogout标记的API调用，防止401时自动跳转
      const response = await hotelApi.getHotelById(id, {
        metadata: { skipAutoLogout: true }
      })
      console.log('API响应:', response)
      const hotel = response.data
      setHotel(hotel)
      console.log('酒店数据:', hotel)
      
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
          hotelTotalRooms: hotel.totalRooms || 0,
          supportMultiPrice: hotel.supportMultiPrice || 'no',
          multiPriceOptions: hotel.multiPriceOptions ? hotel.multiPriceOptions.split(',') : [],
          supportRoomTypePriceDiff: hotel.supportRoomTypePriceDiff || 'no',
          supportPersonPriceDiff: hotel.supportPersonPriceDiff || 'no',
          allowCreateRateCode: hotel.allowCreateRateCode || 'allow',
          allowCreateRoomType: hotel.allowCreateRoomType || 'allow'
        })
        
        // 设置支持多人价状态
        setSupportMultiPrice(hotel.supportMultiPrice || 'no')
      
      setLoading(false)
    } catch (error) {
      console.error('加载酒店数据失败:', error)
      console.error('错误详情:', error.response)
      console.error('错误状态码:', error.response?.status)
      message.error('加载酒店数据失败，请稍后重试')
      setLoading(false)
    }
  }
  
  // 加载酒店设施
  const loadHotelFacilities = async (code) => {
    try {
      const hotelFacilities = await hotelFacilityApi.getHotelFacilitiesByCode(code, {
        metadata: { skipAutoLogout: true }
      })
      
      // 分类设施
      const transportationServices = []
      const diningServices = []
      const cleaningServices = []
      const businessServices = []
      const recreationServices = []
      const frontDeskServices = []
      
      // 处理返回的设施数据
      if (Array.isArray(hotelFacilities)) {
        hotelFacilities.forEach(facility => {
          if (facility.facilityCode) {
            const facilityCode = facility.facilityCode
            const facilityType = facility.facilityType
            
            // 根据类型分类
            if (facilityType === '交通服务') {
              transportationServices.push(facilityCode)
            } else if (facilityType === '餐饮服务') {
              diningServices.push(facilityCode)
            } else if (facilityType === '清洁服务') {
              cleaningServices.push(facilityCode)
            } else if (facilityType === '商务服务') {
              businessServices.push(facilityCode)
            } else if (facilityType === '休闲娱乐') {
              recreationServices.push(facilityCode)
            } else if (facilityType === '前台服务') {
              frontDeskServices.push(facilityCode)
            }
          }
        })
      }
      
      // 更新设施状态
      setFacilities({
        transportationServices,
        diningServices,
        cleaningServices,
        businessServices,
        recreationServices,
        frontDeskServices
      })
      console.log('酒店设施加载成功')
    } catch (facilityError) {
      console.error('加载酒店设施失败:', facilityError)
    }
  }
  
  // 加载酒店图片
  const loadHotelImages = async (code) => {
    try {
      const images = await hotelImageApi.getHotelImagesByCode(code, {
        metadata: { skipAutoLogout: true }
      })
      
      if (Array.isArray(images)) {
        // 分类图片
        const logoImages = []
        const externalImages = []
        const restaurantImages = []
        const lobbyImages = []
        const videoItems = []
        
        images.forEach(image => {
          const imageItem = {
            uid: image.id,
            name: image.imageName,
            status: 'done',
            url: `/api/hotel-images/view/${image.id}`
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
            case 'video':
              videoItems.push(imageItem)
              break
          }
        })
        
        // 更新图片状态
        setHotelImages({
          logo: logoImages,
          external: externalImages,
          restaurant: restaurantImages,
          lobby: lobbyImages,
          video: videoItems
        })
        console.log('酒店图片加载成功')
      }
    } catch (imageError) {
      console.error('加载酒店图片失败:', imageError)
    }
  }
  
  // 加载酒店房价码分配
  const loadRateCodeAllocations = async (code) => {
    try {
      console.log('开始加载酒店房价码分配，酒店代码:', code)
      
      // 先加载集团房价码
      let activeRateCodes = []
      try {
        const rateCodes = await groupRateCodeApi.getActiveGroupRateCodes({
          metadata: { skipAutoLogout: true }
        })
        activeRateCodes = Array.isArray(rateCodes) ? rateCodes : (rateCodes.data || [])
        setGroupRateCodes(activeRateCodes)
        console.log('集团房价码加载成功:', activeRateCodes)
      } catch (rateCodeError) {
        console.error('加载集团房价码失败:', rateCodeError)
      }
      
      // 加载酒店房价码分配
      let allocations = []
      try {
        allocations = await hotelRateCodeAllocationApi.getAllocationsByHotelCode(code, {
          metadata: { skipAutoLogout: true }
        })
        allocations = Array.isArray(allocations) ? allocations : (allocations.data || [])
        console.log('酒店房价码分配原始数据:', allocations)
      } catch (allocError) {
        console.error('加载房价码分配失败:', allocError)
      }
      
      // 创建分配映射
      const allocationMap = {}
      if (Array.isArray(allocations)) {
        allocations.forEach(alloc => {
          console.log('分配数据:', alloc, 'rateCode:', alloc.rateCode)
          if (alloc.rateCode) {
            allocationMap[alloc.rateCode] = alloc
          }
        })
      }
      console.log('分配映射:', allocationMap)
      
      // 基于集团房价码创建数据
      const rateCodeData = []
      if (activeRateCodes && activeRateCodes.length > 0) {
        activeRateCodes.forEach(rateCode => {
          console.log('处理集团房价码:', rateCode.id, rateCode.rateCode)
          const existingAllocation = allocationMap[rateCode.rateCode]
          console.log('找到分配:', existingAllocation)
          rateCodeData.push({
            key: rateCode.id.toString(),
            rateCodeId: rateCode.id,
            rateCode: rateCode.rateName,
            rateCodeValue: rateCode.rateCode,
            rateCategory: rateCode.rateCategory || '',
            derivativeLevel: rateCode.derivativeLevel,
            allocated: existingAllocation ? existingAllocation.allocated : false,
            basicInfoEditable: existingAllocation ? existingAllocation.basicInfoEditable : false,
            priceInfoEditable: existingAllocation ? existingAllocation.priceInfoEditable : false,
            bookingLimitEditable: existingAllocation ? existingAllocation.bookingLimitEditable : false,
            guaranteeRuleEditable: existingAllocation ? existingAllocation.guaranteeRuleEditable : false,
            promotionEditable: existingAllocation ? existingAllocation.promotionEditable : false
          })
        })
      } else {
        // 如果没有集团房价码，使用默认数据
        rateCodeData.push(
          { key: '1', rateCodeId: 1, rateCode: '房价码A', rateCodeValue: 'RACK1', derivativeLevel: 'basic', allocated: false, basicInfoEditable: false, priceInfoEditable: false, bookingLimitEditable: false, guaranteeRuleEditable: false, promotionEditable: false },
          { key: '2', rateCodeId: 2, rateCode: '房价码B', rateCodeValue: 'WEEKEND', derivativeLevel: 'basic', allocated: false, basicInfoEditable: false, priceInfoEditable: false, bookingLimitEditable: false, guaranteeRuleEditable: false, promotionEditable: false }
        )
      }
      
      setRateCodeData(rateCodeData)
      console.log('最终酒店房价码分配数据:', rateCodeData)
    } catch (rateCodeError) {
      console.error('加载酒店房价码分配失败:', rateCodeError)
    }
  }
  
  // 加载酒店房型分配
  const loadRoomTypeAllocations = async (code) => {
    try {
      // 先加载集团房型
      let activeRoomTypes = []
      try {
        const roomTypesResponse = await groupRoomTypeApi.getAllGroupRoomTypes({
          metadata: { skipAutoLogout: true }
        })
        // 处理响应格式
        const roomTypes = roomTypesResponse.success ? roomTypesResponse.data : []
        activeRoomTypes = roomTypes.filter(type => type.status === 'active')
        setGroupRoomTypes(activeRoomTypes)
        console.log('集团房型加载成功:', activeRoomTypes)
      } catch (roomTypeError) {
        console.error('加载集团房型失败:', roomTypeError)
      }
      
      let allocations = []
      try {
        const allocationsResponse = await groupRoomTypeHotelApi.getHotelRoomTypeAllocationsByCode(code, {
          metadata: { skipAutoLogout: true }
        })
        // 处理响应格式
        allocations = allocationsResponse.success ? allocationsResponse.data : []
      } catch (allocError) {
        console.error('加载房型分配失败:', allocError)
        allocations = []
      }
      
      // 创建分配映射
      const allocationMap = {}
      if (Array.isArray(allocations)) {
        allocations.forEach(alloc => {
          allocationMap[alloc.groupRoomTypeCode] = alloc
        })
      }
      
      // 基于集团房型创建数据
      const roomTypeData = []
      if (activeRoomTypes && activeRoomTypes.length > 0) {
        activeRoomTypes.forEach(roomType => {
          const existingAllocation = allocationMap[roomType.roomTypeCode]
          roomTypeData.push({
            key: roomType.id.toString(),
            groupRoomTypeId: roomType.id,
            roomType: roomType.roomTypeName,
            roomTypeCode: roomType.roomTypeCode,
            allocated: existingAllocation ? existingAllocation.allocated : false,
            roomInfoEditable: existingAllocation ? existingAllocation.roomInfoEditable : false
          })
        })
      } else {
        // 如果没有集团房型，使用默认数据
        roomTypeData.push(
          { key: '1', groupRoomTypeId: 1, roomType: '高级大床房', roomTypeCode: 'SUPERIOR_KING', allocated: false, roomInfoEditable: false },
          { key: '2', groupRoomTypeId: 2, roomType: '高级双床房', roomTypeCode: 'SUPERIOR_TWIN', allocated: false, roomInfoEditable: false }
        )
      }
      
      setRoomTypeData(roomTypeData)
      console.log('酒店房型分配数据:', roomTypeData)
    } catch (roomTypeError) {
      console.error('加载酒店房型分配失败:', roomTypeError)
    }
  }
  
  // 处理TAB切换，按需加载数据
  const handleTabChange = async (key) => {
    setActiveTabKey(key)
    
    if (!loadedTabs.has(key) && hotel?.hotelCode) {
      console.log(`加载TAB ${key} 的数据`)
      
      switch (key) {
        case '2':
          await loadHotelFacilities(hotel.hotelCode)
          break
        case '3':
          await loadHotelImages(hotel.hotelCode)
          break
        case '5':
          await loadRateCodeAllocations(hotel.hotelCode)
          break
        case '6':
          await loadRoomTypeAllocations(hotel.hotelCode)
          break
      }
      
      setLoadedTabs(prev => new Set([...prev, key]))
    }
  }
  
  // 保存酒店基本信息
  const saveBasicInfo = async (values) => {
    try {
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
        supportMultiPrice: values.supportMultiPrice || 'no',
        multiPriceOptions: values.multiPriceOptions ? values.multiPriceOptions.join(',') : '',
        supportRoomTypePriceDiff: values.supportRoomTypePriceDiff || 'no',
        supportPersonPriceDiff: values.supportPersonPriceDiff || 'no',
        allowCreateRateCode: values.allowCreateRateCode || 'allow',
        allowCreateRoomType: values.allowCreateRoomType || 'allow'
      }
      
      await hotelApi.updateHotelByCode(hotel.hotelCode, hotelData, {
        metadata: { skipAutoLogout: true }
      })
      
      console.log('酒店基本信息保存成功')
      return true
    } catch (error) {
      console.error('保存酒店基本信息失败:', error)
      message.error('保存酒店基本信息失败，请稍后重试')
      return false
    }
  }
  
  // 保存酒店设施
  const saveFacilities = async (values) => {
    try {
      const selectedFacilities = []
      
      if (values.transportationServices && Array.isArray(values.transportationServices)) {
        values.transportationServices.forEach(service => {
          selectedFacilities.push({
            facilityName: getFacilityName(service),
            facilityCode: service,
            facilityType: '交通服务'
          })
        })
      }
      
      if (values.diningServices && Array.isArray(values.diningServices)) {
        values.diningServices.forEach(service => {
          selectedFacilities.push({
            facilityName: getFacilityName(service),
            facilityCode: service,
            facilityType: '餐饮服务'
          })
        })
      }
      
      if (values.cleaningServices && Array.isArray(values.cleaningServices)) {
        values.cleaningServices.forEach(service => {
          selectedFacilities.push({
            facilityName: getFacilityName(service),
            facilityCode: service,
            facilityType: '清洁服务'
          })
        })
      }
      
      if (values.businessServices && Array.isArray(values.businessServices)) {
        values.businessServices.forEach(service => {
          selectedFacilities.push({
            facilityName: getFacilityName(service),
            facilityCode: service,
            facilityType: '商务服务'
          })
        })
      }
      
      if (values.recreationServices && Array.isArray(values.recreationServices)) {
        values.recreationServices.forEach(service => {
          selectedFacilities.push({
            facilityName: getFacilityName(service),
            facilityCode: service,
            facilityType: '休闲娱乐'
          })
        })
      }
      
      if (values.frontDeskServices && Array.isArray(values.frontDeskServices)) {
        values.frontDeskServices.forEach(service => {
          selectedFacilities.push({
            facilityName: getFacilityName(service),
            facilityCode: service,
            facilityType: '前台服务'
          })
        })
      }
      
      await hotelFacilityApi.deleteHotelFacilitiesByCode(hotel.hotelCode, {
        metadata: { skipAutoLogout: true }
      })
      
      if (selectedFacilities.length > 0) {
        for (const facility of selectedFacilities) {
          const facilityData = {
            hotelCode: hotel.hotelCode,
            facilityName: facility.facilityName,
            facilityCode: facility.facilityCode,
            facilityType: facility.facilityType
          }
          await hotelFacilityApi.createHotelFacility(facilityData, {
            metadata: { skipAutoLogout: true }
          })
        }
      }
      
      console.log('酒店设施保存成功')
      return true
    } catch (error) {
      console.error('保存酒店设施失败:', error)
      message.error('保存酒店设施失败，请稍后重试')
      return false
    }
  }
  
  // 保存酒店房价码分配
  const saveRateCodeAllocations = async () => {
    try {
      await hotelRateCodeAllocationApi.deleteAllocationsByHotelCode(hotel.hotelCode, {
        metadata: { skipAutoLogout: true }
      })
      
      const failedItems = []
      for (const rateCode of rateCodeData) {
        const allocationData = {
          hotelCode: hotel.hotelCode,
          rateCode: rateCode.rateCodeValue,
          allocated: rateCode.allocated,
          basicInfoEditable: rateCode.basicInfoEditable,
          priceInfoEditable: rateCode.priceInfoEditable,
          bookingLimitEditable: rateCode.bookingLimitEditable,
          guaranteeRuleEditable: rateCode.guaranteeRuleEditable,
          promotionEditable: rateCode.promotionEditable
        }
        try {
          await hotelRateCodeAllocationApi.createAllocation(allocationData, {
            metadata: { skipAutoLogout: true }
          })
        } catch (itemError) {
          console.error(`保存房价码 ${rateCode.rateCodeValue} 分配失败:`, itemError)
          failedItems.push(rateCode.rateCodeValue)
        }
      }
      
      if (failedItems.length > 0) {
        message.warning(`以下房价码分配保存失败: ${failedItems.join(', ')}`)
        return false
      }
      
      console.log('酒店房价码分配保存成功')
      return true
    } catch (error) {
      console.error('保存酒店房价码分配失败:', error)
      message.error('保存酒店房价码分配失败，请稍后重试')
      return false
    }
  }
  
  // 保存酒店房型分配
  const saveRoomTypeAllocations = async () => {
    try {
      const roomTypeAllocations = roomTypeData.map(roomType => ({
        groupRoomTypeCode: roomType.roomTypeCode,
        hotelCode: hotel.hotelCode,
        allocated: roomType.allocated,
        roomInfoEditable: roomType.roomInfoEditable
      }))
      
      await groupRoomTypeHotelApi.batchSaveRoomTypeAllocations(roomTypeAllocations, {
        metadata: { skipAutoLogout: true }
      })
      
      console.log('酒店房型分配保存成功')
      return true
    } catch (error) {
      console.error('保存酒店房型分配失败:', error)
      message.error('保存酒店房型分配失败，请稍后重试')
      return false
    }
  }
  
  // 处理保存酒店信息 - 只保存当前TAB的数据
  const handleSave = async (values) => {
    let success = false
    
    try {
      switch (activeTabKey) {
        case '1':
          // 酒店基本信息
          success = await saveBasicInfo(values)
          break
        case '2':
          // 酒店设施
          success = await saveFacilities(values)
          break
        case '3':
          // 酒店图片 - 图片是即时上传的，这里只显示成功消息
          success = true
          message.success('图片已保存')
          break
        case '4':
          // 酒店管控 - 仅包含管控配置
          success = await saveBasicInfo(values)
          break
        case '5':
          // 分配房价码
          success = await saveRateCodeAllocations()
          break
        case '6':
          // 分配房型
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
  
  // 保存并返回列表 - 只保存当前Tab
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
  const handleSaveAndNext = async (values, nextTabKey) => {
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
        if (nextTabKey) {
          setActiveTabKey(nextTabKey)
        }
      }
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
                    <Button type="primary" size="large" onClick={() => form.validateFields().then(values => handleSaveAndReturn(values))}>
                      保存并返回列表
                    </Button>
                    <Button type="default" size="large" onClick={() => form.validateFields().then(values => handleSaveAndNext(values, '2'))}>
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
            {/* 交通服务 */}
            <Form.Item
              name="transportationServices"
              label="交通服务"
            >
              <Checkbox.Group>
                <Space wrap>
                  {groupFacilities
                    .filter(f => f.facilityType === '交通服务')
                    .map(facility => (
                      <Checkbox key={facility.facilityCode} value={facility.facilityCode}>
                        {facility.facilityName}
                      </Checkbox>
                    ))}
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
                  {groupFacilities
                    .filter(f => f.facilityType === '餐饮服务')
                    .map(facility => (
                      <Checkbox key={facility.facilityCode} value={facility.facilityCode}>
                        {facility.facilityName}
                      </Checkbox>
                    ))}
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
                  {groupFacilities
                    .filter(f => f.facilityType === '清洁服务')
                    .map(facility => (
                      <Checkbox key={facility.facilityCode} value={facility.facilityCode}>
                        {facility.facilityName}
                      </Checkbox>
                    ))}
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            {/* 商务服务 */}
            <Form.Item
              name="businessServices"
              label="商务服务"
            >
              <Checkbox.Group>
                <Space wrap>
                  {groupFacilities
                    .filter(f => f.facilityType === '商务服务')
                    .map(facility => (
                      <Checkbox key={facility.facilityCode} value={facility.facilityCode}>
                        {facility.facilityName}
                      </Checkbox>
                    ))}
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            {/* 休闲娱乐 */}
            <Form.Item
              name="recreationServices"
              label="休闲娱乐"
            >
              <Checkbox.Group>
                <Space wrap>
                  {groupFacilities
                    .filter(f => f.facilityType === '休闲娱乐')
                    .map(facility => (
                      <Checkbox key={facility.facilityCode} value={facility.facilityCode}>
                        {facility.facilityName}
                      </Checkbox>
                    ))}
                </Space>
              </Checkbox.Group>
            </Form.Item>
            
            {/* 前台服务 */}
            <Form.Item
              name="frontDeskServices"
              label="前台服务"
            >
              <Checkbox.Group>
                <Space wrap>
                  {groupFacilities
                    .filter(f => f.facilityType === '前台服务')
                    .map(facility => (
                      <Checkbox key={facility.facilityCode} value={facility.facilityCode}>
                        {facility.facilityName}
                      </Checkbox>
                    ))}
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
                  <Button type="primary" size="large" onClick={() => form.validateFields().then(values => handleSaveAndReturn(values))}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => form.validateFields().then(values => handleSaveAndNext(values, '3'))}>
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
          <Form form={form} layout="vertical">
            {/* 酒店店图 */}
            <Form.Item
              name="hotelLogo"
              label="酒店店图"
            >
              <Upload
                customRequest={async (options) => {
                  const { file, data } = options;
                  const formData = new FormData();
                  formData.append('file', file);
                  formData.append('hotelCode', data.hotelCode);
                  formData.append('imageType', data.imageType);
                  
                  try {
                    const response = await hotelImageApi.uploadHotelImage(formData, {
                      metadata: { skipAutoLogout: true }
                    });
                    options.onSuccess(response);
                    const newImage = {
                      uid: response.id,
                      name: file.name,
                      status: 'done',
                      url: `/api/hotel-images/view/${response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      logo: [newImage]
                    }));
                    message.success(`${file.name} 上传成功`);
                  } catch (error) {
                    options.onError(error);
                    message.error(`${file.name} 上传失败`);
                    console.error('Upload error:', error);
                  }
                }}
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelCode: hotel?.hotelCode, imageType: 'logo' }}
                fileList={hotelImages.logo}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    logo: prev.logo.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    hotelImageApi.deleteHotelImage(file.uid, {
                      metadata: { skipAutoLogout: true }
                    })
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
                customRequest={async (options) => {
                  const { file, data } = options;
                  const formData = new FormData();
                  formData.append('file', file);
                  formData.append('hotelCode', data.hotelCode);
                  formData.append('imageType', data.imageType);
                  
                  try {
                    const response = await hotelImageApi.uploadHotelImage(formData, {
                      metadata: { skipAutoLogout: true }
                    });
                    options.onSuccess(response);
                    const newImage = {
                      uid: response.id,
                      name: file.name,
                      status: 'done',
                      url: `/api/hotel-images/view/${response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      external: [newImage]
                    }));
                    message.success(`${file.name} 上传成功`);
                  } catch (error) {
                    options.onError(error);
                    message.error(`${file.name} 上传失败`);
                    console.error('Upload error:', error);
                  }
                }}
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelCode: hotel?.hotelCode, imageType: 'external' }}
                fileList={hotelImages.external}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    external: prev.external.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    hotelImageApi.deleteHotelImage(file.uid, {
                      metadata: { skipAutoLogout: true }
                    })
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
                customRequest={async (options) => {
                  const { file, data } = options;
                  const formData = new FormData();
                  formData.append('file', file);
                  formData.append('hotelCode', data.hotelCode);
                  formData.append('imageType', data.imageType);
                  
                  try {
                    const response = await hotelImageApi.uploadHotelImage(formData, {
                      metadata: { skipAutoLogout: true }
                    });
                    options.onSuccess(response);
                    const newImage = {
                      uid: response.id,
                      name: file.name,
                      status: 'done',
                      url: `/api/hotel-images/view/${response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      restaurant: [newImage]
                    }));
                    message.success(`${file.name} 上传成功`);
                  } catch (error) {
                    options.onError(error);
                    message.error(`${file.name} 上传失败`);
                    console.error('Upload error:', error);
                  }
                }}
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelCode: hotel?.hotelCode, imageType: 'restaurant' }}
                fileList={hotelImages.restaurant}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    restaurant: prev.restaurant.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    hotelImageApi.deleteHotelImage(file.uid, {
                      metadata: { skipAutoLogout: true }
                    })
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
                customRequest={async (options) => {
                  const { file, data } = options;
                  const formData = new FormData();
                  formData.append('file', file);
                  formData.append('hotelCode', data.hotelCode);
                  formData.append('imageType', data.imageType);
                  
                  try {
                    const response = await hotelImageApi.uploadHotelImage(formData, {
                      metadata: { skipAutoLogout: true }
                    });
                    options.onSuccess(response);
                    const newImage = {
                      uid: response.id,
                      name: file.name,
                      status: 'done',
                      url: `/api/hotel-images/view/${response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      lobby: [newImage]
                    }));
                    message.success(`${file.name} 上传成功`);
                  } catch (error) {
                    options.onError(error);
                    message.error(`${file.name} 上传失败`);
                    console.error('Upload error:', error);
                  }
                }}
                name="file"
                listType="picture-card"
                maxCount={1}
                accept="image/*"
                data={{ hotelCode: hotel?.hotelCode, imageType: 'lobby' }}
                fileList={hotelImages.lobby}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除图片
                  setHotelImages(prev => ({
                    ...prev,
                    lobby: prev.lobby.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除图片
                  if (file.uid) {
                    hotelImageApi.deleteHotelImage(file.uid)
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
            
            {/* 酒店视频 */}
            <Form.Item
              name="hotelVideo"
              label="酒店视频"
            >
              <Upload
                customRequest={async (options) => {
                  const { file, data } = options;
                  const formData = new FormData();
                  formData.append('file', file);
                  formData.append('hotelCode', data.hotelCode);
                  formData.append('imageType', data.imageType);
                  
                  try {
                    const response = await hotelImageApi.uploadHotelImage(formData, {
                      metadata: { skipAutoLogout: true }
                    });
                    options.onSuccess(response);
                    const newVideo = {
                      uid: response.id,
                      name: file.name,
                      status: 'done',
                      url: `/api/hotel-images/view/${response.id}`
                    };
                    setHotelImages(prev => ({
                      ...prev,
                      video: [newVideo]
                    }));
                    message.success(`${file.name} 上传成功`);
                  } catch (error) {
                    options.onError(error);
                    message.error(`${file.name} 上传失败`);
                    console.error('Upload error:', error);
                  }
                }}
                name="file"
                listType="text"
                maxCount={1}
                accept="video/*"
                data={{ hotelCode: hotel?.hotelCode, imageType: 'video' }}
                fileList={hotelImages.video}
                onRemove={(file) => {
                  console.log('Remove file:', file);
                  // 从前端状态中移除视频
                  setHotelImages(prev => ({
                    ...prev,
                    video: prev.video.filter(item => item.uid !== file.uid)
                  }));
                  // 调用后端 API 删除视频
                  if (file.uid) {
                    hotelImageApi.deleteHotelImage(file.uid, {
                      metadata: { skipAutoLogout: true }
                    })
                      .then(() => {
                        message.success('视频删除成功');
                      })
                      .catch(error => {
                        console.error('删除视频失败:', error);
                        message.error('删除视频失败，请稍后重试');
                      });
                  }
                }}
              >
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传视频</div>
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
                  <Button type="primary" size="large" onClick={() => handleSaveAndReturn({})}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => handleSaveAndNext({}, '4')}>
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
          <Form form={form} layout="vertical">
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
            
            {/* 多人价配置 */}
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
              <Col span={12}>
                <Form.Item
                  name="supportMultiPrice"
                  label="酒店支持多人价"
                >
                  <Radio.Group 
                    defaultValue="no" 
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
            
            {/* 价差配置 */}
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
              <Col span={12}>
                <Form.Item
                  name="supportRoomTypePriceDiff"
                  label="支持房型价差"
                >
                  <Radio.Group defaultValue="no">
                    <Radio value="yes">是</Radio>
                    <Radio value="no">否</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="supportPersonPriceDiff"
                  label="支持人数价差"
                >
                  <Radio.Group defaultValue="no">
                    <Radio value="yes">是</Radio>
                    <Radio value="no">否</Radio>
                  </Radio.Group>
                </Form.Item>
              </Col>
            </Row>
            
            {/* 保存按钮 */}
            <Form.Item style={{ marginTop: 32 }}>
              <div style={{ textAlign: 'center' }}>
                <Space>
                  <Button size="large" onClick={() => window.location.href = '/group-management/hotel-management'}>
                    <LeftOutlined /> 返回
                  </Button>
                  <Button type="primary" size="large" onClick={() => form.validateFields().then(values => handleSaveAndReturn(values))}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => form.validateFields().then(values => handleSaveAndNext(values, '5'))}>
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
      key: '5',
      label: '分配房价码',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 1200 }}>
          <Form form={form} layout="vertical">
            {/* 房价大类筛选 */}
            <div style={{ marginBottom: 16 }}>
              <span style={{ marginRight: 8 }}>房价大类筛选：</span>
              <Select
                value={filterRateCategory}
                onChange={setFilterRateCategory}
                style={{ width: 250 }}
                allowClear
                placeholder="全部房价大类"
              >
                {[...new Set(rateCodeData.map(r => r.rateCategory).filter(Boolean))].map(cat => {
                  const count = rateCodeData.filter(r => r.rateCategory === cat).length
                  return (
                    <Option key={cat} value={cat}>{cat}（{count}个）</Option>
                  )
                })}
              </Select>
            </div>
            {/* 分配房价码表格 */}
            <Form.Item style={{ marginBottom: 32 }}>
              <Table
                columns={[
                  {
                    title: '房价码',
                    dataIndex: 'rateCode',
                    key: 'rateCode',
                    width: 250,
                    render: (text, record) => {
                      const getDerivativeLevelInfo = (level) => {
                        switch (level) {
                          case 'basic':
                            return { label: '基础', color: 'blue' }
                          case 'level1':
                          case '一级衍生':
                            return { label: '一级衍生', color: 'green' }
                          case 'level2':
                          case '二级衍生':
                            return { label: '二级衍生', color: 'orange' }
                          default:
                            return { label: '基础', color: 'blue' }
                        }
                      }
                      
                      const levelInfo = getDerivativeLevelInfo(record.derivativeLevel)
                      
                      return (
                        <div style={{ position: 'relative', display: 'inline-block', paddingTop: 12 }}>
                          <span>
                            <strong>{text}</strong>
                            <span style={{ color: '#999', marginLeft: 4 }}>({record.rateCodeValue})</span>
                          </span>
                          <span style={{ 
                            position: 'absolute',
                            top: 0,
                            right: -4,
                            padding: '0px 3px', 
                            borderRadius: 2, 
                            backgroundColor: levelInfo.color === 'blue' ? '#e6f7ff' : 
                                           levelInfo.color === 'green' ? '#f6ffed' : '#fff7e6',
                            color: levelInfo.color === 'blue' ? '#1890ff' : 
                                   levelInfo.color === 'green' ? '#52c41a' : '#fa8c16',
                            fontSize: 8,
                            fontWeight: 500,
                            lineHeight: 1.2,
                            whiteSpace: 'nowrap'
                          }}>
                            {levelInfo.label}
                          </span>
                        </div>
                      )
                    }
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
                dataSource={filterRateCategory ? rateCodeData.filter(r => r.rateCategory === filterRateCategory) : rateCodeData}
                pagination={false}
                bordered
                size="middle"
              />
            </Form.Item>
            
            {/* 保存按钮 */}
            <Form.Item style={{ marginTop: 32 }}>
              <div style={{ textAlign: 'center' }}>
                <Space>
                  <Button size="large" onClick={() => window.location.href = '/group-management/hotel-management'}>
                    <LeftOutlined /> 返回
                  </Button>
                  <Button type="primary" size="large" onClick={() => handleSaveAndReturn({})}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => handleSaveAndNext({}, '6')}>
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
      key: '6',
      label: '分配房型',
      children: (
        <Card style={{ marginBottom: 24, maxWidth: 800 }}>
          <Form form={form} layout="vertical">
            {/* 分配房型表格 */}
            <Form.Item style={{ marginBottom: 32 }}>
              <Table
                columns={[
                  {
                    title: '房型',
                    dataIndex: 'roomType',
                    key: 'roomType',
                    width: 250,
                    render: (text, record) => (
                      <span>{record.roomType}（{record.roomTypeCode}）</span>
                    )
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
                  <Button type="primary" size="large" onClick={() => handleSaveAndReturn({})}>
                    保存并返回列表
                  </Button>
                  <Button type="default" size="large" onClick={() => handleSave({})}>
                    保存
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
      
      <Tabs 
        activeKey={activeTabKey} 
        onChange={handleTabChange}
        items={tabItems} 
      />
    </div>
  )
}

export default EditHotel
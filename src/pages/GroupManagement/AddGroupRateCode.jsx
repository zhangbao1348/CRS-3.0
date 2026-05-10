import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Checkbox, Button, Space, Card, Row, Col, Tabs, Tag, Radio, Table, Switch, message, Spin, Modal, DatePicker } from 'antd'
import { PlusOutlined, CloseOutlined, SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import axios from 'axios'
import dayjs from 'dayjs'
import { groupRateCodeApi, hotelApi } from '../../utils/api'
import { getCurrentTenantId } from '../../utils/tenantUtils'

const { Option } = Select

// 日期格式化函数
const formatDate = (date) => {
  if (!date) return null
  
  // 处理字符串格式的日期
  if (typeof date === 'string') {
    return date
  }
  
  // 处理 Moment 对象
  if (date.format) {
    return date.format('YYYY-MM-DD')
  }
  
  // 处理 Date 对象
  if (date instanceof Date && !isNaN(date.getTime())) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }
  
  return null
}

const AddGroupRateCode = () => {
  // 路由管理
  const navigate = useNavigate()
  const location = useLocation()
  
  // 状态管理
  const [form] = Form.useForm()
  const [includedPackages, setIncludedPackages] = useState([])
  const [selectedPackages, setSelectedPackages] = useState([])
  
  // 适用房型选中状态
  const [selectedApplicableRoomTypes, setSelectedApplicableRoomTypes] = useState([])
  
  // 适用房型全选状态
  const [selectAllRoomTypes, setSelectAllRoomTypes] = useState({
    standard: false,
    king: false,
    twin: false,
    suite: false,
    executive: false,
    family: false
  })
  
  // 房型大类与房型的映射关系
  const roomTypeMap = {
    standard: ['standard'],
    king: ['king', 'city-view-king', 'sea-view-king'],
    twin: ['twin', 'city-view-twin', 'sea-view-twin'],
    suite: ['suite', 'deluxe-suite', 'presidential-suite'],
    executive: ['executive', 'executive-suite'],
    family: ['family', 'family-suite']
  }
  const [hotelData, setHotelData] = useState([])
  const [channelPublishData, setChannelPublishData] = useState([
    {
      key: '1',
      channel: ['携程'],
      hotel: ['南风酒店'],
      rateCode: 'OTA双早价',
      roomTypes: ['大床房', '双床房', '高级房'],
      published: true
    },
    {
      key: Date.now(),
      channel: [],
      hotel: [],
      rateCode: 'OTA双早价',
      roomTypes: [],
      published: false
    }
  ])
  // 优惠券和促销规则状态
  const [couponRule, setCouponRule] = useState('unlimited')
  const [promotionRule, setPromotionRule] = useState('unlimited')
  // 积分兑换状态
  const [allowPoints, setAllowPoints] = useState(false)
  const [pointsType, setPointsType] = useState('fixed') // fixed: 固定值积分, rate: 按1元兑换多少积分
  // 当前选择的房价码类型
  const [rateType, setRateType] = useState('basic')
  // API 调用状态
  const [loading, setLoading] = useState(false)
  const [isEditing, setIsEditing] = useState(false)
  const [currentId, setCurrentId] = useState(null)
  const [currentStatus, setCurrentStatus] = useState('active') // 保存当前的状态
  const [parentRateCodes, setParentRateCodes] = useState([])
  const [loadingParentRateCodes, setLoadingParentRateCodes] = useState(false)
  
  // 预订限制状态
  const [personalMembership, setPersonalMembership] = useState([])
  const [companyMembership, setCompanyMembership] = useState([])
  const [advanceBookingMin, setAdvanceBookingMin] = useState(null)
  const [advanceBookingMax, setAdvanceBookingMax] = useState(null)
  const [minimumStayMin, setMinimumStayMin] = useState(null)
  const [minimumStayMax, setMinimumStayMax] = useState(null)
  const [bookingStartTime, setBookingStartTime] = useState(null)
  const [bookingEndTime, setBookingEndTime] = useState(null)
  const [checkinStartTime, setCheckinStartTime] = useState(null)
  const [checkinEndTime, setCheckinEndTime] = useState(null)
  
  // 市场码和来源码状态
  const [marketCodes, setMarketCodes] = useState([])
  const [loadingMarketCodes, setLoadingMarketCodes] = useState(false)
  const [sourceCodes, setSourceCodes] = useState([])
  const [loadingSourceCodes, setLoadingSourceCodes] = useState(false)
  
  // 包价状态
  const [packages, setPackages] = useState([])
  const [loadingPackages, setLoadingPackages] = useState(false)
  
  // 集团房型状态
  const [groupRoomTypes, setGroupRoomTypes] = useState([])
  const [loadingRoomTypes, setLoadingRoomTypes] = useState(false)
  const [roomTypesByCategory, setRoomTypesByCategory] = useState({})
  
  // 担保规则和取消规则状态
  const [guaranteePolicies, setGuaranteePolicies] = useState([])
  const [cancellationPolicies, setCancellationPolicies] = useState([])
  const [loadingPolicies, setLoadingPolicies] = useState(false)
  
  // 房价大类状态
  const [rateCategories, setRateCategories] = useState([])
  const [loadingRateCategories, setLoadingRateCategories] = useState(false)
  
  // 获取可选父级房价码
  const fetchParentRateCodes = async () => {
    try {
      setLoadingParentRateCodes(true)
      
      // 根据当前选择的房价码类型确定目标衍生级别
      let targetDerivativeLevel = ''
      if (rateType === 'level1') {
        // 一级衍生房价的父级是基础房价
        targetDerivativeLevel = 'basic'
      } else if (rateType === 'level2') {
        // 二级衍生房价的父级是一级衍生房价
        targetDerivativeLevel = 'level1'
      }
      
      const excludeId = isEditing && currentId ? currentId : null
      
      console.log('fetchParentRateCodes: rateType =', rateType, ', targetDerivativeLevel =', targetDerivativeLevel)
      const response = await groupRateCodeApi.getSelectableParentRateCodes(targetDerivativeLevel, excludeId)
      console.log('fetchParentRateCodes: response =', response)
      setParentRateCodes(response || [])
    } catch (error) {
      console.error('获取父级房价码失败:', error)
      message.error('获取父级房价码失败')
      setParentRateCodes([])
    } finally {
      setLoadingParentRateCodes(false)
    }
  }

  // 获取第三级市场码
  const fetchThirdLevelMarketCodes = async () => {
    try {
      setLoadingMarketCodes(true)
      const response = await axios.get('/api/market-codes/third-level')
      setMarketCodes(response.data)
    } catch (error) {
      console.error('获取第三级市场码失败:', error)
      setMarketCodes([])
      message.error('获取市场码数据失败，请稍后重试')
    } finally {
      setLoadingMarketCodes(false)
    }
  }

  // 获取第三级来源码
  const fetchThirdLevelSourceCodes = async () => {
    try {
      setLoadingSourceCodes(true)
      const response = await axios.get('/api/source-codes/third-level')
      setSourceCodes(response.data)
    } catch (error) {
      console.error('获取第三级来源码失败:', error)
      setSourceCodes([])
      message.error('获取来源码数据失败，请稍后重试')
    } finally {
      setLoadingSourceCodes(false)
    }
  }

  // 获取包价数据
  const fetchPackages = async () => {
    try {
      setLoadingPackages(true)
      const response = await axios.get('/api/packages')
      setPackages(response.data)
    } catch (error) {
      console.error('获取包价数据失败:', error)
      setPackages([])
      message.error('获取包价数据失败，请稍后重试')
    } finally {
      setLoadingPackages(false)
    }
  }

  // 获取集团房型数据
  const fetchGroupRoomTypes = async () => {
    try {
      setLoadingRoomTypes(true)
      const groupId = getCurrentTenantId()
      
      if (!groupId) {
        throw new Error('无法获取当前租户信息')
      }
      
      // 并行获取房型大类和集团房型
      const [categoriesResponse, roomTypesResponse] = await Promise.all([
        axios.get(`/api/room-type-categories/group/${groupId}`),
        axios.get(`/api/group-room-types/group/${groupId}`)
      ])
      
      // 获取房型大类映射
      const categoryList = categoriesResponse.data || []
      const categoryMap = categoryList.reduce((map, cat) => {
        map[cat.id] = cat.categoryName
        return map
      }, {})
      
      // 直接获取当前租户的集团房型
      const filteredRoomTypes = roomTypesResponse.data || []
      setGroupRoomTypes(filteredRoomTypes)
      
      // 按房型大类分组
      const grouped = filteredRoomTypes.reduce((acc, roomType) => {
        const categoryId = roomType.roomTypeCategoryId || 0
        const category = categoryMap[categoryId] || '其他'
        if (!acc[category]) {
          acc[category] = []
        }
        acc[category].push(roomType)
        return acc
      }, {})
      setRoomTypesByCategory(grouped)
    } catch (error) {
      console.error('获取集团房型数据失败:', error)
      setGroupRoomTypes([])
      setRoomTypesByCategory({})
      message.error('获取集团房型数据失败，请稍后重试')
    } finally {
      setLoadingRoomTypes(false)
    }
  }

  // 获取担保规则和取消规则
  const fetchPolicies = async () => {
    try {
      setLoadingPolicies(true)
      const groupId = getCurrentTenantId()
      if (!groupId) {
        throw new Error('无法获取当前租户信息')
      }
      
      // 并行获取担保规则和取消规则
      const [guaranteeResponse, cancellationResponse] = await Promise.all([
        axios.get(`/api/guarantee-policies?tenantId=${groupId}`),
        axios.get(`/api/cancellation-policies?tenantId=${groupId}`)
      ])
      
      setGuaranteePolicies(guaranteeResponse.data)
      setCancellationPolicies(cancellationResponse.data)
    } catch (error) {
      console.error('获取政策数据失败:', error)
      setGuaranteePolicies([])
      setCancellationPolicies([])
      message.error('获取政策数据失败，请稍后重试')
    } finally {
      setLoadingPolicies(false)
    }
  }

  // 获取房价大类数据
  const fetchRateCategories = async () => {
    try {
      setLoadingRateCategories(true)
      const response = await axios.get('/api/rate-types/active')
      setRateCategories(response.data || [])
    } catch (error) {
      console.error('获取房价大类数据失败:', error)
      setRateCategories([])
    } finally {
      setLoadingRateCategories(false)
    }
  }


  
  // 获取路由参数中的编辑数据
  useEffect(() => {
    const record = location.state?.record
    if (record) {
      setIsEditing(true)
      setCurrentId(record.id)
      
      // 从后端重新加载完整数据
      const fetchFullRateCode = async () => {
        try {
          const fullRecord = await groupRateCodeApi.getGroupRateCodeById(record.id)
          console.log('完整数据:', fullRecord)
          
          // 设置状态
          if (fullRecord.rateType) {
            setRateType(fullRecord.rateType)
          } else if (fullRecord.derivativeLevel) {
            // 兼容旧数据
            setRateType(fullRecord.derivativeLevel)
          }
          if (fullRecord.couponRule) {
            setCouponRule(fullRecord.couponRule)
          }
          if (fullRecord.promotionRule) {
            setPromotionRule(fullRecord.promotionRule)
          }
          if (fullRecord.allowPoints !== undefined) {
            setAllowPoints(fullRecord.allowPoints)
          }
          if (fullRecord.pointsType) {
            setPointsType(fullRecord.pointsType)
          }
          
          // 处理包价数据
          if (fullRecord.packages) {
            try {
              const packages = typeof fullRecord.packages === 'string' ? JSON.parse(fullRecord.packages) : fullRecord.packages
              setSelectedPackages(packages)
            } catch (error) {
              console.error('解析包价数据失败:', error)
              setSelectedPackages([])
            }
          }
          
          // 处理适用房型数据
          if (fullRecord.applicableRoomTypes) {
            try {
              const roomTypes = typeof fullRecord.applicableRoomTypes === 'string' ? JSON.parse(fullRecord.applicableRoomTypes) : fullRecord.applicableRoomTypes
              setSelectedApplicableRoomTypes(roomTypes)
            } catch (error) {
              console.error('解析适用房型数据失败:', error)
              setSelectedApplicableRoomTypes([])
            }
          }
          
          // 填充表单数据
          const status = fullRecord.status === 'active' ? 'active' : 'inactive'
          setCurrentStatus(status)
          form.setFieldsValue({
            rateName: fullRecord.rateName,
            rateCode: fullRecord.rateCode,
            description: fullRecord.description,
            rateCategory: fullRecord.rateCategory,
            marketCode: fullRecord.marketCode || undefined,
            sourceCode: fullRecord.sourceCode || undefined,
            rateType: fullRecord.rateType,
            parentRateCode: fullRecord.parentRateCode || undefined,
            discount: fullRecord.discount,
            rounding: fullRecord.rounding,
            guaranteeRule: fullRecord.guaranteeRule,
            cancellationRule: fullRecord.cancellationRule
          })
          
          // 处理父级房价码的类型转换
          if (fullRecord.parentRateCode) {
            setParentRateCode(fullRecord.parentRateCode)
          }
          
          // 处理预订限制数据
          if (fullRecord.personalMembership) {
            try {
              const personalMembershipData = typeof fullRecord.personalMembership === 'string' ? JSON.parse(fullRecord.personalMembership) : fullRecord.personalMembership
              setPersonalMembership(personalMembershipData)
            } catch (error) {
              console.error('解析个人会员数据失败:', error)
              setPersonalMembership([])
            }
          }
          
          if (fullRecord.companyMembership) {
            try {
              const companyMembershipData = typeof fullRecord.companyMembership === 'string' ? JSON.parse(fullRecord.companyMembership) : fullRecord.companyMembership
              setCompanyMembership(companyMembershipData)
            } catch (error) {
              console.error('解析企业会员数据失败:', error)
              setCompanyMembership([])
            }
          }
          
          // 设置其他预订限制字段
          setAdvanceBookingMin(fullRecord.advanceBookingMin || null)
          setAdvanceBookingMax(fullRecord.advanceBookingMax || null)
          setMinimumStayMin(fullRecord.minimumStayMin || null)
          setMinimumStayMax(fullRecord.minimumStayMax || null)
          
          // 设置日期字段 - 转换为 dayjs 对象供 DatePicker 使用
          if (fullRecord.bookingStartTime) {
            setBookingStartTime(dayjs(fullRecord.bookingStartTime))
          }
          if (fullRecord.bookingEndTime) {
            setBookingEndTime(dayjs(fullRecord.bookingEndTime))
          }
          if (fullRecord.checkinStartTime) {
            setCheckinStartTime(dayjs(fullRecord.checkinStartTime))
          }
          if (fullRecord.checkinEndTime) {
            setCheckinEndTime(dayjs(fullRecord.checkinEndTime))
          }
        } catch (error) {
          console.error('获取完整房价码数据失败:', error)
          // 如果获取失败，使用列表传来的基本数据
          const status = record.status === '启用' ? 'active' : 'inactive'
          setCurrentStatus(status)
          form.setFieldsValue({
            rateName: record.name,
            rateCode: record.code
          })
        }
      }
      
      fetchFullRateCode()
    }
  }, [location.state, form])
  
  // 获取酒店数据，返回格式化后的酒店列表
  const fetchHotels = async () => {
    try {
      const groupId = getCurrentTenantId()
      if (!groupId) {
        setHotelData([])
        return []
      }
      
      const response = await hotelApi.getHotelsByTenantId(groupId)
      if (response && response.success && response.data && response.data.length > 0) {
        const formattedHotelData = response.data.map((hotel, index) => ({
          key: String(hotel.id || index),
          hotelId: hotel.id,
          hotelCode: hotel.hotelCode,
          hotel: hotel.chineseName,
          region: hotel.province,
          city: hotel.city,
          brand: '',
          allocated: false,
          basicInfoEditable: false,
          priceInfoEditable: false,
          bookingLimitEditable: false,
          guaranteeRuleEditable: false,
          promotionEditable: false
        }))
        setHotelData(formattedHotelData)
        return formattedHotelData
      } else {
        setHotelData([])
        return []
      }
    } catch (error) {
      console.error('获取酒店数据失败:', error)
      setHotelData([])
      return []
    }
  }

  // 加载父级房价码数据
  useEffect(() => {
    if (rateType === 'level1' || rateType === 'level2') {
      fetchParentRateCodes()
    } else {
      setParentRateCodes([])
    }
  }, [rateType, isEditing, currentId])

  // 加载市场码和来源码数据
  useEffect(() => {
    const loadData = async () => {
      fetchThirdLevelMarketCodes()
      fetchThirdLevelSourceCodes()
      fetchPackages()
      fetchGroupRoomTypes()
      fetchPolicies()
      fetchRateCategories()
      await fetchHotels()
    }
    loadData()
  }, [])
  
  // 当编辑模式和currentId设置完成后，加载分配数据
  useEffect(() => {
    console.log('编辑模式或currentId变化，isEditing:', isEditing, 'currentId:', currentId)
    if (isEditing && currentId) {
      console.log('开始加载分配数据...')
      // 如果hotelData还没有加载，先加载酒店数据
      if (hotelData.length === 0) {
        fetchHotels().then(hotelBaseData => {
          if (hotelBaseData.length > 0) {
            fetchAllocations(currentId, hotelBaseData)
          }
        })
      } else {
        fetchAllocations(currentId, hotelData)
      }
    }
  }, [isEditing, currentId])

  // 处理包价选择
  const handlePackageChange = (value) => {
    setSelectedPackages(value)
  }
  
  // 处理房型大类全选
  const handleRoomTypeSelectAll = (category, checked) => {
    let newSelected = [...selectedApplicableRoomTypes]
    
    if (checked) {
      // 全选：添加该大类下的所有房型
      roomTypeMap[category].forEach(roomType => {
        if (!newSelected.includes(roomType)) {
          newSelected.push(roomType)
        }
      })
    } else {
      // 取消全选：移除该大类下的所有房型
      newSelected = newSelected.filter(roomType => !roomTypeMap[category].includes(roomType))
    }
    
    setSelectedApplicableRoomTypes(newSelected)
    
    // 更新所有分类的全选状态
    const newSelectAll = {}
    Object.keys(roomTypeMap).forEach(cat => {
      const allSelected = roomTypeMap[cat].every(roomType => 
        newSelected.includes(roomType)
      )
      newSelectAll[cat] = allSelected
    })
    setSelectAllRoomTypes(newSelectAll)
  }
  
  // 处理单个房型选择
  const handleSingleRoomTypeChange = (roomType, checked) => {
    let newSelected = [...selectedApplicableRoomTypes]
    
    if (checked) {
      if (!newSelected.includes(roomType)) {
        newSelected.push(roomType)
      }
    } else {
      newSelected = newSelected.filter(rt => rt !== roomType)
    }
    
    setSelectedApplicableRoomTypes(newSelected)
    
    // 更新全选状态
    updateSelectAllStatus()
  }
  
  // 更新全选状态
  const updateSelectAllStatus = () => {
    const newSelectAll = { ...selectAllRoomTypes }
    
    Object.keys(roomTypeMap).forEach(category => {
      const allSelected = roomTypeMap[category].every(roomType => 
        selectedApplicableRoomTypes.includes(roomType)
      )
      newSelectAll[category] = allSelected
    })
    
    setSelectAllRoomTypes(newSelectAll)
  }

  // 添加包价
  const handleAddPackage = () => {
    // 模拟添加包价逻辑
    console.log('添加包价')
  }

  // 字段名中英文映射
  const fieldNameMap = {
    rateName: '房价名称',
    rateCategory: '房价大类',
    marketCodeId: '市场码',
    sourceCodeId: '来源码',
    guaranteeRule: '担保规则',
    cancellationRule: '取消规则',
    discount: '折扣',
    rounding: '取整方式',
    couponRule: '优惠券规则',
    promotionRule: '促销规则',
    allowPoints: '积分兑换',
    pointsType: '积分类型',
    pointsValue: '积分值',
    applicableRoomTypes: '适用房型',
    packages: '包价',
    description: '描述',
    advanceBookingMin: '最少提前预订天数',
    advanceBookingMax: '最多提前预订天数',
    minimumStayMin: '最少连住天数',
    minimumStayMax: '最多连住天数',
    personalMembership: '个人会员等级',
    companyMembership: '企业会员等级',
    bookingStartTime: '预订开始时间',
    bookingEndTime: '预订结束时间',
    checkinStartTime: '入住开始时间',
    checkinEndTime: '入住结束时间'
  }
  const translateFields = (fields) => fields.map(f => fieldNameMap[f] || f)

  // 保存并下一步
  // 处理保存
  const handleSave = async () => {
    try {
      setLoading(true)
      const values = await form.validateFields()
      console.log('表单数据:', values)
      
      // 衍生码必填校验
      if (values.rateType === 'level1' || values.rateType === 'level2') {
        if (!values.parentRateCode) {
          message.error('请选择父级房价码')
          setLoading(false)
          return
        }
        if (!values.discount && values.discount !== 0) {
          message.error('请输入折扣')
          setLoading(false)
          return
        }
      }
      
      // 适用房型必选校验
      console.log('适用房型校验:', selectedApplicableRoomTypes)
      if (!selectedApplicableRoomTypes || !Array.isArray(selectedApplicableRoomTypes) || selectedApplicableRoomTypes.length === 0) {
        message.error('请至少选择一个适用房型')
        setLoading(false)
        return
      }
      
      const groupId = getCurrentTenantId()
      
      // 准备提交数据 - 完整字段
      const submitData = {
        rateCode: values.rateCode,
        rateName: values.rateName,
        description: values.description || '',
        status: currentStatus,
        groupId: groupId,
        rateCategory: values.rateCategory || null,
        marketCode: values.marketCode || null,
        sourceCode: values.sourceCode || null,
        rateType: values.rateType || 'basic',
        parentRateCode: values.parentRateCode || null,
        discount: values.discount ? parseFloat(values.discount) : null,
        rounding: values.rounding || null,
        guaranteeRule: values.guaranteeRule || null,
        cancellationRule: values.cancellationRule || null,
        couponRule: couponRule || 'unlimited',
        promotionRule: promotionRule || 'unlimited',
        allowPoints: allowPoints || false,
        pointsType: pointsType || null,
        pointsValue: pointsType === 'fixed' ? parseFloat(form.getFieldValue('pointsValue')) : null,
        derivativeLevel: values.rateType || 'basic',
        applicableRoomTypes: JSON.stringify(selectedApplicableRoomTypes),
        packages: JSON.stringify(selectedPackages),
        // 预订限制
        personalMembership: JSON.stringify(personalMembership),
        companyMembership: JSON.stringify(companyMembership),
        advanceBookingMin: advanceBookingMin,
        advanceBookingMax: advanceBookingMax,
        minimumStayMin: minimumStayMin,
        minimumStayMax: minimumStayMax,
        bookingStartTime: bookingStartTime ? formatDate(bookingStartTime) : null,
        bookingEndTime: bookingEndTime ? formatDate(bookingEndTime) : null,
        checkinStartTime: checkinStartTime ? formatDate(checkinStartTime) : null,
        checkinEndTime: checkinEndTime ? formatDate(checkinEndTime) : null
      }
      
      console.log('提交数据:', submitData)
      
      // 调用后端API
      if (isEditing && currentId) {
        // 编辑模式
        const result = await groupRateCodeApi.updateGroupRateCode(currentId, submitData)
        message.success('房价码更新成功')
        
        // Task 8: 检查是否需要同步到酒店
        if (result && result.syncRequired && result.affectedHotels && result.affectedHotels.length > 0) {
          // 关联查询原则：使用 hotelCode 而非 hotelId
          const selectedHotelCodes = result.affectedHotels.map(h => h.hotelCode)
          Modal.confirm({
            title: '同步确认',
            content: (
              <div>
                <p>该房价码已下发到以下酒店，是否同步更新？</p>
                <ul style={{ maxHeight: 200, overflow: 'auto' }}>
                  {result.affectedHotels.map(h => (
                    // 使用 hotelCode 作为 key，符合CODE关联规范
                    <li key={h.hotelCode}>
                      {h.hotelName}（变更字段：{translateFields(h.diffFields).join('、')}）
                    </li>
                  ))}
                </ul>
              </div>
            ),
            okText: '同步更新',
            cancelText: '跳过',
            onOk: async () => {
              try {
                // 同步时传入 hotelCodes，关联查询原则：使用 CODE
                await groupRateCodeApi.syncToHotels(currentId, selectedHotelCodes)
                message.success('同步成功')
              } catch (syncError) {
                message.error('同步失败: ' + (syncError.response?.data?.error || syncError.message))
              }
              navigate('/group-management/group-rate-code')
            },
            onCancel: () => {
              navigate('/group-management/group-rate-code')
            }
          })
          return
        }
      } else {
        // 创建模式
        await groupRateCodeApi.createGroupRateCode(submitData)
        message.success('房价码创建成功')
      }
      
      // 保存成功后返回列表页面
      setTimeout(() => {
        navigate('/group-management/group-rate-code')
      }, 1000)
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败: ' + (error.response?.data || error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  // 处理Switch开关变化
  const handleSwitchChange = (record, field) => {
    return (checked) => {
      const updatedData = hotelData.map(item => {
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
            // 只有当酒店被分配时，才能修改其他字段
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
      })
      
      setHotelData(updatedData)
      
      // 更新全选状态
      if (field === 'allocated') {
        const allChecked = updatedData.every(item => item.allocated)
        setSelectAll(allChecked)
      } else if (field === 'basicInfoEditable') {
        const allAllocated = updatedData.filter(item => item.allocated)
        const allChecked = allAllocated.every(item => item.basicInfoEditable)
        setSelectAllBasicInfo(allChecked)
      } else if (field === 'priceInfoEditable') {
        const allAllocated = updatedData.filter(item => item.allocated)
        const allChecked = allAllocated.every(item => item.priceInfoEditable)
        setSelectAllPriceInfo(allChecked)
      } else if (field === 'bookingLimitEditable') {
        const allAllocated = updatedData.filter(item => item.allocated)
        const allChecked = allAllocated.every(item => item.bookingLimitEditable)
        setSelectAllBookingLimit(allChecked)
      } else if (field === 'guaranteeRuleEditable') {
        const allAllocated = updatedData.filter(item => item.allocated)
        const allChecked = allAllocated.every(item => item.guaranteeRuleEditable)
        setSelectAllGuaranteeRule(allChecked)
      } else if (field === 'promotionEditable') {
        const allAllocated = updatedData.filter(item => item.allocated)
        const allChecked = allAllocated.every(item => item.promotionEditable)
        setSelectAllPromotion(allChecked)
      }
    }
  }

  // 分配到酒店 - 调用后端 API 保存分配设置
  const handleAllocateToHotel = async () => {
    if (!currentId) {
      message.error('请先保存房价码基础信息')
      return
    }
    try {
      setLoading(true)
      const allocationData = hotelData.map(item => ({
        hotelId: item.hotelId,
        hotelCode: item.hotelCode,
        allocated: item.allocated,
        basicInfoEditable: item.basicInfoEditable,
        priceInfoEditable: item.priceInfoEditable,
        bookingLimitEditable: item.bookingLimitEditable,
        guaranteeRuleEditable: item.guaranteeRuleEditable,
        promotionEditable: item.promotionEditable
      }))
      const result = await groupRateCodeApi.allocate(currentId, allocationData)
      
      // Task 9: 检查重新分配时的差异
      if (result && result.reallocationDiffs && result.reallocationDiffs.length > 0) {
        Modal.info({
          title: '重新下发差异提示',
          content: (
            <div>
              <p>以下酒店的价格计划已按最新集团房价码数据更新：</p>
              <ul style={{ maxHeight: 200, overflow: 'auto' }}>
                {result.reallocationDiffs.map(d => (
                  <li key={d.hotelId}>
                    {d.hotelName}（更新字段：{translateFields(d.diffFields).join('、')}）
                  </li>
                ))}
              </ul>
            </div>
          ),
          okText: '知道了',
          onOk: () => {
            navigate('/group-management/group-rate-code')
          }
        })
      } else {
        message.success('分配设置保存成功')
        navigate('/group-management/group-rate-code')
      }
    } catch (error) {
      console.error('保存分配设置失败:', error)
      // Task 10: 显示衍生码链检查错误
      const errorMsg = error?.error || error?.response?.data?.error || error?.message || '未知错误'
      if (typeof errorMsg === 'string' && errorMsg !== '未知错误') {
        Modal.error({
          title: '下发失败',
          content: errorMsg
        })
      } else {
        message.error('保存分配设置失败: ' + errorMsg)
      }
    } finally {
      setLoading(false)
    }
  }

  // 加载分配状态（编辑模式下切换到分配Tab时调用）
  // 加载分配状态，baseData 为可选的酒店基础数据（避免竞态条件）
  const fetchAllocations = async (rateCodeId, baseData) => {
    if (!rateCodeId) return
    try {
      console.log('=== 开始获取分配状态 ===')
      console.log('rateCodeId:', rateCodeId)
      console.log('API地址:', `/api/group-rate-codes/${rateCodeId}/allocations`)
      const allocations = await groupRateCodeApi.getAllocations(rateCodeId)
      console.log('API响应:', allocations)
      console.log('获取到的分配数据:', allocations)
      console.log('分配数据类型:', typeof allocations)
      console.log('是否是数组:', Array.isArray(allocations))
      
      if (Array.isArray(allocations)) {
        console.log('分配数据长度:', allocations.length)
        // 如果传入了 baseData，直接用它合并；否则用 prev state
        if (baseData) {
          console.log('使用baseData合并，baseData长度:', baseData.length)
          console.log('baseData内容:', baseData)
          const merged = baseData.map(item => {
            console.log('处理酒店:', item.hotelCode, item.hotel)
            // 关联查询原则：使用 hotelCode 匹配，而非 hotelId
            const alloc = allocations.find(a => a.hotelCode && a.hotelCode === item.hotelCode)
            console.log('匹配结果:', alloc ? '找到' : '未找到', '分配数据:', alloc)
            if (alloc) {
              const result = {
                ...item,
                allocated: alloc.allocated || false,
                basicInfoEditable: alloc.basicInfoEditable || false,
                priceInfoEditable: alloc.priceInfoEditable || false,
                bookingLimitEditable: alloc.bookingLimitEditable || false,
                guaranteeRuleEditable: alloc.guaranteeRuleEditable || false,
                promotionEditable: alloc.promotionEditable || false
              }
              console.log('合并后:', result)
              return result
            }
            return item
          })
          console.log('=== 最终合并后的数据 ===')
          console.log(merged)
          setHotelData(merged)
        } else {
          setHotelData(prev => {
            console.log('使用prev state合并:', prev)
            return prev.map(item => {
              // 关联查询原则：使用 hotelCode 匹配，而非 hotelId
              const alloc = allocations.find(a => a.hotelCode && a.hotelCode === item.hotelCode)
              if (alloc) {
                console.log('找到分配:', item.hotelCode, alloc)
                return {
                  ...item,
                  allocated: alloc.allocated || false,
                  basicInfoEditable: alloc.basicInfoEditable || false,
                  priceInfoEditable: alloc.priceInfoEditable || false,
                  bookingLimitEditable: alloc.bookingLimitEditable || false,
                  guaranteeRuleEditable: alloc.guaranteeRuleEditable || false,
                  promotionEditable: alloc.promotionEditable || false
                }
              }
              return item
            })
          })
        }
      } else {
        console.error('分配数据不是数组:', allocations)
      }
    } catch (error) {
      console.error('=== 获取分配状态失败 ===')
      console.error('错误:', error)
      console.error('错误响应:', error.response)
    }
  }

  // 全选/取消全选
  const [selectAll, setSelectAll] = useState(false)
  const [batchModalVisible, setBatchModalVisible] = useState(false)
  const [selectedHotels, setSelectedHotels] = useState([])
  const [selectedLimits, setSelectedLimits] = useState([])
  
  // 渠道发布房型全选
  const [selectAllPublishRoomTypes, setSelectAllPublishRoomTypes] = useState(false)
  // 渠道发布房价全选
  const [selectAllPublishRateCode, setSelectAllPublishRateCode] = useState(false)
  
  // 各可修改列的全选状态
  const [selectAllBasicInfo, setSelectAllBasicInfo] = useState(false)
  const [selectAllPriceInfo, setSelectAllPriceInfo] = useState(false)
  const [selectAllBookingLimit, setSelectAllBookingLimit] = useState(false)
  const [selectAllGuaranteeRule, setSelectAllGuaranteeRule] = useState(false)
  const [selectAllPromotion, setSelectAllPromotion] = useState(false)
  
  // 城市筛选状态
  const [filterCity, setFilterCity] = useState('')
  
  // 根据城市筛选酒店数据
  const filteredHotelData = filterCity ? hotelData.filter(h => h.city === filterCity) : hotelData

  const handleSelectAll = (checked) => {
    setSelectAll(checked)
    const newData = hotelData.map(item => ({
      ...item,
      allocated: checked
    }))
    setHotelData(newData)
  }
  
  // 全选基础信息是否可修改
  const handleSelectAllBasicInfo = (checked) => {
    setSelectAllBasicInfo(checked)
    const newData = hotelData.map(item => {
      if (item.allocated) {
        return { ...item, basicInfoEditable: checked }
      }
      return item
    })
    setHotelData(newData)
  }
  
  // 全选价格信息是否可修改
  const handleSelectAllPriceInfo = (checked) => {
    setSelectAllPriceInfo(checked)
    const newData = hotelData.map(item => {
      if (item.allocated) {
        return { ...item, priceInfoEditable: checked }
      }
      return item
    })
    setHotelData(newData)
  }
  
  // 全选预订限制是否可修改
  const handleSelectAllBookingLimit = (checked) => {
    setSelectAllBookingLimit(checked)
    const newData = hotelData.map(item => {
      if (item.allocated) {
        return { ...item, bookingLimitEditable: checked }
      }
      return item
    })
    setHotelData(newData)
  }
  
  // 全选担保/取消规则是否可修改
  const handleSelectAllGuaranteeRule = (checked) => {
    setSelectAllGuaranteeRule(checked)
    const newData = hotelData.map(item => {
      if (item.allocated) {
        return { ...item, guaranteeRuleEditable: checked }
      }
      return item
    })
    setHotelData(newData)
  }
  
  // 全选促销优惠是否可修改
  const handleSelectAllPromotion = (checked) => {
    setSelectAllPromotion(checked)
    const newData = hotelData.map(item => {
      if (item.allocated) {
        return { ...item, promotionEditable: checked }
      }
      return item
    })
    setHotelData(newData)
  }

  // 批量分配
  const handleBatchAllocate = () => {
    setSelectedHotels([])
    setSelectedLimits([])
    setBatchModalVisible(true)
  }

  const handleBatchConfirm = () => {
    if (selectedHotels.length === 0) {
      message.warning('请至少选择一个酒店')
      return
    }
    
    setHotelData(hotelData.map(item => {
      if (selectedHotels.includes(item.key)) {
        const updatedItem = { ...item, allocated: true }
        selectedLimits.forEach(limit => {
          updatedItem[limit] = true
        })
        return updatedItem
      }
      return item
    }))
    
    setBatchModalVisible(false)
    message.success('批量分配成功')
  }

  const handleBatchCancel = () => {
    setBatchModalVisible(false)
  }

  // 渠道发布房型全选处理函数
  const handleSelectAllPublishRoomTypes = (checked) => {
    setSelectAllPublishRoomTypes(checked)
    setChannelPublishData(channelPublishData.map(item => {
      if (!item.published) {
        return {
          ...item,
          roomTypes: checked ? ['大床房', '双床房', '高级房'] : []
        }
      }
      return item
    }))
  }

  // 渠道发布房价全选处理函数
  const handleSelectAllPublishRateCode = (checked) => {
    setSelectAllPublishRateCode(checked)
    setChannelPublishData(channelPublishData.map(item => {
      if (!item.published) {
        return {
          ...item,
          rateCode: checked ? 'OTA双早价' : ''
        }
      }
      return item
    }))
  }

  // 处理渠道发布数据操作
  const handleAddChannelPublish = () => {
    const newItem = {
      key: Date.now(),
      channel: [],
      hotel: [],
      rateCode: 'OTA双早价',
      roomTypes: [],
      published: false
    }
    setChannelPublishData([...channelPublishData, newItem])
  }

  const handleDeleteChannelPublish = (key) => {
    setChannelPublishData(channelPublishData.filter(item => item.key !== key))
  }

  const handleSaveChannelPublish = (record) => {
    setChannelPublishData(channelPublishData.map(item => {
      if (item.key === record.key) {
        return {
          ...item,
          published: true
        }
      }
      return item
    }))
    message.success('保存发布成功')
  }

  const handleCancelPublish = (record) => {
    setChannelPublishData(channelPublishData.map(item => {
      if (item.key === record.key) {
        return {
          ...item,
          published: false
        }
      }
      return item
    }))
    message.success('取消发布成功')
  }

  const handleChannelChange = (key, value) => {
    setChannelPublishData(channelPublishData.map(item => {
      if (item.key === key) {
        return { ...item, channel: value }
      }
      return item
    }))
  }

  const handleHotelChange = (key, value) => {
    setChannelPublishData(channelPublishData.map(item => {
      if (item.key === key) {
        return { ...item, hotel: value }
      }
      return item
    }))
  }

  const handleRoomTypeChange = (key, type, checked) => {
    setChannelPublishData(channelPublishData.map(item => {
      if (item.key === key) {
      const newRoomTypes = checked 
        ? [...item.roomTypes, type]
        : item.roomTypes.filter(t => t !== type)
      return { ...item, roomTypes: newRoomTypes }
      }
      return item
    }))
  }

  // 定义标签页内容
  const tabItems = [
    {
      key: '1',
      label: '房价码维护',
      children: (
        <Card style={{ marginBottom: 24 }}>
          <Form form={form} layout="vertical" style={{ maxWidth: 800, overflow: 'visible' }}>
            {/* 基础信息 */}
            <h3 style={{ marginBottom: 16, fontWeight: 600 }}>基础信息</h3>
            
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="rateCode"
                  label="房价代码"
                  rules={[
                    { required: true, message: '请输入房价代码' },
                    { pattern: /^[A-Za-z0-9_]+$/, message: '房价代码只能包含英文字母、数字和下划线' }
                  ]}
                >
                  <Input placeholder="请输入房价代码" disabled={isEditing} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="rateName"
                  label="房价名称"
                  rules={[{ required: true, message: '请输入房价名称' }]}
                >
                  <Input placeholder="请输入房价名称" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="rateCategory"
                  label="房价大类"
                >
                  <Select placeholder="请选择房价大类" loading={loadingRateCategories}>
                    {rateCategories.map(cat => (
                      <Option key={cat.id} value={cat.code}>
                        {cat.name}（{cat.code}）
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="marketCode"
                  label="市场码"
                >
                  <Select 
                    placeholder="请选择市场码"
                    loading={loadingMarketCodes}
                  >
                    {marketCodes.map(code => (
                      <Option key={code.id} value={code.code}>
                        {code.name} ({code.code})
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="sourceCode"
                  label="来源码"
                >
                  <Select 
                    placeholder="请选择来源码"
                    loading={loadingSourceCodes}
                  >
                    {sourceCodes.map(code => (
                      <Option key={code.id} value={code.code}>
                        {code.name} ({code.code})
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="rateType"
                  label="类型"
                  initialValue="basic"
                >
                  <Select 
                    placeholder="请选择类型"
                    onChange={(value) => setRateType(value)}
                    defaultValue="basic"
                  >
                    <Option value="basic">基础房价码</Option>
                    <Option value="level1">一级衍生码</Option>
                    <Option value="level2">二级衍生码</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            
            {/* 包价/早餐 */}
            <Form.Item
              name="packages"
              label="包价/早餐"
            >
              <div style={{ marginBottom: 8 }}>
                <Select
                  mode="tags"
                  value={selectedPackages}
                  onChange={handlePackageChange}
                  style={{ width: 'calc(100% - 40px)' }}
                  placeholder="请选择或输入集团包价/早餐"
                  loading={loadingPackages}
                >
                  {packages.map(pkg => (
                    <Option key={pkg.id} value={pkg.code}>
                      {pkg.code} - {pkg.name} (集团包价)
                    </Option>
                  ))}
                </Select>
                <Button
                  type="primary"
                  icon={<PlusOutlined />}
                  style={{ marginLeft: 8 }}
                  onClick={handleAddPackage}
                  size="middle"
                />
              </div>
              <div style={{ marginTop: 8 }}>
                {includedPackages.map(pkg => (
                  <Tag
                    key={pkg.id}
                    closable
                    onClose={() => {
                      setIncludedPackages(includedPackages.filter(item => item.id !== pkg.id))
                      setSelectedPackages(selectedPackages.filter(code => code !== pkg.code))
                    }}
                    style={{ marginRight: 8, marginBottom: 8 }}
                  >
                    {pkg.code} - {pkg.name} (集团包价)
                  </Tag>
                ))}
              </div>
            </Form.Item>
            
            {/* 适用房型 */}
            <Form.Item
              label="适用房型"
            >
              {loadingRoomTypes ? (
                <div style={{ padding: '20px', textAlign: 'center' }}>
                  <Spin size="small" />
                  <span style={{ marginLeft: '8px' }}>加载中...</span>
                </div>
              ) : (
                <div>
                  {Object.entries(roomTypesByCategory).map(([category, roomTypes]) => (
                    <div key={category} style={{ marginBottom: 16 }}>
                      <div style={{ marginBottom: 8, fontWeight: 500, color: '#1890ff' }}>{category}</div>
                      <Space wrap>
                        <Checkbox
                          checked={roomTypes.every(roomType => selectedApplicableRoomTypes.includes(roomType.roomTypeCode))}
                          onChange={(e) => {
                            const roomTypeCodes = roomTypes.map(roomType => roomType.roomTypeCode)
                            if (e.target.checked) {
                              setSelectedApplicableRoomTypes(prev => [...new Set([...prev, ...roomTypeCodes])])
                            } else {
                              setSelectedApplicableRoomTypes(prev => prev.filter(c => !roomTypeCodes.includes(c)))
                            }
                          }}
                          style={{ fontWeight: 500 }}
                        >
                          全选
                        </Checkbox>
                        {roomTypes.map(roomType => (
                          <Checkbox
                            key={roomType.id}
                            checked={selectedApplicableRoomTypes.includes(roomType.roomTypeCode)}
                            onChange={(e) => {
                              if (e.target.checked) {
                                setSelectedApplicableRoomTypes(prev => [...prev, roomType.roomTypeCode])
                              } else {
                                setSelectedApplicableRoomTypes(prev => prev.filter(c => c !== roomType.roomTypeCode))
                              }
                            }}
                          >
                            {roomType.roomTypeName}（{roomType.roomTypeCode}）
                          </Checkbox>
                        ))}
                      </Space>
                    </div>
                  ))}
                  {Object.keys(roomTypesByCategory).length === 0 && (
                    <div style={{ padding: '20px', textAlign: 'center', color: '#999' }}>
                      暂无集团房型数据
                    </div>
                  )}
                </div>
              )}
            </Form.Item>
            
            {/* 价格信息 - 仅当选择衍生房价码时显示 */}
            {(rateType === 'level1' || rateType === 'level2') && (
              <>
                <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>价格信息</h3>
                
                <Row gutter={[16, 16]}>
                  <Col span={12}>
                    <Form.Item
                      name="parentRateCode"
                      label="父级房价码"
                    >
                      <Select 
                        placeholder="请选择父级房价码"
                        loading={loadingParentRateCodes}
                      >
                        {parentRateCodes.map(rateCode => (
                          <Option key={rateCode.id} value={rateCode.rateCode}>
                            {rateCode.rateCode} - {rateCode.rateName}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name="discount"
                      label="折扣"
                    >
                      <Input addonAfter="%" placeholder="请输入折扣" type="number" />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name="rounding"
                      label="取整方式"
                    >
                      <Select placeholder="请选择取整方式">
                        <Option value="round">四舍五入</Option>
                        <Option value="floor">向下取整</Option>
                        <Option value="ceil">向上取整</Option>
                      </Select>
                    </Form.Item>
                  </Col>
                </Row>
              </>
            )}
            
            {/* 预订限制 */}
            <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>预订限制</h3>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>需要个人会员:</div>
              <Space wrap>
                <Checkbox 
                  value="silver" 
                  checked={personalMembership.includes('silver')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setPersonalMembership([...personalMembership, 'silver'])
                    } else {
                      setPersonalMembership(personalMembership.filter(item => item !== 'silver'))
                    }
                  }}
                >银卡</Checkbox>
                <Checkbox 
                  value="gold" 
                  checked={personalMembership.includes('gold')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setPersonalMembership([...personalMembership, 'gold'])
                    } else {
                      setPersonalMembership(personalMembership.filter(item => item !== 'gold'))
                    }
                  }}
                >金卡</Checkbox>
                <Checkbox 
                  value="platinum" 
                  checked={personalMembership.includes('platinum')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setPersonalMembership([...personalMembership, 'platinum'])
                    } else {
                      setPersonalMembership(personalMembership.filter(item => item !== 'platinum'))
                    }
                  }}
                >铂金卡</Checkbox>
                <Checkbox 
                  value="diamond" 
                  checked={personalMembership.includes('diamond')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setPersonalMembership([...personalMembership, 'diamond'])
                    } else {
                      setPersonalMembership(personalMembership.filter(item => item !== 'diamond'))
                    }
                  }}
                >黑金卡</Checkbox>
              </Space>
            </div>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>需要企业会员:</div>
              <Space wrap>
                <Checkbox 
                  value="silver-company" 
                  checked={companyMembership.includes('silver-company')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setCompanyMembership([...companyMembership, 'silver-company'])
                    } else {
                      setCompanyMembership(companyMembership.filter(item => item !== 'silver-company'))
                    }
                  }}
                >银卡</Checkbox>
                <Checkbox 
                  value="gold-company" 
                  checked={companyMembership.includes('gold-company')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setCompanyMembership([...companyMembership, 'gold-company'])
                    } else {
                      setCompanyMembership(companyMembership.filter(item => item !== 'gold-company'))
                    }
                  }}
                >金卡</Checkbox>
                <Checkbox 
                  value="platinum-company" 
                  checked={companyMembership.includes('platinum-company')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setCompanyMembership([...companyMembership, 'platinum-company'])
                    } else {
                      setCompanyMembership(companyMembership.filter(item => item !== 'platinum-company'))
                    }
                  }}
                >铂金卡</Checkbox>
                <Checkbox 
                  value="diamond-company" 
                  checked={companyMembership.includes('diamond-company')}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setCompanyMembership([...companyMembership, 'diamond-company'])
                    } else {
                      setCompanyMembership(companyMembership.filter(item => item !== 'diamond-company'))
                    }
                  }}
                >黑金卡</Checkbox>
              </Space>
            </div>
            
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <div style={{ marginBottom: 8, fontWeight: 500 }}>提前预订天数限制:</div>
                <Space>
                  <Input 
                    placeholder="最小值" 
                    style={{ width: 100 }} 
                    type="number" 
                    value={advanceBookingMin} 
                    onChange={(e) => setAdvanceBookingMin(e.target.value ? parseInt(e.target.value) : null)}
                  />
                  <span>-</span>
                  <Input 
                    placeholder="最大值" 
                    style={{ width: 100 }} 
                    type="number" 
                    value={advanceBookingMax} 
                    onChange={(e) => setAdvanceBookingMax(e.target.value ? parseInt(e.target.value) : null)}
                  />
                </Space>
              </Col>
              <Col span={12}>
                <div style={{ marginBottom: 8, fontWeight: 500 }}>连住天数限制:</div>
                <Space>
                  <Input 
                    placeholder="最小值" 
                    style={{ width: 100 }} 
                    type="number" 
                    value={minimumStayMin} 
                    onChange={(e) => setMinimumStayMin(e.target.value ? parseInt(e.target.value) : null)}
                  />
                  <span>-</span>
                  <Input 
                    placeholder="最大值" 
                    style={{ width: 100 }} 
                    type="number" 
                    value={minimumStayMax} 
                    onChange={(e) => setMinimumStayMax(e.target.value ? parseInt(e.target.value) : null)}
                  />
                </Space>
              </Col>
            </Row>

            <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
              <Col span={12}>
                <div style={{ marginBottom: 8, fontWeight: 500 }}>预订生效时间:</div>
                <Space>
                  <DatePicker 
                    placeholder="开始日期" 
                    style={{ width: 180 }}
                    value={bookingStartTime ? dayjs(bookingStartTime) : null}
                    onChange={(date) => setBookingStartTime(date)}
                  />
                  <span>-</span>
                  <DatePicker 
                    placeholder="结束日期" 
                    style={{ width: 180 }}
                    value={bookingEndTime ? dayjs(bookingEndTime) : null}
                    onChange={(date) => setBookingEndTime(date)}
                  />
                </Space>
              </Col>
              <Col span={12}>
                <div style={{ marginBottom: 8, fontWeight: 500 }}>入住生效时间:</div>
                <Space>
                  <DatePicker 
                    placeholder="开始日期" 
                    style={{ width: 180 }}
                    value={checkinStartTime ? dayjs(checkinStartTime) : null}
                    onChange={(date) => setCheckinStartTime(date)}
                  />
                  <span>-</span>
                  <DatePicker 
                    placeholder="结束日期" 
                    style={{ width: 180 }}
                    value={checkinEndTime ? dayjs(checkinEndTime) : null}
                    onChange={(date) => setCheckinEndTime(date)}
                  />
                </Space>
              </Col>
            </Row>
            
            {/* 担保及取消规则 */}
            <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>担保及取消规则</h3>
            
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Form.Item
                  name="guaranteeRule"
                  label="担保规则"
                  rules={[{ required: true, message: '请选择担保规则' }]}
                >
                  <Select 
                    placeholder="请选择担保规则" 
                    loading={loadingPolicies}
                  >
                    {guaranteePolicies.map(policy => (
                      <Option key={policy.id} value={policy.code}>
                        {policy.name}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="cancellationRule"
                  label="取消规则"
                  rules={[{ required: true, message: '请选择取消规则' }]}
                >
                  <Select 
                    placeholder="请选择取消规则" 
                    loading={loadingPolicies}
                  >
                    {cancellationPolicies.map(policy => (
                      <Option key={policy.id} value={policy.code}>
                        {policy.name}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            
            {/* 促销优惠 */}
            <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>促销优惠</h3>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>可用优惠券:</div>
              <Space wrap>
                <Radio.Group 
                  name="coupon-rule" 
                  value={couponRule}
                  onChange={(e) => setCouponRule(e.target.value)}
                >
                  <Radio value="unlimited">不限制</Radio>
                  <Radio value="limited">限制部分优惠券</Radio>
                  <Radio value="disabled">不可用优惠券</Radio>
                </Radio.Group>
              </Space>
              {couponRule === 'limited' && (
                <div style={{ marginTop: 8 }}>
                  <Space wrap>
                    <Checkbox value="300-30">满300减30</Checkbox>
                    <Checkbox value="500-80">满500打8折</Checkbox>
                  </Space>
                </div>
              )}
            </div>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>可用促销:</div>
              <Space wrap>
                <Radio.Group 
                  name="promotion-rule" 
                  value={promotionRule}
                  onChange={(e) => setPromotionRule(e.target.value)}
                >
                  <Radio value="unlimited">不限制</Radio>
                  <Radio value="limited">限制部分优惠</Radio>
                  <Radio value="disabled">不可用优惠</Radio>
                </Radio.Group>
              </Space>
              {promotionRule === 'limited' && (
                <div style={{ marginTop: 8 }}>
                  <Space wrap>
                    <Checkbox value="promo-300-30">满300减30</Checkbox>
                    <Checkbox value="promo-500-80">满500打8折</Checkbox>
                  </Space>
                </div>
              )}
            </div>
            
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>可用积分兑换:</div>
              <Space wrap>
                <Checkbox 
                  value="allow-points" 
                  checked={allowPoints}
                  onChange={(e) => setAllowPoints(e.target.checked)}
                >
                  允许
                </Checkbox>
              </Space>
              {allowPoints && (
                <div style={{ marginTop: 12, marginLeft: 24 }}>
                  <div style={{ marginBottom: 8, fontWeight: 500 }}>积分兑换方式:</div>
                  <Radio.Group 
                    name="points-type" 
                    value={pointsType}
                    onChange={(e) => setPointsType(e.target.value)}
                  >
                    <Radio value="fixed">固定值积分</Radio>
                    <Radio value="rate">按1元兑换多少积分</Radio>
                  </Radio.Group>
                  {pointsType === 'fixed' && (
                    <div style={{ marginTop: 8 }}>
                      <Input placeholder="请输入固定积分值" type="number" style={{ width: 200 }} />
                    </div>
                  )}
                  {pointsType === 'rate' && (
                    <div style={{ marginTop: 8 }}>
                      <Input placeholder="请输入兑换比例" type="number" style={{ width: 200 }} addonAfter="积分/元" />
                    </div>
                  )}
                </div>
              )}
            </div>
            
            {/* 保存按钮 */}
            <Form.Item style={{ marginTop: 32 }}>
              <Button type="primary" size="large" onClick={handleSave}>
                保存, 并下一步
              </Button>
            </Form.Item>
          </Form>
        </Card>
      )
    },
    {
      key: '2',
      label: '房价码分配',
      children: (
        <Card style={{ marginBottom: 24 }}>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <span style={{ marginRight: 8 }}>城市筛选：</span>
                <Select
                  value={filterCity}
                  onChange={setFilterCity}
                  style={{ width: 150 }}
                  allowClear
                  placeholder="全部城市"
                >
                  {[...new Set(hotelData.map(h => h.city).filter(Boolean))].map(city => {
                    const count = hotelData.filter(h => h.city === city).length
                    return (
                      <Option key={city} value={city}>{city}（{count}家）</Option>
                    )
                  })}
                </Select>
              </div>
              <Button type="primary" onClick={handleBatchAllocate}>
                批量分配
              </Button>
            </div>
            
                <Table
                  columns={[
                    {
                      title: '酒店',
                      dataIndex: 'hotel',
                      key: 'hotel',
                      width: 150
                    },
                    {
                      title: (
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                          <span>是否分配到酒店</span>
                          <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                            <Checkbox
                              checked={filteredHotelData.every(item => item.allocated)}
                              onChange={(e) => {
                                const newData = hotelData.map(item => {
                                  if (filteredHotelData.some(fd => fd.key === item.key)) {
                                    return { ...item, allocated: e.target.checked }
                                  }
                                  return item
                                })
                                setHotelData(newData)
                              }}
                              style={{ marginRight: 6 }}
                            />
                            <span style={{ fontSize: '12px', color: '#52c41a' }}>分配到所有酒店</span>
                          </div>
                        </div>
                      ),
                      dataIndex: 'allocated',
                      key: 'allocated',
                      width: 180,
                      render: (text, record) => (
                        <Switch 
                          checked={text} 
                          onChange={handleSwitchChange(record, 'allocated')}
                        />
                      )
                    },
                    {
                      title: (
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                          <span>基础信息是否可修改</span>
                          <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                            <Checkbox
                              checked={filteredHotelData.filter(item => item.allocated).length > 0 && 
                                      filteredHotelData.filter(item => item.allocated).every(item => item.basicInfoEditable)}
                              onChange={(e) => {
                                const newData = hotelData.map(item => {
                                  if (filteredHotelData.some(fd => fd.key === item.key) && item.allocated) {
                                    return { ...item, basicInfoEditable: e.target.checked }
                                  }
                                  return item
                                })
                                setHotelData(newData)
                              }}
                              style={{ marginRight: 6 }}
                            />
                            <span style={{ fontSize: '12px', color: '#52c41a' }}>全部酒店可以修改</span>
                          </div>
                        </div>
                      ),
                      dataIndex: 'basicInfoEditable',
                      key: 'basicInfoEditable',
                      width: 200,
                      render: (text, record) => (
                        <Switch 
                          checked={text} 
                          onChange={handleSwitchChange(record, 'basicInfoEditable')}
                          disabled={!record.allocated}
                        />
                      )
                    },
                    {
                      title: (
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                          <span>价格信息是否可修改</span>
                          <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                            <Checkbox
                              checked={filteredHotelData.filter(item => item.allocated).length > 0 && 
                                      filteredHotelData.filter(item => item.allocated).every(item => item.priceInfoEditable)}
                              onChange={(e) => {
                                const newData = hotelData.map(item => {
                                  if (filteredHotelData.some(fd => fd.key === item.key) && item.allocated) {
                                    return { ...item, priceInfoEditable: e.target.checked }
                                  }
                                  return item
                                })
                                setHotelData(newData)
                              }}
                              style={{ marginRight: 6 }}
                            />
                            <span style={{ fontSize: '12px', color: '#52c41a' }}>全部酒店可以修改</span>
                          </div>
                        </div>
                      ),
                      dataIndex: 'priceInfoEditable',
                      key: 'priceInfoEditable',
                      width: 200,
                      render: (text, record) => (
                        <Switch 
                          checked={text} 
                          onChange={handleSwitchChange(record, 'priceInfoEditable')}
                          disabled={!record.allocated}
                        />
                      )
                    },
                    {
                      title: (
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                          <span>预订限制是否可修改</span>
                          <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                            <Checkbox
                              checked={filteredHotelData.filter(item => item.allocated).length > 0 && 
                                      filteredHotelData.filter(item => item.allocated).every(item => item.bookingLimitEditable)}
                              onChange={(e) => {
                                const newData = hotelData.map(item => {
                                  if (filteredHotelData.some(fd => fd.key === item.key) && item.allocated) {
                                    return { ...item, bookingLimitEditable: e.target.checked }
                                  }
                                  return item
                                })
                                setHotelData(newData)
                              }}
                              style={{ marginRight: 6 }}
                            />
                            <span style={{ fontSize: '12px', color: '#52c41a' }}>全部酒店可以修改</span>
                          </div>
                        </div>
                      ),
                      dataIndex: 'bookingLimitEditable',
                      key: 'bookingLimitEditable',
                      width: 200,
                      render: (text, record) => (
                        <Switch 
                          checked={text} 
                          onChange={handleSwitchChange(record, 'bookingLimitEditable')}
                          disabled={!record.allocated}
                        />
                      )
                    },
                    {
                      title: (
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                          <span>担保/取消规则是否可修改</span>
                          <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                            <Checkbox
                              checked={filteredHotelData.filter(item => item.allocated).length > 0 && 
                                      filteredHotelData.filter(item => item.allocated).every(item => item.guaranteeRuleEditable)}
                              onChange={(e) => {
                                const newData = hotelData.map(item => {
                                  if (filteredHotelData.some(fd => fd.key === item.key) && item.allocated) {
                                    return { ...item, guaranteeRuleEditable: e.target.checked }
                                  }
                                  return item
                                })
                                setHotelData(newData)
                              }}
                              style={{ marginRight: 6 }}
                            />
                            <span style={{ fontSize: '12px', color: '#52c41a' }}>全部酒店可以修改</span>
                          </div>
                        </div>
                      ),
                      dataIndex: 'guaranteeRuleEditable',
                      key: 'guaranteeRuleEditable',
                      width: 240,
                      render: (text, record) => (
                        <Switch 
                          checked={text} 
                          onChange={handleSwitchChange(record, 'guaranteeRuleEditable')}
                          disabled={!record.allocated}
                        />
                      )
                    },
                    {
                      title: (
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                          <span>促销优惠是否可修改</span>
                          <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                            <Checkbox
                              checked={filteredHotelData.filter(item => item.allocated).length > 0 && 
                                      filteredHotelData.filter(item => item.allocated).every(item => item.promotionEditable)}
                              onChange={(e) => {
                                const newData = hotelData.map(item => {
                                  if (filteredHotelData.some(fd => fd.key === item.key) && item.allocated) {
                                    return { ...item, promotionEditable: e.target.checked }
                                  }
                                  return item
                                })
                                setHotelData(newData)
                              }}
                              style={{ marginRight: 6 }}
                            />
                            <span style={{ fontSize: '12px', color: '#52c41a' }}>全部酒店可以修改</span>
                          </div>
                        </div>
                      ),
                      dataIndex: 'promotionEditable',
                      key: 'promotionEditable',
                      width: 200,
                      render: (text, record) => (
                        <Switch 
                          checked={text} 
                          onChange={handleSwitchChange(record, 'promotionEditable')}
                          disabled={!record.allocated}
                        />
                      )
                    }
                  ]}
                  dataSource={filteredHotelData}
                  pagination={false}
                  bordered
                  size="middle"
                  scroll={{ x: 1600 }}
                />
            <div style={{ marginTop: 16, display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
              <Button onClick={() => navigate('/group-management/group-rate-code')}>
                取消
              </Button>
              <Button 
                type="primary" 
                icon={<SaveOutlined />}
                loading={loading}
                onClick={handleAllocateToHotel}
              >
                保存分配设置
              </Button>
            </div>
        </Card>
      )
    },
    {
      key: '3',
      label: '渠道发布',
      children: (
        <Card style={{ marginBottom: 24 }}>
          <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
            <Button type="primary" style={{ marginRight: 10 }} onClick={handleAddChannelPublish}>
              新增
            </Button>
            <Button type="primary">
              保存发布
            </Button>
          </div>
          
          <Table
            columns={[
              {
                title: '渠道',
                dataIndex: 'channel',
                key: 'channel',
                width: 200,
                render: (text, record) => {
                  if (record.published) {
                    return <span>{text.join(', ')}</span>
                  }
                  return (
                    <Select
                      mode="multiple"
                      style={{ width: '100%' }}
                      placeholder="请选择渠道"
                      value={text}
                      onChange={(value) => handleChannelChange(record.key, value)}
                    >
                      <Option value="携程">携程</Option>
                      <Option value="飞猪">飞猪</Option>
                      <Option value="美团">美团</Option>
                      <Option value="Booking.com">Booking.com</Option>
                    </Select>
                  )
                }
              },
              {
                title: '酒店',
                dataIndex: 'hotel',
                key: 'hotel',
                width: 200,
                render: (text, record) => {
                  if (record.published) {
                    return <span>{text.join(', ')}</span>
                  }
                  return (
                    <Select
                      mode="multiple"
                      style={{ width: '100%' }}
                      placeholder="请选择酒店"
                      value={text}
                      onChange={(value) => handleHotelChange(record.key, value)}
                    >
                      <Option value="南风酒店">南风酒店</Option>
                      <Option value="上海宝丽嘉">上海宝丽嘉</Option>
                      <Option value="杭州钓美">杭州钓美</Option>
                    </Select>
                  )
                }
              },
              {
                title: (
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                    <span>房价</span>
                    <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                      <Checkbox
                        checked={selectAllPublishRateCode}
                        onChange={(e) => handleSelectAllPublishRateCode(e.target.checked)}
                        style={{ marginRight: 6 }}
                      />
                      <span style={{ fontSize: '12px', color: '#52c41a' }}>全选房价</span>
                    </div>
                  </div>
                ),
                dataIndex: 'rateCode',
                key: 'rateCode',
                width: 150
              },
              {
                title: (
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                    <span>房型</span>
                    <div style={{ marginTop: 4, display: 'flex', alignItems: 'center' }}>
                      <Checkbox
                        checked={selectAllPublishRoomTypes}
                        onChange={(e) => handleSelectAllPublishRoomTypes(e.target.checked)}
                        style={{ marginRight: 6 }}
                      />
                      <span style={{ fontSize: '12px', color: '#52c41a' }}>全选房型</span>
                    </div>
                  </div>
                ),
                dataIndex: 'roomTypes',
                key: 'roomTypes',
                width: 300,
                render: (text, record) => (
                  <Space wrap>
                    <Checkbox
                      checked={record.roomTypes.includes('大床房')}
                      onChange={(e) => handleRoomTypeChange(record.key, '大床房', e.target.checked)}
                      disabled={record.published}
                    >
                      大床房
                    </Checkbox>
                    <Checkbox
                      checked={record.roomTypes.includes('双床房')}
                      onChange={(e) => handleRoomTypeChange(record.key, '双床房', e.target.checked)}
                      disabled={record.published}
                    >
                      双床房
                    </Checkbox>
                    <Checkbox
                      checked={record.roomTypes.includes('高级房')}
                      onChange={(e) => handleRoomTypeChange(record.key, '高级房', e.target.checked)}
                      disabled={record.published}
                    >
                      高级房
                    </Checkbox>
                  </Space>
                )
              },
              {
                title: '操作',
                key: 'action',
                width: 150,
                fixed: 'right',
                render: (_, record) => (
                  <Space>
                    {record.published ? (
                      <Button 
                        type="link" 
                        size="small" 
                        onClick={() => handleCancelPublish(record)}
                      >
                        取消发布
                      </Button>
                    ) : (
                      <Button 
                        type="link" 
                        size="small" 
                        danger
                        onClick={() => handleDeleteChannelPublish(record.key)}
                      >
                        删除
                      </Button>
                    )}
                  </Space>
                )
              }
            ]}
            dataSource={channelPublishData}
            pagination={false}
            bordered
            size="middle"
            scroll={{ x: 1000 }}
          />
        </Card>
      )
    }
  ]

  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', overflow: 'auto' }}>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 className="page-title">
          {isEditing ? '编辑集团房价码' : '新增集团房价码'}
        </h1>
        <Button 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/group-management/group-rate-code')}
        >
          返回列表
        </Button>
      </div>
      
      <Tabs 
        defaultActiveKey="1" 
        items={tabItems}
        onChange={(key) => {
          console.log('切换Tab:', key)
          console.log('当前状态 - isEditing:', isEditing, 'currentId:', currentId, 'hotelData长度:', hotelData.length)
          // 切换到分配Tab时，如果是编辑模式则加载分配状态
          if (key === '2' && isEditing && currentId) {
            console.log('切换到分配Tab，强制重新加载分配数据')
            // 先加载酒店数据，确保数据是最新的
            fetchHotels().then(hotelBaseData => {
              console.log('重新加载的酒店数据:', hotelBaseData)
              if (hotelBaseData.length > 0) {
                console.log('准备调用fetchAllocations，currentId:', currentId)
                fetchAllocations(currentId, hotelBaseData)
              }
            })
          }
        }}
      />
      
      {/* 批量分配模态框 */}
      <Modal
        title="批量分配"
        open={batchModalVisible}
        onOk={handleBatchConfirm}
        onCancel={handleBatchCancel}
        width={600}
      >
        <div style={{ marginBottom: 24 }}>
          <h4 style={{ marginBottom: 12 }}>选择酒店</h4>
          <Space wrap>
            {hotelData.map(hotel => (
              <Checkbox
                key={hotel.key}
                checked={selectedHotels.includes(hotel.key)}
                onChange={(e) => {
                  if (e.target.checked) {
                    setSelectedHotels([...selectedHotels, hotel.key])
                  } else {
                    setSelectedHotels(selectedHotels.filter(key => key !== hotel.key))
                  }
                }}
              >
                {hotel.hotel}
              </Checkbox>
            ))}
          </Space>
        </div>
        
        <div>
          <h4 style={{ marginBottom: 12 }}>选择可修改限制</h4>
          <Space wrap>
            <Checkbox
              checked={selectedLimits.includes('basicInfoEditable')}
              onChange={(e) => {
                if (e.target.checked) {
                  setSelectedLimits([...selectedLimits, 'basicInfoEditable'])
                } else {
                  setSelectedLimits(selectedLimits.filter(limit => limit !== 'basicInfoEditable'))
                }
              }}
            >
              基础信息
            </Checkbox>
            <Checkbox
              checked={selectedLimits.includes('priceInfoEditable')}
              onChange={(e) => {
                if (e.target.checked) {
                  setSelectedLimits([...selectedLimits, 'priceInfoEditable'])
                } else {
                  setSelectedLimits(selectedLimits.filter(limit => limit !== 'priceInfoEditable'))
                }
              }}
            >
              价格信息
            </Checkbox>
            <Checkbox
              checked={selectedLimits.includes('bookingLimitEditable')}
              onChange={(e) => {
                if (e.target.checked) {
                  setSelectedLimits([...selectedLimits, 'bookingLimitEditable'])
                } else {
                  setSelectedLimits(selectedLimits.filter(limit => limit !== 'bookingLimitEditable'))
                }
              }}
            >
              预订限制
            </Checkbox>
            <Checkbox
              checked={selectedLimits.includes('guaranteeRuleEditable')}
              onChange={(e) => {
                if (e.target.checked) {
                  setSelectedLimits([...selectedLimits, 'guaranteeRuleEditable'])
                } else {
                  setSelectedLimits(selectedLimits.filter(limit => limit !== 'guaranteeRuleEditable'))
                }
              }}
            >
              担保/取消规则
            </Checkbox>
            <Checkbox
              checked={selectedLimits.includes('promotionEditable')}
              onChange={(e) => {
                if (e.target.checked) {
                  setSelectedLimits([...selectedLimits, 'promotionEditable'])
                } else {
                  setSelectedLimits(selectedLimits.filter(limit => limit !== 'promotionEditable'))
                }
              }}
            >
              促销优惠
            </Checkbox>
          </Space>
        </div>
      </Modal>
    </div>
  )
}

export default AddGroupRateCode
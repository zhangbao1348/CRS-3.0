import { useState, useEffect, useRef } from 'react'
import { App, Form, Input, Select, Checkbox, Button, Space, Card, Row, Col, Tabs, Radio, Table, Switch, Spin, Modal, DatePicker } from 'antd'
import dayjs from 'dayjs'
import { SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import { groupRateCodeApi, hotelApi, marketCodeApi, sourceCodeApi, packageApi, groupRoomTypeApi, guaranteePolicyApi, cancellationPolicyApi, rateTypeApi, dictionaryApi, channelPublishApi, tenantChannelApi } from '../../utils/api'
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
  const { message, modal } = App.useApp()
  
  // 状态管理
  const [form] = Form.useForm()
  const [selectedPackages, setSelectedPackages] = useState([])
  
  // 适用房型选中状态
  const [selectedApplicableRoomTypes, setSelectedApplicableRoomTypes] = useState([])
  
  const [hotelData, setHotelData] = useState([])
  const [regionDictionaryItems, setRegionDictionaryItems] = useState([])
  const [channelPublishData, setChannelPublishData] = useState([])
  const [channels, setChannels] = useState([])
  const [loadingChannels, setLoadingChannels] = useState(false)
  const [channelTabLoading, setChannelTabLoading] = useState(false)

  // 获取渠道数据
  const fetchChannels = async () => {
    try {
      setLoadingChannels(true)
      const tenantId = getCurrentTenantId()
      if (!tenantId) return
      const response = await tenantChannelApi.getAllChannels(tenantId)
      if (response) {
        setChannels(response.filter(c => c.status === 'active'))
      }
    } catch (error) {
      console.error('获取渠道数据失败:', error)
    } finally {
      setLoadingChannels(false)
    }
  }

  // 获取已发布渠道配置
  const fetchPublishRecords = async (rateCode) => {
    if (!rateCode) return
    try {
      const records = await channelPublishApi.getGroupRateCodeRecords(rateCode)
      if (Array.isArray(records)) {
        const groupMap = {}
        records.forEach(r => {
          const key = `${r.channelCode}_${r.hotelCode}`
          if (!groupMap[key]) {
            groupMap[key] = {
              key: key,
              channel: [r.channelCode],
              hotel: [r.hotelCode],
              rateCode: rateCode,
              roomTypes: [],
              published: r.status === 'published'
            }
          }
          groupMap[key].roomTypes.push(r.roomTypeCode)
        })
        setChannelPublishData(Object.values(groupMap))
      }
    } catch (error) {
      console.error('获取渠道发布记录失败:', error)
    }
  }

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
  const [allocationLoading, setAllocationLoading] = useState(false)
  const [recordLoading, setRecordLoading] = useState(Boolean(location.state?.record))
  const recordRequestRef = useRef(0)
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
      
      const response = await groupRateCodeApi.getSelectableParentRateCodes(targetDerivativeLevel, excludeId)
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
      const data = await marketCodeApi.getThirdLevelMarketCodes()
      setMarketCodes(data || [])
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
      const data = await sourceCodeApi.getThirdLevelSourceCodes()
      setSourceCodes(data || [])
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
      const data = await packageApi.getAllPackages()
      setPackages(data || [])
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
      
      const roomTypesData = await groupRoomTypeApi.getGroupRoomTypesByGroupId(groupId)
      
      // 直接获取当前租户的集团房型
      const filteredRoomTypes = roomTypesData || []
      setGroupRoomTypes(filteredRoomTypes)
      
      // 按房型大类分组，直接使用后端返回的 categoryName
      const grouped = filteredRoomTypes.reduce((acc, roomType) => {
        const category = roomType.roomTypeCategory?.categoryName || '其他'
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
      const [guaranteeData, cancellationData] = await Promise.all([
        guaranteePolicyApi.getAllGuaranteePolicies(groupId),
        cancellationPolicyApi.getAllCancellationPolicies(groupId)
      ])
      
      setGuaranteePolicies(guaranteeData || [])
      setCancellationPolicies(cancellationData || [])
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
      const data = await rateTypeApi.getActiveRateTypes()
      setRateCategories(data || [])
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
    const requestId = ++recordRequestRef.current
    if (record) {
      setRecordLoading(true)
      setIsEditing(true)
      setCurrentId(record.id)
      
      // 从后端重新加载完整数据
      const fetchFullRateCode = async () => {
        try {
          const fullRecord = await groupRateCodeApi.getGroupRateCodeById(record.id)
          if (requestId !== recordRequestRef.current) return
          
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
          if (requestId !== recordRequestRef.current) return
          console.error('获取完整房价码数据失败:', error)
          // 如果获取失败，使用列表传来的基本数据
          const status = record.status === '启用' ? 'active' : 'inactive'
          setCurrentStatus(status)
          form.setFieldsValue({
            rateName: record.name,
            rateCode: record.code
          })
        } finally {
          if (requestId === recordRequestRef.current) {
            setRecordLoading(false)
          }
        }
      }
      
      fetchFullRateCode()
    } else {
      setRecordLoading(false)
    }

    return () => {
      if (recordRequestRef.current === requestId) {
        recordRequestRef.current += 1
      }
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
          hotelCode: hotel.hotelCode,
          hotel: hotel.chineseName,
          region: hotel.region || '',
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

  const fetchRegionDictionaryItems = async () => {
    try {
      const response = await dictionaryApi.getActiveDictionaryItems('REGION')
      const items = Array.isArray(response) ? response : (response.data || [])
      setRegionDictionaryItems(items)
    } catch (error) {
      console.error('获取区域字典项失败:', error)
      setRegionDictionaryItems([])
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
      fetchRegionDictionaryItems()
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
  
  // 处理包价选择
  const handlePackageChange = (value) => {
    setSelectedPackages(value)
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
      
      
      // 调用后端API
      if (isEditing && currentId) {
        // 编辑模式
        const result = await groupRateCodeApi.updateGroupRateCode(currentId, submitData)
        message.success('房价码更新成功')
        
        // Task 8: 检查是否需要同步到酒店
        if (result && result.syncRequired && result.affectedHotels && result.affectedHotels.length > 0) {
          // 关联查询原则：使用 hotelCode 而非 hotelId
          const selectedHotelCodes = result.affectedHotels.map(h => h.hotelCode)
          modal.confirm({
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
      if (error?.errorFields) {
        return
      }
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
        modal.info({
          title: '重新下发差异提示',
          content: (
            <div>
              <p>以下酒店的价格计划已按最新集团房价码数据更新：</p>
              <ul style={{ maxHeight: 200, overflow: 'auto' }}>
                {result.reallocationDiffs.map(d => (
                  <li key={d.hotelCode}>
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
        modal.error({
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
      const allocations = await groupRateCodeApi.getAllocations(rateCodeId)
      
      if (Array.isArray(allocations)) {
        // 如果传入了 baseData，直接用它合并；否则用 prev state
        if (baseData) {
          const merged = baseData.map(item => {
            // 关联查询原则：使用 hotelCode 匹配，而非 hotelId
            const alloc = allocations.find(a => a.hotelCode && a.hotelCode === item.hotelCode)
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
              return result
            }
            return item
          })
          setHotelData(merged)
        } else {
          setHotelData(prev => {
            return prev.map(item => {
              // 关联查询原则：使用 hotelCode 匹配，而非 hotelId
              const alloc = allocations.find(a => a.hotelCode && a.hotelCode === item.hotelCode)
              if (alloc) {
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

  const [batchModalVisible, setBatchModalVisible] = useState(false)
  const [selectedHotels, setSelectedHotels] = useState([])
  const [selectedLimits, setSelectedLimits] = useState([])
  
  // 渠道发布房型全选
  const [selectAllPublishRoomTypes, setSelectAllPublishRoomTypes] = useState(false)
  
  // 区域筛选状态
  const [filterRegion, setFilterRegion] = useState('')
  // 城市筛选状态
  const [filterCity, setFilterCity] = useState('')

  const regionFilteredHotelData = filterRegion
    ? hotelData.filter(h => h.region === filterRegion)
    : hotelData

  const filteredHotelData = filterCity
    ? regionFilteredHotelData.filter(h => h.city === filterCity)
    : regionFilteredHotelData

  const regionOptions = [...new Set(hotelData.map(h => h.region).filter(Boolean))]
  const cityOptions = [...new Set(regionFilteredHotelData.map(h => h.city).filter(Boolean))]
  const getRegionLabel = (regionCode) => {
    const matchedItem = regionDictionaryItems.find(item => item.code === regionCode)
    return matchedItem?.name || regionCode
  }

  useEffect(() => {
    if (filterCity && !cityOptions.includes(filterCity)) {
      setFilterCity('')
    }
  }, [filterRegion, filterCity, cityOptions])

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
    const applicableRoomTypesMetadata = groupRoomTypes.filter(rt => selectedApplicableRoomTypes.includes(rt.roomTypeCode))
    const allCodes = applicableRoomTypesMetadata.map(rt => rt.roomTypeCode)
    setChannelPublishData(channelPublishData.map(item => {
      if (!item.published) {
        return {
          ...item,
          roomTypes: checked ? allCodes : []
        }
      }
      return item
    }))
  }


  // 处理渠道发布数据操作
  const handleAddChannelPublish = () => {
    const currentRateCode = form.getFieldValue('rateCode')
    if (!currentRateCode) {
      message.warning('请先在“房价码维护”中输入房价代码')
      return
    }
    const newItem = {
      key: Date.now(),
      channel: [],
      hotel: [],
      rateCode: currentRateCode,
      roomTypes: [],
      published: false
    }
    setChannelPublishData([...channelPublishData, newItem])
  }

  const handleDeleteChannelPublish = (key) => {
    setChannelPublishData(channelPublishData.filter(item => item.key !== key))
  }

  // 批量保存所有发布配置（包含修改已发布的和新增的）
  const handleSaveAllPublishConfigs = async () => {
    const currentRateCode = form.getFieldValue('rateCode')
    if (!currentRateCode) {
      message.warning('无法保存，未获取到有效的房价代码')
      return
    }

    if (channelPublishData.length === 0) {
      message.info('没有配置可以保存')
      return
    }

    // 验证有效性
    for (const config of channelPublishData) {
      if (config.channel.length === 0 || config.hotel.length === 0 || config.roomTypes.length === 0) {
        message.error('请完整填写渠道发布配置（渠道、酒店及房型均为必选）')
        return
      }
    }

    try {
      setLoading(true)
      const payload = {
        rateCode: currentRateCode,
        configs: channelPublishData.map(item => ({
          channels: item.channel,
          hotels: item.hotel,
          roomTypes: item.roomTypes
        }))
      }
      const response = await channelPublishApi.saveGroupRateCodePublish(payload)
      if (response && response.success) {
        message.success(`保存发布配置成功，已同步 ${response.count} 条记录`)
        fetchPublishRecords(currentRateCode)
      } else {
        message.error('保存发布配置失败')
      }
    } catch (error) {
      console.error('保存发布失败:', error)
      message.error(error.message || '保存发布失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  // 取消发布
  const handleCancelPublish = (record) => {
    const currentRateCode = form.getFieldValue('rateCode')
    const hotelCode = record.hotel[0]
    const channelCode = record.channel[0]

    const hotelName = hotelData.find(h => h.hotelCode === hotelCode)?.hotel || hotelCode
    const channelName = channels.find(c => c.channelCode === channelCode)?.channelName || channelCode

    modal.confirm({
      title: '确认取消发布吗？',
      content: `确认要将 ${hotelName} 的 ${currentRateCode} 房价码在 ${channelName} 渠道取消发布吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        try {
          await channelPublishApi.cancelGroupRateCodePublish({
            rateCode: currentRateCode,
            hotelCode: hotelCode,
            channelCode: channelCode
          })
          message.success('取消发布成功')
          fetchPublishRecords(currentRateCode)
        } catch (error) {
          console.error('取消发布失败:', error)
          message.error('取消发布失败，请稍后重试')
        }
      }
    })
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
          <Form
            form={form}
            layout="vertical"
            disabled={recordLoading}
            style={{ maxWidth: 800, overflow: 'visible' }}
          >
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
              <Button
                type="primary"
                size="large"
                onClick={handleSave}
                loading={recordLoading || loading}
                disabled={recordLoading}
              >
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
              <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                <div>
                  <span style={{ marginRight: 8 }}>区域筛选：</span>
                  <Select
                    value={filterRegion}
                    onChange={setFilterRegion}
                    style={{ width: 160 }}
                    allowClear
                    placeholder="全部区域"
                  >
                    {regionOptions.map(region => {
                      const count = hotelData.filter(h => h.region === region).length
                      return (
                        <Option key={region} value={region}>{getRegionLabel(region)}（{count}家）</Option>
                      )
                    })}
                  </Select>
                </div>
                <div>
                  <span style={{ marginRight: 8 }}>城市筛选：</span>
                  <Select
                    value={filterCity}
                    onChange={setFilterCity}
                    style={{ width: 150 }}
                    allowClear
                    placeholder="全部城市"
                  >
                    {cityOptions.map(city => {
                      const count = regionFilteredHotelData.filter(h => h.city === city).length
                      return (
                        <Option key={city} value={city}>{city}（{count}家）</Option>
                      )
                    })}
                  </Select>
                </div>
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
                          disabled={allocationLoading}
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
                          disabled={allocationLoading || !record.allocated}
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
                          disabled={allocationLoading || !record.allocated}
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
                          disabled={allocationLoading || !record.allocated}
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
                          disabled={allocationLoading || !record.allocated}
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
                          disabled={allocationLoading || !record.allocated}
                        />
                      )
                    }
                  ]}
                  dataSource={allocationLoading ? [] : filteredHotelData}
                  loading={allocationLoading}
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
            <Button
              type="primary"
              style={{ marginRight: 10 }}
              onClick={handleAddChannelPublish}
              loading={channelTabLoading}
              disabled={channelTabLoading}
            >
              新增
            </Button>
            <Button
              type="primary"
              onClick={handleSaveAllPublishConfigs}
              loading={loading}
              disabled={channelTabLoading}
            >
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
                    const names = text.map(code => {
                      const found = channels.find(c => c.channelCode === code)
                      return found ? found.channelName : code
                    })
                    return <span>{names.join(', ')}</span>
                  }
                  return (
                    <Select
                      mode="multiple"
                      virtual={false}
                      style={{ width: '100%' }}
                      placeholder="请选择渠道"
                      value={text}
                      onChange={(value) => handleChannelChange(record.key, value)}
                      loading={loadingChannels || channelTabLoading}
                      disabled={loadingChannels || channelTabLoading}
                    >
                      {channels.map(c => (
                        <Option key={c.channelCode} value={c.channelCode}>{c.channelName}</Option>
                      ))}
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
                    const names = text.map(code => {
                      const found = hotelData.find(h => h.hotelCode === code)
                      return found ? found.hotel : code
                    })
                    return <span>{names.join(', ')}</span>
                  }
                  const allocatedHotels = hotelData.filter(h => h.allocated)
                  return (
                    <Select
                      mode="multiple"
                      virtual={false}
                      style={{ width: '100%' }}
                      placeholder="请选择酒店"
                      value={text}
                      onChange={(value) => handleHotelChange(record.key, value)}
                      loading={channelTabLoading}
                      disabled={channelTabLoading}
                    >
                      {allocatedHotels.map(h => (
                        <Option key={h.hotelCode} value={h.hotelCode}>{h.hotel}</Option>
                      ))}
                    </Select>
                  )
                }
              },
              {
                title: '房价',
                dataIndex: 'rateCode',
                key: 'rateCode',
                width: 150,
                render: (text) => <span>{form.getFieldValue('rateName') || text || '房价计划'}</span>
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
                render: (text, record) => {
                  const applicableRoomTypesMetadata = groupRoomTypes.filter(rt => selectedApplicableRoomTypes.includes(rt.roomTypeCode))
                  return (
                    <Space wrap>
                      {applicableRoomTypesMetadata.map(rt => (
                        <Checkbox
                          key={rt.roomTypeCode}
                          checked={record.roomTypes.includes(rt.roomTypeCode)}
                          onChange={(e) => handleRoomTypeChange(record.key, rt.roomTypeCode, e.target.checked)}
                        >
                          {rt.roomTypeName}
                        </Checkbox>
                      ))}
                    </Space>
                  )
                }
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
          // 切换到分配Tab时，如果是编辑模式则加载分配状态
          if (key === '2' && isEditing && currentId) {
            // 先加载酒店数据，确保数据是最新的
            setAllocationLoading(true)
            fetchHotels()
              .then(hotelBaseData => {
                if (hotelBaseData.length > 0) {
                  return fetchAllocations(currentId, hotelBaseData)
                }
                return undefined
              })
              .finally(() => setAllocationLoading(false))
          }
          // 切换到渠道发布Tab时
          if (key === '3') {
            setChannelTabLoading(true)
            const rateCode = form.getFieldValue('rateCode')
            const tasks = [
              fetchChannels(),
              fetchHotels().then(hotelBaseData => {
                if (isEditing && currentId && hotelBaseData.length > 0) {
                  return fetchAllocations(currentId, hotelBaseData)
                }
                return undefined
              })
            ]
            if (isEditing && rateCode) {
              tasks.push(fetchPublishRecords(rateCode))
            } else {
              setChannelPublishData([])
            }
            Promise.all(tasks).finally(() => setChannelTabLoading(false))
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

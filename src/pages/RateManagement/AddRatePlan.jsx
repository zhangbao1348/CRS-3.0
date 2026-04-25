import React, { useState, useEffect } from 'react'
import { Form, Input, Select, Checkbox, Button, Space, Card, Row, Col, Tag, Radio, Table, Switch, message, Spin, Modal, DatePicker, Tooltip } from 'antd'
import { PlusOutlined, CloseOutlined, SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate, useLocation, useParams } from 'react-router-dom'
import axios from 'axios'
import dayjs from 'dayjs'
import { ratePlanApi, hotelApi, hotelRoomTypeApi } from '../../utils/api'
import { getCurrentTenantId } from '../../utils/tenantUtils'
import { useHotelContext } from '../../contexts/HotelContext'

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

const AddRatePlan = () => {
  // 路由管理
  const navigate = useNavigate()
  const location = useLocation()
  const { id } = useParams()
  
  // 获取当前酒店
  const { selectedHotel, selectedHotelId } = useHotelContext()
  
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
  
  // 优惠券和促销规则状态
  const [couponRule, setCouponRule] = useState('unlimited')
  const [promotionRule, setPromotionRule] = useState('unlimited')
  // 积分兑换状态
  const [allowPoints, setAllowPoints] = useState(false)
  const [pointsType, setPointsType] = useState('fixed') // fixed: 固定值积分, rate: 按1元兑换多少积分
  // 当前选择的价格计划类型
  const [rateType, setRateType] = useState('basic')
  // API 调用状态
  const [loading, setLoading] = useState(false)
  const [isEditing, setIsEditing] = useState(false)
  const [currentId, setCurrentId] = useState(null)
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
  
  // 房价大类状态
  const [rateCategories, setRateCategories] = useState([])
  const [loadingRateCategories, setLoadingRateCategories] = useState(false)
  
  // 集团房型状态
  const [groupRoomTypes, setGroupRoomTypes] = useState([])
  const [loadingRoomTypes, setLoadingRoomTypes] = useState(false)
  const [roomTypesByCategory, setRoomTypesByCategory] = useState({})
  
  // 担保规则和取消规则状态
  const [guaranteePolicies, setGuaranteePolicies] = useState([])
  const [cancellationPolicies, setCancellationPolicies] = useState([])
  const [loadingPolicies, setLoadingPolicies] = useState(false)
  
  // 集团权限控制状态
  const [permissions, setPermissions] = useState(null)
  
  // 获取可选父级价格计划
  const fetchParentRateCodes = async () => {
    try {
      setLoadingParentRateCodes(true)
      const groupId = getCurrentTenantId()
      if (!groupId) {
        setParentRateCodes([])
        return
      }
      
      // 衍生价格计划需要获取基础价格计划作为父级
      const targetDerivativeLevel = 'level1'
      const excludeId = isEditing && currentId ? currentId : null
      
      const response = await ratePlanApi.getSelectableParentRateCodes(groupId, targetDerivativeLevel, excludeId)
      setParentRateCodes(response || [])
    } catch (error) {
      console.error('获取父级价格计划失败:', error)
      message.error('获取父级价格计划失败')
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

  // 获取酒店房型数据
  const fetchHotelRoomTypes = async () => {
    try {
      setLoadingRoomTypes(true)
      
      if (!selectedHotelId) {
        throw new Error('请先选择酒店')
      }
      
      // 获取房型大类
      const groupId = getCurrentTenantId()
      const categoriesResponse = await axios.get(`/api/room-type-categories/group/${groupId}`)
      
      // 获取酒店房型
      const response = await hotelRoomTypeApi.getHotelRoomTypes(selectedHotelId)
      const roomTypes = response?.data || []
      
      // 获取房型大类映射
      const categoryList = categoriesResponse.data || []
      const categoryMap = categoryList.reduce((map, cat) => {
        map[cat.id] = cat.categoryName
        return map
      }, {})
      
      setGroupRoomTypes(roomTypes)
      
      // 按房型大类分组
      const grouped = roomTypes.reduce((acc, roomType) => {
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
      console.error('获取酒店房型数据失败:', error)
      setGroupRoomTypes([])
      setRoomTypesByCategory({})
      message.error('获取酒店房型数据失败，请稍后重试')
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
    const fetchFullRatePlan = async (ratePlanId) => {
      try {
        const response = await ratePlanApi.getRatePlanById(ratePlanId)
        console.log('完整响应:', response)
        const fullRecord = response?.data || response
        console.log('完整数据:', fullRecord)
        
        // 设置状态
        if (fullRecord.rateType) {
          setRateType(fullRecord.rateType)
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
        form.setFieldsValue({
          rateName: fullRecord.rateName,
          rateCode: fullRecord.rateCode,
          description: fullRecord.description,
          status: fullRecord.status === 'active' ? 'active' : 'inactive',
          rateCategory: fullRecord.rateCategory,
          marketCode: fullRecord.marketCodeId ? String(fullRecord.marketCodeId) : undefined,
          sourceCode: fullRecord.sourceCodeId ? String(fullRecord.sourceCodeId) : undefined,
          rateType: fullRecord.rateType,
          parentRateCode: fullRecord.parentRateCodeId ? String(fullRecord.parentRateCodeId) : undefined,
          discount: fullRecord.discount,
          rounding: fullRecord.rounding,
          guaranteeRule: fullRecord.guaranteeRule,
          cancellationRule: fullRecord.cancellationRule,
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
        console.error('获取完整价格计划数据失败:', error)
        message.error('获取价格计划数据失败')
      }
    }
    
    // 检查是否有编辑数据
    let editId = null
    if (id) {
      // 从 URL 参数获取 id
      editId = id
    } else if (location.state?.record) {
      // 从 location.state 获取 id
      editId = location.state.record.id
    }
    
    if (editId) {
      setIsEditing(true)
      setCurrentId(editId)
      fetchFullRatePlan(editId)
      // 获取权限信息
      ratePlanApi.getPermissions(editId).then(res => {
        const permData = res?.data || res
        if (permData) {
          setPermissions(permData)
        }
      }).catch(err => {
        console.error('获取权限信息失败:', err)
      })
    }
  }, [id, location.state, form])

  // 加载数据
  useEffect(() => {
    const loadData = async () => {
      fetchThirdLevelMarketCodes()
      fetchThirdLevelSourceCodes()
      fetchPackages()
      fetchPolicies()
      fetchRateCategories()
    }
    loadData()
  }, [])
  
  // 当选择的酒店变化时，重新获取酒店房型
  useEffect(() => {
    if (selectedHotelId) {
      fetchHotelRoomTypes()
    }
  }, [selectedHotelId])

  // 加载父级价格计划数据
  useEffect(() => {
    if (rateType === 'derivative') {
      fetchParentRateCodes()
    } else {
      setParentRateCodes([])
    }
  }, [rateType, isEditing, currentId])

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

  // 检查酒店CODE数据
  const checkHotelCode = async (hotelId) => {
    try {
      const response = await hotelApi.checkHotelCode(hotelId)
      return response?.data?.exists || false
    } catch (error) {
      console.error('检查酒店CODE失败:', error)
      return false
    }
  }

  // 保存
  const handleSave = async () => {
    try {
      setLoading(true)
      const values = await form.validateFields()
      console.log('表单数据:', values)
      
      // 检查是否选择了酒店
      if (!selectedHotel) {
        message.error('请先选择酒店')
        return
      }
      
      // 检查酒店CODE数据
      const hasHotelCode = await checkHotelCode(selectedHotelId)
      if (!hasHotelCode) {
        message.error('酒店CODE数据不存在，请先配置酒店CODE')
        return
      }
      
      // 准备提交数据 - 完整字段
      const submitData = {
        hotelCode: selectedHotel,
        hotelId: selectedHotelId,
        rateCode: values.rateCode,
        rateName: values.rateName,
        description: values.description || '',
        status: values.status === 'active' ? 'active' : 'inactive',
        rateCategory: values.rateCategory || null,
        marketCodeId: values.marketCode ? parseInt(values.marketCode) : null,
        marketCode: values.marketCode || null,
        sourceCodeId: values.sourceCode ? parseInt(values.sourceCode) : null,
        sourceCode: values.sourceCode || null,
        rateType: values.rateType || 'basic',
        parentRateCodeId: values.parentRateCode ? parseInt(values.parentRateCode) : null,
        parentRateCode: values.parentRateCode ? (parentRateCodes.find(p => String(p.id) === values.parentRateCode)?.rateCode || null) : null,
        discount: values.discount ? parseFloat(values.discount) : null,
        rounding: values.rounding || null,
        guaranteeRule: values.guaranteeRule || null,
        cancellationRule: values.cancellationRule || null,
        couponRule: couponRule || 'unlimited',
        promotionRule: promotionRule || 'unlimited',
        allowPoints: allowPoints || false,
        pointsType: pointsType || null,
        pointsValue: pointsType === 'fixed' ? parseFloat(form.getFieldValue('pointsValue')) : null,
        derivativeLevel: values.rateType === 'derivative' ? 'level1' : 'basic',
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
        checkinEndTime: checkinEndTime ? formatDate(checkinEndTime) : null,
      }
      
      console.log('提交数据:', submitData)
      
      // 调用后端API
      if (isEditing && currentId) {
        // 编辑模式
        await ratePlanApi.updateRatePlan(currentId, submitData)
        message.success('价格计划更新成功')
      } else {
        // 创建模式
        await ratePlanApi.createRatePlan(submitData)
        message.success('价格计划创建成功')
      }
      
      // 保存成功后返回列表页面
      setTimeout(() => {
        navigate('/rate-management/rate-plan')
      }, 1000)
    } catch (error) {
      console.error('保存失败:', error)
      message.error('保存失败: ' + (error.response?.data || error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  // 权限控制辅助函数
  const isGroupDistributed = permissions?.isGroupDistributed === true
  const permissionTooltip = '此字段由集团管控，如需修改请联系集团管理员'
  
  const isFieldDisabled = (permissionKey) => {
    if (!isGroupDistributed || !permissions) return false
    return permissions[permissionKey] === false
  }
  
  // 包装禁用字段的Tooltip
  const withPermissionTooltip = (permissionKey, element) => {
    if (isFieldDisabled(permissionKey)) {
      return (
        <Tooltip title={permissionTooltip}>
          <div>{element}</div>
        </Tooltip>
      )
    }
    return element
  }

  return (
    <div className="fade-in" style={{ padding: '0 24px 24px', minHeight: '100vh', overflow: 'auto' }}>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 className="page-title">
          {isEditing ? '编辑价格计划' : '新增价格计划'}
        </h1>
        <Button 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/rate-management/rate-plan')}
        >
          返回列表
        </Button>
      </div>
      
      <Card style={{ marginBottom: 24 }}>
        <Form form={form} layout="vertical" style={{ maxWidth: 800, overflow: 'visible' }}>
          {/* 基础信息 */}
          <h3 style={{ marginBottom: 16, fontWeight: 600 }}>基础信息</h3>
          
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <Form.Item
                name="rateCode"
                label="价格计划代码"
                rules={[
                  { required: true, message: '请输入价格计划代码' },
                  { pattern: /^[A-Za-z0-9_]+$/, message: '价格计划代码只能包含英文字母、数字和下划线' }
                ]}
              >
                <Input placeholder="请输入价格计划代码" disabled={isEditing} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="rateName"
                label="价格计划名称"
                rules={[{ required: true, message: '请输入价格计划名称' }]}
              >
                {withPermissionTooltip('basicInfoEditable',
                  <Input placeholder="请输入价格计划名称" disabled={isFieldDisabled('basicInfoEditable')} />
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="rateCategory"
                label="价格计划类别"
              >
                {withPermissionTooltip('basicInfoEditable',
                  <Select placeholder="请选择价格计划类别" loading={loadingRateCategories} disabled={isFieldDisabled('basicInfoEditable')}>
                    {rateCategories.map(cat => (
                      <Option key={cat.id} value={cat.code}>
                        {cat.name}（{cat.code}）
                      </Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="marketCode"
                label="市场码"
              >
                {withPermissionTooltip('basicInfoEditable',
                  <Select 
                    placeholder="请选择市场码"
                    loading={loadingMarketCodes}
                    disabled={isFieldDisabled('basicInfoEditable')}
                  >
                    {marketCodes.map(code => (
                      <Option key={code.id} value={String(code.id)}>
                        {code.name} ({code.code})
                      </Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="sourceCode"
                label="来源码"
              >
                {withPermissionTooltip('basicInfoEditable',
                  <Select 
                    placeholder="请选择来源码"
                    loading={loadingSourceCodes}
                    disabled={isFieldDisabled('basicInfoEditable')}
                  >
                    {sourceCodes.map(code => (
                      <Option key={code.id} value={String(code.id)}>
                        {code.name} ({code.code})
                      </Option>
                    ))}
                  </Select>
                )}
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
                  disabled={isGroupDistributed}
                >
                  <Option value="basic">基础价格计划</Option>
                  <Option value="derivative">衍生价格计划</Option>
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
                        checked={roomTypes.every(roomType => selectedApplicableRoomTypes.includes(roomType.id))}
                        onChange={(e) => {
                          const roomTypeIds = roomTypes.map(roomType => roomType.id)
                          if (e.target.checked) {
                            setSelectedApplicableRoomTypes([...selectedApplicableRoomTypes, ...roomTypeIds])
                          } else {
                            setSelectedApplicableRoomTypes(selectedApplicableRoomTypes.filter(id => !roomTypeIds.includes(id)))
                          }
                        }}
                        style={{ fontWeight: 500 }}
                      >
                        全选
                      </Checkbox>
                      {roomTypes.map(roomType => (
                        <Checkbox
                          key={roomType.id}
                          checked={selectedApplicableRoomTypes.includes(roomType.id)}
                          onChange={(e) => {
                            if (e.target.checked) {
                              setSelectedApplicableRoomTypes([...selectedApplicableRoomTypes, roomType.id])
                            } else {
                              setSelectedApplicableRoomTypes(selectedApplicableRoomTypes.filter(id => id !== roomType.id))
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
                    暂无酒店房型数据
                  </div>
                )}
              </div>
            )}
          </Form.Item>
          
          {/* 价格信息 */}
          {rateType === 'derivative' && (
            <>
              <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>价格信息</h3>
              
              <Row gutter={[16, 16]}>
                <Col span={12}>
                  <Form.Item
                    name="parentRateCode"
                    label="父级价格计划"
                  >
                    {withPermissionTooltip('priceInfoEditable',
                      <Select 
                        placeholder="请选择父级价格计划"
                        loading={loadingParentRateCodes}
                        disabled={isFieldDisabled('priceInfoEditable')}
                      >
                        {parentRateCodes.map(rateCode => (
                          <Option key={rateCode.id} value={String(rateCode.id)}>
                            {rateCode.rateCode} - {rateCode.rateName}
                          </Option>
                        ))}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="discount"
                    label="折扣"
                  >
                    {withPermissionTooltip('priceInfoEditable',
                      <Input addonAfter="%" placeholder="请输入折扣" type="number" disabled={isFieldDisabled('priceInfoEditable')} />
                    )}
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="rounding"
                    label="取整方式"
                  >
                    {withPermissionTooltip('priceInfoEditable',
                      <Select placeholder="请选择取整方式" disabled={isFieldDisabled('priceInfoEditable')}>
                        <Option value="round">四舍五入</Option>
                        <Option value="floor">向下取整</Option>
                        <Option value="ceil">向上取整</Option>
                      </Select>
                    )}
                  </Form.Item>
                </Col>
              </Row>
            </>
          )}
          
          {/* 预订限制 */}
          <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>预订限制</h3>
          
          <div style={{ marginBottom: 16 }}>
            <div style={{ marginBottom: 8, fontWeight: 500 }}>需要个人会员:</div>
            {isFieldDisabled('bookingLimitEditable') ? (
              <Tooltip title={permissionTooltip}>
                <div>
                <Space wrap>
                  <Checkbox value="silver" checked={personalMembership.includes('silver')} disabled>银卡</Checkbox>
                  <Checkbox value="gold" checked={personalMembership.includes('gold')} disabled>金卡</Checkbox>
                  <Checkbox value="platinum" checked={personalMembership.includes('platinum')} disabled>铂金卡</Checkbox>
                  <Checkbox value="diamond" checked={personalMembership.includes('diamond')} disabled>黑金卡</Checkbox>
                </Space>
                </div>
              </Tooltip>
            ) : (
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
            )}
          </div>
          
          <div style={{ marginBottom: 16 }}>
            <div style={{ marginBottom: 8, fontWeight: 500 }}>需要企业会员:</div>
            {isFieldDisabled('bookingLimitEditable') ? (
              <Tooltip title={permissionTooltip}>
                <div>
                <Space wrap>
                  <Checkbox value="silver-company" checked={companyMembership.includes('silver-company')} disabled>银卡</Checkbox>
                  <Checkbox value="gold-company" checked={companyMembership.includes('gold-company')} disabled>金卡</Checkbox>
                  <Checkbox value="platinum-company" checked={companyMembership.includes('platinum-company')} disabled>铂金卡</Checkbox>
                  <Checkbox value="diamond-company" checked={companyMembership.includes('diamond-company')} disabled>黑金卡</Checkbox>
                </Space>
                </div>
              </Tooltip>
            ) : (
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
            )}
          </div>
          
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>提前预订天数限制:</div>
              {isFieldDisabled('bookingLimitEditable') ? (
                <Tooltip title={permissionTooltip}>
                  <div>
                  <Space>
                    <Input placeholder="最小值" style={{ width: 100 }} type="number" value={advanceBookingMin} disabled />
                    <span>-</span>
                    <Input placeholder="最大值" style={{ width: 100 }} type="number" value={advanceBookingMax} disabled />
                  </Space>
                  </div>
                </Tooltip>
              ) : (
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
              )}
            </Col>
            <Col span={12}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>连住天数限制:</div>
              {isFieldDisabled('bookingLimitEditable') ? (
                <Tooltip title={permissionTooltip}>
                  <div>
                  <Space>
                    <Input placeholder="最小值" style={{ width: 100 }} type="number" value={minimumStayMin} disabled />
                    <span>-</span>
                    <Input placeholder="最大值" style={{ width: 100 }} type="number" value={minimumStayMax} disabled />
                  </Space>
                  </div>
                </Tooltip>
              ) : (
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
              )}
            </Col>
          </Row>

          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={12}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>预订生效时间:</div>
              {isFieldDisabled('bookingLimitEditable') ? (
                <Tooltip title={permissionTooltip}>
                  <div>
                  <Space>
                    <DatePicker placeholder="开始日期" style={{ width: 180 }} value={bookingStartTime ? dayjs(bookingStartTime) : null} disabled />
                    <span>-</span>
                    <DatePicker placeholder="结束日期" style={{ width: 180 }} value={bookingEndTime ? dayjs(bookingEndTime) : null} disabled />
                  </Space>
                  </div>
                </Tooltip>
              ) : (
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
              )}
            </Col>
            <Col span={12}>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>入住生效时间:</div>
              {isFieldDisabled('bookingLimitEditable') ? (
                <Tooltip title={permissionTooltip}>
                  <div>
                  <Space>
                    <DatePicker placeholder="开始日期" style={{ width: 180 }} value={checkinStartTime ? dayjs(checkinStartTime) : null} disabled />
                    <span>-</span>
                    <DatePicker placeholder="结束日期" style={{ width: 180 }} value={checkinEndTime ? dayjs(checkinEndTime) : null} disabled />
                  </Space>
                  </div>
                </Tooltip>
              ) : (
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
              )}
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
                {withPermissionTooltip('guaranteeRuleEditable',
                  <Select 
                    placeholder="请选择担保规则" 
                    loading={loadingPolicies}
                    disabled={isFieldDisabled('guaranteeRuleEditable')}
                  >
                    {guaranteePolicies.map(policy => (
                      <Option key={policy.id} value={policy.code}>
                        {policy.name}
                      </Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="cancellationRule"
                label="取消规则"
                rules={[{ required: true, message: '请选择取消规则' }]}
              >
                {withPermissionTooltip('guaranteeRuleEditable',
                  <Select 
                    placeholder="请选择取消规则" 
                    loading={loadingPolicies}
                    disabled={isFieldDisabled('guaranteeRuleEditable')}
                  >
                    {cancellationPolicies.map(policy => (
                      <Option key={policy.id} value={policy.code}>
                        {policy.name}
                      </Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
          </Row>
          
          {/* 促销优惠 */}
          <h3 style={{ marginBottom: 16, marginTop: 32, fontWeight: 600 }}>促销优惠</h3>
          
          <div style={{ marginBottom: 16 }}>
            <div style={{ marginBottom: 8, fontWeight: 500 }}>可用优惠券:</div>
            {isFieldDisabled('promotionEditable') ? (
              <Tooltip title={permissionTooltip}>
                <div>
                <Space wrap>
                  <Radio.Group name="coupon-rule" value={couponRule} disabled>
                    <Radio value="unlimited">不限制</Radio>
                    <Radio value="limited">限制部分优惠券</Radio>
                    <Radio value="disabled">不可用优惠券</Radio>
                  </Radio.Group>
                </Space>
                </div>
              </Tooltip>
            ) : (
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
            )}
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
            {isFieldDisabled('promotionEditable') ? (
              <Tooltip title={permissionTooltip}>
                <div>
                <Space wrap>
                  <Radio.Group name="promotion-rule" value={promotionRule} disabled>
                    <Radio value="unlimited">不限制</Radio>
                    <Radio value="limited">限制部分优惠</Radio>
                    <Radio value="disabled">不可用优惠</Radio>
                  </Radio.Group>
                </Space>
                </div>
              </Tooltip>
            ) : (
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
            )}
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
            {isFieldDisabled('promotionEditable') ? (
              <Tooltip title={permissionTooltip}>
                <div>
                <Space wrap>
                  <Checkbox value="allow-points" checked={allowPoints} disabled>允许</Checkbox>
                </Space>
                </div>
              </Tooltip>
            ) : (
            <Space wrap>
              <Checkbox 
                value="allow-points" 
                checked={allowPoints}
                onChange={(e) => setAllowPoints(e.target.checked)}
              >
                允许
              </Checkbox>
            </Space>
            )}
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
                    <Form.Item name="pointsValue" noStyle>
                      <Input placeholder="请输入固定积分值" type="number" style={{ width: 200 }} />
                    </Form.Item>
                  </div>
                )}
                {pointsType === 'rate' && (
                  <div style={{ marginTop: 8 }}>
                    <Form.Item name="pointsValue" noStyle>
                      <Input placeholder="请输入兑换比例" type="number" style={{ width: 200 }} addonAfter="积分/元" />
                    </Form.Item>
                  </div>
                )}
              </div>
            )}
          </div>
          
          {/* 保存按钮 */}
          <Form.Item style={{ marginTop: 32 }}>
            <Button type="primary" size="large" onClick={handleSave} loading={loading}>
              保存
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

export default AddRatePlan

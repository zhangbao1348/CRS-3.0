import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Card, Row, Col, Input, Select, Radio, message, Modal, Spin } from 'antd'
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  DollarOutlined,
  ReloadOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

const { Option } = Select
const { Group: RadioGroup } = Radio

const GroupRateCode = () => {
  const navigate = useNavigate()
  
  // 状态管理
  const [rateCodes, setRateCodes] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [searchParams, setSearchParams] = useState({
    name: '',
    code: '',
    rateCategory: '',
    marketCode: '',
    sourceCode: '',
    type: '',
    promotion: '',
    status: ''
  })
  const [deleteModalVisible, setDeleteModalVisible] = useState(false)
  const [deleteId, setDeleteId] = useState(null)
  
  // 处理新增房价码
  const handleAddRateCode = () => {
    navigate('/group-management/add-rate-code')
  }
  
  // 处理编辑房价码
  const handleEditRateCode = (record) => {
    navigate('/group-management/add-rate-code', { state: { record } })
  }
  
  // 处理查看房价码
  const handleViewRateCode = (record) => {
    // 可以实现查看详情的逻辑
    message.info(`查看房价码: ${record.name}`)
  }
  
  // 处理删除房价码
  const handleDeleteRateCode = (record) => {
    setDeleteId(record.id)
    setDeleteModalVisible(true)
  }
  
  // 确认删除
  const handleConfirmDelete = async () => {
    if (!deleteId) return
    
    try {
      setLoading(true)
      await axios.delete(`http://localhost:8080/api/group-rate-codes/${deleteId}`)
      message.success('房价码删除成功')
      // 重新获取数据
      fetchRateCodes()
      setDeleteModalVisible(false)
    } catch (error) {
      message.error('删除失败: ' + (error.response?.data || '未知错误'))
    } finally {
      setLoading(false)
    }
  }
  
  // 从后端API获取集团房价码数据
  const fetchRateCodes = async () => {
    try {
      setLoading(true)
      setError(null)
      
      // 构建查询参数
      const params = {}
      Object.entries(searchParams).forEach(([key, value]) => {
        if (value) params[key] = value
      })
      
      const response = await axios.get('http://localhost:8080/api/group-rate-codes', {
        params
      })
      
      // 转换数据格式以匹配前端展示需求
      const formattedRateCodes = response.data.map(item => ({
        id: item.id,
        name: item.rateName,
        code: item.rateCode,
        rateCategory: '公共价', // 可以根据实际数据调整
        marketCode: 'MARKET01', // 可以根据实际数据调整
        sourceCode: 'SOURCE01', // 可以根据实际数据调整
        type: '基础房价码', // 可以根据实际数据调整
        status: item.status === 'active' ? '启用' : '停用',
        includeBreakfast: '含单早', // 可以根据实际数据调整
        refundable: '可退', // 可以根据实际数据调整
        guarantee: '无需担保', // 可以根据实际数据调整
        promotion: '不限制' // 可以根据实际数据调整
      }))
      
      setRateCodes(formattedRateCodes)
    } catch (error) {
      setError('获取房价码数据失败')
      message.error('获取数据失败: ' + (error.message || '网络错误'))
    } finally {
      setLoading(false)
    }
  }
  
  // 初始化时获取数据
  useEffect(() => {
    fetchRateCodes()
  }, [])
  
  // 处理搜索参数变化
  const handleSearchParamChange = (key, value) => {
    setSearchParams(prev => ({
      ...prev,
      [key]: value
    }))
  }
  
  // 处理搜索
  const handleSearch = () => {
    fetchRateCodes()
  }
  
  // 处理重置
  const handleReset = () => {
    setSearchParams({
      name: '',
      code: '',
      rateCategory: '',
      marketCode: '',
      sourceCode: '',
      type: '',
      promotion: '',
      status: ''
    })
    fetchRateCodes()
  }
  
  const columns = [
    {
      title: '房价码名称',
      dataIndex: 'name',
      key: 'name',
      width: 180
    },
    {
      title: '房价码代码',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: '房价类别',
      dataIndex: 'rateCategory',
      key: 'rateCategory',
      width: 120
    },
    {
      title: '市场码',
      dataIndex: 'marketCode',
      key: 'marketCode',
      width: 120
    },
    {
      title: '来源码',
      dataIndex: 'sourceCode',
      key: 'sourceCode',
      width: 120
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 120
    },
    {
      title: '早餐',
      dataIndex: 'includeBreakfast',
      key: 'includeBreakfast',
      width: 120
    },
    {
      title: '退改政策',
      dataIndex: 'refundable',
      key: 'refundable',
      width: 120
    },
    {
      title: '担保政策',
      dataIndex: 'guarantee',
      key: 'guarantee',
      width: 150
    },
    {
      title: '促销优惠',
      dataIndex: 'promotion',
      key: 'promotion',
      width: 150
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <span style={{ 
          color: status === '启用' ? '#52c41a' : '#ff4d4f',
          fontWeight: 500
        }}>
          {status}
        </span>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewRateCode(record)}>查看</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditRateCode(record)}>编辑</Button>
          <Button type="link" size="small" icon={<DeleteOutlined />} danger onClick={() => handleDeleteRateCode(record)}>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <DollarOutlined />
        集团房价码管理
      </h1>
      
      {/* 搜索筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="房价码名称" 
              prefix={<SearchOutlined />} 
              allowClear
              value={searchParams.name}
              onChange={(e) => handleSearchParamChange('name', e.target.value)}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input 
              placeholder="房价码代码" 
              allowClear
              value={searchParams.code}
              onChange={(e) => handleSearchParamChange('code', e.target.value)}
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="房价类别" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.rateCategory || undefined}
              onChange={(value) => handleSearchParamChange('rateCategory', value)}
            >
              <Option value="公共价">公共价</Option>
              <Option value="协议价">协议价</Option>
              <Option value="团队价">团队价</Option>
              <Option value="会员价">会员价</Option>
              <Option value="促销价">促销价</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="市场码" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.marketCode || undefined}
              onChange={(value) => handleSearchParamChange('marketCode', value)}
            >
              <Option value="MARKET01">MARKET01</Option>
              <Option value="MARKET02">MARKET02</Option>
              <Option value="MARKET03">MARKET03</Option>
              <Option value="MARKET04">MARKET04</Option>
              <Option value="MARKET05">MARKET05</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="来源码" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.sourceCode || undefined}
              onChange={(value) => handleSearchParamChange('sourceCode', value)}
            >
              <Option value="SOURCE01">SOURCE01</Option>
              <Option value="SOURCE02">SOURCE02</Option>
              <Option value="SOURCE03">SOURCE03</Option>
              <Option value="SOURCE04">SOURCE04</Option>
              <Option value="SOURCE05">SOURCE05</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="类型" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.type || undefined}
              onChange={(value) => handleSearchParamChange('type', value)}
            >
              <Option value="基础房价码">基础房价码</Option>
              <Option value="衍生房价码">衍生房价码</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="促销优惠" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.promotion || undefined}
              onChange={(value) => handleSearchParamChange('promotion', value)}
            >
              <Option value="不限制">不限制</Option>
              <Option value="限制部分优惠">限制部分优惠</Option>
              <Option value="不可用优惠">不可用优惠</Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select 
              placeholder="状态" 
              allowClear 
              style={{ width: '100%' }}
              value={searchParams.status || undefined}
              onChange={(value) => handleSearchParamChange('status', value === '启用' ? 'active' : 'inactive')}
            >
              <Option value="启用">启用</Option>
              <Option value="停用">停用</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={16} lg={12} style={{ textAlign: 'right' }}>
            <Space>
              <Button type="default" onClick={handleReset}>重置</Button>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>搜索</Button>
              <Button 
                icon={<ReloadOutlined />} 
                onClick={fetchRateCodes}
                loading={loading}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 操作按钮区域 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={handleAddRateCode}>
          新增房价码
        </Button>
      </div>

      {/* 房价码列表表格 */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '100px 0' }}>
          <Spin size="large" tip="加载中..." />
        </div>
      ) : error ? (
        <div style={{ textAlign: 'center', padding: '100px 0', color: '#ff4d4f' }}>
          <p>{error}</p>
          <Button type="primary" onClick={fetchRateCodes} style={{ marginTop: 16 }}>
            重新加载
          </Button>
        </div>
      ) : (
        <Table
          columns={columns}
          dataSource={rateCodes}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
          }}
          scroll={{ x: 1000 }}
          locale={{
            emptyText: '暂无集团房价码数据'
          }}
        />
      )}
      
      {/* 删除确认弹窗 */}
      <Modal
        title="确认删除"
        open={deleteModalVisible}
        onOk={handleConfirmDelete}
        onCancel={() => setDeleteModalVisible(false)}
        okText="确认删除"
        cancelText="取消"
        okButtonProps={{ danger: true }}
      >
        <p>确定要删除该集团房价码吗？此操作不可撤销。</p>
      </Modal>
    </div>
  )
}

export default GroupRateCode
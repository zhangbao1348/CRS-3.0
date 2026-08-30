import { useState, useEffect } from 'react'
import { App, Card, Table, Button, Space, Tag, Form, Input, Select, Row, Col, Popconfirm } from 'antd'
import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { archiveApi } from '../../utils/api'

const { Option } = Select

const ArchiveManagement = () => {
  const { message } = App.useApp()
  const [data, setData] = useState([])
  const [filteredData, setFilteredData] = useState([])
  const [loading, setLoading] = useState(false)
  const [form] = Form.useForm()
  const navigate = useNavigate()

  const loadData = async () => {
    try {
      setLoading(true)
      const res = await archiveApi.getAllArchives()
      let list = []
      if (res && res.success) {
        list = res.data || []
      } else if (Array.isArray(res)) {
        list = res
      }

      const formattedList = list.map(item => {
        let rc = item.rateCodes
        if (typeof rc === 'string' && rc.startsWith('[')) {
          try {
            rc = JSON.parse(rc)
          } catch (e) {
            rc = []
          }
        }
        return {
          ...item,
          contact: item.contactName || item.contact,
          phone: item.contactPhone || item.phone,
          rateCodes: Array.isArray(rc) ? rc : []
        }
      })
      setData(formattedList)
      setFilteredData(formattedList)
    } catch (error) {
      console.error('加载档案列表失败:', error)
      setData([])
      setFilteredData([])
      message.error('加载档案列表失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  const handleSearch = (values) => {
    let result = data
    if (values.archiveId) {
      result = result.filter(item => item.archiveId && item.archiveId.includes(values.archiveId))
    }
    if (values.name) {
      result = result.filter(item => item.name && item.name.includes(values.name))
    }
    if (values.type) {
      result = result.filter(item => item.type === values.type)
    }
    if (values.bookingCode) {
      result = result.filter(item => item.bookingCode && item.bookingCode.includes(values.bookingCode))
    }
    if (values.phone) {
      result = result.filter(item => item.phone && item.phone.includes(values.phone))
    }
    if (values.status) {
      result = result.filter(item => item.status === values.status)
    }
    setFilteredData(result)
  }

  const handleReset = () => {
    form.resetFields()
    setFilteredData(data)
  }

  const handleToggleStatus = async (record) => {
    const newStatus = record.status === '启用' ? '停用' : '启用'
    try {
      const updatedRecord = {
        ...record,
        status: newStatus,
        contactName: record.contact,
        contactPhone: record.phone,
        rateCodes: JSON.stringify(record.rateCodes)
      }
      await archiveRepoUpdate(record.id, updatedRecord)
      message.success(`档案已${newStatus}`)
      loadData()
    } catch (error) {
      console.error('更新状态失败:', error)
      message.error('更新状态失败，服务器未保存任何变更')
    }
  }

  // 辅助方法以避免 eslint 或运行期未定义 archiveApi 调用的问题
  const archiveRepoUpdate = async (id, updatedRecord) => {
    return await archiveApi.updateArchive(id, updatedRecord)
  }

  const columns = [
    {
      title: '档案ID',
      dataIndex: 'archiveId',
      key: 'archiveId'
    },
    {
      title: '档案名称',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '预订代码',
      dataIndex: 'bookingCode',
      key: 'bookingCode'
    },
    {
      title: '档案类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => (
        <Tag color={type === '公司' ? 'blue' : 'green'}>
          {type}
        </Tag>
      )
    },

    {
      title: '档案联系人',
      dataIndex: 'contact',
      key: 'contact'
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      key: 'phone'
    },
    {
      title: '档案联系地址',
      dataIndex: 'address',
      key: 'address'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status, record) => (
        <Popconfirm
          title={`确定要${status === '启用' ? '停用' : '启用'}该档案吗？`}
          onConfirm={() => handleToggleStatus(record)}
          okText="确定"
          cancelText="取消"
        >
          <Tag
            color={status === '启用' ? 'success' : 'default'}
            style={{ cursor: 'pointer' }}
          >
            {status}
          </Tag>
        </Popconfirm>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button
          type="link"
          icon={<EditOutlined />}
          onClick={() => handleEdit(record)}
        >
          编辑
        </Button>
      )
    }
  ]

  const handleAdd = () => {
    navigate('/group-management/archive-management/add')
  }

  const handleEdit = (record) => {
    navigate(`/group-management/archive-management/edit/${record.id}`)
  }

  return (
    <div className="fade-in">
      <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', marginBottom: 24 }}>
        <h1 className="page-title" style={{ marginBottom: 0 }}>档案管理</h1>
      </div>

      <Card style={{ marginBottom: 16 }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSearch}
        >
          <Row gutter={16}>
            <Col span={4}>
              <Form.Item name="archiveId" label="档案ID">
                <Input placeholder="请输入档案ID" />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="name" label="档案名称">
                <Input placeholder="请输入档案名称" />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="type" label="档案类型">
                <Select placeholder="请选择档案类型" allowClear>
                  <Option value="公司">公司</Option>
                  <Option value="旅行社">旅行社</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="bookingCode" label="预订代码">
                <Input placeholder="请输入预订代码" />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="phone" label="联系电话">
                <Input placeholder="请输入联系电话" />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="status" label="状态">
                <Select placeholder="请选择状态" allowClear>
                  <Option value="启用">启用</Option>
                  <Option value="停用">停用</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={24} style={{ textAlign: 'right' }}>
              <Space>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                  新增档案
                </Button>
                <Button icon={<SearchOutlined />} type="primary" htmlType="submit">
                  查询
                </Button>
                <Button icon={<ReloadOutlined />} onClick={handleReset}>
                  重置
                </Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </Card>

      <Card>
        <Table
          loading={loading}
          columns={columns}
          dataSource={filteredData}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </div>
  )
}

export default ArchiveManagement

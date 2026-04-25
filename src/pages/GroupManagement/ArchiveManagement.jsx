import React, { useState, useEffect } from 'react'
import { Card, Table, Button, Space, Tag, Form, Input, Select, Row, Col, Popconfirm, message } from 'antd'
import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Option } = Select

const ArchiveManagement = () => {
  const [data, setData] = useState([])
  const [filteredData, setFilteredData] = useState([])
  const [form] = Form.useForm()
  const navigate = useNavigate()

  useEffect(() => {
    const savedData = localStorage.getItem('archiveData')
    if (savedData) {
      const parsedData = JSON.parse(savedData)
      const dataWithLevel = parsedData.map(item => ({
        ...item,
        level: item.level || '银卡'
      }))
      setData(dataWithLevel)
      setFilteredData(dataWithLevel)
      localStorage.setItem('archiveData', JSON.stringify(dataWithLevel))
    } else {
      const initialData = [
        { id: 1, archiveId: 'ARCH001', name: '档案1', type: '公司', contact: '张三', address: '北京市', status: '启用', level: '金卡' },
        { id: 2, archiveId: 'ARCH002', name: '档案2', type: '旅行社', contact: '李四', address: '上海市', status: '启用', level: '银卡' }
      ]
      setData(initialData)
      setFilteredData(initialData)
      localStorage.setItem('archiveData', JSON.stringify(initialData))
    }
  }, [])

  const handleSearch = (values) => {
    let result = data
    if (values.archiveId) {
      result = result.filter(item => item.archiveId.includes(values.archiveId))
    }
    if (values.name) {
      result = result.filter(item => item.name.includes(values.name))
    }
    if (values.type) {
      result = result.filter(item => item.type === values.type)
    }
    if (values.level) {
      result = result.filter(item => item.level === values.level)
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

  const handleToggleStatus = (record) => {
    const newStatus = record.status === '启用' ? '停用' : '启用'
    const updatedData = data.map(item => 
      item.id === record.id ? { ...item, status: newStatus } : item
    )
    setData(updatedData)
    setFilteredData(updatedData)
    localStorage.setItem('archiveData', JSON.stringify(updatedData))
    message.success(`档案已${newStatus}`)
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
      title: '档案等级',
      dataIndex: 'level',
      key: 'level',
      render: (level) => {
        const levelColors = {
          '银卡': 'default',
          '金卡': 'gold',
          '铂金卡': 'orange',
          '黑卡': 'purple'
        }
        return <Tag color={levelColors[level] || 'default'}>{level}</Tag>
      }
    },
    {
      title: '档案联系人',
      dataIndex: 'contact',
      key: 'contact'
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
              <Form.Item name="level" label="档案等级">
                <Select placeholder="请选择档案等级" allowClear>
                  <Option value="银卡">银卡</Option>
                  <Option value="金卡">金卡</Option>
                  <Option value="铂金卡">铂金卡</Option>
                  <Option value="黑卡">黑卡</Option>
                </Select>
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
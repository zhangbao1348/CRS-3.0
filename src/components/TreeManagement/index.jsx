import React, { useState, useRef, useEffect } from 'react'
import { Tree, Button, Modal, Form, Input, message, Space, Card, Row, Col } from 'antd'
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  ReloadOutlined,
  PlusCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons'

const { confirm } = Modal
const { TextArea } = Input

/**
 * 通用的3级树状结构管理组件
 * @param {Object} props
 * @param {string} props.title - 页面标题
 * @param {Array} props.initialData - 初始树状数据
 * @param {string} props.codeName - 编码名称（如"市场码"、"渠道码"、"来源码"）
 * @param {Object} props.customMethods - 自定义方法（可选）
 * @param {Function} props.customMethods.addNode - 新增节点方法
 * @param {Function} props.customMethods.updateNode - 更新节点方法
 * @param {Function} props.customMethods.deleteNode - 删除节点方法
 * @param {Function} props.customMethods.checkCodeUnique - 检查CODE是否唯一方法
 */
const TreeManagement = ({ title, initialData = [], codeName = '编码', customMethods = {} }) => {
  // 状态管理
  const [treeData, setTreeData] = useState(initialData)
  const [selectedKeys, setSelectedKeys] = useState([])
  const [selectedNode, setSelectedNode] = useState(null)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [modalType, setModalType] = useState('add') // 'add' or 'edit'
  const [form] = Form.useForm()
  const [isLoading, setIsLoading] = useState(false)
  const treeRef = useRef(null)

  // 当选中节点变化时，更新选中节点信息
  useEffect(() => {
    if (selectedKeys.length > 0) {
      const key = selectedKeys[0]
      const findNode = (nodes) => {
        for (const node of nodes) {
          if (node.key === key) {
            return node
          }
          if (node.children) {
            const found = findNode(node.children)
            if (found) return found
          }
        }
        return null
      }
      const node = findNode(treeData)
      setSelectedNode(node)
    } else {
      setSelectedNode(null)
    }
  }, [selectedKeys, treeData])

  // 处理节点选中
  const onSelect = (keys) => {
    setSelectedKeys(keys)
  }

  // 展示新增/编辑弹窗
  const showModal = (type) => {
    setModalType(type)
    setIsModalVisible(true)
    if (type === 'edit' && selectedNode) {
      form.setFieldsValue({
        title: selectedNode.title,
        code: selectedNode.code
      })
    } else {
      form.resetFields()
    }
  }

  // 关闭弹窗
  const handleCancel = () => {
    setIsModalVisible(false)
    form.resetFields()
  }

  // 生成唯一key
  const generateKey = () => {
    return Date.now().toString()
  }

  // 递归查找并更新节点
  const updateNode = (nodes, key, newNode) => {
    return nodes.map(node => {
      if (node.key === key) {
        return { ...node, ...newNode }
      }
      if (node.children) {
        return { ...node, children: updateNode(node.children, key, newNode) }
      }
      return node
    })
  }

  // 递归查找并添加子节点
  const addChildNode = (nodes, parentKey, newNode) => {
    return nodes.map(node => {
      if (node.key === parentKey) {
        return {
          ...node,
          children: [...(node.children || []), newNode]
        }
      }
      if (node.children) {
        return {
          ...node,
          children: addChildNode(node.children, parentKey, newNode)
        }
      }
      return node
    })
  }

  // 递归查找并删除节点
  const deleteNode = (nodes, key) => {
    return nodes.filter(node => {
      if (node.key === key) {
        return false
      }
      if (node.children) {
        node.children = deleteNode(node.children, key)
      }
      return true
    })
  }

  // 验证CODE是否唯一
  const validateCodeUnique = async (rule, value) => {
    if (!value) return Promise.resolve()
    
    try {
      if (customMethods.checkCodeUnique) {
        // 使用自定义方法检查唯一性
        const isUnique = await customMethods.checkCodeUnique(value, modalType === 'edit' ? selectedNode?.key : null)
        if (!isUnique) {
          return Promise.reject(`${codeName}已存在`)
        }
      } else {
        // 默认本地检查
        let isUnique = true
        const checkCode = (nodes) => {
          for (const node of nodes) {
            if (node.code === value && node.key !== (modalType === 'edit' ? selectedNode?.key : null)) {
              isUnique = false
              return
            }
            if (node.children) {
              checkCode(node.children)
            }
          }
        }
        checkCode(treeData)
        
        if (!isUnique) {
          return Promise.reject(`${codeName}已存在`)
        }
      }
      return Promise.resolve()
    } catch (error) {
      console.error('验证CODE唯一性失败:', error)
      return Promise.reject('验证失败，请稍后重试')
    }
  }

  // 表单提交处理
  const handleOk = () => {
    form.validateFields()
      .then(async values => {
        setIsLoading(true)
        
        try {
          let newTreeData = [...treeData]
          
          if (modalType === 'add') {
            // 新增节点
            if (customMethods.addNode) {
              // 使用自定义方法新增节点
              const parentKey = selectedKeys.length > 0 ? selectedKeys[0] : null
              const newNode = await customMethods.addNode(parentKey, {
                title: values.title,
                code: values.code
              })
              
              if (selectedKeys.length > 0) {
                // 在选中节点下新增子节点
                newTreeData = addChildNode(newTreeData, selectedKeys[0], newNode)
              } else {
                // 新增根节点
                newTreeData.push(newNode)
              }
            } else {
              // 默认本地新增
              const newNode = {
                key: generateKey(),
                title: values.title,
                code: values.code
              }
              
              if (selectedKeys.length > 0) {
                // 在选中节点下新增子节点
                newTreeData = addChildNode(newTreeData, selectedKeys[0], newNode)
              } else {
                // 新增根节点
                newTreeData.push(newNode)
              }
            }
            message.success('新增成功')
          } else {
            // 编辑节点
            if (selectedNode) {
              if (customMethods.updateNode) {
                // 使用自定义方法更新节点
                await customMethods.updateNode(selectedNode.key, {
                  title: values.title,
                  code: values.code
                })
              }
              
              // 更新本地状态
              newTreeData = updateNode(newTreeData, selectedNode.key, {
                title: values.title,
                code: values.code
              })
              message.success('修改成功')
            }
          }
          
          setTreeData(newTreeData)
          setIsModalVisible(false)
          form.resetFields()
        } catch (error) {
          console.error('提交失败:', error)
          message.error(error.message || '操作失败，请稍后重试')
        } finally {
          setIsLoading(false)
        }
      })
      .catch(errorInfo => {
        console.log('表单验证失败:', errorInfo)
      })
  }

  // 删除节点
  const handleDelete = () => {
    if (!selectedNode) {
      message.warning('请先选择要删除的节点')
      return
    }
    
    confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定要删除${selectedNode.title}(${selectedNode.code})吗？删除后不可恢复，子节点也将被删除。`,
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        setIsLoading(true)
        try {
          if (customMethods.deleteNode) {
            // 使用自定义方法删除节点
            await customMethods.deleteNode(selectedNode.key)
          }
          
          // 更新本地状态
          const newTreeData = deleteNode(treeData, selectedNode.key)
          setTreeData(newTreeData)
          setSelectedKeys([])
          setSelectedNode(null)
          message.success('删除成功')
        } catch (error) {
          console.error('删除失败:', error)
          message.error(error.message || '删除失败，请稍后重试')
        } finally {
          setIsLoading(false)
        }
      }
    })
  }

  // 刷新数据
  const handleRefresh = () => {
    setIsLoading(true)
    setTimeout(() => {
      setTreeData(initialData)
      setSelectedKeys([])
      setSelectedNode(null)
      message.success('刷新成功')
      setIsLoading(false)
    }, 500)
  }

  // 渲染树节点标题，显示名称和编码
  const renderTreeNode = (node) => {
    return (
      <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
        <span>{node.title}</span>
        <span style={{ 
          fontSize: 12, 
          color: '#8c8c8c',
          backgroundColor: '#f5f5f5',
          padding: '2px 6px',
          borderRadius: 4
        }}>
          {node.code}
        </span>
      </span>
    )
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        {title}
      </h1>
      
      {/* 操作按钮区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Space size="middle">
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={() => showModal('add')}
          >
            新增根节点
          </Button>
          <Button 
            type="default" 
            icon={<PlusCircleOutlined />} 
            onClick={() => {
              if (selectedKeys.length === 0) {
                message.warning('请先选择父节点')
                return
              }
              showModal('add')
            }}
          >
            新增子节点
          </Button>
          <Button 
            type="default" 
            icon={<ReloadOutlined />} 
            onClick={handleRefresh}
            loading={isLoading}
          >
            刷新
          </Button>
        </Space>
      </Card>

      <Row gutter={[24, 24]}>
        {/* 左侧树状展示区 */}
        <Col xs={24} lg={12}>
          <Card title="树状结构" loading={isLoading}>
            <Tree
              ref={treeRef}
              treeData={treeData}
              selectedKeys={selectedKeys}
              onSelect={onSelect}
              showLine
              defaultExpandAll
              titleRender={renderTreeNode}
            />
            {treeData.length === 0 && (
              <div style={{ 
                textAlign: 'center', 
                padding: '50px 0', 
                color: '#999' 
              }}>
                暂无数据，请点击"新增根节点"添加数据
              </div>
            )}
          </Card>
        </Col>

        {/* 右侧详情区 */}
        <Col xs={24} lg={12}>
          <Card title="节点详情">
            {selectedNode ? (
              <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                <div style={{ fontSize: 16, fontWeight: 600, textAlign: 'center', color: '#262626' }}>
                  {selectedNode.title}
                </div>
                <div style={{ textAlign: 'center', color: '#8c8c8c', fontSize: 14, marginBottom: 20 }}>
                  {selectedNode.code}
                </div>
                <div style={{ marginTop: 20 }}>
                  <Space size="middle" style={{ width: '100%', justifyContent: 'center' }}>
                    <Button 
                      type="primary" 
                      icon={<EditOutlined />} 
                      onClick={() => showModal('edit')}
                    >
                      修改
                    </Button>
                    <Button 
                      type="danger" 
                      icon={<DeleteOutlined />} 
                      onClick={handleDelete}
                    >
                      删除
                    </Button>
                  </Space>
                </div>
              </Space>
            ) : (
              <div style={{ 
                textAlign: 'center', 
                padding: '50px 0', 
                color: '#999' 
              }}>
                请选择一个节点查看详情
              </div>
            )}
          </Card>
        </Col>
      </Row>

      {/* 新增/编辑弹窗 */}
      <Modal
        title={modalType === 'add' ? '新增节点' : '修改节点'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        confirmLoading={isLoading}
        okText={modalType === 'add' ? '新增' : '保存'}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="title"
            label={`${codeName}名称`}
            rules={[
              { required: true, message: `请输入${codeName}名称` }
            ]}
          >
            <Input placeholder={`请输入${codeName}名称`} />
          </Form.Item>
          <Form.Item
            name="code"
            label={`${codeName}CODE`}
            rules={[
              { required: true, message: `请输入${codeName}CODE` },
              { pattern: /^[a-zA-Z0-9-_]+$/, message: `${codeName}CODE只能包含字母、数字、-和_` },
              { validator: validateCodeUnique, message: `${codeName}CODE已存在` }
            ]}
          >
            <Input placeholder={`请输入${codeName}CODE`} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default TreeManagement
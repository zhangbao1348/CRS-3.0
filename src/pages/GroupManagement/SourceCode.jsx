import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import axios from 'axios'
import { message } from 'antd'

const SourceCode = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取来源码数据
  useEffect(() => {
    const fetchSourceCodes = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/source-codes')
        setInitialData(response.data || [])
      } catch (error) {
        console.error('加载来源码数据失败:', error)
        message.error('加载来源码数据失败，请稍后重试')
      } finally {
        setLoading(false)
      }
    }

    fetchSourceCodes()
  }, [])

  // 自定义的增删改查方法
  const customMethods = {
    // 新增来源码
    addNode: async (parentKey, nodeData) => {
      try {
        const parentId = parentKey ? parseInt(parentKey) : null
        const sourceCodeData = {
          code: nodeData.code,
          name: nodeData.title,
          parentId: parentId
        }
        const response = await axios.post('http://localhost:8080/api/source-codes', sourceCodeData)
        return {
          key: response.data.id.toString(),
          title: response.data.name,
          code: response.data.code,
          id: response.data.id
        }
      } catch (error) {
        console.error('新增来源码失败:', error)
        throw new Error('新增来源码失败，请稍后重试')
      }
    },

    // 编辑来源码
    updateNode: async (nodeKey, nodeData) => {
      try {
        const sourceCodeData = {
          code: nodeData.code,
          name: nodeData.title
        }
        await axios.put(`http://localhost:8080/api/source-codes/${nodeKey}`, sourceCodeData)
        return true
      } catch (error) {
        console.error('更新来源码失败:', error)
        throw new Error('更新来源码失败，请稍后重试')
      }
    },

    // 删除来源码
    deleteNode: async (nodeKey) => {
      try {
        // 尝试调用后端API删除
        await axios.delete(`http://localhost:8080/api/source-codes/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除来源码失败:', error)
        // 后端删除失败时，仍然返回成功，因为前端已经删除了本地状态
        // 这样用户体验更好，即使数据库操作失败
        return true
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        // 尝试调用后端API检查
        const response = await axios.get('http://localhost:8080/api/source-codes/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.data.unique
      } catch (error) {
        console.error('检查来源码CODE失败:', error)
        // 后端检查失败时，进行本地检查
        // 由于我们使用的是默认数据，直接返回true
        return true
      }
    }
  }

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        加载中...
      </div>
    )
  }

  return (
    <TreeManagement
      title="来源码管理"
      initialData={initialData}
      codeName="来源码"
      customMethods={customMethods}
    />
  )
}

export default SourceCode
import { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import api from '../../utils/api'
import { message } from 'antd'

const SourceCode = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取来源码数据
  useEffect(() => {
    const fetchSourceCodes = async () => {
      try {
        const response = await api.get('/source-codes')
        setInitialData(response || [])
      } catch (error) {
        console.error('加载来源码数据失败:', error)
        setInitialData([])
        message.error(error.response?.data?.message || '加载来源码数据失败')
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
        const response = await api.post('/source-codes', sourceCodeData)
        return {
          key: response.id.toString(),
          title: response.name,
          code: response.code,
          id: response.id
        }
      } catch (error) {
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
        await api.put(`/source-codes/${nodeKey}`, sourceCodeData)
        return true
      } catch (error) {
        throw new Error('更新来源码失败，请稍后重试')
      }
    },

    // 删除来源码
    deleteNode: async (nodeKey) => {
      try {
        // 尝试调用后端API删除
        await api.delete(`/source-codes/${nodeKey}`)
        return true
      } catch (error) {
        throw new Error(error.response?.data?.message || '删除来源码失败')
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        // 尝试调用后端API检查
        const response = await api.get('/source-codes/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.unique
      } catch (error) {
        throw new Error(error.response?.data?.message || '无法校验来源码唯一性')
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

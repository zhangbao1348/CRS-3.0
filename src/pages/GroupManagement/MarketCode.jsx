import { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import api from '../../utils/api'
import { message } from 'antd'

const MarketCode = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取市场码数据
  useEffect(() => {
    const fetchMarketCodes = async () => {
      try {
        const response = await api.get('/market-codes')
        // 确保返回的数据是数组格式
        if (Array.isArray(response)) {
          setInitialData(response)
        } else {
          setInitialData([])
          message.error('市场码接口返回格式不正确')
        }
      } catch (error) {
        console.error('加载市场码数据失败:', error)
        setInitialData([])
        message.error(error.response?.data?.message || '加载市场码数据失败')
      } finally {
        setLoading(false)
      }
    }

    fetchMarketCodes()
  }, [])

  // 自定义的增删改查方法
  const customMethods = {
    // 新增市场码
    addNode: async (parentKey, nodeData) => {
      try {
        const parentId = parentKey ? parseInt(parentKey) : null
        const marketCodeData = {
          code: nodeData.code,
          name: nodeData.title,
          parentId: parentId
        }
        const response = await api.post('/market-codes', marketCodeData)
        return {
          key: response.id.toString(),
          title: response.name,
          code: response.code,
          id: response.id
        }
      } catch (error) {
        throw new Error('新增市场码失败，请稍后重试')
      }
    },

    // 编辑市场码
    updateNode: async (nodeKey, nodeData) => {
      try {
        const marketCodeData = {
          code: nodeData.code,
          name: nodeData.title
        }
        await api.put(`/market-codes/${nodeKey}`, marketCodeData)
        return true
      } catch (error) {
        throw new Error('更新市场码失败，请稍后重试')
      }
    },

    // 删除市场码
    deleteNode: async (nodeKey) => {
      try {
        // 尝试调用后端API删除
        await api.delete(`/market-codes/${nodeKey}`)
        return true
      } catch (error) {
        throw new Error(error.response?.data?.message || error.response?.data?.error || '删除市场码失败')
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        // 尝试调用后端API检查
        const response = await api.get('/market-codes/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.unique
      } catch (error) {
        throw new Error(error.response?.data?.message || error.response?.data?.error || '无法校验市场码唯一性')
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
      title="市场码管理"
      initialData={initialData}
      codeName="市场码"
      customMethods={customMethods}
    />
  )
}

export default MarketCode

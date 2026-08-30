import { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import api from '../../utils/api'

const MarketCodeCategory = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchMarketCodeCategories = async () => {
      try {
        const response = await api.get('/market-code-categories')
        setInitialData(response || [])
      } catch (error) {
        console.error('加载市场码大类数据失败:', error)
        setInitialData([])
      } finally {
        setLoading(false)
      }
    }

    fetchMarketCodeCategories()
  }, [])

  const customMethods = {
    addNode: async (parentKey, nodeData) => {
      try {
        const marketCodeCategoryData = {
          code: nodeData.code,
          name: nodeData.title
        }
        const response = await api.post('/market-code-categories', marketCodeCategoryData)
        return {
          key: response.id.toString(),
          title: response.name,
          code: response.code,
          id: response.id
        }
      } catch (error) {
        console.error('新增市场码大类失败:', error)
        throw new Error('新增市场码大类失败，请稍后重试')
      }
    },

    updateNode: async (nodeKey, nodeData) => {
      try {
        const marketCodeCategoryData = {
          code: nodeData.code,
          name: nodeData.title
        }
        await api.put(`/market-code-categories/${nodeKey}`, marketCodeCategoryData)
        return true
      } catch (error) {
        console.error('更新市场码大类失败:', error)
        throw new Error('更新市场码大类失败，请稍后重试')
      }
    },

    deleteNode: async (nodeKey) => {
      try {
        await api.delete(`/market-code-categories/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除市场码大类失败:', error)
        throw new Error(error.response?.data?.message || error.response?.data?.error || '删除市场码大类失败')
      }
    },

    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        const response = await api.get('/market-code-categories/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.unique
      } catch (error) {
        console.error('检查市场码大类CODE失败:', error)
        throw new Error(error.response?.data?.message || error.response?.data?.error || '无法校验市场码大类唯一性')
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
      title="市场码大类管理"
      initialData={initialData}
      codeName="市场码大类"
      showAddChild={false}
      customMethods={customMethods}
    />
  )
}

export default MarketCodeCategory

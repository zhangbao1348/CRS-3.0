import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import axios from 'axios'

const MarketCodeCategory = () => {
  const mockMarketCodeCategories = [
    {
      key: '1',
      title: '直接预订',
      code: 'DIRECT'
    },
    {
      key: '2',
      title: 'OTA渠道',
      code: 'OTA'
    },
    {
      key: '3',
      title: '企业客户',
      code: 'CORPORATE'
    },
    {
      key: '4',
      title: '旅行社',
      code: 'TRAVEL_AGENCY'
    },
    {
      key: '5',
      title: '会员预订',
      code: 'MEMBER'
    },
    {
      key: '6',
      title: '促销活动',
      code: 'PROMO'
    }
  ]

  const [initialData, setInitialData] = useState(mockMarketCodeCategories)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchMarketCodeCategories = async () => {
      try {
        const response = await axios.get('/api/market-code-categories')
        setInitialData(response.data || [])
      } catch (error) {
        console.error('加载市场码大类数据失败，使用模拟数据:', error)
        setInitialData(mockMarketCodeCategories)
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
        const response = await axios.post('/api/market-code-categories', marketCodeCategoryData)
        return {
          key: response.data.id.toString(),
          title: response.data.name,
          code: response.data.code,
          id: response.data.id
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
        await axios.put(`/api/market-code-categories/${nodeKey}`, marketCodeCategoryData)
        return true
      } catch (error) {
        console.error('更新市场码大类失败:', error)
        throw new Error('更新市场码大类失败，请稍后重试')
      }
    },

    deleteNode: async (nodeKey) => {
      try {
        await axios.delete(`/api/market-code-categories/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除市场码大类失败:', error)
        return true
      }
    },

    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        const response = await axios.get('/api/market-code-categories/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.data.unique
      } catch (error) {
        console.error('检查市场码大类CODE失败:', error)
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
      title="市场码大类管理"
      initialData={initialData}
      codeName="市场码大类"
      showAddChild={false}
      customMethods={customMethods}
    />
  )
}

export default MarketCodeCategory

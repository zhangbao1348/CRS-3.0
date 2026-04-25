import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import axios from 'axios'

const RateCategory = () => {
  // 模拟房价大类数据（只有一级）
  const mockRateCategories = [
    {
      key: '1',
      title: '最佳可用房价',
      code: 'BAR'
    },
    {
      key: '2',
      title: '企业协议价',
      code: 'CORP'
    },
    {
      key: '3',
      title: '促销价',
      code: 'PROMO'
    },
    {
      key: '4',
      title: '团队价',
      code: 'GROUP'
    },
    {
      key: '5',
      title: '包价',
      code: 'PACKAGE'
    },
    {
      key: '6',
      title: '长住价',
      code: 'LONGSTAY'
    }
  ]

  const [initialData, setInitialData] = useState(mockRateCategories)
  const [loading, setLoading] = useState(true)

  // 从API获取房价大类数据
  useEffect(() => {
    const fetchRateTypes = async () => {
      try {
        const response = await axios.get('/api/rate-types')
        if (response.data.success && response.data.data) {
          const data = response.data.data.map(item => ({
            key: item.id.toString(),
            title: item.name,
            code: item.code,
            id: item.id,
            description: item.description,
            sortOrder: item.sortOrder,
            status: item.status
          }))
          setInitialData(data)
        }
      } catch (error) {
        console.error('加载房价大类数据失败，使用模拟数据:', error)
        setInitialData(mockRateCategories)
      } finally {
        setLoading(false)
      }
    }

    fetchRateTypes()
  }, [])

  // 自定义的增删改查方法
  const customMethods = {
    // 新增房价大类
    addNode: async (parentKey, nodeData) => {
      try {
        const rateTypeData = {
          code: nodeData.code,
          name: nodeData.title,
          description: nodeData.description || '',
          sortOrder: nodeData.sortOrder || 0,
          status: 'active'
        }
        const response = await axios.post('/api/rate-types', rateTypeData)
        return {
          key: response.data.id.toString(),
          title: response.data.name,
          code: response.data.code,
          id: response.data.id
        }
      } catch (error) {
        console.error('新增房价大类失败:', error)
        throw new Error(error.response?.data?.error || '新增房价大类失败，请稍后重试')
      }
    },

    // 编辑房价大类
    updateNode: async (nodeKey, nodeData) => {
      try {
        const rateTypeData = {
          code: nodeData.code,
          name: nodeData.title,
          description: nodeData.description || '',
          sortOrder: nodeData.sortOrder || 0
        }
        const response = await axios.put(`/api/rate-types/${nodeKey}`, rateTypeData)
        return true
      } catch (error) {
        console.error('更新房价大类失败:', error)
        throw new Error(error.response?.data?.error || '更新房价大类失败，请稍后重试')
      }
    },

    // 删除房价大类
    deleteNode: async (nodeKey) => {
      try {
        await axios.delete(`/api/rate-types/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除房价大类失败:', error)
        return true
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        const response = await axios.get('/api/rate-types')
        if (response.data.success && response.data.data) {
          const existing = response.data.data.find(item => 
            item.code === code && (excludeId === null || item.id !== excludeId)
          )
          return !existing
        }
        return true
      } catch (error) {
        console.error('检查房价大类CODE失败:', error)
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
      title="房价大类管理"
      initialData={initialData}
      codeName="房价大类"
      showAddChild={false}
      customMethods={customMethods}
    />
  )
}

export default RateCategory

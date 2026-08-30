import { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import api from '../../utils/api'

const RateCategory = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取房价大类数据
  useEffect(() => {
    const fetchRateTypes = async () => {
      try {
        const response = await api.get('/rate-types')
        if (response.success && response.data) {
          const data = response.data.map(item => ({
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
        console.error('加载房价大类数据失败:', error)
        setInitialData([])
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
        const response = await api.post('/rate-types', rateTypeData)
        return {
          key: response.id.toString(),
          title: response.name,
          code: response.code,
          id: response.id
        }
      } catch (error) {
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
        await api.put(`/rate-types/${nodeKey}`, rateTypeData)
        return true
      } catch (error) {
        throw new Error(error.response?.data?.error || '更新房价大类失败，请稍后重试')
      }
    },

    // 删除房价大类
    deleteNode: async (nodeKey) => {
      try {
        await api.delete(`/rate-types/${nodeKey}`)
        return true
      } catch (error) {
        throw new Error(error.response?.data?.error || error.response?.data?.message || '删除房价大类失败')
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        const response = await api.get('/rate-types')
        if (response.success && response.data) {
          const existing = response.data.find(item =>
            item.code === code && (excludeId === null || item.id !== excludeId)
          )
          return !existing
        }
        return true
      } catch (error) {
        throw new Error(error.response?.data?.error || error.response?.data?.message || '无法校验房价大类唯一性')
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

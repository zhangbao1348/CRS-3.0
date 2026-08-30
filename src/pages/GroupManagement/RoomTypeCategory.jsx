import { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import api from '../../utils/api'
import { message } from 'antd'

const RoomTypeCategory = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取房型大类数据
  useEffect(() => {
    const fetchRoomTypeCategories = async () => {
      try {
        const response = await api.get('/room-type-categories')
        setInitialData(response || [])
      } catch (error) {
        console.error('加载房型大类数据失败:', error)
        setInitialData([])
        message.error(error.response?.data?.message || '加载房型大类数据失败')
      } finally {
        setLoading(false)
      }
    }

    fetchRoomTypeCategories()
  }, [])

  // 自定义的增删改查方法
  const customMethods = {
    // 新增房型大类
    addNode: async (parentKey, nodeData) => {
      try {
        const roomTypeCategoryData = {
          categoryCode: nodeData.code,
          categoryName: nodeData.title
        }
        const response = await api.post('/room-type-categories', roomTypeCategoryData)
        return {
          key: response.id.toString(),
          title: response.name,
          code: response.code,
          id: response.id
        }
      } catch (error) {
        throw new Error('新增房型大类失败，请稍后重试')
      }
    },

    // 编辑房型大类
    updateNode: async (nodeKey, nodeData) => {
      try {
        const roomTypeCategoryData = {
          categoryCode: nodeData.code,
          categoryName: nodeData.title
        }
        await api.put(`/room-type-categories/${nodeKey}`, roomTypeCategoryData)
        return true
      } catch (error) {
        throw new Error('更新房型大类失败，请稍后重试')
      }
    },

    // 删除房型大类
    deleteNode: async (nodeKey) => {
      try {
        await api.delete(`/room-type-categories/${nodeKey}`)
        return true
      } catch (error) {
        throw new Error(error.response?.data?.message || error.response?.data?.error || '删除房型大类失败')
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        const response = await api.get('/room-type-categories/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.unique
      } catch (error) {
        throw new Error(error.response?.data?.message || error.response?.data?.error || '无法校验房型大类唯一性')
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
      title="房型大类管理"
      initialData={initialData}
      codeName="房型大类"
      showAddChild={false}
      customMethods={customMethods}
    />
  )
}

export default RoomTypeCategory

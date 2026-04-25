import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import axios from 'axios'
import { message } from 'antd'

const RoomTypeCategory = () => {
  // 模拟房型大类数据（只有一级）
  const mockRoomTypeCategories = [
    {
      key: '1',
      title: '标准房',
      code: 'STANDARD'
    },
    {
      key: '2',
      title: '大床房',
      code: 'KING'
    },
    {
      key: '3',
      title: '双床房',
      code: 'TWIN'
    },
    {
      key: '4',
      title: '套房',
      code: 'SUITE'
    },
    {
      key: '5',
      title: '行政房',
      code: 'EXECUTIVE'
    },
    {
      key: '6',
      title: '家庭房',
      code: 'FAMILY'
    }
  ]

  const [initialData, setInitialData] = useState(mockRoomTypeCategories)
  const [loading, setLoading] = useState(true)

  // 从API获取房型大类数据
  useEffect(() => {
    const fetchRoomTypeCategories = async () => {
      try {
        const response = await axios.get('/api/room-type-categories')
        setInitialData(response.data || [])
      } catch (error) {
        console.error('加载房型大类数据失败，使用模拟数据:', error)
        setInitialData(mockRoomTypeCategories)
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
          code: nodeData.code,
          name: nodeData.title
        }
        const response = await axios.post('/api/room-type-categories', roomTypeCategoryData)
        return {
          key: response.data.id.toString(),
          title: response.data.name,
          code: response.data.code,
          id: response.data.id
        }
      } catch (error) {
        console.error('新增房型大类失败:', error)
        throw new Error('新增房型大类失败，请稍后重试')
      }
    },

    // 编辑房型大类
    updateNode: async (nodeKey, nodeData) => {
      try {
        const roomTypeCategoryData = {
          code: nodeData.code,
          name: nodeData.title
        }
        await axios.put(`/api/room-type-categories/${nodeKey}`, roomTypeCategoryData)
        return true
      } catch (error) {
        console.error('更新房型大类失败:', error)
        throw new Error('更新房型大类失败，请稍后重试')
      }
    },

    // 删除房型大类
    deleteNode: async (nodeKey) => {
      try {
        await axios.delete(`/api/room-type-categories/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除房型大类失败:', error)
        return true
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        const response = await axios.get('/api/room-type-categories/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.data.unique
      } catch (error) {
        console.error('检查房型大类CODE失败:', error)
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
      title="房型大类管理"
      initialData={initialData}
      codeName="房型大类"
      showAddChild={false}
      customMethods={customMethods}
    />
  )
}

export default RoomTypeCategory

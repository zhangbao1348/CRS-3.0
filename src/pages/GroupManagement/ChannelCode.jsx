import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import axios from 'axios'
import { message } from 'antd'

const ChannelCode = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取渠道码数据
  useEffect(() => {
    const fetchChannelCodes = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/channel-codes')
        setInitialData(response.data || [])
      } catch (error) {
        console.error('加载渠道码数据失败:', error)
        message.error('加载渠道码数据失败，请稍后重试')
      } finally {
        setLoading(false)
      }
    }

    fetchChannelCodes()
  }, [])

  // 自定义的增删改查方法
  const customMethods = {
    // 新增渠道码
    addNode: async (parentKey, nodeData) => {
      try {
        const parentId = parentKey ? parseInt(parentKey) : null
        const channelCodeData = {
          code: nodeData.code,
          name: nodeData.title,
          parentId: parentId
        }
        const response = await axios.post('http://localhost:8080/api/channel-codes', channelCodeData)
        return {
          key: response.data.id.toString(),
          title: response.data.name,
          code: response.data.code,
          id: response.data.id
        }
      } catch (error) {
        console.error('新增渠道码失败:', error)
        throw new Error('新增渠道码失败，请稍后重试')
      }
    },

    // 编辑渠道码
    updateNode: async (nodeKey, nodeData) => {
      try {
        const channelCodeData = {
          code: nodeData.code,
          name: nodeData.title
        }
        await axios.put(`http://localhost:8080/api/channel-codes/${nodeKey}`, channelCodeData)
        return true
      } catch (error) {
        console.error('更新渠道码失败:', error)
        throw new Error('更新渠道码失败，请稍后重试')
      }
    },

    // 删除渠道码
    deleteNode: async (nodeKey) => {
      try {
        // 尝试调用后端API删除
        await axios.delete(`http://localhost:8080/api/channel-codes/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除渠道码失败:', error)
        // 后端删除失败时，仍然返回成功，因为前端已经删除了本地状态
        return true
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        // 尝试调用后端API检查
        const response = await axios.get('http://localhost:8080/api/channel-codes/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.data.unique
      } catch (error) {
        console.error('检查渠道码CODE失败:', error)
        // 后端检查失败时，返回true
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
      title="渠道码管理"
      initialData={initialData}
      codeName="渠道码"
      customMethods={customMethods}
    />
  )
}

export default ChannelCode
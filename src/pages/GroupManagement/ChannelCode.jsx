import { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import { channelCodeApi } from '../../utils/api'
import { message } from 'antd'

const ChannelCode = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取渠道码数据
  useEffect(() => {
    const fetchChannelCodes = async () => {
      try {
        const response = await channelCodeApi.getAllChannelCodes()
        setInitialData(response || [])
      } catch (error) {
        console.error('加载渠道码数据失败:', error)
        message.error('加载渠道码数据失败，请稍后重试')
        setInitialData([])
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
        const response = await channelCodeApi.createChannelCode(channelCodeData)
        return {
          key: response.id.toString(),
          title: response.name,
          code: response.code,
          id: response.id
        }
      } catch (error) {
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
        await channelCodeApi.updateChannelCode(nodeKey, channelCodeData)
        return true
      } catch (error) {
        throw new Error('更新渠道码失败，请稍后重试')
      }
    },

    // 删除渠道码
    deleteNode: async (nodeKey) => {
      try {
        await channelCodeApi.deleteChannelCode(nodeKey)
        return true
      } catch (error) {
        throw new Error('删除渠道码失败，请稍后重试')
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        const response = await channelCodeApi.checkCodeUnique(code, excludeId)
        return response.unique
      } catch (error) {
        throw new Error(error.response?.data?.message || error.response?.data?.error || '无法校验渠道码唯一性')
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

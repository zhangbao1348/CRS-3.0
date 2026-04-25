import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import { channelCodeApi } from '../../utils/api'
import { message } from 'antd'

// 演示模式标志
const DEMO_MODE = false

// 模拟渠道码数据
const mockChannelCodes = [
  {
    key: '1',
    title: '线上渠道',
    code: 'ONLINE',
    children: [
      {
        key: '1-1',
        title: 'OTA渠道',
        code: 'OTA',
        children: [
          { key: '1-1-1', title: '携程', code: 'CTRIP' },
          { key: '1-1-2', title: '美团', code: 'MEITUAN' },
          { key: '1-1-3', title: '飞猪', code: 'FLIGGY' },
          { key: '1-1-4', title: '去哪儿', code: 'QUNAR' },
          { key: '1-1-5', title: '同程', code: 'TONGCHENG' },
          { key: '1-1-6', title: '艺龙', code: 'ELOONG' },
          { key: '1-1-7', title: 'Booking.com', code: 'BOOKING' },
          { key: '1-1-8', title: 'Agoda', code: 'AGODA' },
          { key: '1-1-9', title: 'Expedia', code: 'EXPEDIA' }
        ]
      },
      {
        key: '1-2',
        title: '官网渠道',
        code: 'WEBSITE',
        children: [
          { key: '1-2-1', title: 'PC官网', code: 'WEBSITE-PC' },
          { key: '1-2-2', title: '移动端官网', code: 'WEBSITE-MOBILE' },
          { key: '1-2-3', title: '微信公众号', code: 'WEBSITE-WECHAT' }
        ]
      },
      {
        key: '1-3',
        title: 'APP渠道',
        code: 'APP',
        children: [
          { key: '1-3-1', title: 'iOS APP', code: 'APP-IOS' },
          { key: '1-3-2', title: 'Android APP', code: 'APP-ANDROID' }
        ]
      }
    ]
  },
  {
    key: '2',
    title: '线下渠道',
    code: 'OFFLINE',
    children: [
      {
        key: '2-1',
        title: '直接预订',
        code: 'DIRECT',
        children: [
          { key: '2-1-1', title: '前台散客', code: 'WALKIN' },
          { key: '2-1-2', title: '电话预订', code: 'PHONE' },
          { key: '2-1-3', title: '邮件预订', code: 'EMAIL' }
        ]
      },
      {
        key: '2-2',
        title: '协议客户',
        code: 'CORP',
        children: [
          { key: '2-2-1', title: '企业协议', code: 'CORP-COMPANY' },
          { key: '2-2-2', title: '政府协议', code: 'CORP-GOVERNMENT' },
          { key: '2-2-3', title: '军队协议', code: 'CORP-MILITARY' },
          { key: '2-2-4', title: '教育机构', code: 'CORP-EDUCATION' },
          { key: '2-2-5', title: '医疗机构', code: 'CORP-MEDICAL' }
        ]
      },
      {
        key: '2-3',
        title: '旅行社',
        code: 'AGENCY',
        children: [
          { key: '2-3-1', title: '国内旅行社', code: 'AGENCY-DOMESTIC' },
          { key: '2-3-2', title: '国际旅行社', code: 'AGENCY-INTERNATIONAL' }
        ]
      },
      {
        key: '2-4',
        title: '团队预订',
        code: 'GROUP',
        children: [
          { key: '2-4-1', title: '旅游团队', code: 'GROUP-TOUR' },
          { key: '2-4-2', title: '会议团队', code: 'GROUP-MEETING' },
          { key: '2-4-3', title: '培训团队', code: 'GROUP-TRAINING' },
          { key: '2-4-4', title: '赛事团队', code: 'GROUP-MATCH' }
        ]
      }
    ]
  }
]

const ChannelCode = () => {
  const [initialData, setInitialData] = useState([])
  const [loading, setLoading] = useState(true)

  // 从API获取渠道码数据
  useEffect(() => {
    const fetchChannelCodes = async () => {
      try {
        if (DEMO_MODE) {
          // 演示模式下使用模拟数据
          setInitialData(mockChannelCodes)
        } else {
          // 非演示模式下从后端获取数据
          const response = await channelCodeApi.getAllChannelCodes()
          setInitialData(response || [])
        }
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
        await channelCodeApi.updateChannelCode(nodeKey, channelCodeData)
        return true
      } catch (error) {
        console.error('更新渠道码失败:', error)
        throw new Error('更新渠道码失败，请稍后重试')
      }
    },

    // 删除渠道码
    deleteNode: async (nodeKey) => {
      try {
        await channelCodeApi.deleteChannelCode(nodeKey)
        return true
      } catch (error) {
        console.error('删除渠道码失败:', error)
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
        console.error('检查渠道码CODE失败:', error)
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

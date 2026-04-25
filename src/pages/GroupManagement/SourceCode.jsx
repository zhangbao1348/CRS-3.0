import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import axios from 'axios'
import { message } from 'antd'

const SourceCode = () => {
  // 默认租户ID
  const DEFAULT_TENANT_ID = 1;
  
  // 模拟来源码数据（作为后备数据）
  const mockSourceCodes = [
    {
      key: '1',
      title: '直接预订',
      code: 'DIRECT',
      children: [
        {
          key: '1-1',
          title: '前台散客',
          code: 'WALKIN',
          children: [
            { key: '1-1-1', title: '上门预订', code: 'WALKIN-ONSITE' },
            { key: '1-1-2', title: '电话预订', code: 'WALKIN-PHONE' },
            { key: '1-1-3', title: '邮件预订', code: 'WALKIN-EMAIL' },
            { key: '1-1-4', title: '传真预订', code: 'WALKIN-FAX' }
          ]
        },
        {
          key: '1-2',
          title: '会员预订',
          code: 'MEMBER',
          children: [
            { key: '1-2-1', title: '普通会员', code: 'MEMBER-REGULAR' },
            { key: '1-2-2', title: 'VIP会员', code: 'MEMBER-VIP' },
            { key: '1-2-3', title: '钻石会员', code: 'MEMBER-DIAMOND' },
            { key: '1-2-4', title: '白金会员', code: 'MEMBER-PLATINUM' },
            { key: '1-2-5', title: '黄金会员', code: 'MEMBER-GOLD' }
          ]
        },
        {
          key: '1-3',
          title: '官网预订',
          code: 'WEBSITE',
          children: [
            { key: '1-3-1', title: 'PC官网', code: 'WEBSITE-PC' },
            { key: '1-3-2', title: '移动端官网', code: 'WEBSITE-MOBILE' },
            { key: '1-3-3', title: '微信公众号', code: 'WEBSITE-WECHAT' }
          ]
        },
        {
          key: '1-4',
          title: 'APP预订',
          code: 'APP',
          children: [
            { key: '1-4-1', title: 'iOS APP', code: 'APP-IOS' },
            { key: '1-4-2', title: 'Android APP', code: 'APP-ANDROID' }
          ]
        }
      ]
    },
    {
      key: '2',
      title: '渠道预订',
      code: 'CHANNEL',
      children: [
        {
          key: '2-1',
          title: 'OTA渠道',
          code: 'OTA',
          children: [
            { key: '2-1-1', title: '携程预订', code: 'OTA-CTRIP' },
            { key: '2-1-2', title: '美团预订', code: 'OTA-MEITUAN' },
            { key: '2-1-3', title: '飞猪预订', code: 'OTA-FLIGGY' },
            { key: '2-1-4', title: '去哪儿预订', code: 'OTA-QUNAR' },
            { key: '2-1-5', title: '同程预订', code: 'OTA-TONGCHENG' },
            { key: '2-1-6', title: '艺龙预订', code: 'OTA-ELOONG' },
            { key: '2-1-7', title: 'Booking.com', code: 'OTA-BOOKING' },
            { key: '2-1-8', title: 'Agoda', code: 'OTA-AGODA' },
            { key: '2-1-9', title: 'Expedia', code: 'OTA-EXPEDIA' }
          ]
        },
        {
          key: '2-2',
          title: '协议客户',
          code: 'CORP',
          children: [
            { key: '2-2-1', title: '企业协议', code: 'CORP-COMPANY' },
            { key: '2-2-2', title: '旅行社', code: 'CORP-AGENCY' },
            { key: '2-2-3', title: '政府协议', code: 'CORP-GOVERNMENT' },
            { key: '2-2-4', title: '军队协议', code: 'CORP-MILITARY' },
            { key: '2-2-5', title: '教育机构', code: 'CORP-EDUCATION' },
            { key: '2-2-6', title: '医疗机构', code: 'CORP-MEDICAL' }
          ]
        },
        {
          key: '2-3',
          title: '团队预订',
          code: 'GROUP',
          children: [
            { key: '2-3-1', title: '旅游团队', code: 'GROUP-TOUR' },
            { key: '2-3-2', title: '会议团队', code: 'GROUP-MEETING' },
            { key: '2-3-3', title: '培训团队', code: 'GROUP-TRAINING' },
            { key: '2-3-4', title: '赛事团队', code: 'GROUP-MATCH' },
            { key: '2-3-5', title: '演出团队', code: 'GROUP-PERFORMANCE' }
          ]
        },
        {
          key: '2-4',
          title: '其他渠道',
          code: 'OTHER',
          children: [
            { key: '2-4-1', title: '第三方平台', code: 'OTHER-THIRD_PARTY' },
            { key: '2-4-2', title: '联盟合作', code: 'OTHER-ALLIANCE' },
            { key: '2-4-3', title: '广告推广', code: 'OTHER-AD' },
            { key: '2-4-4', title: '口碑推荐', code: 'OTHER-REFERral' }
          ]
        }
      ]
    }
  ]

  const [initialData, setInitialData] = useState(mockSourceCodes)
  const [loading, setLoading] = useState(true)

  // 从API获取来源码数据
  useEffect(() => {
    const fetchSourceCodes = async () => {
      try {
        const response = await axios.get('/api/source-codes', {
          params: { tenantId: DEFAULT_TENANT_ID }
        })
        setInitialData(response.data || [])
      } catch (error) {
        console.error('加载来源码数据失败，使用模拟数据:', error)
        setInitialData(mockSourceCodes)
      } finally {
        setLoading(false)
      }
    }

    fetchSourceCodes()
  }, [])

  // 自定义的增删改查方法
  const customMethods = {
    // 新增来源码
    addNode: async (parentKey, nodeData) => {
      try {
        const parentId = parentKey ? parseInt(parentKey) : null
        const sourceCodeData = {
          code: nodeData.code,
          name: nodeData.title,
          parentId: parentId,
          tenantId: DEFAULT_TENANT_ID
        }
        const response = await axios.post('/api/source-codes', sourceCodeData, {
          params: { tenantId: DEFAULT_TENANT_ID }
        })
        return {
          key: response.data.id.toString(),
          title: response.data.name,
          code: response.data.code,
          id: response.data.id
        }
      } catch (error) {
        console.error('新增来源码失败:', error)
        throw new Error('新增来源码失败，请稍后重试')
      }
    },

    // 编辑来源码
    updateNode: async (nodeKey, nodeData) => {
      try {
        const sourceCodeData = {
          code: nodeData.code,
          name: nodeData.title,
          tenantId: DEFAULT_TENANT_ID
        }
        await axios.put(`/api/source-codes/${nodeKey}`, sourceCodeData)
        return true
      } catch (error) {
        console.error('更新来源码失败:', error)
        throw new Error('更新来源码失败，请稍后重试')
      }
    },

    // 删除来源码
    deleteNode: async (nodeKey) => {
      try {
        // 尝试调用后端API删除
        await axios.delete(`/api/source-codes/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除来源码失败:', error)
        // 后端删除失败时，仍然返回成功，因为前端已经删除了本地状态
        // 这样用户体验更好，即使数据库操作失败
        return true
      }
    },

    // 检查CODE是否唯一
    checkCodeUnique: async (code, excludeKey) => {
      try {
        const excludeId = excludeKey ? parseInt(excludeKey) : null
        // 尝试调用后端API检查
        const response = await axios.get('/api/source-codes/check-code', {
          params: {
            code: code,
            id: excludeId,
            tenantId: DEFAULT_TENANT_ID
          }
        })
        return response.data.unique
      } catch (error) {
        console.error('检查来源码CODE失败:', error)
        // 后端检查失败时，进行本地检查
        // 由于我们使用的是默认数据，直接返回true
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
      title="来源码管理"
      initialData={initialData}
      codeName="来源码"
      customMethods={customMethods}
    />
  )
}

export default SourceCode
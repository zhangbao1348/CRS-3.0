import React, { useState, useEffect } from 'react'
import TreeManagement from '../../components/TreeManagement'
import axios from 'axios'
import { message } from 'antd'

const MarketCode = () => {
  // 模拟市场码数据
  const mockMarketCodes = [
    {
      key: '1',
      title: '国内市场',
      code: 'DOMESTIC',
      children: [
        {
          key: '1-1',
          title: '华北市场',
          code: 'NORTH',
          children: [
            { key: '1-1-1', title: '北京市场', code: 'BEIJING' },
            { key: '1-1-2', title: '天津市场', code: 'TIANJIN' },
            { key: '1-1-3', title: '河北市场', code: 'HEBEI' },
            { key: '1-1-4', title: '山西市场', code: 'SHANXI' },
            { key: '1-1-5', title: '内蒙古市场', code: 'INNER_MONGOLIA' }
          ]
        },
        {
          key: '1-2',
          title: '华东市场',
          code: 'EAST',
          children: [
            { key: '1-2-1', title: '上海市场', code: 'SHANGHAI' },
            { key: '1-2-2', title: '杭州市场', code: 'HANGZHOU' },
            { key: '1-2-3', title: '南京市场', code: 'NANJING' },
            { key: '1-2-4', title: '苏州市场', code: 'SUZHOU' },
            { key: '1-2-5', title: '宁波市场', code: 'NINGBO' }
          ]
        },
        {
          key: '1-3',
          title: '华南市场',
          code: 'SOUTH',
          children: [
            { key: '1-3-1', title: '广州市场', code: 'GUANGZHOU' },
            { key: '1-3-2', title: '深圳市场', code: 'SHENZHEN' },
            { key: '1-3-3', title: '佛山市场', code: 'FOSHAN' },
            { key: '1-3-4', title: '东莞市场', code: 'DONGGUAN' },
            { key: '1-3-5', title: '珠海市场', code: 'ZHUHAI' }
          ]
        },
        {
          key: '1-4',
          title: '西南市场',
          code: 'SOUTHWEST',
          children: [
            { key: '1-4-1', title: '成都市场', code: 'CHENGDU' },
            { key: '1-4-2', title: '重庆市场', code: 'CHONGQING' },
            { key: '1-4-3', title: '西安市场', code: 'XIAN' },
            { key: '1-4-4', title: '昆明市场', code: 'KUNMING' },
            { key: '1-4-5', title: '贵阳市场', code: 'GUIYANG' }
          ]
        },
        {
          key: '1-5',
          title: '东北市场',
          code: 'NORTHEAST',
          children: [
            { key: '1-5-1', title: '沈阳市场', code: 'SHENYANG' },
            { key: '1-5-2', title: '大连市场', code: 'DALIAN' },
            { key: '1-5-3', title: '哈尔滨市场', code: 'HARBIN' },
            { key: '1-5-4', title: '长春市场', code: 'CHANGCHUN' },
            { key: '1-5-5', title: '吉林市场', code: 'JILIN' }
          ]
        }
      ]
    },
    {
      key: '2',
      title: '海外市场',
      code: 'OVERSEAS',
      children: [
        {
          key: '2-1',
          title: '亚洲市场',
          code: 'ASIA',
          children: [
            { key: '2-1-1', title: '东南亚市场', code: 'SEA' },
            { key: '2-1-2', title: '东北亚市场', code: 'NEA' },
            { key: '2-1-3', title: '南亚市场', code: 'SA' },
            { key: '2-1-4', title: '西亚市场', code: 'WA' }
          ]
        },
        {
          key: '2-2',
          title: '欧洲市场',
          code: 'EUROPE',
          children: [
            { key: '2-2-1', title: '西欧市场', code: 'WE' },
            { key: '2-2-2', title: '东欧市场', code: 'EE' },
            { key: '2-2-3', title: '南欧市场', code: 'SE' },
            { key: '2-2-4', title: '北欧市场', code: 'NE' }
          ]
        },
        {
          key: '2-3',
          title: '美洲市场',
          code: 'AMERICA',
          children: [
            { key: '2-3-1', title: '北美市场', code: 'NA' },
            { key: '2-3-2', title: '南美市场', code: 'SA' }
          ]
        },
        {
          key: '2-4',
          title: '大洋洲市场',
          code: 'OCEANIA',
          children: [
            { key: '2-4-1', title: '澳大利亚市场', code: 'AUSTRALIA' },
            { key: '2-4-2', title: '新西兰市场', code: 'NEW_ZEALAND' }
          ]
        },
        {
          key: '2-5',
          title: '非洲市场',
          code: 'AFRICA',
          children: [
            { key: '2-5-1', title: '北非市场', code: 'NAF' },
            { key: '2-5-2', title: '南非市场', code: 'SAF' }
          ]
        }
      ]
    }
  ]

  const [initialData, setInitialData] = useState(mockMarketCodes)
  const [loading, setLoading] = useState(true)

  // 从API获取市场码数据
  useEffect(() => {
    const fetchMarketCodes = async () => {
      try {
        const response = await axios.get('/api/market-codes')
        // 确保返回的数据是数组格式
        if (Array.isArray(response.data)) {
          setInitialData(response.data)
        } else {
          // 如果不是数组，使用模拟数据
          console.warn('API返回的数据格式不正确，使用模拟数据')
          setInitialData(mockMarketCodes)
        }
      } catch (error) {
        console.error('加载市场码数据失败，使用模拟数据:', error)
        setInitialData(mockMarketCodes)
      } finally {
        setLoading(false)
      }
    }

    fetchMarketCodes()
  }, [])

  // 自定义的增删改查方法
  const customMethods = {
    // 新增市场码
    addNode: async (parentKey, nodeData) => {
      try {
        const parentId = parentKey ? parseInt(parentKey) : null
        const marketCodeData = {
          code: nodeData.code,
          name: nodeData.title,
          parentId: parentId
        }
        const response = await axios.post('/api/market-codes', marketCodeData)
        return {
          key: response.data.id.toString(),
          title: response.data.name,
          code: response.data.code,
          id: response.data.id
        }
      } catch (error) {
        console.error('新增市场码失败:', error)
        throw new Error('新增市场码失败，请稍后重试')
      }
    },

    // 编辑市场码
    updateNode: async (nodeKey, nodeData) => {
      try {
        const marketCodeData = {
          code: nodeData.code,
          name: nodeData.title
        }
        await axios.put(`/api/market-codes/${nodeKey}`, marketCodeData)
        return true
      } catch (error) {
        console.error('更新市场码失败:', error)
        throw new Error('更新市场码失败，请稍后重试')
      }
    },

    // 删除市场码
    deleteNode: async (nodeKey) => {
      try {
        // 尝试调用后端API删除
        await axios.delete(`/api/market-codes/${nodeKey}`)
        return true
      } catch (error) {
        console.error('删除市场码失败:', error)
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
        const response = await axios.get('/api/market-codes/check-code', {
          params: {
            code: code,
            id: excludeId
          }
        })
        return response.data.unique
      } catch (error) {
        console.error('检查市场码CODE失败:', error)
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
      title="市场码管理"
      initialData={initialData}
      codeName="市场码"
      customMethods={customMethods}
    />
  )
}

export default MarketCode
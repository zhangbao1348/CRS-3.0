import React from 'react'
import TreeManagement from '../../components/TreeManagement'

// 模拟初始来源码数据
const initialSourceCodeData = [
  {
    key: '1',
    title: '直接来源',
    code: 'DIRECT_SOURCE',
    children: [
      {
        key: '1-1',
        title: '官网直接预订',
        code: 'OFFICIAL_BOOKING',
        children: [
          {
            key: '1-1-1',
            title: 'PC官网',
            code: 'PC_WEBSITE'
          },
          {
            key: '1-1-2',
            title: '移动官网',
            code: 'MOBILE_WEBSITE'
          }
        ]
      },
      {
        key: '1-2',
        title: '电话预订',
        code: 'PHONE_BOOKING',
        children: [
          {
            key: '1-2-1',
            title: '前台电话',
            code: 'FRONT_DESK_PHONE'
          },
          {
            key: '1-2-2',
            title: '预订中心',
            code: 'RESERVATION_CENTER'
          }
        ]
      }
    ]
  },
  {
    key: '2',
    title: '间接来源',
    code: 'INDIRECT_SOURCE',
    children: [
      {
        key: '2-1',
        title: '搜索引擎',
        code: 'SEARCH_ENGINE',
        children: [
          {
            key: '2-1-1',
            title: '百度',
            code: 'BAIDU'
          },
          {
            key: '2-1-2',
            title: '谷歌',
            code: 'GOOGLE'
          },
          {
            key: '2-1-3',
            title: '必应',
            code: 'BING'
          }
        ]
      },
      {
        key: '2-2',
        title: '社交媒体',
        code: 'SOCIAL_MEDIA',
        children: [
          {
            key: '2-2-1',
            title: '微信',
            code: 'WECHAT_SOURCE'
          },
          {
            key: '2-2-2',
            title: '微博',
            code: 'WEIBO'
          },
          {
            key: '2-2-3',
            title: '抖音',
            code: 'DOUYIN'
          }
        ]
      },
      {
        key: '2-3',
        title: '合作网站',
        code: 'PARTNER_WEBSITE',
        children: [
          {
            key: '2-3-1',
            title: '旅游博客',
            code: 'TRAVEL_BLOG'
          },
          {
            key: '2-3-2',
            title: '酒店比价网站',
            code: 'PRICE_COMPARISON'
          }
        ]
      }
    ]
  }
]

const SourceCode = () => {
  return (
    <TreeManagement
      title="来源码管理"
      initialData={initialSourceCodeData}
      codeName="来源码"
    />
  )
}

export default SourceCode
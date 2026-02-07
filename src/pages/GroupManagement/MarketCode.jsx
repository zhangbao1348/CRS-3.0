import React from 'react'
import TreeManagement from '../../components/TreeManagement'

// 模拟初始市场码数据
const initialMarketCodeData = [
  {
    key: '1',
    title: '线上市场',
    code: 'ONLINE',
    children: [
      {
        key: '1-1',
        title: 'OTA平台',
        code: 'OTA',
        children: [
          {
            key: '1-1-1',
            title: '携程',
            code: 'CTRIP'
          },
          {
            key: '1-1-2',
            title: '美团',
            code: 'MEITUAN'
          },
          {
            key: '1-1-3',
            title: '飞猪',
            code: 'FLIGGY'
          }
        ]
      },
      {
        key: '1-2',
        title: '直销平台',
        code: 'DIRECT',
        children: [
          {
            key: '1-2-1',
            title: '官网预订',
            code: 'OFFICIAL'
          },
          {
            key: '1-2-2',
            title: '微信小程序',
            code: 'WECHAT'
          }
        ]
      }
    ]
  },
  {
    key: '2',
    title: '线下市场',
    code: 'OFFLINE',
    children: [
      {
        key: '2-1',
        title: '旅行社',
        code: 'TRAVEL_AGENCY',
        children: [
          {
            key: '2-1-1',
            title: '国内旅行社',
            code: 'DOMESTIC_TA'
          },
          {
            key: '2-1-2',
            title: '国际旅行社',
            code: 'INTERNATIONAL_TA'
          }
        ]
      },
      {
        key: '2-2',
        title: '企业客户',
        code: 'CORPORATE',
        children: [
          {
            key: '2-2-1',
            title: '本地企业',
            code: 'LOCAL_CORP'
          },
          {
            key: '2-2-2',
            title: '跨国企业',
            code: 'MNC'
          }
        ]
      }
    ]
  }
]

const MarketCode = () => {
  return (
    <TreeManagement
      title="市场码管理"
      initialData={initialMarketCodeData}
      codeName="市场码"
    />
  )
}

export default MarketCode
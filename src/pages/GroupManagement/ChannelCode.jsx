import React from 'react'
import TreeManagement from '../../components/TreeManagement'

// 模拟初始渠道码数据
const initialChannelCodeData = [
  {
    key: '1',
    title: '线上渠道',
    code: 'ONLINE_CHANNEL',
    children: [
      {
        key: '1-1',
        title: '分销渠道',
        code: 'DISTRIBUTION',
        children: [
          {
            key: '1-1-1',
            title: '携程分销',
            code: 'CTRIP_DIST'
          },
          {
            key: '1-1-2',
            title: '美团分销',
            code: 'MEITUAN_DIST'
          }
        ]
      },
      {
        key: '1-2',
        title: '直销渠道',
        code: 'DIRECT_CHANNEL',
        children: [
          {
            key: '1-2-1',
            title: '官网',
            code: 'WEBSITE'
          },
          {
            key: '1-2-2',
            title: 'APP',
            code: 'MOBILE_APP'
          },
          {
            key: '1-2-3',
            title: '微信',
            code: 'WECHAT_CHANNEL'
          }
        ]
      }
    ]
  },
  {
    key: '2',
    title: '线下渠道',
    code: 'OFFLINE_CHANNEL',
    children: [
      {
        key: '2-1',
        title: '旅行社渠道',
        code: 'TA_CHANNEL',
        children: [
          {
            key: '2-1-1',
            title: '国内社',
            code: 'DOMESTIC_TA_CHANNEL'
          },
          {
            key: '2-1-2',
            title: '国际社',
            code: 'INTL_TA_CHANNEL'
          }
        ]
      },
      {
        key: '2-2',
        title: '企业渠道',
        code: 'CORP_CHANNEL',
        children: [
          {
            key: '2-2-1',
            title: '协议企业',
            code: 'AGREEMENT_CORP'
          },
          {
            key: '2-2-2',
            title: '临时企业',
            code: 'TEMP_CORP'
          }
        ]
      }
    ]
  }
]

const ChannelCode = () => {
  return (
    <TreeManagement
      title="渠道码管理"
      initialData={initialChannelCodeData}
      codeName="渠道码"
    />
  )
}

export default ChannelCode
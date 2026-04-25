import React, { useState } from 'react'
import { Table, Button, Card, Input, message } from 'antd'
import { SearchOutlined, CalendarOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'



// 模拟携程活动数据
const mockCtripActivities = [
  {
    id: 1,
    activityName: '[携程特惠] 约会好去处，情侣度假65折',
    description: '[促销标签] "十亿豪补"\n[活动说明] 与爱妻、闺蜜红色加倍计算，与促销活动不叠加，如同一个房型参与多个促销活动，以力度最大的为准。\n[温馨提示] 此活动长期有效，到期前一周将与您联系，如一周内未答复将自动延续',
    registrationDate: '2019-10-04至2019-12-31',
    stayDate: '2019-10-04至2020-12-31',
    details: '入住日期：2020-01-08至2020-03-31\n可预订日期：现订现付\n价格：468元，原价为498元'
  },
  {
    id: 2,
    activityName: '[时令全覆盖] 万物复苏，春暖花开',
    description: '[促销标签] "十亿豪补"\n[活动说明] 春暖花开，秋收农富，时令覆盖伴随每个季节',
    registrationDate: '2020-05-15至2022-12-31',
    stayDate: '2020-07-15至2022-12-31',
    details: '2023-01-28至2023-03-21\n2023-04-08至2023-04-13\n2023-04-15至2023-04-27\n2023-04-29至2023-05-07\n2023-05-14至2023-05-20\n2023-05-28至2023-06-02\n2023-06-08至2023-06-09\n2023-06-22至2023-06-23'
  },
  {
    id: 3,
    activityName: '[错峰黄金期] 暑假日错峰专属通道',
    description: '[促销标签] "百日壕赚"\n[流量激励] 首页活动专属展场+榜单专属展示+促销榜单',
    registrationDate: '2020-02-28至2022-12-31',
    stayDate: '2020-01-06至2022-12-31',
    details: '2023-05-04至2023-05-08\n2023-05-14至2023-05-18\n2023-05-22至2023-05-26\n2023-05-29至2023-06-02\n2023-06-05至2023-06-09\n2023-06-12至2023-06-16\n2023-06-19至2023-06-23\n2023-06-26至2023-06-30\n2023-07-03至2023-07-07\n2023-07-10至2023-07-14\n2023-07-17至2023-07-21\n2023-07-24至2023-07-28\n2023-07-31至2023-08-04\n2023-08-07至2023-08-11\n2023-08-14至2023-08-18\n2023-08-21至2023-08-25\n2023-08-28至2023-09-01\n2023-09-04至2023-09-08\n2023-09-11至2023-09-15\n2023-09-18至2023-09-22\n2023-09-25至2023-09-29\n2023-10-09至2023-10-13\n2023-10-16至2023-10-20\n2023-10-23至2023-10-27\n2023-10-30至2023-11-03\n2023-11-06至2023-11-10\n2023-11-13至2023-11-17\n2023-11-20至2023-11-24\n2023-11-27至2023-12-01\n2023-12-04至2023-12-08\n2023-12-11至2023-12-15\n2023-12-18至2023-12-22\n2023-12-25至2023-12-29'
  },
  {
    id: 4,
    activityName: '[特惠一口价] 约价狂欢，超值498元起',
    description: '[促销标签] "十亿豪补"\n[活动说明] 酒店活动页面价为468元起，加8元享1407元，参与酒店需保证原价高于468元，加价方式详见活动规则。\n[温馨提示] 如同一个房型参与多个促销活动，以力度最大的为准，促销活动之间不叠加。',
    registrationDate: '2020-03-23至2022-12-31',
    stayDate: '2020-04-15至2022-12-31',
    details: '入住日期：2020-06-10至2023-03-31\n价格：468元，原价为411元'
  }
]



const CtripActivityManagement = () => {
  const navigate = useNavigate()
  const [activities, setActivities] = useState(mockCtripActivities)

  const handleActivityRegistration = (record) => {
    navigate(`/group-promotion-management/ctrip-activity-registration?id=${record.id}`)
  }

  const columns = [
    {
      title: '活动编号',
      dataIndex: 'serialNumber',
      key: 'serialNumber',
      width: 100,
      render: (_, record) => record.id
    },
    {
      title: '渠道',
      dataIndex: 'channel',
      key: 'channel',
      width: 100,
      render: () => '携程'
    },
    {
      title: '活动名称',
      dataIndex: 'activityName',
      key: 'activityName',
      width: 180
    },
    {
      title: '活动解释',
      dataIndex: 'description',
      key: 'description',
      width: 300
    },
    {
      title: '报名日期',
      dataIndex: 'registrationDate',
      key: 'registrationDate',
      width: 150
    },
    {
      title: '入住日期',
      dataIndex: 'stayDate',
      key: 'stayDate',
      width: 150
    },
    {
      title: '详情',
      dataIndex: 'details',
      key: 'details',
      width: 200
    },
    {
      title: '操作',
      key: 'operate',
      width: 100,
      render: (_, record) => (
        <a href="#" style={{ color: '#1890ff' }} onClick={() => handleActivityRegistration(record)}>活动报名</a>
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <CalendarOutlined />
        携程活动
      </h1>
      
      <Card style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
          <span style={{ marginRight: 8 }}>活动名称:</span>
          <Input style={{ width: 200, marginRight: 8 }} placeholder="请输入活动名称" />
          <Button type="primary">查询</Button>
        </div>
      </Card>

      <Table
        columns={columns}
        dataSource={activities}
        rowKey="id"
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
        }}
        scroll={{ x: 2000 }}
      />

    </div>
  )
}

export default CtripActivityManagement
import { useState, useEffect, useMemo } from 'react'
import { Select, Space } from 'antd'
import api, { hotelRoomTypeApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import InventoryQuotaTable from './InventoryQuotaTable'

const { Option } = Select

const ChannelRoomTypeInventory = () => {
  const { selectedHotelId } = useHotelContext()
  const [channels, setChannels] = useState([])
  const [roomTypes, setRoomTypes] = useState([])
  const [channelsLoading, setChannelsLoading] = useState(false)
  const [roomTypesLoading, setRoomTypesLoading] = useState(false)
  const [filterChannel, setFilterChannel] = useState('全部')
  const [filterRoomType, setFilterRoomType] = useState('全部')

  useEffect(() => {
    setChannelsLoading(true)
    api.get('/channel-codes/third-level')
      .then(res => setChannels(Array.isArray(res) ? res : (res?.data || [])))
      .catch(() => {})
      .finally(() => setChannelsLoading(false))
  }, [])

  useEffect(() => {
    if (!selectedHotelId) return
    setRoomTypesLoading(true)
    hotelRoomTypeApi.getHotelRoomTypes(selectedHotelId)
      .then(res => setRoomTypes(res?.data || []))
      .catch(() => {})
      .finally(() => setRoomTypesLoading(false))
  }, [selectedHotelId])

  // 生成交叉组合的 dimensionItems，根据筛选条件过滤
  const items = useMemo(() => {
    const filteredChannels = filterChannel === '全部'
      ? channels
      : channels.filter(c => c.code === filterChannel)
    const filteredRoomTypes = filterRoomType === '全部'
      ? roomTypes
      : roomTypes.filter(rt => rt.roomTypeCode === filterRoomType)

    const result = []
    filteredChannels.forEach(channel => {
      filteredRoomTypes.forEach(roomType => {
        result.push({
          code: channel.code + ':' + roomType.roomTypeCode,
          name: channel.name + ':' + roomType.roomTypeName
        })
      })
    })
    return result
  }, [channels, roomTypes, filterChannel, filterRoomType])

  // 自定义筛选区域：渠道下拉 + 房型下拉
  const customFilter = (
    <Space>
      <span>渠道+房型筛选：</span>
      <Select
        value={filterChannel}
        onChange={setFilterChannel}
        style={{ width: 200 }}
        showSearch
        optionFilterProp="children"
      >
        <Option value="全部">全部渠道</Option>
        {channels.map(c => (
          <Option key={c.code} value={c.code}>{c.name}（{c.code}）</Option>
        ))}
      </Select>
      <Select
        value={filterRoomType}
        onChange={setFilterRoomType}
        style={{ width: 200 }}
        showSearch
        optionFilterProp="children"
      >
        <Option value="全部">全部房型</Option>
        {roomTypes.map(rt => (
          <Option key={rt.roomTypeCode} value={rt.roomTypeCode}>{rt.roomTypeName}（{rt.roomTypeCode}）</Option>
        ))}
      </Select>
    </Space>
  )

  return (
    <InventoryQuotaTable
      dimensionType="channel_room_type"
      dimensionItems={items}
      dimensionLabel="渠道+房型"
      loading={channelsLoading || roomTypesLoading}
      customFilter={customFilter}
    />
  )
}
export default ChannelRoomTypeInventory

import { useState, useEffect } from 'react'
import api from '../../utils/api'
import InventoryQuotaTable from './InventoryQuotaTable'

const ChannelLevelInventory = () => {
  const [channels, setChannels] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    api.get('/channel-codes/third-level')
      .then(res => setChannels(Array.isArray(res) ? res : (res?.data || [])))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const items = channels.map(c => ({
    code: c.code,
    name: c.name + '（' + c.code + '）'
  }))

  return (
    <InventoryQuotaTable
      dimensionType="channel"
      dimensionItems={items}
      dimensionLabel="渠道"
      loading={loading}
    />
  )
}
export default ChannelLevelInventory

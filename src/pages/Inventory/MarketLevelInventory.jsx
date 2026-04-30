import React, { useState, useEffect } from 'react'
import api from '../../utils/api'
import InventoryQuotaTable from './InventoryQuotaTable'

const MarketLevelInventory = () => {
  const [markets, setMarkets] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    api.get('/market-codes/third-level')
      .then(res => setMarkets(Array.isArray(res) ? res : (res?.data || [])))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const items = markets.map(c => ({
    code: c.code,
    name: c.name + '（' + c.code + '）'
  }))

  return (
    <InventoryQuotaTable
      dimensionType="market"
      dimensionItems={items}
      dimensionLabel="市场码"
      loading={loading}
    />
  )
}
export default MarketLevelInventory

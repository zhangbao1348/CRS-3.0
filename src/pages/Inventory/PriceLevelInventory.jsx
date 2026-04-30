import React, { useState, useEffect } from 'react'
import { ratePlanApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import InventoryQuotaTable from './InventoryQuotaTable'

const PriceLevelInventory = () => {
  const { selectedHotelId } = useHotelContext()
  const [ratePlans, setRatePlans] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!selectedHotelId) return
    setLoading(true)
    ratePlanApi.getRatePlans(selectedHotelId)
      .then(res => setRatePlans(res?.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [selectedHotelId])

  const items = ratePlans.map(rp => ({
    code: rp.rateCode,
    name: rp.rateName + '（' + rp.rateCode + '）'
  }))

  return (
    <InventoryQuotaTable
      dimensionType="rate"
      dimensionItems={items}
      dimensionLabel="房价码"
      loading={loading}
    />
  )
}
export default PriceLevelInventory

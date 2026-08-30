import { useState, useEffect } from 'react'
import { ratePlanApi } from '../../utils/api'
import { useHotelContext } from '../../contexts/HotelContext'
import InventoryQuotaTable from './InventoryQuotaTable'

const PriceLevelInventory = () => {
  const { selectedHotel: hotelCode } = useHotelContext()
  const [ratePlans, setRatePlans] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!hotelCode) return
    setLoading(true)
    ratePlanApi.getRatePlansByHotelCode(hotelCode)
      .then(res => setRatePlans(res?.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [hotelCode])

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

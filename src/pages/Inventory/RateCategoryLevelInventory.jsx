import React, { useState, useEffect } from 'react'
import api from '../../utils/api'
import InventoryQuotaTable from './InventoryQuotaTable'

const RateCategoryLevelInventory = () => {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    api.get('/rate-types/active')
      .then(res => setCategories(Array.isArray(res) ? res : (res?.data || [])))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const items = categories.map(c => ({
    code: c.code,
    name: c.name + '（' + c.code + '）'
  }))

  return (
    <InventoryQuotaTable
      dimensionType="rate_category"
      dimensionItems={items}
      dimensionLabel="房价大类"
      loading={loading}
    />
  )
}
export default RateCategoryLevelInventory

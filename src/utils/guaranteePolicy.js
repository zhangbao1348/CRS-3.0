export const GUARANTEE_TYPE_OPTIONS = [
  { value: 'none', label: '无担保' },
  { value: 'credit_card', label: '信用卡' },
  { value: 'prepaid', label: '预付' },
  { value: 'company', label: '公司' },
  { value: 'third_party', label: '第三方' },
  { value: 'special', label: '特殊' }
]

const GUARANTEE_TYPE_VALUE_MAP = {
  none: 'none',
  '无担保': 'none',
  credit_card: 'credit_card',
  信用卡: 'credit_card',
  prepaid: 'prepaid',
  prepay: 'prepaid',
  预付: 'prepaid',
  company: 'company',
  公司: 'company',
  third_party: 'third_party',
  thirdparty: 'third_party',
  第三方: 'third_party',
  special: 'special',
  特殊: 'special'
}

const GUARANTEE_TYPE_LABEL_MAP = GUARANTEE_TYPE_OPTIONS.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

export const normalizeGuaranteeType = (value) => {
  if (!value) return ''
  const trimmedValue = String(value).trim()
  return GUARANTEE_TYPE_VALUE_MAP[trimmedValue] || GUARANTEE_TYPE_VALUE_MAP[trimmedValue.toLowerCase()] || trimmedValue
}

export const getGuaranteeTypeLabel = (value) => {
  const normalizedValue = normalizeGuaranteeType(value)
  return GUARANTEE_TYPE_LABEL_MAP[normalizedValue] || value || '-'
}

export const isCreditCardGuaranteeType = (value) => normalizeGuaranteeType(value) === 'credit_card'

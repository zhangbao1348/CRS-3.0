export const getSettings = () => {
  const saved = localStorage.getItem('groupSettings')
  if (saved) {
    return JSON.parse(saved)
  }
  return {
    groupControlMode: 'strong',
    hourlyRoom: 'support',
    otaPromotionMode: 'groupRegistration',
    showCtripPrice: false,
    showMeituanPrice: false
  }
}

export const saveSettings = (settings) => {
  localStorage.setItem('groupSettings', JSON.stringify(settings))
}

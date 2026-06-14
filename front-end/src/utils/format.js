export function money(value) {
  return Number(value || 0).toFixed(2)
}

export function shortTime(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

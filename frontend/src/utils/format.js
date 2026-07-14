/**
 * 时间格式化工具函数
 */

/**
 * 相对时间格式化（刚刚 / x分钟前 / x小时前 / x天前 / 具体日期）
 * @param {string|Date} time 时间
 * @returns {string}
 */
export function formatRelativeTime(time) {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return ''

  const now = new Date()
  const diff = now - date
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return formatDate(date)
}

/**
 * 标准日期格式化 yyyy-MM-dd
 * @param {string|Date} time
 * @returns {string}
 */
export function formatDate(time) {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return ''
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

/**
 * 日期时间格式化 yyyy-MM-dd HH:mm
 * @param {string|Date} time
 * @returns {string}
 */
export function formatDateTime(time) {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return ''
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

/**
 * 截止日期倒计时
 * @param {string|Date} dueDate 截止日期
 * @returns {{ text: string, overdue: boolean }}
 */
export function formatDueDateCountdown(dueDate) {
  if (!dueDate) return { text: '', overdue: false }
  const date = new Date(dueDate)
  if (isNaN(date.getTime())) return { text: '', overdue: false }

  const now = new Date()
  const diff = date - now
  const overdue = diff < 0

  const absDiff = Math.abs(diff)
  const days = Math.floor(absDiff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((absDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((absDiff % (1000 * 60 * 60)) / (1000 * 60))

  let text
  if (overdue) {
    if (days > 0) text = `已逾期 ${days}天`
    else if (hours > 0) text = `已逾期 ${hours}小时`
    else text = `已逾期 ${minutes}分钟`
  } else {
    if (days > 0) text = `还剩 ${days}天`
    else if (hours > 0) text = `还剩 ${hours}小时`
    else text = `还剩 ${minutes}分钟`
  }
  return { text, overdue }
}

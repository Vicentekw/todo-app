import { ref } from 'vue'

/**
 * 暗黑模式管理
 * 基于 Element Plus 暗黑模式：在 html 标签上切换 dark class
 */
const isDark = ref(localStorage.getItem('darkMode') === 'true')

function applyDark(value) {
  if (value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
  localStorage.setItem('darkMode', String(value))
}

export function useDark() {
  function toggleDark() {
    isDark.value = !isDark.value
    applyDark(isDark.value)
  }

  return { isDark, toggleDark }
}

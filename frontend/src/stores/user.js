import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 设置登录信息
  function setLogin(data) {
    token.value = data.token
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  // 更新昵称（修改资料后同步）
  function updateNickname(nickname) {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, nickname }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  // 登出，清理状态
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, setLogin, updateNickname, logout }
})

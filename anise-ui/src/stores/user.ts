import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface UserInfo {
  userId: number
  token: string
  nickname: string
  avatarUrl: string
  phone: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref<UserInfo>({
    userId: 0,
    token: '',
    nickname: '未登录',
    avatarUrl: '',
    phone: ''
  })

  const isLoggedIn = computed(() => !!token.value)
  const welcomeText = computed(() => {
    if (isLoggedIn.value) {
      return `欢迎回来, ${userInfo.value.nickname}!`
    }
    return '请先登录'
  })

  function login(newToken: string, info: Partial<UserInfo>) {
    token.value = newToken
    userInfo.value = { ...userInfo.value, ...info }
  }

  function logout() {
    token.value = ''
    userInfo.value = {
      userId: 0,
      token: '',
      nickname: '未登录',
      avatarUrl: '',
      phone: ''
    }
  }

  function updateName(newName: string) {
    userInfo.value.nickname = newName
  }

  function updateAvatar(newAvatarUrl: string) {
    userInfo.value.avatarUrl = newAvatarUrl
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    welcomeText,
    login,
    logout,
    updateName,
    updateAvatar
  }
}, {
  persist: {
    storage: {
      getItem(key: string) {
        return uni.getStorageSync(key)
      },
      setItem(key: string, value: any) {
        uni.setStorageSync(key, value)
      }
    }
  }
})

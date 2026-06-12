import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getImageUrl } from '@/utils/image'

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

  function login(newToken: string, info: Partial<UserInfo>) {
    token.value = newToken
    // 头像URL标准化：非http开头时拼接BASE_URL
    if (info.avatarUrl) {
      info.avatarUrl = getImageUrl(info.avatarUrl)
    }
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
    userInfo.value.avatarUrl = getImageUrl(newAvatarUrl)
  }

  return {
    token,
    userInfo,
    isLoggedIn,
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

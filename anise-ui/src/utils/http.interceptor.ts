import type { RequestConfig, RequestInterceptor, RequestMeta, RequestOptions } from 'uview-pro'

import { envConfig, baseUrl } from '@/env'
import { useUserStore } from '@/stores/user'

// 全局配置
const httpRequestConfig: RequestConfig = {
  baseUrl,
  header: {
    'content-type': 'application/json',
  },
  timeout: 50000,
  meta: {
    originalData: true,
    toast: true,
    loading: true,
  },
}


function showToast(
  title = '',
  icon: 'success' | 'error' | 'none' = 'none',
  options: { duration: number } = { duration: 2000 },
) {
  if (title.length === 0) {
    return
  }
   uni.showToast({
      title,
      icon: title.length && title.length > 7 ? 'none' : icon,
      duration: options.duration || 2000,
    })
}

function showLoading() {
  uni.showLoading({
    title: '加载中...',
    mask: true,
  })
}

// 隐藏加载中，可以替换为uview-pro的u-loading-popup组件
function hideLoading() {
  // 代码示例使用settimeout，仅为演示，实际开发中去掉
  uni.hideLoading()
}


function navigateToLogin(): void {
  const userStore = useUserStore()
  userStore.logout()
  setTimeout(() => {
    uni.$grouter.navigateTo('login');
  }, 100)
}

// 请求/响应拦截器
const httpInterceptor: RequestInterceptor = {
  // 请求拦截器
  request: (config: RequestOptions) => {
    const meta: RequestMeta = config.meta || {}
    meta.loading && showLoading()
    config.header = config.header || {}

    const userStore = useUserStore()
    if (userStore.userInfo.token) {
      config.header.token = `${userStore.userInfo.token}`
    }
    if (userStore.userInfo.userId) {
      config.header.uid = `${userStore.userInfo.userId}`
    }
    return config
  },
  // 响应拦截器
  response: async (response: any) => {
    const meta: RequestMeta = response.config?.meta || {}
    meta.loading && hideLoading()
    const { statusCode, data: rawData, errMsg } = response as any

    // 网络错误
    if (errMsg && errMsg.includes('Failed to connect')) {
      meta.toast && showToast('网络错误', 'error')
      throw new Error('网络错误')
    }
    if (errMsg && errMsg.includes('request:fail')) {
      meta.toast && showToast('请求错误：未知', 'error')
      throw new Error('请求错误：未知')
    }
    // 请求错误
    if (!(statusCode >= 200 && statusCode < 300)) {
      const errorMessage = `请求错误[${statusCode}]`
      meta.toast && showToast(errorMessage, 'error')
      throw new Error(`${errorMessage}：${errMsg}`)
    }
    console.log('原始响应:', response)
    if(rawData.code === 2001) {
      navigateToLogin()
      throw new Error('登录过期，请重新登录')
    }else if(rawData.code !== 200 && rawData.code !== 0 ) {
      meta.toast && showToast(rawData.msg || '请求失败', 'error')
      throw new Error(rawData.msg || '请求失败')
    }
    return rawData.data
  },
}


export { httpRequestConfig, httpInterceptor }

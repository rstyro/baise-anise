# UniApp Vue3 TypeScript 脚手架

一个基于 UniApp + Vue 3 + TypeScript + Vite 的现代化跨平台应用开发脚手架，集成了 Pinia、uView Plus、自动导入等功能。

## 功能特性

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript 超集
- **Vite** - 下一代前端构建工具
- **UniApp** - 一套代码多端运行
- **Pinia** - Vue 官方状态管理库
- **uView Pro** - 优秀的 UniApp UI 组件库
- **自动导入** - 自动导入 Vue 组合式 API 和自定义工具函数
- **环境变量** - 支持开发、测试、生产多环境配置
- **HTTP 请求封装** - 统一的请求拦截和响应处理
- **路由管理** - 基于 pages.json 的路由工具函数

## 快速开始

### 环境要求

- Node.js >= 18.0.0
- pnpm >= 8.0.0

### 安装依赖

```bash
pnpm install
```

### 开发模式

```bash
# H5 开发
pnpm run dev:h5

# 微信小程序开发
pnpm run dev:mp-weixin

# 支付宝小程序开发
pnpm run dev:mp-alipay

# 其他平台开发 (参考 package.json 中的 scripts)
```

### 构建生产版本

```bash
# H5 构建
pnpm run build:h5

# 微信小程序构建
pnpm run build:mp-weixin

# 测试环境 H5 构建
pnpm run build:h5:test
```

### 类型检查

```bash
pnpm run type-check
```

## 项目结构

```
src/
├── api/                 # API 接口定义
│   ├── types/           # 接口类型定义
│   ├── userApi.ts       # 用户相关接口
│   ├── commonApi.ts     # 通用接口
│   └── index.ts         # 接口导出
├── components/          # 自定义组件
│   ├── c-test-card.vue  # 测试卡片组件 (c-前缀自动导入)
│   └── AudioPlayerDemo.vue  # 音频播放器组件
├── pages/               # 页面目录
│   ├── tab-bar/         # TabBar 页面
│   │   ├── index/       # 首页
│   │   └── me/          # 我的页面
│   ├── pinia-demo/      # Pinia 状态管理演示
│   ├── uview-demo/      # uView 组件演示
│   ├── auto-import-demo/ # 自动导入演示
│   ├── http-demo/       # HTTP 请求演示
│   └── audio-demo/      # 音频播放演示
├── stores/              # Pinia 状态管理
│   ├── counter.ts       # 计数器示例
│   ├── user.ts          # 用户状态
│   ├── cart.ts          # 购物车状态
│   └── index.ts         # 状态管理导出
├── utils/               # 工具函数
│   ├── global/          # 全局工具
│   │   ├── audio.ts     # 音频工具
│   │   ├── const.ts     # 常量定义
│   │   ├── tool.ts      # 通用工具函数
│   │   ├── version.ts   # 版本管理
│   │   └── index.ts     # 全局工具导出
│   ├── http.ts          # HTTP 请求配置与拦截器
│   ├── request.ts       # 请求封装
│   └── router.ts        # 路由工具
├── types/               # TypeScript 类型定义
│   ├── api.ts           # API 响应类型
│   └── uview-pro.d.ts   # uView 类型声明
├── App.vue              # 应用根组件
├── main.ts              # 应用入口
├── pages.json           # 页面配置
├── manifest.json        # 应用配置
├── env.ts               # 环境变量配置
└── uni.scss             # 全局样式
```

---

## 1. 请求拦截器

### 1.1 拦截器文件位置

请求拦截器配置在 `src/utils/http.ts` 文件中，基于 uView Plus 的 `$u.http` 模块实现。

### 1.2 实现原理

拦截器分为三个层次：

| 层次 | 配置位置 | 作用 |
|------|----------|------|
| **全局配置** | `u.http.setConfig()` | 设置默认请求头、超时时间等基础配置 |
| **请求拦截器** | `u.http.interceptors.request.use()` | 在请求发送前统一处理（如添加 token） |
| **响应拦截器** | `u.http.interceptors.response.use()` | 在响应返回后统一处理（如处理错误、保存 token） |

### 1.3 完整代码详解

```typescript
// src/utils/http.ts

// 扩展 uni 对象类型，添加 $u 属性支持
declare module '@dcloudio/uni-app' {
  interface Uni {
    $u: any  // 声明 uni.$u 存在
  }
}

/**
 * 配置 HTTP 请求拦截器
 * 由于 uView 需要异步初始化，所以用递归调用确保 $u 已就绪
 */
function setupHttpConfig(): void {
  // 获取 uView 实例
  const u = (uni as any).$u
  
  // 如果 $u 还未初始化，100ms 后重试
  if (!u) {
    setTimeout(setupHttpConfig, 100)
    return
  }

  // ==================== 1. 全局配置 ====================
  u.http.setConfig((config: any) => {
    // 从本地存储获取 token 和 uid
    const token = uni.getStorageSync('token')
    const uid = uni.getStorageSync('uid')
    
    return {
      ...config,  // 保留原有配置
      header: {
        ...config.header,  // 保留原有请求头
        'Content-Type': 'application/json',      // 设置 JSON 格式
        'X-Requested-With': 'XMLHttpRequest',   // 标识 AJAX 请求
        token: token ? `${token}` : null,        // 添加 token
        uid: uid ? `${uid}` : null              // 添加 uid
      }
    }
  })

  // ==================== 2. 请求拦截器 ====================
  u.http.interceptors.request.use((config: any) => {
    // 每次请求前刷新 token（防止 token 过期）
    const token = uni.getStorageSync('token')
    const uid = uni.getStorageSync('uid')
    
    // 动态更新请求头中的 token 和 uid
    config.header.token = token ? `${token}` : null
    config.header.uid = uid ? `${uid}` : null
    
    // 必须返回 config，否则请求会中断
    return config
  })

  // ==================== 3. 响应拦截器 ====================
  u.http.interceptors.response.use(
    // 成功响应处理
    (response: any) => {
      // 如果响应中包含新的 token，自动保存到本地存储
      if (response.data && response.data.token) {
        uni.setStorageSync('token', response.data.token)
      }
      return response
    },
    // 错误响应处理
    (error: any) => {
      console.error('HTTP Error:', error)
      
      // 401 未授权：清除本地 token，跳转到首页重新登录
      if (error.statusCode === 401) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/index/index' })
        }, 1500)
      }
      
      // 抛出错误，让业务代码可以捕获处理
      return Promise.reject(error)
    }
  )
}

// 立即执行配置函数
setupHttpConfig()

// 导出封装的请求方法
export { request, get, post, put, del, patch } from './request'
export type { RequestOptions, ApiResponse, ErrorResponse } from '@/types/api'
```

### 1.4 请求封装层（request.ts）

`src/utils/request.ts` 对底层 HTTP 请求进行了进一步封装：

```typescript
// src/utils/request.ts

import { envConfig, baseUrl } from '@/env'
import type { RequestOptions, ApiResponse, ErrorResponse } from '@/types/api'

/**
 * 基础请求方法
 * @param options 请求配置
 * @returns Promise<T> 返回泛型数据
 */
export async function request<T = any>(options: RequestOptions): Promise<T> {
  const {
    url,                  // 请求地址
    method = 'GET',       // 请求方法，默认 GET
    data = {},            // POST 数据
    params = {},          // URL 参数
    headers = {},         // 自定义请求头
    timeout = envConfig.timeout,  // 超时时间
    showLoading: showLoad = true, // 是否显示加载动画
    loadingText = '加载中...'      // 加载提示文字
  } = options

  const uview = (uni as any).$u
  if (!uview) {
    throw new Error('uview-pro is not initialized')
  }

  // 拼接完整 URL（支持绝对路径和相对路径）
  const fullUrl = url.startsWith('http') ? url : `${baseUrl}${url}`

  const config = {
    url: fullUrl,
    method: method.toLowerCase() as 'get' | 'post' | 'put' | 'delete' | 'patch',
    params,
    data,
    header: headers,
    timeout,
    loading: showLoad,      // uView 自动显示加载动画
    loadingText
  }

  try {
    const result = await uview.http.request(config)
    const response = result.data as ApiResponse<T>
    
    // 业务成功判断：code 为 0 或 200
    if (response.code === 0 || response.code === 200) {
      return response.data
    } else {
      // 业务失败：显示错误提示
      throw handleBusinessError(response.code, response.msg || '请求失败')
    }
  } catch (error: any) {
    const errorResponse: ErrorResponse = {
      code: error.code || -1,
      msg: error.msg || '请求失败',
      error
    }
    
    // 401 未授权处理
    if (errorResponse.code === 401) {
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      setTimeout(() => {
        uni.reLaunch({ url: '/pages/index/index' })
      }, 1500)
    }
    throw errorResponse
  }
}

// 便捷方法封装
export function get<T = any>(url: string, params?: Record<string, any>, options?: Omit<RequestOptions, 'url' | 'method' | 'data' | 'params'>): Promise<T> {
  return request<T>({ url, method: 'GET', params, ...options })
}

export function post<T = any>(url: string, data?: Record<string, any>, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>): Promise<T> {
  return request<T>({ url, method: 'POST', data, ...options })
}

export function put<T = any>(url: string, data?: Record<string, any>, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>): Promise<T> {
  return request<T>({ url, method: 'PUT', data, ...options })
}

export function del<T = any>(url: string, data?: Record<string, any>, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>): Promise<T> {
  return request<T>({ url, method: 'DELETE', data, ...options })
}

export function patch<T = any>(url: string, data?: Record<string, any>, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>): Promise<T> {
  return request<T>({ url, method: 'PATCH', data, ...options })
}
```

### 1.5 使用示例

```typescript
// 在组件或页面中使用
import { get, post } from '@/utils/http'

// GET 请求
const users = await get('/api/users', { page: 1, size: 10 })

// POST 请求
const result = await post('/api/login', { 
  username: 'admin', 
  password: '123456' 
}, {
  showLoading: true,  // 显示加载动画
  loadingText: '登录中...'
})
```

---

## 2. Pinia 状态管理

### 2.1 安装方式

Pinia 已在 `package.json` 中声明，安装依赖时自动安装：

```json
{
  "dependencies": {
    "pinia": "^2.1.7",
    "pinia-plugin-persistedstate": "^3.2.1"  // 状态持久化插件
  }
}
```

### 2.2 配置步骤

**步骤 1：在 main.ts 中注册 Pinia**

```typescript
// src/main.ts
import { createSSRApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";  // 导入持久化插件
import App from "./App.vue";

export function createApp() {
  const app = createSSRApp(App);
  
  // 创建 Pinia 实例
  const pinia = createPinia();
  
  // 安装持久化插件（用于自动保存状态到本地存储）
  pinia.use(piniaPluginPersistedstate);
  
  // 将 Pinia 挂载到 Vue 应用
  app.use(pinia);
  
  return { app, pinia };
}
```

### 2.3 创建 Store

**示例 1：计数器 Store（`src/stores/counter.ts`）**

```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// defineStore 接收两个参数：
// 1. store 名称（必须唯一）
// 2. setup 函数（返回状态和方法）
export const useCounterStore = defineStore('counter', () => {
  // ============ 状态定义 ============
  const count = ref(0)
  
  // ============ 计算属性 ============
  const doubleCount = computed(() => count.value * 2)

  // ============ 方法定义 ============
  function increment() {
    count.value++
  }

  function decrement() {
    count.value--
  }

  function reset() {
    count.value = 0
  }

  function incrementBy(amount: number) {
    count.value += amount
  }

  // 返回需要暴露的状态和方法
  return { count, doubleCount, increment, decrement, reset, incrementBy }
})
```

**示例 2：用户 Store（带持久化，`src/stores/user.ts`）**

```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 用户信息接口定义
interface UserInfo {
  id: number | null
  name: string
  avatar: string
  phone: string
}

export const useUserStore = defineStore('user', () => {
  // ============ 状态定义 ============
  const token = ref('')
  const userInfo = ref<UserInfo>({
    id: null,
    name: '未登录',
    avatar: '',
    phone: ''
  })

  // ============ 计算属性 ============
  // 判断是否已登录
  const isLoggedIn = computed(() => !!token.value)
  
  // 欢迎语
  const welcomeText = computed(() => {
    if (isLoggedIn.value) {
      return `欢迎回来, ${userInfo.value.name}!`
    }
    return '请先登录'
  })

  // ============ 方法定义 ============
  function login(newToken: string, info: Partial<UserInfo>) {
    token.value = newToken
    userInfo.value = { ...userInfo.value, ...info }
  }

  function logout() {
    token.value = ''
    userInfo.value = {
      id: null,
      name: '未登录',
      avatar: '',
      phone: ''
    }
  }

  function updateName(newName: string) {
    userInfo.value.name = newName
  }

  // ============ 返回暴露的内容 ============
  return {
    token,
    userInfo,
    isLoggedIn,
    welcomeText,
    login,
    logout,
    updateName
  }
}, {
  // ============ 持久化配置 ============
  persist: {
    storage: {
      // 使用 uni.setStorageSync/uni.getStorageSync 进行持久化
      getItem(key: string) {
        return uni.getStorageSync(key)
      },
      setItem(key: string, value: any) {
        uni.setStorageSync(key, value)
      }
    }
  }
})
```

### 2.4 Store 导出

所有 Store 在 `src/stores/index.ts` 中统一导出：

```typescript
export { useCounterStore } from './counter'
export { useUserStore } from './user'
export { useCartStore } from './cart'
```

### 2.5 使用方式

**在 Vue 组件中使用**：

```vue
<template>
  <view class="user-info">
    <text>{{ userStore.welcomeText }}</text>
    <u-button v-if="!userStore.isLoggedIn" @click="handleLogin">登录</u-button>
    <u-button v-else @click="handleLogout">退出</u-button>
  </view>
</template>

<script setup lang="ts">
// 导入 Store
import { useUserStore } from '@/stores'

// 获取 Store 实例
const userStore = useUserStore()

// 使用状态
console.log('当前用户:', userStore.userInfo)
console.log('是否登录:', userStore.isLoggedIn)

// 调用方法
async function handleLogin() {
  // 模拟登录请求
  const loginResult = await post('/api/login', { username: 'admin', password: '123' })
  
  // 更新状态
  userStore.login(loginResult.token, {
    id: loginResult.id,
    name: loginResult.name,
    avatar: loginResult.avatar
  })
}

function handleLogout() {
  userStore.logout()
}
</script>
```

**在非组件文件中使用**：

```typescript
// 在 API 文件中获取 token
import { useUserStore } from '@/stores'

const userStore = useUserStore()

function getAuthorizationHeader() {
  return {
    token: userStore.token,
    uid: userStore.userInfo.id
  }
}
```

---

## 3. 路由管理

### 3.1 封装原理

路由工具 `src/utils/router.ts` 基于 `pages.json` 自动构建路由映射表，支持通过页面 `name` 属性进行跳转，无需手动写完整路径。

### 3.2 配置步骤

**步骤 1：在 pages.json 中配置页面名称**

```json
{
  "pages": [
    {
      "path": "pages/pinia-demo/pinia-demo",
      "name": "pinia-demo",        // 必须配置 name 属性
      "style": {
        "navigationBarTitleText": "Pinia 状态管理"
      }
    },
    {
      "path": "pages/http-demo/http-demo",
      "name": "http-demo",
      "style": {
        "navigationBarTitleText": "HTTP 请求演示"
      }
    }
  ],
  "tabBar": {
    "list": [
      {
        "pagePath": "pages/tab-bar/index/index",
        "name": "tab-bar-index",    // TabBar 页面也需要配置 name
        "text": "首页",
        "iconPath": "static/images/tabbar/home.png",
        "selectedIconPath": "static/images/tabbar/home-active.png"
      }
    ]
  }
}
```

### 3.3 封装实现

```typescript
// src/utils/router.ts

// 导入 pages.json 配置
import pagesJson from '../pages.json'

// 页面配置接口定义
interface PageConfig {
  path: string
  name?: string
  style?: Record<string, unknown>
}

interface PagesConfig {
  pages: PageConfig[]
  subPackages?: Array<{
    root: string
    pages: PageConfig[]
  }>
}

const pagesConfig = pagesJson as PagesConfig

// 路由映射表：name -> path
const routeMap = new Map<string, string>()

/**
 * 构建路由映射表
 * 自动从 pages.json 中读取所有页面的 name 和 path
 */
function buildRouteMap() {
  // 遍历普通页面
  pagesConfig.pages.forEach((page) => {
    if (page.name) {
      routeMap.set(page.name, '/' + page.path)
    }
  })

  // 遍历分包页面（如果有）
  pagesConfig.subPackages?.forEach((subPkg) => {
    subPkg.pages.forEach((page) => {
      if (page.name) {
        routeMap.set(page.name, `/${subPkg.root}/${page.path}`)
      }
    })
  })
}

// 初始化路由映射
buildRouteMap()

/**
 * 根据名称获取路径
 * @param name 页面名称
 * @returns 页面路径或 undefined
 */
export function getPathByName(name: string): string | undefined {
  return routeMap.get(name)
}

// 导航选项接口
interface NavigateOptions {
  query?: Record<string, string | number>  // URL 参数
  success?: () => void                     // 成功回调
  fail?: (err: Error) => void              // 失败回调
  complete?: () => void                    // 完成回调
}

/**
 * 构建带参数的 URL
 * @param path 页面路径
 * @param query 查询参数
 * @returns 完整 URL
 */
function buildUrl(path: string, query?: Record<string, string | number>): string {
  if (!query) return path
  const params = Object.entries(query)
    .map(([key, value]) => `${key}=${encodeURIComponent(String(value))}`)
    .join('&')
  return `${path}?${params}`
}

/**
 * 保留当前页面，跳转到应用内的某个页面
 * @param name 页面名称
 * @param options 导航选项
 */
export function navigateTo(name: string, options: NavigateOptions = {}) {
  const path = getPathByName(name)
  if (!path) {
    const error = new Error(`Route not found: ${name}`)
    options.fail?.(error)
    options.complete?.()
    return
  }

  uni.navigateTo({
    url: buildUrl(path, options.query),
    success: options.success,
    fail: (err) => options.fail?.(new Error(err.errMsg || 'Navigation failed')),
    complete: options.complete
  })
}

/**
 * 关闭当前页面，跳转到应用内的某个页面
 * @param name 页面名称
 * @param options 导航选项
 */
export function redirectTo(name: string, options: NavigateOptions = {}) {
  const path = getPathByName(name)
  if (!path) {
    const error = new Error(`Route not found: ${name}`)
    options.fail?.(error)
    options.complete?.()
    return
  }

  uni.redirectTo({
    url: buildUrl(path, options.query),
    success: options.success,
    fail: (err) => options.fail?.(new Error(err.errMsg || 'Redirect failed')),
    complete: options.complete
  })
}

/**
 * 跳转到 tabBar 页面，并关闭其他所有非 tabBar 页面
 * @param name 页面名称
 * @param options 导航选项（无 query 参数）
 */
export function switchTab(name: string, options: Omit<NavigateOptions, 'query'> = {}) {
  const path = getPathByName(name)
  if (!path) {
    const error = new Error(`Route not found: ${name}`)
    options.fail?.(error)
    options.complete?.()
    return
  }

  uni.switchTab({
    url: path,
    success: options.success,
    fail: (err) => options.fail?.(new Error(err.errMsg || 'Switch tab failed')),
    complete: options.complete
  })
}

/**
 * 关闭所有页面，打开应用内的某个页面
 * @param name 页面名称
 * @param options 导航选项
 */
export function reLaunch(name: string, options: NavigateOptions = {}) {
  const path = getPathByName(name)
  if (!path) {
    const error = new Error(`Route not found: ${name}`)
    options.fail?.(error)
    options.complete?.()
    return
  }

  uni.reLaunch({
    url: buildUrl(path, options.query),
    success: options.success,
    fail: (err) => options.fail?.(new Error(err.errMsg || 'ReLaunch failed')),
    complete: options.complete
  })
}

/**
 * 返回上一页
 * @param delta 返回的页数，默认 1
 */
export function navigateBack(delta = 1) {
  uni.navigateBack({ delta })
}

/**
 * 组合式函数，返回所有路由方法
 * @returns 路由方法对象
 */
export function useRouter() {
  return {
    navigateTo,
    redirectTo,
    switchTab,
    reLaunch,
    navigateBack,
    getPathByName
  }
}
```

### 3.4 使用示例

```typescript
// 方式一：直接导入使用
import { navigateTo, switchTab, redirectTo, reLaunch, navigateBack } from '@/utils/router'

// 跳转到普通页面（带参数）
navigateTo('pinia-demo', {
  query: { id: 1, name: 'test' },
  success: () => console.log('跳转成功'),
  fail: (err) => console.error('跳转失败:', err)
})

// 跳转到 TabBar 页面（无参数）
switchTab('tab-bar-index')

// 重定向（关闭当前页）
redirectTo('login')

// 关闭所有页面，打开新页面
reLaunch('home')

// 返回上一页
navigateBack()

// 方式二：使用组合式函数
const router = useRouter()
router.navigateTo('http-demo')
```

---

## 4. 自动导入

### 4.1 安装配置

自动导入由 `unplugin-auto-import` 和 `unplugin-vue-components` 两个 Vite 插件实现，已在 `package.json` 中声明：

```json
{
  "devDependencies": {
    "unplugin-auto-import": "^21.0.0",
    "unplugin-vue-components": "^32.0.0"
  }
}
```

### 4.2 Vite 配置

在 `vite.config.ts` 中配置自动导入：

```typescript
import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";

export default async () => {
  // 动态导入 ESM 模块（避免 CommonJS/ESM 兼容问题）
  const AutoImport = await import("unplugin-auto-import/vite").then(m => m.default || m);
  const Components = await import("unplugin-vue-components/vite").then(m => m.default || m);
  const path = await import("path").then(m => m.default || m);

  return defineConfig({
    plugins: [
      uni(),  // UniApp 插件

      // ============ 自动导入配置 ============
      AutoImport({
        // 需要自动导入的模块
        imports: [
          // 1. Vue 核心 API（ref, reactive, computed 等）
          "vue",
          
          // 2. UniApp 生命周期钩子
          {
            "@dcloudio/uni-app": [
              "onLoad",        // 页面加载完成
              "onShow",        // 页面显示
              "onReady",       // 页面初次渲染完成
              "onHide",        // 页面隐藏
              "onUnload",      // 页面卸载
              "onPullDownRefresh",  // 下拉刷新
              "onReachBottom",      // 触底加载
              "onShareAppMessage",  // 分享
              "onPageScroll",       // 页面滚动
              "onTabItemTap",       // Tab 点击
            ],
          },
          
          // 3. 自定义 API 模块
          {
            "@/api": ["userApi", "commonApi"],  // 自动导入 API 模块
          },
          
          // 4. 路由工具函数
          {
            "@/utils/router": ["navigateTo", "redirectTo", "switchTab", "reLaunch", "navigateBack"],
          },
        ],
        
        // 生成类型声明文件（用于 TypeScript 类型提示）
        dts: "src/auto-imports.d.ts",
        
        // 生成 ESLint 配置（解决 no-undef 警告）
        eslintrc: {
          enabled: true,
          filepath: "./.eslintrc-auto-import.json",
        },
      }),

      // ============ 组件自动导入配置 ============
      Components({
        // 组件搜索目录
        dirs: ["src/components"],
        
        // 自定义组件解析器
        resolvers: [
          (name: string) => {
            // 以 c- 开头的组件自动从 src/components 目录导入
            if (name.startsWith("c-")) {
              return {
                name: "default",  // 默认导出
                from: path.resolve(__dirname, `src/components/${name}.vue`),
                sideEffects: null,
              };
            }
          },
        ],
        
        // 生成组件类型声明文件
        dts: "src/components.d.ts",
        
        // 深度搜索（包括子目录）
        deep: true,
        
        // 支持的文件扩展名
        extensions: ["vue"],
      }),
    ],
    
    // 路径别名配置
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "src"),
      },
    }
  });
};
```

### 4.3 自动导入的内容

| 类型 | 模块 | 自动导入的内容 |
|------|------|----------------|
| **Vue API** | `vue` | ref, reactive, computed, watch, onMounted, onUnmounted 等 |
| **UniApp 生命周期** | `@dcloudio/uni-app` | onLoad, onShow, onReady, onHide, onUnload, onPullDownRefresh 等 |
| **API 模块** | `@/api` | userApi, commonApi |
| **路由工具** | `@/utils/router` | navigateTo, redirectTo, switchTab, reLaunch, navigateBack |
| **组件** | `src/components` | 所有以 `c-` 开头的 `.vue` 文件 |

### 4.4 使用示例

**自动导入 Vue API**：

```typescript
// 无需 import，直接使用
const count = ref(0)
const doubled = computed(() => count.value * 2)
watch(count, (newVal) => console.log('count changed:', newVal))
```

**自动导入 UniApp 生命周期**：

```typescript
// 无需 import，直接使用
onLoad((options) => {
  console.log('页面加载完成', options)
})

onShow(() => {
  console.log('页面显示')
})

onPullDownRefresh(() => {
  console.log('下拉刷新')
  // 刷新数据...
  uni.stopPullDownRefresh()
})
```

**自动导入 API**：

```typescript
// 无需 import，直接使用 userApi
const user = await userApi.login({ username: 'admin', password: '123' })
const list = await commonApi.getList()
```

**自动导入路由**：

```typescript
// 无需 import，直接使用导航函数
navigateTo('pinia-demo')
switchTab('tab-bar-index')
```

**自动导入组件**：

```vue
<template>
  <!-- 无需 import，直接使用 c- 开头的组件 -->
  <c-test-card title="测试卡片" />
  <c-audio-player />
</template>
```

### 4.5 类型声明文件

自动导入会生成两个类型声明文件：

1. **`src/auto-imports.d.ts`** - 自动导入 API 的类型声明
2. **`src/components.d.ts`** - 自动导入组件的类型声明

这两个文件会自动更新，无需手动修改。

---

## 5. 全局配置

### 5.1 全局工具挂载

全局工具在 `src/utils/global/index.ts` 中统一管理，通过 Vue 插件机制挂载到全局。

```typescript
// src/utils/global/index.ts

import type { App } from 'vue'
import versionUtils from './version'   // 版本管理工具
import toolUtils from './tool'         // 通用工具函数
import constConfig from './const'       // 常量配置
import audioUtils from './audio'        // 音频工具

// ============ TypeScript 类型增强 ============

// 增强 Vue 组件实例类型
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $t: typeof toolUtils      // 通用工具
    $version: typeof versionUtils  // 版本工具
    $const: typeof constConfig     // 常量配置
    $audio: typeof audioUtils      // 音频工具
  }
}

// 增强 uni 对象类型
declare global {
  interface Uni {
    $gversion: typeof versionUtils
    $gtool: typeof toolUtils
    $gconst: typeof constConfig
    $gaudio: typeof audioUtils
  }
}

// ============ 全局插件定义 ============
export default {
  install(app: App) {
    // 方式一：挂载到 Vue 全局属性（需通过 getCurrentInstance().proxy 访问）
    app.config.globalProperties.$t = toolUtils
    app.config.globalProperties.$version = versionUtils
    app.config.globalProperties.$const = constConfig
    app.config.globalProperties.$audio = audioUtils

    // 方式二：挂载到 uni 对象（推荐，直接通过 uni.$gxxx 访问）
    if (typeof uni !== 'undefined') {
      uni.$gversion = versionUtils
      uni.$gtool = toolUtils
      uni.$gconst = constConfig
      uni.$gaudio = audioUtils
    } else {
      console.warn('uni 对象不存在，无法挂载工具到 uni')
    }
  }
}

// 按需导出各模块
export { versionUtils, toolUtils, constConfig, audioUtils }
```

### 5.2 注册全局插件

在 `main.ts` 中注册全局插件：

```typescript
// src/main.ts
import globalPlugins from '@/utils/global/index'

export function createApp() {
  const app = createSSRApp(App)
  // ... 其他配置
  
  // 注册全局工具插件
  app.use(globalPlugins)
  
  return { app }
}
```

### 5.3 全局工具详解

#### 5.3.1 版本管理工具（`src/utils/global/version.ts`）

| 方法 | 功能 | 参数 | 返回值 |
|------|------|------|--------|
| `getMiniProgramVersion()` | 获取小程序版本 | 无 | `string` (develop/trial/release) |
| `isDevOrTrialVersion()` | 判断是否为开发或体验版 | 无 | `boolean` |
| `checkUpdate()` | 检查小程序更新 | 无 | `void` |

**使用示例**：

```typescript
// 在组件中
uni.$gversion.checkUpdate()

// 或通过导入使用
import { versionUtils } from '@/utils/global'
versionUtils.checkUpdate()
```

#### 5.3.2 通用工具函数（`src/utils/global/tool.ts`）

| 方法 | 功能 | 参数 | 返回值 |
|------|------|------|--------|
| `toUpperCase(arg)` | 字符串转大写 | `arg: string \| null \| undefined` | `string \| undefined` |
| `copyData(data)` | 复制文本到剪贴板 | `data: string` | `void` |

**使用示例**：

```typescript
// 转大写
const result = uni.$gtool.toUpperCase('hello')  // 'HELLO'

// 复制文本
uni.$gtool.copyData('需要复制的内容')  // 弹出"复制成功"提示
```

#### 5.3.3 常量配置（`src/utils/global/const.ts`）

| 常量 | 值 | 说明 |
|------|-----|------|
| `IMAGES.SHARE_URL` | `'https://66dashun.xyz/static/images/share.png'` | 分享图片地址 |

**使用示例**：

```typescript
const shareUrl = uni.$gconst.IMAGES.SHARE_URL
```

#### 5.3.4 音频工具（`src/utils/global/audio.ts`）

| 方法 | 功能 | 参数 | 返回值 |
|------|------|------|--------|
| `createAudio(audioId, src)` | 创建音频实例 | `audioId: string`, `src: string` | `Promise<void>` |
| `play(audioId)` | 播放音频 | `audioId: string` | `void` |
| `pause(audioId)` | 暂停播放 | `audioId: string` | `void` |
| `stop(audioId)` | 停止播放 | `audioId: string` | `void` |
| `seek(audioId, position)` | 跳转到指定位置 | `audioId: string`, `position: number` | `void` |
| `setVolume(audioId, volume)` | 设置音量 | `audioId: string`, `volume: number` | `void` |
| `setLoop(audioId, loop)` | 设置循环播放 | `audioId: string`, `loop: boolean` | `void` |
| `getDuration(audioId)` | 获取音频时长 | `audioId: string` | `number` |
| `getCurrentTime(audioId)` | 获取当前播放时间 | `audioId: string` | `number` |
| `isPaused(audioId)` | 判断是否暂停 | `audioId: string` | `boolean` |
| `destroyAudio(audioId)` | 销毁音频实例 | `audioId: string` | `void` |

**使用示例**：

```typescript
// 创建并播放音频
await uni.$gaudio.createAudio('bgm', '/static/sound/music.mp3')
uni.$gaudio.play('bgm')

// 设置音量
uni.$gaudio.setVolume('bgm', 0.5)

// 暂停播放
uni.$gaudio.pause('bgm')

// 销毁音频
uni.$gaudio.destroyAudio('bgm')
```

### 5.4 全局工具使用方式对比

| 使用方式 | 代码示例 | 适用场景 |
|----------|----------|----------|
| **uni 对象方式**（推荐） | `uni.$gtool.copyData('text')` | 任意地方（组件、工具函数、API 文件） |
| **Vue 全局属性** | `getCurrentInstance()?.proxy?.$t.copyData('text')` | 仅在 Vue 组件中 |
| **按需导入** | `import { toolUtils } from '@/utils/global'; toolUtils.copyData('text')` | 需要树摇优化的场景 |

---

## 多端构建命令

### H5 端

| 命令 | 描述 |
|------|------|
| `pnpm run dev:h5` | H5 开发模式（默认端口 5173） |
| `pnpm run build:h5` | H5 生产构建 |
| `pnpm run dev:h5:ssr` | H5 SSR 开发模式 |
| `pnpm run build:h5:ssr` | H5 SSR 生产构建 |
| `pnpm run dev:h5:test` | H5 测试环境开发 |
| `pnpm run build:h5:test` | H5 测试环境构建 |

### 小程序端

| 命令 | 描述 |
|------|------|
| `pnpm run dev:mp-weixin` | 微信小程序开发 |
| `pnpm run build:mp-weixin` | 微信小程序构建 |
| `pnpm run dev:mp-alipay` | 支付宝小程序开发 |
| `pnpm run build:mp-alipay` | 支付宝小程序构建 |
| `pnpm run dev:mp-baidu` | 百度小程序开发 |
| `pnpm run build:mp-baidu` | 百度小程序构建 |
| `pnpm run dev:mp-qq` | QQ 小程序开发 |
| `pnpm run build:mp-qq` | QQ 小程序构建 |
| `pnpm run dev:mp-toutiao` | 头条小程序开发 |
| `pnpm run build:mp-toutiao` | 头条小程序构建 |
| `pnpm run dev:mp-jd` | 京东小程序开发 |
| `pnpm run build:mp-jd` | 京东小程序构建 |
| `pnpm run dev:mp-xhs` | 小红书小程序开发 |
| `pnpm run build:mp-xhs` | 小红书小程序构建 |
| `pnpm run dev:mp-harmony` | HarmonyOS 小程序开发 |
| `pnpm run build:mp-harmony` | HarmonyOS 小程序构建 |

### 快应用

| 命令 | 描述 |
|------|------|
| `pnpm run dev:quickapp-webview` | 快应用开发 |
| `pnpm run build:quickapp-webview` | 快应用构建 |
| `pnpm run dev:quickapp-webview-huawei` | 华为快应用开发 |
| `pnpm run build:quickapp-webview-huawei` | 华为快应用构建 |

---

## 开发规范

### 组件命名

- **公共组件**：`c-` 前缀，如 `c-test-card.vue`（自动导入）
- **页面组件**：使用 PascalCase，如 `AudioPlayerDemo.vue`（手动导入）

### 目录结构规范

| 目录 | 用途 | 说明 |
|------|------|------|
| `src/pages/` | 页面目录 | 每个页面一个子目录 |
| `src/components/` | 公共组件 | `c-` 前缀自动导入 |
| `src/api/` | API 接口 | 按模块划分 |
| `src/stores/` | Pinia Store | 每个 Store 一个文件 |
| `src/utils/` | 工具函数 | 通用工具和业务工具 |
| `src/types/` | TypeScript 类型 | 全局类型定义 |

### TypeScript 规范

- 使用 `type` 而非 `interface` 定义类型
- 为所有函数和变量添加类型注解
- 使用 `@/` 路径别名引用项目文件
- 导出类型时使用 `export type`

---

## 注意事项

1. **微信小程序开发**：需要在微信开发者工具中打开 `dist/dev/mp-weixin` 目录
2. **H5 开发**：开发服务器默认端口为 5173
3. **环境变量**：通过 `NODE_ENV` 环境变量控制，可选值：`development`、`test`、`production`
4. **样式文件**：使用 SCSS 编写，全局样式在 `src/uni.scss` 中定义
5. **iOS 音频播放**：需要用户交互后才能播放，详见音频工具说明

---

## 常见问题

### Q: 如何添加新页面？

1. 在 `src/pages/` 目录下创建新页面目录（如 `new-page/`）
2. 在目录中创建 `new-page.vue` 文件
3. 在 `src/pages.json` 中添加页面配置，**必须配置 `name` 属性**：
   ```json
   {
     "pages": [
       {
         "path": "pages/new-page/new-page",
         "name": "new-page",
         "style": { "navigationBarTitleText": "新页面" }
       }
     ]
   }
   ```
4. 如果是 TabBar 页面，还需要在 `tabBar.list` 中配置

### Q: 如何添加新的 API 接口？

1. 在 `src/api/types/` 目录下定义接口类型：
   ```typescript
   export interface NewApiResponse {
     id: number
     name: string
   }
   ```
2. 在 `src/api/` 目录下创建或更新 API 文件：
   ```typescript
   import { get, post } from '@/utils/http'
   import type { NewApiResponse } from './types'
   
   export const newApi = {
     getList: () => get<NewApiResponse[]>('/api/new-list'),
     create: (data: { name: string }) => post<NewApiResponse>('/api/new', data)
   }
   ```
3. 在 `src/api/index.ts` 中导出新的 API：
   ```typescript
   export { newApi } from './newApi'
   ```

### Q: 如何创建新的 Pinia Store？

1. 在 `src/stores/` 目录下创建新的 store 文件（如 `settings.ts`）：
   ```typescript
   import { defineStore } from 'pinia'
   import { ref } from 'vue'
   
   export const useSettingsStore = defineStore('settings', () => {
     const theme = ref('light')
     
     function setTheme(newTheme: string) {
       theme.value = newTheme
     }
     
     return { theme, setTheme }
   }, { persist: true })
   ```
2. 在 `src/stores/index.ts` 中导出新的 store：
   ```typescript
   export { useSettingsStore } from './settings'
   ```
3. 在组件中使用：
   ```typescript
   import { useSettingsStore } from '@/stores'
   const settingsStore = useSettingsStore()
   ```

### Q: 如何添加新的全局工具？

1. 在 `src/utils/global/` 目录下创建新工具文件（如 `storage.ts`）：
   ```typescript
   const storageUtils = {
     getItem: (key: string) => uni.getStorageSync(key),
     setItem: (key: string, value: any) => uni.setStorageSync(key, value),
     removeItem: (key: string) => uni.removeStorageSync(key)
   }
   export default storageUtils
   ```
2. 在 `src/utils/global/index.ts` 中导入并挂载：
   ```typescript
   import storageUtils from './storage'
   
   declare module '@vue/runtime-core' {
     interface ComponentCustomProperties {
       $storage: typeof storageUtils
     }
   }
   
   declare global {
     interface Uni {
       $gstorage: typeof storageUtils
     }
   }
   
   export default {
     install(app: App) {
       app.config.globalProperties.$storage = storageUtils
       if (typeof uni !== 'undefined') {
         uni.$gstorage = storageUtils
       }
     }
   }
   
   export { storageUtils }
   ```

---

## License

MIT
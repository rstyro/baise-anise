declare module 'uview-pro' {
  import { Plugin } from 'vue'
  const uviewPro: Plugin
  export interface RequestMeta {
    originalData?: boolean
    toast?: boolean
    loading?: boolean
    [key: string]: any
  }

  export interface RequestConfig {
    baseUrl?: string
    header?: Record<string, any>
    timeout?: number
    meta?: RequestMeta
    [key: string]: any
  }

  export interface RequestOptions extends RequestConfig {
    url?: string
    method?: string
    data?: any
  }

  export interface RequestInterceptor {
    request?: (config: RequestOptions) => RequestOptions | Promise<RequestOptions>
    response?: (response: any) => any
  }

  export const http: {
    get: (url: string, data?: any, config?: any) => Promise<any>
    post: (url: string, data?: any, config?: any) => Promise<any>
    put: (url: string, data?: any, config?: any) => Promise<any>
    delete: (url: string, data?: any, config?: any) => Promise<any>
    request: (config: any) => Promise<any>
    [key: string]: any
  }
  export const $u: any
  export const httpPlugin: Plugin
  export default uviewPro
}

declare interface Uni {
  $u: {
    toast: (message: string, duration?: number, options?: any) => void
    message: (options: any) => void
    [key: string]: any
  }
}

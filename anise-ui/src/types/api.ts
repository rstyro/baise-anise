export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
  timestamp?: number
  trackerId: string
  extendMap?: Map<string, object>
    [key: string]: any;
}

export interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
  data?: Record<string, any>
  params?: Record<string, any>
  headers?: Record<string, any>
  timeout?: number
  showLoading?: boolean
  loadingText?: string
}

export interface ErrorResponse {
  code: number
  msg: string
  error?: any
}
export interface PageResponse<T = any> {
  list: T[]
  total: number
  page: number
  pageSize: number
}
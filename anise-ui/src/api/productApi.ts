import { http } from 'uview-pro'
import { baseUrl } from '@/env'
import type { ProductItem, ProductDetail, ProductListParams, PageResult } from './types/product'

export const productApi = {
  /** Banner轮播列表 */
  bannerList(): Promise<any[]> {
    return http.post(`${baseUrl}/app/banner/list`)
  },
  /** 小程序 - 商品分类列表 */
  categoryList(): Promise<any[]> {
    return http.post(`${baseUrl}/app/product/categoryList`)
  },

  /**
   * 小程序 - 商品列表（分页 + 分类筛选 + 搜索）
   * pageNum/pageSize 通过 URL query 参数传递，
   * 后端 getHeaderOrParam() 先查 header，再查 request.getParameter()（URL query）
   */
  list(data: ProductListParams): Promise<PageResult<ProductItem>> {
    const { pageNum, pageSize, ...body } = data
    const query = `pageNum=${pageNum ?? 1}&pageSize=${pageSize ?? 10}`
    return http.post(`${baseUrl}/app/product/list?${query}`, body, {
      showLoading: true,
      loadingText: '加载中...',
    })
  },

  /**
   * 小程序 - 商品详情
   */
  detail(productId: number): Promise<ProductDetail> {
    return http.post(`${baseUrl}/app/product/detail`, { productId }, { showLoading: true, loadingText: '加载中...' })
  },
}

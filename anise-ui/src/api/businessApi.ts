import { http } from 'uview-pro'
import { baseUrl } from '@/env'
import type { CartItem } from './types/product'

export const cartApi = {
  /** 加入购物车 */
  add(skuId: number, quantity: number = 1): Promise<void> {
    return http.post(`${baseUrl}/app/cart/add`, { skuId, quantity })
  },
  /** 购物车列表 */
  list(): Promise<CartItem[]> {
    return http.post(`${baseUrl}/app/cart/list`)
  },
  /** 更新数量 */
  updateQuantity(cartId: number, quantity: number): Promise<void> {
    return http.post(`${baseUrl}/app/cart/updateQuantity`, { cartId, quantity })
  },
  /** 更新选中状态 */
  updateSelected(cartId: number, selected: number): Promise<void> {
    return http.post(`${baseUrl}/app/cart/updateSelected`, { cartId, selected })
  },
  /** 全选/取消全选 */
  selectAll(selected: number): Promise<void> {
    return http.post(`${baseUrl}/app/cart/selectAll`, { selected })
  },
  /** 删除 */
  delete(cartId: number): Promise<void> {
    return http.post(`${baseUrl}/app/cart/delete`, { cartId })
  },
}

export const orderApi = {
  /** 提交订单 */
  submit(data: { addressId: number; remark?: string; cartIds?: number[] }): Promise<{ orderId: number; orderNo: string; payAmount: number }> {
    return http.post(`${baseUrl}/app/order/submit`, data, { showLoading: true, loadingText: '提交中...' })
  },
  /** 订单列表 */
  list(status?: number, pageNum = 1, pageSize = 10): Promise<any> {
    const query = `pageNum=${pageNum}&pageSize=${pageSize}`
    return http.post(`${baseUrl}/app/order/list?${query}`, { status })
  },
  /** 订单详情 */
  detail(orderId: number): Promise<any> {
    return http.post(`${baseUrl}/app/order/detail`, { orderId })
  },
  /** 取消订单 */
  cancel(orderId: number): Promise<void> {
    return http.post(`${baseUrl}/app/order/cancel`, { orderId })
  },
  /** 确认收货 */
  confirmReceive(orderId: number): Promise<void> {
    return http.post(`${baseUrl}/app/order/confirmReceive`, { orderId })
  },
}

export const addressApi = {
  list(): Promise<any[]> {
    return http.post(`${baseUrl}/app/address/list`)
  },
  add(data: any): Promise<any> {
    return http.post(`${baseUrl}/app/address/add`, data)
  },
  edit(data: any): Promise<void> {
    return http.post(`${baseUrl}/app/address/edit`, data)
  },
  delete(id: number): Promise<void> {
    return http.post(`${baseUrl}/app/address/delete`, { id })
  },
  setDefault(id: number): Promise<void> {
    return http.post(`${baseUrl}/app/address/setDefault`, { id })
  },
}

export const payApi = {
  unifiedOrder(orderId: number): Promise<any> {
    return http.post(`${baseUrl}/app/pay/unifiedOrder`, { orderId }, { showLoading: true, loadingText: '拉起支付...' })
  },
  mockPaySuccess(orderId: number): Promise<void> {
    return http.post(`${baseUrl}/app/pay/mockPaySuccess`, { orderId })
  },
}

export const aftersaleApi = {
  apply(data: any): Promise<any> {
    return http.post(`${baseUrl}/app/aftersale/apply`, data, { showLoading: true })
  },
  list(status?: number): Promise<any[]> {
    return http.post(`${baseUrl}/app/aftersale/list`, { status })
  },
  detail(id: number): Promise<any> {
    return http.post(`${baseUrl}/app/aftersale/detail`, { id })
  },
}

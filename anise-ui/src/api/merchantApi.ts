import { http } from 'uview-pro'
import { baseUrl } from '@/env'

export interface MerchantDetail {
  id: number
  userId: number
  merchantName: string
  logoUrl: string
  description: string
  contactName: string
  contactPhone: string
  originPlace: string
  licenseImage: string
  foodLicenseImage: string
  auditStatus: number
  auditRemark: string
  commissionRate: number
  settlementType: number
  status: number
  extraJson: any
  createTime: string
  updateTime: string
  isDeleted: number
}

export const merchantApi = {
  detail(merchantId: number): Promise<MerchantDetail> {
    return http.post(`${baseUrl}/app/merchant/detail`, { merchantId }, { showLoading: true, loadingText: '加载中...' })
  },

  productList(merchantId: number, pageNum: number = 1, pageSize: number = 10): Promise<any> {
    const query = `pageNum=${pageNum}&pageSize=${pageSize}`
    return http.post(`${baseUrl}/app/merchant/productList?${query}`, { merchantId }, {
      showLoading: true,
      loadingText: '加载中...',
    })
  },
}
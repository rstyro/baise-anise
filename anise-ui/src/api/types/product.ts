/** 商品列表项 */
export interface ProductItem {
  id: number
  productName: string
  productTitle: string
  mainImage: string
  minPrice: number
  maxPrice: number
  originalPrice: number | null
  sales: number
  originPlace: string
  isSulfurFree: boolean
  dryingLevel: string
  categoryName: string
}

/** 购物车列表项 */
export interface CartItem {
  id: number
  merchantId: number
  merchantName: string
  productId: number
  skuId: number
  productName: string
  mainImage: string
  specName: string
  price: number
  originalPrice: number | null
  quantity: number
  stock: number
  selected: number  // 0或1
}
export interface ProductSku {
  id: number
  specName: string
  specValues: string  // JSON string
  price: number
  originalPrice: number | null
  stock: number
  sales: number
}

/** 商品详情 */
export interface ProductDetail {
  id: number
  productName: string
  productTitle: string
  mainImage: string
  imageList: string[]
  description: string
  originPlace: string
  isSulfurFree: boolean
  dryingLevel: string
  plantingProcess: string
  categoryId: number
  categoryName: string
  sales: number
  merchantId: number
  merchantName: string
  merchantOriginPlace: string
  skuList: ProductSku[]
}

/** 商品列表查询参数 */
export interface ProductListParams {
  categoryId?: number
  keyword?: string
  /** 页码，从1开始，默认1 */
  pageNum: number
  /** 每页条数，默认10，最大1000 */
  pageSize: number
}

/** 分页响应 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

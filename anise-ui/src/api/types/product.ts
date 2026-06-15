export interface SpuAttrItem {
  attrId: number
  attrName: string
  attrValueId: number
  attrValue: string
}

export interface SkuAttrItem {
  attrId: number
  attrName: string
  attrValueId: number
  attrValue: string
}

export interface ProductItem {
  id: number
  productName: string
  productTitle: string
  mainImage: string
  minPrice: number
  maxPrice: number
  originalPrice: number | null
  sales: number
  categoryName: string
  spuAttrs?: SpuAttrItem[]
}

export interface CartItem {
  id: number
  merchantId: number
  merchantName: string
  productId: number
  skuId: number
  productName: string
  mainImage: string
  specName: string
  skuSpecs: string
  price: number
  originalPrice: number | null
  quantity: number
  stock: number
  selected: number
}

export interface ProductSku {
  id: number
  skuCode: string
  saleUnit: string
  unitWeight: number | null
  isVariableWeight: boolean
  minQuantity: number | null
  maxQuantity: number | null
  quantityStep: number | null
  price: number
  originalPrice: number | null
  wholesalePrice: number | null
  stock: number
  sales: number
  skuAttrs?: SkuAttrItem[]
}

export interface ProductDetail {
  id: number
  productName: string
  productTitle: string
  mainImage: string
  imageList: string[]
  description: string
  categoryId: number
  categoryName: string
  sales: number
  merchantId: number
  merchantName: string
  merchantOriginPlace: string
  skuList: ProductSku[]
  spuAttrs?: SpuAttrItem[]
  preSaleStart?: string
  preSaleEnd?: string
  estimatedShipDate?: string
  seasonTag?: string
}

export interface ProductListParams {
  categoryId?: number
  keyword?: string
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

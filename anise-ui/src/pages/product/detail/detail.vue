<template>
  <view class="page">
    <swiper
      class="product-swiper"
      :autoplay="true"
      circular
      indicator-dots
      indicator-color="rgba(255,255,255,0.55)"
      :indicator-active-color="THEME_SUCCESS"
    >
      <swiper-item v-for="(img, idx) in displayImages" :key="idx">
        <image :src="getImageUrl(img)" class="swiper-img" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <view class="content">
      <view class="info-section section-card">
        <view class="price-row">
          <view class="price-wrap">
            <text class="price-symbol">¥</text>
            <text class="price">{{ selectedSku ? selectedSku.price : '--' }}</text>
            <text v-if="selectedSku?.originalPrice" class="original">¥{{ selectedSku.originalPrice }}</text>
          </view>
          <text class="sales">已售 {{ detail.sales || 0 }}</text>
        </view>

        <view class="name">{{ detail.productName }}</view>
        <view v-if="detail.productTitle" class="title">{{ detail.productTitle }}</view>

        <view v-if="productTags.length > 0" class="tags-row">
          <text v-for="tag in productTags" :key="tag" class="tag-outline">{{ tag }}</text>
        </view>
      </view>

      <view v-if="detail.preSaleStart" class="presale-info">
        <view class="presale-main">
          <text class="presale-label">预售中</text>
          <text class="presale-text">{{ formatPresaleTime(detail.preSaleStart, detail.preSaleEnd) }}</text>
        </view>
        <text v-if="detail.estimatedShipDate" class="presale-ship">预计 {{ detail.estimatedShipDate }} 发货</text>
      </view>

      <view class="sku-section section-card">
        <view class="section-header">
          <text class="section-title">选择规格</text>
          <text v-if="selectedSku" class="section-extra">库存 {{ selectedSku.stock }}</text>
        </view>

        <view v-if="skuAttrGroups.length > 0" class="sku-attrs">
          <view v-for="group in skuAttrGroups" :key="group.attrId" class="attr-group">
            <view class="attr-label">{{ group.attrName }}</view>
            <view class="attr-options">
              <view
                v-for="option in group.options"
                :key="option.attrValueId"
                class="attr-option"
                :class="{
                  active: isAttrSelected(group.attrId, option.attrValueId),
                  disabled: !isOptionAvailable(group.attrId, option.attrValueId)
                }"
                @click="selectAttrOption(group.attrId, option.attrValueId)"
              >
                {{ option.attrValue }}
              </view>
            </view>
          </view>
        </view>

        <view v-if="selectedSku" class="sku-selected">
          <view class="selected-meta">
            <text class="selected-label">已选</text>
            <text class="selected-name u-line-1">{{ getSkuAttrDisplay(selectedSku) }}</text>
          </view>
          <view class="selected-status">
            <text class="selected-price">¥{{ selectedSku.price }}</text>
            <text v-if="selectedSku.stock <= 0" class="stock sold-out">售罄</text>
            <text v-else-if="selectedSku.stock < 10" class="stock low-stock">仅剩 {{ selectedSku.stock }}{{ unitName }}</text>
          </view>
        </view>
      </view>

      <view class="qty-section section-card">
        <view class="section-header qty-header">
          <view>
            <text class="section-title">购买数量</text>
            <text v-if="selectedSku" class="qty-hint">
              {{ minQuantity }}-{{ maxQuantity }}{{ unitName }}
            </text>
          </view>
          <view class="qty-box">
            <view class="qty-btn" :class="{ disabled: quantity <= minQuantity }" @click="decreaseQty">
              <u-icon name="minus" size="16" :color="quantity <= minQuantity ? THEME_TEXT_DISABLED : THEME_TEXT" />
            </view>
            <text class="qty-value">{{ quantity }}</text>
            <view class="qty-btn" :class="{ disabled: quantity >= quantityLimit }" @click="increaseQty">
              <u-icon name="plus" size="16" :color="quantity >= quantityLimit ? THEME_TEXT_DISABLED : THEME_TEXT" />
            </view>
          </view>
        </view>
      </view>

      <view v-if="detail.spuAttrs?.length || detail.merchantName" class="param-section section-card">
        <view class="section-header">
          <text class="section-title">商品参数</text>
        </view>
        <view v-for="attr in detail.spuAttrs" :key="attr.attrId" class="param-item">
          <text class="param-label">{{ attr.attrName }}</text>
          <text class="param-value" :class="{ highlight: attr.attrName === '无硫' && attr.attrValue === '是' }">
            {{ attr.attrValue }}{{ attr.attrName === '无硫' && attr.attrValue === '是' ? ' 是' : '' }}
          </text>
        </view>
        <view v-if="detail.merchantName" class="param-item">
          <text class="param-label">店铺</text>
          <text class="param-value">{{ detail.merchantName }}</text>
        </view>
      </view>

      <view v-if="detail.merchantName" class="merchant-section section-card" @click="toMerchant">
        <view class="merchant-icon">
          <u-icon name="shop" size="22" :color="THEME_SUCCESS" />
        </view>
        <view class="merchant-info">
          <text class="merchant-name u-line-1">{{ detail.merchantName }}</text>
          <text v-if="detail.merchantOriginPlace" class="merchant-place">{{ detail.merchantOriginPlace }}</text>
        </view>
        <u-icon name="arrow-right" size="18" :color="THEME_TEXT_GREY" />
      </view>

      <view class="desc-section section-card">
        <view class="section-header">
          <text class="section-title">商品详情</text>
        </view>
        <view v-if="detail.description" class="desc-content" v-html="detail.description" />
        <u-empty v-else text="暂无详情" mode="list" />
      </view>
    </view>

    <view class="bottom-bar">
      <view class="bar-left" @click="toMerchant">
        <u-icon name="home" size="24" :color="THEME_TEXT_SECONDARY" />
        <text class="bar-label">店铺</text>
      </view>
      <view class="bar-left" @click="goCart">
        <u-icon name="shopping-cart" size="24" :color="THEME_TEXT_SECONDARY" />
        <text class="bar-label">购物车</text>
      </view>
      <button class="btn-cart" @click="addToCart">加入购物车</button>
      <button class="btn-buy" :class="{ disabled: buyDisabled }" @click="buyNow">
        {{ detail.preSaleStart ? '立即预订' : '立即购买' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import timeFormat from 'uview-pro/libs/function/timeFormat'
import { productApi } from '@/api/productApi'
import { cartApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import { useCartStore } from '@/stores/cart'
import { THEME_SUCCESS, THEME_TEXT, THEME_TEXT_SECONDARY, THEME_TEXT_GREY, THEME_TEXT_DISABLED } from '@/styles/theme'
import type { ProductDetail, ProductSku, SkuAttrItem } from '@/api/types/product'

type ProductSkuCompat = ProductSku & {
  attrs?: SkuAttrItem[]
}

interface SkuAttrOption {
  attrValueId: number
  attrValue: string
}

interface SkuAttrGroup {
  attrId: number
  attrName: string
  options: SkuAttrOption[]
}

const createEmptyDetail = (): ProductDetail => ({
  id: 0,
  productName: '',
  productTitle: '',
  mainImage: '',
  imageList: [],
  description: '',
  categoryId: 0,
  categoryName: '',
  sales: 0,
  merchantId: 0,
  merchantName: '',
  merchantOriginPlace: '',
  skuList: [],
  spuAttrs: []
})

const detail = ref<ProductDetail>(createEmptyDetail())
const selectedSku = ref<ProductSkuCompat | null>(null)
const quantity = ref(1)
const selectedAttrs = ref<Record<number, number>>({})
const cartStore = useCartStore()

const displayImages = computed(() => {
  const images = detail.value.imageList?.filter(Boolean) || []
  if (images.length > 0) return images
  return detail.value.mainImage ? [detail.value.mainImage] : []
})

const productTags = computed(() => {
  const tags = detail.value.spuAttrs?.map(attr => attr.attrValue).filter(Boolean) || []
  if (detail.value.seasonTag) tags.push(detail.value.seasonTag)
  return tags
})

const unitName = computed(() => selectedSku.value?.isVariableWeight ? '斤' : '件')
const minQuantity = computed(() => selectedSku.value?.minQuantity || 1)
const maxQuantity = computed(() => selectedSku.value?.maxQuantity || 999)
const quantityLimit = computed(() => Math.min(maxQuantity.value, selectedSku.value?.stock || maxQuantity.value))
const buyDisabled = computed(() => !selectedSku.value || selectedSku.value.stock <= 0 || !canPurchase.value)

const canPurchase = computed(() => {
  if (!detail.value.preSaleStart) return true
  const preSaleStart = new Date(detail.value.preSaleStart).getTime()
  return Date.now() >= preSaleStart
})

const getSkuAttrs = (sku: ProductSkuCompat): SkuAttrItem[] => {
  return sku.skuAttrs || sku.attrs || []
}

const skuAttrGroups = computed<SkuAttrGroup[]>(() => {
  const groups: Record<number, SkuAttrGroup> = {}
  detail.value.skuList?.forEach(rawSku => {
    const sku = rawSku as ProductSkuCompat
    getSkuAttrs(sku).forEach(attr => {
      if (!groups[attr.attrId]) {
        groups[attr.attrId] = {
          attrId: attr.attrId,
          attrName: attr.attrName,
          options: []
        }
      }
      const exists = groups[attr.attrId].options.some(option => option.attrValueId === attr.attrValueId)
      if (!exists) {
        groups[attr.attrId].options.push({
          attrValueId: attr.attrValueId,
          attrValue: attr.attrValue
        })
      }
    })
  })
  return Object.values(groups)
})

const isAttrSelected = (attrId: number, attrValueId: number) => {
  return selectedAttrs.value[attrId] === attrValueId
}

const isOptionAvailable = (attrId: number, attrValueId: number) => {
  const tempAttrs: Record<number, number> = {}
  Object.entries(selectedAttrs.value).forEach(([aId, vId]) => {
    if (Number(aId) !== attrId) {
      tempAttrs[Number(aId)] = Number(vId)
    }
  })
  tempAttrs[attrId] = attrValueId

  return detail.value.skuList?.some(rawSku => {
    const sku = rawSku as ProductSkuCompat
    if (sku.stock <= 0) return false
    const skuAttrs = getSkuAttrs(sku)
    return Object.entries(tempAttrs).every(([aId, vId]) => {
      return skuAttrs.some(attr => attr.attrId === Number(aId) && attr.attrValueId === Number(vId))
    })
  }) ?? false
}

const selectAttrOption = (attrId: number, attrValueId: number) => {
  const matchedSku = detail.value.skuList?.find(rawSku => {
    const sku = rawSku as ProductSkuCompat
    if (sku.stock <= 0) return false
    return getSkuAttrs(sku).some(attr => attr.attrId === attrId && attr.attrValueId === attrValueId)
  }) as ProductSkuCompat | undefined

  if (!matchedSku) return
  applySelectedSku(matchedSku)
}

const applySelectedSku = (sku: ProductSkuCompat) => {
  selectedSku.value = { ...sku }
  quantity.value = sku.minQuantity || 1
  const attrs: Record<number, number> = {}
  getSkuAttrs(sku).forEach(attr => {
    attrs[attr.attrId] = attr.attrValueId
  })
  selectedAttrs.value = attrs
}

onLoad((options) => {
  const productId = Number(options?.id)
  if (productId) {
    loadDetail(productId)
  }
})

const loadDetail = async (productId: number) => {
  try {
    const res = await productApi.detail(productId)
    detail.value = {
      ...createEmptyDetail(),
      ...res,
      imageList: res.imageList || [],
      skuList: res.skuList || [],
      spuAttrs: res.spuAttrs || []
    }
    const available = detail.value.skuList.find(sku => sku.stock > 0)
    const initialSku = (available || detail.value.skuList[0]) as ProductSkuCompat | undefined
    if (initialSku) {
      applySelectedSku(initialSku)
    }
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
    console.error('加载商品详情失败', e)
  }
}

const getSkuAttrDisplay = (sku: ProductSkuCompat) => {
  const skuAttrs = getSkuAttrs(sku)
  if (skuAttrs.length > 0) {
    return skuAttrs.map(attr => attr.attrValue).join(' / ')
  }
  return sku.skuCode || `规格${sku.id}`
}

const decreaseQty = () => {
  if (!selectedSku.value) return
  const step = selectedSku.value.quantityStep || 1
  if (quantity.value > minQuantity.value) {
    quantity.value = Math.max(minQuantity.value, quantity.value - step)
  }
}

const increaseQty = () => {
  if (!selectedSku.value) return
  const step = selectedSku.value.quantityStep || 1
  if (quantity.value < quantityLimit.value) {
    quantity.value = Math.min(quantityLimit.value, quantity.value + step)
  }
}

const formatPresaleTime = (start?: string, end?: string) => {
  if (!start) return ''
  const startStr = timeFormat(start, 'mm月dd日 hh:MM:ss')
  if (end) {
    const endStr = timeFormat(end, 'mm月dd日 hh:MM:ss')
    return `${startStr} - ${endStr}预售`
  }
  return `${startStr} 开始预售`
}

const buildSkuSpecs = (): string => {
  const specs: Record<string, string> = {}
  Object.entries(selectedAttrs.value).forEach(([attrId, attrValueId]) => {
    skuAttrGroups.value.forEach(group => {
      if (Number(group.attrId) === Number(attrId)) {
        const option = group.options.find(opt => opt.attrValueId === Number(attrValueId))
        if (option) {
          specs[group.attrName] = option.attrValue
        }
      }
    })
  })
  return JSON.stringify(specs)
}

const addToCart = async () => {
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    return
  }
  if (selectedSku.value.stock <= 0) {
    uni.showToast({ title: '当前规格已售罄', icon: 'none' })
    return
  }
  if (!canPurchase.value) {
    uni.showToast({ title: '预售尚未开始', icon: 'none' })
    return
  }
  try {
    await cartApi.add(selectedSku.value.id, quantity.value, buildSkuSpecs())
    uni.showToast({ title: '已加入购物车', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '加入失败，请重试', icon: 'none' })
  }
}

const buyNow = () => {
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    return
  }
  if (selectedSku.value.stock <= 0) {
    uni.showToast({ title: '当前规格已售罄', icon: 'none' })
    return
  }
  if (!canPurchase.value) {
    uni.showToast({ title: '预售尚未开始', icon: 'none' })
    return
  }
  cartStore.setDirectBuyGoods({
    productId: detail.value.id,
    productName: detail.value.productName,
    mainImage: detail.value.mainImage,
    merchantId: detail.value.merchantId,
    merchantName: detail.value.merchantName,
    skuId: selectedSku.value.id,
    specName: getSkuAttrDisplay(selectedSku.value),
    price: selectedSku.value.price,
    quantity: quantity.value
  })
  uni.$grouter.navigateTo('orderConfirm')
}

const goCart = () => {
  uni.$grouter.switchTab('cart')
}

const toMerchant = () => {
  if (detail.value.merchantId) {
    uni.$grouter.navigateTo('merchantDetail', { query: { id: detail.value.merchantId } })
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding-bottom: 132rpx;
  background: $uni-bg-color-page;
}

.product-swiper {
  width: 100%;
  height: 560rpx;
  background: $uni-bg-color-grey;
}

.swiper-img {
  width: 100%;
  height: 100%;
  background: $uni-bg-color-grey;
}

.content {
  padding: 20rpx;
}

.section-card {
  margin-bottom: 20rpx;
  padding: 24rpx;
  background: $uni-bg-color;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 18rpx;
  box-shadow: 0 8rpx 24rpx rgba(31, 31, 31, 0.04);
}

.price-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 14rpx;
}

.price-wrap {
  display: flex;
  align-items: baseline;
  min-width: 0;
  color: $uni-color-error;
}

.price-symbol {
  font-size: 28rpx;
  font-weight: 700;
}

.price {
  font-size: 52rpx;
  font-weight: 800;
  line-height: 60rpx;
}

.original {
  margin-left: 12rpx;
  color: $uni-text-color-grey;
  font-size: 24rpx;
  text-decoration: line-through;
}

.sales {
  flex-shrink: 0;
  color: $uni-text-color-grey;
  font-size: 24rpx;
}

.name {
  color: $uni-text-color;
  font-size: 34rpx;
  font-weight: 700;
  line-height: 46rpx;
}

.title {
  margin-top: 8rpx;
  color: $uni-text-color-secondary;
  font-size: 26rpx;
  line-height: 38rpx;
}

.tags-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 18rpx;
}

.tag-outline {
  padding: 6rpx 14rpx;
  color: $uni-text-color-secondary;
  font-size: 22rpx;
  line-height: 30rpx;
  background: $uni-bg-color-grey;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 8rpx;
}

.presale-info {
  margin-bottom: 20rpx;
  padding: 22rpx 24rpx;
  background: $uni-color-warning-light;
  border: 1rpx solid rgba(250, 173, 20, 0.24);
  border-radius: 18rpx;
}

.presale-main {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.presale-label {
  flex-shrink: 0;
  padding: 4rpx 12rpx;
  color: $uni-text-color-inverse;
  font-size: 22rpx;
  line-height: 30rpx;
  background: $uni-color-warning;
  border-radius: 6rpx;
}

.presale-text {
  min-width: 0;
  overflow: hidden;
  color: $uni-text-color;
  font-size: 26rpx;
  line-height: 36rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.presale-ship {
  display: block;
  margin-top: 10rpx;
  color: $uni-text-color-secondary;
  font-size: 24rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.section-title {
  color: $uni-text-color;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 40rpx;
}

.section-extra {
  color: $uni-text-color-grey;
  font-size: 24rpx;
}

.sku-attrs {
  display: flex;
  flex-direction: column;
  gap: 22rpx;
}

.attr-label {
  margin-bottom: 14rpx;
  color: $uni-text-color-secondary;
  font-size: 26rpx;
  line-height: 36rpx;
}

.attr-options {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
}

.attr-option {
  max-width: 100%;
  padding: 14rpx 24rpx;
  overflow: hidden;
  color: $uni-text-color;
  font-size: 26rpx;
  line-height: 36rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
  background: $uni-bg-color-grey;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 10rpx;

  &.active {
    color: $uni-color-success;
    background: $uni-color-success-light;
    border-color: $uni-color-success;
    font-weight: 600;
  }

  &.disabled {
    color: $uni-text-color-placeholder;
    background: $uni-bg-color-page;
  }
}

.sku-selected {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: 24rpx;
  padding: 18rpx;
  background: $uni-bg-color-page;
  border-radius: 12rpx;
}

.selected-meta {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 12rpx;
}

.selected-label {
  flex-shrink: 0;
  color: $uni-text-color-grey;
  font-size: 24rpx;
}

.selected-name {
  min-width: 0;
  color: $uni-text-color;
  font-size: 26rpx;
  font-weight: 600;
}

.selected-status {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  gap: 10rpx;
}

.selected-price {
  color: $uni-color-error;
  font-size: 28rpx;
  font-weight: 700;
}

.stock {
  font-size: 22rpx;
}

.sold-out {
  color: $uni-text-color-placeholder;
}

.low-stock {
  color: $uni-color-warning;
}

.qty-header {
  margin-bottom: 0;
}

.qty-hint {
  display: block;
  margin-top: 6rpx;
  color: $uni-text-color-grey;
  font-size: 22rpx;
}

.qty-box {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  height: 64rpx;
  overflow: hidden;
  background: $uni-bg-color-grey;
  border-radius: 12rpx;
}

.qty-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;

  &.disabled {
    background: $uni-bg-color-page;
  }
}

.qty-value {
  width: 80rpx;
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 600;
  text-align: center;
}

.param-item {
  display: flex;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;

  &:last-child {
    border-bottom: none;
  }
}

.param-label {
  width: 150rpx;
  flex-shrink: 0;
  color: $uni-text-color-grey;
  font-size: 26rpx;
}

.param-value {
  flex: 1;
  min-width: 0;
  color: $uni-text-color;
  font-size: 26rpx;
  line-height: 36rpx;

  &.highlight {
    color: $uni-color-success;
    font-weight: 600;
  }
}

.merchant-section {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.merchant-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 54rpx;
  height: 54rpx;
  flex-shrink: 0;
  background: $uni-color-success-light;
  border-radius: 50%;
}

.merchant-info {
  flex: 1;
  min-width: 0;
}

.merchant-name {
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 700;
  line-height: 38rpx;
}

.merchant-place {
  display: block;
  margin-top: 4rpx;
  color: $uni-text-color-grey;
  font-size: 24rpx;
  line-height: 34rpx;
}

.desc-section {
  margin-bottom: 0;
}

.desc-content {
  color: $uni-text-color-secondary;
  font-size: 28rpx;
  line-height: 1.8;
}

.bottom-bar {
  position: fixed;
  bottom: var(--window-bottom, 0);
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  height: 112rpx;
  padding: 0 20rpx;
  background: $uni-bg-color;
  box-shadow: 0 -8rpx 24rpx rgba(31, 31, 31, 0.08);
}

.bar-left {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 76rpx;
  flex-shrink: 0;
  flex-direction: column;
  gap: 4rpx;
}

.bar-label {
  color: $uni-text-color-secondary;
  font-size: 20rpx;
  line-height: 28rpx;
}

.btn-cart,
.btn-buy {
  height: 72rpx;
  flex: 1;
  margin: 0 0 0 14rpx;
  border: none;
  border-radius: 36rpx;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 72rpx;
}

.btn-cart {
  color: $uni-color-success;
  background: $uni-color-success-light;
}

.btn-buy {
  color: $uni-text-color-inverse;
  background: $uni-color-success;

  &.disabled {
    color: $uni-text-color-grey;
    background: $uni-text-color-disable;
  }
}
</style>

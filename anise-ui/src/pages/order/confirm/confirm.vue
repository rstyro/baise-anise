<template>
  <view class="page">
    <view class="content">
      <view v-if="selectedAddress" class="address-card section-card" @click="selectAddress">
        <view class="address-icon">
          <u-icon name="map" size="22" :color="THEME_SUCCESS" />
        </view>
        <view class="address-body">
          <view class="address-top">
            <text class="addr-name">{{ selectedAddress.realName }}</text>
            <text class="addr-phone">{{ selectedAddress.phone }}</text>
            <text v-if="selectedAddress.isDefault === 1" class="default-tag">默认</text>
          </view>
          <text class="addr-detail">
            {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }} {{ selectedAddress.detailAddress }}
          </text>
        </view>
        <u-icon name="arrow-right" size="18" :color="THEME_TEXT_GREY" />
      </view>

      <view v-else class="address-empty section-card" @click="selectAddress">
        <view class="address-icon">
          <u-icon name="plus" size="22" :color="THEME_SUCCESS" />
        </view>
        <text class="address-empty-text">请选择收货地址</text>
        <u-icon name="arrow-right" size="18" :color="THEME_TEXT_GREY" />
      </view>

      <view v-for="group in groupedGoodsList" :key="group.merchantId" class="goods-section section-card">
        <view class="shop-header">
          <view class="shop-title">
            <view class="shop-icon">
              <u-icon name="shop" size="18" :color="THEME_SUCCESS" />
            </view>
            <text class="shop-name u-line-1">{{ group.merchantName }}</text>
          </view>
          <text class="shop-count">{{ group.items.length }}件商品</text>
        </view>

        <view v-for="item in group.items" :key="item.id" class="goods-item">
          <image :src="getImageUrl(item.mainImage)" class="goods-img" mode="aspectFill" />
          <view class="goods-info">
            <view class="goods-name u-line-2">{{ item.productName }}</view>
            <view v-if="getSpecText(item)" class="goods-spec u-line-1">{{ getSpecText(item) }}</view>
          </view>
          <view class="goods-right">
            <text class="goods-price">¥{{ item.price }}</text>
            <text class="goods-qty">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>

      <view class="amount-section section-card">
        <view class="amount-row">
          <text>商品合计</text>
          <text>¥{{ totalAmount }}</text>
        </view>
        <view class="amount-row">
          <text>运费</text>
          <text class="free-fee">免运费</text>
        </view>
        <view class="amount-row total-row">
          <text>实付款</text>
          <text class="total-price">¥{{ totalAmount }}</text>
        </view>
      </view>

      <view class="remark-section section-card">
        <text class="remark-label">买家备注</text>
        <input
          v-model="remark"
          class="remark-input"
          placeholder="选填，请先和商家协商一致"
          placeholder-class="remark-placeholder"
        />
      </view>
    </view>

    <view class="submit-area">
      <view class="submit-total">
        <text class="submit-label">合计</text>
        <text class="submit-price">¥{{ totalAmount }}</text>
      </view>
      <button class="submit-btn" :disabled="submitting" @click="submitOrder">
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { cartApi, orderApi, addressApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import { useCartStore } from '@/stores/cart'
import { THEME_SUCCESS, THEME_TEXT_GREY } from '@/styles/theme'
import type { CartItem } from '@/api/types/product'

interface AddressItem {
  id: number
  realName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault?: number
}

interface GoodsGroup {
  merchantId: number
  merchantName: string
  items: CartItem[]
}

type OrderSubmitData = {
  addressId: number
  remark?: string
  cartIds?: number[]
  skuId?: number
  quantity?: number
  merchantId?: number
}

const cartIds = ref<number[]>([])
const selectedAddress = ref<AddressItem | null>(null)
const goodsList = ref<CartItem[]>([])
const remark = ref('')
const merchantId = ref<number | undefined>()
const isDirectBuy = ref(false)
const submitting = ref(false)
const cartStore = useCartStore()

const groupedGoodsList = computed<GoodsGroup[]>(() => {
  const groups: Map<number, GoodsGroup> = new Map()
  goodsList.value.forEach(item => {
    const currentMerchantId = item.merchantId || 0
    if (!groups.has(currentMerchantId)) {
      groups.set(currentMerchantId, {
        merchantId: currentMerchantId,
        merchantName: item.merchantName || '默认店铺',
        items: []
      })
    }
    groups.get(currentMerchantId)!.items.push(item)
  })
  return Array.from(groups.values())
})

const totalAmount = computed(() => {
  const sum = goodsList.value.reduce((s, item) => s + item.price * item.quantity, 0)
  return sum.toFixed(2)
})

const getSpecText = (item: CartItem): string => {
  return item.skuSpecs ? formatSkuSpecs(item.skuSpecs) : item.specName
}

const formatSkuSpecs = (skuSpecs: string): string => {
  if (!skuSpecs || skuSpecs === '{}') return ''
  try {
    const obj = JSON.parse(skuSpecs) as Record<string, unknown>
    const values = Object.values(obj).filter((v): v is string | number => v !== null && v !== undefined && v !== '')
    return values.join(', ')
  } catch (e) {
    return skuSpecs
  }
}

const parseCartIds = (options?: { cartIds?: string; query?: { cartIds?: string } }) => {
  const cartIdsStr = options?.cartIds || options?.query?.cartIds || ''
  if (!cartIdsStr) return
  cartIds.value = cartIdsStr
    .split(',')
    .map(Number)
    .filter(id => !Number.isNaN(id))
}

const loadDefaultAddress = async () => {
  const addrs = await addressApi.list() as AddressItem[]
  selectedAddress.value = (addrs || []).find(addr => addr.isDefault === 1) || addrs?.[0] || null
}

const loadGoods = async () => {
  const buyNowGoods = cartStore.getAndClearDirectBuyGoods()
  if (buyNowGoods) {
    goodsList.value = [{
      id: buyNowGoods.skuId,
      productId: buyNowGoods.productId,
      skuId: buyNowGoods.skuId,
      productName: buyNowGoods.productName,
      mainImage: buyNowGoods.mainImage,
      merchantId: buyNowGoods.merchantId,
      merchantName: buyNowGoods.merchantName,
      specName: buyNowGoods.specName,
      skuSpecs: '',
      price: buyNowGoods.price,
      originalPrice: null,
      quantity: buyNowGoods.quantity,
      stock: 0,
      selected: 1
    }]
    merchantId.value = buyNowGoods.merchantId
    isDirectBuy.value = true
    cartIds.value = [buyNowGoods.skuId]
    return
  }

  const cart = await cartApi.list()
  goodsList.value = (cart || []).filter(item => item.selected === 1 && (cartIds.value.length === 0 || cartIds.value.includes(item.id)))
  merchantId.value = goodsList.value[0]?.merchantId || undefined
}

onLoad(async (options?: { cartIds?: string; query?: { cartIds?: string } }) => {
  parseCartIds(options)
  await loadGoods()
  await loadDefaultAddress()
})

onShow(() => {
  const app = getApp() as { globalData?: { selectedAddress?: AddressItem } }
  const addr = app.globalData?.selectedAddress
  if (addr) {
    selectedAddress.value = addr
    delete app.globalData?.selectedAddress
  }
})

const selectAddress = () => {
  uni.$grouter.navigateTo('addressList', { query: { mode: 'select' } })
}

const submitOrder = async () => {
  if (submitting.value) return
  if (!selectedAddress.value) {
    uni.showToast({ title: '请选择收货地址', icon: 'none' })
    return
  }
  if (!goodsList.value.length) {
    uni.showToast({ title: '暂无可提交商品', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const submitData: OrderSubmitData = {
      addressId: selectedAddress.value.id,
      remark: remark.value,
      merchantId: merchantId.value
    }

    if (isDirectBuy.value) {
      const item = goodsList.value[0]
      submitData.skuId = item.skuId || item.id
      submitData.quantity = item.quantity
    } else {
      submitData.cartIds = cartIds.value.length > 0 ? cartIds.value : undefined
    }

    await orderApi.submit(submitData)
    uni.showToast({ title: '下单成功', icon: 'success' })
    setTimeout(() => {
      uni.$grouter.redirectTo('orderList')
    }, 800)
  } catch (e) {
    console.error(e)
    uni.showToast({ title: '提交失败，请重试', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding-bottom: 132rpx;
  background: $uni-bg-color-page;
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

.address-card,
.address-empty {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.address-icon,
.shop-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: $uni-color-success-light;
  border-radius: 50%;
}

.address-icon {
  width: 56rpx;
  height: 56rpx;
}

.address-body {
  flex: 1;
  min-width: 0;
}

.address-top {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.addr-name {
  color: $uni-text-color;
  font-size: 30rpx;
  font-weight: 700;
}

.addr-phone {
  color: $uni-text-color-secondary;
  font-size: 28rpx;
}

.default-tag {
  padding: 2rpx 10rpx;
  color: $uni-color-success;
  font-size: 20rpx;
  line-height: 28rpx;
  background: $uni-color-success-light;
  border-radius: 6rpx;
}

.addr-detail {
  color: $uni-text-color-secondary;
  font-size: 26rpx;
  line-height: 38rpx;
}

.address-empty-text {
  flex: 1;
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 600;
}

.shop-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding-bottom: 18rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.shop-title {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 12rpx;
}

.shop-icon {
  width: 42rpx;
  height: 42rpx;
}

.shop-name {
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 700;
}

.shop-count {
  flex-shrink: 0;
  color: $uni-text-color-grey;
  font-size: 24rpx;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 22rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;

  &:last-child {
    padding-bottom: 0;
    border-bottom: none;
  }
}

.goods-img {
  width: 116rpx;
  height: 116rpx;
  flex-shrink: 0;
  background: $uni-bg-color-grey;
  border-radius: 14rpx;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 38rpx;
}

.goods-spec {
  display: inline-block;
  max-width: 100%;
  margin-top: 10rpx;
  padding: 4rpx 10rpx;
  color: $uni-text-color-grey;
  font-size: 22rpx;
  line-height: 30rpx;
  background: $uni-bg-color-grey;
  border-radius: 8rpx;
}

.goods-right {
  display: flex;
  align-items: flex-end;
  flex-shrink: 0;
  flex-direction: column;
  gap: 8rpx;
}

.goods-price {
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 700;
}

.goods-qty {
  color: $uni-text-color-grey;
  font-size: 24rpx;
}

.amount-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12rpx 0;
  color: $uni-text-color-secondary;
  font-size: 28rpx;
}

.free-fee {
  color: $uni-color-success;
}

.total-row {
  margin-top: 8rpx;
  padding-top: 20rpx;
  color: $uni-text-color;
  font-weight: 700;
  border-top: 1rpx solid $uni-border-color-light;
}

.total-price {
  color: $uni-color-error;
  font-size: 36rpx;
  font-weight: 800;
}

.remark-section {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.remark-label {
  flex-shrink: 0;
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 600;
}

.remark-input {
  flex: 1;
  min-width: 0;
  color: $uni-text-color;
  font-size: 28rpx;
}

:deep(.remark-placeholder) {
  color: $uni-text-color-placeholder;
}

.submit-area {
  position: fixed;
  bottom: var(--window-bottom, 0);
  left: 0;
  right: 0;
  z-index: 99;
  display: flex;
  align-items: center;
  gap: 20rpx;
  height: 112rpx;
  padding: 0 20rpx;
  background: $uni-bg-color;
  box-shadow: 0 -8rpx 24rpx rgba(31, 31, 31, 0.08);
}

.submit-total {
  display: flex;
  align-items: baseline;
  flex: 1;
  min-width: 0;
  gap: 8rpx;
}

.submit-label {
  color: $uni-text-color-secondary;
  font-size: 24rpx;
}

.submit-price {
  color: $uni-color-error;
  font-size: 36rpx;
  font-weight: 800;
}

.submit-btn {
  width: 260rpx;
  height: 76rpx;
  flex-shrink: 0;
  margin: 0;
  color: $uni-text-color-inverse;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 76rpx;
  background: $uni-color-success;
  border: none;
  border-radius: 38rpx;

  &[disabled] {
    color: $uni-text-color-grey;
    background: $uni-text-color-disable;
  }
}
</style>

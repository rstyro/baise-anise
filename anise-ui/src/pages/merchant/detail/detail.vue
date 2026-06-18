<template>
  <view class="page">
    <view class="merchant-hero">
      <view class="hero-main">
        <image :src="getImageUrl(detail.logoUrl)" class="logo" mode="aspectFill" />
        <view class="hero-info">
          <view class="merchant-name u-line-2">{{ detail.merchantName || '店铺详情' }}</view>
          <view class="hero-tags">
            <view v-if="detail.auditStatus === 1" class="cert-tag">
              <u-icon name="checkmark-circle" size="16" :color="THEME_SUCCESS" />
              <text>平台认证</text>
            </view>
            <view v-if="detail.originPlace" class="origin-tag">
              <u-icon name="map" size="16" :color="THEME_TEXT_INVERSE" />
              <text class="u-line-1">{{ detail.originPlace }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="content">
      <view class="intro-section section-card">
        <view class="section-head">
          <view class="section-title">
            <view class="section-icon success">
              <u-icon name="file-text" size="18" :color="THEME_SUCCESS" />
            </view>
            <text>店铺简介</text>
          </view>
        </view>
        <view v-if="detail.description" class="desc-content">{{ detail.description }}</view>
        <view v-else class="empty-text">暂无简介</view>
      </view>

      <view class="public-info section-card">
        <view class="section-head">
          <view class="section-title">
            <view class="section-icon success">
              <u-icon name="home" size="18" :color="THEME_SUCCESS" />
            </view>
            <text>店铺信息</text>
          </view>
        </view>
        <view class="info-list">
          <view class="info-item">
            <view class="info-icon">
              <u-icon name="map" size="18" :color="THEME_SUCCESS" />
            </view>
            <text class="info-label">产地</text>
            <text class="info-value u-line-1">{{ detail.originPlace || '暂未填写' }}</text>
          </view>
          <view class="info-item">
            <view class="info-icon">
              <u-icon name="checkmark-circle" size="18" :color="getAuditColor(detail.auditStatus)" />
            </view>
            <text class="info-label">认证</text>
            <text class="info-value" :class="getAuditClass(detail.auditStatus)">
              {{ getAuditText(detail.auditStatus) }}
            </text>
          </view>
          <view class="info-item">
            <view class="info-icon">
              <u-icon name="grid" size="18" :color="detail.status === 1 ? THEME_SUCCESS : THEME_TEXT_GREY" />
            </view>
            <text class="info-label">状态</text>
            <text class="info-value" :class="detail.status === 1 ? 'status-active' : 'status-disable'">
              {{ detail.status === 1 ? '正常营业' : '暂停营业' }}
            </text>
          </view>
        </view>
      </view>

      <view class="products-section section-card">
        <view class="section-header">
          <view class="section-title">
            <view class="section-icon success">
              <u-icon name="shopping-cart" size="18" :color="THEME_SUCCESS" />
            </view>
            <text>店铺商品</text>
          </view>
          <view class="more-link" @click="goProductList">
            <text>全部</text>
            <u-icon name="arrow-right" size="16" :color="THEME_TEXT_GREY" />
          </view>
        </view>

        <view v-if="products.length > 0" class="product-grid">
          <view
            v-for="item in products"
            :key="item.id"
            class="product-card"
            @click="goProductDetail(item.id)"
          >
            <image :src="getImageUrl(item.mainImage)" class="product-img" mode="aspectFill" />
            <view class="product-body">
              <view class="product-name u-line-2">{{ item.productName }}</view>
              <view class="product-price-row">
                <text class="product-price">¥{{ formatAmount(item.minPrice) }}</text>
                <text class="product-sales">已售{{ item.sales || 0 }}</text>
              </view>
            </view>
          </view>
        </view>

        <view v-else class="empty-section">
          <u-empty text="暂无商品" mode="list" />
        </view>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="bar-btn primary" @click="goProductList">
        <u-icon name="shopping-cart" size="26" :color="THEME_TEXT_INVERSE" />
        <text>进入店铺</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { merchantApi } from '@/api/merchantApi'
import { getImageUrl } from '@/utils/image'
import { THEME_SUCCESS, THEME_WARNING, THEME_TEXT_GREY, THEME_TEXT_INVERSE } from '@/styles/theme'

interface PublicMerchantDetail {
  id: number
  merchantName: string
  logoUrl: string
  description: string
  originPlace: string
  auditStatus: number
  status: number
}

interface MerchantProduct {
  id: number
  mainImage: string
  productName: string
  minPrice: number | string
  sales?: number
}

interface ProductPageResult {
  records?: MerchantProduct[]
}

const createEmptyDetail = (): PublicMerchantDetail => ({
  id: 0,
  merchantName: '',
  logoUrl: '',
  description: '',
  originPlace: '',
  auditStatus: 0,
  status: 1,
})

const detail = ref<PublicMerchantDetail>(createEmptyDetail())
const products = ref<MerchantProduct[]>([])

onLoad((options?: { id?: string | number }) => {
  const merchantId = Number(options?.id)
  if (!merchantId) {
    uni.showToast({ title: '店铺不存在', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1000)
    return
  }
  loadDetail(merchantId)
  loadProducts(merchantId)
})

const loadDetail = async (merchantId: number) => {
  try {
    const res = await merchantApi.detail(merchantId)
    detail.value = {
      id: res.id,
      merchantName: res.merchantName || '',
      logoUrl: res.logoUrl || '',
      description: res.description || '',
      originPlace: res.originPlace || '',
      auditStatus: res.auditStatus ?? 0,
      status: res.status ?? 1,
    }
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
    console.error('加载商家详情失败', e)
  }
}

const loadProducts = async (merchantId: number) => {
  try {
    const res = await merchantApi.productList(merchantId, 1, 6) as ProductPageResult
    products.value = res.records || []
  } catch (e) {
    console.error('加载商家商品失败', e)
  }
}

const getAuditText = (status: number): string => {
  const map: Record<number, string> = {
    0: '认证审核中',
    1: '平台认证',
    2: '暂未认证',
  }
  return map[status] || '暂未认证'
}

const getAuditClass = (status: number): string => {
  if (status === 1) return 'status-active'
  if (status === 0) return 'status-pending'
  return 'status-disable'
}

const getAuditColor = (status: number): string => {
  if (status === 1) return THEME_SUCCESS
  if (status === 0) return THEME_WARNING
  return THEME_TEXT_GREY
}

const formatAmount = (value?: number | string): string => {
  const amount = Number(value || 0)
  return Number.isNaN(amount) ? '0.00' : amount.toFixed(2)
}

const goProductDetail = (productId: number) => {
  if (!productId) return
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}

const goProductList = () => {
  if (!detail.value.id) return
  uni.$grouter.navigateTo('merchantProductList', { query: { id: detail.value.id } })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding-bottom: 132rpx;
  background: $uni-bg-color-page;
}

.merchant-hero {
  position: relative;
  padding: 56rpx 28rpx 52rpx;
  background: $uni-color-success;

  &::after {
    position: absolute;
    right: 0;
    bottom: -1rpx;
    left: 0;
    height: 28rpx;
    content: '';
    background: $uni-bg-color-page;
    border-radius: 28rpx 28rpx 0 0;
  }
}

.hero-main {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.logo {
  width: 132rpx;
  height: 132rpx;
  flex-shrink: 0;
  padding: 6rpx;
  background: $uni-bg-color;
  border-radius: 22rpx;
  box-shadow: 0 10rpx 28rpx rgba(31, 31, 31, 0.14);
}

.hero-info {
  flex: 1;
  min-width: 0;
}

.merchant-name {
  color: $uni-text-color-inverse;
  font-size: 38rpx;
  font-weight: 800;
  line-height: 48rpx;
}

.hero-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 16rpx;
}

.cert-tag,
.origin-tag {
  display: flex;
  align-items: center;
  max-width: 100%;
  gap: 6rpx;
  padding: 6rpx 14rpx;
  font-size: 23rpx;
  line-height: 32rpx;
  border-radius: 18rpx;
}

.cert-tag {
  color: $uni-color-success;
  background: $uni-bg-color;
}

.origin-tag {
  color: $uni-text-color-inverse;
  background: rgba(255, 255, 255, 0.16);
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

.section-head {
  padding-bottom: 18rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.section-title {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 12rpx;
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 700;
}

.section-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42rpx;
  height: 42rpx;
  flex-shrink: 0;
  border-radius: 50%;

  &.success {
    background: $uni-color-success-light;
  }
}

.desc-content {
  padding-top: 18rpx;
  color: $uni-text-color-secondary;
  font-size: 28rpx;
  line-height: 44rpx;
}

.empty-text {
  padding-top: 18rpx;
  color: $uni-text-color-grey;
  font-size: 26rpx;
}

.info-list {
  padding-top: 6rpx;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 14rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;

  &:last-child {
    padding-bottom: 0;
    border-bottom: none;
  }
}

.info-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42rpx;
  height: 42rpx;
  flex-shrink: 0;
  background: $uni-bg-color-grey;
  border-radius: 50%;
}

.info-label {
  flex-shrink: 0;
  color: $uni-text-color-grey;
  font-size: 26rpx;
}

.info-value {
  flex: 1;
  min-width: 0;
  color: $uni-text-color-secondary;
  font-size: 27rpx;
  text-align: right;
}

.status-active {
  color: $uni-color-success;
}

.status-pending {
  color: $uni-color-warning;
}

.status-disable {
  color: $uni-text-color-grey;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding-bottom: 18rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.more-link {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  gap: 4rpx;
  color: $uni-text-color-grey;
  font-size: 26rpx;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
  padding-top: 20rpx;
}

.product-card {
  overflow: hidden;
  background: $uni-bg-color;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 14rpx;
}

.product-img {
  width: 100%;
  height: 230rpx;
  background: $uni-bg-color-grey;
}

.product-body {
  padding: 16rpx;
}

.product-name {
  min-height: 68rpx;
  color: $uni-text-color;
  font-size: 26rpx;
  font-weight: 600;
  line-height: 34rpx;
}

.product-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 12rpx;
}

.product-price {
  color: $uni-color-error;
  font-size: 30rpx;
  font-weight: 800;
}

.product-sales {
  flex-shrink: 0;
  color: $uni-text-color-grey;
  font-size: 22rpx;
}

.empty-section {
  padding: 56rpx 0 40rpx;
}

.bottom-bar {
  position: fixed;
  right: 0;
  bottom: var(--window-bottom, 0);
  left: 0;
  z-index: 99;
  display: flex;
  align-items: center;
  min-height: 112rpx;
  padding: 0 24rpx;
  background: $uni-bg-color;
  box-shadow: 0 -8rpx 24rpx rgba(31, 31, 31, 0.08);
}

.bar-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 76rpx;
  gap: 10rpx;
  color: $uni-text-color-inverse;
  font-size: 30rpx;
  font-weight: 700;
  border-radius: 38rpx;
  background: $uni-color-success;
}
</style>

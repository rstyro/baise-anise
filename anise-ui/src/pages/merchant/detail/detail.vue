<template>
  <view class="page">
    <view class="header-bg">
      <view class="header-content">
        <image :src="getImageUrl(detail.logoUrl)" class="logo" mode="aspectFill" />
        <view class="header-info">
          <view class="merchant-name">{{ detail.merchantName }}</view>
          <view class="header-tags">
            <u-tag v-if="detail.auditStatus === 1" text="已认证" type="success" size="mini" />
            <u-tag v-else-if="detail.auditStatus === 0" text="审核中" type="warning" size="mini" />
            <u-tag v-else text="已拒绝" type="error" size="mini" />
            <view class="origin-tag" v-if="detail.originPlace">
              <u-icon name="location-o" size="18" color="#999" />
              <text>{{ detail.originPlace }}</text>
            </view>
          </view>
        </view>
        <view class="header-actions">
          <view class="action-btn" @click="handleContact">
            <u-icon name="phone" size="32" color="#4caf50" />
          </view>
        </view>
      </view>
    </view>

    <view class="info-section">
      <view class="section-title">店铺简介</view>
      <view class="desc-content" v-if="detail.description">
        {{ detail.description }}
      </view>
      <view class="empty-text" v-else>暂无简介</view>
    </view>

    <view class="info-section">
      <view class="section-title">联系方式</view>
      <view class="contact-list">
        <view class="contact-item">
          <u-icon name="user" size="28" color="#4caf50" />
          <text class="contact-label">联系人</text>
          <text class="contact-value">{{ detail.contactName }}</text>
        </view>
        <view class="contact-item" @click="handleContact">
          <u-icon name="phone" size="28" color="#4caf50" />
          <text class="contact-label">联系电话</text>
          <text class="contact-value phone">{{ detail.contactPhone }}</text>
          <u-icon name="arrow-right" size="24" color="#ccc" />
        </view>
        <view class="contact-item">
          <u-icon name="location-o" size="28" color="#4caf50" />
          <text class="contact-label">产地地址</text>
          <text class="contact-value">{{ detail.originPlace || '-' }}</text>
        </view>
      </view>
    </view>

    <view class="info-section">
      <view class="section-title">资质信息</view>
      <view class="license-list">
        <view class="license-item" v-if="detail.licenseImage" @click="previewImage(detail.licenseImage)">
          <view class="license-icon-wrap">
            <u-icon name="file-text" size="36" color="#4caf50" />
          </view>
          <view class="license-info">
            <text class="license-name">营业执照</text>
            <text class="license-hint">点击查看</text>
          </view>
          <u-icon name="arrow-right" size="24" color="#ccc" />
        </view>
        <view class="license-item" v-if="detail.foodLicenseImage" @click="previewImage(detail.foodLicenseImage)">
          <view class="license-icon-wrap orange">
            <u-icon name="ticket" size="36" color="#ff9800" />
          </view>
          <view class="license-info">
            <text class="license-name">食品经营许可证</text>
            <text class="license-hint">点击查看</text>
          </view>
          <u-icon name="arrow-right" size="24" color="#ccc" />
        </view>
        <view class="license-empty" v-if="!detail.licenseImage && !detail.foodLicenseImage">
          <text>暂无资质信息</text>
        </view>
      </view>
    </view>

    <view class="info-section">
      <view class="section-title">商家信息</view>
      <view class="merchant-info-list">
        <view class="info-item">
          <text class="info-label">审核状态</text>
          <text class="info-value" :class="getStatusClass(detail.auditStatus)">
            {{ getAuditStatusText(detail.auditStatus) }}
          </text>
        </view>
        <view class="info-item">
          <text class="info-label">结算方式</text>
          <text class="info-value">{{ getSettlementText(detail.settlementType) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">平台抽成</text>
          <text class="info-value">{{ detail.commissionRate }}%</text>
        </view>
        <view class="info-item">
          <text class="info-label">店铺状态</text>
          <text class="info-value" :class="detail.status === 1 ? 'status-active' : 'status-disable'">
            {{ detail.status === 1 ? '正常营业' : '已禁用' }}
          </text>
        </view>
      </view>
    </view>

    <view class="products-section">
      <view class="section-header">
        <view class="section-title">店铺商品</view>
        <view class="more-link" @click="goProductList">
          <text>更多</text>
          <u-icon name="arrow-right" size="24" color="#999" />
        </view>
      </view>

      <view class="product-grid" v-if="products.length > 0">
        <view class="product-card" v-for="item in products" :key="item.id" @click="goProductDetail(item.id)">
          <image :src="getImageUrl(item.mainImage)" class="product-img" mode="widthFix" />
          <view class="product-body">
            <view class="product-name">{{ item.productName }}</view>
            <view class="product-price-row">
              <text class="product-price">¥{{ item.minPrice }}</text>
              <text class="product-sales">已售{{ item.sales }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="empty-section" v-else>
        <u-empty text="暂无商品" mode="list" />
      </view>
    </view>

    <view class="bottom-bar">
      <view class="bar-btn" @click="handleContact">
        <u-icon name="phone" size="32" color="#fff" />
        <text>联系商家</text>
      </view>
      <view class="bar-btn primary" @click="goProductList">
        <u-icon name="shopping-cart" size="32" color="#fff" />
        <text>进入店铺</text>
      </view>
    </view>

    <u-toast ref="toastRef" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { merchantApi } from '@/api/merchantApi'
import { getImageUrl } from '@/utils/image'

const detail = ref({
  id: 0,
  merchantName: '',
  logoUrl: '',
  description: '',
  contactName: '',
  contactPhone: '',
  originPlace: '',
  licenseImage: '',
  foodLicenseImage: '',
  auditStatus: 0,
  commissionRate: 3.00,
  settlementType: 1,
  status: 1,
})

const products = ref([])

onLoad((options) => {
  const merchantId = Number(options.id)
  if (merchantId) {
    loadDetail(merchantId)
    loadProducts(merchantId)
  }
})

const loadDetail = async (merchantId: number) => {
  try {
    const res = await merchantApi.detail(merchantId)
    detail.value = res
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
    console.error('加载商家详情失败', e)
  }
}

const loadProducts = async (merchantId: number) => {
  try {
    const res = await merchantApi.productList(merchantId, 1, 6)
    products.value = res.records || []
  } catch (e) {
    console.error('加载商家商品失败', e)
  }
}

const getAuditStatusText = (status: number) => {
  const map = { 0: '待审核', 1: '审核通过', 2: '审核拒绝' }
  return map[status] || '未知'
}

const getStatusClass = (status: number) => {
  if (status === 0) return 'status-pending'
  if (status === 1) return 'status-active'
  return 'status-error'
}

const getSettlementText = (type: number) => {
  const map = { 1: 'T+1结算', 7: 'T+7结算', 30: 'T+30结算' }
  return map[type] || '未知'
}

const handleContact = () => {
  if (!detail.value.contactPhone) {
    uni.showToast({ title: '暂无联系电话', icon: 'none' })
    return
  }
  uni.makePhoneCall({
    phoneNumber: detail.value.contactPhone,
    fail: () => {
      uni.showToast({ title: '拨打电话失败', icon: 'none' })
    }
  })
}

const previewImage = (url: string) => {
  uni.previewImage({
    urls: [getImageUrl(url)],
    current: getImageUrl(url)
  })
}

const goProductDetail = (productId: number) => {
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}

const goProductList = () => {
  uni.$grouter.navigateTo('merchantProductList', { query: { id: detail.value.id } })
}
</script>

<style lang="scss" scoped>
.page { background: #f5f9f5; min-height: 100vh; padding-bottom: 140rpx; }

.header-bg {
  background: linear-gradient(135deg, #15803D 0%, #22C55E 100%);
  padding: 60rpx 30rpx 40rpx;
  position: relative;
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 30rpx;
    background: #f5f9f5;
    border-radius: 30rpx 30rpx 0 0;
  }
}

.header-content {
  display: flex;
  align-items: center;
  gap: 24rpx;
  position: relative;
  z-index: 1;
}

.logo {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: #fff;
  padding: 6rpx;
  box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.15);
}

.header-info {
  flex: 1;
}

.merchant-name {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12rpx;
}

.header-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
}

.origin-tag {
  display: flex;
  align-items: center;
  gap: 4rpx;
  font-size: 24rpx;
  color: rgba(255,255,255,0.85);
  padding: 4rpx 12rpx;
  background: rgba(255,255,255,0.15);
  border-radius: 20rpx;
}

.header-actions {
  .action-btn {
    width: 80rpx;
    height: 80rpx;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.info-section {
  background: #fff;
  margin: 20rpx;
  padding: 24rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #2e3b2e;
  margin-bottom: 20rpx;
  padding-left: 12rpx;
  border-left: 6rpx solid #4caf50;
}

.desc-content {
  font-size: 28rpx;
  color: #555;
  line-height: 1.8;
  text-indent: 2em;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
}

.contact-list {
  .contact-item {
    display: flex;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    &:last-child { border-bottom: none; }
  }
}

.contact-label {
  font-size: 28rpx;
  color: #666;
  width: 160rpx;
  margin-left: 12rpx;
}

.contact-value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  &.phone { color: #4caf50; }
}

.license-list {
  .license-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    &:last-child { border-bottom: none; }
  }
}

.license-icon-wrap {
  width: 72rpx;
  height: 72rpx;
  background: rgba(76,175,80,0.1);
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  &.orange {
    background: rgba(255,152,0,0.1);
  }
}

.license-info {
  flex: 1;
}

.license-name {
  font-size: 28rpx;
  color: #333;
  display: block;
}

.license-hint {
  font-size: 24rpx;
  color: #999;
}

.license-empty {
  padding: 40rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: #999;
}

.merchant-info-list {
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    &:last-child { border-bottom: none; }
  }
}

.info-label {
  font-size: 28rpx;
  color: #666;
}

.info-value {
  font-size: 28rpx;
  color: #333;
  &.status-pending { color: #ff9800; }
  &.status-active { color: #4caf50; }
  &.status-error { color: #ff4d4f; }
  &.status-disable { color: #999; }
}

.products-section {
  background: #fff;
  margin: 20rpx;
  padding: 24rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.more-link {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  color: #999;
}

.product-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.product-card {
  width: calc(50% - 8rpx);
  background: #fafafa;
  border-radius: 12rpx;
  overflow: hidden;
}

.product-img {
  width: 100%;
  background: #e8f5e9;
}

.product-body {
  padding: 16rpx;
}

.product-name {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8rpx;
}

.product-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 30rpx;
  font-weight: 700;
  color: #ff4d4f;
}

.product-sales {
  font-size: 22rpx;
  color: #999;
}

.empty-section {
  padding: 60rpx 0;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 16rpx 20rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08);
  gap: 20rpx;
}

.bar-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  background: #f0fdf4;
  color: #4caf50;
  border-radius: 44rpx;
  font-size: 30rpx;
  font-weight: 600;
  &.primary {
    background: linear-gradient(135deg, #15803D 0%, #22C55E 100%);
    color: #fff;
  }
}
</style>
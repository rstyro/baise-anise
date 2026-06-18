<template>
  <view class="page">
    <view class="hero">
      <view class="hero-icon">
        <u-icon name="car-fill" size="36" :color="THEME_TEXT_INVERSE" />
      </view>
      <view class="hero-copy">
        <text class="hero-title">{{ heroTitle }}</text>
        <text class="hero-desc">{{ logistics.orderNo || '-' }}</text>
      </view>
    </view>

    <view class="content">
      <view class="section-card">
        <view class="section-title">
          <u-icon name="map" size="20" :color="THEME_SUCCESS" />
          <text>物流概览</text>
        </view>
        <view class="summary-row">
          <text class="summary-label">配送方式</text>
          <text class="summary-value">{{ deliveryTypeLabel(logistics.deliveryType) }}</text>
        </view>
        <view class="summary-row">
          <text class="summary-label">快递公司</text>
          <text class="summary-value">{{ primaryPackage?.logisticsCompany || '待商家填写' }}</text>
        </view>
        <view class="summary-row">
          <text class="summary-label">快递单号</text>
          <view class="express-value">
            <text class="summary-value u-line-1">{{ primaryPackage?.trackingNo || '-' }}</text>
            <text v-if="primaryPackage?.trackingNo" class="copy-btn" @click="copyExpressNo">复制</text>
          </view>
        </view>
        <view v-if="primaryPackage?.lastTrackDetail" class="summary-row">
          <text class="summary-label">最新轨迹</text>
          <text class="summary-value">{{ primaryPackage.lastTrackDetail }}</text>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">
          <u-icon name="clock" size="20" :color="THEME_SUCCESS" />
          <text>物流进度</text>
        </view>
        <view class="timeline">
          <view
            v-for="item in timeline"
            :key="item.title"
            class="timeline-item"
            :class="{ active: item.active }"
          >
            <view class="dot" />
            <view class="timeline-body">
              <text class="timeline-title">{{ item.title }}</text>
              <text class="timeline-time">{{ item.time || item.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="logistics.subList.length" class="section-card">
        <view class="section-title">
          <u-icon name="shop" size="20" :color="THEME_SUCCESS" />
          <text>商家包裹</text>
        </view>
        <view v-for="sub in logistics.subList" :key="sub.subNo" class="package-card">
          <view class="package-head">
            <text class="merchant-name u-line-1">{{ sub.merchantName || '默认店铺' }}</text>
            <text class="status-pill" :class="packageStatusClass(sub.deliveryStatus)">
              {{ packageStatusLabel(sub.deliveryStatus) }}
            </text>
          </view>
          <view class="package-row">
            <text>子订单号</text>
            <text class="u-line-1">{{ sub.subNo || '-' }}</text>
          </view>
          <view v-if="sub.deliveryTime" class="package-row">
            <text>发货时间</text>
            <text>{{ sub.deliveryTime }}</text>
          </view>
          <view v-if="sub.packages?.length" class="logistics-list">
            <view
              v-for="pkg in sub.packages"
              :key="pkg.trackingNo"
              class="logistics-card"
            >
              <view class="logistics-head">
                <view class="company-info">
                  <text class="company-name u-line-1">{{ pkg.logisticsCompany || '快递配送' }}</text>
                  <text class="track-no u-line-1">{{ pkg.trackingNo || '-' }}</text>
                </view>
                <text class="logistics-status" :class="logisticsStatusClass(pkg.status)">
                  {{ logisticsStatusLabel(pkg.status) }}
                </text>
              </view>
              <view v-if="pkg.lastTrackDetail" class="track-detail">
                {{ pkg.lastTrackDetail }}
              </view>
              <view v-if="pkg.estimatedDeliveryDate || pkg.deliveredTime" class="package-row compact">
                <text>{{ pkg.deliveredTime ? '签收时间' : '预计送达' }}</text>
                <text>{{ pkg.deliveredTime || pkg.estimatedDeliveryDate }}</text>
              </view>
            </view>
          </view>
          <view v-else class="package-empty">暂无物流单，等待商家填写</view>
        </view>
      </view>

      <view v-if="!loading && !logistics.orderId" class="empty-state">
        <u-empty text="暂无物流信息" mode="car" marginTop="120" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderApi } from '@/api/businessApi'
import { THEME_SUCCESS, THEME_TEXT_INVERSE } from '@/styles/theme'

interface SubLogistics {
  subNo?: string
  merchantName?: string
  deliveryStatus?: number
  deliveryTime?: string
  receiveTime?: string
  packages: LogisticsPackage[]
}

interface LogisticsPackage {
  logisticsCompany?: string
  expressCode?: string
  trackingNo?: string
  status?: number
  estimatedDeliveryDate?: string
  deliveredTime?: string
  lastTrackDetail?: string
  createTime?: string
}

interface OrderLogistics {
  orderId?: number
  orderNo?: string
  status?: number
  deliveryType?: number
  shipTime?: string
  receiveTime?: string
  subList: SubLogistics[]
}

const logistics = ref<OrderLogistics>({ subList: [] })
const loading = ref(false)

const primaryPackage = computed(() => {
  for (const sub of logistics.value.subList) {
    const pkg = (sub.packages || []).find(item => item.trackingNo || item.logisticsCompany)
    if (pkg) return pkg
  }
  return null
})

const hasShipped = computed(() => {
  return Number(logistics.value.status) >= 3 || logistics.value.subList.some(item => Number(item.deliveryStatus) >= 1)
})

const hasReceived = computed(() => {
  return Number(logistics.value.status) >= 4 || logistics.value.subList.every(item => logistics.value.subList.length > 0 && Number(item.deliveryStatus) >= 2)
})

const heroTitle = computed(() => {
  if (hasReceived.value) return '已确认收货'
  if (hasShipped.value) return '包裹运输中'
  return '等待商家发货'
})

const timeline = computed(() => [
  { title: '订单已支付', desc: '商家正在准备商品', time: '', active: Number(logistics.value.status) >= 2 },
  { title: '商家已发货', desc: '暂无发货时间', time: logistics.value.shipTime || logistics.value.subList.find(item => item.deliveryTime)?.deliveryTime || '', active: hasShipped.value },
  { title: '确认收货', desc: '等待收货确认', time: logistics.value.receiveTime || logistics.value.subList.find(item => item.receiveTime)?.receiveTime || '', active: hasReceived.value },
])

const deliveryTypeLabel = (type?: number) => {
  const map: Record<number, string> = { 1: '快递配送', 2: '到店自提', 3: '送货上门' }
  return map[Number(type || 1)] || '快递配送'
}

const packageStatusLabel = (status?: number) => {
  const map: Record<number, string> = { 0: '待发货', 1: '已发货', 2: '已收货' }
  return map[Number(status || 0)] || '待发货'
}

const packageStatusClass = (status?: number) => {
  if (Number(status) === 2) return 'received'
  if (Number(status) === 1) return 'shipped'
  return 'pending'
}

const logisticsStatusLabel = (status?: number) => {
  const map: Record<number, string> = {
    0: '已揽收',
    1: '运输中',
    2: '派件中',
    3: '已签收',
    4: '异常',
  }
  return map[Number(status || 0)] || '已揽收'
}

const logisticsStatusClass = (status?: number) => {
  if (Number(status) === 3) return 'delivered'
  if (Number(status) === 4) return 'exception'
  if (Number(status) === 1 || Number(status) === 2) return 'moving'
  return 'collected'
}

const copyExpressNo = () => {
  const data = primaryPackage.value?.trackingNo
  if (!data) return
  uni.setClipboardData({
    data,
    success: () => uni.showToast({ title: '已复制', icon: 'success' }),
  })
}

onLoad(async (options?: { orderId?: string | number; id?: string | number }) => {
  const orderId = Number(options?.orderId || options?.id)
  if (!orderId) {
    uni.showToast({ title: '订单不存在', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 800)
    return
  }

  loading.value = true
  try {
    const res = await orderApi.logistics(orderId) as OrderLogistics
    logistics.value = {
      ...res,
      subList: (res.subList || []).map(item => ({ ...item, packages: item.packages || [] })),
    }
  } catch {
    uni.showToast({ title: '物流加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: $uni-bg-color-page;
}

.hero {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 58rpx 28rpx 34rpx;
  color: $uni-text-color-inverse;
  background: $uni-color-success;
}

.hero-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80rpx;
  height: 80rpx;
  flex-shrink: 0;
  border: 1rpx solid rgba(255, 255, 255, 0.36);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
}

.hero-copy {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 8rpx;
}

.hero-title {
  font-size: 38rpx;
  font-weight: 800;
  line-height: 48rpx;
}

.hero-desc {
  font-size: 25rpx;
  line-height: 34rpx;
  opacity: 0.9;
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
  box-shadow: 0 8rpx 24rpx rgba($uni-text-color, 0.04);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding-bottom: 18rpx;
  color: $uni-text-color;
  font-size: 29rpx;
  font-weight: 800;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  padding: 16rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;
  font-size: 26rpx;

  &:last-child {
    border-bottom: none;
  }
}

.summary-label {
  flex-shrink: 0;
  color: $uni-text-color-grey;
}

.summary-value {
  min-width: 0;
  color: $uni-text-color-secondary;
  text-align: right;
}

.express-value {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-width: 0;
  gap: 12rpx;
}

.copy-btn {
  flex-shrink: 0;
  padding: 3rpx 12rpx;
  color: $uni-color-success;
  font-size: 22rpx;
  line-height: 30rpx;
  border: 1rpx solid $uni-color-success;
  border-radius: 8rpx;
}

.timeline {
  padding-top: 20rpx;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 16rpx;
  padding-bottom: 30rpx;

  &::before {
    position: absolute;
    top: 18rpx;
    bottom: -2rpx;
    left: 9rpx;
    width: 2rpx;
    background: $uni-border-color-light;
    content: '';
  }

  &:last-child {
    padding-bottom: 0;

    &::before {
      display: none;
    }
  }

  &.active .dot {
    background: $uni-color-success;
  }
}

.dot {
  position: relative;
  z-index: 1;
  width: 20rpx;
  height: 20rpx;
  margin-top: 8rpx;
  flex-shrink: 0;
  border-radius: 50%;
  background: $uni-text-color-disable;
}

.timeline-body {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.timeline-title {
  color: $uni-text-color;
  font-size: 27rpx;
  font-weight: 700;
}

.timeline-time {
  color: $uni-text-color-grey;
  font-size: 23rpx;
}

.package-card {
  padding: 20rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;

  &:last-child {
    padding-bottom: 0;
    border-bottom: none;
  }
}

.package-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 14rpx;
}

.merchant-name {
  flex: 1;
  min-width: 0;
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 800;
}

.status-pill {
  flex-shrink: 0;
  padding: 5rpx 14rpx;
  font-size: 22rpx;
  line-height: 30rpx;
  border-radius: 999rpx;

  &.pending {
    color: $uni-color-warning;
    background: $uni-color-warning-light;
  }

  &.shipped {
    color: $uni-color-primary;
    background: $uni-color-primary-light;
  }

  &.received {
    color: $uni-color-success;
    background: $uni-color-success-light;
  }
}

.package-row {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  padding: 8rpx 0;
  color: $uni-text-color-grey;
  font-size: 24rpx;

  text:last-child {
    min-width: 0;
    color: $uni-text-color-secondary;
    text-align: right;
  }
}

.package-row.compact {
  padding-bottom: 0;
  font-size: 23rpx;
}

.logistics-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 14rpx;
}

.logistics-card {
  padding: 18rpx;
  background: $uni-bg-color-grey;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 14rpx;
}

.logistics-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14rpx;
}

.company-info {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 6rpx;
}

.company-name {
  color: $uni-text-color;
  font-size: 26rpx;
  font-weight: 700;
}

.track-no {
  color: $uni-text-color-grey;
  font-size: 23rpx;
}

.logistics-status {
  flex-shrink: 0;
  padding: 4rpx 12rpx;
  font-size: 21rpx;
  line-height: 30rpx;
  border-radius: 999rpx;

  &.collected {
    color: $uni-color-primary;
    background: $uni-color-primary-light;
  }

  &.moving {
    color: $uni-color-warning;
    background: $uni-color-warning-light;
  }

  &.delivered {
    color: $uni-color-success;
    background: $uni-color-success-light;
  }

  &.exception {
    color: $uni-color-error;
    background: $uni-color-error-light;
  }
}

.track-detail {
  margin-top: 14rpx;
  color: $uni-text-color-secondary;
  font-size: 24rpx;
  line-height: 34rpx;
}

.package-empty {
  margin-top: 14rpx;
  padding: 18rpx;
  color: $uni-text-color-grey;
  font-size: 24rpx;
  text-align: center;
  background: $uni-bg-color-grey;
  border-radius: 12rpx;
}

.empty-state {
  padding: 80rpx 0;
}
</style>

<template>
  <view class="page">
    <view class="status-panel" :class="statusMeta.className">
      <view class="status-icon">
        <u-icon :name="statusMeta.icon" size="38" :color="THEME_TEXT_INVERSE" />
      </view>
      <view class="status-copy">
        <text class="status-title">{{ statusMeta.label }}</text>
        <text class="status-desc">{{ statusMeta.desc }}</text>
      </view>
    </view>

    <view class="content">
      <view v-if="detail.id" class="section-card">
        <view class="section-title">
          <u-icon name="file-text" size="20" :color="THEME_SUCCESS" />
          <text>售后信息</text>
        </view>
        <view class="info-row">
          <text class="info-label">售后编号</text>
          <text class="info-value u-line-1">{{ detail.afterSaleNo || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">订单编号</text>
          <text class="info-value u-line-1">{{ detail.orderNo || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">售后类型</text>
          <text class="info-value">{{ typeLabel(detail.type) }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">申请原因</text>
          <text class="info-value">{{ detail.reason || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">申请金额</text>
          <text class="amount-value">¥{{ formatAmount(detail.applyAmount || detail.refundAmount) }}</text>
        </view>
        <view v-if="detail.userRemark" class="info-row block">
          <text class="info-label">问题描述</text>
          <text class="info-value remark">{{ detail.userRemark }}</text>
        </view>
      </view>

      <view v-if="goods.productName" class="section-card">
        <view class="section-title">
          <u-icon name="shopping-cart" size="20" :color="THEME_SUCCESS" />
          <text>关联商品</text>
        </view>
        <view class="goods-item">
          <image :src="getImageUrl(goods.productImage || '')" class="goods-img" mode="aspectFill" />
          <view class="goods-info">
            <text class="goods-name u-line-2">{{ goods.productName || goods.goodsName }}</text>
            <text class="goods-tip">订单售后商品</text>
          </view>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">
          <u-icon name="clock" size="20" :color="THEME_SUCCESS" />
          <text>处理进度</text>
        </view>
        <view class="timeline">
          <view class="timeline-item active">
            <view class="dot" />
            <view class="timeline-body">
              <text class="timeline-title">已提交申请</text>
              <text class="timeline-time">{{ detail.createTime || '-' }}</text>
            </view>
          </view>
          <view class="timeline-item" :class="{ active: Number(detail.status) > 0 }">
            <view class="dot" />
            <view class="timeline-body">
              <text class="timeline-title">商家处理中</text>
              <text class="timeline-time">{{ detail.handleTime || '等待商家处理' }}</text>
            </view>
          </view>
          <view v-if="detail.handleRemark || detail.handleResult" class="handle-box">
            <text class="handle-title">处理说明</text>
            <text class="handle-text">{{ detail.handleRemark || detail.handleResult }}</text>
          </view>
        </view>
      </view>

      <view v-if="!detail.id && !loading" class="empty-state">
        <u-empty text="暂无售后记录" mode="order" marginTop="120" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { aftersaleApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import { THEME_SUCCESS, THEME_TEXT_INVERSE } from '@/styles/theme'

interface AfterSaleDetail {
  id?: number
  afterSaleNo?: string
  orderId?: number
  orderNo?: string
  type?: number
  reason?: string
  applyAmount?: number | string
  refundAmount?: number | string
  userRemark?: string
  status?: number
  handleResult?: string
  handleRemark?: string
  handleTime?: string
  createTime?: string
  productName?: string
  goodsName?: string
  productImage?: string
}

const detail = ref<AfterSaleDetail>({})
const goods = ref<AfterSaleDetail>({})
const loading = ref(false)

const statusMap: Record<number, { label: string; desc: string; icon: string; className: string }> = {
  0: { label: '待处理', desc: '商家会尽快审核您的申请', icon: 'clock', className: 'status-pending' },
  1: { label: '审核中', desc: '商家正在核实售后问题', icon: 'info-circle', className: 'status-review' },
  2: { label: '已同意', desc: '请按商家要求继续处理', icon: 'checkmark-circle', className: 'status-success' },
  3: { label: '已拒绝', desc: '可查看处理说明后联系商家', icon: 'close-circle', className: 'status-error' },
  4: { label: '已退款', desc: '退款处理已完成', icon: 'checkmark-circle', className: 'status-success' },
  5: { label: '已退货', desc: '退货流程已完成', icon: 'checkmark-circle', className: 'status-success' },
  6: { label: '已换货', desc: '换货流程已完成', icon: 'checkmark-circle', className: 'status-success' },
  7: { label: '已取消', desc: '售后申请已取消', icon: 'close-circle', className: 'status-muted' },
  8: { label: '平台介入', desc: '平台客服正在协助处理', icon: 'server-man', className: 'status-review' },
}

const statusMeta = computed(() => statusMap[Number(detail.value.status || 0)] || statusMap[0])

const typeLabel = (type?: number) => {
  const map: Record<number, string> = { 1: '仅退款', 2: '退货退款', 3: '换货', 4: '坏果赔付' }
  return map[Number(type)] || '-'
}

const formatAmount = (value?: number | string) => {
  const amount = Number(value || 0)
  return Number.isNaN(amount) ? '0.00' : amount.toFixed(2)
}

const loadById = async (id: number) => {
  detail.value = await aftersaleApi.detail(id) as AfterSaleDetail
}

const loadByOrderId = async (orderId: number) => {
  const list = await aftersaleApi.list() as AfterSaleDetail[]
  const item = (list || []).find(record => Number(record.orderId) === orderId)
  if (item?.id) {
    goods.value = item
    await loadById(item.id)
  }
}

onLoad(async (options?: { id?: string | number; orderId?: string | number }) => {
  const id = Number(options?.id)
  const orderId = Number(options?.orderId)
  loading.value = true
  try {
    if (id) {
      await loadById(id)
    } else if (orderId) {
      await loadByOrderId(orderId)
    }
  } catch {
    uni.showToast({ title: '售后加载失败', icon: 'none' })
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

.status-panel {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 58rpx 28rpx 34rpx;
  color: $uni-text-color-inverse;
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 76rpx;
  height: 76rpx;
  flex-shrink: 0;
  border: 1rpx solid rgba(255, 255, 255, 0.36);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
}

.status-copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.status-title {
  font-size: 38rpx;
  font-weight: 800;
  line-height: 46rpx;
}

.status-desc {
  font-size: 25rpx;
  line-height: 34rpx;
  opacity: 0.9;
}

.status-pending,
.status-review {
  background: $uni-color-warning;
}

.status-success {
  background: $uni-color-success;
}

.status-error {
  background: $uni-color-error;
}

.status-muted {
  background: $uni-text-color-grey;
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

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  padding: 16rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;
  font-size: 26rpx;

  &:last-child {
    border-bottom: none;
  }

  &.block {
    flex-direction: column;
  }
}

.info-label {
  flex-shrink: 0;
  color: $uni-text-color-grey;
}

.info-value {
  min-width: 0;
  color: $uni-text-color-secondary;
  text-align: right;
}

.amount-value {
  color: $uni-color-error;
  font-size: 30rpx;
  font-weight: 800;
}

.remark {
  line-height: 38rpx;
  text-align: left;
}

.goods-item {
  display: flex;
  gap: 16rpx;
  padding-top: 20rpx;
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
  font-weight: 700;
  line-height: 38rpx;
}

.goods-tip {
  display: inline-block;
  margin-top: 10rpx;
  padding: 4rpx 10rpx;
  color: $uni-color-success;
  font-size: 22rpx;
  background: $uni-color-success-light;
  border-radius: 8rpx;
}

.timeline {
  padding-top: 18rpx;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 16rpx;
  padding-bottom: 28rpx;

  &::before {
    position: absolute;
    top: 18rpx;
    bottom: -2rpx;
    left: 9rpx;
    width: 2rpx;
    background: $uni-border-color-light;
    content: '';
  }

  &:last-child::before {
    display: none;
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

.handle-box {
  padding: 18rpx;
  background: $uni-bg-color-grey;
  border-radius: 14rpx;
}

.handle-title {
  display: block;
  margin-bottom: 8rpx;
  color: $uni-text-color;
  font-size: 26rpx;
  font-weight: 700;
}

.handle-text {
  color: $uni-text-color-secondary;
  font-size: 25rpx;
  line-height: 36rpx;
}

.empty-state {
  padding: 80rpx 0;
}
</style>

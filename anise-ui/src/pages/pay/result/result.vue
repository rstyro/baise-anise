<template>
  <view class="page">
    <view class="result-panel" :class="{ success: isSuccess, fail: !isSuccess }">
      <view class="result-icon">
        <u-icon :name="isSuccess ? 'checkmark-circle' : 'close-circle'" size="58" :color="THEME_TEXT_INVERSE" />
      </view>
      <text class="result-title">{{ isSuccess ? '支付成功' : '支付未完成' }}</text>
      <text class="result-desc">{{ isSuccess ? '商家会尽快为您发货' : '订单仍可在待支付中继续付款' }}</text>
    </view>

    <view class="content">
      <view class="section-card">
        <view class="info-row">
          <text class="info-label">订单编号</text>
          <text class="info-value u-line-1">{{ orderNo || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">支付金额</text>
          <text class="amount-value">¥{{ amount }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">支付方式</text>
          <text class="info-value">微信支付</text>
        </view>
      </view>

      <view class="action-grid">
        <button class="action-btn primary" @click="goOrderDetail">查看订单</button>
        <button class="action-btn plain" @click="goHome">返回首页</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { THEME_TEXT_INVERSE } from '@/styles/theme'

const orderId = ref(0)
const orderNo = ref('')
const amount = ref('0.00')
const isSuccess = ref(true)

onLoad((options?: { orderId?: string | number; orderNo?: string; amount?: string | number; status?: string }) => {
  orderId.value = Number(options?.orderId || 0)
  orderNo.value = options?.orderNo ? decodeURIComponent(String(options.orderNo)) : ''
  amount.value = Number(options?.amount || 0).toFixed(2)
  isSuccess.value = options?.status !== 'fail'
})

const goOrderDetail = () => {
  if (orderId.value) {
    uni.$grouter.redirectTo('orderDetail', { query: { id: orderId.value } })
  } else {
    uni.$grouter.redirectTo('orderList')
  }
}

const goHome = () => {
  uni.$grouter.switchTab('index')
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: $uni-bg-color-page;
}

.result-panel {
  display: flex;
  align-items: center;
  flex-direction: column;
  padding: 86rpx 28rpx 56rpx;
  color: $uni-text-color-inverse;

  &.success {
    background: $uni-color-success;
  }

  &.fail {
    background: $uni-color-warning;
  }
}

.result-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 118rpx;
  height: 118rpx;
  margin-bottom: 22rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.36);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
}

.result-title {
  font-size: 42rpx;
  font-weight: 800;
  line-height: 52rpx;
}

.result-desc {
  margin-top: 10rpx;
  font-size: 26rpx;
  line-height: 36rpx;
  opacity: 0.9;
}

.content {
  padding: 20rpx;
}

.section-card {
  padding: 24rpx;
  background: $uni-bg-color;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 18rpx;
  box-shadow: 0 8rpx 24rpx rgba($uni-text-color, 0.04);
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
  font-size: 32rpx;
  font-weight: 800;
}

.action-grid {
  display: flex;
  gap: 18rpx;
  margin-top: 28rpx;
}

.action-btn {
  height: 76rpx;
  flex: 1;
  margin: 0;
  font-size: 28rpx;
  font-weight: 800;
  line-height: 74rpx;
  border-radius: 38rpx;

  &.primary {
    color: $uni-text-color-inverse;
    border: 1rpx solid $uni-color-success;
    background: $uni-color-success;
  }

  &.plain {
    color: $uni-text-color-secondary;
    border: 1rpx solid $uni-border-color;
    background: $uni-bg-color;
  }
}
</style>

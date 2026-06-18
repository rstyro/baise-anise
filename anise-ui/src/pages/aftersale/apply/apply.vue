<template>
  <view class="page">
    <view class="content">
      <view class="hero">
        <view class="hero-icon">
          <u-icon name="server-man" size="34" :color="THEME_TEXT_INVERSE" />
        </view>
        <view class="hero-copy">
          <text class="hero-title">申请售后</text>
          <text class="hero-desc">请填写真实问题，商家会尽快处理</text>
        </view>
      </view>

      <view v-if="order.id" class="section-card">
        <view class="section-title">
          <u-icon name="order" size="20" :color="THEME_SUCCESS" />
          <text>订单商品</text>
        </view>
        <view class="order-line">
          <text class="order-no u-line-1">{{ order.orderNo }}</text>
          <text class="order-amount">¥{{ formatAmount(order.payAmount || order.totalAmount) }}</text>
        </view>
        <view v-for="item in order.items" :key="item.skuId || item.productId" class="goods-item">
          <image :src="getImageUrl(item.productImage)" class="goods-img" mode="aspectFill" />
          <view class="goods-info">
            <text class="goods-name u-line-2">{{ item.productName }}</text>
            <text v-if="formatSkuSpecs(item.skuSpecs)" class="goods-spec u-line-1">{{ formatSkuSpecs(item.skuSpecs) }}</text>
          </view>
          <view class="goods-side">
            <text class="goods-price">¥{{ formatAmount(item.price) }}</text>
            <text class="goods-num">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">
          <u-icon name="edit-pen" size="20" :color="THEME_SUCCESS" />
          <text>售后信息</text>
        </view>

        <view class="field-block">
          <text class="field-label">售后类型</text>
          <view class="option-grid">
            <view
              v-for="item in typeOptions"
              :key="item.value"
              class="option-item"
              :class="{ active: form.type === item.value }"
              @click="form.type = item.value"
            >
              <text>{{ item.label }}</text>
            </view>
          </view>
        </view>

        <view class="field-block">
          <text class="field-label">申请原因</text>
          <view class="option-list">
            <view
              v-for="item in reasonOptions"
              :key="item"
              class="reason-item"
              :class="{ active: form.reason === item }"
              @click="form.reason = item"
            >
              <text>{{ item }}</text>
              <u-icon v-if="form.reason === item" name="checkmark-circle-fill" size="18" :color="THEME_SUCCESS" />
            </view>
          </view>
        </view>

        <view class="field-row">
          <text class="field-label">退款金额</text>
          <view class="amount-input">
            <text class="amount-symbol">¥</text>
            <input v-model="form.applyAmount" type="digit" class="input" placeholder="0.00" placeholder-class="input-placeholder" />
          </view>
        </view>

        <view class="field-block">
          <text class="field-label">问题描述</text>
          <textarea
            v-model="form.userRemark"
            class="textarea"
            maxlength="200"
            placeholder="请补充商品问题、诉求或协商情况"
            placeholder-class="input-placeholder"
          />
          <text class="counter">{{ form.userRemark.length }}/200</text>
        </view>
      </view>
    </view>

    <view class="bottom-bar">
      <button class="submit-btn" :disabled="submitting" @click="submitAfterSale">
        {{ submitting ? '提交中...' : '提交申请' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { aftersaleApi, orderApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import { THEME_SUCCESS, THEME_TEXT_INVERSE } from '@/styles/theme'

interface OrderGoodsItem {
  skuId?: number
  productId: number
  productImage: string
  productName: string
  skuSpecs?: string
  price: number | string
  quantity: number
}

interface OrderDetail {
  id?: number
  orderNo?: string
  totalAmount?: number | string
  payAmount?: number | string
  merchantId?: number
  subList?: Array<{ subId?: number; merchantId?: number }>
  items: OrderGoodsItem[]
}

const order = ref<OrderDetail>({ items: [] })
const submitting = ref(false)

const typeOptions = [
  { label: '仅退款', value: 1 },
  { label: '退货退款', value: 2 },
  { label: '换货', value: 3 },
  { label: '坏果赔付', value: 4 },
]

const reasonOptions = ['商品破损/坏果', '商品与描述不符', '少发/漏发', '不想要了', '其他原因']

const form = reactive({
  type: 2,
  reason: '商品破损/坏果',
  applyAmount: '',
  userRemark: '',
})

const showToast = (title: string, icon: 'none' | 'success' = 'none') => {
  uni.showToast({ title, icon })
}

const formatAmount = (value?: number | string) => {
  const amount = Number(value || 0)
  return Number.isNaN(amount) ? '0.00' : amount.toFixed(2)
}

const formatSkuSpecs = (skuSpecs?: string): string => {
  if (!skuSpecs || skuSpecs === '{}') return ''
  try {
    const obj = JSON.parse(skuSpecs) as Record<string, unknown>
    return Object.values(obj).filter(Boolean).join(', ')
  } catch {
    return skuSpecs
  }
}

const loadOrder = async (orderId: number) => {
  const res = await orderApi.detail(orderId) as OrderDetail
  order.value = { ...res, items: res.items || [] }
  form.applyAmount = formatAmount(res.payAmount || res.totalAmount)
}

onLoad(async (options?: { orderId?: string | number; id?: string | number }) => {
  const orderId = Number(options?.orderId || options?.id)
  if (!orderId) {
    showToast('订单不存在')
    setTimeout(() => uni.navigateBack(), 800)
    return
  }
  try {
    await loadOrder(orderId)
  } catch {
    showToast('订单加载失败')
  }
})

const submitAfterSale = async () => {
  if (submitting.value || !order.value.id) return
  if (!form.reason) {
    showToast('请选择申请原因')
    return
  }
  const applyAmount = Number(form.applyAmount)
  if (!applyAmount || applyAmount <= 0) {
    showToast('请输入退款金额')
    return
  }

  submitting.value = true
  try {
    const firstSub = order.value.subList?.[0]
    const result = await aftersaleApi.apply({
      orderId: order.value.id,
      orderNo: order.value.orderNo,
      subId: firstSub?.subId,
      merchantId: firstSub?.merchantId || order.value.merchantId,
      type: form.type,
      reason: form.reason,
      applyAmount,
      userRemark: form.userRemark,
    }) as { id?: number }
    showToast('申请已提交', 'success')
    setTimeout(() => {
      uni.$grouter.redirectTo('afterSaleDetail', { query: { id: result.id || 0, orderId: order.value.id || 0 } })
    }, 800)
  } catch {
    showToast('提交失败，请重试')
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

.hero {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin-bottom: 20rpx;
  padding: 28rpx;
  color: $uni-text-color-inverse;
  background: $uni-color-success;
  border-radius: 18rpx;
}

.hero-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 76rpx;
  height: 76rpx;
  flex-shrink: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.16);
}

.hero-copy {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.hero-title {
  font-size: 36rpx;
  font-weight: 800;
  line-height: 44rpx;
}

.hero-desc {
  font-size: 25rpx;
  line-height: 34rpx;
  opacity: 0.9;
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

.order-line,
.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 20rpx 0;
}

.order-no {
  flex: 1;
  min-width: 0;
  color: $uni-text-color-secondary;
  font-size: 25rpx;
}

.order-amount {
  color: $uni-color-error;
  font-size: 30rpx;
  font-weight: 800;
}

.goods-item {
  display: flex;
  gap: 16rpx;
  padding: 18rpx 0;
  border-top: 1rpx solid $uni-border-color-light;
}

.goods-img {
  width: 112rpx;
  height: 112rpx;
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
  font-size: 27rpx;
  font-weight: 700;
  line-height: 36rpx;
}

.goods-spec {
  display: inline-block;
  max-width: 100%;
  margin-top: 8rpx;
  padding: 4rpx 10rpx;
  color: $uni-text-color-grey;
  font-size: 22rpx;
  background: $uni-bg-color-grey;
  border-radius: 8rpx;
}

.goods-side {
  display: flex;
  align-items: flex-end;
  flex-direction: column;
  gap: 8rpx;
  flex-shrink: 0;
}

.goods-price {
  color: $uni-text-color;
  font-size: 27rpx;
  font-weight: 800;
}

.goods-num {
  color: $uni-text-color-grey;
  font-size: 23rpx;
}

.field-block {
  padding: 20rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;

  &:last-child {
    border-bottom: none;
  }
}

.field-label {
  display: block;
  flex-shrink: 0;
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 700;
}

.option-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14rpx;
  margin-top: 18rpx;
}

.option-item {
  height: 70rpx;
  color: $uni-text-color-secondary;
  font-size: 26rpx;
  line-height: 68rpx;
  text-align: center;
  border: 1rpx solid $uni-border-color;
  border-radius: 12rpx;
  background: $uni-bg-color;

  &.active {
    color: $uni-color-success;
    border-color: $uni-color-success;
    background: $uni-color-success-light;
    font-weight: 700;
  }
}

.option-list {
  margin-top: 16rpx;
}

.reason-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 64rpx;
  color: $uni-text-color-secondary;
  font-size: 26rpx;

  &.active {
    color: $uni-text-color;
    font-weight: 700;
  }
}

.amount-input {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex: 1;
  min-width: 0;
}

.amount-symbol {
  color: $uni-color-error;
  font-size: 30rpx;
  font-weight: 800;
}

.input {
  width: 180rpx;
  color: $uni-color-error;
  font-size: 30rpx;
  font-weight: 800;
  text-align: right;
}

.textarea {
  width: 100%;
  min-height: 160rpx;
  box-sizing: border-box;
  margin-top: 18rpx;
  padding: 18rpx;
  color: $uni-text-color;
  font-size: 27rpx;
  line-height: 38rpx;
  background: $uni-bg-color-grey;
  border-radius: 14rpx;
}

:deep(.input-placeholder) {
  color: $uni-text-color-placeholder;
}

.counter {
  display: block;
  margin-top: 10rpx;
  color: $uni-text-color-grey;
  font-size: 22rpx;
  text-align: right;
}

.bottom-bar {
  position: fixed;
  right: 0;
  bottom: var(--window-bottom, 0);
  left: 0;
  z-index: 99;
  padding: 18rpx 24rpx;
  background: $uni-bg-color;
  box-shadow: 0 -8rpx 24rpx rgba($uni-text-color, 0.08);
}

.submit-btn {
  height: 76rpx;
  margin: 0;
  color: $uni-text-color-inverse;
  font-size: 29rpx;
  font-weight: 800;
  line-height: 76rpx;
  border: none;
  border-radius: 38rpx;
  background: $uni-color-success;

  &[disabled] {
    color: $uni-text-color-grey;
    background: $uni-text-color-disable;
  }
}
</style>

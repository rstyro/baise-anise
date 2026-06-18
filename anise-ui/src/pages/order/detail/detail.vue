<template>
  <view class="page">
    <view class="status-panel" :class="currentStatusMeta.className">
      <view class="status-main">
        <view class="status-icon">
          <u-icon :name="currentStatusMeta.icon" size="38" :color="THEME_TEXT_INVERSE" />
        </view>
        <view class="status-copy">
          <text class="status-text">{{ currentStatusMeta.label }}</text>
          <text v-if="currentStatusMeta.desc" class="status-desc">{{ currentStatusMeta.desc }}</text>
        </view>
      </view>
      <view v-if="order.orderNo" class="status-order">
        <text class="status-order-label">订单编号</text>
        <text class="status-order-no u-line-1">{{ order.orderNo }}</text>
      </view>
    </view>

    <view class="content">
      <view v-if="order.address" class="address-card section-card">
        <view class="section-head">
          <view class="section-title">
            <view class="section-icon success">
              <u-icon name="map" size="18" :color="THEME_SUCCESS" />
            </view>
            <text>收货信息</text>
          </view>
        </view>
        <view class="address-content">
          <view class="addr-top">
            <text class="addr-name">{{ order.address.realName }}</text>
            <text class="addr-phone">{{ order.address.phone }}</text>
          </view>
          <text class="addr-detail">
            {{ order.address.province }}{{ order.address.city }}{{ order.address.district }} {{ order.address.detailAddress }}
          </text>
        </view>
      </view>

      <view class="goods-section section-card">
        <view class="shop-header">
          <view class="shop-title">
            <view class="section-icon success">
              <u-icon name="shop" size="18" :color="THEME_SUCCESS" />
            </view>
            <text class="shop-name u-line-1">{{ order.merchantName || '百色田阳自家八角种植园' }}</text>
          </view>
          <text class="shop-count">{{ order.items.length }}件商品</text>
        </view>

        <view
          v-for="item in order.items"
          :key="item.skuId || item.productId"
          class="goods-item"
          @click="goProduct(item.productId)"
        >
          <image :src="getImageUrl(item.productImage)" class="goods-img" mode="aspectFill" />
          <view class="goods-info">
            <view class="goods-name u-line-2">{{ item.productName }}</view>
            <view v-if="item.skuSpecs" class="goods-spec u-line-1">{{ formatSkuSpecs(item.skuSpecs) }}</view>
          </view>
          <view class="goods-right">
            <text class="goods-price">¥{{ formatAmount(item.price) }}</text>
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
          <text class="pay-amount">¥{{ payAmount }}</text>
        </view>
      </view>

      <view class="info-section section-card">
        <view class="section-head">
          <view class="section-title">
            <view class="section-icon muted">
              <u-icon name="order" size="18" :color="THEME_TEXT_GREY" />
            </view>
            <text>订单信息</text>
          </view>
        </view>
        <view class="info-row">
          <text class="info-label">订单编号</text>
          <view class="info-value order-no">
            <text class="u-line-1">{{ order.orderNo || '-' }}</text>
            <text v-if="order.orderNo" class="copy-btn" @click.stop="copyOrderNo">复制</text>
          </view>
        </view>
        <view class="info-row">
          <text class="info-label">创建时间</text>
          <text class="info-value">{{ order.createTime || '-' }}</text>
        </view>
        <view v-if="order.payTime" class="info-row">
          <text class="info-label">支付时间</text>
          <text class="info-value">{{ order.payTime }}</text>
        </view>
        <view v-if="order.deliveryTime" class="info-row">
          <text class="info-label">发货时间</text>
          <text class="info-value">{{ order.deliveryTime }}</text>
        </view>
        <view v-if="order.receiveTime" class="info-row">
          <text class="info-label">收货时间</text>
          <text class="info-value">{{ order.receiveTime }}</text>
        </view>
        <view v-if="order.remark" class="info-row">
          <text class="info-label">买家备注</text>
          <text class="info-value remark-value">{{ order.remark }}</text>
        </view>
      </view>
    </view>

    <view class="bottom-bar">
      <template v-if="order.status === 1">
        <view class="action-btn outline" @click="cancelOrder">取消订单</view>
        <view class="action-btn primary" @click="goPay">去付款</view>
      </template>
      <view v-else-if="order.status === 2" class="action-btn primary" @click="showToast('已提醒发货')">
        提醒发货
      </view>
      <template v-else-if="order.status === 3">
        <view class="action-btn outline" @click="viewLogistics">查看物流</view>
        <view class="action-btn primary" @click="confirmReceive">确认收货</view>
      </template>
      <template v-else-if="order.status === 4">
        <view class="action-btn outline" @click="goAfterSale">申请售后</view>
        <view class="action-btn primary" @click="buyAgain">再次购买</view>
      </template>
      <template v-else-if="order.status === 5">
        <view class="action-btn outline" @click="viewAfterSale">查看售后进度</view>
      </template>
      <view v-else-if="order.status === 0" class="action-btn outline" @click="buyAgain">
        再次购买
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import { payOrder } from '@/utils/pay'
import { THEME_SUCCESS, THEME_TEXT_GREY, THEME_TEXT_INVERSE } from '@/styles/theme'

type OrderStatus = 0 | 1 | 2 | 3 | 4 | 5

interface OrderAddress {
  realName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
}

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
  status?: OrderStatus
  orderNo?: string
  createTime?: string
  payTime?: string
  deliveryTime?: string
  receiveTime?: string
  remark?: string
  payAmount?: number | string
  totalAmount?: number | string
  merchantName?: string
  address: OrderAddress | null
  items: OrderGoodsItem[]
}

interface StatusMeta {
  label: string
  desc: string
  icon: string
  className: string
}

const createEmptyOrder = (): OrderDetail => ({
  address: null,
  items: [],
})

const order = ref<OrderDetail>(createEmptyOrder())

const statusMap: Record<OrderStatus, StatusMeta> = {
  0: { label: '已取消', desc: '该订单已取消', icon: 'close-circle', className: 'status-cancelled' },
  1: { label: '待支付', desc: '请在30分钟内完成支付，超时自动取消', icon: 'clock', className: 'status-wait-pay' },
  2: { label: '待发货', desc: '商家正在备货中', icon: 'car', className: 'status-wait-delivery' },
  3: { label: '已发货', desc: '商品已在路上，请耐心等待', icon: 'car-fill', className: 'status-delivering' },
  4: { label: '已完成', desc: '感谢您的购买，欢迎再次光临', icon: 'checkmark-circle', className: 'status-completed' },
  5: { label: '售后中', desc: '售后服务正在处理中', icon: 'info-circle', className: 'status-aftersale' },
}

const unknownStatus: StatusMeta = {
  label: '未知状态',
  desc: '',
  icon: 'info-circle',
  className: 'status-unknown',
}

const currentStatusMeta = computed(() => {
  const status = order.value.status
  return status === undefined ? unknownStatus : statusMap[status] || unknownStatus
})

const formatAmount = (value?: number | string): string => {
  const amount = Number(value || 0)
  return Number.isNaN(amount) ? '0.00' : amount.toFixed(2)
}

const totalAmount = computed(() => {
  const sum = order.value.items.reduce((total, item) => {
    return total + Number(item.price || 0) * Number(item.quantity || 1)
  }, 0)
  return formatAmount(sum)
})

const payAmount = computed(() => {
  return formatAmount(order.value.payAmount ?? order.value.totalAmount ?? totalAmount.value)
})

const showToast = (title: string, icon: 'none' | 'success' = 'none') => {
  uni.showToast({ title, icon })
}

const getOrderId = (): number => order.value.id || 0

const formatCurrentTime = (): string => {
  return new Date().toISOString().replace('T', ' ').substring(0, 19)
}

// 解析 skuSpecs JSON，提取规格值并用逗号拼接。
const formatSkuSpecs = (skuSpecs: string): string => {
  if (!skuSpecs || skuSpecs === '{}') return ''
  try {
    const obj = JSON.parse(skuSpecs) as Record<string, unknown>
    const values = Object.values(obj).filter((value): value is string | number => {
      return value !== null && value !== undefined && value !== ''
    })
    return values.join(', ')
  } catch (e) {
    return skuSpecs
  }
}

const loadDetail = async (id: number) => {
  const res = await orderApi.detail(id) as OrderDetail
  order.value = {
    ...createEmptyOrder(),
    ...res,
    address: res.address || null,
    items: res.items || [],
  }
}

onLoad(async (options?: { id?: string | number }) => {
  const id = Number(options?.id)
  if (!id) {
    showToast('订单不存在')
    setTimeout(() => uni.navigateBack(), 1000)
    return
  }

  try {
    await loadDetail(id)
  } catch (e) {
    showToast('加载失败')
  }
})

const goProduct = (productId: number) => {
  if (!productId) return
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}

const copyOrderNo = () => {
  if (!order.value.orderNo) return
  uni.setClipboardData({
    data: order.value.orderNo,
    success: () => showToast('已复制', 'success'),
  })
}

const cancelOrder = async () => {
  const orderId = getOrderId()
  if (!orderId) return

  const result = await uni.showModal({ title: '取消订单', content: '确定取消该订单吗？' })
  if (!result.confirm) return

  try {
    await orderApi.cancel(orderId)
    showToast('已取消', 'success')
    order.value.status = 0
  } catch (e) {
    showToast('取消失败')
  }
}

const goPay = async () => {
  const orderId = getOrderId()
  if (!orderId) return

  try {
    const payResult = await payOrder(orderId)
    order.value.status = 2
    order.value.payTime = formatCurrentTime()
    uni.$grouter.redirectTo('payResult', {
      query: {
        orderId,
        orderNo: payResult.orderNo || order.value.orderNo || '',
        amount: Number(payResult.payAmount || order.value.payAmount || 0).toFixed(2),
      },
    })
  } catch {
    showToast('支付失败')
  }
}

const confirmReceive = async () => {
  const orderId = getOrderId()
  if (!orderId) return

  const result = await uni.showModal({ title: '确认收货', content: '确定已收到商品吗？' })
  if (!result.confirm) return

  try {
    await orderApi.confirmReceive(orderId)
    showToast('已确认收货', 'success')
    order.value.status = 4
    order.value.receiveTime = formatCurrentTime()
  } catch {
    showToast('操作失败')
  }
}

const buyAgain = () => {
  showToast('已加入购物车', 'success')
  setTimeout(() => uni.$grouter.switchTab('cart'), 800)
}

const goAfterSale = () => {
  const orderId = getOrderId()
  if (!orderId) return
  uni.$grouter.navigateTo('afterSaleApply', { query: { orderId } })
}

const viewAfterSale = () => {
  const orderId = getOrderId()
  if (!orderId) return
  uni.$grouter.navigateTo('afterSaleDetail', { query: { orderId } })
}

const viewLogistics = () => {
  const orderId = getOrderId()
  if (!orderId) return
  uni.$grouter.navigateTo('orderLogistics', { query: { orderId } })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding-bottom: 132rpx;
  background: $uni-bg-color-page;
}

.status-panel {
  padding: 58rpx 28rpx 30rpx;
  color: $uni-text-color-inverse;
}

.status-main {
  display: flex;
  align-items: center;
  gap: 20rpx;
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
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 8rpx;
}

.status-text {
  font-size: 38rpx;
  font-weight: 800;
  line-height: 48rpx;
}

.status-desc {
  font-size: 25rpx;
  line-height: 34rpx;
  opacity: 0.9;
}

.status-order {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 26rpx;
  padding: 14rpx 18rpx;
  border-radius: 12rpx;
  background: rgba(255, 255, 255, 0.14);
}

.status-order-label {
  flex-shrink: 0;
  font-size: 24rpx;
  opacity: 0.82;
}

.status-order-no {
  flex: 1;
  min-width: 0;
  font-size: 24rpx;
  text-align: right;
}

.status-wait-pay,
.status-wait-delivery {
  background: $uni-color-warning;
}

.status-delivering {
  background: $uni-color-primary;
}

.status-completed {
  background: $uni-color-success;
}

.status-aftersale {
  background: $uni-color-error;
}

.status-cancelled,
.status-unknown {
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
  box-shadow: 0 8rpx 24rpx rgba(31, 31, 31, 0.04);
}

.section-head {
  padding-bottom: 18rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.section-title,
.shop-title {
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

  &.muted {
    background: $uni-bg-color-grey;
  }
}

.address-content {
  padding-top: 18rpx;
}

.addr-top {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 14rpx;
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

.addr-detail {
  color: $uni-text-color-secondary;
  font-size: 26rpx;
  line-height: 38rpx;
}

.shop-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding-bottom: 18rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
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
  font-weight: 800;
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

.pay-amount {
  color: $uni-color-error;
  font-size: 36rpx;
  font-weight: 800;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  padding: 14rpx 0;
  border-bottom: 1rpx solid $uni-border-color-light;
  font-size: 26rpx;

  &:last-child {
    padding-bottom: 0;
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

.order-no {
  display: flex;
  align-items: center;
  justify-content: flex-end;
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

.remark-value {
  line-height: 36rpx;
}

.bottom-bar {
  position: fixed;
  right: 0;
  bottom: var(--window-bottom, 0);
  left: 0;
  z-index: 99;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16rpx;
  min-height: 112rpx;
  padding: 0 24rpx;
  background: $uni-bg-color;
  box-shadow: 0 -8rpx 24rpx rgba(31, 31, 31, 0.08);
}

.action-btn {
  min-width: 176rpx;
  height: 68rpx;
  padding: 0 28rpx;
  border-radius: 34rpx;
  font-size: 28rpx;
  font-weight: 700;
  line-height: 68rpx;
  text-align: center;
}

.action-btn.outline {
  color: $uni-text-color-secondary;
  border: 1rpx solid $uni-border-color;
  background: $uni-bg-color;
}

.action-btn.primary {
  color: $uni-text-color-inverse;
  background: $uni-color-success;
}
</style>

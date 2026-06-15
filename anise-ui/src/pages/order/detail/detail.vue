<template>
  <view class="page">
    <!-- 状态头 -->
    <view class="status-bar" :style="{ background: statusBg(order.status) }">
      <view class="status-icon">
        <u-icon
          v-if="order.status === 1"
          name="clock"
          size="44"
          color="#fff"
        />
        <u-icon
          v-else-if="order.status === 2"
          name="car"
          size="44"
          color="#fff"
        />
        <u-icon
          v-else-if="order.status === 3"
          name="car-fill"
          size="44"
          color="#fff"
        />
        <u-icon
          v-else-if="order.status === 4"
          name="checkmark-circle"
          size="44"
          color="#fff"
        />
        <u-icon
          v-else-if="order.status === 0"
          name="close-circle"
          size="44"
          color="#fff"
        />
        <u-icon
          v-else
          name="info-circle"
          size="44"
          color="#fff"
        />
      </view>
      <view class="status-text">{{ statusLabel(order.status) }}</view>
      <view class="status-desc" v-if="statusDesc(order.status)">{{ statusDesc(order.status) }}</view>
    </view>

    <!-- 收货地址 -->
    <view class="section addr-section" v-if="order.address">
      <view class="section-title">
        <u-icon name="map" size="20" color="#4caf50" />
        <text>收货信息</text>
      </view>
      <view class="addr-content">
        <view class="addr-top">
          <text class="addr-name">{{ order.address.realName }}</text>
          <text class="addr-phone">{{ order.address.phone }}</text>
        </view>
        <text class="addr-detail">{{ order.address.province }}{{ order.address.city }}{{ order.address.district }} {{ order.address.detailAddress }}</text>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="section">
      <view class="section-title">
        <u-icon name="shop" size="20" color="#4caf50" />
        <text>百色田阳自家八角种植园</text>
      </view>
      <view class="goods-item" v-for="item in order.items" :key="item.skuId || item.productId" @click="goProduct(item.productId)">
        <image
          :src="getImageUrl(item.productImage)"
          class="goods-img"
          mode="aspectFill"
        />
        <view class="goods-info">
          <view class="goods-name u-line-2">{{ item.productName }}</view>
          <view class="goods-spec" v-if="item.skuSpecs">{{ formatSkuSpecs(item.skuSpecs) }}</view>
        </view>
        <view class="goods-right">
          <text class="goods-price">¥{{ item.price }}</text>
          <text class="goods-qty">×{{ item.quantity }}</text>
        </view>
      </view>
    </view>

    <!-- 价格明细 -->
    <view class="section">
      <view class="price-row">
        <text>商品合计</text>
        <text>¥{{ totalAmount }}</text>
      </view>
      <view class="price-row">
        <text>运费</text>
        <text style="color:#4caf50;">免运费</text>
      </view>
      <view class="price-row bold">
        <text>实付款</text>
        <text class="pay-amount">¥{{ order.payAmount || order.totalAmount }}</text>
      </view>
    </view>

    <!-- 订单信息 -->
    <view class="section">
      <view class="section-title">
        <u-icon name="order" size="20" color="#999" />
        <text>订单信息</text>
      </view>
      <view class="info-row">
        <text class="info-label">订单编号</text>
        <view class="info-value">
          <text>{{ order.orderNo }}</text>
          <text class="copy-btn" @click="copyOrderNo">复制</text>
        </view>
      </view>
      <view class="info-row">
        <text class="info-label">创建时间</text>
        <text class="info-value">{{ order.createTime }}</text>
      </view>
      <view class="info-row" v-if="order.payTime">
        <text class="info-label">支付时间</text>
        <text class="info-value">{{ order.payTime }}</text>
      </view>
      <view class="info-row" v-if="order.deliveryTime">
        <text class="info-label">发货时间</text>
        <text class="info-value">{{ order.deliveryTime }}</text>
      </view>
      <view class="info-row" v-if="order.receiveTime">
        <text class="info-label">收货时间</text>
        <text class="info-value">{{ order.receiveTime }}</text>
      </view>
      <view class="info-row" v-if="order.remark">
        <text class="info-label">买家备注</text>
        <text class="info-value">{{ order.remark }}</text>
      </view>
    </view>

    <!-- 底部留白 -->
    <view style="height: 140rpx;" />

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <!-- 待付款 -->
      <template v-if="order.status === 1">
        <view class="btn cancel" @click="cancelOrder">取消订单</view>
        <view class="btn pay" @click="goPay">去付款</view>
      </template>
      <!-- 待发货 -->
      <view
        v-else-if="order.status === 2"
        class="btn remind"
        @click="$u.toast('已提醒发货')"
      >
        提醒发货
      </view>
      <!-- 已发货 -->
      <template v-else-if="order.status === 3">
        <view class="btn outline" @click="$u.toast('查看物流')">查看物流</view>
        <view class="btn primary" @click="confirmReceive">确认收货</view>
      </template>
      <!-- 已完成 -->
      <template v-else-if="order.status === 4">
        <view class="btn outline" @click="goAfterSale">申请售后</view>
        <view class="btn primary" @click="buyAgain">再次购买</view>
      </template>
      <!-- 售后中 -->
      <template v-else-if="order.status === 5">
        <view class="btn outline" @click="viewAfterSale">查看售后进度</view>
      </template>
      <!-- 已取消 -->
      <view v-else-if="order.status === 0" class="btn outline" @click="buyAgain">
        再次购买
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderApi, payApi, aftersaleApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'

const order = ref({
  items: [],
  address: null,
})

onLoad(async (options) => {
  const id = Number(options.id)
  if (!id) {
    uni.showToast({ title: '订单不存在', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1000)
    return
  }
  try {
    const res = await orderApi.detail(id)
    order.value = res
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
})

// ========== 工具方法 ==========
// 解析 skuSpecs JSON，提取 value 用逗号隔开
const formatSkuSpecs = (skuSpecs: string): string => {
  if (!skuSpecs || skuSpecs === '{}') return ''
  try {
    const obj = JSON.parse(skuSpecs)
    const values = Object.values(obj).filter((v: any) => v !== null && v !== undefined && v !== '')
    return values.join(', ')
  } catch (e) {
    return skuSpecs
  }
}

// ========== 计算属性 ==========
const totalAmount = computed(() => {
  if (!order.value.items || !order.value.items.length) return '0.00'
  return order.value.items
    .reduce((sum, item) => sum + (item.price || 0) * (item.quantity || 1), 0)
    .toFixed(2)
})

// ========== 状态显示 ==========
const statusLabel = (s) =>
  ({ 0: '已取消', 1: '待支付', 2: '待发货', 3: '已发货', 4: '已完成', 5: '售后中' }[s] || '未知')
const statusBg = (s) =>
  ({ 0: '#bbb', 1: '#f29100', 2: '#4caf50', 3: '#2979ff', 4: '#999', 5: '#ff4d4f' }[s] || '#999')
const statusDesc = (s) =>
  ({ 1: `请在30分钟内完成支付，超时自动取消`, 2: '商家正在备货中', 3: '商品已在路上，请耐心等待', 4: '感谢您的购买，欢迎再次光临', 0: '该订单已取消' }[s] || '')

// ========== 操作 ==========
const goProduct = (productId: number) => {
  if (!productId) return
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}
const copyOrderNo = () => {
  uni.setClipboardData({ data: order.value.orderNo, success: () => uni.$u.toast('已复制') })
}

const cancelOrder = async () => {
  const r = await uni.showModal({ title: '取消订单', content: '确定取消该订单吗？' })
  if (r.confirm) {
    try {
      await orderApi.cancel(order.value.id)
      uni.$u.toast('已取消')
      order.value.status = 0
    } catch (e) {
      uni.$u.toast('取消失败')
    }
  }
}

const goPay = async () => {
  try {
    await payApi.mockPaySuccess(order.value.id)
    uni.$u.toast('支付成功')
    order.value.status = 2
    order.value.payTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
  } catch {
    uni.$u.toast('支付失败')
  }
}

const confirmReceive = async () => {
  const r = await uni.showModal({ title: '确认收货', content: '确定已收到商品吗？' })
  if (r.confirm) {
    try {
      await orderApi.confirmReceive(order.value.id)
      uni.$u.toast('已确认收货')
      order.value.status = 4
      order.value.receiveTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
    } catch {
      uni.$u.toast('操作失败')
    }
  }
}

const buyAgain = () => {
  uni.$u.toast('已加入购物车')
  setTimeout(() => uni.$grouter.switchTab('cart'), 800)
}

const goAfterSale = () => {
  uni.$grouter.navigateTo('orderDetail', { query: { id: order.value.id, mode: 'aftersale' } })
}

const viewAfterSale = async () => {
  try {
    const list = await aftersaleApi.list()
    const item = (list || []).find((a) => a.orderId === order.value.id)
    if (item) {
      uni.showModal({
        title: '售后状态',
        content: `售后编号：${item.afterSaleNo}\n状态：${
          { 0: '待处理', 1: '已同意', 2: '已拒绝', 3: '已完成' }[item.status] || '未知'
        }\n处理备注：${item.handleRemark || '暂无'}`,
        showCancel: false,
      })
    } else {
      uni.$u.toast('暂无售后记录')
    }
  } catch {
    uni.$u.toast('查询失败')
  }
}
</script>

<style lang="scss" scoped>
.page {
  background: #f5f9f5;
  min-height: 100vh;
}

// ========== 状态头 ==========
.status-bar {
  padding: 60rpx 32rpx 36rpx;
  color: #fff;
  .status-icon {
    text-align: center;
    margin-bottom: 12rpx;
  }
  .status-text {
    font-size: 36rpx;
    font-weight: 700;
    text-align: center;
  }
  .status-desc {
    font-size: 24rpx;
    text-align: center;
    margin-top: 8rpx;
    opacity: 0.85;
  }
}

// ========== 通用区块 ==========
.section {
  background: #fff;
  margin: 12rpx 16rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #2e3b2e;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
  margin-bottom: 16rpx;
}

// ========== 地址 ==========
.addr-section {
  .addr-content {
    .addr-top {
      display: flex;
      gap: 16rpx;
      margin-bottom: 6rpx;
    }
    .addr-name {
      font-size: 30rpx;
      font-weight: 600;
    }
    .addr-phone {
      font-size: 28rpx;
      color: #666;
    }
    .addr-detail {
      font-size: 26rpx;
      color: #999;
    }
  }
}

// ========== 商品列表 ==========
.goods-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  &:last-child {
    border-bottom: none;
  }
}
.goods-img {
  width: 120rpx;
  height: 120rpx;
  border-radius: 10rpx;
  background: #e8f5e9;
  flex-shrink: 0;
}
.goods-info {
  flex: 1;
  .goods-name {
    font-size: 28rpx;
    font-weight: 500;
  }
  .goods-spec {
    font-size: 24rpx;
    color: #999;
    margin-top: 4rpx;
  }
}
.goods-right {
  text-align: right;
  flex-shrink: 0;
  .goods-price {
    font-size: 28rpx;
    font-weight: 600;
    display: block;
  }
  .goods-qty {
    font-size: 24rpx;
    color: #999;
    margin-top: 4rpx;
    display: block;
  }
}

// ========== 价格明细 ==========
.price-row {
  display: flex;
  justify-content: space-between;
  font-size: 28rpx;
  color: #333;
  padding: 8rpx 0;
  &.bold {
    font-weight: 700;
    font-size: 30rpx;
    padding-top: 12rpx;
    border-top: 1rpx solid #f0f0f0;
    margin-top: 4rpx;
  }
}
.pay-amount {
  color: #ff4d4f;
  font-size: 36rpx;
}

// ========== 订单信息 ==========
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10rpx 0;
  font-size: 26rpx;
  .info-label {
    color: #999;
    flex-shrink: 0;
  }
  .info-value {
    color: #333;
    text-align: right;
  }
}
.copy-btn {
  color: #4caf50;
  margin-left: 12rpx;
  font-size: 24rpx;
  padding: 2rpx 12rpx;
  border: 1rpx solid #4caf50;
  border-radius: 6rpx;
}

// ========== 底部操作栏 ==========
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.04);
}
.btn {
  line-height: 64rpx;
  padding: 0 36rpx;
  border-radius: 32rpx;
  font-size: 28rpx;
  text-align: center;
}
.btn.outline {
  border: 2rpx solid #ccc;
  color: #666;
}
.btn.cancel {
  border: 2rpx solid #ccc;
  color: #666;
}
.btn.pay {
  background: linear-gradient(135deg, #f29100, #ffb74d);
  color: #fff;
}
.btn.remind {
  background: linear-gradient(135deg, #f29100, #ffb74d);
  color: #fff;
}
.btn.primary {
  background: linear-gradient(135deg, #f29100, #ffb74d);
  color: #fff;
}
</style>

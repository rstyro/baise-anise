<template>
  <view class="page">
    <view class="wrap">
      <view class="tabs-wrap">
        <u-tabs-swiper
          ref="tabs"
          :list="tabList"
          :current="current"
          activeColor="#faad14"
          inactiveColor="#595959"
          bg-color="#ffffff"
          :is-scroll="false"
          swiperWidth="750"
          @change="change"
        />
      </view>

      <swiper
        class="swiper-box"
        :current="swiperCurrent"
        @transition="transition"
        @animationfinish="animationfinish"
      >
        <swiper-item
          class="swiper-item"
          v-for="(tab, tabIdx) in tabList"
          :key="tabIdx"
        >
          <scroll-view
            scroll-y
            class="order-scroll"
            @scrolltolower="reachBottom"
          >
            <view class="page-box">
              <view
                class="order-card"
                v-for="order in orderList[tabIdx]"
                :key="order.id"
                @click="goDetail(order)"
              >
                <view class="order-head">
                  <view class="order-shop">
                    <u-icon name="home" size="28" color="#52c41a" />
                    <text class="shop-name">{{ order.store }}</text>
                    <u-icon name="arrow-right" size="22" color="#bfbfbf" />
                  </view>
                  <view class="status-pill" :style="{ color: statusColor(order.status), backgroundColor: statusBgColor(order.status) }">
                    {{ order.deal }}
                  </view>
                </view>

                <view
                  v-for="group in groupGoodsByMerchant(order.goodsList)"
                  :key="group.merchantId"
                  class="merchant-group"
                >
                  <view class="merchant-name" v-if="group.merchantName !== order.store">
                    <u-icon name="shop" size="22" color="#8c8c8c" />
                    <text>{{ group.merchantName }}</text>
                  </view>

                  <view class="goods-item" v-for="item in group.items" :key="item.goodsUrl + item.title">
                    <image :src="getImageUrl(item.goodsUrl)" class="goods-img" mode="aspectFill" />
                    <view class="goods-info">
                      <view class="goods-title u-line-2">{{ item.title }}</view>
                      <view class="goods-spec" v-if="item.type">{{ item.type }}</view>
                    </view>
                    <view class="goods-side">
                      <view class="goods-price">
                        ¥{{ priceInt(item.price) }}<text class="decimal">.{{ priceDecimal(item.price) }}</text>
                      </view>
                      <view class="goods-num">x{{ item.number }}</view>
                    </view>
                  </view>
                </view>

                <view class="order-summary">
                  <text class="summary-text">共 {{ totalNum(order.goodsList) }} 件</text>
                  <text class="summary-label">合计</text>
                  <text class="summary-price">
                    ¥{{ priceInt(totalPrice(order.goodsList)) }}<text class="decimal">.{{ priceDecimal(totalPrice(order.goodsList)) }}</text>
                  </text>
                </view>

                <view class="order-actions">
                  <view class="more-action">
                    <u-icon name="more-dot-fill" color="#bfbfbf" size="30" />
                  </view>
                  <view class="action-group">
                    <template v-if="order.status === 1">
                      <view class="btn btn-plain" @click.stop="cancelOrder(order)">取消订单</view>
                      <view class="btn btn-primary" @click.stop="goPay(order)">去付款</view>
                    </template>
                    <view v-else-if="order.status === 2" class="btn btn-primary" @click.stop="remindDelivery">
                      提醒发货
                    </view>
                    <template v-else-if="order.status === 3">
                      <view class="btn btn-plain" @click.stop="viewLogistics(order)">查看物流</view>
                      <view class="btn btn-primary" @click.stop="confirmReceive(order)">确认收货</view>
                    </template>
                    <template v-else-if="order.status === 4">
                      <view class="btn btn-plain" @click.stop="evaluateOrder">评价</view>
                      <view class="btn btn-primary" @click.stop="buyAgain">再次购买</view>
                    </template>
                  </view>
                </view>
              </view>

              <view class="empty-state" v-if="!orderList[tabIdx] || orderList[tabIdx].length === 0">
                <u-empty text="您还没有相关订单" mode="order" marginTop="120" />
                <view class="empty-tip">可以去看看有哪些想买的</view>
                <view class="go-btn" @click="goShop">随便逛逛</view>
              </view>

              <view class="load-more" v-if="orderList[tabIdx] && orderList[tabIdx].length > 0">
                <u-loadmore :status="loadStatus[tabIdx]" />
              </view>
            </view>
          </scroll-view>
        </swiper-item>
      </swiper>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getImageUrl } from '@/utils/image'
import { orderApi } from '@/api/businessApi'
import { payOrder } from '@/utils/pay'

// ========== 类型 ==========
interface GoodsItem {
  merchantId: number
  merchantName: string
  goodsUrl: string
  title: string
  type: string
  price: string
  number: number
}
interface OrderItem {
  id: number | string
  store: string
  deal: string
  status: number
  orderNo: string
  payAmount?: number
  goodsList: GoodsItem[]
}

// 商品分组类型
interface GoodsGroup {
  merchantId: number
  merchantName: string
  items: GoodsItem[]
}

// 按商铺分组商品
function groupGoodsByMerchant(items: GoodsItem[]): GoodsGroup[] {
  const groups: Map<number, GoodsGroup> = new Map()
  items.forEach(item => {
    const merchantId = item.merchantId || 0
    if (!groups.has(merchantId)) {
      groups.set(merchantId, {
        merchantId,
        merchantName: item.merchantName || '默认店铺',
        items: []
      })
    }
    groups.get(merchantId)!.items.push(item)
  })
  return Array.from(groups.values())
}

const { proxy } = getCurrentInstance() as any

// ========== Tab ==========
const tabList = ref([{ name: '全部' }, { name: '待支付' }, { name: '进行中' }, { name: '待评价' }])
const current = ref(0)
const swiperCurrent = ref(0)
const loadStatus = ref<string[]>(['loadmore', 'loadmore', 'loadmore', 'loadmore'])

// ========== 分页参数 ==========
const PAGE_SIZE = 10
const pageNum = ref<number[]>([1, 1, 1, 1])
const total = ref<number[]>([0, 0, 0, 0])

// ========== 订单数据 ==========
const orderList = ref<OrderItem[][]>([[], [], [], []])

// tab索引对应查询的状态：0-全部, 1-待支付(1), 2-进行中(2,3), 3-待评价(4)
const tabStatusMap: Record<number, number> = {
  0: 0,     // 全部
  1: 1,     // 待支付
  2: -1,    // 进行中（特殊标记，包含多种状态）
  3: 4      // 待评价（已完成）
}

onLoad((options) => {
  const status = Number(options?.status) || 0
  let tabIdx = 0
  if (status === 0) {
    tabIdx = 0
  } else if (status === 1) {
    tabIdx = 1
  } else if (status >= 2 && status <= 3) {
    tabIdx = 2
  } else if (status === 4) {
    tabIdx = 3
  }
  current.value = tabIdx
  swiperCurrent.value = tabIdx
  loadOrders(tabIdx)
})

async function loadOrders(tabIdx: number, isLoadMore = false) {
  const status = tabStatusMap[tabIdx]
  const currentPage = pageNum.value[tabIdx]
  
  if (!isLoadMore) {
    loadStatus.value.splice(tabIdx, 1, 'loading')
  }
  
  try {
    const res = await orderApi.list(status, currentPage, PAGE_SIZE)
    const records = res.records || []
    const totalCount = res.total || 0
    
    const items: OrderItem[] = records.map((r: any) => ({
      id: r.id,
      store: '百色田阳自家八角种植园',
      deal: dealLabel(r.status),
      status: r.status,
      orderNo: r.orderNo,
      payAmount: r.payAmount || 0,
      goodsList: (r.goodsList || []).map((g: any) => ({
        merchantId: g.merchantId || 0,
        merchantName: g.merchantName || '默认店铺',
        goodsUrl: g.goodsUrl || '/static/logo.png',
        title: g.title || '商品',
        type: g.type || '',
        price: String(g.price || 0),
        number: g.number || 1
      })) || [{
        merchantId: 0,
        merchantName: '默认店铺',
        goodsUrl: '/static/logo.png',
        title: r.goodsName || '商品',
        type: r.specName || '',
        price: String(r.price || r.payAmount || 0),
        number: r.quantity || 1
      }]
    }))
    
    if (isLoadMore) {
      orderList.value[tabIdx] = [...orderList.value[tabIdx], ...items]
    } else {
      orderList.value[tabIdx] = items
    }
    
    total.value[tabIdx] = totalCount
    
    const hasMore = orderList.value[tabIdx].length < totalCount
    loadStatus.value.splice(tabIdx, 1, hasMore ? 'loadmore' : 'nomore')
  } catch (e) {
    console.error('loadOrders error:', e)
    loadStatus.value.splice(tabIdx, 1, 'loadmore')
    if (!isLoadMore) {
      orderList.value[tabIdx] = mockOrders(tabIdx)
    }
  }
}

function mockOrders(tabIdx: number): OrderItem[] {
  const now = Date.now()
  const data: Record<number, OrderItem[]> = {
    0: [
      { id: now + 1, store: '百色田阳自家八角种植园', deal: dealLabel(1), status: 1, orderNo: 'BX' + now, goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '百色无硫大红八角', type: '500g/袋', price: '45.00', number: 2 }
      ]},
      { id: now + 2, store: '百色田阳自家八角种植园', deal: dealLabel(2), status: 2, orderNo: 'BX' + (now + 1), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '百色野生蜂蜜', type: '250g/瓶', price: '38.00', number: 1 }
      ]},
      { id: now + 3, store: '百色田阳自家八角种植园', deal: dealLabel(3), status: 3, orderNo: 'BX' + (now + 2), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '八角粉调料', type: '50g/瓶', price: '12.00', number: 2 }
      ]},
      { id: now + 4, store: '百色田阳自家八角种植园', deal: dealLabel(4), status: 4, orderNo: 'BX' + (now + 3), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '田阳青花椒', type: '100g/袋', price: '15.00', number: 1 }
      ]}
    ],
    1: [
      { id: now + 1, store: '百色田阳自家八角种植园', deal: dealLabel(1), status: 1, orderNo: 'BX' + now, goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '百色无硫大红八角', type: '500g/袋', price: '45.00', number: 2 }
      ]},
      { id: now + 2, store: '百色田阳自家八角种植园', deal: dealLabel(1), status: 1, orderNo: 'BX' + (now + 1), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '田阳青花椒', type: '100g/袋', price: '15.00', number: 3 }
      ]}
    ],
    2: [
      { id: now + 3, store: '百色田阳自家八角种植园', deal: dealLabel(2), status: 2, orderNo: 'BX' + (now + 2), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '百色野生蜂蜜', type: '250g/瓶', price: '38.00', number: 1 }
      ]},
      { id: now + 4, store: '百色田阳自家八角种植园', deal: dealLabel(3), status: 3, orderNo: 'BX' + (now + 3), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '八角粉调料', type: '50g/瓶', price: '12.00', number: 2 },
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '八角油', type: '100ml/瓶', price: '28.00', number: 1 }
      ]},
      { id: now + 5, store: '百色田阳自家八角种植园', deal: dealLabel(3), status: 3, orderNo: 'BX' + (now + 4), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '百色无硫大红八角', type: '250g/袋', price: '25.00', number: 1 }
      ]}
    ],
    3: [
      { id: now + 6, store: '百色田阳自家八角种植园', deal: dealLabel(4), status: 4, orderNo: 'BX' + (now + 5), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '百色无硫大红八角', type: '500g/袋', price: '45.00', number: 1 }
      ]},
      { id: now + 7, store: '百色田阳自家八角种植园', deal: dealLabel(4), status: 4, orderNo: 'BX' + (now + 6), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '百色野生蜂蜜', type: '250g/瓶', price: '38.00', number: 1 }
      ]},
      { id: now + 8, store: '百色田阳自家八角种植园', deal: dealLabel(4), status: 4, orderNo: 'BX' + (now + 7), goodsList: [
        { merchantId: 1, merchantName: '百色田阳自家八角种植园', goodsUrl: '/static/logo.png', title: '八角粉调料', type: '50g/瓶', price: '12.00', number: 5 }
      ]}
    ]
  }
  return data[tabIdx] || []
}

// ========== 状态标签 ==========
const statusLabel: Record<number, string> = { 0: '已取消', 1: '待支付', 2: '待发货', 3: '已发货', 4: '已完成', 5: '售后中', 6: '退款中', 7: '已退款' }
const statusColors: Record<number, string> = { 0: '#999', 1: '#f29100', 2: '#4caf50', 3: '#2979ff', 4: '#999', 5: '#ff4d4f', 6: '#ff9800', 7: '#e91e63' }
const statusBgColors: Record<number, string> = { 0: '#f5f5f5', 1: '#fffbe6', 2: '#f6ffed', 3: '#e6f7ff', 4: '#f5f5f5', 5: '#fff2f0', 6: '#fff7e6', 7: '#fff0f6' }
function dealLabel(status: number) { return statusLabel[status] || '未知' }
function statusColor(status: number) { return statusColors[status] || '#999' }
function statusBgColor(status: number) { return statusBgColors[status] || '#f5f5f5' }

// ========== 价格工具 ==========
function priceInt(val: string): string {
  if (val !== String(parseInt(val))) return val.split('.')[0]
  return val
}
function priceDecimal(val: string): string {
  if (val !== String(parseInt(val))) return val.slice(-2)
  return '00'
}
function totalPrice(items: GoodsItem[]): string {
  let price = 0
  items.forEach(v => price += parseFloat(v.price) * (v.number || 1))
  return price.toFixed(2)
}
function totalNum(items: GoodsItem[]): number {
  let num = 0
  items.forEach(v => num += v.number || 1)
  return num
}

// ========== Tab/Swiper 联动 ==========
function change(index: number) {
  swiperCurrent.value = index
}
function transition({ detail: { dx } }: { detail: { dx: number } }) {
  proxy?.$refs.tabs.setDx(dx)
}
function animationfinish({ detail: { current: cur } }: { detail: { current: number } }) {
  proxy?.$refs.tabs.setFinishCurrent(cur)
  swiperCurrent.value = cur
  current.value = cur
  
  if (orderList.value[cur].length === 0 && loadStatus.value[cur] === 'loadmore') {
    loadOrders(cur)
  }
}
function reachBottom() {
  const tabIdx = current.value
  if (loadStatus.value[tabIdx] !== 'loadmore') return
  
  pageNum.value.splice(tabIdx, 1, pageNum.value[tabIdx] + 1)
  loadOrders(tabIdx, true)
}

// ========== 操作 ==========
const goDetail = (order: OrderItem) => {
  uni.$grouter.navigateTo('orderDetail', { query: { id: order.id } })
}
const cancelOrder = async (order: OrderItem) => {
  const r = await uni.showModal({ title: '取消订单', content: '确定取消该订单吗？' })
  if (r.confirm) {
    await orderApi.cancel(order.id as number)
    uni.$u.toast('已取消')
    const tabIdx = Math.max(0, Math.min(order.status - 1, 3))
    pageNum.value.splice(tabIdx, 1, 1)
    loadOrders(tabIdx)
  }
}
const goPay = async (order: OrderItem) => {
  try { 
    const payResult = await payOrder(order.id as number)
    uni.$grouter.navigateTo('payResult', {
      query: {
        orderId: order.id,
        orderNo: payResult.orderNo || order.orderNo || '',
        amount: Number(payResult.payAmount || order.payAmount || totalPrice(order.goodsList)).toFixed(2),
      },
    })
  } catch { 
    uni.$u.toast('支付失败') 
  }
}
const confirmReceive = async (order: OrderItem) => {
  const r = await uni.showModal({ title: '确认收货', content: '确定已收到商品吗？' })
  if (r.confirm) {
    await orderApi.confirmReceive(order.id as number)
    uni.$u.toast('已确认收货')
    const tabIdx = Math.max(0, Math.min(order.status - 1, 3))
    pageNum.value.splice(tabIdx, 1, 1)
    loadOrders(tabIdx)
  }
}
const remindDelivery = () => uni.$u.toast('已提醒发货')
const viewLogistics = (order: OrderItem) => {
  uni.$grouter.navigateTo('orderLogistics', { query: { orderId: order.id } })
}
const evaluateOrder = () => uni.$u.toast('评价')
const buyAgain = () => uni.$u.toast('再次购买')
const goShop = () => uni.$grouter.switchTab('index')
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: $uni-bg-color-page;
}

.wrap {
  display: flex;
  flex-direction: column;
  height: calc(100vh - var(--window-top));
  width: 100%;
  background: $uni-bg-color-page;
}

.tabs-wrap {
  position: relative;
  z-index: 3;
  background: $uni-bg-color;
  box-shadow: 0 6rpx 20rpx rgba($uni-text-color, 0.04);
}

.swiper-box {
  flex: 1;
}

.swiper-item {
  height: 100%;
}

.order-scroll {
  height: 100%;
  width: 100%;
}

.page-box {
  padding: 18rpx 20rpx 32rpx;
}

.order-card {
  margin-bottom: 18rpx;
  padding: 24rpx 24rpx 22rpx;
  border-radius: 16rpx;
  background: $uni-bg-color;
  box-shadow: 0 8rpx 24rpx rgba($uni-text-color, 0.05);
}

.order-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.order-shop {
  display: flex;
  align-items: center;
  min-width: 0;
  flex: 1;
}

.shop-name {
  min-width: 0;
  margin: 0 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 29rpx;
  font-weight: 700;
  line-height: 38rpx;
  color: $uni-text-color;
}

.status-pill {
  flex-shrink: 0;
  min-width: 108rpx;
  padding: 7rpx 16rpx;
  border-radius: 999rpx;
  text-align: center;
  font-size: 23rpx;
  font-weight: 600;
  line-height: 30rpx;
}

.merchant-group {
  padding-top: 18rpx;
}

.merchant-name {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 12rpx;
  font-size: 24rpx;
  line-height: 32rpx;
  color: $uni-text-color-grey;
}

.goods-item {
  display: flex;
  gap: 18rpx;
  padding: 14rpx 0;
}

.goods-img {
  flex-shrink: 0;
  width: 150rpx;
  height: 150rpx;
  border-radius: 12rpx;
  background: $uni-bg-color-grey;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-title {
  min-height: 72rpx;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 36rpx;
  color: $uni-text-color;
}

.goods-spec {
  display: inline-block;
  max-width: 100%;
  margin-top: 12rpx;
  padding: 6rpx 12rpx;
  border-radius: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 23rpx;
  line-height: 30rpx;
  color: $uni-text-color-grey;
  background: $uni-bg-color-grey;
}

.goods-side {
  flex-shrink: 0;
  min-width: 112rpx;
  text-align: right;
}

.goods-price {
  font-size: 28rpx;
  font-weight: 700;
  line-height: 36rpx;
  color: $uni-text-color;
}

.decimal {
  font-size: 22rpx;
}

.goods-num {
  margin-top: 10rpx;
  font-size: 23rpx;
  line-height: 30rpx;
  color: $uni-text-color-grey;
}

.order-summary {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  gap: 10rpx;
  margin-top: 14rpx;
  padding-top: 18rpx;
  border-top: 1rpx solid $uni-border-color-light;
  font-size: 24rpx;
  line-height: 34rpx;
  color: $uni-text-color-secondary;
}

.summary-text {
  color: $uni-text-color-grey;
}

.summary-price {
  font-size: 34rpx;
  font-weight: 800;
  color: $uni-color-error;
}

.order-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: 22rpx;
}

.more-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
}

.action-group {
  display: flex;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 14rpx;
  flex: 1;
}

.btn {
  min-width: 148rpx;
  height: 56rpx;
  padding: 0 22rpx;
  border-radius: 999rpx;
  box-sizing: border-box;
  text-align: center;
  font-size: 25rpx;
  line-height: 54rpx;
}

.btn-plain {
  border: 1rpx solid $uni-border-color;
  color: $uni-text-color-secondary;
  background: $uni-bg-color;
}

.btn-primary {
  border: 1rpx solid $uni-color-warning;
  color: $uni-text-color-inverse;
  background: $uni-color-warning;
}

.empty-state {
  padding: 70rpx 0 120rpx;
  text-align: center;
}

.empty-tip {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 32rpx;
  color: $uni-text-color-grey;
}

.go-btn {
  width: 208rpx;
  height: 64rpx;
  margin: 44rpx auto 0;
  border-radius: 999rpx;
  text-align: center;
  font-size: 26rpx;
  line-height: 64rpx;
  color: $uni-text-color-inverse;
  background: $uni-color-warning;
}

.load-more {
  padding: 24rpx 0 8rpx;
}
</style>

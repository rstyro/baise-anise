<template>
  <view class="wrap">
    <!-- Tab切换 -->
    <view class="u-tabs-box">
      <u-tabs-swiper
        activeColor="#f29100"
        ref="tabs"
        :list="tabList"
        :current="current"
        @change="change"
        :is-scroll="false"
        swiperWidth="750"
      />
    </view>

    <!-- Swiper内容区 -->
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
          style="height: 100%; width: 100%"
          @scrolltolower="reachBottom"
        >
          <view class="page-box">
            <!-- 订单卡片 -->
            <view
              class="order"
              v-for="order in orderList[tabIdx]"
              :key="order.id"
              @click="goDetail(order)"
            >
              <view class="top">
                <view class="left">
                  <u-icon name="home" :size="30" color="rgb(94,94,94)" />
                  <view class="store">{{ order.store }}</view>
                  <u-icon name="arrow-right" color="rgb(203,203,203)" :size="26" />
                </view>
                <view class="right" :style="{ color: statusColor(order.status) }">
                  {{ order.deal }}
                </view>
              </view>

              <!-- 商品列表 -->
              <view class="item" v-for="item in order.goodsList" :key="item.goodsUrl">
                <view class="left">
                  <image :src="getImageUrl(item.goodsUrl)" mode="aspectFill" />
                </view>
                <view class="content">
                  <view class="title u-line-2">{{ item.title }}</view>
                  <view class="type">{{ item.type }}</view>
                </view>
                <view class="right">
                  <view class="price">
                    ￥{{ priceInt(item.price) }}
                    <text class="decimal">.{{ priceDecimal(item.price) }}</text>
                  </view>
                  <view class="number">x{{ item.number }}</view>
                </view>
              </view>

              <!-- 合计 -->
              <view class="total">
                共{{ totalNum(order.goodsList) }}件商品 合计:
                <text class="total-price">
                  ￥{{ priceInt(totalPrice(order.goodsList)) }}.
                  <text class="decimal">{{ priceDecimal(totalPrice(order.goodsList)) }}</text>
                </text>
              </view>

              <!-- 底部操作 -->
              <view class="bottom">
                <view class="more"><u-icon name="more-dot-fill" color="rgb(203,203,203)" /></view>
                <!-- 待付款 -->
                <template v-if="order.status === 1">
                  <view class="cancel btn" @click.stop="cancelOrder(order)">取消订单</view>
                  <view class="pay btn" @click.stop="goPay(order)">去付款</view>
                </template>
                <!-- 待发货 -->
                <view v-else-if="order.status === 2" class="remind btn" @click.stop="$u.toast('已提醒发货')">
                  提醒发货
                </view>
                <!-- 已发货 -->
                <template v-else-if="order.status === 3">
                  <view class="logistics btn" @click.stop="$u.toast('查看物流')">查看物流</view>
                  <view class="confirm btn" @click.stop="confirmReceive(order)">确认收货</view>
                </template>
                <!-- 已完成 -->
                <template v-else-if="order.status === 4">
                  <view class="evaluate btn" @click.stop="$u.toast('评价')">评价</view>
                  <view class="buy-again btn" @click.stop="$u.toast('再次购买')">再次购买</view>
                </template>
              </view>
            </view>

            <!-- 空状态 -->
            <view class="centre" v-if="!orderList[tabIdx] || orderList[tabIdx].length === 0">
              <image src="https://ik.imagekit.io/anyup/uview-pro/template/taobao-order.png" mode="" />
              <view class="explain">
                您还没有相关的订单
                <view class="tips">可以去看看有那些想买的</view>
              </view>
              <view class="go-btn" @click="goShop">随便逛逛</view>
            </view>

            <u-loadmore :status="loadStatus[tabIdx]" bgColor="#f2f2f2" />
          </view>
        </scroll-view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { getImageUrl } from '@/utils/image'
import { orderApi, payApi } from '@/api/businessApi'

// ========== 类型 ==========
interface GoodsItem {
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

const { proxy } = getCurrentInstance() as any

// ========== Tab ==========
const tabList = ref([{ name: '待付款' }, { name: '待发货' }, { name: '已发货' }, { name: '已完成' }])
const current = ref(0)
const swiperCurrent = ref(0)
const loadStatus = ref<string[]>(['loadmore', 'loadmore', 'loadmore', 'loadmore'])

// ========== 分页参数 ==========
const PAGE_SIZE = 10
const pageNum = ref<number[]>([1, 1, 1, 1])
const total = ref<number[]>([0, 0, 0, 0])

// ========== 订单数据 ==========
const orderList = ref<OrderItem[][]>([[], [], [], []])

onMounted(() => {
  loadOrders(current.value)
})

async function loadOrders(tabIdx: number, isLoadMore = false) {
  const status = tabIdx + 1
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
        goodsUrl: g.goodsUrl || '/static/logo.png',
        title: g.title || '商品',
        type: g.type || '',
        price: String(g.price || 0),
        number: g.number || 1
      })) || [{
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
      orderList.value[tabIdx] = mockOrders(status)
    }
  }
}

function mockOrders(status: number): OrderItem[] {
  const base: OrderItem[] = [
    { id: Date.now() + 1, store: '百色田阳自家八角种植园', deal: dealLabel(status), status, orderNo: 'BX' + Date.now(), goodsList: [
      { goodsUrl: '/static/logo.png', title: '百色无硫大红八角', type: '500g/袋', price: '45.00', number: 1 }
    ]},
    { id: Date.now() + 2, store: '百色田阳自家八角种植园', deal: dealLabel(status), status, orderNo: 'BX' + (Date.now() + 1), goodsList: [
      { goodsUrl: '/static/logo.png', title: '百色野生蜂蜜', type: '250g/瓶', price: '38.00', number: 2 },
      { goodsUrl: '/static/logo.png', title: '田阳青花椒', type: '100g/袋', price: '15.00', number: 1 }
    ]}
  ]
  return status === 3 ? base : base  // 待收货有数据
}

// ========== 状态标签 ==========
const statusLabel: Record<number, string> = { 0: '已取消', 1: '待支付', 2: '待发货', 3: '已发货', 4: '已完成', 5: '售后中' }
const statusColors: Record<number, string> = { 0: '#999', 1: '#f29100', 2: '#4caf50', 3: '#2979ff', 4: '#999', 5: '#ff4d4f' }
function dealLabel(status: number) { return statusLabel[status] || '未知' }
function statusColor(status: number) { return statusColors[status] || '#999' }

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
    await payApi.mockPaySuccess(order.id as number)
    uni.$u.toast('支付成功')
    const tabIdx = Math.max(0, Math.min(order.status - 1, 3))
    pageNum.value.splice(tabIdx, 1, 1)
    loadOrders(tabIdx)
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
const goShop = () => uni.$grouter.switchTab('index')
</script>

<style lang="scss" scoped>
.order {
  width: 710rpx;
  background-color: $u-bg-white;
  margin: 20rpx auto;
  border-radius: 20rpx;
  box-sizing: border-box;
  padding: 20rpx;
  font-size: 28rpx;
  .top {
    display: flex;
    justify-content: space-between;
    .left {
      display: flex;
      align-items: center;
      .store {
        margin: 0 10rpx;
        font-size: 32rpx;
        font-weight: bold;
      }
    }
    .right {
      color: $u-type-warning-dark;
    }
  }
  .item {
    display: flex;
    margin: 20rpx 0 0;
    .left {
      margin-right: 20rpx;
      image {
        width: 200rpx;
        height: 200rpx;
        border-radius: 10rpx;
      }
    }
    .content {
      .title {
        font-size: 28rpx;
        line-height: 50rpx;
      }
      .type {
        margin: 10rpx 0;
        font-size: 24rpx;
        color: $u-tips-color;
      }
    }
    .right {
      margin-left: 10rpx;
      padding-top: 20rpx;
      text-align: right;
      .decimal {
        font-size: 24rpx;
        margin-top: 4rpx;
      }
      .number {
        color: $u-tips-color;
        font-size: 24rpx;
      }
    }
  }
  .total {
    margin-top: 20rpx;
    text-align: right;
    font-size: 24rpx;
    .total-price {
      font-size: 32rpx;
    }
  }
  .bottom {
    display: flex;
    margin-top: 40rpx;
    padding: 0 10rpx;
    justify-content: space-between;
    align-items: center;
    .btn {
      line-height: 52rpx;
      width: 160rpx;
      border-radius: 26rpx;
      border: 2rpx solid $u-border-color;
      font-size: 26rpx;
      text-align: center;
      color: $u-type-info-dark;
    }
    .pay {
      color: $u-type-warning-dark;
      border-color: $u-type-warning-dark;
    }
    .confirm {
      color: $u-type-warning-dark;
      border-color: $u-type-warning-dark;
    }
  }
}
.centre {
  text-align: center;
  margin: 200rpx auto;
  font-size: 32rpx;
  image {
    width: 164rpx;
    height: 164rpx;
    border-radius: 50%;
    margin-bottom: 20rpx;
  }
  .tips {
    font-size: 24rpx;
    color: $u-tips-color;
    margin-top: 20rpx;
  }
  .go-btn {
    margin: 80rpx auto;
    width: 200rpx;
    border-radius: 32rpx;
    line-height: 64rpx;
    color: #fff;
    font-size: 26rpx;
    background: linear-gradient(270deg, #f29100 0%, #ffb74d 100%);
  }
}
.wrap {
  display: flex;
  flex-direction: column;
  height: calc(100vh - var(--window-top));
  width: 100%;
}
.swiper-box {
  flex: 1;
}
.swiper-item {
  height: 100%;
}
</style>

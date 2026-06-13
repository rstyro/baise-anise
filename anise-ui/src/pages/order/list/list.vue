<template>
  <view class="page">
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
              <!-- 按商铺分组显示商品 -->
              <view v-for="group in groupGoodsByMerchant(order.goodsList)" :key="group.merchantId" class="merchant-group">
                <view class="top">
                  <view class="left">
                    <u-icon name="home" :size="30" color="rgb(94,94,94)" />
                    <view class="store">{{ group.merchantName }}</view>
                    <u-icon name="arrow-right" color="rgb(203,203,203)" :size="26" />
                  </view>
                  <view class="right" :style="{ color: statusColor(order.status) }">
                    {{ order.deal }}
                  </view>
                </view>

                <!-- 商品列表 -->
                <view class="item" v-for="item in group.items" :key="item.goodsUrl + item.title">
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
              <image src="/static/images/order/taobao-order.png" mode="" />
              <view class="explain">
                您还没有相关的订单
                <view class="tips">可以去看看有那些想买的</view>
              </view>
              <view class="go-btn" @click="goShop">随便逛逛</view>
            </view>

            <u-loadmore v-if="orderList[tabIdx] && orderList[tabIdx].length > 0" :status="loadStatus[tabIdx]"  />
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
import { orderApi, payApi } from '@/api/businessApi'

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
const tabStatusMap = {
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
.page {
  background: #f5f9f5;
  min-height: 100vh;
}
.wrap {
  background: #f5f9f5;
}
.order {
  width: 710rpx;
  background-color: $u-bg-white;
  margin: 20rpx auto;
  border-radius: 20rpx;
  box-sizing: border-box;
  padding: 20rpx;
  font-size: 28rpx;
  .merchant-group {
    margin-bottom: 20rpx;
    &:last-child {
      margin-bottom: 0;
    }
  }
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

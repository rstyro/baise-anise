<template>
  <view class="page">
    <view class="section">
      <view class="addr-card" @click="selectAddress" v-if="selectedAddress">
        <view class="addr-top">
          <text class="addr-name">{{ selectedAddress.realName }}</text>
          <text class="addr-phone">{{ selectedAddress.phone }}</text>
        </view>
        <text class="addr-detail">{{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }} {{ selectedAddress.detailAddress }}</text>
      </view>
      <view class="addr-empty" v-else @click="selectAddress">+ 请选择收货地址</view>
    </view>

    <!-- 按商铺分组显示商品 -->
    <view class="section" v-for="group in groupedGoodsList" :key="group.merchantId">
      <view class="shop-header"><u-icon name="shop" size="20" color="#4caf50" /> {{ group.merchantName }}</view>
      <view class="goods-item" v-for="item in group.items" :key="item.id">
        <image :src="getImageUrl(item.mainImage)" class="goods-img" mode="aspectFill" />
        <view class="goods-info">
          <view class="goods-name">{{ item.productName }}</view>
          <view class="goods-spec">{{ item.skuSpecs ? formatSkuSpecs(item.skuSpecs) : item.specName }}</view>
        </view>
        <view class="goods-right">
          <text class="goods-price">¥{{ item.price }}</text>
          <text class="goods-qty">×{{ item.quantity }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="row"><text>商品合计</text><text>¥{{ totalAmount }}</text></view>
      <view class="row"><text>运费</text><text style="color:#4caf50;">免运费</text></view>
      <view class="row bold"><text>实付款</text><text style="font-size:36rpx;color:#ff4d4f;">¥{{ totalAmount }}</text></view>
    </view>

    <view class="remark"><input placeholder="买家备注（选填）" v-model="remark" /></view>

    <view class="submit-area">
      <button class="submit-btn" @click="submitOrder">提交订单 ¥{{ totalAmount }}</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { cartApi, orderApi, addressApi, payApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import type { CartItem } from '@/api/types/product'
import { useCartStore } from '@/stores/cart'

const cartIds = ref<number[]>([])
const selectedAddress = ref<any>(null)
const goodsList = ref<CartItem[]>([])
const remark = ref('')
const merchantId = ref<number | null>(null)
const cartStore = useCartStore()
const isDirectBuy = ref(false)

// 按商铺分组
interface GoodsGroup {
  merchantId: number
  merchantName: string
  items: CartItem[]
}

const groupedGoodsList = computed<GoodsGroup[]>(() => {
  const groups: Map<number, GoodsGroup> = new Map()
  goodsList.value.forEach(item => {
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
})

onShow(() => {
  const addr = getApp().globalData.selectedAddress
  if (addr) {
    selectedAddress.value = addr
    delete getApp().globalData.selectedAddress
  }
})
const totalAmount = computed(() => goodsList.value.reduce((s, i) => s + i.price * i.quantity, 0).toFixed(2))

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

onLoad(async (options: { cartIds?: string } | any) => {
  console.log("options:", options)
  // 兼容 H5 和微信小程序的参数传递方式
  const cartIdsStr = options?.cartIds || options?.query?.cartIds || ''
  if (cartIdsStr) {
    cartIds.value = cartIdsStr.split(',').map(Number).filter(id => !isNaN(id))
  }
  console.log("cartIds parsed:", cartIds.value)

  // 检查是否是立即购买（优先）
  const buyNowGoods = cartStore.getAndClearDirectBuyGoods()
  if (buyNowGoods) {
    // 立即购买：使用保存的商品信息
    goodsList.value = [{
      id: buyNowGoods.skuId,
      productId: buyNowGoods.productId,
      productName: buyNowGoods.productName,
      mainImage: buyNowGoods.mainImage,
      merchantId: buyNowGoods.merchantId,
      merchantName: buyNowGoods.merchantName,
      specName: buyNowGoods.specName,
      price: buyNowGoods.price,
      quantity: buyNowGoods.quantity,
      selected: 1
    }]
    merchantId.value = buyNowGoods.merchantId
    isDirectBuy.value = true
    cartIds.value = [buyNowGoods.skuId]
    console.log("立即购买商品:", goodsList.value)
  } else {
    // 购物车结算：加载购物车选中商品
    const cart = await cartApi.list();
    goodsList.value = (cart || []).filter(i => i.selected === 1 && (cartIds.value.length === 0 || cartIds.value.includes(i.id)))
    console.log("购物车商品:", goodsList.value)
    if (goodsList.value.length > 0) {
      merchantId.value = goodsList.value[0].merchantId || 1
    }
  }

  // 加载默认地址
  const addrs = await addressApi.list()
  selectedAddress.value = (addrs || []).find(a => a.isDefault === 1) || (addrs && addrs[0]) || null
})

const selectAddress = () => uni.$grouter.navigateTo('addressList', { query: { mode: 'select' } })

const submitOrder = async () => {
  if (!selectedAddress.value) { uni.showToast({ title: '请选择收货地址', icon: 'none' }); return }
  try {
    const submitData = {
      addressId: selectedAddress.value.id,
      remark: remark.value,
      merchantId: merchantId.value
    }
    if (isDirectBuy.value && goodsList.value.length > 0) {
      // 立即购买：直接传递商品信息
      const item = goodsList.value[0]
      submitData['skuId'] = item.id
      submitData['quantity'] = item.quantity
    } else {
      // 购物车结算：传递购物车ID列表
      submitData['cartIds'] = cartIds.value.length > 0 ? cartIds.value : undefined
    }
    const res = await orderApi.submit(submitData)
    // 模拟支付成功
    // await payApi.mockPaySuccess(res.orderId)
    uni.showToast({ title: '下单成功', icon: 'success' })
    setTimeout(() => {
      uni.$grouter.redirectTo('orderList')
    }, 800)
  } catch (e) { console.error(e) }
}
</script>

<style lang="scss" scoped>
.page { background: #f5f9f5; min-height: 100vh; padding-bottom: 120rpx; }
.section { background: #fff; margin: 12rpx 16rpx; border-radius: 16rpx; padding: 20rpx 24rpx; }
.addr-card { .addr-top { display: flex; gap: 16rpx; margin-bottom: 6rpx; } .addr-name { font-size: 30rpx; font-weight: 600; } .addr-phone { font-size: 28rpx; color: #666; } .addr-detail { font-size: 26rpx; color: #999; } }
.addr-empty { color: #4caf50; font-size: 28rpx; padding: 20rpx 0; }
.shop-header { font-size: 28rpx; font-weight: 600; color: #2e3b2e; padding-bottom: 16rpx; border-bottom: 1rpx solid #f0f0f0; margin-bottom: 12rpx; }
.goods-item { display: flex; align-items: center; gap: 16rpx; padding: 12rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.goods-item:last-child { border-bottom: none; }
.goods-img { width: 100rpx; height: 100rpx; border-radius: 10rpx; background: #e8f5e9; }
.goods-info { flex: 1; .goods-name { font-size: 28rpx; font-weight: 500; } .goods-spec { font-size: 24rpx; color: #999; margin-top: 4rpx; } }
.goods-right { text-align: right; .goods-price { font-size: 28rpx; font-weight: 600; color: #333; display: block; } .goods-qty { font-size: 24rpx; color: #999; } }
.row { display: flex; justify-content: space-between; padding: 8rpx 0; font-size: 28rpx; color: #666; }
.row.bold { font-weight: 600; color: #333; font-size: 30rpx; margin-top: 8rpx; padding-top: 12rpx; border-top: 1rpx solid #f0f0f0; }
.remark { background: #fff; margin: 12rpx 16rpx; border-radius: 16rpx; padding: 20rpx 24rpx; input { font-size: 28rpx; } }
.submit-area { position: fixed; bottom: 0; left: 0; right: 0; padding: 16rpx 24rpx; background: #fff; box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.06); }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #ff8f00; color: #fff; border-radius: 44rpx; font-size: 32rpx; border: none; margin: 0; }
</style>

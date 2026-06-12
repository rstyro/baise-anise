<template>
  <view class="page">
    <!-- 店铺头部 -->
    <view class="shop-header">
      <u-icon name="shop" size="22" color="#4caf50" />
      <text class="shop-name">百色田阳自家八角种植园</text>
    </view>

    <!-- 购物车列表 -->
    <view v-if="cartList.length > 0" class="cart-list">
      <u-swipe-action
        v-for="item in cartList"
        :key="item.id"
        :options="delOptions"
        @click="delItem(item)"
      >
        <view class="cart-item">
          <u-checkbox
            :modelValue="item.selected === 1"
            @change="(val) => toggleItem(item, val)"
            shape="circle"
            activeColor="#4caf50"
          />
          <image :src="getImageUrl(item.mainImage)" class="item-img" mode="aspectFill" @click="goProduct(item.productId)" />
          <view class="item-body" @click="goProduct(item.productId)">
            <view class="item-name">{{ item.productName }}</view>
            <view class="item-spec">{{ item.specName }}</view>
            <view class="item-price-row">
              <text class="item-price">¥{{ item.price }}</text>
              <u-number-box
                v-model="item.quantity"
                :min="1"
                :max="item.stock"
                integer
                @change="onQtyChange(item)"
              />
            </view>
          </view>
        </view>
      </u-swipe-action>
    </view>

    <!-- 空购物车 -->
    <view class="empty" v-else>
      <u-empty text="购物车空空如也" mode="list" marginTop="200" />
      <u-button type="primary" shape="circle" text="去逛逛" @click="goShop" :customStyle="{ width: '240rpx', margin: '30rpx auto 0' }" />
    </view>

    <!-- 底部结算栏 -->
    <view class="bottom-bar" v-if="cartList.length > 0">
      <view class="bar-check">
        <u-checkbox :modelValue="allSelected" shape="circle" activeColor="#4caf50" @change="onToggleAll" />
        <text>全选</text>
      </view>
      <view class="bar-right">
        <text class="total-label">合计：</text>
        <text class="total-price">¥{{ totalPrice }}</text>
        <u-button
          type="warning"
          shape="circle"
          :text="'结算(' + checkedCount + ')'"
          :disabled="totalPrice === '0.00'"
          size="small"
          @click="checkout"
        />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { cartApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'

const cartList = ref([])
const delOptions = ref([{ text: '删除', style: { backgroundColor: '#ff4d4f' } }])

const loadCart = async () => {
  try {
    cartList.value = await cartApi.list() || []
  } catch (e) {
    console.error('加载购物车失败', e)
  }
}

const toggleItem = async (item, checked) => {
  item.selected = checked ? 1 : 0
  await cartApi.updateSelected(item.id, item.selected)
}

const onToggleAll = async (checked) => {
  await cartApi.selectAll(checked ? 1 : 0)
  await loadCart()
}

const onQtyChange = async (item) => {
  await cartApi.updateQuantity(item.id, item.quantity)
}

const delItem = async (item) => {
  const res = await uni.showModal({ title: '删除', content: '确定删除该商品吗？' })
  if (res.confirm) {
    await cartApi.delete(item.id)
    await loadCart()
  }
}

const totalPrice = computed(() => {
  const sum = cartList.value.filter(i => i.selected === 1).reduce((s, i) => s + i.price * i.quantity, 0)
  return sum.toFixed(2)
})
const checkedCount = computed(() => cartList.value.filter(i => i.selected === 1).reduce((s, i) => s + i.quantity, 0))
const allSelected = computed(() => cartList.value.length > 0 && cartList.value.every(i => i.selected === 1))

const checkout = () => {
  const selected = cartList.value.filter(i => i.selected === 1)
  if (!selected.length) { uni.showToast({ title: '请选择商品', icon: 'none' }); return }
  const ids = selected.map(i => i.id)
  uni.$grouter.navigateTo('orderConfirm', { query: { cartIds: ids.join(',') } })
}

const goShop = () => uni.$grouter.switchTab('index')

const goProduct = (productId: number) => {
  if (!productId) return
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}

onShow(loadCart)
</script>

<style lang="scss" scoped>
.page { background: #f5f9f5; min-height: 100vh; padding-bottom: 100rpx; }
.shop-header {
  background: #fff; padding: 20rpx 24rpx; display: flex; align-items: center; gap: 10rpx;
  margin-bottom: 8rpx;
  .shop-name { font-size: 28rpx; font-weight: 600; color: #2e3b2e; }
}

.cart-list { padding: 0 16rpx; }
.cart-item {
  background: #fff; border-radius: 16rpx; padding: 20rpx 16rpx;
  display: flex; align-items: center; gap: 12rpx;
  .item-img { width: 140rpx; height: 140rpx; border-radius: 12rpx; background: #e8f5e9; flex-shrink: 0; }
  .item-body { flex: 1; min-width: 0; }
  .item-name {
    font-size: 28rpx; font-weight: 600; color: #2e3b2e; line-height: 1.4;
  }
  .item-spec { font-size: 24rpx; color: #999; margin: 6rpx 0; }
  .item-price-row { display: flex; justify-content: space-between; align-items: center; margin-top: 10rpx; }
  .item-price { font-size: 30rpx; font-weight: 700; color: #ff4d4f; }
}

.empty { padding: 40rpx 0; text-align: center; }

.bottom-bar {
  position: fixed; bottom: var(--window-bottom, 0); left: 0; right: 0;
  height: 100rpx; background: #fff;
  display: flex; align-items: center; padding: 0 20rpx;
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.06); z-index: 99;
  .bar-check { display: flex; align-items: center; gap: 6rpx; font-size: 26rpx; color: #666; }
  .bar-right { flex: 1; display: flex; align-items: center; justify-content: flex-end; gap: 10rpx; }
  .total-label { font-size: 26rpx; color: #333; }
  .total-price { font-size: 34rpx; font-weight: 700; color: #ff4d4f; }
}
</style>

<template>
  <view class="page">
    <view v-if="groupedCartList.length > 0" class="cart-groups">
      <view v-for="group in groupedCartList" :key="group.merchantId" class="cart-group">
        <view class="shop-header">
          <view class="shop-title">
            <view class="shop-icon">
              <u-icon name="shop" size="18" :color="THEME_SUCCESS" />
            </view>
            <text class="shop-name">{{ group.merchantName }}</text>
          </view>
          <text class="shop-count">{{ group.items.length }}件商品</text>
        </view>

        <view class="cart-list">
          <u-swipe-action
            v-for="(item, idx) in group.items"
            :key="item.id"
            :index="idx"
            :options="delOptions"
            @click="onSwipeDelete(item)"
          >
            <view class="cart-item">
              <view class="check-wrap">
                <u-checkbox
                  :modelValue="item.selected === 1"
                  shape="circle"
                  :activeColor="THEME_SUCCESS"
                  @change="onItemCheckedChange(item, $event)"
                />
              </view>
              <image
                :src="getImageUrl(item.mainImage)"
                class="item-img"
                mode="aspectFill"
                @click="goProduct(item.productId)"
              />
              <view class="item-body">
                <view class="item-main" @click="goProduct(item.productId)">
                  <view class="item-name u-line-2">{{ item.productName }}</view>
                  <view v-if="getSpecText(item)" class="item-spec u-line-1">{{ getSpecText(item) }}</view>
                </view>
                <view class="item-action-row">
                  <view class="price-wrap">
                    <text class="price-symbol">¥</text>
                    <text class="item-price">{{ item.price }}</text>
                  </view>
                  <view class="qty-wrap">
                    <u-number-box
                      v-model="item.quantity"
                      :min="1"
                      :max="item.stock"
                      integer
                      :input-width="58"
                      :input-height="44"
                      :size="24"
                      :bg-color="THEME_BG_GREY"
                      @change="onQtyChange(item)"
                    />
                  </view>
                </view>
              </view>
            </view>
          </u-swipe-action>
        </view>
      </view>
    </view>

    <view class="empty" v-else>
      <u-empty text="购物车空空如也" mode="car" marginTop="200" />
      <u-button
        type="primary"
        shape="circle"
        text="去逛逛"
        @click="goShop"
        :customStyle="{ width: '240rpx', margin: '30rpx auto 0' }"
      />
    </view>

    <view class="bottom-bar" v-if="cartList.length > 0">
      <view class="bar-check">
        <u-checkbox :modelValue="allSelected" shape="circle" :activeColor="THEME_SUCCESS" @change="onToggleAll" />
        <text>全选</text>
      </view>
      <view class="bar-right">
        <view class="total-wrap">
          <text class="total-label">合计</text>
          <text class="total-price">¥{{ totalPrice }}</text>
        </view>
        <u-button
          type="primary"
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
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { cartApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import { THEME_SUCCESS, THEME_ERROR, THEME_BG_GREY } from '@/styles/theme'
import type { CartItem } from '@/api/types/product'

const cartList = ref<CartItem[]>([])
const delOptions = ref([{ text: '删除', style: { backgroundColor: THEME_ERROR } }])
const loading = ref(false)

// 按商铺分组
interface CartGroup {
  merchantId: number
  merchantName: string
  items: CartItem[]
}

const groupedCartList = computed<CartGroup[]>(() => {
  const groups: Map<number, CartGroup> = new Map()
  cartList.value.forEach(item => {
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

const loadCart = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const list = await cartApi.list() || []
    // 过滤掉可能的 null/undefined 元素，确保数据完整性
    cartList.value = list.filter(item => item && item.id)
  } catch (e) {
    console.error('加载购物车失败', e)
    cartList.value = []
  } finally {
    loading.value = false
  }
}

const toggleItem = async (item: CartItem, checked: boolean) => {
  if (!item || !item.id) return
  item.selected = checked ? 1 : 0
  await cartApi.updateSelected(item.id, item.selected)
}

const onItemCheckedChange = (item: CartItem, checked: boolean) => {
  toggleItem(item, checked)
}

const onToggleAll = async (checked: boolean) => {
  await cartApi.selectAll(checked ? 1 : 0)
  await loadCart()
}

const onQtyChange = async (item: CartItem) => {
  if (!item || !item.id) return
  await cartApi.updateQuantity(item.id, item.quantity)
}

// 购物车当前只有一个滑动操作按钮，点击即执行删除确认。
const onSwipeDelete = async (item: CartItem) => {
  if (!item) return
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

const getSpecText = (item: CartItem): string => {
  return item.skuSpecs ? formatSkuSpecs(item.skuSpecs) : item.specName
}

// 解析 skuSpecs JSON，提取 value 用逗号隔开
const formatSkuSpecs = (skuSpecs: string): string => {
  if (!skuSpecs || skuSpecs === '{}') return ''
  try {
    const obj = JSON.parse(skuSpecs) as Record<string, unknown>
    const values = Object.values(obj).filter((v): v is string | number => v !== null && v !== undefined && v !== '')
    return values.join(', ')
  } catch (e) {
    return skuSpecs
  }
}

onPullDownRefresh(async () => {
  await loadCart()
  uni.stopPullDownRefresh()
})

onLoad(() => {
  loadCart()
})

onShow(() => {
  const app = getApp() as any
  if (app.globalData?.refreshPages?.cart) {
    loadCart()
    app.globalData.refreshPages.cart = false
  }
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding-bottom: 144rpx;
  background: $uni-bg-color-page;
}

.cart-groups {
  padding: 18rpx 20rpx 0;
}

.cart-group {
  margin-bottom: 20rpx;
  overflow: hidden;
  background: $uni-bg-color;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 18rpx;
  box-shadow: 0 8rpx 24rpx rgba(31, 31, 31, 0.04);
}

.shop-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 22rpx 24rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.shop-title {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 12rpx;
}

.shop-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42rpx;
  height: 42rpx;
  flex-shrink: 0;
  background: $uni-color-success-light;
  border-radius: 50%;
}

.shop-name {
  min-width: 0;
  overflow: hidden;
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 40rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-count {
  flex-shrink: 0;
  color: $uni-text-color-grey;
  font-size: 24rpx;
  line-height: 34rpx;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 20rpx;
  background: $uni-bg-color;

  & + .cart-item {
    border-top: 1rpx solid $uni-border-color-light;
  }
}

.check-wrap {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.item-img {
  width: 150rpx;
  height: 150rpx;
  flex-shrink: 0;
  background: $uni-bg-color-grey;
  border-radius: 14rpx;
}

.item-body {
  display: flex;
  flex: 1;
  min-width: 0;
  min-height: 150rpx;
  flex-direction: column;
  justify-content: space-between;
}

.item-main {
  min-width: 0;
}

.item-name {
  color: $uni-text-color;
  font-size: 28rpx;
  font-weight: 600;
  line-height: 38rpx;
}

.item-spec {
  display: inline-block;
  max-width: 100%;
  margin-top: 10rpx;
  padding: 6rpx 12rpx;
  color: $uni-text-color-grey;
  font-size: 22rpx;
  line-height: 30rpx;
  background: $uni-bg-color-grey;
  border-radius: 8rpx;
}

.item-action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-top: 14rpx;
}

.price-wrap {
  display: flex;
  align-items: baseline;
  min-width: 0;
  color: $uni-color-error;
}

.price-symbol {
  font-size: 22rpx;
  font-weight: 600;
}

.item-price {
  font-size: 34rpx;
  font-weight: 700;
  line-height: 42rpx;
}

.qty-wrap {
  flex-shrink: 0;
}

.empty {
  padding: 40rpx 0;
  text-align: center;
}

.bottom-bar {
  position: fixed;
  bottom: var(--window-bottom, 0);
  left: 0;
  right: 0;
  z-index: 99;
  display: flex;
  align-items: center;
  height: 112rpx;
  padding: 0 20rpx;
  background: $uni-bg-color;
  box-shadow: 0 -8rpx 24rpx rgba(31, 31, 31, 0.08);
}

.bar-check {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  gap: 8rpx;
  color: $uni-text-color-secondary;
  font-size: 26rpx;
}

.bar-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex: 1;
  min-width: 0;
  gap: 18rpx;
}

.total-wrap {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  min-width: 0;
  gap: 8rpx;
}

.total-label {
  color: $uni-text-color-secondary;
  font-size: 24rpx;
}

.total-price {
  color: $uni-color-error;
  font-size: 34rpx;
  font-weight: 700;
  line-height: 42rpx;
}
</style>

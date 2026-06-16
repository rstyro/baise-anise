<template>
  <view class="page">
    <view class="header-bg">
      <view class="header-content">
        <image :src="getImageUrl(merchantInfo.logoUrl)" class="logo" mode="aspectFill" />
        <view class="header-info">
          <view class="merchant-name">{{ merchantInfo.merchantName }}</view>
          <view class="product-count">共 {{ totalProducts }} 件商品</view>
        </view>
      </view>
    </view>

    <view class="u-wrap">
      <view class="u-search-box">
        <u-search
          v-model="searchText"
          placeholder="搜索商品"
          shape="round"
          bg-color="#f5f9f5"
          :show-action="searchText.length > 0"
          action-text="搜索"
          @search="handleSearch"
          @custom="handleSearch"
          @clear="handleSearchClear"
        ></u-search>
      </view>

      <view class="u-menu-wrap">
        <scroll-view
          scroll-y
          scroll-with-animation
          class="u-tab-view menu-scroll-view"
          :scroll-top="scrollTop"
          :scroll-into-view="itemId"
        >
          <view
            v-for="(item, index) in categoryList"
            :key="index"
            class="u-tab-item"
            :class="[current == index ? 'u-tab-item-active' : '']"
            @tap.stop="switchMenu(index)"
          >
            <text class="u-line-1">{{ item.name }}</text>
          </view>
        </scroll-view>

        <scroll-view
          :scroll-top="scrollRightTop"
          scroll-y
          scroll-with-animation
          class="right-box"
          @scroll="rightScroll"
        >
          <view class="page-view">
            <view class="class-item" :id="'item' + index" v-for="(item, index) in categoryList" :key="index">
              <view class="item-title">
                <text>{{ item.name }}</text>
              </view>
              <view class="item-container" v-if="item.products && item.products.length > 0">
                <view
                  class="product-card"
                  :class="{ 'product-card--match': isMatch(product) }"
                  v-for="product in item.products"
                  :key="product.id"
                  @click="goProductDetail(product.id)"
                >
                  <image class="product-image" :src="getImageUrl(product.mainImage)" mode="widthFix" />
                  <view class="product-info">
                    <view class="product-name" :class="{ 'product-name--match': isMatch(product) }">
                      {{ product.productName }}
                    </view>
                    <view class="product-price-row">
                      <text class="product-price">¥{{ product.minPrice }}</text>
                      <text class="product-sales">已售{{ product.sales }}</text>
                    </view>
                    <view class="product-tags" v-if="product.spuAttrs && product.spuAttrs.length > 0">
                      <u-tag
                        v-for="attr in getDisplayAttrs(product)"
                        :key="attr.attrId"
                        :text="attr.attrValue"
                        type="info"
                        size="mini"
                        plain
                      />
                    </view>
                  </view>
                </view>
              </view>
              <view class="empty-content" v-else>
                <u-empty text="暂无商品" mode="list" />
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="bar-btn" @click="goMerchantDetail">
        <u-icon name="shop" size="32" color="#4caf50" />
        <text>店铺首页</text>
      </view>
      <view class="bar-btn primary" @click="goCart">
        <u-icon name="shopping-cart" size="32" color="#fff" />
        <text>购物车</text>
      </view>
    </view>

    <u-toast ref="toastRef" />
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, getCurrentInstance, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { merchantApi } from '@/api/merchantApi'
import { getImageUrl } from '@/utils/image'
import type { ProductItem, SpuAttrItem } from '@/api/types/product'

interface CategoryItem {
  name: string
  products: ProductItem[]
}

const merchantId = ref<number>(0)
const merchantInfo = ref({
  merchantName: '',
  logoUrl: '',
})
const totalProducts = ref(0)
const allProducts = ref<ProductItem[]>([])
const searchText = ref('')
const keyword = computed(() => searchText.value.trim().toLowerCase())

const categoryList = computed<CategoryItem[]>(() => {
  const filtered = keyword.value
    ? allProducts.value.filter(p => p.productName.toLowerCase().includes(keyword.value))
    : allProducts.value

  const categoryMap = new Map<string, ProductItem[]>()
  filtered.forEach(product => {
    const catName = product.categoryName || '其他'
    if (!categoryMap.has(catName)) {
      categoryMap.set(catName, [])
    }
    categoryMap.get(catName)!.push(product)
  })

  const result: CategoryItem[] = []
  categoryMap.forEach((products, name) => {
    result.push({ name, products })
  })

  return result.length > 0 ? result : [{ name: '全部', products: [] }]
})
// 左侧菜单滚动条位置
const scrollTop = ref(0)
// 记录上次右侧滚动条位置
const oldScrollTop = ref(0)
// 当前选中菜单下标
const current = ref(0)
// 左侧菜单整体高度
const menuHeight = ref(0)
// 单个菜单项高度
const menuItemHeight = ref(0)
// 右侧scroll-view用于滚动的id
const itemId = ref('')
// 右侧每个item到顶部的距离
const arr = ref<number[]>([])
// 右侧scroll-view的滚动条高度
const scrollRightTop = ref(0)
// 节流定时器
const timer = ref<ReturnType<typeof setTimeout> | null>(null)

const instance = getCurrentInstance()

onLoad((options) => {
  const id = Number(options.id)
  if (id) {
    merchantId.value = id
    loadMerchantInfo(id)
    loadProducts(id)
  }
})

onMounted(() => {
  getMenuItemTop()
})

const loadMerchantInfo = async (id: number) => {
  try {
    const res = await merchantApi.detail(id)
    merchantInfo.value = {
      merchantName: res.merchantName,
      logoUrl: res.logoUrl,
    }
  } catch (e) {
    console.error('加载商家信息失败', e)
  }
}

const loadProducts = async (id: number) => {
  try {
    const res = await merchantApi.productList(id, 1, 100)
    allProducts.value = res.records || []
    totalProducts.value = res.total || 0
    await nextTick()
    getMenuItemTop()
  } catch (e) {
    console.error('加载商品列表失败', e)
  }
}

const handleSearch = () => {
  if (!keyword.value) return
  const idx = categoryList.value.findIndex(item =>
    (item.products || []).some(product => product.productName.toLowerCase().includes(keyword.value))
  )
  if (idx !== -1) {
    switchMenu(idx)
  } else {
    uni.showToast({ title: '没有找到该商品', icon: 'none' })
  }
}

const handleSearchClear = () => {
  searchText.value = ''
  current.value = 0
  scrollRightTop.value = 0
}

const isMatch = (product: ProductItem) => {
  if (!keyword.value) return false
  return product.productName.toLowerCase().includes(keyword.value)
}

const getDisplayAttrs = (product: ProductItem) => {
  return product.spuAttrs?.filter(attr => attr.attrName !== '无硫').slice(0, 2) || []
}

const switchMenu = async (index: number) => {
  if (!categoryList.value.length) return
  if (arr.value.length === 0) {
    await getMenuItemTop()
  }
  if (index === current.value) return
  scrollRightTop.value = oldScrollTop.value
  await nextTick()
  scrollRightTop.value = arr.value[index]
  current.value = index
  leftMenuStatus(index)
}

const getElRect = (elClass: string, dataVal: 'menuHeight' | 'menuItemHeight'): Promise<void> => {
  return new Promise<void>(resolve => {
    const query = uni.createSelectorQuery().in(instance?.proxy!)
    query
      .select('.' + elClass)
      .fields({ size: true }, (res: any) => {
        const height = Array.isArray(res) ? res[0]?.height : res?.height
        if (typeof height !== 'number') {
          setTimeout(() => {
            getElRect(elClass, dataVal)
          }, 10)
          return
        }
        if (dataVal === 'menuHeight') menuHeight.value = height
        if (dataVal === 'menuItemHeight') menuItemHeight.value = height
        resolve()
      })
      .exec()
  })
}

const leftMenuStatus = async (index: number) => {
  current.value = index
  if (menuHeight.value === 0 || menuItemHeight.value === 0) {
    await getElRect('menu-scroll-view', 'menuHeight')
    await getElRect('u-tab-item', 'menuItemHeight')
  }
  scrollTop.value = index * menuItemHeight.value + menuItemHeight.value / 2 - menuHeight.value / 2
}

const getMenuItemTop = (): Promise<void> => {
  return new Promise<void>(resolve => {
    const selectorQuery = uni.createSelectorQuery().in(instance?.proxy!)
    selectorQuery
      .selectAll('.class-item')
      .boundingClientRect((rects: any) => {
        if (!rects || !Array.isArray(rects) || !rects.length) {
          setTimeout(() => {
            getMenuItemTop()
          }, 10)
          return
        }
        arr.value = []
        rects.forEach((rect: any) => {
          arr.value.push(rect.top - rects[0].top)
        })
        resolve()
      })
      .exec()
  })
}

const rightScroll = async (e: { detail: { scrollTop: number } }) => {
  oldScrollTop.value = e.detail.scrollTop
  if (arr.value.length === 0) {
    await getMenuItemTop()
  }
  if (timer.value) return
  if (!menuHeight.value) {
    await getElRect('menu-scroll-view', 'menuHeight')
  }
  timer.value = setTimeout(() => {
    timer.value = null
    const scrollHeight = e.detail.scrollTop + menuHeight.value / 2
    for (let i = 0; i < arr.value.length; i++) {
      const height1 = arr.value[i]
      const height2 = arr.value[i + 1]
      if (!height2 || (scrollHeight >= height1 && scrollHeight < height2)) {
        leftMenuStatus(i)
        return
      }
    }
  }, 10)
}

const goProductDetail = (productId: number) => {
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}

const goMerchantDetail = () => {
  uni.$grouter.navigateTo('merchantDetail', { query: { id: merchantId.value } })
}

const goCart = () => {
  uni.$grouter.switchTab('cart')
}
</script>

<style lang="scss" scoped>
.page {
  background: #f5f9f5;
  min-height: 100vh;
  padding-bottom: 140rpx;
}

.header-bg {
  background: linear-gradient(135deg, #15803D 0%, #22C55E 100%);
  padding: 60rpx 30rpx 30rpx;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #fff;
  padding: 4rpx;
}

.header-info {
  flex: 1;
}

.merchant-name {
  font-size: 32rpx;
  font-weight: 700;
  color: #fff;
}

.product-count {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
  margin-top: 6rpx;
}

.u-wrap {
  height: calc(100vh - 280rpx);
  display: flex;
  flex-direction: column;
}

.u-search-box {
  padding: 18rpx 30rpx;
  background: #fff;
}

.u-menu-wrap {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.u-tab-view {
  width: 200rpx;
  height: 100%;
}

.u-tab-item {
  height: 110rpx;
  background: #f5f9f5;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  color: #666;
  font-weight: 400;
  line-height: 1.4;
  padding: 0 10rpx;
}

.u-tab-item-active {
  position: relative;
  color: #15803D;
  font-size: 28rpx;
  font-weight: 600;
  background-color: #fff;
}

.u-tab-item-active::before {
  content: '';
  position: absolute;
  border-left: 4rpx solid #4caf50;
  height: 32rpx;
  left: 0;
  top: 39rpx;
}

.right-box {
  flex: 1;
  background-color: #fff;
}

.page-view {
  padding: 16rpx;
}

.class-item {
  margin-bottom: 30rpx;
}

.item-title {
  font-size: 28rpx;
  color: #2e3b2e;
  font-weight: 600;
  padding: 10rpx 0;
  border-left: 6rpx solid #4caf50;
  padding-left: 12rpx;
  margin-bottom: 16rpx;
}

.item-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.product-card {
  width: calc(50% - 6rpx);
  background: #fafafa;
  border-radius: 12rpx;
  overflow: hidden;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;
  &--match {
    border-color: #4caf50;
    background: rgba(76,175,80,0.05);
  }
}

.product-image {
  width: 100%;
  background: #e8f5e9;
}

.product-info {
  padding: 12rpx;
}

.product-name {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-height: 1.4;
  margin-bottom: 8rpx;
  &--match {
    color: #4caf50;
    font-weight: 600;
  }
}

.product-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.product-price {
  font-size: 30rpx;
  font-weight: 700;
  color: #ff4d4f;
}

.product-sales {
  font-size: 22rpx;
  color: #999;
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6rpx;
}

.empty-content {
  padding: 40rpx 0;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 16rpx 20rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08);
  gap: 20rpx;
}

.bar-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  background: #f0fdf4;
  color: #4caf50;
  border-radius: 44rpx;
  font-size: 30rpx;
  font-weight: 600;
  &.primary {
    background: linear-gradient(135deg, #15803D 0%, #22C55E 100%);
    color: #fff;
  }
}
</style>
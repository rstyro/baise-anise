<template>
  <view class="page">
    <!-- 顶部Banner轮播 -->
    <u-swiper v-if="bannerList.length" :list="bannerList" name="imageUrl" indicator indicatorActiveColor="#4caf50" radius="0" height="320" />

    <!-- 分类导航 -->
    <view class="category-section">
      <scroll-view scroll-x class="cat-scroll" :show-scrollbar="false">
        <view class="cat-row">
          <view
            v-for="cat in categoryList"
            :key="cat.id"
            class="cat-item"
            @click="switchCategory(cat.id)"
          >
            <view class="cat-icon-wrap" :class="{ active: activeCatId === cat.id }">
              <image
                v-if="cat.categoryIcon && !cat._imgErr"
                :src="getImageUrl(cat.categoryIcon)"
                class="cat-img"
                mode="aspectFill"
                @error="cat._imgErr = true"
              />
              <u-icon
                v-else
                :name="cat.icon"
                size="28"
                :color="activeCatId === cat.id ? '#fff' : '#7B9E85'"
              />
            </view>
            <text class="cat-label" :class="{ active: activeCatId === cat.id }">{{ cat.categoryName }}</text>
            <view v-if="activeCatId === cat.id" class="cat-bar" />
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 搜索栏 -->
    <view class="search-bar">
      <u-search
        v-model="keyword"
        placeholder="搜索八角、花椒、水果..."
        shape="round"
        bg-color="#f5f9f5"
        :show-action="keyword.length > 0"
        action-text="搜索"
        @search="onSearch"
        @custom="onSearch"
        @clear="onSearchClear"
      />
    </view>

    <!-- 商品瀑布流 -->
    <u-waterfall ref="waterfallRef" v-model="products" idKey="id" :addTime="100">
      <template #left="{ leftList }">
        <view class="wf-card" v-for="item in leftList" :key="item.id" @click="goDetail(item.id)">
          <image :src="getImageUrl(item.mainImage)" class="wf-img" mode="widthFix" />
          <view class="wf-body">
            <view class="wf-name">{{ item.productName }}</view>
            <view class="wf-tags">
              <u-tag v-if="item.isSulfurFree" text="无硫" type="success" size="mini" />
              <u-tag v-if="item.dryingLevel" :text="item.dryingLevel" type="info" size="mini" plain />
              <u-tag v-if="item.originPlace" :text="item.originPlace" type="warning" size="mini" plain />
            </view>
            <view class="wf-price-row">
              <text class="wf-price">¥{{ item.minPrice }}</text>
              <text class="wf-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
            </view>
            <text class="wf-sales">已售 {{ item.sales }}</text>
          </view>
        </view>
      </template>
      <template #right="{ rightList }">
        <view class="wf-card" v-for="item in rightList" :key="item.id" @click="goDetail(item.id)">
          <image :src="getImageUrl(item.mainImage)" class="wf-img" mode="widthFix" />
          <view class="wf-body">
            <view class="wf-name">{{ item.productName }}</view>
            <view class="wf-tags">
              <u-tag v-if="item.isSulfurFree" text="无硫" type="success" size="mini" />
              <u-tag v-if="item.dryingLevel" :text="item.dryingLevel" type="info" size="mini" plain />
              <u-tag v-if="item.originPlace" :text="item.originPlace" type="warning" size="mini" plain />
            </view>
            <view class="wf-price-row">
              <text class="wf-price">¥{{ item.minPrice }}</text>
              <text class="wf-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
            </view>
            <text class="wf-sales">已售 {{ item.sales }}</text>
          </view>
        </view>
      </template>
    </u-waterfall>

    <u-empty v-if="!loading && products.length === 0" text="暂无商品" mode="list" marginTop="120" />

    <view class="load-more" v-if="products.length > 0">
      <u-loadmore :status="loadStatus" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onReachBottom, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { productApi } from '@/api/productApi'
import { getImageUrl } from '@/utils/image'

// Banner
const bannerList = ref([])

// 加载Banner
const loadBanners = async () => {
  try {
    const list = await productApi.bannerList() || []
    bannerList.value = list.map(b => ({ ...b, imageUrl: getImageUrl(b.imageUrl) }))
  } catch (e) { console.error('Banner加载失败', e) }
}

// 分类
const categories = ref([])
const activeCatId = ref(0)

// 搜索
const keyword = ref('')

// 商品
const products = ref([])
const waterfallRef = ref()
const currentPage = ref(1)
const totalPages = ref(0)
const loading = ref(false)
const loadStatus = ref('loadmore')
const PAGE_SIZE = 10  // 每页条数，与后端 DEFAULT_PAGE_SIZE 一致

// 分类列表（含"全部"）
const categoryList = computed(() => {
  const iconMap = { '八角干货': 'star-fill', '花椒香料': 'fire', '时令水果': 'gift' }
  const list = categories.value.map((c) => ({
    ...c,
    icon: iconMap[c.categoryName] || 'grid'
  }))
  return [{ id: 0, categoryName: '全部', icon: 'home' }, ...list]
})

// 加载分类
const loadCategories = async () => {
  try { categories.value = await productApi.categoryList() || [] } catch (e) { console.error(e) }
}

// 加载商品
const loadProducts = async (isRefresh = false) => {
  if (loading.value) return
  if (!isRefresh && totalPages.value > 0 && currentPage.value >= totalPages.value ) return

  loading.value = true
  loadStatus.value = isRefresh ? 'loadmore' : 'loading'

  try {
    const params: any = {
      pageNum: currentPage.value,
      pageSize: PAGE_SIZE,
    }
    if (keyword.value) params.keyword = keyword.value
    if (activeCatId.value > 0) params.categoryId = activeCatId.value

    const res = await productApi.list(params)
    if (isRefresh) {
      products.value = res.records || []
    } else {
      products.value = [...products.value, ...(res.records || [])]
    }
    currentPage.value = res.current + 1
    totalPages.value = res.pages
    loadStatus.value = currentPage.value > totalPages.value ? 'nomore' : 'loadmore'
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const switchCategory = (catId) => {
  activeCatId.value = catId
  waterfallRef.value?.clear()
  currentPage.value = 1
  loadProducts(true)
}

const onSearch = () => {
  waterfallRef.value?.clear()
  currentPage.value = 1
  loadProducts(true)
}

const onSearchClear = () => {
  keyword.value = ''
  waterfallRef.value?.clear()
  currentPage.value = 1
  loadProducts(true)
}

const goDetail = (productId) => {
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}

onReachBottom(() => loadProducts(false))
onPullDownRefresh(() => {
  waterfallRef.value?.clear()
  currentPage.value = 1
  loadProducts(true).finally(() => uni.stopPullDownRefresh())
})

// 初始化加载
const init = () => {
  loadBanners()
  loadCategories()
  loadProducts(true)
}

onMounted(init)

onShow(() => {
  // 检查是否需要刷新（登录后返回场景）
  const app = getApp() as any
  if (app.globalData?.refreshPages?.index) {
    init()
    // 重置刷新标记
    app.globalData.refreshPages.index = false
  }
})
</script>

<style lang="scss" scoped>
.page { background: #f5f9f5; min-height: 100vh; padding-bottom: 20rpx; }

.category-section {
  background: #fff; padding: 24rpx 0 12rpx; margin-bottom: 8rpx;
  .cat-scroll { white-space: nowrap; }
  .cat-row { display: inline-flex; padding: 0 16rpx; gap: 8rpx; }
  .cat-item {
    display: inline-flex; flex-direction: column; align-items: center;
    width: 140rpx; flex-shrink: 0; position: relative; padding-bottom: 6rpx;
  }
  .cat-icon-wrap {
    width: 96rpx; height: 96rpx; border-radius: 50%;
    background: #F0FDF4; overflow: hidden;
    display: flex; align-items: center; justify-content: center;
    margin-bottom: 10rpx;
    transition: all 0.2s ease;
    box-shadow: 0 4rpx 12rpx rgba(21, 128, 61, 0.06);
    .cat-img { width: 100%; height: 100%; }
    &.active {
      background: #15803D;
      box-shadow: 0 6rpx 20rpx rgba(21, 128, 61, 0.25);
    }
  }
  .cat-label {
    font-size: 24rpx; color: #6B7280; white-space: nowrap;
    transition: all 0.2s ease;
    &.active { color: #15803D; font-weight: 600; }
  }
  .cat-bar {
    position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
    width: 32rpx; height: 4rpx; border-radius: 2rpx; background: #15803D;
  }
}

.search-bar { padding: 12rpx 20rpx; background: #fff; margin-bottom: 8rpx; }

.wf-card {
  background: #fff; border-radius: 16rpx; overflow: hidden; margin: 6rpx;
  box-shadow: 0 2rpx 12rpx rgba(76, 175, 80, 0.06);
  .wf-img { width: 100%; display: block; background: #e8f5e9; }
  .wf-body { padding: 16rpx 20rpx 20rpx; }
  .wf-name {
    font-size: 28rpx; font-weight: 600; color: #2e3b2e; line-height: 1.4;
    overflow: hidden; text-overflow: ellipsis;
    display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2;
    margin-bottom: 8rpx;
  }
  .wf-tags { display: flex; flex-wrap: wrap; gap: 6rpx; margin-bottom: 10rpx; }
  .wf-price-row { display: flex; align-items: baseline; gap: 8rpx; margin-bottom: 6rpx; }
  .wf-price { font-size: 32rpx; font-weight: 700; color: #ff4d4f; }
  .wf-original { font-size: 22rpx; color: #999; text-decoration: line-through; }
  .wf-sales { font-size: 22rpx; color: #999; }
}

.load-more { padding: 20rpx 0; }
</style>

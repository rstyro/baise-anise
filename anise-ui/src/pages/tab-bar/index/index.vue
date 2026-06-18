<template>
  <view class="page">
    <view class="banner-section" v-if="bannerList.length">
      <u-swiper
        :list="bannerList"
        name="imageUrl"
        indicator
        indicatorActiveColor="#52c41a"
        radius="12"
        height="350"
      />
    </view>

    <view class="search-section">
      <u-search
        v-model="keyword"
        placeholder="搜索八角、花椒、水果..."
        shape="round"
        bg-color="#ffffff"
        :show-action="keyword.length > 0"
        action-text="搜索"
        @search="onSearch"
        @custom="onSearch"
        @clear="onSearchClear"
      />
    </view>

    <view class="category-section">
      <view class="section-head">
        <view>
          <text class="section-title">商品分类</text>
          <text class="section-subtitle">{{ activeCategoryName }}</text>
        </view>
        <view class="section-count" v-if="categoryList.length > 1">{{ categoryList.length - 1 }} 类</view>
      </view>

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
                size="30"
                :color="activeCatId === cat.id ? '#ffffff' : '#52c41a'"
              />
            </view>
            <text class="cat-label" :class="{ active: activeCatId === cat.id }">{{ cat.categoryName }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="product-section">
      <view class="section-head product-head">
        <view>
          <text class="section-title">{{ keyword ? '搜索结果' : '精选商品' }}</text>
          <text class="section-subtitle">{{ productSectionDesc }}</text>
        </view>
      </view>

      <u-waterfall ref="waterfallRef" v-model="products" idKey="id" :addTime="100">
        <template #left="{ leftList }">
          <view class="wf-card" v-for="item in leftList" :key="item.id" @click="goDetail(item.id)">
            <image :src="getImageUrl(item.mainImage)" class="wf-img" mode="aspectFill" />
            <view class="wf-body">
              <view class="wf-name">{{ item.productName }}</view>
              <view class="wf-tags">
                <u-tag v-if="getSulfurFreeAttr(item)" text="无硫" type="success" size="mini" />
                <u-tag
                  v-for="attr in getDisplayAttrs(item)"
                  :key="attr.attrId"
                  :text="attr.attrValue"
                  type="info"
                  size="mini"
                  plain
                />
              </view>
              <view class="wf-foot">
                <view class="wf-price-row">
                  <text class="wf-price">¥{{ item.minPrice }}</text>
                  <text class="wf-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
                </view>
                <text class="wf-sales">已售 {{ item.sales || 0 }}</text>
              </view>
            </view>
          </view>
        </template>
        <template #right="{ rightList }">
          <view class="wf-card" v-for="item in rightList" :key="item.id" @click="goDetail(item.id)">
            <image :src="getImageUrl(item.mainImage)" class="wf-img" mode="aspectFill" />
            <view class="wf-body">
              <view class="wf-name">{{ item.productName }}</view>
              <view class="wf-tags">
                <u-tag v-if="getSulfurFreeAttr(item)" text="无硫" type="success" size="mini" />
                <u-tag
                  v-for="attr in getDisplayAttrs(item)"
                  :key="attr.attrId"
                  :text="attr.attrValue"
                  type="info"
                  size="mini"
                  plain
                />
              </view>
              <view class="wf-foot">
                <view class="wf-price-row">
                  <text class="wf-price">¥{{ item.minPrice }}</text>
                  <text class="wf-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
                </view>
                <text class="wf-sales">已售 {{ item.sales || 0 }}</text>
              </view>
            </view>
          </view>
        </template>
      </u-waterfall>

      <view v-if="!loading && products.length === 0" class="empty-wrap">
        <u-empty text="暂无商品" mode="list" marginTop="80" />
      </view>

      <view class="load-more" v-if="products.length > 0">
        <u-loadmore :status="loadStatus" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onReachBottom, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { productApi } from '@/api/productApi'
import type { ProductItem, ProductListParams } from '@/api/types/product'
import { getImageUrl } from '@/utils/image'

interface BannerItem {
  imageUrl: string
  [key: string]: any
}

interface CategoryItem {
  id: number
  categoryName: string
  categoryIcon?: string
  icon?: string
  _imgErr?: boolean
  [key: string]: any
}

const bannerList = ref<BannerItem[]>([])

const loadBanners = async () => {
  try {
    const list = await productApi.bannerList() || []
    bannerList.value = list.map((b: BannerItem) => ({ ...b, imageUrl: getImageUrl(b.imageUrl) }))
  } catch (e) { console.error('Banner加载失败', e) }
}

const categories = ref<CategoryItem[]>([])
const activeCatId = ref(0)

const keyword = ref('')

const products = ref<ProductItem[]>([])
const waterfallRef = ref()
const currentPage = ref(1)
const totalPages = ref(0)
const loading = ref(false)
const loadStatus = ref('loadmore')
const PAGE_SIZE = 10

const categoryList = computed<CategoryItem[]>(() => {
  const iconMap: Record<string, string> = { '八角干货': 'star-fill', '花椒香料': 'fire', '时令水果': 'gift' }
  const list = categories.value.map((c) => ({
    ...c,
    icon: iconMap[c.categoryName] || 'grid'
  }))
  return [{ id: 0, categoryName: '全部', icon: 'home' }, ...list]
})

const activeCategoryName = computed(() => {
  return categoryList.value.find((item) => item.id === activeCatId.value)?.categoryName || '全部'
})

const productSectionDesc = computed(() => {
  if (keyword.value) return `关键词：${keyword.value}`
  return activeCatId.value > 0 ? activeCategoryName.value : '为你推荐'
})

const loadCategories = async () => {
  try { categories.value = await productApi.categoryList() || [] } catch (e) { console.error(e) }
}

const loadProducts = async (isRefresh = false) => {
  if (loading.value) return
  if (!isRefresh && totalPages.value > 0 && currentPage.value > totalPages.value) return

  loading.value = true
  loadStatus.value = isRefresh ? 'loadmore' : 'loading'

  try {
    const params: ProductListParams = {
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

const getSulfurFreeAttr = (item: ProductItem) => {
  return item.spuAttrs?.some(attr => attr.attrName === '无硫' && attr.attrValue === '是')
}

const getDisplayAttrs = (item: ProductItem) => {
  return item.spuAttrs?.filter(attr => attr.attrName !== '无硫').slice(0, 2) || []
}

const switchCategory = (catId: number) => {
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

const goDetail = (productId: number) => {
  uni.$grouter.navigateTo('productDetail', { query: { id: productId } })
}

onReachBottom(() => loadProducts(false))
onPullDownRefresh(() => {
  waterfallRef.value?.clear()
  currentPage.value = 1
  loadProducts(true).finally(() => uni.stopPullDownRefresh())
})

const init = () => {
  loadBanners()
  loadCategories()
  loadProducts(true)
}

onMounted(init)

onShow(() => {
  const app = getApp() as any
  if (app.globalData?.refreshPages?.index) {
    init()
    app.globalData.refreshPages.index = false
  }
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 20rpx 0 28rpx;
  background: $uni-bg-color-page;
}

.banner-section {
  padding: 0 24rpx 18rpx;
}

.search-section {
  position: sticky;
  top: 0;
  z-index: 8;
  padding: 18rpx 24rpx;
  background: rgba($uni-bg-color-page, 0.96);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24rpx 18rpx;
}

.section-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  line-height: 42rpx;
  color: $uni-text-color;
}

.section-subtitle {
  display: block;
  margin-top: 6rpx;
  font-size: 24rpx;
  line-height: 30rpx;
  color: $uni-text-color-grey;
}

.section-count {
  flex-shrink: 0;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  line-height: 28rpx;
  color: $uni-color-success;
  background: rgba($uni-color-success, 0.1);
}

.category-section {
  padding: 18rpx 0 22rpx;
  margin-bottom: 14rpx;
  background: $uni-bg-color;

  .cat-scroll {
    white-space: nowrap;
  }

  .cat-row {
    display: inline-flex;
    padding: 0 18rpx;
    gap: 12rpx;
  }

  .cat-item {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    width: 132rpx;
    flex-shrink: 0;
    position: relative;
    padding: 6rpx 0;
  }

  .cat-icon-wrap {
    width: 92rpx;
    height: 92rpx;
    border-radius: 28rpx;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10rpx;
    background: rgba($uni-color-success, 0.1);
    border: 2rpx solid rgba($uni-color-success, 0.08);
    transition: all 0.2s ease;

    .cat-img {
      width: 100%;
      height: 100%;
    }

    &.active {
      background: $uni-color-success;
      border-color: $uni-color-success;
      box-shadow: 0 10rpx 22rpx rgba($uni-color-success, 0.22);
    }
  }

  .cat-label {
    max-width: 120rpx;
    font-size: 24rpx;
    line-height: 30rpx;
    color: $uni-text-color-secondary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: all 0.2s ease;

    &.active {
      color: $uni-color-success;
      font-weight: 700;
    }
  }
}

.product-section {
  padding: 10rpx 18rpx 0;
}

.product-head {
  padding-right: 6rpx;
  padding-left: 6rpx;
}

.wf-card {
  margin: 8rpx;
  overflow: hidden;
  border-radius: 16rpx;
  background: $uni-bg-color;
  box-shadow: 0 8rpx 24rpx rgba($uni-text-color, 0.06);

  .wf-img {
    display: block;
    width: 100%;
    height: 320rpx;
    background: rgba($uni-color-success, 0.08);
  }

  .wf-body {
    padding: 18rpx 18rpx 20rpx;
  }

  .wf-name {
    min-height: 78rpx;
    margin-bottom: 12rpx;
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    font-size: 28rpx;
    font-weight: 600;
    line-height: 39rpx;
    color: $uni-text-color;
  }

  .wf-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8rpx;
    min-height: 36rpx;
    margin-bottom: 14rpx;
  }

  .wf-foot {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  .wf-price-row {
    display: flex;
    align-items: baseline;
    gap: 8rpx;
    min-width: 0;
  }

  .wf-price {
    font-size: 34rpx;
    font-weight: 800;
    line-height: 40rpx;
    color: $uni-color-error;
  }

  .wf-original {
    max-width: 120rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 22rpx;
    line-height: 28rpx;
    color: $uni-text-color-grey;
    text-decoration: line-through;
  }

  .wf-sales {
    font-size: 22rpx;
    line-height: 28rpx;
    color: $uni-text-color-grey;
  }
}

.empty-wrap {
  padding: 20rpx 0 80rpx;
}

.load-more {
  padding: 24rpx 0 12rpx;
}
</style>

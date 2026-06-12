<template>
  <view class="page">
    <!-- 商品轮播图 -->
    <swiper class="swiper" :autoplay="true" indicator-dots indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#4caf50">
      <swiper-item v-for="(img, idx) in (detail.imageList && detail.imageList.length ? detail.imageList : [detail.mainImage])" :key="idx">
        <image :src="getImageUrl(img)" class="swiper-img" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <!-- 商品基本信息 -->
    <view class="info-section">
      <view class="price-row">
        <text class="price">¥{{ selectedSku ? selectedSku.price : '--' }}</text>
        <text class="original" v-if="selectedSku && selectedSku.originalPrice">¥{{ selectedSku.originalPrice }}</text>
        <text class="sales">已售 {{ detail.sales || 0 }}</text>
      </view>
      <view class="name">{{ detail.productName }}</view>
      <view class="title" v-if="detail.productTitle">{{ detail.productTitle }}</view>

      <!-- 标签 -->
      <view class="tags-row">
        <text class="tag-green" v-if="detail.isSulfurFree">无硫</text>
        <text class="tag-outline" v-if="detail.dryingLevel">{{ detail.dryingLevel }}</text>
        <text class="tag-outline" v-if="detail.plantingProcess">{{ detail.plantingProcess }}</text>
      </view>
    </view>

    <!-- SKU选择 -->
    <view class="sku-section">
      <view class="section-title">选择规格</view>
      <view class="sku-list">
        <view
          v-for="sku in detail.skuList"
          :key="sku.id"
          class="sku-item"
          :class="{ active: selectedSku && selectedSku.id === sku.id, disabled: sku.stock <= 0 }"
          @click="selectSku(sku)"
        >
          <text class="sku-name">{{ sku.specName }}</text>
          <text class="sku-price">¥{{ sku.price }}</text>
          <text class="sku-stock" v-if="sku.stock <= 0">售罄</text>
          <text class="sku-stock-low" v-else-if="sku.stock < 10">仅剩{{ sku.stock }}件</text>
        </view>
      </view>
    </view>

    <!-- 数量选择 -->
    <view class="qty-section">
      <text class="section-title">购买数量</text>
      <u-number-box v-model="quantity" :min="1" :max="selectedSku ? selectedSku.stock : 99" integer />
    </view>

    <!-- 商品参数 -->
    <view class="param-section">
      <view class="section-title">商品参数</view>
      <view class="param-item" v-if="detail.originPlace">
        <text class="param-label">产地</text>
        <text class="param-value">{{ detail.originPlace }}</text>
      </view>
      <view class="param-item">
        <text class="param-label">是否无硫</text>
        <text class="param-value" :style="{ color: detail.isSulfurFree ? '#4caf50' : '#999' }">
          {{ detail.isSulfurFree ? '是 ✅' : '否' }}
        </text>
      </view>
      <view class="param-item" v-if="detail.dryingLevel">
        <text class="param-label">干度</text>
        <text class="param-value">{{ detail.dryingLevel }}</text>
      </view>
      <view class="param-item" v-if="detail.plantingProcess">
        <text class="param-label">种植工艺</text>
        <text class="param-value">{{ detail.plantingProcess }}</text>
      </view>
      <view class="param-item" v-if="detail.merchantName">
        <text class="param-label">店铺</text>
        <text class="param-value">{{ detail.merchantName }}</text>
      </view>
    </view>

    <!-- 商家信息 -->
    <view class="merchant-section" v-if="detail.merchantName" @click="toMerchant">
      <view class="merchant-icon"><u-icon name="shop" size="24" color="#4caf50" /></view>
      <view class="merchant-info">
        <text class="merchant-name">{{ detail.merchantName }}</text>
        <text class="merchant-place" v-if="detail.merchantOriginPlace">{{ detail.merchantOriginPlace }}</text>
      </view>
      <text class="merchant-arrow">›</text>
    </view>

    <!-- 商品详情 -->
    <view class="desc-section">
      <view class="section-title">商品详情</view>
      <view class="desc-content" v-if="detail.description" v-html="detail.description" />
      <u-empty v-else text="暂无详情" mode="list" />
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bar-left" @click="goCart">
        <u-icon name="shopping-cart" size="44" color="#666" />
        <text class="bar-label">购物车</text>
      </view>
      <button class="btn-cart" @click="addToCart">加入购物车</button>
      <button class="btn-buy" @click="buyNow">立即购买</button>
    </view>

    <!-- 加入购物车成功提示 -->
    <u-toast ref="toastRef" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { productApi } from '@/api/productApi'
import { cartApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'

const detail = ref({
  imageList: [],
  skuList: [],
})
const selectedSku = ref(null)
const quantity = ref(1)

onLoad((options) => {
  const productId = Number(options.id)
  if (productId) {
    loadDetail(productId)
  }
})

const loadDetail = async (productId) => {
  try {
    const res = await productApi.detail(productId)
    detail.value = res
    // 默认选中第一个有库存的SKU
    if (res.skuList && res.skuList.length > 0) {
      const available = res.skuList.find(s => s.stock > 0)
      selectedSku.value = available || res.skuList[0]
    }
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
    console.error('加载商品详情失败', e)
  }
}

const selectSku = (sku) => {
  if (sku.stock <= 0) return
  selectedSku.value = sku
}

const addToCart = async () => {
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    return
  }
  try {
    await cartApi.add(selectedSku.value.id, quantity.value)
    uni.showToast({ title: '已加入购物车', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '加入失败，请重试', icon: 'none' })
  }
}

const buyNow = () => {
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    return
  }
  uni.$grouter.navigateTo('orderConfirm')
}

const goCart = () => {
  uni.$grouter.switchTab('cart')
}

const toMerchant = () => {
  uni.showToast({ title: '商家详情开发中', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.page { background: #f5f9f5; min-height: 100vh; padding-bottom: 120rpx; }

.swiper { width: 100%; height: 560rpx; }
.swiper-img { width: 100%; height: 100%; background: #e8f5e9; }

.info-section {
  background: #fff; padding: 24rpx 28rpx; margin-bottom: 16rpx;
  .price-row { display: flex; align-items: baseline; gap: 12rpx; margin-bottom: 10rpx; }
  .price { font-size: 44rpx; font-weight: 700; color: #ff4d4f; }
  .original { font-size: 26rpx; color: #999; text-decoration: line-through; }
  .sales { font-size: 24rpx; color: #999; margin-left: auto; }
  .name { font-size: 32rpx; font-weight: 600; color: #2e3b2e; margin-bottom: 6rpx; }
  .title { font-size: 26rpx; color: #999; }
  .tags-row { display: flex; gap: 10rpx; margin-top: 12rpx; }
  .tag-green { font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx; background: rgba(76,175,80,0.1); color: #4caf50; }
  .tag-outline { font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx; border: 1rpx solid #e0e0e0; color: #666; }
}

.sku-section {
  background: #fff; padding: 24rpx 28rpx; margin-bottom: 16rpx;
  .section-title { font-size: 28rpx; font-weight: 600; color: #2e3b2e; margin-bottom: 16rpx; }
  .sku-list { display: flex; flex-wrap: wrap; gap: 12rpx; }
  .sku-item {
    padding: 16rpx 24rpx; border-radius: 12rpx; border: 2rpx solid #e8e8e8; background: #fafafa;
    text-align: center; min-width: 160rpx; position: relative;
    &.active { border-color: #4caf50; background: rgba(76,175,80,0.06); }
    &.disabled { opacity: 0.4; }
    .sku-name { display: block; font-size: 26rpx; color: #333; font-weight: 500; margin-bottom: 4rpx; }
    .sku-price { display: block; font-size: 28rpx; color: #ff4d4f; font-weight: 600; }
    .sku-stock { font-size: 20rpx; color: #ccc; }
    .sku-stock-low { font-size: 20rpx; color: #ff9800; }
  }
}

.qty-section {
  background: #fff; padding: 24rpx 28rpx; margin-bottom: 16rpx;
  display: flex; align-items: center; justify-content: space-between;
  .section-title { font-size: 28rpx; font-weight: 600; color: #2e3b2e; }
}

.param-section {
  background: #fff; padding: 24rpx 28rpx; margin-bottom: 16rpx;
  .section-title { font-size: 28rpx; font-weight: 600; color: #2e3b2e; margin-bottom: 12rpx; }
  .param-item { display: flex; padding: 10rpx 0; border-bottom: 1rpx solid #f5f5f5; }
  .param-item:last-child { border-bottom: none; }
  .param-label { width: 140rpx; font-size: 26rpx; color: #999; }
  .param-value { flex: 1; font-size: 26rpx; color: #333; }
}

.merchant-section {
  background: #fff; padding: 20rpx 28rpx; margin-bottom: 16rpx;
  display: flex; align-items: center; gap: 16rpx;
  .merchant-icon { }
  .merchant-info { flex: 1; }
  .merchant-name { font-size: 28rpx; font-weight: 600; color: #2e3b2e; display: block; }
  .merchant-place { font-size: 24rpx; color: #999; }
  .merchant-arrow { font-size: 36rpx; color: #ccc; }
}

.desc-section {
  background: #fff; padding: 24rpx 28rpx;
  .section-title { font-size: 28rpx; font-weight: 600; color: #2e3b2e; margin-bottom: 16rpx; }
  .desc-content { font-size: 28rpx; color: #555; line-height: 1.8; }
}

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0; height: 100rpx;
  background: #fff; display: flex; align-items: center; padding: 0 20rpx;
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.06); z-index: 100;
  .bar-left { display: flex; flex-direction: column; align-items: center; padding: 0 20rpx; }
  .bar-label { font-size: 20rpx; color: #666; }
  .btn-cart {
    flex: 1; height: 72rpx; line-height: 72rpx; text-align: center;
    background: #fff; color: #4caf50; border: 2rpx solid #4caf50;
    border-radius: 36rpx; font-size: 28rpx; margin: 0 12rpx;
  }
  .btn-buy {
    flex: 1; height: 72rpx; line-height: 72rpx; text-align: center;
    background: #ff8f00; color: #fff; border-radius: 36rpx; font-size: 28rpx;
    border: none; margin: 0;
  }
}
</style>

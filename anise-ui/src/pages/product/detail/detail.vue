<template>
  <view class="page">
    <swiper class="swiper" :autoplay="true" indicator-dots indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#4caf50">
      <swiper-item v-for="(img, idx) in (detail.imageList && detail.imageList.length ? detail.imageList : [detail.mainImage])" :key="idx">
        <image :src="getImageUrl(img)" class="swiper-img" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <view class="info-section">
      <view class="price-row">
        <text class="price">¥{{ selectedSku ? selectedSku.price : '--' }}</text>
        <text class="original" v-if="selectedSku && selectedSku.originalPrice">¥{{ selectedSku.originalPrice }}</text>
        <text class="sales">已售 {{ detail.sales || 0 }}</text>
      </view>
      <view class="name">{{ detail.productName }}</view>
      <view class="title" v-if="detail.productTitle">{{ detail.productTitle }}</view>

      <view class="tags-row">
        <text class="tag-outline" v-for="attr in detail.spuAttrs" :key="attr.attrId">{{ attr.attrValue }}</text>
        <text class="tag-outline" v-if="detail.seasonTag">{{ detail.seasonTag }}</text>
      </view>
    </view>

    <view class="sku-section">
      <view class="section-title">选择规格</view>
      <view class="sku-attrs" v-if="skuAttrGroups.length > 0">
        <view class="attr-group" v-for="group in skuAttrGroups" :key="group.attrName">
          <view class="attr-label">{{ group.attrName }}</view>
          <view class="attr-options">
            <view
              v-for="option in group.options"
              :key="option.attrValueId"
              class="attr-option"
              :class="{ active: isAttrSelected(group.attrId, option.attrValueId), disabled: !isOptionAvailable(group.attrId, option.attrValueId) }"
              @click="selectAttrOption(group.attrId, option.attrValueId)"
            >
              {{ option.attrValue }}
            </view>
          </view>
        </view>
      </view>
      <view class="sku-selected" v-if="selectedSku">
        <view class="selected-label">已选规格：</view>
        <view class="selected-info">
          <text class="selected-name">{{ getSkuAttrDisplay(selectedSku) }}</text>
          <text class="selected-price">¥{{ selectedSku.price }}</text>
          <text class="selected-stock" v-if="selectedSku.stock <= 0">售罄</text>
          <text class="selected-stock-low" v-else-if="selectedSku.stock < 10">仅剩{{ selectedSku.stock }}{{ selectedSku.isVariableWeight ? '斤' : '件' }}</text>
        </view>
      </view>
    </view>

    <view class="qty-section">
      <text class="section-title">购买数量</text>
      <view class="qty-box">
        <view class="qty-btn" :class="{ disabled: quantity <= (selectedSku?.minQuantity || 1) }" @click="decreaseQty">-</view>
        <text class="qty-value">{{ quantity }}</text>
        <view class="qty-btn" :class="{ disabled: quantity >= (selectedSku?.maxQuantity || 999) }" @click="increaseQty">+</view>
      </view>
      <text class="qty-hint" v-if="selectedSku">
        {{ selectedSku.minQuantity }}-{{ selectedSku.maxQuantity }}{{ selectedSku.isVariableWeight ? '斤' : '件' }}
      </text>
    </view>

    <view class="param-section">
      <view class="section-title">商品参数</view>
      <view class="param-item" v-for="attr in detail.spuAttrs" :key="attr.attrId">
        <text class="param-label">{{ attr.attrName }}</text>
        <text class="param-value" :style="{ color: attr.attrName === '无硫' && attr.attrValue === '是' ? '#4caf50' : '#333' }">
          {{ attr.attrValue }}{{ attr.attrName === '无硫' && attr.attrValue === '是' ? ' ✅' : '' }}
        </text>
      </view>
      <view class="param-item" v-if="detail.merchantName">
        <text class="param-label">店铺</text>
        <text class="param-value">{{ detail.merchantName }}</text>
      </view>
    </view>

    <view class="merchant-section" v-if="detail.merchantName" @click="toMerchant">
      <view class="merchant-icon"><u-icon name="shop" size="24" color="#4caf50" /></view>
      <view class="merchant-info">
        <text class="merchant-name">{{ detail.merchantName }}</text>
        <text class="merchant-place" v-if="detail.merchantOriginPlace">{{ detail.merchantOriginPlace }}</text>
      </view>
      <text class="merchant-arrow">›</text>
    </view>

    <view class="presale-info" v-if="detail.preSaleStart">
      <view class="presale-row">
        <text class="presale-label">预售中</text>
        <text class="presale-text">{{ formatPresaleTime(detail.preSaleStart, detail.preSaleEnd) }}</text>
      </view>
      <text class="presale-ship" v-if="detail.estimatedShipDate">预计{{ detail.estimatedShipDate }}发货</text>
    </view>

    <view class="desc-section">
      <view class="section-title">商品详情</view>
      <view class="desc-content" v-if="detail.description" v-html="detail.description" />
      <u-empty v-else text="暂无详情" mode="list" />
    </view>

    <view class="bottom-bar">
      <view class="bar-left" @click="toMerchant">
        <u-icon name="home" size="40" color="#666" />
        <text class="bar-label">店铺</text>
      </view>

      <view class="bar-left" @click="goCart">
        <u-icon name="shopping-cart" size="40" color="#666" />
        <text class="bar-label">购物车</text>
      </view>
      <button class="btn-cart" @click="addToCart">加入购物车</button>
      <button class="btn-buy" :class="{ disabled: !canPurchase && detail.preSaleStart }" @click="buyNow">{{ detail.preSaleStart ? '立即预订' : '立即购买' }}</button>
    </view>

    <u-toast ref="toastRef" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { productApi } from '@/api/productApi'
import { cartApi } from '@/api/businessApi'
import { getImageUrl } from '@/utils/image'
import { useCartStore } from '@/stores/cart'

const detail = ref({
  imageList: [],
  skuList: [],
  spuAttrs: [],
})
const selectedSku = ref(null)
const quantity = ref(1)
const cartStore = useCartStore()
const selectedAttrs = ref({})

const canPurchase = computed(() => {
  if (!detail.value.preSaleStart) return true
  const now = new Date().getTime()
  const preSaleStart = new Date(detail.value.preSaleStart).getTime()
  return now >= preSaleStart
})

const skuAttrGroups = computed(() => {
  const groups = {}
  detail.value.skuList?.forEach(sku => {
    const skuAttrs = sku.skuAttrs || sku.attrs || []
    skuAttrs.forEach(attr => {
      if (!groups[attr.attrId]) {
        groups[attr.attrId] = {
          attrId: attr.attrId,
          attrName: attr.attrName,
          options: []
        }
      }
      const exists = groups[attr.attrId].options.some(opt => opt.attrValueId === attr.attrValueId)
      if (!exists) {
        groups[attr.attrId].options.push({
          attrValueId: attr.attrValueId,
          attrValue: attr.attrValue
        })
      }
    })
  })
  console.log('skuAttrGroups:', Object.values(groups))
  return Object.values(groups)
})

const isAttrSelected = (attrId, attrValueId) => {
  return selectedAttrs.value[attrId] === attrValueId
}

const isOptionAvailable = (attrId, attrValueId) => {
  // 检查当前选择组合是否可用（用于显示灰色）
  // 创建临时属性对象：只保留其他属性组的选择，替换当前属性组的值
  const tempAttrs = {}
  Object.entries(selectedAttrs.value).forEach(([aId, vId]) => {
    if (Number(aId) !== attrId) {
      tempAttrs[aId] = vId
    }
  })
  tempAttrs[attrId] = attrValueId
  
  // 检查是否存在有库存的SKU满足所有已选属性组合
  return detail.value.skuList?.some(sku => {
    if (sku.stock <= 0) return false
    const skuAttrs = sku.skuAttrs || sku.attrs || []
    return Object.entries(tempAttrs).every(([aId, vId]) => {
      return skuAttrs.some(attr => attr.attrId === Number(aId) && attr.attrValueId === Number(vId))
    })
  }) ?? false
}

const selectAttrOption = (attrId, attrValueId) => {
  // 灰色选项也可以点击，点击后自动调整到有库存的SKU
  
  // 找到包含当前属性值的第一个有库存的SKU
  const matchedSku = detail.value.skuList?.find(sku => {
    if (sku.stock <= 0) return false
    const skuAttrs = sku.skuAttrs || sku.attrs || []
    return skuAttrs.some(attr => attr.attrId === attrId && attr.attrValueId === attrValueId)
  })
  
  if (matchedSku) {
    // 更新选中的属性为这个SKU的所有属性
    const newAttrs = {}
    const skuAttrs = matchedSku.skuAttrs || matchedSku.attrs || []
    skuAttrs.forEach(attr => {
      newAttrs[attr.attrId] = attr.attrValueId
    })
    selectedAttrs.value = newAttrs
    
    // 更新选中的SKU和价格
    selectedSku.value = { ...matchedSku }
    quantity.value = matchedSku.minQuantity || 1
  }
}

onLoad((options) => {
  const productId = Number(options.id)
  if (productId) {
    loadDetail(productId)
  }
})

const loadDetail = async (productId) => {
  try {
    const res = await productApi.detail(productId)
    console.log('商品详情数据:', res)
    detail.value = res
    if (res.skuList && res.skuList.length > 0) {
      const available = res.skuList.find(s => s.stock > 0)
      selectedSku.value = available || res.skuList[0]
      quantity.value = selectedSku.value?.minQuantity || 1
      
      const skuAttrs = selectedSku.value?.skuAttrs || selectedSku.value?.attrs || []
      if (skuAttrs.length > 0) {
        const attrs = {}
        skuAttrs.forEach(attr => {
          attrs[attr.attrId] = attr.attrValueId
        })
        selectedAttrs.value = attrs
      }
    }
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
    console.error('加载商品详情失败', e)
  }
}

const selectSku = (sku) => {
  if (sku.stock <= 0) return
  selectedSku.value = sku
  quantity.value = sku.minQuantity || 1
}

const getSkuAttrDisplay = (sku) => {
  const skuAttrs = sku.skuAttrs || sku.attrs || []
  if (skuAttrs.length > 0) {
    return skuAttrs.map(a => a.attrValue).join(' / ')
  }
  return sku.skuCode || `规格${sku.id}`
}

const decreaseQty = () => {
  if (!selectedSku.value) return
  const minQty = selectedSku.value.minQuantity || 1
  const step = selectedSku.value.quantityStep || 1
  if (quantity.value > minQty) {
    quantity.value = Math.max(minQty, quantity.value - step)
  }
}

const increaseQty = () => {
  if (!selectedSku.value) return
  const maxQty = selectedSku.value.maxQuantity || 999
  const step = selectedSku.value.quantityStep || 1
  const stock = selectedSku.value.stock || 999
  if (quantity.value < Math.min(maxQty, stock)) {
    quantity.value = Math.min(maxQty, stock, quantity.value + step)
  }
}

const formatPresaleTime = (start, end) => {
  if (!start) return ''
  const startStr = $u.timeFormat(start, 'mm月dd日 hh:MM:ss')
  if (end) {
    const endStr = $u.timeFormat(end, 'mm月dd日 hh:MM:ss')
    return `${startStr} - ${endStr}预售`
  }
  return `${startStr} 开始预售`
}

const addToCart = async () => {
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    return
  }
  if (!canPurchase.value) {
    uni.showToast({ title: '预售尚未开始', icon: 'none' })
    return
  }
  try {
    // 构建 skuSpecs JSON
    const skuSpecs = buildSkuSpecs()
    await cartApi.add(selectedSku.value.id, quantity.value, skuSpecs)
    uni.showToast({ title: '已加入购物车', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '加入失败，请重试', icon: 'none' })
  }
}

// 根据选中的属性构建 skuSpecs JSON
const buildSkuSpecs = (): string => {
  const specs: Record<string, string> = {}
  
  // 只添加用户动态选择的属性值
  Object.entries(selectedAttrs.value).forEach(([attrId, attrValueId]) => {
    skuAttrGroups.value.forEach(group => {
      if (Number(group.attrId) === Number(attrId)) {
        const option = group.options.find(opt => opt.attrValueId === attrValueId)
        if (option) {
          specs[group.attrName] = option.attrValue
        }
      }
    })
  })
  
  return JSON.stringify(specs)
}

const buyNow = () => {
  if (!selectedSku.value) {
    uni.showToast({ title: '请选择规格', icon: 'none' })
    return
  }
  if (!canPurchase.value) {
    uni.showToast({ title: '预售尚未开始', icon: 'none' })
    return
  }
  cartStore.setDirectBuyGoods({
    productId: detail.value.id,
    productName: detail.value.productName,
    mainImage: detail.value.mainImage,
    merchantId: detail.value.merchantId,
    merchantName: detail.value.merchantName,
    skuId: selectedSku.value.id,
    specName: getSkuAttrDisplay(selectedSku.value),
    price: selectedSku.value.price,
    quantity: quantity.value
  })
  uni.$grouter.navigateTo('orderConfirm')
}

const goCart = () => {
  uni.$grouter.switchTab('cart')
}

const toMerchant = () => {
  if (detail.value.merchantId) {
    uni.$grouter.navigateTo('merchantDetail', { query: { id: detail.value.merchantId } })
  }
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
  .section-title { font-size: 28rpx; font-weight: 600; color: #2e3b2e; margin-bottom: 20rpx; }
  .sku-attrs { margin-bottom: 20rpx; }
  .attr-group { margin-bottom: 20rpx; }
  .attr-label { font-size: 26rpx; color: #666; margin-bottom: 12rpx; }
  .attr-options { display: flex; flex-wrap: wrap; gap: 14rpx; }
  .attr-option {
    padding: 16rpx 28rpx; border-radius: 8rpx; border: 2rpx solid #e0e0e0;
    font-size: 26rpx; color: #333; background: #fafafa;
    &.active { border-color: #4caf50; background: rgba(76,175,80,0.08); color: #4caf50; }
    &.disabled { opacity: 0.4; color: #999; }
  }
  .sku-selected {
    padding: 16rpx; background: #f8faf8; border-radius: 12rpx;
    display: flex; align-items: center; gap: 12rpx;
    .selected-label { font-size: 24rpx; color: #999; }
    .selected-info { flex: 1; display: flex; flex-wrap: wrap; align-items: center; gap: 12rpx; }
    .selected-name { font-size: 26rpx; color: #333; font-weight: 500; }
    .selected-price { font-size: 30rpx; color: #ff4d4f; font-weight: 600; }
    .selected-stock { font-size: 22rpx; color: #ccc; }
    .selected-stock-low { font-size: 22rpx; color: #ff9800; }
  }
}

.qty-section {
  background: #fff; padding: 24rpx 28rpx; margin-bottom: 16rpx;
  display: flex; align-items: center; gap: 16rpx;
  .section-title { font-size: 28rpx; font-weight: 600; color: #2e3b2e; }
  .qty-box { display: flex; align-items: center; border: 1rpx solid #e0e0e0; border-radius: 8rpx; }
  .qty-btn { width: 64rpx; height: 64rpx; display: flex; align-items: center; justify-content: center; font-size: 36rpx; color: #333; &.disabled { color: #ccc; } }
  .qty-value { width: 80rpx; text-align: center; font-size: 28rpx; }
  .qty-hint { font-size: 22rpx; color: #999; margin-left: auto; }
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

.presale-info {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  padding: 20rpx 28rpx; margin-bottom: 16rpx;
  display: flex; flex-direction: column; gap: 10rpx;
  .presale-row { display: flex; align-items: center; gap: 12rpx; }
  .presale-label { font-size: 22rpx; padding: 4rpx 10rpx; background: #ff9800; color: #fff; border-radius: 4rpx; flex-shrink: 0; }
  .presale-text { font-size: 26rpx; color: #e65100; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .presale-ship { font-size: 24rpx; color: #ff7043; }
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
    &.disabled {
      background: #ccc;
      color: #999;
    }
  }
}
</style>

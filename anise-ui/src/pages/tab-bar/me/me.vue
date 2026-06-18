<template>
  <view class="page">
    <view class="profile-hero">
      <view class="hero-bg" />

      <view class="user-card">
        <view class="card-content">
          <!-- #ifdef MP-WEIXIN -->
          <button class="avatar-btn" open-type="chooseAvatar" @chooseavatar="onChooseAvatar" :disabled="isChoosingAvatar">
            <image :src="getImageUrl(userStore.userInfo.avatarUrl)" class="user-avatar" mode="aspectFill" />
          </button>
          <!-- #endif -->
          <!-- #ifndef MP-WEIXIN -->
          <view class="user-avatar-wrapper" @click="chooseAvatarForH5">
            <image :src="getImageUrl(userStore.userInfo.avatarUrl)" class="user-avatar" mode="aspectFill" />
          </view>
          <!-- #endif -->

          <view class="user-info">
            <view class="user-name-row" @click="userStore.isLoggedIn ? toPage('userInfo') : toLogin()">
              <text class="user-name">{{ displayName }}</text>
              <u-icon name="arrow-right" size="24" color="#bfbfbf" />
            </view>
            <view class="user-phone" v-if="userStore.isLoggedIn && userStore.userInfo.phone">
              {{ userStore.userInfo.phone }}
            </view>
            <view class="user-phone" v-else>
              {{ userStore.isLoggedIn ? '资料未完善' : '未登录' }}
            </view>
          </view>

          <view class="login-badge" :class="{ logged: userStore.isLoggedIn }">
            {{ userStore.isLoggedIn ? '已登录' : '未登录' }}
          </view>
        </view>

        <view class="quick-login" v-if="!userStore.isLoggedIn">
          <!-- #ifdef MP-WEIXIN -->
          <button class="login-btn avatar-btn" open-type="chooseAvatar" @chooseavatar="onChooseAvatar" :disabled="isChoosingAvatar">
            微信一键登录
          </button>
          <!-- #endif -->
          <!-- #ifndef MP-WEIXIN -->
          <button class="login-btn" @click="chooseAvatarForH5">登录账号</button>
          <!-- #endif -->
        </view>
      </view>
    </view>

    <view class="content">
      <view class="section-card order-panel">
        <view class="section-head" @click="goOrder(0)">
          <view>
            <view class="section-title">我的订单</view>
          </view>
          <view class="section-more">
            <text>全部订单</text>
            <u-icon name="arrow-right" size="22" color="#bfbfbf" />
          </view>
        </view>

        <view class="stats-grid">
          <view class="stat-item" @click="goOrder(0)">
            <view class="stat-icon stat-all">
              <u-icon name="order" size="32" color="#1890ff" />
            </view>
            <text class="stat-number">{{ orderStats.total }}</text>
            <text class="stat-label">全部</text>
          </view>
          <view class="stat-item" @click="goOrder(1)">
            <view class="stat-icon stat-pay">
              <u-icon name="rmb-circle" size="32" color="#faad14" />
            </view>
            <text class="stat-number">{{ orderStats.pendingPayment }}</text>
            <text class="stat-label">待支付</text>
          </view>
          <view class="stat-item" @click="goOrder(2)">
            <view class="stat-icon stat-progress">
              <u-icon name="car" size="32" color="#52c41a" />
            </view>
            <text class="stat-number">{{ orderStats.processing }}</text>
            <text class="stat-label">进行中</text>
          </view>
          <view class="stat-item" @click="goOrder(4)">
            <view class="stat-icon stat-review">
              <u-icon name="edit-pen" size="32" color="#f5222d" />
            </view>
            <text class="stat-number">{{ orderStats.pendingReview }}</text>
            <text class="stat-label">待评价</text>
          </view>
        </view>
      </view>

      <view class="section-card menu-card">
        <view class="menu-item" @click="goAddress">
          <view class="menu-icon icon-address">
            <u-icon name="map" size="30" color="#52c41a" />
          </view>
          <view class="menu-center">
            <text class="menu-title">收货地址</text>
          </view>
          <u-icon name="arrow-right" size="24" color="#bfbfbf" />
        </view>
        <view class="menu-divider" />
        <view class="menu-item" @click="toPage('setting')">
          <view class="menu-icon icon-setting">
            <u-icon name="setting" size="30" color="#1890ff" />
          </view>
          <view class="menu-center">
            <text class="menu-title">系统设置</text>
          </view>
          <u-icon name="arrow-right" size="24" color="#bfbfbf" />
        </view>
      </view>

      <view class="button-area" v-if="userStore.isLoggedIn">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { onShow } from '@dcloudio/uni-app'
import { getImageUrl } from '@/utils/image'
import { orderApi } from '@/api/businessApi'

const userStore = useUserStore()
const isChoosingAvatar = ref(false)

const orderStats = ref({
  pendingPayment: 0,
  processing: 0,
  pendingReview: 0,
  total: 0
})

const displayName = computed(() => {
  return userStore.isLoggedIn ? (userStore.userInfo.nickname || '匿名用户') : '点击登录'
})

const loadOrderStats = async () => {
  try {
    const res = await orderApi.count()
    orderStats.value = {
      pendingPayment: res.pendingPayment || 0,
      processing: (res.pendingDelivery || 0) + (res.pendingReceive || 0),
      pendingReview: (res as any).completed || 0,
      total: res.total || 0
    }
  } catch (e) {
    console.error('loadOrderStats error:', e)
    orderStats.value = {
      pendingPayment: 2,
      processing: 3,
      pendingReview: 3,
      total: 10
    }
  }
}

const toPage = (pageName: string) => {
  uni.$grouter.navigateTo(pageName)
}

const toLogin = () => {
  uni.$grouter.navigateTo('login')
}

const goOrder = (status: number) => {
  uni.$grouter.navigateTo('orderList', { query: { status } })
}

const goAddress = () => {
  uni.$grouter.navigateTo('addressList')
}

// 微信小程序 - 选择头像
const onChooseAvatar = async (e: { detail?: { avatarUrl?: string } }) => {
  try {
    isChoosingAvatar.value = true
    if (e.detail?.avatarUrl) {
      userStore.updateAvatar(e.detail.avatarUrl)
      // 头像更换成功后自动视为已登录
      if (!userStore.isLoggedIn) {
        userStore.login('wechat_quick', { nickname: '微信用户' })
      }
      uni.showToast({ title: '头像更新成功', icon: 'success' })
    }
  } catch (err) {
    console.error('头像处理失败:', err)
    uni.showToast({ title: '头像更新失败，请重试', icon: 'none' })
  } finally {
    isChoosingAvatar.value = false
  }
}

// H5专用头像选择
const chooseAvatarForH5 = () => {
  if (!userStore.isLoggedIn) {
    uni.$grouter.navigateTo('login')
    return
  }
  toPage('userInfo')
}

const handleLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出当前账号吗？',
    success: (res: UniApp.ShowModalRes) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出', icon: 'none' })
      }
    }
  })
}

onShow(() => {
  loadOrderStats()
  const app = getApp() as any
  if (app.globalData?.refreshPages?.me) {
    app.globalData.refreshPages.me = false
  }
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding-bottom: 120rpx;
  background: $uni-bg-color-page;
}

.profile-hero {
  position: relative;
  padding: 28rpx 24rpx 0;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 260rpx;
  background: linear-gradient(135deg, $uni-color-success 0%, $uni-color-primary 100%);
}

.content {
  position: relative;
  z-index: 2;
  padding: 22rpx 24rpx 0;
}

/* 微信头像按钮 — 去掉默认button样式 */
.avatar-btn {
  padding: 0;
  margin: 0;
  background: transparent;
  border: none;
  line-height: 1;
}
.avatar-btn::after {
  border: none;
}

.user-card {
  position: relative;
  z-index: 2;
  margin-top: 100rpx;
  border-radius: 18rpx;
  overflow: hidden;
  background: $uni-bg-color;
  box-shadow: 0 12rpx 34rpx rgba($uni-text-color, 0.08);
}

.card-content {
  position: relative;
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 34rpx 28rpx 28rpx;
}

.user-avatar-wrapper {
  flex-shrink: 0;
}

.user-avatar {
  display: block;
  width: 132rpx;
  height: 132rpx;
  border: 6rpx solid $uni-color-success-light;
  border-radius: 50%;
  background: $uni-bg-color-grey;
  box-sizing: border-box;
}

.user-info {
  flex: 1;
  min-width: 0;
  padding-right: 92rpx;
}

.user-name-row {
  display: flex;
  align-items: center;
  min-width: 0;
}

.user-name {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 36rpx;
  font-weight: 700;
  line-height: 46rpx;
  color: $uni-text-color;
}

.user-phone {
  margin-top: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 25rpx;
  line-height: 34rpx;
  color: $uni-text-color-grey;
}

.login-badge {
  position: absolute;
  top: 30rpx;
  right: 28rpx;
  padding: 7rpx 16rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  line-height: 28rpx;
  color: $uni-text-color-grey;
  background: $uni-bg-color-grey;
}

.login-badge.logged {
  color: $uni-color-success;
  background: $uni-color-success-light;
}

.quick-login {
  padding: 0 28rpx 28rpx;
}

.login-btn {
  height: 82rpx;
  border: none;
  border-radius: 999rpx;
  background: $uni-color-success;
  box-shadow: 0 8rpx 20rpx rgba($uni-color-success, 0.22);
  color: $uni-text-color-inverse;
  text-align: center;
  font-size: 29rpx;
  line-height: 82rpx;
}

.login-btn::after {
  border: none;
}

.section-card {
  border-radius: 18rpx;
  background: $uni-bg-color;
  box-shadow: 0 8rpx 24rpx rgba($uni-text-color, 0.05);
}

.order-panel {
  padding: 26rpx 24rpx 24rpx;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.section-title {
  font-size: 31rpx;
  font-weight: 700;
  line-height: 40rpx;
  color: $uni-text-color;
}

.section-more {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  font-size: 23rpx;
  line-height: 32rpx;
  color: $uni-text-color-grey;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin-top: 28rpx;
}

.stat-item {
  display: flex;
  align-items: center;
  flex-direction: column;
  min-width: 0;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: 18rpx;
}

.stat-all {
  background: $uni-color-primary-light;
}

.stat-pay {
  background: $uni-color-warning-light;
}

.stat-progress {
  background: $uni-color-success-light;
}

.stat-review {
  background: $uni-color-error-light;
}

.stat-number {
  margin-top: 12rpx;
  font-size: 32rpx;
  font-weight: 800;
  line-height: 38rpx;
  color: $uni-text-color;
}

.stat-label {
  margin-top: 4rpx;
  font-size: 23rpx;
  line-height: 30rpx;
  color: $uni-text-color-grey;
}

.menu-card {
  margin-top: 22rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  min-height: 96rpx;
  padding: 0 24rpx;
}

.menu-item:active {
  background: $uni-bg-color-hover;
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 62rpx;
  height: 62rpx;
  border-radius: 18rpx;
}

.icon-address {
  background: $uni-color-success-light;
}

.icon-setting {
  background: $uni-color-primary-light;
}

.menu-center {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
}

.menu-title {
  font-size: 29rpx;
  font-weight: 600;
  line-height: 38rpx;
  color: $uni-text-color;
}

.menu-divider {
  height: 1rpx;
  margin-left: 106rpx;
  background: $uni-border-color-light;
}

.button-area {
  margin-top: 28rpx;

  .logout-btn {
    height: 82rpx;
    border: 1rpx solid $uni-color-error-light;
    border-radius: 999rpx;
    background: $uni-bg-color;
    color: $uni-color-error;
    font-size: 29rpx;
    line-height: 82rpx;
  }

  .logout-btn::after {
    border: none;
  }
}

</style>

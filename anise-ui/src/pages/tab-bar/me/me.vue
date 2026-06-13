<template>
  <view class="page">
    <!-- 顶部渐变背景 -->
    <view class="top-banner" />

    <!-- 用户信息卡片 -->
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
          <view class="user-name" @click="userStore.isLoggedIn ? toPage('userInfo') : toLogin()">
            {{ userStore.isLoggedIn ? (userStore.userInfo.nickname || '匿名') : '点击登录' }}
          </view>
          <view class="user-phone" v-if="userStore.isLoggedIn && userStore.userInfo.phone">
            {{ userStore.userInfo.phone }}
          </view>
          
        </view>
      </view>

      <!-- 订单统计 -->
      <view class="stats-bar">
        <view class="stat-item" @click="goOrder(0)">
          <text class="stat-number">{{ orderStats.total }}</text>
          <text class="stat-label">全部</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item" @click="goOrder(1)">
          <text class="stat-number">{{ orderStats.pendingPayment }}</text>
          <text class="stat-label">待支付</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item" @click="goOrder(2)">
          <text class="stat-number">{{ orderStats.processing }}</text>
          <text class="stat-label">进行中</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item" @click="goOrder(4)">
          <text class="stat-number">{{ orderStats.pendingReview }}</text>
          <text class="stat-label">待评价</text>
        </view>
      </view>
    </view>

    <!-- 登录/头像区域 -->
    <view class="button-area" v-if="!userStore.isLoggedIn">
      <!-- #ifdef MP-WEIXIN -->
      <button class="login-btn avatar-btn" open-type="chooseAvatar" @chooseavatar="onChooseAvatar" :disabled="isChoosingAvatar">
        微信一键登录
      </button>
      <!-- #endif -->
      <!-- #ifndef MP-WEIXIN -->
      <button class="login-btn" @click="chooseAvatarForH5">登录账号</button>
      <!-- #endif -->
    </view>

    <!-- 功能菜单 -->
    <view class="menu-card">
      <view class="menu-item" @click="goOrder(0)">
        <view class="menu-left"><text style="font-size:36rpx;">📋</text></view>
        <view class="menu-center">我的订单</view>
        <view class="menu-right"><u-icon name="arrow-right" color="#ccc" /></view>
      </view>
      <view class="menu-item" @click="goAddress">
        <view class="menu-left"><text style="font-size:36rpx;">📍</text></view>
        <view class="menu-center">收货地址</view>
        <view class="menu-right"><u-icon name="arrow-right" color="#ccc" /></view>
      </view>
    </view>

    <view class="menu-card">
      <view class="menu-item" @click="toPage('setting')">
        <view class="menu-left"><u-icon name="setting" size="36" color="#4caf50" /></view>
        <view class="menu-center">系统设置</view>
        <view class="menu-right"><u-icon name="arrow-right" color="#ccc" /></view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="button-area" v-if="userStore.isLoggedIn">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
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

const loadOrderStats = async () => {
  try {
    const res = await orderApi.count()
    orderStats.value = {
      pendingPayment: res.pendingPayment || 0,
      processing: (res.pendingDelivery || 0) + (res.pendingReceive || 0),
      pendingReview: res.completed || 0,
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

const toPage = (pageName) => {
  uni.$grouter.navigateTo(pageName)
}

const toLogin = () => {
  uni.$grouter.navigateTo('login')
}

const goOrder = (status) => {
  uni.$grouter.navigateTo('orderList', { query: { status } })
}

const goAddress = () => {
  uni.$grouter.navigateTo('addressList')
}

// 微信小程序 - 选择头像
const onChooseAvatar = async (e) => {
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
    success: (res) => {
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
.page { background: #f5f9f5; min-height: 100vh; padding-bottom: 120rpx; }

.top-banner {
  height: 240rpx; background: linear-gradient(135deg, #4caf50, #81c784);
}

/* 微信头像按钮 — 去掉默认button样式 */
.avatar-btn {
  padding: 0; margin: 0; background: transparent; border: none; line-height: 1;
}
.avatar-btn::after { border: none; }

.user-card {
  position: relative; margin: -80rpx 25rpx 30rpx;
  background: #fff; border-radius: 24rpx;
  box-shadow: 0 10rpx 30rpx rgba(76, 175, 80, 0.12); overflow: hidden;
  .card-content { padding: 40rpx 30rpx 30rpx; display: flex; align-items: center; }
  .user-avatar-wrapper { margin-right: 24rpx; }
  .user-avatar {
    width: 130rpx; height: 130rpx; border-radius: 50%;
    border: 4rpx solid #e8f5e9; background: #f5f5f5;
    display: block;
  }
  .user-info { flex: 1; }
  .user-name { font-size: 36rpx; font-weight: 600; color: #2e3b2e; margin-bottom: 6rpx; }
  .user-phone { font-size: 26rpx; color: #999; margin-bottom: 4rpx; }
  .user-tagline { font-size: 24rpx; color: #999; }

  .stats-bar {
    display: flex; height: 90rpx; background: #f9fdf9; border-top: 1rpx solid #e8f5e9;
    .stat-item {
      flex: 1; display: flex; flex-direction: column; justify-content: center; align-items: center;
      .stat-number { font-size: 32rpx; font-weight: 700; color: #4caf50; }
      .stat-label { font-size: 22rpx; color: #999; margin-top: 4rpx; }
    }
    .stat-divider { width: 1px; height: 50rpx; background: #e8f5e9; margin: auto 0; }
  }
}

.menu-card {
  margin: 0 25rpx 30rpx; background: #fff; border-radius: 20rpx;
  overflow: hidden; box-shadow: 0 8rpx 25rpx rgba(76,175,80,0.06);
  .menu-item {
    display: flex; height: 96rpx; align-items: center; padding: 0 30rpx;
    &:active { background: #f9fdf9; }
    .menu-left { width: 70rpx; }
    .menu-center { flex: 1; font-size: 30rpx; color: #333; }
    .menu-right { width: 50rpx; text-align: right; }
  }
}

.button-area {
  margin: 0 25rpx 30rpx;
  .login-btn {
    height: 90rpx; line-height: 90rpx; font-size: 30rpx; border-radius: 50rpx;
    background: linear-gradient(to right, #4caf50, #81c784); color: #fff;
    border: none; box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.2);
    text-align: center;
  }
  .login-btn::after { border: none; }
  .logout-btn {
    height: 90rpx; line-height: 90rpx; font-size: 30rpx; border-radius: 50rpx;
    background: linear-gradient(to right, #ff8f00, #ffb74d); color: #fff;
    border: none; box-shadow: 0 10rpx 20rpx rgba(255, 143, 0, 0.2);
  }
}
</style>

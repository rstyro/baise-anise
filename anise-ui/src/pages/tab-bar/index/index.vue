<template>
  <view class="content">
    <image class="logo" src="/static/logo.png" />
    <view class="text-area">
      <text class="title">{{ title }}</text>
    </view>

    <view class="demo-section">
      <text class="demo-title">功能演示</text>
      <view class="demo-list">
        <view class="demo-item" @click="goToPiniaDemo">
          <text class="demo-icon">🍍</text>
          <text class="demo-text">Pinia 状态管理</text>
          <text class="demo-arrow">›</text>
        </view>
        <view class="demo-item" @click="goToUviewDemo">
          <text class="demo-icon">🧩</text>
          <text class="demo-text">uView Plus 组件库</text>
          <text class="demo-arrow">›</text>
        </view>
        <view class="demo-item" @click="goToAutoImportDemo">
          <text class="demo-icon">⚡</text>
          <text class="demo-text">自动导入演示</text>
          <text class="demo-arrow">›</text>
        </view>

        <view class="demo-item" @click="goToHttpDemo">
          <text class="demo-icon">⚡</text>
          <text class="demo-text">HTTP 请求演示</text>
          <text class="demo-arrow">›</text>
        </view>

        <view class="demo-item" @click="goToAudioDemo">
          <text class="demo-icon">⚡</text>
          <text class="demo-text">音频播放演示</text>
          <text class="demo-arrow">›</text>
        </view>
      </view>
    </view>

    <view>
      <text class="demo-title">全局插件测试</text>
      <view class="demo-list">
        <view class="demo-item">
          <view>{{ $const.IMAGES.SHARE_URL }}</view>
        </view>
      </view>
    </view>


  </view>
</template>

<script setup lang="ts">
const title = ref('Hello')


function goToPiniaDemo() {
  uni.$grouter.navigateTo('pinia-demo')
}

function goToUviewDemo() {
  uni.$grouter.navigateTo('uview-demo')
}

function goToAutoImportDemo() {
  uni.$grouter.navigateTo('auto-import-demo')
}

function goToHttpDemo() {
  uni.$grouter.navigateTo('http-demo')
}

function goToAudioDemo() {
  uni.$grouter.navigateTo('audio-demo')
}
</script>

<style scoped lang="scss">
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.logo {
  height: 200rpx;
  width: 200rpx;
  margin-top: 100rpx;
  margin-bottom: 50rpx;
}

.text-area {
  display: flex;
  justify-content: center;
  margin-bottom: 80rpx;
}

.title {
  font-size: 36rpx;
  color: #8f8f94;
}

.demo-section {
  width: 100%;
  background: white;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.demo-title {
  display: block;
  padding: 30rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  border-bottom: 1rpx solid #f0f0f0;
}

.demo-list {
  padding: 0 20rpx;
}

.demo-item {
  display: flex;
  align-items: center;
  padding: 30rpx 20rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.demo-item:last-child {
  border-bottom: none;
}

.demo-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.demo-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.demo-arrow {
  font-size: 32rpx;
  color: #c7c7cc;
}
</style>

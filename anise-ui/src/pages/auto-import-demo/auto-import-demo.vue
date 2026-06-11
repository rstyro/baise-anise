<template>
  <view class="container">
    <view class="header">
      <text class="header-icon">⚡</text>
      <text class="header-title">自动导入演示</text>
      <text class="header-subtitle">无需 import，开箱即用</text>
    </view>

    <!-- Vue API 自动导入演示 -->
    <view class="section">
      <view class="section-title">
        <text class="tag">API</text>
        <text>Vue API 自动导入</text>
      </view>
      <view class="card">
        <view class="demo-item">
          <text class="label">ref 计数器 (无需导入)</text>
          <view class="counter-box">
            <text class="counter">{{ count }}</text>
            <button class="btn" @click="increment">+1</button>
          </view>
        </view>
        <view class="demo-item">
          <text class="label">computed 计算属性</text>
          <text class="computed-text">双倍值: {{ doubleCount }}</text>
        </view>
        <view class="demo-item">
          <text class="label">onMounted 生命周期</text>
          <text class="lifecycle-text">{{ mountStatus }}</text>
        </view>
      </view>
    </view>

    <!-- 组件自动导入演示 -->
    <view class="section">
      <view class="section-title">
        <text class="tag">组件</text>
        <text>c- 前缀组件自动导入</text>
      </view>
      <view class="card">
        <text class="desc-text">
          下面这个组件使用了 c-test-card，组件会自动从 src/components 目录导入
        </text>
        <!-- 使用 c- 前缀自动导入组件 -->
        <c-test-card title="我是自动导入的组件"></c-test-card>
        <c-test-card></c-test-card>
      </view>
    </view>

    <!-- 代码示例 -->
    <view class="section">
      <view class="section-title">
        <text class="tag">说明</text>
        <text>使用方法</text>
      </view>
      <view class="card code-card">
        <view class="code-block">
          <text class="code-title">1. 自动导入 Vue API：</text>
          <text class="code-content">// 无需 import { ref } from 'vue'
const count = ref(0)
const doubleCount = computed(() => count.value * 2)
onMounted(() => { console.log('mounted') })</text>
        </view>
        <view class="code-block">
          <text class="code-title">2. 自动导入组件：</text>
          <text class="code-content">
<c-test-card title="标题"></c-test-card></text>
        </view>
        <view class="code-block">
          <text class="code-title">3. 组件命名规则</text>
          <text class="code-content">文件名必须以 c- 开头</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
// 🎉 注意：这里不需要导入 ref, computed, onMounted 等 API！
// 它们已经通过 unplugin-auto-import 自动导入了

// 响应式数据 - ref 自动导入
const count = ref(0)
const mountStatus = ref('等待挂载...')

// 计算属性 - computed 自动导入
const doubleCount = computed(() => count.value * 2)

// 方法
function increment() {
  count.value++
}

// 生命周期 - onMounted 自动导入
onMounted(() => {
  mountStatus.value = '✅ 组件已挂载 (onMounted 自动导入成功)'
  console.log('🎉 自动导入演示页面已加载')
  console.log('ref 值:', count.value)
  console.log('计算属性:', doubleCount.value)
})
</script>

<style scoped>
.container {
  padding: 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.header {
  text-align: center;
  padding: 40rpx 0;
}

.header-icon {
  font-size: 60rpx;
  display: block;
  margin-bottom: 10rpx;
}

.header-title {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
}

.header-subtitle {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 10rpx;
}

.section {
  margin-bottom: 30rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 16rpx;
  font-size: 28rpx;
  color: #fff;
  margin-bottom: 16rpx;
  padding: 0 10rpx;
}

.tag {
  background: #ffd93d;
  color: #333;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: bold;
}

.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.15);
}

.demo-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.demo-item:last-child {
  border-bottom: none;
}

.label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.counter-box {
  display: flex;
  align-items: center;
  gap: 30rpx;
}

.counter {
  font-size: 48rpx;
  font-weight: bold;
  color: #667eea;
  min-width: 100rpx;
}

.btn {
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 12rpx;
  padding: 16rpx 40rpx;
  font-size: 28rpx;
}

.computed-text {
  font-size: 32rpx;
  color: #52c41a;
  font-weight: bold;
}

.lifecycle-text {
  font-size: 28rpx;
  color: #333;
}

.desc-text {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
  line-height: 1.6;
}

.code-card {
  background: #1e1e1e;
}

.code-block {
  margin-bottom: 30rpx;
}

.code-block:last-child {
  margin-bottom: 0;
}

.code-title {
  display: block;
  font-size: 26rpx;
  color: #ffd93d;
  margin-bottom: 12rpx;
}

.code-content {
  display: block;
  font-size: 24rpx;
  color: #9cdcfe;
  background: #2d2d2d;
  padding: 20rpx;
  border-radius: 8rpx;
  line-height: 1.8;
  white-space: pre-wrap;
  font-family: 'Courier New', monospace;
}
</style>

<template>
  <view class="test-card">
    <view class="card-header">
      <text class="title">{{ title }}</text>
      <text class="subtitle">自动导入组件示例</text>
    </view>
    <view class="card-body">
      <slot>
        <text class="default-content">这是一个自动导入的组件，组件文件名 c-test-card.vue，使用时写成 &lt;c-test-card&gt;</text>
      </slot>
    </view>
    <view class="card-footer">
      <text class="tip">💡 组件以 c- 开头即可自动导入，无需手动 import</text>
    </view>
  </view>
</template>

<script setup lang="ts">
// 不需要手动导入 ref 等 API，已经配置自动导入
const props = defineProps<{
  title?: string
}>()

// 使用默认标题
const title = props.title || '测试卡片'
</script>

<style scoped>
.test-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  padding: 30rpx;
  margin: 20rpx 0;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.15);
}

.card-header {
  border-bottom: 2rpx solid rgba(255, 255, 255, 0.2);
  padding-bottom: 20rpx;
  margin-bottom: 20rpx;
}

.title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.subtitle {
  display: block;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
}

.card-body {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12rpx;
  padding: 30rpx;
}

.default-content {
  font-size: 28rpx;
  color: #fff;
  line-height: 1.6;
}

.card-footer {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid rgba(255, 255, 255, 0.2);
}

.tip {
  font-size: 24rpx;
  color: #ffd93d;
}
</style>

<template>
  <view class="container">
    <view class="header">
      <text class="header-title">🌐 HTTP 请求演示</text>
      <text class="header-subtitle">接口调用示例</text>
    </view>

    <view class="env-info">
      <view class="env-item">
        <text class="env-label">当前环境:</text>
        <text class="env-value">{{ currentEnv }}</text>
      </view>
      <view class="env-item">
        <text class="env-label">API 基础地址:</text>
        <text class="env-value">{{ baseUrl }}</text>
      </view>
    </view>

    <view class="section">
      <view class="card">
        <view class="api-header">
          <view class="api-method-badge get">GET</view>
          <text class="api-url">{{ baseUrl }}/user/isLogin</text>
        </view>
        <view class="api-desc">检查用户登录状态</view>
        <u-button type="primary" @click="handleCheckLogin">检查登录状态</u-button>
        <view v-if="loginStatusResult" class="result-box">
          <view class="result-header">
            <text class="result-title">响应结果</text>
            <text class="result-status success">成功</text>
          </view>
          <scroll-view scroll-y class="result-content">
            <text>{{ loginStatusResult }}</text>
          </scroll-view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="card">
        <view class="api-header">
          <view class="api-method-badge post">POST</view>
          <text class="api-url">{{ baseUrl }}/user/login</text>
        </view>
        <view class="api-desc">用户登录</view>
        <view class="form-group">
          <u-input v-model="loginForm.username" placeholder="用户名"></u-input>
          <u-input v-model="loginForm.password" placeholder="密码" password class="mt-16"></u-input>
        </view>
        <u-button type="success" @click="handleLogin">执行登录</u-button>
        <view v-if="loginResult" class="result-box">
          <view class="result-header">
            <text class="result-title">响应结果</text>
            <text class="result-status" :class="loginSuccess ? 'success' : 'error'">
              {{ loginSuccess ? '成功' : '失败' }}
            </text>
          </view>
          <scroll-view scroll-y class="result-content">
            <text>{{ loginResult }}</text>
          </scroll-view>
        </view>
      </view>
    </view>

    <view class="footer">
      <text>请求地址已配置在环境变量中</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { currentEnv, baseUrl } from '@/env'

const loginForm = ref({
  username: '',
  password: ''
})

const loginStatusResult = ref('')
const loginResult = ref('')
const loginSuccess = ref(false)

async function handleCheckLogin() {
  try {
    const result = await userApi.isLogin();
    console.log("==isLogin=",result)
    loginStatusResult.value = JSON.stringify(result, null, 2)
  } catch (error: any) {
    console.log("==error=",error)
    loginStatusResult.value = `请求失败: ${error.msg || '网络错误'}`
  }
}

async function handleLogin() {
  try {
    const result = await userApi.login(loginForm.value)
    loginResult.value = JSON.stringify(result, null, 2)
    loginSuccess.value = true
  } catch (error: any) {
    loginResult.value = `请求失败: ${error.msg || '网络错误'}`
    loginSuccess.value = false
  }
}
</script>

<style scoped lang="scss">
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60rpx;
}

.header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  padding: 40rpx 30rpx;
  text-align: center;
}

.header-title {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: white;
}

.header-subtitle {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 10rpx;
}

.env-info {
  margin: 20rpx;
  padding: 20rpx;
  background: white;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.env-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
  
  &:not(:last-child) {
    border-bottom: 1rpx solid #f0f0f0;
  }
}

.env-label {
  font-size: 26rpx;
  color: #666;
}

.env-value {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
  max-width: 400rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.section {
  padding: 0 20rpx;
  margin-bottom: 20rpx;
}

.card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.api-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.api-method-badge {
  font-size: 22rpx;
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
  color: white;
  font-weight: bold;
  
  &.get {
    background-color: #52c41a;
  }
  
  &.post {
    background-color: #007aff;
  }
}

.api-url {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.api-desc {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 24rpx;
}

.form-group {
  margin-bottom: 24rpx;
}

.mt-16 {
  margin-top: 16rpx;
}

.result-box {
  margin-top: 24rpx;
  background-color: #f8f9fa;
  border-radius: 12rpx;
  overflow: hidden;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 20rpx;
  background-color: #e9ecef;
}

.result-title {
  font-size: 26rpx;
  color: #666;
}

.result-status {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 6rpx;
  
  &.success {
    background-color: #d4edda;
    color: #155724;
  }
  
  &.error {
    background-color: #f8d7da;
    color: #721c24;
  }
}

.result-content {
  padding: 20rpx;
  max-height: 300rpx;
}

.result-content text {
  font-size: 24rpx;
  color: #333;
  white-space: pre-wrap;
  word-break: break-all;
}

.footer {
  text-align: center;
  padding: 40rpx 20rpx;
  color: #999;
  font-size: 24rpx;
}
</style>

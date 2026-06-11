<template>
  <view class="container">
    <!-- 页面标题 -->
    <view class="header">
      <text class="header-icon">🍍</text>
      <text class="header-title">Pinia 状态管理演示</text>
    </view>

    <!-- ========== 示例1: 计数器 ========== -->
    <view class="section">
      <view class="section-title">
        <text class="tag">示例1</text>
        <text>计数器 (State + Getter + Action)</text>
      </view>

      <view class="card">
        <view class="counter-box">
          <view class="counter-item">
            <text class="label">当前数值 (state)</text>
            <text class="value">{{ counter.count }}</text>
          </view>
          <view class="counter-item">
            <text class="label">双倍数值 (getter)</text>
            <text class="value highlight">{{ counter.doubleCount }}</text>
          </view>
        </view>

        <view class="btn-row">
          <button class="btn primary" @click="counter.increment()">+1</button>
          <button class="btn danger" @click="counter.decrement()">-1</button>
          <button class="btn default" @click="counter.reset()">重置</button>
        </view>

        <view class="code-hint">
          <text class="hint-text">💡 点击按钮调用 action 修改 state</text>
        </view>
      </view>
    </view>

    <!-- ========== 示例2: 购物车 ========== -->
    <view class="section">
      <view class="section-title">
        <text class="tag">示例2</text>
        <text>购物车 (多个 State 管理)</text>
      </view>

      <view class="card">
        <!-- 商品列表 -->
        <view class="goods-list">
          <view class="goods-item" v-for="item in goodsList" :key="item.id">
            <view class="goods-info">
              <text class="goods-name">{{ item.name }}</text>
              <text class="goods-price">¥{{ item.price }}</text>
            </view>
            <button class="btn small success" @click="cart.addItem(item)">
              加入购物车
            </button>
          </view>
        </view>

        <!-- 购物车信息 -->
        <view class="cart-info">
          <view class="cart-row">
            <text>商品数量:</text>
            <text class="cart-num">{{ cart.totalCount }} 件</text>
          </view>
          <view class="cart-row">
            <text>总计金额:</text>
            <text class="cart-total">¥{{ cart.totalPrice }}</text>
          </view>
        </view>

        <!-- 购物车商品 -->
        <view class="cart-items" v-if="cart.items.length > 0">
          <view class="cart-title">已选商品:</view>
          <view class="cart-item" v-for="item in cart.items" :key="item.id">
            <text class="item-name">{{ item.name }}</text>
            <view class="item-control">
              <text class="minus" @click="cart.decrease(item.id)">-</text>
              <text class="num">{{ item.quantity }}</text>
              <text class="plus" @click="cart.increase(item.id)">+</text>
              <text class="delete" @click="cart.removeItem(item.id)">删除</text>
            </view>
          </view>
          <button class="btn block danger mt-20" @click="cart.clear()">清空购物车</button>
        </view>

        <view class="empty-cart" v-else>
          <text class="empty-text">购物车是空的，快去选购吧~</text>
        </view>
      </view>
    </view>

    <!-- ========== 示例3: 用户登录 (持久化) ========== -->
    <view class="section">
      <view class="section-title">
        <text class="tag">示例3</text>
        <text>用户登录 (数据持久化)</text>
      </view>

      <view class="card">
        <!-- 登录状态 -->
        <view class="login-status">
          <view class="status-dot" :class="user.isLoggedIn ? 'online' : 'offline'"></view>
          <text class="status-text">
            {{ user.isLoggedIn ? '已登录' : '未登录' }}
          </text>
        </view>

        <!-- 用户信息 -->
        <view class="user-panel" v-if="user.isLoggedIn">
          <view class="user-row">
            <text class="user-label">欢迎语:</text>
            <text class="user-value">{{ user.welcomeText }}</text>
          </view>
          <view class="user-row">
            <text class="user-label">用户ID:</text>
            <text class="user-value">{{ user.userInfo.userId }}</text>
          </view>
		  <view class="user-row">
		    <text class="user-label">用户Token:</text>
		    <text class="user-value">{{ user.userInfo.token }}</text>
		  </view>
          <view class="user-row">
            <text class="user-label">用户名:</text>
            <text class="user-value">{{ user.userInfo.nickname }}</text>
          </view>
          <view class="user-row">
            <text class="user-label">手机号:</text>
            <text class="user-value">{{ user.userInfo.phone }}</text>
          </view>

          <view class="btn-row mt-20">
            <button class="btn warning" @click="randomUpdateName">随机改名</button>
            <button class="btn danger" @click="user.logout()">退出登录</button>
          </view>
        </view>

        <!-- 登录表单 -->
        <view class="login-form" v-else>
          <input
            class="input"
            v-model="loginForm.name"
            placeholder="请输入用户名"
          />
          <input
            class="input"
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            type="number"
          />
          <button class="btn block success mt-20" @click="handleLogin">
            立即登录
          </button>
        </view>

        <view class="persist-tip">
          <text class="tip-icon">💾</text>
          <text class="tip-text">用户数据已持久化，重启应用后仍然保留</text>
        </view>
      </view>
    </view>

    <!-- ========== 示例4: $patch 批量更新 ========== -->
    <view class="section">
      <view class="section-title">
        <text class="tag">示例4</text>
        <text>$patch 批量更新</text>
      </view>

      <view class="card">
        <view class="patch-demo">
          <text class="patch-desc">同时修改多个 store 的数据:</text>
          <button class="btn primary block" @click="batchUpdate">
            执行 $patch 批量更新
          </button>
        </view>
      </view>
    </view>

    <!-- 底部说明 -->
    <view class="footer">
      <text class="footer-text">查看控制台输出状态变化日志</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useCounterStore } from '@/stores/counter'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

// 使用 store
const counter = useCounterStore()
const user = useUserStore()
const cart = useCartStore()

// 登录表单
const loginForm = ref({
  name: '',
  phone: ''
})

// 示例商品数据
const goodsList = ref([
  { id: 1, name: '🍎 苹果手机', price: 6999 },
  { id: 2, name: '📱 华为手机', price: 5999 },
  { id: 3, name: '💻 MacBook Pro', price: 12999 },
  { id: 4, name: '🎧 AirPods Pro', price: 1899 },
])

// 登录处理
function handleLogin() {
  if (!loginForm.value.name || !loginForm.value.phone) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  user.login('token_' + Date.now(), {
    id: 10000 + Math.floor(Math.random() * 90000),
    name: loginForm.value.name,
    phone: loginForm.value.phone
  })
  loginForm.value = { name: '', phone: '' }
  uni.showToast({ title: '登录成功', icon: 'success' })
}

// 随机改名
function randomUpdateName() {
  const names = ['张三', '李四', '王五', '赵六', '小明', '小红']
  const newName = names[Math.floor(Math.random() * names.length)]
  user.updateName(newName)
  uni.showToast({ title: '已更名为' + newName, icon: 'none' })
}

// 批量更新示例
function batchUpdate() {
  // 方式1: 对象形式
  counter.$patch({ count: 88 })

  // 方式2: 函数形式 (可以修改嵌套数据)
  cart.$patch((state) => {
    state.items.push({
      id: 999,
      name: '批量添加的商品',
      price: 100,
      quantity: 1
    })
  })

  uni.showToast({ title: '批量更新完成', icon: 'success' })
}

// 订阅状态变化
onMounted(() => {
  console.log('========== Pinia 状态管理 ==========')
  console.log('计数器初始值:', counter.count)
  console.log('购物车商品数:', cart.totalCount)
  console.log('登录状态:', user.isLoggedIn)

  // 监听计数器变化
  counter.$subscribe((mutation, state) => {
    console.log('【计数器变化】', mutation.type, '=>', state.count)
  })

  // 监听购物车变化
  cart.$subscribe((mutation, state) => {
    console.log('【购物车变化】', mutation.type, '=>', state.items.length, '件商品')
  })

  // 监听用户变化
  user.$subscribe((mutation, state) => {
	  console.log("user",user.userInfo)
    console.log('【用户状态变化】', mutation.type, '=>', state.userInfo.name)
  })
})
</script>

<style scoped>
.container {
  padding: 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

/* 头部 */
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
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

/* 区块 */
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

/* 卡片 */
.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.15);
}

/* 计数器 */
.counter-box {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30rpx;
}

.counter-item {
  text-align: center;
}

.label {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.value {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
}

.value.highlight {
  color: #667eea;
}

/* 按钮 */
.btn-row {
  display: flex;
  gap: 20rpx;
}

.btn {
  flex: 1;
  height: 76rpx;
  line-height: 76rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  border: none;
  color: #fff;
}

.btn.small {
  flex: none;
  height: 56rpx;
  line-height: 56rpx;
  font-size: 24rpx;
  padding: 0 24rpx;
}

.btn.block {
  width: 100%;
}

.btn.primary { background: #667eea; }
.btn.success { background: #52c41a; }
.btn.danger { background: #ff4d4f; }
.btn.warning { background: #faad14; }
.btn.default { background: #d9d9d9; color: #333; }

.mt-20 { margin-top: 20rpx; }

/* 提示 */
.code-hint {
  margin-top: 20rpx;
  padding: 16rpx;
  background: #f6ffed;
  border-radius: 8rpx;
}

.hint-text {
  font-size: 24rpx;
  color: #52c41a;
}

/* 商品列表 */
.goods-list {
  margin-bottom: 30rpx;
}

.goods-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.goods-info {
  flex: 1;
}

.goods-name {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 8rpx;
}

.goods-price {
  display: block;
  font-size: 32rpx;
  color: #ff4d4f;
  font-weight: bold;
}

/* 购物车信息 */
.cart-info {
  background: #f6ffed;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.cart-row {
  display: flex;
  justify-content: space-between;
  padding: 10rpx 0;
  font-size: 28rpx;
}

.cart-num {
  color: #52c41a;
  font-weight: bold;
}

.cart-total {
  color: #ff4d4f;
  font-size: 36rpx;
  font-weight: bold;
}

/* 购物车商品 */
.cart-title {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx;
  background: #fafafa;
  border-radius: 8rpx;
  margin-bottom: 12rpx;
}

.item-name {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.item-control {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.minus, .plus {
  width: 44rpx;
  height: 44rpx;
  line-height: 44rpx;
  text-align: center;
  background: #667eea;
  color: #fff;
  border-radius: 50%;
  font-size: 28rpx;
}

.num {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  min-width: 40rpx;
  text-align: center;
}

.delete {
  font-size: 24rpx;
  color: #ff4d4f;
  margin-left: 20rpx;
}

.empty-cart {
  text-align: center;
  padding: 40rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 登录状态 */
.login-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  margin-bottom: 30rpx;
}

.status-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
}

.status-dot.online {
  background: #52c41a;
  box-shadow: 0 0 10rpx #52c41a;
}

.status-dot.offline {
  background: #d9d9d9;
}

.status-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

/* 用户信息面板 */
.user-panel {
  background: #f6ffed;
  border-radius: 12rpx;
  padding: 20rpx;
}

.user-row {
  display: flex;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #e8f5e9;
}

.user-row:last-child {
  border-bottom: none;
}

.user-label {
  width: 140rpx;
  font-size: 26rpx;
  color: #666;
}

.user-value {
  flex: 1;
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}

/* 登录表单 */
.login-form {
  padding: 20rpx 0;
}

.input {
  height: 88rpx;
  border: 2rpx solid #d9d9d9;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  margin-bottom: 20rpx;
}

.input:focus {
  border-color: #667eea;
}

/* 持久化提示 */
.persist-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-top: 30rpx;
  padding: 20rpx;
  background: #e6f7ff;
  border-radius: 8rpx;
}

.tip-icon {
  font-size: 32rpx;
}

.tip-text {
  font-size: 24rpx;
  color: #1890ff;
}

/* patch 示例 */
.patch-demo {
  text-align: center;
}

.patch-desc {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
}

/* 底部 */
.footer {
  text-align: center;
  padding: 40rpx 0;
}

.footer-text {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}
</style>

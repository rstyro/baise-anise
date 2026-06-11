<template>
  <view class="container">
    <u-sticky>
      <view class="header">
        <text class="header-title">🎨 uView Pro 组件演示</text>
        <text class="header-subtitle">精选常用组件示例</text>
      </view>
    </u-sticky>

    <view class="section">
      <view class="section-title">Button 按钮</view>
      <view class="card">
        <view class="btn-row">
          <u-button type="primary">主要按钮</u-button>
          <u-button type="success">成功按钮</u-button>
          <u-button type="warning">警告按钮</u-button>
          <u-button type="error">危险按钮</u-button>
        </view>
        <view class="btn-row mt-20">
          <u-button type="primary" plain>镂空按钮</u-button>
          <u-button type="primary" disabled>禁用按钮</u-button>
          <u-button type="primary" shape="circle">圆角按钮</u-button>
          <u-button type="primary" size="small">小按钮</u-button>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Form 表单</view>
      <view class="card">
        <u-input v-model="formData.username" placeholder="请输入用户名"></u-input>
        <u-input v-model="formData.password" placeholder="请输入密码" password class="mt-20"></u-input>
        <view class="form-row mt-30">
          <u-switch v-model="formData.remember" active-color="#007aff" inactive-color="#d9d9d9"></u-switch>
          <text>记住密码</text>
        </view>
        <u-button type="primary" size="large" class="mt-30" @click="handleSubmit">提交表单</u-button>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Slider & Rate</view>
      <view class="card">
        <view class="slider-box">
          <text class="slider-label">滑块选择: {{ sliderValue }}</text>
          <u-slider v-model="sliderValue" active-color="#007aff" :height="8"></u-slider>
        </view>
        <view class="rate-box mt-30">
          <text class="rate-label">评分: {{ rateValue }} 星</text>
          <u-rate v-model="rateValue" active-color="#ffd700" void-color="#e0e0e0"></u-rate>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Tag & Badge</view>
      <view class="card">
        <view class="tag-group">
          <u-tag type="primary" text="标签一"></u-tag>
          <u-tag type="success" text="标签二"></u-tag>
          <u-tag type="warning" text="标签三"></u-tag>
          <u-tag type="error" text="标签四"></u-tag>
          <u-tag type="info" text="标签五"></u-tag>
        </view>
        <view class="badge-group mt-30">
          <u-badge :count="6">
            <u-button type="primary" size="mini">消息</u-button>
          </u-badge>
          <u-badge :count="100" :max-count="99">
            <u-button type="success" size="mini">超过99</u-button>
          </u-badge>
          <u-badge dot>
            <u-button type="warning" size="mini">红点</u-button>
          </u-badge>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Progress 进度条</view>
      <view class="card">
        <view class="progress-box">
          <text class="progress-label">线性进度</text>
          <u-line-progress :percent="60" active-color="#007aff"></u-line-progress>
        </view>
        <view class="circle-progress mt-30">
          <u-circle-progress :percent="progressValue" :width="120" active-color="#ff9500"></u-circle-progress>
          <text class="circle-label">{{ progressValue }}%</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Avatar 头像</view>
      <view class="card">
        <view class="avatar-row">
          <u-avatar src="https://picsum.photos/id/237/100" size="large"></u-avatar>
          <u-avatar src="https://picsum.photos/id/1/100" size="medium"></u-avatar>
          <u-avatar src="https://picsum.photos/id/10/100" size="small"></u-avatar>
          <u-avatar size="mini">默认</u-avatar>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Swiper 轮播图</view>
      <view class="card">
        <u-swiper :list="swiperList" :height="200" indicator-mode="line"></u-swiper>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Divider 分割线</view>
      <view class="card">
        <u-divider>我是分割线</u-divider>
        <u-divider dashed color="#ff0000" class="mt-20">虚线红色</u-divider>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Modal 对话框</view>
      <view class="card">
        <u-button type="primary" @click="showModal = true">打开弹窗</u-button>
        <u-modal v-model="showModal" title="提示" content="这是一个弹窗示例" @confirm="showModal = false"></u-modal>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Action Sheet 底部菜单</view>
      <view class="card">
        <u-button type="success" @click="showActionSheet = true">显示操作菜单</u-button>
        <u-action-sheet v-model="showActionSheet" :actions="actionList" description="请选择操作" @select="onActionSelect"></u-action-sheet>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Toast 消息提示</view>
      <view class="card">
        <view class="btn-row">
          <u-button type="primary" @click="showToast('default')">默认提示</u-button>
          <u-button type="success" @click="showToast('success')">成功提示</u-button>
          <u-button type="error" @click="showToast('error')">错误提示</u-button>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Icon 图标</view>
      <view class="card">
        <view class="icon-group">
          <view class="icon-item">
            <u-icon name="account" size="40"></u-icon>
            <text>account</text>
          </view>
          <view class="icon-item">
            <u-icon name="lock" size="40"></u-icon>
            <text>lock</text>
          </view>
          <view class="icon-item">
            <u-icon name="phone" size="40"></u-icon>
            <text>phone</text>
          </view>
          <view class="icon-item">
            <u-icon name="setting" size="40"></u-icon>
            <text>setting</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Grid 网格</view>
      <view class="card">
        <u-grid :column="4">
          <u-grid-item v-for="i in 8" :key="i">
            <u-icon name="star" size="48" color="#007aff"></u-icon>
            <text class="grid-text">{{ i }}</text>
          </u-grid-item>
        </u-grid>
      </view>
    </view>

    <view class="section">
      <view class="section-title">Loading 加载</view>
      <view class="card">
        <view class="loading-group">
          <u-loading mode="circle" color="#007aff"></u-loading>
          <u-loading mode="spinner" color="#52c41a"></u-loading>
        </view>
      </view>
    </view>


    <view class="footer">
      <text>更多组件请参考官方文档: https://uiadmin.net/uview-pro/</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted,getCurrentInstance } from 'vue'

const formData = ref({
  username: '',
  password: '',
  remember: false
})

const sliderValue = ref(50)
const rateValue = ref(3)
const progressValue = ref(0)
const showModal = ref(false)
const showActionSheet = ref(false)

const actionList = ref([
  { text: '选项一', name: 1 },
  { text: '选项二', name: 2 },
  { text: '选项三', name: 3 }
])

const swiperList = ref([
  { image: 'https://picsum.photos/id/1015/800/400', title: '图片一' },
  { image: 'https://picsum.photos/id/1016/800/400', title: '图片二' },
  { image: 'https://picsum.photos/id/1018/800/400', title: '图片三' }
])

function handleSubmit() {
  uni.showToast({
    title: '表单已提交',
    icon: 'success'
  })
}

function onActionSelect(index: number) {
  uni.showToast({
    title: `选择了${actionList.value[index].text}`,
    icon: 'none'
  })
}

function showToast(type: string) {
  uni.$u.toast(`这是一条${type}消息`, 2000, {
    type: type as any,
    position: 'center'
  })
}

onMounted(() => {
  const timer = setInterval(() => {
    if (progressValue.value < 100) {
      progressValue.value += 2
    } else {
      progressValue.value = 0
    }
  }, 100)
})
</script>

<style scoped lang="scss">
.container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80rpx;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30rpx;
  border-radius: 0 0 20rpx 20rpx;
}

.header-title {
  display: block;
  text-align: center;
  color: white;
  font-size: 36rpx;
  font-weight: bold;
}

.header-subtitle {
  display: block;
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 24rpx;
  margin-top: 10rpx;
}

.section {
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  padding-left: 10rpx;
  border-left: 6rpx solid #007aff;
}

.card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.btn-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.btn-row :deep(.u-button) {
  flex: 1;
  min-width: 120rpx;
}

.mt-20 { margin-top: 20rpx; }
.mt-30 { margin-top: 30rpx; }

.form-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.slider-box, .rate-box {
  padding: 10rpx 0;
}

.slider-label, .rate-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.badge-group {
  display: flex;
  gap: 30rpx;
  align-items: center;
  justify-content: center;
}

.progress-box {
  padding: 10rpx 0;
}

.progress-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.circle-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.circle-label {
  font-size: 28rpx;
  color: #ff9500;
  font-weight: bold;
  margin-top: 10rpx;
}

.avatar-row {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.icon-group {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 150rpx;
}

.icon-item text {
  font-size: 22rpx;
  color: #666;
  margin-top: 10rpx;
}

.grid-text {
  font-size: 24rpx;
  color: #666;
}

.loading-group {
  display: flex;
  justify-content: center;
  gap: 60rpx;
}

.footer {
  text-align: center;
  padding: 40rpx 20rpx;
  color: #999;
  font-size: 24rpx;
}
</style>
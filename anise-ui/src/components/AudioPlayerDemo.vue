<template>
  <view class="audio-demo-container">
    <view class="demo-header">
      <text class="title">音频播放演示</text>
      <text class="subtitle">Audio Player Demo</text>
    </view>

    <view class="audio-card">
      <view class="audio-info">
        <text class="audio-title">{{ audioTitle }}</text>
        <text class="audio-artist">音频演示 - Audio Demo</text>
      </view>

      <view class="progress-section">
        <view class="progress-bar" @click="onProgressClick">
          <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          <view class="progress-thumb" :style="{ left: progressPercent + '%' }"></view>
        </view>
        <view class="time-display">
          <text class="current-time">{{ formatTime(currentTime) }}</text>
          <text class="duration">{{ formatTime(duration) }}</text>
        </view>
      </view>

      <view class="controls">
        <view class="control-btn" @click="toggleLoop">
          <text class="icon">{{ isLooping ? '🔄' : '↺' }}</text>
        </view>
        <view class="control-btn" @click="handlePrev">
          <text class="icon">⏮</text>
        </view>
        <view class="control-btn play-btn" @click="togglePlay">
          <text class="icon">{{ isPlaying ? '⏸' : '▶' }}</text>
        </view>
        <view class="control-btn" @click="handleNext">
          <text class="icon">⏭</text>
        </view>
        <view class="control-btn" @click="showVolumeModal">
          <text class="icon">🔊</text>
        </view>
      </view>

      <view class="volume-control">
        <text class="volume-label">音量 Volume</text>
        <view class="volume-bar" @click="onVolumeClick">
          <view class="volume-fill" :style="{ width: (volume * 100) + '%' }"></view>
        </view>
        <text class="volume-value">{{ Math.round(volume * 100) }}%</text>
      </view>
    </view>

    <view class="demo-actions">
      <view class="action-btn" :class="{ active: isPlaying }" @click="togglePlay">
        <text>{{ isPlaying ? '暂停播放' : '开始播放' }}</text>
      </view>
      <view class="action-btn" @click="stopAudio">
        <text>停止播放</text>
      </view>
      <view class="action-btn" @click="destroyAudio">
        <text>销毁音频</text>
      </view>
    </view>

    <view class="status-panel">
      <text class="status-title">状态信息</text>
      <view class="status-item">
        <text class="status-label">音频状态：</text>
        <text class="status-value" :class="isPlaying ? 'playing' : 'paused'">
          {{ isPlaying ? '播放中' : '已暂停' }}
        </text>
      </view>
      <view class="status-item">
        <text class="status-label">循环播放：</text>
        <text class="status-value">{{ isLooping ? '开启' : '关闭' }}</text>
      </view>
      <view class="status-item">
        <text class="status-label">音量大小：</text>
        <text class="status-value">{{ Math.round(volume * 100) }}%</text>
      </view>
      <view class="status-item">
        <text class="status-label">当前时间：</text>
        <text class="status-value">{{ formatTime(currentTime) }}</text>
      </view>
      <view class="status-item">
        <text class="status-label">总时长：</text>
        <text class="status-value">{{ formatTime(duration) }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import audioUtils from '@/utils/global/audio'

const audioId = 'demo-audio'
const audioSrc = '/static/sound/sound1.aac'

const audioTitle = ref('SoundHelix Demo Track')
const isPlaying = ref(false)
const isLooping = ref(false)
const volume = ref(0.7)
const currentTime = ref(0)
const duration = ref(0)
const audioLoaded = ref(false)

const progressPercent = computed(() => {
  if (duration.value === 0) return 0
  return (currentTime.value / duration.value) * 100
})

let updateTimer: ReturnType<typeof setInterval> | null = null

const initAudio = async () => {
  try {
    await audioUtils.createAudio(audioId, audioSrc)
    audioLoaded.value = true
    await audioUtils.setVolume(audioId, volume.value)
    duration.value = audioUtils.getDuration(audioId)
    uni.showToast({ title: '音频加载成功', icon: 'success' })
  } catch (error) {
    console.error('音频初始化失败:', error)
    uni.showToast({ title: '音频加载失败', icon: 'none' })
  }
}

const togglePlay = () => {
  if (!audioLoaded.value) {
    uni.showToast({ title: '请先加载音频', icon: 'none' })
    return
  }

  if (isPlaying.value) {
    audioUtils.pause(audioId)
    isPlaying.value = false
  } else {
    audioUtils.play(audioId)
    isPlaying.value = true
    startUpdateTimer()
  }
}

const stopAudio = () => {
  if (!audioLoaded.value) return
  audioUtils.stop(audioId)
  isPlaying.value = false
  currentTime.value = 0
  stopUpdateTimer()
}

const destroyAudio = () => {
  if (!audioLoaded.value) return
  audioUtils.destroyAudio(audioId)
  audioLoaded.value = false
  isPlaying.value = false
  currentTime.value = 0
  duration.value = 0
  stopUpdateTimer()
  uni.showToast({ title: '音频已销毁', icon: 'success' })
}

const toggleLoop = () => {
  if (!audioLoaded.value) return
  isLooping.value = !isLooping.value
  audioUtils.setLoop(audioId, isLooping.value)
  uni.showToast({
    title: isLooping.value ? '循环播放已开启' : '循环播放已关闭',
    icon: 'none'
  })
}

const handlePrev = () => {
  if (!audioLoaded.value) return
  const newTime = Math.max(0, currentTime.value - 10)
  audioUtils.seek(audioId, newTime)
  currentTime.value = newTime
}

const handleNext = () => {
  if (!audioLoaded.value) return
  const newTime = Math.min(duration.value, currentTime.value + 10)
  audioUtils.seek(audioId, newTime)
  currentTime.value = newTime
}

const onProgressClick = (e: TouchEvent) => {
  if (!audioLoaded.value || duration.value === 0) return
  const target = e.currentTarget as HTMLElement
  const rect = target.getBoundingClientRect?.() || { width: 300, left: 0 }
  const clickX = (e as unknown as { detail: { x: number } }).detail.x || 0
  const percent = (clickX - rect.left) / rect.width
  const newTime = Math.max(0, Math.min(duration.value, percent * duration.value))
  audioUtils.seek(audioId, newTime)
  currentTime.value = newTime
}

const onVolumeClick = (e: TouchEvent) => {
  if (!audioLoaded.value) return
  const target = e.currentTarget as HTMLElement
  const rect = target.getBoundingClientRect?.() || { width: 200, left: 0 }
  const clickX = (e as unknown as { detail: { x: number } }).detail.x || 0
  const percent = (clickX - rect.left) / rect.width
  const newVolume = Math.max(0, Math.min(1, percent))
  volume.value = newVolume
  audioUtils.setVolume(audioId, newVolume)
}

const showVolumeModal = () => {
  uni.showModal({
    title: '音量设置',
    content: `当前音量: ${Math.round(volume.value * 100)}%`,
    showCancel: false
  })
}

const formatTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const startUpdateTimer = () => {
  if (updateTimer) return
  updateTimer = setInterval(() => {
    if (audioLoaded.value && isPlaying.value) {
      currentTime.value = audioUtils.getCurrentTime(audioId)
      duration.value = audioUtils.getDuration(audioId)
      
      if (!audioUtils.isPaused(audioId) === false && currentTime.value >= duration.value && !isLooping.value) {
        isPlaying.value = false
        stopUpdateTimer()
      }
    }
  }, 100)
}

const stopUpdateTimer = () => {
  if (updateTimer) {
    clearInterval(updateTimer)
    updateTimer = null
  }
}

onMounted(() => {
  initAudio()
})

onUnmounted(() => {
  stopUpdateTimer()
  if (audioLoaded.value) {
    audioUtils.destroyAudio(audioId)
  }
})
</script>

<style lang="scss" scoped>
.audio-demo-container {
  padding: 30rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.demo-header {
  text-align: center;
  margin-bottom: 40rpx;
  padding-top: 40rpx;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 10rpx;
}

.subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.audio-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
}

.audio-info {
  text-align: center;
  margin-bottom: 30rpx;
}

.audio-title {
  display: block;
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.audio-artist {
  font-size: 26rpx;
  color: #999;
}

.progress-section {
  margin-bottom: 30rpx;
}

.progress-bar {
  position: relative;
  height: 8rpx;
  background: #e8e8e8;
  border-radius: 4rpx;
  margin-bottom: 15rpx;
}

.progress-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4rpx;
  transition: width 0.1s ease;
}

.progress-thumb {
  position: absolute;
  top: 50%;
  width: 24rpx;
  height: 24rpx;
  background: #fff;
  border: 4rpx solid #667eea;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: left 0.1s ease;
  box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.4);
}

.time-display {
  display: flex;
  justify-content: space-between;
}

.current-time, .duration {
  font-size: 24rpx;
  color: #999;
}

.controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40rpx;
  margin: 40rpx 0;
}

.control-btn {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f5f5;
  transition: all 0.3s ease;

  .icon {
    font-size: 36rpx;
  }

  &:active {
    transform: scale(0.95);
    background: #e8e8e8;
  }
}

.play-btn {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .icon {
    font-size: 48rpx;
    color: #fff;
  }

  &:active {
    transform: scale(0.95);
  }
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.volume-label {
  font-size: 24rpx;
  color: #999;
  width: 120rpx;
}

.volume-bar {
  flex: 1;
  height: 12rpx;
  background: #e8e8e8;
  border-radius: 6rpx;
}

.volume-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 6rpx;
  transition: width 0.1s ease;
}

.volume-value {
  font-size: 24rpx;
  color: #667eea;
  width: 80rpx;
  text-align: right;
}

.demo-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 30rpx;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 44rpx;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;

  text {
    font-size: 28rpx;
    color: #fff;
    font-weight: 500;
  }

  &:active {
    background: rgba(255, 255, 255, 0.3);
    transform: scale(0.98);
  }

  &.active {
    background: rgba(255, 255, 255, 0.3);
  }
}

.status-panel {
  margin-top: 30rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20rpx;
  padding: 30rpx;
}

.status-title {
  display: block;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  padding-bottom: 15rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.status-item {
  display: flex;
  justify-content: space-between;
  padding: 15rpx 0;
}

.status-label {
  font-size: 26rpx;
  color: #666;
}

.status-value {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;

  &.playing {
    color: #667eea;
  }

  &.paused {
    color: #999;
  }
}
</style>

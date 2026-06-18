<template>
  <view class="page">
    <view class="profile-hero">
      <view class="hero-bg" />
      <view class="profile-card">
        <!-- #ifdef MP-WEIXIN -->
        <button class="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
          <image :src="displayAvatarUrl" class="avatar-img" mode="aspectFill" />
          <view class="avatar-edit-icon">
            <u-icon name="camera-fill" size="22" :color="THEME_TEXT_INVERSE" />
          </view>
        </button>
        <!-- #endif -->

        <!-- #ifndef MP-WEIXIN -->
        <view class="avatar-wrapper" @click="onH5ChooseAvatar">
          <image :src="displayAvatarUrl" class="avatar-img" mode="aspectFill" />
          <view class="avatar-edit-icon">
            <u-icon name="camera-fill" size="22" :color="THEME_TEXT_INVERSE" />
          </view>
        </view>
        <!-- #endif -->

        <view class="profile-main">
          <text class="nickname u-line-1">{{ userInfo.nickname || '未设置昵称' }}</text>
          <text class="profile-subtitle">{{ userInfo.phone || '资料待完善' }}</text>
        </view>
        <view class="login-badge" :class="{ active: isLoggedIn }">
          {{ isLoggedIn ? '已登录' : '未登录' }}
        </view>
      </view>
    </view>

    <view class="content">
      <view class="section-card info-section">
        <view class="section-title">基础资料</view>

        <view class="info-item" :class="{ clickable: !userInfo.username }" @click="!userInfo.username && editUsername()">
          <view class="info-left">
            <view class="info-icon primary">
              <u-icon name="user" size="20" :color="THEME_PRIMARY" />
            </view>
            <text class="info-label">用户名</text>
          </view>
          <view class="info-right">
            <text class="info-value u-line-1">{{ userInfo.username || '未设置' }}</text>
            <u-icon v-if="!userInfo.username" name="arrow-right" size="18" :color="THEME_TEXT_GREY" />
          </view>
        </view>

        <view class="info-item clickable" @click="editNickname">
          <view class="info-left">
            <view class="info-icon success">
              <u-icon name="account" size="20" :color="THEME_SUCCESS" />
            </view>
            <text class="info-label">昵称</text>
          </view>
          <view class="info-right">
            <text class="info-value u-line-1">{{ userInfo.nickname || '未设置' }}</text>
            <u-icon name="arrow-right" size="18" :color="THEME_TEXT_GREY" />
          </view>
        </view>

        <view class="info-item clickable" @click="editSex">
          <view class="info-left">
            <view class="info-icon warning">
              <u-icon name="man" size="20" :color="THEME_WARNING" />
            </view>
            <text class="info-label">性别</text>
          </view>
          <view class="info-right">
            <text class="info-value">{{ sexMap[userInfo.sex] || '保密' }}</text>
            <u-icon name="arrow-right" size="18" :color="THEME_TEXT_GREY" />
          </view>
        </view>

        <view class="info-item">
          <view class="info-left">
            <view class="info-icon success">
              <u-icon name="phone" size="20" :color="THEME_SUCCESS" />
            </view>
            <text class="info-label">手机号</text>
          </view>
          <view class="info-right">
            <text class="info-value u-line-1">{{ userInfo.phone || '未绑定' }}</text>
          </view>
        </view>

        <view class="info-item">
          <view class="info-left">
            <view class="info-icon muted">
              <u-icon name="grid" size="20" :color="THEME_TEXT_GREY" />
            </view>
            <text class="info-label">用户ID</text>
          </view>
          <view class="info-right">
            <text class="info-value">{{ userInfo.userId || '-' }}</text>
          </view>
        </view>
      </view>

      <view v-if="isLoggedIn" class="action-section">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </view>
    </view>

    <view v-if="showNicknameModal" class="edit-modal" @click="closeNicknameModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">修改昵称</text>
          <view class="close-btn" @click="closeNicknameModal">
            <u-icon name="close" size="24" :color="THEME_TEXT_GREY" />
          </view>
        </view>
        <view class="modal-body">
          <input
            v-model="tempNickname"
            class="text-input"
            placeholder="请输入昵称"
            placeholder-class="input-placeholder"
            type="nickname"
            maxlength="20"
          />
        </view>
        <view class="modal-footer">
          <button class="confirm-btn" @click="confirmNickname">确认修改</button>
        </view>
      </view>
    </view>

    <view v-if="showUsernameModal" class="edit-modal" @click="closeUsernameModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">设置用户名</text>
          <view class="close-btn" @click="closeUsernameModal">
            <u-icon name="close" size="24" :color="THEME_TEXT_GREY" />
          </view>
        </view>
        <view class="modal-body">
          <input
            v-model="tempUsername"
            class="text-input"
            placeholder="请输入用户名"
            placeholder-class="input-placeholder"
            type="text"
            maxlength="20"
          />
          <text class="input-hint">用户名设置后不可在当前页面再次修改</text>
        </view>
        <view class="modal-footer">
          <button class="confirm-btn" @click="confirmUsername">确认设置</button>
        </view>
      </view>
    </view>

    <view v-if="showSexModal" class="edit-modal" @click="closeSexModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">选择性别</text>
          <view class="close-btn" @click="closeSexModal">
            <u-icon name="close" size="24" :color="THEME_TEXT_GREY" />
          </view>
        </view>
        <view class="modal-body">
          <view class="sex-options">
            <view
              v-for="(option, index) in sexOptions"
              :key="option"
              class="sex-option"
              :class="{ active: tempSex === index }"
              @click="setTempSex(index)"
            >
              <view class="sex-check">
                <u-icon v-if="tempSex === index" name="check" size="20" :color="THEME_TEXT_INVERSE" />
              </view>
              <text class="sex-text">{{ option }}</text>
            </view>
          </view>
        </view>
        <view class="modal-footer">
          <button class="confirm-btn" @click="confirmSex">确认修改</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { userApi } from '@/api/userApi'
import { useUserStore } from '@/stores/user'
import { baseUrl } from '@/env'
import { getImageUrl } from '@/utils/image'
import { THEME_PRIMARY, THEME_SUCCESS, THEME_WARNING, THEME_TEXT_GREY, THEME_TEXT_INVERSE } from '@/styles/theme'

interface AppUserInfo {
  userId: number
  token: string
  username: string
  nickname: string
  avatarUrl: string
  phone: string
  sex: 0 | 1 | 2
}

interface ChooseAvatarEvent {
  detail?: {
    avatarUrl?: string
  }
}

interface ChooseImageResult {
  tempFilePaths: string[]
}

interface UploadAvatarResponse {
  code?: number
  success?: boolean
  data?: string
  url?: string
  message?: string
}

const userStore = useUserStore()

const userInfo = reactive<AppUserInfo>({
  userId: 0,
  token: '',
  username: '',
  nickname: '',
  avatarUrl: '',
  phone: '',
  sex: 0,
})

const isLoggedIn = ref(false)

const showNicknameModal = ref(false)
const tempNickname = ref('')

const showUsernameModal = ref(false)
const tempUsername = ref('')

const showSexModal = ref(false)
const tempSex = ref<0 | 1 | 2>(0)

const sexOptions = ['保密', '男', '女']
const sexMap: Record<number, string> = {
  0: '保密',
  1: '男',
  2: '女',
}

const displayAvatarUrl = computed(() => getImageUrl(userInfo.avatarUrl))

onMounted(async () => {
  await loadUserInfo()
})

const showToast = (title: string, icon: 'none' | 'success' = 'none') => {
  uni.showToast({ title, icon })
}

const loadUserInfo = async () => {
  try {
    const res = await userApi.getUserInfo() as Partial<AppUserInfo>
    Object.assign(userInfo, {
      userId: res.userId || 0,
      token: res.token || '',
      username: res.username || '',
      nickname: res.nickname || '',
      avatarUrl: res.avatarUrl || '',
      phone: res.phone || '',
      sex: (res.sex ?? 0) as 0 | 1 | 2,
    })
    userStore.login(userInfo.token || userStore.token, userInfo)
    isLoggedIn.value = true
  } catch (error) {
    console.error('获取用户信息失败:', error)
    isLoggedIn.value = false
  }
}

const editNickname = () => {
  tempNickname.value = userInfo.nickname
  showNicknameModal.value = true
}

const editUsername = () => {
  tempUsername.value = userInfo.username
  showUsernameModal.value = true
}

const editSex = () => {
  tempSex.value = userInfo.sex
  showSexModal.value = true
}

const setTempSex = (index: number) => {
  if (index === 0 || index === 1 || index === 2) {
    tempSex.value = index
  }
}

const closeNicknameModal = () => {
  showNicknameModal.value = false
}

const closeUsernameModal = () => {
  showUsernameModal.value = false
}

const closeSexModal = () => {
  showSexModal.value = false
}

const confirmNickname = async () => {
  const nickname = tempNickname.value.trim()
  if (!nickname) {
    showToast('昵称不能为空')
    return
  }

  try {
    uni.showLoading({ title: '修改中...' })
    await userApi.updateUserInfo({ nickname } as any)
    userInfo.nickname = nickname
    userStore.updateName(nickname)
    showNicknameModal.value = false
    showToast('修改成功', 'success')
  } catch (error) {
    console.error('修改昵称失败:', error)
    showToast('修改失败，请重试')
  } finally {
    uni.hideLoading()
  }
}

const confirmUsername = async () => {
  const username = tempUsername.value.trim()
  if (!username) {
    showToast('用户名不能为空')
    return
  }

  try {
    uni.showLoading({ title: '设置中...' })
    await userApi.updateUserInfo({ username } as any)
    userInfo.username = username
    showUsernameModal.value = false
    showToast('设置成功', 'success')
  } catch (error) {
    console.error('设置用户名失败:', error)
    showToast('设置失败，请重试')
  } finally {
    uni.hideLoading()
  }
}

const confirmSex = async () => {
  try {
    uni.showLoading({ title: '修改中...' })
    await userApi.updateUserInfo({ sex: tempSex.value } as any)
    userInfo.sex = tempSex.value
    showSexModal.value = false
    showToast('修改成功', 'success')
  } catch (error) {
    console.error('修改性别失败:', error)
    showToast('修改失败，请重试')
  } finally {
    uni.hideLoading()
  }
}

const onChooseAvatar = async (event: ChooseAvatarEvent) => {
  const avatarUrl = event.detail?.avatarUrl
  if (!avatarUrl) return

  try {
    uni.showLoading({ title: '上传中...' })
    const uploadResult = await uploadAvatar(avatarUrl)
    userInfo.avatarUrl = uploadResult
    userStore.updateAvatar(uploadResult)
    showToast('头像修改成功', 'success')
  } catch (error) {
    console.error('上传头像失败:', error)
    showToast('上传失败，请重试')
  } finally {
    uni.hideLoading()
  }
}

const onH5ChooseAvatar = async () => {
  try {
    const chooseResult = await new Promise<ChooseImageResult>((resolve, reject) => {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: res => resolve(res as ChooseImageResult),
        fail: reject,
      })
    })

    const tempFilePath = chooseResult.tempFilePaths[0]
    if (!tempFilePath) return

    uni.showLoading({ title: '上传中...' })
    const uploadResult = await uploadAvatar(tempFilePath)
    userInfo.avatarUrl = uploadResult
    userStore.updateAvatar(uploadResult)
    showToast('头像修改成功', 'success')
  } catch (error) {
    console.error('上传头像失败:', error)
    showToast('上传失败，请重试')
  } finally {
    uni.hideLoading()
  }
}

const uploadAvatar = async (filePath: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${baseUrl}/app/user/updateUserAvatar`,
      filePath,
      name: 'avatarFile',
      header: {
        token: userStore.userInfo.token,
        uid: userStore.userInfo.userId,
      },
      success: uploadRes => {
        try {
          const data = JSON.parse(uploadRes.data) as UploadAvatarResponse
          if (data.code === 200 || data.success) {
            resolve(data.data || data.url || '')
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (err) {
          reject(err)
        }
      },
      fail: reject,
    })
  })
}

const handleLogout = async () => {
  const result = await uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
  })

  if (!result.confirm) return

  try {
    await userApi.logout()
  } catch (error) {
    console.error('退出登录失败:', error)
  }

  userStore.logout()
  showToast('已退出登录')
  uni.$grouter.reLaunch('login')
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding-bottom: 60rpx;
  background: $uni-bg-color-page;
}

.profile-hero {
  position: relative;
  padding: 28rpx 24rpx 0;
}

.hero-bg {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  height: 260rpx;
  background: linear-gradient(135deg, $uni-color-success 0%, $uni-color-primary 100%);
}

.profile-card {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-top: 98rpx;
  padding: 30rpx 28rpx;
  background: $uni-bg-color;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 18rpx;
  box-shadow: 0 12rpx 34rpx rgba($uni-text-color, 0.08);
}

.avatar-wrapper {
  position: relative;
  width: 136rpx;
  height: 136rpx;
  flex-shrink: 0;
  padding: 0;
  margin: 0;
  line-height: 1;
  background: transparent;
  border: none;

  &::after {
    border: none;
  }
}

.avatar-img {
  display: block;
  width: 136rpx;
  height: 136rpx;
  background: $uni-bg-color-grey;
  border: 6rpx solid $uni-color-success-light;
  border-radius: 50%;
  box-sizing: border-box;
}

.avatar-edit-icon {
  position: absolute;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46rpx;
  height: 46rpx;
  background: $uni-color-success;
  border: 3rpx solid $uni-bg-color;
  border-radius: 50%;
}

.profile-main {
  flex: 1;
  min-width: 0;
  padding-right: 92rpx;
}

.nickname {
  color: $uni-text-color;
  font-size: 36rpx;
  font-weight: 800;
  line-height: 46rpx;
}

.profile-subtitle {
  display: block;
  margin-top: 10rpx;
  color: $uni-text-color-grey;
  font-size: 25rpx;
  line-height: 34rpx;
}

.login-badge {
  position: absolute;
  top: 30rpx;
  right: 28rpx;
  padding: 7rpx 16rpx;
  color: $uni-text-color-grey;
  font-size: 22rpx;
  line-height: 28rpx;
  background: $uni-bg-color-grey;
  border-radius: 999rpx;

  &.active {
    color: $uni-color-success;
    background: $uni-color-success-light;
  }
}

.content {
  padding: 22rpx 24rpx 0;
}

.section-card {
  background: $uni-bg-color;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 18rpx;
  box-shadow: 0 8rpx 24rpx rgba($uni-text-color, 0.04);
}

.info-section {
  overflow: hidden;
}

.section-title {
  padding: 26rpx 24rpx 8rpx;
  color: $uni-text-color;
  font-size: 30rpx;
  font-weight: 800;
  line-height: 40rpx;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  min-height: 96rpx;
  padding: 0 24rpx;

  &.clickable:active {
    background: $uni-bg-color-hover;
  }
}

.info-left,
.info-right {
  display: flex;
  align-items: center;
  min-width: 0;
}

.info-left {
  flex-shrink: 0;
  gap: 16rpx;
}

.info-right {
  justify-content: flex-end;
  flex: 1;
  gap: 8rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
  align-self: stretch;
}

.info-item:last-child .info-right {
  border-bottom: none;
}

.info-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58rpx;
  height: 58rpx;
  border-radius: 18rpx;

  &.primary {
    background: $uni-color-primary-light;
  }

  &.success {
    background: $uni-color-success-light;
  }

  &.warning {
    background: $uni-color-warning-light;
  }

  &.muted {
    background: $uni-bg-color-grey;
  }
}

.info-label {
  color: $uni-text-color;
  font-size: 29rpx;
  font-weight: 600;
}

.info-value {
  max-width: 360rpx;
  color: $uni-text-color-grey;
  font-size: 28rpx;
  text-align: right;
}

.action-section {
  margin-top: 28rpx;
}

.logout-btn {
  width: 100%;
  height: 82rpx;
  color: $uni-color-error;
  font-size: 29rpx;
  line-height: 82rpx;
  background: $uni-bg-color;
  border: 1rpx solid $uni-color-error-light;
  border-radius: 999rpx;

  &::after {
    border: none;
  }
}

.edit-modal {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  background: $uni-bg-color-mask;
}

.modal-content {
  width: 100%;
  max-width: 620rpx;
  overflow: hidden;
  background: $uni-bg-color;
  border-radius: 20rpx;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 30rpx;
  border-bottom: 1rpx solid $uni-border-color-light;
}

.modal-title {
  color: $uni-text-color;
  font-size: 32rpx;
  font-weight: 800;
}

.close-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
}

.modal-body {
  padding: 30rpx;
}

.text-input {
  width: 100%;
  height: 82rpx;
  padding: 0 22rpx;
  color: $uni-text-color;
  font-size: 29rpx;
  background: $uni-bg-color-grey;
  border-radius: 12rpx;
  box-sizing: border-box;
}

:deep(.input-placeholder) {
  color: $uni-text-color-placeholder;
}

.input-hint {
  display: block;
  margin-top: 14rpx;
  color: $uni-text-color-grey;
  font-size: 24rpx;
  line-height: 32rpx;
}

.modal-footer {
  padding: 0 30rpx 30rpx;
}

.confirm-btn {
  width: 100%;
  height: 80rpx;
  color: $uni-text-color-inverse;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 80rpx;
  background: $uni-color-success;
  border: none;
  border-radius: 40rpx;

  &::after {
    border: none;
  }
}

.sex-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.sex-option {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 86rpx;
  gap: 10rpx;
  color: $uni-text-color-secondary;
  background: $uni-bg-color-grey;
  border: 1rpx solid $uni-border-color-light;
  border-radius: 14rpx;

  &.active {
    color: $uni-color-success;
    background: $uni-color-success-light;
    border-color: $uni-color-success;
  }
}

.sex-check {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30rpx;
  height: 30rpx;
  background: $uni-color-success;
  border-radius: 50%;
}

.sex-text {
  font-size: 28rpx;
  font-weight: 600;
}
</style>

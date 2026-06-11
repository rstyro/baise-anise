<template>
	<view class="page">
		<view class="header-section">
			<view class="header-title">系统设置</view>
		</view>

		<view class="settings-section">
			<view class="settings-group">
				<view class="group-title">通用设置</view>
				<view class="settings-card">
					<view class="setting-item" @click="toggleNotification">
						<view class="setting-left">
							<u-icon name="bell" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">消息通知</text>
						</view>
						<view class="setting-right">
							<u-switch v-model="notificationEnabled" size="20" activeColor="#5199ff"></u-switch>
						</view>
					</view>

					<view class="setting-item item-bottom-solid" @click="clearCache">
						<view class="setting-left">
							<u-icon name="trash" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">清除缓存</text>
						</view>
						<view class="setting-right">
							<text class="setting-value">{{ cacheSize }}</text>
							<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
						</view>
					</view>

					<view class="setting-item" @click="checkUpdate">
						<view class="setting-left">
							<u-icon name="reload" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">版本更新</text>
						</view>
						<view class="setting-right">
							<text class="setting-value">当前 v{{ version }}</text>
							<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
						</view>
					</view>
				</view>
			</view>

			<view class="settings-group">
				<view class="group-title">隐私与安全</view>
				<view class="settings-card">
					<view class="setting-item" @click="openPrivacyPolicy">
						<view class="setting-left">
							<u-icon name="file-text" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">隐私政策</text>
						</view>
						<view class="setting-right">
							<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
						</view>
					</view>

					<view class="setting-item item-bottom-solid" @click="openTermsOfService">
						<view class="setting-left">
							<u-icon name="info-circle" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">用户协议</text>
						</view>
						<view class="setting-right">
							<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
						</view>
					</view>

					<view class="setting-item" @click="toggleDarkMode">
						<view class="setting-left">
							<u-icon name="moon" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">深色模式</text>
						</view>
						<view class="setting-right">
							<u-switch v-model="darkModeEnabled" size="20" activeColor="#5199ff"></u-switch>
						</view>
					</view>
				</view>
			</view>

			<view class="settings-group">
				<view class="group-title">其他</view>
				<view class="settings-card">
					<view class="setting-item" @click="openAbout">
						<view class="setting-left">
							<u-icon name="question-circle" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">关于我们</text>
						</view>
						<view class="setting-right">
							<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
						</view>
					</view>

					<view class="setting-item" @click="contactSupport">
						<view class="setting-left">
							<u-icon name="phone" size="24" color="#5199ff"></u-icon>
							<text class="setting-label">联系客服</text>
						</view>
						<view class="setting-right">
							<text class="setting-value">400-888-8888</text>
							<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="logout-section">
			<button class="logout-btn" @click="handleLogout">退出登录</button>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'

const notificationEnabled = ref(true)
const darkModeEnabled = ref(false)
const cacheSize = ref('12.5MB')
const version = ref('1.0.0')

const toggleNotification = () => {
	notificationEnabled.value = !notificationEnabled.value
	uni.$u.toast(notificationEnabled.value ? '已开启消息通知' : '已关闭消息通知')
}

const clearCache = () => {
	uni.showModal({
		title: '清除缓存',
		content: '确定要清除本地缓存吗？',
		success: (res) => {
			if (res.confirm) {
				cacheSize.value = '0MB'
				uni.$u.toast('缓存已清除')
			}
		}
	})
}

const checkUpdate = () => {
	uni.showModal({
		title: '版本更新',
		content: '当前已是最新版本',
		showCancel: false
	})
}

const openPrivacyPolicy = () => {
	uni.showModal({
		title: '隐私政策',
		content: '这里是隐私政策内容...\n我们承诺保护您的个人隐私信息。',
		showCancel: false
	})
}

const openTermsOfService = () => {
	uni.showModal({
		title: '用户协议',
		content: '这里是用户协议内容...\n使用本应用即表示您同意我们的服务条款。',
		showCancel: false
	})
}

const toggleDarkMode = () => {
	darkModeEnabled.value = !darkModeEnabled.value
	uni.$u.toast(darkModeEnabled.value ? '已开启深色模式' : '已关闭深色模式')
}

const openAbout = () => {
	uni.showModal({
		title: '关于我们',
		content: 'Anise UI\n版本：1.0.0\n一款基于 uni-app 的现代化 UI 框架',
		showCancel: false
	})
}

const contactSupport = () => {
	uni.makePhoneCall({
		phoneNumber: '400-888-8888',
		fail: () => {
			uni.$u.toast('拨打失败，请手动拨打 400-888-8888')
		}
	})
}

const handleLogout = () => {
	uni.showModal({
		title: '提示',
		content: '确定要退出登录吗？',
		success: (res) => {
			if (res.confirm) {
				uni.$u.toast('已退出登录')
				uni.$grouter.reLaunch('login')
			}
		}
	})
}
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: #f5f7ff;
	padding-bottom: 120rpx;
}

.header-section {
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	padding: 40rpx;
	display: flex;
	justify-content: center;
	align-items: center;
}

.header-title {
	font-size: 38rpx;
	font-weight: 600;
	color: #fff;
}

.settings-section {
	margin: -20rpx 25rpx 0;
}

.settings-group {
	margin-bottom: 30rpx;
}

.group-title {
	font-size: 26rpx;
	color: #999;
	padding: 20rpx 10rpx 10rpx;
}

.settings-card {
	background: #fff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(81, 153, 255, 0.1);
	overflow: hidden;
}

.setting-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	position: relative;

	&:active {
		background-color: #f9fafd;
	}
}

.item-bottom-solid {
	&::after {
		content: '';
		position: absolute;
		left: 30rpx;
		right: 30rpx;
		bottom: 0;
		height: 1px;
		background: linear-gradient(to right, transparent, #f0f5ff, transparent);
	}
}

.setting-left {
	display: flex;
	align-items: center;
}

.setting-label {
	font-size: 30rpx;
	color: #333;
	margin-left: 16rpx;
}

.setting-right {
	display: flex;
	align-items: center;
}

.setting-value {
	font-size: 28rpx;
	color: #999;
	margin-right: 8rpx;
}

.logout-section {
	margin: 60rpx 30rpx;
}

.logout-btn {
	width: 100%;
	height: 90rpx;
	line-height: 90rpx;
	font-size: 32rpx;
	font-weight: 600;
	color: #fff;
	background: #fff;
	border-radius: 45rpx;
	border: none;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.08);
	color: #ff4d4f;

	&:active {
		transform: translateY(4rpx);
	}
}
</style>
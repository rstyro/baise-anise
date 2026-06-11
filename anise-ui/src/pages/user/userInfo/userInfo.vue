<template>
	<view class="page">
		<view class="header-section">

		<!-- #ifdef MP-WEIXIN -->
			<button class="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
				<image 
					:src="displayAvatarUrl || '/static/logo.png'" 
					class="avatar-img" 
					mode="aspectFill"
				></image>
				<view class="avatar-edit-icon">
					<u-icon name="camera-fill" size="24" color="#fff"></u-icon>
				</view>
			</button>
			<!-- #endif -->

			<!-- H5专用点击区域 -->
			<!-- #ifdef H5 -->
			<view class="avatar-wrapper" @click="onH5ChooseAvatar">
				<image 
					:src="displayAvatarUrl || '/static/logo.png'" 
					class="avatar-img" 
					mode="aspectFill"
				></image>
				<view class="avatar-edit-icon">
					<u-icon name="camera-fill" size="24" color="#fff"></u-icon>
				</view>
			</view>
			<!-- #endif -->


			<text class="nickname">{{ userInfo.nickname || '未设置昵称' }}</text>
		</view>

		<view class="info-section">
			<view class="info-item" @click="editUsername" v-if="!userInfo.username">
				<view class="info-left">
					<u-icon name="user" size="24" color="#5199ff"></u-icon>
					<text class="info-label">用户名</text>
				</view>
				<view class="info-right">
					<text class="info-value">{{ userInfo.username || '未设置' }}</text>
					<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
				</view>
			</view>

			<view class="info-item" v-else>
				<view class="info-left">
					<u-icon name="user" size="24" color="#5199ff"></u-icon>
					<text class="info-label">用户名</text>
				</view>
				<view class="info-right">
					<text class="info-value">{{ userInfo.username }}</text>
				</view>
			</view>

			<view class="info-item" @click="editNickname">
				<view class="info-left">
					<u-icon name="account" size="24" color="#5199ff"></u-icon>
					<text class="info-label">昵称</text>
				</view>
				<view class="info-right">
					<text class="info-value">{{ userInfo.nickname || '未设置' }}</text>
					<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
				</view>
			</view>

			<view class="info-item" @click="editSex">
				<view class="info-left">
					<u-icon name="man" size="24" color="#5199ff"></u-icon>
					<text class="info-label">性别</text>
				</view>
				<view class="info-right">
					<text class="info-value">{{ sexMap[userInfo.sex] }}</text>
					<u-icon name="arrow-right" size="20" color="#ccc"></u-icon>
				</view>
			</view>

			<view class="info-item">
				<view class="info-left">
					<u-icon name="phone" size="24" color="#5199ff"></u-icon>
					<text class="info-label">手机号</text>
				</view>
				<view class="info-right">
					<text class="info-value">{{ userInfo.phone || '未绑定' }}</text>
				</view>
			</view>

			<view class="info-item">
				<view class="info-left">
					<u-icon name="grid" size="24" color="#5199ff"></u-icon>
					<text class="info-label">用户ID</text>
				</view>
				<view class="info-right">
					<text class="info-value">{{ userInfo.userId || '-' }}</text>
				</view>
			</view>
		</view>

		<view class="action-section" v-if="isLoggedIn">
			<button class="logout-btn" @click="handleLogout">退出登录</button>
		</view>

		<view class="nickname-modal" v-if="showNicknameModal" @click="showNicknameModal = false">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">修改昵称</text>
					<view class="close-btn" @click="showNicknameModal = false">
						<u-icon name="close" size="32" color="#999"></u-icon>
					</view>
				</view>
				<view class="modal-body">
					<input 
						v-model="tempNickname" 
						class="nickname-input" 
						placeholder="请输入昵称"
						type="nickname"
						maxlength="20"
					/>
				</view>
				<view class="modal-footer">
					<button class="confirm-btn" @click="confirmNickname">确认修改</button>
				</view>
			</view>
		</view>

		<view class="nickname-modal" v-if="showUsernameModal" @click="showUsernameModal = false">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">设置用户名</text>
					<view class="close-btn" @click="showUsernameModal = false">
						<u-icon name="close" size="32" color="#999"></u-icon>
					</view>
				</view>
				<view class="modal-body">
					<input 
						v-model="tempUsername" 
						class="nickname-input" 
						placeholder="请输入用户名"
						type="text"
						maxlength="20"
					/>
				</view>
				<view class="modal-footer">
					<button class="confirm-btn" @click="confirmUsername">确认设置</button>
				</view>
			</view>
		</view>

		<view class="nickname-modal" v-if="showSexModal" @click="showSexModal = false">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">选择性别</text>
					<view class="close-btn" @click="showSexModal = false">
						<u-icon name="close" size="32" color="#999"></u-icon>
					</view>
				</view>
				<view class="modal-body">
					<view class="sex-options">
						<view 
							v-for="(option, index) in sexOptions" 
							:key="index"
							class="sex-option"
							:class="{ active: tempSex === index }"
							@click="tempSex = index"
						>
							<u-icon name="check" size="28" color="#5199ff" v-if="tempSex === index"></u-icon>
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

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { userApi } from '@/api/userApi'
import { useUserStore } from '@/stores/user'
import { baseUrl } from '@/env'

const userStore = useUserStore()

const userInfo = reactive({
	userId: 0,
	username: '',
	nickname: '',
	avatarUrl: '',
	phone: '',
	sex: 0
})

const isLoggedIn = ref(false)

const showNicknameModal = ref(false)
const tempNickname = ref('')

const showUsernameModal = ref(false)
const tempUsername = ref('')

const showSexModal = ref(false)
const tempSex = ref(0)

const sexOptions = ['保密', '男', '女']
const sexMap = {
	0: '保密',
	1: '男',
	2: '女'
}

// 头像地址，拼接
const displayAvatarUrl = computed(() => {
	if (!userInfo.avatarUrl) {
		return ''
	}
	if (/^https?:\/\//.test(userInfo.avatarUrl)) {
		return userInfo.avatarUrl
	}
	return uni.$gconst.IMAGES.BASE_URL + userInfo.avatarUrl
})

onMounted(async () => {
	await loadUserInfo()
})

const loadUserInfo = async () => {
	try {
		const res = await userApi.getUserInfo();
		Object.assign(userInfo, res)
		userStore.login(res.token, res)
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

const confirmNickname = async () => {
	if (!tempNickname.value.trim()) {
		uni.$u.toast('昵称不能为空')
		return
	}

	try {
		uni.showLoading({ title: '修改中...' })
		await userApi.updateUserInfo({ nickname: tempNickname.value })
		userInfo.nickname = tempNickname.value
		userStore.updateName(tempNickname.value)
		showNicknameModal.value = false
		uni.hideLoading()
		uni.$u.toast('修改成功')
	} catch (error) {
		uni.hideLoading()
		console.error('修改昵称失败:', error)
		uni.$u.toast('修改失败，请重试')
	}
}

const confirmUsername = async () => {
	if (!tempUsername.value.trim()) {
		uni.$u.toast('用户名不能为空')
		return
	}

	try {
		uni.showLoading({ title: '设置中...' })
		await userApi.updateUserInfo({ username: tempUsername.value })
		userInfo.username = tempUsername.value
		showUsernameModal.value = false
		uni.hideLoading()
		uni.$u.toast('设置成功')
	} catch (error) {
		uni.hideLoading()
		console.error('设置用户名失败:', error)
		uni.$u.toast('设置失败，请重试')
	}
}

const confirmSex = async () => {
	try {
		uni.showLoading({ title: '修改中...' })
		await userApi.updateUserInfo({ sex: tempSex.value })
		userInfo.sex = tempSex.value
		showSexModal.value = false
		uni.hideLoading()
		uni.$u.toast('修改成功')
	} catch (error) {
		uni.hideLoading()
		console.error('修改性别失败:', error)
		uni.$u.toast('修改失败，请重试')
	}
}

const onChooseAvatar = async (e) => {
	try {
		uni.showLoading({ title: '上传中...' })
		
		const { avatarUrl } = e.detail
		
		const uploadResult = await uploadAvatar(avatarUrl)

		userInfo.avatarUrl = uploadResult
		userStore.updateAvatar(uploadResult)
		uni.hideLoading()
		uni.$u.toast('头像修改成功')
	} catch (error) {
		uni.hideLoading()
		console.error('上传头像失败:', error)
		uni.$u.toast('上传失败，请重试')
	}
}

const onH5ChooseAvatar = async () => {
	try {
		uni.showLoading({ title: '选择图片...' })
		const chooseResult = await new Promise((resolve, reject) => {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: resolve,
				fail: reject
			})
		})
		uni.hideLoading()
		const tempFilePath = chooseResult.tempFilePaths[0]
		console.log('选择的图片路径:', tempFilePath)
		uni.showLoading({ title: '上传中...' })
		const uploadResult = await uploadAvatar(tempFilePath)
		
		userInfo.avatarUrl = uploadResult
		userStore.updateAvatar(uploadResult)
		uni.hideLoading()
		uni.$u.toast('头像修改成功')
	} catch (error) {
		uni.hideLoading()
		console.error('上传头像失败:', error)
		uni.$u.toast('上传失败，请重试')
	}
}

const uploadAvatar = async (filePath) => {
	console.log('上传文件路径:', filePath)
	return new Promise((resolve, reject) => {
		uni.uploadFile({
			url: `${baseUrl}/app/user/updateUserAvatar`,
			filePath: filePath,
			name: 'avatarFile',
			header: {
				'token': userStore.userInfo.token,
        'uid':userStore.userInfo.userId
			},
			success: (uploadRes) => {
				try {
					const data = JSON.parse(uploadRes.data)
					if (data.code === 200 || data.success) {
						resolve(data.data || data.url)
					} else {
						reject(new Error(data.message || '上传失败'))
					}
				} catch (err) {
					reject(err)
				}
			},
			fail: reject
		})
	})
}

const handleLogout = async () => {
	uni.showModal({
		title: '提示',
		content: '确定要退出登录吗？',
		success: async (res) => {
			if (res.confirm) {
				try {
					await userApi.logout()
				} catch (error) {
					console.error('退出登录失败:', error)
				}
				userStore.logout()
				uni.$u.toast('已退出登录');
        uni.$grouter.reLaunch("login");
			}
		}
	})
}
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: #f5f7ff;
}

.header-section {
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	padding: 60rpx 40rpx 80rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.avatar-wrapper {
	position: relative;
	width: 160rpx;
	height: 160rpx;
	margin-bottom: 24rpx;
	padding: 0;
	background: transparent;
	border: none;
	
	&::after {
		border: none;
	}
}

.avatar-img {
	width: 160rpx;
	height: 160rpx;
	border-radius: 50%;
	border: 6rpx solid rgba(255, 255, 255, 0.5);
	box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.15);
}

.avatar-edit-icon {
	position: absolute;
	bottom: 0;
	right: 0;
	width: 50rpx;
	height: 50rpx;
	background: #5199ff;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	border: 3rpx solid #fff;
}

.nickname {
	font-size: 36rpx;
	font-weight: 600;
	color: #fff;
}

.info-section {
	margin: -40rpx 30rpx 0;
	background: #fff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(81, 153, 255, 0.1);
	overflow: hidden;
}

.info-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f5f5f5;

	&:last-child {
		border-bottom: none;
	}
}

.info-left {
	display: flex;
	align-items: center;
}

.info-label {
	font-size: 30rpx;
	color: #333;
	margin-left: 16rpx;
}

.info-right {
	display: flex;
	align-items: center;
}

.info-value {
	font-size: 28rpx;
	color: #999;
	margin-right: 8rpx;
}

.action-section {
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

.nickname-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 100;
	padding: 40rpx;
}

.modal-content {
	width: 100%;
	max-width: 600rpx;
	background: #fff;
	border-radius: 24rpx;
	overflow: hidden;
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
	font-size: 34rpx;
	font-weight: 600;
	color: #333;
}

.close-btn {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	justify-content: center;
	align-items: center;
}

.modal-body {
	padding: 30rpx;
}

.nickname-input {
	width: 100%;
	height: 80rpx;
	padding: 0 20rpx;
	font-size: 30rpx;
	color: #333;
	background: #f5f7ff;
	border-radius: 12rpx;
}

.modal-footer {
	padding: 20rpx 30rpx 30rpx;
}

.confirm-btn {
	width: 100%;
	height: 80rpx;
	line-height: 80rpx;
	font-size: 32rpx;
	font-weight: 600;
	color: #fff;
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	border-radius: 40rpx;
	border: none;

	&:active {
		transform: translateY(4rpx);
	}
}

.sex-options {
	display: flex;
	justify-content: space-around;
	padding: 20rpx 0;
}

.sex-option {
	display: flex;
	flex-direction: column;
	align-items: center;
	width: 160rpx;
	height: 160rpx;
	border-radius: 50%;
	background: #f5f7ff;
	justify-content: center;
	transition: all 0.3s;

	&.active {
		background: #e8f0fe;
		border: 2rpx solid #5199ff;
	}
}

.sex-text {
	font-size: 28rpx;
	color: #666;
	margin-top: 12rpx;

	.active & {
		color: #5199ff;
		font-weight: 600;
	}
}
</style>

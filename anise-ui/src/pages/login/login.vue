<template>
	<view class="page">
		<view class="bg-container">
			<view class="bg-circle bg-circle-1"></view>
			<view class="bg-circle bg-circle-2"></view>
			<view class="bg-circle bg-circle-3"></view>
		</view>

		<view class="login-container">
			<view class="logo-section">
				<view class="logo-wrapper">
					<image src="/static/logo.png" class="logo" mode="aspectFill"></image>
				</view>
				<text class="app-name">智能助手</text>
				<text class="app-slogan">让生活更智能</text>
			</view>

			<view class="form-section">
				<view class="input-group">
					<view class="input-item">
						<u-icon name="phone" size="28" color="#999"></u-icon>
						<input 
							v-model="formData.phone" 
							type="number" 
							placeholder="请输入手机号"
							class="input-content"
							maxlength="11"
						/>
					</view>
					<view class="input-item">
						<u-icon name="lock" size="28" color="#999"></u-icon>
						<input 
							v-model="formData.code" 
							type="number" 
							placeholder="请输入验证码"
							class="input-content"
							maxlength="6"
						/>
						<view class="code-btn" :class="{disabled: !canGetCode}" @click="getCode">
							{{ codeText }}
						</view>
					</view>
				</view>

				<view class="agreement-row">
					<u-switch v-model="formData.agreed" active-color="#5199ff"></u-switch>
					<text class="agreement-text">
						我已阅读并同意
						<text class="link">《用户协议》</text>
						和
						<text class="link">《隐私政策》</text>
					</text>
				</view>

				<button class="login-btn" :disabled="!canLogin" @click="handleLogin">
					登录/注册
				</button>
			</view>

			<view class="divider-row">
				<view class="divider"></view>
				<text class="divider-text">其他登录方式</text>
				<view class="divider"></view>
			</view>

			<view class="other-login">
        <!-- #ifdef MP-WEIXIN -->
        <button
            class="wechat-login-btn"
            open-type="getUserInfo" @getuserinfo="wxGetUserInfo"
        >
          <u-icon name="weixin" size="48" color="#4bd48d"></u-icon>
          <text class="login-type">微信快捷登录</text>
        </button>
        <!-- #endif -->
			</view>

		</view>

		<view class="phone-modal" v-if="showPhoneModal" @click="showPhoneModal = false">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">绑定手机号</text>
					<view class="close-btn" @click="showPhoneModal = false">
						<u-icon name="close" size="32" color="#999"></u-icon>
					</view>
				</view>
				<view class="modal-body">
					<view class="modal-desc">检测到您尚未绑定手机号，请完成绑定</view>
					<view class="modal-input-item">
						<u-icon name="phone" size="28" color="#999"></u-icon>
						<input 
							v-model="bindPhone" 
							type="number" 
							placeholder="请输入手机号"
							class="modal-input"
							maxlength="11"
						/>
					</view>
					<view class="modal-input-item">
						<u-icon name="lock" size="28" color="#999"></u-icon>
						<input 
							v-model="bindCode" 
							type="number" 
							placeholder="请输入验证码"
							class="modal-input"
							maxlength="6"
						/>
						<view class="modal-code-btn" :class="{disabled: !canBindGetCode}" @click="getBindCode">
							{{ bindCodeText }}
						</view>
					</view>
				</view>
				<view class="modal-footer">
					<button 
						class="bind-btn" 
						:disabled="!canBind" 
						@click="handleBind"
					>
						完成绑定
					</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import UserInfo from '../user/userInfo/userInfo.vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/userApi'

const userStore = useUserStore()

const formData = reactive({
	phone: '18818868688',
	code: '',
	agreed: false
})

const codeText = ref('获取验证码')
const codeTimer = ref(0)

const showPhoneModal = ref(false)
const bindPhone = ref('')
const bindCode = ref('')
const bindCodeText = ref('获取验证码')
const bindCodeTimer = ref(0)

const canGetCode = computed(() => {
	return /^1[3-9]\d{9}$/.test(formData.phone) && codeTimer.value === 0
})

const canLogin = computed(() => {
	return /^1[3-9]\d{9}$/.test(formData.phone) && 
		   /^\d{6}$/.test(formData.code) && 
		   formData.agreed
})

const canBindGetCode = computed(() => {
	return /^1[3-9]\d{9}$/.test(bindPhone.value) && bindCodeTimer.value === 0
})

const canBind = computed(() => {
	return /^1[3-9]\d{9}$/.test(bindPhone.value) && 
		   /^\d{6}$/.test(bindCode.value)
})

const getCode = () => {
	if (!canGetCode.value) return
	console.log("发送验证码，phone=",formData.phone)
	uni.$u.toast('验证码已发送')
	
	codeTimer.value = 60
	codeText.value = `${codeTimer.value}s`
	
	const timer = setInterval(() => {
		codeTimer.value--
		if (codeTimer.value <= 0) {
			clearInterval(timer)
			codeText.value = '获取验证码'
		} else {
			codeText.value = `${codeTimer.value}s`
		}
	}, 1000)
}

const handleLogin = async () => {
	if (!canLogin.value) return
	
	try {
		uni.showLoading({ title: '登录中...' })
		
		const result = await userApi.login({
			account: formData.phone,
			code: formData.code,
			actionType: 'login',
			agreed: formData.agreed
		})
		
		uni.hideLoading()
		
		if (result.token && result.userId) {
			setLoginInfo(result)
			uni.$u.toast('登录成功')
			uni.switchTab({ url: '/pages/tab-bar/index/index' })
		} else {
			uni.$u.toast('登录失败，请重试')
		}
	} catch (error) {
		uni.hideLoading()
		console.error('登录失败:', error)
		uni.$u.toast('登录失败，请重试')
	}
}

const wxGetUserInfo = (res) => {
	var tagUserInfo = res.detail.userInfo;
	handleWechatLogin(tagUserInfo)
}

const handleWechatLogin = async (userInfo) => {
	try {
		uni.showLoading({ title: '登录中...' })
		const loginRes = await new Promise((resolve, reject) => {
			uni.login({
				success: resolve,
				fail: reject
			})
		})
		
		if (!loginRes.code) {
			throw new Error('获取登录凭证失败')
		}
		
		const result = await userApi.appletLogin({
			code: loginRes.code,
			avatarUrl: userInfo.avatarUrl,
			nickname: userInfo.nickName,
			sex: userInfo.gender,
			city: userInfo.city,
			province: userInfo.province
		})
		uni.hideLoading()
		console.log("result:",result)
		if (result.token && result.userId) {
			setLoginInfo(result)
			uni.$u.toast('微信登录成功')
			uni.$grouter.switchTab("index");
		} else {
			showPhoneModal.value = true
		}
	} catch (error) {
		uni.hideLoading()
		console.error('微信登录失败:', error)
		uni.$u.toast('登录失败，请重试')
	}
}

const setLoginInfo = (userInfo) => {
	uni.setStorageSync('token', userInfo.token)
	uni.setStorageSync('userId', userInfo.userId)
	
	userStore.login(userInfo.token, userInfo)
}

const getBindCode = () => {
	if (!canBindGetCode.value) return
	
	uni.$u.toast('验证码已发送')
	
	bindCodeTimer.value = 60
	bindCodeText.value = `${bindCodeTimer.value}s`
	
	const timer = setInterval(() => {
		bindCodeTimer.value--
		if (bindCodeTimer.value <= 0) {
			clearInterval(timer)
			bindCodeText.value = '获取验证码'
		} else {
			bindCodeText.value = `${bindCodeTimer.value}s`
		}
	}, 1000)
}

const handleBind = async () => {
	if (!canBind.value) return
	
	try {
		uni.showLoading({ title: '绑定中...' })
		
		const result = await userApi.login({
			account: bindPhone.value,
			password: bindCode.value,
			actionType: 'login',
			agreed: true
		})
		
		uni.hideLoading()
		
		if (result.token && result.userId) {
			setLoginInfo(result)
			showPhoneModal.value = false
			bindPhone.value = ''
			bindCode.value = ''
			uni.$u.toast('绑定成功，登录成功')
			uni.switchTab({ url: '/pages/tab-bar/index/index' })
		} else {
			uni.$u.toast('绑定失败，请重试')
		}
	} catch (error) {
		uni.hideLoading()
		console.error('绑定失败:', error)
		uni.$u.toast('绑定失败，请重试')
	}
}
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: linear-gradient(135deg, #f0f5ff 0%, #e6edff 100%);
	position: relative;
	overflow: hidden;
}

.bg-container {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	overflow: hidden;
}

.bg-circle {
	position: absolute;
	border-radius: 50%;
	opacity: 0.3;
}

.bg-circle-1 {
	width: 600rpx;
	height: 600rpx;
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	top: -100rpx;
	right: -100rpx;
}

.bg-circle-2 {
	width: 400rpx;
	height: 400rpx;
	background: linear-gradient(135deg, #7e8cfa, #a8b8ff);
	top: 300rpx;
	left: -100rpx;
}

.bg-circle-3 {
	width: 300rpx;
	height: 300rpx;
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	bottom: 200rpx;
	right: 50rpx;
}

.login-container {
	position: relative;
	z-index: 10;
	padding: 120rpx 40rpx 80rpx;
}

.logo-section {
	text-align: center;
	margin-bottom: 80rpx;
}

.logo-wrapper {
	width: 160rpx;
	height: 160rpx;
	margin: 0 auto 30rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	display: flex;
	justify-content: center;
	align-items: center;
	box-shadow: 0 20rpx 40rpx rgba(81, 153, 255, 0.3);
}

.logo {
	width: 100rpx;
	height: 100rpx;
}

.app-name {
	display: block;
	font-size: 48rpx;
	font-weight: 700;
	color: #333;
	margin-bottom: 12rpx;
}

.app-slogan {
	font-size: 28rpx;
	color: #999;
}

.form-section {
	background: #fff;
	border-radius: 24rpx;
	padding: 40rpx;
	box-shadow: 0 10rpx 40rpx rgba(81, 153, 255, 0.1);
	margin-bottom: 40rpx;
}

.input-group {
	margin-bottom: 30rpx;
}

.input-item {
	display: flex;
	align-items: center;
	height: 90rpx;
	border-bottom: 1rpx solid #f0f0f0;
	padding: 0 10rpx;
	margin-bottom: 10rpx;

	&:last-child {
		margin-bottom: 0;
	}
}

.input-content {
	flex: 1;
	height: 100%;
	font-size: 32rpx;
	color: #333;
	padding: 0 20rpx;
}

.code-btn {
	width: 160rpx;
	height: 60rpx;
	line-height: 60rpx;
	text-align: center;
	font-size: 26rpx;
	color: #5199ff;
	background: #f0f7ff;
	border-radius: 30rpx;

	&.disabled {
		color: #ccc;
		background: #f5f5f5;
	}
}

.agreement-row {
	display: flex;
	align-items: center;
	margin-bottom: 40rpx;
	padding: 0 10rpx;
}

.agreement-text {
	font-size: 24rpx;
	color: #666;
	margin-left: 16rpx;
	flex: 1;
}

.link {
	color: #5199ff;
}

.login-btn {
	width: 100%;
	height: 96rpx;
	line-height: 96rpx;
	font-size: 34rpx;
	font-weight: 600;
	color: #fff;
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	border-radius: 48rpx;
	border: none;
	box-shadow: 0 10rpx 30rpx rgba(81, 153, 255, 0.3);

	&:disabled {
		background: #ccc;
		box-shadow: none;
	}

	&:active {
		transform: translateY(4rpx);
		box-shadow: 0 5rpx 15rpx rgba(81, 153, 255, 0.3);
	}
}

.divider-row {
	display: flex;
	align-items: center;
	margin-bottom: 40rpx;
	padding: 0 20rpx;
}

.divider {
	flex: 1;
	height: 1rpx;
	background: linear-gradient(to right, transparent, #ddd, transparent);
}

.divider-text {
	font-size: 24rpx;
	color: #999;
	margin: 0 20rpx;
}

.other-login {
	display: flex;
	justify-content: center;
	margin-bottom: 60rpx;
}

.wechat-login-btn {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 60rpx;
	background: #fff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(75, 212, 141, 0.2);
	border: none;

	.login-type {
		font-size: 26rpx;
		color: #4bd48d;
		margin-top: 12rpx;
	}

	&::after {
		border: none;
	}
}

.phone-modal {
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

.modal-desc {
	font-size: 28rpx;
	color: #666;
	margin-bottom: 30rpx;
	text-align: center;
}

.modal-input-item {
	display: flex;
	align-items: center;
	height: 80rpx;
	border-bottom: 1rpx solid #f0f0f0;
	padding: 0 10rpx;
	margin-bottom: 20rpx;
}

.modal-input {
	flex: 1;
	height: 100%;
	font-size: 30rpx;
	color: #333;
	padding: 0 20rpx;
}

.modal-code-btn {
	width: 140rpx;
	height: 50rpx;
	line-height: 50rpx;
	text-align: center;
	font-size: 24rpx;
	color: #5199ff;
	background: #f0f7ff;
	border-radius: 25rpx;

	&.disabled {
		color: #ccc;
		background: #f5f5f5;
	}
}

.modal-footer {
	padding: 20rpx 30rpx 30rpx;
}

.bind-btn {
	width: 100%;
	height: 80rpx;
	line-height: 80rpx;
	font-size: 32rpx;
	font-weight: 600;
	color: #fff;
	background: linear-gradient(135deg, #5199ff, #7e8cfa);
	border-radius: 40rpx;
	border: none;

	&:disabled {
		background: #ccc;
	}
}
</style>
import { http } from 'uview-pro'
import { baseUrl } from '@/env'
import type { UserInfo, IsLoginResponse, LoginDto,UserAvatarDto } from './types'

export const userApi = {
  // 检查是否登录
  isLogin(): Promise<boolean> {
    return http.get(`${baseUrl}/app/user/isLogin`)
  },
  
  // 登录,Partial<LoginDto>表示可以部分填写登录信息,不加Partial则必须填写所有字段
  login(data: Partial<LoginDto>): Promise<UserInfo> {
    return http.post(`${baseUrl}/app/user/login`, data, { showLoading: true, loadingText: '登录中...' })
  },

  // 小程序登录
  appletLogin(data: Partial<LoginDto>): Promise<UserInfo> {
    return http.post(`${baseUrl}/app/user/appletLogin`, data)
  },
  
  // 退出登录
  logout(): Promise<void> {
    return http.post('/app/user/logout')
  },
  
  // 获取用户信息
  getUserInfo(): Promise<UserInfo> {
    return http.get('/app/user/getUserInfo')
  },
  
  // 更新用户信息
  updateUserInfo(data: Partial<UserInfo>): Promise<boolean> {
    return http.post(`${baseUrl}/app/user/updateUserInfo`, data)
  },

  // 更新用户头像,返回新的头像URL（不包含http前缀）
  updateUserAvatar(data: Partial<UserAvatarDto>): Promise<string> {
    return http.post(`${baseUrl}/app/user/updateUserAvatar`, data)
  },

}
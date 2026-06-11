export interface UserInfo {
  userId: number
  token: string
  nickname: string
  avatarUrl: string
  phone: string
}

export interface UserAvatarDto {
  // 二者选一个就行
  avatarUrl: string
  avatarFile: File
}

export interface LoginDto {
  code: string
  account: string
  password: string
  // register=注册，login=登录
  actionType: string
  agreed: boolean
}

export interface IsLoginResponse {
  isLogin: boolean
  user?: UserInfo
}
interface VersionUtils {
  getMiniProgramVersion(): string
  isDevOrTrialVersion(): boolean
  checkUpdate(): void
}

const versionUtils: VersionUtils = {
  getMiniProgramVersion() {
    try {
      const accountInfo = wx.getAccountInfoSync()
      return accountInfo.miniProgram.envVersion || 'release'
    } catch (error) {
      console.error('获取小程序版本信息失败:', error)
      return 'release'
    }
  },

  isDevOrTrialVersion() {
    const version = this.getMiniProgramVersion()
    return version === 'develop' || version === 'trial'
  },

  checkUpdate() {
    if (!wx.canIUse('getUpdateManager')) {
      console.warn('当前客户端版本不支持 getUpdateManager 方法')
      return
    }

    const updateManager = uni.getUpdateManager()

    updateManager.onCheckForUpdate((res) => {
      if (res.hasUpdate) {
        console.log('检测到新版本，正在下载更新...')
      } else {
        console.log('当前已是最新版本')
      }
    })

    updateManager.onUpdateReady(() => {
      uni.showModal({
        title: '更新提示',
        content: '新版本已准备好，是否重启小程序？',
        success(resModal) {
          if (resModal.confirm) {
            updateManager.applyUpdate()
          }
        }
      })
    })

    updateManager.onUpdateFailed(() => {
      uni.showToast({
        title: '更新失败，请稍后再试',
        icon: 'none'
      })
    })
  }
}

// 2个export 语句，分别导出默认导出和命名导出,只保留一个也可以正常工作
export default versionUtils
export { versionUtils }
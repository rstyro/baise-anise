interface ToolUtils {
  toUpperCase(arg: string | null | undefined): string | undefined
  copyData(data: string): void
}

const toolUtils: ToolUtils = {
  toUpperCase(arg: string) {
    return arg && arg.toUpperCase()
  },
  copyData(data: string) {
    uni.setClipboardData({
      data: data,
      success: () => {
        uni.showToast({
          title: '复制成功',
          icon: 'success',
          duration: 1000
        })
      }
    })
  }
}

// 2个export 语句，分别导出默认导出和命名导出,只保留一个也可以正常工作
export default toolUtils
export { toolUtils }
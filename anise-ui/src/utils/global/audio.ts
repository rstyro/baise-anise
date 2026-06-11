// audioUtils.ts

/**
 * 音频实例的完整状态
 */
interface AudioInstance {
  id: string
  audio: UniApp.InnerAudioContext
  duration: number
  currentTime: number
  volume: number
  loop: boolean
  paused: boolean
}

/**
 * 音频工具对外暴露的接口
 */
interface AudioUtils {
  createAudio(audioId: string, src: string): Promise<void>
  play(audioId: string): void
  pause(audioId: string): void
  stop(audioId: string): void
  seek(audioId: string, position: number): void
  setVolume(audioId: string, volume: number): void
  setLoop(audioId: string, loop: boolean): void
  getDuration(audioId: string): number
  getCurrentTime(audioId: string): number
  isPaused(audioId: string): boolean
  destroyAudio(audioId: string): void
  getAudioState(audioId: string): AudioInstance | null
}

// 存储所有音频实例的 Map
const audioInstances = new Map<string, AudioInstance>()

// iOS 用户交互标记（iOS 需要用户交互后才能播放音频）
let _hasUserInteraction = false

// 平台检测
function _isIOS(): boolean {
  try {
    const system = uni.getSystemInfoSync()
    const isIOSPlatform = system.platform === 'ios'
    const hasIOSInSystem = typeof system.system === 'string' && system.system.includes('iOS')
    return isIOSPlatform || hasIOSInSystem
  } catch (e) {
    try {
      const uniWx = (uni as any).$wx || (window as any).wx || {}
      const appBaseInfo = uniWx.getAppBaseInfo?.()
      return appBaseInfo?.platform === 'ios'
    } catch (e2) {
      return false
    }
  }
}

// 初始化用户交互监听
function _initUserInteraction(): void {
  uni.$on('global-user-interaction', () => {
    _hasUserInteraction = true
  })
}

// 立即初始化用户交互监听
_initUserInteraction()

/**
 * 将相对路径转换为 uni-app 可用的绝对路径
 * 适用于本地 static 目录下的文件：如 'static/xxx.mp3' -> '/static/xxx.mp3'
 */
function normalizeLocalPath(src: string): string {
  // 已经是绝对路径或网络路径(http://, https://, file://) 则直接返回
  if (src.startsWith('/') || src.startsWith('http://') || src.startsWith('https://') || src.startsWith('file://')) {
    return src
  }
  // 如果是相对路径如 'static/xxx.mp3'，加上 '/'
  if (!src.startsWith('/')) {
    return '/' + src
  }
  return src
}

/**
 * 音频工具实现
 */
const audioUtils: AudioUtils = {
  createAudio(audioId: string, src: string): Promise<void> {
    return new Promise((resolve, reject) => {
      // 1. 防重复
      if (audioInstances.has(audioId)) {
        reject(new Error(`Audio instance with id "${audioId}" already exists`))
        return
      }

      // 2. 修正本地路径
      const finalSrc = normalizeLocalPath(src)

      // 3. 创建音频对象
      const audio: UniApp.InnerAudioContext = uni.createInnerAudioContext()
      audio.src = finalSrc
      audio.loop = false
      audio.volume = 1.0

      // 标记是否已成功初始化（防止多次 resolve/reject）
      let resolved = false
      let errorTimer: number | undefined

      console.log('创建音频:', finalSrc, audio)

      // 超时保护：5 秒后若仍未就绪，则按失败处理
      const timeout = setTimeout(() => {
        if (!resolved) {
          resolved = true
          audio.destroy() // 清理无用对象
          reject(new Error(`创建音频超时: ${finalSrc}`))
        }
      }, 5000) as unknown as number  // setTimeout 返回 NodeJS.Timeout，但 uni-app 环境是 number，强制转换

      // 可以播放时触发（元数据已加载）
      audio.onCanplay(() => {
        if (resolved) return
        resolved = true
        clearTimeout(timeout)

        const duration = audio.duration || 0
        const instance: AudioInstance = {
          id: audioId,
          audio,
          duration,
          currentTime: 0,
          volume: audio.volume,
          loop: audio.loop,
          paused: true
        }
        audioInstances.set(audioId, instance)
        resolve()
      })

      // 时间进度更新——持续同步 currentTime 和 duration
      audio.onTimeUpdate(() => {
        const instance = audioInstances.get(audioId)
        if (instance) {
          instance.currentTime = audio.currentTime || 0
          // duration 可能在播放过程中才正确获取
          if (audio.duration && audio.duration > 0) {
            instance.duration = audio.duration
          }
        }
      })

      // 播放结束
      audio.onEnded(() => {
        const instance = audioInstances.get(audioId)
        if (instance) {
          instance.paused = true
          if (!instance.loop) {
            instance.currentTime = 0
          }
        }
      })

      // 播放开始
      audio.onPlay(() => {
        const instance = audioInstances.get(audioId)
        if (instance) {
          instance.paused = false
        }
      })

      // 播放暂停
      audio.onPause(() => {
        const instance = audioInstances.get(audioId)
        if (instance) {
          instance.paused = true
        }
      })

      // 停止（uni-app 没有 onStop，但 stop 方法会触发 onEnded 或 onStop？实际测试 stop 后不会触发 onPause，需要手动处理）
      // 这里不依赖事件，在 stop 方法中手动修改状态

      // 错误处理
      audio.onError((err) => {
        console.log('音频错误:', err)
        if (resolved) {
          // 如果已经 resolve 了，只打印错误，不重复 reject
          console.error(`音频 "${audioId}" 运行时错误:`, err)
          return
        }
        resolved = true
        clearTimeout(timeout)
        audio.destroy()
        reject(new Error(`音频加载失败 (${finalSrc}): ${err || '未知错误'}`))
      })
    })
  },

  play(audioId: string): void {
    const instance = audioInstances.get(audioId)
    if (!instance) {
      console.error(`Audio instance "${audioId}" not found. Call createAudio first.`)
      return
    }

    // iOS 交互检查
    if (_isIOS() && !_hasUserInteraction) {
      console.warn('iOS需用户点击界面后方可播放')
      uni.showToast({
        title: '请点击屏幕后播放',
        icon: 'none'
      })
      return
    }

    try {
      instance.audio.play()

      // iOS 首次播放延迟处理
      if (_isIOS() && instance.currentTime === 0) {
        setTimeout(() => {
          const audio = audioInstances.get(audioId)
          if (audio) {
            audio.audio.seek(0)
          }
        }, 50)
      }

      // 注意：paused 状态会在 onPlay 事件中更新，这里先乐观设置
      instance.paused = false
    } catch (err) {
      console.error(`[播放异常] ${audioId}:`, err)
    }
  },

  pause(audioId: string): void {
    const instance = audioInstances.get(audioId)
    if (!instance) {
      console.error(`Audio instance "${audioId}" not found`)
      return
    }
    instance.audio.pause()
    instance.paused = true
  },

  stop(audioId: string): void {
    const instance = audioInstances.get(audioId)
    if (!instance) {
      console.error(`Audio instance "${audioId}" not found`)
      return
    }
    instance.audio.stop()
    instance.paused = true
    instance.currentTime = 0
  },

  seek(audioId: string, position: number): void {
    const instance = audioInstances.get(audioId)
    if (!instance) {
      console.error(`Audio instance "${audioId}" not found`)
      return
    }
    const duration = instance.duration || 0
    if (duration === 0) {
      console.warn(`音频 "${audioId}" 未准备好，无法 seek`)
      return
    }
    const seekPos = Math.max(0, Math.min(position, duration))
    instance.audio.seek(seekPos)
    instance.currentTime = seekPos
  },

  setVolume(audioId: string, volume: number): void {
    const instance = audioInstances.get(audioId)
    if (!instance) {
      console.error(`Audio instance "${audioId}" not found`)
      return
    }
    const vol = Math.max(0, Math.min(volume, 1))
    instance.audio.volume = vol
    instance.volume = vol
  },

  setLoop(audioId: string, loop: boolean): void {
    const instance = audioInstances.get(audioId)
    if (!instance) {
      console.error(`Audio instance "${audioId}" not found`)
      return
    }
    instance.audio.loop = loop
    instance.loop = loop
  },

  getDuration(audioId: string): number {
    const instance = audioInstances.get(audioId)
    return instance ? instance.duration : 0
  },

  getCurrentTime(audioId: string): number {
    const instance = audioInstances.get(audioId)
    return instance ? instance.currentTime : 0
  },

  isPaused(audioId: string): boolean {
    const instance = audioInstances.get(audioId)
    return instance ? instance.paused : true
  },

  destroyAudio(audioId: string): void {
    const instance = audioInstances.get(audioId)
    if (!instance) {
      console.error(`Audio instance "${audioId}" not found`)
      return
    }
    try {
      instance.audio.destroy()
    } catch (e) {
      console.warn('销毁音频对象时出错:', e)
    }
    audioInstances.delete(audioId)
  },

  getAudioState(audioId: string): AudioInstance | null {
    return audioInstances.get(audioId) || null
  }
}

// 导出：同时支持默认导入和命名导入
export default audioUtils
export { audioUtils }
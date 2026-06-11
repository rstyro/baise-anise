import type { App } from 'vue'
import versionUtils from './version'
import toolUtils from './tool'
import constConfig from './const'
import audioUtils from './audio'
import routerUtils from '../router'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $t: typeof toolUtils
    $version: typeof versionUtils
    $const: typeof constConfig
    $audio: typeof audioUtils
  }
}

declare global {
  interface Uni {
    $gversion: typeof versionUtils
    $gtool: typeof toolUtils
    $gconst: typeof constConfig
    $gaudio: typeof audioUtils
    $grouter: typeof routerUtils
  }
}

export default {
  install(app: App) {
    app.config.globalProperties.$t = toolUtils
    app.config.globalProperties.$version = versionUtils
    app.config.globalProperties.$const = constConfig
    app.config.globalProperties.$audio = audioUtils

    if (typeof uni !== 'undefined') {
      uni.$gversion = versionUtils
      uni.$gtool = toolUtils
      uni.$gconst = constConfig
      uni.$gaudio = audioUtils
      uni.$grouter = routerUtils
    } else {
      console.warn('uni 对象不存在，无法挂载工具到 uni')
    }
  }
}

export { versionUtils, toolUtils, constConfig, audioUtils, routerUtils }
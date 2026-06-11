interface ConstImages {
  BASE_URL: string
  SHARE_URL: string
}

interface ConstConfig {
  IMAGES: ConstImages
}

const constConfig: ConstConfig = {
  IMAGES: {
    BASE_URL: 'http://localhost:8800',
    SHARE_URL: 'https://66dashun.xyz/static/images/share.png'
  }
}

// 默认导出：import anything from './file' 时得到的就是 constConfig
export default constConfig

// 单独导出 constConfig，允许按需导入
// 命名导出：import { constConfig } from './file' 时也可以拿到同一个对象
export { constConfig }
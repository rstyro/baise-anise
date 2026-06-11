declare module 'uview-pro' {
  import { Plugin } from 'vue'
  const uviewPro: Plugin
  export default uviewPro
}

declare interface Uni {
  $u: {
    toast: (message: string, duration?: number, options?: any) => void
    message: (options: any) => void
    [key: string]: any
  }
}
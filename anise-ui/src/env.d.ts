/// <reference types="vite/client" />

declare module '*.vue' {
  import { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare global {
  interface ImportMeta {
    hot?: {
      accept: (...args: any[]) => void
      dispose: (...args: any[]) => void
      [key: string]: any
    }
  }
}

export {}

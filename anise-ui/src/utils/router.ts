import pagesJson from '../pages.json'

interface PageConfig {
  path: string
  name?: string
  style?: Record<string, unknown>
}

interface PagesConfig {
  pages: PageConfig[]
  subPackages?: Array<{
    root: string
    pages: PageConfig[]
  }>
}

const pagesConfig = pagesJson as PagesConfig

const routeMap = new Map<string, string>()

function buildRouteMap() {
  pagesConfig.pages.forEach((page) => {
    if (page.name) {
      routeMap.set(page.name, '/' + page.path)
    }
  })

  pagesConfig.subPackages?.forEach((subPkg) => {
    subPkg.pages.forEach((page) => {
      if (page.name) {
        routeMap.set(page.name, `/${subPkg.root}/${page.path}`)
      }
    })
  })
}

buildRouteMap()

interface NavigateOptions {
  query?: Record<string, string | number>
  success?: () => void
  fail?: (err: Error) => void
  complete?: () => void
}

function buildUrl(path: string, query?: Record<string, string | number>): string {
  if (!query) return path
  const params = Object.entries(query)
    .map(([key, value]) => `${key}=${encodeURIComponent(String(value))}`)
    .join('&')
  return `${path}?${params}`
}

const router = {
  getPathByName(name: string): string | undefined {
    return routeMap.get(name)
  },

  navigateTo(name: string, options: NavigateOptions = {}) {
    const path = this.getPathByName(name)
    if (!path) {
      const error = new Error(`Route not found: ${name}`)
      options.fail?.(error)
      options.complete?.()
      return
    }

    uni.navigateTo({
      url: buildUrl(path, options.query),
      success: options.success,
      fail: (err) => options.fail?.(new Error(err.errMsg || 'Navigation failed')),
      complete: options.complete
    })
  },

  redirectTo(name: string, options: NavigateOptions = {}) {
    const path = this.getPathByName(name)
    if (!path) {
      const error = new Error(`Route not found: ${name}`)
      options.fail?.(error)
      options.complete?.()
      return
    }

    uni.redirectTo({
      url: buildUrl(path, options.query),
      success: options.success,
      fail: (err) => options.fail?.(new Error(err.errMsg || 'Redirect failed')),
      complete: options.complete
    })
  },

  switchTab(name: string, options: Omit<NavigateOptions, 'query'> = {}) {
    const path = this.getPathByName(name)
    if (!path) {
      const error = new Error(`Route not found: ${name}`)
      options.fail?.(error)
      options.complete?.()
      return
    }

    uni.switchTab({
      url: path,
      success: options.success,
      fail: (err) => options.fail?.(new Error(err.errMsg || 'Switch tab failed')),
      complete: options.complete
    })
  },

  reLaunch(name: string, options: NavigateOptions = {}) {
    const path = this.getPathByName(name)
    if (!path) {
      const error = new Error(`Route not found: ${name}`)
      options.fail?.(error)
      options.complete?.()
      return
    }

    uni.reLaunch({
      url: buildUrl(path, options.query),
      success: options.success,
      fail: (err) => options.fail?.(new Error(err.errMsg || 'ReLaunch failed')),
      complete: options.complete
    })
  },

  navigateBack(delta = 1) {
    uni.navigateBack({ delta })
  }
}

export { router }
export const useRouter = () => router

export const getPathByName = router.getPathByName
export const navigateTo = router.navigateTo
export const redirectTo = router.redirectTo
export const switchTab = router.switchTab
export const reLaunch = router.reLaunch
export const navigateBack = router.navigateBack
export default router
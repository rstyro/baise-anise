/**
 * 获取完整图片URL
 * 后端返回的图片路径不以 http 开头时，自动拼接 BASE_URL 前缀
 */
export function getImageUrl(url: string): string {
  if (!url) return '/static/logo.png'
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return uni.$gconst.IMAGES.BASE_URL + url
}

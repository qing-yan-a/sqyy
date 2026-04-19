/**
 * 头像 URL：数据库只存路径如 /avatars/xxx.jpg，此处拼接后端地址确保图片可加载
 */
const AVATAR_BASE = import.meta.env.VITE_AVATAR_BASE || ''

export function avatarUrl(path?: string | null): string {
  if (!path || !path.trim()) return ''
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  // 如果没有配置 AVATAR_BASE，直接返回相对路径让 Vite 代理处理
  if (!AVATAR_BASE) return path
  const base = AVATAR_BASE.replace(/\/$/, '')
  const p = path.startsWith('/') ? path : '/' + path
  return base + p
}

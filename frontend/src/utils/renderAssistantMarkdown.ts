/**
 * 将助手返回的常见 Markdown 转为安全 HTML（先转义再替换，避免 XSS）。
 * 支持：### 标题、- 列表、行内代码 `、`**粗体**`、`*斜体*`、换行。
 */
export function renderAssistantMarkdown(text: string): string {
  if (!text) return ''
  let s = escapeHtml(text)
  // 行内代码（先于粗体/斜体）
  s = s.replace(/`([^`]+)`/g, '<code>$1</code>')
  // 粗体
  s = s.replace(/\*\*([\s\S]+?)\*\*/g, '<strong>$1</strong>')
  s = s.replace(/__([\s\S]+?)__/g, '<strong>$1</strong>')
  // 斜体（不与 ** 冲突）
  s = s.replace(/(?<!\*)\*([^*]+)\*(?!\*)/g, '<em>$1</em>')
  s = s.replace(/(?<!_)_([^_]+)_(?!_)/g, '<em>$1</em>')
  // ### 标题
  s = s.replace(/^### (.+)$/gm, '<h4>$1</h4>')
  // - 列表项
  s = s.replace(/^- (.+)$/gm, '<li>$1</li>')
  // 换行
  s = s.replace(/\n/g, '<br>')
  return s
}

function escapeHtml(s: string): string {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { Delete, Plus, Promotion } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { api } from '../api'
import { useAuthStore } from '../stores/auth'
import { renderAssistantMarkdown } from '../utils/renderAssistantMarkdown'

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  time: string
}

interface ChatSession {
  id: string
  title: string
  createdAt: number
  messages: ChatMessage[]
}

const STORAGE_KEY_PREFIX = 'sqyy-doctor-ai-sessions-v2'

const auth = useAuthStore()
const sessions = ref<ChatSession[]>([])
const currentSessionId = ref<string | null>(null)
const messages = ref<ChatMessage[]>([])
const inputText = ref('')
const loading = ref(false)
const chatBody = ref<HTMLElement | null>(null)

const hasUserMessage = computed(() => messages.value.some((m) => m.role === 'user'))
const storageKey = computed(() => {
  const userId = auth.user?.id
  const username = auth.user?.username?.trim()
  if (userId != null) return `${STORAGE_KEY_PREFIX}:${userId}`
  if (username) return `${STORAGE_KEY_PREFIX}:username:${username}`
  return `${STORAGE_KEY_PREFIX}:anonymous`
})

const examplePrompts = [
  '这个症状可能是什么疾病？',
  '某某药的用法用量与注意事项是什么？',
  '帮我梳理一下这个病例的问诊要点',
]

function getTime() {
  return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function welcomeContent() {
  return `您好${auth.user?.realName ? '，' + auth.user.realName : ''}医生！我是您的 AI 医疗助手。\n\n陪您聊天、查资料或辅助临床思路，有问题尽管问。`
}

function loadSessions() {
  try {
    const raw = localStorage.getItem(storageKey.value)
    if (raw) sessions.value = JSON.parse(raw) as ChatSession[]
  } catch {
    sessions.value = []
  }
}

function saveSessions() {
  localStorage.setItem(storageKey.value, JSON.stringify(sessions.value))
}

function newChat() {
  const id = crypto.randomUUID()
  const session: ChatSession = {
    id,
    title: '新对话',
    createdAt: Date.now(),
    messages: [{ role: 'assistant', content: welcomeContent(), time: getTime() }],
  }
  sessions.value.unshift(session)
  currentSessionId.value = id
  messages.value = [...session.messages]
  saveSessions()
}

function selectSession(id: string) {
  const s = sessions.value.find((x) => x.id === id)
  if (!s) return
  currentSessionId.value = id
  messages.value = [...s.messages]
  nextTick(() => scrollToBottom())
}

function persistCurrent() {
  if (!currentSessionId.value) return
  const s = sessions.value.find((x) => x.id === currentSessionId.value)
  if (!s) return
  s.messages = messages.value.map((m) => ({ ...m }))
  const firstUser = messages.value.find((m) => m.role === 'user')
  if (firstUser) {
    s.title = firstUser.content.slice(0, 22) + (firstUser.content.length > 22 ? '…' : '')
  }
  saveSessions()
}

onMounted(() => {
  loadSessions()
  if (sessions.value.length === 0) {
    newChat()
  } else {
    currentSessionId.value = sessions.value[0].id
    messages.value = [...sessions.value[0].messages]
  }
})

watch(
  () => [auth.user?.id, auth.user?.username],
  () => {
    currentSessionId.value = null
    messages.value = []
    loadSessions()
    if (sessions.value.length === 0) {
      newChat()
    } else {
      currentSessionId.value = sessions.value[0].id
      messages.value = [...sessions.value[0].messages]
      scrollToBottom()
    }
  },
)

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text, time: getTime() })
  inputText.value = ''
  persistCurrent()
  scrollToBottom()

  loading.value = true
  try {
    const result = await api.sendDoctorAiMessage(text)
    messages.value.push({ role: 'assistant', content: result.reply, time: getTime() })
  } catch (error) {
    messages.value.push({
      role: 'assistant',
      content: error instanceof Error ? error.message : 'AI 服务暂时不可用，请稍后重试',
      time: getTime(),
    })
  } finally {
    loading.value = false
    persistCurrent()
    scrollToBottom()
  }
}

function onKeyDown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (chatBody.value) chatBody.value.scrollTop = chatBody.value.scrollHeight
  })
}

function pickPrompt(t: string) {
  inputText.value = t
}

function formatSessionTitle(s: ChatSession) {
  return s.title || '新对话'
}

function assistantHtml(content: string) {
  return renderAssistantMarkdown(content)
}

async function confirmRemoveSession(id: string) {
  try {
    await ElMessageBox.confirm('确定删除这条对话？删除后无法恢复。', '删除对话', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  removeSession(id)
}

function removeSession(id: string) {
  const idx = sessions.value.findIndex((x) => x.id === id)
  if (idx === -1) return
  const wasCurrent = currentSessionId.value === id
  sessions.value.splice(idx, 1)
  saveSessions()
  if (!wasCurrent) return
  if (sessions.value.length > 0) {
    selectSession(sessions.value[0].id)
  } else {
    newChat()
  }
}

async function confirmClearAll() {
  if (sessions.value.length === 0) return
  try {
    await ElMessageBox.confirm('确定清空全部历史对话？此操作不可恢复。', '清空历史', {
      type: 'warning',
      confirmButtonText: '清空',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  sessions.value = []
  saveSessions()
  newChat()
}
</script>

<template>
  <div class="doctor-ai-studio">
    <aside class="studio-sidebar">
      <div class="studio-brand">
        <span class="studio-brand__name">社区医院 AI</span>
        <span class="studio-brand__tag">助手</span>
      </div>
      <el-button class="studio-new-btn" round @click="newChat">+ 新对话</el-button>
      <div class="studio-history-header">
        <span class="studio-history-label">历史记录</span>
        <button
          v-if="sessions.length > 0"
          type="button"
          class="studio-history-clear"
          @click="confirmClearAll"
        >
          清空
        </button>
      </div>
      <div class="studio-history-scroll">
        <div
          v-for="s in sessions"
          :key="s.id"
          class="studio-history-row"
          :class="{ active: currentSessionId === s.id }"
        >
          <button type="button" class="studio-history-item" @click="selectSession(s.id)">
            <span class="studio-history-item__title">{{ formatSessionTitle(s) }}</span>
          </button>
          <button
            type="button"
            class="studio-history-delete"
            title="删除"
            @click.stop="confirmRemoveSession(s.id)"
          >
            <el-icon><Delete /></el-icon>
          </button>
        </div>
      </div>
      <div class="studio-sidebar-footer">
        <div class="studio-user-avatar">{{ (auth.user?.realName || '?').slice(0, 1) }}</div>
        <span class="studio-user-name">{{ auth.user?.realName || '用户' }}</span>
      </div>
    </aside>

    <div class="studio-main">
      <header class="studio-toolbar">
        <div class="studio-model">
          <span class="studio-model-label">模型</span>
          <span class="studio-model-value">MiMo-V2-Flash</span>
        </div>
        <a
          class="studio-api-link"
          href="https://platform.xiaomimimo.com/#/docs/welcome"
          target="_blank"
          rel="noopener noreferrer"
        >
          MiMo 开放平台 ↗
        </a>
      </header>

      <div v-if="!hasUserMessage" class="studio-welcome">
        <h1 class="studio-welcome__title">社区医院 AI 助手</h1>
        <p class="studio-welcome__subtitle">
          陪您聊天、查资料或辅助临床思路，准备好探索更多可能
        </p>
        <div class="studio-welcome__hint">您可以这样问</div>
        <div class="studio-prompts">
          <button
            v-for="(p, i) in examplePrompts"
            :key="i"
            type="button"
            class="studio-prompt-chip"
            @click="pickPrompt(p)"
          >
            {{ p }}
          </button>
        </div>
      </div>

      <div v-else ref="chatBody" class="studio-chat">
        <div v-for="(msg, i) in messages" :key="i" class="studio-msg" :class="msg.role">
          <div class="studio-msg__bubble">
            <div
              v-if="msg.role === 'assistant'"
              class="studio-msg__text studio-msg__markdown"
              v-html="assistantHtml(msg.content)"
            />
            <div v-else class="studio-msg__text">{{ msg.content }}</div>
            <div class="studio-msg__time">{{ msg.time }}</div>
          </div>
        </div>
        <div v-if="loading" class="studio-msg assistant">
          <div class="studio-msg__bubble studio-msg__typing">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>

      <div class="studio-input-area">
        <div class="studio-input-shell">
          <el-input
            v-model="inputText"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 8 }"
            :rows="1"
            class="studio-input-field"
            placeholder="有问题，尽管问，Shift + Enter 换行"
            resize="none"
            @keydown="onKeyDown"
          />
          <div class="studio-input-actions">
            <el-button text circle class="studio-icon-btn" title="附件（演示）">
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button
              type="primary"
              round
              class="studio-send-btn"
              :loading="loading"
              :disabled="!inputText.trim()"
              @click="sendMessage"
            >
              <el-icon><Promotion /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.doctor-ai-studio {
  display: flex;
  margin: -24px;
  margin-top: -16px;
  min-height: calc(100vh - 88px);
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
}

.studio-sidebar {
  width: 240px;
  flex-shrink: 0;
  background: #f5f7f9;
  border-right: 1px solid #e8eaed;
  display: flex;
  flex-direction: column;
  padding: 20px 12px 16px;
}

.studio-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px 16px;
}

.studio-brand__name {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  letter-spacing: -0.02em;
}

.studio-brand__tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.12);
  color: #2563eb;
  font-weight: 500;
}

.studio-new-btn {
  width: 100%;
  margin-bottom: 16px;
  border: 1px dashed #c4c9d4;
  color: #374151;
  background: #fff;
}

.studio-history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px 8px 8px;
  gap: 8px;
}

.studio-history-label {
  font-size: 12px;
  color: #9ca3af;
}

.studio-history-clear {
  font-size: 12px;
  color: #9ca3af;
  border: none;
  background: none;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 6px;
}

.studio-history-clear:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.08);
}

.studio-history-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-height: 120px;
}

.studio-history-row {
  display: flex;
  align-items: center;
  gap: 2px;
  border-radius: 10px;
  transition: background 0.15s;
}

.studio-history-row:hover {
  background: rgba(0, 0, 0, 0.04);
}

.studio-history-row.active {
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
}

.studio-history-row.active .studio-history-item {
  font-weight: 500;
}

.studio-history-item {
  flex: 1;
  min-width: 0;
  text-align: left;
  padding: 10px 4px 10px 12px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #374151;
  font-size: 13px;
  cursor: pointer;
  line-height: 1.4;
  transition: background 0.15s;
}

.studio-history-item__title {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.studio-history-delete {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  margin-right: 4px;
  padding: 0;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #9ca3af;
  cursor: pointer;
  opacity: 0.45;
  transition:
    color 0.15s,
    opacity 0.15s,
    background 0.15s;
}

.studio-history-row:hover .studio-history-delete,
.studio-history-row.active .studio-history-delete {
  opacity: 0.85;
}

.studio-history-delete:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
  opacity: 1;
}

.studio-sidebar-footer {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 8px 0;
  border-top: 1px solid #e8eaed;
  margin-top: 12px;
}

.studio-user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e5e7eb, #d1d5db);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
  color: #4b5563;
}

.studio-user-name {
  font-size: 13px;
  color: #374151;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.studio-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: #fff;
}

.studio-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  border-bottom: 1px solid #f0f1f3;
}

.studio-model {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.studio-model-label {
  color: #9ca3af;
}

.studio-model-value {
  color: #111827;
  font-weight: 500;
}

.studio-api-link {
  font-size: 13px;
  color: #6b7280;
  text-decoration: none;
}

.studio-api-link:hover {
  color: #2563eb;
}

.studio-welcome {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 24px 24px;
  text-align: center;
}

.studio-welcome__title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  letter-spacing: -0.03em;
}

.studio-welcome__subtitle {
  margin: 12px 0 0;
  font-size: 15px;
  color: #6b7280;
  max-width: 480px;
  line-height: 1.6;
}

.studio-welcome__hint {
  margin-top: 40px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #9ca3af;
}

.studio-prompts {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  max-width: 520px;
}

.studio-prompt-chip {
  padding: 14px 18px;
  border: 1px solid #e8eaed;
  border-radius: 14px;
  background: #f9fafb;
  color: #374151;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
}

.studio-prompt-chip:hover {
  border-color: #d1d5db;
  background: #f3f4f6;
}

.studio-chat {
  flex: 1;
  overflow-y: auto;
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.studio-msg {
  display: flex;
  max-width: 720px;
  width: 100%;
}

.studio-msg.user {
  align-self: flex-end;
  justify-content: flex-end;
}

.studio-msg.assistant {
  align-self: flex-start;
}

.studio-msg__bubble {
  padding: 14px 18px;
  border-radius: 16px;
  max-width: 100%;
}

.studio-msg.user .studio-msg__bubble {
  background: #111827;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.studio-msg.assistant .studio-msg__bubble {
  background: #f5f7f9;
  color: #1f2937;
  border-bottom-left-radius: 4px;
}

.studio-msg__text {
  white-space: pre-wrap;
  line-height: 1.65;
  font-size: 14px;
}

.studio-msg__markdown {
  white-space: normal;
  word-break: break-word;
}

.studio-msg__markdown :deep(strong) {
  font-weight: 600;
}

.studio-msg__markdown :deep(em) {
  font-style: italic;
}

.studio-msg__markdown :deep(code) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  font-size: 0.9em;
  padding: 0.12em 0.38em;
  border-radius: 4px;
  background: rgba(15, 23, 42, 0.07);
}

.studio-msg__markdown :deep(h4) {
  margin: 8px 0 4px;
  font-size: 15px;
  font-weight: 600;
}

.studio-msg__markdown :deep(li) {
  margin: 2px 0 2px 16px;
  list-style-type: disc;
}


.studio-msg__time {
  margin-top: 8px;
  font-size: 11px;
  opacity: 0.45;
  text-align: right;
}

.studio-msg.user .studio-msg__time {
  color: rgba(255, 255, 255, 0.7);
}

.studio-msg__typing {
  display: flex;
  gap: 6px;
  align-items: center;
  padding: 12px 16px;
}

.studio-msg__typing span {
  width: 7px;
  height: 7px;
  background: #9ca3af;
  border-radius: 50%;
  animation: studioDot 1.2s infinite ease-in-out both;
}

.studio-msg__typing span:nth-child(2) {
  animation-delay: 0.15s;
}
.studio-msg__typing span:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes studioDot {
  0%,
  80%,
  100% {
    transform: scale(0.6);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.studio-input-area {
  padding: 16px 24px 24px;
  background: #fff;
}

.studio-input-shell {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 10px 12px 10px 16px;
  border: 1px solid #e8eaed;
  border-radius: 20px;
  background: #fafbfc;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.studio-input-shell:focus-within {
  border-color: #d1d5db;
  box-shadow: 0 0 0 3px rgba(15, 23, 42, 0.04);
}

.studio-input-field {
  flex: 1;
  min-width: 0;
}

.studio-input-field :deep(.el-textarea__inner) {
  border: none;
  box-shadow: none;
  background: transparent;
  padding: 6px 0;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
}

.studio-input-field :deep(.el-textarea__inner:focus) {
  box-shadow: none;
}

.studio-input-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
  padding-bottom: 2px;
}

.studio-icon-btn {
  color: #9ca3af;
}

.studio-send-btn {
  width: 40px;
  height: 40px;
  padding: 0;
}

@media (max-width: 900px) {
  .doctor-ai-studio {
    flex-direction: column;
    margin: -16px;
    min-height: calc(100vh - 72px);
  }

  .studio-sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    border-right: none;
    border-bottom: 1px solid #e8eaed;
    padding: 12px;
  }

  .studio-history-scroll {
    flex-direction: row;
    overflow-x: auto;
    width: 100%;
    min-height: auto;
  }

  .studio-history-item {
    white-space: nowrap;
    flex-shrink: 0;
  }

  .studio-sidebar-footer {
    width: 100%;
    border-top: none;
    margin-top: 8px;
    padding-top: 8px;
  }
}
</style>

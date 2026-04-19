<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { api } from '../../api'
import { useAuthStore } from '../../stores/auth'
import { Position } from '@element-plus/icons-vue'

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  time: string
}

const auth = useAuthStore()
const messages = ref<ChatMessage[]>([])
const inputText = ref('')
const loading = ref(false)
const chatBody = ref<HTMLElement | null>(null)

onMounted(() => {
  messages.value.push({
    role: 'assistant',
    content: '您好' + (auth.user?.realName ? '，' + auth.user.realName : '') + '！我是您的AI健康助手 🩺\n\n有什么健康问题可以问我，比如：\n• 最近头疼怎么办？\n• 感冒了应该吃什么药？\n• 如何预防高血压？',
    time: getTime(),
  })
})

function getTime() {
  return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({
    role: 'user',
    content: text,
    time: getTime(),
  })
  inputText.value = ''
  scrollToBottom()

  loading.value = true
  try {
    const result = await api.sendAiMessage(text)
    messages.value.push({
      role: 'assistant',
      content: result.reply,
      time: getTime(),
    })
  } catch (error) {
    messages.value.push({
      role: 'assistant',
      content: error instanceof Error ? error.message : '抱歉，AI服务暂时不可用，请稍后重试 😥',
      time: getTime(),
    })
  } finally {
    loading.value = false
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
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}
</script>

<template>
  <div class="ai-assistant">
    <div ref="chatBody" class="chat-body">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        class="message-wrapper"
        :class="msg.role"
      >
        <div class="avatar">
          {{ msg.role === 'assistant' ? '🤖' : '😊' }}
        </div>
        <div class="message-bubble">
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-time">{{ msg.time }}</div>
        </div>
      </div>

      <div v-if="loading" class="message-wrapper assistant">
        <div class="avatar">🤖</div>
        <div class="message-bubble typing">
          <div class="typing-dots">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="inputText"
        type="textarea"
        :rows="1"
        :autosize="{ minRows: 1, maxRows: 4 }"
        placeholder="输入您的健康问题..."
        @keydown="onKeyDown"
      />
      <el-button
        type="primary"
        circle
        :icon="Position"
        :loading="loading"
        :disabled="!inputText.trim()"
        @click="sendMessage"
      />
    </div>
  </div>
</template>

<style scoped>
.ai-assistant {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px - 48px);
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-wrapper {
  display: flex;
  gap: 8px;
  max-width: 85%;
}

.message-wrapper.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-wrapper.assistant {
  align-self: flex-start;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.user .message-bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.assistant .message-bubble {
  background: #fff;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.message-content {
  white-space: pre-wrap;
}

.message-time {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.3);
  margin-top: 4px;
  text-align: right;
}

.user .message-time {
  color: rgba(255, 255, 255, 0.6);
}

.typing-dots {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  background: #c0c4cc;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing-dots span:nth-child(1) { animation-delay: -0.32s; }
.typing-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 20px;
  resize: none;
  padding: 8px 16px;
}
</style>

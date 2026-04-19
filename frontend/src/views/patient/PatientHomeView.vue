<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../../api'
import { useAuthStore } from '../../stores/auth'
import { Right, Refresh } from '@element-plus/icons-vue'
import type { HealthNewsItem } from '../../types'

const router = useRouter()
const auth = useAuthStore()

interface QuickAction {
  title: string
  desc: string
  icon: string
  color: string
  bg: string
  route: string
}

const quickActions: QuickAction[] = [
  { title: '在线挂号', desc: '选择科室和医生，预约就诊', icon: '📅', color: '#409eff', bg: 'rgba(64,158,255,0.08)', route: '/user-center/register' },
  { title: '挂号记录', desc: '查看挂号历史，支持退号', icon: '📋', color: '#67c23a', bg: 'rgba(103,194,58,0.08)', route: '/user-center/records' },
  { title: '历史就诊记录', desc: '就诊、诊断、处方记录', icon: '📖', color: '#9b59b6', bg: 'rgba(155,89,182,0.08)', route: '/user-center/history' },
  { title: '科室与医生', desc: '查看科室介绍和医生信息', icon: '🏥', color: '#e6a23c', bg: 'rgba(230,162,60,0.08)', route: '/user-center/departments' },
  { title: 'AI 健康助手', desc: '智能问答，解答健康疑问', icon: '🤖', color: '#f56c6c', bg: 'rgba(245,108,108,0.08)', route: '/user-center/ai' },
  { title: '个人信息', desc: '管理个人资料和医疗信息', icon: '👤', color: '#909399', bg: 'rgba(144,147,153,0.08)', route: '/user-center/profile' },
]

const newsList = ref<HealthNewsItem[]>([])
const newsLoading = ref(false)

async function loadNews() {
  newsLoading.value = true
  try {
    const data = await api.getHealthNews()
    newsList.value = Array.isArray(data) ? data.slice(0, 3) : []
  } catch { newsList.value = [] }
  finally { newsLoading.value = false }
}

function openUrl(url: string) { if (url) window.open(url, '_blank') }

function onImageError(e: Event) {
  const img = e.target as HTMLImageElement
  img.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="120" fill="%23ddd"><rect width="200" height="120"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23999" font-size="14">暂无图片</text></svg>'
}

function go(path: string) { router.push(path) }

onMounted(() => { loadNews() })
</script>

<template>
  <div class="patient-home">
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2>你好，{{ auth.user?.realName || '患者' }}！</h2>
        <p>欢迎使用社区医院患者服务平台，在这里您可以完成挂号、查看就诊记录、咨询健康问题等操作。</p>
      </div>
    </div>

    <div class="section">
      <h3 class="section-title">常用功能</h3>
      <div class="quick-grid">
        <div v-for="action in quickActions" :key="action.title" class="quick-card" @click="go(action.route)">
          <div class="quick-icon" :style="{ background: action.bg, color: action.color }">{{ action.icon }}</div>
          <div class="quick-info">
            <div class="quick-title">{{ action.title }}</div>
            <div class="quick-desc">{{ action.desc }}</div>
          </div>
          <el-icon class="quick-arrow"><Right /></el-icon>
        </div>
      </div>
    </div>

    <div class="section">
      <div class="section-header">
        <h3 class="section-title">健康资讯</h3>
        <el-button :icon="Refresh" circle size="small" :loading="newsLoading" @click="loadNews" />
      </div>
      <div v-loading="newsLoading" class="news-list" element-loading-text="AI 正在生成资讯...">
        <el-empty v-if="newsList.length === 0 && !newsLoading" description="暂无资讯" />
        <div v-for="(news, index) in newsList" :key="index" class="news-card" @click="openUrl(news.url)">
          <div class="news-image" v-if="news.image">
            <img :src="news.image" :alt="news.title" @error="onImageError" />
          </div>
          <div class="news-body">
            <div class="news-title">{{ news.title }}</div>
            <div class="news-summary">{{ news.summary }}</div>
            <div class="news-meta">
              <span class="news-source" v-if="news.source">{{ news.source }}</span>
              <span class="news-link" v-if="news.url">查看原文</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.patient-home { display: flex; flex-direction: column; gap: 24px; }

.welcome-banner {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 24px 28px;
}
.welcome-text h2 { margin: 0 0 8px; font-size: 22px; color: #111827; }
.welcome-text p { margin: 0; color: #6b7280; font-size: 14px; line-height: 1.6; }

.section { display: flex; flex-direction: column; gap: 16px; }
.section-title { margin: 0; font-size: 17px; font-weight: 600; color: #1f2937; }
.section-header { display: flex; justify-content: space-between; align-items: center; }

.quick-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.quick-card {
  display: flex; align-items: center; gap: 14px; padding: 18px 20px;
  background: rgba(255, 255, 255, 0.8); border: 1px solid rgba(229, 231, 235, 0.8);
  border-radius: 12px; cursor: pointer; transition: all 0.2s;
}
.quick-card:hover { box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08); transform: translateY(-1px); }
.quick-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; flex-shrink: 0; }
.quick-info { flex: 1; min-width: 0; }
.quick-title { font-size: 15px; font-weight: 600; color: #111827; }
.quick-desc { font-size: 13px; color: #9ca3af; margin-top: 2px; }
.quick-arrow { color: #d1d5db; flex-shrink: 0; }

.news-list { display: flex; flex-direction: column; gap: 16px; min-height: 120px; }
.news-card {
  display: flex; gap: 20px; background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.8); border-radius: 14px;
  padding: 20px 24px; cursor: pointer; transition: all 0.2s; width: 80%;
}
.news-card:hover { box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08); transform: translateY(-1px); }
.news-image { width: 180px; height: 120px; flex-shrink: 0; border-radius: 10px; overflow: hidden; background: #f5f7fa; }
.news-image img { width: 100%; height: 100%; object-fit: cover; }
.news-body { flex: 1; display: flex; flex-direction: column; justify-content: space-between; min-width: 0; }
.news-title { font-size: 17px; font-weight: 600; color: #303133; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.news-summary { font-size: 14px; color: #606266; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; margin-top: 8px; }
.news-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.news-source { font-size: 13px; color: #909399; }
.news-link { font-size: 13px; color: #409eff; }
</style>

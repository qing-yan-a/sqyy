<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import type { HealthNewsItem } from '../../types'

const loading = ref(false)
const newsList = ref<HealthNewsItem[]>([])

onMounted(() => {
  loadNews()
})

async function loadNews() {
  loading.value = true
  try {
    const data = await api.getHealthNews()
    newsList.value = Array.isArray(data) ? data.slice(0, 3) : []
  } catch {
    ElMessage.error('获取健康资讯失败')
  } finally {
    loading.value = false
  }
}

function openUrl(url: string) {
  if (url) window.open(url, '_blank')
}

function onImageError(e: Event) {
  const img = e.target as HTMLImageElement
  img.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="120" fill="%23ddd"><rect width="200" height="120"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23999" font-size="14">暂无图片</text></svg>'
}
</script>

<template>
  <div class="health-news">
    <div class="news-header">
      <h3>今日健康资讯</h3>
      <el-button :icon="Refresh" circle size="small" :loading="loading" @click="loadNews" />
    </div>

    <div v-loading="loading" class="news-list" element-loading-text="AI 正在生成资讯，请稍候...">
      <el-empty v-if="newsList.length === 0 && !loading" description="暂无资讯" />

      <div
        v-for="(news, index) in newsList"
        :key="index"
        class="news-card"
        @click="openUrl(news.url)"
      >
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
</template>

<style scoped>
.health-news {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.news-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 200px;
}

.news-card {
  display: flex;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.news-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.news-image {
  width: 100px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.news-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-summary {
  font-size: 13px;
  color: #909399;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-top: 4px;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.news-source {
  font-size: 12px;
  color: #c0c4cc;
}

.news-link {
  font-size: 12px;
  color: #409eff;
}
</style>

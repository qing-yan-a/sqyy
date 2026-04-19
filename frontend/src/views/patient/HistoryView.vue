<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'
import { ElMessage } from 'element-plus'
import type { TimelineResult } from '../../types'

const timeline = ref<TimelineResult | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    timeline.value = await api.getPatientTimeline()
  } catch {
    ElMessage.error('加载就诊记录失败')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="history-view">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <span>📋 历史就诊记录</span>
      </template>

      <el-empty v-if="!loading && (!timeline || timeline.events.length === 0)" description="暂无就诊记录" />

      <el-timeline v-else-if="timeline">
        <el-timeline-item
          v-for="(event, index) in timeline.events"
          :key="index"
          :timestamp="new Date(event.time).toLocaleString('zh-CN')"
          placement="top"
          :type="event.type === 'VISIT' ? 'primary' : event.type === 'DIAGNOSIS' ? 'warning' : 'success'"
        >
          <div class="timeline-event">
            <span class="event-type">
              {{ event.type === 'VISIT' ? '🏥 就诊' : event.type === 'DIAGNOSIS' ? '🩺 诊断' : '💊 处方' }}
            </span>
            <span class="event-title">{{ event.title }}</span>
            <span v-if="event.description" class="event-desc">{{ event.description }}</span>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<style scoped>
.history-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.timeline-event {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.event-type {
  font-size: 12px;
  color: #909399;
}

.event-title {
  font-weight: 500;
  color: #303133;
}

.event-desc {
  font-size: 13px;
  color: #606266;
}
</style>

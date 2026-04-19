<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { VisitDetail } from '../../types'

const loading = ref(false)
const cancellingId = ref<number | null>(null)
const registrations = ref<VisitDetail[]>([])

onMounted(async () => {
  await loadRegistrations()
})

async function loadRegistrations() {
  loading.value = true
  try {
    registrations.value = await api.getPatientRegistrations()
  } catch {
    // 静默失败
  } finally {
    loading.value = false
  }
}

async function handleCancel(visitId: number) {
  try {
    await ElMessageBox.confirm('确定要退号吗？退号后需重新挂号。', '退号确认', { type: 'warning' })
    cancellingId.value = visitId
    await api.cancelPatientRegistration(visitId)
    ElMessage.success('退号成功')
    await loadRegistrations()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e instanceof Error ? e.message : '退号失败')
  } finally {
    cancellingId.value = null
  }
}

function statusTag(status: string) {
  const map: Record<string, { text: string; type: 'success' | 'warning' | 'info' | 'danger' }> = {
    PENDING: { text: '待就诊', type: 'warning' },
    COMPLETED: { text: '已完成', type: 'success' },
    CANCELLED: { text: '已退号', type: 'info' },
  }
  return map[status] || { text: status, type: 'info' }
}
</script>

<template>
  <div class="records-page">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>📋 就诊记录</span>
          <el-button type="primary" size="small" @click="loadRegistrations">刷新</el-button>
        </div>
      </template>

      <el-empty v-if="registrations.length === 0 && !loading" description="暂无就诊记录" />

      <div v-else class="record-list">
        <el-card v-for="item in registrations" :key="item.visit.id" class="record-card" shadow="hover">
          <div class="record-header">
            <span class="visit-no">{{ item.visit.visitNo }}</span>
            <el-tag
              :type="statusTag(item.visit.status || '').type"
              size="small"
            >
              {{ statusTag(item.visit.status || '').text }}
            </el-tag>
          </div>
          <div class="record-body">
            <div class="record-row">
              <span class="row-label">科室</span>
              <span>{{ item.visit.department }}</span>
            </div>
            <div class="record-row">
              <span class="row-label">医生</span>
              <span>{{ item.visit.doctorName }}</span>
            </div>
            <div class="record-row">
              <span class="row-label">主诉</span>
              <span>{{ item.visit.chiefComplaint || '-' }}</span>
            </div>
            <div class="record-row">
              <span class="row-label">就诊时间</span>
              <span>{{ new Date(item.visit.visitTime).toLocaleString('zh-CN') }}</span>
            </div>
            <div v-if="item.visit.queueNumber" class="record-row">
              <span class="row-label">排队号</span>
              <span class="queue-num">{{ item.visit.queueNumber }}</span>
            </div>
            <div v-if="item.visit.status === 'PENDING'" class="record-row cancel-row">
              <el-button type="danger" size="small" :loading="cancellingId === item.visit.id" @click="handleCancel(item.visit.id)">
                退号
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.records-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-card {
  border-radius: 10px;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.visit-no {
  font-weight: 600;
  color: #409eff;
  font-size: 14px;
}

.record-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-row {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.row-label {
  width: 64px;
  flex-shrink: 0;
  color: #909399;
}

.queue-num {
  color: #e6a23c;
  font-weight: 600;
}

.cancel-row {
  margin-top: 8px;
}
</style>

<script setup lang="ts">
import { reactive } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { api } from '../api'
import StatCard from '../components/StatCard.vue'
import type { SearchResult } from '../types'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

const state = reactive<{
  loading: boolean
  syncing: boolean
  keyword: string
  result: SearchResult
}>({
  loading: false,
  syncing: false,
  keyword: '',
  result: {
    keyword: '',
    patients: [],
    visits: [],
    drugs: [],
    engine: 'unknown',
  },
})

async function search() {
  if (!state.keyword.trim()) { ElMessage.warning('请输入关键词'); return }
  state.loading = true
  try {
    state.result = await api.search(state.keyword)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '检索失败')
  } finally {
    state.loading = false
  }
}

async function syncSearch() {
  state.syncing = true
  try {
    const result = await api.syncSearch()
    ElMessage.success(result.message)
    state.result.engine = result.engine
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '同步失败')
  } finally {
    state.syncing = false
  }
}
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>全局智能检索</span>
        <el-space>
          <el-tag type="success">引擎：{{ state.result.engine }}</el-tag>
          <el-button v-if="authStore.hasRole(['ADMIN'])" size="small" :loading="state.syncing" @click="syncSearch">
            同步 ES 索引
          </el-button>
        </el-space>
      </div>
    </template>

    <div class="search-bar">
      <el-input
        v-model="state.keyword"
        :prefix-icon="Search"
        placeholder="输入患者姓名、就诊主诉、药品名称..."
        size="large"
        clearable
        @keyup.enter="search"
      />
      <el-button type="primary" size="large" :loading="state.loading" @click="search">搜索</el-button>
    </div>

    <div class="stat-row">
      <StatCard label="患者命中" :value="state.result.patients.length" />
      <StatCard label="就诊命中" :value="state.result.visits.length" />
      <StatCard label="药品命中" :value="state.result.drugs.length" />
    </div>

    <div v-if="state.result.patients.length || state.result.visits.length || state.result.drugs.length" class="results">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-card shadow="never">
            <template #header><span>患者 ({{ state.result.patients.length }})</span></template>
            <el-empty v-if="state.result.patients.length === 0" description="无匹配" :image-size="60" />
            <div v-for="p in state.result.patients" :key="p.id" class="result-item">
              <div class="result-name">{{ p.name }}</div>
              <div class="result-sub">{{ p.patientNo }} | {{ p.gender }} | {{ p.phone || '-' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="never">
            <template #header><span>就诊 ({{ state.result.visits.length }})</span></template>
            <el-empty v-if="state.result.visits.length === 0" description="无匹配" :image-size="60" />
            <div v-for="v in state.result.visits" :key="v.visit.id" class="result-item">
              <div class="result-name">{{ v.patient.name }} / {{ v.visit.department }}</div>
              <div class="result-sub">{{ v.visit.chiefComplaint || '无主诉' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="never">
            <template #header><span>药品 ({{ state.result.drugs.length }})</span></template>
            <el-empty v-if="state.result.drugs.length === 0" description="无匹配" :image-size="60" />
            <div v-for="d in state.result.drugs" :key="d.id" class="result-item">
              <div class="result-name">{{ d.drugName }}</div>
              <div class="result-sub">{{ d.specification || '-' }} | 库存 {{ d.stock }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-bar { display: flex; gap: 12px; margin-bottom: 20px; }
.search-bar .el-input { flex: 1; }
.stat-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 20px; }
.results .el-card { min-height: 200px; }
.result-item { padding: 10px 0; border-bottom: 1px solid var(--el-border-color-lighter); }
.result-item:last-child { border-bottom: none; }
.result-name { font-weight: 500; color: var(--el-text-color-primary); }
.result-sub { font-size: 13px; color: var(--el-text-color-secondary); margin-top: 2px; }
</style>

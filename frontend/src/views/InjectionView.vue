<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api'
import type { PharmacyVisitDetail } from '../types'

const loading = ref(false)
const visits = ref<PharmacyVisitDetail[]>([])
const actionLoading = ref<number | null>(null)

function getInjectionItems(row: PharmacyVisitDetail) {
  return row.prescriptions.filter((item) => item.drugType === 'INJECTION')
}

async function loadData() {
  loading.value = true
  try {
    visits.value = await api.getInjectionTodos()
  } finally {
    loading.value = false
  }
}

async function handleComplete(row: PharmacyVisitDetail) {
  try {
    actionLoading.value = row.visit.id
    await api.completeInjection(row.visit.id)
    ElMessage.success('注射待办已完成')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '操作失败')
  } finally {
    actionLoading.value = null
    await loadData()
  }
}

onMounted(loadData)
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>注射药物</span>
        <el-button type="primary" :disabled="actionLoading !== null" @click="loadData">刷新</el-button>
      </div>
    </template>

    <el-table :data="visits" v-loading="loading" border>
      <el-table-column prop="visit.visitNo" label="就诊编号" min-width="140" />
      <el-table-column prop="patient.name" label="患者" width="100" />
      <el-table-column prop="visit.department" label="科室" width="120" />
      <el-table-column prop="visit.doctorName" label="医生" width="100" />
      <el-table-column label="注射药物" min-width="260">
        <template #default="{ row }">
          <div
            v-for="item in getInjectionItems(row)"
            :key="item.id"
            class="prescription-item"
          >
            {{ item.drugName }}{{ item.dosage ? ` · ${item.dosage}` : '' }}
            {{ item.frequency ? ` · ${item.frequency}` : '' }} · {{ item.days }}天 · x{{ item.quantity }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="取药状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.pickupStatus === 'DISPENSED' ? 'success' : 'warning'">
            {{ row.pickupStatus === 'DISPENSED' ? '已取药' : '待取药' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注射状态" width="120">
        <template #default>
          <el-tag type="warning">待注射</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            link
            :loading="actionLoading === row.visit.id"
            :disabled="row.pickupStatus !== 'DISPENSED'"
            @click="handleComplete(row)"
          >
            完成注射
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.prescription-item {
  padding: 4px 0;
  font-size: 13px;
}
</style>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api'
import type { PharmacyVisitDetail } from '../types'

const route = useRoute()
const loading = ref(false)
const actionLoading = ref(false)
const detail = ref<PharmacyVisitDetail | null>(null)

const visitId = computed(() => Number(route.params.visitId))
const injectionPrescriptions = computed(() =>
  (detail.value?.prescriptions ?? []).filter((item) => item.drugType === 'INJECTION')
)

async function loadData() {
  if (!visitId.value) {
    return
  }
  loading.value = true
  try {
    detail.value = await api.getPharmacyVisitDetail(visitId.value)
  } finally {
    loading.value = false
  }
}

async function handlePickup() {
  if (!detail.value) {
    return
  }
  const needPayment = detail.value.paymentStatus !== 'PAID'
  const confirmButtonText = needPayment ? '确认收款并取药' : '确认取药'
  try {
    await ElMessageBox.confirm(
      `本次应收总金额：￥${Number(detail.value.totalAmount ?? 0).toFixed(2)}`,
      '取药确认',
      {
        type: 'warning',
        confirmButtonText,
        cancelButtonText: '取消',
      }
    )
    actionLoading.value = true
    if (needPayment) {
      await api.confirmPayment(detail.value.visit.id)
    }
    detail.value = await api.confirmPickup(detail.value.visit.id)
    ElMessage.success(
      injectionPrescriptions.value.length > 0
        ? '取药完成，库存已同步扣减，注射任务已分配给当前药师'
        : '取药完成，库存已同步扣减'
    )
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '操作失败')
    }
  } finally {
    actionLoading.value = false
    await loadData()
  }
}

onMounted(loadData)
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <div class="card-header">
        <span>取药窗口</span>
        <el-button type="primary" :disabled="actionLoading" @click="loadData">刷新</el-button>
      </div>
    </template>

    <template v-if="detail">
      <div class="summary-grid">
        <div class="summary-item">
          <div class="summary-label">患者</div>
          <div class="summary-value">{{ detail.patient.name }} / {{ detail.patient.patientNo }}</div>
        </div>
        <div class="summary-item">
          <div class="summary-label">就诊编号</div>
          <div class="summary-value">{{ detail.visit.visitNo }}</div>
        </div>
        <div class="summary-item">
          <div class="summary-label">医生</div>
          <div class="summary-value">{{ detail.visit.doctorName }}</div>
        </div>
        <div class="summary-item">
          <div class="summary-label">总金额</div>
          <div class="summary-value price">￥{{ Number(detail.totalAmount ?? 0).toFixed(2) }}</div>
        </div>
      </div>

      <div class="status-row">
        <el-tag :type="detail.paymentStatus === 'PAID' ? 'success' : 'warning'">
          {{ detail.paymentStatus === 'PAID' ? '已收款' : '待收款' }}
        </el-tag>
        <el-tag :type="detail.pickupStatus === 'DISPENSED' ? 'success' : 'warning'">
          {{ detail.pickupStatus === 'DISPENSED' ? '已取药' : '待取药' }}
        </el-tag>
        <el-tag v-if="detail.hasInjection" :type="detail.injectionStatus === 'COMPLETED' ? 'success' : 'warning'">
          {{ detail.injectionStatus === 'COMPLETED' ? '注射已完成' : '待注射' }}
        </el-tag>
      </div>

      <el-table :data="detail.prescriptions" border>
        <el-table-column prop="drugName" label="药品" min-width="160" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            {{ row.drugType === 'INJECTION' ? '注射' : '口服' }}
          </template>
        </el-table-column>
        <el-table-column prop="dosage" label="剂量" width="100" />
        <el-table-column prop="frequency" label="频次" width="120" />
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="单价" width="100">
          <template #default="{ row }">
            ￥{{ Number(row.unitPrice ?? 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            {{ row.quantity }} × ￥{{ Number(row.unitPrice ?? 0).toFixed(2) }} = ￥{{ Number(row.lineAmount ?? 0).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>

      <div v-if="injectionPrescriptions.length > 0" class="tips">
        含注射类药物，完成取药后会继续出现在“注射药物”待办中。
      </div>

      <div class="actions">
        <el-button
          type="primary"
          :loading="actionLoading"
          :disabled="detail.pickupStatus === 'DISPENSED'"
          @click="handlePickup"
        >
          {{ detail.paymentStatus === 'PAID' ? '确认取药' : '确认收款并取药' }}
        </el-button>
      </div>
    </template>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}
.summary-item {
  padding: 12px;
  border-radius: 8px;
  background: var(--el-fill-color-light);
}
.summary-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
}
.summary-value {
  font-weight: 600;
}
.price {
  color: var(--el-color-danger);
}
.status-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.tips {
  margin-top: 12px;
  color: var(--el-text-color-secondary);
}
.actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

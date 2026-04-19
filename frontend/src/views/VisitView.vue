<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { api } from '../api'
import FormDialog from '../components/FormDialog.vue'
import { useAuthStore } from '../stores/auth'
import type { Drug, Patient, VisitDetail } from '../types'

const authStore = useAuthStore()
const route = useRoute()
const loading = ref(false)
const visible = ref(false)
const editingVisitId = ref<number | null>(null)
const visits = ref<VisitDetail[]>([])
const visitSearch = ref('')
const visitPage = ref(1)
const visitPageSize = ref(15)
const visitTotal = ref(0)

async function loadVisits() {
  loading.value = true
  try {
    const kw = visitSearch.value.trim()
    const res = await api.searchVisitsPage({ q: kw || undefined, page: visitPage.value, size: visitPageSize.value })
    visits.value = res.list
    visitTotal.value = res.total
  } finally {
    loading.value = false
  }
}

function handleVisitPageChange(page: number) {
  visitPage.value = page
  loadVisits()
}

function handleVisitSizeChange(size: number) {
  visitPageSize.value = size
  visitPage.value = 1
  loadVisits()
}

let visitSearchTimer: ReturnType<typeof setTimeout> | null = null
function onVisitSearch() {
  if (visitSearchTimer) clearTimeout(visitSearchTimer)
  visitSearchTimer = setTimeout(() => {
    visitPage.value = 1
    loadVisits()
  }, 300)
}
const patientSearchDisplay = ref('')
const drugSearchDisplay = ref('')
const selectedDrug = ref<Drug | null>(null)
const selectedInjectionDrug = ref<Drug | null>(null)
const dialogTitle = computed(() => (editingVisitId.value ? '编辑就诊记录' : '新增就诊记录'))

const form = reactive({
  patientId: undefined as number | undefined,
  doctorName: '张医生',
  department: '全科门诊',
  chiefComplaint: '',
  visitTime: '',
  notes: '',
  diagnosesText: '',
  drugId: undefined as number | undefined,
  dosage: '',
  frequency: '',
  days: 1,
  quantity: 1,
  injectionDrugId: undefined as number | undefined,
  injectionDosage: '',
  injectionFrequency: '',
  injectionDays: 1,
  injectionQuantity: 1,
})
const injectionDrugSearchDisplay = ref('')

function normalizeDrugSearchKeyword(query: string, currentDisplay: string) {
  const normalizedQuery = (query ?? '').trim()
  return normalizedQuery === currentDisplay.trim() ? '' : normalizedQuery
}

async function fetchPatientSuggestions(
  query: string,
  cb: (items: { value: string; patient: Patient }[]) => void
) {
  try {
    const patients = await api.searchPatients(query ?? '')
    cb(
      patients.map((p) => ({
        value: `${p.name} / ${p.patientNo}`,
        patient: p,
      }))
    )
  } catch {
    cb([])
  }
}

async function fetchDrugSuggestions(
  query: string,
  cb: (items: { value: string; drug: Drug }[]) => void
) {
  try {
    const keyword = normalizeDrugSearchKeyword(query, drugSearchDisplay.value)
    const res = await api.searchDrugs({ q: keyword, page: 1, size: 20 })
    cb(
      res.list
        .filter((d) => d.drugType !== 'INJECTION')
        .map((d) => ({
          value: `${d.drugName} / ${d.drugCode} / 可用${d.availableStock} / 预占${d.reservedStock}`,
          drug: d,
        }))
    )
  } catch {
    cb([])
  }
}

async function fetchInjectionDrugSuggestions(
  query: string,
  cb: (items: { value: string; drug: Drug }[]) => void
) {
  try {
    const keyword = normalizeDrugSearchKeyword(query, injectionDrugSearchDisplay.value)
    const res = await api.searchDrugs({
      q: keyword,
      page: 1,
      size: 20,
      drugType: 'INJECTION',
    })
    cb(
      res.list.map((d) => ({
        value: `${d.drugName} / ${d.drugCode} / 可用${d.availableStock} / 预占${d.reservedStock}`,
        drug: d,
      }))
    )
  } catch {
    cb([])
  }
}

async function submit() {
  try {
    if (!form.patientId) {
      ElMessage.warning('请选择患者')
      return
    }
    if (!form.doctorName.trim()) {
      ElMessage.warning('请输入医生')
      return
    }
    if (!form.department.trim()) {
      ElMessage.warning('请输入科室')
      return
    }
    if (!form.chiefComplaint.trim()) {
      ElMessage.warning('请输入主诉')
      return
    }
    if (!form.visitTime) {
      ElMessage.warning('请选择就诊时间')
      return
    }
    if (form.drugId && selectedDrug.value && form.quantity > selectedDrug.value.availableStock) {
      ElMessage.warning(`药品 ${selectedDrug.value.drugName} 可用库存不足，当前可用 ${selectedDrug.value.availableStock}，预占 ${selectedDrug.value.reservedStock}`)
      return
    }
    if (form.injectionDrugId && selectedInjectionDrug.value && form.injectionQuantity > selectedInjectionDrug.value.availableStock) {
      ElMessage.warning(`药品 ${selectedInjectionDrug.value.drugName} 可用库存不足，当前可用 ${selectedInjectionDrug.value.availableStock}，预占 ${selectedInjectionDrug.value.reservedStock}`)
      return
    }

    const prescriptions: { drugId: number; dosage: string; frequency: string; days: number; quantity: number }[] = []
    if (form.drugId) {
      prescriptions.push({
        drugId: form.drugId,
        dosage: form.dosage,
        frequency: form.frequency,
        days: form.days,
        quantity: form.quantity,
      })
    }
    if (form.injectionDrugId) {
      prescriptions.push({
        drugId: form.injectionDrugId,
        dosage: form.injectionDosage,
        frequency: form.injectionFrequency,
        days: form.injectionDays,
        quantity: form.injectionQuantity,
      })
    }
    const payload = {
      patientId: form.patientId,
      doctorName: form.doctorName,
      department: form.department,
      chiefComplaint: form.chiefComplaint,
      visitTime: form.visitTime,
      notes: form.notes,
      diagnoses: form.diagnosesText
        .split(/[，,]/)
        .map((item) => item.trim())
        .filter(Boolean),
      prescriptions,
    }

    if (editingVisitId.value) {
      await api.updateVisit(editingVisitId.value, payload)
      ElMessage.success('就诊记录已更新')
    } else {
      await api.createVisit(payload)
      ElMessage.success('就诊记录已保存')
    }

    visible.value = false
    resetForm()
    await loadVisits()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交失败')
  }
}

function handlePatientSelect(item: { value: string; patient: Patient }) {
  form.patientId = item.patient.id
  patientSearchDisplay.value = item.value
}

function handleDrugSelect(item: { value: string; drug: Drug }) {
  form.drugId = item.drug.id
  drugSearchDisplay.value = item.value
  selectedDrug.value = item.drug
}

function handleInjectionDrugSelect(item: { value: string; drug: Drug }) {
  form.injectionDrugId = item.drug.id
  injectionDrugSearchDisplay.value = item.value
  selectedInjectionDrug.value = item.drug
}

function clearDrugSelection() {
  form.drugId = undefined
  drugSearchDisplay.value = ''
  selectedDrug.value = null
}

function clearInjectionDrugSelection() {
  form.injectionDrugId = undefined
  injectionDrugSearchDisplay.value = ''
  selectedInjectionDrug.value = null
}

function openCreate() {
  resetForm()
  visible.value = true
}

function resetForm() {
  editingVisitId.value = null
  form.patientId = undefined
  patientSearchDisplay.value = ''
  form.doctorName = '张医生'
  form.department = '全科门诊'
  form.chiefComplaint = ''
  form.visitTime = ''
  form.notes = ''
  form.diagnosesText = ''
  form.drugId = undefined
  drugSearchDisplay.value = ''
  selectedDrug.value = null
  form.dosage = ''
  form.frequency = ''
  form.days = 1
  form.quantity = 1
  form.injectionDrugId = undefined
  injectionDrugSearchDisplay.value = ''
  selectedInjectionDrug.value = null
  form.injectionDosage = ''
  form.injectionFrequency = ''
  form.injectionDays = 1
  form.injectionQuantity = 1
}

async function openEdit(row: VisitDetail) {
  editingVisitId.value = row.visit.id
  form.patientId = row.patient?.id
  patientSearchDisplay.value = row.patient ? `${row.patient.name} / ${row.patient.patientNo}` : ''
  form.doctorName = row.visit.doctorName
  form.department = row.visit.department
  form.chiefComplaint = row.visit.chiefComplaint
  form.visitTime = row.visit.visitTime ? String(row.visit.visitTime).slice(0, 19) : ''
  form.notes = row.visit.notes ?? ''
  form.diagnosesText = (row.diagnoses ?? []).map((item) => item.diagnosisName).join('，')

  visible.value = true

  try {
    const drugs = await api.getDrugs()
    const drugMap = Object.fromEntries(drugs.map((d) => [d.id, d]))
    const oralPrescription = (row.prescriptions ?? []).find((p) => drugMap[p.drugId]?.drugType !== 'INJECTION')
    const injectionPrescription = (row.prescriptions ?? []).find((p) => drugMap[p.drugId]?.drugType === 'INJECTION')

    form.drugId = oralPrescription?.drugId
    drugSearchDisplay.value = oralPrescription ? `${oralPrescription.drugName} x${oralPrescription.quantity}` : ''
    selectedDrug.value = oralPrescription ? (drugMap[oralPrescription.drugId] ?? null) : null
    form.dosage = oralPrescription?.dosage ?? ''
    form.frequency = oralPrescription?.frequency ?? ''
    form.days = oralPrescription?.days ?? 1
    form.quantity = oralPrescription?.quantity ?? 1

    form.injectionDrugId = injectionPrescription?.drugId
    injectionDrugSearchDisplay.value = injectionPrescription ? `${injectionPrescription.drugName} x${injectionPrescription.quantity}` : ''
    selectedInjectionDrug.value = injectionPrescription ? (drugMap[injectionPrescription.drugId] ?? null) : null
    form.injectionDosage = injectionPrescription?.dosage ?? ''
    form.injectionFrequency = injectionPrescription?.frequency ?? ''
    form.injectionDays = injectionPrescription?.days ?? 1
    form.injectionQuantity = injectionPrescription?.quantity ?? 1
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载处方信息失败')
    const [oral, injection] = row.prescriptions ?? []
    form.drugId = oral?.drugId
    drugSearchDisplay.value = oral ? `${oral.drugName} x${oral.quantity}` : ''
    selectedDrug.value = null
    form.dosage = oral?.dosage ?? ''
    form.frequency = oral?.frequency ?? ''
    form.days = oral?.days ?? 1
    form.quantity = oral?.quantity ?? 1
    form.injectionDrugId = injection?.drugId
    injectionDrugSearchDisplay.value = injection ? `${injection.drugName} x${injection.quantity}` : ''
    selectedInjectionDrug.value = null
    form.injectionDosage = injection?.dosage ?? ''
    form.injectionFrequency = injection?.frequency ?? ''
    form.injectionDays = injection?.days ?? 1
    form.injectionQuantity = injection?.quantity ?? 1
  }
}

onMounted(async () => {
  await loadVisits()
  const editId = route.query.edit
  if (editId) {
    const id = Number(editId)
    const found = visits.value.find((v) => v.visit.id === id)
    if (found) openEdit(found)
  }
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span>就诊记录</span>
          <el-input v-model="visitSearch" :prefix-icon="Search" placeholder="搜索（患者/就诊号/科室/医生/主诉）..." size="small" clearable style="width: 320px; margin-left: 16px;" @input="onVisitSearch" @clear="onVisitSearch" />
        </div>
        <el-button type="primary" @click="openCreate">新增就诊</el-button>
      </div>
    </template>

    <el-table :data="visits" v-loading="loading" border stripe>
      <el-table-column prop="visit.visitNo" label="就诊编号" min-width="140" />
      <el-table-column prop="patient.name" label="患者" width="100" />
      <el-table-column prop="visit.department" label="科室" width="120" />
      <el-table-column prop="visit.doctorName" label="医生" width="100" />
      <el-table-column prop="visit.chiefComplaint" label="主诉" min-width="180" />
      <el-table-column label="诊断" min-width="180">
        <template #default="{ row }">
          {{ row.diagnoses.map((item: { diagnosisName: string }) => item.diagnosisName).join('、') }}
        </template>
      </el-table-column>
      <el-table-column label="处方" min-width="200">
        <template #default="{ row }">
          {{ row.prescriptions.map((item: { drugName: string; quantity: number }) => `${item.drugName} x${item.quantity}`).join('；') }}
        </template>
      </el-table-column>
      <el-table-column v-if="authStore.hasRole(['ADMIN', 'DOCTOR'])" label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-bar">
      <span class="total-text">共 {{ visitTotal }} 条记录</span>
      <el-pagination
        v-model:current-page="visitPage"
        v-model:page-size="visitPageSize"
        :total="visitTotal"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="handleVisitPageChange"
        @size-change="handleVisitSizeChange"
      />
    </div>
  </el-card>

  <FormDialog v-model="visible" :title="dialogTitle">
    <el-form label-position="top">
      <div class="form-grid">
        <el-form-item label="患者">
          <el-autocomplete
            v-model="patientSearchDisplay"
            :fetch-suggestions="fetchPatientSuggestions"
            placeholder="搜索患者姓名或编号"
            value-key="value"
            class="full-width"
            @select="handlePatientSelect"
          />
        </el-form-item>
        <el-form-item label="医生"><el-input v-model="form.doctorName" /></el-form-item>
        <el-form-item label="科室"><el-input v-model="form.department" /></el-form-item>
        <el-form-item label="就诊时间">
          <el-date-picker
            v-model="form.visitTime"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            class="full-width"
          />
        </el-form-item>
      </div>

      <el-form-item label="主诉"><el-input v-model="form.chiefComplaint" /></el-form-item>
      <el-form-item label="诊断结论（逗号分隔）"><el-input v-model="form.diagnosesText" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="form.notes" type="textarea" :rows="3" /></el-form-item>

      <el-divider>处方信息（可选）</el-divider>
      <div class="form-grid">
        <el-form-item label="药品">
          <el-autocomplete
            v-model="drugSearchDisplay"
            :fetch-suggestions="fetchDrugSuggestions"
            placeholder="搜索药品名称或编号"
            value-key="value"
            class="full-width"
            :trigger-on-focus="true"
            clearable
            @select="handleDrugSelect"
            @clear="clearDrugSelection"
          />
        </el-form-item>
        <el-form-item label="剂量说明"><el-input v-model="form.dosage" placeholder="如：5mg / 每次 1 片" /></el-form-item>
        <el-form-item label="频次"><el-input v-model="form.frequency" /></el-form-item>
        <el-form-item label="天数"><el-input-number v-model="form.days" :min="1" class="full-width" /></el-form-item>
        <el-form-item :label="`开药数量（可用 ${selectedDrug?.availableStock ?? '-'} / 预占 ${selectedDrug?.reservedStock ?? '-'}）`">
          <el-input-number v-model="form.quantity" :min="1" class="full-width" />
        </el-form-item>
      </div>

      <el-divider>注射类药物（可选）</el-divider>
      <div class="form-grid">
        <el-form-item label="注射药品">
          <el-autocomplete
            v-model="injectionDrugSearchDisplay"
            :fetch-suggestions="fetchInjectionDrugSuggestions"
            placeholder="点击显示全部，输入可模糊搜索"
            value-key="value"
            class="full-width"
            :trigger-on-focus="true"
            clearable
            @select="handleInjectionDrugSelect"
            @clear="clearInjectionDrugSelection"
          />
        </el-form-item>
        <el-form-item label="剂量说明"><el-input v-model="form.injectionDosage" placeholder="如：1 支 / 每次 1 支" /></el-form-item>
        <el-form-item label="频次"><el-input v-model="form.injectionFrequency" /></el-form-item>
        <el-form-item label="天数"><el-input-number v-model="form.injectionDays" :min="1" class="full-width" /></el-form-item>
        <el-form-item :label="`开药数量（可用 ${selectedInjectionDrug?.availableStock ?? '-'} / 预占 ${selectedInjectionDrug?.reservedStock ?? '-'}）`">
          <el-input-number v-model="form.injectionQuantity" :min="1" class="full-width" />
        </el-form-item>
      </div>
    </el-form>

    <template #footer>
      <el-button @click="visible = false; resetForm()">取消</el-button>
      <el-button type="primary" @click="submit">{{ editingVisitId ? '更新' : '保存' }}</el-button>
    </template>
  </FormDialog>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
}
.pagination-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
}
.total-text {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
</style>

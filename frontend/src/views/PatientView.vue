<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Edit, Search, User } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api'
import FormDialog from '../components/FormDialog.vue'
import { useAuthStore } from '../stores/auth'
import { avatarUrl } from '../utils/avatar'
import type { Patient } from '../types'

interface Doctor {
  id: number
  realName: string
  username: string
  pendingCount?: number
}

const authStore = useAuthStore()
const canEdit = () => authStore.hasRole(['ADMIN', 'DOCTOR', 'RECEPTION'])
const canRegister = () => authStore.hasRole(['RECEPTION'])
const canDelete = () => authStore.hasRole(['ADMIN'])

const patients = ref<Patient[]>([])
const patientSearch = ref('')
const uploadingPatientId = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = ref(15)
const total = ref(0)

async function loadPatients() {
  loading.value = true
  try {
    const kw = patientSearch.value.trim()
    const res = await api.searchPatientsPage({ q: kw || undefined, page: currentPage.value, size: pageSize.value })
    patients.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadPatients()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadPatients()
}

let searchTimer: ReturnType<typeof setTimeout> | null = null
function onSearch() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadPatients()
  }, 300)
}

const loading = ref(false)
const visible = ref(false)
const editVisible = ref(false)
const registerVisible = ref(false)
const editingId = ref<number | null>(null)
const registerPatient = ref<Patient | null>(null)
const departments = ref<string[]>([])
const doctors = ref<Doctor[]>([])
const selectedDepartment = ref('')
const selectedDoctor = ref('')

async function handlePatientAvatarChange(patientId: number, e: Event) {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  uploadingPatientId.value = patientId
  try {
    const path = await api.uploadPatientAvatar(patientId, file)
    const p = patients.value.find((x) => x.id === patientId)
    if (p) p.avatar = path
    ElMessage.success('头像上传成功')
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '上传失败')
  } finally {
    uploadingPatientId.value = null
    target.value = ''
  }
}
const form = reactive({
  name: '',
  gender: '男',
  age: 0,
  phone: '',
  idCard: '',
  address: '',
  allergyHistory: '',
  medicalHistory: '',
})

function openRegister(row: Patient) {
  registerPatient.value = row
  selectedDepartment.value = ''
  selectedDoctor.value = ''
  registerVisible.value = true
  loadDepartments()
}

async function loadDepartments() {
  try {
    departments.value = await api.getDepartments()
    if (departments.value.length > 0 && !selectedDepartment.value) {
      selectedDepartment.value = departments.value[0]
      await loadDoctorsByDept(departments.value[0])
    }
  } catch {
    ElMessage.error('获取科室列表失败')
  }
}

async function loadDoctorsByDept(dept: string) {
  try {
    doctors.value = await api.getDoctors(dept)
    if (doctors.value.length > 0) {
      selectedDoctor.value = doctors.value[0].realName
    } else {
      selectedDoctor.value = ''
    }
  } catch {
    ElMessage.error('获取医生列表失败')
    doctors.value = []
    selectedDoctor.value = ''
  }
}

async function loadDoctors() {
  if (!selectedDepartment.value) {
    doctors.value = []
    selectedDoctor.value = ''
    return
  }
  await loadDoctorsByDept(selectedDepartment.value)
}

async function submitRegister() {
  const p = registerPatient.value
  if (!p || !selectedDepartment.value.trim()) {
    ElMessage.warning('请选择科室')
    return
  }
  if (!selectedDoctor.value.trim()) {
    ElMessage.warning('请选择医生')
    return
  }
  try {
    await api.register({ patientId: p.id, doctorName: selectedDoctor.value.trim() })
    ElMessage.success(`挂号成功，排队号已生成`)
    registerVisible.value = false
    registerPatient.value = null
    selectedDepartment.value = ''
    selectedDoctor.value = ''
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '挂号失败')
  }
}

async function submit() {
  try {
    await api.createPatient(form)
    ElMessage.success('患者建档成功')
    visible.value = false
    resetForm()
    await loadPatients()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交失败')
  }
}

function openEdit(row: Patient) {
  editingId.value = row.id
  form.name = row.name
  form.gender = row.gender
  form.age = row.age
  form.phone = row.phone ?? ''
  form.idCard = row.idCard ?? ''
  form.address = row.address ?? ''
  form.allergyHistory = row.allergyHistory ?? ''
  form.medicalHistory = row.medicalHistory ?? ''
  editVisible.value = true
}

async function submitEdit() {
  const id = editingId.value
  if (id == null) return
  try {
    await api.updatePatient(id, form)
    ElMessage.success('患者信息已更新')
    editVisible.value = false
    editingId.value = null
    resetForm()
    await loadPatients()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

async function handleDelete(row: Patient) {
  try {
    await ElMessageBox.confirm(
      `确定要删除患者「${row.name}」（编号：${row.patientNo}）吗？删除后相关登录账号也将被移除。`,
      '删除确认',
      { type: 'warning' },
    )
    await api.deletePatient(row.id)
    ElMessage.success('删除成功')
    await loadPatients()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error instanceof Error ? error.message : '删除失败')
  }
}

function resetForm() {
  form.name = ''
  form.gender = '男'
  form.age = 0
  form.phone = ''
  form.idCard = ''
  form.address = ''
  form.allergyHistory = ''
  form.medicalHistory = ''
}

onMounted(loadPatients)
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span>{{ canRegister() ? '挂号服务' : '患者管理' }}</span>
          <el-input v-model="patientSearch" :prefix-icon="Search" placeholder="搜索（姓名/病案号/手机/身份证）..." size="small" clearable style="width: 300px; margin-left: 16px;" @input="onSearch" @clear="onSearch" />
        </div>
        <el-button type="primary" @click="visible = true">新增患者</el-button>
      </div>
    </template>

    <el-table :data="patients" v-loading="loading" border stripe size="small">
      <el-table-column label="头像" width="120" align="center">
        <template #default="{ row }">
          <div class="avatar-cell">
            <el-avatar :size="40" :src="avatarUrl(row.avatar) || undefined">
              <el-icon><User /></el-icon>
            </el-avatar>
            <label class="upload-label">
              <span class="upload-btn">{{ uploadingPatientId === row.id ? '上传中' : '上传' }}</span>
              <input
                :id="'patient-upload-' + row.id"
                type="file"
                accept="image/jpeg,image/png,image/gif,image/webp"
                class="hidden-input"
                @change="(e) => handlePatientAvatarChange(row.id, e)"
              />
            </label>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="patientNo" label="患者编号" min-width="140" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="idCard" label="身份证号" min-width="180" />
      <el-table-column prop="gender" label="性别" width="70" align="center" />
      <el-table-column prop="age" label="年龄" width="70" align="center" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="allergyHistory" label="过敏史" min-width="160" />
      <el-table-column prop="medicalHistory" label="既往病史" width="140" show-overflow-tooltip />
      <el-table-column v-if="canRegister() || canEdit() || canDelete()" label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-if="canRegister()" type="primary" link @click="openRegister(row)">挂号</el-button>
          <el-button v-if="canEdit()" type="primary" link :icon="Edit" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="canDelete()" type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-bar">
      <span class="total-text">共 {{ total }} 位患者</span>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </el-card>

  <FormDialog v-model="visible" title="新增患者">
    <el-form label-position="top">
      <div class="form-grid">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" class="full-width" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="身份证"><el-input v-model="form.idCard" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
      </div>
      <el-form-item label="过敏史"><el-input v-model="form.allergyHistory" /></el-form-item>
      <el-form-item label="既往病史"><el-input v-model="form.medicalHistory" type="textarea" :rows="3" /></el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>
  </FormDialog>

  <FormDialog v-model="editVisible" title="编辑患者">
    <el-form label-position="top">
      <div class="form-grid">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" class="full-width" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="身份证"><el-input v-model="form.idCard" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
      </div>
      <el-form-item label="过敏史"><el-input v-model="form.allergyHistory" /></el-form-item>
      <el-form-item label="既往病史"><el-input v-model="form.medicalHistory" type="textarea" :rows="3" /></el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="editVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">保存</el-button>
    </template>
  </FormDialog>

  <FormDialog v-model="registerVisible" title="挂号" width="420px">
    <el-form label-position="top" v-if="registerPatient">
      <el-form-item label="患者">
        <el-input :model-value="registerPatient.name + ' / ' + registerPatient.patientNo" disabled />
      </el-form-item>
      <el-form-item label="选择科室" required>
        <el-select v-model="selectedDepartment" placeholder="请先选择科室" class="full-width" @change="loadDoctors">
          <el-option v-for="d in departments" :key="d" :label="d" :value="d" />
        </el-select>
      </el-form-item>
      <el-form-item label="选择医生" required>
        <el-select v-model="selectedDoctor" placeholder="请选择医生" class="full-width" :disabled="!selectedDepartment">
          <el-option
            v-for="d in doctors"
            :key="d.id"
            :label="d.realName + (d.pendingCount ? `（${d.pendingCount}人排队）` : '（暂无排队）')"
            :value="d.realName"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="registerVisible = false">取消</el-button>
      <el-button type="primary" @click="submitRegister">确认挂号</el-button>
    </template>
  </FormDialog>
</template>

<style scoped>
.avatar-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.avatar-cell :deep(.el-avatar) {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}
.avatar-cell :deep(.el-avatar img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-label {
  cursor: pointer;
}
.upload-btn {
  color: var(--el-color-primary);
  font-size: 12px;
}
.hidden-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
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

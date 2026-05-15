<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { User, Search, ArrowDown, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api'
import FormDialog from '../components/FormDialog.vue'
import { avatarUrl } from '../utils/avatar'
import type { AdminUser } from '../types'

const loading = ref(false)
const uploadingUserId = ref<number | null>(null)
const employeeExpanded = ref(true)
const employeeSearch = ref('')

async function handleUserAvatarChange(userId: number, e: Event) {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  uploadingUserId.value = userId
  try {
    const path = await api.uploadUserAvatar(userId, file)
    const user = users.value.find((u) => u.id === userId)
    if (user) user.avatar = path
    ElMessage.success('头像上传成功')
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '上传失败')
  } finally {
    uploadingUserId.value = null
    target.value = ''
  }
}

const users = ref<AdminUser[]>([])
const visible = ref(false)
const editVisible = ref(false)
const editingId = ref<number | null>(null)
const roles = ref<{ id: number; roleCode: string; roleName: string }[]>([])
const departments = ref<string[]>([])
const pharmacyList = ref<string[]>([])
const receptionList = ref<string[]>([])
const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  idCard: '',
  roleCodes: [] as string[],
  department: '',
  pharmacy: '',
  receptionDesk: '',
})

const employeeUsers = computed(() => {
  const list = users.value.filter(u => !u.roles.includes('PATIENT'))
  if (!employeeSearch.value.trim()) return list
  const kw = employeeSearch.value.trim().toLowerCase()
  return list.filter(u =>
    u.username.toLowerCase().includes(kw) ||
    u.realName.toLowerCase().includes(kw) ||
    (u.department || '').toLowerCase().includes(kw) ||
    (u.pharmacy || '').toLowerCase().includes(kw) ||
    u.roles.some(r => r.toLowerCase().includes(kw))
  )
})

async function loadUsers() {
  loading.value = true
  try {
    users.value = await api.getAdminUsers()
  } finally {
    loading.value = false
  }
}

async function loadRoles() { roles.value = await api.getAdminRoles() }
async function loadDepartments() { departments.value = await api.getDepartments() }
async function loadPharmacyList() { pharmacyList.value = await api.getPharmacyList() }
async function loadReceptionList() { receptionList.value = await api.getReceptionList() }

const isDoctor = () => form.roleCodes.includes('DOCTOR')
const isPharmacist = () => form.roleCodes.includes('PHARMACIST')
const isReception = () => form.roleCodes.includes('RECEPTION')

async function submit() {
  if (!form.username.trim()) { ElMessage.warning('请输入用户名'); return }
  if (!form.password.trim()) { ElMessage.warning('请输入密码'); return }
  if (!form.realName.trim()) { ElMessage.warning('请输入姓名'); return }
  if (!form.phone.trim()) { ElMessage.warning('请输入手机号'); return }
  if (!form.idCard.trim()) { ElMessage.warning('请输入身份证号'); return }
  if (form.roleCodes.length === 0) { ElMessage.warning('请选择至少一个角色'); return }
  if (form.roleCodes.includes('DOCTOR') && !form.department?.trim()) { ElMessage.warning('医生必须选择所属科室'); return }
  if (form.roleCodes.includes('PHARMACIST') && !form.pharmacy?.trim()) { ElMessage.warning('药师必须选择所属药房'); return }
  if (form.roleCodes.includes('RECEPTION') && !form.receptionDesk?.trim()) { ElMessage.warning('前台必须选择所属前台'); return }
  try {
    await api.createUser({
      username: form.username.trim(),
      password: form.password,
      realName: form.realName.trim(),
      phone: form.phone.trim(),
      idCard: form.idCard.trim(),
      roles: form.roleCodes,
      department: form.roleCodes.includes('DOCTOR') ? form.department.trim() : undefined,
      pharmacy: form.roleCodes.includes('PHARMACIST') ? form.pharmacy.trim() : undefined,
      receptionDesk: form.roleCodes.includes('RECEPTION') ? form.receptionDesk.trim() : undefined,
    })
    ElMessage.success('用户创建成功')
    visible.value = false
    resetForm()
    await loadUsers()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '创建失败')
  }
}

function resetForm() {
  Object.assign(form, { username: '', password: '', realName: '', phone: '', idCard: '', roleCodes: [], department: '', pharmacy: '', receptionDesk: '' })
}

function openEdit(row: AdminUser) {
  editingId.value = row.id
  form.realName = row.realName
  form.phone = (row as any).phone || ''
  form.idCard = row.idCard || ''
  form.roleCodes = [...row.roles]
  form.department = row.department || ''
  form.pharmacy = row.pharmacy || ''
  form.receptionDesk = row.receptionDesk || ''
  editVisible.value = true
}

async function submitEdit() {
  const id = editingId.value
  if (id == null) return
  if (!form.realName.trim()) { ElMessage.warning('请输入姓名'); return }
  if (!form.phone.trim()) { ElMessage.warning('请输入手机号'); return }
  if (!form.idCard.trim()) { ElMessage.warning('请输入身份证号'); return }
  if (form.roleCodes.length === 0) { ElMessage.warning('请选择至少一个角色'); return }
  if (form.roleCodes.includes('DOCTOR') && !form.department?.trim()) { ElMessage.warning('医生必须选择所属科室'); return }
  if (form.roleCodes.includes('PHARMACIST') && !form.pharmacy?.trim()) { ElMessage.warning('药师必须选择所属药房'); return }
  if (form.roleCodes.includes('RECEPTION') && !form.receptionDesk?.trim()) { ElMessage.warning('前台必须选择所属前台'); return }
  try {
    await api.updateUser(id, {
      realName: form.realName.trim(),
      phone: form.phone.trim(),
      idCard: form.idCard.trim(),
      roles: form.roleCodes,
      department: form.roleCodes.includes('DOCTOR') ? form.department.trim() : undefined,
      pharmacy: form.roleCodes.includes('PHARMACIST') ? form.pharmacy.trim() : undefined,
      receptionDesk: form.roleCodes.includes('RECEPTION') ? form.receptionDesk.trim() : undefined,
    })
    ElMessage.success('用户信息已更新')
    editVisible.value = false
    editingId.value = null
    resetForm()
    await loadUsers()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

async function handleDelete(row: AdminUser) {
  if (row.username === 'admin') { ElMessage.warning('admin 用户不能被删除'); return }
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.realName}」吗？`, '删除确认', { type: 'warning' })
    await api.deleteUser(row.id)
    ElMessage.success('删除成功')
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error instanceof Error ? error.message : '删除失败')
  }
}

function roleTagType(role: string) {
  return ({ ADMIN: 'danger', DOCTOR: 'success', PHARMACIST: 'warning', RECEPTION: '', PATIENT: 'info' } as Record<string, string>)[role] || 'info'
}

onMounted(async () => {
  await Promise.all([loadUsers(), loadRoles(), loadDepartments(), loadPharmacyList(), loadReceptionList()])
})
</script>

<template>
  <div class="user-admin">
    <!-- 员工管理 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left" @click="employeeExpanded = !employeeExpanded">
            <el-icon class="expand-icon"><component :is="employeeExpanded ? ArrowDown : ArrowRight" /></el-icon>
            <span>员工管理 ({{ employeeUsers.length }})</span>
          </div>
          <div class="header-right">
            <el-input v-model="employeeSearch" :prefix-icon="Search" placeholder="搜索员工..." size="small" clearable style="width: 200px; margin-right: 12px;" />
            <el-button type="primary" size="small" @click="visible = true">新增员工</el-button>
          </div>
        </div>
      </template>

      <transition name="slide">
        <el-table v-show="employeeExpanded" :data="employeeUsers" v-loading="loading" border>
          <el-table-column label="头像" width="100">
            <template #default="{ row }">
              <div class="avatar-cell">
                <el-avatar :size="40" :src="avatarUrl(row.avatar) || undefined">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <label class="upload-label">
                  <span class="upload-btn">{{ uploadingUserId === row.id ? '上传中' : '上传' }}</span>
                  <input type="file" accept="image/*" class="hidden-input" @change="(e) => handleUserAvatarChange(row.id, e)" />
                </label>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="realName" label="姓名" min-width="100" />
          <el-table-column label="部门" width="120">
            <template #default="{ row }">{{ row.department || row.pharmacy || row.receptionDesk || '-' }}</template>
          </el-table-column>
          <el-table-column label="角色" min-width="180">
            <template #default="{ row }">
              <el-space wrap>
                <el-tag v-for="role in row.roles" :key="role" :type="roleTagType(role) as any" size="small">{{ role }}</el-tag>
              </el-space>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
              <el-button type="danger" link :disabled="row.username === 'admin'" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </transition>
    </el-card>

  </div>

  <!-- 新增员工弹窗 -->
  <FormDialog v-model="visible" title="新增员工">
    <el-form label-position="top">
      <el-form-item label="用户名"><el-input v-model="form.username" placeholder="登录名，唯一" /></el-form-item>
      <el-form-item label="密码"><el-input v-model="form.password" type="password" placeholder="登录密码" show-password /></el-form-item>
      <el-form-item label="姓名"><el-input v-model="form.realName" placeholder="真实姓名" required /></el-form-item>
      <el-form-item label="手机号" required><el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" /></el-form-item>
      <el-form-item label="身份证号" required><el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" /></el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.roleCodes" multiple placeholder="请选择至少一个角色" class="full-width">
          <el-option v-for="r in roles.filter(r => r.roleCode !== 'PATIENT')" :key="r.id" :label="r.roleName" :value="r.roleCode" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isDoctor()" label="所属科室" required>
        <el-select v-model="form.department" placeholder="请选择科室" class="full-width">
          <el-option v-for="d in departments" :key="d" :label="d" :value="d" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isPharmacist()" label="所属药房" required>
        <el-select v-model="form.pharmacy" placeholder="请选择药房" class="full-width">
          <el-option v-for="p in pharmacyList" :key="p" :label="p" :value="p" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isReception()" label="所属前台" required>
        <el-select v-model="form.receptionDesk" placeholder="请选择前台" class="full-width">
          <el-option v-for="r in receptionList" :key="r" :label="r" :value="r" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>
  </FormDialog>

  <!-- 编辑员工弹窗 -->
  <FormDialog v-model="editVisible" title="编辑员工">
    <el-form label-position="top">
      <el-form-item label="姓名"><el-input v-model="form.realName" placeholder="真实姓名" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" /></el-form-item>
      <el-form-item label="身份证号"><el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" /></el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.roleCodes" multiple placeholder="请选择至少一个角色" class="full-width">
          <el-option v-for="r in roles.filter(r => r.roleCode !== 'PATIENT')" :key="r.id" :label="r.roleName" :value="r.roleCode" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isDoctor()" label="所属科室" required>
        <el-select v-model="form.department" placeholder="请选择科室" class="full-width">
          <el-option v-for="d in departments" :key="d" :label="d" :value="d" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isPharmacist()" label="所属药房" required>
        <el-select v-model="form.pharmacy" placeholder="请选择药房" class="full-width">
          <el-option v-for="p in pharmacyList" :key="p" :label="p" :value="p" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isReception()" label="所属前台" required>
        <el-select v-model="form.receptionDesk" placeholder="请选择前台" class="full-width">
          <el-option v-for="r in receptionList" :key="r" :label="r" :value="r" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">保存</el-button>
    </template>
  </FormDialog>
</template>

<style scoped>
.user-admin { display: flex; flex-direction: column; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-left { display: flex; align-items: center; gap: 8px; cursor: pointer; font-weight: 600; user-select: none; }
.header-left:hover { color: var(--el-color-primary); }
.expand-icon { transition: transform 0.2s; }
.header-right { display: flex; align-items: center; }
.avatar-cell { display: flex; align-items: center; gap: 8px; }
.upload-label { cursor: pointer; }
.upload-btn { color: var(--el-color-primary); font-size: 12px; }
.upload-btn:hover { color: var(--el-color-primary-light-3); }
.hidden-input { position: absolute; width: 0; height: 0; opacity: 0; }
.slide-enter-active, .slide-leave-active { transition: all 0.3s ease; overflow: hidden; }
.slide-enter-from, .slide-leave-to { opacity: 0; max-height: 0; }
</style>

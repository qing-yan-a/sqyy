<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'
import { useAuthStore } from '../../stores/auth'
import { ElMessage } from 'element-plus'
import { Edit, Check } from '@element-plus/icons-vue'

interface ProfileData {
  name?: string
  patientNo?: string
  gender?: string
  age?: number
  phone?: string
  address?: string
  allergyHistory?: string
  medicalHistory?: string
  avatar?: string
}

const authStore = useAuthStore()
const profile = ref<ProfileData>({})
const loading = ref(true)
const editing = ref(false)
const uploading = ref(false)
const fileInputRef = ref<HTMLInputElement>()
const editForm = ref({
  phone: '',
  address: '',
  allergyHistory: '',
  medicalHistory: '',
})

onMounted(async () => {
  await loadProfile()
})

async function loadProfile() {
  loading.value = true
  try {
    const data = await api.getPatientProfile() as ProfileData
    profile.value = data
    editForm.value = {
      phone: data.phone || '',
      address: data.address || '',
      allergyHistory: data.allergyHistory || '',
      medicalHistory: data.medicalHistory || '',
    }
  } catch {
    ElMessage.error('加载个人信息失败')
  } finally {
    loading.value = false
  }
}

function startEdit() {
  editing.value = true
}

async function saveProfile() {
  try {
    await api.updatePatientProfile(editForm.value)
    ElMessage.success('保存成功')
    editing.value = false
    await loadProfile()
  } catch {
    ElMessage.error('保存失败')
  }
}

function cancelEdit() {
  editing.value = false
  editForm.value = {
    phone: profile.value.phone || '',
    address: profile.value.address || '',
    allergyHistory: profile.value.allergyHistory || '',
    medicalHistory: profile.value.medicalHistory || '',
  }
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

async function handleAvatarChange(e: Event) {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  uploading.value = true
  target.value = ''
  try {
    const path = await api.uploadMyPatientAvatar(file)
    profile.value = { ...profile.value, avatar: path }
    if (authStore.user) {
      authStore.user = { ...authStore.user, avatar: path }
      localStorage.setItem('sqyy-user', JSON.stringify(authStore.user))
    }
    ElMessage.success('头像上传成功')
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '上传失败')
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <div class="patient-profile">
    <el-card class="profile-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>👤 个人信息</span>
          <el-button v-if="!editing" type="primary" link :icon="Edit" @click="startEdit">编辑</el-button>
          <div v-else>
            <el-button type="primary" link :icon="Check" @click="saveProfile">保存</el-button>
            <el-button type="info" link @click="cancelEdit">取消</el-button>
          </div>
        </div>
      </template>

      <div class="profile-top">
        <div class="avatar-area">
          <el-avatar :size="72" :src="profile.avatar || undefined">
            <span style="font-size: 28px;">😊</span>
          </el-avatar>
          <el-button size="small" :loading="uploading" @click="triggerFileInput">
            {{ uploading ? '上传中...' : '更换头像' }}
          </el-button>
          <input
            ref="fileInputRef"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleAvatarChange"
          />
        </div>

        <div class="profile-info">
          <div class="info-row">
            <span class="label">姓名</span>
            <span class="value">{{ profile.name }}</span>
          </div>
          <div class="info-row">
            <span class="label">病案号</span>
            <span class="value">{{ profile.patientNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">性别</span>
            <span class="value">{{ profile.gender }}</span>
          </div>
          <div class="info-row">
            <span class="label">年龄</span>
            <span class="value">{{ profile.age }} 岁</span>
          </div>
          <div class="info-row">
            <span class="label">手机号</span>
            <template v-if="!editing">
              <span class="value">{{ profile.phone || '未填写' }}</span>
            </template>
            <template v-else>
              <el-input v-model="editForm.phone" size="small" placeholder="手机号" />
            </template>
          </div>
          <div class="info-row">
            <span class="label">地址</span>
            <template v-if="!editing">
              <span class="value">{{ profile.address || '未填写' }}</span>
            </template>
            <template v-else>
              <el-input v-model="editForm.address" size="small" placeholder="地址" />
            </template>
          </div>
          <div class="info-row">
            <span class="label">过敏史</span>
            <template v-if="!editing">
              <span class="value">{{ profile.allergyHistory || '无' }}</span>
            </template>
            <template v-else>
              <el-input v-model="editForm.allergyHistory" type="textarea" :rows="2" placeholder="过敏史" />
            </template>
          </div>
          <div class="info-row">
            <span class="label">既往病史</span>
            <template v-if="!editing">
              <span class="value">{{ profile.medicalHistory || '无' }}</span>
            </template>
            <template v-else>
              <el-input v-model="editForm.medicalHistory" type="textarea" :rows="2" placeholder="既往病史" />
            </template>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.patient-profile {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.profile-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.profile-top {
  display: flex;
  gap: 32px;
}

.avatar-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.profile-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.info-row .label {
  width: 72px;
  flex-shrink: 0;
  color: #909399;
  font-size: 14px;
}

.info-row .value {
  flex: 1;
  color: #303133;
  font-size: 14px;
}

.info-row :deep(.el-input),
.info-row :deep(.el-textarea) {
  flex: 1;
}
</style>

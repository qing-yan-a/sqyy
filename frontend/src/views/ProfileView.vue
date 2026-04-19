<script setup lang="ts">
import { ref, reactive } from 'vue'
import { User, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { api } from '../api'
import { useAuthStore } from '../stores/auth'
import { avatarUrl as toAvatarUrl } from '../utils/avatar'

const authStore = useAuthStore()
const uploading = ref(false)
const editing = ref(false)
const saving = ref(false)
const fileInputRef = ref<HTMLInputElement>()

const form = reactive({
  phone: '',
  idCard: '',
})

function myAvatarUrl() {
  return toAvatarUrl(authStore.user?.avatar)
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
    const path = await api.uploadMyAvatar(file)
    authStore.user = { ...authStore.user!, avatar: path }
    localStorage.setItem('sqyy-user', JSON.stringify(authStore.user))
    ElMessage.success('头像上传成功')
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '上传失败')
  } finally {
    uploading.value = false
  }
}

function startEdit() {
  form.phone = authStore.user?.phone || ''
  form.idCard = authStore.user?.idCard || ''
  editing.value = true
}

function cancelEdit() {
  editing.value = false
}

async function saveProfile() {
  if (!form.phone.trim()) { ElMessage.warning('请输入手机号'); return }
  if (!form.idCard.trim()) { ElMessage.warning('请输入身份证号'); return }
  saving.value = true
  try {
    await api.updateProfile({ phone: form.phone.trim(), idCard: form.idCard.trim() })
    authStore.user = { ...authStore.user!, phone: form.phone.trim(), idCard: form.idCard.trim() }
    localStorage.setItem('sqyy-user', JSON.stringify(authStore.user))
    editing.value = false
    ElMessage.success('个人信息已更新')
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>个人信息</span>
        <el-button v-if="!editing" type="primary" link :icon="Edit" @click="startEdit">编辑</el-button>
      </div>
    </template>

    <div class="profile-content">
      <div class="avatar-section">
        <el-avatar :size="100" :src="myAvatarUrl() || undefined">
          <el-icon :size="50"><User /></el-icon>
        </el-avatar>
        <el-button type="primary" :loading="uploading" @click="triggerFileInput">
          {{ uploading ? '上传中' : '上传头像' }}
        </el-button>
        <input
          ref="fileInputRef"
          type="file"
          accept="image/jpeg,image/png,image/gif,image/webp"
          class="hidden-input"
          @change="handleAvatarChange"
        />
      </div>

      <!-- 查看模式 -->
      <div v-if="!editing" class="info-section">
        <div class="info-item"><span class="label">用户名：</span>{{ authStore.user?.username }}</div>
        <div class="info-item"><span class="label">姓名：</span>{{ authStore.user?.realName }}</div>
        <div class="info-item"><span class="label">手机号：</span>{{ authStore.user?.phone || '未填写' }}</div>
        <div class="info-item"><span class="label">身份证号：</span>{{ authStore.user?.idCard || '未填写' }}</div>
        <div class="info-item"><span class="label">角色：</span>{{ authStore.user?.roles?.join('、') }}</div>
        <div class="info-item">
          <span class="label">所属部门：</span>
          {{ authStore.user?.department || authStore.user?.pharmacy || authStore.user?.receptionDesk || '-' }}
        </div>
      </div>

      <!-- 编辑模式 -->
      <div v-else class="info-section">
        <div class="info-item"><span class="label">用户名：</span>{{ authStore.user?.username }}</div>
        <div class="info-item"><span class="label">姓名：</span>{{ authStore.user?.realName }}</div>
        <div class="info-item">
          <span class="label">手机号：</span>
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" size="small" style="width: 200px;" />
        </div>
        <div class="info-item">
          <span class="label">身份证号：</span>
          <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" size="small" style="width: 200px;" />
        </div>
        <div class="info-item"><span class="label">角色：</span>{{ authStore.user?.roles?.join('、') }}</div>
        <div class="info-item">
          <span class="label">所属部门：</span>
          {{ authStore.user?.department || authStore.user?.pharmacy || authStore.user?.receptionDesk || '-' }}
        </div>
        <div class="edit-actions">
          <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </div>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.profile-content {
  display: flex;
  gap: 48px;
  align-items: flex-start;
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}
.hidden-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
}
.info-section {
  flex: 1;
}
.info-item {
  margin-bottom: 12px;
  font-size: 15px;
}
.info-item .label {
  color: #909399;
  margin-right: 8px;
}
.edit-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}
</style>

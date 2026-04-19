<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, FirstAidKit } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const loginType = ref<'employee' | 'patient'>('employee')
const form = reactive({
  username: '',
  password: '',
})

const accounts = [
  { username: 'admin', password: '123456', role: '管理员' },
  { username: 'doctor', password: '123456', role: '医生' },
  { username: 'pharmacist', password: '123456', role: '药师' },
  { username: 'reception', password: '123456', role: '前台' },
]

async function submit() {
  if (!form.username.trim()) { ElMessage.warning('请输入用户名'); return }
  if (!form.password.trim()) { ElMessage.warning('请输入密码'); return }
  loading.value = true
  try {
    await authStore.login(form)
    // 角色隔离：检查登录类型与实际角色是否匹配
    const isPatient = authStore.hasRole(['PATIENT'])
    if (loginType.value === 'employee' && isPatient) {
      authStore.logout()
      ElMessage.error('该账号为患者账号，请切换到「患者登录」')
      return
    }
    if (loginType.value === 'patient' && !isPatient) {
      authStore.logout()
      ElMessage.error('该账号为员工账号，请切换到「员工登录」')
      return
    }
    ElMessage.success('登录成功')
    if (isPatient) {
      router.push('/user-center/home')
    } else if (authStore.hasRole(['ADMIN', 'DOCTOR', 'RECEPTION'])) {
      router.push('/dashboard')
    } else if (authStore.hasRole(['PHARMACIST'])) {
      router.push('/drugs')
    } else {
      router.push('/profile')
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}

function fill(username: string, password: string) {
  form.username = username
  form.password = password
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h2>🏥 社区医院信息平台</h2>

      <!-- 登录类型切换 -->
      <div class="login-tabs">
        <div class="login-tab" :class="{ active: loginType === 'employee' }" @click="loginType = 'employee'">
          <el-icon><User /></el-icon>
          <span>员工登录</span>
        </div>
        <div class="login-tab" :class="{ active: loginType === 'patient' }" @click="loginType = 'patient'">
          <el-icon><FirstAidKit /></el-icon>
          <span>患者登录</span>
        </div>
      </div>

      <el-form label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :placeholder="loginType === 'employee' ? '请输入员工账号' : '请输入病案号'" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" class="full-width" size="large" :loading="loading" @click="submit">登 录</el-button>
      </el-form>

      <!-- 患者注册入口 -->
      <div v-if="loginType === 'patient'" class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>

      <!-- 演示账号（仅员工登录显示） -->
      <div v-if="loginType === 'employee'" class="demo-accounts">
        <div class="demo-accounts__title">演示账号（点击快速填充）</div>
        <div class="demo-account-list">
          <div
            v-for="account in accounts"
            :key="account.username"
            class="demo-tag"
            @click="fill(account.username, account.password)"
          >
            {{ account.role }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('/beijing.jpg') center/cover no-repeat fixed;
  position: relative;
}

.login-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
}

.login-card {
  position: relative;
  width: 400px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 36px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(10px);
}

.login-card h2 {
  text-align: center;
  margin-bottom: 24px;
  font-size: 22px;
  color: #303133;
}

.login-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.login-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 0;
  border-radius: 8px;
  cursor: pointer;
  background: #f4f4f5;
  color: #909399;
  font-size: 14px;
  transition: all 0.2s;
  user-select: none;
}

.login-tab:hover {
  background: #e9e9eb;
}

.login-tab.active {
  background: #409eff;
  color: #fff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.35);
}

.full-width {
  width: 100%;
  margin-top: 4px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #909399;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
}

.demo-accounts {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.demo-accounts__title {
  font-size: 12px;
  color: #c0c4cc;
  margin-bottom: 10px;
}

.demo-account-list {
  display: flex;
  gap: 8px;
}

.demo-tag {
  padding: 6px 14px;
  border-radius: 20px;
  background: #f0f2f5;
  color: #606266;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.demo-tag:hover {
  background: #409eff;
  color: #fff;
}
</style>

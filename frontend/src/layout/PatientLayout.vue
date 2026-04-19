<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, HomeFilled, ChatDotRound, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'
import { api } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const pwdVisible = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const menuItems = [
  { path: '/user-center/home', title: '首页', icon: HomeFilled },
  { path: '/user-center/profile', title: '个人信息', icon: User },
  { path: '/user-center/ai', title: 'AI 健康助手', icon: ChatDotRound },
]

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const item = menuItems.find(m => m.path === route.path)
  return item?.title ?? '患者中心'
})

function logout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    authStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => {})
}

async function submitChangePassword() {
  const { oldPassword, newPassword, confirmPassword } = pwdForm
  if (!oldPassword.trim()) { ElMessage.warning('请输入原密码'); return }
  if (!newPassword.trim()) { ElMessage.warning('请输入新密码'); return }
  if (newPassword !== confirmPassword) { ElMessage.warning('两次输入的新密码不一致'); return }
  if (newPassword.length < 6) { ElMessage.warning('新密码至少 6 位'); return }
  try {
    await api.changePassword({ oldPassword, newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    pwdVisible.value = false
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    authStore.logout()
    router.push('/login')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '修改失败')
  }
}
</script>

<template>
  <el-container class="app-shell">
    <el-aside class="app-sidebar" width="220px">
      <div class="app-logo">
        <div class="app-logo__title">🏥 社区医院</div>
        <div class="app-logo__subtitle">患者中心</div>
      </div>

      <el-menu :default-active="activeMenu" class="app-menu" router>
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="page-title">{{ pageTitle }}</div>

        <div class="header-user">
          <el-avatar :size="32">
            <span style="font-size: 16px;">😊</span>
          </el-avatar>
          <span>{{ authStore.user?.realName || '患者' }}</span>
          <el-tag type="success" size="small">患者</el-tag>
          <el-button link @click="pwdVisible = true">修改密码</el-button>
          <el-button link type="danger" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </el-header>

      <el-dialog v-model="pwdVisible" title="修改密码" width="400px">
        <el-form label-position="top">
          <el-form-item label="原密码">
            <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码（至少 6 位）" show-password />
          </el-form-item>
          <el-form-item label="确认新密码">
            <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="pwdVisible = false">取消</el-button>
          <el-button type="primary" @click="submitChangePassword">确定</el-button>
        </template>
      </el-dialog>

      <el-main class="app-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.app-sidebar {
  background: linear-gradient(180deg, rgba(16, 185, 129, 0.85), rgba(5, 150, 105, 0.9));
  color: #fff;
}

.app-logo {
  padding: 24px 20px 16px;
}

.app-logo__title {
  font-size: 20px;
  font-weight: 700;
}

.app-logo__subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
}

.app-menu {
  border-right: none;
  background: transparent;
}

.app-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.88);
}

.app-menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.16);
  color: #fff;
}

.app-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.7);
  border-bottom: 1px solid rgba(229, 231, 235, 0.7);
  backdrop-filter: blur(8px);
}

.page-title {
  font-size: 20px;
  font-weight: 700;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-main {
  background: transparent;
  padding: 24px;
}
</style>

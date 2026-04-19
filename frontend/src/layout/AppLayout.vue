<script setup lang="ts">
import { User } from '@element-plus/icons-vue'
import { computed, reactive, ref } from 'vue'
import { avatarUrl } from '../utils/avatar'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../api'
import { routes } from '../router'
import { useAuthStore } from '../stores/auth'

interface MenuItem {
  path: string
  title: string
  roles?: string[]
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const pwdVisible = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const menuItems = computed<MenuItem[]>(() => {
  const root = routes.find((item) => item.path === '/')
  const children = (root?.children ?? []) as Array<{
    path: string
    meta?: { title?: string; roles?: string[]; hideInMenu?: boolean }
  }>

  return children
    .filter((item) => authStore.hasRole(item.meta?.roles) && !item.meta?.hideInMenu)
    .map((item) => {
      let title = item.meta?.title ?? item.path
      if (item.path === 'patients') {
        title = authStore.hasRole(['RECEPTION']) ? '挂号服务' : '患者信息'
      }
      return {
        path: item.path ? `/${item.path}` : '/',
        title,
        roles: item.meta?.roles,
      }
    })
})

const pageTitle = computed(() => {
  if (route.path === '/patients' || route.path.startsWith('/patients')) {
    return authStore.hasRole(['RECEPTION']) ? '挂号服务' : '患者信息'
  }
  return (route.meta?.title as string) ?? '社区医院平台'
})

const activeMenu = computed(() => route.path)

function logout() {
  authStore.logout()
  router.push('/login')
}

async function submitChangePassword() {
  const { oldPassword, newPassword, confirmPassword } = pwdForm
  if (!oldPassword.trim()) {
    ElMessage.warning('请输入原密码')
    return
  }
  if (!newPassword.trim()) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (newPassword !== confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  if (newPassword.length < 6) {
    ElMessage.warning('新密码至少 6 位')
    return
  }
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
        <div class="app-logo__title">社区医院平台</div>
      </div>

      <el-menu :default-active="activeMenu" class="app-menu" router>
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          {{ item.title }}
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="page-title">{{ pageTitle }}</div>

        <div class="header-user">
          <el-avatar :size="32" :src="avatarUrl(authStore.user?.avatar) || undefined">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span>{{ authStore.user?.realName }}</span>
          <el-tag type="primary">{{ authStore.user?.roles?.join(' / ') }}</el-tag>
          <el-button link @click="$router.push('/profile')">个人信息</el-button>
          <el-button link @click="pwdVisible = true">修改密码</el-button>
          <el-button link type="danger" @click="logout">退出登录</el-button>
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

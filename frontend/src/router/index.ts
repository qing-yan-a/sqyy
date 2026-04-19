import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true, title: '登录' },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/PatientRegisterView.vue'),
    meta: { public: true, title: '患者注册' },
  },
  // 患者端路由
  {
    path: '/user-center',
    component: () => import('../layout/PatientLayout.vue'),
    meta: { roles: ['PATIENT'] },
    children: [
      {
        path: '',
        redirect: '/user-center/home',
      },
      {
        path: 'home',
        name: 'patient-home',
        component: () => import('../views/patient/PatientHomeView.vue'),
        meta: { title: '首页', roles: ['PATIENT'] },
      },
      {
        path: 'records',
        name: 'patient-records',
        component: () => import('../views/patient/RecordsView.vue'),
        meta: { title: '挂号记录', roles: ['PATIENT'] },
      },
      {
        path: 'history',
        name: 'patient-history',
        component: () => import('../views/patient/HistoryView.vue'),
        meta: { title: '历史就诊记录', roles: ['PATIENT'] },
      },
      {
        path: 'departments',
        name: 'patient-departments',
        component: () => import('../views/patient/DepartmentsView.vue'),
        meta: { title: '科室与医生', roles: ['PATIENT'] },
      },
      {
        path: 'profile',
        name: 'patient-profile',
        component: () => import('../views/patient/PatientProfileView.vue'),
        meta: { title: '个人信息', roles: ['PATIENT'] },
      },
      {
        path: 'register',
        name: 'patient-register',
        component: () => import('../views/patient/RegistrationView.vue'),
        meta: { title: '挂号服务', roles: ['PATIENT'] },
      },
      {
        path: 'ai',
        name: 'patient-ai',
        component: () => import('../views/patient/AiAssistantView.vue'),
        meta: { title: 'AI助手', roles: ['PATIENT'] },
      },
      {
        path: 'news',
        name: 'patient-news',
        component: () => import('../views/patient/HealthNewsView.vue'),
        meta: { title: '健康资讯', roles: ['PATIENT'] },
      },
    ],
  },
  // 员工端路由
  {
    path: '/',
    component: () => import('../layout/AppLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('../views/DashboardView.vue'),
        meta: { title: '工作台' },
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('../views/ProfileView.vue'),
        meta: { title: '个人信息' },
      },
      {
        path: 'patients',
        name: 'patients',
        component: () => import('../views/PatientView.vue'),
        meta: { title: '挂号服务', roles: ['ADMIN', 'DOCTOR', 'RECEPTION'] },
      },
      {
        path: 'visits',
        name: 'visits',
        component: () => import('../views/VisitView.vue'),
        meta: { title: '就诊记录', roles: ['ADMIN', 'DOCTOR', 'RECEPTION'] },
      },
      {
        path: 'drugs',
        name: 'drugs',
        component: () => import('../views/DrugView.vue'),
        meta: { title: '药品管理', roles: ['ADMIN', 'PHARMACIST'] },
      },
      {
        path: 'inventory',
        name: 'inventory',
        component: () => import('../views/InventoryView.vue'),
        meta: { title: '库存流水', roles: ['ADMIN', 'PHARMACIST'] },
      },
      {
        path: 'injection',
        name: 'injection',
        component: () => import('../views/InjectionView.vue'),
        meta: { title: '注射药物', roles: ['ADMIN', 'PHARMACIST'] },
      },
      {
        path: 'dispense/:visitId',
        name: 'dispense',
        component: () => import('../views/DispenseView.vue'),
        meta: { title: '取药窗口', roles: ['ADMIN', 'PHARMACIST'], hideInMenu: true },
      },
      {
        path: 'admin/users',
        name: 'admin-users',
        component: () => import('../views/UserAdminView.vue'),
        meta: { title: '用户角色', roles: ['ADMIN'] },
      },
      {
        path: 'search',
        name: 'search',
        component: () => import('../views/SearchView.vue'),
        meta: { title: '智能检索', roles: ['ADMIN', 'DOCTOR', 'PHARMACIST', 'RECEPTION'] },
      },
      {
        path: 'ai',
        name: 'doctor-ai',
        component: () => import('../views/DoctorAiView.vue'),
        meta: { title: 'AI 助手', roles: ['ADMIN', 'DOCTOR'] },
      },
      {
        path: 'timeline',
        name: 'timeline',
        component: () => import('../views/TimelineView.vue'),
        meta: { title: '病历时间轴', roles: ['ADMIN', 'DOCTOR'] },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (to.path === '/') {
    return '/login'
  }

  if (to.meta.public) {
    return true
  }

  if (!authStore.isLoggedIn) {
    return '/login'
  }

  if (!authStore.user) {
    try {
      await authStore.refreshProfile()
    } catch {
      authStore.logout()
      return '/login'
    }
  }

  const roles = to.meta.roles as string[] | undefined
  if (!authStore.hasRole(roles)) {
    // 患者角色没有权限访问员工页面时，跳转患者首页
    if (authStore.hasRole(['PATIENT'])) {
      return '/user-center/home'
    }
    return '/dashboard'
  }

  return true
})

export default router
export { routes }

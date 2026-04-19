<script setup lang="ts">
import { ref, computed, onMounted, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import StatCard from '../components/StatCard.vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import * as echarts from 'echarts'
import type { VisitDetail, Patient } from '../types'

const authStore = useAuthStore()
const router = useRouter()

const patients = ref<{ id: number; patientNo: string; name: string }[]>([])
const visits = ref<VisitDetail[]>([])
const drugs = ref<{ id: number; drugName: string; stock: number; availableStock: number; warningStock: number; drugType?: string }[]>([])
const pendingVisits = ref<{ visit: { id: number; queueNumber?: number; visitNo: string }; patient?: { name: string; patientNo: string } }[]>([])
const pickupTodos = ref<{ visit: { id: number; visitNo: string }; patient?: { name: string }; totalAmount?: number }[]>([])
const injectionTodos = ref<{ visit: { id: number; visitNo: string }; patient?: { name: string }; prescriptions: { drugType: string; drugName: string }[] }[]>([])

// 前台工作台数据
const receptionDashboard = ref<{
  todayTotal: number
  todayPending: number
  todayCompleted: number
  todayPatients: number
  queue: VisitDetail[]
  departmentStats: Record<string, { total: number; pending: number; completed: number }>
  recentRegistrations?: VisitDetail[]
}>({
  todayTotal: 0,
  todayPending: 0,
  todayCompleted: 0,
  todayPatients: 0,
  queue: [],
  departmentStats: {},
  recentRegistrations: [],
})
const receptionLoading = ref(false)

// 患者快速检索
const patientKeyword = ref('')
const patientSearchResults = ref<Patient[]>([])
const patientSearchLoading = ref(false)
let searchTimer: ReturnType<typeof setTimeout> | null = null

function onPatientSearch() {
  if (searchTimer) clearTimeout(searchTimer)
  if (!patientKeyword.value.trim()) {
    patientSearchResults.value = []
    return
  }
  searchTimer = setTimeout(async () => {
    patientSearchLoading.value = true
    try {
      patientSearchResults.value = await api.searchPatients(patientKeyword.value.trim())
    } catch {
      patientSearchResults.value = []
    } finally {
      patientSearchLoading.value = false
    }
  }, 300)
}

const isDoctor = computed(() => authStore.hasRole(['DOCTOR']))
const isPharmacist = computed(() => authStore.hasRole(['PHARMACIST']))
const isReception = computed(() => authStore.hasRole(['RECEPTION']) && !authStore.hasRole(['ADMIN']))
const lowStockCount = computed(() => drugs.value.filter((d) => d.availableStock <= d.warningStock).length)

// 图表 refs
const visitChartRef = ref<HTMLElement>()
const drugChartRef = ref<HTMLElement>()
const visitChart = shallowRef<echarts.ECharts | null>(null)
const drugChart = shallowRef<echarts.ECharts | null>(null)

onMounted(async () => {
  if (isReception.value) {
    // 前台：加载前台工作台数据
    receptionLoading.value = true
    try {
      receptionDashboard.value = await api.getReceptionDashboard()
    } catch {
      // 接口未就绪时静默失败
    } finally {
      receptionLoading.value = false
    }
  } else if (authStore.hasRole(['PHARMACIST'])) {
    drugs.value = await api.getDrugs()
  } else {
    const [p, v, d] = await Promise.all([api.getPatients(), api.getVisits(), api.getDrugs()])
    patients.value = p
    visits.value = v as VisitDetail[]
    drugs.value = d
  }
  if (authStore.user?.roles?.includes('DOCTOR') && authStore.user?.realName) {
    try { pendingVisits.value = await api.getPendingVisits(authStore.user.realName) } catch { pendingVisits.value = [] }
  }
  if (authStore.hasRole(['PHARMACIST'])) {
    try {
      const [pickup, injection] = await Promise.all([api.getPickupTodos(), api.getInjectionTodos()])
      pickupTodos.value = pickup
      injectionTodos.value = injection
    } catch { pickupTodos.value = []; injectionTodos.value = [] }
  }
  // 渲染图表（仅管理员）
  if (!isDoctor.value && !isPharmacist.value && !isReception.value) {
    setTimeout(renderCharts, 100)
  }
})

function renderCharts() {
  renderVisitChart()
  renderDrugChart()
  window.addEventListener('resize', () => {
    visitChart.value?.resize()
    drugChart.value?.resize()
  })
}

function renderVisitChart() {
  if (!visitChartRef.value) return
  visitChart.value = echarts.init(visitChartRef.value)

  const dateMap = new Map<string, number>()
  const now = new Date()
  for (let i = 29; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    const key = `${d.getMonth() + 1}/${d.getDate()}`
    dateMap.set(key, 0)
  }
  for (const v of visits.value) {
    const t = new Date(v.visit.visitTime)
    const key = `${t.getMonth() + 1}/${t.getDate()}`
    if (dateMap.has(key)) {
      dateMap.set(key, (dateMap.get(key) || 0) + 1)
    }
  }

  visitChart.value.setOption({
    title: { text: '近30天就诊趋势', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: [...dateMap.keys()], axisLabel: { rotate: 45, fontSize: 11 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'line',
      data: [...dateMap.values()],
      smooth: true,
      areaStyle: { color: 'rgba(64, 158, 255, 0.15)' },
      lineStyle: { color: '#409eff' },
      itemStyle: { color: '#409eff' },
    }],
    grid: { left: 40, right: 20, bottom: 60, top: 40 },
  })
}

function renderDrugChart() {
  if (!drugChartRef.value) return
  drugChart.value = echarts.init(drugChartRef.value)

  const normal = drugs.value.filter(d => d.availableStock > d.warningStock).length
  const warning = drugs.value.filter(d => d.availableStock > 0 && d.availableStock <= d.warningStock).length
  const outOfStock = drugs.value.filter(d => d.availableStock <= 0).length

  drugChart.value.setOption({
    title: { text: '药品库存状态', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 10 },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      data: [
        { value: normal, name: '库存正常', itemStyle: { color: '#67c23a' } },
        { value: warning, name: '库存预警', itemStyle: { color: '#e6a23c' } },
        { value: outOfStock, name: '已缺货', itemStyle: { color: '#f56c6c' } },
      ],
      label: { formatter: '{b}\n{c} 种 ({d}%)' },
      emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.2)' } },
    }],
  })
}

function goToVisit(visitId: number) {
  router.push({ path: '/visits', query: { edit: String(visitId) } })
}

function goToVisitsList() {
  router.push('/visits')
}
function goToDispense(visitId: number) {
  router.push(`/dispense/${visitId}`)
}
function goToInjection() {
  router.push('/injection')
}

function deptStatusClass(pending: number, total: number) {
  if (total === 0) return 'status-idle'
  const ratio = pending / total
  if (ratio > 0.6) return 'status-busy'
  if (ratio > 0.3) return 'status-normal'
  return 'status-idle'
}

function deptStatusText(pending: number) {
  if (pending > 5) return '较忙'
  if (pending > 2) return '一般'
  return '空闲'
}

async function handleCancelVisit(visitId: number, patientName: string) {
  try {
    await ElMessageBox.confirm(`确定要取消「${patientName}」的挂号吗？`, '取消挂号', { type: 'warning' })
    await api.cancelVisit(visitId)
    ElMessage.success('已取消挂号')
    // 刷新数据
    receptionDashboard.value = await api.getReceptionDashboard()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '取消失败')
    }
  }
}
</script>

<template>
  <!-- 药师工作台 -->
  <template v-if="isPharmacist">
    <div class="page-grid">
      <div class="stat-clickable" @click="router.push('/drugs')"><StatCard label="药品条目" :value="drugs.length" helper="含口服与注射类药品" /></div>
      <StatCard label="待取药" :value="pickupTodos.length" helper="待收款或待发药患者" />
      <StatCard label="待注射" :value="injectionTodos.length" helper="含注射类药物的待办" />
      <div class="stat-clickable" @click="router.push('/drugs')"><StatCard label="低库存预警" :value="lowStockCount" helper="药师可及时补货" /></div>
    </div>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>待取药</span>
          <el-button v-if="pickupTodos.length > 0" type="primary" link @click="goToDispense(pickupTodos[0].visit.id)">进入取药窗口</el-button>
        </div>
      </template>
      <div v-if="pickupTodos.length === 0" class="empty-queue">暂无待取药患者</div>
      <div v-else class="queue-list">
        <div v-for="(item, idx) in pickupTodos.slice(0, 5)" :key="item.visit.id" class="queue-item" @click="goToDispense(item.visit.id)">
          <span class="queue-num">{{ idx + 1 }}</span>
          <span class="queue-name">{{ item.patient?.name }} / {{ item.visit.visitNo }}</span>
          <span class="queue-time">应收 ￥{{ Number(item.totalAmount ?? 0).toFixed(2) }}</span>
        </div>
      </div>
    </el-card>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>注射待办</span>
          <el-button v-if="injectionTodos.length > 0" type="primary" link @click="goToInjection">查看全部 ({{ injectionTodos.length }})</el-button>
        </div>
      </template>
      <div v-if="injectionTodos.length === 0" class="empty-queue">暂无待注射患者</div>
      <div v-else class="queue-list">
        <div v-for="(item, idx) in injectionTodos.slice(0, 5)" :key="item.visit.id" class="queue-item" @click="goToInjection">
          <span class="queue-num">{{ idx + 1 }}</span>
          <span class="queue-name">{{ item.patient?.name }} / {{ item.visit.visitNo }}</span>
          <span class="queue-time">{{ item.prescriptions.filter(p => p.drugType === 'INJECTION').map(p => p.drugName).join('、') }}</span>
        </div>
      </div>
    </el-card>
  </template>

  <!-- 医生工作台 -->
  <template v-else-if="isDoctor">
    <div class="page-grid">
      <div class="stat-clickable" @click="router.push('/patients')"><StatCard label="患者总数" :value="patients.length" helper="点击查看详情" /></div>
      <div class="stat-clickable" @click="router.push('/visits')"><StatCard label="就诊记录" :value="visits.length" helper="点击查看详情" /></div>
      <div class="stat-clickable" @click="router.push('/drugs')"><StatCard label="药品条目" :value="drugs.length" helper="点击查看详情" /></div>
      <div class="stat-clickable" @click="router.push('/drugs')"><StatCard label="低库存预警" :value="lowStockCount" helper="点击查看详情" /></div>
    </div>
    <el-card shadow="never">
      <template #header><span>待接诊患者</span></template>
      <div v-if="pendingVisits.length === 0" class="empty-queue">暂无待接诊患者</div>
      <div v-else class="queue-list">
        <div v-for="(item, idx) in pendingVisits" :key="item.visit.id" class="queue-item" @click="goToVisit(item.visit.id)">
          <span class="queue-num">{{ item.visit.queueNumber ?? idx + 1 }}</span>
          <span class="queue-name">{{ item.patient?.name }} / {{ item.patient?.patientNo }}</span>
          <span class="queue-time">{{ item.visit.visitNo }}</span>
        </div>
      </div>
    </el-card>
  </template>

  <!-- 前台工作台 -->
  <template v-else-if="isReception">
    <div class="page-grid">
      <StatCard label="今日挂号" :value="receptionDashboard.todayTotal" helper="今日所有挂号数" />
      <StatCard label="待就诊" :value="receptionDashboard.todayPending" helper="排队中等待接诊" />
      <StatCard label="已完成" :value="receptionDashboard.todayCompleted" helper="今日已完成就诊" />
      <StatCard label="今日患者" :value="receptionDashboard.todayPatients" helper="不重复患者数" />
    </div>

    <div class="reception-columns">
      <!-- 候诊队列 -->
      <el-card shadow="never" class="queue-card">
        <template #header>
          <div class="card-header">
            <span>候诊队列</span>
            <el-button type="primary" link @click="router.push('/patients')">快捷挂号 →</el-button>
          </div>
        </template>
        <div v-if="receptionLoading" class="empty-queue">加载中...</div>
        <div v-else-if="receptionDashboard.queue.length === 0" class="empty-queue">暂无候诊患者</div>
        <div v-else class="queue-list">
          <div
            v-for="item in receptionDashboard.queue"
            :key="item.visit.id"
            class="queue-item"
            @click="goToVisitsList()"
          >
            <span class="queue-num">{{ item.visit.queueNumber ?? '-' }}</span>
            <div class="queue-info">
              <span class="queue-name">{{ item.patient?.name ?? '未知' }}</span>
              <span class="queue-detail">{{ item.visit.department }} · {{ item.visit.doctorName }}</span>
            </div>
            <span class="queue-visit-no">{{ item.visit.visitNo }}</span>
            <el-button
              type="danger"
              size="small"
              link
              @click.stop="handleCancelVisit(item.visit.id, item.patient?.name ?? '未知')"
            >退号</el-button>
          </div>
        </div>
      </el-card>

      <!-- 各科室排队情况 -->
      <el-card shadow="never" class="dept-card">
        <template #header>
          <span>各科室排队情况</span>
        </template>
        <div v-if="receptionLoading" class="empty-queue">加载中...</div>
        <div v-else-if="Object.keys(receptionDashboard.departmentStats).length === 0" class="empty-queue">今日暂无挂号数据</div>
        <div v-else class="dept-list">
          <div
            v-for="(stats, dept) in receptionDashboard.departmentStats"
            :key="dept"
            class="dept-item"
          >
            <div class="dept-header">
              <span class="dept-name">{{ dept }}</span>
              <el-tag
                :type="deptStatusClass(stats.pending, stats.total) === 'status-busy' ? 'danger' : deptStatusClass(stats.pending, stats.total) === 'status-normal' ? 'warning' : 'success'"
                size="small"
              >{{ deptStatusText(stats.pending) }}</el-tag>
            </div>
            <div class="dept-stats">
              <span>挂号 <strong>{{ stats.total }}</strong></span>
              <span>待诊 <strong>{{ stats.pending }}</strong></span>
              <span>完成 <strong>{{ stats.completed }}</strong></span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 患者快速检索 + 最近挂号记录 -->
    <div class="reception-bottom">
      <el-card shadow="never" class="search-card">
        <template #header><span>患者快速检索</span></template>
        <div class="quick-search">
          <el-input
            v-model="patientKeyword"
            placeholder="输入姓名 / 手机号 / 身份证搜索患者..."
            clearable
            size="large"
            @input="onPatientSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <div v-if="patientKeyword.trim()" class="search-results">
            <div v-if="patientSearchLoading" class="empty-queue">搜索中...</div>
            <div v-else-if="patientSearchResults.length === 0" class="empty-queue">未找到匹配患者</div>
            <div v-else class="search-result-list">
              <div
                v-for="p in patientSearchResults.slice(0, 5)"
                :key="p.id"
                class="search-result-item"
                @click="router.push({ path: '/patients', query: { keyword: p.name } })"
              >
                <span class="result-name">{{ p.name }}</span>
                <span class="result-detail">{{ p.patientNo }} · {{ p.phone || '无手机' }}</span>
              </div>
              <div v-if="patientSearchResults.length > 5" class="search-more">
                还有 {{ patientSearchResults.length - 5 }} 条结果，
                <el-button type="primary" link @click="router.push({ path: '/patients', query: { keyword: patientKeyword } })">查看全部</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="recent-card">
        <template #header>
          <div class="card-header">
            <span>最近挂号记录</span>
            <el-button type="primary" link @click="router.push('/visits')">查看全部 →</el-button>
          </div>
        </template>
        <div v-if="receptionDashboard.recentRegistrations?.length === 0" class="empty-queue">今日暂无挂号记录</div>
        <div v-else class="recent-list">
          <div
            v-for="item in (receptionDashboard.recentRegistrations || []).slice(0, 6)"
            :key="item.visit.id"
            class="recent-item"
            @click="goToVisitsList()"
          >
            <span class="recent-name">{{ item.patient?.name ?? '未知' }}</span>
            <span class="recent-dept">{{ item.visit.department }}</span>
            <span class="recent-doctor">{{ item.visit.doctorName }}</span>
            <el-tag
              :type="item.visit.status === 'PENDING' ? 'warning' : item.visit.status === 'COMPLETED' ? 'success' : 'info'"
              size="small"
            >{{ item.visit.status === 'PENDING' ? '候诊中' : item.visit.status === 'COMPLETED' ? '已完成' : item.visit.status }}</el-tag>
            <span class="recent-time">{{ new Date(item.visit.visitTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 快捷操作 -->
    <el-card shadow="never" class="quick-actions">
      <template #header><span>快捷操作</span></template>
      <div class="action-grid">
        <el-button type="primary" @click="router.push('/patients')">
          📋 前往挂号
        </el-button>
        <el-button @click="router.push('/patients')">
          ➕ 新增患者
        </el-button>
        <el-button @click="router.push('/visits')">
          📑 就诊记录
        </el-button>
        <el-button @click="router.push('/search')">
          🔎 智能检索
        </el-button>
      </div>
    </el-card>
  </template>

  <!-- 管理员工作台 -->
  <template v-else>
    <div class="page-grid">
      <div class="stat-clickable" @click="router.push('/patients')"><StatCard label="患者总数" :value="patients.length" helper="点击查看详情" /></div>
      <div class="stat-clickable" @click="router.push('/visits')"><StatCard label="就诊记录" :value="visits.length" helper="点击查看详情" /></div>
      <div class="stat-clickable" @click="router.push('/drugs')"><StatCard label="药品条目" :value="drugs.length" helper="点击查看详情" /></div>
      <div class="stat-clickable" @click="router.push('/drugs')"><StatCard label="低库存预警" :value="lowStockCount" helper="点击查看详情" /></div>
    </div>
    <div class="chart-row">
      <el-card shadow="never" class="chart-card">
        <div ref="visitChartRef" class="chart-container"></div>
      </el-card>
      <el-card shadow="never" class="chart-card">
        <div ref="drugChartRef" class="chart-container"></div>
      </el-card>
    </div>
  </template>
</template>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.empty-queue { color: var(--el-text-color-secondary); text-align: center; padding: 24px; }
.queue-list { display: flex; flex-direction: column; gap: 8px; }
.queue-item { background: var(--el-fill-color-light); cursor: pointer; border-radius: 8px; align-items: center; gap: 16px; padding: 12px 16px; display: flex; transition: background 0.2s; }
.queue-item:hover { background: var(--el-fill-color); }
.queue-num { background: var(--el-color-primary); color: #fff; border-radius: 50%; justify-content: center; align-items: center; min-width: 32px; height: 32px; font-weight: 600; display: flex; font-size: 13px; }
.queue-name { flex: 1; font-weight: 500; }
.queue-time { color: var(--el-text-color-secondary); font-size: 13px; }
.chart-row { display: flex; gap: 16px; margin-top: 16px; }
.chart-card { flex: 1; }
.chart-container { width: 100%; height: 320px; }
.stat-clickable { cursor: pointer; transition: transform 0.2s; }
.stat-clickable:hover { transform: translateY(-2px); }

/* 前台工作台样式 */
.reception-columns { display: flex; gap: 16px; margin-top: 16px; }
.queue-card { flex: 2; }
.dept-card { flex: 1; }
.queue-info { display: flex; flex-direction: column; flex: 1; gap: 2px; }
.queue-detail { color: var(--el-text-color-secondary); font-size: 12px; }
.queue-visit-no { color: var(--el-text-color-secondary); font-size: 12px; white-space: nowrap; }
.dept-list { display: flex; flex-direction: column; gap: 12px; }
.dept-item { background: var(--el-fill-color-light); border-radius: 8px; padding: 12px 16px; }
.dept-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.dept-name { font-weight: 600; font-size: 14px; }
.dept-stats { display: flex; gap: 16px; font-size: 13px; color: var(--el-text-color-secondary); }
.dept-stats strong { color: var(--el-text-color-primary); margin-left: 4px; }
.quick-actions { margin-top: 16px; }
.action-grid { display: flex; gap: 12px; flex-wrap: wrap; }
.action-grid .el-button { min-width: 140px; }

/* 患者检索 + 最近挂号 */
.reception-bottom { display: flex; gap: 16px; margin-top: 16px; }
.search-card { flex: 1; }
.recent-card { flex: 2; }
.quick-search { display: flex; flex-direction: column; gap: 12px; }
.search-results { max-height: 280px; overflow-y: auto; }
.search-result-list { display: flex; flex-direction: column; gap: 4px; }
.search-result-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 12px; border-radius: 6px; background: var(--el-fill-color-light); cursor: pointer; transition: background 0.2s; }
.search-result-item:hover { background: var(--el-fill-color); }
.result-name { font-weight: 500; }
.result-detail { color: var(--el-text-color-secondary); font-size: 13px; }
.search-more { text-align: center; padding: 8px; font-size: 13px; color: var(--el-text-color-secondary); }
.recent-list { display: flex; flex-direction: column; gap: 6px; }
.recent-item { display: flex; align-items: center; gap: 16px; padding: 10px 12px; border-radius: 6px; background: var(--el-fill-color-light); cursor: pointer; transition: background 0.2s; }
.recent-item:hover { background: var(--el-fill-color); }
.recent-name { font-weight: 500; min-width: 60px; }
.recent-dept { color: var(--el-text-color-secondary); font-size: 13px; }
.recent-doctor { color: var(--el-text-color-secondary); font-size: 13px; }
.recent-time { color: var(--el-text-color-secondary); font-size: 12px; margin-left: auto; white-space: nowrap; }
</style>

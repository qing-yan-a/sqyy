<script setup lang="ts">
import * as echarts from 'echarts'
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api'
import type { Patient, TimelineResult } from '../types'

const selectedPatientId = ref<number>()
const searchDisplay = ref('')
const loading = ref(false)
const timeline = ref<TimelineResult | null>(null)
const chartRef = ref<HTMLElement>()

let chart: echarts.ECharts | null = null

async function fetchSuggestions(
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

function handleSelect(item: { value: string; patient: Patient }) {
  selectedPatientId.value = item.patient.id
  searchDisplay.value = item.value
}

async function loadTimeline() {
  if (!selectedPatientId.value) return
  loading.value = true
  try {
    timeline.value = await api.getTimeline(selectedPatientId.value)
    await nextTick()
    renderChart()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载时间轴失败')
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!chartRef.value || !timeline.value) return

  chart ??= echarts.init(chartRef.value)
  const colors: Record<string, string> = {
    VISIT: '#409eff',
    DIAGNOSIS: '#67c23a',
    PRESCRIPTION: '#e6a23c',
  }

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: { data: [string, number, string, string] }) =>
        `${params.data[0]}<br/>${params.data[2]}<br/>${params.data[3]}`,
    },
    xAxis: {
      type: 'time',
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 2,
      axisLabel: { show: false },
      splitLine: { show: false },
    },
    series: [
      {
        type: 'scatter',
        symbolSize: 18,
        data: timeline.value.events.map((item) => {
          const y = item.type === 'DIAGNOSIS' ? 1.5 : item.type === 'PRESCRIPTION' ? 0.5 : 1
          return [item.time, y, item.title, item.description, item.type]
        }),
        itemStyle: {
          color: (params: { data: [string, number, string, string, string] }) =>
            colors[params.data[4]] ?? '#909399',
        },
        label: {
          show: true,
          formatter: (params: { data: [string, number, string] }) => params.data[2],
          position: (params: { data: [string, number, string, string, string] }) =>
            params.data[4] === 'DIAGNOSIS' ? 'top' : params.data[4] === 'PRESCRIPTION' ? 'bottom' : 'top',
        },
      },
    ],
  })
}

watch(selectedPatientId, loadTimeline)

onMounted(async () => {
  const patients = await api.searchPatients('')
  if (patients.length > 0) {
    selectedPatientId.value = patients[0].id
    searchDisplay.value = `${patients[0].name} / ${patients[0].patientNo}`
  }
  await loadTimeline()
  window.addEventListener('resize', resizeChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  chart?.dispose()
  chart = null
})

function resizeChart() {
  chart?.resize()
}
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <div class="card-header">
        <span>患者病历时间轴</span>
        <el-autocomplete
          v-model="searchDisplay"
          :fetch-suggestions="fetchSuggestions"
          placeholder="搜索患者姓名或编号"
          value-key="value"
          style="width: 320px"
          @select="handleSelect"
        />
      </div>
    </template>

    <div v-if="timeline" class="timeline-overview">
      <el-card shadow="never">
        <template #header>患者病史</template>
        <div class="timeline-overview__item"><strong>患者：</strong>{{ timeline.patient.name }}</div>
        <div class="timeline-overview__item"><strong>既往病史：</strong>{{ timeline.patient.medicalHistory || '暂无' }}</div>
        <div class="timeline-overview__item"><strong>过敏史：</strong>{{ timeline.patient.allergyHistory || '暂无' }}</div>
      </el-card>

      <el-card shadow="never">
        <template #header>用药信息</template>
        <div v-if="timeline.medications.length" class="timeline-medications">
          <div
            v-for="item in timeline.medications"
            :key="`${item.drugName}-${item.visitTime}-${item.quantity}`"
            class="timeline-medication-item"
          >
            <strong>{{ item.drugName }}</strong>
            <span>{{ item.dosage || '未填剂量' }} / {{ item.frequency || '未填频次' }}</span>
            <span>{{ item.days }} 天 / {{ item.quantity }} 份</span>
          </div>
        </div>
        <el-empty v-else description="暂无用药记录" :image-size="60" />
      </el-card>
    </div>

    <div ref="chartRef" class="timeline-chart"></div>

    <el-timeline>
      <el-timeline-item v-for="event in timeline?.events ?? []" :key="`${event.type}-${event.time}-${event.title}`" :timestamp="event.time">
        <strong>{{ event.title }}</strong>
        <div>{{ event.description }}</div>
      </el-timeline-item>
    </el-timeline>
  </el-card>
</template>

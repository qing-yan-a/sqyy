<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api'
import FormDialog from '../components/FormDialog.vue'
import type { Drug } from '../types'

const loading = ref(false)
const visible = ref(false)
const drugs = ref<Drug[]>([])
const allDrugs = ref<Drug[]>([])
const searchKeyword = ref('')
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const drugTypeFilter = ref('')
const stockFilter = ref('')

const form = reactive({
  drugName: '',
  specification: '',
  manufacturer: '',
  unit: '盒',
  initialStock: 0,
  warningStock: 10,
  unitPrice: 0,
  drugType: 'ORAL' as 'ORAL' | 'INJECTION',
})

async function loadDrugs() {
  loading.value = true
  try {
    const res = await api.searchDrugs({
      q: searchKeyword.value.trim(),
      page: page.value,
      size: pageSize.value,
      drugType: drugTypeFilter.value || undefined,
    })
    drugs.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handlePageChange(p: number) {
  page.value = p
  loadDrugs()
}

function handleSizeChange(s: number) {
  pageSize.value = s
  page.value = 1
  loadDrugs()
}

watch(searchKeyword, () => {
  page.value = 1
  const timer = setTimeout(loadDrugs, 300)
  return () => clearTimeout(timer)
})

async function loadAllDrugs() {
  try {
    allDrugs.value = await api.getDrugs()
  } catch {
    allDrugs.value = []
  }
}

function handleTypeChange() {
  page.value = 1
  loadDrugs()
}

function filterByStock(filter: string) {
  stockFilter.value = stockFilter.value === filter ? '' : filter
  page.value = 1
  loadDrugs()
}

function getFilteredDrugs() {
  if (!stockFilter.value) return drugs.value
  if (stockFilter.value === 'warning') {
    return drugs.value.filter(d => d.availableStock > 0 && d.availableStock <= d.warningStock)
  }
  if (stockFilter.value === 'danger') {
    return drugs.value.filter(d => d.availableStock <= 0)
  }
  return drugs.value
}

async function submit() {
  try {
    await api.createDrug({ ...form, drugType: form.drugType })
    ElMessage.success('药品新增成功')
    visible.value = false
    resetForm()
    await loadDrugs()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交失败')
  }
}

function resetForm() {
  form.drugName = ''
  form.specification = ''
  form.manufacturer = ''
  form.unit = '盒'
  form.initialStock = 0
  form.warningStock = 10
  form.unitPrice = 0
  form.drugType = 'ORAL'
}

async function updateWarningStock(row: Drug, val: number) {
  try {
    await api.updateDrugWarningStock(row.id, val)
    ElMessage.success('预警值已更新')
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '修改失败')
    await loadDrugs()
  }
}

async function handleDelete(row: Drug) {
  try {
    await ElMessageBox.confirm(`确定要删除药品「${row.drugName}」吗？`, '删除确认', { type: 'warning' })
    await api.deleteDrug(row.id)
    ElMessage.success('删除成功')
    await loadDrugs()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e instanceof Error ? e.message : '删除失败')
  }
}

onMounted(() => { loadDrugs(); loadAllDrugs() })
</script>

<template>
  <div class="drug-page">
    <!-- 顶部统计 -->
    <div class="stat-cards">
      <div class="stat-card" @click="stockFilter = ''">
        <div class="stat-value">{{ total }}</div>
        <div class="stat-label">药品总数</div>
      </div>
      <div class="stat-card clickable" :class="{ active: stockFilter === 'warning' }" @click="filterByStock('warning')">
        <div class="stat-value warning">{{ allDrugs.filter(d => d.availableStock > 0 && d.availableStock <= d.warningStock).length }}</div>
        <div class="stat-label">低库存 <span v-if="stockFilter === 'warning'" class="filter-badge">筛选中</span></div>
      </div>
      <div class="stat-card clickable" :class="{ active: stockFilter === 'danger' }" @click="filterByStock('danger')">
        <div class="stat-value danger">{{ allDrugs.filter(d => d.availableStock <= 0).length }}</div>
        <div class="stat-label">缺货 <span v-if="stockFilter === 'danger'" class="filter-badge">筛选中</span></div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input v-model="searchKeyword" placeholder="搜索药品名称、编码、厂家..." clearable style="width: 300px">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-radio-group v-model="drugTypeFilter" @change="handleTypeChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="ORAL">口服</el-radio-button>
          <el-radio-button value="INJECTION">注射</el-radio-button>
        </el-radio-group>
        <el-button type="primary" :icon="Plus" @click="visible = true">新增药品</el-button>
      </div>
    </el-card>

    <!-- 药品表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="getFilteredDrugs()" v-loading="loading" stripe size="default" style="width: 100%">
        <el-table-column prop="drugName" label="药品名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="specification" label="规格" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.drugType === 'INJECTION' ? 'warning' : 'success'" size="small" effect="plain">
              {{ row.drugType === 'INJECTION' ? '注射' : '口服' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="70" align="center" />
        <el-table-column label="单价" width="90" align="right">
          <template #default="{ row }">￥{{ Number(row.unitPrice ?? 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column label="可用" width="80" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.availableStock <= 0, 'text-warning': row.availableStock > 0 && row.availableStock <= row.warningStock }">
              {{ row.availableStock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="预警" width="100" align="center">
          <template #default="{ row }">
            <el-input-number
              v-model="row.warningStock"
              :min="0"
              size="small"
              controls-position="right"
              style="width: 90px;"
              @change="(val: number) => updateWarningStock(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.availableStock <= 0" type="danger" size="small">缺货</el-tag>
            <el-tag v-else-if="row.availableStock <= row.warningStock" type="warning" size="small">低库存</el-tag>
            <el-tag v-else type="success" size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>

  <!-- 新增药品弹窗 -->
  <FormDialog v-model="visible" title="新增药品">
    <el-form label-position="top">
      <div class="form-grid">
        <el-form-item label="药品名称"><el-input v-model="form.drugName" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="form.specification" /></el-form-item>
        <el-form-item label="厂家"><el-input v-model="form.manufacturer" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" /></el-form-item>
        <el-form-item label="药品类型">
          <el-select v-model="form.drugType" class="full-width">
            <el-option label="口服" value="ORAL" />
            <el-option label="注射" value="INJECTION" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" :step="0.5" class="full-width" />
        </el-form-item>
        <el-form-item label="初始库存"><el-input-number v-model="form.initialStock" :min="0" class="full-width" /></el-form-item>
        <el-form-item label="预警库存"><el-input-number v-model="form.warningStock" :min="0" class="full-width" /></el-form-item>
      </div>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>
  </FormDialog>
</template>

<style scoped>
.drug-page { display: flex; flex-direction: column; gap: 16px; }

.stat-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.stat-card { background: #fff; border-radius: 12px; padding: 20px 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); display: flex; flex-direction: column; align-items: center; }
.stat-card.clickable { cursor: pointer; transition: all 0.2s; }
.stat-card.clickable:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); transform: translateY(-2px); }
.stat-card.active { box-shadow: 0 0 0 2px var(--el-color-primary); }
.filter-badge { font-size: 11px; color: #409eff; margin-left: 4px; }
.stat-value { font-size: 32px; font-weight: 700; color: #303133; }
.stat-value.warning { color: #e6a23c; }
.stat-value.danger { color: #f56c6c; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

.filter-card :deep(.el-card__body) { padding: 16px 20px; }
.filter-bar { display: flex; align-items: center; gap: 16px; }
.filter-bar .el-radio-group { margin-left: 8px; }

.table-card :deep(.el-card__body) { padding: 0 20px 20px; }
.table-card :deep(.el-table) { margin: 0 -20px; width: calc(100% + 40px) !important; }

.text-danger { color: #f56c6c; font-weight: 600; }
.text-warning { color: #e6a23c; font-weight: 600; }

.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; padding-top: 16px; border-top: 1px solid #ebeef5; }

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 20px; }
</style>

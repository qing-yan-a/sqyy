<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { api } from '../api'
import FormDialog from '../components/FormDialog.vue'
import type { Drug, InventoryLog } from '../types'

const loading = ref(false)
const visible = ref(false)
const logs = ref<InventoryLog[]>([])
const drugSearchDisplay = ref('')
const page = ref(1)
const pageSize = ref(15)
const total = ref(0)
const logKeyword = ref('')
let logSearchTimer: ReturnType<typeof setTimeout> | null = null

const form = reactive({
  drugId: undefined as number | undefined,
  changeType: 'IN',
  quantity: 1,
  operatorName: '李药师',
  remark: '',
})

async function fetchDrugSuggestions(
  query: string,
  cb: (items: { value: string; drug: Drug }[]) => void
) {
  try {
    const res = await api.searchDrugs({ q: query ?? '', page: 1, size: 20 })
    cb(
      res.list.map((d) => ({
        value: `${d.drugName} / ${d.drugCode} / 实际${d.stock} / 可用${d.availableStock}`,
        drug: d,
      }))
    )
  } catch {
    cb([])
  }
}

function handleDrugSelect(item: { value: string; drug: Drug }) {
  form.drugId = item.drug.id
  drugSearchDisplay.value = item.value
}

async function loadData() {
  const q = logKeyword.value.trim()
  console.log('[InventorySearch] loadData called, q:', q, 'page:', page.value)
  loading.value = true
  try {
    const res = await api.searchInventoryLogs({
      page: page.value,
      size: pageSize.value,
      ...(q ? { q } : {}),
    })
    console.log('[InventorySearch] response:', res)
    logs.value = res.list
    total.value = res.total
  } catch (err) {
    console.error('[InventorySearch] error:', err)
  } finally {
    loading.value = false
  }
}

/** 关键词变化后防抖拉取；watch + flush:post 避免早于 DOM/模型同步 */
watch(
  logKeyword,
  () => {
    if (logSearchTimer) clearTimeout(logSearchTimer)
    logSearchTimer = setTimeout(() => {
      page.value = 1
      loadData()
    }, 300)
  },
  { flush: 'post' }
)

function searchNow() {
  console.log('[InventorySearch] searchNow called, keyword:', logKeyword.value)
  if (logSearchTimer) clearTimeout(logSearchTimer)
  page.value = 1
  loadData()
}

function handlePageChange(p: number) {
  page.value = p
  loadData()
}

function handleSizeChange(s: number) {
  pageSize.value = s
  page.value = 1
  loadData()
}

async function submit() {
  if (!form.drugId) {
    ElMessage.warning('请选择药品')
    return
  }
  try {
    await api.adjustStock(form)
    ElMessage.success('库存调整成功')
    visible.value = false
    form.drugId = undefined
    form.changeType = 'IN'
    form.quantity = 1
    form.operatorName = '李药师'
    form.remark = ''
    drugSearchDisplay.value = ''
    await loadData()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span>库存流水</span>
          <el-input
            v-model="logKeyword"
            :prefix-icon="Search"
            placeholder="搜索药品名称或编号（ES）"
            size="small"
            clearable
            style="width: 260px; margin-left: 16px"
            @keyup.enter="searchNow"
          />
          <el-button size="small" type="primary" plain style="margin-left: 8px" @click="searchNow">搜索</el-button>
        </div>
        <el-button type="primary" @click="visible = true">库存变更</el-button>
      </div>
    </template>

    <el-table :data="logs" v-loading="loading" border stripe>
      <el-table-column prop="drugName" label="药品名称" min-width="180" />
      <el-table-column prop="changeType" label="类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.changeType === 'IN' ? 'success' : 'danger'" size="small">
            {{ row.changeType === 'IN' ? '入库' : '出库' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80" align="center" />
      <el-table-column prop="beforeStock" label="变更前" width="80" align="center" />
      <el-table-column prop="afterStock" label="变更后" width="80" align="center" />
      <el-table-column prop="operatorName" label="操作人" width="100" />
      <el-table-column prop="remark" label="备注" min-width="180" />
      <el-table-column prop="createdAt" label="时间" min-width="180" />
    </el-table>

    <div class="pagination-bar">
      <span class="total-text">共 {{ total }} 条记录</span>
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

  <FormDialog v-model="visible" title="库存变更">
    <el-form label-position="top">
      <div class="form-grid">
        <el-form-item label="药品">
          <el-autocomplete
            v-model="drugSearchDisplay"
            :fetch-suggestions="fetchDrugSuggestions"
            placeholder="搜索药品名称或编号"
            value-key="value"
            class="full-width"
            @select="handleDrugSelect"
          />
        </el-form-item>
        <el-form-item label="变更类型">
          <el-select v-model="form.changeType">
            <el-option label="入库" value="IN" />
            <el-option label="出库" value="OUT" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.quantity" :min="1" class="full-width" /></el-form-item>
        <el-form-item label="操作人"><el-input v-model="form.operatorName" /></el-form-item>
      </div>
      <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false; drugSearchDisplay = ''; form.drugId = undefined">取消</el-button>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>
  </FormDialog>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
}
.pagination-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
}
.total-text {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
</style>

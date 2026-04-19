<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../../api'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

interface DoctorInfo {
  realName: string
  username: string
  department: string
}

const router = useRouter()
const submitting = ref(false)
const selectedDept = ref('')
const doctors = ref<DoctorInfo[]>([])
const loadingDoctors = ref(false)

// ===== 科室数据（写死） =====
interface DeptInfo {
  name: string
  icon: string
  desc: string
}

const departments: DeptInfo[] = [
  { name: '全科诊室', icon: '🩺', desc: '常见病、多发病的诊断与治疗，慢性病管理' },
  { name: '预防保健室', icon: '💉', desc: '疫苗接种、健康体检、儿童保健、妇女保健' },
  { name: '妇幼保健诊室', icon: '👶', desc: '孕产期保健、妇科常见病、儿童健康检查' },
]

const timeSlots = ['上午', '下午']

// ===== 流程状态：dept → doctor =====
type Step = 'dept' | 'doctor'
const currentStep = ref<Step>('dept')

const form = ref({
  doctorName: '',
  appointmentDate: '',
  timeSlot: '',
})

async function selectDept(dept: string) {
  selectedDept.value = dept
  form.value.doctorName = ''
  form.value.appointmentDate = ''
  form.value.timeSlot = ''
  currentStep.value = 'doctor'
  loadingDoctors.value = true
  try {
    const result = await api.getPatientDoctors(dept)
    doctors.value = result as unknown as DoctorInfo[]
  } catch {
    doctors.value = []
  } finally {
    loadingDoctors.value = false
  }
}

function backToDept() {
  currentStep.value = 'dept'
  selectedDept.value = ''
  form.value.doctorName = ''
}

async function handleSubmit() {
  if (!form.value.doctorName || !form.value.appointmentDate || !form.value.timeSlot) {
    ElMessage.warning('请填写完整挂号信息')
    return
  }
  submitting.value = true
  try {
    await api.patientSelfRegister({
      department: selectedDept.value,
      doctorName: form.value.doctorName,
      appointmentDate: form.value.appointmentDate,
      timeSlot: form.value.timeSlot,
    })
    ElMessage.success('挂号成功！')
    router.push('/user-center/records')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err?.message || '挂号失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="registration-view">
    <!-- ===== 选择科室 ===== -->
    <template v-if="currentStep === 'dept'">
      <h3 class="page-subtitle">请选择科室</h3>

      <div class="dept-list">
        <div
          v-for="dept in departments"
          :key="dept.name"
          class="dept-card"
          @click="selectDept(dept.name)"
        >
          <div class="dept-info">
            <div class="dept-name">{{ dept.name }}</div>
            <div class="dept-desc">{{ dept.desc }}</div>
          </div>
        </div>
      </div>
    </template>

    <!-- ===== 选择医生并挂号 ===== -->
    <template v-else-if="currentStep === 'doctor'">
      <div class="step-header">
        <el-button :icon="ArrowLeft" link @click="backToDept">返回选择科室</el-button>
        <h3>{{ selectedDept }}</h3>
      </div>

      <div v-loading="loadingDoctors">
        <el-empty v-if="doctors.length === 0 && !loadingDoctors" description="该科室暂无医生" />

        <template v-else>
          <div class="doctor-list">
            <div v-for="doc in doctors" :key="doc.username" class="doctor-card">
              <div class="doctor-info">
                <div class="doctor-name">{{ doc.realName }}</div>
                <div class="doctor-dept">{{ doc.department }}</div>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="register-form">
            <el-form label-position="top">
              <el-form-item label="选择医生" required>
                <el-select v-model="form.doctorName" placeholder="请选择医生" style="width: 100%">
                  <el-option v-for="doc in doctors" :key="doc.username" :label="doc.realName" :value="doc.realName" />
                </el-select>
              </el-form-item>
              <el-form-item label="预约日期" required>
                <el-date-picker
                  v-model="form.appointmentDate"
                  type="date"
                  placeholder="选择日期"
                  value-format="YYYY-MM-DD"
                  format="YYYY年MM月DD日"
                  :disabled-date="(d: Date) => d.getTime() < Date.now() - 86400000"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="时段" required>
                <el-radio-group v-model="form.timeSlot">
                  <el-radio-button v-for="slot in timeSlots" :key="slot" :value="slot">{{ slot }}</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-form>

            <el-button type="primary" size="large" :loading="submitting" style="width: 100%; margin-top: 12px" @click="handleSubmit">
              确认挂号
            </el-button>
          </div>
        </template>
      </div>
    </template>
  </div>
</template>

<style scoped>
.registration-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-subtitle {
  margin: 0;
  font-size: 16px;
  color: #6b7280;
  font-weight: 500;
}

/* 步骤头部 */
.step-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.step-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

/* 科室列表 — 竖排左对齐 */
.dept-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 600px;
}

.dept-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(229, 231, 235, 0.7);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.dept-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
  border-color: rgba(64, 158, 255, 0.3);
}

.dept-icon {
  font-size: 36px;
  flex-shrink: 0;
}

.dept-info {
  flex: 1;
}

.dept-name {
  font-size: 17px;
  font-weight: 600;
  color: #000;
}

.dept-desc {
  font-size: 13px;
  color: #000;
  margin-top: 4px;
}

/* 医生列表 — 竖排左对齐 */
.doctor-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 600px;
}

.doctor-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.doctor-info {
  flex: 1;
}

.doctor-name {
  font-size: 15px;
  font-weight: 600;
  color: #000;
}

.doctor-dept {
  font-size: 12px;
  color: #000;
  margin-top: 2px;
}

/* 挂号表单 */
.register-form {
  max-width: 500px;
}
</style>
e>

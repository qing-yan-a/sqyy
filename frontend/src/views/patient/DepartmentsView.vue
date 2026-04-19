<script setup lang="ts">
import { ref } from 'vue'

interface DoctorInfo {
  name: string
  title: string
  specialty: string
  detail: string
  avatar: string
  deptName: string
}

interface DeptInfo {
  name: string
  desc: string
  introduction: string
}

const departments: DeptInfo[] = [
  {
    name: '全科诊室',
    desc: '常见病、多发病的诊断与治疗，慢性病管理',
    introduction: '全科诊室是社区医院的核心科室，承担着社区居民常见病、多发病的首诊和全程管理工作。科室配备了先进的诊疗设备，拥有一支经验丰富的全科医疗团队。服务范围涵盖：呼吸系统疾病（感冒、肺炎、支气管炎等）、消化系统疾病（胃炎、腹泻、便秘等）、心脑血管疾病（高血压、冠心病等）、内分泌疾病（糖尿病、甲状腺疾病等）以及各类慢性病的长期管理和随访。科室推行"以患者为中心"的服务理念，为每位居民建立健康档案，提供个性化的健康管理方案。',
  },
  {
    name: '预防保健室',
    desc: '疫苗接种、健康体检、儿童保健、妇女保健',
    introduction: '预防保健室是社区公共卫生服务的重要窗口，承担着辖区居民的预防接种、健康体检、儿童保健和妇女保健等工作。科室严格执行国家免疫规划，提供从新生儿到老年人的全程疫苗接种服务。同时开展0-6岁儿童健康体检、生长发育监测、营养评估与指导，以及孕产妇系统管理、产后访视等服务。科室环境温馨舒适，设有独立的接种区、留观区和儿童活动区，让每一位来访者都能感受到贴心的服务。',
  },
  {
    name: '妇幼保健诊室',
    desc: '孕产期保健、妇科常见病、儿童健康检查',
    introduction: '妇幼保健诊室专注于妇女和儿童的健康保障，提供从孕前到产后、从新生儿到青春期的全周期健康管理服务。妇科方面：开展妇科常见病诊治、宫颈癌筛查（TCT+HPV检测）、妇科超声检查、计划生育技术服务等。产科方面：提供孕前优生咨询、孕期全程保健、高危妊娠管理、产后康复指导等服务。儿科方面：擅长儿童常见病诊治、新生儿护理指导、儿童营养评估与膳食指导，守护每一位孩子的健康成长。',
  },
]

const doctors: DoctorInfo[] = [
  { name: '张明远', title: '主治医师', specialty: '高血压、糖尿病等慢性病管理', detail: '毕业于首都医科大学临床医学系，从事全科医学工作15年。擅长高血压、糖尿病、高脂血症等慢性病的规范化管理与长期随访，对老年综合评估、多病共存患者的综合诊疗有丰富经验。曾获市级"优秀全科医生"称号。', avatar: '/doctors/1.webp', deptName: '全科诊室' },
  { name: '李晓华', title: '副主任医师', specialty: '呼吸系统疾病、消化系统疾病', detail: '医学硕士，副主任医师，从医20年。专注于呼吸系统疾病（肺炎、慢性阻塞性肺疾病、支气管哮喘）和消化系统疾病（胃炎、胃食管反流、功能性消化不良）的诊治，擅长胃肠镜检查及内镜下治疗。', avatar: '/doctors/2.webp', deptName: '全科诊室' },
  { name: '王建国', title: '主治医师', specialty: '心脑血管疾病、老年病', detail: '心血管内科硕士，主治医师。擅长冠心病、心律失常、脑卒中后康复管理，在老年心脑血管疾病的综合防治方面经验丰富。注重"防大于治"的理念，为每位患者制定个性化健康管理方案。', avatar: '/doctors/3.webp', deptName: '全科诊室' },
  { name: '陈雪梅', title: '主管护师', specialty: '儿童预防接种、生长发育评估', detail: '主管护师，预防接种工作12年。精通国家免疫规划疫苗和非免疫规划疫苗的接种程序，擅长儿童生长发育监测与评估、营养指导。工作细致耐心，深受家长信赖。', avatar: '/doctors/4.webp', deptName: '预防保健室' },
  { name: '刘芳', title: '副主任医师', specialty: '孕产妇保健、妇科检查', detail: '妇产科副主任医师，从业18年。擅长孕前优生咨询、孕期保健管理、产后康复指导，以及宫颈癌筛查（TCT+HPV）、妇科超声检查等。对高危妊娠的早期识别和转诊有丰富经验。', avatar: '/doctors/5.webp', deptName: '预防保健室' },
  { name: '赵雅琴', title: '副主任医师', specialty: '孕产期保健、高危妊娠管理', detail: '妇产科副主任医师，硕士学历，从医22年。擅长孕产期全程保健管理，对妊娠期糖尿病、妊娠期高血压、前置胎盘等高危妊娠的识别与管理有深入研究。发表学术论文十余篇。', avatar: '/doctors/6.webp', deptName: '妇幼保健诊室' },
  { name: '孙丽', title: '主治医师', specialty: '妇科炎症、月经不调', detail: '妇科主治医师，从事妇科临床工作10年。擅长各类妇科炎症（阴道炎、盆腔炎、宫颈炎）、月经不调、多囊卵巢综合征的诊治，以及计划生育技术服务。注重中西医结合治疗。', avatar: '/doctors/7.webp', deptName: '妇幼保健诊室' },
  { name: '周小燕', title: '主治医师', specialty: '儿童常见病、营养指导', detail: '儿科主治医师，毕业于华中科技大学同济医学院。擅长小儿呼吸系统疾病（上呼吸道感染、支气管炎、肺炎）、消化系统疾病（腹泻、便秘）的诊治，以及儿童营养评估与膳食指导。', avatar: '/doctors/8.webp', deptName: '妇幼保健诊室' },
]

const detailVisible = ref(false)
const selectedDoctor = ref<DoctorInfo | null>(null)

function showDetail(doc: DoctorInfo) {
  selectedDoctor.value = doc
  detailVisible.value = true
}
</script>

<template>
  <div class="dept-page">
    <!-- 左栏：医生列表 -->
    <div class="left-col">
      <h3 class="col-title">医生团队</h3>
      <div class="doctor-grid">
        <div v-for="doc in doctors" :key="doc.name" class="doctor-card" @click="showDetail(doc)">
          <el-avatar :size="56" :src="doc.avatar" shape="circle" />
          <div class="doctor-info">
            <div class="doctor-name">
              {{ doc.name }}
              <el-tag size="small" type="info">{{ doc.title }}</el-tag>
            </div>
            <div class="doctor-dept">{{ doc.deptName }}</div>
            <div class="doctor-specialty">{{ doc.specialty }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右栏：科室信息 -->
    <div class="right-col">
      <h3 class="col-title">科室介绍</h3>
      <div class="dept-list">
        <div v-for="dept in departments" :key="dept.name" class="dept-card">
          <div class="dept-header">
            <div class="dept-name">{{ dept.name }}</div>
            <div class="dept-desc">{{ dept.desc }}</div>
          </div>
          <div class="dept-intro">{{ dept.introduction }}</div>
        </div>
      </div>
    </div>

    <!-- 医生详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="selectedDoctor?.name + ' 医生'" width="520px" align-center>
      <div class="doctor-detail" v-if="selectedDoctor">
        <div class="detail-top">
          <el-avatar :size="100" :src="selectedDoctor.avatar" shape="circle" />
          <div class="detail-info">
            <div class="detail-name">{{ selectedDoctor.name }}</div>
            <el-tag type="info">{{ selectedDoctor.title }}</el-tag>
            <div class="detail-dept">{{ selectedDoctor.deptName }}</div>
            <div class="detail-specialty">擅长：{{ selectedDoctor.specialty }}</div>
          </div>
        </div>
        <el-divider />
        <div class="detail-section">
          <div class="detail-label">医生简介</div>
          <div class="detail-text">{{ selectedDoctor.detail }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.dept-page {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.col-title {
  margin: 0 0 16px;
  font-size: 17px;
  font-weight: 600;
  color: #1f2937;
}

.doctor-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.doctor-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(229, 231, 235, 0.7);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.doctor-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
  border-color: rgba(64, 158, 255, 0.3);
}

.doctor-info { flex: 1; }

.doctor-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  display: flex;
  align-items: center;
  gap: 8px;
}

.doctor-dept {
  font-size: 12px;
  color: #409eff;
  margin-top: 3px;
}

.doctor-specialty {
  font-size: 13px;
  color: #6b7280;
  margin-top: 2px;
}

.dept-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dept-card {
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(229, 231, 235, 0.7);
  border-radius: 12px;
  padding: 20px 24px;
}

.dept-header { margin-bottom: 14px; }

.dept-name {
  font-size: 17px;
  font-weight: 600;
  color: #111827;
}

.dept-desc {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 2px;
}

.dept-intro {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  text-indent: 2em;
}

.doctor-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-top {
  display: flex;
  gap: 20px;
  align-items: center;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-name {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}

.detail-dept {
  font-size: 14px;
  color: #409eff;
}

.detail-specialty {
  font-size: 14px;
  color: #6b7280;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-label {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.detail-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

@media (max-width: 900px) {
  .dept-page { grid-template-columns: 1fr; }
}
</style>

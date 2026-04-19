<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  gender: '男',
  age: 25,
  phone: '',
  idCard: '',
  address: '',
  allergyHistory: '',
  medicalHistory: '',
})

async function handleRegister() {
  if (!form.value.username || !form.value.password || !form.value.name || !form.value.phone || !form.value.idCard) {
    ElMessage.warning('请填写所有必填项（用户名、密码、姓名、手机号、身份证号）')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  if (form.value.password.length < 6) {
    ElMessage.warning('密码至少6位')
    return
  }

  loading.value = true
  try {
    await api.patientRegister({
      username: form.value.username,
      password: form.value.password,
      name: form.value.name,
      gender: form.value.gender,
      age: form.value.age,
      phone: form.value.phone,
      idCard: form.value.idCard,
      address: form.value.address,
      allergyHistory: form.value.allergyHistory,
      medicalHistory: form.value.medicalHistory,
    })
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } }
    ElMessage.error(err?.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <h2>🏥 患者注册</h2>
      <p class="subtitle">创建您的个人健康档案</p>

      <el-form label-position="top" :model="form">
        <el-divider content-position="left">账号信息</el-divider>
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" placeholder="用于登录" />
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="form.password" type="password" placeholder="至少6位" show-password />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password />
        </el-form-item>

        <el-divider content-position="left">个人信息</el-divider>
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio-button value="男">男</el-radio-button>
            <el-radio-button value="女">女</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="150" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="身份证号" required>
          <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="选填" />
        </el-form-item>

        <el-divider content-position="left">医疗信息</el-divider>
        <el-form-item label="过敏史">
          <el-input v-model="form.allergyHistory" type="textarea" :rows="2" placeholder="如无过敏可不填" />
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input v-model="form.medicalHistory" type="textarea" :rows="2" placeholder="如无可不填" />
        </el-form-item>
      </el-form>

      <el-button type="primary" size="large" :loading="loading" style="width: 100%; margin-top: 16px" @click="handleRegister">
        注册
      </el-button>

      <div class="login-link">
        已有账号？<router-link to="/login">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 450px;
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.register-card h2 {
  text-align: center;
  margin-bottom: 4px;
  color: #303133;
}

.subtitle {
  text-align: center;
  color: #909399;
  margin-bottom: 24px;
  font-size: 14px;
}

.login-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #909399;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}
</style>

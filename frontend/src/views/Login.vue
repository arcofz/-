<template>
  <div class="page">
    <el-card shadow="never" class="auth-card">
      <h1 class="auth-title">{{ tab === 'login' ? '登录' : '注册' }}</h1>
      <el-tabs v-model="tab" stretch>
        <el-tab-pane label="登录" name="login" />
        <el-tab-pane label="注册" name="register" />
      </el-tabs>

      <!-- 登录 -->
      <el-form v-if="tab === 'login'" ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0" @keyup.enter="onLogin">
        <el-form-item prop="studentNo">
          <el-input v-model="loginForm.studentNo" placeholder="学号" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="submitting" @click="onLogin">登录</el-button>
      </el-form>

      <!-- 注册 -->
      <el-form v-else ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="0">
        <el-form-item prop="studentNo">
          <el-input v-model="registerForm.studentNo" placeholder="学号" size="large" />
        </el-form-item>
        <el-form-item prop="name">
          <el-input v-model="registerForm.name" placeholder="姓名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" size="large" show-password />
        </el-form-item>
        <el-form-item prop="contact">
          <el-input v-model="registerForm.contact" placeholder="联系方式（选填，用于交接联系）" size="large" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="submitting" @click="onRegister">注册</el-button>
      </el-form>

      <p class="mock-tip" v-if="isMock">当前为演示数据：学生 2021001 / 2021002 / 2021003，管理员 admin，密码均为 123456</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const isMock = import.meta.env.VITE_USE_MOCK === 'true'

const tab = ref('login')
const submitting = ref(false)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({ studentNo: '', password: '' })
const registerForm = reactive({ studentNo: '', name: '', password: '', confirmPassword: '', contact: '' })

const loginRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        value === registerForm.password ? callback() : callback(new Error('两次输入的密码不一致'))
      },
      trigger: 'blur'
    }
  ]
}

const afterLogin = () => {
  const redirect = route.query.redirect
  router.push(typeof redirect === 'string' && redirect.startsWith('/') ? redirect : auth.isAdmin ? '/admin' : '/')
}

const onLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await auth.login({ studentNo: loginForm.studentNo, password: loginForm.password })
      ElMessage.success('登录成功')
      afterLogin()
    } catch (e) {
      ElMessage.error(e.message || '登录失败')
    } finally {
      submitting.value = false
    }
  })
}

const onRegister = () => {
  registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await auth.register({
        studentNo: registerForm.studentNo,
        name: registerForm.name,
        password: registerForm.password,
        contact: registerForm.contact || undefined
      })
      ElMessage.success('注册成功，请登录')
      loginForm.studentNo = registerForm.studentNo
      loginForm.password = ''
      tab.value = 'login'
    } catch (e) {
      ElMessage.error(e.message || '注册失败')
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.auth-title {
  text-align: center;
  font-size: 22px;
  margin: 8px 0 4px;
}

.submit-btn {
  width: 100%;
}

.mock-tip {
  margin-top: 12px;
  font-size: 12px;
  color: #8a94a3;
  text-align: center;
}
</style>

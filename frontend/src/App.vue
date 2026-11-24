<template>
  <el-container class="app">
    <el-header class="topbar" height="56px">
      <router-link to="/" class="brand">寻物雷达</router-link>
      <nav class="nav">
        <router-link to="/">首页</router-link>
        <router-link to="/items">失物列表</router-link>
        <router-link to="/publish">发布</router-link>
        <router-link v-if="auth.isLoggedIn" to="/claims">我的认领</router-link>
        <router-link v-if="auth.isAdmin" to="/admin">后台</router-link>
      </nav>
      <div class="user-area">
        <el-badge v-if="auth.isLoggedIn" :value="notifyStore.unread" :hidden="notifyStore.unread === 0" class="bell">
          <el-icon class="bell-icon" @click="$router.push('/notifications')"><Bell /></el-icon>
        </el-badge>
        <el-dropdown v-if="auth.isLoggedIn" @command="onCommand">
          <span class="user-name">{{ auth.user?.name || auth.user?.studentNo || '我' }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>
                {{ auth.user?.studentNo }}<template v-if="auth.user?.role === 'admin'"> · 管理员</template>
              </el-dropdown-item>
              <el-dropdown-item command="claims">我的认领</el-dropdown-item>
              <el-dropdown-item command="notifications">通知</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <router-link v-else to="/login" class="login-link">登录 / 注册</router-link>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { useAuthStore } from './stores/auth'
import { useNotificationStore } from './stores/notifications'

const router = useRouter()
const auth = useAuthStore()
const notifyStore = useNotificationStore()

const onCommand = (cmd) => {
  if (cmd === 'logout') {
    auth.logout()
    notifyStore.unread = 0
    router.push('/')
  } else {
    router.push(`/${cmd}`)
  }
}

watch(
  () => auth.token,
  () => notifyStore.refresh()
)

onMounted(() => {
  notifyStore.refresh()
})
</script>

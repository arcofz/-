<template>
  <div class="page">
    <div class="head">
      <h1>通知</h1>
      <span class="muted">共 {{ list.length }} 条，未读 {{ unreadCount }} 条</span>
    </div>

    <div v-loading="loading" class="notice-list">
      <el-card v-for="n in list" :key="n.id" shadow="never" class="notice-card" @click="onRead(n)">
        <div class="notice-row" :class="{ unread: !n.isRead }">
          <div class="notice-main">
            <div class="notice-top">
              <el-tag size="small" :type="typeTag(n.type)" effect="light" round>{{ typeLabel(n.type) }}</el-tag>
              <span class="time">{{ formatTime(n.createdAt) }}</span>
            </div>
            <p class="content">{{ n.content }}</p>
          </div>
          <el-icon v-if="!n.isRead" class="dot"><CircleCloseFilled /></el-icon>
        </div>
      </el-card>
    </div>
    <el-empty v-if="!loading && list.length === 0" description="暂无通知" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { getNotifications, readNotification } from '../api/notifications'
import { useNotificationStore } from '../stores/notifications'
import { NOTIFICATION_TYPES } from '../constants/options'
import { formatTime } from '../utils/format'

const loading = ref(false)
const list = ref([])
const notifyStore = useNotificationStore()

const unreadCount = computed(() => list.value.filter((n) => !n.isRead).length)

const typeLabel = (type) => NOTIFICATION_TYPES[type] || '通知'
const typeTag = (type) => ({ match: 'primary', claim: 'warning', review: 'success', handover: 'success', system: 'info' }[type] || 'info')

const fetchList = async () => {
  loading.value = true
  try {
    list.value = await getNotifications()
    notifyStore.refresh()
  } finally {
    loading.value = false
  }
}

const onRead = async (n) => {
  if (n.isRead) return
  try {
    await readNotification(n.id)
    // 用索引整体替换元素，避免 Vue 3.5 reactive 数组读方法返回 raw 元素导致视图不更新
    const idx = list.value.findIndex((x) => x.id === n.id)
    if (idx !== -1) list.value[idx] = { ...list.value[idx], isRead: true }
    notifyStore.refresh()
  } catch (e) {
    // 标记失败不打断浏览
  }
}

onMounted(fetchList)
</script>

<style scoped>
.head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 200px;
}

.notice-card {
  border-radius: 10px;
  cursor: pointer;
}

.notice-row {
  border-left: 3px solid transparent;
  padding-left: 8px;
}

.notice-row.unread {
  border-left: 3px solid #409eff;
}

.notice-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-main {
  flex: 1;
}

.notice-top {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time {
  font-size: 12px;
  color: #8a94a3;
}

.content {
  margin: 8px 0 0;
  font-size: 14px;
  color: #252d38;
  line-height: 1.6;
}

.dot {
  color: #409eff;
  font-size: 16px;
  flex-shrink: 0;
}
</style>

<template>
  <div class="page">
    <h1>后台管理</h1>
    <el-tabs v-model="tab">
      <el-tab-pane label="统计" name="stats" />
      <el-tab-pane label="用户" name="users" />
      <el-tab-pane label="分类" name="categories" />
      <el-tab-pane label="地点" name="locations" />
    </el-tabs>

    <!-- 统计 -->
    <div v-if="tab === 'stats'" v-loading="statsLoading">
      <el-row :gutter="16" class="stats-row">
        <el-col v-for="(entry, index) in statEntries" :key="entry.key" :xs="12" :sm="8" :md="6">
          <el-card shadow="hover" class="stat-card" :class="`tone-${index % 4}`">
            <div class="stat-label">{{ entry.label }}</div>
            <div class="stat-value">{{ entry.value }}</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 用户 -->
    <el-table v-else-if="tab === 'users'" v-loading="usersLoading" :data="users" empty-text="暂无用户">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="studentNo" label="学号" width="140" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'info'" size="small">{{ row.role === 'admin' ? '管理员' : '学生' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="contact" label="联系方式" min-width="140" />
    </el-table>

    <!-- 分类 -->
    <el-table v-else-if="tab === 'categories'" v-loading="categoriesLoading" :data="categories" empty-text="暂无分类">
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column prop="name" label="分类名" />
    </el-table>

    <!-- 地点 -->
    <el-table v-else v-loading="locationsLoading" :data="locations" empty-text="暂无地点">
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column prop="building" label="楼栋" />
      <el-table-column prop="floor" label="楼层" width="120">
        <template #default="{ row }">{{ row.floor || '-' }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { getAdminUsers, getAdminCategories, getAdminLocations, getAdminStats } from '../api/admin'

const tab = ref('stats')

const stats = ref(null)
const statsLoading = ref(false)
const users = ref([])
const usersLoading = ref(false)
const categories = ref([])
const categoriesLoading = ref(false)
const locations = ref([])
const locationsLoading = ref(false)

// docs/api.md 未细化统计字段，这里按语义映射，未识别的字段原样展示
const STAT_LABELS = {
  totalUsers: '用户总数',
  totalItems: '物品总数',
  lostCount: '失物数',
  foundCount: '拾物数',
  pendingClaims: '待审核认领',
  totalMatches: '匹配总数',
  confirmedMatches: '已确认匹配',
  completedItems: '已完成交接'
}

const statEntries = computed(() => {
  if (!stats.value) return []
  if (Array.isArray(stats.value)) {
    return stats.value.map((s, i) => ({ key: `stat-${i}`, label: s.label || s.name || `指标${i + 1}`, value: s.value ?? s.count ?? '-' }))
  }
  return Object.entries(stats.value).map(([key, value]) => ({ key, label: STAT_LABELS[key] || key, value }))
})

const loaders = {
  stats: async () => {
    statsLoading.value = true
    try {
      stats.value = await getAdminStats()
    } finally {
      statsLoading.value = false
    }
  },
  users: async () => {
    usersLoading.value = true
    try {
      users.value = await getAdminUsers()
    } finally {
      usersLoading.value = false
    }
  },
  categories: async () => {
    categoriesLoading.value = true
    try {
      categories.value = await getAdminCategories()
    } finally {
      categoriesLoading.value = false
    }
  },
  locations: async () => {
    locationsLoading.value = true
    try {
      locations.value = await getAdminLocations()
    } finally {
      locationsLoading.value = false
    }
  }
}

watch(tab, (name) => {
  if (!loaded.has(name)) {
    loaded.add(name)
    loaders[name]?.()
  }
})

const loaded = new Set()
onMounted(() => {
  loaders.stats()
  loaded.add('stats')
})
</script>

<style scoped>
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 16px;
  text-align: center;
  border-radius: 10px;
}

.stat-label {
  font-size: 13px;
  color: #8a94a3;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: #182c44;
  margin-top: 6px;
}

.tone-0 .stat-value { color: #3b82f6; }
.tone-1 .stat-value { color: #10b981; }
.tone-2 .stat-value { color: #f59e0b; }
.tone-3 .stat-value { color: #8b5cf6; }
</style>

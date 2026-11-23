<template>
  <div class="page">
    <div class="hero">
      <h1>寻物雷达</h1>
      <p class="muted">校园失物智能匹配与认领平台 · 发布 → 匹配 → 通知 → 认领审核 → 交接归档</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="$router.push('/publish')">发布信息</el-button>
        <el-button size="large" @click="$router.push('/items')">浏览列表</el-button>
      </div>
    </div>

    <el-steps :active="0" simple class="flow-steps">
      <el-step title="发布" />
      <el-step title="匹配" />
      <el-step title="通知" />
      <el-step title="认领审核" />
      <el-step title="交接归档" />
    </el-steps>

    <div class="section-head">
      <h2>最新发布</h2>
      <el-button text type="primary" @click="$router.push('/items')">查看全部</el-button>
    </div>

    <div v-loading="loading" class="item-grid">
      <ItemCard v-for="item in recentItems" :key="item.id" :item="item" />
    </div>
    <el-empty v-if="!loading && recentItems.length === 0" description="还没有发布记录，来发布第一条吧" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getItems } from '../api/items'
import ItemCard from '../components/ItemCard.vue'

const loading = ref(false)
const recentItems = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const { list } = await getItems({ page: 1, pageSize: 6 })
    recentItems.value = list
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 40px 0 16px;
}

.hero h1 {
  font-size: 32px;
  margin-bottom: 8px;
}

.hero-actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 12px;
}

.flow-steps {
  margin: 24px 0 32px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-head h2 {
  font-size: 18px;
  color: #182c44;
  margin: 0;
}
</style>

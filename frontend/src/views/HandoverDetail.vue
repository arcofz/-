<template>
  <div class="page">
    <h1>交接详情</h1>
    <el-card v-if="handover" shadow="never" class="detail-card">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="物品">
          <span v-if="handover.itemTitle">{{ handover.itemTitle }}</span>
          <StatusTag v-if="handover.itemType" dict="itemType" :value="handover.itemType" class="inline-tag" />
        </el-descriptions-item>
        <el-descriptions-item label="发布者">{{ handover.ownerName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="认领人">{{ handover.claimantName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="交接时间">{{ formatTime(handover.handTime) }}</el-descriptions-item>
        <el-descriptions-item label="交接地点">{{ handover.place }}</el-descriptions-item>
        <el-descriptions-item label="登记时间">{{ formatTime(handover.createdAt) }}</el-descriptions-item>
      </el-descriptions>
      <p class="muted tip">交接完成后，物品已归档为「已完成」。如有问题请联系管理员。</p>
    </el-card>
    <el-empty v-else-if="!loading" description="交接记录不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getHandover } from '../api/handovers'
import { formatTime } from '../utils/format'
import StatusTag from '../components/StatusTag.vue'

const route = useRoute()
const loading = ref(false)
const handover = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    handover.value = await getHandover(route.params.id)
  } catch (e) {
    // 空状态兜底
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-card {
  max-width: 560px;
  border-radius: 10px;
}

.inline-tag {
  margin-left: 6px;
}

.tip {
  margin-top: 16px;
  font-size: 13px;
}
</style>

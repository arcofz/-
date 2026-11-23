<template>
  <div class="page">
    <div class="head">
      <h1>匹配推荐</h1>
      <el-button text @click="backToList">返回列表</el-button>
    </div>

    <el-card v-if="item" shadow="never" class="source-card">
      <div class="source">
        <StatusTag dict="itemType" :value="item.type" />
        <span class="source-title">{{ item.title }}</span>
        <span class="muted">{{ item.description || '' }}</span>
      </div>
    </el-card>

    <div v-loading="loading" class="match-list">
      <el-card v-for="m in matches" :key="m.id" shadow="never" class="match-card">
        <div class="match-row">
          <div class="score-block">
            <div class="score">{{ m.score }}</div>
            <div class="score-label">匹配度</div>
          </div>
          <div class="match-main">
            <div class="match-title-row">
              <span class="match-title">{{ m.otherTitle || `物品 #${m.otherId}` }}</span>
              <StatusTag dict="matchStatus" :value="m.status" />
            </div>
            <p class="muted match-desc">{{ m.otherDescription || '' }}</p>
            <div class="reason">
              <el-icon class="reason-icon"><InfoFilled /></el-icon>
              <span>{{ reasonText(m) }}</span>
            </div>
          </div>
          <div class="match-actions" v-if="canOperate">
            <template v-if="m.status === 'pending'">
              <el-button type="success" size="small" @click="onAction(m, 'confirm')">确认匹配</el-button>
              <el-button type="danger" size="small" plain @click="onAction(m, 'reject')">拒绝</el-button>
            </template>
            <el-button v-else text type="primary" size="small" @click="goDetail(m)">查看物品</el-button>
          </div>
          <div class="match-actions" v-else>
            <el-button text type="primary" size="small" @click="goDetail(m)">查看物品</el-button>
          </div>
        </div>
        <el-progress :percentage="Math.min(100, Math.round(m.score))" :stroke-width="8" class="score-bar" :color="scoreColor(m.score)" />
      </el-card>
    </div>

    <el-empty v-if="!loading && matches.length === 0" description="暂无匹配结果，可能物品特征较特殊或对应物品尚未发布" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { getItem } from '../api/items'
import { getMatches, confirmMatch, rejectMatch } from '../api/matches'
import { useAuthStore } from '../stores/auth'
import StatusTag from '../components/StatusTag.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const itemId = route.params.id
const item = ref(null)
const matches = ref([])
const loading = ref(false)

const canOperate = computed(() => auth.isLoggedIn && item.value && Number(item.value.userId) === Number(auth.user.id))

const reasonText = (m) => {
  if (m.reason) return m.reason
  if (Array.isArray(m.reasons)) return m.reasons.join('；')
  return ''
}

const scoreColor = (score) => (score >= 80 ? '#67c23a' : score >= 60 ? '#e6a23c' : '#909399')

const goDetail = (m) => {
  router.push(`/items/${m.otherId}`)
}

const fetchAll = async () => {
  loading.value = true
  try {
    const [itemData, matchList] = await Promise.all([getItem(itemId), getMatches(itemId)])
    item.value = itemData
    // 兼容后端直接内嵌对方物品信息（otherItem）或只返回 ID 的形态
    const base = (matchList || []).map((m) => {
      const otherId = m.otherItem?.id ?? (Number(m.lostItemId) === Number(itemId) ? m.foundItemId : m.lostItemId)
      return {
        ...m,
        otherId,
        otherTitle: m.otherItem?.title || m.otherTitle || m.opponentTitle || '',
        otherDescription: m.otherItem?.description || m.otherDescription || ''
      }
    })
    // 只返回 ID 时补拉对方物品详情用于展示
    await Promise.all(
      base.map(async (m) => {
        if (m.otherTitle) return
        try {
          const other = await getItem(m.otherId)
          m.otherTitle = other.title
          m.otherDescription = other.description
        } catch (e) {
          // 拉取失败仍展示物品 ID
        }
      })
    )
    matches.value = base
  } finally {
    loading.value = false
  }
}

const onAction = (m, action) => {
  const isConfirm = action === 'confirm'
  ElMessageBox.confirm(
    isConfirm ? '确认后双方物品将标记为「已匹配」，并通知对方，确定吗？' : '拒绝后该匹配不再推荐，确定吗？',
    isConfirm ? '确认匹配' : '拒绝匹配',
    { confirmButtonText: isConfirm ? '确认' : '拒绝', cancelButtonText: '取消', type: isConfirm ? 'success' : 'warning' }
  )
    .then(async () => {
      try {
        await (isConfirm ? confirmMatch(m.id) : rejectMatch(m.id))
        ElMessage.success(isConfirm ? '已确认匹配' : '已拒绝')
        fetchAll()
      } catch (e) {
        ElMessage.error(e.message || '操作失败')
      }
    })
    .catch(() => {})
}

onMounted(fetchAll)
</script>

<style scoped>
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.source-card {
  margin-bottom: 16px;
  border-radius: 10px;
}

.source {
  display: flex;
  align-items: center;
  gap: 10px;
}

.source-title {
  font-weight: 600;
}

.match-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 200px;
}

.match-card {
  border-radius: 10px;
}

.match-row {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  flex-wrap: wrap;
}

.score-block {
  width: 72px;
  text-align: center;
  flex-shrink: 0;
}

.score {
  font-size: 28px;
  font-weight: 700;
  color: #182c44;
}

.score-label {
  font-size: 12px;
  color: #8a94a3;
}

.match-main {
  flex: 1;
  min-width: 260px;
}

.match-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.match-title {
  font-weight: 600;
  font-size: 15px;
}

.match-desc {
  margin: 6px 0;
  font-size: 13px;
}

.reason {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 13px;
  color: #4a5568;
  background: #f0f7ff;
  border-radius: 6px;
  padding: 6px 10px;
}

.reason-icon {
  color: #409eff;
  margin-top: 2px;
}

.match-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.score-bar {
  margin-top: 12px;
}
</style>

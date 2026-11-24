<template>
  <div class="page">
    <div v-loading="loading">
      <template v-if="item">
        <el-card shadow="never" class="detail-card">
          <div class="detail-layout">
            <div class="detail-cover">
              <el-image v-if="item.imageUrl" :src="item.imageUrl" fit="cover" class="cover-img">
                <template #error>
                  <div class="cover-empty"><el-icon><Picture /></el-icon></div>
                </template>
              </el-image>
              <div v-else class="cover-empty"><el-icon><Picture /></el-icon></div>
            </div>
            <div class="detail-body">
              <div class="title-row">
                <h1>{{ item.title }}</h1>
                <div class="tags">
                  <StatusTag dict="itemType" :value="item.type" />
                  <StatusTag dict="itemStatus" :value="item.status" />
                </div>
              </div>
              <el-descriptions :column="1" class="info">
                <el-descriptions-item label="分类">{{ item.categoryName || categoryName(item.categoryId) || '未分类' }}</el-descriptions-item>
                <el-descriptions-item label="地点">{{ item.locationName || locationName(item.locationId) || '未知地点' }}</el-descriptions-item>
                <el-descriptions-item label="时间">{{ formatTime(item.eventTime) }}</el-descriptions-item>
                <el-descriptions-item label="发布者">{{ item.publisherName || '匿名' }}</el-descriptions-item>
                <el-descriptions-item v-if="auth.isLoggedIn && item.publisherContact" label="联系方式">{{ item.publisherContact }}</el-descriptions-item>
                <el-descriptions-item label="发布时间">{{ formatTime(item.createdAt) }}</el-descriptions-item>
              </el-descriptions>
              <div class="desc">
                <div class="desc-label">详细描述</div>
                <p class="pre-wrap desc-text">{{ item.description || '暂无描述' }}</p>
              </div>
              <div class="actions">
                <template v-if="isOwner">
                  <el-button type="primary" @click="$router.push(`/items/${item.id}/edit`)">编辑</el-button>
                  <el-button type="danger" plain @click="onDelete">删除</el-button>
                </template>
                <el-button type="warning" plain @click="$router.push(`/matches/${item.id}`)">匹配推荐</el-button>
                <el-button
                  v-if="!isOwner && auth.isLoggedIn && item.type === 'found' && ['pending', 'matched', 'claimed'].includes(item.status)"
                  type="success"
                  :disabled="claimed"
                  @click="claimDialogVisible = true"
                >
                  {{ claimed ? '已申请' : '我要认领' }}
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </template>
      <el-empty v-else-if="!loading" description="物品不存在或已删除" />
    </div>

    <el-dialog v-model="claimDialogVisible" title="提交认领申请" width="480px">
      <p class="muted">请描述能证明物品归属的证据（特征、票据、证件信息等），发布者审核通过后即可交接。</p>
      <el-input v-model="claimEvidence" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="例如：伞柄有蓝色挂绳，是周四下午丢的" />
      <template #footer>
        <el-button @click="claimDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="claimSubmitting" @click="onClaim">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getItem, deleteItem } from '../api/items'
import { createClaim, getMyClaims } from '../api/claims'
import { useAuthStore } from '../stores/auth'
import { categoryName, locationName } from '../constants/options'
import { formatTime } from '../utils/format'
import StatusTag from '../components/StatusTag.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const loading = ref(false)
const item = ref(null)
const claimDialogVisible = ref(false)
const claimEvidence = ref('')
const claimSubmitting = ref(false)
const claimed = ref(false)

const isOwner = computed(() => auth.user && item.value && Number(item.value.userId) === Number(auth.user.id))

const fetchDetail = async () => {
  loading.value = true
  item.value = null
  claimed.value = false
  try {
    item.value = await getItem(route.params.id)
    // 进页面时按 itemId 查询本人认领记录，避免离开页面后 claimed 状态丢失
    if (auth.isLoggedIn && item.value.type === 'found') {
      try {
        const claims = await getMyClaims()
        claimed.value = claims.some(
          (c) => Number(c.itemId) === Number(item.value.id) && Number(c.claimantId) === Number(auth.user?.id)
        )
      } catch (e) {
        // 查询失败不影响详情展示，提交时仍会由 409 兜底
      }
    }
  } catch (e) {
    // 404 等错误由空状态兜底
  } finally {
    loading.value = false
  }
}

const onDelete = () => {
  ElMessageBox.confirm('删除后无法恢复，确定删除这条信息吗？', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deleteItem(item.value.id)
      ElMessage.success('已删除')
      router.push('/items')
    })
    .catch(() => {})
}

const onClaim = async () => {
  if (!claimEvidence.value.trim()) {
    ElMessage.warning('请填写认领证据')
    return
  }
  claimSubmitting.value = true
  try {
    await createClaim({ itemId: item.value.id, evidence: claimEvidence.value.trim() })
    claimed.value = true
    ElMessage.success('认领申请已提交，等待发布者审核')
    claimDialogVisible.value = false
    claimEvidence.value = ''
  } catch (e) {
    // 409：该物品已有本人的认领申请，同步按钮状态防止重复提交
    if (e.code === 409) {
      claimed.value = true
      ElMessage.info(e.message || '你已经提交过认领申请')
    } else {
      ElMessage.error(e.message || '提交失败')
    }
  } finally {
    claimSubmitting.value = false
  }
}

onMounted(fetchDetail)
watch(() => route.params.id, fetchDetail)
</script>

<style scoped>
.detail-card {
  border-radius: 10px;
}

.detail-layout {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.detail-cover {
  width: 320px;
  height: 240px;
  border-radius: 8px;
  overflow: hidden;
  background: #eef1f5;
  flex-shrink: 0;
}

.cover-img {
  width: 100%;
  height: 100%;
}

.cover-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #b3bac4;
  font-size: 48px;
}

.detail-body {
  flex: 1;
  min-width: 300px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.title-row h1 {
  margin: 0;
  font-size: 22px;
}

.tags {
  display: flex;
  gap: 6px;
}

.info {
  margin-top: 12px;
}

.desc {
  margin-top: 12px;
}

.desc-label {
  font-weight: 600;
  font-size: 14px;
  color: #182c44;
  margin-bottom: 6px;
}

.desc-text {
  color: #4a5568;
  line-height: 1.7;
  margin: 0;
}

.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>

<template>
  <div class="page">
    <h1>我的认领</h1>
    <el-tabs v-model="tab">
      <el-tab-pane label="我发起的认领" name="mine" />
      <el-tab-pane :label="`待我审核 (${reviewCount})`" name="review" />
    </el-tabs>

    <el-table v-loading="loading" :data="visibleList" empty-text="暂无记录">
      <el-table-column prop="itemTitle" label="物品" min-width="140" show-overflow-tooltip />
      <el-table-column label="类型" width="80">
        <template #default="{ row }"><StatusTag dict="itemType" :value="row.itemType" /></template>
      </el-table-column>
      <el-table-column :label="tab === 'mine' ? '发布者' : '申请人'" width="100">
        <template #default="{ row }">{{ tab === 'mine' ? row.ownerName : row.claimantName }}</template>
      </el-table-column>
      <el-table-column label="证据" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">{{ row.evidence }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><StatusTag dict="claimStatus" :value="row.status" /></template>
      </el-table-column>
      <el-table-column label="提交时间" width="150">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="tab === 'review' && row.status === 'pending'">
            <el-button size="small" type="success" @click="openApprove(row)">通过</el-button>
            <el-button size="small" type="danger" plain @click="onReject(row)">驳回</el-button>
          </template>
          <template v-else-if="tab === 'review' && row.status === 'approved' && !row.handoverId">
            <el-button size="small" type="primary" @click="openHandover(row)">登记交接</el-button>
          </template>
          <template v-else>
            <el-button
              v-if="row.handoverId"
              size="small"
              text
              type="primary"
              @click="$router.push(`/handovers/${row.handoverId}`)"
            >
              交接详情
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 审核通过：确认 + 可选的交接信息 -->
    <el-dialog v-model="approveVisible" title="通过认领申请" width="480px">
      <p class="muted">通过后系统会通知申请人。若已约定交接，可同时登记交接信息，物品将标记为「已完成」。</p>
      <el-form label-width="90px">
        <el-form-item label="交接时间">
          <el-date-picker v-model="approveForm.handTime" type="datetime" placeholder="选填，先通过后再登记" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss+08:00" />
        </el-form-item>
        <el-form-item label="交接地点">
          <el-input v-model="approveForm.place" placeholder="选填，例如：图书馆一楼服务台" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" :loading="acting" @click="onApprove">确认通过</el-button>
      </template>
    </el-dialog>

    <!-- 单独登记交接 -->
    <el-dialog v-model="handoverVisible" title="登记交接" width="480px">
      <el-form label-width="90px">
        <el-form-item label="交接时间" required>
          <el-date-picker v-model="handoverForm.handTime" type="datetime" placeholder="实际交接时间" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss+08:00" />
        </el-form-item>
        <el-form-item label="交接地点" required>
          <el-input v-model="handoverForm.place" placeholder="例如：图书馆一楼服务台" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handoverVisible = false">取消</el-button>
        <el-button type="primary" :loading="acting" @click="onHandover">登记</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyClaims, approveClaim, rejectClaim } from '../api/claims'
import { createHandover } from '../api/handovers'
import { useAuthStore } from '../stores/auth'
import { formatTime } from '../utils/format'
import StatusTag from '../components/StatusTag.vue'

const auth = useAuthStore()

const tab = ref('mine')
const loading = ref(false)
const allClaims = ref([])
const acting = ref(false)

const approveVisible = ref(false)
const handoverVisible = ref(false)
const currentRow = ref(null)
const approveForm = reactive({ handTime: '', place: '' })
const handoverForm = reactive({ handTime: '', place: '' })

// 我发起的：我是申请人；待我审核：我是物品发布者
const mineClaims = computed(() => allClaims.value.filter((c) => Number(c.claimantId) === Number(auth.user?.id)))
const reviewClaims = computed(() => allClaims.value.filter((c) => Number(c.ownerId) === Number(auth.user?.id)))
const reviewCount = computed(() => reviewClaims.value.filter((c) => c.status === 'pending').length)
const visibleList = computed(() => (tab.value === 'mine' ? mineClaims.value : reviewClaims.value))

const fetchClaims = async () => {
  loading.value = true
  try {
    allClaims.value = await getMyClaims()
  } finally {
    loading.value = false
  }
}

const openApprove = (row) => {
  currentRow.value = row
  approveForm.handTime = ''
  approveForm.place = ''
  approveVisible.value = true
}

const onApprove = async () => {
  acting.value = true
  try {
    await approveClaim(currentRow.value.id)
    // 若同时填了交接信息，通过后直接登记交接
    if (approveForm.handTime && approveForm.place) {
      await createHandover({ claimId: currentRow.value.id, handTime: approveForm.handTime, place: approveForm.place })
      ElMessage.success('已通过并登记交接')
    } else {
      ElMessage.success('已通过')
    }
    approveVisible.value = false
    fetchClaims()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    acting.value = false
  }
}

const onReject = (row) => {
  ElMessageBox.confirm('驳回后申请人会收到通知，确定驳回吗？', '驳回认领', {
    confirmButtonText: '驳回',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await rejectClaim(row.id)
        ElMessage.success('已驳回')
        fetchClaims()
      } catch (e) {
        ElMessage.error(e.message || '操作失败')
      }
    })
    .catch(() => {})
}

const openHandover = (row) => {
  currentRow.value = row
  handoverForm.handTime = ''
  handoverForm.place = ''
  handoverVisible.value = true
}

const onHandover = async () => {
  if (!handoverForm.handTime || !handoverForm.place) {
    ElMessage.warning('请填写交接时间和地点')
    return
  }
  acting.value = true
  try {
    await createHandover({ claimId: currentRow.value.id, handTime: handoverForm.handTime, place: handoverForm.place })
    ElMessage.success('交接已登记，物品已归档')
    handoverVisible.value = false
    fetchClaims()
  } catch (e) {
    ElMessage.error(e.message || '登记失败')
  } finally {
    acting.value = false
  }
}

onMounted(fetchClaims)
</script>

<style scoped>
.muted {
  font-size: 13px;
}
</style>

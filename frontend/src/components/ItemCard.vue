<template>
  <el-card shadow="hover" class="item-card" @click="goDetail">
    <div class="cover">
      <el-image v-if="item.imageUrl" :src="item.imageUrl" fit="cover" class="cover-img" lazy>
        <template #error>
          <div class="cover-empty"><el-icon><Picture /></el-icon></div>
        </template>
      </el-image>
      <div v-else class="cover-empty"><el-icon><Picture /></el-icon></div>
      <div class="cover-tags">
        <StatusTag dict="itemType" :value="item.type" effect="dark" />
        <StatusTag dict="itemStatus" :value="item.status" />
      </div>
    </div>
    <div class="body">
      <div class="title" :title="item.title">{{ item.title }}</div>
      <div class="desc">{{ item.description || '暂无描述' }}</div>
      <div class="meta">
        <span class="meta-item"><el-icon><Folder /></el-icon>{{ categoryName(item.categoryId) || '未分类' }}</span>
        <span class="meta-item"><el-icon><Location /></el-icon>{{ locationName(item.locationId) || '未知地点' }}</span>
      </div>
      <div class="meta time"><el-icon><Clock /></el-icon>{{ formatTime(item.eventTime) }}</div>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Picture, Folder, Location, Clock } from '@element-plus/icons-vue'
import StatusTag from './StatusTag.vue'
import { categoryName, locationName } from '../constants/options'
import { formatTime } from '../utils/format'

const props = defineProps({
  item: { type: Object, required: true }
})

const router = useRouter()

const goDetail = () => {
  router.push(`/items/${props.item.id}`)
}
</script>

<style scoped>
.item-card {
  cursor: pointer;
  border-radius: 10px;
  transition: transform 0.2s;
  overflow: hidden;
}

.item-card:hover {
  transform: translateY(-4px);
}

.cover {
  position: relative;
  height: 150px;
  background: #eef1f5;
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
  font-size: 36px;
}

.cover-tags {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 6px;
}

.body {
  padding: 4px 4px 0;
}

.title {
  font-size: 15px;
  font-weight: 600;
  color: #252d38;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.desc {
  margin-top: 6px;
  font-size: 13px;
  color: #6b7684;
  height: 38px;
  line-height: 19px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  margin-top: 8px;
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #8a94a3;
  flex-wrap: wrap;
}

.meta.time {
  margin-top: 4px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>

<template>
  <div class="page">
    <div class="list-head">
      <h1>失物列表</h1>
      <el-button type="primary" @click="$router.push('/publish')">发布信息</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filters">
        <el-radio-group v-model="filters.type" @change="onFilterChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="lost">失物</el-radio-button>
          <el-radio-button value="found">拾物</el-radio-button>
        </el-radio-group>
        <el-select v-model="filters.category" placeholder="分类" clearable style="width: 140px" @change="onFilterChange">
          <el-option v-for="c in CATEGORIES" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="filters.location" placeholder="地点" clearable style="width: 160px" @change="onFilterChange">
          <el-option v-for="l in LOCATIONS" :key="l.id" :label="l.floor ? `${l.building} ${l.floor}` : l.building" :value="l.id" />
        </el-select>
      </div>
    </el-card>

    <div v-loading="loading" class="item-grid list-grid">
      <ItemCard v-for="item in list" :key="item.id" :item="item" />
    </div>
    <el-empty v-if="!loading && list.length === 0" description="没有符合条件的物品" />

    <div class="pager">
      <el-pagination
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="page"
        @current-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getItems, normalizePage } from '../api/items'
import { CATEGORIES, LOCATIONS } from '../constants/options'
import ItemCard from '../components/ItemCard.vue'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 12
const filters = reactive({ type: '', category: null, location: null })

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getItems({
      type: filters.type || undefined,
      category: filters.category ?? undefined,
      location: filters.location ?? undefined,
      page: page.value,
      pageSize
    })
    const pageData = normalizePage(data)
    list.value = pageData.list
    total.value = pageData.total
  } finally {
    loading.value = false
  }
}

const onFilterChange = () => {
  page.value = 1
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.filter-card {
  margin-bottom: 16px;
  border-radius: 10px;
}

.filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.list-grid {
  min-height: 200px;
}

.pager {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

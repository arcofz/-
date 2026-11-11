<template>
  <div class="page">
    <h1>失物列表</h1>
    <p class="muted">TODO：类型、分类、地点筛选和分页。</p>
    <el-table :data="[]" empty-text="暂无数据">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="location" label="地点" />
      <el-table-column prop="eventTime" label="时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
</script>

<template>
  <div class="page">
    <h1>我的认领</h1>
    <p class="muted">TODO：认领状态、证据、发布者审核。</p>
    <el-table :data="[]" empty-text="暂无认领">
      <el-table-column prop="itemTitle" label="物品" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="createdAt" label="提交时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
</script>

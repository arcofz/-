<template>
  <div class="page">
    <h1>匹配结果</h1>
    <p class="muted">物品 ID：{{ route.params.id }}。TODO：Top 5、分数、理由、确认 / 拒绝。</p>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

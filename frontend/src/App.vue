<template>
  <el-container class="app">
    <el-header class="topbar">
      <router-link to="/" class="brand">寻物雷达</router-link>
      <nav class="nav">
        <router-link to="/items">失物列表</router-link>
        <router-link to="/publish">发布</router-link>
        <router-link to="/claims">我的认领</router-link>
        <router-link to="/admin">后台</router-link>
      </nav>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
</script>

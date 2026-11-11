<template>
  <div class="page">
    <h1>登录 / 注册</h1>
    <p class="muted">TODO：表单校验、JWT 保存、按角色跳转。</p>
    <el-form label-width="80px" style="max-width: 420px">
      <el-form-item label="学号">
        <el-input placeholder="student_no" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" placeholder="password" />
      </el-form-item>
      <el-button type="primary">登录</el-button>
    </el-form>
  </div>
</template>

<script setup>
</script>

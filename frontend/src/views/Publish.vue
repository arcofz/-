<template>
  <div class="page">
    <h1>发布失物 / 拾物</h1>
    <p class="muted">TODO：分类、地点、时间、描述、图片上传。</p>
    <el-form label-width="80px" style="max-width: 640px">
      <el-form-item label="类型">
        <el-radio-group>
          <el-radio value="lost">失物</el-radio>
          <el-radio value="found">拾物</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="标题">
        <el-input placeholder="例如：黑色折叠伞" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input type="textarea" :rows="4" />
      </el-form-item>
      <el-button type="primary">发布</el-button>
    </el-form>
  </div>
</template>

<script setup>
</script>

<template>
  <div class="page">
    <el-card shadow="never" class="form-card">
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑信息' : '发布失物 / 拾物' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button value="lost">失物</el-radio-button>
            <el-radio-button value="found">拾物</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="120" show-word-limit placeholder="例如：黑色折叠伞" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="地点" prop="locationId">
          <el-select v-model="form.locationId" placeholder="选择地点" style="width: 100%">
            <el-option v-for="l in locations" :key="l.id" :label="l.floor ? `${l.building} ${l.floor}` : l.building" :value="l.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="发生时间" prop="eventTime">
          <el-date-picker
            v-model="form.eventTime"
            type="datetime"
            placeholder="丢失 / 拾取时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss+08:00"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="1000" show-word-limit placeholder="物品特征、丢失经过等" />
        </el-form-item>
        <el-form-item label="图片">
          <!-- 选图后随表单提交时上传（api.md §9），拿到 URL 存 imageUrl；编辑时回填已有图 -->
          <el-upload
            :auto-upload="false"
            :limit="1"
            accept="image/*"
            :on-change="onImageChange"
            :on-remove="onImageRemove"
            :file-list="imageFileList"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="onSubmit">{{ isEdit ? '保存修改' : '发布' }}</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createItem, updateItem, getItem } from '../api/items'
import { uploadImage } from '../api/uploads'
import { getCategories, getLocations } from '../api/options'
import { CATEGORIES, LOCATIONS } from '../constants/options'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

const formRef = ref(null)
const submitting = ref(false)
const imageFileList = ref([])
// 下拉选项优先走公开接口（api.md §1），接口失败时回退种子常量
const categories = ref(CATEGORIES)
const locations = ref(LOCATIONS)

const form = reactive({
  type: 'lost',
  title: '',
  categoryId: null,
  locationId: null,
  eventTime: '',
  description: '',
  imageUrl: null
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [
    { required: true, message: '请填写标题', trigger: 'blur' },
    { max: 120, message: '标题不能超过 120 字', trigger: 'blur' }
  ],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  locationId: [{ required: true, message: '请选择地点', trigger: 'change' }],
  eventTime: [{ required: true, message: '请选择发生时间', trigger: 'change' }]
}

const onImageChange = (file) => {
  imageFileList.value = [file]
}

const onImageRemove = () => {
  imageFileList.value = []
  form.imageUrl = null
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      // 有新选的本地文件时先上传拿 URL；编辑回填的已有图（无 raw）保留原 URL
      const fileEntry = imageFileList.value[0]
      let imageUrl = fileEntry && !fileEntry.raw ? form.imageUrl : null
      if (fileEntry?.raw) {
        if (!fileEntry.raw.type.startsWith('image/')) {
          ElMessage.error('只支持图片文件')
          submitting.value = false
          return
        }
        if (fileEntry.raw.size > 2 * 1024 * 1024) {
          ElMessage.error('图片大小不能超过 2MB')
          submitting.value = false
          return
        }
        const data = await uploadImage(fileEntry.raw)
        imageUrl = data.url
      }
      const payload = {
        type: form.type,
        title: form.title.trim(),
        categoryId: form.categoryId,
        locationId: form.locationId,
        eventTime: form.eventTime,
        description: (form.description || '').trim(),
        imageUrl
      }
      if (isEdit.value) {
        await updateItem(route.params.id, payload)
        ElMessage.success('修改成功')
        router.push(`/items/${route.params.id}`)
      } else {
        await createItem(payload)
        ElMessage.success('发布成功')
        router.push('/items')
      }
    } catch (e) {
      ElMessage.error(e.message || (isEdit.value ? '修改失败' : '发布失败'))
    } finally {
      submitting.value = false
    }
  })
}

const resetForm = () => {
  form.type = 'lost'
  form.title = ''
  form.categoryId = null
  form.locationId = null
  form.eventTime = ''
  form.description = ''
  form.imageUrl = null
  imageFileList.value = []
  formRef.value?.clearValidate()
}

const loadEdit = async () => {
  // /publish 与 /items/:id/edit 共用本组件，路由切换时组件被复用，
  // 用 fullPath 做竞态保护，防止旧请求回填到新页面
  const path = route.fullPath
  const item = await getItem(route.params.id)
  if (path !== route.fullPath) return
  form.type = item.type
  form.title = item.title
  form.categoryId = item.categoryId ?? null
  form.locationId = item.locationId ?? null
  form.eventTime = item.eventTime || ''
  form.description = item.description || ''
  form.imageUrl = item.imageUrl || null
  imageFileList.value = item.imageUrl ? [{ name: '当前图片', url: item.imageUrl }] : []
}

const init = async () => {
  resetForm()
  if (isEdit.value) {
    try {
      await loadEdit()
    } catch (e) {
      // 404 等错误由空表单兜底，保存时会再次报错
    }
  }
}

const loadOptions = async () => {
  try {
    const [cats, locs] = await Promise.all([getCategories(), getLocations()])
    categories.value = cats
    locations.value = locs
  } catch (e) {
    // 接口失败保留常量兜底
  }
}

onMounted(() => {
  init()
  loadOptions()
})
watch(() => route.fullPath, init)
</script>

<style scoped>
.card-title {
  font-weight: 600;
  color: #182c44;
}
</style>

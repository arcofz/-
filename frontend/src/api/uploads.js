import http from './http'

// POST /uploads/images 上传图片（multipart/form-data，字段名 file），返回 { url }
export async function uploadImage(file) {
  const form = new FormData()
  form.append('file', file)
  const data = await http.post('/uploads/images', form)
  return data
}

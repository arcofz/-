import http from './http'

// 列表接口的分页字段在 docs/api.md 中未细化，这里兼容常见返回形态：
// { list, total } / { records, total } / 纯数组，联调时按后端实际返回取其一。
export function normalizePage(data) {
  if (Array.isArray(data)) {
    return { list: data, total: data.length }
  }
  if (data && Array.isArray(data.list)) {
    return { list: data.list, total: data.total ?? data.list.length }
  }
  if (data && Array.isArray(data.records)) {
    return { list: data.records, total: data.total ?? data.records.length }
  }
  return { list: [], total: 0 }
}

// GET /items 列表，支持 type/category/location 筛选
export function getItems(params) {
  return http.get('/items', { params })
}

// POST /items 发布失物或拾物
export function createItem(data) {
  return http.post('/items', data)
}

// GET /items/{id} 详情
export function getItem(id) {
  return http.get(`/items/${id}`)
}

// PUT /items/{id} 编辑，仅发布者
export function updateItem(id, data) {
  return http.put(`/items/${id}`, data)
}

// DELETE /items/{id} 软删除，仅发布者或管理员
export function deleteItem(id) {
  return http.delete(`/items/${id}`)
}

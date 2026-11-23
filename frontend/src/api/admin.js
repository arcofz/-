import http from './http'

// GET /admin/users 用户管理
export function getAdminUsers() {
  return http.get('/admin/users')
}

// GET /admin/categories 分类管理
export function getAdminCategories() {
  return http.get('/admin/categories')
}

// GET /admin/locations 地点管理
export function getAdminLocations() {
  return http.get('/admin/locations')
}

// GET /admin/stats 统计
export function getAdminStats() {
  return http.get('/admin/stats')
}

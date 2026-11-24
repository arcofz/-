import http from './http'
import { normalizePage } from './items'

// 后台列表统一按数组返回，兼容 { list, total } 与纯数组
async function listOf(url) {
  return normalizePage(await http.get(url)).list
}

// GET /admin/users 用户管理
export function getAdminUsers() {
  return listOf('/admin/users')
}

// GET /admin/categories 分类管理
export function getAdminCategories() {
  return listOf('/admin/categories')
}

// GET /admin/locations 地点管理
export function getAdminLocations() {
  return listOf('/admin/locations')
}

// GET /admin/stats 统计
export function getAdminStats() {
  return http.get('/admin/stats')
}

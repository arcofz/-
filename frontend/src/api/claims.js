import http from './http'
import { normalizePage } from './items'

// POST /claims 提交认领与证据
export function createClaim(data) {
  return http.post('/claims', data)
}

// GET /claims/mine 我的认领（我发起的 + 我发布物品收到的）
// 后端返回 { list, total }，页面统一按数组使用
export async function getMyClaims() {
  const data = await http.get('/claims/mine')
  return normalizePage(data).list
}

// POST /claims/{id}/approve 发布者通过
export function approveClaim(id) {
  return http.post(`/claims/${id}/approve`)
}

// POST /claims/{id}/reject 发布者驳回（携带驳回理由）
export function rejectClaim(id, reason) {
  return http.post(`/claims/${id}/reject`, { reason })
}

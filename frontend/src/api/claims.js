import http from './http'

// POST /claims 提交认领与证据
export function createClaim(data) {
  return http.post('/claims', data)
}

// GET /claims/mine 我的认领（我发起的 + 我发布物品收到的）
export function getMyClaims() {
  return http.get('/claims/mine')
}

// POST /claims/{id}/approve 发布者通过
export function approveClaim(id) {
  return http.post(`/claims/${id}/approve`)
}

// POST /claims/{id}/reject 发布者驳回（携带驳回理由）
export function rejectClaim(id, reason) {
  return http.post(`/claims/${id}/reject`, { reason })
}

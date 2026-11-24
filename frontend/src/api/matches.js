import http from './http'

// GET /items/{id}/matches 返回 Top 5 与分数、理由
export function getMatches(itemId) {
  return http.get(`/items/${itemId}/matches`)
}

// POST /matches/{id}/confirm 确认匹配
export function confirmMatch(id) {
  return http.post(`/matches/${id}/confirm`)
}

// POST /matches/{id}/reject 拒绝匹配
export function rejectMatch(id) {
  return http.post(`/matches/${id}/reject`)
}

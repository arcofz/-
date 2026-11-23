import http from './http'

// POST /handovers 登记交接
export function createHandover(data) {
  return http.post('/handovers', data)
}

// GET /handovers/{id} 交接详情
export function getHandover(id) {
  return http.get(`/handovers/${id}`)
}

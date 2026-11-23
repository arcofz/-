import http from './http'

// GET /notifications 通知列表
export function getNotifications() {
  return http.get('/notifications')
}

// POST /notifications/{id}/read 标记已读
export function readNotification(id) {
  return http.post(`/notifications/${id}/read`)
}

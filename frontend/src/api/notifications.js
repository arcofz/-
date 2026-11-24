import http from './http'
import { normalizePage } from './items'

// GET /notifications 通知列表
// 后端返回 { list, total }，页面统一按数组使用
export async function getNotifications() {
  const data = await http.get('/notifications')
  return normalizePage(data).list
}

// POST /notifications/{id}/read 标记已读
export function readNotification(id) {
  return http.post(`/notifications/${id}/read`)
}

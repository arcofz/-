// 与 database/seed.sql 中的种子数据一一对应。
// docs/api.md 已确认公开的 /categories、/locations 接口，后续可改为接口获取；
// 当前下拉选项暂用这里的固定值（与种子一致），联调时替换即可。
export const CATEGORIES = [
  { id: 1, name: '证件' },
  { id: 2, name: '电子产品' },
  { id: 3, name: '雨伞' },
  { id: 4, name: '钥匙' },
  { id: 5, name: '其他' }
]

export const LOCATIONS = [
  { id: 1, building: '图书馆', floor: '1F' },
  { id: 2, building: '图书馆', floor: '2F' },
  { id: 3, building: '教学楼A', floor: '1F' },
  { id: 4, building: '食堂', floor: '1F' },
  { id: 5, building: '操场', floor: null }
]

export const ITEM_TYPES = {
  lost: { label: '失物', tag: 'danger' },
  found: { label: '拾物', tag: 'success' }
}

export const ITEM_STATUS = {
  pending: { label: '待处理', tag: 'warning' },
  matched: { label: '已匹配', tag: 'primary' },
  claimed: { label: '认领中', tag: 'info' },
  completed: { label: '已完成', tag: 'success' },
  cancelled: { label: '已取消', tag: 'info' }
}

export const CLAIM_STATUS = {
  pending: { label: '待审核', tag: 'warning' },
  approved: { label: '已通过', tag: 'success' },
  rejected: { label: '已驳回', tag: 'danger' }
}

export const MATCH_STATUS = {
  pending: { label: '待确认', tag: 'warning' },
  confirmed: { label: '已确认', tag: 'success' },
  rejected: { label: '已拒绝', tag: 'info' }
}

export const NOTIFICATION_TYPES = {
  match: '匹配推荐',
  claim: '认领申请',
  review: '审核结果',
  handover: '交接通知',
  system: '系统通知'
}

export function categoryName(id) {
  if (id == null) return ''
  const hit = CATEGORIES.find((c) => c.id === Number(id))
  return hit ? hit.name : `#${id}`
}

export function locationName(id) {
  if (id == null) return ''
  const hit = LOCATIONS.find((l) => l.id === Number(id))
  if (!hit) return `#${id}`
  return hit.floor ? `${hit.building} ${hit.floor}` : hit.building
}

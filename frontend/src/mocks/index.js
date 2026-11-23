// ============================================================
// 开发期 Mock：VITE_USE_MOCK=true 时启用（开关见 .env.example）。
// 全部接口路径、请求/响应字段与 docs/api.md 契约保持一致，
// 仅用于前端独立开发与页面演示。后端接口就绪后，
// 关闭开关并删除本文件即可，业务代码无需任何改动。
// ============================================================
import http from '../api/http'
import { CATEGORIES, LOCATIONS } from '../constants/options'

const delay = (ms = 200) => new Promise((resolve) => setTimeout(resolve, ms))

const ok = (data) => ({ status: 200, body: { code: 0, message: 'ok', data } })
const fail = (code, message, status = 200) => ({ status, body: { code, message, data: null } })

let seq = 100
const nextId = () => ++seq

const IMG = (seed) => `https://picsum.photos/seed/xunwu${seed}/400/300`

const db = {
  users: [
    { id: 1, studentNo: 'admin', name: '管理员', role: 'admin', contact: '13800000000', password: '123456' },
    { id: 2, studentNo: '2021001', name: '张三', role: 'student', contact: '13811112222', password: '123456' },
    { id: 3, studentNo: '2021002', name: '李四', role: 'student', contact: '13833334444', password: '123456' },
    { id: 4, studentNo: '2021003', name: '王五', role: 'student', contact: '13855556666', password: '123456' }
  ],
  items: [
    { id: 1, userId: 2, title: '黑色折叠伞', description: '周四下午在图书馆三楼自习室遗失，伞柄有蓝色挂绳。', type: 'lost', status: 'pending', categoryId: 3, locationId: 1, eventTime: '2026-11-20T14:30:00+08:00', imageUrl: IMG(1), deletedAt: null, createdAt: '2026-11-20T15:00:00+08:00', updatedAt: '2026-11-20T15:00:00+08:00' },
    { id: 2, userId: 3, title: '校园卡', description: '学号 2021002，卡套是深绿色。', type: 'lost', status: 'pending', categoryId: 1, locationId: 4, eventTime: '2026-11-21T12:00:00+08:00', imageUrl: null, deletedAt: null, createdAt: '2026-11-21T12:30:00+08:00', updatedAt: '2026-11-21T12:30:00+08:00' },
    { id: 3, userId: 4, title: 'AirPods 耳机仓', description: '白色充电仓，右耳有划痕，操场看台附近丢失。', type: 'lost', status: 'pending', categoryId: 2, locationId: 5, eventTime: '2026-11-21T17:40:00+08:00', imageUrl: IMG(3), deletedAt: null, createdAt: '2026-11-21T18:00:00+08:00', updatedAt: '2026-11-21T18:00:00+08:00' },
    { id: 4, userId: 2, title: '黑色雨伞', description: '食堂一楼捡到一把黑色长柄伞，伞面完好。', type: 'found', status: 'pending', categoryId: 3, locationId: 4, eventTime: '2026-11-21T13:00:00+08:00', imageUrl: IMG(4), deletedAt: null, createdAt: '2026-11-21T13:20:00+08:00', updatedAt: '2026-11-21T13:20:00+08:00' },
    { id: 5, userId: 3, title: '学生证', description: '教学楼A 一层大厅捡到，学院：计算机学院。', type: 'found', status: 'claimed', categoryId: 1, locationId: 3, eventTime: '2026-11-20T10:00:00+08:00', imageUrl: IMG(5), deletedAt: null, createdAt: '2026-11-20T10:20:00+08:00', updatedAt: '2026-11-22T09:00:00+08:00' },
    { id: 6, userId: 2, title: '钥匙串', description: '三把钥匙加一个蓝色门禁扣，图书馆门口捡到。', type: 'found', status: 'completed', categoryId: 4, locationId: 1, eventTime: '2026-11-19T08:30:00+08:00', imageUrl: IMG(6), deletedAt: null, createdAt: '2026-11-19T09:00:00+08:00', updatedAt: '2026-11-20T16:00:00+08:00' },
    { id: 7, userId: 4, title: '水杯', description: '粉色保温杯，杯身贴有卡通贴纸。', type: 'lost', status: 'matched', categoryId: 5, locationId: 2, eventTime: '2026-11-19T16:00:00+08:00', imageUrl: IMG(7), deletedAt: null, createdAt: '2026-11-19T16:30:00+08:00', updatedAt: '2026-11-21T10:00:00+08:00' },
    { id: 8, userId: 2, title: '粉色保温杯', description: '图书馆二楼捡到，杯身有卡通贴纸。', type: 'found', status: 'matched', categoryId: 5, locationId: 2, eventTime: '2026-11-20T09:00:00+08:00', imageUrl: IMG(8), deletedAt: null, createdAt: '2026-11-20T09:30:00+08:00', updatedAt: '2026-11-21T10:00:00+08:00' }
  ],
  matches: [
    { id: 1, lostItemId: 7, foundItemId: 8, score: 86.5, status: 'pending', createdAt: '2026-11-21T10:00:00+08:00' },
    { id: 2, lostItemId: 1, foundItemId: 4, score: 72.0, status: 'pending', createdAt: '2026-11-21T14:00:00+08:00' }
  ],
  claims: [
    { id: 1, itemId: 6, claimantId: 4, evidence: '钥匙扣上有我的姓名贴，可以现场说出三把钥匙的样式。', status: 'approved', createdAt: '2026-11-19T12:00:00+08:00', reviewedAt: '2026-11-20T15:30:00+08:00', handoverId: 1 },
    { id: 2, itemId: 5, claimantId: 4, evidence: '学生证上的学号是 2021003，可出示同班同学证明。', status: 'approved', createdAt: '2026-11-21T09:00:00+08:00', reviewedAt: '2026-11-22T09:00:00+08:00', handoverId: null },
    { id: 3, itemId: 4, claimantId: 3, evidence: '我的伞是周四下午丢的，伞柄没有挂绳但款式一致，想当面确认。', status: 'pending', createdAt: '2026-11-21T15:00:00+08:00', reviewedAt: null, handoverId: null }
  ],
  handovers: [
    { id: 1, claimId: 1, handTime: '2026-11-20T16:00:00+08:00', place: '图书馆一楼服务台', createdAt: '2026-11-20T15:30:00+08:00' }
  ],
  notifications: [
    { id: 1, userId: 2, type: 'claim', content: '你发布的「黑色雨伞」收到新的认领申请，请及时审核。', isRead: false, createdAt: '2026-11-21T15:00:00+08:00' },
    { id: 2, userId: 2, type: 'match', content: '你发布的「粉色保温杯」有 1 条新的匹配推荐。', isRead: false, createdAt: '2026-11-21T10:00:00+08:00' },
    { id: 3, userId: 4, type: 'review', content: '你对「钥匙串」的认领已通过，请联系发布者完成交接。', isRead: true, createdAt: '2026-11-20T15:30:00+08:00' },
    { id: 4, userId: 2, type: 'handover', content: '「钥匙串」交接完成，感谢你使用寻物雷达。', isRead: true, createdAt: '2026-11-20T16:00:00+08:00' }
  ]
}

// ---------- 工具函数 ----------

const byId = (arr, id) => arr.find((x) => x.id === Number(id))

const tokenUserId = (config) => {
  const headers = config.headers || {}
  const auth = headers.Authorization ?? (typeof headers.get === 'function' ? headers.get('Authorization') : '')
  if (!auth || !auth.startsWith('Bearer mock-token-')) return null
  return Number(auth.replace('Bearer mock-token-', ''))
}

const currentUser = (config) => {
  const id = tokenUserId(config)
  const user = id ? byId(db.users, id) : null
  if (!user) throw fail(401, '未登录或登录已过期', 401)
  return user
}

const publicUser = (u) => ({ id: u.id, studentNo: u.studentNo, name: u.name, role: u.role, contact: u.contact })

const enrichItem = (item) => {
  const user = byId(db.users, item.userId)
  const cat = CATEGORIES.find((c) => c.id === Number(item.categoryId))
  const loc = LOCATIONS.find((l) => l.id === Number(item.locationId))
  return {
    ...item,
    publisherName: user ? user.name : '',
    publisherContact: user ? user.contact : '',
    categoryName: cat ? cat.name : '',
    locationName: loc ? (loc.floor ? `${loc.building} ${loc.floor}` : loc.building) : ''
  }
}

const matchReason = (m) => {
  const lost = byId(db.items, m.lostItemId)
  const found = byId(db.items, m.foundItemId)
  const parts = []
  if (lost?.categoryId === found?.categoryId) parts.push('分类一致')
  if (lost?.locationId === found?.locationId) parts.push('地点一致')
  if (lost && found) {
    const diff = Math.abs(new Date(lost.eventTime) - new Date(found.eventTime))
    if (diff < 24 * 3600 * 1000) parts.push('时间接近（24 小时内）')
  }
  if (lost && found && lost.title && found.title) {
    const a = lost.title.toLowerCase()
    const b = found.title.toLowerCase()
    if (a.includes('伞') && b.includes('伞')) parts.push('文本相似：均含「伞」')
    else if (a.includes('杯') && b.includes('杯')) parts.push('文本相似：均含「杯」')
    else if (a.includes('卡') && b.includes('卡')) parts.push('文本相似：均含「卡」')
  }
  if (parts.length === 0) parts.push('综合特征有一定相似度')
  return parts.join('；')
}

const enrichMatch = (m) => ({ ...m, reason: matchReason(m) })

const enrichClaim = (claim) => {
  const item = byId(db.items, claim.itemId)
  const owner = item ? byId(db.users, item.userId) : null
  const claimant = byId(db.users, claim.claimantId)
  return {
    ...claim,
    itemTitle: item ? item.title : '',
    itemType: item ? item.type : '',
    ownerId: item ? item.userId : null,
    ownerName: owner ? owner.name : '',
    claimantName: claimant ? claimant.name : ''
  }
}

const notify = (userId, type, content) => {
  db.notifications.unshift({ id: nextId(), userId, type, content, isRead: false, createdAt: new Date().toISOString() })
}

// ---------- 路由表 ----------

const routes = {
  'POST /auth/register': (config) => {
    const { studentNo, name, password, contact } = JSON.parse(config.data || '{}')
    if (!studentNo || !name || !password) throw fail(400, '学号、姓名、密码不能为空')
    if (db.users.some((u) => u.studentNo === studentNo)) throw fail(409, '该学号已注册')
    const user = { id: nextId(), studentNo, name, role: 'student', contact: contact || '', password }
    db.users.push(user)
    return ok(publicUser(user))
  },
  'POST /auth/login': (config) => {
    const { studentNo, password } = JSON.parse(config.data || '{}')
    const user = db.users.find((u) => u.studentNo === studentNo && u.password === password)
    if (!user) throw fail(400, '学号或密码错误')
    return ok({ token: `mock-token-${user.id}`, user: publicUser(user) })
  },
  'GET /auth/me': (config) => ok(publicUser(currentUser(config))),

  'GET /items': (config) => {
    const { type, category, location, page = 1, pageSize = 12 } = config.params || {}
    let list = db.items.filter((i) => !i.deletedAt)
    if (type) list = list.filter((i) => i.type === type)
    if (category) list = list.filter((i) => i.categoryId === Number(category))
    if (location) list = list.filter((i) => i.locationId === Number(location))
    list = [...list].sort((a, b) => (b.createdAt > a.createdAt ? 1 : -1))
    const total = list.length
    const start = (Number(page) - 1) * Number(pageSize)
    return ok({ list: list.slice(start, start + Number(pageSize)).map(enrichItem), total, page: Number(page), pageSize: Number(pageSize) })
  },
  'POST /items': (config) => {
    const user = currentUser(config)
    const body = JSON.parse(config.data || '{}')
    const { title, type } = body
    if (!title) throw fail(400, '标题不能为空')
    if (!['lost', 'found'].includes(type)) throw fail(400, '类型只能为 lost 或 found')
    const now = new Date().toISOString()
    const item = {
      id: nextId(), userId: user.id,
      title, description: body.description || '', type,
      status: 'pending', categoryId: body.categoryId ?? null, locationId: body.locationId ?? null,
      eventTime: body.eventTime || now, imageUrl: body.imageUrl || null,
      deletedAt: null, createdAt: now, updatedAt: now
    }
    db.items.unshift(item)
    return ok(enrichItem(item))
  },
  'GET /items/:id': (config, id) => {
    const item = byId(db.items, id)
    if (!item || item.deletedAt) throw fail(404, '物品不存在', 404)
    return ok(enrichItem(item))
  },
  'PUT /items/:id': (config, id) => {
    const user = currentUser(config)
    const item = byId(db.items, id)
    if (!item || item.deletedAt) throw fail(404, '物品不存在', 404)
    if (item.userId !== user.id && user.role !== 'admin') throw fail(403, '只有发布者可以编辑')
    const body = JSON.parse(config.data || '{}')
    if (body.title) item.title = body.title
    if (body.description != null) item.description = body.description
    if (body.type) item.type = body.type
    if (body.categoryId != null) item.categoryId = body.categoryId
    if (body.locationId != null) item.locationId = body.locationId
    if (body.eventTime) item.eventTime = body.eventTime
    if (body.imageUrl != null) item.imageUrl = body.imageUrl
    item.updatedAt = new Date().toISOString()
    return ok(enrichItem(item))
  },
  'DELETE /items/:id': (config, id) => {
    const user = currentUser(config)
    const item = byId(db.items, id)
    if (!item || item.deletedAt) throw fail(404, '物品不存在', 404)
    if (item.userId !== user.id && user.role !== 'admin') throw fail(403, '只有发布者或管理员可以删除')
    item.deletedAt = new Date().toISOString()
    return ok(null)
  },

  'GET /items/:id/matches': (config, id) => {
    const list = db.matches
      .filter((m) => m.lostItemId === Number(id) || m.foundItemId === Number(id))
      .sort((a, b) => b.score - a.score)
      .slice(0, 5)
      .map(enrichMatch)
    return ok(list)
  },
  'POST /matches/:id/confirm': (config, id) => {
    const user = currentUser(config)
    const m = byId(db.matches, id)
    if (!m) throw fail(404, '匹配记录不存在', 404)
    const lost = byId(db.items, m.lostItemId)
    if (lost && lost.userId !== user.id && user.role !== 'admin') throw fail(403, '只有失主可以确认匹配')
    m.status = 'confirmed'
    if (lost) lost.status = 'matched'
    const found = byId(db.items, m.foundItemId)
    if (found) found.status = 'matched'
    if (found) notify(found.userId, 'match', `你发布的「${found.title}」匹配已被确认，请留意认领申请。`)
    return ok(enrichMatch(m))
  },
  'POST /matches/:id/reject': (config, id) => {
    const user = currentUser(config)
    const m = byId(db.matches, id)
    if (!m) throw fail(404, '匹配记录不存在', 404)
    const lost = byId(db.items, m.lostItemId)
    if (lost && lost.userId !== user.id && user.role !== 'admin') throw fail(403, '只有失主可以拒绝匹配')
    m.status = 'rejected'
    return ok(enrichMatch(m))
  },

  'POST /claims': (config) => {
    const user = currentUser(config)
    const body = JSON.parse(config.data || '{}')
    const { itemId, evidence } = body
    const item = byId(db.items, itemId)
    if (!item || item.deletedAt) throw fail(404, '物品不存在', 404)
    if (!evidence) throw fail(400, '请填写认领证据')
    if (item.userId === user.id) throw fail(400, '不能认领自己发布的物品')
    if (db.claims.some((c) => c.itemId === Number(itemId) && c.claimantId === user.id)) throw fail(409, '你已经提交过认领申请')
    const claim = { id: nextId(), itemId: Number(itemId), claimantId: user.id, evidence, status: 'pending', createdAt: new Date().toISOString(), reviewedAt: null, handoverId: null }
    db.claims.unshift(claim)
    notify(item.userId, 'claim', `你发布的「${item.title}」收到新的认领申请，请及时审核。`)
    return ok(enrichClaim(claim))
  },
  'GET /claims/mine': (config) => {
    const user = currentUser(config)
    const list = db.claims
      .filter((c) => c.claimantId === user.id || byId(db.items, c.itemId)?.userId === user.id)
      .map(enrichClaim)
      .sort((a, b) => (b.createdAt > a.createdAt ? 1 : -1))
    return ok(list)
  },
  'POST /claims/:id/approve': (config, id) => {
    const user = currentUser(config)
    const claim = byId(db.claims, id)
    if (!claim) throw fail(404, '认领记录不存在', 404)
    const item = byId(db.items, claim.itemId)
    if (item.userId !== user.id && user.role !== 'admin') throw fail(403, '只有发布者可以审核认领')
    if (claim.status !== 'pending') throw fail(409, '该认领已审核过')
    claim.status = 'approved'
    claim.reviewedAt = new Date().toISOString()
    notify(claim.claimantId, 'review', `你对「${item.title}」的认领已通过，请及时与发布者联系完成交接。`)
    return ok(enrichClaim(claim))
  },
  'POST /claims/:id/reject': (config, id) => {
    const user = currentUser(config)
    const claim = byId(db.claims, id)
    if (!claim) throw fail(404, '认领记录不存在', 404)
    const item = byId(db.items, claim.itemId)
    if (item.userId !== user.id && user.role !== 'admin') throw fail(403, '只有发布者可以审核认领')
    if (claim.status !== 'pending') throw fail(409, '该认领已审核过')
    claim.status = 'rejected'
    claim.reviewedAt = new Date().toISOString()
    notify(claim.claimantId, 'review', `你对「${item.title}」的认领已被驳回，可重新提交证据。`)
    return ok(enrichClaim(claim))
  },

  'POST /handovers': (config) => {
    const user = currentUser(config)
    const body = JSON.parse(config.data || '{}')
    const { claimId, handTime, place } = body
    const claim = byId(db.claims, claimId)
    if (!claim) throw fail(404, '认领记录不存在', 404)
    const item = byId(db.items, claim.itemId)
    if (item.userId !== user.id && user.role !== 'admin') throw fail(403, '只有发布者可以登记交接')
    if (claim.status !== 'approved') throw fail(409, '只有审核通过的认领才能登记交接')
    if (!handTime || !place) throw fail(400, '交接时间和地点不能为空')
    const handover = { id: nextId(), claimId: Number(claimId), handTime, place, createdAt: new Date().toISOString() }
    db.handovers.push(handover)
    claim.handoverId = handover.id
    item.status = 'completed'
    notify(claim.claimantId, 'handover', `「${item.title}」交接完成，请确认物品无误。`)
    return ok(handover)
  },
  'GET /handovers/:id': (config, id) => {
    const handover = byId(db.handovers, id)
    if (!handover) throw fail(404, '交接记录不存在', 404)
    const claim = byId(db.claims, handover.claimId)
    const item = claim ? byId(db.items, claim.itemId) : null
    return ok({ ...handover, itemTitle: item ? item.title : '', itemId: item ? item.id : null })
  },

  'GET /notifications': (config) => {
    const user = currentUser(config)
    const list = db.notifications
      .filter((n) => n.userId === user.id)
      .sort((a, b) => (b.createdAt > a.createdAt ? 1 : -1))
    return ok(list)
  },
  'POST /notifications/:id/read': (config, id) => {
    const user = currentUser(config)
    const n = byId(db.notifications, id)
    if (!n || n.userId !== user.id) throw fail(404, '通知不存在', 404)
    n.isRead = true
    return ok(n)
  },

  'GET /admin/users': (config) => {
    const user = currentUser(config)
    if (user.role !== 'admin') throw fail(403, '无权限访问')
    return ok(db.users.map(publicUser))
  },
  'GET /admin/categories': (config) => {
    const user = currentUser(config)
    if (user.role !== 'admin') throw fail(403, '无权限访问')
    return ok([{ id: 1, name: '证件' }, { id: 2, name: '电子产品' }, { id: 3, name: '雨伞' }, { id: 4, name: '钥匙' }, { id: 5, name: '其他' }])
  },
  'GET /admin/locations': (config) => {
    const user = currentUser(config)
    if (user.role !== 'admin') throw fail(403, '无权限访问')
    return ok([{ id: 1, building: '图书馆', floor: '1F' }, { id: 2, building: '图书馆', floor: '2F' }, { id: 3, building: '教学楼A', floor: '1F' }, { id: 4, building: '食堂', floor: '1F' }, { id: 5, building: '操场', floor: null }])
  },
  'GET /admin/stats': (config) => {
    const user = currentUser(config)
    if (user.role !== 'admin') throw fail(403, '无权限访问')
    return ok({
      totalUsers: db.users.length,
      totalItems: db.items.filter((i) => !i.deletedAt).length,
      lostCount: db.items.filter((i) => !i.deletedAt && i.type === 'lost').length,
      foundCount: db.items.filter((i) => !i.deletedAt && i.type === 'found').length,
      pendingClaims: db.claims.filter((c) => c.status === 'pending').length,
      totalMatches: db.matches.length,
      confirmedMatches: db.matches.filter((m) => m.status === 'confirmed').length,
      completedItems: db.items.filter((i) => i.status === 'completed').length
    })
  }
}

function dispatch(url, method, config) {
  const path = url.split('?')[0]
  const parts = path.split('/').filter(Boolean)
  const normalized = '/' + parts.map((p) => (/^\d+$/.test(p) ? ':id' : p)).join('/')
  const handler = routes[`${method} ${normalized}`]
  if (!handler) throw fail(500, `Mock 未实现该接口：${method} ${normalized}`, 501)
  const id = parts.find((p) => /^\d+$/.test(p))
  return handler(config, id)
}

export function installMock() {
  if (import.meta.env.VITE_USE_MOCK !== 'true') return
  http.defaults.adapter = async (config) => {
    await delay()
    try {
      const { status, body } = dispatch(config.url || '', (config.method || 'get').toUpperCase(), config)
      return { data: body, status, statusText: '', headers: {}, config }
    } catch (e) {
      if (e && e.status && e.body) {
        return { data: e.body, status: e.status, statusText: '', headers: {}, config }
      }
      throw e
    }
  }
}

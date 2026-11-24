import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/Home.vue') },
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  { path: '/items', name: 'items', component: () => import('../views/ItemList.vue') },
  { path: '/items/:id', name: 'item-detail', component: () => import('../views/ItemDetail.vue') },
  { path: '/publish', name: 'publish', component: () => import('../views/Publish.vue'), meta: { requiresAuth: true } },
  { path: '/items/:id/edit', name: 'item-edit', component: () => import('../views/Publish.vue'), meta: { requiresAuth: true } },
  { path: '/matches/:id', name: 'matches', component: () => import('../views/Matches.vue') },
  { path: '/claims', name: 'claims', component: () => import('../views/Claims.vue'), meta: { requiresAuth: true } },
  { path: '/notifications', name: 'notifications', component: () => import('../views/Notifications.vue'), meta: { requiresAuth: true } },
  { path: '/handovers/:id', name: 'handover-detail', component: () => import('../views/HandoverDetail.vue'), meta: { requiresAuth: true } },
  { path: '/admin', name: 'admin', component: () => import('../views/Admin.vue'), meta: { requiresAuth: true, requiresAdmin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  // 有 token 但用户信息未加载（如刷新页面）时先拉取
  if (auth.token && !auth.user) {
    try {
      await auth.fetchMe()
    } catch (e) {
      auth.logout()
    }
  }
  if (to.meta.requiresAuth && !auth.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { path: '/' }
  }
  if (to.path === '/login' && auth.token) {
    return { path: '/' }
  }
  return true
})

export default router

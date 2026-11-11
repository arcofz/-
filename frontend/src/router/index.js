import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/Home.vue') },
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  { path: '/items', name: 'items', component: () => import('../views/ItemList.vue') },
  { path: '/publish', name: 'publish', component: () => import('../views/Publish.vue') },
  { path: '/matches/:id', name: 'matches', component: () => import('../views/Matches.vue') },
  { path: '/claims', name: 'claims', component: () => import('../views/Claims.vue') },
  { path: '/admin', name: 'admin', component: () => import('../views/Admin.vue') }
]

export default createRouter({
  history: createWebHistory(),
  routes
})

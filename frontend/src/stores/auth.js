import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getMe } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'admin'
  },
  actions: {
    async login(payload) {
      const data = await loginApi(payload)
      this.token = data.token
      localStorage.setItem('token', data.token)
      // 登录接口返回用户信息时直接用，否则拉取 /auth/me
      this.user = data.user || (await getMe())
    },
    async register(payload) {
      return registerApi(payload)
    },
    async fetchMe() {
      this.user = await getMe()
      return this.user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
    }
  }
})

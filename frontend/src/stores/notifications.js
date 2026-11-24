import { defineStore } from 'pinia'
import { getNotifications } from '../api/notifications'
import { useAuthStore } from './auth'

export const useNotificationStore = defineStore('notifications', {
  state: () => ({
    unread: 0
  }),
  actions: {
    async refresh() {
      const auth = useAuthStore()
      if (!auth.token) {
        this.unread = 0
        return
      }
      try {
        const list = await getNotifications()
        this.unread = list.filter((n) => !n.isRead).length
      } catch (e) {
        this.unread = 0
      }
    }
  }
})

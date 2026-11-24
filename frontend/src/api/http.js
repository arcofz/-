import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && body.code !== 0) {
      const err = new Error(body.message || '请求失败')
      err.code = body.code
      return Promise.reject(err)
    }
    return body.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    // HTTP 4xx/5xx 时同样把业务错误码与后端 message 挂到错误对象上，
    // 让页面可以统一用 e.code / e.message 处理
    const body = error.response?.data
    error.code = body?.code ?? error.response?.status
    if (body?.message) error.message = body.message
    return Promise.reject(error)
  }
)

export default http

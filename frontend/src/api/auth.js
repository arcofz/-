import http from './http'

// POST /auth/register 注册
export function register(data) {
  return http.post('/auth/register', data)
}

// POST /auth/login 登录，返回 JWT
export function login(data) {
  return http.post('/auth/login', data)
}

// GET /auth/me 当前用户
export function getMe() {
  return http.get('/auth/me')
}

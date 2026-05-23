import request from './request'

export function login(data: { username: string; password: string }) {
  return request.post('/api/auth/login', data)
}

export function getUserInfo() {
  return request.get('/api/auth/info')
}

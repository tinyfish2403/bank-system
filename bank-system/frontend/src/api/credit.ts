import request from './request'

export function getApplicationList(params: { page?: number; size?: number; keyword?: string; status?: number }) {
  return request.get('/api/credit/applications', { params })
}

export function getApplicationById(id: number) {
  return request.get(`/api/credit/applications/${id}`)
}

export function createApplication(data: any) {
  return request.post('/api/credit/applications', data)
}

export function updateApplication(id: number, data: any) {
  return request.put(`/api/credit/applications/${id}`, data)
}

export function approveApplication(id: number, data: any) {
  return request.post(`/api/credit/applications/${id}/approve`, data)
}

export function getApprovalList(params: { page?: number; size?: number; keyword?: string; status?: number }) {
  return request.get('/api/credit/approvals', { params })
}

export function getApprovalById(id: number) {
  return request.get(`/api/credit/approvals/${id}`)
}

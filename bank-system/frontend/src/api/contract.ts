import request from './request'

export function getContractList(params: { page?: number; size?: number; keyword?: string; status?: number }) {
  return request.get('/api/contracts', { params })
}

export function getContractById(id: number) {
  return request.get(`/api/contracts/${id}`)
}

export function createContract(data: any) {
  return request.post('/api/contracts', data)
}

export function updateContract(id: number, data: any) {
  return request.put(`/api/contracts/${id}`, data)
}

export function cancelContract(id: number) {
  return request.put(`/api/contracts/${id}/cancel`)
}

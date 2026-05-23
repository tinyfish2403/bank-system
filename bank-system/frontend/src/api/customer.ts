import request from './request'

export function getCustomerList(params: { page?: number; size?: number; keyword?: string }) {
  return request.get('/api/customers', { params })
}

export function getCustomerById(id: number) {
  return request.get(`/api/customers/${id}`)
}

export function createCustomer(data: any) {
  return request.post('/api/customers', data)
}

export function updateCustomer(id: number, data: any) {
  return request.put(`/api/customers/${id}`, data)
}

export function deleteCustomer(id: number) {
  return request.delete(`/api/customers/${id}`)
}

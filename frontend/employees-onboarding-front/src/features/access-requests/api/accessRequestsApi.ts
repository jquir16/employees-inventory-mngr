import axiosClient from '@/shared/api/axiosClient'
import { 
  AccessRequestCreate,
  AccessRequestUpdate,
  AccessRequestsResponse 
} from '../../../entities/access-request/accessRequestTypes'

export const fetchAccessRequests = async (): Promise<AccessRequestsResponse[]> => {
  const response = await axiosClient.get('/access-requests')
  return response.data
}

export const getUserAccessRequests = async (userId: number): Promise<AccessRequestsResponse[]> => {
  const response = await axiosClient.get(`/access-requests/userId/${userId}`)
  return response.data
}

export const getAccessRequestById = async (id: number): Promise<AccessRequestsResponse> => {
  const response = await axiosClient.get(`/access-requests/${id}`)
  return response.data
}

export const createAccessRequest = async (
  data: AccessRequestCreate
): Promise<AccessRequestsResponse> => {
  const response = await axiosClient.post('/access-requests', data)
  return response.data
}

export const updateAccessRequest = async (
  id: number,
  data: AccessRequestUpdate
): Promise<AccessRequestsResponse> => {
  const response = await axiosClient.put(`/access-requests/${id}`, data)
  return response.data
}

export const deleteAccessRequest = async (id: number): Promise<void> => {
  await axiosClient.delete(`/access-requests/${id}`)
}
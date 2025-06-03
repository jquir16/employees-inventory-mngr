import axiosClient from '@/shared/api/axiosClient'
import { UserResponse, UpdateUserRequest } from '@/entities/user/userTypes'

export const getUserById = async (userId: number): Promise<UserResponse> => {
    const response = await axiosClient.get(`/users/${userId}`)
    return response.data
}

export const fetchAllUsers = async (): Promise<UserResponse[]> => {
  const response = await axiosClient.get('/users')
  return response.data
}

export const updateUser = async (
  id: number,
  data: UpdateUserRequest
): Promise<UserResponse> => {
  const response = await axiosClient.put(`/users/${id}`, data)
  return response.data
}

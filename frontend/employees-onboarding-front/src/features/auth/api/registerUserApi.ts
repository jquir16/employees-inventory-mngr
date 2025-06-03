import axiosClient from '@/shared/api/axiosClient'
import { AuthResponse } from '@/entities/auth/authTypes'
import { CreateUserRequest } from '@/entities/user/userTypes'

export const registerUser = async (data: CreateUserRequest): Promise<AuthResponse> => {
    const response = await axiosClient.post('/auth/register', data)
    return response.data
}

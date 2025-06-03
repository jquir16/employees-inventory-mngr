import axiosClient from '@/shared/api/axiosClient'
import { LoginFormValues, AuthResponse } from '@/entities/auth/authTypes'

export const login = async (data: LoginFormValues): Promise<AuthResponse> => {
    const response = await axiosClient.post('/auth/login', data)
    return response.data
}

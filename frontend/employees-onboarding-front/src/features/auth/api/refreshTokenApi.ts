import axiosClient from '@/shared/api/axiosClient'
import { AuthResponse } from '@/entities/auth/authTypes'

export const refreshToken = async (token: string): Promise<AuthResponse> => {
    const response = await axiosClient.post(
        '/auth/refresh',
        {},
        { headers: { Authorization: `Bearer ${token}` } }
    )
    return response.data
}
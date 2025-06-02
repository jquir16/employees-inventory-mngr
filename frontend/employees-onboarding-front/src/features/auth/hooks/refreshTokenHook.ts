import { useMutation, UseMutationOptions } from '@tanstack/react-query'
import { AuthResponse } from '@/entities/auth/authTypes'
import { refreshToken } from '@/features/auth/api/refreshTokenApi'

export const useRefreshToken = (
    options?: UseMutationOptions<AuthResponse, Error, string>
) => {
    return useMutation<AuthResponse, Error, string>({
        mutationFn: refreshToken,
        ...options,
    })
}
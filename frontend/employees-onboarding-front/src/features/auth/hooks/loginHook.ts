'use client'

import { useMutation, UseMutationOptions } from '@tanstack/react-query'
import { LoginFormValues, AuthResponse } from '@/entities/auth/authTypes'
import { login } from '@/features/auth/api/loginApi'


export const useLogin = (
    options?: UseMutationOptions<AuthResponse, Error, LoginFormValues>
) => {
    return useMutation<AuthResponse, Error, LoginFormValues>({
        mutationFn: login,
        ...options,
    })
}
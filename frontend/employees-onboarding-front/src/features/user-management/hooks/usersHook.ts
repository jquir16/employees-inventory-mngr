import { CreateUserRequest, UpdateUserRequest, UserResponse } from "@/entities/user/userTypes"
import { useMutation, UseMutationOptions, useQuery, UseQueryOptions } from "@tanstack/react-query"
import { fetchAllUsers, getUserById, updateUser } from "../api/usersApi"
import { AuthResponse } from "@/entities/auth/authTypes"
import { registerUser } from "@/features/auth/api/registerUserApi"

export const userKeys = {
  all: ['users'] as const,
  lists: () => [...userKeys.all, 'list'] as const,
  list: (filters: Record<string, any>) => [...userKeys.lists(), filters] as const,
  details: () => [...userKeys.all, 'detail'] as const,
  detail: (id: number) => [...userKeys.details(), id] as const,
}

export const useUser = (
  id: number,
  options?: Omit<UseQueryOptions<UserResponse>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<UserResponse>({
    queryKey: userKeys.detail(id),
    queryFn: () => getUserById(id),
    enabled: !!id,
    ...options,
  });
}

export const useUsers = (
  options?: Omit<UseQueryOptions<UserResponse[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<UserResponse[]>({
    queryKey: userKeys.lists(),
    queryFn: fetchAllUsers,
    ...options,
  });
}

export const useRegisterUser = (
  options?: UseMutationOptions<AuthResponse, Error, CreateUserRequest>
) => {
  return useMutation<AuthResponse, Error, CreateUserRequest>({
    mutationFn: registerUser,
    ...options,
  })
}

export const useUpdateUser = (
  options?: UseMutationOptions<UserResponse, Error, { id: number; data: UpdateUserRequest }>
) => {
  return useMutation<UserResponse, Error, { id: number; data: UpdateUserRequest }>({
    mutationFn: ({ id, data }) => updateUser(id, data),
    ...options,
  })
}

import { UserResponse } from "@/entities/user/userTypes"
import { useQuery, UseQueryOptions } from "@tanstack/react-query"
import { fetchAllUsers, getUserById } from "../api/usersApi"

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
'use client'

import {
  useQuery,
  useMutation,
  UseMutationOptions,
  UseQueryOptions,
  QueryClient,
  queryOptions
} from '@tanstack/react-query'
import {
  fetchAccessRequests,
  getUserAccessRequests,
  getAccessRequestById,
  createAccessRequest,
  updateAccessRequest,
  deleteAccessRequest,
} from '../api/accessRequestsApi'
import { AccessRequestCreate, AccessRequestsResponse, AccessRequestUpdate } from '@/entities/access-request/accessRequestTypes'

export const accessRequestKeys = {
  all: ['access-requests'] as const,
  lists: () => [...accessRequestKeys.all, 'list'] as const,
  list: (filters: Record<string, any>) => [...accessRequestKeys.lists(), filters] as const,
  details: () => [...accessRequestKeys.all, 'detail'] as const,
  detail: (id: number) => [...accessRequestKeys.details(), id] as const,
}

export const useAccessRequests = (
  options?: Omit<UseQueryOptions<AccessRequestsResponse[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<AccessRequestsResponse[]>({
    queryKey: accessRequestKeys.lists(),
    queryFn: fetchAccessRequests,
    ...options,
  })
}

export const useUserAccessRequests = (
  userId: number,
  options?: Omit<UseQueryOptions<AccessRequestsResponse[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<AccessRequestsResponse[]>({
    queryKey: accessRequestKeys.list({ userId }),
    queryFn: () => getUserAccessRequests(userId),
    enabled: !!userId,
    ...options,
  })
}

export const useAccessRequest = (
  id: number,
  options?: Omit<UseQueryOptions<AccessRequestsResponse>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<AccessRequestsResponse>({
    queryKey: accessRequestKeys.detail(id),
    queryFn: () => getAccessRequestById(id),
    enabled: !!id,
    ...options,
  })
}

export const useCreateAccessRequest = (
  options?: UseMutationOptions<AccessRequestsResponse, Error, AccessRequestCreate>
) => {
  return useMutation<AccessRequestsResponse, Error, AccessRequestCreate>({
    mutationFn: createAccessRequest,
    ...options,
  })
}

export const useUpdateAccessRequest = (
  options?: UseMutationOptions<AccessRequestsResponse, Error, { id: number; data: AccessRequestUpdate }>
) => {
  return useMutation<AccessRequestsResponse, Error, { id: number; data: AccessRequestUpdate }>({
    mutationFn: ({ id, data }) => updateAccessRequest(id, data),
    ...options,
  })
}

export const useDeleteAccessRequest = (
  options?: UseMutationOptions<void, Error, number>
) => {
  return useMutation<void, Error, number>({
    mutationFn: deleteAccessRequest,
    ...options,
  })
}

export const prefetchAccessRequests = async (queryClient: QueryClient) => {
  await queryClient.prefetchQuery({
    queryKey: accessRequestKeys.lists(),
    queryFn: fetchAccessRequests,
  })
}
import { CreateInventoryRequest, InventoryResponse, UpdateInventoryRequest } from "@/entities/inventory/inventoryTypes"
import { QueryClient, useMutation, UseMutationOptions, useQuery, UseQueryOptions } from "@tanstack/react-query"
import { assignInventory, createInventory, fetchAllInventory, getInventoryById, getUserInventory, unAssignInventory, updateInventory } from "../api/inventoryApi"

export const inventoryKeys = {
  all: ['inventory'] as const,
  lists: () => [...inventoryKeys.all, 'list'] as const,
  list: (filters: Record<string, any>) => [...inventoryKeys.lists(), filters] as const,
  details: () => [...inventoryKeys.all, 'detail'] as const,
  detail: (id: number) => [...inventoryKeys.details(), id] as const,
}

export const useGetAllInventory = (
  options?: Omit<UseQueryOptions<InventoryResponse[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<InventoryResponse[]>({
    queryKey: inventoryKeys.lists(),
    queryFn: fetchAllInventory,
    ...options,
  })
}

export const useUserInventory = (
  userId: number,
  options?: Omit<UseQueryOptions<InventoryResponse[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<InventoryResponse[]>({
    queryKey: inventoryKeys.list({ userId }),
    queryFn: () => getUserInventory(userId),
    enabled: !!userId,
    ...options,
  })
}

export const useInventory = (
  id: number,
  options?: Omit<UseQueryOptions<InventoryResponse>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<InventoryResponse>({
    queryKey: inventoryKeys.detail(id),
    queryFn: () => getInventoryById(id),
    enabled: !!id,
    ...options,
  })
}

export const useCreateInventory = (
  options?: UseMutationOptions<InventoryResponse, Error, CreateInventoryRequest>
) => {
  return useMutation<InventoryResponse, Error, CreateInventoryRequest>({
    mutationFn: createInventory,
    ...options,
  })
}

export const useUpdateInventory = (
  options?: UseMutationOptions<InventoryResponse, Error, { id: number; data: UpdateInventoryRequest }>
) => {
  return useMutation<InventoryResponse, Error, { id: number; data: UpdateInventoryRequest }>({
    mutationFn: ({ id, data }) => updateInventory(id, data),
    ...options,
  })
}

export const useAssignInventory = (
  options?: UseMutationOptions<InventoryResponse, Error, { inventoryId: number; userId: number }>
) => {
  return useMutation<InventoryResponse, Error, { inventoryId: number; userId: number }>({
    mutationFn: ({ inventoryId, userId }) => assignInventory(inventoryId, userId),
    ...options,
  })
}

export const useUnAssignInventory = (
  options?: UseMutationOptions<InventoryResponse, Error, { inventoryId: number; userId: number }>
) => {
  return useMutation<InventoryResponse, Error, { inventoryId: number; userId: number }>({
    mutationFn: ({ inventoryId, userId }) => unAssignInventory(inventoryId, userId),
    ...options,
  })
}

export const prefetchAccessRequests = async (queryClient: QueryClient) => {
  await queryClient.prefetchQuery({
    queryKey: inventoryKeys.lists(),
    queryFn: fetchAllInventory,
  })
}
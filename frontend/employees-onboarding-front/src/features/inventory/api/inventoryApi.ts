import { CreateInventoryRequest, InventoryResponse, UpdateInventoryRequest } from "@/entities/inventory/inventoryTypes"
import axiosClient from "@/shared/api/axiosClient"

export const fetchAllInventory = async (): Promise<InventoryResponse[]> => {
  const response = await axiosClient.get('/inventory');
  return response.data;
}

export const getUserInventory = async (userId: number): Promise<InventoryResponse[]> => {
  const response = await axiosClient.get(`/user/${userId}`);
  return response.data;
}

export const getInventoryById = async (id: number): Promise<InventoryResponse> => {
  const response = await axiosClient.get(`/inventory/${id}`);
  return response.data;
}

export const createInventory = async (
  data: CreateInventoryRequest
): Promise<InventoryResponse> => {
  console.log('create inventory request: ', data);
  const response = await axiosClient.post('/inventory', data);
  return response.data;
}

export const updateInventory = async (
  id: number,
  data: UpdateInventoryRequest
): Promise<InventoryResponse> => {
  const response = await axiosClient.put(`/inventory/${id}`, data);
  return response.data;
}

export const assignInventory = async (inventoryId: number, userId: number): Promise<InventoryResponse> => {
    const response = await axiosClient.post(`/inventory/${inventoryId}/assign/${userId}`);
    return response.data;
}
export const unAssignInventory = async (inventoryId: number, userId: number): Promise<InventoryResponse> => {
  return await axiosClient.delete(`/inventory/${inventoryId}/user/${userId}`);
}
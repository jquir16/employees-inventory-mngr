
export interface InventoryResponse {
    id: number,
    description: string,
    userId: number,
    serialNumber: string,
    date: Date
}

export interface CreateInventoryRequest {
    description: string,
    serialNumber: string
}

export interface UpdateInventoryRequest {
    description?: string,
    userId?: number,
    serialNumber?: string,
}
'use client'

import { InventoryForm } from "@/features/inventory/components/inventoryForm"
import { InventoryTable } from "@/features/inventory/components/inventoryTable"

export default function InventoryPage() {
    return (
        <div className="space-y-8">
            <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Nuevo item de inventario</h2>
                <InventoryForm />
            </div>

            <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Inventario Disponible</h2>
                <InventoryTable />
            </div>
        </div>
    )
}
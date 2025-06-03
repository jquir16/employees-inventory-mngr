'use client'

import { ColumnDef } from '@tanstack/react-table'
import { BaseTable } from '@/shared/ui/table/baseTable'
import { useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { useAuthStore } from '@/features/auth/model/authStore'
import { useUser } from '@/features/user-management/hooks/usersHook'
import {
  useAssignInventory,
  useUnAssignInventory,
  useGetAllInventory,
  inventoryKeys
} from '../hooks/useInventory'

import { InventoryResponse } from '@/entities/inventory/inventoryTypes'

export function InventoryTable({ className = '' }: { className?: string }) {
  const { user } = useAuthStore()
  const { data: inventory, isLoading } = useGetAllInventory()
  const queryClient = useQueryClient()

  const { mutate: assignInventory } = useAssignInventory({
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: inventoryKeys.lists() })
      toast.success('Inventario asignado exitosamente')
    },
    onError: (error: any) => {
      toast.error(`Error al asignar: ${error.message}`)
    }
  })

  const { mutate: unAssignInventory } = useUnAssignInventory({
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: inventoryKeys.lists() })
      toast.success('Inventario desasignado exitosamente')
    },
    onError: (error: any) => {
      toast.error(`Error al desasignar: ${error.message}`)
    }
  })

  const columns: ColumnDef<InventoryResponse>[] = [
    {
      accessorKey: 'id',
      header: 'ID',
    },
    {
      accessorKey: 'serialNumber',
      header: 'Número de Serie',
    },
    {
      accessorKey: 'description',
      header: 'Descripción',
    },
    {
      accessorKey: 'userId',
      header: 'Asignado a',
      cell: ({ row }) => {
        const userId = row.original.userId
        const { data: assignedUser } = useUser(userId ?? 0)

        return userId ? (
          <div>
            <p className="font-medium">{assignedUser?.name ?? 'Cargando...'}</p>
            <p className="text-xs text-gray-500">{assignedUser?.email ?? ''}</p>
          </div>
        ) : (
          <span className="italic text-gray-500">No asignado</span>
        )
      }
    },
    {
      accessorKey: 'acciones',
      header: 'Acciones',
      cell: ({ row }) => {
        const inventory = row.original
        return inventory.userId ? (
          <button
            className="px-3 py-1 text-sm bg-red-600 text-white rounded hover:bg-red-700"
            onClick={() => unAssignInventory({ inventoryId: inventory.id, userId: inventory.userId! })}
          >
            Desasignar
          </button>
        ) : (
          <button
            className="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700"
            onClick={() => {
              if (!user?.id) return toast.error("No hay usuario autenticado")
              assignInventory({ inventoryId: inventory.id, userId: user.id })
            }}
          >
            Asignarme
          </button>
        )
      }
    }
  ]

  return (
    <>
      {!isLoading && (!inventory || inventory.length === 0) ? (
        <div className="text-center py-8">
          <p className="text-gray-500">No hay inventario disponible</p>
        </div>
      ) : (
        <BaseTable
          data={inventory || []}
          columns={columns}
          isLoading={isLoading}
          className={className}
        />
      )}
    </>
  )
}

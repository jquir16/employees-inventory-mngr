'use client'

import { ColumnDef } from '@tanstack/react-table'
import { AccessRequestsResponse, AccessRequestStatus } from '../../../entities/access-request/accessRequestTypes'
import { BaseTable } from '@/shared/ui/table/baseTable'
import { accessRequestKeys, useAccessRequests, useUpdateAccessRequest, useUserAccessRequests } from '../hooks/useAccessRequests'
import { useAuthStore } from '@/features/auth/model/authStore'
import { accessRequestStatusToColor, formatDate } from '../../../shared/lib/utils'
import { useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { useUser } from '@/features/user-management/hooks/usersHook'

const columns: ColumnDef<AccessRequestsResponse>[] = [
    {
        accessorKey: 'systemName',
        header: 'Sistema',
        cell: ({ row }) => (
            <span className="font-medium text-gray-900">
                {Array.isArray(row.original.systems)
                    ? row.original.systems.join(', ')
                    : row.original.systems}
            </span>
        ),
    },
    {
        accessorKey: 'status',
        header: 'Estado',
        cell: ({ row }) => {
            const status = row.original.status as AccessRequestStatus;
            const { bgClass, textClass } = accessRequestStatusToColor(status);

            const statusLabels: Record<AccessRequestStatus, string> = {
                PENDING: 'Pendiente',
                APPROVED: 'Aprobado',
                REJECTED: 'Rechazado'
            };

            return(
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${bgClass} ${textClass}`}>
                    {statusLabels[status]}
                </span>
            )
        },
    },
    {
        accessorKey: 'createdAt',
        header: 'Fecha',
        cell: ({ row }) => (
            <span className="text-gray-500">
                {formatDate(row.original.createdAt)}
            </span>
        ),
    },
]

export function AccessRequestsTable({ className = '' }: { className?: string }) {
    const queryClient = useQueryClient();
    const { user } = useAuthStore()
    const { data: allRequests, isLoading: isLoadingAll } = useAccessRequests()
    const { data: userRequests, isLoading: isLoadingUser } = useUserAccessRequests(user?.id || 0)

    const data = user?.role === 'PM' || user?.role === 'AC' ? allRequests : userRequests
    const isLoading = user?.role === 'PM' || user?.role === 'AC' ? isLoadingAll : isLoadingUser

    const dynamicColumns = [...columns]

    if (user?.role === 'PM' || user?.role === 'AC') {
        dynamicColumns.unshift({
            accessorKey: 'userName',
            header: 'Usuario',
            cell: ({ row }) => {
                const { data: userData } = useUser(row.original.userId)

                return (
                    <div>
                        <p className="font-medium text-gray-900">{userData?.name || 'Cargando...'}</p>
                        <p className="text-xs text-gray-500">{userData?.email || 'Cargando...'}</p>
                        <p className="text-xs text-gray-500">ID: {row.original.userId}</p>
                    </div>
                )
            },
        })
    }

    if (user?.role === 'PM' || user?.role === 'AC') {
        dynamicColumns.unshift({
            accessorKey: 'requestId',
            header: 'Solicitud',
            cell: ({ row }) => (
                <div>
                    <p className="text-xs text-gray-500">{row.original.id}</p>
                </div>
            ),
        })
    }

    if (user?.role !== 'QA' && user?.role !== 'DEV') {
        dynamicColumns.push({
            accessorKey: 'actions',
            header: 'Acciones',
            cell: ({ row }) => (
                <div className="flex space-x-2">
                    {row.original.status === 'PENDING' && (
                        <>
                            <button
                                className="px-3 py-1.5 text-sm font-medium rounded-md bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                                onClick={() => handleStatusUpdate(row.original.id, 'APPROVED', row.original.userId)}
                            >
                                Aprobar
                            </button>
                            <button
                                className="px-3 py-1.5 text-sm font-medium rounded-md bg-red-600 text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
                                onClick={() => handleStatusUpdate(row.original.id, 'REJECTED', row.original.userId)}
                            >
                                Rechazar
                            </button>
                        </>
                    )}
                    {row.original.status !== 'PENDING' && (
                        <span className="text-gray-400 text-sm">
                            Resuelto
                        </span>
                    )}
                </div>
            ),
        })
    }

    const { mutate: updateRequest, isPending: isUpdating } = useUpdateAccessRequest({
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: accessRequestKeys.all
            });
            toast.success('Solicitud de acceso actualizada exitosamente');
        },
        onError: (error: any) => {
            toast.error(`Error al actualizar solicitud: ${error.message}`);
        }
    });

    const handleStatusUpdate = (id: number, status: 'APPROVED' | 'REJECTED', userId?: number) => {
        updateRequest({
            id,
            data: { status, userId }
        }, {
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: accessRequestKeys.all })
                toast.success(`Solicitud ${status === 'APPROVED' ? 'aprobada' : 'rechazada'}`)
            },
            onError: (error) => {
                toast.error(`Error al actualizar: ${error.message}`)
            }
        })
    }

    return (
        <>
            {!isLoading && (!data || data.length === 0) ? (
                <div className="text-center py-8">
                    <p className="text-gray-500">No se encontraron solicitudes</p>
                </div>
            ) : (
                <BaseTable
                    data={data || []}
                    columns={dynamicColumns}
                    isLoading={isLoading}
                    className={className}
                />
            )}
        </>
    )
}
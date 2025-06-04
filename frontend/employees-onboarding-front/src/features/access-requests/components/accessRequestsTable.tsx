'use client'

import { ColumnDef } from '@tanstack/react-table'
import { useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { AccessRequestsResponse, AccessRequestStatus } from '../../../entities/access-request/accessRequestTypes'
import { BaseTable } from '@/shared/ui/table/baseTable'
import { accessRequestKeys, useAccessRequests, useUpdateAccessRequest, useUserAccessRequests } from '../hooks/useAccessRequests'
import { useAuthStore } from '@/features/auth/model/authStore'
import { accessRequestStatusToColor, formatDate } from '../../../shared/lib/utils'
import { useUser } from '@/features/user-management/hooks/usersHook'

const STATUS_LABELS: Record<AccessRequestStatus, string> = {
    PENDING: 'Pendiente',
    APPROVED: 'Aprobado',
    REJECTED: 'Rechazado'
}

const ACTION_BUTTON_STYLES = {
    approve: 'px-3 py-1.5 text-sm font-medium rounded-md bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
    reject: 'px-3 py-1.5 text-sm font-medium rounded-md bg-red-600 text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2'
}

const BASE_COLUMNS: ColumnDef<AccessRequestsResponse>[] = [
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
            const status = row.original.status as AccessRequestStatus
            const { bgClass, textClass } = accessRequestStatusToColor(status)

            return (
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${bgClass} ${textClass}`}>
                    {STATUS_LABELS[status]}
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

const UserCell = ({ userId }: { userId: number }) => {
    const { data: userData } = useUser(userId)

    return (
        <div>
            <p className="font-medium text-gray-900">{userData?.name || 'Cargando...'}</p>
            <p className="text-xs text-gray-500">{userData?.email || 'Cargando...'}</p>
            <p className="text-xs text-gray-500">ID: {userId}</p>
        </div>
    )
}

const RequestIdCell = ({ requestId }: { requestId: number }) => (
    <div>
        <p className="text-xs text-gray-500">{requestId}</p>
    </div>
)

const ActionsCell = ({
    requestId,
    status,
    userId,
    onStatusUpdate
}: {
    requestId: number
    status: AccessRequestStatus
    userId?: number
    onStatusUpdate: (id: number, status: 'APPROVED' | 'REJECTED', userId?: number) => void
}) => {
    if (status !== 'PENDING') {
        return <span className="text-gray-400 text-sm">Resuelto</span>
    }

    return (
        <div className="flex space-x-2">
            <button
                className={ACTION_BUTTON_STYLES.approve}
                onClick={() => onStatusUpdate(requestId, 'APPROVED', userId)}
            >
                Aprobar
            </button>
            <button
                className={ACTION_BUTTON_STYLES.reject}
                onClick={() => onStatusUpdate(requestId, 'REJECTED', userId)}
            >
                Rechazar
            </button>
        </div>
    )
}

const EmptyState = () => (
    <div className="text-center py-8">
        <p className="text-gray-500">No se encontraron solicitudes</p>
    </div>
)

export function AccessRequestsTable({ className = '' }: { className?: string }) {
    const queryClient = useQueryClient()
    const { user } = useAuthStore()
    const isAdmin = user?.role === 'PM' || user?.role === 'AC'

    const {
        data,
        isLoading
    } = isAdmin
            ? useAccessRequests()
            : useUserAccessRequests(user?.id || 0);


    const { mutate: updateRequest } = useUpdateAccessRequest({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: accessRequestKeys.all })
            toast.success('Solicitud de acceso actualizada exitosamente')
        },
        onError: (error: Error) => {
            toast.error(`Error al actualizar solicitud: ${error.message}`)
        }
    })

    const handleStatusUpdate = (id: number, status: 'APPROVED' | 'REJECTED', userId?: number) => {
        updateRequest({ id, data: { status, userId } })
    }

    const buildColumns = () => {
        const columns = [...BASE_COLUMNS]

        if (isAdmin) {
            columns.unshift(
                {
                    accessorKey: 'requestId',
                    header: 'Solicitud',
                    cell: ({ row }) => <RequestIdCell requestId={row.original.id} />
                },
                {
                    accessorKey: 'userName',
                    header: 'Usuario',
                    cell: ({ row }) => <UserCell userId={row.original.userId} />
                }
            )
        }

        if (user?.role !== 'QA' && user?.role !== 'DEV') {
            columns.push({
                accessorKey: 'actions',
                header: 'Acciones',
                cell: ({ row }) => (
                    <ActionsCell
                        requestId={row.original.id}
                        status={row.original.status}
                        userId={row.original.userId}
                        onStatusUpdate={handleStatusUpdate}
                    />
                )
            })
        }

        return columns
    }

    if (!isLoading && (!data || data.length === 0)) {
        return <EmptyState />
    }

    return (
        <BaseTable
            data={data || []}
            columns={buildColumns()}
            isLoading={isLoading}
            className={className}
        />
    )
}

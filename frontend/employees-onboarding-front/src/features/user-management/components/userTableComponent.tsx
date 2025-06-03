'use client'

import { BaseTable } from '@/shared/ui/table/baseTable'
import { ColumnDef } from '@tanstack/react-table'
import { UserResponse, UserStatus } from '@/entities/user/userTypes'
import { useUsers } from '@/features/user-management/hooks/usersHook'
import { userStatusToColor } from '@/shared/lib/utils'

const columns: ColumnDef<UserResponse>[] = [
    {
        accessorKey: 'name',
        header: 'Nombre',
        cell: ({ row }) => (
            <div className="font-medium">
                {row.original.name}
                <p className="text-xs text-gray-500">{row.original.email}</p>
            </div>
        ),
    },
    {
        accessorKey: 'role',
        header: 'Rol',
        cell: ({ row }) => {
            const roleNames: Record<string, string> = {
                'DEV': 'Desarrollador',
                'QA': 'QA',
                'PM': 'Project Manager',
                'AC': 'Administrador',
            }
            return <span>{roleNames[row.original.role] || row.original.role}</span>
        },
    },
    {
        accessorKey: 'status',
        header: 'Estado',
        cell: ({ row }) => {
            const status = row.original.status as UserStatus;
            const { bgClass, textClass } = userStatusToColor(status);

            const statusLabels: Record<UserStatus, string> = {
                PENDING: 'Pendiente',
                APPROVED: 'Aprobado',
                DISABLED: 'Deshabilitado',
                REJECTED: 'Rechazado'
            };

            return (
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${bgClass} ${textClass}`}>
                    {statusLabels[status]}
                </span>
            );
        },
    }

]

export function UsersTable() {
    const { data: users, isLoading, error } = useUsers()

    const handleRowClick = (user: UserResponse) => {
        console.log('Usuario seleccionado:', user)
    }

    return (
        <div className="space-y-4">
            <div className="flex justify-between items-center">
                <h2 className="text-xl font-semibold">Lista de Usuarios</h2>
                { }
            </div>

            <BaseTable<UserResponse>
                data={users || []}
                columns={columns}
                isLoading={isLoading}
                error={error}
                onRowClick={handleRowClick}
                className="shadow-sm"
            />
        </div>
    )
}
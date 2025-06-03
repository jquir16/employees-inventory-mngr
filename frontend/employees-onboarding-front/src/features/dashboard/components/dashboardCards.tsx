'use client'

import { UserRole } from '@/entities/user/userTypes'
import { ActionCard } from '@/shared/ui/cards/actionCard'

export default function DashboardCards({ userRole }: { userRole?: UserRole }) {
    return (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {(userRole === 'PM' || userRole === 'AC') && (
                <ActionCard
                    title="Crear Usuario"
                    description="Registra un nuevo colaborador en el sistema"
                    icon="👤"
                    href="users"
                    actionText="Nuevo Usuario"
                    variant="primary"
                    iconBg="bg-blue-500"
                />
            )}

            {(userRole === 'PM' || userRole === 'AC') && (
                <ActionCard
                    title="Asignar Inventario"
                    description="Gestiona la asignación de equipos"
                    icon="💻"
                    href="inventory"
                    actionText="Ver Inventario"
                    variant="primary"
                    iconBg="bg-green-100 text-green-800"
                />
            )}

            <ActionCard
                title="Solicitar Accesos"
                description="Solicita permisos para sistemas"
                icon="🔑"
                href="access-request"
                actionText="Nueva Solicitud"
                variant="primary"
                iconBg="bg-purple-100 text-purple-800"
            />
        </div>
    )
}
'use client'

import { UserForm } from "@/features/user-management/components/userFormComponent"
import { UsersTable } from "@/features/user-management/components/userTableComponent"

export default function UsersPage() {
    return (
        <div className="space-y-8">
            <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Editar o crear Usuario</h2>
                <UserForm />
            </div>

            <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Solicitudes Recientes</h2>
                <UsersTable />
            </div>
        </div>
    )
}
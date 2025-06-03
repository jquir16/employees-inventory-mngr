'use client'

import { AccessRequestsTable } from '@/features/access-requests/components/accessRequestsTable'
import { AccessRequestForm } from '@/features/access-requests/components/accessRequestForm'

export default function AccessRequestsPage() {
    return (
        <div className="space-y-8">
            <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Nueva Solicitud de Acceso</h2>
                <AccessRequestForm />
            </div>

            <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Solicitudes Recientes</h2>
                <AccessRequestsTable />
            </div>
        </div>
    )
}
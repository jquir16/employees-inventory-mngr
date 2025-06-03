'use client'

import DashboardCards from '@/features/dashboard/components/dashboardCards'
import { AccessRequestsTable } from '@/features/access-requests/components/accessRequestsTable'
import { useAuthStore } from '@/features/auth/model/authStore'
import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useUser } from '@/features/user-management/hooks/usersHook'


export default function DashboardPage() {
  const token = useAuthStore(state => state.token);
  const updateUser = useAuthStore(state => state.updateUser);
  const router = useRouter();
  const decodedUserId = useAuthStore(state => state.user?.id);
  const user = decodedUserId ? useUser(decodedUserId) : null;

  useEffect(() => {
    if (user?.data) {
      updateUser({
        name: user.data.name,
        role: user.data.role,
        status: user.data.status
      });
    }
  }, [user?.data, updateUser]);

  useEffect(() => {
    if (!token) {
      router.push('/public/login');
    }
  }, [token, router]);

  return (
    <div className="space-y-8">
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-gray-900">Bienvenido, {user?.data?.name}</h2>
        <p className="mt-1 text-sm text-gray-500">
          Panel de gestión de nuevos colaboradores
        </p>
      </div>

      <DashboardCards userRole={user?.data?.role} />

      <div className="mt-8">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Solicitudes Recientes</h3>
        <AccessRequestsTable />
      </div>
    </div>
  );
}
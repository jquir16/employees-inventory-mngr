import { redirect } from 'next/navigation'
import { useAuthStore } from '@/features/auth/model/authStore'
import DashboardNav from '@/features/dashboard/components/dashboardNav'

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const { token, user } = useAuthStore.getState()
  
  if (!token || !user) {
    redirect('/login')
  }

  return (
    <div className="flex h-screen bg-gray-50">
      <DashboardNav userRole={user.role} />
      
      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="bg-white shadow-sm">
          <div className="px-4 py-4 sm:px-6 lg:px-8 flex justify-between items-center">
            <h1 className="text-lg font-semibold text-gray-900">
              Panel de Gestión
            </h1>
            <div className="flex items-center space-x-4">
              <span className="text-sm font-medium text-gray-500">
                {user.name}
              </span>
              {}
            </div>
          </div>
        </header>
        
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
          {children}
        </main>
      </div>
    </div>
  )
}
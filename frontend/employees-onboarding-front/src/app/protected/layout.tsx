import { ProtectedRoute } from '../../shared/ui/protected-route/protectedRoute'
import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import '../globals.css'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Dashboard | Gestión de Colaboradores',
  description: 'Panel de administración',
}

export default function ProtectedLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className={`${inter.className} min-h-screen bg-gray-50`}>
      <header className="bg-white shadow-sm">
        {}
      </header>
      <ProtectedRoute requiredRoles={['DEV', 'AC', 'PM', 'QA']}>
        <main className="py-6 px-4 sm:px-6 lg:px-8">
          {children}
        </main>
      </ProtectedRoute>
    </div>
  )
}
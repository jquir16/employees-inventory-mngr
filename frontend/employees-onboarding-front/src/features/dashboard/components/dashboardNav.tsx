'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { UserRole } from '@/entities/user/userTypes'

const navigation = [
  { name: 'Resumen', href: '/dashboard', icon: '🏠', roles: ['admin', 'manager', 'user'] },
  { name: 'Usuarios', href: '/dashboard/users', icon: '👥', roles: ['admin', 'manager'] },
  { name: 'Accesos', href: '/dashboard/access', icon: '🔑', roles: ['admin', 'manager'] },
  { name: 'Inventario', href: '/dashboard/inventory', icon: '💻', roles: ['admin'] },
]

export default function DashboardNav({ userRole }: { userRole: UserRole }) {
  const pathname = usePathname()

  return (
    <nav className="hidden md:flex md:w-64 md:flex-col md:fixed md:inset-y-0 bg-white border-r border-gray-200">
      <div className="flex-1 flex flex-col min-h-0 pt-8">
        <div className="flex-1 flex flex-col pb-4 overflow-y-auto">
          <div className="space-y-1 px-4">
            {navigation.map((item) => (
              item.roles.includes(userRole) && (
                <Link
                  key={item.name}
                  href={item.href}
                  className={`group flex items-center px-3 py-3 text-sm font-medium rounded-md ${
                    pathname === item.href
                      ? 'bg-indigo-50 text-indigo-700'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                >
                  <span className="mr-3 text-lg">{item.icon}</span>
                  {item.name}
                </Link>
              )
            ))}
          </div>
        </div>
      </div>
    </nav>
  )
}
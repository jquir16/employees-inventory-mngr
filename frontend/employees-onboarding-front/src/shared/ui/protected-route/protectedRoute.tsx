'use client'

import { useAuthStore } from '@/features/auth/model/authStore'
import { useRouter } from 'next/navigation'
import { ReactNode, useEffect } from 'react'

export function ProtectedRoute({
  children,
  requiredRoles,
}: {
  children: ReactNode
  requiredRoles: string[]
}) {
  const router = useRouter()
  const { token, user } = useAuthStore()

  useEffect(() => {
    if (!token) {
      router.push('/public/login')
    } else if (user?.role && !requiredRoles.includes(user.role)) {
      router.push('/public/login')
    }
  }, [token, user, requiredRoles, router])

  if (!token || (user?.role && !requiredRoles.includes(user.role))) {
    return null
  }

  return <>{children}</>
}
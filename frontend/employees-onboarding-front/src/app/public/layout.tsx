import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import '../globals.css'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Login | Gestión de Colaboradores',
}

export default function PublicLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <main className={`${inter.className} min-h-screen flex items-center justify-center bg-gray-50`}>
      {children}
    </main>
  )
}
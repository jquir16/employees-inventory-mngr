import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

const protectedRoutes = ['/protected/dashboard', '/protected/settings']
const publicRoutes = ['/public/login', '/public/forgot-password']

export function middleware(request: NextRequest) {
  const token = request.cookies.get('auth-token')?.value
  const pathname = request.nextUrl.pathname
  
  if (protectedRoutes.some(route => pathname.startsWith(route)) && !token) {
    return NextResponse.redirect(new URL('/public/login', request.url))
  }

  if (publicRoutes.some(route => pathname.startsWith(route)) && token) {
    return NextResponse.redirect(new URL('/protected/dashboard', request.url))
  }

  return NextResponse.next()
}
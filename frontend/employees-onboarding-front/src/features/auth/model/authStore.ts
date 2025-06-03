import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { jwtDecode } from 'jwt-decode';
import { UserRole, UserStatus } from '@/entities/user/userTypes';



interface AuthState {
  token: string | null
  user: {
    id: number
    email: string
    name: string,
    role?: UserRole,
    status?: UserStatus
  } | null
  login: (token: string) => void
  logout: () => void
  isAuthenticated: () => boolean
  hasRole: (role: string) => boolean
  isLoading: boolean
  updateUser: (userData: Partial<AuthState['user']>) => void
}
export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      isLoading: true,
      login: (token) => {
        const decoded = jwtDecode<{ sub: string; jti: number, name: string }>(token)
        set({
          token,
          isLoading: false,
          user: {
            id: decoded.jti,
            email: decoded.sub,
            name: decoded.name,
            role: undefined,
            status: undefined
          },
        })
      },
      logout: () => set({ token: null, user: null, isLoading: false }),
      isAuthenticated: () => !!get().token,
      hasRole: (role) => {
        const currentUser = get().user
        return currentUser?.role === role
      },
      updateUser: (userData) => {
        set(state => ({
          user: state.user ? { ...state.user, ...userData } : null
        }))
      }
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ token: state.token })
    }
  )
)
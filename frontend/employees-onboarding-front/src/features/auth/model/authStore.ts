import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { jwtDecode } from 'jwt-decode';
import { UserRole, UserStatus, User } from '@/entities/user/userTypes';
import { AuthState, DecodedToken } from '@/entities/auth/authTypes';


const STORAGE_NAME = 'auth-storage';
const INITIAL_STATE = {
  token: null,
  user: null,
  isLoading: true,
};

const decodeToken = (token: string): DecodedToken => {
  return jwtDecode<DecodedToken>(token);
};

const createUserFromToken = (decodedToken: DecodedToken): User => ({
  id: decodedToken.jti,
  email: decodedToken.sub,
  name: decodedToken.name,
});

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      ...INITIAL_STATE,
      login: (token) => {
        const decoded = decodeToken(token);
        set({
          token,
          isLoading: false,
          user: createUserFromToken(decoded),
        });
      },
      logout: () => set({ ...INITIAL_STATE, isLoading: false }),
      isAuthenticated: () => Boolean(get().token),
      hasRole: (role) => get().user?.role === role,
      updateUser: (userData) => {
        set((state) => ({
          user: state.user ? { ...state.user, ...userData } : null,
        }));
      },
    }),
    {
      name: STORAGE_NAME,
      partialize: (state) => ({ token: state.token }),
    }
  )
);
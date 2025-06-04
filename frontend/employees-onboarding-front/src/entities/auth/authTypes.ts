import { User, UserRole } from "../user/userTypes";

export interface LoginFormValues {
  email: string
  password: string
}

export interface AuthResponse {
  access_token: string
  refresh_token: string
}

export interface DecodedToken {
  sub: string;
  jti: number;
  name: string;
}


export interface AuthState {
  token: string | null;
  user: User | null;
  isLoading: boolean;
  login: (token: string) => void;
  logout: () => void;
  isAuthenticated: () => boolean;
  hasRole: (role: UserRole) => boolean;
  updateUser: (userData: Partial<User>) => void;
}
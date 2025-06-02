export interface LoginFormValues {
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  refreshToken: string
}
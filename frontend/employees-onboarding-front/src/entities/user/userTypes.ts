export type UserRole = 'DEV' | 'QA' | 'PM' | 'AC';
export type UserStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'DISABLED';

export interface User {
  id: number;
  name: string;
  email: string;
  role?: UserRole;
  status?: UserStatus;
  createdAt?: Date;
  updated_at?: Date;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  role: UserRole;
  status?: UserStatus;
}

export interface UpdateUserRequest {
  name?: string;
  email?: string;
  password?: string;
  role?: UserRole;
  status?: UserStatus;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  status: UserStatus;
}
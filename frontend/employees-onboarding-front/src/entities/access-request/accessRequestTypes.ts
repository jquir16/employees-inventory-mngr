export interface AccessRequestsResponse {
  id: number;
  userId: number;
  systems: string[];
  status: AccessRequestStatus;
  createdAt: string;
}

export type AccessRequestStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'COMPLETED'
  | string;

export const SYSTEM_OPTIONS = [
  { value: 'GITHUB', label: 'Github' },
  { value: 'HR_SYSTEM', label: 'Sistema de RH' },
  { value: 'EMAIL', label: 'Correo Corporativo' },
  { value: 'VPN', label: 'Acceso VPN' },
  { value: 'CRM', label: 'Sistema CRM' },
];

export type AccessRequest = {
  id: number
  userId: number
  userName: string
  userEmail: string
  systems: string[]
  status: AccessRequestStatus
  createdAt: string
  updatedAt: string
}

export type AccessRequestCreate = {
  userId: number
  systems: string[]
  status: string
}

export type AccessRequestUpdate = {
  systems?: string[]
  userId?: number
  status?: AccessRequestStatus
}
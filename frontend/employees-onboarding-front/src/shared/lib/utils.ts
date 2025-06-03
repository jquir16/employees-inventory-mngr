import { AccessRequestStatus } from "@/entities/access-request/accessRequestTypes"
import { UserStatus } from "@/entities/user/userTypes"


type StatusColorClasses = {
  bgClass: string
  textClass: string
}

export function userStatusToColor(status: UserStatus): StatusColorClasses {
  const statusMap: Record<UserStatus, StatusColorClasses> = {
    PENDING: { bgClass: 'bg-yellow-100', textClass: 'text-yellow-800' },
    APPROVED: { bgClass: 'bg-green-100', textClass: 'text-green-800' },
    DISABLED: { bgClass: 'bg-red-100', textClass: 'text-red-800' },
    REJECTED: { bgClass: 'bg-red-100', textClass: 'text-red-800' }, 
  };

  return statusMap[status] || { bgClass: 'bg-gray-100', textClass: 'text-gray-800' };
}

export function accessRequestStatusToColor(status: AccessRequestStatus): StatusColorClasses {
  const statusMap: Record<AccessRequestStatus, StatusColorClasses> = {
    PENDING: { bgClass: 'bg-yellow-100', textClass: 'text-yellow-800' },
    APPROVED: { bgClass: 'bg-green-100', textClass: 'text-green-800' },
    REJECTED: { bgClass: 'bg-red-100', textClass: 'text-red-800' },
  };

  return statusMap[status] || { bgClass: 'bg-gray-100', textClass: 'text-gray-800' };
}

export const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export const truncateText = (text: string, maxLength: number): string => {
  return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text
}
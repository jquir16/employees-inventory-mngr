import { useAuthStore } from "@/features/auth/model/authStore";
import axios from 'axios';

const axiosClient = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/',
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosClient.interceptors.request.use((config: any) => {
    const token = useAuthStore.getState().token

    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    return config
});

axiosClient.interceptors.response.use(
    (response: any) => response,
    (error: any) => {
        if (error.response) {
            const { status, data } = error.response

            if (status === 403) {
                console.error('Acceso denegado - Token puede ser inválido o expirado')
            }

            return Promise.reject({
                status,
                message: data.message || 'Unknown error',
                code: data.code || 'UNKNOWN_ERROR'
            })
        }
        return Promise.reject(error)
    }
)

export default axiosClient;
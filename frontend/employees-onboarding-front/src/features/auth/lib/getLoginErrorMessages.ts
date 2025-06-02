export default function getLoginErrorMessage(error: any): string {
    if (error.response) {
        switch (error.response.status) {
            case 400: return 'Solicitud inválida. Revisa los datos ingresados.';
            case 401: return 'Credenciales inválidas. Intenta de nuevo.';
            case 403: return 'No tienes permisos para acceder.';
            case 500: return 'Error interno del servidor. Intenta más tarde.';
            default: return error.response.data?.message || 'Error desconocido. Intenta de nuevo.';
        }
    } else if (error.request) {
        return 'No se pudo conectar con el servidor.';
    } else {
        return error.message || 'Error desconocido. Intenta de nuevo.';
    }
}
const axios = require('axios').default;

const axiosClient = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/',
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosClient.interceptors.response.use(
    (response: any) => response,
    (error: any) => {
        if (error.response) {
            const { status, data } = error.response
            return Promise.reject({ status, message: data.message || 'Unknown error' })
        }
        return Promise.reject(error)
    }
);

export default axiosClient;
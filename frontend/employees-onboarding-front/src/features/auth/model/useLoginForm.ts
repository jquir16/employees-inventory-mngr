import { useFormik } from 'formik'
import * as Yup from 'yup'
import { useRouter } from 'next/navigation'
import { useLogin } from '../hooks/loginHook'
import { toast } from 'react-hot-toast'
import getLoginErrorMessage from '../lib/getLoginErrorMessages'

export const useLoginForm = () => {
    const router = useRouter()
    const { mutate: login, isPending } = useLogin({
        onSuccess: (data) => {
            localStorage.setItem('token', data.token)
            localStorage.setItem('refreshToken', data.refreshToken)
            toast.success('¡Bienvenido!', { duration: 6000 })
            router.push('/dashboard')
        },
        onError: (error) => {
            toast.error(getLoginErrorMessage(error), { duration: 7000 })
        }
    })

    const formik = useFormik({
        initialValues: { email: '', password: '' },
        validationSchema: Yup.object({
            email: Yup.string().email('Email invalido').required('Required'),
            password: Yup.string().min(6, 'La contraseña debe tener al menos 6 caracteres').max(20, 'La contraseña no puede tener más de 20 caracteres').required('Required')
        }),
        onSubmit: (values) => login(values)
    })

    return { formik, isPending }
}
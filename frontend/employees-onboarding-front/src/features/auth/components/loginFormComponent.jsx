'use client'

import Link from 'next/link'
import InputField from '@/shared/ui/form/inputField'
import LoadingButton from '@/shared/ui/form/loadingButton'
import { useLoginForm } from '../model/useLoginForm'

function LoginFormComponent() {
    const { formik, isPending } = useLoginForm()

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8 bg-white p-8 rounded-lg shadow-md">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        Gestión de Colaboradores
                    </h2>
                    <p className="mt-2 text-center text-sm text-gray-600">
                        Sistema de gestión para nuevos ingresos
                    </p>
                </div>
                <form className="mt-8 space-y-6" onSubmit={formik.handleSubmit}>
                    <div className="rounded-md shadow-sm space-y-4">
                        <InputField
                            id="email"
                            label="Email"
                            type="email"
                            value={formik.values.email}
                            onChange={formik.handleChange}
                            onBlur={formik.handleBlur}
                            error={formik.errors.email}
                            touched={formik.touched.email}
                            autoComplete="email"
                        />
                        <InputField
                            id="password"
                            label="Contraseña"
                            type="password"
                            value={formik.values.password}
                            onChange={formik.handleChange}
                            onBlur={formik.handleBlur}
                            error={formik.errors.password}
                            touched={formik.touched.password}
                            autoComplete="current-password"
                        />
                    </div>
                    <LoadingButton loading={isPending}>Iniciar sesión</LoadingButton>
                </form>
            </div>
        </div>
    )
}

export default LoginFormComponent
'use client'

import InputField from '@/shared/ui/form/inputField'
import LoadingButton from '@/shared/ui/form/loadingButton'
import { useUserForm } from '../model/useUserForm'

export function UserForm() {
    const { formik, isPending } = useUserForm()

    return (
        <form onSubmit={formik.handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <InputField
                    id="id"
                    label="ID de Usuario"
                    type="number"
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    value={formik.values.id ?? ''}
                    error={formik.errors.id}
                    touched={formik.touched.id}
                    placeholder="(solo para editar)"

                />
                <InputField
                    id="name"
                    label="Nombre *"
                    type="text"
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    value={formik.values.name}
                    error={formik.errors.name}
                    touched={formik.touched.name}
                />
                <InputField
                    id="email"
                    label="Correo Electrónico *"
                    type="email"
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    value={formik.values.email}
                    error={formik.errors.email}
                    touched={formik.touched.email}
                />
                <InputField
                    id="password"
                    label="Contraseña"
                    type="password"
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    value={formik.values.password ?? ''}
                    error={formik.errors.password}
                    touched={formik.touched.password}
                />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label htmlFor="role" className="block text-sm font-medium text-gray-700 mb-1">
                        Rol *
                    </label>
                    <select
                        id="role"
                        name="role"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.role}
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    >
                        <option value="DEV">Desarrollador</option>
                        <option value="QA">QA</option>
                        <option value="PM">Project Manager</option>
                        <option value="AC">Agile Coach</option>
                    </select>
                    {formik.touched.role && formik.errors.role && (
                        <p className="mt-1 text-sm text-red-600">{formik.errors.role}</p>
                    )}
                </div>

                <div>
                    <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-1">
                        Estado *
                    </label>
                    <select
                        id="status"
                        name="status"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.status}
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                    >
                        <option value="PENDING">Pendiente</option>
                        <option value="APPROVED">Aprobado</option>
                        <option value="DISABLED">Deshabilitado</option>
                    </select>
                    {formik.touched.status && formik.errors.status && (
                        <p className="mt-1 text-sm text-red-600">{formik.errors.status}</p>
                    )}
                </div>
            </div>

            <div className="flex justify-end">
                <LoadingButton
                    loading={isPending}
                    disabled={!formik.isValid}
                >
                    {isPending ? 'Guardando...' : 'Guardar Usuario'}
                </LoadingButton>
            </div>
        </form>
    )
}

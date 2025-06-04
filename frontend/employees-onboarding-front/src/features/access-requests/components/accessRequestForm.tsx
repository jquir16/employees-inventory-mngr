'use client'

import { SYSTEM_OPTIONS } from "@/entities/access-request/accessRequestTypes";
import { useAccessRequestForm } from "../model/useAccessRequestForm";
import { useAuthStore } from "@/features/auth/model/authStore";
import { useUsers } from "@/features/user-management/hooks/usersHook";
import InputField from "@/shared/ui/form/inputField";
import LoadingButton from "@/shared/ui/form/loadingButton";

export function AccessRequestForm() {
    const { formik, isPending, existingRequest, isPM } = useAccessRequestForm();
    const user = useAuthStore(state => state.user);
    const { data: users = [] } = useUsers();
    const isDevOrQA = user?.role === 'DEV' || user?.role === 'QA';

    const handleSystemChange = (systemId: string) => {
        const currentSystems = formik.values.systems;
        const newSystems = currentSystems.includes(systemId)
            ? currentSystems.filter(id => id !== systemId)
            : [...currentSystems, systemId];

        formik.setFieldValue('systems', newSystems);
    };

    const handleUserChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const value = e.target.value === '' ? null : Number(e.target.value);
        formik.setFieldValue('userId', value);
    };

    return (
        <form onSubmit={formik.handleSubmit} className="space-y-6">
            {/* Solo mostrar el campo de usuario para PM */}
            {isPM && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label htmlFor="userId" className="block text-sm font-medium text-gray-700 mb-1">
                            Usuario *
                        </label>
                        <select
                            id="userId"
                            name="userId"
                            value={formik.values.userId ?? ''}
                            onChange={handleUserChange}
                            onBlur={formik.handleBlur}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
                        >
                            <option value="">Seleccione un usuario</option>
                            {users.map(user => (
                                <option key={user.id} value={user.id}>
                                    {user.name} ({user.role})
                                </option>
                            ))}
                        </select>
                        {formik.touched.userId && formik.errors.userId && (
                            <p className="mt-1 text-sm text-red-600">{formik.errors.userId}</p>
                        )}
                    </div>
                </div>
            )}

            <div>
                <fieldset>
                    <legend className="block text-sm font-medium text-gray-700 mb-2">
                        Sistemas Requeridos *
                    </legend>
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                        {SYSTEM_OPTIONS.map((system) => (
                            <div key={system.label} className="flex items-start">
                                <div className="flex h-5 items-center">
                                    <input
                                        id={`system-${system.label}`}
                                        name="systems"
                                        type="checkbox"
                                        checked={formik.values.systems.includes(system.label)}
                                        onChange={() => handleSystemChange(system.label)}
                                        onBlur={formik.handleBlur}
                                        className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                                    />
                                </div>
                                <div className="ml-3 text-sm">
                                    <label htmlFor={`system-${system.label}`} className="font-medium text-gray-700">
                                        {system.label}
                                    </label>
                                </div>
                            </div>
                        ))}
                    </div>
                    {formik.touched.systems && formik.errors.systems && (
                        <p className="mt-1 text-sm text-red-600">{formik.errors.systems}</p>
                    )}
                </fieldset>
            </div>

            <div className="flex justify-end">
                <LoadingButton
                    loading={isPending}
                    disabled={!formik.isValid}
                >
                    {existingRequest ? 'Actualizar Solicitud' : 'Crear Solicitud'}
                </LoadingButton>
            </div>
        </form>
    )
}

'use client'

import { useInventoryForm } from "../model/useInventoryForm";
import InputField from "@/shared/ui/form/inputField";
import LoadingButton from "@/shared/ui/form/loadingButton";
import { useUsers } from "@/features/user-management/hooks/usersHook";

export function InventoryForm() {
  const { formik, isPending } = useInventoryForm();
  const { data: users = [] } = useUsers();

  const handleUserChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value === '' ? null : Number(e.target.value);
    formik.setFieldValue('assignedTo', value);
  };

  return (
    <form onSubmit={formik.handleSubmit} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <InputField
          id="id"
          label="ID de Inventario"
          type="number"
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          value={formik.values.id ?? ''}
          error={formik.errors.id}
          touched={formik.touched.id}
          placeholder="(solo para editar)"
        />

        <InputField
          id="serialNumber"
          label="Número de Serie *"
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          value={formik.values.serialNumber}
          error={formik.errors.serialNumber}
          touched={formik.touched.serialNumber}
        />
      </div>

      <InputField
        id="description"
        label="Descripción"
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
        value={formik.values.description}
        error={formik.errors.description}
        touched={formik.touched.description}
      />

      <div>
        <label htmlFor="assignedTo" className="block text-sm font-medium text-gray-700 mb-1">
          Asignar a Usuario
        </label>
        <select
          id="assignedTo"
          name="assignedTo"
          value={formik.values.assignedTo ?? ''}
          onChange={handleUserChange}
          onBlur={formik.handleBlur}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
        >
          <option value="">Sin asignar</option>
          {users.map(user => (
            <option key={user.id} value={user.id}>
              {user.name} ({user.role})
            </option>
          ))}
        </select>
        {formik.touched.assignedTo && formik.errors.assignedTo && (
          <p className="mt-1 text-sm text-red-600">{formik.errors.assignedTo}</p>
        )}
      </div>

      <div className="flex justify-end">
                <LoadingButton
                    loading={isPending}
                    disabled={!formik.isValid}
                >
                    {isPending ? 'Enviando...' : 'Enviar Solicitud'}
                </LoadingButton>
            </div>
    </form>
  );
}

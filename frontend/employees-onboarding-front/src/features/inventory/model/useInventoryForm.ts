'use client'

import * as Yup from 'yup';
import { useFormik } from 'formik';
import toast from 'react-hot-toast';
import { useQueryClient } from '@tanstack/react-query';
import { useEffect, useMemo } from 'react';
import {
    CreateInventoryRequest,
    InventoryResponse,
    UpdateInventoryRequest
} from '@/entities/inventory/inventoryTypes';
import {
    useCreateInventory,
    useUpdateInventory,
    useGetAllInventory,
    useAssignInventory,
    useUnAssignInventory,
    inventoryKeys
} from '../hooks/useInventory';

export const useInventoryForm = () => {
    const queryClient = useQueryClient();
    const { data: allInventory } = useGetAllInventory();

    const validationSchema = Yup.object({
        id: Yup.number().nullable(),
        description: Yup.string()
            .nullable()
            .max(255, 'Máximo 255 caracteres'),
        serialNumber: Yup.string()
            .required('El número de serie es requerido')
            .min(3, 'Debe tener al menos 3 caracteres'),
        assignedTo: Yup.number()
            .nullable()
            .typeError('Debe ser un número')
            .positive('Debe ser un número positivo'),
    });

    const { mutate: createInventory, isPending: isCreating } = useCreateInventory({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: inventoryKeys.lists() });
            toast.success('Inventario creado exitosamente');
            formik.resetForm();
        },
        onError: (err: any) => {
            toast.error(`Error al crear: ${err.message}`);
        }
    });

    const { mutate: updateInventory, isPending: isUpdating } = useUpdateInventory({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: inventoryKeys.lists() });
            toast.success('Inventario actualizado exitosamente');
            formik.resetForm();
        },
        onError: (err: any) => {
            toast.error(`Error al actualizar: ${err.message}`);
        }
    });

    const { mutate: assignInventory, isPending: isAssigning } = useAssignInventory({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: inventoryKeys.lists() });
            toast.success('Inventario asignado');
        },
        onError: (err: any) => {
            toast.error(`Error al asignar: ${err.message}`);
        }
    });

    const { mutate: unAssignInventory, isPending: isUnAssigning } = useUnAssignInventory({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: inventoryKeys.lists() });
            toast.success('Inventario desasignado');
        },
        onError: (err: any) => {
            toast.error(`Error al desasignar: ${err.message}`);
        }
    });

    const formik = useFormik<CreateInventoryRequest & { id?: number | null; assignedTo?: number | null }>({
        initialValues: {
            id: null,
            description: '',
            serialNumber: '',
            assignedTo: null
        },
        validationSchema,
        onSubmit: (values) => {
            const { id, description, assignedTo, serialNumber } = values;

            if (id) {
                const data: UpdateInventoryRequest = {
                    description,
                    serialNumber,
                    ...(assignedTo !== null ? { userId: assignedTo } : {}),
                };
                updateInventory({ id, data });
            } else {
                createInventory({ serialNumber, description });
            }

            if (id && assignedTo) {
                assignInventory({ inventoryId: id, userId: assignedTo });
            } else if (id && assignedTo === null) {
                unAssignInventory({ inventoryId: id, userId: assignedTo! });
            }
        }
    });

    const existingInventory = useMemo<InventoryResponse | undefined>(() => {
        if (!formik.values.id || !allInventory) return undefined;
        return allInventory.find(item => item.id === formik.values.id);
    }, [formik.values.id, allInventory]);

    useEffect(() => {
        if (existingInventory) {
            formik.setValues({
                id: existingInventory.id,
                serialNumber: existingInventory.serialNumber,
                description: existingInventory.description ?? '',
                assignedTo: existingInventory.userId ?? null
            });
        }
    }, [existingInventory]);

    return {
        formik,
        isPending: isCreating || isUpdating || isAssigning || isUnAssigning,
        existingInventory
    };
};

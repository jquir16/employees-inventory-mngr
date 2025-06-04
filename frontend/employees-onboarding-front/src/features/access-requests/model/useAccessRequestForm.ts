'use client'

import { AccessRequestCreate, AccessRequestsResponse, SYSTEM_OPTIONS } from "@/entities/access-request/accessRequestTypes";
import {
    accessRequestKeys,
    useAccessRequests,
    useCreateAccessRequest,
    useUpdateAccessRequest,
    useUserAccessRequests,
} from "../hooks/useAccessRequests";
import toast from "react-hot-toast";
import * as Yup from 'yup';
import { useFormik } from "formik";
import { useAuthStore } from "@/features/auth/model/authStore";
import { useQueryClient } from '@tanstack/react-query';
import { useEffect, useMemo } from 'react';

type UseAccessRequestFormReturn = {
    formik: ReturnType<typeof useFormik<AccessRequestCreate>>;
    isPending: boolean;
    existingRequest?: AccessRequestsResponse;
    isPM: boolean;
};

export const useAccessRequestForm = (): UseAccessRequestFormReturn => {
    const user = useAuthStore(state => state.user);
    const queryClient = useQueryClient();

    const { id: userId = 0, role } = user || {};
    const isPM = role === 'PM';

    const { data: allRequests } = isPM ? useAccessRequests() : useUserAccessRequests(userId);

    const formik = useFormik<AccessRequestCreate>({
        initialValues: {
            userId: isPM ? 0 : userId,
            systems: [],
            status: 'PENDING',
        },
        validationSchema: Yup.object({
            userId: Yup.number()
                .required('El ID de usuario es requerido')
                .positive('El ID debe ser un número positivo')
                .integer('El ID debe ser un número entero'),
            systems: Yup.array()
                .of(Yup.string().oneOf(SYSTEM_OPTIONS.map(opt => opt.label)))
                .min(1, 'Debes seleccionar al menos un sistema')
                .required('Se requiere al menos un sistema'),
        }),
        onSubmit: (values) => {
            const data: AccessRequestCreate = {
                userId: isPM ? Number(values.userId) : userId,
                systems: values.systems,
                status: 'PENDING',
            };

            existingRequest
                ? updateRequest({ id: existingRequest.id, data })
                : createRequest(data);
        }
    });

    const targetUserId = isPM ? Number(formik.values.userId || 0) : Number(userId);
    const existingRequest = useMemo(
        () => findExistingRequest(allRequests, targetUserId),
        [allRequests, targetUserId]
    );

    const { mutate: createRequest, isPending: isCreating } = useCreateAccessRequest({
        onSuccess: () => handleMutationSuccess(queryClient, formik, 'creada'),
        onError: (error) => toast.error(`Error al crear solicitud: ${error.message}`)
    });

    const { mutate: updateRequest, isPending: isUpdating } = useUpdateAccessRequest({
        onSuccess: () => handleMutationSuccess(queryClient, formik, 'actualizada'),
        onError: (error) => toast.error(`Error al actualizar solicitud: ${error.message}`)
    });

    // Efecto para cargar los datos existentes cuando cambia el userId o se detecta una request existente
    useEffect(() => {
        if (existingRequest) {
            formik.setValues({
                userId: existingRequest.userId,
                systems: existingRequest.systems,
                status: existingRequest.status,
            });
        } else if (isPM && formik.values.userId !== 0) {
            // Si es PM y seleccionó un usuario, pero no hay request existente, resetear sistemas
            formik.setFieldValue('systems', []);
        } else if (!isPM) {
            // Si no es PM, mantener los valores del usuario actual
            formik.setValues({
                userId: userId,
                systems: formik.values.systems,
                status: 'PENDING',
            });
        }
    }, [existingRequest, isPM, userId, formik.values.userId]);

    return {
        formik,
        isPending: isCreating || isUpdating,
        existingRequest,
        isPM
    };
};

const findExistingRequest = (
    allRequests?: AccessRequestsResponse[],
    targetUserId?: number
): AccessRequestsResponse | undefined => {
    return allRequests?.find(req =>
        req.userId === targetUserId && req.status === 'PENDING'
    );
};

const handleMutationSuccess = (
    queryClient: ReturnType<typeof useQueryClient>,
    formik: ReturnType<typeof useFormik<AccessRequestCreate>>,
    action: string
) => {
    queryClient.invalidateQueries({ queryKey: accessRequestKeys.all });
    toast.success(`Solicitud de acceso ${action} exitosamente`);
    formik.resetForm();
};

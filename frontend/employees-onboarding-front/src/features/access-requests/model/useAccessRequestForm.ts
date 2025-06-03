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

export const useAccessRequestForm = () => {
    const user = useAuthStore(state => state.user);
    const userId = user?.id || 0;
    const isPM = user?.role === 'PM';
    const queryClient = useQueryClient();

    const { data: allRequests } = isPM ? useAccessRequests() : useUserAccessRequests(userId);

    const validationSchema = Yup.object({
        userId: Yup.number()
            .required('El ID de usuario es requerido')
            .positive('El ID debe ser un número positivo')
            .integer('El ID debe ser un número entero'),
        systems: Yup.array()
            .of(Yup.string().oneOf(SYSTEM_OPTIONS.map(opt => opt.label)))
            .min(1, 'Debes seleccionar al menos un sistema')
            .required('Se requiere al menos un sistema'),
        justification: Yup.string()
            .max(500, 'La justificación no puede exceder los 500 caracteres')
            .notRequired()
    });

    const { mutate: createRequest, isPending: isCreating } = useCreateAccessRequest({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: accessRequestKeys.all });
            toast.success('Solicitud de acceso creada exitosamente');
            formik.resetForm();
        },
        onError: (error: any) => {
            toast.error(`Error al crear solicitud: ${error.message}`);
        }
    });

    const { mutate: updateRequest, isPending: isUpdating } = useUpdateAccessRequest({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: accessRequestKeys.all });
            toast.success('Solicitud de acceso actualizada exitosamente');
            formik.resetForm();
        },
        onError: (error: any) => {
            toast.error(`Error al actualizar solicitud: ${error.message}`);
        }
    });

    const formik = useFormik<AccessRequestCreate>({
        initialValues: {
            userId: isPM ? 0 : userId,
            systems: [],
            status: 'PENDING',
        },
        validationSchema,
        onSubmit: (values) => {
            const targetUserId = isPM ? Number(values.userId) : userId;
            const requestData = {
                userId: targetUserId,
                systems: values.systems,
                status: 'PENDING',
            };

            if (existingRequest) {
                updateRequest({
                    id: existingRequest.id,
                    data: requestData
                });
            } else {
                createRequest(requestData);
            }
        },
    });

    const existingRequest = useMemo<AccessRequestsResponse | undefined>(() => {
        if (!allRequests) return undefined;

        const targetUserId = isPM ? Number(formik.values.userId) : userId;

        return allRequests.find((request: AccessRequestsResponse) =>
            request.userId === targetUserId &&
            request.status === 'PENDING'
        );
    }, [allRequests, formik.values.userId, userId, isPM]);

    useEffect(() => {
        if (existingRequest) {
            formik.setFieldValue('systems', existingRequest.systems);
        }
    }, [existingRequest]);

    useEffect(() => {
        if (isPM) {
            formik.setFieldValue('userId', '');
            formik.setFieldValue('systems', []);
            formik.setFieldValue('justification', '');
        }
    }, [user?.id]);

    return {
        formik,
        isPending: isCreating || isUpdating,
        existingRequest,
        isPM
    };
};
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

    const { data: allRequests } = useAccessRequestsData(isPM, userId);
    
    const formik: any = useFormik<AccessRequestCreate>({
        initialValues: getInitialValues(isPM, userId),
        validationSchema: getValidationSchema(),
        onSubmit: (values) => handleSubmit(values, isPM, userId, existingRequest, updateRequest, createRequest),
    });

    const existingRequest = useMemo(() => 
        findExistingRequest(allRequests, isPM, formik.values.userId, userId),
        [allRequests, formik.values.userId, userId, isPM]
    );

    const { mutate: createRequest, isPending: isCreating } = useCreateAccessRequest({
        onSuccess: () => handleMutationSuccess(queryClient, formik, 'creada'),
        onError: (error) => handleMutationError(error, 'crear')
    });

    const { mutate: updateRequest, isPending: isUpdating } = useUpdateAccessRequest({
        onSuccess: () => handleMutationSuccess(queryClient, formik, 'actualizada'),
        onError: (error) => handleMutationError(error, 'actualizar')
    });

    useEffect(() => {
        if (existingRequest) {
            formik.setFieldValue('systems', existingRequest.systems);
        }
    }, [existingRequest]);

    useEffect(() => {
        if (isPM) {
            formik.setFieldValue('userId', '');
            formik.setFieldValue('systems', []);
        }
    }, [userId]);

    return {
        formik,
        isPending: isCreating || isUpdating,
        existingRequest,
        isPM
    };
};

const useAccessRequestsData = (isPM: boolean, userId: number) => {
    return isPM ? useAccessRequests() : useUserAccessRequests(userId);
};

const getInitialValues = (isPM: boolean, userId: number): AccessRequestCreate => ({
    userId: isPM ? 0 : userId,
    systems: [],
    status: 'PENDING',
});

const getValidationSchema = () => Yup.object({
    userId: Yup.number()
        .required('El ID de usuario es requerido')
        .positive('El ID debe ser un número positivo')
        .integer('El ID debe ser un número entero'),
    systems: Yup.array()
        .of(Yup.string().oneOf(SYSTEM_OPTIONS.map(opt => opt.label)))
        .min(1, 'Debes seleccionar al menos un sistema')
        .required('Se requiere al menos un sistema'),
});

const findExistingRequest = (
    allRequests: AccessRequestsResponse[] | undefined,
    isPM: boolean,
    formUserId: string | number,
    userId: number
): AccessRequestsResponse | undefined => {
    if (!allRequests) return undefined;
    const targetUserId = isPM ? Number(formUserId) : userId;
    
    return allRequests.find(request => 
        request.userId === targetUserId && request.status === 'PENDING'
    );
};

const handleSubmit = (
    values: AccessRequestCreate,
    isPM: boolean,
    userId: number,
    existingRequest: AccessRequestsResponse | undefined,
    updateRequest: (params: { id: number; data: AccessRequestCreate }) => void,
    createRequest: (data: AccessRequestCreate) => void
) => {
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

const handleMutationError = (error: Error, action: string) => {
    toast.error(`Error al ${action} solicitud: ${error.message}`);
};
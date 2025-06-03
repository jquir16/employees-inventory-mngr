'use client'

import * as Yup from 'yup';
import { useFormik } from 'formik';
import { useAuthStore } from '@/features/auth/model/authStore';
import { useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { useEffect, useMemo } from 'react';
import { CreateUserRequest, UpdateUserRequest, UserResponse, UserRole, UserStatus } from '@/entities/user/userTypes';
import { AuthResponse } from '@/entities/auth/authTypes';
import { useUsers, useRegisterUser, userKeys, useUpdateUser } from '../hooks/usersHook';

export const useUserForm = () => {
    const user = useAuthStore(state => state.user);
    const isAdmin = user?.role === 'AC';
    const queryClient = useQueryClient();

    const { data: allUsers } = useUsers();

    const validationSchema = Yup.object({
        id: Yup.number()
            .positive('El ID debe ser un número positivo')
            .integer('El ID debe ser un número entero')
            .nullable(),
        name: Yup.string()
            .required('El nombre es requerido')
            .min(4, 'El nombre debe tener al menos 4 caracteres')
            .max(50, 'El nombre no puede superar los 50 caracteres')
            .matches(/^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$/, 'Solo se permiten letras y espacios'),
        email: Yup.string()
            .required('El email es requerido')
            .email('Ingrese un email válido'),
        role: Yup.string()
            .required('El rol es requerido')
            .oneOf(['DEV', 'QA', 'PM', 'AC'], 'Rol inválido'),
        status: Yup.string()
            .required('El estado es requerido')
            .oneOf(['PENDING', 'APPROVED', 'DISABLED'], 'Estado inválido'),
        password: Yup.string().when('id', (id, schema) => {
            if (!id) {
                return schema
                    .required('La contraseña es requerida')
                    .min(8, 'La contraseña debe tener al menos 8 caracteres');
            }
            return schema
                .min(8, 'La contraseña debe tener al menos 8 caracteres')
                .nullable();
        }),
    });

    const { mutate: createUser, isPending: isCreating } = useRegisterUser({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: userKeys.all });
            toast.success('Usuario creado exitosamente');
            formik.resetForm();
        },
        onError: (error: any) => {
            toast.error(`Error al crear usuario: ${error.message}`);
        }
    });

    const { mutate: updateUser, isPending: isUpdating } = useUpdateUser({
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: userKeys.all });
            toast.success('Usuario actualizado exitosamente');
            formik.resetForm();
        },
        onError: (error: any) => {
            toast.error(`Error al actualizar usuario: ${error.message}`);
        }
    });

    const formik = useFormik<CreateUserRequest & { id?: number | null }>({
        initialValues: {
            id: null,
            name: '',
            email: '',
            role: 'DEV' as UserRole,
            status: 'PENDING' as UserStatus,
            password: ''
        },
        validationSchema,
        onSubmit: (values) => {
            if (values.id) {
                const userUpdateData: UpdateUserRequest = {
                    name: values.name,
                    email: values.email,
                    role: values.role,
                    status: values.status,
                    ...(values.password && { password: values.password })
                };
                updateUser({
                    id: values.id,
                    data: userUpdateData
                });
            } else {
                const userCreateData: CreateUserRequest = {
                    name: values.name,
                    email: values.email,
                    role: values.role,
                    status: values.status,
                    password: values.password
                }

                createUser(userCreateData);
            }
        },
    });

    const existingUser = useMemo<UserResponse | undefined>(() => {
        if (!allUsers || !formik.values.id) return undefined;
        return allUsers.find((user: UserResponse) => user.id === formik.values.id);
    }, [allUsers, formik.values.id]);

    useEffect(() => {
        if (existingUser) {
            formik.setValues({
                id: existingUser.id,
                name: existingUser.name,
                email: existingUser.email,
                role: existingUser.role,
                status: existingUser.status,
                password: ''
            });
        } else if (formik.values.id && !isCreating && !isUpdating) {
            toast.error('No se encontró un usuario con ese ID');
            formik.setFieldValue('id', null);
        }
    }, [existingUser]);

    return {
        formik,
        isPending: isCreating || isUpdating,
        existingUser,
        isAdmin,
        selectedUserById: (id: number) => formik.setFieldValue('id', id)
    };
};
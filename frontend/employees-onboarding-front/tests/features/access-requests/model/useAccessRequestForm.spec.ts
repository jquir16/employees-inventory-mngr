import { renderHook, act } from '@testing-library/react';
import { useAccessRequestForm } from '../../../../src/features/access-requests/model/useAccessRequestForm';
import { useAuthStore } from '../../../../src/features/auth/model/authStore';
import { useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import * as Yup from 'yup';

jest.mock('../../../../src/features/auth/model/authStore');
jest.mock('@tanstack/react-query');
jest.mock('react-hot-toast');
jest.mock('../../../../src/features/access-requests/hooks/useAccessRequests', () => ({
    useAccessRequests: jest.fn(),
    useUserAccessRequests: jest.fn(),
    useCreateAccessRequest: jest.fn(),
    useUpdateAccessRequest: jest.fn(),
    accessRequestKeys: {
        all: ['accessRequests'],
    },
}));

const mockRegularUser = {
    id: 1,
    role: 'USER',
    name: 'Test User',
    email: 'user@test.com'
};

const mockProjectManager = {
    id: 2,
    role: 'PM',
    name: 'Test PM',
    email: 'pm@test.com'
};

describe('useAccessRequestForm', () => {
    let mockQueryClient: jest.Mocked<ReturnType<typeof useQueryClient>>;
    let mockUseAccessRequests: jest.Mock;
    let mockUseUserAccessRequests: jest.Mock;
    let mockCreateAccessRequest: jest.Mock;
    let mockUpdateAccessRequest: jest.Mock;

    beforeEach(() => {
        mockQueryClient = {
            invalidateQueries: jest.fn(),
        } as any;
        (useQueryClient as jest.Mock).mockReturnValue(mockQueryClient);

        (toast.error as jest.Mock).mockImplementation(() => { });
        (toast.success as jest.Mock).mockImplementation(() => { });

        mockUseAccessRequests = jest.fn().mockReturnValue({ data: [] });
        mockUseUserAccessRequests = jest.fn().mockReturnValue({ data: [] });
        mockCreateAccessRequest = jest.fn();
        mockUpdateAccessRequest = jest.fn();

        const useAccessRequests = require('../../../../src/features/access-requests/hooks/useAccessRequests');
        useAccessRequests.useAccessRequests.mockImplementation(mockUseAccessRequests);
        useAccessRequests.useUserAccessRequests.mockImplementation(mockUseUserAccessRequests);
        useAccessRequests.useCreateAccessRequest.mockReturnValue({
            mutate: mockCreateAccessRequest,
            isPending: false
        });
        useAccessRequests.useUpdateAccessRequest.mockReturnValue({
            mutate: mockUpdateAccessRequest,
            isPending: false
        });

        jest.clearAllMocks();
    });

    const setupTest = (options?: {
        user?: typeof mockRegularUser | typeof mockProjectManager,
        existingRequests?: Array<{ id: number, userId: number, systems: string[], status: string }>,
        isPM?: boolean
    }) => {
        const user = options?.user || (options?.isPM ? mockProjectManager : mockRegularUser);
        (useAuthStore as unknown as jest.Mock).mockReturnValue({ user });

        if (options?.existingRequests) {
            if (options.isPM) {
                mockUseAccessRequests.mockReturnValue({ data: options.existingRequests });
            } else {
                mockUseUserAccessRequests.mockReturnValue({ data: options.existingRequests });
            }
        }

        return renderHook(() => useAccessRequestForm());
    };

    describe('validation', () => {
        it('should validate systems field as required', async () => {
            const { result } = setupTest();

            await act(async () => {
                await result.current.formik.validateForm();
            });

            expect(result.current.formik.errors.systems).toBe('Debes seleccionar al menos un sistema');
        });

        it('should validate userId field for PM users', async () => {
            const { result } = setupTest({ isPM: true });

            await act(async () => {
                result.current.formik.setFieldValue('userId', '');
                await result.current.formik.validateForm();
            });

            expect(result.current.formik.errors.userId).toBe('El ID debe ser un número positivo');
        });
    });
});

describe('validation', () => {


    let mockQueryClient: jest.Mocked<ReturnType<typeof useQueryClient>>;
    let mockUseAccessRequests: jest.Mock;
    let mockUseUserAccessRequests: jest.Mock;
    let mockCreateAccessRequest: jest.Mock;
    let mockUpdateAccessRequest: jest.Mock;

    beforeEach(() => {
        mockQueryClient = {
            invalidateQueries: jest.fn(),
        } as any;
        (useQueryClient as jest.Mock).mockReturnValue(mockQueryClient);

        (toast.error as jest.Mock).mockImplementation(() => { });
        (toast.success as jest.Mock).mockImplementation(() => { });

        mockUseAccessRequests = jest.fn().mockReturnValue({ data: [] });
        mockUseUserAccessRequests = jest.fn().mockReturnValue({ data: [] });
        mockCreateAccessRequest = jest.fn();
        mockUpdateAccessRequest = jest.fn();

        const useAccessRequests = require('../../../../src/features/access-requests/hooks/useAccessRequests');
        useAccessRequests.useAccessRequests.mockImplementation(mockUseAccessRequests);
        useAccessRequests.useUserAccessRequests.mockImplementation(mockUseUserAccessRequests);
        useAccessRequests.useCreateAccessRequest.mockReturnValue({
            mutate: mockCreateAccessRequest,
            isPending: false
        });
        useAccessRequests.useUpdateAccessRequest.mockReturnValue({
            mutate: mockUpdateAccessRequest,
            isPending: false
        });

        jest.clearAllMocks();
    });

    const setupTest = (options?: {
        user?: typeof mockRegularUser | typeof mockProjectManager,
        existingRequests?: Array<{ id: number, userId: number, systems: string[], status: string }>,
        isPM?: boolean
    }) => {
        const user = options?.user || (options?.isPM ? mockProjectManager : mockRegularUser);
        (useAuthStore as unknown as jest.Mock).mockReturnValue({ user });

        if (options?.existingRequests) {
            if (options.isPM) {
                mockUseAccessRequests.mockReturnValue({ data: options.existingRequests });
            } else {
                mockUseUserAccessRequests.mockReturnValue({ data: options.existingRequests });
            }
        }

        return renderHook(() => useAccessRequestForm());
    };

    it('should validate systems field as required', async () => {
        setupTest();
        const { result } = renderHook(() => useAccessRequestForm());

        await act(async () => {
            await result.current.formik.validateForm();
        });

        expect(result.current.formik.errors.systems).toBe('Debes seleccionar al menos un sistema');
    });

    it('should validate userId field for PM users', async () => {
        setupTest({ isPM: true });
        const { result } = renderHook(() => useAccessRequestForm());

        await act(async () => {
            result.current.formik.setFieldValue('userId', '');
            await result.current.formik.validateForm();
        });

        expect(result.current.formik.errors.userId).toBe('El ID debe ser un número positivo');
    });
});

describe('existing request detection', () => {

    let mockQueryClient: jest.Mocked<ReturnType<typeof useQueryClient>>;
    let mockUseAccessRequests: jest.Mock;
    let mockUseUserAccessRequests: jest.Mock;
    let mockCreateAccessRequest: jest.Mock;
    let mockUpdateAccessRequest: jest.Mock;

    beforeEach(() => {
        mockQueryClient = {
            invalidateQueries: jest.fn(),
        } as any;
        (useQueryClient as jest.Mock).mockReturnValue(mockQueryClient);

        (toast.error as jest.Mock).mockImplementation(() => { });
        (toast.success as jest.Mock).mockImplementation(() => { });

        mockUseAccessRequests = jest.fn().mockReturnValue({ data: [] });
        mockUseUserAccessRequests = jest.fn().mockReturnValue({ data: [] });
        mockCreateAccessRequest = jest.fn();
        mockUpdateAccessRequest = jest.fn();

        const useAccessRequests = require('../../../../src/features/access-requests/hooks/useAccessRequests');
        useAccessRequests.useAccessRequests.mockImplementation(mockUseAccessRequests);
        useAccessRequests.useUserAccessRequests.mockImplementation(mockUseUserAccessRequests);
        useAccessRequests.useCreateAccessRequest.mockReturnValue({
            mutate: mockCreateAccessRequest,
            isPending: false
        });
        useAccessRequests.useUpdateAccessRequest.mockReturnValue({
            mutate: mockUpdateAccessRequest,
            isPending: false
        });

        jest.clearAllMocks();
    });

    const setupTest = (options?: {
        user?: typeof mockRegularUser | typeof mockProjectManager,
        existingRequests?: Array<{ id: number, userId: number, systems: string[], status: string }>,
        isPM?: boolean
    }) => {
        const user = options?.user || (options?.isPM ? mockProjectManager : mockRegularUser);
        (useAuthStore as unknown as jest.Mock).mockReturnValue({ user });

        if (options?.existingRequests) {
            if (options.isPM) {
                mockUseAccessRequests.mockReturnValue({ data: options.existingRequests });
            } else {
                mockUseUserAccessRequests.mockReturnValue({ data: options.existingRequests });
            }
        }

        return renderHook(() => useAccessRequestForm());
    };

    it('should not detect non-pending requests as existing', () => {
        const approvedRequest = {
            id: 123,
            userId: mockRegularUser.id,
            systems: ['SYSTEM1'],
            status: 'APPROVED'
        };
        setupTest({ existingRequests: [approvedRequest] });

        const { result } = renderHook(() => useAccessRequestForm());

        expect(result.current.existingRequest).toBeUndefined();
    });
});
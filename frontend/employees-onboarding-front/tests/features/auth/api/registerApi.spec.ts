import axiosClient from '../../../../src/shared/api/axiosClient';
import { registerUser } from '../../../../src/features/auth/api/registerUserApi';
import { AuthResponse } from '../../../../src/entities/auth/authTypes';
import { CreateUserRequest } from '../../../../src/entities/user/userTypes';

jest.mock('../../../../src/shared/api/axiosClient');

describe('Auth Services - registerUser', () => {
  const mockAxios = axiosClient as jest.Mocked<typeof axiosClient>;
  const mockAuthResponse: AuthResponse = {
    access_token: 'jwt-token-mock',
    refresh_token: 'refresh',
  };

  const validUserData: CreateUserRequest = {
    email: 'test@example.com',
    password: 'ValidPassword123!',
    name: 'Test User',
    role: 'DEV',
    status: 'APPROVED'
  };

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should return auth response with token when registration is successful', async () => {
    mockAxios.post.mockResolvedValue({ data: mockAuthResponse });

    const result = await registerUser(validUserData);

    expect(mockAxios.post).toHaveBeenCalledTimes(1);
    expect(mockAxios.post).toHaveBeenCalledWith('/auth/register', validUserData);
    expect(result).toEqual(mockAuthResponse);
    expect(result.access_token).toBeDefined();
  });

  it('should throw error when email is already registered', async () => {
    const duplicateEmailData = { ...validUserData };
    const errorMessage = 'Email already in use';
    mockAxios.post.mockRejectedValue({
      response: {
        status: 409,
        data: { message: errorMessage },
      },
    });

    await expect(registerUser(duplicateEmailData)).rejects.toEqual({
      response: {
        status: 409,
        data: { message: errorMessage },
      },
    });
  });

  it('should throw validation error when required fields are missing', async () => {
    const invalidUserData = {} as CreateUserRequest;
    const validationErrors = {
      email: 'Email is required',
      password: 'Password is required',
    };
    mockAxios.post.mockRejectedValue({
      response: {
        status: 400,
        data: { errors: validationErrors },
      },
    });

    await expect(registerUser(invalidUserData)).rejects.toEqual({
      response: {
        status: 400,
        data: { errors: validationErrors },
      },
    });
  });

  it('should throw error when password does not meet requirements', async () => {
    const weakPasswordData: CreateUserRequest = {
      ...validUserData,
      password: 'weak',
    };
    const errorMessage = 'Password does not meet requirements';
    mockAxios.post.mockRejectedValue({
      response: {
        status: 400,
        data: { message: errorMessage },
      },
    });

    await expect(registerUser(weakPasswordData)).rejects.toEqual({
      response: {
        status: 400,
        data: { message: errorMessage },
      },
    });
  });

  it('should handle network errors', async () => {
    const networkError = new Error('Network Error');
    mockAxios.post.mockRejectedValue(networkError);

    await expect(registerUser(validUserData)).rejects.toThrow('Network Error');
  });

  describe('Email validation', () => {
    it('should throw error when email format is invalid', async () => {
      const invalidEmailData: CreateUserRequest = {
        ...validUserData,
        email: 'invalid-email',
      };
      mockAxios.post.mockRejectedValue({
        response: {
          status: 400,
          data: { message: 'Invalid email format' },
        },
      });

      await expect(registerUser(invalidEmailData)).rejects.toEqual({
        response: {
          status: 400,
          data: { message: 'Invalid email format' },
        },
      });
    });
  });

  describe('Password validation', () => {
    it('should throw error when password is too short', async () => {
      const shortPasswordData: CreateUserRequest = {
        ...validUserData,
        password: 'short',
      };
      mockAxios.post.mockRejectedValue({
        response: {
          status: 400,
          data: { message: 'Password must be at least 8 characters' },
        },
      });

      await expect(registerUser(shortPasswordData)).rejects.toEqual({
        response: {
          status: 400,
          data: { message: 'Password must be at least 8 characters' },
        },
      });
    });
  });
});
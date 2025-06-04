import axiosClient from '../../../../src/shared/api/axiosClient';
import { login } from '../../../../src/features/auth/api/loginApi';
import { LoginFormValues, AuthResponse } from '@/entities/auth/authTypes';

jest.mock('../../../../src/shared/api/axiosClient');

describe('Auth Services - login', () => {
  const mockAxios = axiosClient as jest.Mocked<typeof axiosClient>;
  
  const validCredentials: LoginFormValues = {
    email: 'user@example.com',
    password: 'Password123'
  };

  const invalidCredentials = {
    email: 'invalid-email',
    password: 'short'
  };

  const successResponse: AuthResponse = {
    access_token: "token",
    refresh_token: "refresh"
  };

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Successful login scenarios', () => {
    it('should return auth response with token when valid credentials are provided', async () => {
      mockAxios.post.mockResolvedValue({ data: successResponse });

      const result = await login(validCredentials);

      expect(mockAxios.post).toHaveBeenCalledTimes(1);
      expect(mockAxios.post).toHaveBeenCalledWith('/auth/login', validCredentials);
      expect(result).toEqual(successResponse);
      expect(result.access_token).toBeDefined();
    });

    it('should call API exactly once', async () => {
      mockAxios.post.mockResolvedValue({ data: successResponse });

      await login(validCredentials);

      expect(mockAxios.post).toHaveBeenCalledTimes(1);
    });
  });

  describe('Error handling', () => {
    it('should throw validation error when invalid email format is provided', async () => {
      const invalidData = { ...validCredentials, email: 'not-an-email' };
      mockAxios.post.mockRejectedValue({
        response: {
          status: 400,
          data: { message: 'Invalid email format' }
        }
      });

      await expect(login(invalidData)).rejects.toEqual({
        response: {
          status: 400,
          data: { message: 'Invalid email format' }
        }
      });
    });

    it('should throw unauthorized error when wrong credentials are provided', async () => {
      mockAxios.post.mockRejectedValue({
        response: {
          status: 401,
          data: { message: 'Invalid credentials' }
        }
      });

      await expect(login(validCredentials)).rejects.toEqual({
        response: {
          status: 401,
          data: { message: 'Invalid credentials' }
        }
      });
    });

    it('should throw error when server is unavailable', async () => {
      mockAxios.post.mockRejectedValue(new Error('Network Error'));

      await expect(login(validCredentials)).rejects.toThrow('Network Error');
    });
  });

  describe('Edge cases', () => {
    it('should handle empty password (but reject at API level)', async () => {
      const emptyPasswordData = { ...validCredentials, password: '' };
      mockAxios.post.mockRejectedValue({
        response: {
          status: 400,
          data: { message: 'Password is required' }
        }
      });

      await expect(login(emptyPasswordData)).rejects.toEqual({
        response: {
          status: 400,
          data: { message: 'Password is required' }
        }
      });
    });

    it('should handle unexpected response format gracefully', async () => {
      const malformedResponse = { token: 'abc' };
      mockAxios.post.mockResolvedValue({ data: malformedResponse });

      const result = await login(validCredentials);

      expect(result).toEqual(malformedResponse);
      expect(result.access_token).toBeUndefined();
    });
  });
});
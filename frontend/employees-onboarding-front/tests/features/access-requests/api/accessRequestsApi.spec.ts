import axiosClient from '../../../../src/shared/api/axiosClient';
import {
  fetchAccessRequests,
  getUserAccessRequests,
  getAccessRequestById,
  createAccessRequest,
  updateAccessRequest,
  deleteAccessRequest
} from '../../../../src/features/access-requests/api/accessRequestsApi';
import { AccessRequestCreate, AccessRequestsResponse } from '../../../../src/entities/access-request/accessRequestTypes';

jest.mock('../../../../src/shared/api/axiosClient');

describe('AccessRequest Services', () => {
  const mockAxios = axiosClient as jest.Mocked<typeof axiosClient>;
  const mockAccessRequest: AccessRequestsResponse = {
    id: 1,
    userId: 123,
    status: 'pending',
    createdAt: '2023-01-01',
    systems: ["git", "hub"]
  };

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('fetchAccessRequests', () => {
    it('should return access requests array when API call succeeds', async () => {
      const mockResponse = [mockAccessRequest];
      mockAxios.get.mockResolvedValue({ data: mockResponse });

      const result = await fetchAccessRequests();

      expect(mockAxios.get).toHaveBeenCalledWith('/access-requests');
      expect(result).toEqual(mockResponse);
    });

    it('should throw error when API call fails', async () => {
      const errorMessage = 'Network Error';
      mockAxios.get.mockRejectedValue(new Error(errorMessage));

      await expect(fetchAccessRequests()).rejects.toThrow(errorMessage);
    });
  });

  describe('getUserAccessRequests', () => {
    it('should return user-specific access requests when valid userId is provided', async () => {
      const userId = 123;
      const mockResponse = [mockAccessRequest];
      mockAxios.get.mockResolvedValue({ data: mockResponse });

      const result = await getUserAccessRequests(userId);

      expect(mockAxios.get).toHaveBeenCalledWith(`/access-requests/userId/${userId}`);
      expect(result).toEqual(mockResponse);
    });

    it('should throw error when invalid userId is provided', async () => {
      const invalidUserId = NaN;
      mockAxios.get.mockRejectedValue(new Error('Invalid user ID'));

      await expect(getUserAccessRequests(invalidUserId)).rejects.toThrow('Invalid user ID');
    });
  });

  describe('getAccessRequestById', () => {
    it('should return single access request when valid ID is provided', async () => {
      const requestId = 1;
      mockAxios.get.mockResolvedValue({ data: mockAccessRequest });

      const result = await getAccessRequestById(requestId);

      expect(mockAxios.get).toHaveBeenCalledWith(`/access-requests/${requestId}`);
      expect(result).toEqual(mockAccessRequest);
    });

    it('should throw error when request is not found', async () => {
      const nonExistentId = 999;
      mockAxios.get.mockRejectedValue({ response: { status: 404 } });

      await expect(getAccessRequestById(nonExistentId)).rejects.toEqual({ response: { status: 404 } });
    });
  });

  describe('createAccessRequest', () => {
    it('should create and return new access request with valid data', async () => {
      const newRequestData: AccessRequestCreate = {
        userId: 123,
        systems: ["git"],
        status: "APPROVED"
      };
      mockAxios.post.mockResolvedValue({ data: mockAccessRequest });

      const result = await createAccessRequest(newRequestData);

      expect(mockAxios.post).toHaveBeenCalledWith('/access-requests', newRequestData);
      expect(result).toEqual(mockAccessRequest);
    });

    it('should throw validation error when required fields are missing', async () => {
      const invalidRequestData = {} as AccessRequestCreate;
      mockAxios.post.mockRejectedValue({ response: { status: 400 } });

      await expect(createAccessRequest(invalidRequestData)).rejects.toEqual({ response: { status: 400 } });
    });
  });

  describe('updateAccessRequest', () => {
    it('should update and return modified access request with valid data', async () => {
      const requestId = 1;
      const updateData = { status: 'approved' };
      const updatedRequest = { ...mockAccessRequest, ...updateData };
      mockAxios.put.mockResolvedValue({ data: updatedRequest });

      const result = await updateAccessRequest(requestId, updateData);

      expect(mockAxios.put).toHaveBeenCalledWith(
        `/access-requests/${requestId}`,
        updateData
      );
      expect(result).toEqual(updatedRequest);
    });

    it('should throw error when trying to update non-existent request', async () => {
      const nonExistentId = 999;
      const updateData = { status: 'approved' };
      mockAxios.put.mockRejectedValue({ response: { status: 404 } });

      await expect(updateAccessRequest(nonExistentId, updateData)).rejects.toEqual({
        response: { status: 404 },
      });
    });
  });

  describe('deleteAccessRequest', () => {
    it('should successfully delete access request when valid ID is provided', async () => {
      const requestId = 1;
      mockAxios.delete.mockResolvedValue({ status: 204 });

      await deleteAccessRequest(requestId);

      expect(mockAxios.delete).toHaveBeenCalledWith(`/access-requests/${requestId}`);
    });

    it('should throw error when trying to delete already deleted request', async () => {
      const alreadyDeletedId = 2;
      mockAxios.delete.mockRejectedValue({ response: { status: 410 } });

      await expect(deleteAccessRequest(alreadyDeletedId)).rejects.toEqual({
        response: { status: 410 },
      });
    });
  });
});
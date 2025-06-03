package com.katabdb.employee.onboarding.mngr.services.spec;

import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsRequest;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsResponse;

import java.util.List;
import java.util.Optional;

public interface IAccessRequestsQueryService {
    AccessRequestsResponse getAccessRequestById(Integer id);
    List<AccessRequestsResponse> getAllAccessRequests();
    AccessRequestsResponse createAccessRequest(AccessRequestsRequest accessRequestsRequest);
    AccessRequestsResponse updateAccessRequest(Integer id, AccessRequestsRequest accessRequestsRequest);
    Optional<List<AccessRequestsResponse>> getRequestsByUser(Integer userId);
}

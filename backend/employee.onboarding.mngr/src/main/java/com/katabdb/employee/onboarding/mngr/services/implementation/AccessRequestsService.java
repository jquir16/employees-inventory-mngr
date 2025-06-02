package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsRequest;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsResponse;
import com.katabdb.employee.onboarding.mngr.repository.IAccessRequestRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.spec.IAccessRequestsQueryService;
import com.katabdb.employee.onboarding.mngr.validation.mappers.AccessRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccessRequestsService implements IAccessRequestsQueryService {

    private final IAccessRequestRepository accessRequestRepository;

    private final IUserRepository userRepository;

    @Autowired
    public AccessRequestsService(IAccessRequestRepository accessRequestRepository, IUserRepository userRepository) {
        this.accessRequestRepository = accessRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccessRequestsResponse getAccessRequestById(Integer id) {
        return AccessRequestMapper.buildRequestFromEntity(accessRequestRepository.getAccessRequestEntityById(id));
    }

    @Override
    public List<AccessRequestsResponse> getAllAccessRequests() {
        return accessRequestRepository.findAll()
                .stream()
                .map(AccessRequestMapper::buildRequestFromEntity)
                .toList();
    }

    @Override
    public AccessRequestsResponse createAccessRequest(AccessRequestsRequest accessRequestsRequest) {
        var user = userRepository.findById(accessRequestsRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("user does not exist"));
        var accessRequest = AccessRequestMapper.buildEntityFromRequest(accessRequestsRequest, user);
        accessRequestRepository.save(accessRequest);
        return AccessRequestMapper.buildRequestFromEntity(accessRequest);
    }

    @Override
    public AccessRequestsResponse updateAccessRequest(Integer id, AccessRequestsRequest accessRequestsRequest) {
        var requestToUpdate = accessRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("access request does not exist"));
        var user = userRepository.findById(accessRequestsRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("user does not exist"));
        if (user != null) {
            requestToUpdate.setUser(user);
        }
        if (accessRequestsRequest.systems() != null) {
            requestToUpdate.setSystems(accessRequestsRequest.systems());
        }
        if (accessRequestsRequest.status() != null) {
            requestToUpdate.setStatus(accessRequestsRequest.status());
        }
        return AccessRequestMapper.buildRequestFromEntity(accessRequestRepository.save(requestToUpdate));
    }

    @Override
    public Optional<List<AccessRequestsResponse>> getRequestsByUser(Integer id) {
        return Optional.of(
                accessRequestRepository.findAllByUserId(id)
                        .stream()
                        .map(AccessRequestMapper::buildRequestFromEntity)
                        .toList()
        );
    }
}

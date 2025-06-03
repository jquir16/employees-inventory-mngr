package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.AccessRequestEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsRequest;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsResponse;
import com.katabdb.employee.onboarding.mngr.repository.IAccessRequestRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.spec.IAccessRequestsQueryService;
import com.katabdb.employee.onboarding.mngr.validation.mappers.AccessRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccessRequestsService implements IAccessRequestsQueryService {

    private static final String USER_NOT_FOUND = "User not found with ID: ";
    private static final String REQUEST_NOT_FOUND = "Access request not found with ID: ";

    private final IAccessRequestRepository accessRequestRepository;
    private final IUserRepository userRepository;

    public AccessRequestsService(IAccessRequestRepository accessRequestRepository,
                                 IUserRepository userRepository) {
        this.accessRequestRepository = accessRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccessRequestsResponse getAccessRequestById(Integer id) {
        return accessRequestRepository.findById(id)
                .map(AccessRequestMapper::buildRequestFromEntity)
                .orElseThrow(() -> new IllegalArgumentException(REQUEST_NOT_FOUND + id));
    }

    @Override
    public List<AccessRequestsResponse> getAllAccessRequests() {
        return accessRequestRepository.findAll()
                .stream()
                .map(AccessRequestMapper::buildRequestFromEntity)
                .toList();
    }

    @Override
    @Transactional
    public AccessRequestsResponse createAccessRequest(AccessRequestsRequest request) {
        validateUserExists(request.userId());

        var accessRequest = AccessRequestMapper.buildEntityFromRequest(
                request,
                getUserById(request.userId())
        );

        return AccessRequestMapper.buildRequestFromEntity(
                accessRequestRepository.save(accessRequest)
        );
    }

    @Override
    @Transactional
    public AccessRequestsResponse updateAccessRequest(Integer id, AccessRequestsRequest request) {
        var requestToUpdate = accessRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(REQUEST_NOT_FOUND + id));

        updateRequestFields(requestToUpdate, request);

        return AccessRequestMapper.buildRequestFromEntity(
                accessRequestRepository.save(requestToUpdate)
        );
    }

    @Override
    public Optional<List<AccessRequestsResponse>> getRequestsByUser(Integer userId) {
        return Optional.of(
                accessRequestRepository.findAllByUserId(userId)
                        .stream()
                        .map(AccessRequestMapper::buildRequestFromEntity)
                        .toList()
        );
    }

    private void validateUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(USER_NOT_FOUND + userId);
        }
    }

    private UserEntity getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND + userId));
    }

    private void updateRequestFields(AccessRequestEntity requestToUpdate, AccessRequestsRequest request) {
        if (request.userId() != null) {
            requestToUpdate.setUser(getUserById(request.userId()));
        }
        if (request.systems() != null) {
            requestToUpdate.setSystems(request.systems());
        }
        if (request.status() != null) {
            requestToUpdate.setStatus(request.status());
        }
    }
}


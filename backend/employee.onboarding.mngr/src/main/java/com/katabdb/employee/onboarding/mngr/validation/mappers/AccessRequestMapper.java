package com.katabdb.employee.onboarding.mngr.validation.mappers;

import com.katabdb.employee.onboarding.mngr.domain.entities.AccessRequestEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsRequest;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsResponse;

public class AccessRequestMapper {

    public static AccessRequestsResponse buildRequestFromEntity(AccessRequestEntity accessRequestEntity) {
        if (accessRequestEntity == null) return null;
        return AccessRequestsResponse.builder()
                .id(accessRequestEntity.getId())
                .userId(accessRequestEntity.getUser() !=null ? accessRequestEntity.getUser().getId(): null)
                .systems(accessRequestEntity.getSystems())
                .status(accessRequestEntity.getStatus())
                .build();
    }

    public static AccessRequestEntity buildEntityFromRequest(AccessRequestsRequest accessRequestsRequest, UserEntity user) {
        return AccessRequestEntity.builder()
                .user(user)
                .systems(accessRequestsRequest.systems())
                .status(accessRequestsRequest.status())
                .build();
    }
}

package com.katabdb.employee.onboarding.mngr.dto.accessRequests;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record AccessRequestsResponse(
        Integer id,
        Integer userId,
        String[] systems,
        String status
) {
}

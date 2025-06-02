package com.katabdb.employee.onboarding.mngr.dto.accessRequests;

import lombok.Builder;

import java.util.List;

@Builder
public record AccessRequestsRequest(
        Integer userId,
        String[] systems,
        String status
) {
}

package com.katabdb.employee.onboarding.mngr.dto.accessRequests;

import lombok.Builder;

import java.util.Date;

@Builder
public record AccessRequestsResponse(
        Integer id,
        Integer userId,
        String[] systems,
        String status,
        Date createdAt
) {
}

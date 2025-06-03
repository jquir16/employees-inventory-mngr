package com.katabdb.employee.onboarding.mngr.dto.auth;

import lombok.Builder;

@Builder
public record LoginUserRequest(
        String email,
        String password
) {}

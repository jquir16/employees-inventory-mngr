package com.katabdb.employee.onboarding.mngr.dto.auth;

import com.katabdb.employee.onboarding.mngr.domain.enums.UserRole;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserStatus;
import lombok.Builder;

@Builder
public record RegisterUserRequest(
        String email,
        String password,
        String name,
        UserRole role,
        UserStatus status
){}

package com.katabdb.employee.onboarding.mngr.dto.user;

import com.katabdb.employee.onboarding.mngr.domain.enums.UserRole;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserStatus;

public record UserUpdateRequest(
        String name,
        String email,
        String password,
        UserRole role,
        UserStatus status
) {}

package com.katabdb.employee.onboarding.mngr.dto.user;

import com.katabdb.employee.onboarding.mngr.domain.enums.UserRole;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
public record UserResponse(
        Integer id,
        String name,
        String email,
        String password,
        UserRole role,
        UserStatus status
){}

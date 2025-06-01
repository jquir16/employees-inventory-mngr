package com.katabdb.employee.onboarding.mngr.dto.auth;

public record LoginUserRequest(
        String email,
        String password
) {}

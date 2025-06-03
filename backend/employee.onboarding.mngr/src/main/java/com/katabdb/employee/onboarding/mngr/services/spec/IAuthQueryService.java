package com.katabdb.employee.onboarding.mngr.services.spec;

import com.katabdb.employee.onboarding.mngr.dto.auth.LoginUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.RegisterUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.TokenResponse;

public interface IAuthQueryService {
    TokenResponse register(RegisterUserRequest request);
    TokenResponse login(LoginUserRequest request);
    TokenResponse refreshToken(String header);
}

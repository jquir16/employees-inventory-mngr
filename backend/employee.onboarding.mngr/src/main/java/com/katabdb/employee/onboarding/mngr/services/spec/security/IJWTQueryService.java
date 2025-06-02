package com.katabdb.employee.onboarding.mngr.services.spec.security;

public interface IJWTQueryService {
    String generateToken(String username);
    String extractUsername(String token);
    boolean isTokenValid(String token, String username);
}

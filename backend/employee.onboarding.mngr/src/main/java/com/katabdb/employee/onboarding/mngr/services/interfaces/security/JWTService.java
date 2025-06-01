package com.katabdb.employee.onboarding.mngr.services.interfaces.security;

public interface JWTService {
    String generateToken(String username);
    String extractUsername(String token);
    boolean isTokenValid(String token, String username);
}

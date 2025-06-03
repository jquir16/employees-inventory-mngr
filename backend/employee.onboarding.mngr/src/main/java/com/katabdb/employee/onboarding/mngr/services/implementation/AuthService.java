package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.TokenEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.domain.enums.TokenStatus;
import com.katabdb.employee.onboarding.mngr.dto.auth.LoginUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.RegisterUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.TokenResponse;
import com.katabdb.employee.onboarding.mngr.repository.IAuthRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.JWTService;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.PasswordService;
import com.katabdb.employee.onboarding.mngr.services.spec.IAuthQueryService;
import com.katabdb.employee.onboarding.mngr.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthQueryService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final IAuthRepository authRepository;
    private final IUserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordService passwordService;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse register(RegisterUserRequest request) {
        validateUserDoesNotExist(request.email());

        UserEntity savedUser = saveNewUser(request);
        return generateAndSaveTokens(savedUser);
    }

    @Override
    public TokenResponse login(LoginUserRequest request) {
        authenticateUser(request.email(), request.password());

        UserEntity user = findUserByEmail(request.email());
        revokeAllActiveTokens(user);

        return generateAndSaveTokens(user);
    }

    @Override
    public TokenResponse refreshToken(String authHeader) {
        validateAuthHeader(authHeader);

        String refreshToken = extractTokenFromHeader(authHeader);
        String userEmail = jwtService.extractEmail(refreshToken);

        UserEntity user = findUserByEmail(userEmail);
        validateRefreshToken(refreshToken, user);

        revokeAllActiveTokens(user);
        return generateNewAccessToken(user, refreshToken);
    }

    private void validateUserDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists");
        }
    }

    private UserEntity saveNewUser(RegisterUserRequest request) {
        return userRepository.save(buildUserFromRequest(request));
    }

    private TokenResponse generateAndSaveTokens(UserEntity user) {
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveTokenForUser(user, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    private void authenticateUser(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private void validateAuthHeader(String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Invalid Bearer Token");
        }
    }

    private String extractTokenFromHeader(String authHeader) {
        return authHeader.substring(BEARER_PREFIX_LENGTH);
    }

    private void validateRefreshToken(String refreshToken, UserEntity user) {
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    private TokenResponse generateNewAccessToken(UserEntity user, String refreshToken) {
        String jwtToken = jwtService.generateToken(user);
        saveTokenForUser(user, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    private UserEntity buildUserFromRequest(RegisterUserRequest request) {
        return UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordService.encodePassword(request.password()))
                .role(request.role())
                .status(request.status())
                .createdAt(new Date())
                .build();
    }

    private void saveTokenForUser(UserEntity user, String jwtToken) {
        TokenEntity token = buildTokenForUser(user, jwtToken);
        authRepository.save(token);
    }

    private TokenEntity buildTokenForUser(UserEntity user, String jwtToken) {
        return TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .expiresAt(jwtService.getExpirationDate())
                .tokenStatus(TokenStatus.ACTIVE)
                .build();
    }

    private void revokeAllActiveTokens(UserEntity user) {
        List<TokenEntity> activeTokens = authRepository.findAllByTokenStatus(TokenStatus.ACTIVE);

        if (!activeTokens.isEmpty()) {
            activeTokens.forEach(token -> token.setTokenStatus(TokenStatus.REVOKED));
            authRepository.saveAll(activeTokens);
        }
    }
}

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

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthQueryService {
    private final IAuthRepository authRepository;
    private final IUserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordService passwordService;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        var savedUser = userRepository.save(buildUserFromRequest(request));
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        var token = buildTokenForUser(savedUser, jwtToken);
        authRepository.save(token);
        return new TokenResponse(jwtToken, refreshToken);
    }

    @Override
    public TokenResponse login(LoginUserRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        var token = buildTokenForUser(user, jwtToken);
        authRepository.save(token);
        return new TokenResponse(jwtToken, refreshToken);
    }

    @Override
    public TokenResponse refreshToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Bearer Token");
        }
        final String refreshToken = header.substring(7);
        final String userEmail = jwtService.extractEmail(refreshToken);

        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        final UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        final String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        var token = buildTokenForUser(user, jwtToken);
        authRepository.save(token);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public UserEntity buildUserFromRequest(RegisterUserRequest request) {
        return UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordService.encodePassword(request.password()))
                .role(request.role())
                .createdAt(new Date())
                .build();
    }

    private TokenEntity buildTokenForUser(UserEntity user, String jwtToken) {
        return TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .expiresAt(jwtService.getExpirationDate())
                .tokenStatus(TokenStatus.ACTIVE)
                .build();
    }

    private void revokeAllUserTokens(final UserEntity user) {
        final List<TokenEntity> validUserTokens = authRepository
                .findAllByTokenStatus(TokenStatus.ACTIVE);
        if (!validUserTokens.isEmpty()) {
            for (final TokenEntity token : validUserTokens) {
                token.setTokenStatus(TokenStatus.REVOKED);
                token.setTokenStatus(TokenStatus.REVOKED);
            }
            authRepository.saveAll(validUserTokens);
        }
    }
}

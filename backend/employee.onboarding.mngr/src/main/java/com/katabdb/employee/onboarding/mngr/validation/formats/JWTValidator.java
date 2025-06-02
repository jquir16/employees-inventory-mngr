package com.katabdb.employee.onboarding.mngr.validation.formats;

import com.katabdb.employee.onboarding.mngr.domain.entities.TokenEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.domain.enums.TokenStatus;
import com.katabdb.employee.onboarding.mngr.repository.IAuthRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTValidator {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final IAuthRepository authRepository;
    private final IUserRepository userRepository;

    public void processToken(String jwtToken, HttpServletRequest request) {
        final String userEmail = jwtService.extractEmail(jwtToken);
        if (userEmail == null) return;

        Optional<TokenEntity> tokenOpt = authRepository.findByToken(jwtToken);
        if (tokenOpt.isEmpty() || isTokenInvalid(tokenOpt.get())) return;

        Optional<UserEntity> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty() || !jwtService.isTokenValid(jwtToken, userOpt.get())) return;

        setAuthentication(userEmail, request);
    }

    private boolean isTokenInvalid(TokenEntity token) {
        return token.getExpiresAt().before(new Date()) || !TokenStatus.ACTIVE.equals(token.getTokenStatus());
    }

    private void setAuthentication(String userEmail, HttpServletRequest request) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        final var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
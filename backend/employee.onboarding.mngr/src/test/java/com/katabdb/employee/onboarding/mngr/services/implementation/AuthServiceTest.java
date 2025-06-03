package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.TokenEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.domain.enums.TokenStatus;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserRole;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserStatus;
import com.katabdb.employee.onboarding.mngr.dto.auth.LoginUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.RegisterUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.TokenResponse;
import com.katabdb.employee.onboarding.mngr.exception.UserAlreadyExistsException;
import com.katabdb.employee.onboarding.mngr.repository.IAuthRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.JWTService;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private IAuthRepository authRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private JWTService jwtService;
    @Mock
    private PasswordService passwordService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "securePassword123";
    private static final String VALID_NAME = "Test User";
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String VALID_REFRESH_TOKEN = "valid.refresh.token";
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;

    @Nested
    @DisplayName("Given User Login")
    class UserLoginTests {

        private LoginUserRequest validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new LoginUserRequest(VALID_EMAIL, VALID_PASSWORD);
        }

        @Test
        @DisplayName("When logging in with valid credentials, then return tokens and revoke old ones")
        void login_withValidCredentials_shouldReturnTokens() {
            UserEntity user = UserEntity.builder().email(VALID_EMAIL).build();
            List<TokenEntity> activeTokens = Collections.singletonList(new TokenEntity());

            when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));
            when(authRepository.findAllByTokenStatus(TokenStatus.ACTIVE)).thenReturn(activeTokens);
            when(jwtService.generateToken(user)).thenReturn(VALID_TOKEN);
            when(jwtService.generateRefreshToken(user)).thenReturn(VALID_REFRESH_TOKEN);

            TokenResponse response = authService.login(validRequest);

            assertThat(response.accessToken()).isEqualTo(VALID_TOKEN);
            assertThat(response.refreshToken()).isEqualTo(VALID_REFRESH_TOKEN);
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(authRepository).saveAll(activeTokens);
        }

        @Test
        @DisplayName("When logging in with invalid credentials, then throw BadCredentialsException")
        void login_withInvalidCredentials_shouldThrowException() {
            doThrow(BadCredentialsException.class)
                    .when(authenticationManager)
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));

            assertThatThrownBy(() -> authService.login(validRequest))
                    .isInstanceOf(BadCredentialsException.class);
            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("When user not found after authentication, then throw UsernameNotFoundException")
        void login_whenUserNotFound_shouldThrowException() {
            when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(validRequest))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining(VALID_EMAIL);
        }
    }

    @Nested
    @DisplayName("Given Token Refresh")
    class TokenRefreshTests {

        @Test
        @DisplayName("When refreshing with valid bearer token, then return new access token")
        void refreshToken_withValidBearerToken_shouldReturnNewAccessToken() {
            UserEntity user = UserEntity.builder().email(VALID_EMAIL).build();
            List<TokenEntity> activeTokens = Collections.singletonList(new TokenEntity());

            when(jwtService.extractEmail(VALID_TOKEN)).thenReturn(VALID_EMAIL);
            when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));
            when(jwtService.isTokenValid(VALID_TOKEN, user)).thenReturn(true);
            when(jwtService.generateToken(user)).thenReturn("newAccessToken");
            when(authRepository.findAllByTokenStatus(TokenStatus.ACTIVE)).thenReturn(activeTokens);

            TokenResponse response = authService.refreshToken(BEARER_TOKEN);

            assertThat(response.accessToken()).isEqualTo("newAccessToken");
            assertThat(response.refreshToken()).isEqualTo(VALID_TOKEN);
            verify(authRepository).saveAll(activeTokens);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"Invalid", "Bearer", " Bearer token"})
        @DisplayName("When refreshing with invalid auth header, then throw IllegalArgumentException")
        void refreshToken_withInvalidAuthHeader_shouldThrowException(String invalidHeader) {
            assertThatThrownBy(() -> authService.refreshToken(invalidHeader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid Bearer Token");
        }

        @Test
        @DisplayName("When refreshing with invalid token, then throw IllegalArgumentException")
        void refreshToken_withInvalidToken_shouldThrowException() {
            UserEntity user = UserEntity.builder().email(VALID_EMAIL).build();

            when(jwtService.extractEmail(VALID_TOKEN)).thenReturn(VALID_EMAIL);
            when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));
            when(jwtService.isTokenValid(VALID_TOKEN, user)).thenReturn(false);

            assertThatThrownBy(() -> authService.refreshToken(BEARER_TOKEN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid refresh token");
        }
    }
}
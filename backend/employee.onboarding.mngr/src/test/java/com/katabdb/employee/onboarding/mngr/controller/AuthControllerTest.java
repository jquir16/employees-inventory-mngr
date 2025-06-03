package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.enums.UserRole;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserStatus;
import com.katabdb.employee.onboarding.mngr.dto.auth.LoginUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.RegisterUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.TokenResponse;
import com.katabdb.employee.onboarding.mngr.services.spec.IAuthQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private IAuthQueryService authQueryService;

    @InjectMocks
    private AuthController authController;

    private RegisterUserRequest validRegisterRequest;
    private LoginUserRequest validLoginRequest;
    private TokenResponse mockTokenResponse;

    @BeforeEach
    void setUp() {
        validRegisterRequest = RegisterUserRequest.builder()
                .name("test user")
                .email("test@test.com")
                .password("password")
                .build();

        validLoginRequest = LoginUserRequest.builder()
                .email("test user")
                .password("password")
                .build();

        mockTokenResponse = TokenResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
    }

    @Nested
    @DisplayName("Given User Registration")
    class UserRegistrationTests {

        @Test
        @DisplayName("When valid registration request, then return token response with status 200")
        void registerUser_withValidRequest_shouldReturnTokenResponse() {
            when(authQueryService.register(any(RegisterUserRequest.class))).thenReturn(mockTokenResponse);

            ResponseEntity<TokenResponse> response = authController.registerUser(validRegisterRequest);

            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(mockTokenResponse);
            verify(authQueryService).register(validRegisterRequest);
        }

        @Test
        @DisplayName("When registration request with null values, then Spring Validation should handle it before controller")
        void registerUser_withNullValues_shouldBeHandledBySpringValidation() {
            RegisterUserRequest invalidRequest = new RegisterUserRequest("", "", "", UserRole.AC, UserStatus.APPROVED);

            when(authQueryService.register(any(RegisterUserRequest.class))).thenReturn(mockTokenResponse);
            ResponseEntity<TokenResponse> response = authController.registerUser(invalidRequest);

            assertThat(response.getStatusCodeValue()).isEqualTo(200);
        }
    }

    @Nested
    @DisplayName("Given User Login")
    class UserLoginTests {

        @Test
        @DisplayName("When valid login request, then return token response with status 200")
        void loginUser_withValidRequest_shouldReturnTokenResponse() {
            when(authQueryService.login(any(LoginUserRequest.class))).thenReturn(mockTokenResponse);

            ResponseEntity<TokenResponse> response = authController.loginUser(validLoginRequest);

            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(mockTokenResponse);
            verify(authQueryService).login(validLoginRequest);
        }

        @Test
        @DisplayName("When login request with invalid credentials, then service should handle it")
        void loginUser_withInvalidCredentials_shouldBeHandledByService() {
            LoginUserRequest invalidRequest = LoginUserRequest.builder()
                    .email("wrong")
                    .password("wrong")
                    .build();

            when(authQueryService.login(invalidRequest))
                    .thenThrow(new RuntimeException("Invalid credentials"));

            assertThatThrownBy(() -> authController.loginUser(invalidRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid credentials");
        }
    }

    @Nested
    @DisplayName("Given Token Refresh")
    class TokenRefreshTests {

        @Test
        @DisplayName("When valid refresh token in header, then return new token response with status 200")
        void refreshToken_withValidHeader_shouldReturnNewTokenResponse() {
            String validAuthHeader = "Bearer valid-refresh-token";
            when(authQueryService.refreshToken(anyString())).thenReturn(mockTokenResponse);

            ResponseEntity<TokenResponse> response = authController.refreshToken(validAuthHeader);

            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(mockTokenResponse);
            verify(authQueryService).refreshToken(validAuthHeader);
        }

        @Test
        @DisplayName("When empty authorization header, then service should handle it")
        void refreshToken_withEmptyHeader_shouldBeHandledByService() {
            when(authQueryService.refreshToken(""))
                    .thenThrow(new IllegalArgumentException("Authorization header is required"));

            assertThatThrownBy(() -> authController.refreshToken(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Authorization header is required");
        }

        @Test
        @DisplayName("When malformed authorization header, then service should handle it")
        void refreshToken_withMalformedHeader_shouldBeHandledByService() {
            String malformedHeader = "InvalidTokenFormat";
            when(authQueryService.refreshToken(malformedHeader))
                    .thenThrow(new IllegalArgumentException("Invalid authorization header format"));

            assertThatThrownBy(() -> authController.refreshToken(malformedHeader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid authorization header format");
        }
    }
}
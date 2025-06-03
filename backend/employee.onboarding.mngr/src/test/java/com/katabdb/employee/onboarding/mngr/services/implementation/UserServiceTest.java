package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserRole;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserStatus;
import com.katabdb.employee.onboarding.mngr.dto.user.UserResponse;
import com.katabdb.employee.onboarding.mngr.dto.user.UserUpdateRequest;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("When testing UserService")
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private final Integer EXISTING_USER_ID = 1;
    private final Integer NON_EXISTING_USER_ID = 99;
    private final String EXISTING_EMAIL = "existing@test.com";
    private final String NON_EXISTING_EMAIL = "nonexisting@test.com";

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(EXISTING_USER_ID)
                .name("Test User")
                .email(EXISTING_EMAIL)
                .password("encodedPassword")
                .role(UserRole.DEV)
                .status(UserStatus.APPROVED)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
    }

    @Nested
    @DisplayName("Given user retrieval operations")
    class UserRetrievalTests {

        @Test
        @DisplayName("When getting user by existing ID, then return user response")
        void getUserById_withExistingId_shouldReturnUserResponse() {
            when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(testUser));

            UserResponse result = userService.getUserById(EXISTING_USER_ID);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(EXISTING_USER_ID);
            verify(userRepository).findById(EXISTING_USER_ID);
        }

        @Test
        @DisplayName("When getting all users, then return list of user responses")
        void getAllUsers_shouldReturnListOfUsers() {
            when(userRepository.findAll()).thenReturn(List.of(testUser));

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(EXISTING_USER_ID);
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("When checking existing email, then return true")
        void existsByEmail_withExistingEmail_shouldReturnTrue() {
            when(userRepository.existsByEmail(EXISTING_EMAIL)).thenReturn(true);

            boolean result = userService.existsByEmail(EXISTING_EMAIL);

            assertThat(result).isTrue();
            verify(userRepository).existsByEmail(EXISTING_EMAIL);
        }

        @Test
        @DisplayName("When checking non-existing email, then return false")
        void existsByEmail_withNonExistingEmail_shouldReturnFalse() {
            when(userRepository.existsByEmail(NON_EXISTING_EMAIL)).thenReturn(false);

            boolean result = userService.existsByEmail(NON_EXISTING_EMAIL);

            assertThat(result).isFalse();
            verify(userRepository).existsByEmail(NON_EXISTING_EMAIL);
        }

        @Test
        @DisplayName("When finding by existing email, then return optional with user")
        void findByEmail_withExistingEmail_shouldReturnUser() {
            when(userRepository.findByEmail(EXISTING_EMAIL)).thenReturn(Optional.of(testUser));

            Optional<UserResponse> result = userService.findByEmail(EXISTING_EMAIL);

            assertThat(result).isPresent();
            assertThat(result.get().email()).isEqualTo(EXISTING_EMAIL);
            verify(userRepository).findByEmail(EXISTING_EMAIL);
        }

        @Test
        @DisplayName("When finding by non-existing email, then return empty optional")
        void findByEmail_withNonExistingEmail_shouldReturnEmpty() {
            when(userRepository.findByEmail(NON_EXISTING_EMAIL)).thenReturn(Optional.empty());

            Optional<UserResponse> result = userService.findByEmail(NON_EXISTING_EMAIL);

            assertThat(result).isEmpty();
            verify(userRepository).findByEmail(NON_EXISTING_EMAIL);
        }
    }

    @Nested
    @DisplayName("Given user update operations")
    class UserUpdateTests {

        private UserUpdateRequest updateRequest;

        @BeforeEach
        void setUpUpdateRequest() {
            updateRequest = new UserUpdateRequest(
                    "Updated Name",
                    "updated@test.com",
                    "ADMIN",
                    UserRole.DEV,
                    UserStatus.APPROVED
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("When updating password with empty or null value, then skip password update")
        void updateUser_withEmptyPassword_shouldSkipPasswordUpdate(String emptyPassword) {
            UserUpdateRequest requestWithEmptyPassword = new UserUpdateRequest(
                    null, null, emptyPassword, null, UserStatus.APPROVED
            );
            when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.updateUser(EXISTING_USER_ID, requestWithEmptyPassword);

            assertThat(result).isNotNull();
            verify(passwordService, never()).encodePassword(anyString());
        }
    }
}
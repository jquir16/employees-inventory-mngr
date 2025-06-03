package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.AccessRequestEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.domain.enums.RequestStatus;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserRole;
import com.katabdb.employee.onboarding.mngr.domain.enums.UserStatus;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsRequest;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsResponse;
import com.katabdb.employee.onboarding.mngr.repository.IAccessRequestRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("When testing AccessRequestsService")
class AccessRequestsServiceTest {

    @Mock
    private IAccessRequestRepository accessRequestRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private AccessRequestsService accessRequestsService;

    private AccessRequestsRequest validRequest;
    private AccessRequestEntity savedEntity;
    private UserEntity validUser;

    @BeforeEach
    void setUp() {
        validUser = UserEntity.builder()
                .name("test name")
                .role(UserRole.DEV)
                .email("test@test.com")
                .password("password")
                .status(UserStatus.APPROVED)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        String[] systems = {"Git", "Hub", "Email"};
        validRequest = AccessRequestsRequest.builder()
                .userId(1)
                .status(RequestStatus.APPROVED.toString())
                .systems(systems)
                .build();
        savedEntity = AccessRequestEntity.builder()
                .user(UserEntity.builder().id(1).build())
                .status(RequestStatus.APPROVED.toString())
                .systems(systems)
                .build();
    }

    @Nested
    @DisplayName("Given access request operations")
    class AccessRequestOperations {

        @Test
        @DisplayName("When getting access request by invalid ID, then throw exception")
        void getAccessRequestById_withInvalidId_shouldThrowException() {
            when(accessRequestRepository.findById(anyInt())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accessRequestsService.getAccessRequestById(999))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Access request not found with ID: 999");
        }

        @Test
        @DisplayName("When getting all access requests with empty repository, then return empty list")
        void getAllAccessRequests_withNoRequests_shouldReturnEmptyList() {
            when(accessRequestRepository.findAll()).thenReturn(Collections.emptyList());

            List<AccessRequestsResponse> result = accessRequestsService.getAllAccessRequests();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Given create access request operations")
    class CreateAccessRequestOperations {

        @Test
        @DisplayName("When creating access request with invalid user ID, then throw exception")
        void createAccessRequest_withInvalidUserId_shouldThrowException() {
            when(userRepository.existsById(anyInt())).thenReturn(false);

            assertThatThrownBy(() -> accessRequestsService.createAccessRequest(validRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found with ID: 1");
        }
    }

    @Nested
    @DisplayName("Given update access request operations")
    class UpdateAccessRequestOperations {

        @Test
        @DisplayName("When updating non-existent access request, then throw exception")
        void updateAccessRequest_withInvalidId_shouldThrowException() {
            when(accessRequestRepository.findById(anyInt())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accessRequestsService.updateAccessRequest(999, validRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Access request not found with ID: 999");
        }

        @Test
        @DisplayName("When updating with invalid user ID, then throw exception")
        void updateAccessRequest_withInvalidUserId_shouldThrowException() {
            AccessRequestsRequest invalidRequest = new AccessRequestsRequest(999, null, null);
            when(accessRequestRepository.findById(1)).thenReturn(Optional.of(savedEntity));
            when(userRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accessRequestsService.updateAccessRequest(1, invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found with ID: 999");
        }
    }

    @Nested
    @DisplayName("Given user-specific request operations")
    class UserSpecificOperations {

        @Test
        @DisplayName("When getting requests by valid user ID, then return user's requests")
        void getRequestsByUser_withValidUserId_shouldReturnRequests() {
            when(accessRequestRepository.findAllByUserId(1)).thenReturn(List.of(savedEntity));

            Optional<List<AccessRequestsResponse>> result = accessRequestsService.getRequestsByUser(1);

            assertThat(result).isPresent();
            assertThat(result.get()).hasSize(1);
            assertThat(result.get().get(0).userId()).isEqualTo(1);
        }

        @Test
        @DisplayName("When getting requests by user with no requests, then return empty list")
        void getRequestsByUser_withNoRequests_shouldReturnEmptyList() {
            when(accessRequestRepository.findAllByUserId(anyInt())).thenReturn(Collections.emptyList());

            Optional<List<AccessRequestsResponse>> result = accessRequestsService.getRequestsByUser(1);

            assertThat(result).isPresent();
            assertThat(result.get()).isEmpty();
        }
    }
}
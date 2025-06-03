package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.enums.RequestStatus;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsRequest;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsResponse;
import com.katabdb.employee.onboarding.mngr.services.spec.IAccessRequestsQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("When testing AccessRequestController")
class AccessRequestControllerTest {

    @Mock
    private IAccessRequestsQueryService accessRequestsQueryService;

    @InjectMocks
    private AccessRequestController accessRequestController;

    private AccessRequestsRequest validRequest;
    private AccessRequestsResponse sampleResponse;

    @BeforeEach
    void setUp() {
        String[] systems = {"Git", "Hub", "Email"};
        validRequest = AccessRequestsRequest.builder().userId(1).status(RequestStatus.APPROVED.toString()).systems(systems).build();
        sampleResponse = new AccessRequestsResponse(1, 1, systems, RequestStatus.APPROVED.toString(), new Date());
    }

    @Nested
    @DisplayName("Given GET requests for access requests")
    class GetRequestsTests {

        @Test
        @DisplayName("When getting access request by valid ID, then return 200 OK with response")
        void getById_withValidId_shouldReturnAccessRequest() {
            int validId = 1;
            when(accessRequestsQueryService.getAccessRequestById(validId)).thenReturn(sampleResponse);

            ResponseEntity<AccessRequestsResponse> response = accessRequestController.getById(validId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sampleResponse);
            verify(accessRequestsQueryService).getAccessRequestById(validId);
        }

        @Test
        @DisplayName("When getting all access requests, then return list of access requests")
        void getAll_shouldReturnListOfAccessRequests() {
            List<AccessRequestsResponse> expectedList = Collections.singletonList(sampleResponse);
            when(accessRequestsQueryService.getAllAccessRequests()).thenReturn(expectedList);

            List<AccessRequestsResponse> result = accessRequestController.getAll();

            assertThat(result).isEqualTo(expectedList);
            verify(accessRequestsQueryService).getAllAccessRequests();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 100})
        @DisplayName("When getting access requests by valid user ID, then return optional list")
        void getByUserId_withValidUserId_shouldReturnOptionalList(int userId) {
            List<AccessRequestsResponse> expectedList = Collections.singletonList(sampleResponse);
            when(accessRequestsQueryService.getRequestsByUser(userId))
                    .thenReturn(Optional.of(expectedList));

            ResponseEntity<Optional<List<AccessRequestsResponse>>> response =
                    accessRequestController.getByUserId(userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isPresent();
            assertThat(response.getBody().get()).isEqualTo(expectedList);
            verify(accessRequestsQueryService).getRequestsByUser(userId);
        }

        @Test
        @DisplayName("When getting access requests by non-existent user ID, then return empty optional")
        void getByUserId_withNonExistentUserId_shouldReturnEmptyOptional() {
            int nonExistentUserId = 999;
            when(accessRequestsQueryService.getRequestsByUser(nonExistentUserId))
                    .thenReturn(Optional.empty());

            ResponseEntity<Optional<List<AccessRequestsResponse>>> response =
                    accessRequestController.getByUserId(nonExistentUserId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Given POST requests for creating access requests")
    class PostRequestsTests {

        @Test
        @DisplayName("When creating a valid access request, then return 200 OK with created request")
        void createAccessRequest_withValidRequest_shouldReturnCreatedRequest() {
            when(accessRequestsQueryService.createAccessRequest(validRequest))
                    .thenReturn(sampleResponse);

            ResponseEntity<AccessRequestsResponse> response =
                    accessRequestController.createAccessRequest(validRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sampleResponse);
            verify(accessRequestsQueryService).createAccessRequest(validRequest);
        }
    }

    @Nested
    @DisplayName("Given PUT requests for updating access requests")
    class PutRequestsTests {

        @Test
        @DisplayName("When updating existing access request, then return 200 OK with updated request")
        void updateAccessRequest_withValidIdAndRequest_shouldReturnUpdatedRequest() {
            int existingId = 1;
            when(accessRequestsQueryService.updateAccessRequest(existingId, validRequest))
                    .thenReturn(sampleResponse);

            ResponseEntity<AccessRequestsResponse> response =
                    accessRequestController.updateAccessRequest(existingId, validRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sampleResponse);
            verify(accessRequestsQueryService).updateAccessRequest(existingId, validRequest);
        }

        @Test
        @DisplayName("When updating non-existent access request, then return 404 Not Found")
        void updateAccessRequest_withNonExistentId_shouldReturnNotFound() {
            int nonExistentId = 999;
            when(accessRequestsQueryService.updateAccessRequest(nonExistentId, validRequest))
                    .thenReturn(null);

            ResponseEntity<AccessRequestsResponse> response =
                    accessRequestController.updateAccessRequest(nonExistentId, validRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
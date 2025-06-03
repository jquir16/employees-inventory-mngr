package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import com.katabdb.employee.onboarding.mngr.dto.inventory.CreateInventoryRequest;
import com.katabdb.employee.onboarding.mngr.dto.inventory.InventoryResponse;
import com.katabdb.employee.onboarding.mngr.services.spec.IInventoryQueryService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Controller Tests")
class InventoryControllerTest {

    @Mock
    private IInventoryQueryService inventoryQueryService;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryResponse mockInventoryResponse;
    private CreateInventoryRequest mockCreateRequest;

    @BeforeEach
    void setUp() {
        mockInventoryResponse = InventoryResponse.builder()
                .id(1)
                .description("Test Item")
                .serialNumber("Test number")
                .assignedAt(new Date())
                .userId(1)
                .build();

        mockCreateRequest = CreateInventoryRequest.builder()
                .serialNumber("New number")
                .description("New Description")
                .build();
    }

    @Nested
    @DisplayName("GET /inventory/{id}")
    class GetInventoryById {

        @Test
        @DisplayName("Given valid inventory ID, when getting inventory by ID, then return inventory response")
        void givenValidId_whenGetById_thenReturnInventory() {
            int validId = 1;
            when(inventoryQueryService.getInventoryById(validId)).thenReturn(mockInventoryResponse);

            ResponseEntity<InventoryResponse> response = inventoryController.getById(validId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(mockInventoryResponse);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("Given invalid inventory ID, when getting inventory by ID, then return bad request")
        void givenInvalidId_whenGetById_thenReturnBadRequest(int invalidId) {
            when(inventoryQueryService.getInventoryById(invalidId)).thenThrow(IllegalArgumentException.class);

            assertThatThrownBy(() -> inventoryController.getById(invalidId))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("GET /inventory")
    class GetAllInventory {

        @Test
        @DisplayName("Given inventory items exist, when getting all inventory, then return list of items")
        void givenInventoryExists_whenGetAll_thenReturnInventoryList() {
            List<InventoryResponse> expectedList = Collections.singletonList(mockInventoryResponse);
            when(inventoryQueryService.getAllInventory()).thenReturn(expectedList);

            List<InventoryResponse> result = inventoryController.getAll();

            assertThat(result).isEqualTo(expectedList);
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Given no inventory items, when getting all inventory, then return empty list")
        void givenNoInventory_whenGetAll_thenReturnEmptyList() {
            when(inventoryQueryService.getAllInventory()).thenReturn(Collections.emptyList());

            List<InventoryResponse> result = inventoryController.getAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("POST /inventory")
    class CreateInventory {

        @Test
        @DisplayName("Given valid create request, when creating inventory, then return created inventory")
        void givenValidRequest_whenCreateInventory_thenReturnCreatedInventory() {
            when(inventoryQueryService.createInventoryItem(any(CreateInventoryRequest.class)))
                    .thenReturn(mockInventoryResponse);

            ResponseEntity<InventoryResponse> response = inventoryController.createInventory(mockCreateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(mockInventoryResponse);
        }
    }

    @Nested
    @DisplayName("GET /inventory/user/{userId}")
    class GetInventoryByUserId {

        @Test
        @DisplayName("Given valid user ID with inventory, when getting inventory by user ID, then return inventory list")
        void givenValidUserIdWithInventory_whenGetByUserId_thenReturnInventoryList() {
            int userId = 1;
            List<InventoryResponse> expectedList = Collections.singletonList(mockInventoryResponse);
            when(inventoryQueryService.getInventoryByUserId(userId)).thenReturn(Optional.of(expectedList));

            ResponseEntity<List<InventoryResponse>> response = inventoryController.getInventoryByUserId(userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedList);
        }

        @Test
        @DisplayName("Given valid user ID without inventory, when getting inventory by user ID, then return not found")
        void givenValidUserIdWithoutInventory_whenGetByUserId_thenReturnNotFound() {
            int userId = 2;
            when(inventoryQueryService.getInventoryByUserId(userId)).thenReturn(Optional.empty());

            ResponseEntity<List<InventoryResponse>> response = inventoryController.getInventoryByUserId(userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("PUT /inventory/{id}")
    class UpdateInventory {

        @Test
        @DisplayName("Given valid update request, when updating inventory, then return updated inventory")
        void givenValidRequest_whenUpdateInventory_thenReturnUpdatedInventory() {
            int inventoryId = 1;
            when(inventoryQueryService.updateInventory(anyInt(), any())).thenReturn(mockInventoryResponse);

            ResponseEntity<InventoryResponse> response = inventoryController.updateInventory(inventoryId, new InventoryEntity());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(mockInventoryResponse);
        }
    }

    @Nested
    @DisplayName("POST /inventory/{inventoryId}/assign/{userId}")
    class AssignInventoryToUser {

        @Test
        @DisplayName("Given valid inventory and user IDs, when assigning inventory, then return assigned inventory")
        void givenValidIds_whenAssignInventory_thenReturnAssignedInventory() {
            int inventoryId = 1;
            int userId = 1;
            when(inventoryQueryService.assignInventoryToUser(inventoryId, userId)).thenReturn(mockInventoryResponse);

            ResponseEntity<InventoryResponse> response = inventoryController.assignInventoryToUser(inventoryId, userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(mockInventoryResponse);
        }
    }

    @Nested
    @DisplayName("DELETE /inventory/{inventoryId}/user/{userId}")
    class RemoveInventoryFromUser {

        @Test
        @DisplayName("Given valid inventory and user IDs, when removing inventory, then return updated inventory")
        void givenValidIds_whenRemoveInventory_thenReturnUpdatedInventory() {
            int inventoryId = 1;
            int userId = 1;
            when(inventoryQueryService.removeInventoryFromUser(inventoryId, userId)).thenReturn(mockInventoryResponse);

            ResponseEntity<InventoryResponse> response = inventoryController.removeInventoryFromUser(inventoryId, userId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(mockInventoryResponse);
        }
    }
}
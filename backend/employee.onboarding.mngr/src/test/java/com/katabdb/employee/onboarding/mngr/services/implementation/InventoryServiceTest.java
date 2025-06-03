package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.inventory.CreateInventoryRequest;
import com.katabdb.employee.onboarding.mngr.dto.inventory.InventoryResponse;
import com.katabdb.employee.onboarding.mngr.exception.ResourceNotFoundException;
import com.katabdb.employee.onboarding.mngr.repository.IInventoryRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Service Unit Tests")
class InventoryServiceTest {

    @Mock
    private IInventoryRepository inventoryRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryEntity sampleInventory;
    private UserEntity sampleUser;
    private CreateInventoryRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleUser = UserEntity.builder().id(1).name("test").email("test@test.com").build();
        sampleInventory = new InventoryEntity(1, "Laptop", sampleUser, "sn123", new Date());
        sampleRequest = new CreateInventoryRequest("Laptop", "SN123");
    }

    @Nested
    @DisplayName("Given Inventory Operations")
    class InventoryOperationsTests {

        @Test
        @DisplayName("When getting inventory by valid ID, then return inventory response")
        void getInventoryById_withValidId_shouldReturnInventoryResponse() {
            when(inventoryRepository.findById(1)).thenReturn(Optional.of(sampleInventory));

            InventoryResponse response = inventoryService.getInventoryById(1);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1);
            assertThat(response.description()).isEqualTo("Laptop");
            verify(inventoryRepository).findById(1);
        }

        @Test
        @DisplayName("When getting inventory by invalid ID, then throw ResourceNotFoundException")
        void getInventoryById_withInvalidId_shouldThrowException() {
            when(inventoryRepository.findById(anyInt())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.getInventoryById(999))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Inventory not found with id: 999");
        }

        @Test
        @DisplayName("When getting all inventory, then return list of inventory responses")
        void getAllInventory_shouldReturnAllInventoryItems() {
            when(inventoryRepository.findAll()).thenReturn(List.of(sampleInventory));

            List<InventoryResponse> responses = inventoryService.getAllInventory();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).id()).isEqualTo(1);
            verify(inventoryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Given User Inventory Operations")
    class UserInventoryOperationsTests {

        @Test
        @DisplayName("When getting inventory by valid user ID, then return inventory list")
        void getInventoryByUserId_withValidUserId_shouldReturnInventoryList() {
            when(inventoryRepository.findAllByUserId(1)).thenReturn(List.of(sampleInventory));

            Optional<List<InventoryResponse>> response = inventoryService.getInventoryByUserId(1);

            assertThat(response).isPresent();
            assertThat(response.get()).hasSize(1);
            verify(inventoryRepository).findAllByUserId(1);
        }

        @Test
        @DisplayName("When getting inventory by invalid user ID, then return empty optional")
        void getInventoryByUserId_withInvalidUserId_shouldReturnEmptyOptional() {
            when(inventoryRepository.findAllByUserId(999)).thenReturn(Collections.emptyList());

            Optional<List<InventoryResponse>> response = inventoryService.getInventoryByUserId(999);

            assertThat(response).isPresent();
            assertThat(response.get()).isEmpty();
        }

        @Test
        @DisplayName("When assigning inventory to valid user, then return updated inventory")
        void assignInventoryToUser_withValidIds_shouldReturnUpdatedInventory() {
            when(inventoryRepository.findById(1)).thenReturn(Optional.of(sampleInventory));
            when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));
            when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(sampleInventory);

            InventoryResponse response = inventoryService.assignInventoryToUser(1, 1);

            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(1);
            verify(inventoryRepository).save(any(InventoryEntity.class));
        }

        @Test
        @DisplayName("When removing inventory from user with valid IDs, then return updated inventory")
        void removeInventoryFromUser_withValidIds_shouldReturnUpdatedInventory() {
            when(inventoryRepository.findById(1)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(sampleInventory);

            InventoryResponse response = inventoryService.removeInventoryFromUser(1, 1);

            assertThat(response).isNotNull();
            assertThat(response.userId()).isNull();
            verify(inventoryRepository).save(any(InventoryEntity.class));
        }
    }

    @Nested
    @DisplayName("Given Inventory Update Operations")
    class InventoryUpdateTests {

        @Test
        @DisplayName("When updating inventory with valid data, then return updated inventory")
        void updateInventory_withValidData_shouldReturnUpdatedInventory() {
            InventoryEntity updatedData = new InventoryEntity();
            updatedData.setDescription("Updated Laptop");
            updatedData.setSerialNumber("SN456");

            when(inventoryRepository.findById(1)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(sampleInventory);

            InventoryResponse response = inventoryService.updateInventory(1, updatedData);

            assertThat(response).isNotNull();
            verify(inventoryRepository).save(any(InventoryEntity.class));
        }

        @Test
        @DisplayName("When updating non-existent inventory, then throw ResourceNotFoundException")
        void updateInventory_withInvalidId_shouldThrowException() {
            when(inventoryRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.updateInventory(999, new InventoryEntity()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Inventory not found with id: 999");
        }

        @Test
        @DisplayName("When updating inventory with partial data, then update only provided fields")
        void updateInventory_withPartialData_shouldUpdateOnlyProvidedFields() {
            InventoryEntity partialUpdate = new InventoryEntity();
            partialUpdate.setSerialNumber("SN789");

            when(inventoryRepository.findById(1)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(InventoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            InventoryResponse response = inventoryService.updateInventory(1, partialUpdate);

            assertThat(response).isNotNull();
            assertThat(response.serialNumber()).isEqualTo("SN789");
            assertThat(response.description()).isEqualTo("Laptop");
        }
    }
}
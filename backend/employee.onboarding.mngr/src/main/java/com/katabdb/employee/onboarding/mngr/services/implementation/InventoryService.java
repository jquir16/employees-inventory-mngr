package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.inventory.CreateInventoryRequest;
import com.katabdb.employee.onboarding.mngr.dto.inventory.InventoryResponse;
import com.katabdb.employee.onboarding.mngr.exception.ResourceNotFoundException;
import com.katabdb.employee.onboarding.mngr.repository.IInventoryRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.spec.IInventoryQueryService;
import com.katabdb.employee.onboarding.mngr.validation.mappers.InventoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService implements IInventoryQueryService {

    private static final String INVENTORY_NOT_FOUND = "Inventory not found with id: ";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String USER_INVENTORY_MISMATCH = "User and inventory do not match";

    private final IInventoryRepository inventoryRepository;
    private final IUserRepository userRepository;

    public InventoryService(IInventoryRepository inventoryRepository, IUserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public InventoryResponse getInventoryById(Integer id) {
        InventoryEntity inventory = findInventoryByIdOrThrow(id);
        return InventoryMapper.buildRequestFromEntity(inventory);
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryMapper::buildRequestFromEntity)
                .toList();
    }

    @Override
    public InventoryResponse createInventoryItem(CreateInventoryRequest inventoryRequest) {
        InventoryEntity inventoryItem = InventoryMapper.buildEntityFromRequest(inventoryRequest);
        InventoryEntity savedItem = inventoryRepository.save(inventoryItem);
        return InventoryMapper.buildRequestFromEntity(savedItem);
    }

    @Override
    public Optional<List<InventoryResponse>> getInventoryByUserId(Integer userId) {
        return Optional.of(
                inventoryRepository.findAllByUserId(userId)
                        .stream()
                        .map(InventoryMapper::buildRequestFromEntity)
                        .toList()
        );
    }

    @Override
    public InventoryResponse removeInventoryFromUser(Integer inventoryId, Integer userId) {
        InventoryEntity inventory = findInventoryByIdOrThrow(inventoryId);

        validateUserInventoryMatch(inventory, userId);

        inventory.setUser(null);
        inventory.setAssignedAt(new Date());

        return updateInventory(inventory);
    }

    @Override
    public InventoryResponse assignInventoryToUser(Integer inventoryId, Integer userId) {
        InventoryEntity inventory = findInventoryByIdOrThrow(inventoryId);
        UserEntity user = findUserByIdOrThrow(userId);

        inventory.setUser(user);
        inventory.setAssignedAt(new Date());

        return updateInventory(inventory);
    }

    @Override
    public InventoryResponse updateInventory(Integer id, InventoryEntity inventoryUpdate) {
        InventoryEntity inventory = findInventoryByIdOrThrow(id);

        updateInventoryFields(inventory, inventoryUpdate);
        inventory.setAssignedAt(new Date());

        return InventoryMapper.buildRequestFromEntity(inventoryRepository.save(inventory));
    }

    private InventoryEntity findInventoryByIdOrThrow(Integer id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(INVENTORY_NOT_FOUND + id));
    }

    private UserEntity findUserByIdOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
    }

    private void validateUserInventoryMatch(InventoryEntity inventory, Integer userId) {
        if (!inventory.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(USER_INVENTORY_MISMATCH);
        }
    }

    private void updateInventoryFields(InventoryEntity target, InventoryEntity source) {
        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }
        if (source.getUser() != null) {
            target.setUser(source.getUser());
        }
        if (source.getSerialNumber() != null) {
            target.setSerialNumber(source.getSerialNumber());
        }
    }

    private InventoryResponse updateInventory(InventoryEntity inventory) {
        InventoryEntity updatedInventory = inventoryRepository.save(inventory);
        return InventoryMapper.buildRequestFromEntity(updatedInventory);
    }
}
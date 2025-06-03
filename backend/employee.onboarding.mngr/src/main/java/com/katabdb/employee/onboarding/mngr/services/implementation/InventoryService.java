package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import com.katabdb.employee.onboarding.mngr.dto.inventory.CreateInventoryRequest;
import com.katabdb.employee.onboarding.mngr.dto.inventory.InventoryResponse;
import com.katabdb.employee.onboarding.mngr.repository.IInventoryRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.spec.IInventoryQueryService;
import com.katabdb.employee.onboarding.mngr.validation.mappers.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InventoryService implements IInventoryQueryService {

    private final IInventoryRepository inventoryRepository;
    private final IUserRepository userRepository;

    @Autowired
    public InventoryService(IInventoryRepository inventoryRepository, IUserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public InventoryResponse getInventoryById(Integer id) {
        return InventoryMapper.buildRequestFromEntity(inventoryRepository.getInventoryEntityById(id));
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
        System.out.println(inventoryRequest);
        var inventoryItem = InventoryMapper.buildEntityFromRequest(inventoryRequest);
        inventoryRepository.save(inventoryItem);
        return InventoryMapper.buildRequestFromEntity(inventoryItem);
    }

    @Override
    public Optional<List<InventoryResponse>> getInventoryByUserId(Integer id) {
        return Optional.of(
                inventoryRepository.findAllByUserId(id)
                        .stream()
                        .map(InventoryMapper::buildRequestFromEntity)
                        .toList()
        );
    }

    @Override
    public InventoryResponse removeInventoryFromUser(Integer id, Integer userId) {
        var inventoryEntity =  inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory does not exist"));
        if (!Objects.equals(inventoryEntity.getUser().getId(), userId)) {
            throw new IllegalArgumentException("this user and inventory do not match");
        }
        inventoryEntity.setUser(null);
        return updateInventory(inventoryEntity.getId(), inventoryEntity);
    }

    @Override
    public InventoryResponse assignInventoryToUser(Integer id, Integer userId) {
        var inventoryEntity =  inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory does not exist"));
        var user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("user does not exist"));
        inventoryEntity.setUser(user);
        return updateInventory(inventoryEntity.getId(), inventoryEntity);
    }

    @Override
    public InventoryResponse updateInventory(Integer id, InventoryEntity inventoryUpdate) {
        var inventoryToUpdate = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory does not exist"));
        if (inventoryUpdate.getDescription() != null) {
            inventoryToUpdate.setDescription(inventoryUpdate.getDescription());
        }
        if (inventoryUpdate.getUser() != null) {
            inventoryToUpdate.setUser(inventoryUpdate.getUser());
        }
        if (inventoryUpdate.getSerialNumber() != null) {
            inventoryToUpdate.setSerialNumber(inventoryUpdate.getSerialNumber());
        }
        inventoryToUpdate.setAssignedAt(new Date());

        return InventoryMapper.buildRequestFromEntity(inventoryRepository.save(inventoryToUpdate));
    }
}

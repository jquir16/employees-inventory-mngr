package com.katabdb.employee.onboarding.mngr.services.spec;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import com.katabdb.employee.onboarding.mngr.dto.inventory.CreateInventoryRequest;
import com.katabdb.employee.onboarding.mngr.dto.inventory.InventoryResponse;

import java.util.List;
import java.util.Optional;

public interface IInventoryQueryService {
    InventoryResponse getInventoryById(Integer id);
    List<InventoryResponse> getAllInventory();
    InventoryResponse createInventoryItem(CreateInventoryRequest inventoryRequest);
    Optional<List<InventoryResponse>> getInventoryByUserId(Integer id);
    InventoryResponse removeInventoryFromUser(Integer id, Integer userId);
    InventoryResponse assignInventoryToUser(Integer id, Integer userId);
    InventoryResponse updateInventory(Integer id, InventoryEntity inventoryEntity);
}

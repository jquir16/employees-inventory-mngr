package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import com.katabdb.employee.onboarding.mngr.dto.inventory.CreateInventoryRequest;
import com.katabdb.employee.onboarding.mngr.dto.inventory.InventoryResponse;
import com.katabdb.employee.onboarding.mngr.services.spec.IInventoryQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final IInventoryQueryService inventoryQueryService;

    @Autowired
    public InventoryController(IInventoryQueryService inventoryQueryService) {
        this.inventoryQueryService = inventoryQueryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getById(@Validated @PathVariable Integer id) {
        return ResponseEntity.ok(inventoryQueryService.getInventoryById(id));
    }

    @GetMapping
    public List<InventoryResponse> getAll()
    {
        return inventoryQueryService.getAllInventory();
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody @Validated CreateInventoryRequest request) {
        InventoryResponse created = inventoryQueryService.createInventoryItem(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByUserId(@PathVariable Integer userId) {
        return inventoryQueryService.getInventoryByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Integer id,
            @RequestBody @Validated InventoryEntity inventoryUpdate) {
        InventoryResponse updated = inventoryQueryService.updateInventory(id, inventoryUpdate);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{inventoryId}/assign/{userId}")
    public ResponseEntity<InventoryResponse> assignInventoryToUser(
            @PathVariable Integer inventoryId,
            @PathVariable Integer userId) {
        InventoryResponse response = inventoryQueryService.assignInventoryToUser(inventoryId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{inventoryId}/user/{userId}")
    public ResponseEntity<InventoryResponse> removeInventoryFromUser(
            @PathVariable Integer inventoryId,
            @PathVariable Integer userId) {
        InventoryResponse response = inventoryQueryService.removeInventoryFromUser(inventoryId, userId);
        return ResponseEntity.ok(response);
    }
}

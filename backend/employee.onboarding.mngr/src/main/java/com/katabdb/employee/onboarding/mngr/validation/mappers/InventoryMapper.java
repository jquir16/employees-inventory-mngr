package com.katabdb.employee.onboarding.mngr.validation.mappers;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import com.katabdb.employee.onboarding.mngr.dto.inventory.CreateInventoryRequest;
import com.katabdb.employee.onboarding.mngr.dto.inventory.InventoryResponse;

public class InventoryMapper {

    public static InventoryResponse buildRequestFromEntity(InventoryEntity entity) {
        if (entity == null) return null;
        return InventoryResponse.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .serialNumber(entity.getSerialNumber())
                .assignedAt(entity.getAssignedAt())
                .build();
    }

    public static InventoryEntity buildEntityFromRequest(CreateInventoryRequest createInventoryRequest) {
        return InventoryEntity.builder()
                .serialNumber(createInventoryRequest.serialNumber())
                .description(createInventoryRequest.description())
                .build();
    }
}

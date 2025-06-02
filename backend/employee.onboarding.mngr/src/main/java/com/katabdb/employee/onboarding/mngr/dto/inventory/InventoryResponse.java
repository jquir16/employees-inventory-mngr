package com.katabdb.employee.onboarding.mngr.dto.inventory;

import lombok.Builder;

import java.util.Date;

@Builder
public record InventoryResponse(
        int id,
        String description,
        Integer userId,
        String serialNumber,
        Date assignedAt
) {
}

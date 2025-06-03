package com.katabdb.employee.onboarding.mngr.dto.inventory;

import lombok.Builder;

@Builder
public record CreateInventoryRequest (
        String description,
        String serialNumber
){}

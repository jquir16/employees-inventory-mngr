package com.katabdb.employee.onboarding.mngr.dto.inventory;

public record CreateInventoryRequest (
        String description,
        String serialNumber
){}

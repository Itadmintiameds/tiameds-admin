package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.Data;

@Data
public class PharmacyTypeDto {
    private Long pharmacyTypeId;
    private String pharmacyTypeName;
    private boolean isActive;
}

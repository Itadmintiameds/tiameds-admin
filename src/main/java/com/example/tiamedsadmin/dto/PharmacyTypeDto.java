package com.example.tiamedsadmin.dto;

import lombok.Data;

@Data
public class PharmacyTypeDto {
    private Long pharmacyTypeId;
    private String pharmacyTypeName;
    private boolean isActive;
}

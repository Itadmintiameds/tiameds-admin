package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.Data;

@Data
public class PharmacyDocumentVerifyDto {
    private String pharmacyRegistrationId;
    private Long registrationDocumentId;
    private boolean isVerified;
}

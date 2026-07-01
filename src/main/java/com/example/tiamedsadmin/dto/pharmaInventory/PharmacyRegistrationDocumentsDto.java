package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PharmacyRegistrationDocumentsDto {

    private Long registrationDocumentId;
    private String documentNumber;
    private String documentType;
    private String documentUrl;
    private LocalDateTime issueDate;
    private String issueAuthority;
    private LocalDateTime expiryDate;
    private boolean isActive;
    private boolean isVerified;
}

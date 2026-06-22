package com.example.tiamedsadmin.dto;

import lombok.Data;

@Data
public class PharmacyRegistrationDocumentsDto {

    private Long registrationDocumentId;
    private String documentType;
    private String documentUrl;
    private boolean isActive;
    private boolean isVerified;
}

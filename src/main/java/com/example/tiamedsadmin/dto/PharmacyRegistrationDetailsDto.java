package com.example.tiamedsadmin.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PharmacyRegistrationDetailsDto {

    private String pharmacyRegistrationId;
    private String pharmacyName;
    private PharmacyTypeDto pharmacyType;
    private String pharmacyEmail;
    private String pharmacyPhone;
    private String pharmacyDlNo;
    private LocalDateTime pharmacyDlExpiryDate;
    private String pharmacyGstNo;
    private String pharmacyPanNo;
    private String pharmacyBusinessRegistrationNo;
    private String pharmacyAddress;
    private String pharmacyId;
    private List<PharmacyStatusReviewDto> pharmacyStatusReviews;
    private List<PharmacyRegistrationDocumentsDto> pharmacyRegistrationDocuments;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

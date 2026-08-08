package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PharmacyRegistrationDetailsDto {

    private String pharmacyRegistrationId;
    private String userId;
    private String pharmacyName;
    private String pharmacyType;
    private String pharmacyEmail;
    private String pharmacyPhone;
    private String panNumber;
    private String gstNumber;
    private String pharmacyBranch;
    private String pharmacyBuildingNo;
    private String pharmacyStreet;
    private String pharmacyCity;
    private String pharmacyTaluka;
    private String pharmacyDistricts;
    private Long pharmacyPincode;
    private String pharmacyLandmark;
    private String pharmacyState;
    private Long organizationId;
    private String organizationName;
    private String organizationType;
    private String ownershipType;
    private String organizationPanNumber;
    private String organizationGstNumber;
    private Boolean centralizedInventory;
    private String pharmacyId;
    private String registrationStatus; // DRAFT or SUBMITTED
    private List<PharmacyStatusReviewDto> pharmacyStatusReviews;
    private List<PharmacyRegistrationDocumentsDto> pharmacyRegistrationDocuments;
    private List<PharmacyRegistrationWareHouseDto> pharmacyRegistrationWareHouses;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

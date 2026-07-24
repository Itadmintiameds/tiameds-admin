package com.example.tiamedsadmin.entity.pharmaInventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pharmacy_registration_details")
public class PharmacyRegistrationDetails {

    @Id
    @Column(name = "pharmacy_registration_id")
    private String pharmacyRegistrationId;

    @Column(name = "user_id", nullable = false, length = 60)
    private String userId;

    // Nullable so a half-filled DRAFT can be saved; completeness is enforced at submit time
    @Column(name = "pharmacy_name", length = 60)
    private String pharmacyName;

    @Column(name = "pharmacy_type", length = 60)
    private String pharmacyType;

    @Column(name = "pharmacy_email", length = 60)
    private String pharmacyEmail;

    @Column(name = "pharmacy_phone", length = 60)
    private String pharmacyPhone;

    @Column(name = "pan_number", length = 10)
    private String panNumber;

    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @Column(name = "pharmacy_branch", length = 60)
    private String pharmacyBranch;

    @Column(name = "pharmacy_building_no", length = 60)
    private String pharmacyBuildingNo;

    @Column(name = "pharmacy_street", length = 60)
    private String pharmacyStreet;

    @Column(name = "pharmacy_city", length = 60)
    private String pharmacyCity;

    @Column(name = "pharmacy_taluka", length = 60)
    private String pharmacyTaluka;

    @Column(name = "pharmacy_districts", length = 60)
    private String pharmacyDistricts;

    @Column(name = "pharmacy_pincode", length = 6)
    private Long pharmacyPincode;

    @Column(name = "pharmacy_landmark", length = 60)
    private String pharmacyLandmark;

    @Column(name = "pharmacy_state", length = 60)
    private String pharmacyState;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "organization_name", length = 60)
    private String organizationName;

    @Column(name = "organization_type", length = 60)
    private String organizationType;

    @Column(name = "ownership_type", length = 60)
    private String ownershipType;

    @Column(name = "organization_pan_number", length = 10)
    private String organizationPanNumber;

    @Column(name = "organization_gst_number", length = 20)
    private String organizationGstNumber;

    @Column(name = "pharmacy_id", unique = true, length = 60)        // Pharmacy ID After Approval
    private String pharmacyId;

    // Nullable for legacy rows created before this column existed; null is treated as SUBMITTED
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", length = 20)
    private RegistrationStatus registrationStatus;

    @OneToMany(mappedBy = "pharmacy_registration_id", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PharmacyStatusReview> pharmacyStatusReview;

    @OneToMany(mappedBy = "pharmacy_registration_id", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PharmacyRegistrationDocuments> pharmacyRegistrationDocuments;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "created_by", length = 60)
    private String createdBy;

    @Column(name = "updated_by", length = 60)
    private String updatedBy;
}

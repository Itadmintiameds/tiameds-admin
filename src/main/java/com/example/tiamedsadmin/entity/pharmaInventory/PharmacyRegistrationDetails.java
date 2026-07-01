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

    @Column(name = "pharmacy_name", nullable = false, length = 60)
    private String pharmacyName;

    @Column(name = "pharmacy_type", nullable = false, length = 60)
    private String pharmacyType;

    @Column(name = "pharmacy_email", unique = true, nullable = false, length = 60)
    private String pharmacyEmail;

    @Column(name = "pharmacy_phone", unique = true, length = 60)
    private String pharmacyPhone;

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

    @Column(name = "pharmacy_id", unique = true, length = 60)        // Pharmacy ID After Approval
    private String pharmacyId;

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

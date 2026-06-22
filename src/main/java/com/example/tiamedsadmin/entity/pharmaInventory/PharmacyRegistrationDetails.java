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

    @Column(name = "pharmacy_type",  nullable = false, length = 60)
    private String pharmacyType;

    @Column(name = "pharmacy_email", unique = true, nullable = false, length = 60)
    private String pharmacyEmail;

    @Column(name = "pharmacy_phone", unique = true, length = 60)
    private String pharmacyPhone;

    @Column(name = "pharmacy_dl_no", unique = true, length = 60)        // Drug License Number
    private String pharmacyDlNo;

    @Column(name = "pharmacy_dl_expiry_date")       // Drug License Expiry Date
    private LocalDateTime pharmacyDlExpiryDate;

    @Column(name = "pharmacy_gst_no", unique = true, length = 60)
    private String pharmacyGstNo;

    @Column(name = "pharmacy_pan_no", unique = true, length = 100)
    private String pharmacyPanNo;

    @Column(name = "pharmacy_business_registration_no", unique = true, length = 100)
    private String pharmacyBusinessRegistrationNo;

    @Column(name = "pharmacy_address")
    private String pharmacyAddress;

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

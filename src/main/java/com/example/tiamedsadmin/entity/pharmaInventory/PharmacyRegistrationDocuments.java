package com.example.tiamedsadmin.entity.pharmaInventory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "pharmacy_registration_documents")
public class PharmacyRegistrationDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_document_id")
    private Long registrationDocumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_registration_id")
    private PharmacyRegistrationDetails pharmacy_registration_id;

    @Column(name = "document_number", length = 60)
    private String documentNumber;

    @Column(name = "document_type", nullable = false, length = 100)
    private String documentType;

    @Column(name = "document_url")
    private String documentUrl;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "issue_authority", length = 100)
    private String issueAuthority;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    @Column(name = "is_verified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isVerified;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

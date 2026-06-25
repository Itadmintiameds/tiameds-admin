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
@Table(name = "pharmacy_status_review")
public class PharmacyStatusReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_registration_id")
    private PharmacyRegistrationDetails pharmacy_registration_id;

    @Column(name = "status", nullable = false, length = 60)
    private String status;

    @Column(name = "remark")
    private String remark;

    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy; // admin username/ID later

    @Column(name = "status_date")
    private LocalDateTime statusDate;

}

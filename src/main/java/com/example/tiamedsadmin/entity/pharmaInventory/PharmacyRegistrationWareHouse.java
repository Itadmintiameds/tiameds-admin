package com.example.tiamedsadmin.entity.pharmaInventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pharmacy_registration_warehouse")
public class PharmacyRegistrationWareHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacy_registration_warehouse_id")
    private Long pharmacyRegistrationWarehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "warehouse_code")
    private String warehouseCode;   // Optional

    @Column(name = "warehouse_address")
    private String warehouseAddress;

    @Column(name = "contact_person_name")
    private String contactPersonName;   // Optional

    @Column(name = "mobile_number")
    private String mobileNumber;   // Optional

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_registration_id")
    private PharmacyRegistrationDetails pharmacyRegistrationId;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class PharmaciesByUserIdDto {

    private String pharmacyReqId;
    private String pharmacyId;
    private String pharmacyName;
    private String type;
    private String pharmacyCity;
    private String status;
    private LocalDateTime updatedDate;
}

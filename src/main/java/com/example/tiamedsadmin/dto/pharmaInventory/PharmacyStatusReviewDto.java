package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PharmacyStatusReviewDto {

    private String registrationId;
    private Long statusId;
    private String status;
    private String remark;
    private LocalDateTime statusDate;
}

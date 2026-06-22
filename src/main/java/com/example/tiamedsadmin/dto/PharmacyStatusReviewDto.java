package com.example.tiamedsadmin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PharmacyStatusReviewDto {

    private Long statusId;
    private String status;
    private String remark;
    private LocalDateTime statusDate;
}

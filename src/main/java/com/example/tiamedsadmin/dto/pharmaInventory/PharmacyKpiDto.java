package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyKpiDto {

    private long totalPharmacies;
    private long approved;
    private long underReview;
    private long actionRequired;
    private long rejected;
    private long drafts;
}

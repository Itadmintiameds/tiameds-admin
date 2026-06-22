package com.example.tiamedsadmin.mapper.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyStatusReviewDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyStatusReview;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PharmacyStatusReviewMapper {

    public PharmacyStatusReviewDto  toDto(PharmacyStatusReview pharmacyStatusReview) {
        if (pharmacyStatusReview == null) {
            return null;
        }
        PharmacyStatusReviewDto dto = new PharmacyStatusReviewDto();
        dto.setStatusId(pharmacyStatusReview.getStatusId());
        dto.setStatus(pharmacyStatusReview.getStatus());
        dto.setRemark(pharmacyStatusReview.getRemark());
        dto.setStatusDate(pharmacyStatusReview.getStatusDate());
        return dto;
    }

    public List<PharmacyStatusReviewDto> toDtoList(List<PharmacyStatusReview> pharmacyStatusReviews) {
        return pharmacyStatusReviews.stream()
                .map(this::toDto)
                .toList();
    }
}

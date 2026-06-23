package com.example.tiamedsadmin.mapper.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PharmacyRegistrationDetailsMapper {

    private final PharmacyTypeMapper pharmacyTypeMapper;
    private final PharmacyStatusReviewMapper pharmacyStatusReviewMapper;
    private final PharmacyRegistrationDocumentsMapper pharmacyRegistrationDocumentsMapper;

    public PharmacyRegistrationDetailsDto toDto(PharmacyRegistrationDetails pharmacyRegistrationDetails) {
        if (pharmacyRegistrationDetails == null) {
            return null;
        }

        PharmacyRegistrationDetailsDto dto = new PharmacyRegistrationDetailsDto();
        dto.setPharmacyRegistrationId(pharmacyRegistrationDetails.getPharmacyRegistrationId());
        dto.setPharmacyName(pharmacyRegistrationDetails.getPharmacyName());
        dto.setPharmacyType(pharmacyRegistrationDetails.getPharmacyType());
        dto.setPharmacyEmail(pharmacyRegistrationDetails.getPharmacyEmail());
        dto.setPharmacyPhone(pharmacyRegistrationDetails.getPharmacyPhone());
        dto.setPharmacyDlNo(pharmacyRegistrationDetails.getPharmacyDlNo());
        dto.setPharmacyDlExpiryDate(pharmacyRegistrationDetails.getPharmacyDlExpiryDate());
        dto.setPharmacyGstNo(pharmacyRegistrationDetails.getPharmacyGstNo());
        dto.setPharmacyPanNo(pharmacyRegistrationDetails.getPharmacyPanNo());
        dto.setPharmacyBusinessRegistrationNo(pharmacyRegistrationDetails.getPharmacyBusinessRegistrationNo());
        dto.setPharmacyAddress(pharmacyRegistrationDetails.getPharmacyAddress());
        dto.setPharmacyId(pharmacyRegistrationDetails.getPharmacyId());

        if (pharmacyRegistrationDetails.getPharmacyStatusReview() != null) {
            dto.setPharmacyStatusReviews(pharmacyStatusReviewMapper.toDtoList(pharmacyRegistrationDetails.getPharmacyStatusReview()));
        } else {
            dto.setPharmacyStatusReviews(Collections.emptyList());
        }

        if (pharmacyRegistrationDetails.getPharmacyRegistrationDocuments() != null) {
            dto.setPharmacyRegistrationDocuments(
                    pharmacyRegistrationDetails.getPharmacyRegistrationDocuments().stream()
                            .map(pharmacyRegistrationDocumentsMapper::toDto)
                            .toList()
            );
        } else {
            dto.setPharmacyRegistrationDocuments(Collections.emptyList());
        }

        dto.setCreatedDate(pharmacyRegistrationDetails.getCreatedDate());
        dto.setUpdatedDate(pharmacyRegistrationDetails.getUpdatedDate());

        return dto;
    }

    public List<PharmacyRegistrationDetailsDto> toDtoList(List<PharmacyRegistrationDetails> pharmacyRegistrationDetails) {
        if (pharmacyRegistrationDetails.isEmpty()) {
            return List.of();
        }
        return pharmacyRegistrationDetails.stream()
                .map(this::toDto)
                .toList();
    }

}

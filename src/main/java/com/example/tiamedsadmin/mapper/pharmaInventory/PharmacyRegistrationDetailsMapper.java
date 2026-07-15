package com.example.tiamedsadmin.mapper.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmaciesByUserIdDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyStatusReview;

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
        dto.setUserId(pharmacyRegistrationDetails.getUserId());
        dto.setPharmacyName(pharmacyRegistrationDetails.getPharmacyName());
        dto.setPharmacyType(pharmacyRegistrationDetails.getPharmacyType());
        dto.setPharmacyEmail(pharmacyRegistrationDetails.getPharmacyEmail());
        dto.setPharmacyPhone(pharmacyRegistrationDetails.getPharmacyPhone());
        dto.setPanNumber(pharmacyRegistrationDetails.getPanNumber());
        dto.setGstNumber(pharmacyRegistrationDetails.getGstNumber());
        dto.setPharmacyBranch(pharmacyRegistrationDetails.getPharmacyBranch());
        dto.setPharmacyBuildingNo(pharmacyRegistrationDetails.getPharmacyBuildingNo());
        dto.setPharmacyStreet(pharmacyRegistrationDetails.getPharmacyStreet());
        dto.setPharmacyCity(pharmacyRegistrationDetails.getPharmacyCity());
        dto.setPharmacyTaluka(pharmacyRegistrationDetails.getPharmacyTaluka());
        dto.setPharmacyDistricts(pharmacyRegistrationDetails.getPharmacyDistricts());
        dto.setPharmacyPincode(pharmacyRegistrationDetails.getPharmacyPincode());
        dto.setPharmacyLandmark(pharmacyRegistrationDetails.getPharmacyLandmark());
        dto.setPharmacyState(pharmacyRegistrationDetails.getPharmacyState());
        dto.setOrganizationId(pharmacyRegistrationDetails.getOrganizationId());
        dto.setOrganizationName(pharmacyRegistrationDetails.getOrganizationName());
        dto.setOrganizationType(pharmacyRegistrationDetails.getOrganizationType());
        dto.setOwnershipType(pharmacyRegistrationDetails.getOwnershipType());
        dto.setOrganizationPanNumber(pharmacyRegistrationDetails.getOrganizationPanNumber());
        dto.setOrganizationGstNumber(pharmacyRegistrationDetails.getOrganizationGstNumber());
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

    public PharmaciesByUserIdDto toPharmaciesByUserIdDto(PharmacyRegistrationDetails details) {
        if (details == null) {
            return null;
        }

        PharmaciesByUserIdDto dto = new PharmaciesByUserIdDto();
        dto.setPharmacyReqId(details.getPharmacyRegistrationId());
        dto.setPharmacyId(details.getPharmacyId());
        dto.setPharmacyName(details.getPharmacyName());
        dto.setType(details.getPharmacyType());
        dto.setPharmacyCity(details.getPharmacyCity());
        
        if (details.getPharmacyStatusReview() != null && !details.getPharmacyStatusReview().isEmpty()) {
            PharmacyStatusReview latestReview = 
                details.getPharmacyStatusReview().stream()
                    .max(Comparator.comparing(
                        PharmacyStatusReview::getStatusDate, 
                        Comparator.nullsFirst(Comparator.naturalOrder())
                    ))
                    .orElse(null);
            if (latestReview != null) {
                dto.setStatus(latestReview.getStatus());
            }
        } else {
            dto.setStatus("Pending");
        }

        dto.setUpdatedDate(details.getUpdatedDate());

        return dto;
    }

    public List<PharmaciesByUserIdDto> toPharmaciesByUserIdDtoList(List<PharmacyRegistrationDetails> detailsList) {
        if (detailsList.isEmpty()) {
            return List.of();
        }
        return detailsList.stream()
                .map(this::toPharmaciesByUserIdDto)
                .toList();
    }

}

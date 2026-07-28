package com.example.tiamedsadmin.mapper.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmaciesByUserIdDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyStatusReview;
import com.example.tiamedsadmin.entity.pharmaInventory.RegistrationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
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
        // Legacy rows (null) predate the draft feature and are always submitted
        dto.setRegistrationStatus(pharmacyRegistrationDetails.getRegistrationStatus() != null
                ? pharmacyRegistrationDetails.getRegistrationStatus().name()
                : RegistrationStatus.SUBMITTED.name());

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

        // Drafts have no status reviews; label them explicitly so the UI can
        // tell them apart from submitted-but-unreviewed registrations
        if (details.getRegistrationStatus() == RegistrationStatus.DRAFT) {
            dto.setStatus(RegistrationStatus.DRAFT.toString());
            dto.setUpdatedDate(details.getUpdatedDate());
        } else if (details.getPharmacyStatusReview() != null && !details.getPharmacyStatusReview().isEmpty()) {
            PharmacyStatusReview latestReview =
                    details.getPharmacyStatusReview().stream()
                            .max(Comparator.comparing(
                                    PharmacyStatusReview::getStatusDate,
                                    Comparator.nullsFirst(Comparator.naturalOrder())
                            ))
                            .orElse(null);
            if (latestReview != null) {
                dto.setStatus(latestReview.getStatus());
                dto.setUpdatedDate(latestReview.getStatusDate());
            }
        } else {
            dto.setStatus("Pending");
        }


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

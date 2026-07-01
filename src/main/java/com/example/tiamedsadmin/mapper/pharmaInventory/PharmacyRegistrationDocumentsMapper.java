package com.example.tiamedsadmin.mapper.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDocumentsDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDocuments;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PharmacyRegistrationDocumentsMapper {

    public PharmacyRegistrationDocumentsDto toDto(PharmacyRegistrationDocuments pharmacyRegistrationDocuments) {
        if (pharmacyRegistrationDocuments == null) {
            return null;
        }

        PharmacyRegistrationDocumentsDto dto = new PharmacyRegistrationDocumentsDto();
        dto.setRegistrationDocumentId(pharmacyRegistrationDocuments.getRegistrationDocumentId());
        dto.setDocumentNumber(pharmacyRegistrationDocuments.getDocumentNumber());
        dto.setDocumentType(pharmacyRegistrationDocuments.getDocumentType());
        dto.setDocumentUrl(pharmacyRegistrationDocuments.getDocumentUrl());
        dto.setIssueDate(pharmacyRegistrationDocuments.getIssueDate());
        dto.setIssueAuthority(pharmacyRegistrationDocuments.getIssueAuthority());
        dto.setExpiryDate(pharmacyRegistrationDocuments.getExpiryDate());
        dto.setActive(pharmacyRegistrationDocuments.isActive());
        dto.setVerified(pharmacyRegistrationDocuments.isVerified());

        return dto;
    }

    public List<PharmacyRegistrationDocumentsDto> toDtoList(List<PharmacyRegistrationDocuments> pharmacyRegistrationDocuments) {
        return pharmacyRegistrationDocuments.stream()
                .map(this::toDto)
                .toList();
    }
}

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
        dto.setDocumentType(pharmacyRegistrationDocuments.getDocumentType());
        dto.setDocumentUrl(pharmacyRegistrationDocuments.getDocumentUrl());
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

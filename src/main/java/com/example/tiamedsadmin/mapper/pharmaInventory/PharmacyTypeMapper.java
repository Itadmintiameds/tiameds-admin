package com.example.tiamedsadmin.mapper.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyTypeDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PharmacyTypeMapper {

    public PharmacyTypeDto toDto(PharmacyType pharmacyType) {
        if (pharmacyType == null) {
            return null;
        }
        PharmacyTypeDto dto = new PharmacyTypeDto();
        dto.setPharmacyTypeId(pharmacyType.getPharmacyTypeId());
        dto.setPharmacyTypeName(pharmacyType.getPharmacyTypeName());
        dto.setActive(pharmacyType.isActive());
        return dto;
    }

    public List<PharmacyTypeDto> toDtoList(List<PharmacyType> pharmacyTypes) {
        return pharmacyTypes.stream()
                .map(this::toDto)
                .toList();
    }
}

package com.example.tiamedsadmin.mapper.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationWareHouseDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationWareHouse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PharmacyRegistrationWareHouseMapper {

    public PharmacyRegistrationWareHouseDto toDto(PharmacyRegistrationWareHouse pharmacyRegistrationWareHouse) {
        if (pharmacyRegistrationWareHouse == null) {
            return null;
        }

        PharmacyRegistrationWareHouseDto dto = new PharmacyRegistrationWareHouseDto();
        dto.setPharmacyRegistrationWarehouseId(pharmacyRegistrationWareHouse.getPharmacyRegistrationWarehouseId());
        dto.setWarehouseId(pharmacyRegistrationWareHouse.getWarehouseId());
        dto.setWarehouseName(pharmacyRegistrationWareHouse.getWarehouseName());
        dto.setWarehouseCode(pharmacyRegistrationWareHouse.getWarehouseCode());
        dto.setWarehouseAddress(pharmacyRegistrationWareHouse.getWarehouseAddress());
        dto.setContactPersonName(pharmacyRegistrationWareHouse.getContactPersonName());
        dto.setMobileNumber(pharmacyRegistrationWareHouse.getMobileNumber());
        dto.setActive(pharmacyRegistrationWareHouse.isActive());

        return dto;
    }

    public List<PharmacyRegistrationWareHouseDto> toDtoList(List<PharmacyRegistrationWareHouse> pharmacyRegistrationWareHouses) {
        return pharmacyRegistrationWareHouses.stream()
                .map(this::toDto)
                .toList();
    }
}

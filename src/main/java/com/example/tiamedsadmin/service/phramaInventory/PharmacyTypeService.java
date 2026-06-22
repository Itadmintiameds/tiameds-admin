package com.example.tiamedsadmin.service.phramaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyTypeDto;

import java.util.List;

public interface PharmacyTypeService {

    List<PharmacyTypeDto> findAll();

    PharmacyTypeDto findById(Long id);

    PharmacyTypeDto create(PharmacyTypeDto dto);

    PharmacyTypeDto update(Long id, PharmacyTypeDto dto);

    void delete(Long id);
}

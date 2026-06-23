package com.example.tiamedsadmin.service.phramaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;

import java.util.List;

public interface PharmacyRegistrationDetailsService {

    List<PharmacyRegistrationDetailsDto> findAll();

    PharmacyRegistrationDetailsDto findById(String id);

    PharmacyRegistrationDetailsDto create(PharmacyRegistrationDetailsDto pharmacyRegistrationDetailsDto);

    PharmacyRegistrationDetailsDto update(String id, PharmacyRegistrationDetailsDto pharmacyRegistrationDetailsDto);

    void delete(String id);

    void verifyDocument(String pharmacyRegistrationId,
                        Long registrationDocumentId,
                        boolean isVerified);
}

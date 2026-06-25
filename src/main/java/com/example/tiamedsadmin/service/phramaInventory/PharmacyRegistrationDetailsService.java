package com.example.tiamedsadmin.service.phramaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDocumentsDto;
import org.springframework.web.multipart.MultipartFile;

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

    PharmacyRegistrationDocumentsDto uploadDocument(String registrationId, Long documentId, MultipartFile file);
}

package com.example.tiamedsadmin.service.phramaInventory.phramaInventoryImpl;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDocumentsDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDocuments;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyStatusReview;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.exception.NotFoundException;
import com.example.tiamedsadmin.mapper.pharmaInventory.PharmacyRegistrationDetailsMapper;
import com.example.tiamedsadmin.repository.pharmaInventory.PharmacyRegistrationDetailsRepository;
import com.example.tiamedsadmin.service.phramaInventory.PharmacyRegistrationDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyRegistrationDetailsServiceImpl implements PharmacyRegistrationDetailsService {

    private final PharmacyRegistrationDetailsRepository pharmacyRegistrationDetailsRepository;
    private final PharmacyRegistrationDetailsMapper pharmacyRegistrationDetailsMapper;

    @Override
    public List<PharmacyRegistrationDetailsDto> findAll() {
        List<PharmacyRegistrationDetails> pharmacyRegistrationDetails = pharmacyRegistrationDetailsRepository.findAll();
        if (pharmacyRegistrationDetails.isEmpty()) {
            return List.of();
        }
        return pharmacyRegistrationDetailsMapper.toDtoList(pharmacyRegistrationDetails);
    }

    @Override
    public PharmacyRegistrationDetailsDto findById(String id) {
        PharmacyRegistrationDetails pharmacyRegistrationDetails = pharmacyRegistrationDetailsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration Details not found with id: " + id));
        return pharmacyRegistrationDetailsMapper.toDto(pharmacyRegistrationDetails);
    }

    // ─── API 1: POST - Basic details only ────────────────────────────────────────
    @Override
    @Transactional
    public PharmacyRegistrationDetailsDto create(PharmacyRegistrationDetailsDto dto) {
        // Check for duplicate email
        if (pharmacyRegistrationDetailsRepository.existsByPharmacyEmail(dto.getPharmacyEmail())) {
            throw new ApplicationException("Pharmacy with email already exists: " + dto.getPharmacyEmail());
        }

        // Set basic fields only
        PharmacyRegistrationDetails entity = new PharmacyRegistrationDetails();
        entity.setPharmacyRegistrationId(generateId()); // e.g., Req-0001, Req-0002
        entity.setPharmacyName(dto.getPharmacyName());
        entity.setPharmacyType(dto.getPharmacyType());
        entity.setPharmacyEmail(dto.getPharmacyEmail());

        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setCreatedBy("System"); // Replace with actual user if available
        entity.setUpdatedBy("System"); // Replace with actual user if available

        // Create status row and set directly to entity
        PharmacyStatusReview statusReview = new PharmacyStatusReview();
        statusReview.setPharmacy_registration_id(entity);
        statusReview.setStatus("COMPLIANCE_PENDING");
        statusReview.setRemark("Pharmacy registration initiated");
        statusReview.setStatusDate(LocalDateTime.now());
        entity.setPharmacyStatusReview(new ArrayList<>(List.of(statusReview)));

        PharmacyRegistrationDetails saved = pharmacyRegistrationDetailsRepository.save(entity);
        return pharmacyRegistrationDetailsMapper.toDto(saved);
    }

    // ─── API 2: PUT - Full details + create document rows with NOT_UPLOADED ───────
    @Override
    @Transactional
    public PharmacyRegistrationDetailsDto update(String id, PharmacyRegistrationDetailsDto dto) {

        // Check if pharmacy registration exists
        PharmacyRegistrationDetails existing = pharmacyRegistrationDetailsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration not found with id: " + id));

        // Update basic fields
        existing.setPharmacyName(dto.getPharmacyName());
        existing.setPharmacyType(dto.getPharmacyType());
        existing.setPharmacyEmail(dto.getPharmacyEmail());
        existing.setPharmacyPhone(dto.getPharmacyPhone());
        existing.setPharmacyDlNo(dto.getPharmacyDlNo());
        existing.setPharmacyDlExpiryDate(dto.getPharmacyDlExpiryDate());
        existing.setPharmacyGstNo(dto.getPharmacyGstNo());
        existing.setPharmacyPanNo(dto.getPharmacyPanNo());
        existing.setPharmacyBusinessRegistrationNo(dto.getPharmacyBusinessRegistrationNo());
        existing.setPharmacyAddress(dto.getPharmacyAddress());
        existing.setUpdatedDate(LocalDateTime.now());
        existing.setUpdatedBy("System");

        // Add SUBMITTED status to history (keeps COMPLIANCE_PENDING row intact)
        PharmacyStatusReview statusReview = new PharmacyStatusReview();
        statusReview.setPharmacy_registration_id(existing);
        statusReview.setStatus("SUBMITTED");
        statusReview.setRemark("Pharmacy registration submitted for review");
        statusReview.setStatusDate(LocalDateTime.now());
        existing.getPharmacyStatusReview().add(statusReview); // add to history, not replace

        // Create document rows with NOT_UPLOADED — documentType from frontend
        if (dto.getPharmacyRegistrationDocuments() != null) {
            List<PharmacyRegistrationDocuments> documents = dto.getPharmacyRegistrationDocuments()
                    .stream()
                    .map(docDto -> {
                        PharmacyRegistrationDocuments doc = new PharmacyRegistrationDocuments();
                        doc.setPharmacy_registration_id(existing);
                        doc.setDocumentType(docDto.getDocumentType()); // from frontend
                        doc.setDocumentUrl("NOT_UPLOADED");            // default
                        doc.setActive(true);
                        doc.setVerified(false);
                        doc.setCreatedAt(LocalDateTime.now());
                        doc.setUpdatedAt(LocalDateTime.now());
                        return doc;
                    })
                    .toList();

            existing.getPharmacyRegistrationDocuments().addAll(documents);
        }

        PharmacyRegistrationDetails saved = pharmacyRegistrationDetailsRepository.save(existing);
        return pharmacyRegistrationDetailsMapper.toDto(saved);
    }

    @Override
    public void delete(String id) {
        pharmacyRegistrationDetailsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration Details not found with id: " + id));
        pharmacyRegistrationDetailsRepository.deleteById(id);
    }

    // ─── API 4: PATCH - Verify document ──────────────────────────────────────────
    @Transactional
    @Override
    public void verifyDocument(String pharmacyRegistrationId,
                               Long registrationDocumentId,
                               boolean isVerified) {

        PharmacyRegistrationDetails existing = pharmacyRegistrationDetailsRepository.findById(pharmacyRegistrationId)
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration not found with id: " + pharmacyRegistrationId));

        // Find document by id and update verified status
        PharmacyRegistrationDocuments doc = existing.getPharmacyRegistrationDocuments()
                .stream()
                .filter(d -> d.getRegistrationDocumentId().equals(registrationDocumentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Document not found with id: " + registrationDocumentId));

        doc.setVerified(isVerified);
        doc.setUpdatedAt(LocalDateTime.now());

        pharmacyRegistrationDetailsRepository.save(existing);

        PharmacyRegistrationDocumentsDto responseDto = new PharmacyRegistrationDocumentsDto();
        responseDto.setRegistrationDocumentId(doc.getRegistrationDocumentId());
        responseDto.setDocumentType(doc.getDocumentType());
        responseDto.setDocumentUrl(doc.getDocumentUrl());
        responseDto.setActive(doc.isActive());
        responseDto.setVerified(doc.isVerified());
    }

    public String generateId() {
        int nextSequence = pharmacyRegistrationDetailsRepository.findMaxRegistrationSequence() + 1;
        return String.format("Req-%04d", nextSequence);
    }
}

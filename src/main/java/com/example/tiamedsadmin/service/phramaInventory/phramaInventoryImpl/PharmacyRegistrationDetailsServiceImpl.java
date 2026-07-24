package com.example.tiamedsadmin.service.phramaInventory.phramaInventoryImpl;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyKpiDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDocumentsDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmaciesByUserIdDto;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDocuments;
import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyStatusReview;
import com.example.tiamedsadmin.entity.pharmaInventory.RegistrationStatus;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.exception.NotFoundException;
import com.example.tiamedsadmin.mapper.pharmaInventory.PharmacyRegistrationDetailsMapper;
import com.example.tiamedsadmin.repository.pharmaInventory.PharmacyRegistrationDetailsRepository;
import com.example.tiamedsadmin.service.S3Service;
import com.example.tiamedsadmin.service.phramaInventory.PharmacyRegistrationDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PharmacyRegistrationDetailsServiceImpl implements PharmacyRegistrationDetailsService {

    private static final DateTimeFormatter TS_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final PharmacyRegistrationDetailsRepository pharmacyRegistrationDetailsRepository;
    private final PharmacyRegistrationDetailsMapper pharmacyRegistrationDetailsMapper;
    private final S3Service s3Service;

    @Override
    public List<PharmacyRegistrationDetailsDto> findAll() {
        // Drafts are user-private and must not appear in the admin review list
        List<PharmacyRegistrationDetails> pharmacyRegistrationDetails =
                pharmacyRegistrationDetailsRepository.findAllExcludingStatus(RegistrationStatus.DRAFT);
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

    // ─── API 1: POST - Full details + create document rows with NOT_UPLOADED ──────
    @Override
    @Transactional
    public PharmacyRegistrationDetailsDto create(PharmacyRegistrationDetailsDto dto) {
        // Check for duplicate email
//        if (pharmacyRegistrationDetailsRepository.existsByPharmacyEmail(dto.getPharmacyEmail())) {
//            throw new ApplicationException("Pharmacy with email already exists: " + dto.getPharmacyEmail());
//        }

        // Set all fields
        PharmacyRegistrationDetails entity = new PharmacyRegistrationDetails();
        entity.setPharmacyRegistrationId(generateId()); // e.g., Req-0001, Req-0002
        entity.setUserId(dto.getUserId());
        entity.setPharmacyName(dto.getPharmacyName());
        entity.setPharmacyType(dto.getPharmacyType());
        entity.setPharmacyEmail(dto.getPharmacyEmail());
        entity.setPharmacyPhone(dto.getPharmacyPhone());
        entity.setPanNumber(dto.getPanNumber());
        entity.setGstNumber(dto.getGstNumber());
        entity.setPharmacyBranch(dto.getPharmacyBranch());
        entity.setPharmacyBuildingNo(dto.getPharmacyBuildingNo());
        entity.setPharmacyStreet(dto.getPharmacyStreet());
        entity.setPharmacyCity(dto.getPharmacyCity());
        entity.setPharmacyTaluka(dto.getPharmacyTaluka());
        entity.setPharmacyDistricts(dto.getPharmacyDistricts());
        entity.setPharmacyPincode(dto.getPharmacyPincode());
        entity.setPharmacyLandmark(dto.getPharmacyLandmark());
        entity.setPharmacyState(dto.getPharmacyState());
        entity.setOrganizationId(dto.getOrganizationId());
        entity.setOrganizationName(dto.getOrganizationName());
        entity.setOrganizationType(dto.getOrganizationType());
        entity.setOwnershipType(dto.getOwnershipType());
        entity.setOrganizationPanNumber(dto.getOrganizationPanNumber());
        entity.setOrganizationGstNumber(dto.getOrganizationGstNumber());

        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setCreatedBy("System"); // Replace with actual user if available
        entity.setUpdatedBy("System"); // Replace with actual user if available
        entity.setRegistrationStatus(RegistrationStatus.SUBMITTED);

        // Create status row and set directly to entity
        PharmacyStatusReview statusReview = new PharmacyStatusReview();
        statusReview.setPharmacy_registration_id(entity);
        statusReview.setStatus("SUBMITTED");
        statusReview.setRemark("Pharmacy registration submitted for review");
        statusReview.setReviewedBy("System");
        statusReview.setStatusDate(LocalDateTime.now());
        entity.setPharmacyStatusReview(new ArrayList<>(List.of(statusReview)));

        // Create document rows with NOT_UPLOADED — documentType from frontend
        List<PharmacyRegistrationDocuments> documents = new ArrayList<>();
        if (dto.getPharmacyRegistrationDocuments() != null) {
            documents = dto.getPharmacyRegistrationDocuments()
                    .stream()
                    .map(docDto -> {
                        PharmacyRegistrationDocuments doc = new PharmacyRegistrationDocuments();
                        doc.setPharmacy_registration_id(entity);
                        doc.setDocumentNumber(docDto.getDocumentNumber());
                        doc.setDocumentType(docDto.getDocumentType());
                        doc.setDocumentUrl("NOT_UPLOADED");
                        doc.setIssueDate(docDto.getIssueDate());
                        doc.setIssueAuthority(docDto.getIssueAuthority());
                        doc.setExpiryDate(docDto.getExpiryDate());
                        doc.setActive(true);
                        doc.setVerified(false);
                        doc.setCreatedAt(LocalDateTime.now());
                        doc.setUpdatedAt(LocalDateTime.now());
                        return doc;
                    })
                    .toList();
        }
        entity.setPharmacyRegistrationDocuments(new ArrayList<>(documents));

        PharmacyRegistrationDetails saved = pharmacyRegistrationDetailsRepository.save(entity);
        return pharmacyRegistrationDetailsMapper.toDto(saved);
    }

    // ─── Save Draft: POST - Upsert a half-filled registration without review flow ─
    @Override
    @Transactional
    public PharmacyRegistrationDetailsDto saveDraft(PharmacyRegistrationDetailsDto dto) {
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            throw new ApplicationException("userId is required to save a draft");
        }

        PharmacyRegistrationDetails entity;
        if (dto.getPharmacyRegistrationId() != null && !dto.getPharmacyRegistrationId().isBlank()) {
            entity = pharmacyRegistrationDetailsRepository.findById(dto.getPharmacyRegistrationId())
                    .orElseThrow(() -> new NotFoundException(
                            "Pharmacy Registration not found with id: " + dto.getPharmacyRegistrationId()));
            if (entity.getRegistrationStatus() != RegistrationStatus.DRAFT) {
                throw new ApplicationException(
                        "Only a DRAFT registration can be saved as draft. Current status: "
                                + entity.getRegistrationStatus());
            }
        } else {
            // No id in the payload = new draft; a user can keep multiple drafts,
            // so the frontend must send pharmacyRegistrationId to keep updating the same one
            PharmacyRegistrationDetails draft = new PharmacyRegistrationDetails();
            draft.setPharmacyRegistrationId(generateId());
            draft.setUserId(dto.getUserId());
            draft.setRegistrationStatus(RegistrationStatus.DRAFT);
            draft.setCreatedDate(LocalDateTime.now());
            draft.setCreatedBy("System");
            // No status review row until the draft is submitted
            draft.setPharmacyStatusReview(new ArrayList<>());
            draft.setPharmacyRegistrationDocuments(new ArrayList<>());
            entity = draft;
        }

        applyDetails(entity, dto);
        syncDocuments(entity, dto);

        PharmacyRegistrationDetails saved = pharmacyRegistrationDetailsRepository.save(entity);
        return pharmacyRegistrationDetailsMapper.toDto(saved);
    }

    // ─── Submit Draft: PUT - Validate, flip DRAFT to SUBMITTED, start review flow ─
    @Override
    @Transactional
    public PharmacyRegistrationDetailsDto submitDraft(String id, PharmacyRegistrationDetailsDto dto) {
        PharmacyRegistrationDetails existing = pharmacyRegistrationDetailsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration not found with id: " + id));

        if (existing.getRegistrationStatus() != RegistrationStatus.DRAFT) {
            throw new ApplicationException(
                    "Only a DRAFT registration can be submitted. Current status: "
                            + existing.getRegistrationStatus());
        }

        // Apply the latest form values before validating completeness
        applyDetails(existing, dto);
        syncDocuments(existing, dto);
        validateReadyForSubmission(existing);

        existing.setRegistrationStatus(RegistrationStatus.SUBMITTED);

        PharmacyStatusReview statusReview = new PharmacyStatusReview();
        statusReview.setPharmacy_registration_id(existing);
        statusReview.setStatus("SUBMITTED");
        statusReview.setRemark("Pharmacy registration submitted for review");
        statusReview.setReviewedBy("System");
        statusReview.setStatusDate(LocalDateTime.now());
        existing.getPharmacyStatusReview().add(statusReview);

        PharmacyRegistrationDetails saved = pharmacyRegistrationDetailsRepository.save(existing);
        return pharmacyRegistrationDetailsMapper.toDto(saved);
    }

    private void applyDetails(PharmacyRegistrationDetails entity, PharmacyRegistrationDetailsDto dto) {
        entity.setPharmacyName(dto.getPharmacyName());
        entity.setPharmacyType(dto.getPharmacyType());
        entity.setPharmacyEmail(dto.getPharmacyEmail());
        entity.setPharmacyPhone(dto.getPharmacyPhone());
        entity.setPanNumber(dto.getPanNumber());
        entity.setGstNumber(dto.getGstNumber());
        entity.setPharmacyBranch(dto.getPharmacyBranch());
        entity.setPharmacyBuildingNo(dto.getPharmacyBuildingNo());
        entity.setPharmacyStreet(dto.getPharmacyStreet());
        entity.setPharmacyCity(dto.getPharmacyCity());
        entity.setPharmacyTaluka(dto.getPharmacyTaluka());
        entity.setPharmacyDistricts(dto.getPharmacyDistricts());
        entity.setPharmacyPincode(dto.getPharmacyPincode());
        entity.setPharmacyLandmark(dto.getPharmacyLandmark());
        entity.setPharmacyState(dto.getPharmacyState());
        entity.setOrganizationId(dto.getOrganizationId());
        entity.setOrganizationName(dto.getOrganizationName());
        entity.setOrganizationType(dto.getOrganizationType());
        entity.setOwnershipType(dto.getOwnershipType());
        entity.setOrganizationPanNumber(dto.getOrganizationPanNumber());
        entity.setOrganizationGstNumber(dto.getOrganizationGstNumber());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedBy("System");
    }

    // Update existing document rows in place; add only genuinely new ones
    private void syncDocuments(PharmacyRegistrationDetails entity, PharmacyRegistrationDetailsDto dto) {
        if (dto.getPharmacyRegistrationDocuments() == null) {
            return;
        }
        for (PharmacyRegistrationDocumentsDto docDto : dto.getPharmacyRegistrationDocuments()) {
            if (docDto.getRegistrationDocumentId() != null) {
                PharmacyRegistrationDocuments existingDoc = entity.getPharmacyRegistrationDocuments()
                        .stream()
                        .filter(d -> d.getRegistrationDocumentId().equals(docDto.getRegistrationDocumentId()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(
                                "Document not found with id: " + docDto.getRegistrationDocumentId()));

                existingDoc.setDocumentNumber(docDto.getDocumentNumber());
                existingDoc.setDocumentType(docDto.getDocumentType());
                existingDoc.setIssueDate(docDto.getIssueDate());
                existingDoc.setIssueAuthority(docDto.getIssueAuthority());
                existingDoc.setExpiryDate(docDto.getExpiryDate());
                existingDoc.setVerified(false); // updated document must be re-verified
                existingDoc.setUpdatedAt(LocalDateTime.now());
            } else {
                PharmacyRegistrationDocuments doc = new PharmacyRegistrationDocuments();
                doc.setPharmacy_registration_id(entity);
                doc.setDocumentNumber(docDto.getDocumentNumber());
                doc.setDocumentType(docDto.getDocumentType());
                doc.setDocumentUrl("NOT_UPLOADED");
                doc.setIssueDate(docDto.getIssueDate());
                doc.setIssueAuthority(docDto.getIssueAuthority());
                doc.setExpiryDate(docDto.getExpiryDate());
                doc.setActive(true);
                doc.setVerified(false);
                doc.setCreatedAt(LocalDateTime.now());
                doc.setUpdatedAt(LocalDateTime.now());
                entity.getPharmacyRegistrationDocuments().add(doc);
            }
        }
    }

    // Fields that are optional while drafting but mandatory to enter the review flow
    private void validateReadyForSubmission(PharmacyRegistrationDetails entity) {
        List<String> missing = new ArrayList<>();
        if (entity.getPharmacyName() == null || entity.getPharmacyName().isBlank()) missing.add("pharmacyName");
        if (entity.getPharmacyType() == null || entity.getPharmacyType().isBlank()) missing.add("pharmacyType");
        if (entity.getPharmacyEmail() == null || entity.getPharmacyEmail().isBlank()) missing.add("pharmacyEmail");
        if (!missing.isEmpty()) {
            throw new ApplicationException(
                    "Cannot submit registration, required fields are missing: " + String.join(", ", missing));
        }
    }

    /* Currently not using this method */
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
        existing.setPanNumber(dto.getPanNumber());
        existing.setGstNumber(dto.getGstNumber());
        existing.setPharmacyBranch(dto.getPharmacyBranch());
        existing.setPharmacyBuildingNo(dto.getPharmacyBuildingNo());
        existing.setPharmacyStreet(dto.getPharmacyStreet());
        existing.setPharmacyCity(dto.getPharmacyCity());
        existing.setPharmacyTaluka(dto.getPharmacyTaluka());
        existing.setPharmacyDistricts(dto.getPharmacyDistricts());
        existing.setPharmacyPincode(dto.getPharmacyPincode());
        existing.setPharmacyLandmark(dto.getPharmacyLandmark());
        existing.setPharmacyState(dto.getPharmacyState());
        existing.setOrganizationId(dto.getOrganizationId());
        existing.setOrganizationName(dto.getOrganizationName());
        existing.setOrganizationType(dto.getOrganizationType());
        existing.setOwnershipType(dto.getOwnershipType());
        existing.setOrganizationPanNumber(dto.getOrganizationPanNumber());
        existing.setOrganizationGstNumber(dto.getOrganizationGstNumber());
        existing.setUpdatedDate(LocalDateTime.now());
        existing.setUpdatedBy("System");

        // Update existing document rows in place; add only genuinely new ones
        if (dto.getPharmacyRegistrationDocuments() != null) {
            for (PharmacyRegistrationDocumentsDto docDto : dto.getPharmacyRegistrationDocuments()) {
                if (docDto.getRegistrationDocumentId() != null) {
                    PharmacyRegistrationDocuments existingDoc = existing.getPharmacyRegistrationDocuments()
                            .stream()
                            .filter(d -> d.getRegistrationDocumentId().equals(docDto.getRegistrationDocumentId()))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(
                                    "Document not found with id: " + docDto.getRegistrationDocumentId()));

                    existingDoc.setDocumentNumber(docDto.getDocumentNumber());
                    existingDoc.setDocumentType(docDto.getDocumentType());
                    existingDoc.setIssueDate(docDto.getIssueDate());
                    existingDoc.setIssueAuthority(docDto.getIssueAuthority());
                    existingDoc.setExpiryDate(docDto.getExpiryDate());
                    existingDoc.setVerified(false); // updated document must be re-verified
                    existingDoc.setUpdatedAt(LocalDateTime.now());
                } else {
                    // New document added as part of the update
                    PharmacyRegistrationDocuments doc = new PharmacyRegistrationDocuments();
                    doc.setPharmacy_registration_id(existing);
                    doc.setDocumentNumber(docDto.getDocumentNumber());
                    doc.setDocumentType(docDto.getDocumentType());
                    doc.setDocumentUrl("NOT_UPLOADED");
                    doc.setIssueDate(docDto.getIssueDate());
                    doc.setIssueAuthority(docDto.getIssueAuthority());
                    doc.setExpiryDate(docDto.getExpiryDate());
                    doc.setActive(true);
                    doc.setVerified(false);
                    doc.setCreatedAt(LocalDateTime.now());
                    doc.setUpdatedAt(LocalDateTime.now());
                    existing.getPharmacyRegistrationDocuments().add(doc);
                }
            }
        }

        PharmacyRegistrationDetails saved = pharmacyRegistrationDetailsRepository.save(existing);
        return pharmacyRegistrationDetailsMapper.toDto(saved);
    }

    // ─── API 3: PUT - Resubmit after admin sent for correction ────────────────────
    @Override
    @Transactional
    public PharmacyRegistrationDetailsDto resubmit(String id, PharmacyRegistrationDetailsDto dto) {

        PharmacyRegistrationDetails existing = pharmacyRegistrationDetailsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration not found with id: " + id));

        // Guard: resubmission is only allowed when the latest status is CORRECTION
        PharmacyStatusReview latest = existing.getPharmacyStatusReview().stream()
                .max(Comparator.comparing(PharmacyStatusReview::getStatusDate,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow(() -> new ApplicationException("No status history found for pharmacy: " + id));

        if (!"CORRECTION".equalsIgnoreCase(latest.getStatus())) {
            throw new ApplicationException(
                    "Pharmacy can only be resubmitted when its current status is CORRECTION. Current status: "
                            + latest.getStatus());
        }

        // Update the corrected basic fields
        existing.setPharmacyName(dto.getPharmacyName());
        existing.setPharmacyType(dto.getPharmacyType());
        existing.setPharmacyEmail(dto.getPharmacyEmail());
        existing.setPharmacyPhone(dto.getPharmacyPhone());
        existing.setPanNumber(dto.getPanNumber());
        existing.setGstNumber(dto.getGstNumber());
        existing.setPharmacyBranch(dto.getPharmacyBranch());
        existing.setPharmacyBuildingNo(dto.getPharmacyBuildingNo());
        existing.setPharmacyStreet(dto.getPharmacyStreet());
        existing.setPharmacyCity(dto.getPharmacyCity());
        existing.setPharmacyTaluka(dto.getPharmacyTaluka());
        existing.setPharmacyDistricts(dto.getPharmacyDistricts());
        existing.setPharmacyPincode(dto.getPharmacyPincode());
        existing.setPharmacyLandmark(dto.getPharmacyLandmark());
        existing.setPharmacyState(dto.getPharmacyState());
        existing.setOrganizationId(dto.getOrganizationId());
        existing.setOrganizationName(dto.getOrganizationName());
        existing.setOrganizationType(dto.getOrganizationType());
        existing.setOwnershipType(dto.getOwnershipType());
        existing.setOrganizationPanNumber(dto.getOrganizationPanNumber());
        existing.setOrganizationGstNumber(dto.getOrganizationGstNumber());
        existing.setUpdatedDate(LocalDateTime.now());
        existing.setUpdatedBy("System");

        // Update existing document rows in place; add only genuinely new ones
        if (dto.getPharmacyRegistrationDocuments() != null) {
            for (PharmacyRegistrationDocumentsDto docDto : dto.getPharmacyRegistrationDocuments()) {
                if (docDto.getRegistrationDocumentId() != null) {
                    PharmacyRegistrationDocuments existingDoc = existing.getPharmacyRegistrationDocuments()
                            .stream()
                            .filter(d -> d.getRegistrationDocumentId().equals(docDto.getRegistrationDocumentId()))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(
                                    "Document not found with id: " + docDto.getRegistrationDocumentId()));

                    existingDoc.setDocumentNumber(docDto.getDocumentNumber());
                    existingDoc.setDocumentType(docDto.getDocumentType());
                    existingDoc.setIssueDate(docDto.getIssueDate());
                    existingDoc.setIssueAuthority(docDto.getIssueAuthority());
                    existingDoc.setExpiryDate(docDto.getExpiryDate());
                    existingDoc.setVerified(false); // corrected document must be re-verified
                    existingDoc.setUpdatedAt(LocalDateTime.now());
                } else {
                    // New document added as part of the correction
                    PharmacyRegistrationDocuments doc = new PharmacyRegistrationDocuments();
                    doc.setPharmacy_registration_id(existing);
                    doc.setDocumentNumber(docDto.getDocumentNumber());
                    doc.setDocumentType(docDto.getDocumentType());
                    doc.setDocumentUrl("NOT_UPLOADED");
                    doc.setIssueDate(docDto.getIssueDate());
                    doc.setIssueAuthority(docDto.getIssueAuthority());
                    doc.setExpiryDate(docDto.getExpiryDate());
                    doc.setActive(true);
                    doc.setVerified(false);
                    doc.setCreatedAt(LocalDateTime.now());
                    doc.setUpdatedAt(LocalDateTime.now());
                    existing.getPharmacyRegistrationDocuments().add(doc);
                }
            }
        }

        // Add RESUBMITTED status to history (keeps CORRECTION row intact)
        PharmacyStatusReview statusReview = new PharmacyStatusReview();
        statusReview.setPharmacy_registration_id(existing);
        statusReview.setStatus("RESUBMITTED");
        statusReview.setRemark("Pharmacy registration resubmitted after correction");
        statusReview.setReviewedBy("user");
        statusReview.setStatusDate(LocalDateTime.now());
        existing.getPharmacyStatusReview().add(statusReview);

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

    // ─── API 5: POST - Upload document ──────────────────────────────────────────
    @Transactional
    @Override
    public PharmacyRegistrationDocumentsDto uploadDocument(String registrationId, Long documentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApplicationException("File is missing or empty");
        }

        PharmacyRegistrationDetails existing = pharmacyRegistrationDetailsRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("Pharmacy Registration not found with id: " + registrationId));

        PharmacyRegistrationDocuments doc = existing.getPharmacyRegistrationDocuments()
                .stream()
                .filter(d -> d.getRegistrationDocumentId().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Document not found with id: " + documentId));

        deleteIfRealUrl(doc.getDocumentUrl());

        String now = LocalDateTime.now().format(TS_FORMATTER);
        String safeType = sanitizeDocumentType(doc.getDocumentType());

        String key = String.format("pharma-registration/%s/documents/%s-%s.%s",
                registrationId, safeType, now, extension(file));

        try {
            String url = s3Service.uploadFile(key, file);
            doc.setDocumentUrl(url);
            doc.setUpdatedAt(LocalDateTime.now());
            pharmacyRegistrationDetailsRepository.save(existing);

            PharmacyRegistrationDocumentsDto responseDto = new PharmacyRegistrationDocumentsDto();
            responseDto.setRegistrationDocumentId(doc.getRegistrationDocumentId());
            responseDto.setDocumentType(doc.getDocumentType());
            responseDto.setDocumentUrl(doc.getDocumentUrl());
            responseDto.setActive(doc.isActive());
            responseDto.setVerified(doc.isVerified());
            return responseDto;
        } catch (IOException e) {
            throw new ApplicationException("Failed to upload file to S3: " + e.getMessage());
        }
    }

    // ─── API 6: GET - Dashboard KPIs for a user ──────────────────────────────────
    @Override
    public PharmacyKpiDto getKpis(String userId) {
        PharmacyKpiDto kpi = new PharmacyKpiDto();
        // Drafts are not yet real registrations; count them separately
        kpi.setTotalPharmacies(
                pharmacyRegistrationDetailsRepository.countByUserIdExcludingStatus(userId, RegistrationStatus.DRAFT));
        kpi.setDrafts(
                pharmacyRegistrationDetailsRepository.countByUserIdAndRegistrationStatus(userId, RegistrationStatus.DRAFT));

        for (Object[] row : pharmacyRegistrationDetailsRepository.countByLatestStatusForUser(userId)) {
            String status = (String) row[0];
            long count = ((Number) row[1]).longValue();

            switch (status.toUpperCase()) {
                case "ACCEPT" -> kpi.setApproved(kpi.getApproved() + count);
                case "SUBMITTED", "RESUBMITTED" -> kpi.setUnderReview(kpi.getUnderReview() + count);
                case "CORRECTION" -> kpi.setActionRequired(kpi.getActionRequired() + count);
                case "REJECT" -> kpi.setRejected(kpi.getRejected() + count);
            }
        }
        return kpi;
    }
 
    @Override
    public List<PharmaciesByUserIdDto> findPharmaciesByUserId(String userId) {
        // Admin-facing list: drafts are not yet submitted, so they are excluded
        List<PharmacyRegistrationDetails> details =
                pharmacyRegistrationDetailsRepository.findByUserIdExcludingStatus(userId, RegistrationStatus.DRAFT);
        return pharmacyRegistrationDetailsMapper.toPharmaciesByUserIdDtoList(details);
    }

    // ─── Get Drafts: GET - Fetch the user's drafts so the form can be pre-filled ─
    @Override
    public List<PharmacyRegistrationDetailsDto> getDraftsByUserId(String userId) {
        List<PharmacyRegistrationDetails> drafts = pharmacyRegistrationDetailsRepository
                .findByUserIdAndRegistrationStatus(userId, RegistrationStatus.DRAFT);
        return pharmacyRegistrationDetailsMapper.toDtoList(drafts);
    }

    private String sanitizeDocumentType(String type) {
        if (type == null) return "UNKNOWN_DOC";
        return type.trim().toUpperCase()
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }

    private String extension(MultipartFile file) {
        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        int dot = original.lastIndexOf('.');
        return (dot >= 0 && dot < original.length() - 1)
                ? original.substring(dot + 1).toLowerCase()
                : "bin";
    }

    private void deleteIfRealUrl(String url) {
        if (url == null || url.isBlank()) return;
        if (!url.startsWith("https://")) return;
        try {
            s3Service.deleteFile(s3Service.extractKeyFromUrl(url));
        } catch (Exception e) {
            // Ignored as per reference
        }
    }
}

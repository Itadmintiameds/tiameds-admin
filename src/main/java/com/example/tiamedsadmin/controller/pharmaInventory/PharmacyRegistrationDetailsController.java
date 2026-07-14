package com.example.tiamedsadmin.controller.pharmaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyDocumentVerifyDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyKpiDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDetailsDto;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyRegistrationDocumentsDto;
import com.example.tiamedsadmin.service.phramaInventory.PharmacyRegistrationDetailsService;
import com.example.tiamedsadmin.utility.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pharmacy-registration")
@RequiredArgsConstructor
public class PharmacyRegistrationDetailsController {

    private final PharmacyRegistrationDetailsService pharmacyRegistrationDetailsService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<PharmacyRegistrationDetailsDto> list = pharmacyRegistrationDetailsService.findAll();
        ApiResponse<List<PharmacyRegistrationDetailsDto>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Pharmacy registration details fetched successfully",
                list,
                list.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/kpis/{userId}")
    public ResponseEntity<?> getKpis(@PathVariable String userId) {
        PharmacyKpiDto kpis = pharmacyRegistrationDetailsService.getKpis(userId);
        ApiResponse<PharmacyKpiDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "Pharmacy KPIs fetched successfully",
                kpis);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        PharmacyRegistrationDetailsDto dto = pharmacyRegistrationDetailsService.findById(id);
        ApiResponse<PharmacyRegistrationDetailsDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "Pharmacy registration details fetched successfully",
                dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PharmacyRegistrationDetailsDto dto) {
        PharmacyRegistrationDetailsDto createdDto = pharmacyRegistrationDetailsService.create(dto);
        ApiResponse<PharmacyRegistrationDetailsDto> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Pharmacy registration details created successfully",
                createdDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody PharmacyRegistrationDetailsDto dto) {
        PharmacyRegistrationDetailsDto updatedDto = pharmacyRegistrationDetailsService.update(id, dto);
        ApiResponse<PharmacyRegistrationDetailsDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "Pharmacy registration details updated successfully",
                updatedDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/resubmit")
    public ResponseEntity<?> resubmit(
            @PathVariable String id,
            @RequestBody PharmacyRegistrationDetailsDto dto) {
        PharmacyRegistrationDetailsDto resubmittedDto = pharmacyRegistrationDetailsService.resubmit(id, dto);
        ApiResponse<PharmacyRegistrationDetailsDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "Pharmacy registration resubmitted successfully",
                resubmittedDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        pharmacyRegistrationDetailsService.delete(id);
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "Pharmacy registration details deleted successfully",
                null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/verify-document")
    public ResponseEntity<?> verifyDocument(@RequestBody PharmacyDocumentVerifyDto dto) {
        pharmacyRegistrationDetailsService.verifyDocument(
                dto.getPharmacyRegistrationId(),
                dto.getRegistrationDocumentId(),
                dto.isVerified());
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "Document verified successfully",
                null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/documents/{documentId}/upload")
    public ResponseEntity<?> uploadDocument(
            @PathVariable String id,
            @PathVariable Long documentId,
            @RequestParam("file") MultipartFile file) {
        PharmacyRegistrationDocumentsDto dto = pharmacyRegistrationDetailsService.uploadDocument(id, documentId, file);
        ApiResponse<PharmacyRegistrationDocumentsDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "Document uploaded successfully",
                dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

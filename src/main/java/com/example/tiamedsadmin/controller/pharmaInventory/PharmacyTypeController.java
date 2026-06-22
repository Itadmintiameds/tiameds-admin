package com.example.tiamedsadmin.controller.pharmaInventory;

import com.example.tiamedsadmin.dto.PharmacyTypeDto;
import com.example.tiamedsadmin.service.phramaInventory.PharmacyTypeService;
import com.example.tiamedsadmin.utility.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacyType")
@RequiredArgsConstructor
public class PharmacyTypeController {

    private final PharmacyTypeService pharmacyTypeService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<PharmacyTypeDto> pharmacyTypeDtos = pharmacyTypeService.findAll();
        ApiResponse<List<PharmacyTypeDto>> listApiResponse = new ApiResponse<>(
                HttpStatus.OK, "Request successful", pharmacyTypeDtos, pharmacyTypeDtos.size()
        );
        return new ResponseEntity<>(listApiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        PharmacyTypeDto pharmacyTypeDto = pharmacyTypeService.findById(id);
        ApiResponse<PharmacyTypeDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK, "Request successful", pharmacyTypeDto
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}

package com.example.tiamedsadmin.controller.pharmaInventory;

import com.example.tiamedsadmin.service.phramaInventory.PharmacyAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyStatusReviewDto;
import com.example.tiamedsadmin.utility.responses.ApiResponse;

@RestController
@RequestMapping("/admin/pharmacy")
@RequiredArgsConstructor
public class PharmacyAdminController {

private final PharmacyAdminService pharmacyAdminService;

	@PostMapping("/review")
	public ResponseEntity<?> reviewPharmacy(
			@RequestBody PharmacyStatusReviewDto dto) {

		pharmacyAdminService.reviewPharmacy(dto);

		ApiResponse<Void> response = new ApiResponse<>(
				HttpStatus.OK,
				"Pharmacy review processed successfully",
				null);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

package com.example.tiamedsadmin.service.phramaInventory;

import com.example.tiamedsadmin.dto.pharmaInventory.PharmacyStatusReviewDto;

public interface PharmacyAdminService {

	/**
	 * Process an admin review action for a pharmacy registration.
	 * @param dto the review payload containing status, remarks and optional date
	 */
	void reviewPharmacy(PharmacyStatusReviewDto dto);

}

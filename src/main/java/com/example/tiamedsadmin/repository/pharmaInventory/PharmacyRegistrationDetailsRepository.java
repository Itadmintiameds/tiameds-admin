package com.example.tiamedsadmin.repository.pharmaInventory;

import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PharmacyRegistrationDetailsRepository extends JpaRepository<PharmacyRegistrationDetails, String> {

    // Check if pharmacy email already exists
    boolean existsByPharmacyEmail(String pharmacyEmail);

    // Get the highest sequence number from pharmacy_registration_id (e.g., Req-0005 → returns 5)
    // Returns 0 if no records exist
    @Query(value = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(pharmacy_registration_id FROM 5) AS INTEGER)), 0)
            FROM pharmacy_registration_details
            """, nativeQuery = true)
    Integer findMaxRegistrationSequence();
}

package com.example.tiamedsadmin.repository.pharmaInventory;

import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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

    long countByUserId(String userId);

    // Count a user's registrations grouped by their latest status (one row per status, e.g. [ACCEPT, 4])
    @Query(value = """
            SELECT latest.status, COUNT(*)
            FROM (
                SELECT DISTINCT ON (psr.pharmacy_registration_id) psr.status
                FROM pharmacy_status_review psr
                JOIN pharmacy_registration_details prd
                    ON prd.pharmacy_registration_id = psr.pharmacy_registration_id
                WHERE prd.user_id = :userId
                ORDER BY psr.pharmacy_registration_id, psr.status_date DESC, psr.status_id DESC
            ) latest
            GROUP BY latest.status
            """, nativeQuery = true)
    List<Object[]> countByLatestStatusForUser(String userId);
}

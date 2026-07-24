package com.example.tiamedsadmin.repository.pharmaInventory;

import com.example.tiamedsadmin.entity.pharmaInventory.PharmacyRegistrationDetails;
import com.example.tiamedsadmin.entity.pharmaInventory.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<PharmacyRegistrationDetails> findByUserId(String userId);

    // All drafts of a user (a user can have multiple drafts)
    List<PharmacyRegistrationDetails> findByUserIdAndRegistrationStatus(String userId, RegistrationStatus registrationStatus);

    long countByUserIdAndRegistrationStatus(String userId, RegistrationStatus registrationStatus);

    // All non-draft registrations (null = legacy rows created before registration_status existed)
    @Query("""
            SELECT p FROM PharmacyRegistrationDetails p
            WHERE p.registrationStatus IS NULL OR p.registrationStatus <> :status
            """)
    List<PharmacyRegistrationDetails> findAllExcludingStatus(@Param("status") RegistrationStatus status);

    @Query("""
            SELECT COUNT(p) FROM PharmacyRegistrationDetails p
            WHERE p.userId = :userId
            AND (p.registrationStatus IS NULL OR p.registrationStatus <> :status)
            """)
    long countByUserIdExcludingStatus(@Param("userId") String userId, @Param("status") RegistrationStatus status);

    @Query("""
            SELECT p FROM PharmacyRegistrationDetails p
            WHERE p.userId = :userId
            AND (p.registrationStatus IS NULL OR p.registrationStatus <> :status)
            """)
    List<PharmacyRegistrationDetails> findByUserIdExcludingStatus(@Param("userId") String userId, @Param("status") RegistrationStatus status);

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

package com.example.tiamedsadmin.entity.pharmaInventory;

/**
 * Lifecycle stage of a pharmacy registration row itself.
 * DRAFT      - saved by the user but not yet submitted for review (no status review rows yet)
 * SUBMITTED  - handed over to the admin review flow; from here the current state
 *              is tracked in pharmacy_status_review (SUBMITTED/CORRECTION/RESUBMITTED/ACCEPT/REJECT)
 */
public enum RegistrationStatus {
    DRAFT,
    SUBMITTED
}

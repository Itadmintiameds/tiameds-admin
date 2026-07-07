package com.example.tiamedsadmin.dto.pharmaInventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Response of the inventory service's pharmacy create API.
 * Only the fields needed by the admin server are mapped; the rest are ignored.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryPharmacyResponseDto {

    private String pharmacyId;
    private String pharmacyRegistrationId;
}

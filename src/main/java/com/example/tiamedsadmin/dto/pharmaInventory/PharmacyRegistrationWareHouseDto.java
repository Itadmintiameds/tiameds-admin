package com.example.tiamedsadmin.dto.pharmaInventory;

import lombok.Data;

@Data
public class PharmacyRegistrationWareHouseDto {

    private Long pharmacyRegistrationWarehouseId;
    private String warehouseName;
    private String warehouseCode;
    private String warehouseAddress;
    private String contactPersonName;
    private String mobileNumber;
    private boolean isActive;
}

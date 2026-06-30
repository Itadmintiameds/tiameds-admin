package com.example.tiamedsadmin.dto.module;

import lombok.Data;

@Data
public class OtpRequestDto {

    private String recipient;       // email address or phone number
    private String deliveryType;    // EMAIL, SMS, WHATSAPP
    private String moduleName;      // e.g. INVENTORY, LAB
}
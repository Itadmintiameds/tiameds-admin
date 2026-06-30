package com.example.tiamedsadmin.dto.module;

import lombok.Data;

@Data
public class OtpVerifyDto {

    private String recipient;    // email or phone used when OTP was sent
    private String otp;
    private String moduleName;
}
package com.example.tiamedsadmin.service.module;

import com.example.tiamedsadmin.dto.module.OtpRequestDto;
import com.example.tiamedsadmin.dto.module.OtpResponseDto;
import com.example.tiamedsadmin.dto.module.OtpVerifyDto;

public interface OtpService {

    OtpResponseDto sendOtp(OtpRequestDto dto);

    void verifyOtp(OtpVerifyDto dto);
}
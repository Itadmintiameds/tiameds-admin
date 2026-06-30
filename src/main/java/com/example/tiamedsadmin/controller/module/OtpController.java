package com.example.tiamedsadmin.controller.module;

import com.example.tiamedsadmin.dto.module.OtpRequestDto;
import com.example.tiamedsadmin.dto.module.OtpResponseDto;
import com.example.tiamedsadmin.dto.module.OtpVerifyDto;
import com.example.tiamedsadmin.service.module.OtpService;
import com.example.tiamedsadmin.utility.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequestDto dto) {
        OtpResponseDto result = otpService.sendOtp(dto);
        ApiResponse<OtpResponseDto> response = new ApiResponse<>(
                HttpStatus.OK, result.getMessage(), result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyDto dto) {
        otpService.verifyOtp(dto);
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK, "OTP verified successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

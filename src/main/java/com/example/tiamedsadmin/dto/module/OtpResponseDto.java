package com.example.tiamedsadmin.dto.module;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OtpResponseDto {

    private String message;
    private LocalDateTime expiryAt;
}
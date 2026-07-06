package com.example.tiamedsadmin.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    // Delivered only via HttpOnly cookies, never in the JSON body
    @JsonIgnore
    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    private String tokenType;       // "Bearer"
    private long expiresInSeconds;  // access token validity
    private UserResponseDto user;
}

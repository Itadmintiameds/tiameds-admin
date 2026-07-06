package com.example.tiamedsadmin.service.auth;

import com.example.tiamedsadmin.dto.auth.LoginRequestDto;
import com.example.tiamedsadmin.dto.auth.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);

    LoginResponseDto refresh(String refreshToken);

    void logout(String refreshToken);
}

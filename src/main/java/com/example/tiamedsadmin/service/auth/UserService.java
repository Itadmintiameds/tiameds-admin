package com.example.tiamedsadmin.service.auth;

import com.example.tiamedsadmin.dto.auth.UserRequestDto;
import com.example.tiamedsadmin.dto.auth.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto request);

    List<UserResponseDto> getAllUsers();
}

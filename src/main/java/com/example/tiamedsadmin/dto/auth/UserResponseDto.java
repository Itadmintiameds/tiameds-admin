package com.example.tiamedsadmin.dto.auth;

import com.example.tiamedsadmin.entity.auth.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {

    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

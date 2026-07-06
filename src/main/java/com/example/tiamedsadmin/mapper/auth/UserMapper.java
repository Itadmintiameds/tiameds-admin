package com.example.tiamedsadmin.mapper.auth;

import com.example.tiamedsadmin.dto.auth.UserResponseDto;
import com.example.tiamedsadmin.entity.auth.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public List<UserResponseDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }
}

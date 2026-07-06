package com.example.tiamedsadmin.controller.auth;

import com.example.tiamedsadmin.dto.auth.UserRequestDto;
import com.example.tiamedsadmin.dto.auth.UserResponseDto;
import com.example.tiamedsadmin.service.auth.UserService;
import com.example.tiamedsadmin.utility.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto result = userService.createUser(dto);
        ApiResponse<UserResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED, "User created successfully", result);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserResponseDto> result = userService.getAllUsers();
        ApiResponse<List<UserResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK, "Users fetched successfully", result, result.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

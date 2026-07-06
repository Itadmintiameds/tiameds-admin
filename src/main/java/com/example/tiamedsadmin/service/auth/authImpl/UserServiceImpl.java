package com.example.tiamedsadmin.service.auth.authImpl;

import com.example.tiamedsadmin.dto.auth.UserRequestDto;
import com.example.tiamedsadmin.dto.auth.UserResponseDto;
import com.example.tiamedsadmin.entity.auth.User;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.mapper.auth.UserMapper;
import com.example.tiamedsadmin.repository.auth.UserRepository;
import com.example.tiamedsadmin.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(HttpStatus.CONFLICT, "A user with this email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setActive(true);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }
}

package com.example.tiamedsadmin.service.auth.authImpl;

import com.example.tiamedsadmin.dto.auth.LoginRequestDto;
import com.example.tiamedsadmin.dto.auth.LoginResponseDto;
import com.example.tiamedsadmin.entity.auth.RefreshToken;
import com.example.tiamedsadmin.entity.auth.User;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.mapper.auth.UserMapper;
import com.example.tiamedsadmin.repository.auth.RefreshTokenRepository;
import com.example.tiamedsadmin.repository.auth.UserRepository;
import com.example.tiamedsadmin.security.JwtService;
import com.example.tiamedsadmin.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Value("${security.jwt.refresh-token-validity-days}")
    private long refreshTokenValidityDays;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                // Same message for unknown email and wrong password, to avoid user enumeration
                .orElseThrow(() -> new ApplicationException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (!user.isActive()) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "User account is deactivated");
        }

        RefreshToken refreshToken = issueRefreshToken(user);
        return buildLoginResponse(user, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponseDto refresh(String refreshToken) {
        RefreshToken existing = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApplicationException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (existing.isRevoked()) {
            // Possible token theft: a rotated-out token is being reused.
            // Revoke everything for this user to force a fresh login.
            refreshTokenRepository.revokeAllByUser(existing.getUser(), LocalDateTime.now());
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Refresh token has been revoked");
        }

        if (existing.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        }

        User user = existing.getUser();
        if (!user.isActive()) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "User account is deactivated");
        }

        // Rotation: revoke the used token and issue a new one
        existing.setRevoked(true);
        existing.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(existing);

        RefreshToken newRefreshToken = issueRefreshToken(user);
        return buildLoginResponse(user, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        // Idempotent: logging out with an unknown/already-revoked token is not an error
        refreshTokenRepository.findByToken(refreshToken)
                .filter(token -> !token.isRevoked())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
    }

    private RefreshToken issueRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setIssuedAt(LocalDateTime.now());
        refreshToken.setExpiryAt(LocalDateTime.now().plusDays(refreshTokenValidityDays));
        return refreshTokenRepository.save(refreshToken);
    }

    private LoginResponseDto buildLoginResponse(User user, RefreshToken refreshToken) {
        return LoginResponseDto.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresInSeconds(jwtService.getAccessTokenValiditySeconds())
                .user(userMapper.toDto(user))
                .build();
    }
}

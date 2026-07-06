package com.example.tiamedsadmin.controller.auth;

import com.example.tiamedsadmin.dto.auth.LoginRequestDto;
import com.example.tiamedsadmin.dto.auth.LoginResponseDto;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.service.auth.AuthService;
import com.example.tiamedsadmin.utility.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE = "refreshToken";
    private static final String ACCESS_COOKIE = "accessToken";

    private final AuthService authService;

    @Value("${security.jwt.access-token-validity-minutes}")
    private long accessTokenValidityMinutes;

    @Value("${security.jwt.refresh-token-validity-days}")
    private long refreshTokenValidityDays;

    @Value("${security.jwt.refresh-cookie.secure}")
    private boolean cookieSecure;

    @Value("${security.jwt.refresh-cookie.same-site}")
    private String cookieSameSite;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto dto,
                                   HttpServletRequest request) {
        LoginResponseDto result = authService.login(dto);

        ResponseCookie refreshCookie = buildRefreshCookie(
                result.getRefreshToken(), request, Duration.ofDays(refreshTokenValidityDays));
        ResponseCookie accessCookie = buildAccessCookie(
                result.getAccessToken(), request, Duration.ofMinutes(accessTokenValidityMinutes));

        ApiResponse<LoginResponseDto> response = new ApiResponse<>(
                HttpStatus.OK, "Login successful", result);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
                                     HttpServletRequest request) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Refresh token cookie is missing");
        }

        LoginResponseDto result = authService.refresh(refreshToken);

        ResponseCookie refreshCookie = buildRefreshCookie(
                result.getRefreshToken(), request, Duration.ofDays(refreshTokenValidityDays));
        ResponseCookie accessCookie = buildAccessCookie(
                result.getAccessToken(), request, Duration.ofMinutes(accessTokenValidityMinutes));

        ApiResponse<LoginResponseDto> response = new ApiResponse<>(
                HttpStatus.OK, "Token refreshed successfully", result);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
                                    HttpServletRequest request) {
        authService.logout(refreshToken);

        // Expire both cookies in the browser
        ResponseCookie refreshCookie = buildRefreshCookie("", request, Duration.ZERO);
        ResponseCookie accessCookie = buildAccessCookie("", request, Duration.ZERO);

        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK, "Logged out successfully", null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(response);
    }

    private ResponseCookie buildRefreshCookie(String value, HttpServletRequest request, Duration maxAge) {
        // Only sent to /api/v1/auth/* — never rides along on other API calls
        return buildCookie(REFRESH_COOKIE, value, request.getContextPath() + "/auth", maxAge);
    }

    private ResponseCookie buildAccessCookie(String value, HttpServletRequest request, Duration maxAge) {
        // Sent on every API call so JwtAuthFilter can authenticate from it
        String contextPath = request.getContextPath();
        return buildCookie(ACCESS_COOKIE, value, contextPath.isEmpty() ? "/" : contextPath, maxAge);
    }

    private ResponseCookie buildCookie(String name, String value, String path, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .maxAge(maxAge)
                .sameSite(cookieSameSite)
                .build();
    }
}

package com.example.simplifyStorePrime.controller;

import com.example.simplifyStorePrime.commons.AppConstants;
import com.example.simplifyStorePrime.dto.AuthenticationRequest;
import com.example.simplifyStorePrime.dto.RegisterRequest;
import com.example.simplifyStorePrime.service.AuthenticationService;
import com.example.simplifyStorePrime.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final RateLimiterService rateLimiterService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        if (!rateLimiterService.resolveRegisterBucket(getClientIP(httpRequest)).tryConsume(1)) {
            return tooManyRequests(AppConstants.TOO_MANY_REGISTRATIONS);
        }
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpRequest) {
        if (!rateLimiterService.resolveLoginBucket(getClientIP(httpRequest)).tryConsume(1)) {
            return tooManyRequests(AppConstants.TOO_MANY_LOGINS);
        }
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        if (!rateLimiterService.resolveForgotPasswordBucket(getClientIP(httpRequest)).tryConsume(1)) {
            return tooManyRequests(AppConstants.TOO_MANY_FORGOT_PASSWORD);
        }

        String email = request.get(AppConstants.REQUEST_EMAIL);
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(AppConstants.RESPONSE_MESSAGE, AppConstants.EMAIL_REQUIRED));
        }
        try {
            service.forgotPassword(email);
        } catch (Exception ignored) {
        }
        return ResponseEntity.ok(
                Map.of(AppConstants.RESPONSE_MESSAGE, AppConstants.PASSWORD_RESET_EMAIL_SENT));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        if (!rateLimiterService.resolveResetPasswordBucket(getClientIP(httpRequest)).tryConsume(1)) {
            return tooManyRequests(AppConstants.TOO_MANY_RESET_PASSWORD);
        }

        String token = request.get(AppConstants.REQUEST_TOKEN);
        String newPassword = request.get(AppConstants.REQUEST_NEW_PASSWORD);

        if (token == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of(AppConstants.RESPONSE_MESSAGE, AppConstants.TOKEN_PASSWORD_REQUIRED));
        }

        try {
            service.resetPassword(token, newPassword);
            return ResponseEntity.ok(
                    Map.of(AppConstants.RESPONSE_MESSAGE, AppConstants.PASSWORD_RESET_SUCCESS));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(AppConstants.RESPONSE_MESSAGE, e.getMessage()));
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(AppConstants.X_FORWARDED_FOR);
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private ResponseEntity<Map<String, String>> tooManyRequests(String message) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(AppConstants.RESPONSE_MESSAGE, message));
    }
}
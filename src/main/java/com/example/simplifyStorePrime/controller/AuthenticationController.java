package com.example.simplifyStorePrime.controller;

import com.example.simplifyStorePrime.dto.AuthenticationRequest;
import com.example.simplifyStorePrime.dto.AuthenticationResponse;
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
            return tooManyRequests("Too many registration attempts. Please try again later.");
        }
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpRequest) {
        if (!rateLimiterService.resolveLoginBucket(getClientIP(httpRequest)).tryConsume(1)) {
            return tooManyRequests("Too many login attempts. Please try again in a minute.");
        }
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        if (!rateLimiterService.resolveForgotPasswordBucket(getClientIP(httpRequest)).tryConsume(1)) {
            return tooManyRequests("Too many reset requests. Please try again later.");
        }

        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email is required"));
        }
        try {
            service.forgotPassword(email);
        } catch (Exception ignored) {
        }
        return ResponseEntity.ok(
                Map.of("message", "If an account with that email exists, a reset link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        if (!rateLimiterService.resolveResetPasswordBucket(getClientIP(httpRequest)).tryConsume(1)) {
            return tooManyRequests("Too many reset attempts. Please try again in a minute.");
        }

        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Token and password (min 6 chars) are required"));
        }

        try {
            service.resetPassword(token, newPassword);
            return ResponseEntity.ok(
                    Map.of("message", "Password has been reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private ResponseEntity<Map<String, String>> tooManyRequests(String message) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message", message));
    }
}
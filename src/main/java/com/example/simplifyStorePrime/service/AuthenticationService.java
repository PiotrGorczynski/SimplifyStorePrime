package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.commons.AppConstants;
import com.example.simplifyStorePrime.dto.RegisterRequest;
import com.example.simplifyStorePrime.dto.AuthenticationResponse;
import com.example.simplifyStorePrime.dto.AuthenticationRequest;
import com.example.simplifyStorePrime.entity.AppUser;
import com.example.simplifyStorePrime.entity.PasswordResetToken;
import com.example.simplifyStorePrime.repository.AppUserRepository;
import com.example.simplifyStorePrime.repository.PasswordResetTokenRepository;
import com.example.simplifyStorePrime.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository repository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(AppConstants.USERNAME_ALREADY_EXISTS + request.getUsername());
        }

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : AppConstants.DEFAULT_ROLE)
                .enabled(true)
                .build();

        AppUser saved = repository.save(user);

        UserDetails userDetails = User.withUsername(saved.getUsername())
                .password(saved.getPassword())
                .roles(saved.getRole())
                .build();

        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        AppUser user = repository.findByUsername(request.getUsername()).orElseThrow();

        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        String jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Transactional
    public void forgotPassword(String email) {
        AppUser user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(AppConstants.USER_NOT_FOUND));

        tokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException(AppConstants.INVALID_RESET_TOKEN));

        if (resetToken.isExpired()) {
            throw new RuntimeException(AppConstants.RESET_TOKEN_EXPIRED);
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException(AppConstants.RESET_TOKEN_USED);
        }

        AppUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
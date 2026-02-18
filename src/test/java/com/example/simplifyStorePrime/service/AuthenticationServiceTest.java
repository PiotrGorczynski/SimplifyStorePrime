package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.AuthenticationRequest;
import com.example.simplifyStorePrime.dto.AuthenticationResponse;
import com.example.simplifyStorePrime.dto.RegisterRequest;
import com.example.simplifyStorePrime.entity.AppUser;
import com.example.simplifyStorePrime.entity.PasswordResetToken;
import com.example.simplifyStorePrime.repository.AppUserRepository;
import com.example.simplifyStorePrime.repository.PasswordResetTokenRepository;
import com.example.simplifyStorePrime.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void register_newUser_shouldReturnToken() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@test.com")
                .password("password123")
                .build();

        AppUser savedUser = AppUser.builder()
                .id(1)
                .username("testuser")
                .email("test@test.com")
                .password("encodedPassword")
                .role("USER")
                .enabled(true)
                .build();

        when(repository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(repository.save(any(AppUser.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token-123");

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        verify(repository).save(any(AppUser.class));
    }

    @Test
    void register_existingUsername_shouldThrowException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("existinguser")
                .email("test@test.com")
                .password("password123")
                .build();

        when(repository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> authenticationService.register(request));
        verify(repository, never()).save(any());
    }

    @Test
    void authenticate_validCredentials_shouldReturnToken() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        AppUser user = AppUser.builder()
                .username("testuser")
                .password("encodedPassword")
                .role("USER")
                .build();

        when(repository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("jwt-token-456");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("jwt-token-456", response.getToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void forgotPassword_existingEmail_shouldSendEmail() {
        AppUser user = AppUser.builder()
                .id(1)
                .email("test@test.com")
                .build();

        when(repository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        authenticationService.forgotPassword("test@test.com");

        verify(tokenRepository).deleteByUserId(1);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("test@test.com"), anyString());
    }

    @Test
    void forgotPassword_nonExistingEmail_shouldThrowException() {
        when(repository.findByEmail("fake@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authenticationService.forgotPassword("fake@test.com"));
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void resetPassword_validToken_shouldUpdatePassword() {
        AppUser user = AppUser.builder()
                .id(1)
                .username("testuser")
                .password("oldPassword")
                .build();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("valid-token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        authenticationService.resetPassword("valid-token", "newPassword");

        verify(repository).save(user);
        verify(tokenRepository).save(resetToken);
        assertEquals("encodedNewPassword", user.getPassword());
        assertTrue(resetToken.isUsed());
    }

    @Test
    void resetPassword_expiredToken_shouldThrowException() {
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("expired-token")
                .expiryDate(LocalDateTime.now().minusMinutes(5))
                .used(false)
                .build();

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(resetToken));

        assertThrows(RuntimeException.class,
                () -> authenticationService.resetPassword("expired-token", "newPassword"));
    }

    @Test
    void resetPassword_usedToken_shouldThrowException() {
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("used-token")
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(true)
                .build();

        when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(resetToken));

        assertThrows(RuntimeException.class,
                () -> authenticationService.resetPassword("used-token", "newPassword"));
    }
}
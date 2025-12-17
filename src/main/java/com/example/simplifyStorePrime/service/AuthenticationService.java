package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.RegisterRequest;
import com.example.simplifyStorePrime.dto.AuthenticationResponse;
import com.example.simplifyStorePrime.dto.AuthenticationRequest;
import com.example.simplifyStorePrime.entity.AppUser;
import com.example.simplifyStorePrime.repository.AppUserRepository;
import com.example.simplifyStorePrime.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        repository.save(user);

        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
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
}

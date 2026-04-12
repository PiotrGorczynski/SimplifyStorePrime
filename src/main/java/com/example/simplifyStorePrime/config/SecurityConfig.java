package com.example.simplifyStorePrime.config;

import com.example.simplifyStorePrime.commons.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AppConstants.AUTH_ENDPOINTS).permitAll()
                        .requestMatchers(AppConstants.SWAGGER_UI_PATTERN, AppConstants.API_DOCS_PATTERN, AppConstants.SWAGGER_HTML).permitAll()
                        .requestMatchers(HttpMethod.GET, AppConstants.ALL_API_PATTERN).authenticated()
                        .requestMatchers(HttpMethod.POST, AppConstants.ALL_API_PATTERN).hasRole(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, AppConstants.ALL_API_PATTERN).hasRole(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, AppConstants.ALL_API_PATTERN).hasRole(AppConstants.ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(AppConstants.CORS_LOCALHOST, AppConstants.CORS_RAILWAY));
        configuration.setAllowedMethods(Arrays.asList(
                AppConstants.METHOD_GET,
                AppConstants.METHOD_POST,
                AppConstants.METHOD_PUT,
                AppConstants.METHOD_DELETE,
                AppConstants.METHOD_OPTIONS
        ));
        configuration.setAllowedHeaders(Arrays.asList(AppConstants.AUTHORIZATION_HEADER, AppConstants.CONTENT_TYPE_HEADER, AppConstants.X_REQUESTED_WITH_HEADER));
        configuration.setExposedHeaders(List.of(AppConstants.AUTHORIZATION_HEADER));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(AppConstants.CORS_PATTERN, configuration);
        return source;
    }
}
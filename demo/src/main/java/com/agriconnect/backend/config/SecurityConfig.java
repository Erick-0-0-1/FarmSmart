package com.agriconnect.backend.config;

import com.agriconnect.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // ← This is a configuration class
@EnableWebSecurity  // ← Enable Spring Security
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * PASSWORD ENCODER
     * This encrypts passwords before saving to database
     * Uses BCrypt algorithm (very secure!)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AUTHENTICATION MANAGER
     * Handles login authentication
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * SECURITY FILTER CHAIN
     * Defines security rules for different URLs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for REST APIs with JWT)
                .csrf(csrf -> csrf.disable())

                // Set session management to stateless (no sessions, we use JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define which URLs require authentication
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC URLs (anyone can access)
                        .requestMatchers("/api/auth/**").permitAll()  // Login, Register
                        .requestMatchers("/api/public/**").permitAll()  // Any public content

                        // PROTECTED URLs (must be logged in)
                        .anyRequest().authenticated()  // Everything else requires login
                )

                // Add our JWT filter before Spring Security's filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
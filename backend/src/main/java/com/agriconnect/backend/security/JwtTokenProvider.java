package com.agriconnect.backend.security;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.annotation.PostConstruct;

@Component  // ← Spring will manage this class
public class JwtTokenProvider {

    // These values come from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private final Environment environment;

    public JwtTokenProvider(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateJwtSecret() {
        // Check if running in production and JWT_SECRET is not set properly
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equalsIgnoreCase("prod") 
                        || profile.equalsIgnoreCase("production"));

        if (isProduction) {
            // In production, throw if secret is not set or is the default
            if (jwtSecret == null || jwtSecret.isEmpty() || 
                    jwtSecret.startsWith("$") || jwtSecret.contains("development")) {
                throw new IllegalStateException(
                    "CRITICAL: JWT_SECRET environment variable must be set in production! " +
                    "Generate a secure key with: openssl rand -base64 32"
                );
            }

            // Also validate minimum length for security
            if (jwtSecret.length() < 32) {
                throw new IllegalStateException(
                    "CRITICAL: JWT_SECRET must be at least 32 characters for HS256!"
                );
            }
        }
    }

    // Generate a signing key from the secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * GENERATE TOKEN
     * This creates a new JWT token when user logs in
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Build the token
        return Jwts.builder()
                .setSubject(userDetails.getUsername())  // Who is this token for?
                .setIssuedAt(now)                       // When was it created?
                .setExpiration(expiryDate)              // When does it expire?
                .signWith(getSigningKey())              // Sign it with our secret key
                .compact();                              // Build it!
    }

    /**
     * GET USERNAME FROM TOKEN
     * Extract username from the token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * VALIDATE TOKEN
     * Check if token is valid (not expired, not tampered with)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid
            return false;
        }
    }
}
package com.agriconnect.backend.security;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.security.Key;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component  // ← Spring will manage this class
public class JwtTokenProvider {

    // These values come from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

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
        Claims claims = Jwts.parserBuilder()
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
            Jwts.parserBuilder()
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
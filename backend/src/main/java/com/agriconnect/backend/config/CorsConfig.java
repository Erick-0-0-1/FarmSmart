package com.agriconnect.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    /**
     * CORS configuration for the application.
     * 
     * SECURITY NOTE: For production deployments, consider restricting allowed origins
     * instead of using "*". You can configure this via the ALLOWED_ORIGINS environment variable.
     * Example: ALLOWED_ORIGINS=https://your-domain.com,https://www.your-domain.com
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Get allowed origins from environment, fallback to localhost for development
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
        
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            // Validate and sanitize allowed origins
            String trimmedOrigins = allowedOrigins.trim();
            if ("*".equals(trimmedOrigins)) {
                // WARNING: Allow all origins (only for development)
                System.out.println("⚠️ WARNING: CORS configured to allow all origins. This is insecure for production!");
                corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
            } else {
                // Validate each origin is a proper URL
                List<String> originList = Arrays.asList(trimmedOrigins.split(","));
                for (String origin : originList) {
                    if (!origin.startsWith("http://") && !origin.startsWith("https://")) {
                        throw new IllegalArgumentException("Invalid origin format: " + origin + ". Must start with http:// or https://");
                    }
                }
                corsConfiguration.setAllowedOrigins(originList);
            }
        } else {
            // In production, require explicit ALLOWED_ORIGINS
            String[] activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE") != null 
                ? System.getenv("SPRING_PROFILES_ACTIVE").split(",") 
                : new String[]{};
            boolean isProduction = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equalsIgnoreCase("prod") || profile.equalsIgnoreCase("production"));
            
            if (isProduction) {
                throw new IllegalStateException(
                    "CRITICAL: ALLOWED_ORIGINS environment variable must be set in production! " +
                    "Example: ALLOWED_ORIGINS=https://your-domain.com"
                );
            }
            // Default: only localhost for development
            corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        }
        
corsConfiguration.setAllowCredentials(true);

        // Allow all common headers
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Access-Control-Allow-Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Expose headers
        corsConfiguration.setExposedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // Allow all HTTP methods
        corsConfiguration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
                new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
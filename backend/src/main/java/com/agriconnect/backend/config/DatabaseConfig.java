package com.agriconnect.backend.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        // If no DATABASE_URL is provided, use H2 for local development
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            System.out.println("⚠️ DATABASE_URL not set, using H2 in-memory database for development");
            return DataSourceBuilder.create()
                    .driverClassName("org.h2.Driver")
                    .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                    .username("sa")
                    .password("")
                    .build();
        }

        try {
            // Parse the DATABASE_URL
            URI dbUri = new URI(databaseUrl);

            String userInfo = dbUri.getUserInfo();
            if (userInfo == null || !userInfo.contains(":")) {
                throw new IllegalArgumentException("Invalid DATABASE_URL format: missing credentials");
            }
            
            String[] credentials = userInfo.split(":");
            if (credentials.length < 2) {
                throw new IllegalArgumentException("Invalid DATABASE_URL format: credentials must be in format user:password");
            }
            
            String username = credentials[0];
            String password = credentials[1];
            String host = dbUri.getHost();
            int port = dbUri.getPort();
            String path = dbUri.getPath();
            
            if (path == null || path.isEmpty()) {
                throw new IllegalArgumentException("Invalid DATABASE_URL format: missing database name");
            }
            path = path.substring(1); // Remove leading "/"
            
            if (host == null || host.isEmpty()) {
                throw new IllegalArgumentException("Invalid DATABASE_URL format: missing host");
            }

            // If port is -1 (not specified), use default 5432
            if (port == -1) {
                port = 5432;
            }

            // Build JDBC URL
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", host, port, path);

            System.out.println("Using PostgreSQL database");

            return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .build();

        } catch (URISyntaxException e) {
            throw new RuntimeException("Error parsing DATABASE_URL: " + databaseUrl, e);
        }
    }
}
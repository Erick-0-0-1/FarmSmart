package com.agriconnect.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository  // ← Tells Spring: "This is a database access layer"
public interface UserRepository extends JpaRepository<com.agriconnect.backend.model.User, Long> {

    // Spring automatically creates the SQL query for this!
    // SELECT * FROM users WHERE email = ?
    Optional<com.agriconnect.backend.model.User> findByEmail(String email);

    // SELECT * FROM users WHERE username = ?
    Optional<com.agriconnect.backend.model.User> findByUsername(String username);

    // Check if email exists
    // SELECT COUNT(*) FROM users WHERE email = ?
    boolean existsByEmail(String email);

    // Check if username exists
    boolean existsByUsername(String username);
}
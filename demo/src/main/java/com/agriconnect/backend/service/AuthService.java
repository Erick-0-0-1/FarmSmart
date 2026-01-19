package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.AuthResponse;
import com.agriconnect.backend.dto.LoginRequest;
import com.agriconnect.backend.dto.RegisterRequest;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * REGISTER NEW USER
     */
    public AuthResponse registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBarangay(request.getBarangay());
        user.setMunicipality(request.getMunicipality());
        user.setUserType(request.getUserType() != null ? request.getUserType() : User.UserType.FARMER);

        // Save to database
        User savedUser = userRepository.save(user);

        // Return response (without token for now)
        return new AuthResponse(
                "temporary-token",  // We'll fix this later
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getUserType().name()
        );
    }

    /**
     * LOGIN USER
     */
    public AuthResponse loginUser(LoginRequest request) {
        // Find user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> userRepository.findByEmail(request.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Return response (without token for now)
        return new AuthResponse(
                "temporary-token",  // We'll fix this later
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getUserType().name()
        );
    }
}
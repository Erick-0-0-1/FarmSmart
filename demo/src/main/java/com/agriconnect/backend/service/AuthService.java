package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.AuthResponse;
import com.agriconnect.backend.dto.LoginRequest;
import com.agriconnect.backend.dto.RegisterRequest;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.UserRepository;
import com.agriconnect.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service  // ← This is a service layer
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // For encrypting passwords

    @Autowired
    private JwtTokenProvider tokenProvider;  // For generating tokens

    @Autowired
    private AuthenticationManager authenticationManager;  // For authentication

    /**
     * REGISTER NEW USER
     */
    public AuthResponse registerUser(RegisterRequest request) {
        // 1. Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // 2. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // 3. Create new user object
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // ENCRYPT PASSWORD!
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBarangay(request.getBarangay());
        user.setMunicipality(request.getMunicipality());
        user.setUserType(request.getUserType() != null ? request.getUserType() : User.UserType.FARMER);

        // 4. Save to database
        User savedUser = userRepository.save(user);

        // 5. Automatically log them in (generate token)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        // 6. Return response with token
        return new AuthResponse(
                token,
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
        // 1. Authenticate username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        // 4. Get user details
        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> userRepository.findByEmail(request.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")));

        // 5. Return response with token
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getUserType().name()
        );
    }
}

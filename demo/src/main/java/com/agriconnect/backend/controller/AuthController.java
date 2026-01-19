package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.AuthResponse;
import com.agriconnect.backend.dto.LoginRequest;
import com.agriconnect.backend.dto.RegisterRequest;
import com.agriconnect.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  // ← This is a REST API controller
@RequestMapping("/api/auth")  // ← Base URL is /api/auth
@CrossOrigin(origins = "*")  // ← Allow requests from any domain (for development)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * REGISTER ENDPOINT
     * POST http://localhost:8080/api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * LOGIN ENDPOINT
     * POST http://localhost:8080/api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    /**
     * TEST ENDPOINT (Protected - requires JWT token)
     * GET http://localhost:8080/api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("If you see this, you are authenticated!");
    }
}
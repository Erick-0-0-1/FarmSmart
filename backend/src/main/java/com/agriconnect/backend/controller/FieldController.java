package com.agriconnect.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agriconnect.backend.model.Field;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.FieldRepository;
import com.agriconnect.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/fields")
public class FieldController {

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public FieldController(FieldRepository fieldRepository, UserRepository userRepository) {
        this.fieldRepository = fieldRepository;
        this.userRepository = userRepository;
    }

    // Helper to get current authenticated user, returns Optional.empty() if not authenticated
    private Optional<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    /**
     * GET /api/fields - returns all fields for authenticated user, or empty list for unauthenticated.
     */
    @GetMapping
    public ResponseEntity<List<Field>> getAllFields(Authentication authentication) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isEmpty()) {
            // For unauthenticated users, return an empty list (or you could return a 401, but the requirement is public)
            return ResponseEntity.ok(List.of());
        }
        List<Field> fields = fieldRepository.findByUserOrderByCreatedAtDesc(currentUser.get());
        return ResponseEntity.ok(fields);
    }

    /**
     * GET /api/fields/{id} - returns field if it belongs to authenticated user, otherwise 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id, Authentication authentication) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Field field = fieldRepository.findById(id).orElse(null);
        if (field == null || !field.getUser().getId().equals(currentUser.get().getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(field);
    }

    /**
     * POST /api/fields - create a field for authenticated user.
     */
    @PostMapping
    public ResponseEntity<?> createField(@RequestBody Field field, Authentication authentication) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        field.setUser(currentUser.get());
        Field savedField = fieldRepository.save(field);
        return ResponseEntity.ok(savedField);
    }

    /**
     * PUT /api/fields/{id} - update field if owned by authenticated user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Field> updateField(@PathVariable Long id, @RequestBody Field fieldDetails, Authentication authentication) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Field field = fieldRepository.findById(id).orElse(null);
        if (field == null || !field.getUser().getId().equals(currentUser.get().getId())) {
            return ResponseEntity.notFound().build();
        }

        field.setName(fieldDetails.getName());
        field.setLocation(fieldDetails.getLocation());
        field.setAreaHectares(fieldDetails.getAreaHectares());
        field.setSoilType(fieldDetails.getSoilType());
        field.setIrrigationType(fieldDetails.getIrrigationType());
        field.setDescription(fieldDetails.getDescription());

        Field updatedField = fieldRepository.save(field);
        return ResponseEntity.ok(updatedField);
    }

    /**
     * DELETE /api/fields/{id} - delete field if owned by authenticated user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id, Authentication authentication) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Field field = fieldRepository.findById(id).orElse(null);
        if (field == null || !field.getUser().getId().equals(currentUser.get().getId())) {
            return ResponseEntity.notFound().build();
        }
        fieldRepository.delete(field);
        return ResponseEntity.ok().build();
    }
}
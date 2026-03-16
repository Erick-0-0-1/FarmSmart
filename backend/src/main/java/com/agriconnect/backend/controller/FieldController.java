package com.agriconnect.backend.controller;

import java.util.List;

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

    // Get current authenticated user
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<Field>> getAllFields(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<Field> fields = fieldRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Field field = fieldRepository.findById(id).orElse(null);
        
        if (field == null || !field.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(field);
    }

    @PostMapping
    public ResponseEntity<?> createField(@RequestBody Field field, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        field.setUser(currentUser);
        Field savedField = fieldRepository.save(field);
        return ResponseEntity.ok(savedField);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Field> updateField(@PathVariable Long id, @RequestBody Field fieldDetails, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Field field = fieldRepository.findById(id).orElse(null);
        
        if (field == null || !field.getUser().getId().equals(currentUser.getId())) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Field field = fieldRepository.findById(id).orElse(null);
        
        if (field == null || !field.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }

        fieldRepository.delete(field);
        return ResponseEntity.ok().build();
    }
}

package com.agriconnect.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private static final Logger logger = LoggerFactory.getLogger(FieldController.class);

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public FieldController(FieldRepository fieldRepository, UserRepository userRepository) {
        this.fieldRepository = fieldRepository;
        this.userRepository = userRepository;
    }

    private Optional<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.debug("No authenticated user found");
            return Optional.empty();
        }
        String username = authentication.getName();
        logger.debug("Authenticated user: {}", username);
        return userRepository.findByUsername(username);
    }

    @GetMapping
    public ResponseEntity<List<Field>> getAllFields(Authentication authentication) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isEmpty()) {
            logger.debug("Returning empty field list for unauthenticated user");
            return ResponseEntity.ok(List.of());
        }
        List<Field> fields = fieldRepository.findByUserOrderByCreatedAtDesc(currentUser.get());
        return ResponseEntity.ok(fields);
    }

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

   @DeleteMapping("/{id}")
@PreAuthorize("permitAll()")
public ResponseEntity<?> deleteField(@PathVariable Long id, Authentication authentication) {
    logger.info("DELETE request received for field id: {}", id);
    Optional<User> currentUser = getCurrentUser(authentication);
    if (currentUser.isEmpty()) {
        logger.warn("Delete failed: user not authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    Field field = fieldRepository.findById(id).orElse(null);
    if (field == null) {
        logger.warn("Delete failed: field not found");
        return ResponseEntity.notFound().build();
    }
    if (!field.getUser().getId().equals(currentUser.get().getId())) {
        logger.warn("Delete failed: field not owned by user");
        return ResponseEntity.notFound().build();
    }

    try {
        fieldRepository.delete(field);
        logger.info("Field deleted successfully");
        return ResponseEntity.ok().build();
    } catch (DataIntegrityViolationException e) {
        logger.error("Cannot delete field because it has associated planting records", e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Cannot delete field because it has existing planting records. Please delete those records first.");
    }
}
}
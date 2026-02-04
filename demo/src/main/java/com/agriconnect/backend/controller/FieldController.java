package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.Field;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.FieldRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin(origins = "http://localhost:3000")
public class FieldController {

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public FieldController(FieldRepository fieldRepository, UserRepository userRepository) {
        this.fieldRepository = fieldRepository;
        this.userRepository = userRepository;
    }

    // GET all fields
    @GetMapping
    public ResponseEntity<List<Field>> getAllFields() {
        List<Field> fields = fieldRepository.findAll();
        return ResponseEntity.ok(fields);
    }

    // GET single field by ID
    @GetMapping("/{id}")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id) {
        Field field = fieldRepository.findById(id).orElse(null);
        if (field == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(field);
    }

    // CREATE new field
    @PostMapping
    public ResponseEntity<?> createField(@RequestBody Field field) {
        // Get or create test user
        User testUser = userRepository.findById(1L).orElse(null);
        if (testUser == null) {
            testUser = new User();
            testUser.setUsername("test");
            testUser.setEmail("test@test.com");
            testUser.setPassword("test");
            testUser = userRepository.save(testUser);
        }

        field.setUser(testUser);
        Field savedField = fieldRepository.save(field);
        return ResponseEntity.ok(savedField);
    }

    // UPDATE field
    @PutMapping("/{id}")
    public ResponseEntity<Field> updateField(@PathVariable Long id, @RequestBody Field fieldDetails) {
        Field field = fieldRepository.findById(id).orElse(null);
        if (field == null) {
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

    // DELETE field
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id) {
        Field field = fieldRepository.findById(id).orElse(null);
        if (field == null) {
            return ResponseEntity.notFound().build();
        }

        fieldRepository.delete(field);
        return ResponseEntity.ok().build();
    }
}
package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.CreateFieldRequest;
import com.agriconnect.backend.model.Field;
import com.agriconnect.backend.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin(origins = "*")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    /**
     * Create a new field
     * POST /api/fields
     */
    @PostMapping
    public ResponseEntity<?> createField(@RequestBody CreateFieldRequest request) {
        try {
            Field field = fieldService.createField(request);
            return ResponseEntity.ok(field);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all my fields
     * GET /api/fields
     */
    @GetMapping
    public ResponseEntity<List<Field>> getMyFields() {
        List<Field> fields = fieldService.getMyFields();
        return ResponseEntity.ok(fields);
    }

    /**
     * Get active fields only
     * GET /api/fields/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Field>> getActiveFields() {
        List<Field> fields = fieldService.getActiveFields();
        return ResponseEntity.ok(fields);
    }

    /**
     * Get field by ID
     * GET /api/fields/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFieldById(@PathVariable Long id) {
        try {
            Field field = fieldService.getFieldById(id);
            return ResponseEntity.ok(field);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update field
     * PUT /api/fields/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateField(
            @PathVariable Long id,
            @RequestBody CreateFieldRequest request) {
        try {
            Field field = fieldService.updateField(id, request);
            return ResponseEntity.ok(field);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete field (soft delete)
     * DELETE /api/fields/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id) {
        try {
            fieldService.deleteField(id);
            return ResponseEntity.ok("Field deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
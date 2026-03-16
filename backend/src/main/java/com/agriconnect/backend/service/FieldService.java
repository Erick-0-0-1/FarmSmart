package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.CreateFieldRequest;
import com.agriconnect.backend.model.Field;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.FieldRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldService {

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new field for current user
     */
    public Field createField(CreateFieldRequest request) {
        User user = getCurrentUser();

        Field field = new Field();
        field.setUser(user);
        field.setName(request.getName());
        field.setDescription(request.getDescription());
        field.setAreaHectares(request.getAreaHectares());
        field.setLocation(request.getLocation());
        field.setLatitude(request.getLatitude());
        field.setLongitude(request.getLongitude());
        field.setSoilType(request.getSoilType());

        // Parse irrigation type safely
        if (request.getIrrigationType() != null && !request.getIrrigationType().isEmpty()) {
            try {
                field.setIrrigationType(
                        Field.IrrigationType.valueOf(request.getIrrigationType().toUpperCase())
                );
            } catch (IllegalArgumentException e) {
                // Default to RAINFED if invalid
                field.setIrrigationType(Field.IrrigationType.RAINFED);
            }
        } else {
            field.setIrrigationType(Field.IrrigationType.RAINFED);
        }

        return fieldRepository.save(field);
    }
    /**
     * Get all fields for current user
     */
    public List<Field> getMyFields() {
        User user = getCurrentUser();
        return fieldRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get active fields only
     */
    public List<Field> getActiveFields() {
        User user = getCurrentUser();
        return fieldRepository.findByUserAndIsActiveTrue(user);
    }

    /**
     * Get field by ID (with ownership check)
     */
    public Field getFieldById(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));

        User user = getCurrentUser();
        if (!field.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to field");
        }

        return field;
    }

    /**
     * Update field
     */
    public Field updateField(Long id, CreateFieldRequest request) {
        Field field = getFieldById(id); // This checks ownership

        field.setName(request.getName());
        field.setDescription(request.getDescription());
        field.setAreaHectares(request.getAreaHectares());
        field.setLocation(request.getLocation());
        field.setLatitude(request.getLatitude());
        field.setLongitude(request.getLongitude());
        field.setSoilType(request.getSoilType());

        if (request.getIrrigationType() != null) {
            try {
                field.setIrrigationType(
                        Field.IrrigationType.valueOf(request.getIrrigationType().toUpperCase())
                );
            } catch (IllegalArgumentException e) {
                // Keep existing value
            }
        }

        return fieldRepository.save(field);
    }

    /**
     * Delete field (soft delete - set inactive)
     */
    public void deleteField(Long id) {
        Field field = getFieldById(id); // This checks ownership
        field.setIsActive(false);
        fieldRepository.save(field);
    }

    /**
     * Permanently delete field
     */
    public void permanentlyDeleteField(Long id) {
        Field field = getFieldById(id); // This checks ownership
        fieldRepository.delete(field);
    }

    /**
     * Get current logged-in user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
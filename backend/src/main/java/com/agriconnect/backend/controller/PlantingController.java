package com.agriconnect.backend.controller;

import java.time.LocalDate;
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

import com.agriconnect.backend.dto.CreatePlantingRequest;
import com.agriconnect.backend.model.Field;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.model.RiceVariety;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.FieldRepository;
import com.agriconnect.backend.repository.PlantingRecordRepository;
import com.agriconnect.backend.repository.RiceVarietyRepository;
import com.agriconnect.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/plantings")
public class PlantingController {

    private final PlantingRecordRepository plantingRepository;
    private final FieldRepository fieldRepository;
    private final RiceVarietyRepository riceVarietyRepository;
    private final UserRepository userRepository;

    public PlantingController(PlantingRecordRepository plantingRepository,
                              FieldRepository fieldRepository,
                              RiceVarietyRepository riceVarietyRepository,
                              UserRepository userRepository) {
        this.plantingRepository = plantingRepository;
        this.fieldRepository = fieldRepository;
        this.riceVarietyRepository = riceVarietyRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<PlantingRecord>> getAllPlantings(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<PlantingRecord> plantings = plantingRepository.findByUserOrderByPlantingDateDesc(currentUser);
        return ResponseEntity.ok(plantings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantingRecord> getPlantingById(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        PlantingRecord planting = plantingRepository.findById(id).orElse(null);
        
        if (planting == null || !planting.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planting);
    }

    @PostMapping
    public ResponseEntity<?> createPlanting(@RequestBody CreatePlantingRequest request, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);

            // Get field
            Field field = fieldRepository.findById(request.getFieldId())
                    .orElseThrow(() -> new RuntimeException("Field not found with id: " + request.getFieldId()));

            // Get rice variety
            RiceVariety riceVariety = riceVarietyRepository.findById(request.getRiceVarietyId())
                    .orElseThrow(() -> new RuntimeException("Rice variety not found with id: " + request.getRiceVarietyId()));

            // Create planting
            PlantingRecord planting = new PlantingRecord();
            planting.setUser(currentUser);
            planting.setField(field);
            planting.setRiceVariety(riceVariety);
            planting.setPlantingDate(request.getPlantingDate());
            planting.setAreaHectares(request.getAreaHectares());
            planting.setNotes(request.getNotes());

            // Set status - handle if null
            if (request.getStatus() != null) {
                planting.setStatus(request.getStatus());
            } else {
                planting.setStatus(PlantingRecord.Status.PLANNING); // Default value
            }

            // Calculate expected harvest date
            LocalDate expectedHarvestDate = request.getPlantingDate()
                    .plusDays(riceVariety.getMaturityDays());
            planting.setExpectedHarvestDate(expectedHarvestDate);

            // Save planting
            PlantingRecord savedPlanting = plantingRepository.save(planting);

            return ResponseEntity.ok(savedPlanting);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating planting: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlanting(@PathVariable Long id, @RequestBody CreatePlantingRequest request, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            PlantingRecord planting = plantingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Planting not found"));
            
            // Verify ownership
            if (!planting.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("Unauthorized");
            }

            if (request.getFieldId() != null) {
                Field field = fieldRepository.findById(request.getFieldId())
                        .orElseThrow(() -> new RuntimeException("Field not found"));
                planting.setField(field);
            }

            if (request.getRiceVarietyId() != null) {
                RiceVariety riceVariety = riceVarietyRepository.findById(request.getRiceVarietyId())
                        .orElseThrow(() -> new RuntimeException("Rice variety not found"));
                planting.setRiceVariety(riceVariety);
            }

            if (request.getPlantingDate() != null) {
                planting.setPlantingDate(request.getPlantingDate());
                LocalDate expectedHarvestDate = request.getPlantingDate()
                        .plusDays(planting.getRiceVariety().getMaturityDays());
                planting.setExpectedHarvestDate(expectedHarvestDate);
            }

            if (request.getAreaHectares() != null) {
                planting.setAreaHectares(request.getAreaHectares());
            }

            if (request.getStatus() != null) {
                planting.setStatus(request.getStatus());
            }

            if (request.getNotes() != null) {
                planting.setNotes(request.getNotes());
            }

            PlantingRecord updatedPlanting = plantingRepository.save(planting);
            return ResponseEntity.ok(updatedPlanting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating planting: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/harvest")
    public ResponseEntity<?> markAsHarvested(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            PlantingRecord planting = plantingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Planting not found"));
            
            // Verify ownership
            if (!planting.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("Unauthorized");
            }

            planting.setStatus(PlantingRecord.Status.HARVESTED);
            planting.setActualHarvestDate(LocalDate.now());

            PlantingRecord updatedPlanting = plantingRepository.save(planting);
            return ResponseEntity.ok(updatedPlanting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking as harvested: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlanting(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        PlantingRecord planting = plantingRepository.findById(id).orElse(null);
        
        if (planting == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify ownership
        if (!planting.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        plantingRepository.delete(planting);
        return ResponseEntity.ok().build();
    }

    // Test endpoint for debugging
    @PostMapping("/test")
    public ResponseEntity<?> testEndpoint(@RequestBody CreatePlantingRequest request) {
        System.out.println("Test endpoint received:");
        System.out.println("Field ID: " + request.getFieldId());
        System.out.println("Variety ID: " + request.getRiceVarietyId());
        System.out.println("Status: " + request.getStatus());
        System.out.println("Planting Date: " + request.getPlantingDate());
        System.out.println("Area Hectares: " + request.getAreaHectares());
        System.out.println("Notes: " + request.getNotes());

        return ResponseEntity.ok("Request received successfully");
    }
}
package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.CreatePlantingRequest;
import com.agriconnect.backend.model.Field;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.model.RiceVariety;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.FieldRepository;
import com.agriconnect.backend.repository.PlantingRecordRepository;
import com.agriconnect.backend.repository.RiceVarietyRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/plantings")
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping
    public ResponseEntity<List<PlantingRecord>> getAllPlantings() {
        List<PlantingRecord> plantings = plantingRepository.findAll();
        return ResponseEntity.ok(plantings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantingRecord> getPlantingById(@PathVariable Long id) {
        PlantingRecord planting = plantingRepository.findById(id).orElse(null);
        if (planting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planting);
    }

    @PostMapping
    public ResponseEntity<?> createPlanting(@RequestBody CreatePlantingRequest request) {
        try {
            System.out.println("DEBUG - Request received:");
            System.out.println("Field ID: " + request.getFieldId());
            System.out.println("Variety ID: " + request.getRiceVarietyId());
            System.out.println("Status: " + request.getStatus());
            System.out.println("Planting Date: " + request.getPlantingDate());
            System.out.println("Area Hectares: " + request.getAreaHectares());
            System.out.println("Notes: " + request.getNotes());

            // Get or create test user
            User testUser = userRepository.findById(1L).orElse(null);
            if (testUser == null) {
                testUser = new User();
                testUser.setUsername("test");
                testUser.setEmail("test@test.com");
                testUser.setPassword("test");
                testUser = userRepository.save(testUser);
            }

            // Get field
            Field field = fieldRepository.findById(request.getFieldId())
                    .orElseThrow(() -> new RuntimeException("Field not found with id: " + request.getFieldId()));

            // Get rice variety
            RiceVariety riceVariety = riceVarietyRepository.findById(request.getRiceVarietyId())
                    .orElseThrow(() -> new RuntimeException("Rice variety not found with id: " + request.getRiceVarietyId()));

            // Create planting
            PlantingRecord planting = new PlantingRecord();
            planting.setUser(testUser);
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
            System.out.println("DEBUG - Planting saved with ID: " + savedPlanting.getId());

            return ResponseEntity.ok(savedPlanting);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating planting: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlanting(@PathVariable Long id, @RequestBody CreatePlantingRequest request) {
        try {
            PlantingRecord planting = plantingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Planting not found"));

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
    public ResponseEntity<?> markAsHarvested(@PathVariable Long id) {
        try {
            PlantingRecord planting = plantingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Planting not found"));

            planting.setStatus(PlantingRecord.Status.HARVESTED);
            planting.setActualHarvestDate(LocalDate.now());

            PlantingRecord updatedPlanting = plantingRepository.save(planting);
            return ResponseEntity.ok(updatedPlanting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking as harvested: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlanting(@PathVariable Long id) {
        PlantingRecord planting = plantingRepository.findById(id).orElse(null);
        if (planting == null) {
            return ResponseEntity.notFound().build();
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
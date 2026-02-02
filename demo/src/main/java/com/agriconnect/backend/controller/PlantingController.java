package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.CreatePlantingRequest;
import com.agriconnect.backend.dto.PlantingResponse;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.service.PlantingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plantings")
@CrossOrigin(origins = "*")
public class PlantingController {

    @Autowired
    private PlantingService plantingService;

    /**
     * Create new planting record
     * POST /api/plantings
     */
    @PostMapping
    public ResponseEntity<?> createPlanting(@RequestBody CreatePlantingRequest request) {
        try {
            PlantingResponse response = plantingService.createPlanting(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all my plantings
     * GET /api/plantings
     */
    @GetMapping
    public ResponseEntity<List<PlantingResponse>> getMyPlantings() {
        return ResponseEntity.ok(plantingService.getMyPlantings());
    }

    /**
     * Get active plantings only
     * GET /api/plantings/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<PlantingResponse>> getActivePlantings() {
        return ResponseEntity.ok(plantingService.getActivePlantings());
    }

    /**
     * Get planting by ID
     * GET /api/plantings/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlantingById(@PathVariable Long id) {
        try {
            PlantingResponse response = plantingService.getPlantingById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update planting status
     * PUT /api/plantings/1/status
     * Body: { "status": "HARVESTED" }
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updatePlantingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            PlantingRecord.Status status = PlantingRecord.Status.valueOf(request.get("status"));
            PlantingResponse response = plantingService.updatePlantingStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete planting
     * DELETE /api/plantings/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlanting(@PathVariable Long id) {
        try {
            plantingService.deletePlanting(id);
            return ResponseEntity.ok("Planting record deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
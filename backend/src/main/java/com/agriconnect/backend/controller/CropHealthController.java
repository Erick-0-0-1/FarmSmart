package com.agriconnect.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agriconnect.backend.model.CropHealthAnalysis;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.PlantingRecordRepository;
import com.agriconnect.backend.repository.UserRepository;
import com.agriconnect.backend.service.CropHealthService;

@RestController
@RequestMapping("/api/crop-health")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CropHealthController {

    @Autowired
    private CropHealthService cropHealthService;

    @Autowired
    private PlantingRecordRepository plantingRecordRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to get current authenticated user
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Helper method to verify user owns the planting record
    private ResponseEntity<?> verifyPlantingOwnership(Long plantingRecordId, User currentUser) {
        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
            .orElseThrow(() -> new RuntimeException("Planting record not found"));
        
        if (!planting.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access to this planting record");
        }
        return null;
    }

    /**
     * Analyze crop image
     * POST /api/crop-health/analyze/1
     *
     * THIS IS THE AI IMAGE ANALYSIS FEATURE! 📸
     */
    @PostMapping("/analyze/{plantingRecordId}")
    public ResponseEntity<?> analyzeCropImage(
            @PathVariable Long plantingRecordId,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            verifyPlantingOwnership(plantingRecordId, currentUser);
            
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload an image");
            }

            CropHealthAnalysis analysis =
                    cropHealthService.analyzeCropImage(plantingRecordId, image, notes);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all analyses for a planting
     * GET /api/crop-health/planting/1
     */
    @GetMapping("/planting/{plantingRecordId}")
    public ResponseEntity<?> getAnalysesForPlanting(
            @PathVariable Long plantingRecordId,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            verifyPlantingOwnership(plantingRecordId, currentUser);
            
            List<CropHealthAnalysis> analyses =
                    cropHealthService.getAnalysesForPlanting(plantingRecordId);
            return ResponseEntity.ok(analyses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get latest analysis
     * GET /api/crop-health/latest/1
     */
    @GetMapping("/latest/{plantingRecordId}")
    public ResponseEntity<?> getLatestAnalysis(
            @PathVariable Long plantingRecordId,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            verifyPlantingOwnership(plantingRecordId, currentUser);
            
            CropHealthAnalysis analysis =
                    cropHealthService.getLatestAnalysis(plantingRecordId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get analysis by ID
     * GET /api/crop-health/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAnalysisById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            // Get the analysis by ID
            CropHealthAnalysis analysis = cropHealthService.getAnalysisById(id);
            
            // Verify ownership through the planting record
            User currentUser = getCurrentUser(authentication);
            Long plantingUserId = analysis.getPlantingRecord().getUser().getId();
            
            if (!plantingUserId.equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("Unauthorized");
            }
            
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
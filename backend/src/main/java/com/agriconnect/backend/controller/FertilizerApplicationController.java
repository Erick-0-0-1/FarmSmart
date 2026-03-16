package com.agriconnect.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agriconnect.backend.dto.FertilizerRecommendation;
import com.agriconnect.backend.model.FertilizerApplication;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.PlantingRecordRepository;
import com.agriconnect.backend.repository.UserRepository;
import com.agriconnect.backend.service.FertilizerApplicationService;

@RestController
@RequestMapping("/api/fertilizer")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FertilizerApplicationController {

    @Autowired
    private FertilizerApplicationService applicationService;

    @Autowired
    private PlantingRecordRepository plantingRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.agriconnect.backend.repository.FertilizerApplicationRepository fertilizerApplicationRepository;

    // Helper method to get current authenticated user
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Helper method to verify user owns the planting record
    private ResponseEntity<?> verifyPlantingOwnership(Long plantingRecordId, User currentUser) {
        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
            .orElse(null);
        
        if (planting == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Planting record not found");
        }
        
        if (!planting.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access to this planting record");
        }
        return null;
    }

    // Helper method to verify user owns the application
    private ResponseEntity<?> verifyApplicationOwnership(Long applicationId, User currentUser) {
        FertilizerApplication application = fertilizerApplicationRepository.findById(applicationId)
            .orElse(null);
        
        if (application == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found");
        }
        
        Long plantingUserId = application.getPlantingRecord().getUser().getId();
        if (!plantingUserId.equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access to this application");
        }
        return null;
    }

    /**
     * Generate fertilizer schedule for a planting
     * POST /api/fertilizer/generate/1
     */
    @PostMapping("/generate/{plantingRecordId}")
    public ResponseEntity<?> generateSchedule(
            @PathVariable Long plantingRecordId,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyPlantingOwnership(plantingRecordId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            List<FertilizerApplication> applications =
                    applicationService.generateApplicationSchedule(plantingRecordId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all fertilizer applications for a planting
     * GET /api/fertilizer/planting/1
     */
    @GetMapping("/planting/{plantingRecordId}")
    public ResponseEntity<?> getApplicationsForPlanting(
            @PathVariable Long plantingRecordId,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyPlantingOwnership(plantingRecordId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            List<FertilizerApplication> applications =
                    applicationService.getApplicationsForPlanting(plantingRecordId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get smart recommendation for an application
     * GET /api/fertilizer/recommendation/1
     *
     * THIS IS THE MAIN SMART FEATURE! 🧠
     */
    @GetMapping("/recommendation/{applicationId}")
    public ResponseEntity<?> getSmartRecommendation(
            @PathVariable Long applicationId,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyApplicationOwnership(applicationId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            FertilizerRecommendation recommendation =
                    applicationService.getSmartRecommendation(applicationId);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get upcoming applications (next 7 days)
     * GET /api/fertilizer/upcoming/1
     */
    @GetMapping("/upcoming/{plantingRecordId}")
    public ResponseEntity<?> getUpcomingApplications(
            @PathVariable Long plantingRecordId,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyPlantingOwnership(plantingRecordId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            List<FertilizerApplication> applications =
                    applicationService.getUpcomingApplications(plantingRecordId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get pending applications
     * GET /api/fertilizer/pending/1
     */
    @GetMapping("/pending/{plantingRecordId}")
    public ResponseEntity<?> getPendingApplications(
            @PathVariable Long plantingRecordId,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyPlantingOwnership(plantingRecordId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            List<FertilizerApplication> applications =
                    applicationService.getPendingApplications(plantingRecordId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Mark application as completed
     * POST /api/fertilizer/complete/1
     * Body: { "appliedDate": "2026-01-26", "notes": "Applied successfully" }
     */
    @PostMapping("/complete/{applicationId}")
    public ResponseEntity<?> markAsCompleted(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyApplicationOwnership(applicationId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            LocalDate appliedDate = LocalDate.parse(request.get("appliedDate"));
            String notes = request.get("notes");

            FertilizerApplication application =
                    applicationService.markAsCompleted(applicationId, appliedDate, notes);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Postpone application
     * POST /api/fertilizer/postpone/1
     * Body: { "newDate": "2026-01-30", "reason": "Heavy rain forecasted" }
     */
    @PostMapping("/postpone/{applicationId}")
    public ResponseEntity<?> postponeApplication(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyApplicationOwnership(applicationId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            LocalDate newDate = LocalDate.parse(request.get("newDate"));
            String reason = request.get("reason");

            FertilizerApplication application =
                    applicationService.postponeApplication(applicationId, newDate, reason);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Skip application
     * POST /api/fertilizer/skip/1
     * Body: { "reason": "Farmer decided to skip this application" }
     */
    @PostMapping("/skip/{applicationId}")
    public ResponseEntity<?> skipApplication(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            ResponseEntity<?> ownershipError = verifyApplicationOwnership(applicationId, currentUser);
            if (ownershipError != null) {
                return ownershipError;
            }
            
            String reason = request.get("reason");

            FertilizerApplication application =
                    applicationService.skipApplication(applicationId, reason);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
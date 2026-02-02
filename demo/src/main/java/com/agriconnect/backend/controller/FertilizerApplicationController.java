package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.FertilizerRecommendation;
import com.agriconnect.backend.model.FertilizerApplication;
import com.agriconnect.backend.service.FertilizerApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fertilizer")
@CrossOrigin(origins = "*")
public class FertilizerApplicationController {

    @Autowired
    private FertilizerApplicationService applicationService;

    /**
     * Generate fertilizer schedule for a planting
     * POST /api/fertilizer/generate/1
     */
    @PostMapping("/generate/{plantingRecordId}")
    public ResponseEntity<?> generateSchedule(@PathVariable Long plantingRecordId) {
        try {
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
    public ResponseEntity<?> getApplicationsForPlanting(@PathVariable Long plantingRecordId) {
        try {
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
    public ResponseEntity<?> getSmartRecommendation(@PathVariable Long applicationId) {
        try {
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
    public ResponseEntity<?> getUpcomingApplications(@PathVariable Long plantingRecordId) {
        try {
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
    public ResponseEntity<?> getPendingApplications(@PathVariable Long plantingRecordId) {
        try {
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
            @RequestBody Map<String, String> request) {
        try {
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
            @RequestBody Map<String, String> request) {
        try {
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
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");

            FertilizerApplication application =
                    applicationService.skipApplication(applicationId, reason);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
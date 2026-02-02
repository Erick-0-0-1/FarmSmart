package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.CropHealthAnalysis;
import com.agriconnect.backend.service.CropHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/crop-health")
@CrossOrigin(origins = "*")
public class CropHealthController {

    @Autowired
    private CropHealthService cropHealthService;

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
            @RequestParam(value = "notes", required = false) String notes) {
        try {
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
    public ResponseEntity<?> getAnalysesForPlanting(@PathVariable Long plantingRecordId) {
        try {
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
    public ResponseEntity<?> getLatestAnalysis(@PathVariable Long plantingRecordId) {
        try {
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
    public ResponseEntity<?> getAnalysisById(@PathVariable Long id) {
        try {
            CropHealthAnalysis analysis =
                    cropHealthService.getAnalysesForPlanting(id).get(0);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
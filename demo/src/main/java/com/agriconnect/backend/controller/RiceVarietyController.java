package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.RiceVariety;
import com.agriconnect.backend.service.RiceVarietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/varieties")
@CrossOrigin(origins = "*")
public class RiceVarietyController {

    @Autowired
    private RiceVarietyService riceVarietyService;

    /**
     * Get all rice varieties
     * GET /api/varieties
     */
    @GetMapping
    public ResponseEntity<List<RiceVariety>> getAllVarieties() {
        return ResponseEntity.ok(riceVarietyService.getAllVarieties());
    }

    /**
     * Get variety by ID
     * GET /api/varieties/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<RiceVariety> getVarietyById(@PathVariable Long id) {
        return ResponseEntity.ok(riceVarietyService.getVarietyById(id));
    }

    /**
     * Get recommended varieties for current season
     * GET /api/varieties/recommended
     */
    @GetMapping("/recommended")
    public ResponseEntity<List<RiceVariety>> getRecommendedVarieties() {
        return ResponseEntity.ok(riceVarietyService.getRecommendedVarieties());
    }

    /**
     * Get drought-tolerant varieties
     * GET /api/varieties/drought-tolerant
     */
    @GetMapping("/drought-tolerant")
    public ResponseEntity<List<RiceVariety>> getDroughtTolerantVarieties() {
        return ResponseEntity.ok(riceVarietyService.getDroughtTolerantVarieties());
    }

    /**
     * Get flood-tolerant varieties
     * GET /api/varieties/flood-tolerant
     */
    @GetMapping("/flood-tolerant")
    public ResponseEntity<List<RiceVariety>> getFloodTolerantVarieties() {
        return ResponseEntity.ok(riceVarietyService.getFloodTolerantVarieties());
    }

    /**
     * Get varieties by season
     * GET /api/varieties/season/WET
     */
    @GetMapping("/season/{season}")
    public ResponseEntity<List<RiceVariety>> getVarietiesBySeason(@PathVariable RiceVariety.Season season) {
        return ResponseEntity.ok(riceVarietyService.getVarietiesBySeason(season));
    }
}
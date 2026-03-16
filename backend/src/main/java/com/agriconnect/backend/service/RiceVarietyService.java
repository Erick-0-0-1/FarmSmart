package com.agriconnect.backend.service;

import com.agriconnect.backend.model.RiceVariety;
import com.agriconnect.backend.repository.RiceVarietyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class RiceVarietyService {

    @Autowired
    private RiceVarietyRepository riceVarietyRepository;

    /**
     * Get all rice varieties
     */
    public List<RiceVariety> getAllVarieties() {
        return riceVarietyRepository.findAll();
    }

    /**
     * Get variety by ID
     */
    public RiceVariety getVarietyById(Long id) {
        return riceVarietyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rice variety not found with id: " + id));
    }

    /**
     * Get recommended varieties for current season
     * Wet season: June-November
     * Dry season: December-May
     */
    public List<RiceVariety> getRecommendedVarieties() {
        RiceVariety.Season currentSeason = getCurrentSeason();

        // Get varieties for current season OR varieties that work in BOTH seasons
        return riceVarietyRepository.findBySeasonIn(
                Arrays.asList(currentSeason, RiceVariety.Season.BOTH)
        );
    }

    /**
     * Determine current season based on month
     */
    private RiceVariety.Season getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();

        // Wet season: June (6) to November (11)
        // Dry season: December (12) to May (5)
        if (month >= 6 && month <= 11) {
            return RiceVariety.Season.WET;
        } else {
            return RiceVariety.Season.DRY;
        }
    }

    /**
     * Get drought-tolerant varieties
     */
    public List<RiceVariety> getDroughtTolerantVarieties() {
        return riceVarietyRepository.findByDroughtTolerantTrue();
    }

    /**
     * Get flood-tolerant varieties
     */
    public List<RiceVariety> getFloodTolerantVarieties() {
        return riceVarietyRepository.findByFloodTolerantTrue();
    }

    /**
     * Get varieties by season
     */
    public List<RiceVariety> getVarietiesBySeason(RiceVariety.Season season) {
        return riceVarietyRepository.findBySeason(season);
    }
}
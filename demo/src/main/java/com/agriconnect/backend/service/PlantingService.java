package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.CreatePlantingRequest;
import com.agriconnect.backend.dto.PlantingResponse;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.model.RiceVariety;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.PlantingRecordRepository;
import com.agriconnect.backend.repository.RiceVarietyRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantingService {

    @Autowired
    private PlantingRecordRepository plantingRecordRepository;

    @Autowired
    private RiceVarietyRepository riceVarietyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new planting record
     */
    public PlantingResponse createPlanting(CreatePlantingRequest request) {
        // Get current logged-in user
        User user = getCurrentUser();

        // Get rice variety
        RiceVariety variety = riceVarietyRepository.findById(request.getVarietyId())
                .orElseThrow(() -> new RuntimeException("Rice variety not found"));

        // Create planting record
        PlantingRecord planting = new PlantingRecord();
        planting.setUser(user);
        planting.setRiceVariety(variety);
        planting.setPlantingDate(request.getPlantingDate());
        planting.setAreaHectares(request.getAreaHectares());
        planting.setNotes(request.getNotes());
        planting.setStatus(PlantingRecord.Status.PLANTED);

        // Calculate expected harvest date
        LocalDate harvestDate = request.getPlantingDate().plusDays(variety.getMaturityDays());
        planting.setExpectedHarvestDate(harvestDate);

        // Save
        PlantingRecord savedPlanting = plantingRecordRepository.save(planting);

        // Convert to response
        return convertToResponse(savedPlanting);
    }

    /**
     * Get all plantings for current user
     */
    public List<PlantingResponse> getMyPlantings() {
        User user = getCurrentUser();
        List<PlantingRecord> plantings = plantingRecordRepository.findByUserOrderByPlantingDateDesc(user);

        return plantings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active plantings (not harvested or cancelled)
     */
    public List<PlantingResponse> getActivePlantings() {
        User user = getCurrentUser();
        List<PlantingRecord.Status> activeStatuses = List.of(
                PlantingRecord.Status.PLANTED,
                PlantingRecord.Status.GROWING
        );

        List<PlantingRecord> plantings = plantingRecordRepository.findByUserAndStatusIn(user, activeStatuses);

        return plantings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get planting by ID
     */
    public PlantingResponse getPlantingById(Long id) {
        PlantingRecord planting = plantingRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        // Check if planting belongs to current user
        User user = getCurrentUser();
        if (!planting.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to planting record");
        }

        return convertToResponse(planting);
    }

    /**
     * Update planting status
     */
    public PlantingResponse updatePlantingStatus(Long id, PlantingRecord.Status status) {
        PlantingRecord planting = plantingRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        // Check if planting belongs to current user
        User user = getCurrentUser();
        if (!planting.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to planting record");
        }

        planting.setStatus(status);
        PlantingRecord updatedPlanting = plantingRecordRepository.save(planting);

        return convertToResponse(updatedPlanting);
    }

    /**
     * Delete planting record
     */
    public void deletePlanting(Long id) {
        PlantingRecord planting = plantingRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        // Check if planting belongs to current user
        User user = getCurrentUser();
        if (!planting.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to planting record");
        }

        plantingRecordRepository.delete(planting);
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

    /**
     * Convert PlantingRecord to PlantingResponse
     */
    /**
     * Convert PlantingRecord to PlantingResponse
     */
    private PlantingResponse convertToResponse(PlantingRecord planting) {
        PlantingResponse response = new PlantingResponse();
        response.setId(planting.getId());
        response.setVarietyId(planting.getRiceVariety().getId());
        response.setVarietyName(planting.getRiceVariety().getName());
        response.setVarietyCode(planting.getRiceVariety().getCode());
        response.setPlantingDate(planting.getPlantingDate());
        response.setExpectedHarvestDate(planting.getExpectedHarvestDate());
        response.setActualHarvestDate(planting.getActualHarvestDate());
        response.setAreaHectares(planting.getAreaHectares());
        response.setStatus(planting.getStatus());
        response.setNotes(planting.getNotes());

        // Calculate days until harvest
        if (planting.getExpectedHarvestDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    planting.getExpectedHarvestDate()
            );
            response.setDaysUntilHarvest((int) days);
        }

        // Add field info if available
        if (planting.getField() != null) {
            response.setFieldId(planting.getField().getId());
            response.setFieldName(planting.getField().getName());
        }

        return response;
    }
}
package com.agriconnect.backend.dto;

import java.time.LocalDate;

public class CreatePlantingRequest {
    private Long varietyId;
    private LocalDate plantingDate;
    private Double areaHectares;
    private String notes;

    // Getters and Setters
    public Long getVarietyId() {
        return varietyId;
    }

    public void setVarietyId(Long varietyId) {
        this.varietyId = varietyId;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public Double getAreaHectares() {
        return areaHectares;
    }

    public void setAreaHectares(Double areaHectares) {
        this.areaHectares = areaHectares;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
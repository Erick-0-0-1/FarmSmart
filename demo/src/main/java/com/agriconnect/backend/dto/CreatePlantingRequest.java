package com.agriconnect.backend.dto;

import com.agriconnect.backend.model.PlantingRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class CreatePlantingRequest {
    private Long riceVarietyId;  // Changed from varietyId to riceVarietyId
    private Long fieldId;

    @JsonFormat(pattern = "yyyy-MM-dd")  // Changed from MM/dd/yyyy to yyyy-MM-dd
    private LocalDate plantingDate;

    private Double areaHectares;
    private String notes;
    private PlantingRecord.Status status;

    // Getters and Setters
    public Long getRiceVarietyId() {
        return riceVarietyId;
    }

    public void setRiceVarietyId(Long riceVarietyId) {
        this.riceVarietyId = riceVarietyId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
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

    public PlantingRecord.Status getStatus() {
        return status;
    }

    public void setStatus(PlantingRecord.Status status) {
        this.status = status;
    }
}
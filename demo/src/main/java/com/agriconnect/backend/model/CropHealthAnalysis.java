package com.agriconnect.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crop_health_analyses")
public class CropHealthAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "planting_record_id", nullable = false)
    private PlantingRecord plantingRecord;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;  // URL to uploaded image

    // AI Analysis Results
    @Column(name = "needs_water")
    private Boolean needsWater;

    @Column(name = "water_confidence")
    private Double waterConfidence;  // 0.0 to 1.0

    @Column(name = "needs_fertilizer")
    private Boolean needsFertilizer;

    @Column(name = "fertilizer_confidence")
    private Double fertilizerConfidence;

    @Column(name = "has_pest_disease")
    private Boolean hasPestDisease;

    @Column(name = "pest_disease_type", length = 200)
    private String pestDiseaseType;  // e.g., "Brown spot", "Stem borer"

    @Column(name = "pest_confidence")
    private Double pestConfidence;

    @Column(name = "has_weeds")
    private Boolean hasWeeds;

    @Column(name = "weed_severity")
    private String weedSeverity;  // LOW, MEDIUM, HIGH

    @Column(name = "weed_confidence")
    private Double weedConfidence;

    @Column(name = "overall_health_score")
    private Double overallHealthScore;  // 0-100

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;  // AI-generated advice

    @Column(columnDefinition = "TEXT")
    private String notes;  // Farmer's notes

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }

    // Constructors
    public CropHealthAnalysis() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlantingRecord getPlantingRecord() {
        return plantingRecord;
    }

    public void setPlantingRecord(PlantingRecord plantingRecord) {
        this.plantingRecord = plantingRecord;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getNeedsWater() {
        return needsWater;
    }

    public void setNeedsWater(Boolean needsWater) {
        this.needsWater = needsWater;
    }

    public Double getWaterConfidence() {
        return waterConfidence;
    }

    public void setWaterConfidence(Double waterConfidence) {
        this.waterConfidence = waterConfidence;
    }

    public Boolean getNeedsFertilizer() {
        return needsFertilizer;
    }

    public void setNeedsFertilizer(Boolean needsFertilizer) {
        this.needsFertilizer = needsFertilizer;
    }

    public Double getFertilizerConfidence() {
        return fertilizerConfidence;
    }

    public void setFertilizerConfidence(Double fertilizerConfidence) {
        this.fertilizerConfidence = fertilizerConfidence;
    }

    public Boolean getHasPestDisease() {
        return hasPestDisease;
    }

    public void setHasPestDisease(Boolean hasPestDisease) {
        this.hasPestDisease = hasPestDisease;
    }

    public String getPestDiseaseType() {
        return pestDiseaseType;
    }

    public void setPestDiseaseType(String pestDiseaseType) {
        this.pestDiseaseType = pestDiseaseType;
    }

    public Double getPestConfidence() {
        return pestConfidence;
    }

    public void setPestConfidence(Double pestConfidence) {
        this.pestConfidence = pestConfidence;
    }

    public Boolean getHasWeeds() {
        return hasWeeds;
    }

    public void setHasWeeds(Boolean hasWeeds) {
        this.hasWeeds = hasWeeds;
    }

    public String getWeedSeverity() {
        return weedSeverity;
    }

    public void setWeedSeverity(String weedSeverity) {
        this.weedSeverity = weedSeverity;
    }

    public Double getWeedConfidence() {
        return weedConfidence;
    }

    public void setWeedConfidence(Double weedConfidence) {
        this.weedConfidence = weedConfidence;
    }

    public Double getOverallHealthScore() {
        return overallHealthScore;
    }

    public void setOverallHealthScore(Double overallHealthScore) {
        this.overallHealthScore = overallHealthScore;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }
}
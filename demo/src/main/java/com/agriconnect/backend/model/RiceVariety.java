package com.agriconnect.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rice_varieties")
public class RiceVariety {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 50)
    private String code;  // e.g., "NSIC Rc 222"

    @Column(name = "maturity_days", nullable = false)
    private Integer maturityDays;  // 100-130 days

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Column(name = "yield_potential")
    private Double yieldPotential;  // tons per hectare

    @Column(name = "drought_tolerant")
    private Boolean droughtTolerant = false;

    @Column(name = "flood_tolerant")
    private Boolean floodTolerant = false;

    @Column(name = "pest_resistant")
    private Boolean pestResistant = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommended_fertilizer")
    private String recommendedFertilizer;

    @Column(name = "planting_tips", columnDefinition = "TEXT")
    private String plantingTips;

    public enum Season {
        WET,    // Wet season (June-November)
        DRY,    // Dry season (December-May)
        BOTH    // Can be planted any season
    }

    // Constructors
    public RiceVariety() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getMaturityDays() {
        return maturityDays;
    }

    public void setMaturityDays(Integer maturityDays) {
        this.maturityDays = maturityDays;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Double getYieldPotential() {
        return yieldPotential;
    }

    public void setYieldPotential(Double yieldPotential) {
        this.yieldPotential = yieldPotential;
    }

    public Boolean getDroughtTolerant() {
        return droughtTolerant;
    }

    public void setDroughtTolerant(Boolean droughtTolerant) {
        this.droughtTolerant = droughtTolerant;
    }

    public Boolean getFloodTolerant() {
        return floodTolerant;
    }

    public void setFloodTolerant(Boolean floodTolerant) {
        this.floodTolerant = floodTolerant;
    }

    public Boolean getPestResistant() {
        return pestResistant;
    }

    public void setPestResistant(Boolean pestResistant) {
        this.pestResistant = pestResistant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendedFertilizer() {
        return recommendedFertilizer;
    }

    public void setRecommendedFertilizer(String recommendedFertilizer) {
        this.recommendedFertilizer = recommendedFertilizer;
    }

    public String getPlantingTips() {
        return plantingTips;
    }

    public void setPlantingTips(String plantingTips) {
        this.plantingTips = plantingTips;
    }
}
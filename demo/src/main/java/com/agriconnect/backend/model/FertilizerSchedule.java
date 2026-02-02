package com.agriconnect.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fertilizer_schedules")
public class FertilizerSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variety_id", nullable = false)
    private RiceVariety riceVariety;

    @Column(name = "day_after_planting", nullable = false)
    private Integer dayAfterPlanting;

    @Column(name = "fertilizer_type", nullable = false, length = 100)
    private String fertilizerType;  // e.g., "14-14-14", "Urea"

    @Column(name = "amount_per_hectare", length = 100)
    private String amountPerHectare;  // e.g., "1 bag (50kg)"

    @Column(name = "application_method", columnDefinition = "TEXT")
    private String applicationMethod;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public FertilizerSchedule() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RiceVariety getRiceVariety() {
        return riceVariety;
    }

    public void setRiceVariety(RiceVariety riceVariety) {
        this.riceVariety = riceVariety;
    }

    public Integer getDayAfterPlanting() {
        return dayAfterPlanting;
    }

    public void setDayAfterPlanting(Integer dayAfterPlanting) {
        this.dayAfterPlanting = dayAfterPlanting;
    }

    public String getFertilizerType() {
        return fertilizerType;
    }

    public void setFertilizerType(String fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public String getAmountPerHectare() {
        return amountPerHectare;
    }

    public void setAmountPerHectare(String amountPerHectare) {
        this.amountPerHectare = amountPerHectare;
    }

    public String getApplicationMethod() {
        return applicationMethod;
    }

    public void setApplicationMethod(String applicationMethod) {
        this.applicationMethod = applicationMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
package com.agriconnect.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fertilizer_applications")
public class FertilizerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "planting_record_id", nullable = false)
    private PlantingRecord plantingRecord;

    @ManyToOne
    @JoinColumn(name = "fertilizer_schedule_id")
    private FertilizerSchedule fertilizerSchedule;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @Column(name = "fertilizer_type", nullable = false, length = 100)
    private String fertilizerType;

    @Column(name = "amount_applied", length = 100)
    private String amountApplied;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "postponed_reason", columnDefinition = "TEXT")
    private String postponedReason;  // e.g., "Rain forecasted"

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING,      // Not yet applied
        SCHEDULED,    // Weather-approved, ready to apply
        POSTPONED,    // Delayed due to weather
        COMPLETED,    // Successfully applied
        SKIPPED       // Farmer decided to skip
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public FertilizerApplication() {}

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

    public FertilizerSchedule getFertilizerSchedule() {
        return fertilizerSchedule;
    }

    public void setFertilizerSchedule(FertilizerSchedule fertilizerSchedule) {
        this.fertilizerSchedule = fertilizerSchedule;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getFertilizerType() {
        return fertilizerType;
    }

    public void setFertilizerType(String fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public String getAmountApplied() {
        return amountApplied;
    }

    public void setAmountApplied(String amountApplied) {
        this.amountApplied = amountApplied;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPostponedReason() {
        return postponedReason;
    }

    public void setPostponedReason(String postponedReason) {
        this.postponedReason = postponedReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
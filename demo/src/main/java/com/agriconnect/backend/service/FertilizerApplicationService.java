package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.FertilizerRecommendation;
import com.agriconnect.backend.model.FertilizerApplication;
import com.agriconnect.backend.model.FertilizerSchedule;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.repository.FertilizerApplicationRepository;
import com.agriconnect.backend.repository.FertilizerScheduleRepository;
import com.agriconnect.backend.repository.PlantingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class FertilizerApplicationService {

    @Autowired
    private FertilizerApplicationRepository applicationRepository;

    @Autowired
    private FertilizerScheduleRepository scheduleRepository;

    @Autowired
    private PlantingRecordRepository plantingRecordRepository;

    @Autowired
    private SmartFertilizerService smartFertilizerService;

    /**
     * Generate fertilizer applications for a planting record
     * This creates the schedule based on the rice variety
     */
    public List<FertilizerApplication> generateApplicationSchedule(Long plantingRecordId) {
        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        // Get fertilizer schedule for this variety
        List<FertilizerSchedule> schedules = scheduleRepository
                .findByRiceVarietyOrderByDayAfterPlanting(planting.getRiceVariety());

        List<FertilizerApplication> applications = new ArrayList<>();

        for (FertilizerSchedule schedule : schedules) {
            FertilizerApplication application = new FertilizerApplication();
            application.setPlantingRecord(planting);
            application.setFertilizerSchedule(schedule);
            application.setFertilizerType(schedule.getFertilizerType());
            application.setAmountApplied(schedule.getAmountPerHectare());

            // Calculate scheduled date
            LocalDate scheduledDate = planting.getPlantingDate()
                    .plusDays(schedule.getDayAfterPlanting());
            application.setScheduledDate(scheduledDate);

            application.setStatus(FertilizerApplication.Status.PENDING);

            applications.add(applicationRepository.save(application));
        }

        return applications;
    }

    /**
     * Get all applications for a planting record
     */
    public List<FertilizerApplication> getApplicationsForPlanting(Long plantingRecordId) {
        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        return applicationRepository.findByPlantingRecordOrderByScheduledDateAsc(planting);
    }

    /**
     * Get smart recommendation for a specific application
     */
    public FertilizerRecommendation getSmartRecommendation(Long applicationId) {
        FertilizerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        return smartFertilizerService.getSmartRecommendation(
                application.getPlantingRecord(),
                application.getScheduledDate()
        );
    }

    /**
     * Get upcoming applications (within next 7 days)
     */
    public List<FertilizerApplication> getUpcomingApplications(Long plantingRecordId) {
        LocalDate today = LocalDate.now();
        LocalDate weekFromNow = today.plusDays(7);

        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        List<FertilizerApplication> allApplications =
                applicationRepository.findByPlantingRecordOrderByScheduledDateAsc(planting);

        List<FertilizerApplication> upcoming = new ArrayList<>();
        for (FertilizerApplication app : allApplications) {
            if ((app.getScheduledDate().isAfter(today) || app.getScheduledDate().isEqual(today))
                    && app.getScheduledDate().isBefore(weekFromNow)
                    && app.getStatus() == FertilizerApplication.Status.PENDING) {
                upcoming.add(app);
            }
        }

        return upcoming;
    }

    /**
     * Mark application as completed
     */
    public FertilizerApplication markAsCompleted(Long applicationId, LocalDate appliedDate, String notes) {
        FertilizerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(FertilizerApplication.Status.COMPLETED);
        application.setAppliedDate(appliedDate);
        application.setNotes(notes);

        return applicationRepository.save(application);
    }

    /**
     * Postpone application due to weather
     */
    public FertilizerApplication postponeApplication(Long applicationId, LocalDate newDate, String reason) {
        FertilizerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(FertilizerApplication.Status.POSTPONED);
        application.setScheduledDate(newDate);
        application.setPostponedReason(reason);

        return applicationRepository.save(application);
    }

    /**
     * Skip application
     */
    public FertilizerApplication skipApplication(Long applicationId, String reason) {
        FertilizerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(FertilizerApplication.Status.SKIPPED);
        application.setNotes(reason);

        return applicationRepository.save(application);
    }

    /**
     * Get pending applications
     */
    public List<FertilizerApplication> getPendingApplications(Long plantingRecordId) {
        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        return applicationRepository.findByPlantingRecordAndStatus(
                planting,
                FertilizerApplication.Status.PENDING
        );
    }

    /**
     * Check if application is overdue
     */
    public boolean isOverdue(FertilizerApplication application) {
        LocalDate today = LocalDate.now();
        return application.getScheduledDate().isBefore(today)
                && application.getStatus() == FertilizerApplication.Status.PENDING;
    }

    /**
     * Get days until/past scheduled date
     */
    public long getDaysFromSchedule(FertilizerApplication application) {
        return ChronoUnit.DAYS.between(application.getScheduledDate(), LocalDate.now());
    }
}
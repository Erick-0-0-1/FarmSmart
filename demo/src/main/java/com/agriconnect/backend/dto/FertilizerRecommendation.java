package com.agriconnect.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class FertilizerRecommendation {
    private boolean canApplyToday;
    private LocalDate bestDate;
    private String bestTime;  // e.g., "6-8 AM"
    private String overallRecommendation;  // "Apply on [date]", "Postpone", "Apply early"
    private String urgencyLevel;  // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    private List<WeatherForecast> weekForecast;
    private String detailedAdvice;

    public FertilizerRecommendation() {}

    // Getters and Setters
    public boolean isCanApplyToday() {
        return canApplyToday;
    }

    public void setCanApplyToday(boolean canApplyToday) {
        this.canApplyToday = canApplyToday;
    }

    public LocalDate getBestDate() {
        return bestDate;
    }

    public void setBestDate(LocalDate bestDate) {
        this.bestDate = bestDate;
    }

    public String getBestTime() {
        return bestTime;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }

    public String getOverallRecommendation() {
        return overallRecommendation;
    }

    public void setOverallRecommendation(String overallRecommendation) {
        this.overallRecommendation = overallRecommendation;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public List<WeatherForecast> getWeekForecast() {
        return weekForecast;
    }

    public void setWeekForecast(List<WeatherForecast> weekForecast) {
        this.weekForecast = weekForecast;
    }

    public String getDetailedAdvice() {
        return detailedAdvice;
    }

    public void setDetailedAdvice(String detailedAdvice) {
        this.detailedAdvice = detailedAdvice;
    }
}
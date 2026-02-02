package com.agriconnect.backend.dto;

import java.time.LocalDate;

public class WeatherForecast {
    private LocalDate date;
    private String condition;  // "Clear", "Rain", "Clouds"
    private Double temperature;
    private Integer humidity;
    private Double rainfall;  // mm
    private Double windSpeed;
    private String recommendation;  // "Perfect", "Good", "Acceptable", "Poor", "Bad"
    private String reason;  // Why this recommendation

    public WeatherForecast() {}

    public WeatherForecast(LocalDate date, String condition, Double temperature,
                           Integer humidity, Double rainfall, Double windSpeed) {
        this.date = date;
        this.condition = condition;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getRainfall() {
        return rainfall;
    }

    public void setRainfall(Double rainfall) {
        this.rainfall = rainfall;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
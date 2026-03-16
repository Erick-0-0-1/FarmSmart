package com.agriconnect.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agriconnect.backend.dto.WeatherForecast;
import com.agriconnect.backend.service.WeatherService;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * Get current weather for Isabela
     * GET /api/weather/current
     */
    @GetMapping("/current")
    public ResponseEntity<WeatherForecast> getCurrentWeather(Authentication authentication) {
        WeatherForecast weather = weatherService.getCurrentWeather();
        return ResponseEntity.ok(weather);
    }

    /**
     * Get 7-day forecast for Isabela
     * GET /api/weather/forecast
     */
    @GetMapping("/forecast")
    public ResponseEntity<List<WeatherForecast>> get7DayForecast(Authentication authentication) {
        List<WeatherForecast> forecast = weatherService.get7DayForecast();
        return ResponseEntity.ok(forecast);
    }

    /**
     * Get forecast for specific coordinates
     * GET /api/weather/forecast?lat=16.9754&lon=121.8107
     */
    @GetMapping("/forecast/location")
    public ResponseEntity<List<WeatherForecast>> getForecastByLocation(
            @RequestParam Double lat,
            @RequestParam Double lon,
            Authentication authentication) {
        List<WeatherForecast> forecast = weatherService.get7DayForecast(lat, lon);
        return ResponseEntity.ok(forecast);
    }
}
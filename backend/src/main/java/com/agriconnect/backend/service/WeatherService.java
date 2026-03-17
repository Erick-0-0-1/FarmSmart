package com.agriconnect.backend.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.agriconnect.backend.dto.WeatherForecast;
import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class WeatherService {

    private final WebClient webClient;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.default.lat}")
    private Double defaultLat;

    @Value("${weather.default.lon}")
    private Double defaultLon;

    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Get 5‑day weather forecast for Isabela (using default coordinates)
     */
    public List<WeatherForecast> get5DayForecast() {
        return get5DayForecast(defaultLat, defaultLon);
    }

    /**
     * Get 5‑day weather forecast for specific coordinates
     */
    public List<WeatherForecast> get5DayForecast(Double lat, Double lon) {
        List<WeatherForecast> forecasts = new ArrayList<>();

        try {
            // Call OpenWeatherMap 5-day / 3-hour forecast API
            String url = apiUrl + "/forecast?lat=" + lat + "&lon=" + lon
                    + "&appid=" + apiKey + "&units=metric";

            Mono<JsonNode> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class);

            JsonNode data = response.block();

            if (data != null && data.has("list")) {
                JsonNode forecastList = data.get("list");

                LocalDate currentDate = null;
                WeatherForecast dailyForecast = null;
                double totalTemp = 0;
                int tempCount = 0;
                double maxRainfall = 0;
                int totalHumidity = 0;
                int humidityCount = 0;
                String dominantCondition = "";

                for (JsonNode item : forecastList) {
                    long timestamp = item.get("dt").asLong();
                    LocalDate date = Instant.ofEpochSecond(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // If new day, save previous day's forecast
                    if (currentDate != null && !date.equals(currentDate)) {
                        if (dailyForecast != null) {
                            dailyForecast.setTemperature(totalTemp / tempCount);
                            dailyForecast.setHumidity(totalHumidity / humidityCount);
                            dailyForecast.setRainfall(maxRainfall);
                            dailyForecast.setCondition(dominantCondition);

                            // Analyze and set recommendation
                            analyzeForecast(dailyForecast);

                            forecasts.add(dailyForecast);

                            // Stop after 5 days
                            if (forecasts.size() >= 5) {
                                break;
                            }
                        }

                        // Reset for new day
                        totalTemp = 0;
                        tempCount = 0;
                        maxRainfall = 0;
                        totalHumidity = 0;
                        humidityCount = 0;
                    }

                    // Start new day
                    if (currentDate == null || !date.equals(currentDate)) {
                        currentDate = date;
                        dailyForecast = new WeatherForecast();
                        dailyForecast.setDate(date);
                    }

                    // Aggregate data for the day
                    JsonNode main = item.get("main");
                    JsonNode weather = item.get("weather").get(0);

                    totalTemp += main.get("temp").asDouble();
                    tempCount++;

                    totalHumidity += main.get("humidity").asInt();
                    humidityCount++;

                    // Get rainfall (if present)
                    if (item.has("rain") && item.get("rain").has("3h")) {
                        double rain = item.get("rain").get("3h").asDouble();
                        maxRainfall = Math.max(maxRainfall, rain);
                    }

                    // Get dominant weather condition
                    dominantCondition = weather.get("main").asText();
                }

                // Add last day if we haven't reached 5 yet
                if (dailyForecast != null && forecasts.size() < 5) {
                    dailyForecast.setTemperature(totalTemp / tempCount);
                    dailyForecast.setHumidity(totalHumidity / humidityCount);
                    dailyForecast.setRainfall(maxRainfall);
                    dailyForecast.setCondition(dominantCondition);
                    analyzeForecast(dailyForecast);
                    forecasts.add(dailyForecast);
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching weather: " + e.getMessage());
            // Return mock data if API fails (for development)
            return getMockForecast();
        }

        return forecasts;
    }

    /**
     * Analyze weather and determine if good for fertilizer application
     */
    private void analyzeForecast(WeatherForecast forecast) {
        double rainfall = forecast.getRainfall();
        String condition = forecast.getCondition();
        int humidity = forecast.getHumidity();

        // Decision logic for fertilizer application
        if (rainfall > 10) {
            // Heavy rain
            forecast.setRecommendation("BAD");
            forecast.setReason("Heavy rainfall expected (" + rainfall + "mm). Fertilizer will wash away.");
        } else if (rainfall > 5) {
            // Moderate rain
            forecast.setRecommendation("POOR");
            forecast.setReason("Moderate rain expected (" + rainfall + "mm). Not ideal for application.");
        } else if (rainfall > 2) {
            // Light rain
            forecast.setRecommendation("ACCEPTABLE");
            forecast.setReason("Light rain possible (" + rainfall + "mm). Apply early morning if needed.");
        } else if (condition.equalsIgnoreCase("Clear") && humidity < 80) {
            // Perfect conditions
            forecast.setRecommendation("PERFECT");
            forecast.setReason("Clear skies, low humidity. Ideal for fertilizer application.");
        } else if (condition.equalsIgnoreCase("Clouds") && rainfall == 0) {
            // Good conditions
            forecast.setRecommendation("GOOD");
            forecast.setReason("Cloudy but dry. Good for fertilizer application.");
        } else if (condition.equalsIgnoreCase("Rain")) {
            // Rainy
            forecast.setRecommendation("BAD");
            forecast.setReason("Rainy conditions. Postpone application.");
        } else {
            // Default acceptable
            forecast.setRecommendation("ACCEPTABLE");
            forecast.setReason("Weather is acceptable for application.");
        }
    }

    /**
     * Mock forecast for testing (when API is down or during development)
     * Returns 5 days of mock data.
     */
    private List<WeatherForecast> getMockForecast() {
        List<WeatherForecast> forecasts = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 5; i++) {
            WeatherForecast forecast = new WeatherForecast();
            forecast.setDate(today.plusDays(i));
            forecast.setTemperature(28.0 + (Math.random() * 4 - 2));
            forecast.setHumidity(70 + (int)(Math.random() * 20));
            forecast.setRainfall(Math.random() * 10);
            forecast.setCondition(i % 3 == 0 ? "Clear" : "Clouds");

            analyzeForecast(forecast);
            forecasts.add(forecast);
        }

        return forecasts;
    }

    /**
     * Get current weather for Isabela
     */
    public WeatherForecast getCurrentWeather() {
        try {
            String url = apiUrl + "/weather?lat=" + defaultLat + "&lon=" + defaultLon
                    + "&appid=" + apiKey + "&units=metric";

            Mono<JsonNode> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class);

            JsonNode data = response.block();

            if (data != null) {
                JsonNode main = data.get("main");
                JsonNode weather = data.get("weather").get(0);
                JsonNode wind = data.get("wind");

                WeatherForecast current = new WeatherForecast();
                current.setDate(LocalDate.now());
                current.setTemperature(main.get("temp").asDouble());
                current.setHumidity(main.get("humidity").asInt());
                current.setCondition(weather.get("main").asText());
                current.setWindSpeed(wind.get("speed").asDouble());

                // Check for rain
                if (data.has("rain") && data.get("rain").has("1h")) {
                    current.setRainfall(data.get("rain").get("1h").asDouble());
                } else {
                    current.setRainfall(0.0);
                }

                analyzeForecast(current);
                return current;
            }

        } catch (Exception e) {
            System.err.println("Error fetching current weather: " + e.getMessage());
        }

        // Return mock if fails
        WeatherForecast mock = new WeatherForecast();
        mock.setDate(LocalDate.now());
        mock.setTemperature(28.0);
        mock.setHumidity(75);
        mock.setRainfall(0.0);
        mock.setCondition("Clear");
        analyzeForecast(mock);
        return mock;
    }
}
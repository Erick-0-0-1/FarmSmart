package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.FertilizerRecommendation;
import com.agriconnect.backend.dto.WeatherForecast;
import com.agriconnect.backend.model.FertilizerApplication;
import com.agriconnect.backend.model.PlantingRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SmartFertilizerService {

    @Autowired
    private WeatherService weatherService;

    /**
     * MAIN SMART RECOMMENDATION ENGINE
     * This analyzes weather and gives intelligent fertilizer advice
     */
    public FertilizerRecommendation getSmartRecommendation(
            PlantingRecord planting,
            LocalDate scheduledDate) {

        FertilizerRecommendation recommendation = new FertilizerRecommendation();

        // Get 7-day forecast
        List<WeatherForecast> forecast = weatherService.get7DayForecast();
        recommendation.setWeekForecast(forecast);

        // Calculate urgency
        long daysFromSchedule = ChronoUnit.DAYS.between(scheduledDate, LocalDate.now());
        String urgency = calculateUrgency(daysFromSchedule);
        recommendation.setUrgencyLevel(urgency);

        // Find best day in the week
        WeatherForecast bestDay = findBestDay(forecast);
        LocalDate bestDate = bestDay.getDate();
        recommendation.setBestDate(bestDate);

        // Determine if can apply today
        WeatherForecast today = forecast.get(0);
        boolean canApplyToday = today.getRecommendation().equals("PERFECT")
                || today.getRecommendation().equals("GOOD");
        recommendation.setCanApplyToday(canApplyToday);

        // Smart recommendation logic
        String advice = generateSmartAdvice(
                today, bestDay, urgency, scheduledDate, daysFromSchedule
        );
        recommendation.setOverallRecommendation(advice);

        // Best time of day
        recommendation.setBestTime(determineBestTime(bestDay));

        // Detailed advice
        String detailed = generateDetailedAdvice(
                forecast, bestDay, urgency, scheduledDate, daysFromSchedule
        );
        recommendation.setDetailedAdvice(detailed);

        return recommendation;
    }

    /**
     * Calculate urgency based on how late/early we are
     */
    private String calculateUrgency(long daysFromSchedule) {
        if (daysFromSchedule > 5) {
            return "CRITICAL";  // Very late!
        } else if (daysFromSchedule > 2) {
            return "HIGH";  // Getting late
        } else if (daysFromSchedule >= -2 && daysFromSchedule <= 2) {
            return "MEDIUM";  // Right on time
        } else {
            return "LOW";  // Early, flexible
        }
    }

    /**
     * Find the best day in the 7-day forecast
     */
    private WeatherForecast findBestDay(List<WeatherForecast> forecast) {
        WeatherForecast best = forecast.get(0);
        int bestScore = scoreDay(best);

        for (WeatherForecast day : forecast) {
            int score = scoreDay(day);
            if (score > bestScore) {
                bestScore = score;
                best = day;
            }
        }

        return best;
    }

    /**
     * Score each day (higher = better)
     */
    private int scoreDay(WeatherForecast day) {
        int score = 0;

        switch (day.getRecommendation()) {
            case "PERFECT": score = 100; break;
            case "GOOD": score = 80; break;
            case "ACCEPTABLE": score = 50; break;
            case "POOR": score = 20; break;
            case "BAD": score = 0; break;
        }

        return score;
    }

    /**
     * Generate smart advice based on all factors
     */
    private String generateSmartAdvice(
            WeatherForecast today,
            WeatherForecast bestDay,
            String urgency,
            LocalDate scheduledDate,
            long daysFromSchedule) {

        // SCENARIO 1: Today is perfect and we're on time
        if (today.getRecommendation().equals("PERFECT") &&
                Math.abs(daysFromSchedule) <= 2) {
            return "✅ TODAY is PERFECT for fertilizer application! Apply now.";
        }

        // SCENARIO 2: Today is good and we're getting late
        if ((today.getRecommendation().equals("PERFECT") ||
                today.getRecommendation().equals("GOOD")) &&
                urgency.equals("HIGH") || urgency.equals("CRITICAL")) {
            return "⚠️ APPLY TODAY! You're " + Math.abs(daysFromSchedule) +
                    " days late and today's weather is good.";
        }

        // SCENARIO 3: Today is bad but we're critical
        if (urgency.equals("CRITICAL") && today.getRecommendation().equals("BAD")) {
            if (bestDay.getDate().isAfter(LocalDate.now().plusDays(3))) {
                return "🚨 URGENT: You're very late. Consider applying TODAY in early morning before rain, or apply on " +
                        bestDay.getDate() + " (best available day).";
            }
        }

        // SCENARIO 4: Today is bad, best day is soon
        if (!today.getRecommendation().equals("PERFECT") &&
                bestDay.getDate().isBefore(LocalDate.now().plusDays(3))) {
            return "⏳ WAIT until " + bestDay.getDate() +
                    " (" + bestDay.getRecommendation() + " weather). Better conditions coming soon.";
        }

        // SCENARIO 5: Whole week is bad
        if (bestDay.getRecommendation().equals("POOR") ||
                bestDay.getRecommendation().equals("BAD")) {
            return "🌧️ Unfavorable weather all week. Best available: " + bestDay.getDate() +
                    ". Consider applying early morning if urgent.";
        }

        // SCENARIO 6: We're early, wait for perfect day
        if (daysFromSchedule < -3 && bestDay.getDate().isAfter(LocalDate.now().plusDays(2))) {
            return "📅 You're ahead of schedule. Ideal day: " + bestDay.getDate() +
                    ". No rush yet.";
        }

        // SCENARIO 7: Apply early due to bad weather ahead
        if (today.getRecommendation().equals("PERFECT") &&
                daysFromSchedule < 0 && daysFromSchedule >= -3) {
            // Check if next days are worse
            boolean worseAhead = true;
            for (int i = 1; i < Math.min(3, today.getDate().until(scheduledDate, ChronoUnit.DAYS)); i++) {
                // If upcoming days are better, wait
                if (scoreDay(weatherService.get7DayForecast().get(i)) > 50) {
                    worseAhead = false;
                    break;
                }
            }

            if (worseAhead) {
                return "🌦️ APPLY EARLY (TODAY)! Bad weather expected on your scheduled date.";
            }
        }

        // DEFAULT: Best day recommendation
        return "📅 Best day to apply: " + bestDay.getDate() +
                " (" + bestDay.getRecommendation() + " conditions)";
    }

    /**
     * Determine best time of day to apply
     */
    private String determineBestTime(WeatherForecast day) {
        if (day.getRecommendation().equals("PERFECT") ||
                day.getRecommendation().equals("GOOD")) {
            return "6:00 AM - 9:00 AM (cool morning hours)";
        } else if (day.getRecommendation().equals("ACCEPTABLE")) {
            return "Early morning (5:00 AM - 7:00 AM) before rain";
        } else {
            return "Early morning if urgent, otherwise wait for better weather";
        }
    }

    /**
     * Generate detailed advice with full week analysis
     */
    private String generateDetailedAdvice(
            List<WeatherForecast> forecast,
            WeatherForecast bestDay,
            String urgency,
            LocalDate scheduledDate,
            long daysFromSchedule) {

        StringBuilder advice = new StringBuilder();

        // Current status
        advice.append("📊 ANALYSIS\n\n");
        advice.append("Scheduled Date: ").append(scheduledDate).append("\n");
        advice.append("Current Status: ");

        if (daysFromSchedule > 0) {
            advice.append(daysFromSchedule).append(" days LATE\n");
        } else if (daysFromSchedule < 0) {
            advice.append(Math.abs(daysFromSchedule)).append(" days EARLY\n");
        } else {
            advice.append("ON TIME\n");
        }

        advice.append("Urgency Level: ").append(urgency).append("\n\n");

        // Week forecast summary
        advice.append("📅 7-DAY FORECAST:\n\n");
        for (int i = 0; i < forecast.size(); i++) {
            WeatherForecast day = forecast.get(i);
            String dayLabel = i == 0 ? "TODAY" :
                    i == 1 ? "TOMORROW" :
                            "Day " + (i + 1);

            String emoji = getWeatherEmoji(day.getRecommendation());

            advice.append(emoji).append(" ")
                    .append(dayLabel).append(" (").append(day.getDate()).append("): ")
                    .append(day.getRecommendation()).append("\n")
                    .append("   ").append(day.getReason()).append("\n\n");
        }

        // Final recommendation
        advice.append("\n💡 RECOMMENDATION:\n");
        advice.append("Apply fertilizer on ").append(bestDay.getDate())
                .append(" between ").append(determineBestTime(bestDay)).append("\n\n");

        // Additional tips
        advice.append("✅ TIPS:\n");
        advice.append("• Check weather again on the day of application\n");
        advice.append("• Apply when soil is slightly moist but not waterlogged\n");
        advice.append("• Avoid windy days (fertilizer drift)\n");
        advice.append("• Don't apply if heavy rain is expected within 24 hours\n");

        return advice.toString();
    }

    /**
     * Get emoji for weather recommendation
     */
    private String getWeatherEmoji(String recommendation) {
        switch (recommendation) {
            case "PERFECT": return "☀️";
            case "GOOD": return "⛅";
            case "ACCEPTABLE": return "🌤️";
            case "POOR": return "🌧️";
            case "BAD": return "⛈️";
            default: return "🌥️";
        }
    }
}
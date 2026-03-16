package com.agriconnect.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.agriconnect.backend.model.CropHealthAnalysis;
import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.repository.CropHealthAnalysisRepository;
import com.agriconnect.backend.repository.PlantingRecordRepository;

@Service
public class CropHealthService {

    @Autowired
    private CropHealthAnalysisRepository analysisRepository;

    @Autowired
    private PlantingRecordRepository plantingRecordRepository;

    /**
     * Analyze crop image and provide recommendations
     *
     * For now, this is a MOCK implementation
     * In production, you would integrate:
     * - Google Vision API
     * - Custom ML model
     * - Plant disease detection API
     */
    public CropHealthAnalysis analyzeCropImage(Long plantingRecordId, MultipartFile image, String notes) {
        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        // TODO: Upload image to cloud storage (Cloudinary, AWS S3)
        String imageUrl = uploadImageToCloud(image);

        // TODO: Call AI/ML API for analysis
        // For now, we'll create a mock analysis
        CropHealthAnalysis analysis = performMockAnalysis(imageUrl);

        analysis.setPlantingRecord(planting);
        analysis.setNotes(notes);

        return analysisRepository.save(analysis);
    }

    /**
     * Get all analyses for a planting record
     */
    public List<CropHealthAnalysis> getAnalysesForPlanting(Long plantingRecordId) {
        PlantingRecord planting = plantingRecordRepository.findById(plantingRecordId)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));

        return analysisRepository.findByPlantingRecordOrderByAnalyzedAtDesc(planting);
    }

    /**
     * Get latest analysis
     */
    public CropHealthAnalysis getLatestAnalysis(Long plantingRecordId) {
        List<CropHealthAnalysis> analyses = getAnalysesForPlanting(plantingRecordId);
        if (analyses.isEmpty()) {
            throw new RuntimeException("No analysis found for this planting");
        }
        return analyses.get(0);
    }

    /**
     * Get analysis by ID
     */
    public CropHealthAnalysis getAnalysisById(Long analysisId) {
        return analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analysis not found with id: " + analysisId));
    }

    /**
     * Mock image upload (replace with actual cloud storage)
     */
    private String uploadImageToCloud(MultipartFile image) {
        // TODO: Implement actual image upload
        // For now, return a placeholder URL
        return "https://placeholder.com/crop-image-" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * Mock AI analysis (replace with actual AI/ML model)
     *
     * This generates realistic mock data for testing
     * In production, this would call:
     * - Google Vision API for general image analysis
     * - Plant.id API for disease detection
     * - Custom trained model for rice-specific issues
     */
    private CropHealthAnalysis performMockAnalysis(String imageUrl) {
        CropHealthAnalysis analysis = new CropHealthAnalysis();
        analysis.setImageUrl(imageUrl);

        // Simulate AI analysis with random but realistic results
        double random = Math.random();

        // Water stress detection (30% chance)
        if (random > 0.7) {
            analysis.setNeedsWater(true);
            analysis.setWaterConfidence(0.75 + Math.random() * 0.2);
        } else {
            analysis.setNeedsWater(false);
            analysis.setWaterConfidence(0.8 + Math.random() * 0.15);
        }

        // Nutrient deficiency detection (25% chance)
        if (random > 0.75) {
            analysis.setNeedsFertilizer(true);
            analysis.setFertilizerConfidence(0.7 + Math.random() * 0.25);
        } else {
            analysis.setNeedsFertilizer(false);
            analysis.setFertilizerConfidence(0.85 + Math.random() * 0.1);
        }

        // Pest/disease detection (20% chance)
        if (random > 0.8) {
            analysis.setHasPestDisease(true);
            String[] diseases = {
                    "Brown spot",
                    "Leaf blast",
                    "Sheath blight",
                    "Stem borer infestation",
                    "Rice bug damage"
            };
            analysis.setPestDiseaseType(diseases[(int)(Math.random() * diseases.length)]);
            analysis.setPestConfidence(0.65 + Math.random() * 0.3);
        } else {
            analysis.setHasPestDisease(false);
            analysis.setPestConfidence(0.9 + Math.random() * 0.05);
        }

        // Weed detection (35% chance)
        if (random > 0.65) {
            analysis.setHasWeeds(true);
            String[] severities = {"LOW", "MEDIUM", "HIGH"};
            analysis.setWeedSeverity(severities[(int)(Math.random() * 3)]);
            analysis.setWeedConfidence(0.7 + Math.random() * 0.25);
        } else {
            analysis.setHasWeeds(false);
            analysis.setWeedConfidence(0.85 + Math.random() * 0.1);
        }

        // Calculate overall health score
        double healthScore = 100.0;
        if (analysis.getNeedsWater()) healthScore -= 15;
        if (analysis.getNeedsFertilizer()) healthScore -= 20;
        if (analysis.getHasPestDisease()) healthScore -= 25;
        if (analysis.getHasWeeds()) {
            switch (analysis.getWeedSeverity()) {
                case "LOW": healthScore -= 5; break;
                case "MEDIUM": healthScore -= 10; break;
                case "HIGH": healthScore -= 15; break;
            }
        }
        analysis.setOverallHealthScore(Math.max(healthScore, 0));

        // Generate recommendations
        analysis.setRecommendations(generateRecommendations(analysis));

        return analysis;
    }

    /**
     * Generate AI recommendations based on analysis
     */
    private String generateRecommendations(CropHealthAnalysis analysis) {
        StringBuilder recommendations = new StringBuilder();

        recommendations.append("🌾 CROP HEALTH ANALYSIS RESULTS\n\n");
        recommendations.append("Overall Health Score: ")
                .append(String.format("%.1f", analysis.getOverallHealthScore()))
                .append("/100\n\n");

        recommendations.append("📋 FINDINGS:\n\n");

        // Water recommendations
        if (analysis.getNeedsWater()) {
            recommendations.append("💧 WATER STRESS DETECTED (")
                    .append(String.format("%.0f", analysis.getWaterConfidence() * 100))
                    .append("% confidence)\n");
            recommendations.append("   → Recommendation: Irrigate immediately. Apply 5-7cm of water.\n");
            recommendations.append("   → Signs: Leaf rolling, wilting, bluish-green color\n\n");
        } else {
            recommendations.append("✅ Water Status: GOOD\n");
            recommendations.append("   → Soil moisture appears adequate\n\n");
        }

        // Fertilizer recommendations
        if (analysis.getNeedsFertilizer()) {
            recommendations.append("🌱 NUTRIENT DEFICIENCY DETECTED (")
                    .append(String.format("%.0f", analysis.getFertilizerConfidence() * 100))
                    .append("% confidence)\n");
            recommendations.append("   → Recommendation: Apply nitrogen fertilizer (Urea)\n");
            recommendations.append("   → Signs: Yellowing leaves, stunted growth\n");
            recommendations.append("   → Apply 1 bag (50kg) Urea per hectare\n\n");
        } else {
            recommendations.append("✅ Nutrient Status: GOOD\n");
            recommendations.append("   → Plants show healthy green color\n\n");
        }

        // Pest/disease recommendations
        if (analysis.getHasPestDisease()) {
            recommendations.append("🐛 PEST/DISEASE DETECTED: ")
                    .append(analysis.getPestDiseaseType())
                    .append(" (")
                    .append(String.format("%.0f", analysis.getPestConfidence() * 100))
                    .append("% confidence)\n");

            // Disease-specific recommendations
            switch (analysis.getPestDiseaseType()) {
                case "Brown spot":
                    recommendations.append("   → Treatment: Apply fungicide (Mancozeb or Copper oxychloride)\n");
                    recommendations.append("   → Prevention: Ensure proper drainage, avoid excess nitrogen\n");
                    break;
                case "Leaf blast":
                    recommendations.append("   → Treatment: Apply Tricyclazole or Azoxystrobin\n");
                    recommendations.append("   → Prevention: Use resistant varieties, proper spacing\n");
                    break;
                case "Sheath blight":
                    recommendations.append("   → Treatment: Apply Validamycin or Hexaconazole\n");
                    recommendations.append("   → Prevention: Reduce plant density, improve air circulation\n");
                    break;
                case "Stem borer infestation":
                    recommendations.append("   → Treatment: Apply insecticide (Chlorantraniliprole)\n");
                    recommendations.append("   → Prevention: Remove egg masses, use pheromone traps\n");
                    break;
                case "Rice bug damage":
                    recommendations.append("   → Treatment: Apply Lambda-cyhalothrin during milky stage\n");
                    recommendations.append("   → Prevention: Remove weeds, monitor grain filling stage\n");
                    break;
            }
            recommendations.append("   → Contact your agricultural technician for severe cases\n\n");
        } else {
            recommendations.append("✅ Pest/Disease Status: HEALTHY\n");
            recommendations.append("   → No signs of major pests or diseases\n\n");
        }

        // Weed recommendations
        if (analysis.getHasWeeds()) {
            recommendations.append("🌿 WEEDS DETECTED - Severity: ")
                    .append(analysis.getWeedSeverity())
                    .append(" (")
                    .append(String.format("%.0f", analysis.getWeedConfidence() * 100))
                    .append("% confidence)\n");

            switch (analysis.getWeedSeverity()) {
                case "LOW":
                    recommendations.append("   → Manual weeding recommended\n");
                    recommendations.append("   → Remove weeds before they seed\n");
                    break;
                case "MEDIUM":
                    recommendations.append("   → Consider herbicide application\n");
                    recommendations.append("   → Use selective herbicides (2,4-D or Butachlor)\n");
                    break;
                case "HIGH":
                    recommendations.append("   → URGENT: Apply herbicide immediately\n");
                    recommendations.append("   → Heavy weed competition affecting crop\n");
                    recommendations.append("   → May need manual removal + herbicide\n");
                    break;
            }
            recommendations.append("\n");
        } else {
            recommendations.append("✅ Weed Status: CLEAN\n");
            recommendations.append("   → Field is well-maintained\n\n");
        }

        // General recommendations
        recommendations.append("\n📌 GENERAL RECOMMENDATIONS:\n");
        recommendations.append("• Monitor crop daily for changes\n");
        recommendations.append("• Take follow-up photos in 3-5 days\n");
        recommendations.append("• Maintain proper field water level\n");
        recommendations.append("• Keep records of all interventions\n");
        recommendations.append("• Consult agricultural technician if uncertain\n");

        return recommendations.toString();
    }

    /**
     * Get analysis summary (for dashboard)
     */
    public String getAnalysisSummary(CropHealthAnalysis analysis) {
        StringBuilder summary = new StringBuilder();

        if (analysis.getOverallHealthScore() >= 80) {
            summary.append("✅ Crop health is EXCELLENT");
        } else if (analysis.getOverallHealthScore() >= 60) {
            summary.append("⚠️ Crop health is FAIR - attention needed");
        } else {
            summary.append("🚨 Crop health is POOR - immediate action required");
        }

        return summary.toString();
    }
}
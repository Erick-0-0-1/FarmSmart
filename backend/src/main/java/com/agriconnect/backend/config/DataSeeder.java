package com.agriconnect.backend.config;

import com.agriconnect.backend.model.RiceVariety;
import com.agriconnect.backend.repository.RiceVarietyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(RiceVarietyRepository varietyRepository) {
        return args -> {
            // Only seed if database is empty
            if (varietyRepository.count() == 0) {
                // NSIC Rc 152 (Tubigan 10)
                varietyRepository.save(createVariety(
                        "NSIC Rc 152 (Tubigan 10)",
                        "NSIC_Rc_152",
                        "High-yielding variety suitable for wet season",
                        120,
                        RiceVariety.Season.WET,
                        5.0,
                        true,  // droughtTolerant - MODERATE → true
                        false, // floodTolerant - LOW → false
                        false,
                        "Complete fertilizer (14-14-14) at planting",
                        "Plant at the start of rainy season. Maintain proper water level."
                ));

                // NSIC Rc 222 (Tubigan 18)
                varietyRepository.save(createVariety(
                        "NSIC Rc 222 (Tubigan 18)",
                        "NSIC_Rc_222",
                        "Improved variety with good grain quality",
                        114,
                        RiceVariety.Season.WET,
                        5.5,
                        true,  // droughtTolerant - HIGH → true
                        true,  // floodTolerant - MODERATE → true
                        false,
                        "Urea (46-0-0) + Complete fertilizer",
                        "Requires good water management. Resistant to common pests."
                ));

                // NSIC Rc 160 (GSR 8)
                varietyRepository.save(createVariety(
                        "NSIC Rc 160 (GSR 8)",
                        "NSIC_Rc_160",
                        "Good for rainfed areas",
                        110,
                        RiceVariety.Season.WET,
                        4.5,
                        true,  // droughtTolerant - HIGH → true
                        true,  // floodTolerant - MODERATE → true
                        false,
                        "Organic fertilizer recommended",
                        "Suitable for areas with irregular rainfall."
                ));

                // PSB Rc 82 (IR64)
                varietyRepository.save(createVariety(
                        "PSB Rc 82 (IR64)",
                        "PSB_Rc_82",
                        "Popular variety, good eating quality",
                        115,
                        RiceVariety.Season.DRY,
                        5.0,
                        true,  // droughtTolerant - MODERATE → true
                        false, // floodTolerant - LOW → false
                        false,
                        "Complete fertilizer + Ammonium phosphate",
                        "Plant in well-drained fields. Monitor for pests."
                ));

                // NSIC Rc 298 (Matatag 1)
                varietyRepository.save(createVariety(
                        "NSIC Rc 298 (Matatag 1)",
                        "NSIC_Rc_298",
                        "Resilient variety",
                        105,
                        RiceVariety.Season.WET,
                        6.0,
                        true,  // droughtTolerant - HIGH → true
                        true,  // floodTolerant - HIGH → true
                        true,  // pestResistant
                        "Balanced fertilizer (10-10-10)",
                        "Highly adaptable to various conditions. Requires less pesticide."
                ));

                System.out.println("✅ Rice varieties seeded successfully!");
            } else {
                System.out.println("ℹ️ Rice varieties already exist, skipping seed.");
            }
        };
    }

    private RiceVariety createVariety(String name,
                                      String code,
                                      String description,
                                      int maturityDays,
                                      RiceVariety.Season season,
                                      double yieldPotential,
                                      boolean droughtTolerant,
                                      boolean floodTolerant,
                                      boolean pestResistant,
                                      String recommendedFertilizer,
                                      String plantingTips) {
        RiceVariety variety = new RiceVariety();
        variety.setName(name);
        variety.setCode(code);
        variety.setDescription(description);
        variety.setMaturityDays(maturityDays);
        variety.setSeason(season);
        variety.setYieldPotential(yieldPotential);
        variety.setDroughtTolerant(droughtTolerant);
        variety.setFloodTolerant(floodTolerant);
        variety.setPestResistant(pestResistant);
        variety.setRecommendedFertilizer(recommendedFertilizer);
        variety.setPlantingTips(plantingTips);
        return variety;
    }
}